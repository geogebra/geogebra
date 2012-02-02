/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.ode.events;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.exception.MathInternalError;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;

/** This class handles the state for one {@link EventHandler
 * event handler} during integration steps.
 *
 * <p>Each time the integrator proposes a step, the event handler
 * switching function should be checked. This class handles the state
 * of one handler during one integration step, with references to the
 * state at the end of the preceding step. This information is used to
 * decide if the handler should trigger an event or not during the
 * proposed step (and hence the step should be reduced to ensure the
 * event occurs at a bound rather than inside the step).</p>
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 */
public class EventState {

    /** Event handler. */
    private final EventHandler handler;

    /** Maximal time interval between events handler checks. */
    private final double maxCheckInterval;

    /** Convergence threshold for event localization. */
    private final double convergence;

    /** Upper limit in the iteration count for event localization. */
    private final int maxIterationCount;

    /** Time at the beginning of the step. */
    private double t0;

    /** Value of the events handler at the beginning of the step. */
    private double g0;

    /** Simulated sign of g0 (we cheat when crossing events). */
    private boolean g0Positive;

    /** Indicator of event expected during the step. */
    private boolean pendingEvent;

    /** Occurrence time of the pending event. */
    private double pendingEventTime;

    /** Occurrence time of the previous event. */
    private double previousEventTime;

    /** Integration direction. */
    private boolean forward;

    /** Variation direction around pending event.
     *  (this is considered with respect to the integration direction)
     */
    private boolean increasing;

    /** Next action indicator. */
    private int nextAction;

    /** Simple constructor.
     * @param handler event handler
     * @param maxCheckInterval maximal time interval between switching
     * function checks (this interval prevents missing sign changes in
     * case the integration steps becomes very large)
     * @param convergence convergence threshold in the event time search
     * @param maxIterationCount upper limit of the iteration count in
     * the event time search
     */
    public EventState(final EventHandler handler, final double maxCheckInterval,
                      final double convergence, final int maxIterationCount) {
        this.handler           = handler;
        this.maxCheckInterval  = maxCheckInterval;
        this.convergence       = FastMath.abs(convergence);
        this.maxIterationCount = maxIterationCount;

        // some dummy values ...
        t0                = Double.NaN;
        g0                = Double.NaN;
        g0Positive        = true;
        pendingEvent      = false;
        pendingEventTime  = Double.NaN;
        previousEventTime = Double.NaN;
        increasing        = true;
        nextAction        = EventHandler.CONTINUE;

    }

    /** Get the underlying event handler.
     * @return underlying event handler
     */
    public EventHandler getEventHandler() {
        return handler;
    }

    /** Get the maximal time interval between events handler checks.
     * @return maximal time interval between events handler checks
     */
    public double getMaxCheckInterval() {
        return maxCheckInterval;
    }

    /** Get the convergence threshold for event localization.
     * @return convergence threshold for event localization
     */
    public double getConvergence() {
        return convergence;
    }

    /** Get the upper limit in the iteration count for event localization.
     * @return upper limit in the iteration count for event localization
     */
    public int getMaxIterationCount() {
        return maxIterationCount;
    }

    /** Reinitialize the beginning of the step.
     * @param interpolator valid for the current step
     * @exception EventException if the event handler
     * value cannot be evaluated at the beginning of the step
     */
    public void reinitializeBegin(final StepInterpolator interpolator)
        throws EventException {
        try {
            // excerpt from MATH-421 issue:
            // If an ODE solver is setup with an EventHandler that return STOP
            // when the even is triggered, the integrator stops (which is exactly
            // the expected behavior). If however the user want to restart the
            // solver from the final state reached at the event with the same
            // configuration (expecting the event to be triggered again at a
            // later time), then the integrator may fail to start. It can get stuck
            // at the previous event.

            // The use case for the bug MATH-421 is fairly general, so events occurring
            // less than epsilon after the solver start in the first step should be ignored,
            // where epsilon is the convergence threshold of the event. The sign of the g
            // function should be evaluated after this initial ignore zone, not exactly at
            // beginning (if there are no event at the very beginning g(t0) and g(t0+epsilon)
            // have the same sign, so this does not hurt ; if there is an event at the very
            // beginning, g(t0) and g(t0+epsilon) have opposite signs and we want to start
            // with the second one. Of course, the sign of epsilon depend on the integration
            // direction (forward or backward). This explains what is done below.

            final double ignoreZone = interpolator.isForward() ? getConvergence() : -getConvergence();
            t0 = interpolator.getPreviousTime() + ignoreZone;
            interpolator.setInterpolatedTime(t0);
            g0 = handler.g(t0, interpolator.getInterpolatedState());
            if (g0 == 0) {
                // extremely rare case: there is a zero EXACTLY at end of ignore zone
                // we will use the opposite of sign at step beginning to force ignoring this zero
                final double tStart = interpolator.getPreviousTime();
                interpolator.setInterpolatedTime(tStart);
                g0Positive = handler.g(tStart, interpolator.getInterpolatedState()) <= 0;
            } else {
                g0Positive = g0 >= 0;
            }

        } catch (DerivativeException mue) {
            throw new EventException(mue);
        }
    }

    /** Evaluate the impact of the proposed step on the event handler.
     * @param interpolator step interpolator for the proposed step
     * @return true if the event handler triggers an event before
     * the end of the proposed step
     * @exception DerivativeException if the interpolator fails to
     * compute the switching function somewhere within the step
     * @exception EventException if the switching function
     * cannot be evaluated
     * @exception ConvergenceException if an event cannot be located
     */
    public boolean evaluateStep(final StepInterpolator interpolator)
        throws DerivativeException, EventException, ConvergenceException {

        try {

            forward = interpolator.isForward();
            final double t1 = interpolator.getCurrentTime();
            if (FastMath.abs(t1 - t0) < convergence) {
                // we cannot do anything on such a small step, don't trigger any events
                return false;
            }
            final double start = forward ? (t0 + convergence) : t0 - convergence;
            final double dt    = t1 - start;
            final int    n     = FastMath.max(1, (int) FastMath.ceil(FastMath.abs(dt) / maxCheckInterval));
            final double h     = dt / n;

            double ta = t0;
            double ga = g0;
            for (int i = 0; i < n; ++i) {

                // evaluate handler value at the end of the substep
                final double tb = start + (i + 1) * h;
                interpolator.setInterpolatedTime(tb);
                final double gb = handler.g(tb, interpolator.getInterpolatedState());

                // check events occurrence
                if (g0Positive ^ (gb >= 0)) {
                    // there is a sign change: an event is expected during this step

                    // variation direction, with respect to the integration direction
                    increasing = gb >= ga;

                    final UnivariateRealFunction f = new UnivariateRealFunction() {
                        public double value(final double t) {
                            try {
                                interpolator.setInterpolatedTime(t);
                                return handler.g(t, interpolator.getInterpolatedState());
                            } catch (DerivativeException e) {
                                throw new EmbeddedDerivativeException(e);
                            } catch (EventException e) {
                                throw new EmbeddedEventException(e);
                            }
                        }
                    };
                    final BrentSolver solver = new BrentSolver(convergence);

                    if (ga * gb >= 0) {
                        // this is a corner case:
                        // - there was an event near ta,
                        // - there is another event between ta and tb
                        // - when ta was computed, convergence was reached on the "wrong side" of the interval
                        // this implies that the real sign of ga is the same as gb, so we need to slightly
                        // shift ta to make sure ga and gb get opposite signs and the solver won't complain
                        // about bracketing
                        final double epsilon = (forward ? 0.25 : -0.25) * convergence;
                        for (int k = 0; (k < 4) && (ga * gb > 0); ++k) {
                            ta += epsilon;
                            try {
                                ga = f.value(ta);
                            } catch (FunctionEvaluationException ex) {
                                throw new DerivativeException(ex);
                            }
                        }
                        if (ga * gb > 0) {
                            // this should never happen
                            throw new MathInternalError();
                        }
                    }

                    final double root;
                    try {
                        root = (ta <= tb) ?
                                solver.solve(maxIterationCount, f, ta, tb) :
                                    solver.solve(maxIterationCount, f, tb, ta);
                    } catch (FunctionEvaluationException ex) {
                        throw new DerivativeException(ex);
                    }

                    if ((!Double.isNaN(previousEventTime)) &&
                        (FastMath.abs(root - ta) <= convergence) &&
                        (FastMath.abs(root - previousEventTime) <= convergence)) {
                        // we have either found nothing or found (again ?) a past event, we simply ignore it
                        ta = tb;
                        ga = gb;
                    } else if (Double.isNaN(previousEventTime) ||
                               (FastMath.abs(previousEventTime - root) > convergence)) {
                        pendingEventTime = root;
                        pendingEvent = true;
                        return true;
                    } else {
                        // no sign change: there is no event for now
                        ta = tb;
                        ga = gb;
                    }

                } else {
                    // no sign change: there is no event for now
                    ta = tb;
                    ga = gb;
                }

            }

            // no event during the whole step
            pendingEvent     = false;
            pendingEventTime = Double.NaN;
            return false;

        } catch (EmbeddedDerivativeException ede) {
            throw ede.getDerivativeException();
        } catch (EmbeddedEventException eee) {
            throw eee.getEventException();
        }

    }

    /** Get the occurrence time of the event triggered in the current step.
     * @return occurrence time of the event triggered in the current
     * step or positive infinity if no events are triggered
     */
    public double getEventTime() {
        return pendingEvent ? pendingEventTime : Double.POSITIVE_INFINITY;
    }

    /** Acknowledge the fact the step has been accepted by the integrator.
     * @param t value of the independent <i>time</i> variable at the
     * end of the step
     * @param y array containing the current value of the state vector
     * at the end of the step
     * @exception EventException if the value of the event
     * handler cannot be evaluated
     */
    public void stepAccepted(final double t, final double[] y)
        throws EventException {

        t0 = t;
        g0 = handler.g(t, y);

        if (pendingEvent && (FastMath.abs(pendingEventTime - t) <= convergence)) {
            // force the sign to its value "just after the event"
            previousEventTime = t;
            g0Positive        = increasing;
            nextAction        = handler.eventOccurred(t, y, !(increasing ^ forward));
        } else {
            g0Positive = g0 >= 0;
            nextAction = EventHandler.CONTINUE;
        }
    }

    /** Check if the integration should be stopped at the end of the
     * current step.
     * @return true if the integration should be stopped
     */
    public boolean stop() {
        return nextAction == EventHandler.STOP;
    }

    /** Let the event handler reset the state if it wants.
     * @param t value of the independent <i>time</i> variable at the
     * beginning of the next step
     * @param y array were to put the desired state vector at the beginning
     * of the next step
     * @return true if the integrator should reset the derivatives too
     * @exception EventException if the state cannot be reseted by the event
     * handler
     */
    public boolean reset(final double t, final double[] y)
        throws EventException {

        if (!(pendingEvent && (FastMath.abs(pendingEventTime - t) <= convergence))) {
            return false;
        }

        if (nextAction == EventHandler.RESET_STATE) {
            handler.resetState(t, y);
        }
        pendingEvent      = false;
        pendingEventTime  = Double.NaN;

        return (nextAction == EventHandler.RESET_STATE) ||
               (nextAction == EventHandler.RESET_DERIVATIVES);

    }

    /** Local exception for embedding DerivativeException. */
    private static class EmbeddedDerivativeException extends RuntimeException {

        /** Serializable UID. */
        private static final long serialVersionUID = 3574188382434584610L;

        /** Embedded exception. */
        private final DerivativeException derivativeException;

        /** Simple constructor.
         * @param derivativeException embedded exception
         */
        public EmbeddedDerivativeException(final DerivativeException derivativeException) {
            this.derivativeException = derivativeException;
        }

        /** Get the embedded exception.
         * @return embedded exception
         */
        public DerivativeException getDerivativeException() {
            return derivativeException;
        }

    }

    /** Local exception for embedding EventException. */
    private static class EmbeddedEventException extends RuntimeException {

        /** Serializable UID. */
        private static final long serialVersionUID = -1337749250090455474L;

        /** Embedded exception. */
        private final EventException eventException;

        /** Simple constructor.
         * @param eventException embedded exception
         */
        public EmbeddedEventException(final EventException eventException) {
            this.eventException = eventException;
        }

        /** Get the embedded exception.
         * @return embedded exception
         */
        public EventException getEventException() {
            return eventException;
        }

    }
}

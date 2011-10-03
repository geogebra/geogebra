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

package org.apache.commons.math.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.events.CombinedEventsManager;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.events.EventState;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/**
 * Base class managing common boilerplate for all integrators.
 * @version $Revision: 1073267 $ $Date: 2011-02-22 10:06:20 +0100 (mar. 22 févr. 2011) $
 * @since 2.0
 */
public abstract class AbstractIntegrator implements FirstOrderIntegrator {

    /** Step handler. */
    protected Collection<StepHandler> stepHandlers;

    /** Current step start time. */
    protected double stepStart;

    /** Current stepsize. */
    protected double stepSize;

    /** Indicator for last step. */
    protected boolean isLastStep;

    /** Indicator that a state or derivative reset was triggered by some event. */
    protected boolean resetOccurred;

    /** Events states. */
    private Collection<EventState> eventsStates;

    /** Initialization indicator of events states. */
    private boolean statesInitialized;

    /** Name of the method. */
    private final String name;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of evaluations already performed. */
    private int evaluations;

    /** Differential equations to integrate. */
    private transient FirstOrderDifferentialEquations equations;

    /** Build an instance.
     * @param name name of the method
     */
    public AbstractIntegrator(final String name) {
        this.name = name;
        stepHandlers = new ArrayList<StepHandler>();
        stepStart = Double.NaN;
        stepSize  = Double.NaN;
        eventsStates = new ArrayList<EventState>();
        statesInitialized = false;
        setMaxEvaluations(-1);
        resetEvaluations();
    }

    /** Build an instance with a null name.
     */
    protected AbstractIntegrator() {
        this(null);
    }

    /** {@inheritDoc} */
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void addStepHandler(final StepHandler handler) {
        stepHandlers.add(handler);
    }

    /** {@inheritDoc} */
    public Collection<StepHandler> getStepHandlers() {
        return Collections.unmodifiableCollection(stepHandlers);
    }

    /** {@inheritDoc} */
    public void clearStepHandlers() {
        stepHandlers.clear();
    }

    /** {@inheritDoc} */
    public void addEventHandler(final EventHandler handler,
                                final double maxCheckInterval,
                                final double convergence,
                                final int maxIterationCount) {
        eventsStates.add(new EventState(handler, maxCheckInterval, convergence, maxIterationCount));
    }

    /** {@inheritDoc} */
    public Collection<EventHandler> getEventHandlers() {
        final List<EventHandler> list = new ArrayList<EventHandler>();
        for (EventState state : eventsStates) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection(list);
    }

    /** {@inheritDoc} */
    public void clearEventHandlers() {
        eventsStates.clear();
    }

    /** Check if dense output is needed.
     * @return true if there is at least one event handler or if
     * one of the step handlers requires dense output
     */
    protected boolean requiresDenseOutput() {
        if (!eventsStates.isEmpty()) {
            return true;
        }
        for (StepHandler handler : stepHandlers) {
            if (handler.requiresDenseOutput()) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public double getCurrentStepStart() {
        return stepStart;
    }

    /** {@inheritDoc} */
    public double getCurrentSignedStepsize() {
        return stepSize;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = (maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations;
    }

    /** Reset the number of evaluations to zero.
     */
    protected void resetEvaluations() {
        evaluations = 0;
    }

    /** Set the differential equations.
     * @param equations differential equations to integrate
     * @see #computeDerivatives(double, double[], double[])
     */
    protected void setEquations(final FirstOrderDifferentialEquations equations) {
        this.equations = equations;
    }

    /** Compute the derivatives and check the number of evaluations.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the state vector
     * @param yDot placeholder array where to put the time derivative of the state vector
     * @throws DerivativeException this user-defined exception should be used if an error is
     * is triggered by user code
     */
    public void computeDerivatives(final double t, final double[] y, final double[] yDot)
        throws DerivativeException {
        if (++evaluations > maxEvaluations) {
            throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
        }
        equations.computeDerivatives(t, y, yDot);
    }

    /** Set the stateInitialized flag.
     * <p>This method must be called by integrators with the value
     * {@code false} before they start integration, so a proper lazy
     * initialization is done automatically on the first step.</p>
     * @param stateInitialized new value for the flag
     * @since 2.2
     */
    protected void setStateInitialized(final boolean stateInitialized) {
        this.statesInitialized = stateInitialized;
    }

    /** Accept a step, triggering events and step handlers.
     * @param interpolator step interpolator
     * @param y state vector at step end time, must be reset if an event
     * asks for resetting or if an events stops integration during the step
     * @param yDot placeholder array where to put the time derivative of the state vector
     * @param tEnd final integration time
     * @return time at end of step
     * @throws DerivativeException this exception is propagated to the caller if
     * the underlying user function triggers one
     * @exception IntegratorException if the value of one event state cannot be evaluated
     * @since 2.2
     */
    protected double acceptStep(final AbstractStepInterpolator interpolator,
                                final double[] y, final double[] yDot, final double tEnd)
        throws DerivativeException, IntegratorException {

        try {
            double previousT = interpolator.getGlobalPreviousTime();
            final double currentT = interpolator.getGlobalCurrentTime();
            resetOccurred = false;

            // initialize the events states if needed
            if (! statesInitialized) {
                for (EventState state : eventsStates) {
                    state.reinitializeBegin(interpolator);
                }
                statesInitialized = true;
            }

            // search for next events that may occur during the step
            final int orderingSign = interpolator.isForward() ? +1 : -1;
            SortedSet<EventState> occuringEvents = new TreeSet<EventState>(new Comparator<EventState>() {

                /** {@inheritDoc} */
                public int compare(EventState es0, EventState es1) {
                    return orderingSign * Double.compare(es0.getEventTime(), es1.getEventTime());
                }

            });

            for (final EventState state : eventsStates) {
                if (state.evaluateStep(interpolator)) {
                    // the event occurs during the current step
                    occuringEvents.add(state);
                }
            }

            while (!occuringEvents.isEmpty()) {

                // handle the chronologically first event
                final Iterator<EventState> iterator = occuringEvents.iterator();
                final EventState currentEvent = iterator.next();
                iterator.remove();

                // restrict the interpolator to the first part of the step, up to the event
                final double eventT = currentEvent.getEventTime();
                interpolator.setSoftPreviousTime(previousT);
                interpolator.setSoftCurrentTime(eventT);

                // trigger the event
                interpolator.setInterpolatedTime(eventT);
                final double[] eventY = interpolator.getInterpolatedState();
                currentEvent.stepAccepted(eventT, eventY);
                isLastStep = currentEvent.stop();

                // handle the first part of the step, up to the event
                for (final StepHandler handler : stepHandlers) {
                    handler.handleStep(interpolator, isLastStep);
                }

                if (isLastStep) {
                    // the event asked to stop integration
                    System.arraycopy(eventY, 0, y, 0, y.length);
                    return eventT;
                }

                if (currentEvent.reset(eventT, eventY)) {
                    // some event handler has triggered changes that
                    // invalidate the derivatives, we need to recompute them
                    System.arraycopy(eventY, 0, y, 0, y.length);
                    computeDerivatives(eventT, y, yDot);
                    resetOccurred = true;
                    return eventT;
                }

                // prepare handling of the remaining part of the step
                previousT = eventT;
                interpolator.setSoftPreviousTime(eventT);
                interpolator.setSoftCurrentTime(currentT);

                // check if the same event occurs again in the remaining part of the step
                if (currentEvent.evaluateStep(interpolator)) {
                    // the event occurs during the current step
                    occuringEvents.add(currentEvent);
                }

            }

            interpolator.setInterpolatedTime(currentT);
            final double[] currentY = interpolator.getInterpolatedState();
            for (final EventState state : eventsStates) {
                state.stepAccepted(currentT, currentY);
                isLastStep = isLastStep || state.stop();
            }
            isLastStep = isLastStep || MathUtils.equals(currentT, tEnd, 1);

            // handle the remaining part of the step, after all events if any
            for (StepHandler handler : stepHandlers) {
                handler.handleStep(interpolator, isLastStep);
            }

            return currentT;
        } catch (EventException se) {
            final Throwable cause = se.getCause();
            if ((cause != null) && (cause instanceof DerivativeException)) {
                throw (DerivativeException) cause;
            }
            throw new IntegratorException(se);
        } catch (ConvergenceException ce) {
            throw new IntegratorException(ce);
        }

    }

    /** Perform some sanity checks on the integration parameters.
     * @param ode differential equations set
     * @param t0 start time
     * @param y0 state vector at t0
     * @param t target time for the integration
     * @param y placeholder where to put the state vector
     * @exception IntegratorException if some inconsistency is detected
     */
    protected void sanityChecks(final FirstOrderDifferentialEquations ode,
                                final double t0, final double[] y0,
                                final double t, final double[] y)
        throws IntegratorException {

        if (ode.getDimension() != y0.length) {
            throw new IntegratorException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, ode.getDimension(), y0.length);
        }

        if (ode.getDimension() != y.length) {
            throw new IntegratorException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, ode.getDimension(), y.length);
        }

        if (FastMath.abs(t - t0) <= 1.0e-12 * FastMath.max(FastMath.abs(t0), FastMath.abs(t))) {
            throw new IntegratorException(
                    LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL,
                    FastMath.abs(t - t0));
        }

    }

    /** Add an event handler for end time checking.
     * <p>This method can be used to simplify handling of integration end time.
     * It leverages the nominal stop condition with the exceptional stop
     * conditions.</p>
     * @param startTime integration start time
     * @param endTime desired end time
     * @param manager manager containing the user-defined handlers
     * @return a new manager containing all the user-defined handlers plus a
     * dedicated manager triggering a stop event at entTime
     * @deprecated as of 2.2, this method is not used any more
     */
    @Deprecated
    protected CombinedEventsManager addEndTimeChecker(final double startTime,
                                                      final double endTime,
                                                      final CombinedEventsManager manager) {
        CombinedEventsManager newManager = new CombinedEventsManager();
        for (final EventState state : manager.getEventsStates()) {
            newManager.addEventHandler(state.getEventHandler(),
                                       state.getMaxCheckInterval(),
                                       state.getConvergence(),
                                       state.getMaxIterationCount());
        }
        newManager.addEventHandler(new EndTimeChecker(endTime),
                                   Double.POSITIVE_INFINITY,
                                   FastMath.ulp(FastMath.max(FastMath.abs(startTime), FastMath.abs(endTime))),
                                   100);
        return newManager;
    }

    /** Specialized event handler to stop integration.
     * @deprecated as of 2.2, this class is not used anymore
     */
    @Deprecated
    private static class EndTimeChecker implements EventHandler {

        /** Desired end time. */
        private final double endTime;

        /** Build an instance.
         * @param endTime desired time
         */
        public EndTimeChecker(final double endTime) {
            this.endTime = endTime;
        }

        /** {@inheritDoc} */
        public int eventOccurred(double t, double[] y, boolean increasing) {
            return STOP;
        }

        /** {@inheritDoc} */
        public double g(double t, double[] y) {
            return t - endTime;
        }

        /** {@inheritDoc} */
        public void resetState(double t, double[] y) {
        }

    }

}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/** This class manages several {@link EventHandler event handlers} during integration.
 *
 * @see EventHandler
 * @see EventState
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 * @deprecated as of 2.2, this class is not used anymore
 */
@Deprecated
public class CombinedEventsManager {

    /** Events states. */
    private final List<EventState> states;

    /** First active event. */
    private EventState first;

    /** Initialization indicator. */
    private boolean initialized;

    /** Simple constructor.
     * Create an empty manager
     */
    public CombinedEventsManager() {
        states      = new ArrayList<EventState>();
        first       = null;
        initialized = false;
    }

    /** Add an events handler.
     * @param handler event handler
     * @param maxCheckInterval maximal time interval between events
     * checks (this interval prevents missing sign changes in
     * case the integration steps becomes very large)
     * @param convergence convergence threshold in the event time search
     * @param maxIterationCount upper limit of the iteration count in
     * the event time search
     * @see #getEventsHandlers()
     * @see #clearEventsHandlers()
     */
    public void addEventHandler(final EventHandler handler, final double maxCheckInterval,
                                final double convergence, final int maxIterationCount) {
        states.add(new EventState(handler, maxCheckInterval,
                                  convergence, maxIterationCount));
    }

    /** Get all the events handlers that have been added to the manager.
     * @return an unmodifiable collection of the added event handlers
     * @see #addEventHandler(EventHandler, double, double, int)
     * @see #clearEventsHandlers()
     * @see #getEventsStates()
     */
    public Collection<EventHandler> getEventsHandlers() {
        final List<EventHandler> list = new ArrayList<EventHandler>();
        for (EventState state : states) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection(list);
    }

    /** Remove all the events handlers that have been added to the manager.
     * @see #addEventHandler(EventHandler, double, double, int)
     * @see #getEventsHandlers()
     */
    public void clearEventsHandlers() {
        states.clear();
    }

    /** Get all the events state wrapping the handlers that have been added to the manager.
     * @return a collection of the events states
     * @see #getEventsHandlers()
     */
    public Collection<EventState> getEventsStates() {
        return states;
    }

    /** Check if the manager does not manage any event handlers.
     * @return true if manager is empty
     */
    public boolean isEmpty() {
        return states.isEmpty();
    }

    /** Evaluate the impact of the proposed step on all managed
     * event handlers.
     * @param interpolator step interpolator for the proposed step
     * @return true if at least one event handler triggers an event
     * before the end of the proposed step (this implies the step should
     * be rejected)
     * @exception DerivativeException if the interpolator fails to
     * compute the function somewhere within the step
     * @exception IntegratorException if an event cannot be located
     */
    public boolean evaluateStep(final StepInterpolator interpolator)
    throws DerivativeException, IntegratorException {

        try {

            first = null;
            if (states.isEmpty()) {
                // there is nothing to do, return now to avoid setting the
                // interpolator time (and hence avoid unneeded calls to the
                // user function due to interpolator finalization)
                return false;
            }

            if (! initialized) {

                // initialize the events states
                for (EventState state : states) {
                    state.reinitializeBegin(interpolator);
                }

                initialized = true;

            }

            // check events occurrence
            for (EventState state : states) {

                if (state.evaluateStep(interpolator)) {
                    if (first == null) {
                        first = state;
                    } else {
                        if (interpolator.isForward()) {
                            if (state.getEventTime() < first.getEventTime()) {
                                first = state;
                            }
                        } else {
                            if (state.getEventTime() > first.getEventTime()) {
                                first = state;
                            }
                        }
                    }
                }

            }

            return first != null;

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

    /** Get the occurrence time of the first event triggered in the
     * last evaluated step.
     * @return occurrence time of the first event triggered in the last
     * evaluated step, or </code>Double.NaN</code> if no event is
     * triggered
     */
    public double getEventTime() {
        return (first == null) ? Double.NaN : first.getEventTime();
    }

    /** Inform the event handlers that the step has been accepted
     * by the integrator.
     * @param t value of the independent <i>time</i> variable at the
     * end of the step
     * @param y array containing the current value of the state vector
     * at the end of the step
     * @exception IntegratorException if the value of one of the
     * events states cannot be evaluated
     */
    public void stepAccepted(final double t, final double[] y)
    throws IntegratorException {
        try {
            for (EventState state : states) {
                state.stepAccepted(t, y);
            }
        } catch (EventException se) {
            throw new IntegratorException(se);
        }
    }

    /** Check if the integration should be stopped at the end of the
     * current step.
     * @return true if the integration should be stopped
     */
    public boolean stop() {
        for (EventState state : states) {
            if (state.stop()) {
                return true;
            }
        }
        return false;
    }

    /** Let the event handlers reset the state if they want.
     * @param t value of the independent <i>time</i> variable at the
     * beginning of the next step
     * @param y array were to put the desired state vector at the beginning
     * of the next step
     * @return true if the integrator should reset the derivatives too
     * @exception IntegratorException if one of the events states
     * that should reset the state fails to do it
     */
    public boolean reset(final double t, final double[] y)
        throws IntegratorException {
        try {
            boolean resetDerivatives = false;
            for (EventState state : states) {
                if (state.reset(t, y)) {
                    resetDerivatives = true;
                }
            }
            return resetDerivatives;
        } catch (EventException se) {
            throw new IntegratorException(se);
        }
    }

}

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

package org.apache.commons.math.ode.sampling;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.util.FastMath;

/**
 * This class wraps an object implementing {@link FixedStepHandler}
 * into a {@link StepHandler}.

 * <p>This wrapper allows to use fixed step handlers with general
 * integrators which cannot guaranty their integration steps will
 * remain constant and therefore only accept general step
 * handlers.</p>
 *
 * <p>The stepsize used is selected at construction time. The {@link
 * FixedStepHandler#handleStep handleStep} method of the underlying
 * {@link FixedStepHandler} object is called at the beginning time of
 * the integration t0 and also at times t0+h, t0+2h, ... If the
 * integration range is an integer multiple of the stepsize, then the
 * last point handled will be the endpoint of the integration tend, if
 * not, the last point will belong to the interval [tend - h ;
 * tend].</p>
 *
 * <p>There is no constraint on the integrator, it can use any
 * timestep it needs (time steps longer or shorter than the fixed time
 * step and non-integer ratios are all allowed).</p>
 *
 * @see StepHandler
 * @see FixedStepHandler
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 */

public class StepNormalizer implements StepHandler {

    /** Fixed time step. */
    private double h;

    /** Underlying step handler. */
    private final FixedStepHandler handler;

    /** Last step time. */
    private double lastTime;

    /** Last State vector. */
    private double[] lastState;

    /** Last Derivatives vector. */
    private double[] lastDerivatives;

    /** Integration direction indicator. */
    private boolean forward;

    /** Simple constructor.
     * @param h fixed time step (sign is not used)
     * @param handler fixed time step handler to wrap
     */
    public StepNormalizer(final double h, final FixedStepHandler handler) {
        this.h       = FastMath.abs(h);
        this.handler = handler;
        reset();
    }

    /** Determines whether this handler needs dense output.
     * This handler needs dense output in order to provide data at
     * regularly spaced steps regardless of the steps the integrator
     * uses, so this method always returns true.
     * @return always true
     */
    public boolean requiresDenseOutput() {
        return true;
    }

    /** Reset the step handler.
     * Initialize the internal data as required before the first step is
     * handled.
     */
    public void reset() {
        lastTime        = Double.NaN;
        lastState       = null;
        lastDerivatives = null;
        forward         = true;
    }

    /**
     * Handle the last accepted step
     * @param interpolator interpolator for the last accepted step. For
     * efficiency purposes, the various integrators reuse the same
     * object on each call, so if the instance wants to keep it across
     * all calls (for example to provide at the end of the integration a
     * continuous model valid throughout the integration range), it
     * should build a local copy using the clone method and store this
     * copy.
     * @param isLast true if the step is the last one
     * @throws DerivativeException this exception is propagated to the
     * caller if the underlying user function triggers one
     */
    public void handleStep(final StepInterpolator interpolator, final boolean isLast)
        throws DerivativeException {

        if (lastState == null) {

            lastTime = interpolator.getPreviousTime();
            interpolator.setInterpolatedTime(lastTime);
            lastState = interpolator.getInterpolatedState().clone();
            lastDerivatives = interpolator.getInterpolatedDerivatives().clone();

            // take the integration direction into account
            forward = interpolator.getCurrentTime() >= lastTime;
            if (! forward) {
                h = -h;
            }

        }

        double nextTime = lastTime + h;
        boolean nextInStep = forward ^ (nextTime > interpolator.getCurrentTime());
        while (nextInStep) {

            // output the stored previous step
            handler.handleStep(lastTime, lastState, lastDerivatives, false);

            // store the next step
            lastTime = nextTime;
            interpolator.setInterpolatedTime(lastTime);
            System.arraycopy(interpolator.getInterpolatedState(), 0,
                             lastState, 0, lastState.length);
            System.arraycopy(interpolator.getInterpolatedDerivatives(), 0,
                             lastDerivatives, 0, lastDerivatives.length);

            nextTime  += h;
            nextInStep = forward ^ (nextTime > interpolator.getCurrentTime());

        }

        if (isLast) {
            // there will be no more steps,
            // the stored one should be flagged as being the last
            handler.handleStep(lastTime, lastState, lastDerivatives, true);
        }

    }

}

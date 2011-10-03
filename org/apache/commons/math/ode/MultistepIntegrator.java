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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.apache.commons.math.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;

/**
 * This class is the base class for multistep integrators for Ordinary
 * Differential Equations.
 * <p>We define scaled derivatives s<sub>i</sub>(n) at step n as:
 * <pre>
 * s<sub>1</sub>(n) = h y'<sub>n</sub> for first derivative
 * s<sub>2</sub>(n) = h<sup>2</sup>/2 y''<sub>n</sub> for second derivative
 * s<sub>3</sub>(n) = h<sup>3</sup>/6 y'''<sub>n</sub> for third derivative
 * ...
 * s<sub>k</sub>(n) = h<sup>k</sup>/k! y(k)<sub>n</sub> for k<sup>th</sup> derivative
 * </pre></p>
 * <p>Rather than storing several previous steps separately, this implementation uses
 * the Nordsieck vector with higher degrees scaled derivatives all taken at the same
 * step (y<sub>n</sub>, s<sub>1</sub>(n) and r<sub>n</sub>) where r<sub>n</sub> is defined as:
 * <pre>
 * r<sub>n</sub> = [ s<sub>2</sub>(n), s<sub>3</sub>(n) ... s<sub>k</sub>(n) ]<sup>T</sup>
 * </pre>
 * (we omit the k index in the notation for clarity)</p>
 * <p>
 * Multistep integrators with Nordsieck representation are highly sensitive to
 * large step changes because when the step is multiplied by a factor a, the
 * k<sup>th</sup> component of the Nordsieck vector is multiplied by a<sup>k</sup>
 * and the last components are the least accurate ones. The default max growth
 * factor is therefore set to a quite low value: 2<sup>1/order</sup>.
 * </p>
 *
 * @see org.apache.commons.math.ode.nonstiff.AdamsBashforthIntegrator
 * @see org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegrator
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.0
 */
public abstract class MultistepIntegrator extends AdaptiveStepsizeIntegrator {

    /** First scaled derivative (h y'). */
    protected double[] scaled;

    /** Nordsieck matrix of the higher scaled derivatives.
     * <p>(h<sup>2</sup>/2 y'', h<sup>3</sup>/6 y''' ..., h<sup>k</sup>/k! y(k))</p>
     */
    protected Array2DRowRealMatrix nordsieck;

    /** Starter integrator. */
    private FirstOrderIntegrator starter;

    /** Number of steps of the multistep method (excluding the one being computed). */
    private final int nSteps;

    /** Stepsize control exponent. */
    private double exp;

    /** Safety factor for stepsize control. */
    private double safety;

    /** Minimal reduction factor for stepsize control. */
    private double minReduction;

    /** Maximal growth factor for stepsize control. */
    private double maxGrowth;

    /**
     * Build a multistep integrator with the given stepsize bounds.
     * <p>The default starter integrator is set to the {@link
     * DormandPrince853Integrator Dormand-Prince 8(5,3)} integrator with
     * some defaults settings.</p>
     * <p>
     * The default max growth factor is set to a quite low value: 2<sup>1/order</sup>.
     * </p>
     * @param name name of the method
     * @param nSteps number of steps of the multistep method
     * (excluding the one being computed)
     * @param order order of the method
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param scalAbsoluteTolerance allowed absolute error
     * @param scalRelativeTolerance allowed relative error
     */
    protected MultistepIntegrator(final String name, final int nSteps,
                                  final int order,
                                  final double minStep, final double maxStep,
                                  final double scalAbsoluteTolerance,
                                  final double scalRelativeTolerance) {

        super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);

        if (nSteps <= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.INTEGRATION_METHOD_NEEDS_AT_LEAST_ONE_PREVIOUS_POINT,
                  name);
        }

        starter = new DormandPrince853Integrator(minStep, maxStep,
                                                 scalAbsoluteTolerance,
                                                 scalRelativeTolerance);
        this.nSteps = nSteps;

        exp = -1.0 / order;

        // set the default values of the algorithm control parameters
        setSafety(0.9);
        setMinReduction(0.2);
        setMaxGrowth(FastMath.pow(2.0, -exp));

    }

    /**
     * Build a multistep integrator with the given stepsize bounds.
     * <p>The default starter integrator is set to the {@link
     * DormandPrince853Integrator Dormand-Prince 8(5,3)} integrator with
     * some defaults settings.</p>
     * <p>
     * The default max growth factor is set to a quite low value: 2<sup>1/order</sup>.
     * </p>
     * @param name name of the method
     * @param nSteps number of steps of the multistep method
     * (excluding the one being computed)
     * @param order order of the method
     * @param minStep minimal step (must be positive even for backward
     * integration), the last step can be smaller than this
     * @param maxStep maximal step (must be positive even for backward
     * integration)
     * @param vecAbsoluteTolerance allowed absolute error
     * @param vecRelativeTolerance allowed relative error
     */
    protected MultistepIntegrator(final String name, final int nSteps,
                                  final int order,
                                  final double minStep, final double maxStep,
                                  final double[] vecAbsoluteTolerance,
                                  final double[] vecRelativeTolerance) {
        super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
        starter = new DormandPrince853Integrator(minStep, maxStep,
                                                 vecAbsoluteTolerance,
                                                 vecRelativeTolerance);
        this.nSteps = nSteps;

        exp = -1.0 / order;

        // set the default values of the algorithm control parameters
        setSafety(0.9);
        setMinReduction(0.2);
        setMaxGrowth(FastMath.pow(2.0, -exp));

    }

    /**
     * Get the starter integrator.
     * @return starter integrator
     */
    public ODEIntegrator getStarterIntegrator() {
        return starter;
    }

    /**
     * Set the starter integrator.
     * <p>The various step and event handlers for this starter integrator
     * will be managed automatically by the multi-step integrator. Any
     * user configuration for these elements will be cleared before use.</p>
     * @param starterIntegrator starter integrator
     */
    public void setStarterIntegrator(FirstOrderIntegrator starterIntegrator) {
        this.starter = starterIntegrator;
    }

    /** Start the integration.
     * <p>This method computes one step using the underlying starter integrator,
     * and initializes the Nordsieck vector at step start. The starter integrator
     * purpose is only to establish initial conditions, it does not really change
     * time by itself. The top level multistep integrator remains in charge of
     * handling time propagation and events handling as it will starts its own
     * computation right from the beginning. In a sense, the starter integrator
     * can be seen as a dummy one and so it will never trigger any user event nor
     * call any user step handler.</p>
     * @param t0 initial time
     * @param y0 initial value of the state vector at t0
     * @param t target time for the integration
     * (can be set to a value smaller than <code>t0</code> for backward integration)
     * @throws IntegratorException if the integrator cannot perform integration
     * @throws DerivativeException this exception is propagated to the caller if
     * the underlying user function triggers one
     */
    protected void start(final double t0, final double[] y0, final double t)
        throws DerivativeException, IntegratorException {

        // make sure NO user event nor user step handler is triggered,
        // this is the task of the top level integrator, not the task
        // of the starter integrator
        starter.clearEventHandlers();
        starter.clearStepHandlers();

        // set up one specific step handler to extract initial Nordsieck vector
        starter.addStepHandler(new NordsieckInitializer(y0.length));

        // start integration, expecting a InitializationCompletedMarkerException
        try {
            starter.integrate(new CountingDifferentialEquations(y0.length),
                              t0, y0, t, new double[y0.length]);
        } catch (DerivativeException mue) {
            if (!(mue instanceof InitializationCompletedMarkerException)) {
                // this is not the expected nominal interruption of the start integrator
                throw mue;
            }
        }

        // remove the specific step handler
        starter.clearStepHandlers();

    }

    /** Initialize the high order scaled derivatives at step start.
     * @param first first scaled derivative at step start
     * @param multistep scaled derivatives after step start (hy'1, ..., hy'k-1)
     * will be modified
     * @return high order scaled derivatives at step start
     */
    protected abstract Array2DRowRealMatrix initializeHighOrderDerivatives(final double[] first,
                                                                           final double[][] multistep);

    /** Get the minimal reduction factor for stepsize control.
     * @return minimal reduction factor
     */
    public double getMinReduction() {
        return minReduction;
    }

    /** Set the minimal reduction factor for stepsize control.
     * @param minReduction minimal reduction factor
     */
    public void setMinReduction(final double minReduction) {
        this.minReduction = minReduction;
    }

    /** Get the maximal growth factor for stepsize control.
     * @return maximal growth factor
     */
    public double getMaxGrowth() {
        return maxGrowth;
    }

    /** Set the maximal growth factor for stepsize control.
     * @param maxGrowth maximal growth factor
     */
    public void setMaxGrowth(final double maxGrowth) {
        this.maxGrowth = maxGrowth;
    }

    /** Get the safety factor for stepsize control.
     * @return safety factor
     */
    public double getSafety() {
      return safety;
    }

    /** Set the safety factor for stepsize control.
     * @param safety safety factor
     */
    public void setSafety(final double safety) {
      this.safety = safety;
    }

    /** Compute step grow/shrink factor according to normalized error.
     * @param error normalized error of the current step
     * @return grow/shrink factor for next step
     */
    protected double computeStepGrowShrinkFactor(final double error) {
        return FastMath.min(maxGrowth, FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
    }

    /** Transformer used to convert the first step to Nordsieck representation. */
    public static interface NordsieckTransformer {
        /** Initialize the high order scaled derivatives at step start.
         * @param first first scaled derivative at step start
         * @param multistep scaled derivatives after step start (hy'1, ..., hy'k-1)
         * will be modified
         * @return high order derivatives at step start
         */
        RealMatrix initializeHighOrderDerivatives(double[] first, double[][] multistep);
    }

    /** Specialized step handler storing the first step. */
    private class NordsieckInitializer implements StepHandler {

        /** Problem dimension. */
        private final int n;

        /** Simple constructor.
         * @param n problem dimension
         */
        public NordsieckInitializer(final int n) {
            this.n = n;
        }

        /** {@inheritDoc} */
        public void handleStep(StepInterpolator interpolator, boolean isLast)
            throws DerivativeException {

            final double prev = interpolator.getPreviousTime();
            final double curr = interpolator.getCurrentTime();
            stepStart = prev;
            stepSize  = (curr - prev) / (nSteps + 1);

            // compute the first scaled derivative
            interpolator.setInterpolatedTime(prev);
            scaled = interpolator.getInterpolatedDerivatives().clone();
            for (int j = 0; j < n; ++j) {
                scaled[j] *= stepSize;
            }

            // compute the high order scaled derivatives
            final double[][] multistep = new double[nSteps][];
            for (int i = 1; i <= nSteps; ++i) {
                interpolator.setInterpolatedTime(prev + stepSize * i);
                final double[] msI = interpolator.getInterpolatedDerivatives().clone();
                for (int j = 0; j < n; ++j) {
                    msI[j] *= stepSize;
                }
                multistep[i - 1] = msI;
            }
            nordsieck = initializeHighOrderDerivatives(scaled, multistep);

            // stop the integrator after the first step has been handled
            throw new InitializationCompletedMarkerException();

        }

        /** {@inheritDoc} */
        public boolean requiresDenseOutput() {
            return true;
        }

        /** {@inheritDoc} */
        public void reset() {
            // nothing to do
        }

    }

    /** Marker exception used ONLY to stop the starter integrator after first step. */
    private static class InitializationCompletedMarkerException
        extends DerivativeException {

        /** Serializable version identifier. */
        private static final long serialVersionUID = -4105805787353488365L;

        /** Simple constructor. */
        public InitializationCompletedMarkerException() {
            super((Throwable) null);
        }

    }

    /** Wrapper for differential equations, ensuring start evaluations are counted. */
    private class CountingDifferentialEquations implements ExtendedFirstOrderDifferentialEquations {

        /** Dimension of the problem. */
        private final int dimension;

        /** Simple constructor.
         * @param dimension dimension of the problem
         */
        public CountingDifferentialEquations(final int dimension) {
            this.dimension = dimension;
        }

        /** {@inheritDoc} */
        public void computeDerivatives(double t, double[] y, double[] dot)
                throws DerivativeException {
            MultistepIntegrator.this.computeDerivatives(t, y, dot);
        }

        /** {@inheritDoc} */
        public int getDimension() {
            return dimension;
        }

        /** {@inheritDoc} */
        public int getMainSetDimension() {
            return mainSetDimension;
        }
    }

}

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

package org.apache.commons.math.ode.jacobians;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ExtendedFirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.events.EventException;
import org.apache.commons.math.ode.events.EventHandler;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/** This class enhances a first order integrator for differential equations to
 * compute also partial derivatives of the solution with respect to initial state
 * and parameters.
 * <p>In order to compute both the state and its derivatives, the ODE problem
 * is extended with jacobians of the raw ODE and the variational equations are
 * added to form a new compound problem of higher dimension. If the original ODE
 * problem has dimension n and there are p parameters, the compound problem will
 * have dimension n &times; (1 + n + p).</p>
 * @see ParameterizedODE
 * @see ODEWithJacobians
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.1
 * @deprecated as of 2.2 the complete package is deprecated, it will be replaced
 * in 3.0 by a completely rewritten implementation
 */
@Deprecated
public class FirstOrderIntegratorWithJacobians {

    /** Underlying integrator for compound problem. */
    private final FirstOrderIntegrator integrator;

    /** Raw equations to integrate. */
    private final ODEWithJacobians ode;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of evaluations already performed. */
    private int evaluations;

    /** Build an enhanced integrator using internal differentiation to compute jacobians.
     * @param integrator underlying integrator to solve the compound problem
     * @param ode original problem (f in the equation y' = f(t, y))
     * @param p parameters array (may be null if {@link
     * ParameterizedODE#getParametersDimension()
     * getParametersDimension()} from original problem is zero)
     * @param hY step sizes to use for computing the jacobian df/dy, must have the
     * same dimension as the original problem
     * @param hP step sizes to use for computing the jacobian df/dp, must have the
     * same dimension as the original problem parameters dimension
     * @see #FirstOrderIntegratorWithJacobians(FirstOrderIntegrator,
     * ODEWithJacobians)
     */
    public FirstOrderIntegratorWithJacobians(final FirstOrderIntegrator integrator,
                                             final ParameterizedODE ode,
                                             final double[] p, final double[] hY, final double[] hP) {
        checkDimension(ode.getDimension(), hY);
        checkDimension(ode.getParametersDimension(), p);
        checkDimension(ode.getParametersDimension(), hP);
        this.integrator = integrator;
        this.ode = new FiniteDifferencesWrapper(ode, p, hY, hP);
        setMaxEvaluations(-1);
    }

    /** Build an enhanced integrator using ODE builtin jacobian computation features.
     * @param integrator underlying integrator to solve the compound problem
     * @param ode original problem, which can compute the jacobians by itself
     * @see #FirstOrderIntegratorWithJacobians(FirstOrderIntegrator,
     * ParameterizedODE, double[], double[], double[])
     */
    public FirstOrderIntegratorWithJacobians(final FirstOrderIntegrator integrator,
                                             final ODEWithJacobians ode) {
        this.integrator = integrator;
        this.ode = ode;
        setMaxEvaluations(-1);
    }

    /** Add a step handler to this integrator.
     * <p>The handler will be called by the integrator for each accepted
     * step.</p>
     * @param handler handler for the accepted steps
     * @see #getStepHandlers()
     * @see #clearStepHandlers()
     */
    public void addStepHandler(StepHandlerWithJacobians handler) {
        final int n = ode.getDimension();
        final int k = ode.getParametersDimension();
        integrator.addStepHandler(new StepHandlerWrapper(handler, n, k));
    }

    /** Get all the step handlers that have been added to the integrator.
     * @return an unmodifiable collection of the added events handlers
     * @see #addStepHandler(StepHandlerWithJacobians)
     * @see #clearStepHandlers()
     */
    public Collection<StepHandlerWithJacobians> getStepHandlers() {
        final Collection<StepHandlerWithJacobians> handlers =
            new ArrayList<StepHandlerWithJacobians>();
        for (final StepHandler handler : integrator.getStepHandlers()) {
            if (handler instanceof StepHandlerWrapper) {
                handlers.add(((StepHandlerWrapper) handler).getHandler());
            }
        }
        return handlers;
    }

    /** Remove all the step handlers that have been added to the integrator.
     * @see #addStepHandler(StepHandlerWithJacobians)
     * @see #getStepHandlers()
     */
    public void clearStepHandlers() {
        integrator.clearStepHandlers();
    }

    /** Add an event handler to the integrator.
     * @param handler event handler
     * @param maxCheckInterval maximal time interval between switching
     * function checks (this interval prevents missing sign changes in
     * case the integration steps becomes very large)
     * @param convergence convergence threshold in the event time search
     * @param maxIterationCount upper limit of the iteration count in
     * the event time search
     * @see #getEventHandlers()
     * @see #clearEventHandlers()
     */
    public void addEventHandler(EventHandlerWithJacobians handler,
                                double maxCheckInterval,
                                double convergence,
                                int maxIterationCount) {
        final int n = ode.getDimension();
        final int k = ode.getParametersDimension();
        integrator.addEventHandler(new EventHandlerWrapper(handler, n, k),
                                   maxCheckInterval, convergence, maxIterationCount);
    }

    /** Get all the event handlers that have been added to the integrator.
     * @return an unmodifiable collection of the added events handlers
     * @see #addEventHandler(EventHandlerWithJacobians, double, double, int)
     * @see #clearEventHandlers()
     */
    public Collection<EventHandlerWithJacobians> getEventHandlers() {
        final Collection<EventHandlerWithJacobians> handlers =
            new ArrayList<EventHandlerWithJacobians>();
        for (final EventHandler handler : integrator.getEventHandlers()) {
            if (handler instanceof EventHandlerWrapper) {
                handlers.add(((EventHandlerWrapper) handler).getHandler());
            }
        }
        return handlers;
    }

    /** Remove all the event handlers that have been added to the integrator.
     * @see #addEventHandler(EventHandlerWithJacobians, double, double, int)
     * @see #getEventHandlers()
     */
    public void clearEventHandlers() {
        integrator.clearEventHandlers();
    }

    /** Integrate the differential equations and the variational equations up to the given time.
     * <p>This method solves an Initial Value Problem (IVP) and also computes the derivatives
     * of the solution with respect to initial state and parameters. This can be used as
     * a basis to solve Boundary Value Problems (BVP).</p>
     * <p>Since this method stores some internal state variables made
     * available in its public interface during integration ({@link
     * #getCurrentSignedStepsize()}), it is <em>not</em> thread-safe.</p>
     * @param t0 initial time
     * @param y0 initial value of the state vector at t0
     * @param dY0dP initial value of the state vector derivative with respect to the
     * parameters at t0
     * @param t target time for the integration
     * (can be set to a value smaller than <code>t0</code> for backward integration)
     * @param y placeholder where to put the state vector at each successful
     *  step (and hence at the end of integration), can be the same object as y0
     * @param dYdY0 placeholder where to put the state vector derivative with respect
     * to the initial state (dy[i]/dy0[j] is in element array dYdY0[i][j]) at each successful
     *  step (and hence at the end of integration)
     * @param dYdP placeholder where to put the state vector derivative with respect
     * to the parameters (dy[i]/dp[j] is in element array dYdP[i][j]) at each successful
     *  step (and hence at the end of integration)
     * @return stop time, will be the same as target time if integration reached its
     * target, but may be different if some event handler stops it at some point.
     * @throws IntegratorException if the integrator cannot perform integration
     * @throws DerivativeException this exception is propagated to the caller if
     * the underlying user function triggers one
     */
    public double integrate(final double t0, final double[] y0, final double[][] dY0dP,
                            final double t, final double[] y,
                            final double[][] dYdY0, final double[][] dYdP)
        throws DerivativeException, IntegratorException {

        final int n = ode.getDimension();
        final int k = ode.getParametersDimension();
        checkDimension(n, y0);
        checkDimension(n, y);
        checkDimension(n, dYdY0);
        checkDimension(n, dYdY0[0]);
        if (k != 0) {
            checkDimension(n, dY0dP);
            checkDimension(k, dY0dP[0]);
            checkDimension(n, dYdP);
            checkDimension(k, dYdP[0]);
        }

        // set up initial state, including partial derivatives
        // the compound state z contains the raw state y and its derivatives
        // with respect to initial state y0 and to parameters p
        //    y[i]         is stored in z[i]
        //    dy[i]/dy0[j] is stored in z[n + i * n + j]
        //    dy[i]/dp[j]  is stored in z[n * (n + 1) + i * k + j]
        final double[] z = new double[n * (1 + n + k)];
        System.arraycopy(y0, 0, z, 0, n);
        for (int i = 0; i < n; ++i) {

            // set diagonal element of dy/dy0 to 1.0 at t = t0
            z[i * (1 + n) + n] = 1.0;

            // set initial derivatives with respect to parameters
            System.arraycopy(dY0dP[i], 0, z, n * (n + 1) + i * k, k);

        }

        // integrate the compound state variational equations
        evaluations = 0;
        final double stopTime = integrator.integrate(new MappingWrapper(), t0, z, t, z);

        // dispatch the final compound state into the state and partial derivatives arrays
        dispatchCompoundState(z, y, dYdY0, dYdP);

        return stopTime;

    }

    /** Dispatch a compound state array into state and jacobians arrays.
     * @param z compound state
     * @param y raw state array to fill
     * @param dydy0 jacobian array to fill
     * @param dydp jacobian array to fill
     */
    private static void dispatchCompoundState(final double[] z, final double[] y,
                                              final double[][] dydy0, final double[][] dydp) {

        final int n = y.length;
        final int k = dydp[0].length;

        // state
        System.arraycopy(z, 0, y, 0, n);

        // jacobian with respect to initial state
        for (int i = 0; i < n; ++i) {
            System.arraycopy(z, n * (i + 1), dydy0[i], 0, n);
        }

        // jacobian with respect to parameters
        for (int i = 0; i < n; ++i) {
            System.arraycopy(z, n * (n + 1) + i * k, dydp[i], 0, k);
        }

    }

    /** Get the current value of the step start time t<sub>i</sub>.
     * <p>This method can be called during integration (typically by
     * the object implementing the {@link org.apache.commons.math.ode.FirstOrderDifferentialEquations
     * differential equations} problem) if the value of the current step that
     * is attempted is needed.</p>
     * <p>The result is undefined if the method is called outside of
     * calls to <code>integrate</code>.</p>
     * @return current value of the step start time t<sub>i</sub>
     */
    public double getCurrentStepStart() {
        return integrator.getCurrentStepStart();
    }

    /** Get the current signed value of the integration stepsize.
     * <p>This method can be called during integration (typically by
     * the object implementing the {@link org.apache.commons.math.ode.FirstOrderDifferentialEquations
     * differential equations} problem) if the signed value of the current stepsize
     * that is tried is needed.</p>
     * <p>The result is undefined if the method is called outside of
     * calls to <code>integrate</code>.</p>
     * @return current signed value of the stepsize
     */
    public double getCurrentSignedStepsize() {
        return integrator.getCurrentSignedStepsize();
    }

    /** Set the maximal number of differential equations function evaluations.
     * <p>The purpose of this method is to avoid infinite loops which can occur
     * for example when stringent error constraints are set or when lots of
     * discrete events are triggered, thus leading to many rejected steps.</p>
     * @param maxEvaluations maximal number of function evaluations (negative
     * values are silently converted to maximal integer value, thus representing
     * almost unlimited evaluations)
     */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = (maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations;
    }

    /** Get the maximal number of functions evaluations.
     * @return maximal number of functions evaluations
     */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** Get the number of evaluations of the differential equations function.
     * <p>
     * The number of evaluations corresponds to the last call to the
     * <code>integrate</code> method. It is 0 if the method has not been called yet.
     * </p>
     * @return number of evaluations of the differential equations function
     */
    public int getEvaluations() {
        return evaluations;
    }

    /** Check array dimensions.
     * @param expected expected dimension
     * @param array (may be null if expected is 0)
     * @throws IllegalArgumentException if the array dimension does not match the expected one
     */
    private void checkDimension(final int expected, final Object array)
        throws IllegalArgumentException {
        int arrayDimension = (array == null) ? 0 : Array.getLength(array);
        if (arrayDimension != expected) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, arrayDimension, expected);
        }
    }

    /** Wrapper class used to map state and jacobians into compound state. */
    private class MappingWrapper implements  ExtendedFirstOrderDifferentialEquations {

        /** Current state. */
        private final double[]   y;

        /** Time derivative of the current state. */
        private final double[]   yDot;

        /** Derivatives of yDot with respect to state. */
        private final double[][] dFdY;

        /** Derivatives of yDot with respect to parameters. */
        private final double[][] dFdP;

        /** Simple constructor.
         */
        public MappingWrapper() {

            final int n = ode.getDimension();
            final int k = ode.getParametersDimension();
            y    = new double[n];
            yDot = new double[n];
            dFdY = new double[n][n];
            dFdP = new double[n][k];

        }

        /** {@inheritDoc} */
        public int getDimension() {
            final int n = y.length;
            final int k = dFdP[0].length;
            return n * (1 + n + k);
        }

        /** {@inheritDoc} */
        public int getMainSetDimension() {
            return ode.getDimension();
        }

        /** {@inheritDoc} */
        public void computeDerivatives(final double t, final double[] z, final double[] zDot)
            throws DerivativeException {

            final int n = y.length;
            final int k = dFdP[0].length;

            // compute raw ODE and its jacobians: dy/dt, d[dy/dt]/dy0 and d[dy/dt]/dp
            System.arraycopy(z,    0, y,    0, n);
            if (++evaluations > maxEvaluations) {
                throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
            }
            ode.computeDerivatives(t, y, yDot);
            ode.computeJacobians(t, y, yDot, dFdY, dFdP);

            // state part of the compound equations
            System.arraycopy(yDot, 0, zDot, 0, n);

            // variational equations: from d[dy/dt]/dy0 to d[dy/dy0]/dt
            for (int i = 0; i < n; ++i) {
                final double[] dFdYi = dFdY[i];
                for (int j = 0; j < n; ++j) {
                    double s = 0;
                    final int startIndex = n + j;
                    int zIndex = startIndex;
                    for (int l = 0; l < n; ++l) {
                        s += dFdYi[l] * z[zIndex];
                        zIndex += n;
                    }
                    zDot[startIndex + i * n] = s;
                }
            }

            // variational equations: from d[dy/dt]/dy0 and d[dy/dt]/dp to d[dy/dp]/dt
            for (int i = 0; i < n; ++i) {
                final double[] dFdYi = dFdY[i];
                final double[] dFdPi = dFdP[i];
                for (int j = 0; j < k; ++j) {
                    double s = dFdPi[j];
                    final int startIndex = n * (n + 1) + j;
                    int zIndex = startIndex;
                    for (int l = 0; l < n; ++l) {
                        s += dFdYi[l] * z[zIndex];
                        zIndex += k;
                    }
                    zDot[startIndex + i * k] = s;
                }
            }

        }

    }

    /** Wrapper class to compute jacobians by finite differences for ODE which do not compute them themselves. */
    private class FiniteDifferencesWrapper implements ODEWithJacobians {

        /** Raw ODE without jacobians computation. */
        private final ParameterizedODE ode;

        /** Parameters array (may be null if parameters dimension from original problem is zero) */
        private final double[] p;

        /** Step sizes to use for computing the jacobian df/dy. */
        private final double[] hY;

        /** Step sizes to use for computing the jacobian df/dp. */
        private final double[] hP;

        /** Temporary array for state derivatives used to compute jacobians. */
        private final double[] tmpDot;

        /** Simple constructor.
         * @param ode original ODE problem, without jacobians computations
         * @param p parameters array (may be null if parameters dimension from original problem is zero)
         * @param hY step sizes to use for computing the jacobian df/dy
         * @param hP step sizes to use for computing the jacobian df/dp
         */
        public FiniteDifferencesWrapper(final ParameterizedODE ode,
                                        final double[] p, final double[] hY, final double[] hP) {
            this.ode = ode;
            this.p  = p.clone();
            this.hY = hY.clone();
            this.hP = hP.clone();
            tmpDot = new double[ode.getDimension()];
        }

        /** {@inheritDoc} */
        public int getDimension() {
            return ode.getDimension();
        }

        /** {@inheritDoc} */
        public void computeDerivatives(double t, double[] y, double[] yDot) throws DerivativeException {
            // this call to computeDerivatives has already been counted,
            // we must not increment the counter again
            ode.computeDerivatives(t, y, yDot);
        }

        /** {@inheritDoc} */
        public int getParametersDimension() {
            return ode.getParametersDimension();
        }

        /** {@inheritDoc} */
        public void computeJacobians(double t, double[] y, double[] yDot,
                                     double[][] dFdY, double[][] dFdP)
            throws DerivativeException {

            final int n = hY.length;
            final int k = hP.length;

            evaluations += n + k;
            if (evaluations > maxEvaluations) {
                throw new DerivativeException(new MaxEvaluationsExceededException(maxEvaluations));
            }

            // compute df/dy where f is the ODE and y is the state array
            for (int j = 0; j < n; ++j) {
                final double savedYj = y[j];
                y[j] += hY[j];
                ode.computeDerivatives(t, y, tmpDot);
                for (int i = 0; i < n; ++i) {
                    dFdY[i][j] = (tmpDot[i] - yDot[i]) / hY[j];
                }
                y[j] = savedYj;
            }

            // compute df/dp where f is the ODE and p is the parameters array
            for (int j = 0; j < k; ++j) {
                ode.setParameter(j, p[j] +  hP[j]);
                ode.computeDerivatives(t, y, tmpDot);
                for (int i = 0; i < n; ++i) {
                    dFdP[i][j] = (tmpDot[i] - yDot[i]) / hP[j];
                }
                ode.setParameter(j, p[j]);
            }

        }

    }

    /** Wrapper for step handlers. */
    private static class StepHandlerWrapper implements StepHandler {

        /** Underlying step handler with jacobians. */
        private final StepHandlerWithJacobians handler;

        /** Dimension of the original ODE. */
        private final int n;

        /** Number of parameters. */
        private final int k;

        /** Simple constructor.
         * @param handler underlying step handler with jacobians
         * @param n dimension of the original ODE
         * @param k number of parameters
         */
        public StepHandlerWrapper(final StepHandlerWithJacobians handler,
                                  final int n, final int k) {
            this.handler = handler;
            this.n       = n;
            this.k       = k;
        }

        /** Get the underlying step handler with jacobians.
         * @return underlying step handler with jacobians
         */
        public StepHandlerWithJacobians getHandler() {
            return handler;
        }

        /** {@inheritDoc} */
        public void handleStep(StepInterpolator interpolator, boolean isLast)
            throws DerivativeException {
            handler.handleStep(new StepInterpolatorWrapper(interpolator, n, k), isLast);
        }

        /** {@inheritDoc} */
        public boolean requiresDenseOutput() {
            return handler.requiresDenseOutput();
        }

        /** {@inheritDoc} */
        public void reset() {
            handler.reset();
        }

    }

    /** Wrapper for step interpolators. */
    private static class StepInterpolatorWrapper
        implements StepInterpolatorWithJacobians {

        /** Wrapped interpolator. */
        private StepInterpolator interpolator;

        /** State array. */
        private double[] y;

        /** Jacobian with respect to initial state dy/dy0. */
        private double[][] dydy0;

        /** Jacobian with respect to parameters dy/dp. */
        private double[][] dydp;

        /** Time derivative of the state array. */
        private double[] yDot;

        /** Time derivative of the sacobian with respect to initial state dy/dy0. */
        private double[][] dydy0Dot;

        /** Time derivative of the jacobian with respect to parameters dy/dp. */
        private double[][] dydpDot;

        /** Simple constructor.
         * <p>This constructor is used only for externalization. It does nothing.</p>
         */
        @SuppressWarnings("unused")
        public StepInterpolatorWrapper() {
        }

        /** Simple constructor.
         * @param interpolator wrapped interpolator
         * @param n dimension of the original ODE
         * @param k number of parameters
         */
        public StepInterpolatorWrapper(final StepInterpolator interpolator,
                                       final int n, final int k) {
            this.interpolator = interpolator;
            y        = new double[n];
            dydy0    = new double[n][n];
            dydp     = new double[n][k];
            yDot     = new double[n];
            dydy0Dot = new double[n][n];
            dydpDot  = new double[n][k];
        }

        /** {@inheritDoc} */
        public void setInterpolatedTime(double time) {
            interpolator.setInterpolatedTime(time);
        }

        /** {@inheritDoc} */
        public boolean isForward() {
            return interpolator.isForward();
        }

        /** {@inheritDoc} */
        public double getPreviousTime() {
            return interpolator.getPreviousTime();
        }

        /** {@inheritDoc} */
        public double getInterpolatedTime() {
            return interpolator.getInterpolatedTime();
        }

        /** {@inheritDoc} */
        public double[] getInterpolatedY() throws DerivativeException {
            double[] extendedState = interpolator.getInterpolatedState();
            System.arraycopy(extendedState, 0, y, 0, y.length);
            return y;
        }

        /** {@inheritDoc} */
        public double[][] getInterpolatedDyDy0() throws DerivativeException {
            double[] extendedState = interpolator.getInterpolatedState();
            final int n = y.length;
            int start = n;
            for (int i = 0; i < n; ++i) {
                System.arraycopy(extendedState, start, dydy0[i], 0, n);
                start += n;
            }
            return dydy0;
        }

        /** {@inheritDoc} */
        public double[][] getInterpolatedDyDp() throws DerivativeException {
            double[] extendedState = interpolator.getInterpolatedState();
            final int n = y.length;
            final int k = dydp[0].length;
            int start = n * (n + 1);
            for (int i = 0; i < n; ++i) {
                System.arraycopy(extendedState, start, dydp[i], 0, k);
                start += k;
            }
            return dydp;
        }

        /** {@inheritDoc} */
        public double[] getInterpolatedYDot() throws DerivativeException {
            double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
            System.arraycopy(extendedDerivatives, 0, yDot, 0, yDot.length);
            return yDot;
        }

        /** {@inheritDoc} */
        public double[][] getInterpolatedDyDy0Dot() throws DerivativeException {
            double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
            final int n = y.length;
            int start = n;
            for (int i = 0; i < n; ++i) {
                System.arraycopy(extendedDerivatives, start, dydy0Dot[i], 0, n);
                start += n;
            }
            return dydy0Dot;
        }

        /** {@inheritDoc} */
        public double[][] getInterpolatedDyDpDot() throws DerivativeException {
            double[] extendedDerivatives = interpolator.getInterpolatedDerivatives();
            final int n = y.length;
            final int k = dydpDot[0].length;
            int start = n * (n + 1);
            for (int i = 0; i < n; ++i) {
                System.arraycopy(extendedDerivatives, start, dydpDot[i], 0, k);
                start += k;
            }
            return dydpDot;
        }

        /** {@inheritDoc} */
        public double getCurrentTime() {
            return interpolator.getCurrentTime();
        }

        /** {@inheritDoc} */
        public StepInterpolatorWithJacobians copy() throws DerivativeException {
            final int n = y.length;
            final int k = dydp[0].length;
            StepInterpolatorWrapper copied =
                new StepInterpolatorWrapper(interpolator.copy(), n, k);
            copyArray(y,        copied.y);
            copyArray(dydy0,    copied.dydy0);
            copyArray(dydp,     copied.dydp);
            copyArray(yDot,     copied.yDot);
            copyArray(dydy0Dot, copied.dydy0Dot);
            copyArray(dydpDot,  copied.dydpDot);
            return copied;
        }

        /** {@inheritDoc} */
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(interpolator);
            out.writeInt(y.length);
            out.writeInt(dydp[0].length);
            writeArray(out, y);
            writeArray(out, dydy0);
            writeArray(out, dydp);
            writeArray(out, yDot);
            writeArray(out, dydy0Dot);
            writeArray(out, dydpDot);
        }

        /** {@inheritDoc} */
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            interpolator = (StepInterpolator) in.readObject();
            final int n = in.readInt();
            final int k = in.readInt();
            y        = new double[n];
            dydy0    = new double[n][n];
            dydp     = new double[n][k];
            yDot     = new double[n];
            dydy0Dot = new double[n][n];
            dydpDot  = new double[n][k];
            readArray(in, y);
            readArray(in, dydy0);
            readArray(in, dydp);
            readArray(in, yDot);
            readArray(in, dydy0Dot);
            readArray(in, dydpDot);
        }

        /** Copy an array.
         * @param src source array
         * @param dest destination array
         */
        private static void copyArray(final double[] src, final double[] dest) {
            System.arraycopy(src, 0, dest, 0, src.length);
        }

        /** Copy an array.
         * @param src source array
         * @param dest destination array
         */
        private static void copyArray(final double[][] src, final double[][] dest) {
            for (int i = 0; i < src.length; ++i) {
                copyArray(src[i], dest[i]);
            }
        }

        /** Write an array.
         * @param out output stream
         * @param array array to write
         * @exception IOException if array cannot be read
         */
        private static void writeArray(final ObjectOutput out, final double[] array)
            throws IOException {
            for (int i = 0; i < array.length; ++i) {
                out.writeDouble(array[i]);
            }
        }

        /** Write an array.
         * @param out output stream
         * @param array array to write
         * @exception IOException if array cannot be read
         */
        private static void writeArray(final ObjectOutput out, final double[][] array)
            throws IOException {
            for (int i = 0; i < array.length; ++i) {
                writeArray(out, array[i]);
            }
        }

        /** Read an array.
         * @param in input stream
         * @param array array to read
         * @exception IOException if array cannot be read
         */
        private static void readArray(final ObjectInput in, final double[] array)
            throws IOException {
            for (int i = 0; i < array.length; ++i) {
                array[i] = in.readDouble();
            }
        }

        /** Read an array.
         * @param in input stream
         * @param array array to read
         * @exception IOException if array cannot be read
         */
        private static void readArray(final ObjectInput in, final double[][] array)
            throws IOException {
            for (int i = 0; i < array.length; ++i) {
                readArray(in, array[i]);
            }
        }

    }

    /** Wrapper for event handlers. */
    private static class EventHandlerWrapper implements EventHandler {

        /** Underlying event handler with jacobians. */
        private final EventHandlerWithJacobians handler;

        /** State array. */
        private double[] y;

        /** Jacobian with respect to initial state dy/dy0. */
        private double[][] dydy0;

        /** Jacobian with respect to parameters dy/dp. */
        private double[][] dydp;

        /** Simple constructor.
         * @param handler underlying event handler with jacobians
         * @param n dimension of the original ODE
         * @param k number of parameters
         */
        public EventHandlerWrapper(final EventHandlerWithJacobians handler,
                                   final int n, final int k) {
            this.handler = handler;
            y        = new double[n];
            dydy0    = new double[n][n];
            dydp     = new double[n][k];
        }

        /** Get the underlying event handler with jacobians.
         * @return underlying event handler with jacobians
         */
        public EventHandlerWithJacobians getHandler() {
            return handler;
        }

        /** {@inheritDoc} */
        public int eventOccurred(double t, double[] z, boolean increasing)
            throws EventException {
            dispatchCompoundState(z, y, dydy0, dydp);
            return handler.eventOccurred(t, y, dydy0, dydp, increasing);
        }

        /** {@inheritDoc} */
        public double g(double t, double[] z)
            throws EventException {
            dispatchCompoundState(z, y, dydy0, dydp);
            return handler.g(t, y, dydy0, dydp);
        }

        /** {@inheritDoc} */
        public void resetState(double t, double[] z)
            throws EventException {
            dispatchCompoundState(z, y, dydy0, dydp);
            handler.resetState(t, y, dydy0, dydp);
        }

    }

}

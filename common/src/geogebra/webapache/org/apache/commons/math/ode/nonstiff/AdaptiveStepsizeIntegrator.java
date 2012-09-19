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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.ode.nonstiff;

import geogebra.common.util.Cloner;

//NOTIMPORTANT import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.AbstractIntegrator;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.ExtendedFirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
//MAYBENOTIMPORTANT import org.apache.commons.math.util.FastMath;

/**
 * This abstract class holds the common part of all adaptive
 * stepsize integrators for Ordinary Differential Equations.
 *
 * <p>These algorithms perform integration with stepsize control, which
 * means the user does not specify the integration step but rather a
 * tolerance on error. The error threshold is computed as
 * <pre>
 * threshold_i = absTol_i + relTol_i * max (abs (ym), abs (ym+1))
 * </pre>
 * where absTol_i is the absolute tolerance for component i of the
 * state vector and relTol_i is the relative tolerance for the same
 * component. The user can also use only two scalar values absTol and
 * relTol which will be used for all components.
 * </p>
 *
 * <p>If the Ordinary Differential Equations is an {@link ExtendedFirstOrderDifferentialEquations
 * extended ODE} rather than a {@link FirstOrderDifferentialEquations basic ODE},
 * then <em>only</em> the {@link ExtendedFirstOrderDifferentialEquations#getMainSetDimension()
 * main set} part of the state vector is used for stepsize control, not the complete
 * state vector.
 * </p>
 *
 * <p>If the estimated error for ym+1 is such that
 * <pre>
 * sqrt((sum (errEst_i / threshold_i)^2 ) / n) < 1
 * </pre>
 *
 * (where n is the main set dimension) then the step is accepted,
 * otherwise the step is rejected and a new attempt is made with a new
 * stepsize.</p>
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 *
 */

public abstract class AdaptiveStepsizeIntegrator
  extends AbstractIntegrator {

    /** Allowed absolute scalar error. */
    protected final double scalAbsoluteTolerance;

    /** Allowed relative scalar error. */
    protected final double scalRelativeTolerance;

    /** Allowed absolute vectorial error. */
    protected final double[] vecAbsoluteTolerance;

    /** Allowed relative vectorial error. */
    protected final double[] vecRelativeTolerance;

    /** Main set dimension. */
    protected int mainSetDimension;

    /** User supplied initial step. */
    private double initialStep;

    /** Minimal step. */
    private final double minStep;

    /** Maximal step. */
    private final double maxStep;

  /** Build an integrator with the given stepsize bounds.
   * The default step handler does nothing.
   * @param name name of the method
   * @param minStep minimal step (must be positive even for backward
   * integration), the last step can be smaller than this
   * @param maxStep maximal step (must be positive even for backward
   * integration)
   * @param scalAbsoluteTolerance allowed absolute error
   * @param scalRelativeTolerance allowed relative error
   */
  public AdaptiveStepsizeIntegrator(final String name,
                                    final double minStep, final double maxStep,
                                    final double scalAbsoluteTolerance,
                                    final double scalRelativeTolerance) {

    super(name);

    this.minStep     = /*Fast*/
    		Math.abs(minStep);
    this.maxStep     = /*Fast*/
    		Math.abs(maxStep);
    this.initialStep = -1.0;

    this.scalAbsoluteTolerance = scalAbsoluteTolerance;
    this.scalRelativeTolerance = scalRelativeTolerance;
    this.vecAbsoluteTolerance  = null;
    this.vecRelativeTolerance  = null;

    resetInternalState();

  }

  /** Build an integrator with the given stepsize bounds.
   * The default step handler does nothing.
   * @param name name of the method
   * @param minStep minimal step (must be positive even for backward
   * integration), the last step can be smaller than this
   * @param maxStep maximal step (must be positive even for backward
   * integration)
   * @param vecAbsoluteTolerance allowed absolute error
   * @param vecRelativeTolerance allowed relative error
   */
  public AdaptiveStepsizeIntegrator(final String name,
                                    final double minStep, final double maxStep,
                                    final double[] vecAbsoluteTolerance,
                                    final double[] vecRelativeTolerance) {

    super(name);

    this.minStep     = minStep;
    this.maxStep     = maxStep;
    this.initialStep = -1.0;

    this.scalAbsoluteTolerance = 0;
    this.scalRelativeTolerance = 0;
    this.vecAbsoluteTolerance  = Cloner.clone(vecAbsoluteTolerance);
    this.vecRelativeTolerance  = Cloner.clone(vecRelativeTolerance);

    resetInternalState();

  }

  /** Set the initial step size.
   * <p>This method allows the user to specify an initial positive
   * step size instead of letting the integrator guess it by
   * itself. If this method is not called before integration is
   * started, the initial step size will be estimated by the
   * integrator.</p>
   * @param initialStepSize initial step size to use (must be positive even
   * for backward integration ; providing a negative value or a value
   * outside of the min/max step interval will lead the integrator to
   * ignore the value and compute the initial step size by itself)
   */
  public void setInitialStepSize(final double initialStepSize) {
    if ((initialStepSize < minStep) || (initialStepSize > maxStep)) {
      initialStep = -1.0;
    } else {
      initialStep = initialStepSize;
    }
  }

  /** Perform some sanity checks on the integration parameters.
   * @param equations differential equations set
   * @param t0 start time
   * @param y0 state vector at t0
   * @param t target time for the integration
   * @param y placeholder where to put the state vector
   * @exception IntegratorException if some inconsistency is detected
   */
  @Override
  protected void sanityChecks(final FirstOrderDifferentialEquations equations,
                              final double t0, final double[] y0,
                              final double t, final double[] y)
      throws IntegratorException {

      super.sanityChecks(equations, t0, y0, t, y);

      if (equations instanceof ExtendedFirstOrderDifferentialEquations) {
          mainSetDimension = ((ExtendedFirstOrderDifferentialEquations) equations).getMainSetDimension();
      } else {
          mainSetDimension = equations.getDimension();
      }

      if ((vecAbsoluteTolerance != null) && (vecAbsoluteTolerance.length != mainSetDimension)) {
          throw new IntegratorException("IntegratorException",
                  //NOTIMPORTANT LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE,
                  mainSetDimension, vecAbsoluteTolerance.length);
      }

      if ((vecRelativeTolerance != null) && (vecRelativeTolerance.length != mainSetDimension)) {
          throw new IntegratorException("IntegratorException",
                  //NOTIMPORTANT LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE,
                  mainSetDimension, vecRelativeTolerance.length);
      }

  }

  /** Initialize the integration step.
   * @param equations differential equations set
   * @param forward forward integration indicator
   * @param order order of the method
   * @param scale scaling vector for the state vector (can be shorter than state vector)
   * @param t0 start time
   * @param y0 state vector at t0
   * @param yDot0 first time derivative of y0
   * @param y1 work array for a state vector
   * @param yDot1 work array for the first time derivative of y1
   * @return first integration step
   * @exception DerivativeException this exception is propagated to
   * the caller if the underlying user function triggers one
   */
  public double initializeStep(final FirstOrderDifferentialEquations equations,
                               final boolean forward, final int order, final double[] scale,
                               final double t0, final double[] y0, final double[] yDot0,
                               final double[] y1, final double[] yDot1)
      throws DerivativeException {

    if (initialStep > 0) {
      // use the user provided value
      return forward ? initialStep : -initialStep;
    }

    // very rough first guess : h = 0.01 * ||y/scale|| / ||y'/scale||
    // this guess will be used to perform an Euler step
    double ratio;
    double yOnScale2 = 0;
    double yDotOnScale2 = 0;
    for (int j = 0; j < scale.length; ++j) {
      ratio         = y0[j] / scale[j];
      yOnScale2    += ratio * ratio;
      ratio         = yDot0[j] / scale[j];
      yDotOnScale2 += ratio * ratio;
    }

    double h = ((yOnScale2 < 1.0e-10) || (yDotOnScale2 < 1.0e-10)) ?
               1.0e-6 : (0.01 * /*Fast*/
            		   Math.sqrt(yOnScale2 / yDotOnScale2));
    if (! forward) {
      h = -h;
    }

    // perform an Euler step using the preceding rough guess
    for (int j = 0; j < y0.length; ++j) {
      y1[j] = y0[j] + h * yDot0[j];
    }
    computeDerivatives(t0 + h, y1, yDot1);

    // estimate the second derivative of the solution
    double yDDotOnScale = 0;
    for (int j = 0; j < scale.length; ++j) {
      ratio         = (yDot1[j] - yDot0[j]) / scale[j];
      yDDotOnScale += ratio * ratio;
    }
    yDDotOnScale = /*Fast*/
    		Math.sqrt(yDDotOnScale) / h;

    // step size is computed such that
    // h^order * max (||y'/tol||, ||y''/tol||) = 0.01
    final double maxInv2 = /*Fast*/
    		Math.max(/*Fast*/
    				Math.sqrt(yDotOnScale2), yDDotOnScale);
    final double h1 = (maxInv2 < 1.0e-15) ?
                      /*Fast*/
    		Math.max(1.0e-6, 0.001 * /*Fast*/
    				Math.abs(h)) :
                      /*Fast*/
    					Math.pow(0.01 / maxInv2, 1.0 / order);
    h = /*Fast*/
    		Math.min(100.0 * /*Fast*/
    				Math.abs(h), h1);
    h = /*Fast*/
    		Math.max(h, 1.0e-12 * /*Fast*/
    				Math.abs(t0));  // avoids cancellation when computing t1 - t0
    if (h < getMinStep()) {
      h = getMinStep();
    }
    if (h > getMaxStep()) {
      h = getMaxStep();
    }
    if (! forward) {
      h = -h;
    }

    return h;

  }

  /** Filter the integration step.
   * @param h signed step
   * @param forward forward integration indicator
   * @param acceptSmall if true, steps smaller than the minimal value
   * are silently increased up to this value, if false such small
   * steps generate an exception
   * @return a bounded integration step (h if no bound is reach, or a bounded value)
   * @exception IntegratorException if the step is too small and acceptSmall is false
   */
  protected double filterStep(final double h, final boolean forward, final boolean acceptSmall)
    throws IntegratorException {

      double filteredH = h;
      if (/*Fast*/
    		  Math.abs(h) < minStep) {
          if (acceptSmall) {
              filteredH = forward ? minStep : -minStep;
          } else {
              throw new IntegratorException("IntegratorException",
                      //NOTIMPORTANT LocalizedFormats.MINIMAL_STEPSIZE_REACHED_DURING_INTEGRATION,
                      minStep, /*Fast*/
                      Math.abs(h));
          }
      }

      if (filteredH > maxStep) {
          filteredH = maxStep;
      } else if (filteredH < -maxStep) {
          filteredH = -maxStep;
      }

      return filteredH;

  }

  /** {@inheritDoc} */
  public abstract double integrate (FirstOrderDifferentialEquations equations,
                                    double t0, double[] y0,
                                    double t, double[] y)
    throws DerivativeException, IntegratorException;

  /** {@inheritDoc} */
  @Override
  public double getCurrentStepStart() {
    return stepStart;
  }

  /** Reset internal state to dummy values. */
  protected void resetInternalState() {
    stepStart = Double.NaN;
    stepSize  = /*Fast*/
    		Math.sqrt(minStep * maxStep);
  }

  /** Get the minimal step.
   * @return minimal step
   */
  public double getMinStep() {
    return minStep;
  }

  /** Get the maximal step.
   * @return maximal step
   */
  public double getMaxStep() {
    return maxStep;
  }

}

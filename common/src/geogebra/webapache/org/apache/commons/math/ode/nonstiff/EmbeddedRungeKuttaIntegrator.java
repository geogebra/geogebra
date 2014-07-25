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

package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math.ode.sampling.DummyStepInterpolator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.util.FastMath;

/**
 * This class implements the common part of all embedded Runge-Kutta
 * integrators for Ordinary Differential Equations.
 *
 * <p>These methods are embedded explicit Runge-Kutta methods with two
 * sets of coefficients allowing to estimate the error, their Butcher
 * arrays are as follows :
 * <pre>
 *    0  |
 *   c2  | a21
 *   c3  | a31  a32
 *   ... |        ...
 *   cs  | as1  as2  ...  ass-1
 *       |--------------------------
 *       |  b1   b2  ...   bs-1  bs
 *       |  b'1  b'2 ...   b's-1 b's
 * </pre>
 * </p>
 *
 * <p>In fact, we rather use the array defined by ej = bj - b'j to
 * compute directly the error rather than computing two estimates and
 * then comparing them.</p>
 *
 * <p>Some methods are qualified as <i>fsal</i> (first same as last)
 * methods. This means the last evaluation of the derivatives in one
 * step is the same as the first in the next step. Then, this
 * evaluation can be reused from one step to the next one and the cost
 * of such a method is really s-1 evaluations despite the method still
 * has s stages. This behaviour is true only for successful steps, if
 * the step is rejected after the error estimation phase, no
 * evaluation is saved. For an <i>fsal</i> method, we have cs = 1 and
 * asi = bi for all i.</p>
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 */

public abstract class EmbeddedRungeKuttaIntegrator
  extends AdaptiveStepsizeIntegrator {

    /** Indicator for <i>fsal</i> methods. */
    private final boolean fsal;

    /** Time steps from Butcher array (without the first zero). */
    private final double[] c;

    /** Internal weights from Butcher array (without the first empty row). */
    private final double[][] a;

    /** External weights for the high order method from Butcher array. */
    private final double[] b;

    /** Prototype of the step interpolator. */
    private final RungeKuttaStepInterpolator prototype;

    /** Stepsize control exponent. */
    private final double exp;

    /** Safety factor for stepsize control. */
    private double safety;

    /** Minimal reduction factor for stepsize control. */
    private double minReduction;

    /** Maximal growth factor for stepsize control. */
    private double maxGrowth;

  /** Build a Runge-Kutta integrator with the given Butcher array.
   * @param name name of the method
   * @param fsal indicate that the method is an <i>fsal</i>
   * @param c time steps from Butcher array (without the first zero)
   * @param a internal weights from Butcher array (without the first empty row)
   * @param b propagation weights for the high order method from Butcher array
   * @param prototype prototype of the step interpolator to use
   * @param minStep minimal step (must be positive even for backward
   * integration), the last step can be smaller than this
   * @param maxStep maximal step (must be positive even for backward
   * integration)
   * @param scalAbsoluteTolerance allowed absolute error
   * @param scalRelativeTolerance allowed relative error
   */
  protected EmbeddedRungeKuttaIntegrator(final String name, final boolean fsal,
                                         final double[] c, final double[][] a, final double[] b,
                                         final RungeKuttaStepInterpolator prototype,
                                         final double minStep, final double maxStep,
                                         final double scalAbsoluteTolerance,
                                         final double scalRelativeTolerance) {

    super(name, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);

    this.fsal      = fsal;
    this.c         = c;
    this.a         = a;
    this.b         = b;
    this.prototype = prototype;

    exp = -1.0 / getOrder();

    // set the default values of the algorithm control parameters
    setSafety(0.9);
    setMinReduction(0.2);
    setMaxGrowth(10.0);

  }

  /** Build a Runge-Kutta integrator with the given Butcher array.
   * @param name name of the method
   * @param fsal indicate that the method is an <i>fsal</i>
   * @param c time steps from Butcher array (without the first zero)
   * @param a internal weights from Butcher array (without the first empty row)
   * @param b propagation weights for the high order method from Butcher array
   * @param prototype prototype of the step interpolator to use
   * @param minStep minimal step (must be positive even for backward
   * integration), the last step can be smaller than this
   * @param maxStep maximal step (must be positive even for backward
   * integration)
   * @param vecAbsoluteTolerance allowed absolute error
   * @param vecRelativeTolerance allowed relative error
   */
  protected EmbeddedRungeKuttaIntegrator(final String name, final boolean fsal,
                                         final double[] c, final double[][] a, final double[] b,
                                         final RungeKuttaStepInterpolator prototype,
                                         final double   minStep, final double maxStep,
                                         final double[] vecAbsoluteTolerance,
                                         final double[] vecRelativeTolerance) {

    super(name, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);

    this.fsal      = fsal;
    this.c         = c;
    this.a         = a;
    this.b         = b;
    this.prototype = prototype;

    exp = -1.0 / getOrder();

    // set the default values of the algorithm control parameters
    setSafety(0.9);
    setMinReduction(0.2);
    setMaxGrowth(10.0);

  }

  /** Get the order of the method.
   * @return order of the method
   */
  public abstract int getOrder();

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

  /** {@inheritDoc} */
  @Override
  public double integrate(final FirstOrderDifferentialEquations equations,
                          final double t0, final double[] y0,
                          final double t, final double[] y)
  throws DerivativeException, IntegratorException {

    sanityChecks(equations, t0, y0, t, y);
    setEquations(equations);
    resetEvaluations();
    final boolean forward = t > t0;

    // create some internal working arrays
    final int stages = c.length + 1;
    if (y != y0) {
      System.arraycopy(y0, 0, y, 0, y0.length);
    }
    final double[][] yDotK = new double[stages][y0.length];
    final double[] yTmp    = new double[y0.length];
    final double[] yDotTmp = new double[y0.length];

    // set up an interpolator sharing the integrator arrays
    AbstractStepInterpolator interpolator;
    if (requiresDenseOutput()) {
      final RungeKuttaStepInterpolator rki = (RungeKuttaStepInterpolator) prototype.copy();
      rki.reinitialize(this, yTmp, yDotK, forward);
      interpolator = rki;
    } else {
      interpolator = new DummyStepInterpolator(yTmp, yDotK[stages - 1], forward);
    }
    interpolator.storeTime(t0);

    // set up integration control objects
    stepStart         = t0;
    double  hNew      = 0;
    boolean firstTime = true;
    for (StepHandler handler : stepHandlers) {
        handler.reset();
    }
    setStateInitialized(false);

    // main integration loop
    isLastStep = false;
    do {

      interpolator.shift();

      // iterate over step size, ensuring local normalized error is smaller than 1
      double error = 10;
      while (error >= 1.0) {

        if (firstTime || !fsal) {
          // first stage
          computeDerivatives(stepStart, y, yDotK[0]);
        }

        if (firstTime) {
          final double[] scale = new double[mainSetDimension];
          if (vecAbsoluteTolerance == null) {
              for (int i = 0; i < scale.length; ++i) {
                scale[i] = scalAbsoluteTolerance + scalRelativeTolerance * FastMath.abs(y[i]);
              }
          } else {
              for (int i = 0; i < scale.length; ++i) {
                scale[i] = vecAbsoluteTolerance[i] + vecRelativeTolerance[i] * FastMath.abs(y[i]);
              }
          }
          hNew = initializeStep(equations, forward, getOrder(), scale,
                                stepStart, y, yDotK[0], yTmp, yDotK[1]);
          firstTime = false;
        }

        stepSize = hNew;

        // next stages
        for (int k = 1; k < stages; ++k) {

          for (int j = 0; j < y0.length; ++j) {
            double sum = a[k-1][0] * yDotK[0][j];
            for (int l = 1; l < k; ++l) {
              sum += a[k-1][l] * yDotK[l][j];
            }
            yTmp[j] = y[j] + stepSize * sum;
          }

          computeDerivatives(stepStart + c[k-1] * stepSize, yTmp, yDotK[k]);

        }

        // estimate the state at the end of the step
        for (int j = 0; j < y0.length; ++j) {
          double sum    = b[0] * yDotK[0][j];
          for (int l = 1; l < stages; ++l) {
            sum    += b[l] * yDotK[l][j];
          }
          yTmp[j] = y[j] + stepSize * sum;
        }

        // estimate the error at the end of the step
        error = estimateError(yDotK, y, yTmp, stepSize);
        if (error >= 1.0) {
          // reject the step and attempt to reduce error by stepsize control
          final double factor =
              FastMath.min(maxGrowth,
                           FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
          hNew = filterStep(stepSize * factor, forward, false);
        }

      }

      // local error is small enough: accept the step, trigger events and step handlers
      interpolator.storeTime(stepStart + stepSize);
      System.arraycopy(yTmp, 0, y, 0, y0.length);
      System.arraycopy(yDotK[stages - 1], 0, yDotTmp, 0, y0.length);
      stepStart = acceptStep(interpolator, y, yDotTmp, t);

      if (!isLastStep) {

          // prepare next step
          interpolator.storeTime(stepStart);

          if (fsal) {
              // save the last evaluation for the next step
              System.arraycopy(yDotTmp, 0, yDotK[0], 0, y0.length);
          }

          // stepsize control for next step
          final double factor =
              FastMath.min(maxGrowth, FastMath.max(minReduction, safety * FastMath.pow(error, exp)));
          final double  scaledH    = stepSize * factor;
          final double  nextT      = stepStart + scaledH;
          final boolean nextIsLast = forward ? (nextT >= t) : (nextT <= t);
          hNew = filterStep(scaledH, forward, nextIsLast);

          final double  filteredNextT      = stepStart + hNew;
          final boolean filteredNextIsLast = forward ? (filteredNextT >= t) : (filteredNextT <= t);
          if (filteredNextIsLast) {
              hNew = t - stepStart;
          }

      }

    } while (!isLastStep);

    final double stopTime = stepStart;
    resetInternalState();
    return stopTime;

  }

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

  /** Compute the error ratio.
   * @param yDotK derivatives computed during the first stages
   * @param y0 estimate of the step at the start of the step
   * @param y1 estimate of the step at the end of the step
   * @param h  current step
   * @return error ratio, greater than 1 if step should be rejected
   */
  protected abstract double estimateError(double[][] yDotK,
                                          double[] y0, double[] y1,
                                          double h);

}

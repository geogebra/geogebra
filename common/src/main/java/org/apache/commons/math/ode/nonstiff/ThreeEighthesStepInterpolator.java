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
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**
 * This class implements a step interpolator for the 3/8 fourth
 * order Runge-Kutta integrator.
 *
 * <p>This interpolator allows to compute dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 *
 * <pre>
 *   y(t_n + theta h) = y (t_n + h)
 *                    - (1 - theta) (h/8) [ (1 - 7 theta + 8 theta^2) y'_1
 *                                      + 3 (1 +   theta - 4 theta^2) y'_2
 *                                      + 3 (1 +   theta)             y'_3
 *                                      +   (1 +   theta + 4 theta^2) y'_4
 *                                        ]
 * </pre>
 *
 * where theta belongs to [0 ; 1] and where y'_1 to y'_4 are the four
 * evaluations of the derivatives already computed during the
 * step.</p>
 *
 * @see ThreeEighthesIntegrator
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 */

class ThreeEighthesStepInterpolator
  extends RungeKuttaStepInterpolator {

    /** Serializable version identifier */
    private static final long serialVersionUID = -3345024435978721931L;

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link
   * org.apache.commons.math.ode.sampling.AbstractStepInterpolator#reinitialize}
   * method should be called before using the instance in order to
   * initialize the internal arrays. This constructor is used only
   * in order to delay the initialization in some cases. The {@link
   * RungeKuttaIntegrator} class uses the prototyping design pattern
   * to create the step interpolators by cloning an uninitialized model
   * and later initializing the copy.
   */
  public ThreeEighthesStepInterpolator() {
  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public ThreeEighthesStepInterpolator(final ThreeEighthesStepInterpolator interpolator) {
    super(interpolator);
  }

  /** {@inheritDoc} */
  @Override
  protected StepInterpolator doCopy() {
    return new ThreeEighthesStepInterpolator(this);
  }


  /** {@inheritDoc} */
  @Override
  protected void computeInterpolatedStateAndDerivatives(final double theta,
                                          final double oneMinusThetaH)
      throws DerivativeException {

      final double fourTheta2 = 4 * theta * theta;
      final double s          = oneMinusThetaH / 8.0;
      final double coeff1     = s * (1 - 7 * theta + 2 * fourTheta2);
      final double coeff2     = 3 * s * (1 + theta - fourTheta2);
      final double coeff3     = 3 * s * (1 + theta);
      final double coeff4     = s * (1 + theta + fourTheta2);
      final double coeffDot3  = 0.75 * theta;
      final double coeffDot1  = coeffDot3 * (4 * theta - 5) + 1;
      final double coeffDot2  = coeffDot3 * (5 - 6 * theta);
      final double coeffDot4  = coeffDot3 * (2 * theta - 1);

      for (int i = 0; i < interpolatedState.length; ++i) {
          final double yDot1 = yDotK[0][i];
          final double yDot2 = yDotK[1][i];
          final double yDot3 = yDotK[2][i];
          final double yDot4 = yDotK[3][i];
          interpolatedState[i] =
              currentState[i] - coeff1 * yDot1 - coeff2 * yDot2 - coeff3 * yDot3 - coeff4 * yDot4;
          interpolatedDerivatives[i] =
              coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4;

      }

  }

}

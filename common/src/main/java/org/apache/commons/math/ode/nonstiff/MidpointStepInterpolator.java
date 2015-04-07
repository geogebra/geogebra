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
 * This class implements a step interpolator for second order
 * Runge-Kutta integrator.
 *
 * <p>This interpolator computes dense output inside the last
 * step computed. The interpolation equation is consistent with the
 * integration scheme :
 *
 * <pre>
 *   y(t_n + theta h) = y (t_n + h) + (1-theta) h [theta y'_1 - (1+theta) y'_2]
 * </pre>
 *
 * where theta belongs to [0 ; 1] and where y'_1 and y'_2 are the two
 * evaluations of the derivatives already computed during the
 * step.</p>
 *
 * @see MidpointIntegrator
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 */

class MidpointStepInterpolator
  extends RungeKuttaStepInterpolator {

    /** Serializable version identifier */
    private static final long serialVersionUID = -865524111506042509L;

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
  public MidpointStepInterpolator() {
  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public MidpointStepInterpolator(final MidpointStepInterpolator interpolator) {
    super(interpolator);
  }

  /** {@inheritDoc} */
  @Override
  protected StepInterpolator doCopy() {
    return new MidpointStepInterpolator(this);
  }


  /** {@inheritDoc} */
  @Override
  protected void computeInterpolatedStateAndDerivatives(final double theta,
                                          final double oneMinusThetaH)
    throws DerivativeException {

    final double coeff1    = oneMinusThetaH * theta;
    final double coeff2    = oneMinusThetaH * (1.0 + theta);
    final double coeffDot2 = 2 * theta;
    final double coeffDot1 = 1 - coeffDot2;

    for (int i = 0; i < interpolatedState.length; ++i) {
      final double yDot1 = yDotK[0][i];
      final double yDot2 = yDotK[1][i];
      interpolatedState[i] = currentState[i] + coeff1 * yDot1 - coeff2 * yDot2;
      interpolatedDerivatives[i] = coeffDot1 * yDot1 + coeffDot2 * yDot2;
    }

  }

}

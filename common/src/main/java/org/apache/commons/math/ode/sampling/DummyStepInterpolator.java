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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/** This class is a step interpolator that does nothing.
 *
 * <p>This class is used when the {@link StepHandler "step handler"}
 * set up by the user does not need step interpolation. It does not
 * recompute the state when {@link AbstractStepInterpolator#setInterpolatedTime
 * setInterpolatedTime} is called. This implies the interpolated state
 * is always the state at the end of the current step.</p>
 *
 * @see StepHandler
 *
 * @version $Revision: 1037327 $ $Date: 2010-11-20 21:57:37 +0100 (sam. 20 nov. 2010) $
 * @since 1.2
 */

public class DummyStepInterpolator
  extends AbstractStepInterpolator {

  /** Serializable version identifier. */
  private static final long serialVersionUID = 1708010296707839488L;

  /** Current derivative. */
  private double[] currentDerivative;

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * <code>AbstractStepInterpolator.reinitialize</code> protected method
   * should be called before using the instance in order to initialize
   * the internal arrays. This constructor is used only in order to delay
   * the initialization in some cases. As an example, the {@link
   * org.apache.commons.math.ode.nonstiff.EmbeddedRungeKuttaIntegrator} uses
   * the prototyping design pattern to create the step interpolators by
   * cloning an uninitialized model and latter initializing the copy.
   */
  public DummyStepInterpolator() {
    super();
    currentDerivative = null;
  }

  /** Simple constructor.
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param yDot reference to the integrator array holding the state
   * derivative at some arbitrary point within the step
   * @param forward integration direction indicator
   */
  public DummyStepInterpolator(final double[] y, final double[] yDot, final boolean forward) {
    super(y, forward);
    currentDerivative = yDot;
  }

  /** Copy constructor.
   * @param interpolator interpolator to copy from. The copy is a deep
   * copy: its arrays are separated from the original arrays of the
   * instance
   */
  public DummyStepInterpolator(final DummyStepInterpolator interpolator) {
    super(interpolator);
    currentDerivative = interpolator.currentDerivative.clone();
  }

  /** Really copy the finalized instance.
   * @return a copy of the finalized instance
   */
  @Override
  protected StepInterpolator doCopy() {
    return new DummyStepInterpolator(this);
  }

  /** Compute the state at the interpolated time.
   * In this class, this method does nothing: the interpolated state
   * is always the state at the end of the current step.
   * @param theta normalized interpolation abscissa within the step
   * (theta is zero at the previous time step and one at the current time step)
   * @param oneMinusThetaH time gap between the interpolated time and
   * the current time
   */
  @Override
  protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
      System.arraycopy(currentState,      0, interpolatedState,       0, currentState.length);
      System.arraycopy(currentDerivative, 0, interpolatedDerivatives, 0, currentDerivative.length);
  }

  /** Write the instance to an output channel.
   * @param out output channel
   * @exception IOException if the instance cannot be written
   */
  @Override
  public void writeExternal(final ObjectOutput out)
    throws IOException {

      // save the state of the base class
    writeBaseExternal(out);

    if (currentDerivative != null) {
        for (int i = 0; i < currentDerivative.length; ++i) {
            out.writeDouble(currentDerivative[i]);
        }
    }

  }

  /** Read the instance from an input channel.
   * @param in input channel
   * @exception IOException if the instance cannot be read
   */
  @Override
  public void readExternal(final ObjectInput in)
    throws IOException {

    // read the base class
    final double t = readBaseExternal(in);

    if (currentState == null) {
        currentDerivative = null;
    } else {
        currentDerivative  = new double[currentState.length];
        for (int i = 0; i < currentDerivative.length; ++i) {
            currentDerivative[i] = in.readDouble();
        }
    }

    // we can now set the interpolated time and state
    setInterpolatedTime(t);

  }

}

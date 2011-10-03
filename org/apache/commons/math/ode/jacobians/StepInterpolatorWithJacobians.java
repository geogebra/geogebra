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

import java.io.Externalizable;

import org.apache.commons.math.ode.DerivativeException;

/** This interface represents an interpolator over the last step
 * during an ODE integration.
 *
 * <p>The various ODE integrators provide objects implementing this
 * interface to the step handlers. These objects are often custom
 * objects tightly bound to the integrator internal algorithms. The
 * handlers can use these objects to retrieve the state vector at
 * intermediate times between the previous and the current grid points
 * (this feature is often called dense output).</p>
 * <p>One important thing to note is that the step handlers may be so
 * tightly bound to the integrators that they often share some internal
 * state arrays. This imply that one should <em>never</em> use a direct
 * reference to a step interpolator outside of the step handler, either
 * for future use or for use in another thread. If such a need arise, the
 * step interpolator <em>must</em> be copied using the dedicated
 * {@link #copy()} method.
 * </p>
 *
 * @see FirstOrderIntegratorWithJacobians
 * @see StepHandlerWithJacobians
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.1
 * @deprecated as of 2.2 the complete package is deprecated, it will be replaced
 * in 3.0 by a completely rewritten implementation
 */
@Deprecated
public interface StepInterpolatorWithJacobians extends Externalizable {

  /**
   * Get the previous grid point time.
   * @return previous grid point time
   */
  double getPreviousTime();

  /**
   * Get the current grid point time.
   * @return current grid point time
   */
  double getCurrentTime();

  /**
   * Get the time of the interpolated point.
   * If {@link #setInterpolatedTime} has not been called, it returns
   * the current grid point time.
   * @return interpolation point time
   */
  double getInterpolatedTime();

  /**
   * Set the time of the interpolated point.
   * <p>Setting the time outside of the current step is now allowed, but
   * should be used with care since the accuracy of the interpolator will
   * probably be very poor far from this step. This allowance has been
   * added to simplify implementation of search algorithms near the
   * step endpoints.</p>
   * <p>Setting the time changes the instance internal state. If a
   * specific state must be preserved, a copy of the instance must be
   * created using {@link #copy()}.</p>
   * @param time time of the interpolated point
   */
  void setInterpolatedTime(double time);

  /**
   * Get the state vector of the interpolated point.
   * <p>The returned vector is a reference to a reused array, so
   * it should not be modified and it should be copied if it needs
   * to be preserved across several calls.</p>
   * @return state vector at time {@link #getInterpolatedTime}
   * @see #getInterpolatedYDot()
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  double[] getInterpolatedY() throws DerivativeException;

  /**
   * Get the partial derivatives of the state vector with respect to
   * the initial state of the interpolated point.
   * <p>The returned vector is a reference to a reused array, so
   * it should not be modified and it should be copied if it needs
   * to be preserved across several calls.</p>
   * @return partial derivatives of the state vector with respect to
   * the initial state at time {@link #getInterpolatedTime}
   * @see #getInterpolatedY()
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  double[][] getInterpolatedDyDy0() throws DerivativeException;

  /**
   * Get the partial derivatives of the state vector with respect to
   * the ODE parameters of the interpolated point.
   * <p>The returned vector is a reference to a reused array, so
   * it should not be modified and it should be copied if it needs
   * to be preserved across several calls.</p>
   * @return partial derivatives of the state vector with respect to
   * the ODE parameters at time {@link #getInterpolatedTime}
   * @see #getInterpolatedY()
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  double[][] getInterpolatedDyDp() throws DerivativeException;

  /**
   * Get the time derivatives of the state vector of the interpolated point.
   * <p>The returned vector is a reference to a reused array, so
   * it should not be modified and it should be copied if it needs
   * to be preserved across several calls.</p>
   * @return derivatives of the state vector at time {@link #getInterpolatedTime}
   * @see #getInterpolatedY()
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  double[] getInterpolatedYDot() throws DerivativeException;

  /**
   * Get the time derivatives of the jacobian of the state vector
   * with respect to the initial state of the interpolated point.
   * <p>The returned vector is a reference to a reused array, so
   * it should not be modified and it should be copied if it needs
   * to be preserved across several calls.</p>
   * @return time derivatives of the jacobian of the state vector
   * with respect to the initial state at time {@link #getInterpolatedTime}
   * @see #getInterpolatedY()
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  double[][] getInterpolatedDyDy0Dot() throws DerivativeException;

  /**
   * Get the time derivatives of the jacobian of the state vector
   * with respect to the ODE parameters of the interpolated point.
   * <p>The returned vector is a reference to a reused array, so
   * it should not be modified and it should be copied if it needs
   * to be preserved across several calls.</p>
   * @return time derivatives of the jacobian of the state vector
   * with respect to the ODE parameters at time {@link #getInterpolatedTime}
   * @see #getInterpolatedY()
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   */
  double[][] getInterpolatedDyDpDot() throws DerivativeException;

  /** Check if the natural integration direction is forward.
   * <p>This method provides the integration direction as specified by
   * the integrator itself, it avoid some nasty problems in
   * degenerated cases like null steps due to cancellation at step
   * initialization, step control or discrete events
   * triggering.</p>
   * @return true if the integration variable (time) increases during
   * integration
   */
  boolean isForward();

  /** Copy the instance.
   * <p>The copied instance is guaranteed to be independent from the
   * original one. Both can be used with different settings for
   * interpolated time without any side effect.</p>
   * @return a deep copy of the instance, which can be used independently.
   * @throws DerivativeException if this call induces an automatic
   * step finalization that throws one
   * @see #setInterpolatedTime(double)
   */
   StepInterpolatorWithJacobians copy() throws DerivativeException;

}

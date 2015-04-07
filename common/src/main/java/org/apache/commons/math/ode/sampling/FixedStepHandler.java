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

/**
 * This interface represents a handler that should be called after
 * each successful fixed step.

 * <p>This interface should be implemented by anyone who is interested
 * in getting the solution of an ordinary differential equation at
 * fixed time steps. Objects implementing this interface should be
 * wrapped within an instance of {@link StepNormalizer} that itself
 * is used as the general {@link StepHandler} by the integrator. The
 * {@link StepNormalizer} object is called according to the integrator
 * internal algorithms and it calls objects implementing this
 * interface as necessary at fixed time steps.</p>
 *
 * @see StepHandler
 * @see StepNormalizer
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 1.2
 */

public interface FixedStepHandler  {

  /**
   * Handle the last accepted step
   * @param t time of the current step
   * @param y state vector at t. For efficiency purposes, the {@link
   * StepNormalizer} class reuses the same array on each call, so if
   * the instance wants to keep it across all calls (for example to
   * provide at the end of the integration a complete array of all
   * steps), it should build a local copy store this copy.
   * @param yDot derivatives of the state vector state vector at t.
   * For efficiency purposes, the {@link StepNormalizer} class reuses
   * the same array on each call, so if
   * the instance wants to keep it across all calls (for example to
   * provide at the end of the integration a complete array of all
   * steps), it should build a local copy store this copy.
   * @param isLast true if the step is the last one
   * @throws DerivativeException if some error condition is encountered
   */
  void handleStep(double t, double[] y, double[] yDot, boolean isLast) throws DerivativeException;

}

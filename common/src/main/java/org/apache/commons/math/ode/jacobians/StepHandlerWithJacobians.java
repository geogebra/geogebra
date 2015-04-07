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

import org.apache.commons.math.ode.DerivativeException;

/**
 * This interface represents a handler that should be called after
 * each successful step.
 *
 * <p>The ODE integrators compute the evolution of the state vector at
 * some grid points that depend on their own internal algorithm. Once
 * they have found a new grid point (possibly after having computed
 * several evaluation of the derivative at intermediate points), they
 * provide it to objects implementing this interface. These objects
 * typically either ignore the intermediate steps and wait for the
 * last one, store the points in an ephemeris, or forward them to
 * specialized processing or output methods.</p>
 *
 * <p>Note that is is possible to register a {@link
 * org.apache.commons.math.ode.sampling.StepHandler classical step handler}
 * in the low level integrator used to build a {@link FirstOrderIntegratorWithJacobians}
 * rather than implementing this class. The step handlers registered at low level
 * will see the big compound state whether the step handlers defined by this interface
 * see the original state, and its jacobians in separate arrays.</p>
 *
 * <p>The compound state is guaranteed to contain the original state in the first
 * elements, followed by the jacobian with respect to initial state (in row order),
 * followed by the jacobian with respect to parameters (in row order). If for example
 * the original state dimension is 6 and there are 3 parameters, the compound state will
 * be a 60 elements array. The first 6 elements will be the original state, the next 36
 * elements will be the jacobian with respect to initial state, and the remaining 18 elements
 * will be the jacobian with respect to parameters.</p>
 *
 * <p>Dealing with low level step handlers is cumbersome if one really needs the jacobians
 * in these methods, but it also prevents many data being copied back and forth between
 * state and jacobians on one side and compound state on the other side. So for performance
 * reasons, it is recommended to use this interface <em>only</em> if jacobians are really
 * needed and to use lower level handlers if only state is needed.</p>
 *
 * @see FirstOrderIntegratorWithJacobians
 * @see StepInterpolatorWithJacobians
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.1
 * @deprecated as of 2.2 the complete package is deprecated, it will be replaced
 * in 3.0 by a completely rewritten implementation
 */
@Deprecated
public interface StepHandlerWithJacobians {

  /** Determines whether this handler needs dense output.
   * <p>This method allows the integrator to avoid performing extra
   * computation if the handler does not need dense output.</p>
   * @return true if the handler needs dense output
   */
  boolean requiresDenseOutput();

  /** Reset the step handler.
   * Initialize the internal data as required before the first step is
   * handled.
   */
  void reset();

  /**
   * Handle the last accepted step
   * @param interpolator interpolator for the last accepted step. For
   * efficiency purposes, the various integrators reuse the same
   * object on each call, so if the instance wants to keep it across
   * all calls (for example to provide at the end of the integration a
   * continuous model valid throughout the integration range, as the
   * {@link org.apache.commons.math.ode.ContinuousOutputModel
   * ContinuousOutputModel} class does), it should build a local copy
   * using the clone method of the interpolator and store this copy.
   * Keeping only a reference to the interpolator and reusing it will
   * result in unpredictable behavior (potentially crashing the application).
   * @param isLast true if the step is the last one
   * @throws DerivativeException this exception is propagated to the
   * caller if the underlying user function triggers one
   */
  void handleStep(StepInterpolatorWithJacobians interpolator, boolean isLast) throws DerivativeException;

}

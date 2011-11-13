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
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;


/** This interface represents {@link ParameterizedODE
 * first order differential equations} with parameters and partial derivatives.
 *
 * @see FirstOrderIntegratorWithJacobians
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.1
 * @deprecated as of 2.2 the complete package is deprecated, it will be replaced
 * in 3.0 by a completely rewritten implementation
 */
@Deprecated
public interface ODEWithJacobians extends FirstOrderDifferentialEquations {

    /** Get the number of parameters.
     * @return number of parameters
     */
    int getParametersDimension();

    /** Compute the partial derivatives of ODE with respect to state.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the state vector
     * @param yDot array containing the current value of the time derivative of the state vector
     * @param dFdY placeholder array where to put the jacobian of the ODE with respect to the state vector
     * @param dFdP placeholder array where to put the jacobian of the ODE with respect to the parameters
     * @throws DerivativeException this exception is propagated to the caller if the
     * underlying user function triggers one
     */
    void computeJacobians(double t, double[] y, double[] yDot, double[][] dFdY, double[][] dFdP)
        throws DerivativeException;

}

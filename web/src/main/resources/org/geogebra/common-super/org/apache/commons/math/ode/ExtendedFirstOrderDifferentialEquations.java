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
package org.apache.commons.math.ode;


/** This interface represents a first order differential equations set
 * with a main set of equations and an extension set.
 *
 * <p>
 * This interface is a simple extension on the {@link
 * FirstOrderDifferentialEquations} that allows to identify which part
 * of a complete set of differential equations correspond to the main
 * set and which part correspond to the extension set.
 * </p>
 * <p>
 * One typical use case is the computation of Jacobians. The main
 * set of equations correspond to the raw ode, and we add to this set
 * another bunch of equations which represent the jacobians of the
 * main set. In that case, we want the integrator to use <em>only</em>
 * the main set to estimate the errors and hence the step sizes. It should
 * <em>not</em> use the additional equations in this computation. If the
 * complete ode implements this interface, the {@link FirstOrderIntegrator
 * integrator} will be able to know where the main set ends and where the
 * extended set begins.
 * </p>
 * <p>
 * We consider that the main set always corresponds to the first equations
 * and the extended set to the last equations.
 * </p>
 *
 * @see FirstOrderDifferentialEquations
 *
 * @version $Revision: 980981 $ $Date: 2010-07-31 00:03:04 +0200 (sam. 31 juil. 2010) $
 * @since 2.2
 */

public interface ExtendedFirstOrderDifferentialEquations extends FirstOrderDifferentialEquations {

    /** Return the dimension of the main set of equations.
     * <p>
     * The main set of equations represent the first part of an ODE state.
     * The error estimations and adaptive step size computation should be
     * done on this first part only, not on the final part of the state
     * which represent an extension set of equations which are considered
     * secondary.
     * </p>
     * @return dimension of the main set of equations, must be lesser than or
     * equal to the {@link #getDimension() total dimension}
     */
    int getMainSetDimension();

}

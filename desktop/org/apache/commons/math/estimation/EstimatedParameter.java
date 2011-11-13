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

package org.apache.commons.math.estimation;

import java.io.Serializable;

/** This class represents the estimated parameters of an estimation problem.
 *
 * <p>The parameters of an estimation problem have a name, a value and
 * a bound flag. The value of bound parameters is considered trusted
 * and the solvers should not adjust them. On the other hand, the
 * solvers should adjust the value of unbounds parameters until they
 * satisfy convergence criterions specific to each solver.</p>
 *
 * @version $Revision: 922710 $ $Date: 2010-03-14 02:20:56 +0100 (dim. 14 mars 2010) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general
 *
 */
@Deprecated
public class EstimatedParameter
  implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -555440800213416949L;

    /** Current value of the parameter */
    protected double  estimate;

    /** Name of the parameter */
    private final String  name;

    /** Indicator for bound parameters
     * (ie parameters that should not be estimated)
     */
    private   boolean bound;

    /** Simple constructor.
     * Build an instance from a first estimate of the parameter,
     * initially considered unbound.
     * @param name name of the parameter
     * @param firstEstimate first estimate of the parameter
     */
    public EstimatedParameter(String name, double firstEstimate) {
        this.name = name;
        estimate  = firstEstimate;
        bound     = false;
    }

    /** Simple constructor.
     * Build an instance from a first estimate of the parameter and a
     * bound flag
     * @param name name of the parameter
     * @param firstEstimate first estimate of the parameter
     * @param bound flag, should be true if the parameter is bound
     */
    public EstimatedParameter(String name,
                              double firstEstimate,
                              boolean bound) {
        this.name  = name;
        estimate   = firstEstimate;
        this.bound = bound;
    }

    /** Copy constructor.
     * Build a copy of a parameter
     * @param parameter instance to copy
     */
    public EstimatedParameter(EstimatedParameter parameter) {
        name     = parameter.name;
        estimate = parameter.estimate;
        bound    = parameter.bound;
    }

    /** Set a new estimated value for the parameter.
     * @param estimate new estimate for the parameter
     */
    public void setEstimate(double estimate) {
        this.estimate = estimate;
    }

    /** Get the current estimate of the parameter
     * @return current estimate
     */
    public double getEstimate() {
        return estimate;
    }

    /** get the name of the parameter
     * @return parameter name
     */
    public String getName() {
        return name;
    }

    /** Set the bound flag of the parameter
     * @param bound this flag should be set to true if the parameter is
     * bound (i.e. if it should not be adjusted by the solver).
     */
    public void setBound(boolean bound) {
        this.bound = bound;
    }

    /** Check if the parameter is bound
     * @return true if the parameter is bound */
    public boolean isBound() {
        return bound;
    }

}

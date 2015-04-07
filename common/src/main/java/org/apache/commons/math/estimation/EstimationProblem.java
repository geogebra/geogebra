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

/**
 * This interface represents an estimation problem.
 *
 * <p>This interface should be implemented by all real estimation
 * problems before they can be handled by the estimators through the
 * {@link Estimator#estimate Estimator.estimate} method.</p>
 *
 * <p>An estimation problem, as seen by a solver is a set of
 * parameters and a set of measurements. The parameters are adjusted
 * during the estimation through the {@link #getUnboundParameters
 * getUnboundParameters} and {@link EstimatedParameter#setEstimate
 * EstimatedParameter.setEstimate} methods. The measurements both have
 * a measured value which is generally fixed at construction and a
 * theoretical value which depends on the model and hence varies as
 * the parameters are adjusted. The purpose of the solver is to reduce
 * the residual between these values, it can retrieve the measurements
 * through the {@link #getMeasurements getMeasurements} method.</p>
 *
 * @see Estimator
 * @see WeightedMeasurement
 *
 * @version $Revision: 811786 $ $Date: 2009-09-06 11:36:08 +0200 (dim. 06 sept. 2009) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general
 *
 */
@Deprecated
public interface EstimationProblem {

    /**
     * Get the measurements of an estimation problem.
     * @return measurements
     */
    WeightedMeasurement[] getMeasurements();

    /**
     * Get the unbound parameters of the problem.
     * @return unbound parameters
     */
    EstimatedParameter[] getUnboundParameters();

    /**
     * Get all the parameters of the problem.
     * @return parameters
     */
    EstimatedParameter[] getAllParameters();

}

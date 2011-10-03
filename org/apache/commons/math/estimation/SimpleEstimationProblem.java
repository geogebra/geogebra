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

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of the {@link EstimationProblem
 * EstimationProblem} interface for boilerplate data handling.
 * <p>This class <em>only</em> handles parameters and measurements
 * storage and unbound parameters filtering. It does not compute
 * anything by itself. It should either be used with measurements
 * implementation that are smart enough to know about the
 * various parameters in order to compute the partial derivatives
 * appropriately. Since the problem-specific logic is mainly related to
 * the various measurements models, the simplest way to use this class
 * is by extending it and using one internal class extending
 * {@link WeightedMeasurement WeightedMeasurement} for each measurement
 * type. The instances of the internal classes would have access to the
 * various parameters and their current estimate.</p>

 * @version $Revision: 811827 $ $Date: 2009-09-06 17:32:50 +0200 (dim. 06 sept. 2009) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general

 */
@Deprecated
public class SimpleEstimationProblem implements EstimationProblem {

    /** Estimated parameters. */
    private final List<EstimatedParameter> parameters;

    /** Measurements. */
    private final List<WeightedMeasurement> measurements;

    /**
     * Build an empty instance without parameters nor measurements.
     */
    public SimpleEstimationProblem() {
        parameters   = new ArrayList<EstimatedParameter>();
        measurements = new ArrayList<WeightedMeasurement>();
    }

    /**
     * Get all the parameters of the problem.
     * @return parameters
     */
    public EstimatedParameter[] getAllParameters() {
        return parameters.toArray(new EstimatedParameter[parameters.size()]);
    }

    /**
     * Get the unbound parameters of the problem.
     * @return unbound parameters
     */
    public EstimatedParameter[] getUnboundParameters() {

        // filter the unbound parameters
        List<EstimatedParameter> unbound = new ArrayList<EstimatedParameter>(parameters.size());
        for (EstimatedParameter p : parameters) {
            if (! p.isBound()) {
                unbound.add(p);
            }
        }

        // convert to an array
        return unbound.toArray(new EstimatedParameter[unbound.size()]);

    }

    /**
     * Get the measurements of an estimation problem.
     * @return measurements
     */
    public WeightedMeasurement[] getMeasurements() {
        return measurements.toArray(new WeightedMeasurement[measurements.size()]);
    }

    /** Add a parameter to the problem.
     * @param p parameter to add
     */
    protected void addParameter(EstimatedParameter p) {
        parameters.add(p);
    }

    /**
     * Add a new measurement to the set.
     * @param m measurement to add
     */
    protected void addMeasurement(WeightedMeasurement m) {
        measurements.add(m);
    }

}

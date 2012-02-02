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

package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.FunctionEvaluationException;

/**
 * An interface representing a real function that depends on one independent
 * variable plus some extra parameters.
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public interface ParametricRealFunction {

    /**
     * Compute the value of the function.
     * @param x the point for which the function value should be computed
     * @param parameters function parameters
     * @return the value
     * @throws FunctionEvaluationException if the function evaluation fails
     */
    double value(double x, double[] parameters)
        throws FunctionEvaluationException;

    /**
     * Compute the gradient of the function with respect to its parameters.
     * @param x the point for which the function value should be computed
     * @param parameters function parameters
     * @return the value
     * @throws FunctionEvaluationException if the function evaluation fails
     */
    double[] gradient(double x, double[] parameters)
        throws FunctionEvaluationException;

}

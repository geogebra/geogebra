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
package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * Interface for univariate real integration algorithms.
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */
public interface UnivariateRealIntegrator extends ConvergingAlgorithm {

   /**
     * Set the lower limit for the number of iterations.
     * <p>
     * Minimal iteration is needed to avoid false early convergence, e.g.
     * the sample points happen to be zeroes of the function. Users can
     * use the default value or choose one that they see as appropriate.</p>
     * <p>
     * A <code>ConvergenceException</code> will be thrown if this number
     * is not met.</p>
     *
     * @param count minimum number of iterations
     */
    void setMinimalIterationCount(int count);

    /**
     * Get the lower limit for the number of iterations.
     *
     * @return the actual lower limit
     */
    int getMinimalIterationCount();

    /**
     * Reset the lower limit for the number of iterations to the default.
     * <p>
     * The default value is supplied by the implementation.</p>
     *
     * @see #setMinimalIterationCount(int)
     */
    void resetMinimalIterationCount();

    /**
     * Integrate the function in the given interval.
     *
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the value of integral
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the integrator detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the
     * function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the integrator
     * @deprecated replaced by {@link #integrate(UnivariateRealFunction, double, double)}
     * since 2.0
     */
    @Deprecated
    double integrate(double min, double max)
        throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;

    /**
     * Integrate the function in the given interval.
     *
     * @param f the integrand function
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the value of integral
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the integrator detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the integrator
     */
    double integrate(UnivariateRealFunction f, double min, double max)
        throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;

    /**
     * Get the result of the last run of the integrator.
     *
     * @return the last result
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed
     */
    double getResult() throws IllegalStateException;

}

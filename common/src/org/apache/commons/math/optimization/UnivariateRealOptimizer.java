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
package org.apache.commons.math.optimization;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;


/**
 * Interface for (univariate real) optimization algorithms.
 *
 * @version $Revision: 1073658 $ $Date: 2011-02-23 10:45:42 +0100 (mer. 23 févr. 2011) $
 * @since 2.0
 */
public interface UnivariateRealOptimizer extends ConvergingAlgorithm {

    /** Set the maximal number of functions evaluations.
     * @param maxEvaluations maximal number of function evaluations
     */
    void setMaxEvaluations(int maxEvaluations);

    /** Get the maximal number of functions evaluations.
     * @return the maximal number of functions evaluations.
     */
    int getMaxEvaluations();

    /** Get the number of evaluations of the objective function.
     * <p>
     * The number of evaluations corresponds to the last call to the
     * {@link #optimize(UnivariateRealFunction, GoalType, double, double) optimize}
     * method. It is 0 if the method has not been called yet.
     * </p>
     * @return the number of evaluations of the objective function.
     */
    int getEvaluations();

    /**
     * Find an optimum in the given interval.
     * <p>
     * An optimizer may require that the interval brackets a single optimum.
     * </p>
     * @param f the function to optimize.
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is optimum.
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the optimizer detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the function.
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the optimizer.
     */
    double optimize(UnivariateRealFunction f, GoalType goalType,
                    double min, double max)
        throws ConvergenceException, FunctionEvaluationException;

    /**
     * Find an optimum in the given interval, start at startValue.
     * <p>
     * An optimizer may require that the interval brackets a single optimum.
     * </p>
     * @param f the function to optimize.
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use.
     * @return a value where the function is optimum.
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the optimizer detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the function.
     * @throws IllegalArgumentException if min > max or the arguments do not
     * satisfy the requirements specified by the optimizer.
     * @throws IllegalStateException if there are no data.
     */
    double optimize(UnivariateRealFunction f, GoalType goalType,
                    double min, double max, double startValue)
        throws ConvergenceException, FunctionEvaluationException;

    /**
     * Get the result of the last run of the optimizer.
     *
     * @return the optimum.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getResult();

    /**
     * Get the result of the last run of the optimizer.
     *
     * @return the value of the function at the optimum.
     * @throws FunctionEvaluationException if an error occurs evaluating the function.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getFunctionValue() throws FunctionEvaluationException;

}

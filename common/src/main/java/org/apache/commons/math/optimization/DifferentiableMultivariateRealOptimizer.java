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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;

/**
 * This interface represents an optimization algorithm for
 * {@link DifferentiableMultivariateRealFunction scalar differentiable objective
 * functions}.
 * Optimization algorithms find the input point set that either {@link GoalType
 * maximize or minimize} an objective function.
 *
 * @see MultivariateRealOptimizer
 * @see DifferentiableMultivariateVectorialOptimizer
 * @version $Revision: 1065484 $ $Date: 2011-01-31 06:45:14 +0100 (lun. 31 janv. 2011) $
 * @since 2.0
 */
public interface DifferentiableMultivariateRealOptimizer {

    /** Set the maximal number of iterations of the algorithm.
     * @param maxIterations maximal number of function calls
     */
    void setMaxIterations(int maxIterations);

    /** Get the maximal number of iterations of the algorithm.
     * @return maximal number of iterations
     */
    int getMaxIterations();

    /** Get the number of iterations realized by the algorithm.
     * <p>
     * The number of evaluations corresponds to the last call to the
     * {@code optimize} method. It is 0 if the method has not been called yet.
     * </p>
     * @return number of iterations
     */
    int getIterations();

    /** Set the maximal number of functions evaluations.
     * @param maxEvaluations maximal number of function evaluations
     */
    void setMaxEvaluations(int maxEvaluations);

    /** Get the maximal number of functions evaluations.
     * @return maximal number of functions evaluations
     */
    int getMaxEvaluations();

    /** Get the number of evaluations of the objective function.
     * <p>
     * The number of evaluations corresponds to the last call to the
     * {@link #optimize(DifferentiableMultivariateRealFunction, GoalType, double[]) optimize}
     * method. It is 0 if the method has not been called yet.
     * </p>
     * @return number of evaluations of the objective function
     */
    int getEvaluations();

    /** Get the number of evaluations of the objective function gradient.
     * <p>
     * The number of evaluations corresponds to the last call to the
     * {@link #optimize(DifferentiableMultivariateRealFunction, GoalType, double[]) optimize}
     * method. It is 0 if the method has not been called yet.
     * </p>
     * @return number of evaluations of the objective function gradient
     */
    int getGradientEvaluations();

    /** Set the convergence checker.
     * @param checker object to use to check for convergence
     */
    void setConvergenceChecker(RealConvergenceChecker checker);

    /** Get the convergence checker.
     * @return object used to check for convergence
     */
    RealConvergenceChecker getConvergenceChecker();

    /** Optimizes an objective function.
     * @param f objective function
     * @param goalType type of optimization goal: either {@link GoalType#MAXIMIZE}
     * or {@link GoalType#MINIMIZE}
     * @param startPoint the start point for optimization
     * @return the point/value pair giving the optimal value for objective function
     * @exception FunctionEvaluationException if the objective function throws one during
     * the search
     * @exception OptimizationException if the algorithm failed to converge
     * @exception IllegalArgumentException if the start point dimension is wrong
     */
    RealPointValuePair optimize(DifferentiableMultivariateRealFunction f,
                                  GoalType goalType,
                                  double[] startPoint)
        throws FunctionEvaluationException, OptimizationException, IllegalArgumentException;

}

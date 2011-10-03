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
package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;


/**
 * Interface for (univariate real) rootfinding algorithms.
 * <p>
 * Implementations will search for only one zero in the given interval.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 */
public interface UnivariateRealSolver extends ConvergingAlgorithm {

    /**
     * Set the function value accuracy.
     * <p>
     * This is used to determine when an evaluated function value or some other
     * value which is used as divisor is zero.</p>
     * <p>
     * This is a safety guard and it shouldn't be necessary to change this in
     * general.</p>
     *
     * @param accuracy the accuracy.
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     * the solver or is otherwise deemed unreasonable.
     */
    void setFunctionValueAccuracy(double accuracy);

    /**
     * Get the actual function value accuracy.
     * @return the accuracy
     */
    double getFunctionValueAccuracy();

    /**
     * Reset the actual function accuracy to the default.
     * The default value is provided by the solver implementation.
     */
    void resetFunctionValueAccuracy();

    /**
     * Solve for a zero root in the given interval.
     * <p>A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.</p>
     *
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the solver
     * @deprecated replaced by {@link #solve(UnivariateRealFunction, double, double)}
     * since 2.0
     */
    @Deprecated
    double solve(double min, double max) throws ConvergenceException, FunctionEvaluationException;

    /**
     * Solve for a zero root in the given interval.
     * <p>A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.</p>
     *
     * @param f the function to solve.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min > max or the endpoints do not
     * satisfy the requirements specified by the solver
     * @since 2.0
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    double solve(UnivariateRealFunction f, double min, double max)
        throws ConvergenceException, FunctionEvaluationException;

    /**
     * Solve for a zero in the given interval, start at startValue.
     * <p>A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.</p>
     *
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min > max or the arguments do not
     * satisfy the requirements specified by the solver
     * @deprecated replaced by {@link #solve(UnivariateRealFunction, double, double, double)}
     * since 2.0
     */
    @Deprecated
    double solve(double min, double max, double startValue)
        throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;

    /**
     * Solve for a zero in the given interval, start at startValue.
     * <p>A solver may require that the interval brackets a single zero root.
     * Solvers that do require bracketing should be able to handle the case
     * where one of the endpoints is itself a root.</p>
     *
     * @param f the function to solve.
     * @param min the lower bound for the interval.
     * @param max the upper bound for the interval.
     * @param startValue the start value to use
     * @return a value where the function is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise.
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if min > max or the arguments do not
     * satisfy the requirements specified by the solver
     * @since 2.0
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    double solve(UnivariateRealFunction f, double min, double max, double startValue)
        throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;

    /**
     * Get the result of the last run of the solver.
     *
     * @return the last result.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getResult();

    /**
     * Get the result of the last run of the solver.
     *
     * @return the value of the function at the last result.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    double getFunctionValue();
}

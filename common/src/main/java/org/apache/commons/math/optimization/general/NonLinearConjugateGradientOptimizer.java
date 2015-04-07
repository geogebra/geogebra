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

package org.apache.commons.math.optimization.general;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.util.FastMath;

/**
 * Non-linear conjugate gradient optimizer.
 * <p>
 * This class supports both the Fletcher-Reeves and the Polak-Ribi&egrave;re
 * update formulas for the conjugate search directions. It also supports
 * optional preconditioning.
 * </p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 2.0
 *
 */

public class NonLinearConjugateGradientOptimizer
    extends AbstractScalarDifferentiableOptimizer {

    /** Update formula for the beta parameter. */
    private final ConjugateGradientFormula updateFormula;

    /** Preconditioner (may be null). */
    private Preconditioner preconditioner;

    /** solver to use in the line search (may be null). */
    private UnivariateRealSolver solver;

    /** Initial step used to bracket the optimum in line search. */
    private double initialStep;

    /** Simple constructor with default settings.
     * <p>The convergence check is set to a {@link
     * org.apache.commons.math.optimization.SimpleVectorialValueChecker}
     * and the maximal number of iterations is set to
     * {@link AbstractScalarDifferentiableOptimizer#DEFAULT_MAX_ITERATIONS}.
     * @param updateFormula formula to use for updating the &beta; parameter,
     * must be one of {@link ConjugateGradientFormula#FLETCHER_REEVES} or {@link
     * ConjugateGradientFormula#POLAK_RIBIERE}
     */
    public NonLinearConjugateGradientOptimizer(final ConjugateGradientFormula updateFormula) {
        this.updateFormula = updateFormula;
        preconditioner     = null;
        solver             = null;
        initialStep        = 1.0;
    }

    /**
     * Set the preconditioner.
     * @param preconditioner preconditioner to use for next optimization,
     * may be null to remove an already registered preconditioner
     */
    public void setPreconditioner(final Preconditioner preconditioner) {
        this.preconditioner = preconditioner;
    }

    /**
     * Set the solver to use during line search.
     * @param lineSearchSolver solver to use during line search, may be null
     * to remove an already registered solver and fall back to the
     * default {@link BrentSolver Brent solver}.
     */
    public void setLineSearchSolver(final UnivariateRealSolver lineSearchSolver) {
        this.solver = lineSearchSolver;
    }

    /**
     * Set the initial step used to bracket the optimum in line search.
     * <p>
     * The initial step is a factor with respect to the search direction,
     * which itself is roughly related to the gradient of the function
     * </p>
     * @param initialStep initial step used to bracket the optimum in line search,
     * if a non-positive value is used, the initial step is reset to its
     * default value of 1.0
     */
    public void setInitialStep(final double initialStep) {
        if (initialStep <= 0) {
            this.initialStep = 1.0;
        } else {
            this.initialStep = initialStep;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected RealPointValuePair doOptimize()
        throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {
        try {

            // initialization
            if (preconditioner == null) {
                preconditioner = new IdentityPreconditioner();
            }
            if (solver == null) {
                solver = new BrentSolver();
            }
            final int n = point.length;
            double[] r = computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int i = 0; i < n; ++i) {
                    r[i] = -r[i];
                }
            }

            // initial search direction
            double[] steepestDescent = preconditioner.precondition(point, r);
            double[] searchDirection = steepestDescent.clone();

            double delta = 0;
            for (int i = 0; i < n; ++i) {
                delta += r[i] * searchDirection[i];
            }

            RealPointValuePair current = null;
            while (true) {

                final double objective = computeObjectiveValue(point);
                RealPointValuePair previous = current;
                current = new RealPointValuePair(point, objective);
                if (previous != null) {
                    if (checker.converged(getIterations(), previous, current)) {
                        // we have found an optimum
                        return current;
                    }
                }

                incrementIterationsCounter();

                // find the optimal step in the search direction
                final UnivariateRealFunction lsf = new LineSearchFunction(searchDirection);
                final double step = solver.solve(lsf, 0, findUpperBound(lsf, 0, initialStep));

                // validate new point
                for (int i = 0; i < point.length; ++i) {
                    point[i] += step * searchDirection[i];
                }
                r = computeObjectiveGradient(point);
                if (goal == GoalType.MINIMIZE) {
                    for (int i = 0; i < n; ++i) {
                        r[i] = -r[i];
                    }
                }

                // compute beta
                final double deltaOld = delta;
                final double[] newSteepestDescent = preconditioner.precondition(point, r);
                delta = 0;
                for (int i = 0; i < n; ++i) {
                    delta += r[i] * newSteepestDescent[i];
                }

                final double beta;
                if (updateFormula == ConjugateGradientFormula.FLETCHER_REEVES) {
                    beta = delta / deltaOld;
                } else {
                    double deltaMid = 0;
                    for (int i = 0; i < r.length; ++i) {
                        deltaMid += r[i] * steepestDescent[i];
                    }
                    beta = (delta - deltaMid) / deltaOld;
                }
                steepestDescent = newSteepestDescent;

                // compute conjugate search direction
                if ((getIterations() % n == 0) || (beta < 0)) {
                    // break conjugation: reset search direction
                    searchDirection = steepestDescent.clone();
                } else {
                    // compute new conjugate search direction
                    for (int i = 0; i < n; ++i) {
                        searchDirection[i] = steepestDescent[i] + beta * searchDirection[i];
                    }
                }

            }

        } catch (ConvergenceException ce) {
            throw new OptimizationException(ce);
        }
    }

    /**
     * Find the upper bound b ensuring bracketing of a root between a and b
     * @param f function whose root must be bracketed
     * @param a lower bound of the interval
     * @param h initial step to try
     * @return b such that f(a) and f(b) have opposite signs
     * @exception FunctionEvaluationException if the function cannot be computed
     * @exception OptimizationException if no bracket can be found
     */
    private double findUpperBound(final UnivariateRealFunction f,
                                  final double a, final double h)
        throws FunctionEvaluationException, OptimizationException {
        final double yA = f.value(a);
        double yB = yA;
        for (double step = h; step < Double.MAX_VALUE; step *= FastMath.max(2, yA / yB)) {
            final double b = a + step;
            yB = f.value(b);
            if (yA * yB <= 0) {
                return b;
            }
        }
        throw new OptimizationException(LocalizedFormats.UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH);
    }

    /** Default identity preconditioner. */
    private static class IdentityPreconditioner implements Preconditioner {

        /** {@inheritDoc} */
        public double[] precondition(double[] variables, double[] r) {
            return r.clone();
        }

    }

    /** Internal class for line search.
     * <p>
     * The function represented by this class is the dot product of
     * the objective function gradient and the search direction. Its
     * value is zero when the gradient is orthogonal to the search
     * direction, i.e. when the objective function value is a local
     * extremum along the search direction.
     * </p>
     */
    private class LineSearchFunction implements UnivariateRealFunction {
        /** Search direction. */
        private final double[] searchDirection;

        /** Simple constructor.
         * @param searchDirection search direction
         */
        public LineSearchFunction(final double[] searchDirection) {
            this.searchDirection = searchDirection;
        }

        /** {@inheritDoc} */
        public double value(double x) throws FunctionEvaluationException {

            // current point in the search direction
            final double[] shiftedPoint = point.clone();
            for (int i = 0; i < shiftedPoint.length; ++i) {
                shiftedPoint[i] += x * searchDirection[i];
            }

            // gradient of the objective function
            final double[] gradient;
            gradient = computeObjectiveGradient(shiftedPoint);

            // dot product with the search direction
            double dotProduct = 0;
            for (int i = 0; i < gradient.length; ++i) {
                dotProduct += gradient[i] * searchDirection[i];
            }

            return dotProduct;

        }

    }

}

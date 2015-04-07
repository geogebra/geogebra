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
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/**
 * Implements the <a href="http://mathworld.wolfram.com/MullersMethod.html">
 * Muller's Method</a> for root finding of real univariate functions. For
 * reference, see <b>Elementary Numerical Analysis</b>, ISBN 0070124477,
 * chapter 3.
 * <p>
 * Muller's method applies to both real and complex functions, but here we
 * restrict ourselves to real functions. Methods solve() and solve2() find
 * real zeros, using different ways to bypass complex arithmetics.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */
public class MullerSolver extends UnivariateRealSolverImpl {

    /**
     * Construct a solver for the given function.
     *
     * @param f function to solve
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    public MullerSolver(UnivariateRealFunction f) {
        super(f, 100, 1E-6);
    }

    /**
     * Construct a solver.
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public MullerSolver() {
        super(100, 1E-6);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max)
        throws ConvergenceException, FunctionEvaluationException {
        return solve(f, min, max);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max, final double initial)
        throws ConvergenceException, FunctionEvaluationException {
        return solve(f, min, max, initial);
    }

    /**
     * Find a real root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @param maxEval Maximum number of evaluations.
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max, initial);
    }

    /**
     * Find a real root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        // check for zeros before verifying bracketing
        if (f.value(min) == 0.0) { return min; }
        if (f.value(max) == 0.0) { return max; }
        if (f.value(initial) == 0.0) { return initial; }

        verifyBracketing(min, max, f);
        verifySequence(min, initial, max);
        if (isBracketing(min, initial, f)) {
            return solve(f, min, initial);
        } else {
            return solve(f, initial, max);
        }
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * Original Muller's method would have function evaluation at complex point.
     * Since our f(x) is real, we have to find ways to avoid that. Bracketing
     * condition is one way to go: by requiring bracketing in every iteration,
     * the newly computed approximation is guaranteed to be real.</p>
     * <p>
     * Normally Muller's method converges quadratically in the vicinity of a
     * zero, however it may be very slow in regions far away from zeros. For
     * example, f(x) = exp(x) - 1, min = -50, max = 100. In such case we use
     * bisection as a safety backup if it performs very poorly.</p>
     * <p>
     * The formulas here use divided differences directly.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param maxEval Maximum number of evaluations.
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max);
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * Original Muller's method would have function evaluation at complex point.
     * Since our f(x) is real, we have to find ways to avoid that. Bracketing
     * condition is one way to go: by requiring bracketing in every iteration,
     * the newly computed approximation is guaranteed to be real.</p>
     * <p>
     * Normally Muller's method converges quadratically in the vicinity of a
     * zero, however it may be very slow in regions far away from zeros. For
     * example, f(x) = exp(x) - 1, min = -50, max = 100. In such case we use
     * bisection as a safety backup if it performs very poorly.</p>
     * <p>
     * The formulas here use divided differences directly.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        // [x0, x2] is the bracketing interval in each iteration
        // x1 is the last approximation and an interpolation point in (x0, x2)
        // x is the new root approximation and new x1 for next round
        // d01, d12, d012 are divided differences

        double x0 = min;
        double y0 = f.value(x0);
        double x2 = max;
        double y2 = f.value(x2);
        double x1 = 0.5 * (x0 + x2);
        double y1 = f.value(x1);

        // check for zeros before verifying bracketing
        if (y0 == 0.0) {
            return min;
        }
        if (y2 == 0.0) {
            return max;
        }
        verifyBracketing(min, max, f);

        double oldx = Double.POSITIVE_INFINITY;
        for (int i = 1; i <= maximalIterationCount; ++i) {
            // Muller's method employs quadratic interpolation through
            // x0, x1, x2 and x is the zero of the interpolating parabola.
            // Due to bracketing condition, this parabola must have two
            // real roots and we choose one in [x0, x2] to be x.
            final double d01 = (y1 - y0) / (x1 - x0);
            final double d12 = (y2 - y1) / (x2 - x1);
            final double d012 = (d12 - d01) / (x2 - x0);
            final double c1 = d01 + (x1 - x0) * d012;
            final double delta = c1 * c1 - 4 * y1 * d012;
            final double xplus = x1 + (-2.0 * y1) / (c1 + FastMath.sqrt(delta));
            final double xminus = x1 + (-2.0 * y1) / (c1 - FastMath.sqrt(delta));
            // xplus and xminus are two roots of parabola and at least
            // one of them should lie in (x0, x2)
            final double x = isSequence(x0, xplus, x2) ? xplus : xminus;
            final double y = f.value(x);

            // check for convergence
            final double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x), absoluteAccuracy);
            if (FastMath.abs(x - oldx) <= tolerance) {
                setResult(x, i);
                return result;
            }
            if (FastMath.abs(y) <= functionValueAccuracy) {
                setResult(x, i);
                return result;
            }

            // Bisect if convergence is too slow. Bisection would waste
            // our calculation of x, hopefully it won't happen often.
            // the real number equality test x == x1 is intentional and
            // completes the proximity tests above it
            boolean bisect = (x < x1 && (x1 - x0) > 0.95 * (x2 - x0)) ||
                             (x > x1 && (x2 - x1) > 0.95 * (x2 - x0)) ||
                             (x == x1);
            // prepare the new bracketing interval for next iteration
            if (!bisect) {
                x0 = x < x1 ? x0 : x1;
                y0 = x < x1 ? y0 : y1;
                x2 = x > x1 ? x2 : x1;
                y2 = x > x1 ? y2 : y1;
                x1 = x; y1 = y;
                oldx = x;
            } else {
                double xm = 0.5 * (x0 + x2);
                double ym = f.value(xm);
                if (MathUtils.sign(y0) + MathUtils.sign(ym) == 0.0) {
                    x2 = xm; y2 = ym;
                } else {
                    x0 = xm; y0 = ym;
                }
                x1 = 0.5 * (x0 + x2);
                y1 = f.value(x1);
                oldx = Double.POSITIVE_INFINITY;
            }
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * solve2() differs from solve() in the way it avoids complex operations.
     * Except for the initial [min, max], solve2() does not require bracketing
     * condition, e.g. f(x0), f(x1), f(x2) can have the same sign. If complex
     * number arises in the computation, we simply use its modulus as real
     * approximation.</p>
     * <p>
     * Because the interval may not be bracketing, bisection alternative is
     * not applicable here. However in practice our treatment usually works
     * well, especially near real zeros where the imaginary part of complex
     * approximation is often negligible.</p>
     * <p>
     * The formulas here do not use divided differences directly.</p>
     *
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated replaced by {@link #solve2(UnivariateRealFunction, double, double)}
     * since 2.0
     */
    @Deprecated
    public double solve2(final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return solve2(f, min, max);
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * solve2() differs from solve() in the way it avoids complex operations.
     * Except for the initial [min, max], solve2() does not require bracketing
     * condition, e.g. f(x0), f(x1), f(x2) can have the same sign. If complex
     * number arises in the computation, we simply use its modulus as real
     * approximation.</p>
     * <p>
     * Because the interval may not be bracketing, bisection alternative is
     * not applicable here. However in practice our treatment usually works
     * well, especially near real zeros where the imaginary part of complex
     * approximation is often negligible.</p>
     * <p>
     * The formulas here do not use divided differences directly.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve2(final UnivariateRealFunction f,
                         final double min, final double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {

        // x2 is the last root approximation
        // x is the new approximation and new x2 for next round
        // x0 < x1 < x2 does not hold here

        double x0 = min;
        double y0 = f.value(x0);
        double x1 = max;
        double y1 = f.value(x1);
        double x2 = 0.5 * (x0 + x1);
        double y2 = f.value(x2);

        // check for zeros before verifying bracketing
        if (y0 == 0.0) { return min; }
        if (y1 == 0.0) { return max; }
        verifyBracketing(min, max, f);

        double oldx = Double.POSITIVE_INFINITY;
        for (int i = 1; i <= maximalIterationCount; ++i) {
            // quadratic interpolation through x0, x1, x2
            final double q = (x2 - x1) / (x1 - x0);
            final double a = q * (y2 - (1 + q) * y1 + q * y0);
            final double b = (2 * q + 1) * y2 - (1 + q) * (1 + q) * y1 + q * q * y0;
            final double c = (1 + q) * y2;
            final double delta = b * b - 4 * a * c;
            double x;
            final double denominator;
            if (delta >= 0.0) {
                // choose a denominator larger in magnitude
                double dplus = b + FastMath.sqrt(delta);
                double dminus = b - FastMath.sqrt(delta);
                denominator = FastMath.abs(dplus) > FastMath.abs(dminus) ? dplus : dminus;
            } else {
                // take the modulus of (B +/- FastMath.sqrt(delta))
                denominator = FastMath.sqrt(b * b - delta);
            }
            if (denominator != 0) {
                x = x2 - 2.0 * c * (x2 - x1) / denominator;
                // perturb x if it exactly coincides with x1 or x2
                // the equality tests here are intentional
                while (x == x1 || x == x2) {
                    x += absoluteAccuracy;
                }
            } else {
                // extremely rare case, get a random number to skip it
                x = min + FastMath.random() * (max - min);
                oldx = Double.POSITIVE_INFINITY;
            }
            final double y = f.value(x);

            // check for convergence
            final double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x), absoluteAccuracy);
            if (FastMath.abs(x - oldx) <= tolerance) {
                setResult(x, i);
                return result;
            }
            if (FastMath.abs(y) <= functionValueAccuracy) {
                setResult(x, i);
                return result;
            }

            // prepare the next iteration
            x0 = x1;
            y0 = y1;
            x1 = x2;
            y1 = y2;
            x2 = x;
            y2 = y;
            oldx = x;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }
}

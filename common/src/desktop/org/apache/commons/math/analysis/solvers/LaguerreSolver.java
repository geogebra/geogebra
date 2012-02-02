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
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implements the <a href="http://mathworld.wolfram.com/LaguerresMethod.html">
 * Laguerre's Method</a> for root finding of real coefficient polynomials.
 * For reference, see <b>A First Course in Numerical Analysis</b>,
 * ISBN 048641454X, chapter 8.
 * <p>
 * Laguerre's method is global in the sense that it can start with any initial
 * approximation and be able to solve all roots from that point.</p>
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 1.2
 */
public class LaguerreSolver extends UnivariateRealSolverImpl {

    /** polynomial function to solve.
     * @deprecated as of 2.0 the function is not stored anymore in the instance
     */
    @Deprecated
    private final PolynomialFunction p;

    /**
     * Construct a solver for the given function.
     *
     * @param f function to solve
     * @throws IllegalArgumentException if function is not polynomial
     * @deprecated as of 2.0 the function to solve is passed as an argument
     * to the {@link #solve(UnivariateRealFunction, double, double)} or
     * {@link UnivariateRealSolverImpl#solve(UnivariateRealFunction, double, double, double)}
     * method.
     */
    @Deprecated
    public LaguerreSolver(UnivariateRealFunction f) throws IllegalArgumentException {
        super(f, 100, 1E-6);
        if (f instanceof PolynomialFunction) {
            p = (PolynomialFunction) f;
        } else {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FUNCTION_NOT_POLYNOMIAL);
        }
    }

    /**
     * Construct a solver.
     * @deprecated in 2.2 (to be removed in 3.0)
     */
    @Deprecated
    public LaguerreSolver() {
        super(100, 1E-6);
        p = null;
    }

    /**
     * Returns a copy of the polynomial function.
     *
     * @return a fresh copy of the polynomial function
     * @deprecated as of 2.0 the function is not stored anymore within the instance.
     */
    @Deprecated
    public PolynomialFunction getPolynomialFunction() {
        return new PolynomialFunction(p.getCoefficients());
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max)
        throws ConvergenceException, FunctionEvaluationException {
        return solve(p, min, max);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double solve(final double min, final double max, final double initial)
        throws ConvergenceException, FunctionEvaluationException {
        return solve(p, min, max, initial);
    }

    /**
     * Find a real root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f function to solve (must be polynomial)
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @param maxEval Maximum number of evaluations.
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws ConvergenceException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max, initial);
    }

    /**
     * Find a real root in the given interval with initial value.
     * <p>
     * Requires bracketing condition.</p>
     *
     * @param f function to solve (must be polynomial)
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max, final double initial)
        throws ConvergenceException, FunctionEvaluationException {

        // check for zeros before verifying bracketing
        if (f.value(min) == 0.0) {
            return min;
        }
        if (f.value(max) == 0.0) {
            return max;
        }
        if (f.value(initial) == 0.0) {
            return initial;
        }

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
     * Despite the bracketing condition, the root returned by solve(Complex[],
     * Complex) may not be a real zero inside [min, max]. For example,
     * p(x) = x^3 + 1, min = -2, max = 2, initial = 0. We can either try
     * another initial value, or, as we did here, call solveAll() to obtain
     * all roots and pick up the one that we're looking for.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param maxEval Maximum number of evaluations.
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     */
    @Override
    public double solve(int maxEval, final UnivariateRealFunction f,
                        final double min, final double max)
        throws ConvergenceException, FunctionEvaluationException {
        setMaximalIterationCount(maxEval);
        return solve(f, min, max);
    }

    /**
     * Find a real root in the given interval.
     * <p>
     * Despite the bracketing condition, the root returned by solve(Complex[],
     * Complex) may not be a real zero inside [min, max]. For example,
     * p(x) = x^3 + 1, min = -2, max = 2, initial = 0. We can either try
     * another initial value, or, as we did here, call solveAll() to obtain
     * all roots and pick up the one that we're looking for.</p>
     *
     * @param f the function to solve
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    @Deprecated
    public double solve(final UnivariateRealFunction f,
                        final double min, final double max)
        throws ConvergenceException, FunctionEvaluationException {

        // check function type
        if (!(f instanceof PolynomialFunction)) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FUNCTION_NOT_POLYNOMIAL);
        }

        // check for zeros before verifying bracketing
        if (f.value(min) == 0.0) { return min; }
        if (f.value(max) == 0.0) { return max; }
        verifyBracketing(min, max, f);

        double coefficients[] = ((PolynomialFunction) f).getCoefficients();
        Complex c[] = new Complex[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            c[i] = new Complex(coefficients[i], 0.0);
        }
        Complex initial = new Complex(0.5 * (min + max), 0.0);
        Complex z = solve(c, initial);
        if (isRootOK(min, max, z)) {
            setResult(z.getReal(), iterationCount);
            return result;
        }

        // solve all roots and select the one we're seeking
        Complex[] root = solveAll(c, initial);
        for (int i = 0; i < root.length; i++) {
            if (isRootOK(min, max, root[i])) {
                setResult(root[i].getReal(), iterationCount);
                return result;
            }
        }

        // should never happen
        throw new ConvergenceException();
    }

    /**
     * Returns true iff the given complex root is actually a real zero
     * in the given interval, within the solver tolerance level.
     *
     * @param min the lower bound for the interval
     * @param max the upper bound for the interval
     * @param z the complex root
     * @return true iff z is the sought-after real zero
     */
    protected boolean isRootOK(double min, double max, Complex z) {
        double tolerance = FastMath.max(relativeAccuracy * z.abs(), absoluteAccuracy);
        return (isSequence(min, z.getReal(), max)) &&
               (FastMath.abs(z.getImaginary()) <= tolerance ||
                z.abs() <= functionValueAccuracy);
    }

    /**
     * Find all complex roots for the polynomial with the given coefficients,
     * starting from the given initial value.
     *
     * @param coefficients the polynomial coefficients array
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws ConvergenceException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2.
     */
    @Deprecated
    public Complex[] solveAll(double coefficients[], double initial) throws
        ConvergenceException, FunctionEvaluationException {

        Complex c[] = new Complex[coefficients.length];
        Complex z = new Complex(initial, 0.0);
        for (int i = 0; i < c.length; i++) {
            c[i] = new Complex(coefficients[i], 0.0);
        }
        return solveAll(c, z);
    }

    /**
     * Find all complex roots for the polynomial with the given coefficients,
     * starting from the given initial value.
     *
     * @param coefficients the polynomial coefficients array
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2.
     */
    @Deprecated
    public Complex[] solveAll(Complex coefficients[], Complex initial) throws
        MaxIterationsExceededException, FunctionEvaluationException {

        int n = coefficients.length - 1;
        int iterationCount = 0;
        if (n < 1) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NON_POSITIVE_POLYNOMIAL_DEGREE, n);
        }
        Complex c[] = new Complex[n+1];    // coefficients for deflated polynomial
        for (int i = 0; i <= n; i++) {
            c[i] = coefficients[i];
        }

        // solve individual root successively
        Complex root[] = new Complex[n];
        for (int i = 0; i < n; i++) {
            Complex subarray[] = new Complex[n-i+1];
            System.arraycopy(c, 0, subarray, 0, subarray.length);
            root[i] = solve(subarray, initial);
            // polynomial deflation using synthetic division
            Complex newc = c[n-i];
            Complex oldc = null;
            for (int j = n-i-1; j >= 0; j--) {
                oldc = c[j];
                c[j] = newc;
                newc = oldc.add(newc.multiply(root[i]));
            }
            iterationCount += this.iterationCount;
        }

        resultComputed = true;
        this.iterationCount = iterationCount;
        return root;
    }

    /**
     * Find a complex root for the polynomial with the given coefficients,
     * starting from the given initial value.
     *
     * @param coefficients the polynomial coefficients array
     * @param initial the start value to use
     * @return the point at which the function value is zero
     * @throws MaxIterationsExceededException if the maximum iteration count is exceeded
     * or the solver detects convergence problems otherwise
     * @throws FunctionEvaluationException if an error occurs evaluating the function
     * @throws IllegalArgumentException if any parameters are invalid
     * @deprecated in 2.2.
     */
    @Deprecated
    public Complex solve(Complex coefficients[], Complex initial) throws
        MaxIterationsExceededException, FunctionEvaluationException {

        int n = coefficients.length - 1;
        if (n < 1) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NON_POSITIVE_POLYNOMIAL_DEGREE, n);
        }
        Complex N  = new Complex(n,     0.0);
        Complex N1 = new Complex(n - 1, 0.0);

        int i = 1;
        Complex pv = null;
        Complex dv = null;
        Complex d2v = null;
        Complex G = null;
        Complex G2 = null;
        Complex H = null;
        Complex delta = null;
        Complex denominator = null;
        Complex z = initial;
        Complex oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        while (i <= maximalIterationCount) {
            // Compute pv (polynomial value), dv (derivative value), and
            // d2v (second derivative value) simultaneously.
            pv = coefficients[n];
            dv = Complex.ZERO;
            d2v = Complex.ZERO;
            for (int j = n-1; j >= 0; j--) {
                d2v = dv.add(z.multiply(d2v));
                dv = pv.add(z.multiply(dv));
                pv = coefficients[j].add(z.multiply(pv));
            }
            d2v = d2v.multiply(new Complex(2.0, 0.0));

            // check for convergence
            double tolerance = FastMath.max(relativeAccuracy * z.abs(),
                                        absoluteAccuracy);
            if ((z.subtract(oldz)).abs() <= tolerance) {
                resultComputed = true;
                iterationCount = i;
                return z;
            }
            if (pv.abs() <= functionValueAccuracy) {
                resultComputed = true;
                iterationCount = i;
                return z;
            }

            // now pv != 0, calculate the new approximation
            G = dv.divide(pv);
            G2 = G.multiply(G);
            H = G2.subtract(d2v.divide(pv));
            delta = N1.multiply((N.multiply(H)).subtract(G2));
            // choose a denominator larger in magnitude
            Complex deltaSqrt = delta.sqrt();
            Complex dplus = G.add(deltaSqrt);
            Complex dminus = G.subtract(deltaSqrt);
            denominator = dplus.abs() > dminus.abs() ? dplus : dminus;
            // Perturb z if denominator is zero, for instance,
            // p(x) = x^3 + 1, z = 0.
            if (denominator.equals(new Complex(0.0, 0.0))) {
                z = z.add(new Complex(absoluteAccuracy, absoluteAccuracy));
                oldz = new Complex(Double.POSITIVE_INFINITY,
                                   Double.POSITIVE_INFINITY);
            } else {
                oldz = z;
                z = z.subtract(N.divide(denominator));
            }
            i++;
        }
        throw new MaxIterationsExceededException(maximalIterationCount);
    }
}

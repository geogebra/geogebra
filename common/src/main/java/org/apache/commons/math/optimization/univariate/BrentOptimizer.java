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
package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.util.FastMath;

/**
 * Implements Richard Brent's algorithm (from his book "Algorithms for
 * Minimization without Derivatives", p. 79) for finding minima of real
 * univariate functions. This implementation is an adaptation partly
 * based on the Python code from SciPy (module "optimize.py" v0.5).
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 2.0
 */
public class BrentOptimizer extends AbstractUnivariateRealOptimizer {
    /**
     * Golden section.
     */
    private static final double GOLDEN_SECTION = 0.5 * (3 - FastMath.sqrt(5));

    /**
     * Construct a solver.
     */
    public BrentOptimizer() {
        setMaxEvaluations(1000);
        setMaximalIterationCount(100);
        setAbsoluteAccuracy(1e-11);
        setRelativeAccuracy(1e-9);
    }

    /** {@inheritDoc} */
    @Override
    protected double doOptimize()
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return localMin(getGoalType() == GoalType.MINIMIZE,
                        getMin(), getStartValue(), getMax(),
                        getRelativeAccuracy(), getAbsoluteAccuracy());
    }

    /**
     * Find the minimum of the function within the interval {@code (lo, hi)}.
     *
     * If the function is defined on the interval {@code (lo, hi)}, then
     * this method finds an approximation {@code x} to the point at which
     * the function attains its minimum.<br/>
     * {@code t} and {@code eps} define a tolerance {@code tol = eps |x| + t}
     * and the function is never evaluated at two points closer together than
     * {@code tol}. {@code eps} should be no smaller than <em>2 macheps</em> and
     * preferable not much less than <em>sqrt(macheps)</em>, where
     * <em>macheps</em> is the relative machine precision. {@code t} should be
     * positive.
     * @param isMinim {@code true} when minimizing the function.
     * @param lo Lower bound of the interval.
     * @param mid Point inside the interval {@code [lo, hi]}.
     * @param hi Higher bound of the interval.
     * @param eps Relative accuracy.
     * @param t Absolute accuracy.
     * @return the optimum point.
     * @throws MaxIterationsExceededException if the maximum iteration count
     * is exceeded.
     * @throws FunctionEvaluationException if an error occurs evaluating the function.
     */
    private double localMin(boolean isMinim,
                            double lo, double mid, double hi,
                            double eps, double t)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        if (eps <= 0) {
            throw new NotStrictlyPositiveException(eps);
        }
        if (t <= 0) {
            throw new NotStrictlyPositiveException(t);
        }
        double a;
        double b;
        if (lo < hi) {
            a = lo;
            b = hi;
        } else {
            a = hi;
            b = lo;
        }

        double x = mid;
        double v = x;
        double w = x;
        double d = 0;
        double e = 0;
        double fx = computeObjectiveValue(x);
        if (!isMinim) {
            fx = -fx;
        }
        double fv = fx;
        double fw = fx;

        while (true) {
            double m = 0.5 * (a + b);
            final double tol1 = eps * FastMath.abs(x) + t;
            final double tol2 = 2 * tol1;

            // Check stopping criterion.
            if (FastMath.abs(x - m) > tol2 - 0.5 * (b - a)) {
                double p = 0;
                double q = 0;
                double r = 0;
                double u = 0;

                if (FastMath.abs(e) > tol1) { // Fit parabola.
                    r = (x - w) * (fx - fv);
                    q = (x - v) * (fx - fw);
                    p = (x - v) * q - (x - w) * r;
                    q = 2 * (q - r);

                    if (q > 0) {
                        p = -p;
                    } else {
                        q = -q;
                    }

                    r = e;
                    e = d;

                    if (p > q * (a - x) &&
                        p < q * (b - x) &&
                        FastMath.abs(p) < FastMath.abs(0.5 * q * r)) {
                        // Parabolic interpolation step.
                        d = p / q;
                        u = x + d;

                        // f must not be evaluated too close to a or b.
                        if (u - a < tol2 || b - u < tol2) {
                            if (x <= m) {
                                d = tol1;
                            } else {
                                d = -tol1;
                            }
                        }
                    } else {
                        // Golden section step.
                        if (x < m) {
                            e = b - x;
                        } else {
                            e = a - x;
                        }
                        d = GOLDEN_SECTION * e;
                    }
                } else {
                    // Golden section step.
                    if (x < m) {
                        e = b - x;
                    } else {
                        e = a - x;
                    }
                    d = GOLDEN_SECTION * e;
                }

                // Update by at least "tol1".
                if (FastMath.abs(d) < tol1) {
                    if (d >= 0) {
                        u = x + tol1;
                    } else {
                        u = x - tol1;
                    }
                } else {
                    u = x + d;
                }

                double fu = computeObjectiveValue(u);
                if (!isMinim) {
                    fu = -fu;
                }

                // Update a, b, v, w and x.
                if (fu <= fx) {
                    if (u < x) {
                        b = x;
                    } else {
                        a = x;
                    }
                    v = w;
                    fv = fw;
                    w = x;
                    fw = fx;
                    x = u;
                    fx = fu;
                } else {
                    if (u < x) {
                        a = u;
                    } else {
                        b = u;
                    }
                    if (fu <= fw || w == x) {
                        v = w;
                        fv = fw;
                        w = u;
                        fw = fu;
                    } else if (fu <= fv || v == x || v == w) {
                        v = u;
                        fv = fu;
                    }
                }
            } else { // termination
                setFunctionValue(isMinim ? fx : -fx);
                return x;
            }
            incrementIterationsCounter();
        }
    }
}

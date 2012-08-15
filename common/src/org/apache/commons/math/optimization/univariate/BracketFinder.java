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
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.optimization.GoalType;

/**
 * Provide an interval that brackets a local optimum of a function.
 * This code is based on a Python implementation (from <em>SciPy</em>,
 * module {@code optimize.py} v0.5).
 * @version $Revision$ $Date$
 * @since 2.2
 */
public class BracketFinder {
    /** Tolerance to avoid division by zero. */
    private static final double EPS_MIN = 1e-21;
    /**
     * Golden section.
     */
    private static final double GOLD = 1.618034;
    /**
     * Factor for expanding the interval.
     */
    private final double growLimit;
    /**
     * Maximum number of iterations.
     */
    private final int maxIterations;
    /**
     * Number of iterations.
     */
    private int iterations;
    /**
     * Number of function evaluations.
     */
    private int evaluations;
    /**
     * Lower bound of the bracket.
     */
    private double lo;
    /**
     * Higher bound of the bracket.
     */
    private double hi;
    /**
     * Point inside the bracket.
     */
    private double mid;
    /**
     * Function value at {@link #lo}.
     */
    private double fLo;
    /**
     * Function value at {@link #hi}.
     */
    private double fHi;
    /**
     * Function value at {@link #mid}.
     */
    private double fMid;

    /**
     * Constructor with default values {@code 100, 50} (see the
     * {@link #BracketFinder(double,int) other constructor}).
     */
    public BracketFinder() {
        this(100, 50);
    }

    /**
     * Create a bracketing interval finder.
     *
     * @param growLimit Expanding factor.
     * @param maxIterations Maximum number of iterations allowed for finding
     * a bracketing interval.
     */
    public BracketFinder(double growLimit,
                         int maxIterations) {
        if (growLimit <= 0) {
            throw new NotStrictlyPositiveException(growLimit);
        }
        if (maxIterations <= 0) {
            throw new NotStrictlyPositiveException(maxIterations);
        }

        this.growLimit = growLimit;
        this.maxIterations = maxIterations;
    }

    /**
     * Search new points that bracket a local optimum of the function.
     *
     * @param func Function whose optimum should be bracketted.
     * @param goal {@link GoalType Goal type}.
     * @param xA Initial point.
     * @param xB Initial point.
     * @throws MaxIterationsExceededException if the maximum iteration count
     * is exceeded.
     * @throws FunctionEvaluationException if an error occurs evaluating the function.
     */
    public void search(UnivariateRealFunction func,
                       GoalType goal,
                       double xA,
                       double xB)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        reset();
        final boolean isMinim = goal == GoalType.MINIMIZE;

        double fA = eval(func, xA);
        double fB = eval(func, xB);
        if (isMinim ?
            fA < fB :
            fA > fB) {
            double tmp = xA;
            xA = xB;
            xB = tmp;

            tmp = fA;
            fA = fB;
            fB = tmp;
        }

        double xC = xB + GOLD * (xB - xA);
        double fC = eval(func, xC);

        while (isMinim ? fC < fB : fC > fB) {
            if (++iterations > maxIterations) {
                throw new MaxIterationsExceededException(maxIterations);
            }

            double tmp1 = (xB - xA) * (fB - fC);
            double tmp2 = (xB - xC) * (fB - fA);

            double val = tmp2 - tmp1;
            double denom = Math.abs(val) < EPS_MIN ? 2 * EPS_MIN : 2 * val;

            double w = xB - ((xB - xC) * tmp2 - (xB -xA) * tmp1) / denom;
            double wLim = xB + growLimit * (xC - xB);

            double fW;
            if ((w - xC) * (xB - w) > 0) {
                fW = eval(func, w);
                if (isMinim ?
                    fW < fC :
                    fW > fC) {
                    xA = xB;
                    xB = w;
                    fA = fB;
                    fB = fW;
                    break;
                } else if (isMinim ?
                           fW > fB :
                           fW < fB) {
                    xC = w;
                    fC = fW;
                    break;
                }
                w = xC + GOLD * (xC - xB);
                fW = eval(func, w);
            } else if ((w - wLim) * (wLim - xC) >= 0) {
                w = wLim;
                fW = eval(func, w);
            } else if ((w - wLim) * (xC - w) > 0) {
                fW = eval(func, w);
                if (isMinim ?
                    fW < fC :
                    fW > fC) {
                    xB = xC;
                    xC = w;
                    w = xC + GOLD * (xC -xB);
                    fB = fC;
                    fC =fW;
                    fW = eval(func, w);
                }
            } else {
                w = xC + GOLD * (xC - xB);
                fW = eval(func, w);
            }

            xA = xB;
            xB = xC;
            xC = w;
            fA = fB;
            fB = fC;
            fC = fW;
        }

        lo = xA;
        mid = xB;
        hi = xC;
        fLo = fA;
        fMid = fB;
        fHi = fC;
    }

    /**
     * @return the number of iterations.
     */
    public int getIterations() {
        return iterations;
    }
    /**
     * @return the number of evaluations.
     */
    public int getEvaluations() {
        return evaluations;
    }

    /**
     * @return the lower bound of the bracket.
     * @see #getFLow()
     */
    public double getLo() {
        return lo;
    }

    /**
     * Get function value at {@link #getLo()}.
     * @return function value at {@link #getLo()}
     */
    public double getFLow() {
        return fLo;
    }

    /**
     * @return the higher bound of the bracket.
     * @see #getFHi()
     */
    public double getHi() {
        return hi;
    }

    /**
     * Get function value at {@link #getHi()}.
     * @return function value at {@link #getHi()}
     */
    public double getFHi() {
        return fHi;
    }

    /**
     * @return a point in the middle of the bracket.
     * @see #getFMid()
     */
    public double getMid() {
        return mid;
    }

    /**
     * Get function value at {@link #getMid()}.
     * @return function value at {@link #getMid()}
     */
    public double getFMid() {
        return fMid;
    }

    /**
     * @param f Function.
     * @param x Argument.
     * @return {@code f(x)}
     * @throws FunctionEvaluationException if function cannot be evaluated at x
     */
    private double eval(UnivariateRealFunction f, double x)
        throws FunctionEvaluationException {

        ++evaluations;
        return f.value(x);
    }

    /**
     * Reset internal state.
     */
    private void reset() {
        iterations = 0;
        evaluations = 0;
    }
}

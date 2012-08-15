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

package org.apache.commons.math.optimization.direct;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.general.AbstractScalarDifferentiableOptimizer;
import org.apache.commons.math.optimization.univariate.AbstractUnivariateRealOptimizer;
import org.apache.commons.math.optimization.univariate.BracketFinder;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;

/**
 * Powell algorithm.
 * This code is translated and adapted from the Python version of this
 * algorithm (as implemented in module {@code optimize.py} v0.5 of
 * <em>SciPy</em>).
 *
 * @version $Revision$ $Date$
 * @since 2.2
 */
public class PowellOptimizer
    extends AbstractScalarDifferentiableOptimizer {
    /**
     * Default relative tolerance for line search ({@value}).
     */
    public static final double DEFAULT_LS_RELATIVE_TOLERANCE = 1e-7;
    /**
     * Default absolute tolerance for line search ({@value}).
     */
    public static final double DEFAULT_LS_ABSOLUTE_TOLERANCE = 1e-11;
    /**
     * Line search.
     */
    private final LineSearch line;

    /**
     * Constructor with default line search tolerances (see the
     * {@link #PowellOptimizer(double,double) other constructor}).
     */
    public PowellOptimizer() {
        this(DEFAULT_LS_RELATIVE_TOLERANCE,
             DEFAULT_LS_ABSOLUTE_TOLERANCE);
    }

    /**
     * Constructor with default absolute line search tolerances (see
     * the {@link #PowellOptimizer(double,double) other constructor}).
     *
     * @param lsRelativeTolerance Relative error tolerance for
     * the line search algorithm ({@link BrentOptimizer}).
     */
    public PowellOptimizer(double lsRelativeTolerance) {
        this(lsRelativeTolerance,
             DEFAULT_LS_ABSOLUTE_TOLERANCE);
    }

    /**
     * @param lsRelativeTolerance Relative error tolerance for
     * the line search algorithm ({@link BrentOptimizer}).
     * @param lsAbsoluteTolerance Relative error tolerance for
     * the line search algorithm ({@link BrentOptimizer}).
     */
    public PowellOptimizer(double lsRelativeTolerance,
                           double lsAbsoluteTolerance) {
        line = new LineSearch(lsRelativeTolerance,
                              lsAbsoluteTolerance);
    }

    /** {@inheritDoc} */
    @Override
    protected RealPointValuePair doOptimize()
        throws FunctionEvaluationException,
               OptimizationException {
        final double[] guess = point.clone();
        final int n = guess.length;

        final double[][] direc = new double[n][n];
        for (int i = 0; i < n; i++) {
            direc[i][i] = 1;
        }

        double[] x = guess;
        double fVal = computeObjectiveValue(x);
        double[] x1 = x.clone();
        while (true) {
            incrementIterationsCounter();

            double fX = fVal;
            double fX2 = 0;
            double delta = 0;
            int bigInd = 0;
            double alphaMin = 0;

            for (int i = 0; i < n; i++) {
                final double[] d = /* Arrays.*/ copyOf(direc[i], n); // Java 1.5 does not support Arrays.copyOf()

                fX2 = fVal;

                line.search(x, d);
                fVal = line.getValueAtOptimum();
                alphaMin = line.getOptimum();
                final double[][] result = newPointAndDirection(x, d, alphaMin);
                x = result[0];

                if ((fX2 - fVal) > delta) {
                    delta = fX2 - fVal;
                    bigInd = i;
                }
            }

            final RealPointValuePair previous = new RealPointValuePair(x1, fX);
            final RealPointValuePair current = new RealPointValuePair(x, fVal);
            if (getConvergenceChecker().converged(getIterations(), previous, current)) {
                if (goal == GoalType.MINIMIZE) {
                    return (fVal < fX) ? current : previous;
                } else {
                    return (fVal > fX) ? current : previous;
                }
            }

            final double[] d = new double[n];
            final double[] x2 = new double[n];
            for (int i = 0; i < n; i++) {
                d[i] = x[i] - x1[i];
                x2[i] = 2 * x[i] - x1[i];
            }

            x1 = x.clone();
            fX2 = computeObjectiveValue(x2);

            if (fX > fX2) {
                double t = 2 * (fX + fX2 - 2 * fVal);
                double temp = fX - fVal - delta;
                t *= temp * temp;
                temp = fX - fX2;
                t -= delta * temp * temp;

                if (t < 0.0) {
                    line.search(x, d);
                    fVal = line.getValueAtOptimum();
                    alphaMin = line.getOptimum();
                    final double[][] result = newPointAndDirection(x, d, alphaMin);
                    x = result[0];

                    final int lastInd = n - 1;
                    direc[bigInd] = direc[lastInd];
                    direc[lastInd] = result[1];
                }
            }
        }
    }

    /**
     * Compute a new point (in the original space) and a new direction
     * vector, resulting from the line search.
     * The parameters {@code p} and {@code d} will be changed in-place.
     *
     * @param p Point used in the line search.
     * @param d Direction used in the line search.
     * @param optimum Optimum found by the line search.
     * @return a 2-element array containing the new point (at index 0) and
     * the new direction (at index 1).
     */
    private double[][] newPointAndDirection(double[] p,
                                            double[] d,
                                            double optimum) {
        final int n = p.length;
        final double[][] result = new double[2][n];
        final double[] nP = result[0];
        final double[] nD = result[1];
        for (int i = 0; i < n; i++) {
            nD[i] = d[i] * optimum;
            nP[i] = p[i] + nD[i];
        }
        return result;
    }

    /**
     * Class for finding the minimum of the objective function along a given
     * direction.
     */
    private class LineSearch {
        /**
         * Optimizer.
         */
        private final AbstractUnivariateRealOptimizer optim = new BrentOptimizer();
        /**
         * Automatic bracketing.
         */
        private final BracketFinder bracket = new BracketFinder();
        /**
         * Value of the optimum.
         */
        private double optimum = Double.NaN;
        /**
         * Value of the objective function at the optimum.
         */
        private double valueAtOptimum = Double.NaN;

        /**
         * @param relativeTolerance Relative tolerance.
         * @param absoluteTolerance Absolute tolerance.
         */
        public LineSearch(double relativeTolerance,
                          double absoluteTolerance) {
            optim.setRelativeAccuracy(relativeTolerance);
            optim.setAbsoluteAccuracy(absoluteTolerance);
        }

        /**
         * Find the minimum of the function {@code f(p + alpha * d)}.
         *
         * @param p Starting point.
         * @param d Search direction.
         * @throws FunctionEvaluationException if function cannot be evaluated at some test point
         * @throws OptimizationException if algorithm fails to converge
         */
        public void search(final double[] p, final double[] d)
            throws OptimizationException, FunctionEvaluationException {

            // Reset.
            optimum = Double.NaN;
            valueAtOptimum = Double.NaN;

            try {
                final int n = p.length;
                final UnivariateRealFunction f = new UnivariateRealFunction() {
                        public double value(double alpha)
                            throws FunctionEvaluationException {

                            final double[] x = new double[n];
                            for (int i = 0; i < n; i++) {
                                x[i] = p[i] + alpha * d[i];
                            }
                            final double obj;
                            obj = computeObjectiveValue(x);
                            return obj;
                        }
                    };

                bracket.search(f, goal, 0, 1);
                optimum = optim.optimize(f, goal,
                                         bracket.getLo(),
                                         bracket.getHi(),
                                         bracket.getMid());
                valueAtOptimum = optim.getFunctionValue();
            } catch (MaxIterationsExceededException e) {
                throw new OptimizationException(e);
            }
        }

        /**
         * @return the optimum.
         */
        public double getOptimum() {
            return optimum;
        }
        /**
         * @return the value of the function at the optimum.
         */
        public double getValueAtOptimum() {
            return valueAtOptimum;
        }
    }

    /**
     * Java 1.5 does not support Arrays.copyOf()
     *
     * @param source the array to be copied
     * @param newLen the length of the copy to be returned
     * @return the copied array, truncated or padded as necessary.
     */
     private double[] copyOf(double[] source, int newLen) {
         double[] output = new double[newLen];
         System.arraycopy(source, 0, output, 0, Math.min(source.length, newLen));
         return output;
     }

}

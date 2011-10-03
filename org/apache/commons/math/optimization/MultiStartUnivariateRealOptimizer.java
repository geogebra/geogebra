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
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.util.FastMath;

/**
 * Special implementation of the {@link UnivariateRealOptimizer} interface adding
 * multi-start features to an existing optimizer.
 * <p>
 * This class wraps a classical optimizer to use it several times in
 * turn with different starting points in order to avoid being trapped
 * into a local extremum when looking for a global one.
 * </p>
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 2.0
 */
public class MultiStartUnivariateRealOptimizer implements UnivariateRealOptimizer {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 5983375963110961019L;

    /** Underlying classical optimizer. */
    private final UnivariateRealOptimizer optimizer;

    /** Maximal number of iterations allowed. */
    private int maxIterations;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of iterations already performed for all starts. */
    private int totalIterations;

    /** Number of evaluations already performed for all starts. */
    private int totalEvaluations;

    /** Number of starts to go. */
    private int starts;

    /** Random generator for multi-start. */
    private RandomGenerator generator;

    /** Found optima. */
    private double[] optima;

    /** Found function values at optima. */
    private double[] optimaValues;

    /**
     * Create a multi-start optimizer from a single-start optimizer
     * @param optimizer single-start optimizer to wrap
     * @param starts number of starts to perform (including the
     * first one), multi-start is disabled if value is less than or
     * equal to 1
     * @param generator random generator to use for restarts
     */
    public MultiStartUnivariateRealOptimizer(final UnivariateRealOptimizer optimizer,
                                             final int starts,
                                             final RandomGenerator generator) {
        this.optimizer        = optimizer;
        this.totalIterations  = 0;
        this.starts           = starts;
        this.generator        = generator;
        this.optima           = null;
        setMaximalIterationCount(Integer.MAX_VALUE);
        setMaxEvaluations(Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public double getFunctionValue() {
        return optimaValues[0];
    }

    /** {@inheritDoc} */
    public double getResult() {
        return optima[0];
    }

    /** {@inheritDoc} */
    public double getAbsoluteAccuracy() {
        return optimizer.getAbsoluteAccuracy();
    }

    /** {@inheritDoc} */
    public int getIterationCount() {
        return totalIterations;
    }

    /** {@inheritDoc} */
    public int getMaximalIterationCount() {
        return maxIterations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return totalEvaluations;
    }

    /** {@inheritDoc} */
    public double getRelativeAccuracy() {
        return optimizer.getRelativeAccuracy();
    }

    /** {@inheritDoc} */
    public void resetAbsoluteAccuracy() {
        optimizer.resetAbsoluteAccuracy();
    }

    /** {@inheritDoc} */
    public void resetMaximalIterationCount() {
        optimizer.resetMaximalIterationCount();
    }

    /** {@inheritDoc} */
    public void resetRelativeAccuracy() {
        optimizer.resetRelativeAccuracy();
    }

    /** {@inheritDoc} */
    public void setAbsoluteAccuracy(double accuracy) {
        optimizer.setAbsoluteAccuracy(accuracy);
    }

    /** {@inheritDoc} */
    public void setMaximalIterationCount(int count) {
        this.maxIterations = count;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /** {@inheritDoc} */
    public void setRelativeAccuracy(double accuracy) {
        optimizer.setRelativeAccuracy(accuracy);
    }

    /** Get all the optima found during the last call to {@link
     * #optimize(UnivariateRealFunction, GoalType, double, double) optimize}.
     * <p>The optimizer stores all the optima found during a set of
     * restarts. The {@link #optimize(UnivariateRealFunction, GoalType,
     * double, double) optimize} method returns the best point only. This
     * method returns all the points found at the end of each starts,
     * including the best one already returned by the {@link
     * #optimize(UnivariateRealFunction, GoalType, double, double) optimize}
     * method.
     * </p>
     * <p>
     * The returned array as one element for each start as specified
     * in the constructor. It is ordered with the results from the
     * runs that did converge first, sorted from best to worst
     * objective value (i.e in ascending order if minimizing and in
     * descending order if maximizing), followed by Double.NaN elements
     * corresponding to the runs that did not converge. This means all
     * elements will be NaN if the {@link #optimize(UnivariateRealFunction,
     * GoalType, double, double) optimize} method did throw a {@link
     * ConvergenceException ConvergenceException}). This also means that
     * if the first element is not NaN, it is the best point found across
     * all starts.</p>
     * @return array containing the optima
     * @exception IllegalStateException if {@link #optimize(UnivariateRealFunction,
     * GoalType, double, double) optimize} has not been called
     * @see #getOptimaValues()
     */
    public double[] getOptima() throws IllegalStateException {
        if (optima == null) {
            throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET);
        }
        return optima.clone();
    }

    /** Get all the function values at optima found during the last call to {@link
     * #optimize(UnivariateRealFunction, GoalType, double, double) optimize}.
     * <p>
     * The returned array as one element for each start as specified
     * in the constructor. It is ordered with the results from the
     * runs that did converge first, sorted from best to worst
     * objective value (i.e in ascending order if minimizing and in
     * descending order if maximizing), followed by Double.NaN elements
     * corresponding to the runs that did not converge. This means all
     * elements will be NaN if the {@link #optimize(UnivariateRealFunction,
     * GoalType, double, double) optimize} method did throw a {@link
     * ConvergenceException ConvergenceException}). This also means that
     * if the first element is not NaN, it is the best point found across
     * all starts.</p>
     * @return array containing the optima
     * @exception IllegalStateException if {@link #optimize(UnivariateRealFunction,
     * GoalType, double, double) optimize} has not been called
     * @see #getOptima()
     */
    public double[] getOptimaValues() throws IllegalStateException {
        if (optimaValues == null) {
            throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET);
        }
        return optimaValues.clone();
    }

    /** {@inheritDoc} */
    public double optimize(final UnivariateRealFunction f, final GoalType goalType,
                           final double min, final double max)
        throws ConvergenceException, FunctionEvaluationException {

        optima           = new double[starts];
        optimaValues     = new double[starts];
        totalIterations  = 0;
        totalEvaluations = 0;

        // multi-start loop
        for (int i = 0; i < starts; ++i) {

            try {
                optimizer.setMaximalIterationCount(maxIterations - totalIterations);
                optimizer.setMaxEvaluations(maxEvaluations - totalEvaluations);
                final double bound1 = (i == 0) ? min : min + generator.nextDouble() * (max - min);
                final double bound2 = (i == 0) ? max : min + generator.nextDouble() * (max - min);
                optima[i]       = optimizer.optimize(f, goalType,
                                                     FastMath.min(bound1, bound2),
                                                     FastMath.max(bound1, bound2));
                optimaValues[i] = optimizer.getFunctionValue();
            } catch (FunctionEvaluationException fee) {
                optima[i]       = Double.NaN;
                optimaValues[i] = Double.NaN;
            } catch (ConvergenceException ce) {
                optima[i]       = Double.NaN;
                optimaValues[i] = Double.NaN;
            }

            totalIterations  += optimizer.getIterationCount();
            totalEvaluations += optimizer.getEvaluations();

        }

        // sort the optima from best to worst, followed by NaN elements
        int lastNaN = optima.length;
        for (int i = 0; i < lastNaN; ++i) {
            if (Double.isNaN(optima[i])) {
                optima[i] = optima[--lastNaN];
                optima[lastNaN + 1] = Double.NaN;
                optimaValues[i] = optimaValues[--lastNaN];
                optimaValues[lastNaN + 1] = Double.NaN;
            }
        }

        double currX = optima[0];
        double currY = optimaValues[0];
        for (int j = 1; j < lastNaN; ++j) {
            final double prevY = currY;
            currX = optima[j];
            currY = optimaValues[j];
            if ((goalType == GoalType.MAXIMIZE) ^ (currY < prevY)) {
                // the current element should be inserted closer to the beginning
                int i = j - 1;
                double mIX = optima[i];
                double mIY = optimaValues[i];
                while ((i >= 0) && ((goalType == GoalType.MAXIMIZE) ^ (currY < mIY))) {
                    optima[i + 1]       = mIX;
                    optimaValues[i + 1] = mIY;
                    if (i-- != 0) {
                        mIX = optima[i];
                        mIY = optimaValues[i];
                    } else {
                        mIX = Double.NaN;
                        mIY = Double.NaN;
                    }
                }
                optima[i + 1]       = currX;
                optimaValues[i + 1] = currY;
                currX = optima[j];
                currY = optimaValues[j];
            }
        }

        if (Double.isNaN(optima[0])) {
            throw new OptimizationException(
                    LocalizedFormats.NO_CONVERGENCE_WITH_ANY_START_POINT,
                    starts);
        }

        // return the found point given the best objective function value
        return optima[0];

    }

    /** {@inheritDoc} */
    public double optimize(final UnivariateRealFunction f, final GoalType goalType,
                           final double min, final double max, final double startValue)
            throws ConvergenceException, FunctionEvaluationException {
        return optimize(f, goalType, min, max);
    }
}

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

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.UnivariateRealOptimizer;

/**
 * Provide a default implementation for several functions useful to generic
 * optimizers.
 *
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 2.0
 */
public abstract class AbstractUnivariateRealOptimizer
    extends ConvergingAlgorithmImpl implements UnivariateRealOptimizer {
    /** Indicates where a root has been computed. */
    protected boolean resultComputed;
    /** The last computed root. */
    protected double result;
    /** Value of the function at the last computed result. */
    protected double functionValue;
    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;
    /** Number of evaluations already performed. */
    private int evaluations;
    /** Optimization type */
    private GoalType optimizationGoal;
    /** Lower end of search interval. */
    private double searchMin;
    /** Higher end of search interval. */
    private double searchMax;
    /** Initial guess . */
    private double searchStart;
    /** Function to optimize. */
    private UnivariateRealFunction function;

    /**
     * Construct a solver with given iteration count and accuracy.
     * @param defaultAbsoluteAccuracy maximum absolute error
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the
     * defaultAbsoluteAccuracy is not valid
     * @deprecated in 2.2. Please use the "setter" methods to assign meaningful
     * values to the maximum numbers of iterations and evaluations, and to the
     * absolute and relative accuracy thresholds.
     */
    @Deprecated
    protected AbstractUnivariateRealOptimizer(final int defaultMaximalIterationCount,
                                              final double defaultAbsoluteAccuracy) {
        super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
        resultComputed = false;
        setMaxEvaluations(Integer.MAX_VALUE);
    }

    /**
     * Default constructor.
     * To be removed once the single non-default one has been removed.
     */
    protected AbstractUnivariateRealOptimizer() {}

    /**
     * Check whether a result has been computed.
     * @throws NoDataException if no result has been computed
     * @deprecated in 2.2 (no alternative).
     */
    @Deprecated
    protected void checkResultComputed() {
        if (!resultComputed) {
            throw new NoDataException();
        }
    }

    /** {@inheritDoc} */
    public double getResult() {
        if (!resultComputed) {
            throw new NoDataException();
        }
        return result;
    }

    /** {@inheritDoc} */
    public double getFunctionValue() throws FunctionEvaluationException {
        if (Double.isNaN(functionValue)) {
            final double opt = getResult();
            functionValue = function.value(opt);
        }
        return functionValue;
    }

    /**
     * Convenience function for implementations.
     *
     * @param x the result to set
     * @param fx the result to set
     * @param iterationCount the iteration count to set
     * @deprecated in 2.2 (no alternative).
     */
    @Deprecated
    protected final void setResult(final double x, final double fx,
                                   final int iterationCount) {
        this.result         = x;
        this.functionValue  = fx;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     * @deprecated in 2.2 (no alternative).
     */
    @Deprecated
    protected final void clearResult() {
        this.resultComputed = false;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations;
    }

    /**
     * @return the optimization type.
     */
    public GoalType getGoalType() {
        return optimizationGoal;
    }
    /**
     * @return the lower of the search interval.
     */
    public double getMin() {
        return searchMin;
    }
    /**
     * @return the higher of the search interval.
     */
    public double getMax() {
        return searchMax;
    }
    /**
     * @return the initial guess.
     */
    public double getStartValue() {
        return searchStart;
    }

    /**
     * Compute the objective function value.
     * @param f objective function
     * @param point point at which the objective function must be evaluated
     * @return objective function value at specified point
     * @exception FunctionEvaluationException if the function cannot be evaluated
     * or the maximal number of iterations is exceeded
     * @deprecated in 2.2. Use this {@link #computeObjectiveValue(double)
     * replacement} instead.
     */
    @Deprecated
    protected double computeObjectiveValue(final UnivariateRealFunction f,
                                           final double point)
        throws FunctionEvaluationException {
        if (++evaluations > maxEvaluations) {
            throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations), point);
        }
        return f.value(point);
    }

    /**
     * Compute the objective function value.
     *
     * @param point Point at which the objective function must be evaluated.
     * @return the objective function value at specified point.
     * @exception FunctionEvaluationException if the function cannot be evaluated
     * or the maximal number of iterations is exceeded.
     */
    protected double computeObjectiveValue(double point)
        throws FunctionEvaluationException {
        if (++evaluations > maxEvaluations) {
            resultComputed = false;
            throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations), point);
        }
        return function.value(point);
    }

    /** {@inheritDoc} */
    public double optimize(UnivariateRealFunction f, GoalType goal,
                           double min, double max, double startValue)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        // Initialize.
        this.searchMin = min;
        this.searchMax = max;
        this.searchStart = startValue;
        this.optimizationGoal = goal;
        this.function = f;

        // Reset.
        functionValue = Double.NaN;
        evaluations = 0;
        resetIterationsCounter();

        // Perform computation.
        result = doOptimize();
        resultComputed = true;

        return result;
    }

    /**
     * Set the value at the optimum.
     *
     * @param functionValue Value of the objective function at the optimum.
     */
    protected void setFunctionValue(double functionValue) {
        this.functionValue = functionValue;
    }

    /** {@inheritDoc} */
    public double optimize(UnivariateRealFunction f, GoalType goal,
                           double min, double max)
        throws MaxIterationsExceededException, FunctionEvaluationException {
        return optimize(f, goal, min, max, min + 0.5 * (max - min));
    }

    /**
     * Method for implementing actual optimization algorithms in derived
     * classes.
     *
     * From version 3.0 onwards, this method will be abstract - i.e.
     * concrete implementations will have to implement it.  If this method
     * is not implemented, subclasses must override
     * {@link #optimize(UnivariateRealFunction, GoalType, double, double)}.
     *
     * @return the optimum.
     * @throws MaxIterationsExceededException if the maximum iteration count
     * is exceeded.
     * @throws FunctionEvaluationException if an error occurs evaluating
     * the function.
     */
    protected double doOptimize()
        throws MaxIterationsExceededException, FunctionEvaluationException {
        throw new MathUnsupportedOperationException(LocalizedFormats.NOT_OVERRIDEN);
    }
}

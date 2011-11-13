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
package org.apache.commons.math.distribution;

import java.io.Serializable;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;

/**
 * Base class for continuous distributions.  Default implementations are
 * provided for some of the methods that do not vary from distribution to
 * distribution.
 *
 * @version $Revision: 1073498 $ $Date: 2011-02-22 21:57:26 +0100 (mar. 22 févr. 2011) $
 */
public abstract class AbstractContinuousDistribution
    extends AbstractDistribution
    implements ContinuousDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -38038050983108802L;

    /**
     * RandomData instance used to generate samples from the distribution
     * @since 2.2
     */
    protected final RandomDataImpl randomData = new RandomDataImpl();

    /**
     * Solver absolute accuracy for inverse cumulative computation
     * @since 2.1
     */
    private double solverAbsoluteAccuracy = BrentSolver.DEFAULT_ABSOLUTE_ACCURACY;

    /**
     * Default constructor.
     */
    protected AbstractContinuousDistribution() {
        super();
    }

    /**
     * Return the probability density for a particular point.
     * @param x  The point at which the density should be computed.
     * @return  The pdf at point x.
     * @throws MathRuntimeException if the specialized class hasn't implemented this function
     * @since 2.1
     */
    public double density(double x) throws MathRuntimeException {
        throw new MathRuntimeException(new UnsupportedOperationException(),
                LocalizedFormats.NO_DENSITY_FOR_THIS_DISTRIBUTION);
    }

    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     * @throws MathException if the inverse cumulative probability can not be
     *         computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     */
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p < 0.0 || p > 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.OUT_OF_RANGE_SIMPLE, p, 0.0, 1.0);
        }

        // by default, do simple root finding using bracketing and default solver.
        // subclasses can override if there is a better method.
        UnivariateRealFunction rootFindingFunction =
            new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                double ret = Double.NaN;
                try {
                    ret = cumulativeProbability(x) - p;
                } catch (MathException ex) {
                    throw new FunctionEvaluationException(x, ex.getSpecificPattern(), ex.getGeneralPattern(), ex.getArguments());
                }
                if (Double.isNaN(ret)) {
                    throw new FunctionEvaluationException(x, LocalizedFormats.CUMULATIVE_PROBABILITY_RETURNED_NAN, x, p);
                }
                return ret;
            }
        };

        // Try to bracket root, test domain endpoints if this fails
        double lowerBound = getDomainLowerBound(p);
        double upperBound = getDomainUpperBound(p);
        double[] bracket = null;
        try {
            bracket = UnivariateRealSolverUtils.bracket(
                    rootFindingFunction, getInitialDomain(p),
                    lowerBound, upperBound);
        }  catch (ConvergenceException ex) {
            /*
             * Check domain endpoints to see if one gives value that is within
             * the default solver's defaultAbsoluteAccuracy of 0 (will be the
             * case if density has bounded support and p is 0 or 1).
             */
            if (FastMath.abs(rootFindingFunction.value(lowerBound)) < getSolverAbsoluteAccuracy()) {
                return lowerBound;
            }
            if (FastMath.abs(rootFindingFunction.value(upperBound)) < getSolverAbsoluteAccuracy()) {
                return upperBound;
            }
            // Failed bracket convergence was not because of corner solution
            throw new MathException(ex);
        }

        // find root
        double root = UnivariateRealSolverUtils.solve(rootFindingFunction,
                // override getSolverAbsoluteAccuracy() to use a Brent solver with
                // absolute accuracy different from BrentSolver default
                bracket[0],bracket[1], getSolverAbsoluteAccuracy());
        return root;
    }

    /**
     * Reseeds the random generator used to generate samples.
     *
     * @param seed the new seed
     * @since 2.2
     */
    public void reseedRandomGenerator(long seed) {
        randomData.reSeed(seed);
    }

    /**
     * Generates a random value sampled from this distribution. The default
     * implementation uses the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling"> inversion method.</a>
     *
     * @return random value
     * @since 2.2
     * @throws MathException if an error occurs generating the random value
     */
    public double sample() throws MathException {
        return randomData.nextInversionDeviate(this);
    }

    /**
     * Generates a random sample from the distribution.  The default implementation
     * generates the sample by calling {@link #sample()} in a loop.
     *
     * @param sampleSize number of random values to generate
     * @since 2.2
     * @return an array representing the random sample
     * @throws MathException if an error occurs generating the sample
     * @throws IllegalArgumentException if sampleSize is not positive
     */
    public double[] sample(int sampleSize) throws MathException {
        if (sampleSize <= 0) {
            MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SAMPLE_SIZE, sampleSize);
        }
        double[] out = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    protected abstract double getInitialDomain(double p);

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code>
     */
    protected abstract double getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code>
     */
    protected abstract double getDomainUpperBound(double p);

    /**
     * Returns the solver absolute accuracy for inverse cumulative computation.
     *
     * @return the maximum absolute error in inverse cumulative probability estimates
     * @since 2.1
     */
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

}

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

import org.apache.commons.math.MathException;

/**
 * The default implementation of {@link ChiSquaredDistribution}
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class ChiSquaredDistributionImpl
    extends AbstractContinuousDistribution
    implements ChiSquaredDistribution, Serializable  {

    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier */
    private static final long serialVersionUID = -8352658048349159782L;

    /** Internal Gamma distribution. */
    private GammaDistribution gamma;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     * @param df degrees of freedom.
     */
    public ChiSquaredDistributionImpl(double df) {
        this(df, new GammaDistributionImpl(df / 2.0, 2.0));
    }

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom.
     * @param df degrees of freedom.
     * @param g the underlying gamma distribution used to compute probabilities.
     * @since 1.2
     * @deprecated as of 2.1 (to avoid possibly inconsistent state, the
     * "GammaDistribution" will be instantiated internally)
     */
    @Deprecated
    public ChiSquaredDistributionImpl(double df, GammaDistribution g) {
        super();
        setGammaInternal(g);
        setDegreesOfFreedomInternal(df);
        solverAbsoluteAccuracy = DEFAULT_INVERSE_ABSOLUTE_ACCURACY;
    }

    /**
     * Create a Chi-Squared distribution with the given degrees of freedom and
     * inverse cumulative probability accuracy.
     * @param df degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse cumulative probability estimates
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @since 2.1
     */
    public ChiSquaredDistributionImpl(double df, double inverseCumAccuracy) {
        super();
        gamma = new GammaDistributionImpl(df / 2.0, 2.0);
        setDegreesOfFreedomInternal(df);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Modify the degrees of freedom.
     * @param degreesOfFreedom the new degrees of freedom.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setDegreesOfFreedom(double degreesOfFreedom) {
        setDegreesOfFreedomInternal(degreesOfFreedom);
    }
    /**
     * Modify the degrees of freedom.
     * @param degreesOfFreedom the new degrees of freedom.
     */
    private void setDegreesOfFreedomInternal(double degreesOfFreedom) {
        gamma.setAlpha(degreesOfFreedom / 2.0);
    }

    /**
     * Access the degrees of freedom.
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return gamma.getAlpha() * 2.0;
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @deprecated
     */
    @Deprecated
    public double density(Double x) {
        return density(x.doubleValue());
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @since 2.1
     */
    @Override
    public double density(double x) {
        return gamma.density(x);
    }

    /**
     * For this distribution, X, this method returns P(X &lt; x).
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
        return gamma.cumulativeProbability(x);
    }

    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     * <p>
     * Returns 0 for p=0 and <code>Double.POSITIVE_INFINITY</code> for p=1.</p>
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     * @throws MathException if the inverse cumulative probability can not be
     *         computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     */
    @Override
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p == 0) {
            return 0d;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code>
     */
    @Override
    protected double getDomainLowerBound(double p) {
        return Double.MIN_VALUE * gamma.getBeta();
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code>
     */
    @Override
    protected double getDomainUpperBound(double p) {
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use mean
            ret = getDegreesOfFreedom();
        } else {
            // use max
            ret = Double.MAX_VALUE;
        }

        return ret;
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    @Override
    protected double getInitialDomain(double p) {
        // NOTE: chi squared is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use 1/2 mean
            ret = getDegreesOfFreedom() * .5;
        } else {
            // use mean
            ret = getDegreesOfFreedom();
        }

        return ret;
    }

    /**
     * Modify the underlying gamma distribution.  The caller is responsible for
     * insuring the gamma distribution has the proper parameter settings.
     * @param g the new distribution.
     * @since 1.2 made public
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setGamma(GammaDistribution g) {
        setGammaInternal(g);
    }
    /**
     * Modify the underlying gamma distribution.  The caller is responsible for
     * insuring the gamma distribution has the proper parameter settings.
     * @param g the new distribution.
     * @since 1.2 made public
     */
    private void setGammaInternal(GammaDistribution g) {
        this.gamma = g;

    }


    /**
     * Return the absolute accuracy setting of the solver used to estimate
     * inverse cumulative probabilities.
     *
     * @return the solver absolute accuracy
     * @since 2.1
     */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /**
     * Returns the lower bound of the support for the distribution.
     *
     * The lower bound of the support is always 0 no matter the
     * degrees of freedom.
     *
     * @return lower bound of the support (always 0)
     * @since 2.2
     */
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * Returns the upper bound for the support for the distribution.
     *
     * The upper bound of the support is always positive infinity no matter the
     * degrees of freedom.
     *
     * @return upper bound of the support (always Double.POSITIVE_INFINITY)
     * @since 2.2
     */
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the mean of the distribution.
     *
     * For <code>k</code> degrees of freedom, the mean is
     * <code>k</code>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        return getDegreesOfFreedom();
    }

    /**
     * Returns the variance of the distribution.
     *
     * For <code>k</code> degrees of freedom, the variance is
     * <code>2 * k</code>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        return 2*getDegreesOfFreedom();
    }
}

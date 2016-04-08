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
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;

/**
 * The default implementation of {@link GammaDistribution}.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class GammaDistributionImpl extends AbstractContinuousDistribution
    implements GammaDistribution, Serializable  {

    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier */
    private static final long serialVersionUID = -3239549463135430361L;

    /** The shape parameter. */
    private double alpha;

    /** The scale parameter. */
    private double beta;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a new gamma distribution with the given alpha and beta values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public GammaDistributionImpl(double alpha, double beta) {
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a new gamma distribution with the given alpha and beta values.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     * @param inverseCumAccuracy the maximum absolute error in inverse cumulative probability estimates
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @since 2.1
     */
    public GammaDistributionImpl(double alpha, double beta, double inverseCumAccuracy) {
        super();
        setAlphaInternal(alpha);
        setBetaInternal(beta);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * For this distribution, X, this method returns P(X &lt; x).
     *
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/Chi-SquaredDistribution.html">
     * Chi-Squared Distribution</a>, equation (9).</li>
     * <li>Casella, G., & Berger, R. (1990). <i>Statistical Inference</i>.
     * Belmont, CA: Duxbury Press.</li>
     * </ul>
     *
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException{
        double ret;

        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = Gamma.regularizedGammaP(alpha, x / beta);
        }

        return ret;
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
     * Modify the shape parameter, alpha.
     * @param alpha the new shape parameter.
     * @throws IllegalArgumentException if <code>alpha</code> is not positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setAlpha(double alpha) {
        setAlphaInternal(alpha);
    }

    /**
     * Modify the shape parameter, alpha.
     * @param newAlpha the new shape parameter.
     * @throws IllegalArgumentException if <code>newAlpha</code> is not positive.
     */
    private void setAlphaInternal(double newAlpha) {
        if (newAlpha <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_ALPHA,
                  newAlpha);
        }
        this.alpha = newAlpha;
    }

    /**
     * Access the shape parameter, alpha
     * @return alpha.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Modify the scale parameter, beta.
     * @param newBeta the new scale parameter.
     * @throws IllegalArgumentException if <code>newBeta</code> is not positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setBeta(double newBeta) {
        setBetaInternal(newBeta);
    }

    /**
     * Modify the scale parameter, beta.
     * @param newBeta the new scale parameter.
     * @throws IllegalArgumentException if <code>newBeta</code> is not positive.
     */
    private void setBetaInternal(double newBeta) {
        if (newBeta <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_BETA,
                  newBeta);
        }
        this.beta = newBeta;
    }

    /**
     * Access the scale parameter, beta
     * @return beta.
     */
    public double getBeta() {
        return beta;
    }

    /**
     * Returns the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     */
    @Override
    public double density(double x) {
        if (x < 0) return 0;
        return FastMath.pow(x / beta, alpha - 1) / beta * FastMath.exp(-x / beta) / FastMath.exp(Gamma.logGamma(alpha));
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
        // TODO: try to improve on this estimate
        return Double.MIN_VALUE;
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
        // TODO: try to improve on this estimate
        // NOTE: gamma is skewed to the left
        // NOTE: therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use mean
            ret = alpha * beta;
        } else {
            // use max value
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
        // TODO: try to improve on this estimate
        // Gamma is skewed to the left, therefore, P(X < &mu;) > .5

        double ret;

        if (p < .5) {
            // use 1/2 mean
            ret = alpha * beta * .5;
        } else {
            // use mean
            ret = alpha * beta;
        }

        return ret;
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
     * Returns the upper bound of the support for the distribution.
     *
     * The lower bound of the support is always 0, regardless of the parameters.
     *
     * @return lower bound of the support (always 0)
     * @since 2.2
     */
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * Returns the upper bound of the support for the distribution.
     *
     * The upper bound of the support is always positive infinity,
     * regardless of the parameters.
     *
     * @return upper bound of the support (always Double.POSITIVE_INFINITY)
     * @since 2.2
     */
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the mean.
     *
     * For shape parameter <code>alpha</code> and scale
     * parameter <code>beta</code>, the mean is
     * <code>alpha * beta</code>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        return getAlpha() * getBeta();
    }

    /**
     * Returns the variance.
     *
     * For shape parameter <code>alpha</code> and scale
     * parameter <code>beta</code>, the variance is
     * <code>alpha * beta^2</code>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        final double b = getBeta();
        return getAlpha() * b * b;
    }
}

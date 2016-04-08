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
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.FDistribution}.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class FDistributionImpl
    extends AbstractContinuousDistribution
    implements FDistribution, Serializable  {

    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier */
    private static final long serialVersionUID = -8516354193418641566L;

    /** The numerator degrees of freedom*/
    private double numeratorDegreesOfFreedom;

    /** The numerator degrees of freedom*/
    private double denominatorDegreesOfFreedom;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a F distribution using the given degrees of freedom.
     * @param numeratorDegreesOfFreedom the numerator degrees of freedom.
     * @param denominatorDegreesOfFreedom the denominator degrees of freedom.
     */
    public FDistributionImpl(double numeratorDegreesOfFreedom,
                             double denominatorDegreesOfFreedom) {
        this(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a F distribution using the given degrees of freedom and inverse cumulative probability accuracy.
     * @param numeratorDegreesOfFreedom the numerator degrees of freedom.
     * @param denominatorDegreesOfFreedom the denominator degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse cumulative probability estimates
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @since 2.1
     */
    public FDistributionImpl(double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom,
            double inverseCumAccuracy) {
        super();
        setNumeratorDegreesOfFreedomInternal(numeratorDegreesOfFreedom);
        setDenominatorDegreesOfFreedomInternal(denominatorDegreesOfFreedom);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Returns the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @since 2.1
     */
    @Override
    public double density(double x) {
        final double nhalf = numeratorDegreesOfFreedom / 2;
        final double mhalf = denominatorDegreesOfFreedom / 2;
        final double logx = FastMath.log(x);
        final double logn = FastMath.log(numeratorDegreesOfFreedom);
        final double logm = FastMath.log(denominatorDegreesOfFreedom);
        final double lognxm = FastMath.log(numeratorDegreesOfFreedom * x + denominatorDegreesOfFreedom);
        return FastMath.exp(nhalf*logn + nhalf*logx - logx + mhalf*logm - nhalf*lognxm -
               mhalf*lognxm - Beta.logBeta(nhalf, mhalf));
    }

    /**
     * For this distribution, X, this method returns P(X &lt; x).
     *
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/F-Distribution.html">
     * F-Distribution</a>, equation (4).</li>
     * </ul>
     *
     * @param x the value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            double n = numeratorDegreesOfFreedom;
            double m = denominatorDegreesOfFreedom;

            ret = Beta.regularizedBeta((n * x) / (m + n * x),
                0.5 * n,
                0.5 * m);
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
        return 0.0;
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
        return Double.MAX_VALUE;
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
        double ret = 1.0;
        double d = denominatorDegreesOfFreedom;
        if (d > 2.0) {
            // use mean
            ret = d / (d - 2.0);
        }
        return ret;
    }

    /**
     * Modify the numerator degrees of freedom.
     * @param degreesOfFreedom the new numerator degrees of freedom.
     * @throws IllegalArgumentException if <code>degreesOfFreedom</code> is not
     *         positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setNumeratorDegreesOfFreedom(double degreesOfFreedom) {
        setNumeratorDegreesOfFreedomInternal(degreesOfFreedom);
    }

    /**
     * Modify the numerator degrees of freedom.
     * @param degreesOfFreedom the new numerator degrees of freedom.
     * @throws IllegalArgumentException if <code>degreesOfFreedom</code> is not
     *         positive.
     */
    private void setNumeratorDegreesOfFreedomInternal(double degreesOfFreedom) {
        if (degreesOfFreedom <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_DEGREES_OF_FREEDOM, degreesOfFreedom);
        }
        this.numeratorDegreesOfFreedom = degreesOfFreedom;
    }

    /**
     * Access the numerator degrees of freedom.
     * @return the numerator degrees of freedom.
     */
    public double getNumeratorDegreesOfFreedom() {
        return numeratorDegreesOfFreedom;
    }

    /**
     * Modify the denominator degrees of freedom.
     * @param degreesOfFreedom the new denominator degrees of freedom.
     * @throws IllegalArgumentException if <code>degreesOfFreedom</code> is not
     *         positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setDenominatorDegreesOfFreedom(double degreesOfFreedom) {
        setDenominatorDegreesOfFreedomInternal(degreesOfFreedom);
    }

    /**
     * Modify the denominator degrees of freedom.
     * @param degreesOfFreedom the new denominator degrees of freedom.
     * @throws IllegalArgumentException if <code>degreesOfFreedom</code> is not
     *         positive.
     */
    private void setDenominatorDegreesOfFreedomInternal(double degreesOfFreedom) {
        if (degreesOfFreedom <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_DEGREES_OF_FREEDOM, degreesOfFreedom);
        }
        this.denominatorDegreesOfFreedom = degreesOfFreedom;
    }

    /**
     * Access the denominator degrees of freedom.
     * @return the denominator degrees of freedom.
     */
    public double getDenominatorDegreesOfFreedom() {
        return denominatorDegreesOfFreedom;
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
     * Returns the mean of the distribution.
     *
     * For denominator degrees of freedom parameter <code>b</code>,
     * the mean is
     * <ul>
     *  <li>if <code>b &gt; 2</code> then <code>b / (b - 2)</code></li>
     *  <li>else <code>undefined</code>
     * </ul>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        final double denominatorDF = getDenominatorDegreesOfFreedom();

        if (denominatorDF > 2) {
            return denominatorDF / (denominatorDF - 2);
        }

        return Double.NaN;
    }

    /**
     * Returns the variance of the distribution.
     *
     * For numerator degrees of freedom parameter <code>a</code>
     * and denominator degrees of freedom parameter <code>b</code>,
     * the variance is
     * <ul>
     *  <li>
     *    if <code>b &gt; 4</code> then
     *    <code>[ 2 * b^2 * (a + b - 2) ] / [ a * (b - 2)^2 * (b - 4) ]</code>
     *  </li>
     *  <li>else <code>undefined</code>
     * </ul>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        final double denominatorDF = getDenominatorDegreesOfFreedom();

        if (denominatorDF > 4) {
            final double numeratorDF = getNumeratorDegreesOfFreedom();
            final double denomDFMinusTwo = denominatorDF - 2;

            return ( 2 * (denominatorDF * denominatorDF) * (numeratorDF + denominatorDF - 2) ) /
                    ( (numeratorDF * (denomDFMinusTwo * denomDFMinusTwo) * (denominatorDF - 4)) );
        }

        return Double.NaN;
    }
}

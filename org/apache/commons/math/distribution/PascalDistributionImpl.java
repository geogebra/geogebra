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
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.FastMath;

/**
 * The default implementation of {@link PascalDistribution}.
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 * @since 1.2
 */
public class PascalDistributionImpl extends AbstractIntegerDistribution
    implements PascalDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 6751309484392813623L;

    /** The number of successes */
    private int numberOfSuccesses;

    /** The probability of success */
    private double probabilityOfSuccess;

    /**
     * Create a Pascal distribution with the given number of trials and
     * probability of success.
     * @param r the number of successes
     * @param p the probability of success
     */
    public PascalDistributionImpl(int r, double p) {
        super();
        setNumberOfSuccessesInternal(r);
        setProbabilityOfSuccessInternal(p);
    }

    /**
     * Access the number of successes for this distribution.
     * @return the number of successes
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
    }

    /**
     * Access the probability of success for this distribution.
     * @return the probability of success
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * Change the number of successes for this distribution.
     * @param successes the new number of successes
     * @throws IllegalArgumentException if <code>successes</code> is not
     *         positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setNumberOfSuccesses(int successes) {
        setNumberOfSuccessesInternal(successes);
    }

    /**
     * Change the number of successes for this distribution.
     * @param successes the new number of successes
     * @throws IllegalArgumentException if <code>successes</code> is not
     *         positive.
     */
    private void setNumberOfSuccessesInternal(int successes) {
        if (successes < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NEGATIVE_NUMBER_OF_SUCCESSES,
                  successes);
        }
        numberOfSuccesses = successes;
    }

    /**
     * Change the probability of success for this distribution.
     * @param p the new probability of success
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setProbabilityOfSuccess(double p) {
        setProbabilityOfSuccessInternal(p);
    }

    /**
     * Change the probability of success for this distribution.
     * @param p the new probability of success
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     */
    private void setProbabilityOfSuccessInternal(double p) {
        if (p < 0.0 || p > 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.OUT_OF_RANGE_SIMPLE, p, 0.0, 1.0);
        }
        probabilityOfSuccess = p;
    }

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a PDF root.
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e. P(X &lt; <i>lower bound</i>) &lt;
     *         <code>p</code>
     */
    @Override
    protected int getDomainLowerBound(double p) {
        return -1;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a PDF root.
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e. P(X &lt; <i>upper bound</i>) &gt;
     *         <code>p</code>
     */
    @Override
    protected int getDomainUpperBound(double p) {
        // use MAX - 1 because MAX causes loop
        return Integer.MAX_VALUE - 1;
    }

    /**
     * For this distribution, X, this method returns P(X &le; x).
     * @param x the value at which the PDF is evaluated
     * @return PDF for this distribution
     * @throws MathException if the cumulative probability can not be computed
     *         due to convergence or other numerical errors
     */
    @Override
    public double cumulativeProbability(int x) throws MathException {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            ret = Beta.regularizedBeta(probabilityOfSuccess,
                numberOfSuccesses, x + 1);
        }
        return ret;
    }

    /**
     * For this distribution, X, this method returns P(X = x).
     * @param x the value at which the PMF is evaluated
     * @return PMF for this distribution
     */
    public double probability(int x) {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else {
            ret = MathUtils.binomialCoefficientDouble(x +
                  numberOfSuccesses - 1, numberOfSuccesses - 1) *
                  FastMath.pow(probabilityOfSuccess, numberOfSuccesses) *
                  FastMath.pow(1.0 - probabilityOfSuccess, x);
        }
        return ret;
    }

    /**
     * For this distribution, X, this method returns the largest x, such that
     * P(X &le; x) &le; <code>p</code>.
     * <p>
     * Returns <code>-1</code> for p=0 and <code>Integer.MAX_VALUE</code>
     * for p=1.</p>
     * @param p the desired probability
     * @return the largest x such that P(X &le; x) <= p
     * @throws MathException if the inverse cumulative probability can not be
     *         computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if p < 0 or p > 1
     */
    @Override
    public int inverseCumulativeProbability(final double p)
        throws MathException {
        int ret;

        // handle extreme values explicitly
        if (p == 0) {
            ret = -1;
        } else if (p == 1) {
            ret = Integer.MAX_VALUE;
        } else {
            ret = super.inverseCumulativeProbability(p);
        }

        return ret;
    }

    /**
     * Returns the lower bound of the support for the distribution.
     *
     * The lower bound of the support is always 0 no matter the parameters.
     *
     * @return lower bound of the support (always 0)
     * @since 2.2
     */
    public int getSupportLowerBound() {
        return 0;
    }

    /**
     * Returns the upper bound of the support for the distribution.
     *
     * The upper bound of the support is always positive infinity
     * no matter the parameters. Positive infinity is represented
     * by <code>Integer.MAX_VALUE</code> together with
     * {@link #isSupportUpperBoundInclusive()} being <code>false</code>
     *
     * @return upper bound of the support (always <code>Integer.MAX_VALUE</code> for positive infinity)
     * @since 2.2
     */
    public int getSupportUpperBound() {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the mean.
     *
     * For number of successes <code>r</code> and
     * probability of success <code>p</code>, the mean is
     * <code>( r * p ) / ( 1 - p )</code>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        final double p = getProbabilityOfSuccess();
        final double r = getNumberOfSuccesses();
        return ( r * p ) / ( 1 - p );
    }

    /**
     * Returns the variance.
     *
     * For number of successes <code>r</code> and
     * probability of success <code>p</code>, the mean is
     * <code>( r * p ) / ( 1 - p )^2</code>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        final double p = getProbabilityOfSuccess();
        final double r = getNumberOfSuccesses();
        final double pInv = 1 - p;
        return ( r * p ) / (pInv * pInv);
    }
}

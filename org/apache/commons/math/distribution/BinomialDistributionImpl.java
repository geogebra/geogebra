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
 * The default implementation of {@link BinomialDistribution}.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class BinomialDistributionImpl extends AbstractIntegerDistribution
        implements BinomialDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 6751309484392813623L;

    /** The number of trials. */
    private int numberOfTrials;

    /** The probability of success. */
    private double probabilityOfSuccess;

    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     *
     * @param trials the number of trials.
     * @param p the probability of success.
     */
    public BinomialDistributionImpl(int trials, double p) {
        super();
        setNumberOfTrialsInternal(trials);
        setProbabilityOfSuccessInternal(p);
    }

    /**
     * Access the number of trials for this distribution.
     *
     * @return the number of trials.
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    /**
     * Access the probability of success for this distribution.
     *
     * @return the probability of success.
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * Change the number of trials for this distribution.
     *
     * @param trials the new number of trials.
     * @throws IllegalArgumentException if <code>trials</code> is not a valid
     *             number of trials.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setNumberOfTrials(int trials) {
        setNumberOfTrialsInternal(trials);
    }

    /**
     * Change the number of trials for this distribution.
     *
     * @param trials the new number of trials.
     * @throws IllegalArgumentException if <code>trials</code> is not a valid
     *             number of trials.
     */
    private void setNumberOfTrialsInternal(int trials) {
        if (trials < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NEGATIVE_NUMBER_OF_TRIALS, trials);
        }
        numberOfTrials = trials;
    }

    /**
     * Change the probability of success for this distribution.
     *
     * @param p the new probability of success.
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *             probability.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setProbabilityOfSuccess(double p) {
        setProbabilityOfSuccessInternal(p);
    }

    /**
     * Change the probability of success for this distribution.
     *
     * @param p the new probability of success.
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *             probability.
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
     *
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
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e. P(X &lt; <i>upper bound</i>) &gt;
     *         <code>p</code>
     */
    @Override
    protected int getDomainUpperBound(double p) {
        return numberOfTrials;
    }

    /**
     * For this distribution, X, this method returns P(X &le; x).
     *
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution.
     * @throws MathException if the cumulative probability can not be computed
     *             due to convergence or other numerical errors.
     */
    @Override
    public double cumulativeProbability(int x) throws MathException {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else if (x >= numberOfTrials) {
            ret = 1.0;
        } else {
            ret = 1.0 - Beta.regularizedBeta(getProbabilityOfSuccess(),
                    x + 1.0, numberOfTrials - x);
        }
        return ret;
    }

    /**
     * For this distribution, X, this method returns P(X = x).
     *
     * @param x the value at which the PMF is evaluated.
     * @return PMF for this distribution.
     */
    public double probability(int x) {
        double ret;
        if (x < 0 || x > numberOfTrials) {
            ret = 0.0;
        } else {
            ret = FastMath.exp(SaddlePointExpansion.logBinomialProbability(x,
                    numberOfTrials, probabilityOfSuccess,
                    1.0 - probabilityOfSuccess));
        }
        return ret;
    }

    /**
     * For this distribution, X, this method returns the largest x, such that
     * P(X &le; x) &le; <code>p</code>.
     * <p>
     * Returns <code>-1</code> for p=0 and <code>Integer.MAX_VALUE</code> for
     * p=1.
     * </p>
     *
     * @param p the desired probability
     * @return the largest x such that P(X &le; x) <= p
     * @throws MathException if the inverse cumulative probability can not be
     *             computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if p < 0 or p > 1
     */
    @Override
    public int inverseCumulativeProbability(final double p)
            throws MathException {
        // handle extreme values explicitly
        if (p == 0) {
            return -1;
        }
        if (p == 1) {
            return Integer.MAX_VALUE;
        }

        // use default bisection impl
        return super.inverseCumulativeProbability(p);
    }

    /**
     * Returns the lower bound of the support for the distribution.
     *
     * The lower bound of the support is always 0 no matter the number of trials
     * and probability parameter.
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
     * The upper bound of the support is the number of trials.
     *
     * @return upper bound of the support (equal to number of trials)
     * @since 2.2
     */
    public int getSupportUpperBound() {
        return getNumberOfTrials();
    }

    /**
     * Returns the mean.
     *
     * For <code>n</code> number of trials and
     * probability parameter <code>p</code>, the mean is
     * <code>n * p</code>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        return (double)getNumberOfTrials() * getProbabilityOfSuccess();
    }

    /**
     * Returns the variance.
     *
     * For <code>n</code> number of trials and
     * probability parameter <code>p</code>, the variance is
     * <code>n * p * (1 - p)</code>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        final double p = getProbabilityOfSuccess();
        return (double)getNumberOfTrials() * p * (1 - p);
    }
}

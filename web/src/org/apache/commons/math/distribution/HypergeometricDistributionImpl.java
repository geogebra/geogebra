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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.distribution;

import java.io.Serializable;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.util.MathUtils;

/**
 * The default implementation of {@link HypergeometricDistribution}.
 *
 * @version $Revision: 920852 $ $Date: 2010-03-09 07:53:44 -0500 (Tue, 09 Mar 2010) $
 */
public class HypergeometricDistributionImpl extends AbstractIntegerDistribution
        implements HypergeometricDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -436928820673516179L;

    /** The number of successes in the population. */
    private int numberOfSuccesses;

    /** The population size. */
    private int populationSize;

    /** The sample size. */
    private int sampleSize;

    /**
     * Construct a new hypergeometric distribution with the given the population
     * size, the number of successes in the population, and the sample size.
     *
     * @param populationSize the population size.
     * @param numberOfSuccesses number of successes in the population.
     * @param sampleSize the sample size.
     */
    public HypergeometricDistributionImpl(int populationSize,
            int numberOfSuccesses, int sampleSize) {
        super();
        if (numberOfSuccesses > populationSize) {
            throw MathRuntimeException
                    .createIllegalArgumentException(
                            "number of successes ({0}) must be less than or equal to population size ({1})",
                            numberOfSuccesses, populationSize);
        }
        if (sampleSize > populationSize) {
            throw MathRuntimeException
                    .createIllegalArgumentException(
                            "sample size ({0}) must be less than or equal to population size ({1})",
                            sampleSize, populationSize);
        }

        setPopulationSizeInternal(populationSize);
        setSampleSizeInternal(sampleSize);
        setNumberOfSuccessesInternal(numberOfSuccesses);
    }

    /**
     * For this distribution, X, this method returns P(X &le; x).
     *
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution.
     */
    @Override
    public double cumulativeProbability(int x) {
        double ret;

        int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
        if (x < domain[0]) {
            ret = 0.0;
        } else if (x >= domain[1]) {
            ret = 1.0;
        } else {
            ret = innerCumulativeProbability(domain[0], x, 1, populationSize,
                                             numberOfSuccesses, sampleSize);
        }

        return ret;
    }

    /**
     * Return the domain for the given hypergeometric distribution parameters.
     *
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return a two element array containing the lower and upper bounds of the
     *         hypergeometric distribution.
     */
    private int[] getDomain(int n, int m, int k) {
        return new int[] { getLowerDomain(n, m, k), getUpperDomain(m, k) };
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
        return getLowerDomain(populationSize, numberOfSuccesses, sampleSize);
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
        return getUpperDomain(sampleSize, numberOfSuccesses);
    }

    /**
     * Return the lowest domain value for the given hypergeometric distribution
     * parameters.
     *
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return the lowest domain value of the hypergeometric distribution.
     */
    private int getLowerDomain(int n, int m, int k) {
        return Math.max(0, m - (n - k));
    }

    /**
     * Access the number of successes.
     *
     * @return the number of successes.
     */
    public int getNumberOfSuccesses() {
        return numberOfSuccesses;
    }

    /**
     * Access the population size.
     *
     * @return the population size.
     */
    public int getPopulationSize() {
        return populationSize;
    }

    /**
     * Access the sample size.
     *
     * @return the sample size.
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Return the highest domain value for the given hypergeometric distribution
     * parameters.
     *
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return the highest domain value of the hypergeometric distribution.
     */
    private int getUpperDomain(int m, int k) {
        return Math.min(k, m);
    }

    /**
     * For this distribution, X, this method returns P(X = x).
     *
     * @param x the value at which the PMF is evaluated.
     * @return PMF for this distribution.
     */
    public double probability(int x) {
        double ret;

        int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
        if (x < domain[0] || x > domain[1]) {
            ret = 0.0;
        } else {
            double p = (double) sampleSize / (double) populationSize;
            double q = (double) (populationSize - sampleSize) / (double) populationSize;
            double p1 = SaddlePointExpansion.logBinomialProbability(x,
                    numberOfSuccesses, p, q);
            double p2 =
                SaddlePointExpansion.logBinomialProbability(sampleSize - x,
                    populationSize - numberOfSuccesses, p, q);
            double p3 =
                SaddlePointExpansion.logBinomialProbability(sampleSize, populationSize, p, q);
            ret = Math.exp(p1 + p2 - p3);
        }

        return ret;
    }

    /**
     * For the distribution, X, defined by the given hypergeometric distribution
     * parameters, this method returns P(X = x).
     *
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @param x the value at which the PMF is evaluated.
     * @return PMF for the distribution.
     */
    private double probability(int n, int m, int k, int x) {
        return Math.exp(MathUtils.binomialCoefficientLog(m, x) +
               MathUtils.binomialCoefficientLog(n - m, k - x) -
               MathUtils.binomialCoefficientLog(n, k));
    }

    /**
     * Modify the number of successes.
     *
     * @param num the new number of successes.
     * @throws IllegalArgumentException if <code>num</code> is negative.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setNumberOfSuccesses(int num) {
        setNumberOfSuccessesInternal(num);
    }
    /**
     * Modify the number of successes.
     *
     * @param num the new number of successes.
     * @throws IllegalArgumentException if <code>num</code> is negative.
     */
    private void setNumberOfSuccessesInternal(int num) {
        if (num < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "number of successes must be non-negative ({0})", num);
        }
        numberOfSuccesses = num;
    }

    /**
     * Modify the population size.
     *
     * @param size the new population size.
     * @throws IllegalArgumentException if <code>size</code> is not positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setPopulationSize(int size) {
        setPopulationSizeInternal(size);
    }
    /**
     * Modify the population size.
     *
     * @param size the new population size.
     * @throws IllegalArgumentException if <code>size</code> is not positive.
     */
    private void setPopulationSizeInternal(int size) {
        if (size <= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "population size must be positive ({0})", size);
        }
        populationSize = size;
    }

    /**
     * Modify the sample size.
     *
     * @param size the new sample size.
     * @throws IllegalArgumentException if <code>size</code> is negative.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setSampleSize(int size) {
        setSampleSizeInternal(size);
    }
    /**
     * Modify the sample size.
     *
     * @param size the new sample size.
     * @throws IllegalArgumentException if <code>size</code> is negative.
     */
    private void setSampleSizeInternal(int size) {
        if (size < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "sample size must be positive ({0})", size);
        }
        sampleSize = size;
    }

    /**
     * For this distribution, X, this method returns P(X &ge; x).
     *
     * @param x the value at which the CDF is evaluated.
     * @return upper tail CDF for this distribution.
     * @since 1.1
     */
    public double upperCumulativeProbability(int x) {
        double ret;

        final int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
        if (x < domain[0]) {
            ret = 1.0;
        } else if (x > domain[1]) {
            ret = 0.0;
        } else {
            ret = innerCumulativeProbability(domain[1], x, -1, populationSize, numberOfSuccesses, sampleSize);
        }

        return ret;
    }

    /**
     * For this distribution, X, this method returns P(x0 &le; X &le; x1). This
     * probability is computed by summing the point probabilities for the values
     * x0, x0 + 1, x0 + 2, ..., x1, in the order directed by dx.
     *
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @param dx the direction of summation. 1 indicates summing from x0 to x1.
     *            0 indicates summing from x1 to x0.
     * @param n the population size.
     * @param m number of successes in the population.
     * @param k the sample size.
     * @return P(x0 &le; X &le; x1).
     */
    private double innerCumulativeProbability(int x0, int x1, int dx, int n,
            int m, int k) {
        double ret = probability(n, m, k, x0);
        while (x0 != x1) {
            x0 += dx;
            ret += probability(n, m, k, x0);
        }
        return ret;
    }
}

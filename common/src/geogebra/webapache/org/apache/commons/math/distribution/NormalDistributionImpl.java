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

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.special.Erf;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.NormalDistribution}.
 *
 * @version $Revision: 925812 $ $Date: 2010-03-21 11:49:31 -0400 (Sun, 21 Mar 2010) $
 */
public class NormalDistributionImpl extends AbstractContinuousDistribution
        implements NormalDistribution, Serializable {

    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier */
    private static final long serialVersionUID = 8589540077390120676L;

    /** &sqrt;(2 &pi;) */
    private static final double SQRT2PI = Math.sqrt(2 * Math.PI);

    /** The mean of this distribution. */
    private double mean = 0;

    /** The standard deviation of this distribution. */
    private double standardDeviation = 1;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a normal distribution using the given mean and standard deviation.
     * @param mean mean for this distribution
     * @param sd standard deviation for this distribution
     */
    public NormalDistributionImpl(double mean, double sd){
        this(mean, sd, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create a normal distribution using the given mean, standard deviation and
     * inverse cumulative distribution accuracy.
     *
     * @param mean mean for this distribution
     * @param sd standard deviation for this distribution
     * @param inverseCumAccuracy inverse cumulative probability accuracy
     * @since 2.1
     */
    public NormalDistributionImpl(double mean, double sd, double inverseCumAccuracy) {
        super();
        setMeanInternal(mean);
        setStandardDeviationInternal(sd);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Creates normal distribution with the mean equal to zero and standard
     * deviation equal to one.
     */
    public NormalDistributionImpl(){
        this(0.0, 1.0);
    }

    /**
     * Access the mean.
     * @return mean for this distribution
     */
    public double getMean() {
        return mean;
    }

    /**
     * Modify the mean.
     * @param mean for this distribution
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setMean(double mean) {
        setMeanInternal(mean);
    }
    /**
     * Modify the mean.
     * @param newMean for this distribution
     */
    private void setMeanInternal(double newMean) {
        this.mean = newMean;
    }

    /**
     * Access the standard deviation.
     * @return standard deviation for this distribution
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Modify the standard deviation.
     * @param sd standard deviation for this distribution
     * @throws IllegalArgumentException if <code>sd</code> is not positive.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setStandardDeviation(double sd) {
        setStandardDeviationInternal(sd);
    }
    /**
     * Modify the standard deviation.
     * @param sd standard deviation for this distribution
     * @throws IllegalArgumentException if <code>sd</code> is not positive.
     */
    private void setStandardDeviationInternal(double sd) {
        if (sd <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "standard deviation must be positive ({0})",
                  sd);
        }
        standardDeviation = sd;
    }

    /**
     * Return the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @deprecated
     */
    public double density(Double x) {
        return density(x.doubleValue());
    }

    /**
     * Returns the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @since 2.1
     */
    public double density(double x) {
        double x0 = x - mean;
        return Math.exp(-x0 * x0 / (2 * standardDeviation * standardDeviation)) / (standardDeviation * SQRT2PI);
    }

    /**
     * For this distribution, X, this method returns P(X &lt; <code>x</code>).
     * @param x the value at which the CDF is evaluated.
     * @return CDF evaluted at <code>x</code>.
     * @throws MathException if the algorithm fails to converge; unless
     * x is more than 20 standard deviations from the mean, in which case the
     * convergence exception is caught and 0 or 1 is returned.
     */
    public double cumulativeProbability(double x) throws MathException {
        try {
            return 0.5 * (1.0 + Erf.erf((x - mean) /
                    (standardDeviation * Math.sqrt(2.0))));
        } catch (MaxIterationsExceededException ex) {
            if (x < (mean - 20 * standardDeviation)) { // JDK 1.5 blows at 38
                return 0.0d;
            } else if (x > (mean + 20 * standardDeviation)) {
                return 1.0d;
            } else {
                throw ex;
            }
        }
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
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     * <p>
     * Returns <code>Double.NEGATIVE_INFINITY</code> for p=0 and
     * <code>Double.POSITIVE_INFINITY</code> for p=1.</p>
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
            return Double.NEGATIVE_INFINITY;
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
        double ret;

        if (p < .5) {
            ret = -Double.MAX_VALUE;
        } else {
            ret = mean;
        }

        return ret;
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
        double ret;

        if (p < .5) {
            ret = mean;
        } else {
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
        double ret;

        if (p < .5) {
            ret = mean - standardDeviation;
        } else if (p > .5) {
            ret = mean + standardDeviation;
        } else {
            ret = mean;
        }

        return ret;
    }
}

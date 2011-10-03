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
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;


/**
 * Base class for integer-valued discrete distributions.  Default
 * implementations are provided for some of the methods that do not vary
 * from distribution to distribution.
 *
 * @version $Revision: 1067494 $ $Date: 2011-02-05 20:49:07 +0100 (sam. 05 févr. 2011) $
 */
public abstract class AbstractIntegerDistribution extends AbstractDistribution
    implements IntegerDistribution, Serializable {

   /** Serializable version identifier */
    private static final long serialVersionUID = -1146319659338487221L;

    /**
     * RandomData instance used to generate samples from the distribution
     * @since 2.2
     */
    protected final RandomDataImpl randomData = new RandomDataImpl();

    /**
     * Default constructor.
     */
    protected AbstractIntegerDistribution() {
        super();
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(X &le; x).  In other words,
     * this method represents the  (cumulative) distribution function, or
     * CDF, for this distribution.
     * <p>
     * If <code>x</code> does not represent an integer value, the CDF is
     * evaluated at the greatest integer less than x.
     *
     * @param x the value at which the distribution function is evaluated.
     * @return cumulative probability that a random variable with this
     * distribution takes a value less than or equal to <code>x</code>
     * @throws MathException if the cumulative probability can not be
     * computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
        return cumulativeProbability((int) FastMath.floor(x));
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(x0 &le; X &le; x1).
     *
     * @param x0 the (inclusive) lower bound
     * @param x1 the (inclusive) upper bound
     * @return the probability that a random variable with this distribution
     * will take a value between <code>x0</code> and <code>x1</code>,
     * including the endpoints.
     * @throws MathException if the cumulative probability can not be
     * computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if <code>x0 > x1</code>
     */
    @Override
    public double cumulativeProbability(double x0, double x1)
        throws MathException {
        if (x0 > x1) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1);
        }
        if (FastMath.floor(x0) < x0) {
            return cumulativeProbability(((int) FastMath.floor(x0)) + 1,
               (int) FastMath.floor(x1)); // don't want to count mass below x0
        } else { // x0 is mathematical integer, so use as is
            return cumulativeProbability((int) FastMath.floor(x0),
                (int) FastMath.floor(x1));
        }
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(X &le; x).  In other words,
     * this method represents the probability distribution function, or PDF,
     * for this distribution.
     *
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public abstract double cumulativeProbability(int x) throws MathException;

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(X = x). In other words, this
     * method represents the probability mass function,  or PMF, for the distribution.
     * <p>
     * If <code>x</code> does not represent an integer value, 0 is returned.
     *
     * @param x the value at which the probability density function is evaluated
     * @return the value of the probability density function at x
     */
    public double probability(double x) {
        double fl = FastMath.floor(x);
        if (fl == x) {
            return this.probability((int) x);
        } else {
            return 0;
        }
    }

    /**
    * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(x0 &le; X &le; x1).
     *
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @return the cumulative probability.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if x0 > x1
     */
    public double cumulativeProbability(int x0, int x1) throws MathException {
        if (x0 > x1) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, x0, x1);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0 - 1);
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns the largest x, such
     * that P(X &le; x) &le; <code>p</code>.
     *
     * @param p the desired probability
     * @return the largest x such that P(X &le; x) <= p
     * @throws MathException if the inverse cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if p < 0 or p > 1
     */
    public int inverseCumulativeProbability(final double p) throws MathException{
        if (p < 0.0 || p > 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.OUT_OF_RANGE_SIMPLE, p, 0.0, 1.0);
        }

        // by default, do simple bisection.
        // subclasses can override if there is a better method.
        int x0 = getDomainLowerBound(p);
        int x1 = getDomainUpperBound(p);
        double pm;
        while (x0 < x1) {
            int xm = x0 + (x1 - x0) / 2;
            pm = checkedCumulativeProbability(xm);
            if (pm > p) {
                // update x1
                if (xm == x1) {
                    // this can happen with integer division
                    // simply decrement x1
                    --x1;
                } else {
                    // update x1 normally
                    x1 = xm;
                }
            } else {
                // update x0
                if (xm == x0) {
                    // this can happen with integer division
                    // simply increment x0
                    ++x0;
                } else {
                    // update x0 normally
                    x0 = xm;
                }
            }
        }

        // insure x0 is the correct critical point
        pm = checkedCumulativeProbability(x0);
        while (pm > p) {
            --x0;
            pm = checkedCumulativeProbability(x0);
        }

        return x0;
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
    public int sample() throws MathException {
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
    public int[] sample(int sampleSize) throws MathException {
        if (sampleSize <= 0) {
            MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SAMPLE_SIZE, sampleSize);
        }
        int[] out = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    /**
     * Computes the cumulative probability function and checks for NaN values returned.
     * Throws MathException if the value is NaN. Rethrows any MathException encountered
     * evaluating the cumulative probability function. Throws
     * MathException if the cumulative probability function returns NaN.
     *
     * @param argument input value
     * @return cumulative probability
     * @throws MathException if the cumulative probability is NaN
     */
    private double checkedCumulativeProbability(int argument) throws MathException {
        double result = Double.NaN;
            result = cumulativeProbability(argument);
        if (Double.isNaN(result)) {
            throw new MathException(LocalizedFormats.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, argument);
        }
        return result;
    }

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code>
     */
    protected abstract int getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code>
     */
    protected abstract int getDomainUpperBound(double p);

    /**
     * Use this method to get information about whether the lower bound
     * of the support is inclusive or not. For discrete support,
     * only true here is meaningful.
     *
     * @return true (always but at Integer.MIN_VALUE because of the nature of discrete support)
     * @since 2.2
     */
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    /**
     * Use this method to get information about whether the upper bound
     * of the support is inclusive or not. For discrete support,
     * only true here is meaningful.
     *
     * @return true (always but at Integer.MAX_VALUE because of the nature of discrete support)
     * @since 2.2
     */
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }
}

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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implementation for the {@link ZipfDistribution}.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class ZipfDistributionImpl extends AbstractIntegerDistribution
    implements ZipfDistribution, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -140627372283420404L;

    /** Number of elements. */
    private int numberOfElements;

    /** Exponent parameter of the distribution. */
    private double exponent;

    /**
     * Create a new Zipf distribution with the given number of elements and
     * exponent. Both values must be positive; otherwise an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param numberOfElements the number of elements
     * @param exponent the exponent
     * @exception IllegalArgumentException if n &le; 0 or s &le; 0.0
     */
    public ZipfDistributionImpl(final int numberOfElements, final double exponent)
        throws IllegalArgumentException {
        setNumberOfElementsInternal(numberOfElements);
        setExponentInternal(exponent);
    }

    /**
     * Get the number of elements (e.g. corpus size) for the distribution.
     *
     * @return the number of elements
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * Set the number of elements (e.g. corpus size) for the distribution.
     * The parameter value must be positive; otherwise an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param n the number of elements
     * @exception IllegalArgumentException if n &le; 0
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setNumberOfElements(final int n) {
        setNumberOfElementsInternal(n);
    }
    /**
     * Set the number of elements (e.g. corpus size) for the distribution.
     * The parameter value must be positive; otherwise an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param n the number of elements
     * @exception IllegalArgumentException if n &le; 0
     */
    private void setNumberOfElementsInternal(final int n)
        throws IllegalArgumentException {
        if (n <= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, n, 0);
        }
        this.numberOfElements = n;
    }

    /**
     * Get the exponent characterising the distribution.
     *
     * @return the exponent
     */
    public double getExponent() {
        return exponent;
    }

    /**
     * Set the exponent characterising the distribution.
     * The parameter value must be positive; otherwise an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param s the exponent
     * @exception IllegalArgumentException if s &le; 0.0
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setExponent(final double s) {
        setExponentInternal(s);
    }

    /**
     * Set the exponent characterising the distribution.
     * The parameter value must be positive; otherwise an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param s the exponent
     * @exception IllegalArgumentException if s &le; 0.0
     */
    private void setExponentInternal(final double s)
        throws IllegalArgumentException {
        if (s <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_POSITIVE_EXPONENT,
                    s);
        }
        this.exponent = s;
    }

    /**
     * The probability mass function P(X = x) for a Zipf distribution.
     *
     * @param x the value at which the probability density function is evaluated.
     * @return the value of the probability mass function at x
     */
    public double probability(final int x) {
        if (x <= 0 || x > numberOfElements) {
            return 0.0;
        }

        return (1.0 / FastMath.pow(x, exponent)) / generalizedHarmonic(numberOfElements, exponent);

    }

    /**
     * The probability distribution function P(X <= x) for a Zipf distribution.
     *
     * @param x the value at which the PDF is evaluated.
     * @return Zipf distribution function evaluated at x
     */
    @Override
    public double cumulativeProbability(final int x) {
        if (x <= 0) {
            return 0.0;
        } else if (x >= numberOfElements) {
            return 1.0;
        }

        return generalizedHarmonic(x, exponent) / generalizedHarmonic(numberOfElements, exponent);

    }

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a PDF root.
     *
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code>
     */
    @Override
    protected int getDomainLowerBound(final double p) {
        return 0;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a PDF root.
     *
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code>
     */
    @Override
    protected int getDomainUpperBound(final double p) {
        return numberOfElements;
    }


    /**
     * Calculates the Nth generalized harmonic number. See
     * <a href="http://mathworld.wolfram.com/HarmonicSeries.html">Harmonic
     * Series</a>.
     *
     * @param n the term in the series to calculate (must be &ge; 1)
     * @param m the exponent; special case m == 1.0 is the harmonic series
     * @return the nth generalized harmonic number
     */
    private double generalizedHarmonic(final int n, final double m) {
        double value = 0;
        for (int k = n; k > 0; --k) {
            value += 1.0 / FastMath.pow(k, m);
        }
        return value;
    }

    /**
     * Returns the lower bound of the support for the distribution.
     *
     * The lower bound of the support is always 1 no matter the parameters.
     *
     * @return lower bound of the support (always 1)
     * @since 2.2
     */
    public int getSupportLowerBound() {
        return 1;
    }

    /**
     * Returns the upper bound of the support for the distribution.
     *
     * The upper bound of the support is the number of elements
     *
     * @return upper bound of the support
     * @since 2.2
     */
    public int getSupportUpperBound() {
        return getNumberOfElements();
    }

    /**
     * Returns the mean.
     *
     * For number of elements N and exponent s, the mean is
     * <code>Hs1 / Hs</code> where
     * <ul>
     *  <li><code>Hs1 = generalizedHarmonic(N, s - 1)</code></li>
     *  <li><code>Hs = generalizedHarmonic(N, s)</code></li>
     * </ul>
     *
     * @return the mean
     * @since 2.2
     */
    protected double getNumericalMean() {
        final int N = getNumberOfElements();
        final double s = getExponent();

        final double Hs1 = generalizedHarmonic(N, s - 1);
        final double Hs = generalizedHarmonic(N, s);

        return Hs1 / Hs;
    }

    /**
     * Returns the variance.
     *
     * For number of elements N and exponent s, the mean is
     * <code>(Hs2 / Hs) - (Hs1^2 / Hs^2)</code> where
     * <ul>
     *  <li><code>Hs2 = generalizedHarmonic(N, s - 2)</code></li>
     *  <li><code>Hs1 = generalizedHarmonic(N, s - 1)</code></li>
     *  <li><code>Hs = generalizedHarmonic(N, s)</code></li>
     * </ul>
     *
     * @return the variance
     * @since 2.2
     */
    protected double getNumericalVariance() {
        final int N = getNumberOfElements();
        final double s = getExponent();

        final double Hs2 = generalizedHarmonic(N, s - 2);
        final double Hs1 = generalizedHarmonic(N, s - 1);
        final double Hs = generalizedHarmonic(N, s);

        return (Hs2 / Hs) - ((Hs1 * Hs1) / (Hs * Hs));
    }
}

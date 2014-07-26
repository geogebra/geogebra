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
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.TDistribution}.
 *
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class TDistributionImpl
    extends AbstractContinuousDistribution
    implements TDistribution, Serializable  {

    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
    */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier */
    private static final long serialVersionUID = -5852615386664158222L;

    /** The degrees of freedom*/
    private double degreesOfFreedom;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a t distribution using the given degrees of freedom and the
     * specified inverse cumulative probability absolute accuracy.
     *
     * @param degreesOfFreedom the degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse cumulative probability estimates
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @since 2.1
     */
    public TDistributionImpl(double degreesOfFreedom, double inverseCumAccuracy) {
        super();
        setDegreesOfFreedomInternal(degreesOfFreedom);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Create a t distribution using the given degrees of freedom.
     * @param degreesOfFreedom the degrees of freedom.
     */
    public TDistributionImpl(double degreesOfFreedom) {
        this(degreesOfFreedom, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
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
     * @param newDegreesOfFreedom the new degrees of freedom.
     */
    private void setDegreesOfFreedomInternal(double newDegreesOfFreedom) {
        if (newDegreesOfFreedom <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_DEGREES_OF_FREEDOM,
                  newDegreesOfFreedom);
        }
        this.degreesOfFreedom = newDegreesOfFreedom;
    }

    /**
     * Access the degrees of freedom.
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return degreesOfFreedom;
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
        final double n = degreesOfFreedom;
        final double nPlus1Over2 = (n + 1) / 2;
        return FastMath.exp(Gamma.logGamma(nPlus1Over2) - 0.5 * (FastMath.log(FastMath.PI) + FastMath.log(n)) -
                Gamma.logGamma(n/2) - nPlus1Over2 * FastMath.log(1 + x * x /n));
    }

    /**
     * For this distribution, X, this method returns P(X &lt; <code>x</code>).
     * @param x the value at which the CDF is evaluated.
     * @return CDF evaluated at <code>x</code>.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException{
        double ret;
        if (x == 0.0) {
            ret = 0.5;
        } else {
            double t =
                Beta.regularizedBeta(
                    degreesOfFreedom / (degreesOfFreedom + (x * x)),
                    0.5 * degreesOfFreedom,
                    0.5);
            if (x < 0.0) {
                ret = 0.5 * t;
            } else {
                ret = 1.0 - 0.5 * t;
            }
        }

        return ret;
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
        return -Double.MAX_VALUE;
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
        return 0.0;
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
     * The lower bound of the support is always negative infinity
     * no matter the parameters.
     *
     * @return lower bound of the support (always Double.NEGATIVE_INFINITY)
     * @since 2.2
     */
    public double getSupportLowerBound() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * Returns the upper bound of the support for the distribution.
     *
     * The upper bound of the support is always positive infinity
     * no matter the parameters.
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
     * For degrees of freedom parameter df, the mean is
     * <ul>
     *  <li>if <code>df &gt; 1</code> then <code>0</code></li>
     * <li>else <code>undefined</code></li>
     * </ul>
     *
     * @return the mean
     * @since 2.2
     */
    public double getNumericalMean() {
        final double df = getDegreesOfFreedom();

        if (df > 1) {
            return 0;
        }

        return Double.NaN;
    }

    /**
     * Returns the variance.
     *
     * For degrees of freedom parameter df, the variance is
     * <ul>
     *  <li>if <code>df &gt; 2</code> then <code>df / (df - 2)</code> </li>
     *  <li>if <code>1 &lt; df &lt;= 2</code> then <code>positive infinity</code></li>
     *  <li>else <code>undefined</code></li>
     * </ul>
     *
     * @return the variance
     * @since 2.2
     */
    public double getNumericalVariance() {
        final double df = getDegreesOfFreedom();

        if (df > 2) {
            return df / (df - 2);
        }

        if (df > 1 && df <= 2) {
            return Double.POSITIVE_INFINITY;
        }

        return Double.NaN;
    }

}

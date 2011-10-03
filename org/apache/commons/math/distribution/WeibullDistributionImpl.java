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
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.WeibullDistribution}.
 *
 * @since 1.1
 * @version $Revision: 1054524 $ $Date: 2011-01-03 05:59:18 +0100 (lun. 03 janv. 2011) $
 */
public class WeibullDistributionImpl extends AbstractContinuousDistribution
        implements WeibullDistribution, Serializable {

    /**
     * Default inverse cumulative probability accuracy
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;

    /** Serializable version identifier */
    private static final long serialVersionUID = 8589540077390120676L;

    /** The shape parameter. */
    private double shape;

    /** The scale parameter. */
    private double scale;

    /** Inverse cumulative probability accuracy */
    private final double solverAbsoluteAccuracy;

    /** Cached numerical mean */
    private double numericalMean = Double.NaN;

    /** Whether or not the numerical mean has been calculated */
    private boolean numericalMeanIsCalculated = false;

    /** Cached numerical variance */
    private double numericalVariance = Double.NaN;

    /** Whether or not the numerical variance has been calculated */
    private boolean numericalVarianceIsCalculated = false;

    /**
     * Creates weibull distribution with the given shape and scale and a
     * location equal to zero.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     */
    public WeibullDistributionImpl(double alpha, double beta){
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Creates weibull distribution with the given shape, scale and inverse
     * cumulative probability accuracy and a location equal to zero.
     * @param alpha the shape parameter.
     * @param beta the scale parameter.
     * @param inverseCumAccuracy the maximum absolute error in inverse cumulative probability estimates
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @since 2.1
     */
    public WeibullDistributionImpl(double alpha, double beta, double inverseCumAccuracy){
        super();
        setShapeInternal(alpha);
        setScaleInternal(beta);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * For this distribution, X, this method returns P(X &lt; <code>x</code>).
     * @param x the value at which the CDF is evaluated.
     * @return CDF evaluated at <code>x</code>.
     */
    public double cumulativeProbability(double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = 1.0 - FastMath.exp(-FastMath.pow(x / scale, shape));
        }
        return ret;
    }

    /**
     * Access the shape parameter.
     * @return the shape parameter.
     */
    public double getShape() {
        return shape;
    }

    /**
     * Access the scale parameter.
     * @return the scale parameter.
     */
    public double getScale() {
        return scale;
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
        if (x < 0) {
            return 0;
        }

        final double xscale = x / scale;
        final double xscalepow = FastMath.pow(xscale, shape - 1);

        /*
         * FastMath.pow(x / scale, shape) =
         * FastMath.pow(xscale, shape) =
         * FastMath.pow(xscale, shape - 1) * xscale
         */
        final double xscalepowshape = xscalepow * xscale;

        return (shape / scale) * xscalepow * FastMath.exp(-xscalepowshape);
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
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     */
    @Override
    public double inverseCumulativeProbability(double p) {
        double ret;
        if (p < 0.0 || p > 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.OUT_OF_RANGE_SIMPLE, p, 0.0, 1.0);
        } else if (p == 0) {
            ret = 0.0;
        } else  if (p == 1) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = scale * FastMath.pow(-FastMath.log(1.0 - p), 1.0 / shape);
        }
        return ret;
    }

    /**
     * Modify the shape parameter.
     * @param alpha the new shape parameter value.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setShape(double alpha) {
        setShapeInternal(alpha);
        invalidateParameterDependentMoments();
    }
    /**
     * Modify the shape parameter.
     * @param alpha the new shape parameter value.
     */
    private void setShapeInternal(double alpha) {
        if (alpha <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_SHAPE,
                  alpha);
        }
        this.shape = alpha;
    }

    /**
     * Modify the scale parameter.
     * @param beta the new scale parameter value.
     * @deprecated as of 2.1 (class will become immutable in 3.0)
     */
    @Deprecated
    public void setScale(double beta) {
        setScaleInternal(beta);
        invalidateParameterDependentMoments();
    }
    /**
     * Modify the scale parameter.
     * @param beta the new scale parameter value.
     */
    private void setScaleInternal(double beta) {
        if (beta <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.NOT_POSITIVE_SCALE,
                  beta);
        }
        this.scale = beta;
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
        // use median
        return FastMath.pow(scale * FastMath.log(2.0), 1.0 / shape);
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
     * The lower bound of the support is always 0 no matter the parameters.
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
     * Calculates the mean.
     *
     * The mean is <code>scale * Gamma(1 + (1 / shape))</code>
     * where <code>Gamma(...)</code> is the Gamma-function
     *
     * @return the mean
     * @since 2.2
     */
    protected double calculateNumericalMean() {
        final double sh = getShape();
        final double sc = getScale();

        return sc * FastMath.exp(Gamma.logGamma(1 + (1 / sh)));
    }

    /**
     * Calculates the variance.
     *
     * The variance is
     * <code>scale^2 * Gamma(1 + (2 / shape)) - mean^2</code>
     * where <code>Gamma(...)</code> is the Gamma-function
     *
     * @return the variance
     * @since 2.2
     */
    private double calculateNumericalVariance() {
        final double sh = getShape();
        final double sc = getScale();
        final double mn = getNumericalMean();

        return (sc * sc) *
            FastMath.exp(Gamma.logGamma(1 + (2 / sh))) -
            (mn * mn);
    }

    /**
     * Returns the mean of the distribution.
     *
     * @return the mean or Double.NaN if it's not defined
     * @since 2.2
     */
    public double getNumericalMean() {
        if (!numericalMeanIsCalculated) {
            numericalMean = calculateNumericalMean();
            numericalMeanIsCalculated = true;
        }

        return numericalMean;
    }

    /**
     * Returns the variance of the distribution.
     *
     * @return the variance (possibly Double.POSITIVE_INFINITY as
     * for certain cases in {@link TDistributionImpl}) or
     * Double.NaN if it's not defined
     * @since 2.2
     */
    public double getNumericalVariance() {
        if (!numericalVarianceIsCalculated) {
            numericalVariance = calculateNumericalVariance();
            numericalVarianceIsCalculated = true;
        }

        return numericalVariance;
    }

    /**
     * Invalidates the cached mean and variance.
     */
    private void invalidateParameterDependentMoments() {
        numericalMeanIsCalculated = false;
        numericalVarianceIsCalculated = false;
    }
}

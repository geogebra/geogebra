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

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.WeibullDistribution}.
 *
 * @since 1.1
 * @version $Revision: 925812 $ $Date: 2010-03-21 11:49:31 -0400 (Sun, 21 Mar 2010) $
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
     * @return CDF evaluted at <code>x</code>.
     */
    public double cumulativeProbability(double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        } else {
            ret = 1.0 - Math.exp(-Math.pow(x / scale, shape));
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
        final double xscalepow = Math.pow(xscale, shape - 1);

        /*
         * Math.pow(x / scale, shape) =
         * Math.pow(xscale, shape) =
         * Math.pow(xscale, shape - 1) * xscale
         */
        final double xscalepowshape = xscalepow * xscale;

        return (shape / scale) * xscalepow * Math.exp(-xscalepowshape);
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
                  "{0} out of [{1}, {2}] range", p, 0.0, 1.0);
        } else if (p == 0) {
            ret = 0.0;
        } else  if (p == 1) {
            ret = Double.POSITIVE_INFINITY;
        } else {
            ret = scale * Math.pow(-Math.log(1.0 - p), 1.0 / shape);
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
    }
    /**
     * Modify the shape parameter.
     * @param alpha the new shape parameter value.
     */
    private void setShapeInternal(double alpha) {
        if (alpha <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "shape must be positive ({0})",
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
    }
    /**
     * Modify the scale parameter.
     * @param beta the new scale parameter value.
     */
    private void setScaleInternal(double beta) {
        if (beta <= 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "scale must be positive ({0})",
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
        return Math.pow(scale * Math.log(2.0), 1.0 / shape);
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
}

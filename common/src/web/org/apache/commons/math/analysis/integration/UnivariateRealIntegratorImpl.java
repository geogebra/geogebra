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
package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;



/**
 * Provide a default implementation for several generic functions.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 13:36:48 -0400 (Sat, 05 Sep 2009) $
 * @since 1.2
 */
public abstract class UnivariateRealIntegratorImpl
    extends ConvergingAlgorithmImpl implements UnivariateRealIntegrator {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 6248808456637441533L;

    /** minimum number of iterations */
    protected int minimalIterationCount;

    /** default minimum number of iterations */
    protected int defaultMinimalIterationCount;

    /** indicates whether an integral has been computed */
    protected boolean resultComputed = false;

    /** the last computed integral */
    protected double result;

    /** The integrand functione.
     * @deprecated as of 2.0 the integrand function is passed as an argument
     * to the {@link #integrate(UnivariateRealFunction, double, double)}method. */
    @Deprecated
    protected UnivariateRealFunction f;

    /**
     * Construct an integrator with given iteration count and accuracy.
     *
     * @param f the integrand function
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the iteration
     * limits are not valid
     * @deprecated as of 2.0 the integrand function is passed as an argument
     * to the {@link #integrate(UnivariateRealFunction, double, double)}method.
     */
    @Deprecated
    protected UnivariateRealIntegratorImpl(final UnivariateRealFunction f,
                                           final int defaultMaximalIterationCount)
        throws IllegalArgumentException {
        super(defaultMaximalIterationCount, 1.0e-15);
        if (f == null) {
            throw MathRuntimeException.createIllegalArgumentException("function is null");
        }

        this.f = f;

        // parameters that are problem specific
        setRelativeAccuracy(1.0e-6);
        this.defaultMinimalIterationCount = 3;
        this.minimalIterationCount = defaultMinimalIterationCount;

        verifyIterationCount();
    }

    /**
     * Construct an integrator with given iteration count and accuracy.
     *
     * @param defaultMaximalIterationCount maximum number of iterations
     * @throws IllegalArgumentException if f is null or the iteration
     * limits are not valid
     */
    protected UnivariateRealIntegratorImpl(final int defaultMaximalIterationCount)
        throws IllegalArgumentException {
        super(defaultMaximalIterationCount, 1.0e-15);

        // parameters that are problem specific
        setRelativeAccuracy(1.0e-6);
        this.defaultMinimalIterationCount = 3;
        this.minimalIterationCount = defaultMinimalIterationCount;

        verifyIterationCount();
    }

    /**
     * Access the last computed integral.
     *
     * @return the last computed integral
     * @throws IllegalStateException if no integral has been computed
     */
    public double getResult() throws Exception {
        if (resultComputed) {
            return result;
        } else {
            throw MathRuntimeException.createIllegalStateException("no result available");
        }
    }

    /**
     * Convenience function for implementations.
     *
     * @param newResult the result to set
     * @param iterationCount the iteration count to set
     */
    protected final void setResult(double newResult, int iterationCount) {
        this.result         = newResult;
        this.iterationCount = iterationCount;
        this.resultComputed = true;
    }

    /**
     * Convenience function for implementations.
     */
    protected final void clearResult() {
        this.iterationCount = 0;
        this.resultComputed = false;
    }

    /** {@inheritDoc} */
    public void setMinimalIterationCount(int count) {
        minimalIterationCount = count;
    }

    /** {@inheritDoc} */
    public int getMinimalIterationCount() {
        return minimalIterationCount;
    }

    /** {@inheritDoc} */
    public void resetMinimalIterationCount() {
        minimalIterationCount = defaultMinimalIterationCount;
    }

    /**
     * Verifies that the endpoints specify an interval.
     *
     * @param lower lower endpoint
     * @param upper upper endpoint
     * @throws IllegalArgumentException if not interval
     */
    protected void verifyInterval(double lower, double upper) throws
        IllegalArgumentException {
        if (lower >= upper) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "endpoints do not specify an interval: [{0}, {1}]",
                    lower, upper);
        }
    }

    /**
     * Verifies that the upper and lower limits of iterations are valid.
     *
     * @throws IllegalArgumentException if not valid
     */
    protected void verifyIterationCount() throws IllegalArgumentException {
        if ((minimalIterationCount <= 0) || (maximalIterationCount <= minimalIterationCount)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    "invalid iteration limits: min={0}, max={1}",
                    minimalIterationCount, maximalIterationCount);
        }
    }
}

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

/**
 * Base class for probability distributions.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 13:36:48 -0400 (Sat, 05 Sep 2009) $
 */
public abstract class AbstractDistribution
    implements Distribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -38038050983108802L;

    /**
     * Default constructor.
     */
    protected AbstractDistribution() {
        super();
    }

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(x0 &le; X &le; x1).
     * <p>
     * The default implementation uses the identity</p>
     * <p>
     * P(x0 &le; X &le; x1) = P(X &le; x1) - P(X &le; x0) </p>
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
    public double cumulativeProbability(double x0, double x1)
        throws MathException {
        if (x0 > x1) {
            throw MathRuntimeException.createIllegalArgumentException(
                  "lower endpoint ({0}) must be less than or equal to upper endpoint ({1})",
                  x0, x1);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }
}

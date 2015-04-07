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

import org.apache.commons.math.MathException;

/**
 * Interface representing the Poisson Distribution.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/PoissonDistribution.html">
 * Poisson distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision: 920852 $ $Date: 2010-03-09 13:53:44 +0100 (mar. 09 mars 2010) $
 */
public interface PoissonDistribution extends IntegerDistribution {

    /**
     * Get the mean for the distribution.
     *
     * @return the mean for the distribution.
     */
    double getMean();

    /**
     * Set the mean for the distribution.
     * The parameter value must be positive; otherwise an
     * <code>IllegalArgument</code> is thrown.
     *
     * @param p the mean
     * @throws IllegalArgumentException if p &le; 0
     * @deprecated as of v2.1
     */
    @Deprecated
    void setMean(double p);

    /**
     * Calculates the Poisson distribution function using a normal approximation.
     *
     * @param x the upper bound, inclusive
     * @return the distribution function value calculated using a normal approximation
     * @throws MathException if an error occurs computing the normal approximation
     */
    double normalApproximateProbability(int x) throws MathException;

}

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

/**
 * Normal (Gauss) Distribution.
 *
 * <p>
 * References:</p><p>
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/NormalDistribution.html">
 * Normal Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision: 920852 $ $Date: 2010-03-09 13:53:44 +0100 (mar. 09 mars 2010) $
 */
public interface NormalDistribution extends ContinuousDistribution, HasDensity<Double> {
    /**
     * Access the mean.
     * @return mean for this distribution
     */
    double getMean();
    /**
     * Modify the mean.
     * @param mean for this distribution
     * @deprecated as of v2.1
     */
    @Deprecated
    void setMean(double mean);
    /**
     * Access the standard deviation.
     * @return standard deviation for this distribution
     */
    double getStandardDeviation();
    /**
     * Modify the standard deviation.
     * @param sd standard deviation for this distribution
     * @deprecated as of v2.1
     */
    @Deprecated
    void setStandardDeviation(double sd);

    /**
     * Return the probability density for a particular point.
     * @param x  The point at which the density should be computed.
     * @return  The pdf at point x.
     */
    double density(Double x);
}

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
 * Cauchy Distribution.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/CauchyDistribution.html">
 * Cauchy Distribution</a></li>
 * </ul>
 * </p>
 *
 * @since 1.1
 * @version $Revision: 920852 $ $Date: 2010-03-09 13:53:44 +0100 (mar. 09 mars 2010) $
 */
public interface CauchyDistribution extends ContinuousDistribution {

    /**
     * Access the median.
     * @return median for this distribution
     */
    double getMedian();

    /**
     * Access the scale parameter.
     * @return scale parameter for this distribution
     */
    double getScale();

    /**
     * Modify the median.
     * @param median for this distribution
     * @deprecated as of v2.1
     */
    @Deprecated
    void setMedian(double median);

    /**
     * Modify the scale parameter.
     * @param s scale parameter for this distribution
     * @deprecated as of v2.1
     */
    @Deprecated
    void setScale(double s);
}

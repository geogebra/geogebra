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
 * The Hypergeometric Distribution.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/HypergeometricDistribution.html">
 * Hypergeometric Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision: 920852 $ $Date: 2010-03-09 13:53:44 +0100 (mar. 09 mars 2010) $
 */
public interface HypergeometricDistribution extends IntegerDistribution {

    /**
     * Access the number of successes.
     * @return the number of successes.
     */
    int getNumberOfSuccesses();

    /**
     * Access the population size.
     * @return the population size.
     */
    int getPopulationSize();

    /**
     * Access the sample size.
     * @return the sample size.
     */
    int getSampleSize();

    /**
     * Modify the number of successes.
     * @param num the new number of successes.
     * @deprecated as of v2.1
     */
    @Deprecated
    void setNumberOfSuccesses(int num);

    /**
     * Modify the population size.
     * @param size the new population size.
     * @deprecated as of v2.1
     */
    @Deprecated
    void setPopulationSize(int size);

    /**
     * Modify the sample size.
     * @param size the new sample size.
     * @deprecated as of v2.1
     */
    @Deprecated
    void setSampleSize(int size);
}

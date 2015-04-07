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
 * The Binomial Distribution.
 *
 * <p>
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/BinomialDistribution.html">
 * Binomial Distribution</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision: 920852 $ $Date: 2010-03-09 13:53:44 +0100 (mar. 09 mars 2010) $
 */
public interface BinomialDistribution extends IntegerDistribution {
    /**
     * Access the number of trials for this distribution.
     * @return the number of trials.
     */
    int getNumberOfTrials();

    /**
     * Access the probability of success for this distribution.
     * @return the probability of success.
     */
    double getProbabilityOfSuccess();

    /**
     * Change the number of trials for this distribution.
     * @param trials the new number of trials.
     * @deprecated as of v2.1
     */
    @Deprecated
    void setNumberOfTrials(int trials);

    /**
     * Change the probability of success for this distribution.
     * @param p the new probability of success.
     * @deprecated as of v2.1
     */
    @Deprecated
    void setProbabilityOfSuccess(double p);
}

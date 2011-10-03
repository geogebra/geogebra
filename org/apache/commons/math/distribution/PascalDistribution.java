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
 * The Pascal distribution.  The Pascal distribution is a special case of the
 * Negative Binomial distribution where the number of successes parameter is an
 * integer.
 *
 * There are various ways to express the probability mass and distribution
 * functions for the Pascal distribution.  The convention employed by the
 * library is to express these functions in terms of the number of failures in
 * a Bernoulli experiment [2].
 *
 * <p>
 * References:
 * <ol>
 * <li><a href="http://mathworld.wolfram.com/NegativeBinomialDistribution.html">
 * Negative Binomial Distribution</a></li>
 * <oi><a href="http://en.wikipedia.org/wiki/Negative_binomial_distribution#Waiting_time_in_a_Bernoulli_process">Waiting Time in a Bernoulli Process</a></li>
 * </ul>
 * </p>
 *
 * @version $Revision: 920852 $ $Date: 2010-03-09 13:53:44 +0100 (mar. 09 mars 2010) $
 * @since 1.2
 */
public interface PascalDistribution extends IntegerDistribution {
    /**
     * Access the number of successes for this distribution.
     *
     * @return the number of successes
     */
    int getNumberOfSuccesses();

    /**
     * Access the probability of success for this distribution.
     *
     * @return the probability of success
     */
    double getProbabilityOfSuccess();

    /**
     * Change the number of successes for this distribution.
     *
     * @param successes the new number of successes
     * @deprecated as of v2.1
     */
    @Deprecated
    void setNumberOfSuccesses(int successes);

    /**
     * Change the probability of success for this distribution.
     *
     * @param p the new probability of success
     * @deprecated as of v2.1
     */
    @Deprecated
    void setProbabilityOfSuccess(double p);
}

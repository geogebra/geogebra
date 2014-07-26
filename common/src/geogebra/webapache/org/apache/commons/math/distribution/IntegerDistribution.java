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
 * Interface for discrete distributions of integer-valued random variables.
 *
 * @version $Revision: 949535 $ $Date: 2010-05-30 19:00:15 +0200 (dim. 30 mai 2010) $
 */
public interface IntegerDistribution extends DiscreteDistribution {
    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(X = x). In other words, this
     * method represents the probability mass function for the distribution.
     *
     * @param x the value at which the probability density function is evaluated.
     * @return the value of the probability density function at x
     */
    double probability(int x);

    /**
     * For a random variable X whose values are distributed according
     * to this distribution, this method returns P(X &le; x).  In other words,
     * this method represents the probability distribution function, or PDF
     * for the distribution.
     *
     * @param x the value at which the PDF is evaluated.
     * @return PDF for this distribution.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    double cumulativeProbability(int x) throws MathException;

    /**
     * For this distribution, X, this method returns P(x0 &le; X &le; x1).
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @return the cumulative probability.
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if x0 > x1
     */
    double cumulativeProbability(int x0, int x1) throws MathException;

    /**
     * For this distribution, X, this method returns the largest x such that
     * P(X &le; x) <= p.
     * <p>
     * Note that this definition implies: <ul>
     * <li> If there is a minimum value, <code>m</code>, with positive
     * probability under (the density of) X, then <code>m - 1</code> is
     * returned by <code>inverseCumulativeProbability(0).</code>  If there is
     * no such value <code>m,  Integer.MIN_VALUE</code> is
     * returned.</li>
     * <li> If there is a maximum value, <code>M</code>, such that
     * P(X &le; M) =1, then <code>M</code> is returned by
     * <code>inverseCumulativeProbability(1).</code>
     * If there is no such value, <code>M, Integer.MAX_VALUE</code> is
     * returned.</li></ul></p>
     *
     * @param p the cumulative probability.
     * @return the largest x such that P(X &le; x) <= p
     * @throws MathException if the inverse cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if p is not between 0 and 1 (inclusive)
     */
    int inverseCumulativeProbability(double p) throws MathException;
}

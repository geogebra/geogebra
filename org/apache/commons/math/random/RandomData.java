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

package org.apache.commons.math.random;
import java.util.Collection;

/**
 * Random data generation utilities.
 * @version $Revision: 780975 $ $Date: 2009-06-02 11:05:37 +0200 (mar. 02 juin 2009) $
 */
public interface RandomData {
    /**
     * Generates a random string of hex characters of length
     * <code>len</code>.
     * <p>
     * The generated string will be random, but not cryptographically
     * secure. To generate cryptographically secure strings, use
     * <code>nextSecureHexString</code></p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>len > 0</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     *
     * @param len the length of the string to be generated
     * @return random string of hex characters of length <code>len</code>
     */
    String nextHexString(int len);

    /**
     * Generates a uniformly distributed random integer between
     * <code>lower</code> and <code>upper</code> (endpoints included).
     * <p>
     * The generated integer will be random, but not cryptographically secure.
     * To generate cryptographically secure integer sequences, use
     * <code>nextSecureInt</code>.</p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    int nextInt(int lower, int upper);

    /**
     * Generates a uniformly distributed random long integer between
     * <code>lower</code> and <code>upper</code> (endpoints included).
     * <p>
     * The generated long integer values will be random, but not
     * cryptographically secure.
     * To generate cryptographically secure sequences of longs, use
     * <code>nextSecureLong</code></p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    long nextLong(long lower, long upper);

    /**
     * Generates a random string of hex characters from a secure random
     * sequence.
     * <p>
     * If cryptographic security is not required,
     * use <code>nextHexString()</code>.</p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>len > 0</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     * @param len length of return string
     * @return the random hex string
     */
    String nextSecureHexString(int len);

    /**
     * Generates a uniformly distributed random integer between
     * <code>lower</code> and <code>upper</code> (endpoints included)
     * from a secure random sequence.
     * <p>
     * Sequences of integers generated using this method will be
     * cryptographically secure. If cryptographic security is not required,
     * <code>nextInt</code> should be used instead of this method.</p>
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://en.wikipedia.org/wiki/Cryptographically_secure_pseudo-random_number_generator">
     * Secure Random Sequence</a></p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a random integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    int nextSecureInt(int lower, int upper);

    /**
     * Generates a random long integer between <code>lower</code>
     * and <code>upper</code> (endpoints included).
     * <p>
     * Sequences of long values generated using this method will be
     * cryptographically secure. If cryptographic security is not required,
     * <code>nextLong</code> should be used instead of this method.</p>
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://en.wikipedia.org/wiki/Cryptographically_secure_pseudo-random_number_generator">
     * Secure Random Sequence</a></p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     *
     * @param lower lower bound for generated integer
     * @param upper upper bound for generated integer
     * @return a long integer greater than or equal to <code>lower</code>
     * and less than or equal to <code>upper</code>.
     */
    long nextSecureLong(long lower, long upper);

    /**
     * Generates a random value from the Poisson distribution with
     * the given mean.
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda366j.htm">
     * Poisson Distribution</a></p>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The specified mean <i>must</i> be positive (otherwise an
     *     IllegalArgumentException is thrown.)</li>
     * </ul></p>
     * @param mean Mean of the distribution
     * @return poisson deviate with the specified mean
     */
    long nextPoisson(double mean);

    /**
     * Generates a random value from the
     * Normal (or Gaussian) distribution with the given mean
     * and standard deviation.
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3661.htm">
     * Normal Distribution</a></p>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li><code>sigma > 0</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     * @param mu Mean of the distribution
     * @param sigma Standard deviation of the distribution
     * @return random value from Gaussian distribution with mean = mu,
     * standard deviation = sigma
     */
    double nextGaussian(double mu, double sigma);

    /**
     * Generates a random value from the exponential distribution
     * with expected value = <code>mean</code>.
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3667.htm">
     * Exponential Distribution</a></p>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li><code>mu >= 0</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     * @param mean Mean of the distribution
     * @return random value from exponential distribution
     */
    double nextExponential(double mean);

    /**
     * Generates a uniformly distributed random value from the open interval
     * (<code>lower</code>,<code>upper</code>) (i.e., endpoints excluded).
     * <p>
     * <strong>Definition</strong>:
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda3662.htm">
     * Uniform Distribution</a> <code>lower</code> and
     * <code>upper - lower</code> are the
     * <a href = "http://www.itl.nist.gov/div898/handbook/eda/section3/eda364.htm">
     * location and scale parameters</a>, respectively.</p>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>lower < upper</code> (otherwise an IllegalArgumentException
     *     is thrown.)</li>
     * </ul></p>
     *
     * @param lower lower endpoint of the interval of support
     * @param upper upper endpoint of the interval of support
     * @return uniformly distributed random value between lower
     * and upper (exclusive)
     */
    double nextUniform(double lower, double upper);

    /**
     * Generates an integer array of length <code>k</code> whose entries
     * are selected randomly, without repetition, from the integers <code>
     * 0 through n-1</code> (inclusive).
     * <p>
     * Generated arrays represent permutations
     * of <code>n</code> taken <code>k</code> at a time.</p>
     * <p>
     * <strong>Preconditions:</strong><ul>
     * <li> <code>k <= n</code></li>
     * <li> <code>n > 0</code> </li>
     * </ul>
     * If the preconditions are not met, an IllegalArgumentException is
     * thrown.</p>
     *
     * @param n domain of the permutation
     * @param k size of the permutation
     * @return random k-permutation of n
     */
    int[] nextPermutation(int n, int k);

    /**
     * Returns an array of <code>k</code> objects selected randomly
     * from the Collection <code>c</code>.
     * <p>
     * Sampling from <code>c</code>
     * is without replacement; but if <code>c</code> contains identical
     * objects, the sample may include repeats.  If all elements of <code>
     * c</code> are distinct, the resulting object array represents a
     * <a href="http://rkb.home.cern.ch/rkb/AN16pp/node250.html#SECTION0002500000000000000000">
     * Simple Random Sample</a> of size
     * <code>k</code> from the elements of <code>c</code>.</p>
     * <p>
     * <strong>Preconditions:</strong><ul>
     * <li> k must be less than or equal to the size of c </li>
     * <li> c must not be empty </li>
     * </ul>
     * If the preconditions are not met, an IllegalArgumentException is
     * thrown.</p>
     *
     * @param c collection to be sampled
     * @param k size of the sample
     * @return random sample of k elements from c
     */
    Object[] nextSample(Collection<?> c, int k);
}

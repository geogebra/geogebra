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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Represents an <a href="http://random.mat.sbg.ac.at/~ste/dipl/node11.html">
 * empirical probability distribution</a> -- a probability distribution derived
 * from observed data without making any assumptions about the functional form
 * of the population distribution that the data come from.<p>
 * Implementations of this interface maintain data structures, called
 * <i>distribution digests</i>, that describe empirical distributions and
 * support the following operations: <ul>
 * <li>loading the distribution from a file of observed data values</li>
 * <li>dividing the input data into "bin ranges" and reporting bin frequency
 *     counts (data for histogram)</li>
 * <li>reporting univariate statistics describing the full set of data values
 *     as well as the observations within each bin</li>
 * <li>generating random values from the distribution</li>
 * </ul>
 * Applications can use <code>EmpiricalDistribution</code> implementations to
 * build grouped frequency histograms representing the input data or to
 * generate random values "like" those in the input file -- i.e., the values
 * generated will follow the distribution of the values in the file.</p>
 *
 * @version $Revision: 817128 $ $Date: 2009-09-21 03:30:53 +0200 (lun. 21 sept. 2009) $
 */
public interface EmpiricalDistribution {

    /**
     * Computes the empirical distribution from the provided
     * array of numbers.
     *
     * @param dataArray the data array
     */
    void load(double[] dataArray);

    /**
     * Computes the empirical distribution from the input file.
     *
     * @param file the input file
     * @throws IOException if an IO error occurs
     */
    void load(File file) throws IOException;

    /**
     * Computes the empirical distribution using data read from a URL.
     *
     * @param url url of the input file
     * @throws IOException if an IO error occurs
     */
    void load(URL url) throws IOException;

    /**
     * Generates a random value from this distribution.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li></ul>
     * @return the random value.
     *
     * @throws IllegalStateException if the distribution has not been loaded
     */
    double getNextValue() throws IllegalStateException;


    /**
     * Returns a
     * {@link org.apache.commons.math.stat.descriptive.StatisticalSummary}
     * describing this distribution.
     * <strong>Preconditions:</strong><ul>
     * <li>the distribution must be loaded before invoking this method</li>
     * </ul>
     *
     * @return the sample statistics
     * @throws IllegalStateException if the distribution has not been loaded
     */
    StatisticalSummary getSampleStats() throws IllegalStateException;

    /**
     * Property indicating whether or not the distribution has been loaded.
     *
     * @return true if the distribution has been loaded
     */
    boolean isLoaded();

     /**
     * Returns the number of bins.
     *
     * @return the number of bins
     */
    int getBinCount();

    /**
     * Returns a list of
     * {@link org.apache.commons.math.stat.descriptive.SummaryStatistics}
     * containing statistics describing the values in each of the bins.  The
     * List is indexed on the bin number.
     *
     * @return List of bin statistics
     */
    List<SummaryStatistics> getBinStats();

    /**
     * Returns the array of upper bounds for the bins.  Bins are: <br/>
     * [min,upperBounds[0]],(upperBounds[0],upperBounds[1]],...,
     *  (upperBounds[binCount-2], upperBounds[binCount-1] = max].
     *
     * @return array of bin upper bounds
     */
    double[] getUpperBounds();

}

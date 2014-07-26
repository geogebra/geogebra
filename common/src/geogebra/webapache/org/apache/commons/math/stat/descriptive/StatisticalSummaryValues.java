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
package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;

import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/**
 *  Value object representing the results of a univariate statistical summary.
 *
 * @version $Revision: 1054186 $ $Date: 2011-01-01 03:28:46 +0100 (sam. 01 janv. 2011) $
 */
public class StatisticalSummaryValues implements Serializable,
    StatisticalSummary {

    /** Serialization id */
    private static final long serialVersionUID = -5108854841843722536L;

    /** The sample mean */
    private final double mean;

    /** The sample variance */
    private final double variance;

    /** The number of observations in the sample */
    private final long n;

    /** The maximum value */
    private final double max;

    /** The minimum value */
    private final double min;

    /** The sum of the sample values */
    private final double sum;

    /**
      * Constructor
      *
      * @param mean  the sample mean
      * @param variance  the sample variance
      * @param n  the number of observations in the sample
      * @param max  the maximum value
      * @param min  the minimum value
      * @param sum  the sum of the values
     */
    public StatisticalSummaryValues(double mean, double variance, long n,
        double max, double min, double sum) {
        super();
        this.mean = mean;
        this.variance = variance;
        this.n = n;
        this.max = max;
        this.min = min;
        this.sum = sum;
    }

    /**
     * @return Returns the max.
     */
    public double getMax() {
        return max;
    }

    /**
     * @return Returns the mean.
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return Returns the min.
     */
    public double getMin() {
        return min;
    }

    /**
     * @return Returns the number of values.
     */
    public long getN() {
        return n;
    }

    /**
     * @return Returns the sum.
     */
    public double getSum() {
        return sum;
    }

    /**
     * @return Returns the standard deviation
     */
    public double getStandardDeviation() {
        return FastMath.sqrt(variance);
    }

    /**
     * @return Returns the variance.
     */
    public double getVariance() {
        return variance;
    }

    /**
     * Returns true iff <code>object</code> is a
     * <code>StatisticalSummaryValues</code> instance and all statistics have
     *  the same values as this.
     *
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    @Override
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof StatisticalSummaryValues == false) {
            return false;
        }
        StatisticalSummaryValues stat = (StatisticalSummaryValues) object;
        return MathUtils.equalsIncludingNaN(stat.getMax(),      getMax())  &&
               MathUtils.equalsIncludingNaN(stat.getMean(),     getMean()) &&
               MathUtils.equalsIncludingNaN(stat.getMin(),      getMin())  &&
               MathUtils.equalsIncludingNaN(stat.getN(),        getN())    &&
               MathUtils.equalsIncludingNaN(stat.getSum(),      getSum())  &&
               MathUtils.equalsIncludingNaN(stat.getVariance(), getVariance());
    }

    /**
     * Returns hash code based on values of statistics
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = 31 + MathUtils.hash(getMax());
        result = result * 31 + MathUtils.hash(getMean());
        result = result * 31 + MathUtils.hash(getMin());
        result = result * 31 + MathUtils.hash(getN());
        result = result * 31 + MathUtils.hash(getSum());
        result = result * 31 + MathUtils.hash(getVariance());
        return result;
    }

    /**
     * Generates a text report displaying values of statistics.
     * Each statistic is displayed on a separate line.
     *
     * @return String with line feeds displaying statistics
     */
    @Override
    public String toString() {
        StringBuilder outBuffer = new StringBuilder();
        String endl = "\n";
        outBuffer.append("StatisticalSummaryValues:").append(endl);
        outBuffer.append("n: ").append(getN()).append(endl);
        outBuffer.append("min: ").append(getMin()).append(endl);
        outBuffer.append("max: ").append(getMax()).append(endl);
        outBuffer.append("mean: ").append(getMean()).append(endl);
        outBuffer.append("std dev: ").append(getStandardDeviation())
            .append(endl);
        outBuffer.append("variance: ").append(getVariance()).append(endl);
        outBuffer.append("sum: ").append(getSum()).append(endl);
        return outBuffer.toString();
    }

}

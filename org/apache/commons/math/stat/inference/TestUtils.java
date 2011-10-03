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
package org.apache.commons.math.stat.inference;

import java.util.Collection;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;

/**
 * A collection of static methods to create inference test instances or to
 * perform inference tests.
 *
 * <p>
 * The set methods are not compatible with using the class in multiple threads,
 * and have therefore been deprecated (along with the getters).
 * The setters and getters will be removed in version 3.0.
 *
 * @since 1.1
 * @version $Revision: 1067582 $ $Date: 2011-02-06 04:55:32 +0100 (dim. 06 févr. 2011) $
 */
public class TestUtils  {

    /** Singleton TTest instance using default implementation. */
    private static TTest tTest = new TTestImpl();

    /** Singleton ChiSquareTest instance using default implementation. */
    private static ChiSquareTest chiSquareTest =
        new ChiSquareTestImpl();

    /** Singleton ChiSquareTest instance using default implementation. */
    private static UnknownDistributionChiSquareTest unknownDistributionChiSquareTest =
        new ChiSquareTestImpl();

    /** Singleton OneWayAnova instance using default implementation. */
    private static OneWayAnova oneWayAnova =
        new OneWayAnovaImpl();

    /**
     * Prevent instantiation.
     */
    protected TestUtils() {
        super();
    }

    /**
     * Set the (singleton) TTest instance.
     *
     * @param chiSquareTest the new instance to use
     * @since 1.2
     * @deprecated 2.2 will be removed in 3.0 - not compatible with use from multiple threads
     */
    @Deprecated
    public static void setChiSquareTest(TTest chiSquareTest) {
        TestUtils.tTest = chiSquareTest;
    }

    /**
     * Return a (singleton) TTest instance.  Does not create a new instance.
     *
     * @return a TTest instance
     * @deprecated 2.2 will be removed in 3.0
     */
    @Deprecated
    public static TTest getTTest() {
        return tTest;
    }

    /**
     * Set the (singleton) ChiSquareTest instance.
     *
     * @param chiSquareTest the new instance to use
     * @since 1.2
     * @deprecated 2.2 will be removed in 3.0 - not compatible with use from multiple threads
     */
    @Deprecated
    public static void setChiSquareTest(ChiSquareTest chiSquareTest) {
        TestUtils.chiSquareTest = chiSquareTest;
    }

    /**
     * Return a (singleton) ChiSquareTest instance.  Does not create a new instance.
     *
     * @return a ChiSquareTest instance
     * @deprecated 2.2 will be removed in 3.0
     */
    @Deprecated
    public static ChiSquareTest getChiSquareTest() {
        return chiSquareTest;
    }

    /**
     * Set the (singleton) UnknownDistributionChiSquareTest instance.
     *
     * @param unknownDistributionChiSquareTest the new instance to use
     * @since 1.2
     * @deprecated 2.2 will be removed in 3.0 - not compatible with use from multiple threads
     */
    @Deprecated
    public static void setUnknownDistributionChiSquareTest(UnknownDistributionChiSquareTest unknownDistributionChiSquareTest) {
        TestUtils.unknownDistributionChiSquareTest = unknownDistributionChiSquareTest;
    }

    /**
     * Return a (singleton) UnknownDistributionChiSquareTest instance.  Does not create a new instance.
     *
     * @return a UnknownDistributionChiSquareTest instance
     * @deprecated 2.2 will be removed in 3.0
     */
    @Deprecated
    public static UnknownDistributionChiSquareTest getUnknownDistributionChiSquareTest() {
        return unknownDistributionChiSquareTest;
    }

    /**
     * Set the (singleton) OneWayAnova instance
     *
     * @param oneWayAnova the new instance to use
     * @since 1.2
     * @deprecated 2.2 will be removed in 3.0 - not compatible with use from multiple threads
     */
    @Deprecated
    public static void setOneWayAnova(OneWayAnova oneWayAnova) {
        TestUtils.oneWayAnova = oneWayAnova;
    }

    /**
     * Return a (singleton) OneWayAnova instance.  Does not create a new instance.
     *
     * @return a OneWayAnova instance
     * @since 1.2
     * @deprecated 2.2 will be removed in 3.0
     */
    @Deprecated
    public static OneWayAnova getOneWayAnova() {
        return oneWayAnova;
    }


    // CHECKSTYLE: stop JavadocMethodCheck

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticT(double[], double[])
     */
    public static double homoscedasticT(double[] sample1, double[] sample2)
        throws IllegalArgumentException {
        return tTest.homoscedasticT(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticT(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticT(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2)
        throws IllegalArgumentException {
        return tTest.homoscedasticT(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticTTest(double[], double[], double)
     */
    public static boolean homoscedasticTTest(double[] sample1, double[] sample2,
            double alpha)
        throws IllegalArgumentException, MathException {
        return tTest. homoscedasticTTest(sample1, sample2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticTTest(double[], double[])
     */
    public static double homoscedasticTTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.homoscedasticTTest(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#homoscedasticTTest(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double homoscedasticTTest(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2)
        throws IllegalArgumentException, MathException {
        return tTest.homoscedasticTTest(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#pairedT(double[], double[])
     */
    public static double pairedT(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.pairedT(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#pairedTTest(double[], double[], double)
     */
    public static boolean pairedTTest(double[] sample1, double[] sample2,
        double alpha)
        throws IllegalArgumentException, MathException {
        return tTest.pairedTTest(sample1, sample2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#pairedTTest(double[], double[])
     */
    public static double pairedTTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.pairedTTest(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(double, double[])
     */
    public static double t(double mu, double[] observed)
        throws IllegalArgumentException {
        return tTest.t(mu, observed);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(double, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double t(double mu, StatisticalSummary sampleStats)
        throws IllegalArgumentException {
        return tTest.t(mu, sampleStats);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(double[], double[])
     */
    public static double t(double[] sample1, double[] sample2)
        throws IllegalArgumentException {
        return tTest.t(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#t(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double t(StatisticalSummary sampleStats1,
            StatisticalSummary sampleStats2)
        throws IllegalArgumentException {
        return tTest.t(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, double[], double)
     */
    public static boolean tTest(double mu, double[] sample, double alpha)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(mu, sample, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, double[])
     */
    public static double tTest(double mu, double[] sample)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(mu, sample);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, org.apache.commons.math.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(double mu, StatisticalSummary sampleStats,
        double alpha)
        throws IllegalArgumentException, MathException {
        return tTest. tTest(mu, sampleStats, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(double mu, StatisticalSummary sampleStats)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(mu, sampleStats);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double[], double[], double)
     */
    public static boolean tTest(double[] sample1, double[] sample2, double alpha)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(sample1, sample2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(double[], double[])
     */
    public static double tTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(sample1, sample2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary, double)
     */
    public static boolean tTest(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2, double alpha)
        throws IllegalArgumentException, MathException {
        return tTest. tTest(sampleStats1, sampleStats2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.TTest#tTest(org.apache.commons.math.stat.descriptive.StatisticalSummary, org.apache.commons.math.stat.descriptive.StatisticalSummary)
     */
    public static double tTest(StatisticalSummary sampleStats1,
        StatisticalSummary sampleStats2)
        throws IllegalArgumentException, MathException {
        return tTest.tTest(sampleStats1, sampleStats2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquare(double[], long[])
     */
    public static double chiSquare(double[] expected, long[] observed)
        throws IllegalArgumentException {
        return chiSquareTest.chiSquare(expected, observed);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquare(long[][])
     */
    public static double chiSquare(long[][] counts)
        throws IllegalArgumentException {
        return chiSquareTest.chiSquare(counts);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(double[], long[], double)
     */
    public static boolean chiSquareTest(double[] expected, long[] observed,
        double alpha)
        throws IllegalArgumentException, MathException {
        return chiSquareTest.chiSquareTest(expected, observed, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(double[], long[])
     */
    public static double chiSquareTest(double[] expected, long[] observed)
        throws IllegalArgumentException, MathException {
        return chiSquareTest.chiSquareTest(expected, observed);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(long[][], double)
     */
    public static boolean chiSquareTest(long[][] counts, double alpha)
        throws IllegalArgumentException, MathException {
        return chiSquareTest. chiSquareTest(counts, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.ChiSquareTest#chiSquareTest(long[][])
     */
    public static double chiSquareTest(long[][] counts)
        throws IllegalArgumentException, MathException {
        return chiSquareTest. chiSquareTest(counts);
    }

    /**
     * @see org.apache.commons.math.stat.inference.UnknownDistributionChiSquareTest#chiSquareDataSetsComparison(long[], long[])
     *
     * @since 1.2
     */
    public static double chiSquareDataSetsComparison(long[] observed1, long[] observed2)
        throws IllegalArgumentException {
        return unknownDistributionChiSquareTest.chiSquareDataSetsComparison(observed1, observed2);
    }

    /**
     * @see org.apache.commons.math.stat.inference.UnknownDistributionChiSquareTest#chiSquareTestDataSetsComparison(long[], long[])
     *
     * @since 1.2
     */
    public static double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2)
        throws IllegalArgumentException, MathException {
        return unknownDistributionChiSquareTest.chiSquareTestDataSetsComparison(observed1, observed2);
    }


    /**
     * @see org.apache.commons.math.stat.inference.UnknownDistributionChiSquareTest#chiSquareTestDataSetsComparison(long[], long[], double)
     *
     * @since 1.2
     */
    public static boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2,
        double alpha)
        throws IllegalArgumentException, MathException {
        return unknownDistributionChiSquareTest.chiSquareTestDataSetsComparison(observed1, observed2, alpha);
    }

    /**
     * @see org.apache.commons.math.stat.inference.OneWayAnova#anovaFValue(Collection)
     *
     * @since 1.2
     */
    public static double oneWayAnovaFValue(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException {
        return oneWayAnova.anovaFValue(categoryData);
    }

    /**
     * @see org.apache.commons.math.stat.inference.OneWayAnova#anovaPValue(Collection)
     *
     * @since 1.2
     */
    public static double oneWayAnovaPValue(Collection<double[]> categoryData)
    throws IllegalArgumentException, MathException {
        return oneWayAnova.anovaPValue(categoryData);
    }

    /**
     * @see org.apache.commons.math.stat.inference.OneWayAnova#anovaTest(Collection,double)
     *
     * @since 1.2
     */
    public static boolean oneWayAnovaTest(Collection<double[]> categoryData, double alpha)
    throws IllegalArgumentException, MathException {
        return oneWayAnova.anovaTest(categoryData, alpha);
    }

    // CHECKSTYLE: resume JavadocMethodCheck

}

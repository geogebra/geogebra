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
package org.apache.commons.math;


/**
 * Interface for algorithms handling convergence settings.
 * <p>
 * This interface only deals with convergence parameters setting, not
 * execution of the algorithms per se.
 * </p>
 * @see ConvergenceException
 * @version $Revision: 1042336 $ $Date: 2010-12-05 13:40:48 +0100 (dim. 05 déc. 2010) $
 * @since 2.0
 * @deprecated in 2.2 (to be removed in 3.0). The concept of "iteration" will
 * be moved to a new {@code IterativeAlgorithm}. The concept of "accuracy" is
 * currently is also contained in {@link org.apache.commons.math.optimization.SimpleRealPointChecker}
 * and similar classes.
 */
@Deprecated
public interface ConvergingAlgorithm {

    /**
     * Set the upper limit for the number of iterations.
     * <p>
     * Usually a high iteration count indicates convergence problems. However,
     * the "reasonable value" varies widely for different algorithms. Users are
     * advised to use the default value supplied by the algorithm.</p>
     * <p>
     * A {@link ConvergenceException} will be thrown if this number
     * is exceeded.</p>
     *
     * @param count maximum number of iterations
     */
    void setMaximalIterationCount(int count);

    /**
     * Get the upper limit for the number of iterations.
     *
     * @return the actual upper limit
     */
    int getMaximalIterationCount();

    /**
     * Reset the upper limit for the number of iterations to the default.
     * <p>
     * The default value is supplied by the algorithm implementation.</p>
     *
     * @see #setMaximalIterationCount(int)
     */
    void resetMaximalIterationCount();

    /**
     * Set the absolute accuracy.
     * <p>
     * The default is usually chosen so that results in the interval
     * -10..-0.1 and +0.1..+10 can be found with a reasonable accuracy. If the
     * expected absolute value of your results is of much smaller magnitude, set
     * this to a smaller value.</p>
     * <p>
     * Algorithms are advised to do a plausibility check with the relative
     * accuracy, but clients should not rely on this.</p>
     *
     * @param accuracy the accuracy.
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     * the solver or is otherwise deemed unreasonable.
     */
    void setAbsoluteAccuracy(double accuracy);

    /**
     * Get the actual absolute accuracy.
     *
     * @return the accuracy
     */
    double getAbsoluteAccuracy();

    /**
     * Reset the absolute accuracy to the default.
     * <p>
     * The default value is provided by the algorithm implementation.</p>
     */
    void resetAbsoluteAccuracy();

    /**
     * Set the relative accuracy.
     * <p>
     * This is used to stop iterations if the absolute accuracy can't be
     * achieved due to large values or short mantissa length.</p>
     * <p>
     * If this should be the primary criterion for convergence rather then a
     * safety measure, set the absolute accuracy to a ridiculously small value,
     * like {@link org.apache.commons.math.util.MathUtils#SAFE_MIN MathUtils.SAFE_MIN}.</p>
     *
     * @param accuracy the relative accuracy.
     * @throws IllegalArgumentException if the accuracy can't be achieved by
     *  the algorithm or is otherwise deemed unreasonable.
     */
    void setRelativeAccuracy(double accuracy);

    /**
     * Get the actual relative accuracy.
     * @return the accuracy
     */
    double getRelativeAccuracy();

    /**
     * Reset the relative accuracy to the default.
     * The default value is provided by the algorithm implementation.
     */
    void resetRelativeAccuracy();

    /**
     * Get the number of iterations in the last run of the algorithm.
     * <p>
     * This is mainly meant for testing purposes. It may occasionally
     * help track down performance problems: if the iteration count
     * is notoriously high, check whether the problem is evaluated
     * properly, and whether another algorithm is more amenable to the
     * problem.</p>
     *
     * @return the last iteration count.
     * @throws IllegalStateException if there is no result available, either
     * because no result was yet computed or the last attempt failed.
     */
    int getIterationCount();

}

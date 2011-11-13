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

package org.apache.commons.math.optimization;

import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

/**
 * Simple implementation of the {@link VectorialConvergenceChecker} interface using
 * only point coordinates.
 * <p>
 * Convergence is considered to have been reached if either the relative
 * difference between each point coordinate are smaller than a threshold
 * or if either the absolute difference between the point coordinates are
 * smaller than another threshold.
 * </p>
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 2.0
 */
public class SimpleVectorialPointChecker implements VectorialConvergenceChecker {

    /** Default relative threshold. */
    private static final double DEFAULT_RELATIVE_THRESHOLD = 100 * MathUtils.EPSILON;

    /** Default absolute threshold. */
    private static final double DEFAULT_ABSOLUTE_THRESHOLD = 100 * MathUtils.SAFE_MIN;

    /** Relative tolerance threshold. */
    private final double relativeThreshold;

    /** Absolute tolerance threshold. */
    private final double absoluteThreshold;

   /** Build an instance with default threshold.
     */
    public SimpleVectorialPointChecker() {
        this.relativeThreshold = DEFAULT_RELATIVE_THRESHOLD;
        this.absoluteThreshold = DEFAULT_ABSOLUTE_THRESHOLD;
    }

    /** Build an instance with a specified threshold.
     * <p>
     * In order to perform only relative checks, the absolute tolerance
     * must be set to a negative value. In order to perform only absolute
     * checks, the relative tolerance must be set to a negative value.
     * </p>
     * @param relativeThreshold relative tolerance threshold
     * @param absoluteThreshold absolute tolerance threshold
     */
    public SimpleVectorialPointChecker(final double relativeThreshold,
                                       final double absoluteThreshold) {
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
    }

    /** {@inheritDoc} */
    public boolean converged(final int iteration,
                             final VectorialPointValuePair previous,
                             final VectorialPointValuePair current) {
        final double[] p = previous.getPointRef();
        final double[] c = current.getPointRef();
        for (int i = 0; i < p.length; ++i) {
            final double pi         = p[i];
            final double ci         = c[i];
            final double difference = FastMath.abs(pi - ci);
            final double size       = FastMath.max(FastMath.abs(pi), FastMath.abs(ci));
            if ((difference > (size * relativeThreshold)) &&
                (difference > absoluteThreshold)) {
                return false;
            }
        }
        return true;
    }

}

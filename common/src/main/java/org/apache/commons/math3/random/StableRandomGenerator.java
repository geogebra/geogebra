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
package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

/**
 * <p>This class provides a stable normalized random generator. It samples from a stable
 * distribution with location parameter 0 and scale 1.</p>
 *
 * <p>The implementation uses the Chambers-Mallows-Stuck method as described in
 * <i>Handbook of computational statistics: concepts and methods</i> by
 * James E. Gentle, Wolfgang H&auml;rdle, Yuichi Mori.</p>
 *
 * @since 3.0
 */
public class StableRandomGenerator implements NormalizedRandomGenerator {
    /** Underlying generator. */
    private final RandomGenerator generator;

    /** stability parameter */
    private final double alpha;

    /** skewness parameter */
    private final double beta;

    /** cache of expression value used in generation */
    private final double zeta;

    /**
     * Create a new generator.
     *
     * @param generator underlying random generator to use
     * @param alpha Stability parameter. Must be in range (0, 2]
     * @param beta Skewness parameter. Must be in range [-1, 1]
     * @throws NullArgumentException if generator is null
     * @throws OutOfRangeException if {@code alpha <= 0} or {@code alpha > 2}
     * or {@code beta < -1} or {@code beta > 1}
     */
    public StableRandomGenerator(final RandomGenerator generator,
                                 final double alpha, final double beta)
        throws NullArgumentException, OutOfRangeException {
        if (generator == null) {
            throw new NullArgumentException();
        }

        if (!(alpha > 0d && alpha <= 2d)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_LEFT,
                    alpha, 0, 2);
        }

        if (!(beta >= -1d && beta <= 1d)) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_RANGE_SIMPLE,
                    beta, -1, 1);
        }

        this.generator = generator;
        this.alpha = alpha;
        this.beta = beta;
        if (alpha < 2d && beta != 0d) {
            zeta = beta * Math.tan(Math.PI * alpha / 2);
        } else {
            zeta = 0d;
        }
    }

    /**
     * Generate a random scalar with zero location and unit scale.
     *
     * @return a random scalar with zero location and unit scale
     */
    public double nextNormalizedDouble() {
        // we need 2 uniform random numbers to calculate omega and phi
        double omega = -Math.log(generator.nextDouble());
        double phi = Math.PI * (generator.nextDouble() - 0.5);

        // Normal distribution case (Box-Muller algorithm)
        if (alpha == 2d) {
            return Math.sqrt(2d * omega) * Math.sin(phi);
        }

        double x;
        // when beta = 0, zeta is zero as well
        // Thus we can exclude it from the formula
        if (beta == 0d) {
            // Cauchy distribution case
            if (alpha == 1d) {
                x = Math.tan(phi);
            } else {
                x = Math.pow(omega * Math.cos((1 - alpha) * phi),
                    1d / alpha - 1d) *
                    Math.sin(alpha * phi) /
                    Math.pow(Math.cos(phi), 1d / alpha);
            }
        } else {
            // Generic stable distribution
            double cosPhi = Math.cos(phi);
            // to avoid rounding errors around alpha = 1
            if (Math.abs(alpha - 1d) > 1e-8) {
                double alphaPhi = alpha * phi;
                double invAlphaPhi = phi - alphaPhi;
                x = (Math.sin(alphaPhi) + zeta * Math.cos(alphaPhi)) / cosPhi *
                    (Math.cos(invAlphaPhi) + zeta * Math.sin(invAlphaPhi)) /
                     Math.pow(omega * cosPhi, (1 - alpha) / alpha);
            } else {
                double betaPhi = Math.PI / 2 + beta * phi;
                x = 2d / Math.PI * (betaPhi * Math.tan(phi) - beta *
                    Math.log(Math.PI / 2d * omega * cosPhi / betaPhi));

                if (alpha != 1d) {
                    x += beta * Math.tan(Math.PI * alpha / 2);
                }
            }
        }
        return x;
    }
}

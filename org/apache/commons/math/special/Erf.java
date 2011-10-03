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
package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.FastMath;

/**
 * This is a utility class that provides computation methods related to the
 * error functions.
 *
 * @version $Revision: 1054186 $ $Date: 2011-01-01 03:28:46 +0100 (sam. 01 janv. 2011) $
 */
public class Erf {

    /**
     * Default constructor.  Prohibit instantiation.
     */
    private Erf() {
        super();
    }

    /**
     * <p>Returns the error function</p>
     * <p>erf(x) = 2/&radic;&pi; <sub>0</sub>&int;<sup>x</sup> e<sup>-t<sup>2</sup></sup>dt </p>
     *
     * <p>This implementation computes erf(x) using the
     * {@link Gamma#regularizedGammaP(double, double, double, int) regularized gamma function},
     * following <a href="http://mathworld.wolfram.com/Erf.html"> Erf</a>, equation (3)</p>
     *
     * <p>The value returned is always between -1 and 1 (inclusive).  If {@code abs(x) > 40}, then
     * {@code erf(x)} is indistinguishable from either 1 or -1 as a double, so the appropriate extreme
     * value is returned.</p>
     *
     * @param x the value.
     * @return the error function erf(x)
     * @throws MathException if the algorithm fails to converge.
     * @see Gamma#regularizedGammaP(double, double, double, int)
     */
    public static double erf(double x) throws MathException {
        if (FastMath.abs(x) > 40) {
            return x > 0 ? 1 : -1;
        }
        double ret = Gamma.regularizedGammaP(0.5, x * x, 1.0e-15, 10000);
        if (x < 0) {
            ret = -ret;
        }
        return ret;
    }

    /**
     * <p>Returns the complementary error function</p>
     * <p>erfc(x) = 2/&radic;&pi; <sub>x</sub>&int;<sup>&infin;</sup> e<sup>-t<sup>2</sup></sup>dt <br/>
     *    = 1 - {@link #erf(double) erf(x)} </p>
     *
     * <p>This implementation computes erfc(x) using the
     * {@link Gamma#regularizedGammaQ(double, double, double, int) regularized gamma function},
     * following <a href="http://mathworld.wolfram.com/Erf.html"> Erf</a>, equation (3).</p>
     *
     * <p>The value returned is always between 0 and 2 (inclusive).  If {@code abs(x) > 40}, then
     * {@code erf(x)} is indistinguishable from either 0 or 2 as a double, so the appropriate extreme
     * value is returned.</p>
     *
     * @param x the value
     * @return the complementary error function erfc(x)
     * @throws MathException if the algorithm fails to converge
     * @see Gamma#regularizedGammaQ(double, double, double, int)
     * @since 2.2
     */
    public static double erfc(double x) throws MathException {
        if (FastMath.abs(x) > 40) {
            return x > 0 ? 0 : 2;
        }
        final double ret = Gamma.regularizedGammaQ(0.5, x * x, 1.0e-15, 10000);
        return x < 0 ? 2 - ret : ret;
    }
}


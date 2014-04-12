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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.special;

import org.apache.commons.math.MathException;

/**
 * This is a utility class that provides computation methods related to the
 * error functions.
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 13:36:48 -0400 (Sat, 05 Sep 2009) $
 */
public class Erf {

    /**
     * Default constructor.  Prohibit instantiation.
     */
    private Erf() {
        super();
    }

    /**
     * Returns the error function erf(x).
     *
     * The implementation of this method is based on:
     * <ul>
     * <li>
     * <a href="http://mathworld.wolfram.com/Erf.html">
     * Erf</a>, equation (3).</li>
     * </ul>
     *
     * @param x the value.
     * @return the error function erf(x)
     * @throws MathException if the algorithm fails to converge.
     */
    public static double erf(double x) throws MathException {
    	
        if (Math.abs(x) > 40) {
            return x > 0 ? 1 : -1;
        }

        double ret = Gamma.regularizedGammaP(0.5, x * x, 1.0e-15, 10000);
        if (x < 0) {
            ret = -ret;
        }
        return ret;
    }
}

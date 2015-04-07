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

package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.util.FastMath;

/** Harmonic function of the form <code>f (t) = a cos (&omega; t + &phi;)</code>.
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 2.0
 */
public class HarmonicFunction implements DifferentiableUnivariateRealFunction {

    /** Amplitude a. */
    private final double a;

    /** Pulsation &omega;. */
    private final double omega;

    /** Phase &phi;. */
    private final double phi;

    /** Simple constructor.
     * @param a amplitude
     * @param omega pulsation
     * @param phi phase
     */
    public HarmonicFunction(double a, double omega, double phi) {
        this.a     = a;
        this.omega = omega;
        this.phi   = phi;
    }

    /** {@inheritDoc} */
    public double value(double x) {
        return a * FastMath.cos(omega * x + phi);
    }

    /** {@inheritDoc} */
    public HarmonicFunction derivative() {
        return new HarmonicFunction(a * omega, omega, phi + FastMath.PI / 2);
    }

    /** Get the amplitude a.
     * @return amplitude a;
     */
    public double getAmplitude() {
        return a;
    }

    /** Get the pulsation &omega;.
     * @return pulsation &omega;
     */
    public double getPulsation() {
        return omega;
    }

    /** Get the phase &phi;.
     * @return phase &phi;
     */
    public double getPhase() {
        return phi;
    }

}

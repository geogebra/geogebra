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

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.util.FastMath;

/** This class implements a curve fitting specialized for sinusoids.
 * <p>Harmonic fitting is a very simple case of curve fitting. The
 * estimated coefficients are the amplitude a, the pulsation &omega; and
 * the phase &phi;: <code>f (t) = a cos (&omega; t + &phi;)</code>. They are
 * searched by a least square estimator initialized with a rough guess
 * based on integrals.</p>
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.0
 */
public class HarmonicFitter {

    /** Fitter for the coefficients. */
    private final CurveFitter fitter;

    /** Values for amplitude, pulsation &omega; and phase &phi;. */
    private double[] parameters;

    /** Simple constructor.
     * @param optimizer optimizer to use for the fitting
     */
    public HarmonicFitter(final DifferentiableMultivariateVectorialOptimizer optimizer) {
        this.fitter = new CurveFitter(optimizer);
        parameters  = null;
    }

    /** Simple constructor.
     * <p>This constructor can be used when a first guess of the
     * coefficients is already known.</p>
     * @param optimizer optimizer to use for the fitting
     * @param initialGuess guessed values for amplitude (index 0),
     * pulsation &omega; (index 1) and phase &phi; (index 2)
     */
    public HarmonicFitter(final DifferentiableMultivariateVectorialOptimizer optimizer,
                          final double[] initialGuess) {
        this.fitter     = new CurveFitter(optimizer);
        this.parameters = initialGuess.clone();
    }

    /** Add an observed weighted (x,y) point to the sample.
     * @param weight weight of the observed point in the fit
     * @param x abscissa of the point
     * @param y observed value of the point at x, after fitting we should
     * have P(x) as close as possible to this value
     */
    public void addObservedPoint(double weight, double x, double y) {
        fitter.addObservedPoint(weight, x, y);
    }

    /** Fit an harmonic function to the observed points.
     * @return harmonic function best fitting the observed points
     * @throws OptimizationException if the sample is too short or if
     * the first guess cannot be computed
     */
    public HarmonicFunction fit() throws OptimizationException {

        // shall we compute the first guess of the parameters ourselves ?
        if (parameters == null) {
            final WeightedObservedPoint[] observations = fitter.getObservations();
            if (observations.length < 4) {
                throw new OptimizationException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE,
                                                observations.length, 4);
            }

            HarmonicCoefficientsGuesser guesser = new HarmonicCoefficientsGuesser(observations);
            guesser.guess();
            parameters = new double[] {
                guesser.getGuessedAmplitude(),
                guesser.getGuessedPulsation(),
                guesser.getGuessedPhase()
            };

        }

        try {
            double[] fitted = fitter.fit(new ParametricHarmonicFunction(), parameters);
            return new HarmonicFunction(fitted[0], fitted[1], fitted[2]);
        } catch (FunctionEvaluationException fee) {
            // should never happen
            throw new RuntimeException(fee);
        }

    }

    /** Parametric harmonic function. */
    private static class ParametricHarmonicFunction implements ParametricRealFunction {

        /** {@inheritDoc} */
        public double value(double x, double[] parameters) {
            final double a     = parameters[0];
            final double omega = parameters[1];
            final double phi   = parameters[2];
            return a * FastMath.cos(omega * x + phi);
        }

        /** {@inheritDoc} */
        public double[] gradient(double x, double[] parameters) {
            final double a     = parameters[0];
            final double omega = parameters[1];
            final double phi   = parameters[2];
            final double alpha = omega * x + phi;
            final double cosAlpha = FastMath.cos(alpha);
            final double sinAlpha = FastMath.sin(alpha);
            return new double[] { cosAlpha, -a * x * sinAlpha, -a * sinAlpha };
        }

    }

}

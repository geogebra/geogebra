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
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.fitting.CurveFitter;
import org.apache.commons.math.optimization.fitting.WeightedObservedPoint;

/**
 * Fits points to a Gaussian function (that is, a {@link GaussianFunction}).
 * <p>
 * Usage example:
 * <pre>
 *   GaussianFitter fitter = new GaussianFitter(
 *     new LevenbergMarquardtOptimizer());
 *   fitter.addObservedPoint(4.0254623,  531026.0);
 *   fitter.addObservedPoint(4.03128248, 984167.0);
 *   fitter.addObservedPoint(4.03839603, 1887233.0);
 *   fitter.addObservedPoint(4.04421621, 2687152.0);
 *   fitter.addObservedPoint(4.05132976, 3461228.0);
 *   fitter.addObservedPoint(4.05326982, 3580526.0);
 *   fitter.addObservedPoint(4.05779662, 3439750.0);
 *   fitter.addObservedPoint(4.0636168,  2877648.0);
 *   fitter.addObservedPoint(4.06943698, 2175960.0);
 *   fitter.addObservedPoint(4.07525716, 1447024.0);
 *   fitter.addObservedPoint(4.08237071, 717104.0);
 *   fitter.addObservedPoint(4.08366408, 620014.0);
 *  GaussianFunction fitFunction = fitter.fit();
 * </pre>
 *
 * @see ParametricGaussianFunction
 * @since 2.2
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public class GaussianFitter {

    /** Fitter used for fitting. */
    private final CurveFitter fitter;

    /**
     * Constructs an instance using the specified optimizer.
     *
     * @param optimizer optimizer to use for the fitting
     */
    public GaussianFitter(DifferentiableMultivariateVectorialOptimizer optimizer) {
        fitter = new CurveFitter(optimizer);
    }

    /**
     * Adds point (<code>x</code>, <code>y</code>) to list of observed points
     * with a weight of 1.0.
     *
     * @param x <tt>x</tt> point value
     * @param y <tt>y</tt> point value
     */
    public void addObservedPoint(double x, double y) {
        addObservedPoint(1.0, x, y);
    }

    /**
     * Adds point (<code>x</code>, <code>y</code>) to list of observed points
     * with a weight of <code>weight</code>.
     *
     * @param weight weight assigned to point
     * @param x <tt>x</tt> point value
     * @param y <tt>y</tt> point value
     */
    public void addObservedPoint(double weight, double x, double y) {
        fitter.addObservedPoint(weight, x, y);
    }

    /**
     * Fits Gaussian function to the observed points.
     *
     * @return Gaussian function best fitting the observed points
     *
     * @throws FunctionEvaluationException if <code>CurveFitter.fit</code> throws it
     * @throws OptimizationException if <code>CurveFitter.fit</code> throws it
     * @throws IllegalArgumentException if <code>CurveFitter.fit</code> throws it
     *
     * @see CurveFitter
     */
    public GaussianFunction fit() throws FunctionEvaluationException, OptimizationException {
        return new GaussianFunction(fitter.fit(new ParametricGaussianFunction(),
                                               createParametersGuesser(fitter.getObservations()).guess()));
    }

    /**
     * Factory method to create a <code>GaussianParametersGuesser</code>
     * instance initialized with the specified observations.
     *
     * @param observations points used to initialize the created
     *        <code>GaussianParametersGuesser</code> instance
     *
     * @return new <code>GaussianParametersGuesser</code> instance
     */
    protected GaussianParametersGuesser createParametersGuesser(WeightedObservedPoint[] observations) {
        return new GaussianParametersGuesser(observations);
    }
}

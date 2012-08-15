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

package org.apache.commons.math.estimation;

import java.io.Serializable;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.InvalidMatrixException;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.util.FastMath;

/**
 * This class implements a solver for estimation problems.
 *
 * <p>This class solves estimation problems using a weighted least
 * squares criterion on the measurement residuals. It uses a
 * Gauss-Newton algorithm.</p>
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general
 *
 */
@Deprecated
public class GaussNewtonEstimator extends AbstractEstimator implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 5485001826076289109L;

    /** Default threshold for cost steady state detection. */
    private static final double DEFAULT_STEADY_STATE_THRESHOLD = 1.0e-6;

    /** Default threshold for cost convergence. */
    private static final double DEFAULT_CONVERGENCE = 1.0e-6;

    /** Threshold for cost steady state detection. */
    private double steadyStateThreshold;

    /** Threshold for cost convergence. */
    private double convergence;

    /** Simple constructor with default settings.
     * <p>
     * The estimator is built with default values for all settings.
     * </p>
     * @see #DEFAULT_STEADY_STATE_THRESHOLD
     * @see #DEFAULT_CONVERGENCE
     * @see AbstractEstimator#DEFAULT_MAX_COST_EVALUATIONS
     */
    public GaussNewtonEstimator() {
        this.steadyStateThreshold = DEFAULT_STEADY_STATE_THRESHOLD;
        this.convergence          = DEFAULT_CONVERGENCE;
    }

    /**
     * Simple constructor.
     *
     * <p>This constructor builds an estimator and stores its convergence
     * characteristics.</p>
     *
     * <p>An estimator is considered to have converged whenever either
     * the criterion goes below a physical threshold under which
     * improvements are considered useless or when the algorithm is
     * unable to improve it (even if it is still high). The first
     * condition that is met stops the iterations.</p>
     *
     * <p>The fact an estimator has converged does not mean that the
     * model accurately fits the measurements. It only means no better
     * solution can be found, it does not mean this one is good. Such an
     * analysis is left to the caller.</p>
     *
     * <p>If neither conditions are fulfilled before a given number of
     * iterations, the algorithm is considered to have failed and an
     * {@link EstimationException} is thrown.</p>
     *
     * @param maxCostEval maximal number of cost evaluations allowed
     * @param convergence criterion threshold below which we do not need
     * to improve the criterion anymore
     * @param steadyStateThreshold steady state detection threshold, the
     * problem has converged has reached a steady state if
     * <code>FastMath.abs(J<sub>n</sub> - J<sub>n-1</sub>) &lt;
     * J<sub>n</sub> &times convergence</code>, where <code>J<sub>n</sub></code>
     * and <code>J<sub>n-1</sub></code> are the current and preceding criterion
     * values (square sum of the weighted residuals of considered measurements).
     */
    public GaussNewtonEstimator(final int maxCostEval, final double convergence,
                                final double steadyStateThreshold) {
        setMaxCostEval(maxCostEval);
        this.steadyStateThreshold = steadyStateThreshold;
        this.convergence          = convergence;
    }

    /**
     * Set the convergence criterion threshold.
     * @param convergence criterion threshold below which we do not need
     * to improve the criterion anymore
     */
    public void setConvergence(final double convergence) {
        this.convergence = convergence;
    }

    /**
     * Set the steady state detection threshold.
     * <p>
     * The problem has converged has reached a steady state if
     * <code>FastMath.abs(J<sub>n</sub> - J<sub>n-1</sub>) &lt;
     * J<sub>n</sub> &times convergence</code>, where <code>J<sub>n</sub></code>
     * and <code>J<sub>n-1</sub></code> are the current and preceding criterion
     * values (square sum of the weighted residuals of considered measurements).
     * </p>
     * @param steadyStateThreshold steady state detection threshold
     */
    public void setSteadyStateThreshold(final double steadyStateThreshold) {
        this.steadyStateThreshold = steadyStateThreshold;
    }

    /**
     * Solve an estimation problem using a least squares criterion.
     *
     * <p>This method set the unbound parameters of the given problem
     * starting from their current values through several iterations. At
     * each step, the unbound parameters are changed in order to
     * minimize a weighted least square criterion based on the
     * measurements of the problem.</p>
     *
     * <p>The iterations are stopped either when the criterion goes
     * below a physical threshold under which improvement are considered
     * useless or when the algorithm is unable to improve it (even if it
     * is still high). The first condition that is met stops the
     * iterations. If the convergence it not reached before the maximum
     * number of iterations, an {@link EstimationException} is
     * thrown.</p>
     *
     * @param problem estimation problem to solve
     * @exception EstimationException if the problem cannot be solved
     *
     * @see EstimationProblem
     *
     */
    @Override
    public void estimate(EstimationProblem problem)
    throws EstimationException {

        initializeEstimate(problem);

        // work matrices
        double[] grad             = new double[parameters.length];
        ArrayRealVector bDecrement = new ArrayRealVector(parameters.length);
        double[] bDecrementData   = bDecrement.getDataRef();
        RealMatrix wGradGradT     = MatrixUtils.createRealMatrix(parameters.length, parameters.length);

        // iterate until convergence is reached
        double previous = Double.POSITIVE_INFINITY;
        do {

            // build the linear problem
            incrementJacobianEvaluationsCounter();
            RealVector b = new ArrayRealVector(parameters.length);
            RealMatrix a = MatrixUtils.createRealMatrix(parameters.length, parameters.length);
            for (int i = 0; i < measurements.length; ++i) {
                if (! measurements [i].isIgnored()) {

                    double weight   = measurements[i].getWeight();
                    double residual = measurements[i].getResidual();

                    // compute the normal equation
                    for (int j = 0; j < parameters.length; ++j) {
                        grad[j] = measurements[i].getPartial(parameters[j]);
                        bDecrementData[j] = weight * residual * grad[j];
                    }

                    // build the contribution matrix for measurement i
                    for (int k = 0; k < parameters.length; ++k) {
                        double gk = grad[k];
                        for (int l = 0; l < parameters.length; ++l) {
                            wGradGradT.setEntry(k, l, weight * gk * grad[l]);
                        }
                    }

                    // update the matrices
                    a = a.add(wGradGradT);
                    b = b.add(bDecrement);

                }
            }

            try {

                // solve the linearized least squares problem
                RealVector dX = new LUDecompositionImpl(a).getSolver().solve(b);

                // update the estimated parameters
                for (int i = 0; i < parameters.length; ++i) {
                    parameters[i].setEstimate(parameters[i].getEstimate() + dX.getEntry(i));
                }

            } catch(InvalidMatrixException e) {
                throw new EstimationException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM);
            }


            previous = cost;
            updateResidualsAndCost();

        } while ((getCostEvaluations() < 2) ||
                 (FastMath.abs(previous - cost) > (cost * steadyStateThreshold) &&
                  (FastMath.abs(cost) > convergence)));

    }

}

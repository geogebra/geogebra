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

/**
 * This interface represents solvers for estimation problems.
 *
 * <p>The classes which are devoted to solve estimation problems
 * should implement this interface. The problems which can be handled
 * should implement the {@link EstimationProblem} interface which
 * gather all the information needed by the solver.</p>
 *
 * <p>The interface is composed only of the {@link #estimate estimate}
 * method.</p>
 *
 * @see EstimationProblem
 *
 * @version $Revision: 811786 $ $Date: 2009-09-06 11:36:08 +0200 (dim. 06 sept. 2009) $
 * @since 1.2
 * @deprecated as of 2.0, everything in package org.apache.commons.math.estimation has
 * been deprecated and replaced by package org.apache.commons.math.optimization.general
 *
 */
@Deprecated
public interface Estimator {

  /**
   * Solve an estimation problem.
   *
   * <p>The method should set the parameters of the problem to several
   * trial values until it reaches convergence. If this method returns
   * normally (i.e. without throwing an exception), then the best
   * estimate of the parameters can be retrieved from the problem
   * itself, through the {@link EstimationProblem#getAllParameters
   * EstimationProblem.getAllParameters} method.</p>
   *
   * @param problem estimation problem to solve
   * @exception EstimationException if the problem cannot be solved
   *
   */
  void estimate(EstimationProblem problem) throws EstimationException;

  /**
   * Get the Root Mean Square value.
   * Get the Root Mean Square value, i.e. the root of the arithmetic
   * mean of the square of all weighted residuals. This is related to the
   * criterion that is minimized by the estimator as follows: if
   * <em>c</em> is the criterion, and <em>n</em> is the number of
   * measurements, then the RMS is <em>sqrt (c/n)</em>.
   * @see #guessParametersErrors(EstimationProblem)
   *
   * @param problem estimation problem
   * @return RMS value
   */
  double getRMS(EstimationProblem problem);

  /**
   * Get the covariance matrix of estimated parameters.
   * @param problem estimation problem
   * @return covariance matrix
   * @exception EstimationException if the covariance matrix
   * cannot be computed (singular problem)
   */
  double[][] getCovariances(EstimationProblem problem) throws EstimationException;

  /**
   * Guess the errors in estimated parameters.
   * @see #getRMS(EstimationProblem)
   * @param problem estimation problem
   * @return errors in estimated parameters
     * @exception EstimationException if the error cannot be guessed
   */
  double[] guessParametersErrors(EstimationProblem problem) throws EstimationException;

}

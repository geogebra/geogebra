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

/** This interface specifies how to check if a {@link
 * DifferentiableMultivariateVectorialOptimizer optimization algorithm} has converged.
 *
 * <p>Deciding if convergence has been reached is a problem-dependent issue. The
 * user should provide a class implementing this interface to allow the optimization
 * algorithm to stop its search according to the problem at hand.</p>
 * <p>For convenience, two implementations that fit simple needs are already provided:
 * {@link SimpleVectorialValueChecker} and {@link SimpleVectorialPointChecker}. The first
 * one considers convergence is reached when the objective function value does not
 * change much anymore, it does not use the point set at all. The second one
 * considers convergence is reached when the input point set does not change
 * much anymore, it does not use objective function value at all.</p>
 *
 * @version $Revision: 780674 $ $Date: 2009-06-01 17:10:55 +0200 (lun. 01 juin 2009) $
 * @since 2.0
 */

public interface VectorialConvergenceChecker {

  /** Check if the optimization algorithm has converged considering the last points.
   * <p>
   * This method may be called several time from the same algorithm iteration with
   * different points. This can be detected by checking the iteration number at each
   * call if needed. Each time this method is called, the previous and current point
   * correspond to points with the same role at each iteration, so they can be
   * compared. As an example, simplex-based algorithms call this method for all
   * points of the simplex, not only for the best or worst ones.
   * </p>
   * @param iteration index of current iteration
   * @param previous point from previous iteration
   * @param current point from current iteration
   * @return true if the algorithm is considered to have converged
   */
  boolean converged(int iteration, VectorialPointValuePair previous, VectorialPointValuePair current);

}

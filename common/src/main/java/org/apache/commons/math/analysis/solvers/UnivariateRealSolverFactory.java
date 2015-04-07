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
package org.apache.commons.math.analysis.solvers;

/**
 * Abstract factory class used to create {@link UnivariateRealSolver} instances.
 * <p>
 * Solvers implementing the following algorithms are supported:
 * <ul>
 * <li>Bisection</li>
 * <li>Brent's method</li>
 * <li>Secant method</li>
 * </ul>
 * Concrete factories extending this class also specify a default solver, instances of which
 * are returned by <code>newDefaultSolver()</code>.</p>
 * <p>
 * Common usage:<pre>
 * SolverFactory factory = UnivariateRealSolverFactory.newInstance();</p>
 *
 * // create a Brent solver to use
 * BrentSolver solver = factory.newBrentSolver();
 * </pre>
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public abstract class UnivariateRealSolverFactory {
    /**
     * Default constructor.
     */
    protected UnivariateRealSolverFactory() {
    }

    /**
     * Create a new factory.
     * @return a new factory.
     */
    public static UnivariateRealSolverFactory newInstance() {
        return new UnivariateRealSolverFactoryImpl();
    }

    /**
     * Create a new {@link UnivariateRealSolver}.  The
     * actual solver returned is determined by the underlying factory.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newDefaultSolver();

    /**
     * Create a new {@link UnivariateRealSolver}.  The
     * solver is an implementation of the bisection method.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newBisectionSolver();

    /**
     * Create a new {@link UnivariateRealSolver}.  The
     * solver is an implementation of the Brent method.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newBrentSolver();

    /**
     * Create a new {@link UnivariateRealSolver}.  The
     * solver is an implementation of Newton's Method.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newNewtonSolver();

    /**
     * Create a new {@link UnivariateRealSolver}.  The
     * solver is an implementation of the secant method.
     * @return the new solver.
     */
    public abstract UnivariateRealSolver newSecantSolver();

}

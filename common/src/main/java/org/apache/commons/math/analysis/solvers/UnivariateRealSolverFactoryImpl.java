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
 * A concrete {@link  UnivariateRealSolverFactory}.  This is the default solver factory
 * used by commons-math.
 * <p>
 * The default solver returned by this factory is a {@link BrentSolver}.</p>
 *
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 */
public class UnivariateRealSolverFactoryImpl extends UnivariateRealSolverFactory {

    /**
     * Default constructor.
     */
    public UnivariateRealSolverFactoryImpl() {
    }

    /** {@inheritDoc} */
    @Override
    public UnivariateRealSolver newDefaultSolver() {
        return newBrentSolver();
    }

    /** {@inheritDoc} */
    @Override
    public UnivariateRealSolver newBisectionSolver() {
        return new BisectionSolver();
    }

    /** {@inheritDoc} */
    @Override
    public UnivariateRealSolver newBrentSolver() {
        return new BrentSolver();
    }

    /** {@inheritDoc} */
    @Override
    public UnivariateRealSolver newNewtonSolver() {
        return new NewtonSolver();
    }

    /** {@inheritDoc} */
    @Override
    public UnivariateRealSolver newSecantSolver() {
        return new SecantSolver();
    }
}

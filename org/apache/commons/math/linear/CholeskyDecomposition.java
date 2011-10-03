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

package org.apache.commons.math.linear;


/**
 * An interface to classes that implement an algorithm to calculate the
 * Cholesky decomposition of a real symmetric positive-definite matrix.
 * <p>This interface is based on the class with similar name from the
 * <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library, with the
 * following changes:</p>
 * <ul>
 *   <li>a {@link #getLT() getLT} method has been added,</li>
 *   <li>the <code>isspd</code> method has been removed, the constructors of
 *   implementation classes being expected to throw {@link
 *   NotPositiveDefiniteMatrixException} when a matrix cannot be decomposed,</li>
 *   <li>a {@link #getDeterminant() getDeterminant} method has been added,</li>
 *   <li>the <code>solve</code> method has been replaced by a {@link
 *   #getSolver() getSolver} method and the equivalent method provided by
 *   the returned {@link DecompositionSolver}.</li>
 * </ul>
 *
 * @see <a href="http://mathworld.wolfram.com/CholeskyDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/Cholesky_decomposition">Wikipedia</a>
 * @version $Revision: 826627 $ $Date: 2009-10-19 12:27:47 +0200 (lun. 19 oct. 2009) $
 * @since 2.0
 */
public interface CholeskyDecomposition {

    /**
     * Returns the matrix L of the decomposition.
     * <p>L is an lower-triangular matrix</p>
     * @return the L matrix
     */
    RealMatrix getL();

    /**
     * Returns the transpose of the matrix L of the decomposition.
     * <p>L<sup>T</sup> is an upper-triangular matrix</p>
     * @return the transpose of the matrix L of the decomposition
     */
    RealMatrix getLT();

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    double getDeterminant();

    /**
     * Get a solver for finding the A &times; X = B solution in least square sense.
     * @return a solver
     */
    DecompositionSolver getSolver();

}

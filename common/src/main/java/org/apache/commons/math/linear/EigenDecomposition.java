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
 * eigen decomposition of a real matrix.
 * <p>The eigen decomposition of matrix A is a set of two matrices:
 * V and D such that A = V &times; D &times; V<sup>T</sup>.
 * A, V and D are all m &times; m matrices.</p>
 * <p>This interface is similar in spirit to the <code>EigenvalueDecomposition</code>
 * class from the <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a>
 * library, with the following changes:</p>
 * <ul>
 *   <li>a {@link #getVT() getVt} method has been added,</li>
 *   <li>two {@link #getRealEigenvalue(int) getRealEigenvalue} and {@link #getImagEigenvalue(int)
 *   getImagEigenvalue} methods to pick up a single eigenvalue have been added,</li>
 *   <li>a {@link #getEigenvector(int) getEigenvector} method to pick up a single
 *   eigenvector has been added,</li>
 *   <li>a {@link #getDeterminant() getDeterminant} method has been added.</li>
 *   <li>a {@link #getSolver() getSolver} method has been added.</li>
 * </ul>
 * @see <a href="http://mathworld.wolfram.com/EigenDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix">Wikipedia</a>
 * @version $Revision: 997726 $ $Date: 2010-09-16 14:39:51 +0200 (jeu. 16 sept. 2010) $
 * @since 2.0
 */
public interface EigenDecomposition {

    /**
     * Returns the matrix V of the decomposition.
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * <p>The columns of V are the eigenvectors of the original matrix.</p>
     * <p>No assumption is made about the orientation of the system axes formed
     * by the columns of V (e.g. in a 3-dimension space, V can form a left-
     * or right-handed system).</p>
     * @return the V matrix
     */
    RealMatrix getV();

    /**
     * Returns the block diagonal matrix D of the decomposition.
     * <p>D is a block diagonal matrix.</p>
     * <p>Real eigenvalues are on the diagonal while complex values are on
     * 2x2 blocks { {real +imaginary}, {-imaginary, real} }.</p>
     * @return the D matrix
     * @see #getRealEigenvalues()
     * @see #getImagEigenvalues()
     */
    RealMatrix getD();

    /**
     * Returns the transpose of the matrix V of the decomposition.
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * <p>The columns of V are the eigenvectors of the original matrix.</p>
     * <p>No assumption is made about the orientation of the system axes formed
     * by the columns of V (e.g. in a 3-dimension space, V can form a left-
     * or right-handed system).</p>
     * @return the transpose of the V matrix
     */
    RealMatrix getVT();

    /**
     * Returns a copy of the real parts of the eigenvalues of the original matrix.
     * @return a copy of the real parts of the eigenvalues of the original matrix
     * @see #getD()
     * @see #getRealEigenvalue(int)
     * @see #getImagEigenvalues()
     */
    double[] getRealEigenvalues();

    /**
     * Returns the real part of the i<sup>th</sup> eigenvalue of the original matrix.
     * @param i index of the eigenvalue (counting from 0)
     * @return real part of the i<sup>th</sup> eigenvalue of the original matrix
     * @see #getD()
     * @see #getRealEigenvalues()
     * @see #getImagEigenvalue(int)
     */
    double getRealEigenvalue(int i);

    /**
     * Returns a copy of the imaginary parts of the eigenvalues of the original matrix.
     * @return a copy of the imaginary parts of the eigenvalues of the original matrix
     * @see #getD()
     * @see #getImagEigenvalue(int)
     * @see #getRealEigenvalues()
     */
    double[] getImagEigenvalues();

    /**
     * Returns the imaginary part of the i<sup>th</sup> eigenvalue of the original matrix.
     * @param i index of the eigenvalue (counting from 0)
     * @return imaginary part of the i<sup>th</sup> eigenvalue of the original matrix
     * @see #getD()
     * @see #getImagEigenvalues()
     * @see #getRealEigenvalue(int)
     */
    double getImagEigenvalue(int i);

    /**
     * Returns a copy of the i<sup>th</sup> eigenvector of the original matrix.
     * @param i index of the eigenvector (counting from 0)
     * @return copy of the i<sup>th</sup> eigenvector of the original matrix
     * @see #getD()
     */
    RealVector getEigenvector(int i);

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    double getDeterminant();

    /**
     * Get a solver for finding the A &times; X = B solution in exact linear sense.
     * @return a solver
     */
    DecompositionSolver getSolver();

}

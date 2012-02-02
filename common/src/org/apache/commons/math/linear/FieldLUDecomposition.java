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

import org.apache.commons.math.FieldElement;

/**
 * An interface to classes that implement an algorithm to calculate the
 * LU-decomposition of a real matrix.
 * <p>The LU-decomposition of matrix A is a set of three matrices: P, L and U
 * such that P&times;A = L&times;U. P is a rows permutation matrix that is used
 * to rearrange the rows of A before so that it can be decomposed. L is a lower
 * triangular matrix with unit diagonal terms and U is an upper triangular matrix.</p>
 * <p>This interface is based on the class with similar name from the
 * <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library.</p>
 * <ul>
 *   <li>a {@link #getP() getP} method has been added,</li>
 *   <li>the <code>det</code> method has been renamed as {@link #getDeterminant()
 *   getDeterminant},</li>
 *   <li>the <code>getDoublePivot</code> method has been removed (but the int based
 *   {@link #getPivot() getPivot} method has been kept),</li>
 *   <li>the <code>solve</code> and <code>isNonSingular</code> methods have been replaced
 *   by a {@link #getSolver() getSolver} method and the equivalent methods provided by
 *   the returned {@link DecompositionSolver}.</li>
 * </ul>
 *
 * @param <T> the type of the field elements
 * @see <a href="http://mathworld.wolfram.com/LUDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/LU_decomposition">Wikipedia</a>
 * @version $Revision: 826627 $ $Date: 2009-10-19 12:27:47 +0200 (lun. 19 oct. 2009) $
 * @since 2.0
 */
public interface FieldLUDecomposition<T extends FieldElement<T>> {

    /**
     * Returns the matrix L of the decomposition.
     * <p>L is an lower-triangular matrix</p>
     * @return the L matrix (or null if decomposed matrix is singular)
     */
    FieldMatrix<T> getL();

    /**
     * Returns the matrix U of the decomposition.
     * <p>U is an upper-triangular matrix</p>
     * @return the U matrix (or null if decomposed matrix is singular)
     */
    FieldMatrix<T> getU();

    /**
     * Returns the P rows permutation matrix.
     * <p>P is a sparse matrix with exactly one element set to 1.0 in
     * each row and each column, all other elements being set to 0.0.</p>
     * <p>The positions of the 1 elements are given by the {@link #getPivot()
     * pivot permutation vector}.</p>
     * @return the P rows permutation matrix (or null if decomposed matrix is singular)
     * @see #getPivot()
     */
    FieldMatrix<T> getP();

    /**
     * Returns the pivot permutation vector.
     * @return the pivot permutation vector
     * @see #getP()
     */
    int[] getPivot();

    /**
     * Return the determinant of the matrix
     * @return determinant of the matrix
     */
    T getDeterminant();

    /**
     * Get a solver for finding the A &times; X = B solution in exact linear sense.
     * @return a solver
     */
    FieldDecompositionSolver<T> getSolver();

}

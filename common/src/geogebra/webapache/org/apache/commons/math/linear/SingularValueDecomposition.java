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
 * Singular Value Decomposition of a real matrix.
 * <p>
 * The Singular Value Decomposition of matrix A is a set of three matrices: U,
 * &Sigma; and V such that A = U &times; &Sigma; &times; V<sup>T</sup>. Let A be
 * a m &times; n matrix, then U is a m &times; p orthogonal matrix, &Sigma; is a
 * p &times; p diagonal matrix with positive or null elements, V is a p &times;
 * n orthogonal matrix (hence V<sup>T</sup> is also orthogonal) where
 * p=min(m,n).
 * </p>
 * <p>This interface is similar to the class with similar name from the
 * <a href="http://math.nist.gov/javanumerics/jama/">JAMA</a> library, with the
 * following changes:</p>
 * <ul>
 *   <li>the <code>norm2</code> method which has been renamed as {@link #getNorm()
 *   getNorm},</li>
 *   <li>the <code>cond</code> method which has been renamed as {@link
 *   #getConditionNumber() getConditionNumber},</li>
 *   <li>the <code>rank</code> method which has been renamed as {@link #getRank()
 *   getRank},</li>
 *   <li>a {@link #getUT() getUT} method has been added,</li>
 *   <li>a {@link #getVT() getVT} method has been added,</li>
 *   <li>a {@link #getSolver() getSolver} method has been added,</li>
 *   <li>a {@link #getCovariance(double) getCovariance} method has been added.</li>
 * </ul>
 * @see <a href="http://mathworld.wolfram.com/SingularValueDecomposition.html">MathWorld</a>
 * @see <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">Wikipedia</a>
 * @version $Revision: 928081 $ $Date: 2010-03-26 23:36:38 +0100 (ven. 26 mars 2010) $
 * @since 2.0
 */
public interface SingularValueDecomposition {

    /**
     * Returns the matrix U of the decomposition.
     * <p>U is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the U matrix
     * @see #getUT()
     */
    RealMatrix getU();

    /**
     * Returns the transpose of the matrix U of the decomposition.
     * <p>U is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the U matrix (or null if decomposed matrix is singular)
     * @see #getU()
     */
    RealMatrix getUT();

    /**
     * Returns the diagonal matrix &Sigma; of the decomposition.
     * <p>&Sigma; is a diagonal matrix. The singular values are provided in
     * non-increasing order, for compatibility with Jama.</p>
     * @return the &Sigma; matrix
     */
    RealMatrix getS();

    /**
     * Returns the diagonal elements of the matrix &Sigma; of the decomposition.
     * <p>The singular values are provided in non-increasing order, for
     * compatibility with Jama.</p>
     * @return the diagonal elements of the &Sigma; matrix
     */
    double[] getSingularValues();

    /**
     * Returns the matrix V of the decomposition.
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the V matrix (or null if decomposed matrix is singular)
     * @see #getVT()
     */
    RealMatrix getV();

    /**
     * Returns the transpose of the matrix V of the decomposition.
     * <p>V is an orthogonal matrix, i.e. its transpose is also its inverse.</p>
     * @return the V matrix (or null if decomposed matrix is singular)
     * @see #getV()
     */
    RealMatrix getVT();

    /**
     * Returns the n &times; n covariance matrix.
     * <p>The covariance matrix is V &times; J &times; V<sup>T</sup>
     * where J is the diagonal matrix of the inverse of the squares of
     * the singular values.</p>
     * @param minSingularValue value below which singular values are ignored
     * (a 0 or negative value implies all singular value will be used)
     * @return covariance matrix
     * @exception IllegalArgumentException if minSingularValue is larger than
     * the largest singular value, meaning all singular values are ignored
     */
    RealMatrix getCovariance(double minSingularValue) throws IllegalArgumentException;

    /**
     * Returns the L<sub>2</sub> norm of the matrix.
     * <p>The L<sub>2</sub> norm is max(|A &times; u|<sub>2</sub> /
     * |u|<sub>2</sub>), where |.|<sub>2</sub> denotes the vectorial 2-norm
     * (i.e. the traditional euclidian norm).</p>
     * @return norm
     */
    double getNorm();

    /**
     * Return the condition number of the matrix.
     * @return condition number of the matrix
     */
    double getConditionNumber();

    /**
     * Return the effective numerical matrix rank.
     * <p>The effective numerical rank is the number of non-negligible
     * singular values. The threshold used to identify non-negligible
     * terms is max(m,n) &times; ulp(s<sub>1</sub>) where ulp(s<sub>1</sub>)
     * is the least significant bit of the largest singular value.</p>
     * @return effective numerical matrix rank
     */
    int getRank();

    /**
     * Get a solver for finding the A &times; X = B solution in least square sense.
     * @return a solver
     */
    DecompositionSolver getSolver();

}

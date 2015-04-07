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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Calculates the LUP-decomposition of a square matrix.
 * <p>The LUP-decomposition of a matrix A consists of three matrices
 * L, U and P that satisfy: PA = LU, L is lower triangular, and U is
 * upper triangular and P is a permutation matrix. All matrices are
 * m&times;m.</p>
 * <p>As shown by the presence of the P matrix, this decomposition is
 * implemented using partial pivoting.</p>
 *
 * @version $Revision: 990655 $ $Date: 2010-08-29 23:49:40 +0200 (dim. 29 août 2010) $
 * @since 2.0
 */
public class LUDecompositionImpl implements LUDecomposition {

    /** Default bound to determine effective singularity in LU decomposition */
    private static final double DEFAULT_TOO_SMALL = 10E-12;

    /** Entries of LU decomposition. */
    private double lu[][];

    /** Pivot permutation associated with LU decomposition */
    private int[] pivot;

    /** Parity of the permutation associated with the LU decomposition */
    private boolean even;

    /** Singularity indicator. */
    private boolean singular;

    /** Cached value of L. */
    private RealMatrix cachedL;

    /** Cached value of U. */
    private RealMatrix cachedU;

    /** Cached value of P. */
    private RealMatrix cachedP;

    /**
     * Calculates the LU-decomposition of the given matrix.
     * @param matrix The matrix to decompose.
     * @exception InvalidMatrixException if matrix is not square
     */
    public LUDecompositionImpl(RealMatrix matrix)
        throws InvalidMatrixException {
        this(matrix, DEFAULT_TOO_SMALL);
    }

    /**
     * Calculates the LU-decomposition of the given matrix.
     * @param matrix The matrix to decompose.
     * @param singularityThreshold threshold (based on partial row norm)
     * under which a matrix is considered singular
     * @exception NonSquareMatrixException if matrix is not square
     */
    public LUDecompositionImpl(RealMatrix matrix, double singularityThreshold)
        throws NonSquareMatrixException {

        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }

        final int m = matrix.getColumnDimension();
        lu = matrix.getData();
        pivot = new int[m];
        cachedL = null;
        cachedU = null;
        cachedP = null;

        // Initialize permutation array and parity
        for (int row = 0; row < m; row++) {
            pivot[row] = row;
        }
        even     = true;
        singular = false;

        // Loop over columns
        for (int col = 0; col < m; col++) {

            double sum = 0;

            // upper
            for (int row = 0; row < col; row++) {
                final double[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < row; i++) {
                    sum -= luRow[i] * lu[i][col];
                }
                luRow[col] = sum;
            }

            // lower
            int max = col; // permutation row
            double largest = Double.NEGATIVE_INFINITY;
            for (int row = col; row < m; row++) {
                final double[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < col; i++) {
                    sum -= luRow[i] * lu[i][col];
                }
                luRow[col] = sum;

                // maintain best permutation choice
                if (FastMath.abs(sum) > largest) {
                    largest = FastMath.abs(sum);
                    max = row;
                }
            }

            // Singularity check
            if (FastMath.abs(lu[max][col]) < singularityThreshold) {
                singular = true;
                return;
            }

            // Pivot if necessary
            if (max != col) {
                double tmp = 0;
                final double[] luMax = lu[max];
                final double[] luCol = lu[col];
                for (int i = 0; i < m; i++) {
                    tmp = luMax[i];
                    luMax[i] = luCol[i];
                    luCol[i] = tmp;
                }
                int temp = pivot[max];
                pivot[max] = pivot[col];
                pivot[col] = temp;
                even = !even;
            }

            // Divide the lower elements by the "winning" diagonal elt.
            final double luDiag = lu[col][col];
            for (int row = col + 1; row < m; row++) {
                lu[row][col] /= luDiag;
            }
        }

    }

    /** {@inheritDoc} */
    public RealMatrix getL() {
        if ((cachedL == null) && !singular) {
            final int m = pivot.length;
            cachedL = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                final double[] luI = lu[i];
                for (int j = 0; j < i; ++j) {
                    cachedL.setEntry(i, j, luI[j]);
                }
                cachedL.setEntry(i, i, 1.0);
            }
        }
        return cachedL;
    }

    /** {@inheritDoc} */
    public RealMatrix getU() {
        if ((cachedU == null) && !singular) {
            final int m = pivot.length;
            cachedU = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                final double[] luI = lu[i];
                for (int j = i; j < m; ++j) {
                    cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return cachedU;
    }

    /** {@inheritDoc} */
    public RealMatrix getP() {
        if ((cachedP == null) && !singular) {
            final int m = pivot.length;
            cachedP = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < m; ++i) {
                cachedP.setEntry(i, pivot[i], 1.0);
            }
        }
        return cachedP;
    }

    /** {@inheritDoc} */
    public int[] getPivot() {
        return pivot.clone();
    }

    /** {@inheritDoc} */
    public double getDeterminant() {
        if (singular) {
            return 0;
        } else {
            final int m = pivot.length;
            double determinant = even ? 1 : -1;
            for (int i = 0; i < m; i++) {
                determinant *= lu[i][i];
            }
            return determinant;
        }
    }

    /** {@inheritDoc} */
    public DecompositionSolver getSolver() {
        return new Solver(lu, pivot, singular);
    }

    /** Specialized solver. */
    private static class Solver implements DecompositionSolver {

        /** Entries of LU decomposition. */
        private final double lu[][];

        /** Pivot permutation associated with LU decomposition. */
        private final int[] pivot;

        /** Singularity indicator. */
        private final boolean singular;

        /**
         * Build a solver from decomposed matrix.
         * @param lu entries of LU decomposition
         * @param pivot pivot permutation associated with LU decomposition
         * @param singular singularity indicator
         */
        private Solver(final double[][] lu, final int[] pivot, final boolean singular) {
            this.lu       = lu;
            this.pivot    = pivot;
            this.singular = singular;
        }

        /** {@inheritDoc} */
        public boolean isNonSingular() {
            return !singular;
        }

        /** {@inheritDoc} */
        public double[] solve(double[] b)
            throws IllegalArgumentException, InvalidMatrixException {

            final int m = pivot.length;
            if (b.length != m) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH, b.length, m);
            }
            if (singular) {
                throw new SingularMatrixException();
            }

            final double[] bp = new double[m];

            // Apply permutations to b
            for (int row = 0; row < m; row++) {
                bp[row] = b[pivot[row]];
            }

            // Solve LY = b
            for (int col = 0; col < m; col++) {
                final double bpCol = bp[col];
                for (int i = col + 1; i < m; i++) {
                    bp[i] -= bpCol * lu[i][col];
                }
            }

            // Solve UX = Y
            for (int col = m - 1; col >= 0; col--) {
                bp[col] /= lu[col][col];
                final double bpCol = bp[col];
                for (int i = 0; i < col; i++) {
                    bp[i] -= bpCol * lu[i][col];
                }
            }

            return bp;

        }

        /** {@inheritDoc} */
        public RealVector solve(RealVector b)
            throws IllegalArgumentException, InvalidMatrixException {
            try {
                return solve((ArrayRealVector) b);
            } catch (ClassCastException cce) {

                final int m = pivot.length;
                if (b.getDimension() != m) {
                    throw MathRuntimeException.createIllegalArgumentException(
                            LocalizedFormats.VECTOR_LENGTH_MISMATCH, b.getDimension(), m);
                }
                if (singular) {
                    throw new SingularMatrixException();
                }

                final double[] bp = new double[m];

                // Apply permutations to b
                for (int row = 0; row < m; row++) {
                    bp[row] = b.getEntry(pivot[row]);
                }

                // Solve LY = b
                for (int col = 0; col < m; col++) {
                    final double bpCol = bp[col];
                    for (int i = col + 1; i < m; i++) {
                        bp[i] -= bpCol * lu[i][col];
                    }
                }

                // Solve UX = Y
                for (int col = m - 1; col >= 0; col--) {
                    bp[col] /= lu[col][col];
                    final double bpCol = bp[col];
                    for (int i = 0; i < col; i++) {
                        bp[i] -= bpCol * lu[i][col];
                    }
                }

                return new ArrayRealVector(bp, false);

            }
        }

        /** Solve the linear equation A &times; X = B.
         * <p>The A matrix is implicit here. It is </p>
         * @param b right-hand side of the equation A &times; X = B
         * @return a vector X such that A &times; X = B
         * @exception IllegalArgumentException if matrices dimensions don't match
         * @exception InvalidMatrixException if decomposed matrix is singular
         */
        public ArrayRealVector solve(ArrayRealVector b)
            throws IllegalArgumentException, InvalidMatrixException {
            return new ArrayRealVector(solve(b.getDataRef()), false);
        }

        /** {@inheritDoc} */
        public RealMatrix solve(RealMatrix b)
            throws IllegalArgumentException, InvalidMatrixException {

            final int m = pivot.length;
            if (b.getRowDimension() != m) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                        b.getRowDimension(), b.getColumnDimension(), m, "n");
            }
            if (singular) {
                throw new SingularMatrixException();
            }

            final int nColB = b.getColumnDimension();

            // Apply permutations to b
            final double[][] bp = new double[m][nColB];
            for (int row = 0; row < m; row++) {
                final double[] bpRow = bp[row];
                final int pRow = pivot[row];
                for (int col = 0; col < nColB; col++) {
                    bpRow[col] = b.getEntry(pRow, col);
                }
            }

            // Solve LY = b
            for (int col = 0; col < m; col++) {
                final double[] bpCol = bp[col];
                for (int i = col + 1; i < m; i++) {
                    final double[] bpI = bp[i];
                    final double luICol = lu[i][col];
                    for (int j = 0; j < nColB; j++) {
                        bpI[j] -= bpCol[j] * luICol;
                    }
                }
            }

            // Solve UX = Y
            for (int col = m - 1; col >= 0; col--) {
                final double[] bpCol = bp[col];
                final double luDiag = lu[col][col];
                for (int j = 0; j < nColB; j++) {
                    bpCol[j] /= luDiag;
                }
                for (int i = 0; i < col; i++) {
                    final double[] bpI = bp[i];
                    final double luICol = lu[i][col];
                    for (int j = 0; j < nColB; j++) {
                        bpI[j] -= bpCol[j] * luICol;
                    }
                }
            }

            return new Array2DRowRealMatrix(bp, false);

        }

        /** {@inheritDoc} */
        public RealMatrix getInverse() throws InvalidMatrixException {
            return solve(MatrixUtils.createRealIdentityMatrix(pivot.length));
        }

    }

}

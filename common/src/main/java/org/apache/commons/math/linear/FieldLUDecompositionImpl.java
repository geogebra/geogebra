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

import java.lang.reflect.Array;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Calculates the LUP-decomposition of a square matrix.
 * <p>The LUP-decomposition of a matrix A consists of three matrices
 * L, U and P that satisfy: PA = LU, L is lower triangular, and U is
 * upper triangular and P is a permutation matrix. All matrices are
 * m&times;m.</p>
 * <p>Since {@link FieldElement field elements} do not provide an ordering
 * operator, the permutation matrix is computed here only in order to avoid
 * a zero pivot element, no attempt is done to get the largest pivot element.</p>
 *
 * @param <T> the type of the field elements
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 * @since 2.0
 */
public class FieldLUDecompositionImpl<T extends FieldElement<T>> implements FieldLUDecomposition<T> {

    /** Field to which the elements belong. */
    private final Field<T> field;

    /** Entries of LU decomposition. */
    private T lu[][];

    /** Pivot permutation associated with LU decomposition */
    private int[] pivot;

    /** Parity of the permutation associated with the LU decomposition */
    private boolean even;

    /** Singularity indicator. */
    private boolean singular;

    /** Cached value of L. */
    private FieldMatrix<T> cachedL;

    /** Cached value of U. */
    private FieldMatrix<T> cachedU;

    /** Cached value of P. */
    private FieldMatrix<T> cachedP;

    /**
     * Calculates the LU-decomposition of the given matrix.
     * @param matrix The matrix to decompose.
     * @exception NonSquareMatrixException if matrix is not square
     */
    public FieldLUDecompositionImpl(FieldMatrix<T> matrix)
        throws NonSquareMatrixException {

        if (!matrix.isSquare()) {
            throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
        }

        final int m = matrix.getColumnDimension();
        field = matrix.getField();
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

            T sum = field.getZero();

            // upper
            for (int row = 0; row < col; row++) {
                final T[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < row; i++) {
                    sum = sum.subtract(luRow[i].multiply(lu[i][col]));
                }
                luRow[col] = sum;
            }

            // lower
            int nonZero = col; // permutation row
            for (int row = col; row < m; row++) {
                final T[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < col; i++) {
                    sum = sum.subtract(luRow[i].multiply(lu[i][col]));
                }
                luRow[col] = sum;

                if (lu[nonZero][col].equals(field.getZero())) {
                    // try to select a better permutation choice
                    ++nonZero;
                }
            }

            // Singularity check
            if (nonZero >= m) {
                singular = true;
                return;
            }

            // Pivot if necessary
            if (nonZero != col) {
                T tmp = field.getZero();
                for (int i = 0; i < m; i++) {
                    tmp = lu[nonZero][i];
                    lu[nonZero][i] = lu[col][i];
                    lu[col][i] = tmp;
                }
                int temp = pivot[nonZero];
                pivot[nonZero] = pivot[col];
                pivot[col] = temp;
                even = !even;
            }

            // Divide the lower elements by the "winning" diagonal elt.
            final T luDiag = lu[col][col];
            for (int row = col + 1; row < m; row++) {
                final T[] luRow = lu[row];
                luRow[col] = luRow[col].divide(luDiag);
            }
        }

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getL() {
        if ((cachedL == null) && !singular) {
            final int m = pivot.length;
            cachedL = new Array2DRowFieldMatrix<T>(field, m, m);
            for (int i = 0; i < m; ++i) {
                final T[] luI = lu[i];
                for (int j = 0; j < i; ++j) {
                    cachedL.setEntry(i, j, luI[j]);
                }
                cachedL.setEntry(i, i, field.getOne());
            }
        }
        return cachedL;
    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getU() {
        if ((cachedU == null) && !singular) {
            final int m = pivot.length;
            cachedU = new Array2DRowFieldMatrix<T>(field, m, m);
            for (int i = 0; i < m; ++i) {
                final T[] luI = lu[i];
                for (int j = i; j < m; ++j) {
                    cachedU.setEntry(i, j, luI[j]);
                }
            }
        }
        return cachedU;
    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getP() {
        if ((cachedP == null) && !singular) {
            final int m = pivot.length;
            cachedP = new Array2DRowFieldMatrix<T>(field, m, m);
            for (int i = 0; i < m; ++i) {
                cachedP.setEntry(i, pivot[i], field.getOne());
            }
        }
        return cachedP;
    }

    /** {@inheritDoc} */
    public int[] getPivot() {
        return pivot.clone();
    }

    /** {@inheritDoc} */
    public T getDeterminant() {
        if (singular) {
            return field.getZero();
        } else {
            final int m = pivot.length;
            T determinant = even ? field.getOne() : field.getZero().subtract(field.getOne());
            for (int i = 0; i < m; i++) {
                determinant = determinant.multiply(lu[i][i]);
            }
            return determinant;
        }
    }

    /** {@inheritDoc} */
    public FieldDecompositionSolver<T> getSolver() {
        return new Solver<T>(field, lu, pivot, singular);
    }

    /** Specialized solver. */
    private static class Solver<T extends FieldElement<T>> implements FieldDecompositionSolver<T> {

        /** Serializable version identifier. */
        private static final long serialVersionUID = -6353105415121373022L;

        /** Field to which the elements belong. */
        private final Field<T> field;

        /** Entries of LU decomposition. */
        private final T lu[][];

        /** Pivot permutation associated with LU decomposition. */
        private final int[] pivot;

        /** Singularity indicator. */
        private final boolean singular;

        /**
         * Build a solver from decomposed matrix.
         * @param field field to which the matrix elements belong
         * @param lu entries of LU decomposition
         * @param pivot pivot permutation associated with LU decomposition
         * @param singular singularity indicator
         */
        private Solver(final Field<T> field, final T[][] lu,
                       final int[] pivot, final boolean singular) {
            this.field    = field;
            this.lu       = lu;
            this.pivot    = pivot;
            this.singular = singular;
        }

        /** {@inheritDoc} */
        public boolean isNonSingular() {
            return !singular;
        }

        /** {@inheritDoc} */
        public T[] solve(T[] b)
            throws IllegalArgumentException, InvalidMatrixException {

            final int m = pivot.length;
            if (b.length != m) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                        b.length, m);
            }
            if (singular) {
                throw new SingularMatrixException();
            }

            @SuppressWarnings("unchecked") // field is of type T
            final T[] bp = (T[]) Array.newInstance(field.getZero().getClass(), m);

            // Apply permutations to b
            for (int row = 0; row < m; row++) {
                bp[row] = b[pivot[row]];
            }

            // Solve LY = b
            for (int col = 0; col < m; col++) {
                final T bpCol = bp[col];
                for (int i = col + 1; i < m; i++) {
                    bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
                }
            }

            // Solve UX = Y
            for (int col = m - 1; col >= 0; col--) {
                bp[col] = bp[col].divide(lu[col][col]);
                final T bpCol = bp[col];
                for (int i = 0; i < col; i++) {
                    bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
                }
            }

            return bp;

        }

        /** {@inheritDoc} */
        public FieldVector<T> solve(FieldVector<T> b)
            throws IllegalArgumentException, InvalidMatrixException {
            try {
                return solve((ArrayFieldVector<T>) b);
            } catch (ClassCastException cce) {

                final int m = pivot.length;
                if (b.getDimension() != m) {
                    throw MathRuntimeException.createIllegalArgumentException(
                            LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                            b.getDimension(), m);
                }
                if (singular) {
                    throw new SingularMatrixException();
                }

                @SuppressWarnings("unchecked") // field is of type T
                final T[] bp = (T[]) Array.newInstance(field.getZero().getClass(), m);

                // Apply permutations to b
                for (int row = 0; row < m; row++) {
                    bp[row] = b.getEntry(pivot[row]);
                }

                // Solve LY = b
                for (int col = 0; col < m; col++) {
                    final T bpCol = bp[col];
                    for (int i = col + 1; i < m; i++) {
                        bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
                    }
                }

                // Solve UX = Y
                for (int col = m - 1; col >= 0; col--) {
                    bp[col] = bp[col].divide(lu[col][col]);
                    final T bpCol = bp[col];
                    for (int i = 0; i < col; i++) {
                        bp[i] = bp[i].subtract(bpCol.multiply(lu[i][col]));
                    }
                }

                return new ArrayFieldVector<T>(bp, false);

            }
        }

        /** Solve the linear equation A &times; X = B.
         * <p>The A matrix is implicit here. It is </p>
         * @param b right-hand side of the equation A &times; X = B
         * @return a vector X such that A &times; X = B
         * @exception IllegalArgumentException if matrices dimensions don't match
         * @exception InvalidMatrixException if decomposed matrix is singular
         */
        public ArrayFieldVector<T> solve(ArrayFieldVector<T> b)
            throws IllegalArgumentException, InvalidMatrixException {
            return new ArrayFieldVector<T>(solve(b.getDataRef()), false);
        }

        /** {@inheritDoc} */
        public FieldMatrix<T> solve(FieldMatrix<T> b)
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
            @SuppressWarnings("unchecked") // field is of type T
            final T[][] bp = (T[][]) Array.newInstance(field.getZero().getClass(), new int[] { m, nColB });
            for (int row = 0; row < m; row++) {
                final T[] bpRow = bp[row];
                final int pRow = pivot[row];
                for (int col = 0; col < nColB; col++) {
                    bpRow[col] = b.getEntry(pRow, col);
                }
            }

            // Solve LY = b
            for (int col = 0; col < m; col++) {
                final T[] bpCol = bp[col];
                for (int i = col + 1; i < m; i++) {
                    final T[] bpI = bp[i];
                    final T luICol = lu[i][col];
                    for (int j = 0; j < nColB; j++) {
                        bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
                    }
                }
            }

            // Solve UX = Y
            for (int col = m - 1; col >= 0; col--) {
                final T[] bpCol = bp[col];
                final T luDiag = lu[col][col];
                for (int j = 0; j < nColB; j++) {
                    bpCol[j] = bpCol[j].divide(luDiag);
                }
                for (int i = 0; i < col; i++) {
                    final T[] bpI = bp[i];
                    final T luICol = lu[i][col];
                    for (int j = 0; j < nColB; j++) {
                        bpI[j] = bpI[j].subtract(bpCol[j].multiply(luICol));
                    }
                }
            }

            return new Array2DRowFieldMatrix<T>(bp, false);

        }

        /** {@inheritDoc} */
        public FieldMatrix<T> getInverse() throws InvalidMatrixException {
            final int m = pivot.length;
            final T one = field.getOne();
            FieldMatrix<T> identity = new Array2DRowFieldMatrix<T>(field, m, m);
            for (int i = 0; i < m; ++i) {
                identity.setEntry(i, i, one);
            }
            return solve(identity);
        }

    }

}

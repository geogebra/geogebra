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

/**
 * A collection of static methods that operate on or return matrices.
 *
 * @version $Revision: 983921 $ $Date: 2010-08-10 12:46:06 +0200 (mar. 10 août 2010) $
 */
public class MatrixUtils {

    /**
     * Private constructor.
     */
    private MatrixUtils() {
        super();
    }

    /**
     * Returns a {@link RealMatrix} with specified dimensions.
     * <p>The type of matrix returned depends on the dimension. Below
     * 2<sup>12</sup> elements (i.e. 4096 elements or 64&times;64 for a
     * square matrix) which can be stored in a 32kB array, a {@link
     * Array2DRowRealMatrix} instance is built. Above this threshold a {@link
     * BlockRealMatrix} instance is built.</p>
     * <p>The matrix elements are all set to 0.0.</p>
     * @param rows number of rows of the matrix
     * @param columns number of columns of the matrix
     * @return  RealMatrix with specified dimensions
     * @see #createRealMatrix(double[][])
     */
    public static RealMatrix createRealMatrix(final int rows, final int columns) {
        return (rows * columns <= 4096) ?
                new Array2DRowRealMatrix(rows, columns) : new BlockRealMatrix(rows, columns);
    }

    /**
     * Returns a {@link RealMatrix} whose entries are the the values in the
     * the input array.
     * <p>The type of matrix returned depends on the dimension. Below
     * 2<sup>12</sup> elements (i.e. 4096 elements or 64&times;64 for a
     * square matrix) which can be stored in a 32kB array, a {@link
     * Array2DRowRealMatrix} instance is built. Above this threshold a {@link
     * BlockRealMatrix} instance is built.</p>
     * <p>The input array is copied, not referenced.</p>
     *
     * @param data input array
     * @return  RealMatrix containing the values of the array
     * @throws IllegalArgumentException if <code>data</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if either <code>data</code> or
     * <code>data[0]</code> is null
     * @see #createRealMatrix(int, int)
     */
    public static RealMatrix createRealMatrix(double[][] data) {
        return (data.length * data[0].length <= 4096) ?
                new Array2DRowRealMatrix(data) : new BlockRealMatrix(data);
    }


    /**
     * Returns <code>dimension x dimension</code> identity matrix.
     *
     * @param dimension dimension of identity matrix to generate
     * @return identity matrix
     * @throws IllegalArgumentException if dimension is not positive
     * @since 1.1
     */
    public static RealMatrix createRealIdentityMatrix(int dimension) {
        final RealMatrix m = createRealMatrix(dimension, dimension);
        for (int i = 0; i < dimension; ++i) {
            m.setEntry(i, i, 1.0);
        }
        return m;
    }

    /**
     * Returns a diagonal matrix with specified elements.
     *
     * @param diagonal diagonal elements of the matrix (the array elements
     * will be copied)
     * @return diagonal matrix
     * @since 2.0
     */
    public static RealMatrix createRealDiagonalMatrix(final double[] diagonal) {
        final RealMatrix m = createRealMatrix(diagonal.length, diagonal.length);
        for (int i = 0; i < diagonal.length; ++i) {
            m.setEntry(i, i, diagonal[i]);
        }
        return m;
    }

    /**
     * Creates a {@link RealVector} using the data from the input array.
     *
     * @param data the input data
     * @return a data.length RealVector
     * @throws IllegalArgumentException if <code>data</code> is empty
     * @throws NullPointerException if <code>data</code>is null
     */
    public static RealVector createRealVector(double[] data) {
        return new ArrayRealVector(data, true);
    }

    /**
     * Creates a row {@link RealMatrix} using the data from the input
     * array.
     *
     * @param rowData the input row data
     * @return a 1 x rowData.length RealMatrix
     * @throws IllegalArgumentException if <code>rowData</code> is empty
     * @throws NullPointerException if <code>rowData</code>is null
     */
    public static RealMatrix createRowRealMatrix(double[] rowData) {
        final int nCols = rowData.length;
        final RealMatrix m = createRealMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            m.setEntry(0, i, rowData[i]);
        }
        return m;
    }

    /**
     * Creates a column {@link RealMatrix} using the data from the input
     * array.
     *
     * @param columnData  the input column data
     * @return a columnData x 1 RealMatrix
     * @throws IllegalArgumentException if <code>columnData</code> is empty
     * @throws NullPointerException if <code>columnData</code>is null
     */
    public static RealMatrix createColumnRealMatrix(double[] columnData) {
        final int nRows = columnData.length;
        final RealMatrix m = createRealMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            m.setEntry(i, 0, columnData[i]);
        }
        return m;
    }

    /**
     * Check if a row index is valid.
     * @param m matrix containing the submatrix
     * @param row row index to check
     * @exception MatrixIndexException if index is not valid
     */
    public static void checkRowIndex(final AnyMatrix m, final int row) {
        if (row < 0 || row >= m.getRowDimension()) {
            throw new MatrixIndexException(LocalizedFormats.ROW_INDEX_OUT_OF_RANGE,
                                           row, 0, m.getRowDimension() - 1);
        }
    }

    /**
     * Check if a column index is valid.
     * @param m matrix containing the submatrix
     * @param column column index to check
     * @exception MatrixIndexException if index is not valid
     */
    public static void checkColumnIndex(final AnyMatrix m, final int column)
        throws MatrixIndexException {
        if (column < 0 || column >= m.getColumnDimension()) {
            throw new MatrixIndexException(LocalizedFormats.COLUMN_INDEX_OUT_OF_RANGE,
                                           column, 0, m.getColumnDimension() - 1);
        }
    }

    /**
     * Check if submatrix ranges indices are valid.
     * Rows and columns are indicated counting from 0 to n-1.
     *
     * @param m matrix containing the submatrix
     * @param startRow Initial row index
     * @param endRow Final row index
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @exception MatrixIndexException  if the indices are not valid
     */
    public static void checkSubMatrixIndex(final AnyMatrix m,
                                           final int startRow, final int endRow,
                                           final int startColumn, final int endColumn) {
        checkRowIndex(m, startRow);
        checkRowIndex(m, endRow);
        if (startRow > endRow) {
            throw new MatrixIndexException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW,
                                           startRow, endRow);
        }

        checkColumnIndex(m, startColumn);
        checkColumnIndex(m, endColumn);
        if (startColumn > endColumn) {
            throw new MatrixIndexException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN,
                                           startColumn, endColumn);
        }


    }

    /**
     * Check if submatrix ranges indices are valid.
     * Rows and columns are indicated counting from 0 to n-1.
     *
     * @param m matrix containing the submatrix
     * @param selectedRows Array of row indices.
     * @param selectedColumns Array of column indices.
     * @exception MatrixIndexException if row or column selections are not valid
     */
    public static void checkSubMatrixIndex(final AnyMatrix m,
                                           final int[] selectedRows, final int[] selectedColumns)
        throws MatrixIndexException {
        if (selectedRows.length * selectedColumns.length == 0) {
            if (selectedRows.length == 0) {
                throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY);
            }
            throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY);
        }

        for (final int row : selectedRows) {
            checkRowIndex(m, row);
        }
        for (final int column : selectedColumns) {
            checkColumnIndex(m, column);
        }
    }

    /**
     * Check if matrices are addition compatible
     * @param left left hand side matrix
     * @param right right hand side matrix
     * @exception IllegalArgumentException if matrices are not addition compatible
     */
    public static void checkAdditionCompatible(final AnyMatrix left, final AnyMatrix right)
        throws IllegalArgumentException {
        if ((left.getRowDimension()    != right.getRowDimension()) ||
            (left.getColumnDimension() != right.getColumnDimension())) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_ADDITION_COMPATIBLE_MATRICES,
                    left.getRowDimension(), left.getColumnDimension(),
                    right.getRowDimension(), right.getColumnDimension());
        }
    }

    /**
     * Check if matrices are subtraction compatible
     * @param left left hand side matrix
     * @param right right hand side matrix
     * @exception IllegalArgumentException if matrices are not subtraction compatible
     */
    public static void checkSubtractionCompatible(final AnyMatrix left, final AnyMatrix right)
        throws IllegalArgumentException {
        if ((left.getRowDimension()    != right.getRowDimension()) ||
            (left.getColumnDimension() != right.getColumnDimension())) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_SUBTRACTION_COMPATIBLE_MATRICES,
                    left.getRowDimension(), left.getColumnDimension(),
                    right.getRowDimension(), right.getColumnDimension());
        }
    }

    /**
     * Check if matrices are multiplication compatible
     * @param left left hand side matrix
     * @param right right hand side matrix
     * @exception IllegalArgumentException if matrices are not multiplication compatible
     */
    public static void checkMultiplicationCompatible(final AnyMatrix left, final AnyMatrix right)
        throws IllegalArgumentException {
        if (left.getColumnDimension() != right.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_MULTIPLICATION_COMPATIBLE_MATRICES,
                    left.getRowDimension(), left.getColumnDimension(),
                    right.getRowDimension(), right.getColumnDimension());
        }
    }

}

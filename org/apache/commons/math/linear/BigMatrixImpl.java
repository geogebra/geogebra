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
import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Implementation of {@link BigMatrix} using a BigDecimal[][] array to store entries
 * and <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
 * LU decompostion</a> to support linear system
 * solution and inverse.
 * <p>
 * The LU decompostion is performed as needed, to support the following operations: <ul>
 * <li>solve</li>
 * <li>isSingular</li>
 * <li>getDeterminant</li>
 * <li>inverse</li> </ul></p>
 * <p>
* <strong>Usage notes</strong>:<br>
 * <ul><li>
 * The LU decomposition is stored and reused on subsequent calls.  If matrix
 * data are modified using any of the public setXxx methods, the saved
 * decomposition is discarded.  If data are modified via references to the
 * underlying array obtained using <code>getDataRef()</code>, then the stored
 * LU decomposition will not be discarded.  In this case, you need to
 * explicitly invoke <code>LUDecompose()</code> to recompute the decomposition
 * before using any of the methods above.</li>
 * <li>
 * As specified in the {@link BigMatrix} interface, matrix element indexing
 * is 0-based -- e.g., <code>getEntry(0, 0)</code>
 * returns the element in the first row, first column of the matrix.</li></ul></p>
 *
 * @deprecated as of 2.0, replaced by {@link Array2DRowFieldMatrix} with a {@link
 * org.apache.commons.math.util.BigReal} parameter
 * @version $Revision: 1042376 $ $Date: 2010-12-05 16:54:55 +0100 (dim. 05 déc. 2010) $
 */
@Deprecated
public class BigMatrixImpl implements BigMatrix, Serializable {

    /** BigDecimal 0 */
    static final BigDecimal ZERO = new BigDecimal(0);

    /** BigDecimal 1 */
    static final BigDecimal ONE = new BigDecimal(1);

    /** Bound to determine effective singularity in LU decomposition */
    private static final BigDecimal TOO_SMALL = new BigDecimal(10E-12);

    /** Serialization id */
    private static final long serialVersionUID = -1011428905656140431L;

    /** Entries of the matrix */
    protected BigDecimal data[][] = null;

    /** Entries of cached LU decomposition.
     *  All updates to data (other than luDecompose()) *must* set this to null
     */
    protected BigDecimal lu[][] = null;

    /** Permutation associated with LU decomposition */
    protected int[] permutation = null;

    /** Parity of the permutation associated with the LU decomposition */
    protected int parity = 1;

    /** Rounding mode for divisions **/
    private int roundingMode = BigDecimal.ROUND_HALF_UP;

    /*** BigDecimal scale ***/
    private int scale = 64;

    /**
     * Creates a matrix with no data
     */
    public BigMatrixImpl() {
    }

    /**
     * Create a new BigMatrix with the supplied row and column dimensions.
     *
     * @param rowDimension      the number of rows in the new matrix
     * @param columnDimension   the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not
     *  positive
     */
    public BigMatrixImpl(int rowDimension, int columnDimension) {
        if (rowDimension < 1 ) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, rowDimension, 1);
        }
        if (columnDimension < 1) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, columnDimension, 1);
        }
        data = new BigDecimal[rowDimension][columnDimension];
        lu = null;
    }

    /**
     * Create a new BigMatrix using <code>d</code> as the underlying
     * data array.
     * <p>The input array is copied, not referenced. This constructor has
     * the same effect as calling {@link #BigMatrixImpl(BigDecimal[][], boolean)}
     * with the second argument set to <code>true</code>.</p>
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     */
    public BigMatrixImpl(BigDecimal[][] d) {
        this.copyIn(d);
        lu = null;
    }

    /**
     * Create a new BigMatrix using the input array as the underlying
     * data array.
     * <p>If an array is built specially in order to be embedded in a
     * BigMatrix and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * @param d data for new matrix
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #BigMatrixImpl(BigDecimal[][])
     */
    public BigMatrixImpl(BigDecimal[][] d, boolean copyArray) {
        if (copyArray) {
            copyIn(d);
        } else {
            if (d == null) {
                throw new NullPointerException();
            }
            final int nRows = d.length;
            if (nRows == 0) {
                throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
            }

            final int nCols = d[0].length;
            if (nCols == 0) {
                throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
            }
            for (int r = 1; r < nRows; r++) {
                if (d[r].length != nCols) {
                    throw MathRuntimeException.createIllegalArgumentException(
                          LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                          nCols, d[r].length);
                }
            }
            data = d;
        }
        lu = null;
    }

    /**
     * Create a new BigMatrix using <code>d</code> as the underlying
     * data array.
     * <p>Since the underlying array will hold <code>BigDecimal</code>
     * instances, it will be created.</p>
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     */
    public BigMatrixImpl(double[][] d) {
        final int nRows = d.length;
        if (nRows == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }

        final int nCols = d[0].length;
        if (nCols == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        for (int row = 1; row < nRows; row++) {
            if (d[row].length != nCols) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                      nCols, d[row].length);
            }
        }
        this.copyIn(d);
        lu = null;
    }

    /**
     * Create a new BigMatrix using the values represented by the strings in
     * <code>d</code> as the underlying data array.
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     */
    public BigMatrixImpl(String[][] d) {
        final int nRows = d.length;
        if (nRows == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }

        final int nCols = d[0].length;
        if (nCols == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        for (int row = 1; row < nRows; row++) {
            if (d[row].length != nCols) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                      nCols, d[row].length);
            }
        }
        this.copyIn(d);
        lu = null;
    }

    /**
     * Create a new (column) BigMatrix using <code>v</code> as the
     * data for the unique column of the <code>v.length x 1</code> matrix
     * created.
     * <p>
     * The input array is copied, not referenced.</p>
     *
     * @param v column vector holding data for new matrix
     */
    public BigMatrixImpl(BigDecimal[] v) {
        final int nRows = v.length;
        data = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            data[row][0] = v[row];
        }
    }

    /**
     * Create a new BigMatrix which is a copy of this.
     *
     * @return  the cloned matrix
     */
    public BigMatrix copy() {
        return new BigMatrixImpl(this.copyOut(), false);
    }

    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public BigMatrix add(BigMatrix m) throws IllegalArgumentException {
        try {
            return add((BigMatrixImpl) m);
        } catch (ClassCastException cce) {

            // safety check
            MatrixUtils.checkAdditionCompatible(this, m);

            final int rowCount    = getRowDimension();
            final int columnCount = getColumnDimension();
            final BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
            for (int row = 0; row < rowCount; row++) {
                final BigDecimal[] dataRow    = data[row];
                final BigDecimal[] outDataRow = outData[row];
                for (int col = 0; col < columnCount; col++) {
                    outDataRow[col] = dataRow[col].add(m.getEntry(row, col));
                }
            }
            return new BigMatrixImpl(outData, false);
        }
    }

    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public BigMatrixImpl add(BigMatrixImpl m) throws IllegalArgumentException {

        // safety check
        MatrixUtils.checkAdditionCompatible(this, m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final BigDecimal[] dataRow    = data[row];
            final BigDecimal[] mRow       = m.data[row];
            final BigDecimal[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col].add(mRow[col]);
            }
        }
        return new BigMatrixImpl(outData, false);
    }

    /**
     * Compute  this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public BigMatrix subtract(BigMatrix m) throws IllegalArgumentException {
        try {
            return subtract((BigMatrixImpl) m);
        } catch (ClassCastException cce) {

            // safety check
            MatrixUtils.checkSubtractionCompatible(this, m);

            final int rowCount    = getRowDimension();
            final int columnCount = getColumnDimension();
            final BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
            for (int row = 0; row < rowCount; row++) {
                final BigDecimal[] dataRow    = data[row];
                final BigDecimal[] outDataRow = outData[row];
                for (int col = 0; col < columnCount; col++) {
                    outDataRow[col] = dataRow[col].subtract(getEntry(row, col));
                }
            }
            return new BigMatrixImpl(outData, false);
        }
    }

    /**
     * Compute  this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public BigMatrixImpl subtract(BigMatrixImpl m) throws IllegalArgumentException {

        // safety check
        MatrixUtils.checkSubtractionCompatible(this, m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final BigDecimal[] dataRow    = data[row];
            final BigDecimal[] mRow       = m.data[row];
            final BigDecimal[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col].subtract(mRow[col]);
            }
        }
        return new BigMatrixImpl(outData, false);
    }

    /**
     * Returns the result of adding d to each entry of this.
     *
     * @param d    value to be added to each entry
     * @return     d + this
     */
    public BigMatrix scalarAdd(BigDecimal d) {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final BigDecimal[] dataRow    = data[row];
            final BigDecimal[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col].add(d);
            }
        }
        return new BigMatrixImpl(outData, false);
    }

    /**
     * Returns the result of multiplying each entry of this by <code>d</code>
     * @param d  value to multiply all entries by
     * @return d * this
     */
    public BigMatrix scalarMultiply(BigDecimal d) {
        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
        for (int row = 0; row < rowCount; row++) {
            final BigDecimal[] dataRow    = data[row];
            final BigDecimal[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col].multiply(d);
            }
        }
        return new BigMatrixImpl(outData, false);
    }

    /**
     * Returns the result of postmultiplying this by <code>m</code>.
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public BigMatrix multiply(BigMatrix m) throws IllegalArgumentException {
        try {
            return multiply((BigMatrixImpl) m);
        } catch (ClassCastException cce) {

            // safety check
            MatrixUtils.checkMultiplicationCompatible(this, m);

            final int nRows = this.getRowDimension();
            final int nCols = m.getColumnDimension();
            final int nSum = this.getColumnDimension();
            final BigDecimal[][] outData = new BigDecimal[nRows][nCols];
            for (int row = 0; row < nRows; row++) {
                final BigDecimal[] dataRow    = data[row];
                final BigDecimal[] outDataRow = outData[row];
                for (int col = 0; col < nCols; col++) {
                    BigDecimal sum = ZERO;
                    for (int i = 0; i < nSum; i++) {
                        sum = sum.add(dataRow[i].multiply(m.getEntry(i, col)));
                    }
                    outDataRow[col] = sum;
                }
            }
            return new BigMatrixImpl(outData, false);
        }
    }

    /**
     * Returns the result of postmultiplying this by <code>m</code>.
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public BigMatrixImpl multiply(BigMatrixImpl m) throws IllegalArgumentException {

        // safety check
        MatrixUtils.checkMultiplicationCompatible(this, m);

        final int nRows = this.getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = this.getColumnDimension();
        final BigDecimal[][] outData = new BigDecimal[nRows][nCols];
        for (int row = 0; row < nRows; row++) {
            final BigDecimal[] dataRow    = data[row];
            final BigDecimal[] outDataRow = outData[row];
            for (int col = 0; col < nCols; col++) {
                BigDecimal sum = ZERO;
                for (int i = 0; i < nSum; i++) {
                    sum = sum.add(dataRow[i].multiply(m.data[i][col]));
                }
                outDataRow[col] = sum;
            }
        }
        return new BigMatrixImpl(outData, false);
    }

    /**
     * Returns the result premultiplying this by <code>m</code>.
     * @param m    matrix to premultiply by
     * @return     m * this
     * @throws     IllegalArgumentException
     *             if rowDimension(this) != columnDimension(m)
     */
    public BigMatrix preMultiply(BigMatrix m) throws IllegalArgumentException {
        return m.multiply(this);
    }

    /**
     * Returns matrix entries as a two-dimensional array.
     * <p>
     * Makes a fresh copy of the underlying data.</p>
     *
     * @return    2-dimensional array of entries
     */
    public BigDecimal[][] getData() {
        return copyOut();
    }

    /**
     * Returns matrix entries as a two-dimensional array.
     * <p>
     * Makes a fresh copy of the underlying data converted to
     * <code>double</code> values.</p>
     *
     * @return    2-dimensional array of entries
     */
    public double[][] getDataAsDoubleArray() {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final double d[][] = new double[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                d[i][j] = data[i][j].doubleValue();
            }
        }
        return d;
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>
     * Does not make a fresh copy of the underlying data.</p>
     *
     * @return 2-dimensional array of entries
     */
    public BigDecimal[][] getDataRef() {
        return data;
    }

    /***
     * Gets the rounding mode for division operations
     * The default is {@link java.math.BigDecimal#ROUND_HALF_UP}
     * @see BigDecimal
     * @return the rounding mode.
     */
    public int getRoundingMode() {
        return roundingMode;
    }

    /***
     * Sets the rounding mode for decimal divisions.
     * @see BigDecimal
     * @param roundingMode rounding mode for decimal divisions
     */
    public void setRoundingMode(int roundingMode) {
        this.roundingMode = roundingMode;
    }

    /***
     * Sets the scale for division operations.
     * The default is 64
     * @see BigDecimal
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /***
     * Sets the scale for division operations.
     * @see BigDecimal
     * @param scale scale for division operations
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Returns the <a href="http://mathworld.wolfram.com/MaximumAbsoluteRowSumNorm.html">
     * maximum absolute row sum norm</a> of the matrix.
     *
     * @return norm
     */
    public BigDecimal getNorm() {
        BigDecimal maxColSum = ZERO;
        for (int col = 0; col < this.getColumnDimension(); col++) {
            BigDecimal sum = ZERO;
            for (int row = 0; row < this.getRowDimension(); row++) {
                sum = sum.add(data[row][col].abs());
            }
            maxColSum = maxColSum.max(sum);
        }
        return maxColSum;
    }

    /**
     * Gets a submatrix. Rows and columns are indicated
     * counting from 0 to n-1.
     *
     * @param startRow Initial row index
     * @param endRow Final row index
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @return The subMatrix containing the data of the
     *         specified rows and columns
     * @exception MatrixIndexException if row or column selections are not valid
     */
    public BigMatrix getSubMatrix(int startRow, int endRow,
                                  int startColumn, int endColumn)
        throws MatrixIndexException {

        MatrixUtils.checkRowIndex(this, startRow);
        MatrixUtils.checkRowIndex(this, endRow);
        if (startRow > endRow) {
            throw new MatrixIndexException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW,
                                           startRow, endRow);
        }

        MatrixUtils.checkColumnIndex(this, startColumn);
        MatrixUtils.checkColumnIndex(this, endColumn);
        if (startColumn > endColumn) {
            throw new MatrixIndexException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN,
                                           startColumn, endColumn);
        }

        final BigDecimal[][] subMatrixData =
            new BigDecimal[endRow - startRow + 1][endColumn - startColumn + 1];
        for (int i = startRow; i <= endRow; i++) {
            System.arraycopy(data[i], startColumn,
                             subMatrixData[i - startRow], 0,
                             endColumn - startColumn + 1);
        }

        return new BigMatrixImpl(subMatrixData, false);

    }

    /**
     * Gets a submatrix. Rows and columns are indicated
     * counting from 0 to n-1.
     *
     * @param selectedRows Array of row indices must be non-empty
     * @param selectedColumns Array of column indices must be non-empty
     * @return The subMatrix containing the data in the
     *     specified rows and columns
     * @exception MatrixIndexException  if supplied row or column index arrays
     *     are not valid
     */
    public BigMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
        throws MatrixIndexException {

        if (selectedRows.length * selectedColumns.length == 0) {
            if (selectedRows.length == 0) {
                throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY);
            }
            throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY);
        }

        final BigDecimal[][] subMatrixData =
            new BigDecimal[selectedRows.length][selectedColumns.length];
        try  {
            for (int i = 0; i < selectedRows.length; i++) {
                final BigDecimal[] subI = subMatrixData[i];
                final BigDecimal[] dataSelectedI = data[selectedRows[i]];
                for (int j = 0; j < selectedColumns.length; j++) {
                    subI[j] = dataSelectedI[selectedColumns[j]];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // we redo the loop with checks enabled
            // in order to generate an appropriate message
            for (final int row : selectedRows) {
                MatrixUtils.checkRowIndex(this, row);
            }
            for (final int column : selectedColumns) {
                MatrixUtils.checkColumnIndex(this, column);
            }
        }
        return new BigMatrixImpl(subMatrixData, false);
    }

    /**
     * Replace the submatrix starting at <code>row, column</code> using data in
     * the input <code>subMatrix</code> array. Indexes are 0-based.
     * <p>
     * Example:<br>
     * Starting with <pre>
     * 1  2  3  4
     * 5  6  7  8
     * 9  0  1  2
     * </pre>
     * and <code>subMatrix = {{3, 4} {5,6}}</code>, invoking
     * <code>setSubMatrix(subMatrix,1,1))</code> will result in <pre>
     * 1  2  3  4
     * 5  3  4  8
     * 9  5  6  2
     * </pre></p>
     *
     * @param subMatrix  array containing the submatrix replacement data
     * @param row  row coordinate of the top, left element to be replaced
     * @param column  column coordinate of the top, left element to be replaced
     * @throws MatrixIndexException  if subMatrix does not fit into this
     *    matrix from element in (row, column)
     * @throws IllegalArgumentException if <code>subMatrix</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>subMatrix</code> is null
     * @since 1.1
     */
    public void setSubMatrix(BigDecimal[][] subMatrix, int row, int column)
    throws MatrixIndexException {

        final int nRows = subMatrix.length;
        if (nRows == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }

        final int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }

        for (int r = 1; r < nRows; r++) {
            if (subMatrix[r].length != nCols) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                      nCols, subMatrix[r].length);
            }
        }

        if (data == null) {
            if (row > 0) {
                throw MathRuntimeException.createIllegalStateException(
                        LocalizedFormats.FIRST_ROWS_NOT_INITIALIZED_YET,
                        row);
            }
            if (column > 0) {
                throw MathRuntimeException.createIllegalStateException(
                        LocalizedFormats.FIRST_COLUMNS_NOT_INITIALIZED_YET,
                        column);
            }
            data = new BigDecimal[nRows][nCols];
            System.arraycopy(subMatrix, 0, data, 0, subMatrix.length);
        } else {
            MatrixUtils.checkRowIndex(this, row);
            MatrixUtils.checkColumnIndex(this, column);
            MatrixUtils.checkRowIndex(this, nRows + row - 1);
            MatrixUtils.checkColumnIndex(this, nCols + column - 1);
        }
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(subMatrix[i], 0, data[row + i], column, nCols);
        }

        lu = null;

    }

    /**
     * Returns the entries in row number <code>row</code>
     * as a row matrix.  Row indices start at 0.
     *
     * @param row the row to be fetched
     * @return row matrix
     * @throws MatrixIndexException if the specified row index is invalid
     */
    public BigMatrix getRowMatrix(int row) throws MatrixIndexException {
        MatrixUtils.checkRowIndex(this, row);
        final int ncols = this.getColumnDimension();
        final BigDecimal[][] out = new BigDecimal[1][ncols];
        System.arraycopy(data[row], 0, out[0], 0, ncols);
        return new BigMatrixImpl(out, false);
    }

    /**
     * Returns the entries in column number <code>column</code>
     * as a column matrix.  Column indices start at 0.
     *
     * @param column the column to be fetched
     * @return column matrix
     * @throws MatrixIndexException if the specified column index is invalid
     */
    public BigMatrix getColumnMatrix(int column) throws MatrixIndexException {
        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = this.getRowDimension();
        final BigDecimal[][] out = new BigDecimal[nRows][1];
        for (int row = 0; row < nRows; row++) {
            out[row][0] = data[row][column];
        }
        return new BigMatrixImpl(out, false);
    }

    /**
     * Returns the entries in row number <code>row</code> as an array.
     * <p>
     * Row indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= row < rowDimension.</code></p>
     *
     * @param row the row to be fetched
     * @return array of entries in the row
     * @throws MatrixIndexException if the specified row index is not valid
     */
    public BigDecimal[] getRow(int row) throws MatrixIndexException {
        MatrixUtils.checkRowIndex(this, row);
        final int ncols = this.getColumnDimension();
        final BigDecimal[] out = new BigDecimal[ncols];
        System.arraycopy(data[row], 0, out, 0, ncols);
        return out;
    }

     /**
     * Returns the entries in row number <code>row</code> as an array
     * of double values.
     * <p>
     * Row indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= row < rowDimension.</code></p>
     *
     * @param row the row to be fetched
     * @return array of entries in the row
     * @throws MatrixIndexException if the specified row index is not valid
     */
    public double[] getRowAsDoubleArray(int row) throws MatrixIndexException {
        MatrixUtils.checkRowIndex(this, row);
        final int ncols = this.getColumnDimension();
        final double[] out = new double[ncols];
        for (int i=0;i<ncols;i++) {
            out[i] = data[row][i].doubleValue();
        }
        return out;
    }

     /**
     * Returns the entries in column number <code>col</code> as an array.
     * <p>
     * Column indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= column < columnDimension.</code></p>
     *
     * @param col the column to be fetched
     * @return array of entries in the column
     * @throws MatrixIndexException if the specified column index is not valid
     */
    public BigDecimal[] getColumn(int col) throws MatrixIndexException {
        MatrixUtils.checkColumnIndex(this, col);
        final int nRows = this.getRowDimension();
        final BigDecimal[] out = new BigDecimal[nRows];
        for (int i = 0; i < nRows; i++) {
            out[i] = data[i][col];
        }
        return out;
    }

    /**
     * Returns the entries in column number <code>col</code> as an array
     * of double values.
     * <p>
     * Column indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= column < columnDimension.</code></p>
     *
     * @param col the column to be fetched
     * @return array of entries in the column
     * @throws MatrixIndexException if the specified column index is not valid
     */
    public double[] getColumnAsDoubleArray(int col) throws MatrixIndexException {
        MatrixUtils.checkColumnIndex(this, col);
        final int nrows = this.getRowDimension();
        final double[] out = new double[nrows];
        for (int i=0;i<nrows;i++) {
            out[i] = data[i][col].doubleValue();
        }
        return out;
    }

     /**
     * Returns the entry in the specified row and column.
     * <p>
     * Row and column indices start at 0 and must satisfy
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.</p>
     *
     * @param row  row location of entry to be fetched
     * @param column  column location of entry to be fetched
     * @return matrix entry in row,column
     * @throws MatrixIndexException if the row or column index is not valid
     */
    public BigDecimal getEntry(int row, int column)
    throws MatrixIndexException {
        try {
            return data[row][column];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException(
                    LocalizedFormats.NO_SUCH_MATRIX_ENTRY,
                    row, column, getRowDimension(), getColumnDimension());
        }
    }

    /**
     * Returns the entry in the specified row and column as a double.
     * <p>
     * Row and column indices start at 0 and must satisfy
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.</p>
     *
     * @param row  row location of entry to be fetched
     * @param column  column location of entry to be fetched
     * @return matrix entry in row,column
     * @throws MatrixIndexException if the row
     * or column index is not valid
     */
    public double getEntryAsDouble(int row, int column) throws MatrixIndexException {
        return getEntry(row,column).doubleValue();
    }

    /**
     * Returns the transpose matrix.
     *
     * @return transpose matrix
     */
    public BigMatrix transpose() {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final BigDecimal[][] outData = new BigDecimal[nCols][nRows];
        for (int row = 0; row < nRows; row++) {
            final BigDecimal[] dataRow = data[row];
            for (int col = 0; col < nCols; col++) {
                outData[col][row] = dataRow[col];
            }
        }
        return new BigMatrixImpl(outData, false);
    }

    /**
     * Returns the inverse matrix if this matrix is invertible.
     *
     * @return inverse matrix
     * @throws InvalidMatrixException if this is not invertible
     */
    public BigMatrix inverse() throws InvalidMatrixException {
        return solve(MatrixUtils.createBigIdentityMatrix(getRowDimension()));
    }

    /**
     * Returns the determinant of this matrix.
     *
     * @return determinant
     * @throws InvalidMatrixException if matrix is not square
     */
    public BigDecimal getDeterminant() throws InvalidMatrixException {
        if (!isSquare()) {
            throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
        }
        if (isSingular()) {   // note: this has side effect of attempting LU decomp if lu == null
            return ZERO;
        } else {
            BigDecimal det = (parity == 1) ? ONE : ONE.negate();
            for (int i = 0; i < getRowDimension(); i++) {
                det = det.multiply(lu[i][i]);
            }
            return det;
        }
    }

     /**
     * Is this a square matrix?
     * @return true if the matrix is square (rowDimension = columnDimension)
     */
    public boolean isSquare() {
        return getColumnDimension() == getRowDimension();
    }

    /**
     * Is this a singular matrix?
     * @return true if the matrix is singular
     */
    public boolean isSingular() {
        if (lu == null) {
            try {
                luDecompose();
                return false;
            } catch (InvalidMatrixException ex) {
                return true;
            }
        } else { // LU decomp must have been successfully performed
            return false; // so the matrix is not singular
        }
    }

    /**
     * Returns the number of rows in the matrix.
     *
     * @return rowDimension
     */
    public int getRowDimension() {
        return data.length;
    }

    /**
     * Returns the number of columns in the matrix.
     *
     * @return columnDimension
     */
    public int getColumnDimension() {
        return data[0].length;
    }

     /**
     * Returns the <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * trace</a> of the matrix (the sum of the elements on the main diagonal).
     *
     * @return trace
     *
     * @throws IllegalArgumentException if this matrix is not square.
     */
    public BigDecimal getTrace() throws IllegalArgumentException {
        if (!isSquare()) {
            throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
        }
        BigDecimal trace = data[0][0];
        for (int i = 1; i < this.getRowDimension(); i++) {
            trace = trace.add(data[i][i]);
        }
        return trace;
    }

    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public BigDecimal[] operate(BigDecimal[] v) throws IllegalArgumentException {
        if (v.length != getColumnDimension()) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    v.length, getColumnDimension() );
        }
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final BigDecimal[] out = new BigDecimal[nRows];
        for (int row = 0; row < nRows; row++) {
            BigDecimal sum = ZERO;
            for (int i = 0; i < nCols; i++) {
                sum = sum.add(data[row][i].multiply(v[i]));
            }
            out[row] = sum;
        }
        return out;
    }

    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    public BigDecimal[] operate(double[] v) throws IllegalArgumentException {
        final BigDecimal bd[] = new BigDecimal[v.length];
        for (int i = 0; i < bd.length; i++) {
            bd[i] = new BigDecimal(v[i]);
        }
        return operate(bd);
    }

    /**
     * Returns the (row) vector result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    public BigDecimal[] preMultiply(BigDecimal[] v) throws IllegalArgumentException {
        final int nRows = this.getRowDimension();
        if (v.length != nRows) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    v.length, nRows );
        }
        final int nCols = this.getColumnDimension();
        final BigDecimal[] out = new BigDecimal[nCols];
        for (int col = 0; col < nCols; col++) {
            BigDecimal sum = ZERO;
            for (int i = 0; i < nRows; i++) {
                sum = sum.add(data[i][col].multiply(v[i]));
            }
            out[col] = sum;
        }
        return out;
    }

    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>.
     *
     * @param b  array of constants forming RHS of linear systems to
     * to solve
     * @return solution array
     * @throws IllegalArgumentException if this.rowDimension != row dimension
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    public BigDecimal[] solve(BigDecimal[] b) throws IllegalArgumentException, InvalidMatrixException {
        final int nRows = this.getRowDimension();
        if (b.length != nRows) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    b.length, nRows);
        }
        final BigMatrix bMatrix = new BigMatrixImpl(b);
        final BigDecimal[][] solution = ((BigMatrixImpl) (solve(bMatrix))).getDataRef();
        final BigDecimal[] out = new BigDecimal[nRows];
        for (int row = 0; row < nRows; row++) {
            out[row] = solution[row][0];
        }
        return out;
    }

    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>.
     *
     * @param b  array of constants forming RHS of linear systems to
     * to solve
     * @return solution array
     * @throws IllegalArgumentException if this.rowDimension != row dimension
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    public BigDecimal[] solve(double[] b) throws IllegalArgumentException, InvalidMatrixException {
        final BigDecimal bd[] = new BigDecimal[b.length];
        for (int i = 0; i < bd.length; i++) {
            bd[i] = new BigDecimal(b[i]);
        }
        return solve(bd);
    }

    /**
     * Returns a matrix of (column) solution vectors for linear systems with
     * coefficient matrix = this and constant vectors = columns of
     * <code>b</code>.
     *
     * @param b  matrix of constant vectors forming RHS of linear systems to
     * to solve
     * @return matrix of solution vectors
     * @throws IllegalArgumentException if this.rowDimension != row dimension
     * @throws InvalidMatrixException if this matrix is not square or is singular
     */
    public BigMatrix solve(BigMatrix b) throws IllegalArgumentException, InvalidMatrixException  {
        if (b.getRowDimension() != getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    b.getRowDimension(), b.getColumnDimension(), getRowDimension(), "n");
        }
        if (!isSquare()) {
            throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
        }
        if (this.isSingular()) { // side effect: compute LU decomp
            throw new SingularMatrixException();
        }

        final int nCol = this.getColumnDimension();
        final int nColB = b.getColumnDimension();
        final int nRowB = b.getRowDimension();

        // Apply permutations to b
        final BigDecimal[][] bp = new BigDecimal[nRowB][nColB];
        for (int row = 0; row < nRowB; row++) {
            final BigDecimal[] bpRow = bp[row];
            for (int col = 0; col < nColB; col++) {
                bpRow[col] = b.getEntry(permutation[row], col);
            }
        }

        // Solve LY = b
        for (int col = 0; col < nCol; col++) {
            for (int i = col + 1; i < nCol; i++) {
                final BigDecimal[] bpI = bp[i];
                final BigDecimal[] luI = lu[i];
                for (int j = 0; j < nColB; j++) {
                    bpI[j] = bpI[j].subtract(bp[col][j].multiply(luI[col]));
                }
            }
        }

        // Solve UX = Y
        for (int col = nCol - 1; col >= 0; col--) {
            final BigDecimal[] bpCol = bp[col];
            final BigDecimal luDiag = lu[col][col];
            for (int j = 0; j < nColB; j++) {
                bpCol[j] = bpCol[j].divide(luDiag, scale, roundingMode);
            }
            for (int i = 0; i < col; i++) {
                final BigDecimal[] bpI = bp[i];
                final BigDecimal[] luI = lu[i];
                for (int j = 0; j < nColB; j++) {
                    bpI[j] = bpI[j].subtract(bp[col][j].multiply(luI[col]));
                }
            }
        }

        return new BigMatrixImpl(bp, false);

    }

    /**
     * Computes a new
     * <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
     * LU decompostion</a> for this matrix, storing the result for use by other methods.
     * <p>
     * <strong>Implementation Note</strong>:<br>
     * Uses <a href="http://www.damtp.cam.ac.uk/user/fdl/people/sd/lectures/nummeth98/linear.htm">
     * Crout's algortithm</a>, with partial pivoting.</p>
     * <p>
     * <strong>Usage Note</strong>:<br>
     * This method should rarely be invoked directly. Its only use is
     * to force recomputation of the LU decomposition when changes have been
     * made to the underlying data using direct array references. Changes
     * made using setXxx methods will trigger recomputation when needed
     * automatically.</p>
     *
     * @throws InvalidMatrixException if the matrix is non-square or singular.
     */
    public void luDecompose() throws InvalidMatrixException {

        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
        }
        lu = this.getData();

        // Initialize permutation array and parity
        permutation = new int[nRows];
        for (int row = 0; row < nRows; row++) {
            permutation[row] = row;
        }
        parity = 1;

        // Loop over columns
        for (int col = 0; col < nCols; col++) {

            BigDecimal sum = ZERO;

            // upper
            for (int row = 0; row < col; row++) {
                final BigDecimal[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < row; i++) {
                    sum = sum.subtract(luRow[i].multiply(lu[i][col]));
                }
                luRow[col] = sum;
            }

            // lower
            int max = col; // permutation row
            BigDecimal largest = ZERO;
            for (int row = col; row < nRows; row++) {
                final BigDecimal[] luRow = lu[row];
                sum = luRow[col];
                for (int i = 0; i < col; i++) {
                    sum = sum.subtract(luRow[i].multiply(lu[i][col]));
                }
                luRow[col] = sum;

                // maintain best permutation choice
                if (sum.abs().compareTo(largest) == 1) {
                    largest = sum.abs();
                    max = row;
                }
            }

            // Singularity check
            if (lu[max][col].abs().compareTo(TOO_SMALL) <= 0) {
                lu = null;
                throw new SingularMatrixException();
            }

            // Pivot if necessary
            if (max != col) {
                BigDecimal tmp = ZERO;
                for (int i = 0; i < nCols; i++) {
                    tmp = lu[max][i];
                    lu[max][i] = lu[col][i];
                    lu[col][i] = tmp;
                }
                int temp = permutation[max];
                permutation[max] = permutation[col];
                permutation[col] = temp;
                parity = -parity;
            }

            // Divide the lower elements by the "winning" diagonal elt.
            final BigDecimal luDiag = lu[col][col];
            for (int row = col + 1; row < nRows; row++) {
                final BigDecimal[] luRow = lu[row];
                luRow[col] = luRow[col].divide(luDiag, scale, roundingMode);
            }

        }

    }

    /**
     * Get a string representation for this matrix.
     * @return a string representation for this matrix
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("BigMatrixImpl{");
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                if (i > 0) {
                    res.append(",");
                }
                res.append("{");
                for (int j = 0; j < data[0].length; j++) {
                    if (j > 0) {
                        res.append(",");
                    }
                    res.append(data[i][j]);
                }
                res.append("}");
            }
        }
        res.append("}");
        return res.toString();
    }

    /**
     * Returns true iff <code>object</code> is a
     * <code>BigMatrixImpl</code> instance with the same dimensions as this
     * and all corresponding matrix entries are equal.  BigDecimal.equals
     * is used to compare corresponding entries.
     *
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    @Override
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof BigMatrixImpl == false) {
            return false;
        }
        final BigMatrix m = (BigMatrix) object;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; row++) {
            final BigDecimal[] dataRow = data[row];
            for (int col = 0; col < nCols; col++) {
                if (!dataRow[col].equals(m.getEntry(row, col))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Computes a hashcode for the matrix.
     *
     * @return hashcode for matrix
     */
    @Override
    public int hashCode() {
        int ret = 7;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        ret = ret * 31 + nRows;
        ret = ret * 31 + nCols;
        for (int row = 0; row < nRows; row++) {
            final BigDecimal[] dataRow = data[row];
            for (int col = 0; col < nCols; col++) {
                ret = ret * 31 + (11 * (row+1) + 17 * (col+1)) *
                dataRow[col].hashCode();
            }
        }
        return ret;
    }

    //------------------------ Protected methods

    /**
     *  Returns the LU decomposition as a BigMatrix.
     *  Returns a fresh copy of the cached LU matrix if this has been computed;
     *  otherwise the composition is computed and cached for use by other methods.
     *  Since a copy is returned in either case, changes to the returned matrix do not
     *  affect the LU decomposition property.
     * <p>
     * The matrix returned is a compact representation of the LU decomposition.
     * Elements below the main diagonal correspond to entries of the "L" matrix;
     * elements on and above the main diagonal correspond to entries of the "U"
     * matrix.</p>
     * <p>
     * Example: <pre>
     *
     *     Returned matrix                L                  U
     *         2  3  1                   1  0  0            2  3  1
     *         5  4  6                   5  1  0            0  4  6
     *         1  7  8                   1  7  1            0  0  8
     * </pre>
     *
     * The L and U matrices satisfy the matrix equation LU = permuteRows(this), <br>
     *  where permuteRows reorders the rows of the matrix to follow the order determined
     *  by the <a href=#getPermutation()>permutation</a> property.</p>
     *
     * @return LU decomposition matrix
     * @throws InvalidMatrixException if the matrix is non-square or singular.
     */
    protected BigMatrix getLUMatrix() throws InvalidMatrixException {
        if (lu == null) {
            luDecompose();
        }
        return new BigMatrixImpl(lu);
    }

    /**
     * Returns the permutation associated with the lu decomposition.
     * The entries of the array represent a permutation of the numbers 0, ... , nRows - 1.
     * <p>
     * Example:
     * permutation = [1, 2, 0] means current 2nd row is first, current third row is second
     * and current first row is last.</p>
     * <p>
     * Returns a fresh copy of the array.</p>
     *
     * @return the permutation
     */
    protected int[] getPermutation() {
        final int[] out = new int[permutation.length];
        System.arraycopy(permutation, 0, out, 0, permutation.length);
        return out;
    }

    //------------------------ Private methods

    /**
     * Returns a fresh copy of the underlying data array.
     *
     * @return a copy of the underlying data array.
     */
    private BigDecimal[][] copyOut() {
        final int nRows = this.getRowDimension();
        final BigDecimal[][] out = new BigDecimal[nRows][this.getColumnDimension()];
        // can't copy 2-d array in one shot, otherwise get row references
        for (int i = 0; i < nRows; i++) {
            System.arraycopy(data[i], 0, out[i], 0, data[i].length);
        }
        return out;
    }

    /**
     * Replaces data with a fresh copy of the input array.
     * <p>
     * Verifies that the input array is rectangular and non-empty.</p>
     *
     * @param in data to copy in
     * @throws IllegalArgumentException if input array is emtpy or not
     *    rectangular
     * @throws NullPointerException if input array is null
     */
    private void copyIn(BigDecimal[][] in) {
        setSubMatrix(in,0,0);
    }

    /**
     * Replaces data with a fresh copy of the input array.
     *
     * @param in data to copy in
     */
    private void copyIn(double[][] in) {
        final int nRows = in.length;
        final int nCols = in[0].length;
        data = new BigDecimal[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            final BigDecimal[] dataI = data[i];
            final double[] inI = in[i];
            for (int j = 0; j < nCols; j++) {
                dataI[j] = new BigDecimal(inI[j]);
            }
        }
        lu = null;
    }

    /**
     * Replaces data with BigDecimals represented by the strings in the input
     * array.
     *
     * @param in data to copy in
     */
    private void copyIn(String[][] in) {
        final int nRows = in.length;
        final int nCols = in[0].length;
        data = new BigDecimal[nRows][nCols];
        for (int i = 0; i < nRows; i++) {
            final BigDecimal[] dataI = data[i];
            final String[] inI = in[i];
            for (int j = 0; j < nCols; j++) {
                dataI[j] = new BigDecimal(inI[j]);
            }
        }
        lu = null;
    }

}

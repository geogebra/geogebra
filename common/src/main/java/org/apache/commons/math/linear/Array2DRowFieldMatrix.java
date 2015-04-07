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

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Implementation of FieldMatrix<T> using a {@link FieldElement}[][] array to store entries.
 * <p>
 * As specified in the {@link FieldMatrix} interface, matrix element indexing
 * is 0-based -- e.g., <code>getEntry(0, 0)</code>
 * returns the element in the first row, first column of the matrix.</li></ul>
 * </p>
 *
 * @param <T> the type of the field elements
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public class Array2DRowFieldMatrix<T extends FieldElement<T>> extends AbstractFieldMatrix<T> implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = 7260756672015356458L;

    /** Entries of the matrix */
    protected T[][] data;

    /**
     * Creates a matrix with no data
     * @param field field to which the elements belong
     */
    public Array2DRowFieldMatrix(final Field<T> field) {
        super(field);
    }

    /**
     * Create a new FieldMatrix<T> with the supplied row and column dimensions.
     *
     * @param field field to which the elements belong
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not
     *  positive
     */
    public Array2DRowFieldMatrix(final Field<T> field,
                           final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        super(field, rowDimension, columnDimension);
        data = buildArray(field, rowDimension, columnDimension);
    }

    /**
     * Create a new FieldMatrix<T> using the input array as the underlying
     * data array.
     * <p>The input array is copied, not referenced. This constructor has
     * the same effect as calling {@link #Array2DRowFieldMatrix(FieldElement[][], boolean)}
     * with the second argument set to <code>true</code>.</p>
     *
     * @param d data for new matrix
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #Array2DRowFieldMatrix(FieldElement[][], boolean)
     */
    public Array2DRowFieldMatrix(final T[][] d)
        throws IllegalArgumentException, NullPointerException {
        super(extractField(d));
        copyIn(d);
    }

    /**
     * Create a new FieldMatrix<T> using the input array as the underlying
     * data array.
     * <p>If an array is built specially in order to be embedded in a
     * FieldMatrix<T> and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * @param d data for new matrix
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @throws IllegalArgumentException if <code>d</code> is not rectangular
     *  (not all rows have the same length) or empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #Array2DRowFieldMatrix(FieldElement[][])
     */
    public Array2DRowFieldMatrix(final T[][] d, final boolean copyArray)
        throws IllegalArgumentException, NullPointerException {
        super(extractField(d));
        if (copyArray) {
            copyIn(d);
        } else {
            if (d == null) {
                throw new NullPointerException();
            }
            final int nRows = d.length;
            if (nRows == 0) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.AT_LEAST_ONE_ROW);
            }
            final int nCols = d[0].length;
            if (nCols == 0) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.AT_LEAST_ONE_COLUMN);
            }
            for (int r = 1; r < nRows; r++) {
                if (d[r].length != nCols) {
                    throw MathRuntimeException.createIllegalArgumentException(
                          LocalizedFormats.DIFFERENT_ROWS_LENGTHS, nCols, d[r].length);
                }
            }
            data = d;
        }
    }

    /**
     * Create a new (column) FieldMatrix<T> using <code>v</code> as the
     * data for the unique column of the <code>v.length x 1</code> matrix
     * created.
     * <p>The input array is copied, not referenced.</p>
     *
     * @param v column vector holding data for new matrix
     */
    public Array2DRowFieldMatrix(final T[] v) {
        super(extractField(v));
        final int nRows = v.length;
        data = buildArray(getField(), nRows, 1);
        for (int row = 0; row < nRows; row++) {
            data[row][0] = v[row];
        }
    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        return new Array2DRowFieldMatrix<T>(getField(), rowDimension, columnDimension);
    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> copy() {
        return new Array2DRowFieldMatrix<T>(copyOut(), false);
    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> add(final FieldMatrix<T> m)
        throws IllegalArgumentException {
        try {
            return add((Array2DRowFieldMatrix<T>) m);
        } catch (ClassCastException cce) {
            return super.add(m);
        }
    }

    /**
     * Compute the sum of this and <code>m</code>.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public Array2DRowFieldMatrix<T> add(final Array2DRowFieldMatrix<T> m)
        throws IllegalArgumentException {

        // safety check
        checkAdditionCompatible(m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final T[][] outData = buildArray(getField(), rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            final T[] dataRow    = data[row];
            final T[] mRow       = m.data[row];
            final T[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col].add(mRow[col]);
            }
        }

        return new Array2DRowFieldMatrix<T>(outData, false);

    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> subtract(final FieldMatrix<T> m)
        throws IllegalArgumentException {
        try {
            return subtract((Array2DRowFieldMatrix<T>) m);
        } catch (ClassCastException cce) {
            return super.subtract(m);
        }
    }

    /**
     * Compute  this minus <code>m</code>.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    public Array2DRowFieldMatrix<T> subtract(final Array2DRowFieldMatrix<T> m)
        throws IllegalArgumentException {

        // safety check
        checkSubtractionCompatible(m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final T[][] outData = buildArray(getField(), rowCount, columnCount);
        for (int row = 0; row < rowCount; row++) {
            final T[] dataRow    = data[row];
            final T[] mRow       = m.data[row];
            final T[] outDataRow = outData[row];
            for (int col = 0; col < columnCount; col++) {
                outDataRow[col] = dataRow[col].subtract(mRow[col]);
            }
        }

        return new Array2DRowFieldMatrix<T>(outData, false);

    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> multiply(final FieldMatrix<T> m)
        throws IllegalArgumentException {
        try {
            return multiply((Array2DRowFieldMatrix<T>) m);
        } catch (ClassCastException cce) {
            return super.multiply(m);
        }
    }

    /**
     * Returns the result of postmultiplying this by <code>m</code>.
     * @param m    matrix to postmultiply by
     * @return     this*m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    public Array2DRowFieldMatrix<T> multiply(final Array2DRowFieldMatrix<T> m)
        throws IllegalArgumentException {

        // safety check
        checkMultiplicationCompatible(m);

        final int nRows = this.getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = this.getColumnDimension();
        final T[][] outData = buildArray(getField(), nRows, nCols);
        for (int row = 0; row < nRows; row++) {
            final T[] dataRow    = data[row];
            final T[] outDataRow = outData[row];
            for (int col = 0; col < nCols; col++) {
                T sum = getField().getZero();
                for (int i = 0; i < nSum; i++) {
                    sum = sum.add(dataRow[i].multiply(m.data[i][col]));
                }
                outDataRow[col] = sum;
            }
        }

        return new Array2DRowFieldMatrix<T>(outData, false);

    }

    /** {@inheritDoc} */
    @Override
    public T[][] getData() {
        return copyOut();
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>
     * Does <strong>not</strong> make a fresh copy of the underlying data.</p>
     *
     * @return 2-dimensional array of entries
     */
    public T[][] getDataRef() {
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public void setSubMatrix(final T[][] subMatrix, final int row, final int column)
    throws MatrixIndexException {
        if (data == null) {
            if (row > 0) {
                throw MathRuntimeException.createIllegalStateException(
                      LocalizedFormats.FIRST_ROWS_NOT_INITIALIZED_YET, row);
            }
            if (column > 0) {
                throw MathRuntimeException.createIllegalStateException(
                      LocalizedFormats.FIRST_COLUMNS_NOT_INITIALIZED_YET, column);
            }
            final int nRows = subMatrix.length;
            if (nRows == 0) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.AT_LEAST_ONE_ROW);
            }

            final int nCols = subMatrix[0].length;
            if (nCols == 0) {
                throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.AT_LEAST_ONE_COLUMN);
            }
            data = buildArray(getField(), subMatrix.length, nCols);
            for (int i = 0; i < data.length; ++i) {
                if (subMatrix[i].length != nCols) {
                    throw MathRuntimeException.createIllegalArgumentException(
                          LocalizedFormats.DIFFERENT_ROWS_LENGTHS, nCols, subMatrix[i].length);
                }
                System.arraycopy(subMatrix[i], 0, data[i + row], column, nCols);
            }
        } else {
            super.setSubMatrix(subMatrix, row, column);
        }

    }

    /** {@inheritDoc} */
    @Override
    public T getEntry(final int row, final int column)
        throws MatrixIndexException {
        try {
            return data[row][column];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException(
                      LocalizedFormats.NO_SUCH_MATRIX_ENTRY, row, column, getRowDimension(), getColumnDimension());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEntry(final int row, final int column, final T value)
        throws MatrixIndexException {
        try {
            data[row][column] = value;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException(
                      LocalizedFormats.NO_SUCH_MATRIX_ENTRY, row, column, getRowDimension(), getColumnDimension());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addToEntry(final int row, final int column, final T increment)
        throws MatrixIndexException {
        try {
            data[row][column] = data[row][column].add(increment);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException(
                      LocalizedFormats.NO_SUCH_MATRIX_ENTRY, row, column, getRowDimension(), getColumnDimension());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void multiplyEntry(final int row, final int column, final T factor)
        throws MatrixIndexException {
        try {
            data[row][column] = data[row][column].multiply(factor);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MatrixIndexException(
                      LocalizedFormats.NO_SUCH_MATRIX_ENTRY, row, column, getRowDimension(), getColumnDimension());
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return (data == null) ? 0 : data.length;
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return ((data == null) || (data[0] == null)) ? 0 : data[0].length;
    }

    /** {@inheritDoc} */
    @Override
    public T[] operate(final T[] v)
        throws IllegalArgumentException {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        if (v.length != nCols) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.VECTOR_LENGTH_MISMATCH, v.length, nCols);
        }
        final T[] out = buildArray(getField(), nRows);
        for (int row = 0; row < nRows; row++) {
            final T[] dataRow = data[row];
            T sum = getField().getZero();
            for (int i = 0; i < nCols; i++) {
                sum = sum.add(dataRow[i].multiply(v[i]));
            }
            out[row] = sum;
        }
        return out;
    }

    /** {@inheritDoc} */
    @Override
    public T[] preMultiply(final T[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.VECTOR_LENGTH_MISMATCH, v.length, nRows);
        }

        final T[] out = buildArray(getField(), nCols);
        for (int col = 0; col < nCols; ++col) {
            T sum = getField().getZero();
            for (int i = 0; i < nRows; ++i) {
                sum = sum.add(data[i][col].multiply(v[i]));
            }
            out[col] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    @Override
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int i = 0; i < rows; ++i) {
            final T[] rowI = data[i];
            for (int j = 0; j < columns; ++j) {
                rowI[j] = visitor.visit(i, j, rowI[j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int i = 0; i < rows; ++i) {
            final T[] rowI = data[i];
            for (int j = 0; j < columns; ++j) {
                visitor.visit(i, j, rowI[j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor,
                            final int startRow, final int endRow,
                            final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int i = startRow; i <= endRow; ++i) {
            final T[] rowI = data[i];
            for (int j = startColumn; j <= endColumn; ++j) {
                rowI[j] = visitor.visit(i, j, rowI[j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor,
                            final int startRow, final int endRow,
                            final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int i = startRow; i <= endRow; ++i) {
            final T[] rowI = data[i];
            for (int j = startColumn; j <= endColumn; ++j) {
                visitor.visit(i, j, rowI[j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int j = 0; j < columns; ++j) {
            for (int i = 0; i < rows; ++i) {
                final T[] rowI = data[i];
                rowI[j] = visitor.visit(i, j, rowI[j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int j = 0; j < columns; ++j) {
            for (int i = 0; i < rows; ++i) {
                visitor.visit(i, j, data[i][j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor,
                               final int startRow, final int endRow,
                               final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int j = startColumn; j <= endColumn; ++j) {
            for (int i = startRow; i <= endRow; ++i) {
                final T[] rowI = data[i];
                rowI[j] = visitor.visit(i, j, rowI[j]);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    @Override
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor,
                               final int startRow, final int endRow,
                               final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int j = startColumn; j <= endColumn; ++j) {
            for (int i = startRow; i <= endRow; ++i) {
                visitor.visit(i, j, data[i][j]);
            }
        }
        return visitor.end();
    }

    /**
     * Returns a fresh copy of the underlying data array.
     *
     * @return a copy of the underlying data array.
     */
    private T[][] copyOut() {
        final int nRows = this.getRowDimension();
        final T[][] out = buildArray(getField(), nRows, getColumnDimension());
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
     * @throws IllegalArgumentException if input array is empty or not
     *    rectangular
     * @throws NullPointerException if input array is null
     */
    private void copyIn(final T[][] in) {
        setSubMatrix(in, 0, 0);
    }

}

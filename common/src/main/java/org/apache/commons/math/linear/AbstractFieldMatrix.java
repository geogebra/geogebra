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
import java.util.Arrays;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Basic implementation of {@link FieldMatrix} methods regardless of the underlying storage.
 * <p>All the methods implemented here use {@link #getEntry(int, int)} to access
 * matrix elements. Derived class can provide faster implementations. </p>
 *
 * @param <T> the type of the field elements
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.0
 */
public abstract class AbstractFieldMatrix<T extends FieldElement<T>> implements FieldMatrix<T> {

    /** Field to which the elements belong. */
    private final Field<T> field;

    /**
     * Constructor for use with Serializable
     */
    protected AbstractFieldMatrix() {
        field = null;
    }

    /**
     * Creates a matrix with no data
     * @param field field to which the elements belong
     */
    protected AbstractFieldMatrix(final Field<T> field) {
        this.field = field;
    }

    /**
     * Create a new FieldMatrix<T> with the supplied row and column dimensions.
     *
     * @param field field to which the elements belong
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not positive
     */
    protected AbstractFieldMatrix(final Field<T> field,
                                  final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        if (rowDimension < 1 ) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, rowDimension, 1);
        }
        if (columnDimension < 1) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, columnDimension, 1);
        }
        this.field = field;
    }

    /**
     * Get the elements type from an array.
     * @param <T> the type of the field elements
     * @param d data array
     * @return field to which array elements belong
     * @exception IllegalArgumentException if array is empty
     */
    protected static <T extends FieldElement<T>> Field<T> extractField(final T[][] d)
        throws IllegalArgumentException {
        if (d.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        if (d[0].length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        return d[0][0].getField();
    }

    /**
     * Get the elements type from an array.
     * @param <T> the type of the field elements
     * @param d data array
     * @return field to which array elements belong
     * @exception IllegalArgumentException if array is empty
     */
    protected static <T extends FieldElement<T>> Field<T> extractField(final T[] d)
        throws IllegalArgumentException {
        if (d.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }
        return d[0].getField();
    }

    /** Build an array of elements.
     * <p>
     * Complete arrays are filled with field.getZero()
     * </p>
     * @param <T> the type of the field elements
     * @param field field to which array elements belong
     * @param rows number of rows
     * @param columns number of columns (may be negative to build partial
     * arrays in the same way <code>new Field[rows][]</code> works)
     * @return a new array
     */
    @SuppressWarnings("unchecked")
    protected static <T extends FieldElement<T>> T[][] buildArray(final Field<T> field,
                                                                  final int rows,
                                                                  final int columns) {
        if (columns < 0) {
            T[] dummyRow = (T[]) Array.newInstance(field.getZero().getClass(), 0);
            return (T[][]) Array.newInstance(dummyRow.getClass(), rows);
        }
        T[][] array =
            (T[][]) Array.newInstance(field.getZero().getClass(), new int[] { rows, columns });
        for (int i = 0; i < array.length; ++i) {
            Arrays.fill(array[i], field.getZero());
        }
        return array;
    }

    /** Build an array of elements.
     * <p>
     * Arrays are filled with field.getZero()
     * </p>
     * @param <T> the type of the field elements
     * @param field field to which array elements belong
     * @param length of the array
     * @return a new array
     */
    protected static <T extends FieldElement<T>> T[] buildArray(final Field<T> field,
                                                                final int length) {
        @SuppressWarnings("unchecked") // OK because field must be correct class
        T[] array = (T[]) Array.newInstance(field.getZero().getClass(), length);
        Arrays.fill(array, field.getZero());
        return array;
    }

    /** {@inheritDoc} */
    public Field<T> getField() {
        return field;
    }

    /** {@inheritDoc} */
    public abstract FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException;

    /** {@inheritDoc} */
    public abstract FieldMatrix<T> copy();

    /** {@inheritDoc} */
    public FieldMatrix<T> add(FieldMatrix<T> m) throws IllegalArgumentException {

        // safety check
        checkAdditionCompatible(m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).add(m.getEntry(row, col)));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> subtract(final FieldMatrix<T> m) throws IllegalArgumentException {

        // safety check
        checkSubtractionCompatible(m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).subtract(m.getEntry(row, col)));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> scalarAdd(final T d) {

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).add(d));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> scalarMultiply(final T d) {

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).multiply(d));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> multiply(final FieldMatrix<T> m)
        throws IllegalArgumentException {

        // safety check
        checkMultiplicationCompatible(m);

        final int nRows = getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum  = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                T sum = field.getZero();
                for (int i = 0; i < nSum; ++i) {
                    sum = sum.add(getEntry(row, i).multiply(m.getEntry(i, col)));
                }
                out.setEntry(row, col, sum);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> preMultiply(final FieldMatrix<T> m)
        throws IllegalArgumentException {
        return m.multiply(this);
    }

    /** {@inheritDoc} */
    public T[][] getData() {

        final T[][] data = buildArray(field, getRowDimension(), getColumnDimension());

        for (int i = 0; i < data.length; ++i) {
            final T[] dataI = data[i];
            for (int j = 0; j < dataI.length; ++j) {
                dataI[j] = getEntry(i, j);
            }
        }

        return data;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getSubMatrix(final int startRow, final int endRow,
                                   final int startColumn, final int endColumn)
        throws MatrixIndexException {

        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);

        final FieldMatrix<T> subMatrix =
            createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = startColumn; j <= endColumn; ++j) {
                subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
            }
        }

        return subMatrix;

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getSubMatrix(final int[] selectedRows, final int[] selectedColumns)
        throws MatrixIndexException {

        // safety checks
        checkSubMatrixIndex(selectedRows, selectedColumns);

        // copy entries
        final FieldMatrix<T> subMatrix =
            createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor<T>(field.getZero()) {

            /** {@inheritDoc} */
            @Override
            public T visit(final int row, final int column, final T value) {
                return getEntry(selectedRows[row], selectedColumns[column]);
            }

        });

        return subMatrix;

    }

    /** {@inheritDoc} */
    public void copySubMatrix(final int startRow, final int endRow,
                              final int startColumn, final int endColumn,
                              final T[][] destination)
        throws MatrixIndexException, IllegalArgumentException {

        // safety checks
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        final int rowsCount    = endRow + 1 - startRow;
        final int columnsCount = endColumn + 1 - startColumn;
        if ((destination.length < rowsCount) || (destination[0].length < columnsCount)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    destination.length, destination[0].length,
                    rowsCount, columnsCount);
        }

        // copy entries
        walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>(field.getZero()) {

            /** Initial row index. */
            private int startRow1;

            /** Initial column index. */
            private int startColumn1;

            /** {@inheritDoc} */
            @Override
            public void start(final int rows, final int columns,
                              final int startRow, final int endRow,
                              final int startColumn, final int endColumn) {
                this.startRow1    = startRow;
                this.startColumn1 = startColumn;
            }

            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final T value) {
                destination[row - startRow1][column - startColumn1] = value;
            }

        }, startRow, endRow, startColumn, endColumn);

    }

    /** {@inheritDoc} */
    public void copySubMatrix(int[] selectedRows, int[] selectedColumns, T[][] destination)
        throws MatrixIndexException, IllegalArgumentException {

        // safety checks
        checkSubMatrixIndex(selectedRows, selectedColumns);
        if ((destination.length < selectedRows.length) ||
            (destination[0].length < selectedColumns.length)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    destination.length, destination[0].length,
                    selectedRows.length, selectedColumns.length);
        }

        // copy entries
        for (int i = 0; i < selectedRows.length; i++) {
            final T[] destinationI = destination[i];
            for (int j = 0; j < selectedColumns.length; j++) {
                destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
            }
        }

    }

    /** {@inheritDoc} */
    public void setSubMatrix(final T[][] subMatrix, final int row, final int column)
        throws MatrixIndexException {

        final int nRows = subMatrix.length;
        if (nRows == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW);
        }

        final int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }

        for (int r = 1; r < nRows; ++r) {
            if (subMatrix[r].length != nCols) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                        nCols, subMatrix[r].length);
            }
        }

        checkRowIndex(row);
        checkColumnIndex(column);
        checkRowIndex(nRows + row - 1);
        checkColumnIndex(nCols + column - 1);

        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                setEntry(row + i, column + j, subMatrix[i][j]);
            }
        }

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getRowMatrix(final int row)
        throws MatrixIndexException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(0, i, getEntry(row, i));
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setRowMatrix(final int row, final FieldMatrix<T> matrix)
        throws MatrixIndexException, InvalidMatrixException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if ((matrix.getRowDimension() != 1) ||
            (matrix.getColumnDimension() != nCols)) {
            throw new InvalidMatrixException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            setEntry(row, i, matrix.getEntry(0, i));
        }

    }

    /** {@inheritDoc} */
    public FieldMatrix<T> getColumnMatrix(final int column)
        throws MatrixIndexException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        final FieldMatrix<T> out = createMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, 0, getEntry(i, column));
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setColumnMatrix(final int column, final FieldMatrix<T> matrix)
        throws MatrixIndexException, InvalidMatrixException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if ((matrix.getRowDimension() != nRows) ||
            (matrix.getColumnDimension() != 1)) {
            throw new InvalidMatrixException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            setEntry(i, column, matrix.getEntry(i, 0));
        }

    }

    /** {@inheritDoc} */
    public FieldVector<T> getRowVector(final int row)
        throws MatrixIndexException {
        return new ArrayFieldVector<T>(getRow(row), false);
    }

    /** {@inheritDoc} */
    public void setRowVector(final int row, final FieldVector<T> vector)
        throws MatrixIndexException, InvalidMatrixException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if (vector.getDimension() != nCols) {
            throw new InvalidMatrixException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    1, vector.getDimension(), 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            setEntry(row, i, vector.getEntry(i));
        }

    }

    /** {@inheritDoc} */
    public FieldVector<T> getColumnVector(final int column)
        throws MatrixIndexException {
        return new ArrayFieldVector<T>(getColumn(column), false);
    }

    /** {@inheritDoc} */
    public void setColumnVector(final int column, final FieldVector<T> vector)
        throws MatrixIndexException, InvalidMatrixException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if (vector.getDimension() != nRows) {
            throw new InvalidMatrixException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    vector.getDimension(), 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            setEntry(i, column, vector.getEntry(i));
        }

    }

    /** {@inheritDoc} */
    public T[] getRow(final int row)
        throws MatrixIndexException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        final T[] out = buildArray(field, nCols);
        for (int i = 0; i < nCols; ++i) {
            out[i] = getEntry(row, i);
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setRow(final int row, final T[] array)
        throws MatrixIndexException, InvalidMatrixException {

        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if (array.length != nCols) {
            throw new InvalidMatrixException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    1, array.length, 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            setEntry(row, i, array[i]);
        }

    }

    /** {@inheritDoc} */
    public T[] getColumn(final int column)
        throws MatrixIndexException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        final T[] out = buildArray(field, nRows);
        for (int i = 0; i < nRows; ++i) {
            out[i] = getEntry(i, column);
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setColumn(final int column, final T[] array)
        throws MatrixIndexException, InvalidMatrixException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if (array.length != nRows) {
            throw new InvalidMatrixException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    array.length, 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            setEntry(i, column, array[i]);
        }

    }

    /** {@inheritDoc} */
    public abstract T getEntry(int row, int column)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void setEntry(int row, int column, T value)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void addToEntry(int row, int column, T increment)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void multiplyEntry(int row, int column, T factor)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public FieldMatrix<T> transpose() {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(nCols, nRows);
        walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>(field.getZero()) {

            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final T value) {
                out.setEntry(column, row, value);
            }

        });

        return out;

    }

    /** {@inheritDoc} */
    public boolean isSquare() {
        return getColumnDimension() == getRowDimension();
    }

    /** {@inheritDoc} */
    public abstract int getRowDimension();

    /** {@inheritDoc} */
    public abstract int getColumnDimension();

    /** {@inheritDoc} */
    public T getTrace()
        throws NonSquareMatrixException {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
       }
        T trace = field.getZero();
        for (int i = 0; i < nRows; ++i) {
            trace = trace.add(getEntry(i, i));
        }
        return trace;
    }

    /** {@inheritDoc} */
    public T[] operate(final T[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nCols) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    v.length, nCols);
        }

        final T[] out = buildArray(field, nRows);
        for (int row = 0; row < nRows; ++row) {
            T sum = field.getZero();
            for (int i = 0; i < nCols; ++i) {
                sum = sum.add(getEntry(row, i).multiply(v[i]));
            }
            out[row] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldVector<T> operate(final FieldVector<T> v)
        throws IllegalArgumentException {
        try {
            return new ArrayFieldVector<T>(operate(((ArrayFieldVector<T>) v).getDataRef()), false);
        } catch (ClassCastException cce) {
            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.getDimension() != nCols) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                        v.getDimension(), nCols);
            }

            final T[] out = buildArray(field, nRows);
            for (int row = 0; row < nRows; ++row) {
                T sum = field.getZero();
                for (int i = 0; i < nCols; ++i) {
                    sum = sum.add(getEntry(row, i).multiply(v.getEntry(i)));
                }
                out[row] = sum;
            }

            return new ArrayFieldVector<T>(out, false);
        }
    }

    /** {@inheritDoc} */
    public T[] preMultiply(final T[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    v.length, nRows);
        }

        final T[] out = buildArray(field, nCols);
        for (int col = 0; col < nCols; ++col) {
            T sum = field.getZero();
            for (int i = 0; i < nRows; ++i) {
                sum = sum.add(getEntry(i, col).multiply(v[i]));
            }
            out[col] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    public FieldVector<T> preMultiply(final FieldVector<T> v)
        throws IllegalArgumentException {
        try {
            return new ArrayFieldVector<T>(preMultiply(((ArrayFieldVector<T>) v).getDataRef()), false);
        } catch (ClassCastException cce) {

            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.getDimension() != nRows) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                        v.getDimension(), nRows);
            }

            final T[] out = buildArray(field, nCols);
            for (int col = 0; col < nCols; ++col) {
                T sum = field.getZero();
                for (int i = 0; i < nRows; ++i) {
                    sum = sum.add(getEntry(i, col).multiply(v.getEntry(i)));
                }
                out[col] = sum;
            }

            return new ArrayFieldVector<T>(out);

        }
    }

    /** {@inheritDoc} */
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor,
                            final int startRow, final int endRow,
                            final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor,
                               final int startRow, final int endRow,
                               final int startColumn, final int endColumn)
    throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor,
                               final int startRow, final int endRow,
                               final int startColumn, final int endColumn)
    throws MatrixIndexException, MatrixVisitorException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    /** {@inheritDoc} */
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException {
        return walkInRowOrder(visitor);
    }

    /** {@inheritDoc} */
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException {
        return walkInRowOrder(visitor);
    }

    /** {@inheritDoc} */
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor,
                                       final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    /** {@inheritDoc} */
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor,
                                       final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    /**
     * Get a string representation for this matrix.
     * @return a string representation for this matrix
     */
    @Override
    public String toString() {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final StringBuilder res = new StringBuilder();
        String fullClassName = getClass().getName();
        String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        res.append(shortClassName).append("{");

        for (int i = 0; i < nRows; ++i) {
            if (i > 0) {
                res.append(",");
            }
            res.append("{");
            for (int j = 0; j < nCols; ++j) {
                if (j > 0) {
                    res.append(",");
                }
                res.append(getEntry(i, j));
            }
            res.append("}");
        }

        res.append("}");
        return res.toString();

    }

    /**
     * Returns true iff <code>object</code> is a
     * <code>FieldMatrix</code> instance with the same dimensions as this
     * and all corresponding matrix entries are equal.
     *
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this ) {
            return true;
        }
        if (object instanceof FieldMatrix<?> == false) {
            return false;
        }
        FieldMatrix<?> m = (FieldMatrix<?>) object;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                if (!getEntry(row, col).equals(m.getEntry(row, col))) {
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
        int ret = 322562;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        ret = ret * 31 + nRows;
        ret = ret * 31 + nCols;
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
               ret = ret * 31 + (11 * (row+1) + 17 * (col+1)) * getEntry(row, col).hashCode();
           }
        }
        return ret;
    }

    /**
     * Check if a row index is valid.
     * @param row row index to check
     * @exception MatrixIndexException if index is not valid
     */
    protected void checkRowIndex(final int row) {
        if (row < 0 || row >= getRowDimension()) {
            throw new MatrixIndexException(LocalizedFormats.ROW_INDEX_OUT_OF_RANGE,
                                           row, 0, getRowDimension() - 1);
        }
    }

    /**
     * Check if a column index is valid.
     * @param column column index to check
     * @exception MatrixIndexException if index is not valid
     */
    protected void checkColumnIndex(final int column)
        throws MatrixIndexException {
        if (column < 0 || column >= getColumnDimension()) {
            throw new MatrixIndexException(LocalizedFormats.COLUMN_INDEX_OUT_OF_RANGE,
                                           column, 0, getColumnDimension() - 1);
        }
    }

    /**
     * Check if submatrix ranges indices are valid.
     * Rows and columns are indicated counting from 0 to n-1.
     *
     * @param startRow Initial row index
     * @param endRow Final row index
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @exception MatrixIndexException  if the indices are not valid
     */
    protected void checkSubMatrixIndex(final int startRow, final int endRow,
                                       final int startColumn, final int endColumn) {
        checkRowIndex(startRow);
        checkRowIndex(endRow);
        if (startRow > endRow) {
            throw new MatrixIndexException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW,
                                           startRow, endRow);
        }

        checkColumnIndex(startColumn);
        checkColumnIndex(endColumn);
        if (startColumn > endColumn) {
            throw new MatrixIndexException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN,
                                           startColumn, endColumn);
        }


    }

    /**
     * Check if submatrix ranges indices are valid.
     * Rows and columns are indicated counting from 0 to n-1.
     *
     * @param selectedRows Array of row indices.
     * @param selectedColumns Array of column indices.
     * @exception MatrixIndexException if row or column selections are not valid
     */
    protected void checkSubMatrixIndex(final int[] selectedRows, final int[] selectedColumns) {
        if (selectedRows.length * selectedColumns.length == 0) {
            if (selectedRows.length == 0) {
                throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY);
            }
            throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY);
        }

        for (final int row : selectedRows) {
            checkRowIndex(row);
        }
        for (final int column : selectedColumns) {
            checkColumnIndex(column);
        }
    }

    /**
     * Check if a matrix is addition compatible with the instance
     * @param m matrix to check
     * @exception IllegalArgumentException if matrix is not addition compatible with instance
     */
    protected void checkAdditionCompatible(final FieldMatrix<T> m) {
        if ((getRowDimension()    != m.getRowDimension()) ||
            (getColumnDimension() != m.getColumnDimension())) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_ADDITION_COMPATIBLE_MATRICES,
                    getRowDimension(), getColumnDimension(),
                    m.getRowDimension(), m.getColumnDimension());
        }
    }

    /**
     * Check if a matrix is subtraction compatible with the instance
     * @param m matrix to check
     * @exception IllegalArgumentException if matrix is not subtraction compatible with instance
     */
    protected void checkSubtractionCompatible(final FieldMatrix<T> m) {
        if ((getRowDimension()    != m.getRowDimension()) ||
            (getColumnDimension() != m.getColumnDimension())) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_SUBTRACTION_COMPATIBLE_MATRICES,
                    getRowDimension(), getColumnDimension(),
                    m.getRowDimension(), m.getColumnDimension());
        }
    }

    /**
     * Check if a matrix is multiplication compatible with the instance
     * @param m matrix to check
     * @exception IllegalArgumentException if matrix is not multiplication compatible with instance
     */
    protected void checkMultiplicationCompatible(final FieldMatrix<T> m) {
        if (getColumnDimension() != m.getRowDimension()) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_MULTIPLICATION_COMPATIBLE_MATRICES,
                    getRowDimension(), getColumnDimension(),
                    m.getRowDimension(), m.getColumnDimension());
        }
    }

}

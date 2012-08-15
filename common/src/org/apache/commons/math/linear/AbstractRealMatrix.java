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
import org.apache.commons.math.util.MathUtils;

/**
 * Basic implementation of RealMatrix methods regardless of the underlying storage.
 * <p>All the methods implemented here use {@link #getEntry(int, int)} to access
 * matrix elements. Derived class can provide faster implementations. </p>
 *
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 * @since 2.0
 */
public abstract class AbstractRealMatrix implements RealMatrix {


    /** Cached LU solver.
     * @deprecated as of release 2.0, since all methods using this are deprecated
     */
    @Deprecated
    private DecompositionSolver lu;

    /**
     * Creates a matrix with no data
     */
    protected AbstractRealMatrix() {
        lu = null;
    }

    /**
     * Create a new RealMatrix with the supplied row and column dimensions.
     *
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not positive
     */
    protected AbstractRealMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        if (rowDimension < 1 ) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, rowDimension, 1);
        }
        if (columnDimension <= 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INSUFFICIENT_DIMENSION, columnDimension, 1);
        }
        lu = null;
    }

    /** {@inheritDoc} */
    public abstract RealMatrix createMatrix(final int rowDimension, final int columnDimension)
        throws IllegalArgumentException;

    /** {@inheritDoc} */
    public abstract RealMatrix copy();

    /** {@inheritDoc} */
    public RealMatrix add(RealMatrix m) throws IllegalArgumentException {

        // safety check
        MatrixUtils.checkAdditionCompatible(this, m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) + m.getEntry(row, col));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix subtract(final RealMatrix m) throws IllegalArgumentException {

        // safety check
        MatrixUtils.checkSubtractionCompatible(this, m);

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) - m.getEntry(row, col));
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarAdd(final double d) {

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) + d);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix scalarMultiply(final double d) {

        final int rowCount    = getRowDimension();
        final int columnCount = getColumnDimension();
        final RealMatrix out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col) * d);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix multiply(final RealMatrix m)
        throws IllegalArgumentException {

        // safety check
        MatrixUtils.checkMultiplicationCompatible(this, m);

        final int nRows = getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum  = getColumnDimension();
        final RealMatrix out = createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                double sum = 0;
                for (int i = 0; i < nSum; ++i) {
                    sum += getEntry(row, i) * m.getEntry(i, col);
                }
                out.setEntry(row, col, sum);
            }
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealMatrix preMultiply(final RealMatrix m)
        throws IllegalArgumentException {
        return m.multiply(this);
    }

    /** {@inheritDoc} */
    public double[][] getData() {

        final double[][] data = new double[getRowDimension()][getColumnDimension()];

        for (int i = 0; i < data.length; ++i) {
            final double[] dataI = data[i];
            for (int j = 0; j < dataI.length; ++j) {
                dataI[j] = getEntry(i, j);
            }
        }

        return data;

    }

    /** {@inheritDoc} */
    public double getNorm() {
        return walkInColumnOrder(new RealMatrixPreservingVisitor() {

            /** Last row index. */
            private double endRow;

            /** Sum of absolute values on one column. */
            private double columnSum;

            /** Maximal sum across all columns. */
            private double maxColSum;

            /** {@inheritDoc} */
            public void start(final int rows, final int columns,
                              final int startRow, final int endRow,
                              final int startColumn, final int endColumn) {
                this.endRow = endRow;
                columnSum   = 0;
                maxColSum   = 0;
            }

            /** {@inheritDoc} */
            public void visit(final int row, final int column, final double value) {
                columnSum += FastMath.abs(value);
                if (row == endRow) {
                    maxColSum = FastMath.max(maxColSum, columnSum);
                    columnSum = 0;
                }
            }

            /** {@inheritDoc} */
            public double end() {
                return maxColSum;
            }

        });
    }

    /** {@inheritDoc} */
    public double getFrobeniusNorm() {
        return walkInOptimizedOrder(new RealMatrixPreservingVisitor() {

            /** Sum of squared entries. */
            private double sum;

            /** {@inheritDoc} */
            public void start(final int rows, final int columns,
                              final int startRow, final int endRow,
                              final int startColumn, final int endColumn) {
                sum = 0;
            }

            /** {@inheritDoc} */
            public void visit(final int row, final int column, final double value) {
                sum += value * value;
            }

            /** {@inheritDoc} */
            public double end() {
                return FastMath.sqrt(sum);
            }

        });
    }

    /** {@inheritDoc} */
    public RealMatrix getSubMatrix(final int startRow, final int endRow,
                                   final int startColumn, final int endColumn)
        throws MatrixIndexException {

        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);

        final RealMatrix subMatrix =
            createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = startColumn; j <= endColumn; ++j) {
                subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
            }
        }

        return subMatrix;

    }

    /** {@inheritDoc} */
    public RealMatrix getSubMatrix(final int[] selectedRows, final int[] selectedColumns)
        throws MatrixIndexException {

        // safety checks
        MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);

        // copy entries
        final RealMatrix subMatrix =
            createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor() {

            /** {@inheritDoc} */
            @Override
            public double visit(final int row, final int column, final double value) {
                return getEntry(selectedRows[row], selectedColumns[column]);
            }

        });

        return subMatrix;

    }

    /** {@inheritDoc} */
    public void copySubMatrix(final int startRow, final int endRow,
                              final int startColumn, final int endColumn,
                              final double[][] destination)
        throws MatrixIndexException, IllegalArgumentException {

        // safety checks
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        final int rowsCount    = endRow + 1 - startRow;
        final int columnsCount = endColumn + 1 - startColumn;
        if ((destination.length < rowsCount) || (destination[0].length < columnsCount)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    destination.length, destination[0].length,
                    rowsCount, columnsCount);
        }

        // copy entries
        walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {

            /** Initial row index. */
            private int startRow;

            /** Initial column index. */
            private int startColumn;

            /** {@inheritDoc} */
            @Override
            public void start(final int rows, final int columns,
                              final int startRow, final int endRow,
                              final int startColumn, final int endColumn) {
                this.startRow    = startRow;
                this.startColumn = startColumn;
            }

            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final double value) {
                destination[row - startRow][column - startColumn] = value;
            }

        }, startRow, endRow, startColumn, endColumn);

    }

    /** {@inheritDoc} */
    public void copySubMatrix(int[] selectedRows, int[] selectedColumns, double[][] destination)
        throws MatrixIndexException, IllegalArgumentException {

        // safety checks
        MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
        if ((destination.length < selectedRows.length) ||
            (destination[0].length < selectedColumns.length)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.DIMENSIONS_MISMATCH_2x2,
                    destination.length, destination[0].length,
                    selectedRows.length, selectedColumns.length);
        }

        // copy entries
        for (int i = 0; i < selectedRows.length; i++) {
            final double[] destinationI = destination[i];
            for (int j = 0; j < selectedColumns.length; j++) {
                destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
            }
        }

    }

    /** {@inheritDoc} */
    public void setSubMatrix(final double[][] subMatrix, final int row, final int column)
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

        MatrixUtils.checkRowIndex(this, row);
        MatrixUtils.checkColumnIndex(this, column);
        MatrixUtils.checkRowIndex(this, nRows + row - 1);
        MatrixUtils.checkColumnIndex(this, nCols + column - 1);

        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                setEntry(row + i, column + j, subMatrix[i][j]);
            }
        }

        lu = null;

    }

    /** {@inheritDoc} */
    public RealMatrix getRowMatrix(final int row)
        throws MatrixIndexException {

        MatrixUtils.checkRowIndex(this, row);
        final int nCols = getColumnDimension();
        final RealMatrix out = createMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(0, i, getEntry(row, i));
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setRowMatrix(final int row, final RealMatrix matrix)
        throws MatrixIndexException, InvalidMatrixException {

        MatrixUtils.checkRowIndex(this, row);
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
    public RealMatrix getColumnMatrix(final int column)
        throws MatrixIndexException {

        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = getRowDimension();
        final RealMatrix out = createMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, 0, getEntry(i, column));
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setColumnMatrix(final int column, final RealMatrix matrix)
        throws MatrixIndexException, InvalidMatrixException {

        MatrixUtils.checkColumnIndex(this, column);
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
    public RealVector getRowVector(final int row)
        throws MatrixIndexException {
        return new ArrayRealVector(getRow(row), false);
    }

    /** {@inheritDoc} */
    public void setRowVector(final int row, final RealVector vector)
        throws MatrixIndexException, InvalidMatrixException {

        MatrixUtils.checkRowIndex(this, row);
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
    public RealVector getColumnVector(final int column)
        throws MatrixIndexException {
        return new ArrayRealVector(getColumn(column), false);
    }

    /** {@inheritDoc} */
    public void setColumnVector(final int column, final RealVector vector)
        throws MatrixIndexException, InvalidMatrixException {

        MatrixUtils.checkColumnIndex(this, column);
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
    public double[] getRow(final int row)
        throws MatrixIndexException {

        MatrixUtils.checkRowIndex(this, row);
        final int nCols = getColumnDimension();
        final double[] out = new double[nCols];
        for (int i = 0; i < nCols; ++i) {
            out[i] = getEntry(row, i);
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setRow(final int row, final double[] array)
        throws MatrixIndexException, InvalidMatrixException {

        MatrixUtils.checkRowIndex(this, row);
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
    public double[] getColumn(final int column)
        throws MatrixIndexException {

        MatrixUtils.checkColumnIndex(this, column);
        final int nRows = getRowDimension();
        final double[] out = new double[nRows];
        for (int i = 0; i < nRows; ++i) {
            out[i] = getEntry(i, column);
        }

        return out;

    }

    /** {@inheritDoc} */
    public void setColumn(final int column, final double[] array)
        throws MatrixIndexException, InvalidMatrixException {

        MatrixUtils.checkColumnIndex(this, column);
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
    public abstract double getEntry(int row, int column)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void setEntry(int row, int column, double value)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void addToEntry(int row, int column, double increment)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public abstract void multiplyEntry(int row, int column, double factor)
        throws MatrixIndexException;

    /** {@inheritDoc} */
    public RealMatrix transpose() {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final RealMatrix out = createMatrix(nCols, nRows);
        walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor() {

            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final double value) {
                out.setEntry(column, row, value);
            }

        });

        return out;

    }

    /** {@inheritDoc} */
    @Deprecated
    public RealMatrix inverse()
        throws InvalidMatrixException {
        if (lu == null) {
            lu = new LUDecompositionImpl(this, MathUtils.SAFE_MIN).getSolver();
        }
        return lu.getInverse();
    }

    /** {@inheritDoc} */
    @Deprecated
    public double getDeterminant()
        throws InvalidMatrixException {
        return new LUDecompositionImpl(this, MathUtils.SAFE_MIN).getDeterminant();
    }

    /** {@inheritDoc} */
    public boolean isSquare() {
        return getColumnDimension() == getRowDimension();
    }

    /** {@inheritDoc} */
    @Deprecated
    public boolean isSingular() {
        if (lu == null) {
            lu = new LUDecompositionImpl(this, MathUtils.SAFE_MIN).getSolver();
       }
        return !lu.isNonSingular();
    }

    /** {@inheritDoc} */
    public abstract int getRowDimension();

    /** {@inheritDoc} */
    public abstract int getColumnDimension();

    /** {@inheritDoc} */
    public double getTrace()
        throws NonSquareMatrixException {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (nRows != nCols) {
            throw new NonSquareMatrixException(nRows, nCols);
       }
        double trace = 0;
        for (int i = 0; i < nRows; ++i) {
            trace += getEntry(i, i);
        }
        return trace;
    }

    /** {@inheritDoc} */
    public double[] operate(final double[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nCols) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    v.length, nCols);
        }

        final double[] out = new double[nRows];
        for (int row = 0; row < nRows; ++row) {
            double sum = 0;
            for (int i = 0; i < nCols; ++i) {
                sum += getEntry(row, i) * v[i];
            }
            out[row] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealVector operate(final RealVector v)
        throws IllegalArgumentException {
        try {
            return new ArrayRealVector(operate(((ArrayRealVector) v).getDataRef()), false);
        } catch (ClassCastException cce) {
            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.getDimension() != nCols) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                        v.getDimension(), nCols);
            }

            final double[] out = new double[nRows];
            for (int row = 0; row < nRows; ++row) {
                double sum = 0;
                for (int i = 0; i < nCols; ++i) {
                    sum += getEntry(row, i) * v.getEntry(i);
                }
                out[row] = sum;
            }

            return new ArrayRealVector(out, false);
        }
    }

    /** {@inheritDoc} */
    public double[] preMultiply(final double[] v)
        throws IllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    v.length, nRows);
        }

        final double[] out = new double[nCols];
        for (int col = 0; col < nCols; ++col) {
            double sum = 0;
            for (int i = 0; i < nRows; ++i) {
                sum += getEntry(i, col) * v[i];
            }
            out[col] = sum;
        }

        return out;

    }

    /** {@inheritDoc} */
    public RealVector preMultiply(final RealVector v)
        throws IllegalArgumentException {
        try {
            return new ArrayRealVector(preMultiply(((ArrayRealVector) v).getDataRef()), false);
        } catch (ClassCastException cce) {

            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.getDimension() != nRows) {
                throw MathRuntimeException.createIllegalArgumentException(
                        LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                        v.getDimension(), nRows);
            }

            final double[] out = new double[nCols];
            for (int col = 0; col < nCols; ++col) {
                double sum = 0;
                for (int i = 0; i < nRows; ++i) {
                    sum += getEntry(i, col) * v.getEntry(i);
                }
                out[col] = sum;
            }

            return new ArrayRealVector(out);

        }
    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                final double oldValue = getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        lu = null;
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor)
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
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                final double oldValue = getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        lu = null;
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor,
                                 final int startRow, final int endRow,
                                 final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
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
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor)
        throws MatrixVisitorException {
        final int rows    = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                final double oldValue = getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        lu = null;
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor)
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
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor,
                                    final int startRow, final int endRow,
                                    final int startColumn, final int endColumn)
    throws MatrixIndexException, MatrixVisitorException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                      startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                final double oldValue = getEntry(row, column);
                final double newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        lu = null;
        return visitor.end();
    }

    /** {@inheritDoc} */
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor,
                                    final int startRow, final int endRow,
                                    final int startColumn, final int endColumn)
    throws MatrixIndexException, MatrixVisitorException {
        MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
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
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor)
        throws MatrixVisitorException {
        return walkInRowOrder(visitor);
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor)
        throws MatrixVisitorException {
        return walkInRowOrder(visitor);
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixChangingVisitor visitor,
                                       final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    /** {@inheritDoc} */
    public double walkInOptimizedOrder(final RealMatrixPreservingVisitor visitor,
                                       final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
        throws MatrixIndexException, MatrixVisitorException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    /** {@inheritDoc} */
    @Deprecated
    public double[] solve(final double[] b)
        throws IllegalArgumentException, InvalidMatrixException {
        if (lu == null) {
            lu = new LUDecompositionImpl(this, MathUtils.SAFE_MIN).getSolver();
        }
        return lu.solve(b);
    }

    /** {@inheritDoc} */
    @Deprecated
    public RealMatrix solve(final RealMatrix b)
        throws IllegalArgumentException, InvalidMatrixException  {
        if (lu == null) {
            lu = new LUDecompositionImpl(this, MathUtils.SAFE_MIN).getSolver();
        }
        return lu.solve(b);
    }

    /**
     * Computes a new
     * <a href="http://www.math.gatech.edu/~bourbaki/math2601/Web-notes/2num.pdf">
     * LU decomposition</a> for this matrix, storing the result for use by other methods.
     * <p>
     * <strong>Implementation Note</strong>:<br>
     * Uses <a href="http://www.damtp.cam.ac.uk/user/fdl/people/sd/lectures/nummeth98/linear.htm">
     * Crout's algorithm</a>, with partial pivoting.</p>
     * <p>
     * <strong>Usage Note</strong>:<br>
     * This method should rarely be invoked directly. Its only use is
     * to force recomputation of the LU decomposition when changes have been
     * made to the underlying data using direct array references. Changes
     * made using setXxx methods will trigger recomputation when needed
     * automatically.</p>
     *
     * @throws InvalidMatrixException if the matrix is non-square or singular.
     * @deprecated as of release 2.0, replaced by {@link LUDecomposition}
     */
    @Deprecated
    public void luDecompose()
        throws InvalidMatrixException {
        if (lu == null) {
            lu = new LUDecompositionImpl(this, MathUtils.SAFE_MIN).getSolver();
        }
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
     * <code>RealMatrix</code> instance with the same dimensions as this
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
        if (object instanceof RealMatrix == false) {
            return false;
        }
        RealMatrix m = (RealMatrix) object;
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
            return false;
        }
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                if (getEntry(row, col) != m.getEntry(row, col)) {
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
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
               ret = ret * 31 + (11 * (row+1) + 17 * (col+1)) *
                   MathUtils.hash(getEntry(row, col));
           }
        }
        return ret;
    }

}

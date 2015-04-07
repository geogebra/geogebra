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

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.util.OpenIntToFieldHashMap;

/**
 * Sparse matrix implementation based on an open addressed map.
 *
 * @param <T> the type of the field elements
 * @version $Revision: 811685 $ $Date: 2009-09-05 19:36:48 +0200 (sam. 05 sept. 2009) $
 * @since 2.0
 */
public class SparseFieldMatrix<T extends FieldElement<T>> extends AbstractFieldMatrix<T> {
    /**
     *  Serial id
     */
    private static final long serialVersionUID = 9078068119297757342L;
    /** Storage for (sparse) matrix elements. */
    private final OpenIntToFieldHashMap<T> entries;
    /**
     * row dimension
     */
    private final int rows;
    /**
     * column dimension
     */
    private final int columns;


    /**
     * Creates a matrix with no data.
     * @param field field to which the elements belong
     */
    public SparseFieldMatrix(final Field<T> field) {
        super(field);
        rows = 0;
        columns= 0;
        entries = new OpenIntToFieldHashMap<T>(field);
    }

    /**
     * Create a new SparseFieldMatrix<T> with the supplied row and column dimensions.
     *
     * @param field field to which the elements belong
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @throws IllegalArgumentException if row or column dimension is not positive
     */
    public SparseFieldMatrix(final Field<T> field,
                             final int rowDimension, final int columnDimension)
        throws IllegalArgumentException {
        super(field, rowDimension, columnDimension);
        this.rows = rowDimension;
        this.columns = columnDimension;
        entries = new OpenIntToFieldHashMap<T>(field);
    }

    /**
     * Copy constructor.
     * @param other The instance to copy
     */
    public SparseFieldMatrix(SparseFieldMatrix<T> other) {
        super(other.getField(), other.getRowDimension(), other.getColumnDimension());
        rows = other.getRowDimension();
        columns = other.getColumnDimension();
        entries = new OpenIntToFieldHashMap<T>(other.entries);
    }

    /**
     * Generic copy constructor.
     * @param other The instance to copy
     */
    public SparseFieldMatrix(FieldMatrix<T> other){
        super(other.getField(), other.getRowDimension(), other.getColumnDimension());
        rows = other.getRowDimension();
        columns = other.getColumnDimension();
        entries = new OpenIntToFieldHashMap<T>(getField());
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                setEntry(i, j, other.getEntry(i, j));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addToEntry(int row, int column, T increment)
            throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        final int key = computeKey(row, column);
        final T value = entries.get(key).add(increment);
        if (getField().getZero().equals(value)) {
            entries.remove(key);
        } else {
            entries.put(key, value);
        }

    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> copy() {
        return new SparseFieldMatrix<T>(this);
    }

    /** {@inheritDoc} */
    @Override
    public FieldMatrix<T> createMatrix(int rowDimension, int columnDimension)
            throws IllegalArgumentException {
        return new SparseFieldMatrix<T>(getField(), rowDimension, columnDimension);
    }

    /** {@inheritDoc} */
    @Override
    public int getColumnDimension() {
        return columns;
    }

    /** {@inheritDoc} */
    @Override
    public T getEntry(int row, int column) throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        return entries.get(computeKey(row, column));
    }

    /** {@inheritDoc} */
    @Override
    public int getRowDimension() {
        return rows;
    }

    /** {@inheritDoc} */
    @Override
    public void multiplyEntry(int row, int column, T factor)
            throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        final int key = computeKey(row, column);
        final T value = entries.get(key).multiply(factor);
        if (getField().getZero().equals(value)) {
            entries.remove(key);
        } else {
            entries.put(key, value);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void setEntry(int row, int column, T value)
            throws MatrixIndexException {
        checkRowIndex(row);
        checkColumnIndex(column);
        if (getField().getZero().equals(value)) {
            entries.remove(computeKey(row, column));
        } else {
            entries.put(computeKey(row, column), value);
        }

    }
    /**
     * Compute the key to access a matrix element
     * @param row row index of the matrix element
     * @param column column index of the matrix element
     * @return key within the map to access the matrix element
     */
    private int computeKey(int row, int column) {
        return row * columns + column;
    }

}

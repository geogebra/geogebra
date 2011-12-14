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
import org.apache.commons.math.linear.MatrixVisitorException;

/**
 * Interface defining field-valued matrix with basic algebraic operations.
 * <p>
 * Matrix element indexing is 0-based -- e.g., <code>getEntry(0, 0)</code>
 * returns the element in the first row, first column of the matrix.</p>
 *
 * @param <T> the type of the field elements
 * @version $Revision: 1073158 $ $Date: 2011-02-21 22:46:52 +0100 (lun. 21 févr. 2011) $
 */
public interface FieldMatrix<T extends FieldElement<T>> extends AnyMatrix {

    /**
     * Get the type of field elements of the matrix.
     * @return type of field elements of the matrix
     */
    Field<T> getField();

    /**
     * Create a new FieldMatrix<T> of the same type as the instance with the supplied
     * row and column dimensions.
     *
     * @param rowDimension  the number of rows in the new matrix
     * @param columnDimension  the number of columns in the new matrix
     * @return a new matrix of the same type as the instance
     * @throws IllegalArgumentException if row or column dimension is not positive
     * @since 2.0
     */
    FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension);

    /**
     * Returns a (deep) copy of this.
     *
     * @return matrix copy
     */
    FieldMatrix<T> copy();

    /**
     * Compute the sum of this and m.
     *
     * @param m    matrix to be added
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    FieldMatrix<T> add(FieldMatrix<T> m) throws IllegalArgumentException;

    /**
     * Compute this minus m.
     *
     * @param m    matrix to be subtracted
     * @return     this + m
     * @throws  IllegalArgumentException if m is not the same size as this
     */
    FieldMatrix<T> subtract(FieldMatrix<T> m) throws IllegalArgumentException;

     /**
     * Returns the result of adding d to each entry of this.
     *
     * @param d    value to be added to each entry
     * @return     d + this
     */
    FieldMatrix<T> scalarAdd(T d);

    /**
     * Returns the result multiplying each entry of this by d.
     *
     * @param d    value to multiply all entries by
     * @return     d * this
     */
    FieldMatrix<T> scalarMultiply(T d);

    /**
     * Returns the result of postmultiplying this by m.
     *
     * @param m    matrix to postmultiply by
     * @return     this * m
     * @throws     IllegalArgumentException
     *             if columnDimension(this) != rowDimension(m)
     */
    FieldMatrix<T> multiply(FieldMatrix<T> m) throws IllegalArgumentException;

    /**
     * Returns the result premultiplying this by <code>m</code>.
     * @param m    matrix to premultiply by
     * @return     m * this
     * @throws     IllegalArgumentException
     *             if rowDimension(this) != columnDimension(m)
     */
    FieldMatrix<T> preMultiply(FieldMatrix<T> m) throws IllegalArgumentException;

    /**
     * Returns matrix entries as a two-dimensional array.
     *
     * @return    2-dimensional array of entries
     */
    T[][] getData();

    /**
     * Gets a submatrix. Rows and columns are indicated
     * counting from 0 to n-1.
     *
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index (inclusive)
     * @return The subMatrix containing the data of the
     *         specified rows and columns
     * @exception MatrixIndexException  if the indices are not valid
     */
   FieldMatrix<T> getSubMatrix(int startRow, int endRow, int startColumn, int endColumn)
       throws MatrixIndexException;

   /**
    * Gets a submatrix. Rows and columns are indicated
    * counting from 0 to n-1.
    *
    * @param selectedRows Array of row indices.
    * @param selectedColumns Array of column indices.
    * @return The subMatrix containing the data in the
    *         specified rows and columns
    * @exception MatrixIndexException if row or column selections are not valid
    */
   FieldMatrix<T> getSubMatrix(int[] selectedRows, int[] selectedColumns)
       throws MatrixIndexException;

   /**
    * Copy a submatrix. Rows and columns are indicated
    * counting from 0 to n-1.
    *
    * @param startRow Initial row index
    * @param endRow Final row index (inclusive)
    * @param startColumn Initial column index
    * @param endColumn Final column index (inclusive)
    * @param destination The arrays where the submatrix data should be copied
    * (if larger than rows/columns counts, only the upper-left part will be used)
    * @exception MatrixIndexException if the indices are not valid
    * @exception IllegalArgumentException if the destination array is too small
    */
  void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn,
                     T[][] destination)
      throws MatrixIndexException, IllegalArgumentException;

  /**
   * Copy a submatrix. Rows and columns are indicated
   * counting from 0 to n-1.
   *
    * @param selectedRows Array of row indices.
    * @param selectedColumns Array of column indices.
   * @param destination The arrays where the submatrix data should be copied
   * (if larger than rows/columns counts, only the upper-left part will be used)
   * @exception MatrixIndexException if the indices are not valid
   * @exception IllegalArgumentException if the destination array is too small
   */
  void copySubMatrix(int[] selectedRows, int[] selectedColumns, T[][] destination)
      throws MatrixIndexException, IllegalArgumentException;

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
    * @since 2.0
    */
   void setSubMatrix(T[][] subMatrix, int row, int column)
       throws MatrixIndexException;

   /**
    * Returns the entries in row number <code>row</code>
    * as a row matrix.  Row indices start at 0.
    *
    * @param row the row to be fetched
    * @return row matrix
    * @throws MatrixIndexException if the specified row index is invalid
    */
   FieldMatrix<T> getRowMatrix(int row) throws MatrixIndexException;

   /**
    * Sets the entries in row number <code>row</code>
    * as a row matrix.  Row indices start at 0.
    *
    * @param row the row to be set
    * @param matrix row matrix (must have one row and the same number of columns
    * as the instance)
    * @throws MatrixIndexException if the specified row index is invalid
    * @throws InvalidMatrixException if the matrix dimensions do not match one
    * instance row
    */
   void setRowMatrix(int row, FieldMatrix<T> matrix)
       throws MatrixIndexException, InvalidMatrixException;

   /**
    * Returns the entries in column number <code>column</code>
    * as a column matrix.  Column indices start at 0.
    *
    * @param column the column to be fetched
    * @return column matrix
    * @throws MatrixIndexException if the specified column index is invalid
    */
   FieldMatrix<T> getColumnMatrix(int column) throws MatrixIndexException;

   /**
    * Sets the entries in column number <code>column</code>
    * as a column matrix.  Column indices start at 0.
    *
    * @param column the column to be set
    * @param matrix column matrix (must have one column and the same number of rows
    * as the instance)
    * @throws MatrixIndexException if the specified column index is invalid
    * @throws InvalidMatrixException if the matrix dimensions do not match one
    * instance column
    */
   void setColumnMatrix(int column, FieldMatrix<T> matrix)
       throws MatrixIndexException, InvalidMatrixException;

   /**
    * Returns the entries in row number <code>row</code>
    * as a vector.  Row indices start at 0.
    *
    * @param row the row to be fetched
    * @return row vector
    * @throws MatrixIndexException if the specified row index is invalid
    */
   FieldVector<T> getRowVector(int row) throws MatrixIndexException;

   /**
    * Sets the entries in row number <code>row</code>
    * as a vector.  Row indices start at 0.
    *
    * @param row the row to be set
    * @param vector row vector (must have the same number of columns
    * as the instance)
    * @throws MatrixIndexException if the specified row index is invalid
    * @throws InvalidMatrixException if the vector dimension does not match one
    * instance row
    */
   void setRowVector(int row, FieldVector<T> vector)
       throws MatrixIndexException, InvalidMatrixException;

   /**
    * Returns the entries in column number <code>column</code>
    * as a vector.  Column indices start at 0.
    *
    * @param column the column to be fetched
    * @return column vector
    * @throws MatrixIndexException if the specified column index is invalid
    */
   FieldVector<T> getColumnVector(int column) throws MatrixIndexException;

   /**
    * Sets the entries in column number <code>column</code>
    * as a vector.  Column indices start at 0.
    *
    * @param column the column to be set
    * @param vector column vector (must have the same number of rows as the instance)
    * @throws MatrixIndexException if the specified column index is invalid
    * @throws InvalidMatrixException if the vector dimension does not match one
    * instance column
    */
   void setColumnVector(int column, FieldVector<T> vector)
       throws MatrixIndexException, InvalidMatrixException;

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
    T[] getRow(int row) throws MatrixIndexException;

    /**
     * Sets the entries in row number <code>row</code>
     * as a row matrix.  Row indices start at 0.
     *
     * @param row the row to be set
     * @param array row matrix (must have the same number of columns as the instance)
     * @throws MatrixIndexException if the specified row index is invalid
     * @throws InvalidMatrixException if the array size does not match one
     * instance row
     */
    void setRow(int row, T[] array)
        throws MatrixIndexException, InvalidMatrixException;

    /**
     * Returns the entries in column number <code>col</code> as an array.
     * <p>
     * Column indices start at 0.  A <code>MatrixIndexException</code> is thrown
     * unless <code>0 <= column < columnDimension.</code></p>
     *
     * @param column the column to be fetched
     * @return array of entries in the column
     * @throws MatrixIndexException if the specified column index is not valid
     */
    T[] getColumn(int column) throws MatrixIndexException;

    /**
     * Sets the entries in column number <code>column</code>
     * as a column matrix.  Column indices start at 0.
     *
     * @param column the column to be set
     * @param array column array (must have the same number of rows as the instance)
     * @throws MatrixIndexException if the specified column index is invalid
     * @throws InvalidMatrixException if the array size does not match one
     * instance column
     */
    void setColumn(int column, T[] array)
        throws MatrixIndexException, InvalidMatrixException;

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
    T getEntry(int row, int column) throws MatrixIndexException;

    /**
     * Set the entry in the specified row and column.
     * <p>
     * Row and column indices start at 0 and must satisfy
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.</p>
     *
     * @param row  row location of entry to be set
     * @param column  column location of entry to be set
     * @param value matrix entry to be set in row,column
     * @throws MatrixIndexException if the row or column index is not valid
     * @since 2.0
     */
    void setEntry(int row, int column, T value) throws MatrixIndexException;

    /**
     * Change an entry in the specified row and column.
     * <p>
     * Row and column indices start at 0 and must satisfy
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.</p>
     *
     * @param row  row location of entry to be set
     * @param column  column location of entry to be set
     * @param increment value to add to the current matrix entry in row,column
     * @throws MatrixIndexException if the row or column index is not valid
     * @since 2.0
     */
    void addToEntry(int row, int column, T increment) throws MatrixIndexException;

    /**
     * Change an entry in the specified row and column.
     * <p>
     * Row and column indices start at 0 and must satisfy
     * <ul>
     * <li><code>0 <= row < rowDimension</code></li>
     * <li><code> 0 <= column < columnDimension</code></li>
     * </ul>
     * otherwise a <code>MatrixIndexException</code> is thrown.</p>
     *
     * @param row  row location of entry to be set
     * @param column  column location of entry to be set
     * @param factor multiplication factor for the current matrix entry in row,column
     * @throws MatrixIndexException if the row or column index is not valid
     * @since 2.0
     */
    void multiplyEntry(int row, int column, T factor) throws MatrixIndexException;

    /**
     * Returns the transpose of this matrix.
     *
     * @return transpose matrix
     */
    FieldMatrix<T> transpose();

    /**
     * Returns the <a href="http://mathworld.wolfram.com/MatrixTrace.html">
     * trace</a> of the matrix (the sum of the elements on the main diagonal).
     *
     * @return trace
     * @throws NonSquareMatrixException if the matrix is not square
     */
    T getTrace() throws NonSquareMatrixException;

    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    T[] operate(T[] v) throws IllegalArgumentException;

    /**
     * Returns the result of multiplying this by the vector <code>v</code>.
     *
     * @param v the vector to operate on
     * @return this*v
     * @throws IllegalArgumentException if columnDimension != v.size()
     */
    FieldVector<T> operate(FieldVector<T> v) throws IllegalArgumentException;

    /**
     * Returns the (row) vector result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    T[] preMultiply(T[] v) throws IllegalArgumentException;

    /**
     * Returns the (row) vector result of premultiplying this by the vector <code>v</code>.
     *
     * @param v the row vector to premultiply by
     * @return v*this
     * @throws IllegalArgumentException if rowDimension != v.size()
     */
    FieldVector<T> preMultiply(FieldVector<T> v) throws IllegalArgumentException;

    /**
     * Visit (and possibly change) all matrix entries in row order.
     * <p>Row order starts at upper left and iterating through all elements
     * of a row from left to right before going to the leftmost element
     * of the next row.</p>
     * @param visitor visitor used to process all matrix entries
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end
     * of the walk
     */
    T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException;

    /**
     * Visit (but don't change) all matrix entries in row order.
     * <p>Row order starts at upper left and iterating through all elements
     * of a row from left to right before going to the leftmost element
     * of the next row.</p>
     * @param visitor visitor used to process all matrix entries
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end
     * of the walk
     */
    T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException;

    /**
     * Visit (and possibly change) some matrix entries in row order.
     * <p>Row order starts at upper left and iterating through all elements
     * of a row from left to right before going to the leftmost element
     * of the next row.</p>
     * @param visitor visitor used to process all matrix entries
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @exception MatrixIndexException  if the indices are not valid
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end
     * of the walk
     */
    T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor,
                          int startRow, int endRow, int startColumn, int endColumn)
        throws MatrixIndexException, MatrixVisitorException;

    /**
     * Visit (but don't change) some matrix entries in row order.
     * <p>Row order starts at upper left and iterating through all elements
     * of a row from left to right before going to the leftmost element
     * of the next row.</p>
     * @param visitor visitor used to process all matrix entries
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @exception MatrixIndexException  if the indices are not valid
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end
     * of the walk
     */
    T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor,
                          int startRow, int endRow, int startColumn, int endColumn)
        throws MatrixIndexException, MatrixVisitorException;

    /**
     * Visit (and possibly change) all matrix entries in column order.
     * <p>Column order starts at upper left and iterating through all elements
     * of a column from top to bottom before going to the topmost element
     * of the next column.</p>
     * @param visitor visitor used to process all matrix entries
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end
     * of the walk
     */
    T walkInColumnOrder(FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException;

    /**
     * Visit (but don't change) all matrix entries in column order.
     * <p>Column order starts at upper left and iterating through all elements
     * of a column from top to bottom before going to the topmost element
     * of the next column.</p>
     * @param visitor visitor used to process all matrix entries
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end
     * of the walk
     */
    T walkInColumnOrder(FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException;

    /**
     * Visit (and possibly change) some matrix entries in column order.
     * <p>Column order starts at upper left and iterating through all elements
     * of a column from top to bottom before going to the topmost element
     * of the next column.</p>
     * @param visitor visitor used to process all matrix entries
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @exception MatrixIndexException  if the indices are not valid
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end
     * of the walk
     */
    T walkInColumnOrder(FieldMatrixChangingVisitor<T> visitor,
                             int startRow, int endRow, int startColumn, int endColumn)
        throws MatrixIndexException, MatrixVisitorException;

    /**
     * Visit (but don't change) some matrix entries in column order.
     * <p>Column order starts at upper left and iterating through all elements
     * of a column from top to bottom before going to the topmost element
     * of the next column.</p>
     * @param visitor visitor used to process all matrix entries
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @exception MatrixIndexException  if the indices are not valid
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end
     * of the walk
     */
    T walkInColumnOrder(FieldMatrixPreservingVisitor<T> visitor,
                             int startRow, int endRow, int startColumn, int endColumn)
        throws MatrixIndexException, MatrixVisitorException;

    /**
     * Visit (and possibly change) all matrix entries using the fastest possible order.
     * <p>The fastest walking order depends on the exact matrix class. It may be
     * different from traditional row or column orders.</p>
     * @param visitor visitor used to process all matrix entries
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end
     * of the walk
     */
    T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor)
        throws MatrixVisitorException;

    /**
     * Visit (but don't change) all matrix entries using the fastest possible order.
     * <p>The fastest walking order depends on the exact matrix class. It may be
     * different from traditional row or column orders.</p>
     * @param visitor visitor used to process all matrix entries
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end
     * of the walk
     */
    T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor)
        throws MatrixVisitorException;

    /**
     * Visit (and possibly change) some matrix entries using the fastest possible order.
     * <p>The fastest walking order depends on the exact matrix class. It may be
     * different from traditional row or column orders.</p>
     * @param visitor visitor used to process all matrix entries
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index (inclusive)
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @exception MatrixIndexException  if the indices are not valid
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end
     * of the walk
     */
    T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor,
                                int startRow, int endRow, int startColumn, int endColumn)
        throws MatrixIndexException, MatrixVisitorException;

    /**
     * Visit (but don't change) some matrix entries using the fastest possible order.
     * <p>The fastest walking order depends on the exact matrix class. It may be
     * different from traditional row or column orders.</p>
     * @param visitor visitor used to process all matrix entries
     * @param startRow Initial row index
     * @param endRow Final row index (inclusive)
     * @param startColumn Initial column index
     * @param endColumn Final column index (inclusive)
     * @exception  MatrixVisitorException if the visitor cannot process an entry
     * @exception MatrixIndexException  if the indices are not valid
     * @see #walkInRowOrder(FieldMatrixChangingVisitor)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
     * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
     * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
     * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
     * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end
     * of the walk
     */
    T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor,
                                int startRow, int endRow, int startColumn, int endColumn)
        throws MatrixIndexException, MatrixVisitorException;

}

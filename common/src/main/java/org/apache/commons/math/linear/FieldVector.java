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

/**
 * Interface defining a field-valued vector with basic algebraic operations.
 * <p>
 * vector element indexing is 0-based -- e.g., <code>getEntry(0)</code>
 * returns the first element of the vector.
 * </p>
 * <p>
 * The various <code>mapXxx</code> and <code>mapXxxToSelf</code> methods operate
 * on vectors element-wise, i.e. they perform the same operation (adding a scalar,
 * applying a function ...) on each element in turn. The <code>mapXxx</code>
 * versions create a new vector to hold the result and do not change the instance.
 * The <code>mapXxxToSelf</code> versions use the instance itself to store the
 * results, so the instance is changed by these methods. In both cases, the result
 * vector is returned by the methods, this allows to use the <i>fluent API</i>
 * style, like this:
 * </p>
 * <pre>
 *   RealVector result = v.mapAddToSelf(3.0).mapTanToSelf().mapSquareToSelf();
 * </pre>
 *
 * @param <T> the type of the field elements
 * @version $Revision: 811786 $ $Date: 2009-09-06 11:36:08 +0200 (dim. 06 sept. 2009) $
 * @since 2.0
 */
public interface FieldVector<T extends FieldElement<T>>  {

    /**
     * Get the type of field elements of the vector.
     * @return type of field elements of the vector
     */
    Field<T> getField();

    /**
     * Returns a (deep) copy of this.
     * @return vector copy
     */
    FieldVector<T> copy();

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> add(FieldVector<T> v)
        throws IllegalArgumentException;

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> add(T[] v)
        throws IllegalArgumentException;

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> subtract(FieldVector<T> v)
        throws IllegalArgumentException;

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> subtract(T[] v)
        throws IllegalArgumentException;

    /**
     * Map an addition operation to each entry.
     * @param d value to be added to each entry
     * @return this + d
     */
    FieldVector<T> mapAdd(T d);

    /**
     * Map an addition operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to be added to each entry
     * @return for convenience, return this
     */
    FieldVector<T> mapAddToSelf(T d);

    /**
     * Map a subtraction operation to each entry.
     * @param d value to be subtracted to each entry
     * @return this - d
     */
    FieldVector<T> mapSubtract(T d);

    /**
     * Map a subtraction operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to be subtracted to each entry
     * @return for convenience, return this
     */
    FieldVector<T> mapSubtractToSelf(T d);

    /**
     * Map a multiplication operation to each entry.
     * @param d value to multiply all entries by
     * @return this * d
     */
    FieldVector<T> mapMultiply(T d);

    /**
     * Map a multiplication operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to multiply all entries by
     * @return for convenience, return this
     */
    FieldVector<T> mapMultiplyToSelf(T d);

    /**
     * Map a division operation to each entry.
     * @param d value to divide all entries by
     * @return this / d
     */
    FieldVector<T> mapDivide(T d);

    /**
     * Map a division operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to divide all entries by
     * @return for convenience, return this
     */
    FieldVector<T> mapDivideToSelf(T d);

    /**
     * Map the 1/x function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    FieldVector<T> mapInv();

    /**
     * Map the 1/x function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    FieldVector<T> mapInvToSelf();

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> ebeMultiply(FieldVector<T> v) throws IllegalArgumentException;

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> ebeMultiply(T[] v) throws IllegalArgumentException;

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> ebeDivide(FieldVector<T> v) throws IllegalArgumentException;

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> ebeDivide(T[] v) throws IllegalArgumentException;

    /**
     * Returns vector entries as a T array.
     * @return T array of entries
     */
     T[] getData();

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    T dotProduct(FieldVector<T> v)
        throws IllegalArgumentException;

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    T dotProduct(T[] v)
        throws IllegalArgumentException;

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> projection(FieldVector<T> v)
        throws IllegalArgumentException;

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    FieldVector<T> projection(T[] v)
        throws IllegalArgumentException;

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    FieldMatrix<T> outerProduct(FieldVector<T> v)
        throws IllegalArgumentException;

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    FieldMatrix<T> outerProduct(T[] v)
        throws IllegalArgumentException;

    /**
     * Returns the entry in the specified index.
     * <p>
     * The index start at 0 and must be lesser than the size,
     * otherwise a {@link MatrixIndexException} is thrown.
     * </p>
     * @param index  index location of entry to be fetched
     * @return vector entry at index
     * @throws MatrixIndexException if the index is not valid
     * @see #setEntry(int, FieldElement)
     */
    T getEntry(int index)
        throws MatrixIndexException;

    /**
     * Set a single element.
     * @param index element index.
     * @param value new value for the element.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     * @see #getEntry(int)
     */
    void setEntry(int index, T value)
        throws MatrixIndexException;

    /**
     * Returns the size of the vector.
     * @return size
     */
    int getDimension();

    /**
     * Construct a vector by appending a vector to this vector.
     * @param v vector to append to this one.
     * @return a new vector
     */
    FieldVector<T> append(FieldVector<T> v);

    /**
     * Construct a vector by appending a T to this vector.
     * @param d T to append.
     * @return a new vector
     */
    FieldVector<T> append(T d);

    /**
     * Construct a vector by appending a T array to this vector.
     * @param a T array to append.
     * @return a new vector
     */
    FieldVector<T> append(T[] a);

    /**
     * Get a subvector from consecutive elements.
     * @param index index of first element.
     * @param n number of elements to be retrieved.
     * @return a vector containing n elements.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     */
    FieldVector<T> getSubVector(int index, int n)
        throws MatrixIndexException;

    /**
     * Set a set of consecutive elements.
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     * @see #setSubVector(int, FieldElement[])
     */
    void setSubVector(int index, FieldVector<T> v)
        throws MatrixIndexException;

    /**
     * Set a set of consecutive elements.
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     * @see #setSubVector(int, FieldVector)
     */
    void setSubVector(int index, T[] v)
        throws MatrixIndexException;

    /**
     * Set all elements to a single value.
     * @param value single value to set for all elements
     */
    void set(T value);

    /**
     * Convert the vector to a T array.
     * <p>The array is independent from vector data, it's elements
     * are copied.</p>
     * @return array containing a copy of vector elements
     */
    T[] toArray();

}

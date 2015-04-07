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
import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * This class implements the {@link FieldVector} interface with a {@link FieldElement} array.
 * @param <T> the type of the field elements
 * @version $Revision: 1003997 $ $Date: 2010-10-03 18:45:55 +0200 (dim. 03 oct. 2010) $
 * @since 2.0
 */
public class ArrayFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 7648186910365927050L;

    /** Entries of the vector. */
    protected T[] data;

    /** Field to which the elements belong. */
    private final Field<T> field;

    /**
     * Build a 0-length vector.
     * <p>Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #ArrayFieldVector(ArrayFieldVector, ArrayFieldVector)} constructor
     * or one of the <code>append</code> methods ({@link #append(FieldElement[])},
     * {@link #add(FieldVector)}, {@link #append(ArrayFieldVector)}) to gather data
     * into this vector.</p>
     * @param field field to which the elements belong
     */
    public ArrayFieldVector(final Field<T> field) {
        this(field, 0);
    }

    /**
     * Construct a (size)-length vector of zeros.
     * @param field field to which the elements belong
     * @param size size of the vector
     */
    public ArrayFieldVector(Field<T> field, int size) {
        this.field = field;
        data = buildArray(size);
        Arrays.fill(data, field.getZero());
    }

    /**
     * Construct an (size)-length vector with preset values.
     * @param size size of the vector
     * @param preset fill the vector with this scalar value
     */
    public ArrayFieldVector(int size, T preset) {
        this(preset.getField(), size);
        Arrays.fill(data, preset);
    }

    /**
     * Construct a vector from an array, copying the input array.
     * <p>
     * This constructor needs a non-empty {@code d} array to retrieve
     * the field from its first element. This implies it cannot build
     * 0 length vectors. To build vectors from any size, one should
     * use the {@link #ArrayFieldVector(Field, FieldElement[])} constructor.
     * </p>
     * @param d array of Ts.
     * @throws IllegalArgumentException if <code>d</code> is empty
     * @see #ArrayFieldVector(Field, FieldElement[])
     */
    public ArrayFieldVector(T[] d)
        throws IllegalArgumentException {
        try {
            field = d[0].getField();
            data = d.clone();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
    }

    /**
     * Construct a vector from an array, copying the input array.
     * @param field field to which the elements belong
     * @param d array of Ts.
     * @see #ArrayFieldVector(FieldElement[])
     */
    public ArrayFieldVector(Field<T> field, T[] d) {
        this.field = field;
        data = d.clone();
    }

    /**
     * Create a new ArrayFieldVector using the input array as the underlying
     * data array.
     * <p>If an array is built specially in order to be embedded in a
     * ArrayFieldVector and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * <p>
     * This constructor needs a non-empty {@code d} array to retrieve
     * the field from its first element. This implies it cannot build
     * 0 length vectors. To build vectors from any size, one should
     * use the {@link #ArrayFieldVector(Field, FieldElement[], boolean)} constructor.
     * </p>
     * @param d data for new vector
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @throws IllegalArgumentException if <code>d</code> is empty
     * @throws NullPointerException if <code>d</code> is null
     * @see #ArrayFieldVector(FieldElement[])
     * @see #ArrayFieldVector(Field, FieldElement[], boolean)
     */
    public ArrayFieldVector(T[] d, boolean copyArray)
        throws NullPointerException, IllegalArgumentException {
        if (d.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
        field = d[0].getField();
        data = copyArray ? d.clone() :  d;
    }

    /**
     * Create a new ArrayFieldVector using the input array as the underlying
     * data array.
     * <p>If an array is built specially in order to be embedded in a
     * ArrayFieldVector and not used directly, the <code>copyArray</code> may be
     * set to <code>false</code. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.</p>
     * @param field field to which the elements belong
     * @param d data for new vector
     * @param copyArray if true, the input array will be copied, otherwise
     * it will be referenced
     * @see #ArrayFieldVector(FieldElement[], boolean)
     */
    public ArrayFieldVector(Field<T> field, T[] d, boolean copyArray) {
        this.field = field;
        data = copyArray ? d.clone() :  d;
    }

    /**
     * Construct a vector from part of a array.
     * @param d array of Ts.
     * @param pos position of first entry
     * @param size number of entries to copy
     */
    public ArrayFieldVector(T[] d, int pos, int size) {
        if (d.length < pos + size) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.POSITION_SIZE_MISMATCH_INPUT_ARRAY,
                    pos, size, d.length);
        }
        field = d[0].getField();
        data = buildArray(size);
        System.arraycopy(d, pos, data, 0, size);
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     * @param v vector to copy
     */
    public ArrayFieldVector(FieldVector<T> v) {
        field = v.getField();
        data = buildArray(v.getDimension());
        for (int i = 0; i < data.length; ++i) {
            data[i] = v.getEntry(i);
        }
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     * @param v vector to copy
     */
    public ArrayFieldVector(ArrayFieldVector<T> v) {
        field = v.getField();
        data = v.data.clone();
    }

    /**
     * Construct a vector from another vector.
     * @param v vector to copy
     * @param deep if true perform a deep copy otherwise perform a shallow copy
     */
    public ArrayFieldVector(ArrayFieldVector<T> v, boolean deep) {
        field = v.getField();
        data = deep ? v.data.clone() : v.data;
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayFieldVector(ArrayFieldVector<T> v1, ArrayFieldVector<T> v2) {
        field = v1.getField();
        data = buildArray(v1.data.length + v2.data.length);
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, data, v1.data.length, v2.data.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayFieldVector(ArrayFieldVector<T> v1, T[] v2) {
        field = v1.getField();
        data = buildArray(v1.data.length + v2.length);
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2, 0, data, v1.data.length, v2.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayFieldVector(T[] v1, ArrayFieldVector<T> v2) {
        field = v2.getField();
        data = buildArray(v1.length + v2.data.length);
        System.arraycopy(v1, 0, data, 0, v1.length);
        System.arraycopy(v2.data, 0, data, v1.length, v2.data.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * <p>
     * This constructor needs at least one non-empty array to retrieve
     * the field from its first element. This implies it cannot build
     * 0 length vectors. To build vectors from any size, one should
     * use the {@link #ArrayFieldVector(Field, FieldElement[], FieldElement[])} constructor.
     * </p>
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     * @exception IllegalArgumentException if both vectors are empty
     * @see #ArrayFieldVector(Field, FieldElement[], FieldElement[])
     */
    public ArrayFieldVector(T[] v1, T[] v2) {
        try {
            data = buildArray(v1.length + v2.length);
            System.arraycopy(v1, 0, data, 0, v1.length);
            System.arraycopy(v2, 0, data, v1.length, v2.length);
            field = data[0].getField();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw MathRuntimeException.createIllegalArgumentException(
                      LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param field field to which the elements belong
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     * @see #ArrayFieldVector(FieldElement[], FieldElement[])
     */
    public ArrayFieldVector(Field<T> field, T[] v1, T[] v2) {
        if (v1.length + v2.length == 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
        }
        data = buildArray(v1.length + v2.length);
        System.arraycopy(v1, 0, data, 0, v1.length);
        System.arraycopy(v2, 0, data, v1.length, v2.length);
        this.field = data[0].getField();
    }

    /** Build an array of elements.
     * @param length size of the array to build
     * @return a new array
     */
    @SuppressWarnings("unchecked") // field is of type T
    private T[] buildArray(final int length) {
        return (T[]) Array.newInstance(field.getZero().getClass(), length);
    }

    /** {@inheritDoc} */
    public Field<T> getField() {
        return field;
    }

    /** {@inheritDoc} */
    public FieldVector<T> copy() {
        return new ArrayFieldVector<T>(this, true);
    }

    /** {@inheritDoc} */
    public FieldVector<T> add(FieldVector<T> v) throws IllegalArgumentException {
        try {
            return add((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].add(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(out);
        }
    }

    /** {@inheritDoc} */
    public FieldVector<T> add(T[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].add(v[i]);
        }
        return new ArrayFieldVector<T>(out);
    }

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> add(ArrayFieldVector<T> v)
        throws IllegalArgumentException {
        return (ArrayFieldVector<T>) add(v.data);
    }

    /** {@inheritDoc} */
    public FieldVector<T> subtract(FieldVector<T> v) throws IllegalArgumentException {
        try {
            return subtract((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].subtract(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(out);
        }
    }

    /** {@inheritDoc} */
    public FieldVector<T> subtract(T[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].subtract(v[i]);
        }
        return new ArrayFieldVector<T>(out);
    }

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> subtract(ArrayFieldVector<T> v)
        throws IllegalArgumentException {
        return (ArrayFieldVector<T>) subtract(v.data);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapAdd(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].add(d);
        }
        return new ArrayFieldVector<T>(out);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapAddToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].add(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapSubtract(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].subtract(d);
        }
        return new ArrayFieldVector<T>(out);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapSubtractToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].subtract(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapMultiply(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].multiply(d);
        }
        return new ArrayFieldVector<T>(out);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapMultiplyToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].multiply(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapDivide(T d) {
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].divide(d);
        }
        return new ArrayFieldVector<T>(out);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapDivideToSelf(T d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].divide(d);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapInv() {
        T[] out = buildArray(data.length);
        final T one = field.getOne();
        for (int i = 0; i < data.length; i++) {
            out[i] = one.divide(data[i]);
        }
        return new ArrayFieldVector<T>(out);
    }

    /** {@inheritDoc} */
    public FieldVector<T> mapInvToSelf() {
        final T one = field.getOne();
        for (int i = 0; i < data.length; i++) {
            data[i] = one.divide(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public FieldVector<T> ebeMultiply(FieldVector<T> v)
        throws IllegalArgumentException {
        try {
            return ebeMultiply((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].multiply(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(out);
        }
    }

    /** {@inheritDoc} */
    public FieldVector<T> ebeMultiply(T[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
            out[i] = data[i].multiply(v[i]);
        }
        return new ArrayFieldVector<T>(out);
    }

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> ebeMultiply(ArrayFieldVector<T> v)
        throws IllegalArgumentException {
        return (ArrayFieldVector<T>) ebeMultiply(v.data);
    }

    /** {@inheritDoc} */
    public FieldVector<T> ebeDivide(FieldVector<T> v)
        throws IllegalArgumentException {
        try {
            return ebeDivide((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T[] out = buildArray(data.length);
            for (int i = 0; i < data.length; i++) {
                out[i] = data[i].divide(v.getEntry(i));
            }
            return new ArrayFieldVector<T>(out);
        }
    }

    /** {@inheritDoc} */
    public FieldVector<T> ebeDivide(T[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        T[] out = buildArray(data.length);
        for (int i = 0; i < data.length; i++) {
                out[i] = data[i].divide(v[i]);
        }
        return new ArrayFieldVector<T>(out);
    }

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> ebeDivide(ArrayFieldVector<T> v)
        throws IllegalArgumentException {
        return (ArrayFieldVector<T>) ebeDivide(v.data);
    }

    /** {@inheritDoc} */
    public T[] getData() {
        return data.clone();
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>Does not make a fresh copy of the underlying data.</p>
     * @return array of entries
     */
    public T[] getDataRef() {
        return data;
    }

    /** {@inheritDoc} */
    public T dotProduct(FieldVector<T> v)
        throws IllegalArgumentException {
        try {
            return dotProduct((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            T dot = field.getZero();
            for (int i = 0; i < data.length; i++) {
                dot = dot.add(data[i].multiply(v.getEntry(i)));
            }
            return dot;
        }
    }

    /** {@inheritDoc} */
    public T dotProduct(T[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        T dot = field.getZero();
        for (int i = 0; i < data.length; i++) {
            dot = dot.add(data[i].multiply(v[i]));
        }
        return dot;
    }

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public T dotProduct(ArrayFieldVector<T> v)
        throws IllegalArgumentException {
        return dotProduct(v.data);
    }

    /** {@inheritDoc} */
    public FieldVector<T> projection(FieldVector<T> v) {
        return v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
    }

    /** {@inheritDoc} */
    public FieldVector<T> projection(T[] v) {
        return projection(new ArrayFieldVector<T>(v, false));
    }

   /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayFieldVector<T> projection(ArrayFieldVector<T> v) {
        return (ArrayFieldVector<T>) v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
    }

    /** {@inheritDoc} */
    public FieldMatrix<T> outerProduct(FieldVector<T> v)
        throws IllegalArgumentException {
        try {
            return outerProduct((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            checkVectorDimensions(v);
            final int m = data.length;
            final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(field, m, m);
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    out.setEntry(i, j, data[i].multiply(v.getEntry(j)));
                }
            }
            return out;
        }
    }

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public FieldMatrix<T> outerProduct(ArrayFieldVector<T> v)
        throws IllegalArgumentException {
        return outerProduct(v.data);
    }

    /** {@inheritDoc} */
    public FieldMatrix<T> outerProduct(T[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        final int m = data.length;
        final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(field, m, m);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                out.setEntry(i, j, data[i].multiply(v[j]));
            }
        }
        return out;
    }

    /** {@inheritDoc} */
    public T getEntry(int index) throws MatrixIndexException {
        return data[index];
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return data.length;
    }

    /** {@inheritDoc} */
    public FieldVector<T> append(FieldVector<T> v) {
        try {
            return append((ArrayFieldVector<T>) v);
        } catch (ClassCastException cce) {
            return new ArrayFieldVector<T>(this,new ArrayFieldVector<T>(v));
        }
    }

    /**
     * Construct a vector by appending a vector to this vector.
     * @param v vector to append to this one.
     * @return a new vector
     */
    public ArrayFieldVector<T> append(ArrayFieldVector<T> v) {
        return new ArrayFieldVector<T>(this, v);
    }

    /** {@inheritDoc} */
    public FieldVector<T> append(T in) {
        final T[] out = buildArray(data.length + 1);
        System.arraycopy(data, 0, out, 0, data.length);
        out[data.length] = in;
        return new ArrayFieldVector<T>(out);
    }

    /** {@inheritDoc} */
    public FieldVector<T> append(T[] in) {
        return new ArrayFieldVector<T>(this, in);
    }

    /** {@inheritDoc} */
    public FieldVector<T> getSubVector(int index, int n) {
        ArrayFieldVector<T> out = new ArrayFieldVector<T>(field, n);
        try {
            System.arraycopy(data, index, out.data, 0, n);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + n - 1);
        }
        return out;
    }

    /** {@inheritDoc} */
    public void setEntry(int index, T value) {
        try {
            data[index] = value;
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
        }
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, FieldVector<T> v) {
        try {
            try {
                set(index, (ArrayFieldVector<T>) v);
            } catch (ClassCastException cce) {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    data[i] = v.getEntry(i-index);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.getDimension() - 1);
        }
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, T[] v) {
        try {
            System.arraycopy(v, 0, data, index, v.length);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.length - 1);
        }
    }

    /**
     * Set a set of consecutive elements.
     *
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     */
    public void set(int index, ArrayFieldVector<T> v)
        throws MatrixIndexException {
        setSubVector(index, v.data);
    }

    /** {@inheritDoc} */
    public void set(T value) {
        Arrays.fill(data, value);
    }

    /** {@inheritDoc} */
    public T[] toArray(){
        return data.clone();
    }

    /**
     * Check if instance and specified vectors have the same dimension.
     * @param v vector to compare instance with
     * @exception IllegalArgumentException if the vectors do not
     * have the same dimension
     */
    protected void checkVectorDimensions(FieldVector<T> v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n expected dimension.
     * @exception IllegalArgumentException if the dimension is
     * inconsistent with vector size
     */
    protected void checkVectorDimensions(int n)
        throws IllegalArgumentException {
        if (data.length != n) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.VECTOR_LENGTH_MISMATCH,
                    data.length, n);
        }
    }

    /**
     * Test for the equality of two real vectors.
     * <p>
     * If all coordinates of two real vectors are exactly the same, and none are
     * <code>Double.NaN</code>, the two real vectors are considered to be equal.
     * </p>
     * <p>
     * <code>NaN</code> coordinates are considered to affect globally the vector
     * and be equals to each other - i.e, if either (or all) coordinates of the
     * real vector are equal to <code>Double.NaN</code>, the real vector is equal to
     * a vector with all <code>Double.NaN</code> coordinates.
     * </p>
     *
     * @param other Object to test for equality to this
     * @return true if two 3D vector objects are equal, false if
     *         object is null, not an instance of Vector3D, or
     *         not equal to this Vector3D instance
     *
     */
    @Override
    public boolean equals(Object other) {

      if (this == other) {
        return true;
      }

      if (other == null) {
        return false;
      }

      try {
          @SuppressWarnings("unchecked") // May fail, but we ignore ClassCastException
          FieldVector<T> rhs = (FieldVector<T>) other;
          if (data.length != rhs.getDimension()) {
              return false;
          }

          for (int i = 0; i < data.length; ++i) {
              if (!data[i].equals(rhs.getEntry(i))) {
                  return false;
              }
          }
          return true;

      } catch (ClassCastException ex) {
          // ignore exception
          return false;
      }

    }

    /**
     * Get a hashCode for the real vector.
     * <p>All NaN values have the same hash code.</p>
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        int h = 3542;
        for (final T a : data) {
            h = h ^ a.hashCode();
        }
        return h;
    }

    /**
     * Check if an index is valid.
     * @param index index to check
     * @exception MatrixIndexException if index is not valid
     */
    private void checkIndex(final int index)
        throws MatrixIndexException {
        if (index < 0 || index >= getDimension()) {
            throw new MatrixIndexException(LocalizedFormats.INDEX_OUT_OF_RANGE,
                                           index, 0, getDimension() - 1);
        }
    }

}

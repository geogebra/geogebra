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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.OpenIntToDoubleHashMap;
import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;

/**
 * This class implements the {@link RealVector} interface with a {@link OpenIntToDoubleHashMap} backing store.
 * @version $Revision: 1073262 $ $Date: 2011-02-22 10:02:25 +0100 (mar. 22 févr. 2011) $
 * @since 2.0
*/
public class OpenMapRealVector extends AbstractRealVector implements SparseRealVector, Serializable {

    /** Default Tolerance for having a value considered zero. */
    public static final double DEFAULT_ZERO_TOLERANCE = 1.0e-12;

    /** Serializable version identifier. */
    private static final long serialVersionUID = 8772222695580707260L;

    /** Entries of the vector. */
    private final OpenIntToDoubleHashMap entries;

    /** Dimension of the vector. */
    private final int virtualSize;

    /** Tolerance for having a value considered zero. */
    private final double epsilon;

    /**
     * Build a 0-length vector.
     * <p>Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #OpenMapRealVector(OpenMapRealVector, int)} constructor
     * or one of the <code>append</code> method ({@link #append(double)}, {@link
     * #append(double[])}, {@link #append(RealVector)}) to gather data
     * into this vector.</p>
     */
    public OpenMapRealVector() {
        this(0, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Construct a (dimension)-length vector of zeros.
     * @param dimension size of the vector
     */
    public OpenMapRealVector(int dimension) {
        this(dimension, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Construct a (dimension)-length vector of zeros, specifying zero tolerance.
     * @param dimension Size of the vector
     * @param epsilon The tolerance for having a value considered zero
     */
    public OpenMapRealVector(int dimension, double epsilon) {
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
    }

    /**
     * Build a resized vector, for use with append.
     * @param v The original vector
     * @param resize The amount to resize it
     */
    protected OpenMapRealVector(OpenMapRealVector v, int resize) {
        virtualSize = v.getDimension() + resize;
        entries = new OpenIntToDoubleHashMap(v.entries);
        epsilon = v.epsilon;
    }

    /**
     * Build a vector with known the sparseness (for advanced use only).
     * @param dimension The size of the vector
     * @param expectedSize The expected number of non-zero entries
     */
    public OpenMapRealVector(int dimension, int expectedSize) {
        this(dimension, expectedSize, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Build a vector with known the sparseness and zero tolerance setting (for advanced use only).
     * @param dimension The size of the vector
     * @param expectedSize The expected number of non-zero entries
     * @param epsilon The tolerance for having a value considered zero
     */
    public OpenMapRealVector(int dimension, int expectedSize, double epsilon) {
        virtualSize = dimension;
        entries = new OpenIntToDoubleHashMap(expectedSize, 0.0);
        this.epsilon = epsilon;
    }

    /**
     * Create from a double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     */
    public OpenMapRealVector(double[] values) {
        this(values, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Create from a double array, specifying zero tolerance.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     * @param epsilon The tolerance for having a value considered zero
     */
    public OpenMapRealVector(double[] values, double epsilon) {
        virtualSize = values.length;
        entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
        for (int key = 0; key < values.length; key++) {
            double value = values[key];
            if (!isDefaultValue(value)) {
                entries.put(key, value);
            }
        }
    }

    /**
     * Create from a Double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     */
    public OpenMapRealVector(Double[] values) {
        this(values, DEFAULT_ZERO_TOLERANCE);
    }

    /**
     * Create from a Double array.
     * Only non-zero entries will be stored
     * @param values The set of values to create from
     * @param epsilon The tolerance for having a value considered zero
     */
    public OpenMapRealVector(Double[] values, double epsilon) {
        virtualSize = values.length;
        entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
        for (int key = 0; key < values.length; key++) {
            double value = values[key].doubleValue();
            if (!isDefaultValue(value)) {
                entries.put(key, value);
            }
        }
    }

    /**
     * Copy constructor.
     * @param v The instance to copy from
     */
    public OpenMapRealVector(OpenMapRealVector v) {
        virtualSize = v.getDimension();
        entries = new OpenIntToDoubleHashMap(v.getEntries());
        epsilon = v.epsilon;
    }

    /**
     * Generic copy constructor.
     * @param v The instance to copy from
     */
    public OpenMapRealVector(RealVector v) {
        virtualSize = v.getDimension();
        entries = new OpenIntToDoubleHashMap(0.0);
        epsilon = DEFAULT_ZERO_TOLERANCE;
        for (int key = 0; key < virtualSize; key++) {
            double value = v.getEntry(key);
            if (!isDefaultValue(value)) {
                entries.put(key, value);
            }
        }
    }

    /**
     * Get the entries of this instance.
     * @return entries of this instance
     */
    private OpenIntToDoubleHashMap getEntries() {
        return entries;
    }

    /**
     * Determine if this value is within epsilon of zero.
     * @param value The value to test
     * @return <code>true</code> if this value is within epsilon to zero, <code>false</code> otherwise
     * @since 2.1
     */
    protected boolean isDefaultValue(double value) {
        return FastMath.abs(value) < epsilon;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector add(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return add((OpenMapRealVector) v);
        } else {
            return super.add(v);
        }
    }

    /**
     * Optimized method to add two OpenMapRealVectors.  Copies the larger vector, iterates over the smaller.
     * @param v Vector to add with
     * @return The sum of <code>this</code> with <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public OpenMapRealVector add(OpenMapRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        boolean copyThis = entries.size() > v.entries.size();
        OpenMapRealVector res = copyThis ? this.copy() : v.copy();
        Iterator iter = copyThis ? v.entries.iterator() : entries.iterator();
        OpenIntToDoubleHashMap randomAccess = copyThis ? entries : v.entries;
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (randomAccess.containsKey(key)) {
                res.setEntry(key, randomAccess.get(key) + iter.value());
            } else {
                res.setEntry(key, iter.value());
            }
        }
        return res;
    }

    /**
     * Optimized method to append a OpenMapRealVector.
     * @param v vector to append
     * @return The result of appending <code>v</code> to self
     */
    public OpenMapRealVector append(OpenMapRealVector v) {
        OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
        Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key() + virtualSize, iter.value());
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector append(RealVector v) {
        if (v instanceof OpenMapRealVector) {
            return append((OpenMapRealVector) v);
        }
        return append(v.getData());
    }

    /** {@inheritDoc} */
    public OpenMapRealVector append(double d) {
        OpenMapRealVector res = new OpenMapRealVector(this, 1);
        res.setEntry(virtualSize, d);
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector append(double[] a) {
        OpenMapRealVector res = new OpenMapRealVector(this, a.length);
        for (int i = 0; i < a.length; i++) {
            res.setEntry(i + virtualSize, a[i]);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     * @since 2.1
     */
    @Override
    public OpenMapRealVector copy() {
        return new OpenMapRealVector(this);
    }

    /**
     * Optimized method to compute the dot product with an OpenMapRealVector.
     * Iterates over the smaller of the two.
     * @param v The vector to compute the dot product with
     * @return The dot product of <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public double dotProduct(OpenMapRealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        boolean thisIsSmaller  = entries.size() < v.entries.size();
        Iterator iter = thisIsSmaller  ? entries.iterator() : v.entries.iterator();
        OpenIntToDoubleHashMap larger = thisIsSmaller  ? v.entries : entries;
        double d = 0;
        while(iter.hasNext()) {
            iter.advance();
            d += iter.value() * larger.get(iter.key());
        }
        return d;
    }

    /** {@inheritDoc} */
    @Override
    public double dotProduct(RealVector v) throws IllegalArgumentException {
        if(v instanceof OpenMapRealVector) {
            return dotProduct((OpenMapRealVector)v);
        } else {
            return super.dotProduct(v);
        }
    }

    /** {@inheritDoc} */
    public OpenMapRealVector ebeDivide(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v.getEntry(iter.key()));
        }
        return res;
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector ebeDivide(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() / v[iter.key()]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector ebeMultiply(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        return res;
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector ebeMultiply(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v[iter.key()]);
        }
        return res;
    }

    /** {@inheritDoc} */
    public OpenMapRealVector getSubVector(int index, int n) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + n - 1);
        OpenMapRealVector res = new OpenMapRealVector(n);
        int end = index + n;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (key >= index && key < end) {
                res.setEntry(key - index, iter.value());
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    @Override
    public double[] getData() {
        double[] res = new double[virtualSize];
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res[iter.key()] = iter.value();
        }
        return res;
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return virtualSize;
    }

    /**
     * Optimized method to compute distance.
     * @param v The vector to compute distance to
     * @return The distance from <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public double getDistance(OpenMapRealVector v) throws IllegalArgumentException {
        Iterator iter = entries.iterator();
        double res = 0;
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            double delta;
            delta = iter.value() - v.getEntry(key);
            res += delta * delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                final double value = iter.value();
                res += value * value;
            }
        }
        return FastMath.sqrt(res);
    }

    /** {@inheritDoc} */
    @Override
    public double getDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getDistance((OpenMapRealVector) v);
        }
        return getDistance(v.getData());
    }

    /** {@inheritDoc} */
    @Override
    public double getDistance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double res = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = entries.get(i) - v[i];
            res += delta * delta;
        }
        return FastMath.sqrt(res);
    }

    /** {@inheritDoc} */
    public double getEntry(int index) throws MatrixIndexException {
        checkIndex(index);
        return entries.get(index);
    }

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     */
    public double getL1Distance(OpenMapRealVector v) {
        double max = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
            max += delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                double delta = FastMath.abs(iter.value());
                max +=  FastMath.abs(delta);
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    @Override
    public double getL1Distance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getL1Distance((OpenMapRealVector) v);
        }
        return getL1Distance(v.getData());
    }

    /** {@inheritDoc} */
    @Override
    public double getL1Distance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double max = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = FastMath.abs(getEntry(i) - v[i]);
            max += delta;
        }
        return max;
    }

    /**
     * Optimized method to compute LInfDistance.
     * @param v The vector to compute from
     * @return the LInfDistance
     */
    private double getLInfDistance(OpenMapRealVector v) {
        double max = 0;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
            if (delta > max) {
                max = delta;
            }
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (!entries.containsKey(key)) {
                if (iter.value() > max) {
                    max = iter.value();
                }
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    @Override
    public double getLInfDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return getLInfDistance((OpenMapRealVector) v);
        }
        return getLInfDistance(v.getData());
    }

    /** {@inheritDoc} */
    @Override
    public double getLInfDistance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double max = 0;
        for (int i = 0; i < v.length; i++) {
            double delta = FastMath.abs(getEntry(i) - v[i]);
            if (delta > max) {
                max = delta;
            }
        }
        return max;
    }

    /** {@inheritDoc} */
    public boolean isInfinite() {
        boolean infiniteFound = false;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double value = iter.value();
            if (Double.isNaN(value)) {
                return false;
            }
            if (Double.isInfinite(value)) {
                infiniteFound = true;
            }
        }
        return infiniteFound;
    }

    /** {@inheritDoc} */
    public boolean isNaN() {
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.isNaN(iter.value())) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector mapAddToSelf(double d) {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, getEntry(i) + d);
        }
        return this;
    }

     /** {@inheritDoc} */
    @Override
    public RealMatrix outerProduct(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        RealMatrix res = new OpenMapRealMatrix(virtualSize, virtualSize);
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            int row = iter.key();
            double value = iter.value();
            for (int col = 0; col < virtualSize; col++) {
                res.setEntry(row, col, value * v[col]);
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    public RealVector projection(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector projection(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        return (OpenMapRealVector) projection(new OpenMapRealVector(v));
    }

    /** {@inheritDoc} */
    public void setEntry(int index, double value) throws MatrixIndexException {
        checkIndex(index);
        if (!isDefaultValue(value)) {
            entries.put(index, value);
        } else if (entries.containsKey(index)) {
            entries.remove(index);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setSubVector(int index, RealVector v) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + v.getDimension() - 1);
        setSubVector(index, v.getData());
    }

    /** {@inheritDoc} */
    @Override
    public void setSubVector(int index, double[] v) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + v.length - 1);
        for (int i = 0; i < v.length; i++) {
            setEntry(i + index, v[i]);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void set(double value) {
        for (int i = 0; i < virtualSize; i++) {
            setEntry(i, value);
        }
    }

    /**
     * Optimized method to subtract OpenMapRealVectors.
     * @param v The vector to subtract from <code>this</code>
     * @return The difference of <code>this</code> and <code>v</code>
     * @throws IllegalArgumentException If the dimensions don't match
     */
    public OpenMapRealVector subtract(OpenMapRealVector v) throws IllegalArgumentException{
        checkVectorDimensions(v.getDimension());
        OpenMapRealVector res = copy();
        Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            int key = iter.key();
            if (entries.containsKey(key)) {
                res.setEntry(key, entries.get(key) - iter.value());
            } else {
                res.setEntry(key, -iter.value());
            }
        }
        return res;
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector subtract(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return subtract((OpenMapRealVector) v);
        }
        return subtract(v.getData());
    }

    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector subtract(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        OpenMapRealVector res = new OpenMapRealVector(this);
        for (int i = 0; i < v.length; i++) {
            if (entries.containsKey(i)) {
                res.setEntry(i, entries.get(i) - v[i]);
            } else {
                res.setEntry(i, -v[i]);
            }
        }
        return res;
    }


    /** {@inheritDoc} */
    @Override
    public OpenMapRealVector unitVector() {
        OpenMapRealVector res = copy();
        res.unitize();
        return res;
    }

    /** {@inheritDoc} */
    @Override
    public void unitize() {
        double norm = getNorm();
        if (isDefaultValue(norm)) {
            throw  MathRuntimeException.createArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR);
        }
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            entries.put(iter.key(), iter.value() / norm);
        }

    }


    /** {@inheritDoc} */
    @Override
    public double[] toArray() {
        return getData();
    }

    /** {@inheritDoc}
     * <p> Implementation Note: This works on exact values, and as a result
     * it is possible for {@code a.subtract(b)} to be the zero vector, while
     * {@code a.hashCode() != b.hashCode()}.</p>
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(epsilon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + virtualSize;
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            temp = Double.doubleToLongBits(iter.value());
            result = prime * result + (int) (temp ^ (temp >>32));
        }
        return result;
    }

    /**
     * <p> Implementation Note: This performs an exact comparison, and as a result
     * it is possible for {@code a.subtract(b}} to be the zero vector, while
     * {@code  a.equals(b) == false}.</p>
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpenMapRealVector)) {
            return false;
        }
        OpenMapRealVector other = (OpenMapRealVector) obj;
        if (virtualSize != other.virtualSize) {
            return false;
        }
        if (Double.doubleToLongBits(epsilon) !=
            Double.doubleToLongBits(other.epsilon)) {
            return false;
        }
        Iterator iter = entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            double test = other.getEntry(iter.key());
            if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value())) {
                return false;
            }
        }
        iter = other.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            double test = iter.value();
            if (Double.doubleToLongBits(test) != Double.doubleToLongBits(getEntry(iter.key()))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return the percentage of none zero elements as a decimal percent.
     * @deprecated as of 2.2 replaced by the correctly spelled {@link #getSparsity()}
     */
    @Deprecated
    public double getSparcity() {
        return getSparsity();
    }

    /**
    *
    * @return the percentage of none zero elements as a decimal percent.
    * @since 2.2
    */
   public double getSparsity() {
        return (double)entries.size()/(double)getDimension();
    }

    /** {@inheritDoc} */
    @Override
    public java.util.Iterator<Entry> sparseIterator() {
        return new OpenMapSparseIterator();
    }

    /**
     *  Implementation of <code>Entry</code> optimized for OpenMap.
     * <p>This implementation does not allow arbitrary calls to <code>setIndex</code>
     * since the order that entries are returned is undefined.
     */
    protected class OpenMapEntry extends Entry {

        /** Iterator pointing to the entry. */
        private final Iterator iter;

        /** Build an entry from an iterator point to an element.
         * @param iter iterator pointing to the entry
         */
        protected OpenMapEntry(Iterator iter) {
            this.iter = iter;
        }

        /** {@inheritDoc} */
        @Override
        public double getValue() {
            return iter.value();
        }

        /** {@inheritDoc} */
        @Override
        public void setValue(double value) {
            entries.put(iter.key(), value);
        }

        /** {@inheritDoc} */
        @Override
        public int getIndex() {
            return iter.key();
        }

    }

    /**
     *  Iterator class to do iteration over just the non-zero elements.
     *  <p>This implementation is fail-fast, so cannot be used to modify any zero element.
     *
     */
    protected class OpenMapSparseIterator implements java.util.Iterator<Entry> {

        /** Underlying iterator. */
        private final Iterator iter;

        /** Current entry. */
        private final Entry current;

        /** Simple constructor. */
        protected OpenMapSparseIterator() {
            iter = entries.iterator();
            current = new OpenMapEntry(iter);
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return iter.hasNext();
        }

        /** {@inheritDoc} */
        public Entry next() {
            iter.advance();
            return current;
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException("Not supported");
       }

    }
}

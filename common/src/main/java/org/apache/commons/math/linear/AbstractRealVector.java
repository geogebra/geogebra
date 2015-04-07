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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BinaryFunction;
import org.apache.commons.math.analysis.ComposableFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * This class provides default basic implementations for many methods in the
 * {@link RealVector} interface.
 * @version $Revision: 1070725 $ $Date: 2011-02-15 02:31:12 +0100 (mar. 15 févr. 2011) $
 * @since 2.1
 */
public abstract class AbstractRealVector implements RealVector {

    /**
     * Check if instance and specified vectors have the same dimension.
     * @param v vector to compare instance with
     * @exception DimensionMismatchException if the vectors do not
     * have the same dimension
     */
    protected void checkVectorDimensions(RealVector v) {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n expected dimension.
     * @exception DimensionMismatchException if the dimension is
     * inconsistent with vector size
     */
    protected void checkVectorDimensions(int n)
        throws DimensionMismatchException {
        int d = getDimension();
        if (d != n) {
            throw new DimensionMismatchException(d, n);
        }
    }

    /**
     * Check if an index is valid.
     * @param index index to check
     * @exception MatrixIndexException if index is not valid
     */
    protected void checkIndex(final int index)
        throws MatrixIndexException {
        if (index < 0 || index >= getDimension()) {
            throw new MatrixIndexException(LocalizedFormats.INDEX_OUT_OF_RANGE,
                                           index, 0, getDimension() - 1);
        }
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, RealVector v) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + v.getDimension() - 1);
        setSubVector(index, v.getData());
    }

    /** {@inheritDoc} */
    public void setSubVector(int index, double[] v) throws MatrixIndexException {
        checkIndex(index);
        checkIndex(index + v.length - 1);
        for (int i = 0; i < v.length; i++) {
            setEntry(i + index, v[i]);
        }
    }

    /** {@inheritDoc} */
    public RealVector add(double[] v) throws IllegalArgumentException {
        double[] result = v.clone();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            result[e.getIndex()] += e.getValue();
        }
        return new ArrayRealVector(result, false);
    }

    /** {@inheritDoc} */
    public RealVector add(RealVector v) throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            double[] values = ((ArrayRealVector)v).getDataRef();
            return add(values);
        }
        RealVector result = v.copy();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }

    /** {@inheritDoc} */
    public RealVector subtract(double[] v) throws IllegalArgumentException {
        double[] result = v.clone();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final int index = e.getIndex();
            result[index] = e.getValue() - result[index];
        }
        return new ArrayRealVector(result, false);
    }

    /** {@inheritDoc} */
    public RealVector subtract(RealVector v) throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            double[] values = ((ArrayRealVector)v).getDataRef();
            return add(values);
        }
        RealVector result = v.copy();
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final int index = e.getIndex();
            v.setEntry(index, e.getValue() - result.getEntry(index));
        }
        return result;
    }

    /** {@inheritDoc} */
    public RealVector mapAdd(double d) {
        return copy().mapAddToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapAddToSelf(double d) {
        if (d != 0) {
            try {
                return mapToSelf(BinaryFunction.ADD.fix1stArgument(d));
            } catch (FunctionEvaluationException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return this;
    }

    /** {@inheritDoc} */
    public abstract AbstractRealVector copy();

    /** {@inheritDoc} */
    public double dotProduct(double[] v) throws IllegalArgumentException {
        return dotProduct(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public double dotProduct(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d += e.getValue() * v.getEntry(e.getIndex());
        }
        return d;
    }

    /** {@inheritDoc} */
    public RealVector ebeDivide(double[] v) throws IllegalArgumentException {
        return ebeDivide(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public RealVector ebeMultiply(double[] v) throws IllegalArgumentException {
        return ebeMultiply(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public double getDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final double diff = e.getValue() - v.getEntry(e.getIndex());
            d += diff * diff;
        }
        return FastMath.sqrt(d);
    }

    /** {@inheritDoc} */
    public double getNorm() {
        double sum = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            final double value = e.getValue();
            sum += value * value;
        }
        return FastMath.sqrt(sum);
    }

    /** {@inheritDoc} */
    public double getL1Norm() {
        double norm = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            norm += FastMath.abs(e.getValue());
        }
        return norm;
    }

    /** {@inheritDoc} */
    public double getLInfNorm() {
        double norm = 0;
        Iterator<Entry> it = sparseIterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            norm = FastMath.max(norm, FastMath.abs(e.getValue()));
        }
        return norm;
    }

    /** {@inheritDoc} */
    public double getDistance(double[] v) throws IllegalArgumentException {
        return getDistance(new ArrayRealVector(v,false));
    }

    /** {@inheritDoc} */
    public double getL1Distance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d += FastMath.abs(e.getValue() - v.getEntry(e.getIndex()));
        }
        return d;
    }

    /** {@inheritDoc} */
    public double getL1Distance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d += FastMath.abs(e.getValue() - v[e.getIndex()]);
        }
        return d;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(RealVector v) throws IllegalArgumentException {
        checkVectorDimensions(v);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d = FastMath.max(FastMath.abs(e.getValue() - v.getEntry(e.getIndex())), d);
        }
        return d;
    }

    /** {@inheritDoc} */
    public double getLInfDistance(double[] v) throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double d = 0;
        Iterator<Entry> it = iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            d = FastMath.max(FastMath.abs(e.getValue() - v[e.getIndex()]), d);
        }
        return d;
    }

    /** Get the index of the minimum entry.
     * @return index of the minimum entry or -1 if vector length is 0
     * or all entries are NaN
     */
    public int getMinIndex() {
        int minIndex    = -1;
        double minValue = Double.POSITIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.getValue() <= minValue) {
                minIndex = entry.getIndex();
                minValue = entry.getValue();
            }
        }
        return minIndex;
    }

    /** Get the value of the minimum entry.
     * @return value of the minimum entry or NaN if all entries are NaN
     */
    public double getMinValue() {
        final int minIndex = getMinIndex();
        return minIndex < 0 ? Double.NaN : getEntry(minIndex);
    }

    /** Get the index of the maximum entry.
     * @return index of the maximum entry or -1 if vector length is 0
     * or all entries are NaN
     */
    public int getMaxIndex() {
        int maxIndex    = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        Iterator<Entry> iterator = iterator();
        while (iterator.hasNext()) {
            final Entry entry = iterator.next();
            if (entry.getValue() >= maxValue) {
                maxIndex = entry.getIndex();
                maxValue = entry.getValue();
            }
        }
        return maxIndex;
    }

    /** Get the value of the maximum entry.
     * @return value of the maximum entry or NaN if all entries are NaN
     */
    public double getMaxValue() {
        final int maxIndex = getMaxIndex();
        return maxIndex < 0 ? Double.NaN : getEntry(maxIndex);
    }

    /** {@inheritDoc} */
    public RealVector mapAbs() {
        return copy().mapAbsToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAbsToSelf() {
        try {
            return mapToSelf(ComposableFunction.ABS);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapAcos() {
        return copy().mapAcosToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAcosToSelf() {
        try {
            return mapToSelf(ComposableFunction.ACOS);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapAsin() {
        return copy().mapAsinToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAsinToSelf() {
        try {
            return mapToSelf(ComposableFunction.ASIN);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapAtan() {
        return copy().mapAtanToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapAtanToSelf() {
        try {
            return mapToSelf(ComposableFunction.ATAN);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapCbrt() {
        return copy().mapCbrtToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCbrtToSelf() {
        try {
            return mapToSelf(ComposableFunction.CBRT);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapCeil() {
        return copy().mapCeilToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCeilToSelf() {
        try {
            return mapToSelf(ComposableFunction.CEIL);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapCos() {
        return copy().mapCosToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCosToSelf() {
        try {
            return mapToSelf(ComposableFunction.COS);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapCosh() {
        return copy().mapCoshToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapCoshToSelf() {
        try {
            return mapToSelf(ComposableFunction.COSH);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapDivide(double d) {
        return copy().mapDivideToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapDivideToSelf(double d){
        try {
            return mapToSelf(BinaryFunction.DIVIDE.fix2ndArgument(d));
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapExp() {
        return copy().mapExpToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapExpToSelf() {
        try {
            return mapToSelf(ComposableFunction.EXP);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapExpm1() {
        return copy().mapExpm1ToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapExpm1ToSelf() {
        try {
            return mapToSelf(ComposableFunction.EXPM1);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapFloor() {
        return copy().mapFloorToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapFloorToSelf() {
        try {
            return mapToSelf(ComposableFunction.FLOOR);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapInv() {
        return copy().mapInvToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapInvToSelf() {
        try {
            return mapToSelf(ComposableFunction.INVERT);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapLog() {
        return copy().mapLogToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapLogToSelf() {
        try {
            return mapToSelf(ComposableFunction.LOG);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapLog10() {
        return copy().mapLog10ToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapLog10ToSelf() {
        try {
            return mapToSelf(ComposableFunction.LOG10);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapLog1p() {
        return copy().mapLog1pToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapLog1pToSelf() {
        try {
            return mapToSelf(ComposableFunction.LOG1P);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapMultiply(double d) {
        return copy().mapMultiplyToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapMultiplyToSelf(double d){
        try {
            return mapToSelf(BinaryFunction.MULTIPLY.fix1stArgument(d));
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapPow(double d) {
        return copy().mapPowToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapPowToSelf(double d){
        try {
            return mapToSelf(BinaryFunction.POW.fix2ndArgument(d));
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapRint() {
        return copy().mapRintToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapRintToSelf() {
        try {
            return mapToSelf(ComposableFunction.RINT);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapSignum() {
        return copy().mapSignumToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSignumToSelf() {
        try {
            return mapToSelf(ComposableFunction.SIGNUM);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapSin() {
        return copy().mapSinToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSinToSelf() {
        try {
            return mapToSelf(ComposableFunction.SIN);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapSinh() {
        return copy().mapSinhToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSinhToSelf() {
        try {
            return mapToSelf(ComposableFunction.SINH);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapSqrt() {
        return copy().mapSqrtToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapSqrtToSelf() {
        try {
            return mapToSelf(ComposableFunction.SQRT);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapSubtract(double d) {
        return copy().mapSubtractToSelf(d);
    }

    /** {@inheritDoc} */
    public RealVector mapSubtractToSelf(double d){
        return mapAddToSelf(-d);
    }

    /** {@inheritDoc} */
    public RealVector mapTan() {
        return copy().mapTanToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapTanToSelf() {
        try {
            return mapToSelf(ComposableFunction.TAN);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapTanh() {
        return copy().mapTanhToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapTanhToSelf() {
        try {
            return mapToSelf(ComposableFunction.TANH);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealVector mapUlp() {
        return copy().mapUlpToSelf();
    }

    /** {@inheritDoc} */
    public RealVector mapUlpToSelf() {
        try {
            return mapToSelf(ComposableFunction.ULP);
        } catch (FunctionEvaluationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(RealVector v) throws IllegalArgumentException {
        RealMatrix product;
        if (v instanceof SparseRealVector || this instanceof SparseRealVector) {
            product = new OpenMapRealMatrix(this.getDimension(), v.getDimension());
        } else {
            product = new Array2DRowRealMatrix(this.getDimension(), v.getDimension());
        }
        Iterator<Entry> thisIt = sparseIterator();
        Entry thisE = null;
        while (thisIt.hasNext() && (thisE = thisIt.next()) != null) {
            Iterator<Entry> otherIt = v.sparseIterator();
            Entry otherE = null;
            while (otherIt.hasNext() && (otherE = otherIt.next()) != null) {
                product.setEntry(thisE.getIndex(), otherE.getIndex(),
                                 thisE.getValue() * otherE.getValue());
            }
        }

        return product;

    }

    /** {@inheritDoc} */
    public RealMatrix outerProduct(double[] v) throws IllegalArgumentException {
        return outerProduct(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public RealVector projection(double[] v) throws IllegalArgumentException {
        return projection(new ArrayRealVector(v, false));
    }

    /** {@inheritDoc} */
    public void set(double value) {
        Iterator<Entry> it = iterator();
        Entry e = null;
        while (it.hasNext() && (e = it.next()) != null) {
            e.setValue(value);
        }
    }

    /** {@inheritDoc} */
    public double[] toArray() {
        int dim = getDimension();
        double[] values = new double[dim];
        for (int i = 0; i < dim; i++) {
            values[i] = getEntry(i);
        }
        return values;
    }

    /** {@inheritDoc} */
    public double[] getData() {
        return toArray();
    }

    /** {@inheritDoc} */
    public RealVector unitVector() {
        RealVector copy = copy();
        copy.unitize();
        return copy;
    }

    /** {@inheritDoc} */
    public void unitize() {
        mapDivideToSelf(getNorm());
    }

    /** {@inheritDoc} */
    public Iterator<Entry> sparseIterator() {
        return new SparseEntryIterator();
    }

    /** {@inheritDoc} */
    public Iterator<Entry> iterator() {
        final int dim = getDimension();
        return new Iterator<Entry>() {

            /** Current index. */
            private int i = 0;

            /** Current entry. */
            private EntryImpl e = new EntryImpl();

            /** {@inheritDoc} */
            public boolean hasNext() {
                return i < dim;
            }

            /** {@inheritDoc} */
            public Entry next() {
                e.setIndex(i++);
                return e;
            }

            /** {@inheritDoc} */
            public void remove() {
                throw new MathUnsupportedOperationException();
            }
        };
    }

    /** {@inheritDoc} */
    public RealVector map(UnivariateRealFunction function) throws FunctionEvaluationException {
        return copy().mapToSelf(function);
    }

    /** {@inheritDoc} */
    public RealVector mapToSelf(UnivariateRealFunction function) throws FunctionEvaluationException {
        Iterator<Entry> it = (function.value(0) == 0) ? sparseIterator() : iterator();
        Entry e;
        while (it.hasNext() && (e = it.next()) != null) {
            e.setValue(function.value(e.getValue()));
        }
        return this;
    }

    /** An entry in the vector. */
    protected class EntryImpl extends Entry {

        /** Simple constructor. */
        public EntryImpl() {
            setIndex(0);
        }

        /** {@inheritDoc} */
        @Override
        public double getValue() {
            return getEntry(getIndex());
        }

        /** {@inheritDoc} */
        @Override
        public void setValue(double newValue) {
            setEntry(getIndex(), newValue);
        }
    }

    /**
     * This class should rare be used, but is here to provide
     * a default implementation of sparseIterator(), which is implemented
     * by walking over the entries, skipping those whose values are the default one.
     *
     * Concrete subclasses which are SparseVector implementations should
     * make their own sparse iterator, not use this one.
     *
     * This implementation might be useful for ArrayRealVector, when expensive
     * operations which preserve the default value are to be done on the entries,
     * and the fraction of non-default values is small (i.e. someone took a
     * SparseVector, and passed it into the copy-constructor of ArrayRealVector)
     */
    protected class SparseEntryIterator implements Iterator<Entry> {

        /** Dimension of the vector. */
        private final int dim;

        /** last entry returned by {@link #next()} */
        private EntryImpl current;

        /** Next entry for {@link #next()} to return. */
        private EntryImpl next;

        /** Simple constructor. */
        protected SparseEntryIterator() {
            dim = getDimension();
            current = new EntryImpl();
            next = new EntryImpl();
            if (next.getValue() == 0) {
                advance(next);
            }
        }

        /** Advance an entry up to the next nonzero one.
         * @param e entry to advance
         */
        protected void advance(EntryImpl e) {
            if (e == null) {
                return;
            }
            do {
                e.setIndex(e.getIndex() + 1);
            } while (e.getIndex() < dim && e.getValue() == 0);
            if (e.getIndex() >= dim) {
                e.setIndex(-1);
            }
        }

        /** {@inheritDoc} */
        public boolean hasNext() {
            return next.getIndex() >= 0;
        }

        /** {@inheritDoc} */
        public Entry next() {
            int index = next.getIndex();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            current.setIndex(index);
            advance(next);
            return current;
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new MathUnsupportedOperationException();
        }
    }

}

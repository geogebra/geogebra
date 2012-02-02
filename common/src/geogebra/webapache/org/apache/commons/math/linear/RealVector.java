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
/* This file was modified by GeoGebra Inc. */
package org.apache.commons.math.linear;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

import java.util.Iterator;




/**
 * Interface defining a real-valued vector with basic algebraic operations.
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
 * @version $Revision: 902214 $ $Date: 2010-01-22 13:40:28 -0500 (Fri, 22 Jan 2010) $
 * @since 2.0
 */
public interface RealVector {

    /**
     * Acts as if it is implemented as:
     * Entry e = null;
     * for(Iterator<Entry> it = iterator(); it.hasNext(); e = it.next()) {
     *   e.setValue(function.value(e.getValue()));
     * }
     * @param function to apply to each successive entry
     * @return this vector
     * @throws FunctionEvaluationException if function throws it on application to any entry
     */
    RealVector mapToSelf(UnivariateRealFunction function) throws FunctionEvaluationException;

    /**
     * Acts as if implemented as:
     * return copy().map(function);
     * @param function to apply to each successive entry
     * @return a new vector
     * @throws FunctionEvaluationException if function throws it on application to any entry
     */
    RealVector map(UnivariateRealFunction function) throws FunctionEvaluationException;

    /** Class representing a modifiable entry in the vector. */
    public abstract class Entry {

        /** Index of the entry. */
        private int index;

        /** Get the value of the entry.
         * @return value of the entry
         */
        public abstract double getValue();

        /** Set the value of the entry.
         * @param value new value for the entry
         */
        public abstract void setValue(double value);

        /** Get the index of the entry.
         * @return index of the entry
         */
        public int getIndex() {
            return index;
        }

        /** Set the index of the entry.
         * @param index new index for the entry
         */
        public void setIndex(int index) {
            this.index = index;
        }

    }

    /**
     * Generic dense iterator - starts with index == zero, and hasNext() == true until index == getDimension();
     * @return a dense iterator
     */
    Iterator<Entry> iterator();

    /**
     * Specialized implementations may choose to not iterate over all dimensions, either because those values are
     * unset, or are equal to defaultValue(), or are small enough to be ignored for the purposes of iteration.
     * No guarantees are made about order of iteration.
     * In dense implementations, this method will often delegate to {@link #iterator()}
     * @return a sparse iterator
     */
    Iterator<Entry> sparseIterator();

    /**
     * Returns a (deep) copy of this.
     * @return vector copy
     */
    RealVector copy();

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector add(RealVector v)
        throws IllegalArgumentException;

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector add(double[] v)
        throws IllegalArgumentException;

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector subtract(RealVector v)
        throws IllegalArgumentException;

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector subtract(double[] v)
        throws IllegalArgumentException;

    /**
     * Map an addition operation to each entry.
     * @param d value to be added to each entry
     * @return this + d
     */
    RealVector mapAdd(double d);

    /**
     * Map an addition operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to be added to each entry
     * @return for convenience, return this
     */
    RealVector mapAddToSelf(double d);

    /**
     * Map a subtraction operation to each entry.
     * @param d value to be subtracted to each entry
     * @return this - d
     */
    RealVector mapSubtract(double d);

    /**
     * Map a subtraction operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to be subtracted to each entry
     * @return for convenience, return this
     */
    RealVector mapSubtractToSelf(double d);

    /**
     * Map a multiplication operation to each entry.
     * @param d value to multiply all entries by
     * @return this * d
     */
    RealVector mapMultiply(double d);

    /**
     * Map a multiplication operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to multiply all entries by
     * @return for convenience, return this
     */
    RealVector mapMultiplyToSelf(double d);

    /**
     * Map a division operation to each entry.
     * @param d value to divide all entries by
     * @return this / d
     */
    RealVector mapDivide(double d);

    /**
     * Map a division operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to divide all entries by
     * @return for convenience, return this
     */
    RealVector mapDivideToSelf(double d);

    /**
     * Map a power operation to each entry.
     * @param d value to raise all entries to
     * @return this ^ d
     */
    RealVector mapPow(double d);

    /**
     * Map a power operation to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @param d value to raise all entries to
     * @return for convenience, return this
     */
    RealVector mapPowToSelf(double d);

    /**
     * Map the {@link Math#exp(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapExp();

    /**
     * Map the {@link Math#exp(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapExpToSelf();

    /**
     * Map the {@link Math#expm1(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapExpm1();

    /**
     * Map the {@link Math#expm1(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapExpm1ToSelf();

    /**
     * Map the {@link Math#log(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapLog();

    /**
     * Map the {@link Math#log(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapLogToSelf();

    /**
     * Map the {@link Math#log10(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapLog10();

    /**
     * Map the {@link Math#log10(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapLog10ToSelf();

    /**
     * Map the {@link Math#log1p(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapLog1p();

    /**
     * Map the {@link Math#log1p(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapLog1pToSelf();

    /**
     * Map the {@link Math#cosh(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapCosh();

    /**
     * Map the {@link Math#cosh(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapCoshToSelf();

    /**
     * Map the {@link Math#sinh(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapSinh();

    /**
     * Map the {@link Math#sinh(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapSinhToSelf();

    /**
     * Map the {@link Math#tanh(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapTanh();

    /**
     * Map the {@link Math#tanh(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapTanhToSelf();

    /**
     * Map the {@link Math#cos(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapCos();

    /**
     * Map the {@link Math#cos(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapCosToSelf();

    /**
     * Map the {@link Math#sin(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapSin();

    /**
     * Map the {@link Math#sin(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapSinToSelf();

    /**
     * Map the {@link Math#tan(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapTan();

    /**
     * Map the {@link Math#tan(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapTanToSelf();

    /**
     * Map the {@link Math#acos(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapAcos();

    /**
     * Map the {@link Math#acos(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapAcosToSelf();

    /**
     * Map the {@link Math#asin(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapAsin();

    /**
     * Map the {@link Math#asin(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapAsinToSelf();

    /**
     * Map the {@link Math#atan(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapAtan();

    /**
     * Map the {@link Math#atan(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapAtanToSelf();

    /**
     * Map the 1/x function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapInv();

    /**
     * Map the 1/x function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapInvToSelf();

    /**
     * Map the {@link Math#abs(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapAbs();

    /**
     * Map the {@link Math#abs(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapAbsToSelf();

    /**
     * Map the {@link Math#sqrt(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapSqrt();

    /**
     * Map the {@link Math#sqrt(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapSqrtToSelf();

    /**
     * Map the {@link Math#cbrt(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapCbrt();

    /**
     * Map the {@link Math#cbrt(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapCbrtToSelf();

    /**
     * Map the {@link Math#ceil(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapCeil();

    /**
     * Map the {@link Math#ceil(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapCeilToSelf();

    /**
     * Map the {@link Math#floor(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapFloor();

    /**
     * Map the {@link Math#floor(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapFloorToSelf();

    /**
     * Map the {@link Math#rint(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapRint();

    /**
     * Map the {@link Math#rint(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapRintToSelf();

    /**
     * Map the {@link Math#signum(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapSignum();

    /**
     * Map the {@link Math#signum(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapSignumToSelf();

    /**
     * Map the {@link Math#ulp(double)} function to each entry.
     * @return a vector containing the result of applying the function to each entry
     */
    RealVector mapUlp();

    /**
     * Map the {@link Math#ulp(double)} function to each entry.
     * <p>The instance <strong>is</strong> changed by this method.</p>
     * @return for convenience, return this
     */
    RealVector mapUlpToSelf();

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector ebeMultiply(RealVector v) throws IllegalArgumentException;

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector ebeMultiply(double[] v) throws IllegalArgumentException;

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector ebeDivide(RealVector v) throws IllegalArgumentException;

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector ebeDivide(double[] v) throws IllegalArgumentException;

    /**
     * Returns vector entries as a double array.
     * @return double array of entries
     */
     double[] getData();

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    double dotProduct(RealVector v)
        throws IllegalArgumentException;

    /**
     * Compute the dot product.
     * @param v vector with which dot product should be computed
     * @return the scalar dot product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    double dotProduct(double[] v)
        throws IllegalArgumentException;

    /**
     * Returns the L<sub>2</sub> norm of the vector.
     * <p>The L<sub>2</sub> norm is the root of the sum of
     * the squared elements.</p>
     * @return norm
     * @see #getL1Norm()
     * @see #getLInfNorm()
     * @see #getDistance(RealVector)
     */
    double getNorm();

    /**
     * Returns the L<sub>1</sub> norm of the vector.
     * <p>The L<sub>1</sub> norm is the sum of the absolute
     * values of elements.</p>
     * @return norm
     * @see #getNorm()
     * @see #getLInfNorm()
     * @see #getL1Distance(RealVector)
     */
    double getL1Norm();

    /**
     * Returns the L<sub>&infin;</sub> norm of the vector.
     * <p>The L<sub>&infin;</sub> norm is the max of the absolute
     * values of elements.</p>
     * @return norm
     * @see #getNorm()
     * @see #getL1Norm()
     * @see #getLInfDistance(RealVector)
     */
    double getLInfNorm();

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * elements differences, or euclidian distance.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getL1Distance(RealVector)
     * @see #getLInfDistance(RealVector)
     * @see #getNorm()
     */
    double getDistance(RealVector v)
        throws IllegalArgumentException;

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * elements differences, or euclidian distance.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getL1Distance(double[])
     * @see #getLInfDistance(double[])
     * @see #getNorm()
     */
    double getDistance(double[] v)
        throws IllegalArgumentException;

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(RealVector)
     * @see #getLInfDistance(RealVector)
     * @see #getL1Norm()
     */
    double getL1Distance(RealVector v)
        throws IllegalArgumentException;

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(double[])
     * @see #getLInfDistance(double[])
     * @see #getL1Norm()
     */
    double getL1Distance(double[] v)
        throws IllegalArgumentException;

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>&infin;</sub> norm, i.e. the max of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(RealVector)
     * @see #getL1Distance(RealVector)
     * @see #getLInfNorm()
     */
    double getLInfDistance(RealVector v)
        throws IllegalArgumentException;

    /**
     * Distance between two vectors.
     * <p>This method computes the distance consistent with
     * L<sub>&infin;</sub> norm, i.e. the max of the absolute values of
     * elements differences.</p>
     * @param v vector to which distance is requested
     * @return distance between two vectors.
     * @exception IllegalArgumentException if v is not the same size as this
     * @see #getDistance(double[])
     * @see #getL1Distance(double[])
     * @see #getLInfNorm()
     */
    double getLInfDistance(double[] v)
        throws IllegalArgumentException;

    /** Creates a unit vector pointing in the direction of this vector.
     * <p>The instance is not changed by this method.</p>
     * @return a unit vector pointing in direction of this vector
     * @exception ArithmeticException if the norm is null
     */
    RealVector unitVector();

    /** Converts this vector into a unit vector.
     * <p>The instance itself is changed by this method.</p>
     * @exception ArithmeticException if the norm is null
     */
    void unitize();

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector projection(RealVector v)
        throws IllegalArgumentException;

    /** Find the orthogonal projection of this vector onto another vector.
     * @param v vector onto which instance must be projected
     * @return projection of the instance onto v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    RealVector projection(double[] v)
        throws IllegalArgumentException;

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    RealMatrix outerProduct(RealVector v)
        throws IllegalArgumentException;

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    RealMatrix outerProduct(double[] v)
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
     * @see #setEntry(int, double)
     */
    double getEntry(int index)
        throws MatrixIndexException;

    /**
     * Set a single element.
     * @param index element index.
     * @param value new value for the element.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     * @see #getEntry(int)
     */
    void setEntry(int index, double value)
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
    RealVector append(RealVector v);

    /**
     * Construct a vector by appending a double to this vector.
     * @param d double to append.
     * @return a new vector
     */
    RealVector append(double d);

    /**
     * Construct a vector by appending a double array to this vector.
     * @param a double array to append.
     * @return a new vector
     */
    RealVector append(double[] a);

    /**
     * Get a subvector from consecutive elements.
     * @param index index of first element.
     * @param n number of elements to be retrieved.
     * @return a vector containing n elements.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     */
    RealVector getSubVector(int index, int n)
        throws MatrixIndexException;

    /**
     * Set a set of consecutive elements.
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     * @see #setSubVector(int, double[])
     */
    void setSubVector(int index, RealVector v)
        throws MatrixIndexException;

    /**
     * Set a set of consecutive elements.
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @exception MatrixIndexException if the index is
     * inconsistent with vector size
     * @see #setSubVector(int, RealVector)
     */
    void setSubVector(int index, double[] v)
        throws MatrixIndexException;

    /**
     * Set all elements to a single value.
     * @param value single value to set for all elements
     */
    void set(double value);

    /**
     * Convert the vector to a double array.
     * <p>The array is independent from vector data, it's elements
     * are copied.</p>
     * @return array containing a copy of vector elements
     */
    double[] toArray();

    /**
     * Returns true if any coordinate of this vector is NaN; false otherwise
     * @return  true if any coordinate of this vector is NaN; false otherwise
     */
    boolean isNaN();

    /**
     * Returns true if any coordinate of this vector is infinite and none are NaN;
     * false otherwise
     * @return  true if any coordinate of this vector is infinite and none are NaN;
     * false otherwise
     */
    boolean isInfinite();

}

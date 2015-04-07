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
package org.apache.commons.math.stat.descriptive.rank;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic;
import org.apache.commons.math.util.FastMath;

/**
 * Provides percentile computation.
 * <p>
 * There are several commonly used methods for estimating percentiles (a.k.a.
 * quantiles) based on sample data.  For large samples, the different methods
 * agree closely, but when sample sizes are small, different methods will give
 * significantly different results.  The algorithm implemented here works as follows:
 * <ol>
 * <li>Let <code>n</code> be the length of the (sorted) array and
 * <code>0 < p <= 100</code> be the desired percentile.</li>
 * <li>If <code> n = 1 </code> return the unique array element (regardless of
 * the value of <code>p</code>); otherwise </li>
 * <li>Compute the estimated percentile position
 * <code> pos = p * (n + 1) / 100</code> and the difference, <code>d</code>
 * between <code>pos</code> and <code>floor(pos)</code> (i.e. the fractional
 * part of <code>pos</code>).  If <code>pos >= n</code> return the largest
 * element in the array; otherwise</li>
 * <li>Let <code>lower</code> be the element in position
 * <code>floor(pos)</code> in the array and let <code>upper</code> be the
 * next element in the array.  Return <code>lower + d * (upper - lower)</code>
 * </li>
 * </ol></p>
 * <p>
 * To compute percentiles, the data must be at least partially ordered.  Input
 * arrays are copied and recursively partitioned using an ordering definition.
 * The ordering used by <code>Arrays.sort(double[])</code> is the one determined
 * by {@link java.lang.Double#compareTo(Double)}.  This ordering makes
 * <code>Double.NaN</code> larger than any other value (including
 * <code>Double.POSITIVE_INFINITY</code>).  Therefore, for example, the median
 * (50th percentile) of
 * <code>{0, 1, 2, 3, 4, Double.NaN}</code> evaluates to <code>2.5.</code></p>
 * <p>
 * Since percentile estimation usually involves interpolation between array
 * elements, arrays containing  <code>NaN</code> or infinite values will often
 * result in <code>NaN<code> or infinite values returned.</p>
 * <p>
 * Since 2.2, Percentile implementation uses only selection instead of complete
 * sorting and caches selection algorithm state between calls to the various
 * {@code evaluate} methods when several percentiles are to be computed on the same data.
 * This greatly improves efficiency, both for single percentile and multiple
 * percentiles computations. However, it also induces a need to be sure the data
 * at one call to {@code evaluate} is the same as the data with the cached algorithm
 * state from the previous calls. Percentile does this by checking the array reference
 * itself and a checksum of its content by default. If the user already knows he calls
 * {@code evaluate} on an immutable array, he can save the checking time by calling the
 * {@code evaluate} methods that do <em>not</em>
 * </p>
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 *
 * @version $Revision: 1006299 $ $Date: 2010-10-10 16:47:17 +0200 (dim. 10 oct. 2010) $
 */
public class Percentile extends AbstractUnivariateStatistic implements Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -8091216485095130416L;

    /** Minimum size under which we use a simple insertion sort rather than Hoare's select. */
    private static final int MIN_SELECT_SIZE = 15;

    /** Maximum number of partitioning pivots cached (each level double the number of pivots). */
    private static final int MAX_CACHED_LEVELS = 10;

    /** Determines what percentile is computed when evaluate() is activated
     * with no quantile argument */
    private double quantile = 0.0;

    /** Cached pivots. */
    private int[] cachedPivots;

    /**
     * Constructs a Percentile with a default quantile
     * value of 50.0.
     */
    public Percentile() {
        this(50.0);
    }

    /**
     * Constructs a Percentile with the specific quantile value.
     * @param p the quantile
     * @throws IllegalArgumentException  if p is not greater than 0 and less
     * than or equal to 100
     */
    public Percentile(final double p) {
        setQuantile(p);
        cachedPivots = null;
    }

    /**
     * Copy constructor, creates a new {@code Percentile} identical
     * to the {@code original}
     *
     * @param original the {@code Percentile} instance to copy
     */
    public Percentile(Percentile original) {
        copy(original, this);
    }

    /** {@inheritDoc} */
    @Override
    public void setData(final double[] values) {
        if (values == null) {
            cachedPivots = null;
        } else {
            cachedPivots = new int[(0x1 << MAX_CACHED_LEVELS) - 1];
            Arrays.fill(cachedPivots, -1);
        }
        super.setData(values);
    }

    /** {@inheritDoc} */
    @Override
    public void setData(final double[] values, final int begin, final int length) {
        if (values == null) {
            cachedPivots = null;
        } else {
            cachedPivots = new int[(0x1 << MAX_CACHED_LEVELS) - 1];
            Arrays.fill(cachedPivots, -1);
        }
        super.setData(values, begin, length);
    }

    /**
     * Returns the result of evaluating the statistic over the stored data.
     * <p>
     * The stored array is the one which was set by previous calls to
     * </p>
     * @param p the percentile value to compute
     * @return the value of the statistic applied to the stored data
     */
    public double evaluate(final double p) {
        return evaluate(getDataRef(), p);
    }

    /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.</p>
     * <p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>values</code> has length
     * <code>0</code></li>
     * <li>Returns (for any value of <code>p</code>) <code>values[0]</code>
     *  if <code>values</code> has length <code>1</code></li>
     * <li>Throws <code>IllegalArgumentException</code> if <code>values</code>
     * is null or p is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100) </li>
     * </ul></p>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values input array of values
     * @param p the percentile value to compute
     * @return the percentile value or Double.NaN if the array is empty
     * @throws IllegalArgumentException if <code>values</code> is null
     *     or p is invalid
     */
    public double evaluate(final double[] values, final double p) {
        test(values, 0, 0);
        return evaluate(values, 0, values.length, p);
    }

    /**
     * Returns an estimate of the <code>quantile</code>th percentile of the
     * designated values in the <code>values</code> array.  The quantile
     * estimated is determined by the <code>quantile</code> property.
     * <p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>length = 0</code></li>
     * <li>Returns (for any value of <code>quantile</code>)
     * <code>values[begin]</code> if <code>length = 1 </code></li>
     * <li>Throws <code>IllegalArgumentException</code> if <code>values</code>
     * is null,  or <code>start</code> or <code>length</code>
     * is invalid</li>
     * </ul></p>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values the input array
     * @param start index of the first array element to include
     * @param length the number of elements to include
     * @return the percentile value
     * @throws IllegalArgumentException if the parameters are not valid
     *
     */
    @Override
    public double evaluate( final double[] values, final int start, final int length) {
        return evaluate(values, start, length, quantile);
    }

     /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array, starting with the element in (0-based)
     * position <code>begin</code> in the array and including <code>length</code>
     * values.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.</p>
     * <p>
     * <ul>
     * <li>Returns <code>Double.NaN</code> if <code>length = 0</code></li>
     * <li>Returns (for any value of <code>p</code>) <code>values[begin]</code>
     *  if <code>length = 1 </code></li>
     * <li>Throws <code>IllegalArgumentException</code> if <code>values</code>
     *  is null , <code>begin</code> or <code>length</code> is invalid, or
     * <code>p</code> is not a valid quantile value (p must be greater than 0
     * and less than or equal to 100)</li>
     * </ul></p>
     * <p>
     * See {@link Percentile} for a description of the percentile estimation
     * algorithm used.</p>
     *
     * @param values array of input values
     * @param p  the percentile to compute
     * @param begin  the first (0-based) element to include in the computation
     * @param length  the number of array elements to include
     * @return  the percentile value
     * @throws IllegalArgumentException if the parameters are not valid or the
     * input array is null
     */
    public double evaluate(final double[] values, final int begin,
            final int length, final double p) {

        test(values, begin, length);

        if ((p > 100) || (p <= 0)) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p);
        }
        if (length == 0) {
            return Double.NaN;
        }
        if (length == 1) {
            return values[begin]; // always return single value for n = 1
        }
        double n = length;
        double pos = p * (n + 1) / 100;
        double fpos = FastMath.floor(pos);
        int intPos = (int) fpos;
        double dif = pos - fpos;
        double[] work;
        int[] pivotsHeap;
        if (values == getDataRef()) {
            work = getDataRef();
            pivotsHeap = cachedPivots;
        } else {
            work = new double[length];
            System.arraycopy(values, begin, work, 0, length);
            pivotsHeap = new int[(0x1 << MAX_CACHED_LEVELS) - 1];
            Arrays.fill(pivotsHeap, -1);
        }

        if (pos < 1) {
            return select(work, pivotsHeap, 0);
        }
        if (pos >= n) {
            return select(work, pivotsHeap, length - 1);
        }
        double lower = select(work, pivotsHeap, intPos - 1);
        double upper = select(work, pivotsHeap, intPos);
        return lower + dif * (upper - lower);
    }

    /**
     * Select the k<sup>th</sup> smallest element from work array
     * @param work work array (will be reorganized during the call)
     * @param pivotsHeap set of pivot index corresponding to elements that
     * are already at their sorted location, stored as an implicit heap
     * (i.e. a sorted binary tree stored in a flat array, where the
     * children of a node at index n are at indices 2n+1 for the left
     * child and 2n+2 for the right child, with 0-based indices)
     * @param k index of the desired element
     * @return k<sup>th</sup> smallest element
     */
    private double select(final double[] work, final int[] pivotsHeap, final int k) {

        int begin = 0;
        int end   = work.length;
        int node  = 0;

        while (end - begin > MIN_SELECT_SIZE) {

            final int pivot;
            if ((node < pivotsHeap.length) && (pivotsHeap[node] >= 0)) {
                // the pivot has already been found in a previous call
                // and the array has already been partitioned around it
                pivot = pivotsHeap[node];
            } else {
                // select a pivot and partition work array around it
                pivot = partition(work, begin, end, medianOf3(work, begin, end));
                if (node < pivotsHeap.length) {
                    pivotsHeap[node] =  pivot;
                }
            }

            if (k == pivot) {
                // the pivot was exactly the element we wanted
                return work[k];
            } else if (k < pivot) {
                // the element is in the left partition
                end  = pivot;
                node = Math.min(2 * node + 1, pivotsHeap.length); // the min is here to avoid integer overflow
            } else {
                // the element is in the right partition
                begin = pivot + 1;
                node  = Math.min(2 * node + 2, pivotsHeap.length); // the min is here to avoid integer overflow
            }

        }

        // the element is somewhere in the small sub-array
        // sort the sub-array using insertion sort
        insertionSort(work, begin, end);
        return work[k];

    }

    /** Select a pivot index as the median of three
     * @param work data array
     * @param begin index of the first element of the slice
     * @param end index after the last element of the slice
     * @return the index of the median element chosen between the
     * first, the middle and the last element of the array slice
     */
    int medianOf3(final double[] work, final int begin, final int end) {

        final int inclusiveEnd = end - 1;
        final int    middle    = begin + (inclusiveEnd - begin) / 2;
        final double wBegin    = work[begin];
        final double wMiddle   = work[middle];
        final double wEnd      = work[inclusiveEnd];

        if (wBegin < wMiddle) {
            if (wMiddle < wEnd) {
                return middle;
            } else {
                return (wBegin < wEnd) ? inclusiveEnd : begin;
            }
        } else {
            if (wBegin < wEnd) {
                return begin;
            } else {
                return (wMiddle < wEnd) ? inclusiveEnd : middle;
            }
        }

    }

    /**
     * Partition an array slice around a pivot
     * <p>
     * Partitioning exchanges array elements such that all elements
     * smaller than pivot are before it and all elements larger than
     * pivot are after it
     * </p>
     * @param work data array
     * @param begin index of the first element of the slice
     * @param end index after the last element of the slice
     * @param pivot initial index of the pivot
     * @return index of the pivot after partition
     */
    private int partition(final double[] work, final int begin, final int end, final int pivot) {

        final double value = work[pivot];
        work[pivot] = work[begin];

        int i = begin + 1;
        int j = end - 1;
        while (i < j) {
            while ((i < j) && (work[j] >= value)) {
                --j;
            }
            while ((i < j) && (work[i] <= value)) {
                ++i;
            }

            if (i < j) {
                final double tmp = work[i];
                work[i++] = work[j];
                work[j--] = tmp;
            }
        }

        if ((i >= end) || (work[i] > value)) {
            --i;
        }
        work[begin] = work[i];
        work[i]     = value;
        return i;

    }

    /**
     * Sort in place a (small) array slice using insertion sort
     * @param work array to sort
     * @param begin index of the first element of the slice to sort
     * @param end index after the last element of the slice to sort
     */
    private void insertionSort(final double[] work, final int begin, final int end) {
        for (int j = begin + 1; j < end; j++) {
            final double saved = work[j];
            int i = j - 1;
            while ((i >= begin) && (saved < work[i])) {
                work[i + 1] = work[i];
                i--;
            }
            work[i + 1] = saved;
        }
    }

    /**
     * Returns the value of the quantile field (determines what percentile is
     * computed when evaluate() is called with no quantile argument).
     *
     * @return quantile
     */
    public double getQuantile() {
        return quantile;
    }

    /**
     * Sets the value of the quantile field (determines what percentile is
     * computed when evaluate() is called with no quantile argument).
     *
     * @param p a value between 0 < p <= 100
     * @throws IllegalArgumentException  if p is not greater than 0 and less
     * than or equal to 100
     */
    public void setQuantile(final double p) {
        if (p <= 0 || p > 100) {
            throw MathRuntimeException.createIllegalArgumentException(
                  LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p);
        }
        quantile = p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Percentile copy() {
        Percentile result = new Percentile();
        copy(this, result);
        return result;
    }

    /**
     * Copies source to dest.
     * <p>Neither source nor dest can be null.</p>
     *
     * @param source Percentile to copy
     * @param dest Percentile to copy to
     * @throws NullPointerException if either source or dest is null
     */
    public static void copy(Percentile source, Percentile dest) {
        dest.setData(source.getDataRef());
        if (source.cachedPivots != null) {
            System.arraycopy(source.cachedPivots, 0, dest.cachedPivots, 0, source.cachedPivots.length);
        }
        dest.quantile = source.quantile;
    }

}

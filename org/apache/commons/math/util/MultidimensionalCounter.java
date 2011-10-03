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

package org.apache.commons.math.util;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;

/**
 * Converter between unidimensional storage structure and multidimensional
 * conceptual structure.
 * This utility will convert from indices in a multidimensional structure
 * to the corresponding index in a one-dimensional array. For example,
 * assuming that the ranges (in 3 dimensions) of indices are 2, 4 and 3,
 * the following correspondences, between 3-tuples indices and unidimensional
 * indices, will hold:
 * <ul>
 *  <li>(0, 0, 0) corresponds to 0</li>
 *  <li>(0, 0, 1) corresponds to 1</li>
 *  <li>(0, 0, 2) corresponds to 2</li>
 *  <li>(0, 1, 0) corresponds to 3</li>
 *  <li>...</li>
 *  <li>(1, 0, 0) corresponds to 12</li>
 *  <li>...</li>
 *  <li>(1, 3, 2) corresponds to 23</li>
 * </ul>
 * @version $Revision$ $Date$
 * @since 2.2
 */
public class MultidimensionalCounter implements Iterable<Integer> {
    /**
     * Number of dimensions.
     */
    private final int dimension;
    /**
     * Offset for each dimension.
     */
    private final int[] uniCounterOffset;
    /**
     * Counter sizes.
     */
    private final int[] size;
    /**
     * Total number of (one-dimensional) slots.
     */
    private final int totalSize;
    /**
     * Index of last dimension.
     */
    private final int last;

    /**
     * Perform iteration over the multidimensional counter.
     */
    public class Iterator implements java.util.Iterator<Integer> {
        /**
         * Multidimensional counter.
         */
        private final int[] counter = new int[dimension];
        /**
         * Unidimensional counter.
         */
        private int count = -1;

        /**
         * Create an iterator
         * @see #iterator()
         */
        Iterator() {
            counter[last] = -1;
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasNext() {
            for (int i = 0; i < dimension; i++) {
                if (counter[i] != size[i] - 1) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return the unidimensional count after the counter has been
         * incremented by {@code 1}.
         */
        public Integer next() {
            for (int i = last; i >= 0; i--) {
                if (counter[i] == size[i] - 1) {
                    counter[i] = 0;
                } else {
                    ++counter[i];
                    break;
                }
            }

            return ++count;
        }

        /**
         * Get the current unidimensional counter slot.
         *
         * @return the index within the unidimensionl counter.
         */
        public int getCount() {
            return count;
        }
        /**
         * Get the current multidimensional counter slots.
         *
         * @return the indices within the multidimensional counter.
         */
        public int[] getCounts() {
            return /* Arrays.*/ copyOf(counter, dimension); // Java 1.5 does not support Arrays.copyOf()
        }

        /**
         * Get the current count in the selected dimension.
         *
         * @param dim Dimension index.
         * @return the count at the corresponding index for the current state
         * of the iterator.
         * @throws IndexOutOfBoundsException if {@code index} is not in the
         * correct interval (as defined by the length of the argument in the
         * {@link MultidimensionalCounter#MultidimensionalCounter(int[])
         * constructor of the enclosing class}).
         */
        public int getCount(int dim) {
            return counter[dim];
        }

        /**
         * @throws UnsupportedOperationException
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Create a counter.
     *
     * @param size Counter sizes (number of slots in each dimension).
     * @throws NotStrictlyPositiveException if one of the sizes is
     * negative or zero.
     */
    public MultidimensionalCounter(int ... size) {
        dimension = size.length;
        this.size = /* Arrays.*/ copyOf(size, dimension); // Java 1.5 does not support Arrays.copyOf()

        uniCounterOffset = new int[dimension];

        last = dimension - 1;
        int tS = size[last];
        for (int i = 0; i < last; i++) {
            int count = 1;
            for (int j = i + 1; j < dimension; j++) {
                count *= size[j];
            }
            uniCounterOffset[i] = count;
            tS *= size[i];
        }
        uniCounterOffset[last] = 0;

        if (tS <= 0) {
            throw new NotStrictlyPositiveException(tS);
        }

        totalSize = tS;
    }

    /**
     * Create an iterator over this counter.
     *
     * @return the iterator.
     */
    public Iterator iterator() {
        return new Iterator();
    }

    /**
     * Get the number of dimensions of the multidimensional counter.
     *
     * @return the number of dimensions.
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Convert to multidimensional counter.
     *
     * @param index Index in unidimensional counter.
     * @return the multidimensional counts.
     * @throws OutOfRangeException if {@code index} is not between
     * {@code 0} and the value returned by {@link #getSize()} (excluded).
     */
    public int[] getCounts(int index) {
        if (index < 0 ||
            index >= totalSize) {
            throw new OutOfRangeException(index, 0, totalSize);
        }

        final int[] indices = new int[dimension];

        int count = 0;
        for (int i = 0; i < last; i++) {
            int idx = 0;
            final int offset = uniCounterOffset[i];
            while (count <= index) {
                count += offset;
                ++idx;
            }
            --idx;
            count -= offset;
            indices[i] = idx;
        }

        int idx = 1;
        while (count < index) {
            count += idx;
            ++idx;
        }
        --idx;
        indices[last] = idx;

        return indices;
    }

    /**
     * Convert to unidimensional counter.
     *
     * @param c Indices in multidimensional counter.
     * @return the index within the unidimensionl counter.
     * @throws DimensionMismatchException if the size of {@code c}
     * does not match the size of the array given in the constructor.
     * @throws OutOfRangeException if a value of {@code c} is not in
     * the range of the corresponding dimension, as defined in the
     * {@link MultidimensionalCounter#MultidimensionalCounter(int...) constructor}.
     */
    public int getCount(int ... c) throws OutOfRangeException {
        if (c.length != dimension) {
            throw new DimensionMismatchException(c.length, dimension);
        }
        int count = 0;
        for (int i = 0; i < dimension; i++) {
            final int index = c[i];
            if (index < 0 ||
                index >= size[i]) {
                throw new OutOfRangeException(index, 0, size[i] - 1);
            }
            count += uniCounterOffset[i] * c[i];
        }
        return count + c[last];
    }

    /**
     * Get the total number of elements.
     *
     * @return the total size of the unidimensional counter.
     */
    public int getSize() {
        return totalSize;
    }
    /**
     * Get the number of multidimensional counter slots in each dimension.
     *
     * @return the sizes of the multidimensional counter in each dimension.
     */
    public int[] getSizes() {
        return /* Arrays.*/ copyOf(size, dimension); // Java 1.5 does not support Arrays.copyOf()
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            sb.append("[").append(getCount(i)).append("]");
        }
        return sb.toString();
    }

    /**
     * Java 1.5 does not support Arrays.copyOf()
     *
     * @param source the array to be copied
     * @param newLen the length of the copy to be returned
     * @return the copied array, truncated or padded as necessary.
     */
     private int[] copyOf(int[] source, int newLen) {
         int[] output = new int[newLen];
         System.arraycopy(source, 0, output, 0, Math.min(source.length, newLen));
         return output;
     }

}

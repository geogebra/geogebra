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

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * <p>
 * A variable length {@link DoubleArray} implementation that automatically
 * handles expanding and contracting its internal storage array as elements
 * are added and removed.
 * </p>
 * <p>
 *  The internal storage array starts with capacity determined by the
 * <code>initialCapacity</code> property, which can be set by the constructor.
 * The default initial capacity is 16.  Adding elements using
 * {@link #addElement(double)} appends elements to the end of the array.  When
 * there are no open entries at the end of the internal storage array, the
 * array is expanded.  The size of the expanded array depends on the
 * <code>expansionMode</code> and <code>expansionFactor</code> properties.
 * The <code>expansionMode</code> determines whether the size of the array is
 * multiplied by the <code>expansionFactor</code> (MULTIPLICATIVE_MODE) or if
 * the expansion is additive (ADDITIVE_MODE -- <code>expansionFactor</code>
 * storage locations added).  The default <code>expansionMode</code> is
 * MULTIPLICATIVE_MODE and the default <code>expansionFactor</code>
 * is 2.0.
 * </p>
 * <p>
 * The {@link #addElementRolling(double)} method adds a new element to the end
 * of the internal storage array and adjusts the "usable window" of the
 * internal array forward by one position (effectively making what was the
 * second element the first, and so on).  Repeated activations of this method
 * (or activation of {@link #discardFrontElements(int)}) will effectively orphan
 * the storage locations at the beginning of the internal storage array.  To
 * reclaim this storage, each time one of these methods is activated, the size
 * of the internal storage array is compared to the number of addressable
 * elements (the <code>numElements</code> property) and if the difference
 * is too large, the internal array is contracted to size
 * <code>numElements + 1.</code>  The determination of when the internal
 * storage array is "too large" depends on the <code>expansionMode</code> and
 * <code>contractionFactor</code> properties.  If  the <code>expansionMode</code>
 * is <code>MULTIPLICATIVE_MODE</code>, contraction is triggered when the
 * ratio between storage array length and <code>numElements</code> exceeds
 * <code>contractionFactor.</code>  If the <code>expansionMode</code>
 * is <code>ADDITIVE_MODE,</code> the number of excess storage locations
 * is compared to <code>contractionFactor.</code>
 * </p>
 * <p>
 * To avoid cycles of expansions and contractions, the
 * <code>expansionFactor</code> must not exceed the
 * <code>contractionFactor.</code> Constructors and mutators for both of these
 * properties enforce this requirement, throwing IllegalArgumentException if it
 * is violated.
 * </p>
 * @version $Revision: 1073474 $ $Date: 2011-02-22 20:52:17 +0100 (mar. 22 févr. 2011) $
 */
public class ResizableDoubleArray implements DoubleArray, Serializable {

    /** additive expansion mode */
    public static final int ADDITIVE_MODE = 1;

    /** multiplicative expansion mode */
    public static final int MULTIPLICATIVE_MODE = 0;

    /** Serializable version identifier */
    private static final long serialVersionUID = -3485529955529426875L;

    /**
     * The contraction criteria determines when the internal array will be
     * contracted to fit the number of elements contained in the element
     *  array + 1.
     */
    protected float contractionCriteria = 2.5f;

    /**
     * The expansion factor of the array.  When the array needs to be expanded,
     * the new array size will be
     * <code>internalArray.length * expansionFactor</code>
     * if <code>expansionMode</code> is set to MULTIPLICATIVE_MODE, or
     * <code>internalArray.length + expansionFactor</code> if
     * <code>expansionMode</code> is set to ADDITIVE_MODE.
     */
    protected float expansionFactor = 2.0f;

    /**
     * Determines whether array expansion by <code>expansionFactor</code>
     * is additive or multiplicative.
     */
    protected int expansionMode = MULTIPLICATIVE_MODE;

    /**
     * The initial capacity of the array.  Initial capacity is not exposed as a
     * property as it is only meaningful when passed to a constructor.
     */
    protected int initialCapacity = 16;

    /**
     * The internal storage array.
     */
    protected double[] internalArray;

    /**
     * The number of addressable elements in the array.  Note that this
     * has nothing to do with the length of the internal storage array.
     */
    protected int numElements = 0;

    /**
     * The position of the first addressable element in the internal storage
     * array.  The addressable elements in the array are <code>
     * internalArray[startIndex],...,internalArray[startIndex + numElements -1]
     * </code>
     */
    protected int startIndex = 0;

    /**
     * Create a ResizableArray with default properties.
     * <ul>
     * <li><code>initialCapacity = 16</code></li>
     * <li><code>expansionMode = MULTIPLICATIVE_MODE</code></li>
     * <li><code>expansionFactor = 2.5</code></li>
     * <li><code>contractionFactor = 2.0</code></li>
     * </ul>
     */
    public ResizableDoubleArray() {
        internalArray = new double[initialCapacity];
    }

    /**
     * Create a ResizableArray with the specified initial capacity.  Other
     * properties take default values:
      * <ul>
     * <li><code>expansionMode = MULTIPLICATIVE_MODE</code></li>
     * <li><code>expansionFactor = 2.5</code></li>
     * <li><code>contractionFactor = 2.0</code></li>
     * </ul>
     * @param initialCapacity The initial size of the internal storage array
     * @throws IllegalArgumentException if initialCapacity is not > 0
     */
    public ResizableDoubleArray(int initialCapacity) {
        setInitialCapacity(initialCapacity);
        internalArray = new double[this.initialCapacity];
    }

    /**
     * Create a ResizableArray from an existing double[] with the
     * initial capacity and numElements corresponding to the size of
     * the supplied double[] array. If the supplied array is null, a
     * new empty array with the default initial capacity will be created.
     * The input array is copied, not referenced.
     * Other properties take default values:
     * <ul>
     * <li><code>initialCapacity = 16</code></li>
     * <li><code>expansionMode = MULTIPLICATIVE_MODE</code></li>
     * <li><code>expansionFactor = 2.5</code></li>
     * <li><code>contractionFactor = 2.0</code></li>
     * </ul>
     *
     * @param initialArray initial array
     * @since 2.2
     */
    public ResizableDoubleArray(double[] initialArray) {
        if (initialArray == null) {
            this.internalArray = new double[initialCapacity];
        } else {
            this.internalArray = new double[initialArray.length];
            System.arraycopy(initialArray, 0, this.internalArray, 0, initialArray.length);
            initialCapacity = initialArray.length;
            numElements = initialArray.length;
        }
    }

    /**
     * <p>
     * Create a ResizableArray with the specified initial capacity
     * and expansion factor.  The remaining properties take default
     * values:
     * <ul>
     * <li><code>expansionMode = MULTIPLICATIVE_MODE</code></li>
     * <li><code>contractionFactor = 0.5 + expansionFactor</code></li>
     * </ul></p>
     * <p>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     * <ul>
     * <li><code>initialCapacity > 0</code></li>
     * <li><code>expansionFactor > 1</code></li>
     * </ul></p>
     *
     * @param initialCapacity The initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this
     *                        parameter
     * @throws IllegalArgumentException if parameters are not valid
     */
    public ResizableDoubleArray(int initialCapacity, float expansionFactor) {
        this.expansionFactor = expansionFactor;
        setInitialCapacity(initialCapacity);
        internalArray = new double[initialCapacity];
        setContractionCriteria(expansionFactor +0.5f);
    }

    /**
     * <p>
     * Create a ResizableArray with the specified initialCapacity,
     * expansionFactor, and contractionCriteria. The <code>expansionMode</code>
     * will default to <code>MULTIPLICATIVE_MODE.</code></p>
     * <p>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     * <ul>
     * <li><code>initialCapacity > 0</code></li>
     * <li><code>expansionFactor > 1</code></li>
     * <li><code>contractionFactor >= expansionFactor</code></li>
     * </ul></p>
     * @param initialCapacity The initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this
     *                        parameter
     * @param contractionCriteria The contraction Criteria.
     * @throws IllegalArgumentException if parameters are not valid
     */
    public ResizableDoubleArray(int initialCapacity, float expansionFactor,
        float contractionCriteria) {
        this.expansionFactor = expansionFactor;
        setContractionCriteria(contractionCriteria);
        setInitialCapacity(initialCapacity);
        internalArray = new double[initialCapacity];
    }

    /**
     * <p>
     * Create a ResizableArray with the specified properties.</p>
    * <p>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     * <ul>
     * <li><code>initialCapacity > 0</code></li>
     * <li><code>expansionFactor > 1</code></li>
     * <li><code>contractionFactor >= expansionFactor</code></li>
     * <li><code>expansionMode in {MULTIPLICATIVE_MODE, ADDITIVE_MODE}</code>
     * </li>
     * </ul></p>
     *
     * @param initialCapacity the initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this
     *                        parameter
     * @param contractionCriteria the contraction Criteria
     * @param expansionMode  the expansion mode
     * @throws IllegalArgumentException if parameters are not valid
     */
    public ResizableDoubleArray(int initialCapacity, float expansionFactor,
            float contractionCriteria, int expansionMode) {
        this.expansionFactor = expansionFactor;
        setContractionCriteria(contractionCriteria);
        setInitialCapacity(initialCapacity);
        setExpansionMode(expansionMode);
        internalArray = new double[initialCapacity];
    }

    /**
     * Copy constructor.  Creates a new ResizableDoubleArray that is a deep,
     * fresh copy of the original. Needs to acquire synchronization lock
     * on original.  Original may not be null; otherwise a NullPointerException
     * is thrown.
     *
     * @param original array to copy
     * @since 2.0
     */
    public ResizableDoubleArray(ResizableDoubleArray original) {
        copy(original, this);
    }

    /**
     * Adds an element to the end of this expandable array.
     *
     * @param value to be added to end of array
     */
    public synchronized void addElement(double value) {
        numElements++;
        if ((startIndex + numElements) > internalArray.length) {
            expand();
        }
        internalArray[startIndex + (numElements - 1)] = value;
        if (shouldContract()) {
            contract();
        }
    }

    /**
     * Adds several element to the end of this expandable array.
     *
     * @param values to be added to end of array
     * @since 2.2
     */
    public synchronized void addElements(double[] values) {
        final double[] tempArray = new double[numElements + values.length + 1];
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        System.arraycopy(values, 0, tempArray, numElements, values.length);
        internalArray = tempArray;
        startIndex = 0;
        numElements += values.length;
    }

    /**
     * <p>
     * Adds an element to the end of the array and removes the first
     * element in the array.  Returns the discarded first element.
     * The effect is similar to a push operation in a FIFO queue.
     * </p>
     * <p>
     * Example: If the array contains the elements 1, 2, 3, 4 (in that order)
     * and addElementRolling(5) is invoked, the result is an array containing
     * the entries 2, 3, 4, 5 and the value returned is 1.
     * </p>
     *
     * @param value the value to be added to the array
     * @return the value which has been discarded or "pushed" out of the array
     *         by this rolling insert
     */
    public synchronized double addElementRolling(double value) {
        double discarded = internalArray[startIndex];

        if ((startIndex + (numElements + 1)) > internalArray.length) {
            expand();
        }
        // Increment the start index
        startIndex += 1;

        // Add the new value
        internalArray[startIndex + (numElements - 1)] = value;

        // Check the contraction criteria
        if (shouldContract()) {
            contract();
        }
        return discarded;
    }

    /**
     * Substitutes <code>value</code> for the most recently added value.
     * Returns the value that has been replaced. If the array is empty (i.e.
     * if {@link #numElements} is zero), a MathRuntimeException is thrown.
     *
     * @param value new value to substitute for the most recently added value
     * @return value that has been replaced in the array
     * @since 2.0
     */
    public synchronized double substituteMostRecentElement(double value) {
        if (numElements < 1) {
            throw MathRuntimeException.createArrayIndexOutOfBoundsException(
                    LocalizedFormats.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY);
        }

        double discarded = internalArray[startIndex + (numElements - 1)];

        internalArray[startIndex + (numElements - 1)] = value;

        return discarded;
    }


    /**
     * Checks the expansion factor and the contraction criteria and throws an
     * IllegalArgumentException if the contractionCriteria is less than the
     * expansionCriteria
     *
     * @param expansion factor to be checked
     * @param contraction criteria to be checked
     * @throws IllegalArgumentException if the contractionCriteria is less than
     *         the expansionCriteria.
     */
    protected void checkContractExpand(float contraction, float expansion) {

        if (contraction < expansion) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR,
                    contraction, expansion);
        }

        if (contraction <= 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_ONE,
                    contraction);
        }

        if (expansion <= 1.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.EXPANSION_FACTOR_SMALLER_THAN_ONE,
                    expansion);
        }
    }

    /**
     * Clear the array, reset the size to the initialCapacity and the number
     * of elements to zero.
     */
    public synchronized void clear() {
        numElements = 0;
        startIndex = 0;
        internalArray = new double[initialCapacity];
    }

    /**
     * Contracts the storage array to the (size of the element set) + 1 - to
     * avoid a zero length array. This function also resets the startIndex to
     * zero.
     */
    public synchronized void contract() {
        double[] tempArray = new double[numElements + 1];

        // Copy and swap - copy only the element array from the src array.
        System.arraycopy(internalArray, startIndex, tempArray, 0, numElements);
        internalArray = tempArray;

        // Reset the start index to zero
        startIndex = 0;
    }

    /**
     * Discards the <code>i<code> initial elements of the array.  For example,
     * if the array contains the elements 1,2,3,4, invoking
     * <code>discardFrontElements(2)</code> will cause the first two elements
     * to be discarded, leaving 3,4 in the array.  Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the front of the array
     * @throws IllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    public synchronized void discardFrontElements(int i) {

        discardExtremeElements(i,true);

    }

    /**
     * Discards the <code>i<code> last elements of the array.  For example,
     * if the array contains the elements 1,2,3,4, invoking
     * <code>discardMostRecentElements(2)</code> will cause the last two elements
     * to be discarded, leaving 1,2 in the array.  Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the end of the array
     * @throws IllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    public synchronized void discardMostRecentElements(int i) {

        discardExtremeElements(i,false);

    }

    /**
     * Discards the <code>i<code> first or last elements of the array,
     * depending on the value of <code>front</code>.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * <code>discardExtremeElements(2,false)</code> will cause the last two elements
     * to be discarded, leaving 1,2 in the array.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * <code>discardExtremeElements(2,true)</code> will cause the first two elements
     * to be discarded, leaving 3,4 in the array.
     * Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the front/end of the array
     * @param front true if elements are to be discarded from the front
     * of the array, false if elements are to be discarded from the end
     * of the array
     * @throws IllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    private synchronized void discardExtremeElements(int i,boolean front) {
        if (i > numElements) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY,
                    i, numElements);
       } else if (i < 0) {
           throw MathRuntimeException.createIllegalArgumentException(
                   LocalizedFormats.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS,
                   i);
        } else {
            // "Subtract" this number of discarded from numElements
            numElements -= i;
            if (front) startIndex += i;
        }
        if (shouldContract()) {
            contract();
        }
    }

    /**
     * Expands the internal storage array using the expansion factor.
     * <p>
     * if <code>expansionMode</code> is set to MULTIPLICATIVE_MODE,
     * the new array size will be <code>internalArray.length * expansionFactor.</code>
     * If <code>expansionMode</code> is set to ADDITIVE_MODE,  the length
     * after expansion will be <code>internalArray.length + expansionFactor</code>
     * </p>
     */
    protected synchronized void expand() {

        // notice the use of FastMath.ceil(), this guarantees that we will always
        // have an array of at least currentSize + 1.   Assume that the
        // current initial capacity is 1 and the expansion factor
        // is 1.000000000000000001.  The newly calculated size will be
        // rounded up to 2 after the multiplication is performed.
        int newSize = 0;
        if (expansionMode == MULTIPLICATIVE_MODE) {
            newSize = (int) FastMath.ceil(internalArray.length * expansionFactor);
        } else {
            newSize = internalArray.length + FastMath.round(expansionFactor);
        }
        double[] tempArray = new double[newSize];

        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * Expands the internal storage array to the specified size.
     *
     * @param size Size of the new internal storage array
     */
    private synchronized void expandTo(int size) {
        double[] tempArray = new double[size];
        // Copy and swap
        System.arraycopy(internalArray, 0, tempArray, 0, internalArray.length);
        internalArray = tempArray;
    }

    /**
     * The contraction criteria defines when the internal array will contract
     * to store only the number of elements in the element array.
     * If  the <code>expansionMode</code> is <code>MULTIPLICATIVE_MODE</code>,
     * contraction is triggered when the ratio between storage array length
     * and <code>numElements</code> exceeds <code>contractionFactor</code>.
     * If the <code>expansionMode</code> is <code>ADDITIVE_MODE</code>, the
     * number of excess storage locations is compared to
     * <code>contractionFactor.</code>
     *
     * @return the contraction criteria used to reclaim memory.
     */
    public float getContractionCriteria() {
        return contractionCriteria;
    }

    /**
     * Returns the element at the specified index
     *
     * @param index index to fetch a value from
     * @return value stored at the specified index
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is less than
     *         zero or is greater than <code>getNumElements() - 1</code>.
     */
    public synchronized double getElement(int index) {
        if (index >= numElements) {
            throw MathRuntimeException.createArrayIndexOutOfBoundsException(
                    LocalizedFormats.INDEX_LARGER_THAN_MAX,
                    index, numElements - 1);
        } else if (index >= 0) {
            return internalArray[startIndex + index];
        } else {
            throw MathRuntimeException.createArrayIndexOutOfBoundsException(
                    LocalizedFormats.CANNOT_RETRIEVE_AT_NEGATIVE_INDEX,
                    index);
        }
    }

     /**
     * Returns a double array containing the elements of this
     * <code>ResizableArray</code>.  This method returns a copy, not a
     * reference to the underlying array, so that changes made to the returned
     *  array have no effect on this <code>ResizableArray.</code>
     * @return the double array.
     */
    public synchronized double[] getElements() {
        double[] elementArray = new double[numElements];
        System.arraycopy( internalArray, startIndex, elementArray, 0,
                numElements);
        return elementArray;
    }

    /**
     * The expansion factor controls the size of a new array when an array
     * needs to be expanded.  The <code>expansionMode</code>
     * determines whether the size of the array is multiplied by the
     * <code>expansionFactor</code> (MULTIPLICATIVE_MODE) or if
     * the expansion is additive (ADDITIVE_MODE -- <code>expansionFactor</code>
     * storage locations added).  The default <code>expansionMode</code> is
     * MULTIPLICATIVE_MODE and the default <code>expansionFactor</code>
     * is 2.0.
     *
     * @return the expansion factor of this expandable double array
     */
    public float getExpansionFactor() {
        return expansionFactor;
    }

    /**
     * The <code>expansionMode</code> determines whether the internal storage
     * array grows additively (ADDITIVE_MODE) or multiplicatively
     * (MULTIPLICATIVE_MODE) when it is expanded.
     *
     * @return Returns the expansionMode.
     */
    public int getExpansionMode() {
        return expansionMode;
    }

    /**
     * Notice the package scope on this method.   This method is simply here
     * for the JUnit test, it allows us check if the expansion is working
     * properly after a number of expansions.  This is not meant to be a part
     * of the public interface of this class.
     *
     * @return the length of the internal storage array.
     */
    synchronized int getInternalLength() {
        return internalArray.length;
    }

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.
     *
     * @return number of elements
     */
    public synchronized int getNumElements() {
        return numElements;
    }

    /**
     * Returns the internal storage array.  Note that this method returns
     * a reference to the internal storage array, not a copy, and to correctly
     * address elements of the array, the <code>startIndex</code> is
     * required (available via the {@link #start} method).  This method should
     * only be used in cases where copying the internal array is not practical.
     * The {@link #getElements} method should be used in all other cases.
     *
     *
     * @return the internal storage array used by this object
     * @deprecated replaced by {@link #getInternalValues()} as of 2.0
     */
    @Deprecated
    public synchronized double[] getValues() {
        return internalArray;
    }

    /**
     * Returns the internal storage array.  Note that this method returns
     * a reference to the internal storage array, not a copy, and to correctly
     * address elements of the array, the <code>startIndex</code> is
     * required (available via the {@link #start} method).  This method should
     * only be used in cases where copying the internal array is not practical.
     * The {@link #getElements} method should be used in all other cases.
     *
     *
     * @return the internal storage array used by this object
     * @since 2.0
     */
    public synchronized double[] getInternalValues() {
        return internalArray;
    }

    /**
     * Sets the contraction criteria for this ExpandContractDoubleArray.
     *
     * @param contractionCriteria contraction criteria
     */
    public void setContractionCriteria(float contractionCriteria) {
        checkContractExpand(contractionCriteria, getExpansionFactor());
        synchronized(this) {
            this.contractionCriteria = contractionCriteria;
        }
    }


    /**
     * Sets the element at the specified index.  If the specified index is greater than
     * <code>getNumElements() - 1</code>, the <code>numElements</code> property
     * is increased to <code>index +1</code> and additional storage is allocated
     * (if necessary) for the new element and all  (uninitialized) elements
     * between the new element and the previous end of the array).
     *
     * @param index index to store a value in
     * @param value value to store at the specified index
     * @throws ArrayIndexOutOfBoundsException if <code>index</code> is less than
     *         zero.
     */
    public synchronized void setElement(int index, double value) {
        if (index < 0) {
            throw MathRuntimeException.createArrayIndexOutOfBoundsException(
                    LocalizedFormats.CANNOT_SET_AT_NEGATIVE_INDEX,
                    index);
        }
        if (index + 1 > numElements) {
            numElements = index + 1;
        }
        if ((startIndex + index) >= internalArray.length) {
            expandTo(startIndex + (index + 1));
        }
        internalArray[startIndex + index] = value;
    }

    /**
     * Sets the expansionFactor.  Throws IllegalArgumentException if the
     * the following conditions are not met:
     * <ul>
     * <li><code>expansionFactor > 1</code></li>
     * <li><code>contractionFactor >= expansionFactor</code></li>
     * </ul>
     * @param expansionFactor the new expansion factor value.
     * @throws IllegalArgumentException if expansionFactor is <= 1 or greater
     * than contractionFactor
     */
    public void setExpansionFactor(float expansionFactor) {
        checkContractExpand(getContractionCriteria(), expansionFactor);
        // The check above verifies that the expansion factor is > 1.0;
        synchronized(this) {
            this.expansionFactor = expansionFactor;
        }
    }

    /**
     * Sets the <code>expansionMode</code>. The specified value must be one of
     * ADDITIVE_MODE, MULTIPLICATIVE_MODE.
     *
     * @param expansionMode The expansionMode to set.
     * @throws IllegalArgumentException if the specified mode value is not valid
     */
    public void setExpansionMode(int expansionMode) {
        if (expansionMode != MULTIPLICATIVE_MODE &&
                expansionMode != ADDITIVE_MODE) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.UNSUPPORTED_EXPANSION_MODE,
                    expansionMode, MULTIPLICATIVE_MODE, "MULTIPLICATIVE_MODE",
                    ADDITIVE_MODE, "ADDITIVE_MODE");
        }
        synchronized(this) {
            this.expansionMode = expansionMode;
        }
    }

    /**
     * Sets the initial capacity.  Should only be invoked by constructors.
     *
     * @param initialCapacity of the array
     * @throws IllegalArgumentException if <code>initialCapacity</code> is not
     *         positive.
     */
    protected void setInitialCapacity(int initialCapacity) {
        if (initialCapacity > 0) {
            synchronized(this) {
                this.initialCapacity = initialCapacity;
            }
        } else {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INITIAL_CAPACITY_NOT_POSITIVE,
                    initialCapacity);
        }
    }

    /**
     * This function allows you to control the number of elements contained
     * in this array, and can be used to "throw out" the last n values in an
     * array. This function will also expand the internal array as needed.
     *
     * @param i a new number of elements
     * @throws IllegalArgumentException if <code>i</code> is negative.
     */
    public synchronized void setNumElements(int i) {

        // If index is negative thrown an error
        if (i < 0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.INDEX_NOT_POSITIVE,
                    i);
        }

        // Test the new num elements, check to see if the array needs to be
        // expanded to accommodate this new number of elements
        if ((startIndex + i) > internalArray.length) {
            expandTo(startIndex + i);
        }

        // Set the new number of elements to new value
        numElements = i;
    }

    /**
     * Returns true if the internal storage array has too many unused
     * storage positions.
     *
     * @return true if array satisfies the contraction criteria
     */
    private synchronized boolean shouldContract() {
        if (expansionMode == MULTIPLICATIVE_MODE) {
            return (internalArray.length / ((float) numElements)) > contractionCriteria;
        } else {
            return (internalArray.length - numElements) > contractionCriteria;
        }
    }

    /**
     * Returns the starting index of the internal array.  The starting index is
     * the position of the first addressable element in the internal storage
     * array.  The addressable elements in the array are <code>
     * internalArray[startIndex],...,internalArray[startIndex + numElements -1]
     * </code>
     *
     * @return starting index
     */
    public synchronized int start() {
        return startIndex;
    }

    /**
     * <p>Copies source to dest, copying the underlying data, so dest is
     * a new, independent copy of source.  Does not contract before
     * the copy.</p>
     *
     * <p>Obtains synchronization locks on both source and dest
     * (in that order) before performing the copy.</p>
     *
     * <p>Neither source nor dest may be null; otherwise a NullPointerException
     * is thrown</p>
     *
     * @param source ResizableDoubleArray to copy
     * @param dest ResizableArray to replace with a copy of the source array
     * @since 2.0
     *
     */
    public static void copy(ResizableDoubleArray source, ResizableDoubleArray dest) {
       synchronized(source) {
           synchronized(dest) {
               dest.initialCapacity = source.initialCapacity;
               dest.contractionCriteria = source.contractionCriteria;
               dest.expansionFactor = source.expansionFactor;
               dest.expansionMode = source.expansionMode;
               dest.internalArray = new double[source.internalArray.length];
               System.arraycopy(source.internalArray, 0, dest.internalArray,
                       0, dest.internalArray.length);
               dest.numElements = source.numElements;
               dest.startIndex = source.startIndex;
           }
       }
    }

    /**
     * Returns a copy of the ResizableDoubleArray.  Does not contract before
     * the copy, so the returned object is an exact copy of this.
     *
     * @return a new ResizableDoubleArray with the same data and configuration
     * properties as this
     * @since 2.0
     */
    public synchronized ResizableDoubleArray copy() {
        ResizableDoubleArray result = new ResizableDoubleArray();
        copy(this, result);
        return result;
    }

    /**
     * Returns true iff object is a ResizableDoubleArray with the same properties
     * as this and an identical internal storage array.
     *
     * @param object object to be compared for equality with this
     * @return true iff object is a ResizableDoubleArray with the same data and
     * properties as this
     * @since 2.0
     */
    @Override
    public boolean equals(Object object) {
        if (object == this ) {
            return true;
        }
       if (object instanceof ResizableDoubleArray == false) {
            return false;
        }
       synchronized(this) {
           synchronized(object) {
               boolean result = true;
               ResizableDoubleArray other = (ResizableDoubleArray) object;
               result = result && (other.initialCapacity == initialCapacity);
               result = result && (other.contractionCriteria == contractionCriteria);
               result = result && (other.expansionFactor == expansionFactor);
               result = result && (other.expansionMode == expansionMode);
               result = result && (other.numElements == numElements);
               result = result && (other.startIndex == startIndex);
               if (!result) {
                   return false;
               } else {
                   return Arrays.equals(internalArray, other.internalArray);
               }
           }
       }
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return hash code representing this ResizableDoubleArray
     * @since 2.0
     */
    @Override
    public synchronized int hashCode() {
        int[] hashData = new int[7];
        hashData[0] = new Float(expansionFactor).hashCode();
        hashData[1] = new Float(contractionCriteria).hashCode();
        hashData[2] = expansionMode;
            hashData[3] = Arrays.hashCode(internalArray);
            hashData[4] = initialCapacity;
            hashData[5] = numElements;
            hashData[6] = startIndex;
        return Arrays.hashCode(hashData);
    }

}

// GenericsNote: Converted.
/*
 *  Copyright 1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.commons.collections15.iterators;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections15.Predicate;

/**
 * A proxy {@link ListIterator ListIterator} which
 * takes a {@link Predicate Predicate} instance to filter
 * out objects from an underlying <code>ListIterator</code>
 * instance. Only objects for which the specified
 * <code>Predicate</code> evaluates to <code>true</code> are
 * returned by the iterator.
 *
 * @author Matt Hall, John Watkinson, Rodney Waldhoff
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 2.0
 */
public class FilterListIterator <E> implements ListIterator<E> {

    /**
     * The iterator being used
     */
    private ListIterator<E> iterator;

    /**
     * The predicate being used
     */
    private Predicate<? super E> predicate;

    /**
     * The value of the next (matching) object, when
     * {@link #nextObjectSet} is true.
     */
    private E nextObject;

    /**
     * Whether or not the {@link #nextObject} has been set
     * (possibly to <code>null</code>).
     */
    private boolean nextObjectSet = false;

    /**
     * The value of the previous (matching) object, when
     * {@link #previousObjectSet} is true.
     */
    private E previousObject;

    /**
     * Whether or not the {@link #previousObject} has been set
     * (possibly to <code>null</code>).
     */
    private boolean previousObjectSet = false;

    /**
     * The index of the element that would be returned by {@link #bucket_next}.
     */
    private int nextIndex = 0;
    
    //-----------------------------------------------------------------------
    /**
     * Constructs a new <code>FilterListIterator</code> that will not
     * function until
     * {@link #setPredicate(Predicate) setPredicate} is invoked.
     */
    public FilterListIterator() {
        super();
    }

    /**
     * Constructs a new <code>FilterListIterator</code> that will not
     * function until {@link #setPredicate(Predicate) setPredicate} is invoked.
     *
     * @param iterator the iterator to use
     */
    public FilterListIterator(ListIterator<E> iterator) {
        super();
        this.iterator = iterator;
    }

    /**
     * Constructs a new <code>FilterListIterator</code>.
     *
     * @param iterator  the iterator to use
     * @param predicate the predicate to use
     */
    public FilterListIterator(ListIterator<E> iterator, Predicate<? super E> predicate) {
        super();
        this.iterator = iterator;
        this.predicate = predicate;
    }

    /**
     * Constructs a new <code>FilterListIterator</code>.
     *
     * @param predicate the predicate to use.
     */
    public FilterListIterator(Predicate<? super E> predicate) {
        super();
        this.predicate = predicate;
    }

    //-----------------------------------------------------------------------
    /**
     * Not supported.
     */
    public void add(E o) {
        throw new UnsupportedOperationException("FilterListIterator.add(Object) is not supported.");
    }

    public boolean hasNext() {
        if (nextObjectSet) {
            return true;
        } else {
            return setNextObject();
        }
    }

    public boolean hasPrevious() {
        if (previousObjectSet) {
            return true;
        } else {
            return setPreviousObject();
        }
    }

    public E next() {
        if (!nextObjectSet) {
            if (!setNextObject()) {
                throw new NoSuchElementException();
            }
        }
        nextIndex++;
        E temp = nextObject;
        clearNextObject();
        return temp;
    }

    public int nextIndex() {
        return nextIndex;
    }

    public E previous() {
        if (!previousObjectSet) {
            if (!setPreviousObject()) {
                throw new NoSuchElementException();
            }
        }
        nextIndex--;
        E temp = previousObject;
        clearPreviousObject();
        return temp;
    }

    public int previousIndex() {
        return (nextIndex - 1);
    }

    /**
     * Not supported.
     */
    public void remove() {
        throw new UnsupportedOperationException("FilterListIterator.remove() is not supported.");
    }

    /**
     * Not supported.
     */
    public void set(E o) {
        throw new UnsupportedOperationException("FilterListIterator.set(Object) is not supported.");
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the iterator this iterator is using.
     *
     * @return the iterator.
     */
    public ListIterator<E> getListIterator() {
        return iterator;
    }

    /**
     * Sets the iterator for this iterator to use.
     * If iteration has started, this effectively resets the iterator.
     *
     * @param iterator the iterator to use
     */
    public void setListIterator(ListIterator<E> iterator) {
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the predicate this iterator is using.
     *
     * @return the predicate.
     */
    public Predicate<? super E> getPredicate() {
        return predicate;
    }

    /**
     * Sets the predicate this the iterator to use.
     *
     * @param predicate the transformer to use
     */
    public void setPredicate(Predicate<? super E> predicate) {
        this.predicate = predicate;
    }

    //-----------------------------------------------------------------------
    private void clearNextObject() {
        nextObject = null;
        nextObjectSet = false;
    }

    private boolean setNextObject() {
        // if previousObjectSet,
        // then we've walked back one step in the 
        // underlying list (due to a hasPrevious() call)
        // so skip ahead one matching object
        if (previousObjectSet) {
            clearPreviousObject();
            if (!setNextObject()) {
                return false;
            } else {
                clearNextObject();
            }
        }

        while (iterator.hasNext()) {
            E object = iterator.next();
            if (predicate.evaluate(object)) {
                nextObject = object;
                nextObjectSet = true;
                return true;
            }
        }
        return false;
    }

    private void clearPreviousObject() {
        previousObject = null;
        previousObjectSet = false;
    }

    private boolean setPreviousObject() {
        // if nextObjectSet,
        // then we've walked back one step in the 
        // underlying list (due to a hasNext() call)
        // so skip ahead one matching object
        if (nextObjectSet) {
            clearNextObject();
            if (!setPreviousObject()) {
                return false;
            } else {
                clearPreviousObject();
            }
        }

        while (iterator.hasPrevious()) {
            E object = iterator.previous();
            if (predicate.evaluate(object)) {
                previousObject = object;
                previousObjectSet = true;
                return true;
            }
        }
        return false;
    }

}

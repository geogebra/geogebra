// GenericsNote: Converted.
/*
 *  Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.set;

import java.util.Comparator;
import java.util.SortedSet;

import org.apache.commons.collections15.collection.SynchronizedCollection;

/**
 * Decorates another <code>SortedSet</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated set.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.0
 */
public class SynchronizedSortedSet <E> extends SynchronizedCollection<E> implements SortedSet<E> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 2775582861954500111L;

    /**
     * Factory method to create a synchronized set.
     *
     * @param set the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <E> SortedSet<E> decorate(SortedSet<E> set) {
        return new SynchronizedSortedSet<E>(set);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param set the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSortedSet(SortedSet<E> set) {
        super(set);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param set  the set to decorate, must not be null
     * @param lock the lock object to use, must not be null
     * @throws IllegalArgumentException if set is null
     */
    protected SynchronizedSortedSet(SortedSet<E> set, Object lock) {
        super(set, lock);
    }

    /**
     * Gets the decorated set.
     *
     * @return the decorated set
     */
    protected SortedSet<E> getSortedSet() {
        return (SortedSet<E>) collection;
    }

    //-----------------------------------------------------------------------
    public SortedSet<E> subSet(E fromElement, E toElement) {
        synchronized (lock) {
            SortedSet set = getSortedSet().subSet(fromElement, toElement);
            // the lock is passed into the constructor here to ensure that the
            // subset is synchronized on the same lock as the parent
            return new SynchronizedSortedSet<E>(set, lock);
        }
    }

    public SortedSet<E> headSet(E toElement) {
        synchronized (lock) {
            SortedSet set = getSortedSet().headSet(toElement);
            // the lock is passed into the constructor here to ensure that the
            // headset is synchronized on the same lock as the parent
            return new SynchronizedSortedSet<E>(set, lock);
        }
    }

    public SortedSet<E> tailSet(E fromElement) {
        synchronized (lock) {
            SortedSet<E> set = getSortedSet().tailSet(fromElement);
            // the lock is passed into the constructor here to ensure that the
            // tailset is synchronized on the same lock as the parent
            return new SynchronizedSortedSet<E>(set, lock);
        }
    }

    public E first() {
        synchronized (lock) {
            return getSortedSet().first();
        }
    }

    public E last() {
        synchronized (lock) {
            return getSortedSet().last();
        }
    }

    public Comparator<? super E> comparator() {
        synchronized (lock) {
            return getSortedSet().comparator();
        }
    }

}

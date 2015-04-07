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
package org.apache.commons.collections15.bag;

import java.util.Set;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.collection.SynchronizedCollection;
import org.apache.commons.collections15.set.SynchronizedSet;

/**
 * Decorates another <code>Bag</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated bag.
 * Iterators must be separately synchronized around the loop.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0
 */
public class SynchronizedBag <E> extends SynchronizedCollection<E> implements Bag<E> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 8084674570753837109L;

    /**
     * Factory method to create a synchronized bag.
     *
     * @param bag the bag to decorate, must not be null
     * @return a new synchronized Bag
     * @throws IllegalArgumentException if bag is null
     */
    public static <E> Bag<E> decorate(Bag<E> bag) {
        return new SynchronizedBag<E>(bag);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param bag the bag to decorate, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    protected SynchronizedBag(Bag<E> bag) {
        super(bag);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param bag  the bag to decorate, must not be null
     * @param lock the lock to use, must not be null
     * @throws IllegalArgumentException if bag is null
     */
    protected SynchronizedBag(Bag<E> bag, Object lock) {
        super(bag, lock);
    }

    /**
     * Gets the bag being decorated.
     *
     * @return the decorated bag
     */
    protected Bag<E> getBag() {
        return (Bag<E>) collection;
    }

    //-----------------------------------------------------------------------
    public boolean add(E object, int count) {
        synchronized (lock) {
            return getBag().add(object, count);
        }
    }

    public boolean remove(E object, int count) {
        synchronized (lock) {
            return getBag().remove(object, count);
        }
    }

    public Set<E> uniqueSet() {
        synchronized (lock) {
            Set<E> set = getBag().uniqueSet();
            return new SynchronizedBagSet(set, lock);
        }
    }

    public int getCount(E object) {
        synchronized (lock) {
            return getBag().getCount(object);
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Synchronized Set for the Bag class.
     */
    class SynchronizedBagSet extends SynchronizedSet<E> {
        /**
         * Constructor.
         *
         * @param set  the set to decorate
         * @param lock the lock to use, shared with the bag
         */
        SynchronizedBagSet(Set<E> set, Object lock) {
            super(set, lock);
        }
    }

}

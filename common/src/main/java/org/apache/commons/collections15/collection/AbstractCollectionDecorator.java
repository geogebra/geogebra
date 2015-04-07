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
package org.apache.commons.collections15.collection;

import java.util.Collection;
import java.util.Iterator;

/**
 * Decorates another <code>Collection</code> to provide additional behaviour.
 * <p/>
 * Each method call made on this <code>Collection</code> is forwarded to the
 * decorated <code>Collection</code>. This class is used as a framework on which
 * to build to extensions such as synchronized and unmodifiable behaviour. The
 * main advantage of decoration is that one decorator can wrap any implementation
 * of <code>Collection</code>, whereas sub-classing requires a new class to be
 * written for each implementation.
 * <p/>
 * This implementation does not perform any special processing with
 * {@link #iterator()}. Instead it simply returns the value from the
 * wrapped collection. This may be undesirable, for example if you are trying
 * to write an unmodifiable implementation it might provide a loophole.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 */
public abstract class AbstractCollectionDecorator <E> implements Collection<E> {

    /**
     * The collection being decorated
     */
    protected Collection<E> collection;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     *
     * @since Commons Collections 3.1
     */
    protected AbstractCollectionDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param coll the collection to decorate, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    protected AbstractCollectionDecorator(Collection<E> coll) {
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = coll;
    }

    /**
     * Gets the collection being decorated.
     *
     * @return the decorated collection
     */
    protected Collection<E> getCollection() {
        return collection;
    }

    //-----------------------------------------------------------------------
    public boolean add(E object) {
        return collection.add(object);
    }

    public boolean addAll(Collection<? extends E> coll) {
        return collection.addAll(coll);
    }

    public void clear() {
        collection.clear();
    }

    public boolean contains(Object object) {
        return collection.contains(object);
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public Iterator<E> iterator() {
        return collection.iterator();
    }

    public boolean remove(Object object) {
        return collection.remove(object);
    }

    public int size() {
        return collection.size();
    }

    public Object[] toArray() {
        return collection.toArray();
    }

    public <T> T[] toArray(T[] object) {
        return collection.toArray(object);
    }

    public boolean containsAll(Collection<?> coll) {
        return collection.containsAll(coll);
    }

    public boolean removeAll(Collection<?> coll) {
        return collection.removeAll(coll);
    }

    public boolean retainAll(Collection<?> coll) {
        return collection.retainAll(coll);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        return collection.equals(object);
    }

    public int hashCode() {
        return collection.hashCode();
    }

    public String toString() {
        return collection.toString();
    }

}

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
package org.apache.commons.collections15.iterators;

import org.apache.commons.collections15.OrderedMapIterator;

/**
 * Provides basic behaviour for decorating an ordered map iterator with extra functionality.
 * <p/>
 * All methods are forwarded to the decorated map iterator.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class AbstractOrderedMapIteratorDecorator <K,V> implements OrderedMapIterator<K, V> {

    /**
     * The iterator being decorated
     */
    protected final OrderedMapIterator<K, V> iterator;

    //-----------------------------------------------------------------------
    /**
     * Constructor that decorates the specified iterator.
     *
     * @param iterator the iterator to decorate, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    public AbstractOrderedMapIteratorDecorator(OrderedMapIterator<K, V> iterator) {
        super();
        if (iterator == null) {
            throw new IllegalArgumentException("OrderedMapIterator must not be null");
        }
        this.iterator = iterator;
    }

    /**
     * Gets the iterator being decorated.
     *
     * @return the decorated iterator
     */
    protected OrderedMapIterator<K, V> getOrderedMapIterator() {
        return iterator;
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public K next() {
        return iterator.next();
    }

    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    public K previous() {
        return iterator.previous();
    }

    public void remove() {
        iterator.remove();
    }

    public K getKey() {
        return iterator.getKey();
    }

    public V getValue() {
        return iterator.getValue();
    }

    public V setValue(V obj) {
        return iterator.setValue(obj);
    }

}

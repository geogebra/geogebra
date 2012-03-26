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

import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.Unmodifiable;

/**
 * Decorates a map iterator such that it cannot be modified.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableMapIterator <K,V> implements MapIterator<K, V>, Unmodifiable {

    /**
     * The iterator being decorated
     */
    private MapIterator<K, V> iterator;

    //-----------------------------------------------------------------------
    /**
     * Decorates the specified iterator such that it cannot be modified.
     *
     * @param iterator the iterator to decorate
     * @throws IllegalArgumentException if the iterator is null
     */
    public static <K,V> MapIterator<K, V> decorate(MapIterator<K, V> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("MapIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableMapIterator<K, V>(iterator);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param iterator the iterator to decorate
     */
    private UnmodifiableMapIterator(MapIterator<K, V> iterator) {
        super();
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public K next() {
        return iterator.next();
    }

    public K getKey() {
        return iterator.getKey();
    }

    public V getValue() {
        return iterator.getValue();
    }

    public V setValue(V value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }

}

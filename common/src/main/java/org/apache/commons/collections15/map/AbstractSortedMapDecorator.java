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
package org.apache.commons.collections15.map;

import java.util.Comparator;
import java.util.SortedMap;

/**
 * Provides a base decorator that enables additional functionality to be added
 * to a Map via decoration.
 * <p/>
 * Methods are forwarded directly to the decorated map.
 * <p/>
 * This implementation does not perform any special processing with the map views.
 * Instead it simply returns the set/collection from the wrapped map. This may be
 * undesirable, for example if you are trying to write a validating implementation
 * it would provide a loophole around the validation.
 * But, you might want that loophole, so this class is kept simple.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public abstract class AbstractSortedMapDecorator <K,V> extends AbstractMapDecorator<K, V> implements SortedMap<K, V> {

    /**
     * Constructor only used in deserialization, do not use otherwise.
     *
     * @since Commons Collections 3.1
     */
    protected AbstractSortedMapDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if the collection is null
     */
    public AbstractSortedMapDecorator(SortedMap<K, V> map) {
        super(map);
    }

    /**
     * Gets the map being decorated.
     *
     * @return the decorated map
     */
    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap<K, V>) map;
    }

    //-----------------------------------------------------------------------
    public Comparator<? super K> comparator() {
        return getSortedMap().comparator();
    }

    public K firstKey() {
        return getSortedMap().firstKey();
    }

    public SortedMap<K, V> headMap(K toKey) {
        return getSortedMap().headMap(toKey);
    }

    public K lastKey() {
        return getSortedMap().lastKey();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return getSortedMap().subMap(fromKey, toKey);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        return getSortedMap().tailMap(fromKey);
    }

}

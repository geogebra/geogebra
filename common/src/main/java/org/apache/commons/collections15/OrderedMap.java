// GenericsNote: Converted.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections15;

/**
 * Defines a map that maintains order and allows both forward and backward
 * iteration through that order.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0
 */
public interface OrderedMap <K,V> extends IterableMap<K, V> {

    /**
     * Obtains an <code>OrderedMapIterator</code> over the map.
     * <p/>
     * A ordered map iterator is an efficient way of iterating over maps
     * in both directions.
     * <pre>
     * BidiMap map = new TreeBidiMap();
     * MapIterator it = map.mapIterator();
     * while (it.hasNext()) {
     *   Object key = it.next();
     *   Object value = it.getValue();
     *   it.setValue("newValue");
     *   Object previousKey = it.previous();
     * }
     * </pre>
     *
     * @return a map iterator
     */
    OrderedMapIterator<K, V> orderedMapIterator();

    /**
     * Gets the first key currently in this map.
     *
     * @return the first key currently in this map
     * @throws java.util.NoSuchElementException
     *          if this map is empty
     */
    public K firstKey();

    /**
     * Gets the last key currently in this map.
     *
     * @return the last key currently in this map
     * @throws java.util.NoSuchElementException
     *          if this map is empty
     */
    public K lastKey();

    /**
     * Gets the next key after the one specified.
     *
     * @param key the key to search for next from
     * @return the next key, null if no match or at end
     */
    public K nextKey(K key);

    /**
     * Gets the previous key before the one specified.
     *
     * @param key the key to search for previous from
     * @return the previous key, null if no match or at start
     */
    public K previousKey(K key);

}

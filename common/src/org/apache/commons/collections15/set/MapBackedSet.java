// GenericsNote: Converted.
/*
 *  Copyright 2004 The Apache Software Foundation
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Decorates a <code>Map</code> to obtain <code>Set</code> behaviour.
 * <p/>
 * This class is used to create a <code>Set</code> with the same properties as
 * the key set of any map. Thus, a ReferenceSet can be created by wrapping a
 * <code>ReferenceMap</code> in an instance of this class.
 * <p/>
 * Most map implementation can be used to create a set by passing in dummy values.
 * Exceptions include <code>BidiMap</code> implementations, as they require unique values.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.1
 */
public final class MapBackedSet <K,V> implements Set<K>, Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 6723912213766056587L;

    /**
     * The map being used as the backing store
     */
    protected final Map<K, V> map;
    /**
     * The dummyValue to use
     */
    protected final V dummyValue;

    /**
     * Factory method to create a set from a map.
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <K,V> Set<K> decorate(Map<K, V> map) {
        return decorate(map, null);
    }

    /**
     * Factory method to create a set from a map.
     *
     * @param map        the map to decorate, must not be null
     * @param dummyValue the dummy value to use
     * @throws IllegalArgumentException if map is null
     */
    public static <K,V> Set<K> decorate(Map<K, V> map, V dummyValue) {
        if (map == null) {
            throw new IllegalArgumentException("The map must not be null");
        }
        return new MapBackedSet<K, V>(map, dummyValue);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map        the map to decorate, must not be null
     * @param dummyValue the dummy value to use
     * @throws IllegalArgumentException if map is null
     */
    private MapBackedSet(Map<K, V> map, V dummyValue) {
        super();
        this.map = map;
        this.dummyValue = dummyValue;
    }

    //-----------------------------------------------------------------------
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Iterator<K> iterator() {
        return map.keySet().iterator();
    }

    public boolean contains(Object obj) {
        return map.containsKey(obj);
    }

    public boolean containsAll(Collection<?> coll) {
        return map.keySet().containsAll(coll);
    }

    public boolean add(K obj) {
        int size = map.size();
        map.put(obj, dummyValue);
        return (map.size() != size);
    }

    public boolean addAll(Collection<? extends K> coll) {
        int size = map.size();
        for (Iterator<? extends K> it = coll.iterator(); it.hasNext();) {
            K obj = it.next();
            map.put(obj, dummyValue);
        }
        return (map.size() != size);
    }

    public boolean remove(Object obj) {
        int size = map.size();
        map.remove(obj);
        return (map.size() != size);
    }

    public boolean removeAll(Collection<?> coll) {
        return map.keySet().removeAll(coll);
    }

    public boolean retainAll(Collection<?> coll) {
        return map.keySet().retainAll(coll);
    }

    public void clear() {
        map.clear();
    }

    public Object[] toArray() {
        return map.keySet().toArray();
    }

    public <T> T[] toArray(T[] array) {
        return map.keySet().toArray(array);
    }

    public boolean equals(Object obj) {
        return map.keySet().equals(obj);
    }

    public int hashCode() {
        return map.keySet().hashCode();
    }

}

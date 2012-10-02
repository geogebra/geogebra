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
package org.apache.commons.collections15.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.IterableMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.keyvalue.MultiKey;

/**
 * A <code>Map</code> implementation that uses multiple keys to map the value.
 * <p/>
 * This class is the most efficient way to uses multiple keys to map to a value.
 * The best way to use this class is via the additional map-style methods.
 * These provide <code>get</code>, <code>containsKey</code>, <code>put</code> and
 * <code>remove</code> for individual keys which operate without extra object creation.
 * <p/>
 * The additional methods are the main interface of this map.
 * As such, you will not normally hold this map in a variable of type <code>Map</code>.
 * <p/>
 * The normal map methods take in and return a {@link MultiKey}.
 * If you try to use <code>put()</code> with any other object type a
 * <code>ClassCastException</code> is thrown. If you try to use <code>null</code> as
 * the key in <code>put()</code> a <code>NullPointerException</code> is thrown.
 * <p/>
 * This map is implemented as a decorator of a <code>AbstractHashedMap</code> which
 * enables extra behaviour to be added easily.
 * <ul>
 * <li><code>MultiKeyMap.decorate(new LinkedMap())</code> creates an ordered map.
 * <li><code>MultiKeyMap.decorate(new LRUMap())</code> creates an least recently used map.
 * <li><code>MultiKeyMap.decorate(new ReferenceMap())</code> creates a garbage collector sensitive map.
 * </ul>
 * Note that <code>IdentityMap</code> and <code>ReferenceIdentityMap</code> are unsuitable
 * for use as the key comparison would work on the whole MultiKey, not the elements within.
 * <p/>
 * As an example, consider a least recently used cache that uses a String airline code
 * and a Locale to lookup the airline's name:
 * <pre>
 * private MultiKeyMap cache = MultiKeyMap.decorate(new LRUMap(50));
 * <p/>
 * public String getAirlineName(String code, String locale) {
 *   String name = (String) cache.get(code, locale);
 *   if (name == null) {
 *     name = getAirlineNameFromDB(code, locale);
 *     cache.put(code, locale, name);
 *   }
 *   return name;
 * }
 * </pre>
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.1
 */
public class MultiKeyMap <K,V> implements IterableMap<MultiKey<K>, V>, Serializable {

    /**
     * Serialisation version
     */
    private static final long serialVersionUID = -1788199231038721040L;

    /**
     * The decorated map
     */
    protected final AbstractHashedMap<MultiKey<K>, V> map;

    //-----------------------------------------------------------------------
    /**
     * Decorates the specified map to add the MultiKeyMap API and fast query.
     * The map must not be null and must be empty.
     *
     * @param map the map to decorate, not null
     * @throws IllegalArgumentException if the map is null or not empty
     */
    public static <K,V> MultiKeyMap<K, V> decorate(AbstractHashedMap<MultiKey<K>, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (map.size() > 0) {
            throw new IllegalArgumentException("Map must be empty");
        }
        return new MultiKeyMap<K, V>(map);
    }

    //-----------------------------------------------------------------------    
    /**
     * Constructs a new MultiKeyMap that decorates a <code>HashedMap</code>.
     */
    public MultiKeyMap() {
        super();
        map = new HashedMap<MultiKey<K>, V>();
    }

    /**
     * Constructor that decorates the specified map and is called from
     * {@link #decorate(AbstractHashedMap)}.
     * The map must not be null and should be empty or only contain valid keys.
     * This constructor performs no validation.
     *
     * @param map the map to decorate
     */
    protected MultiKeyMap(AbstractHashedMap<MultiKey<K>, V> map) {
        super();
        this.map = map;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the specified multi-key.
     *
     * @param keys the keys
     * @return the mapped value, null if no match
     */
    public V get(K... keys) {
        int hashCode = hash(keys);
        AbstractHashedMap.HashEntry<MultiKey<K>, V> entry = map.data[map.hashIndex(hashCode, map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, keys)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Checks whether the map contains the specified multi-key.
     *
     * @param keys the keys
     * @return true if the map contains the key
     */
    public boolean containsKey(K... keys) {
        int hashCode = hash(keys);
        AbstractHashedMap.HashEntry<MultiKey<K>, V> entry = map.data[map.hashIndex(hashCode, map.data.length)];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, keys)) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * For backwards compatibility, makes a call to the new varargs {@link MultiKeyMap#putMultiKey}
     */
    public V put(K key1, K key2, V value) {
        return putMultiKey(value, key1, key2);
    }

    /**
     * For backwards compatibility, makes a call to the new varargs {@link MultiKeyMap#putMultiKey}
     */
    public V put(K key1, K key2, K key3, V value) {
        return putMultiKey(value, key1, key2, key3);
    }

    /**
     * For backwards compatibility, makes a call to the new varargs {@link MultiKeyMap#putMultiKey}
     */
    public V put(K key1, K key2, K key3, K key4, V value) {
        return putMultiKey(value, key1, key2, key3, key4);
    }

    /**
     * For backwards compatibility, makes a call to the new varargs {@link MultiKeyMap#putMultiKey}
     */
    public V put(K key1, K key2, K key3, K key4, K key5, V value) {
        return putMultiKey(value, key1, key2, key3, key4, key5);
    }

    /**
     * Stores the value against the specified multi-key.
     *
     * @param value the value to store
     * @param keys  the keys
     * @return the value previously mapped to this combined key, null if none
     */
    public V putMultiKey(V value, K... keys) {
        int hashCode = hash(keys);
        int index = map.hashIndex(hashCode, map.data.length);
        AbstractHashedMap.HashEntry<MultiKey<K>, V> entry = map.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, keys)) {
                V oldValue = entry.getValue();
                map.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }

        map.addMapping(index, hashCode, new MultiKey<K>(keys), value);
        return null;
    }

    /**
     * Removes the specified multi-key from this map.
     *
     * @param keys the keys
     * @return the value mapped to the removed key, null if key not in map
     */
    public Object remove(K... keys) {
        int hashCode = hash(keys);
        int index = map.hashIndex(hashCode, map.data.length);
        AbstractHashedMap.HashEntry<MultiKey<K>, V> entry = map.data[index];
        AbstractHashedMap.HashEntry<MultiKey<K>, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && isEqualKey(entry, keys)) {
                Object oldValue = entry.getValue();
                map.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the hash code for the specified multi-key.
     *
     * @param keys the keys
     * @return the hash code
     */
    protected int hash(K... keys) {
        int h = 0;
        for (int i = 0; i < keys.length; i++) {
            K key = keys[i];
            if (key != null) {
                h ^= key.hashCode();
            }
        }
        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }

    /**
     * Is the key equal to the combined key.
     *
     * @param entry the entry to compare to
     * @param keys  the keys
     * @return true if the key matches
     */
    protected boolean isEqualKey(AbstractHashedMap.HashEntry<MultiKey<K>, V> entry, K... keys) {
        MultiKey multi = entry.getKey();
        if (multi.size() != keys.length) {
            return false;
        } else {
            for (int i = 0; i < keys.length; i++) {
                K key = keys[i];
                if ((key == null ? multi.getKey(i) != null : !key.equals(multi.getKey(i)))) {
                    return false;
                }
            }
        }
        return true;
    }

    //-----------------------------------------------------------------------
    /**
     * Removes all mappings where the first key is that specified.
     * <p/>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has one or more keys, and the first matches that specified.
     *
     * @param key1 the first key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 1 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all mappings where the first two keys are those specified.
     * <p/>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has two or more keys, and the first two match those specified.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1, Object key2) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 2 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all mappings where the first three keys are those specified.
     * <p/>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has three or more keys, and the first three match those specified.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @param key3 the third key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1, Object key2, Object key3) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 3 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) && (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all mappings where the first four keys are those specified.
     * <p/>
     * This method removes all the mappings where the <code>MultiKey</code>
     * has four or more keys, and the first four match those specified.
     *
     * @param key1 the first key
     * @param key2 the second key
     * @param key3 the third key
     * @param key4 the fourth key
     * @return true if any elements were removed
     */
    public boolean removeAll(Object key1, Object key2, Object key3, Object key4) {
        boolean modified = false;
        MapIterator it = mapIterator();
        while (it.hasNext()) {
            MultiKey multi = (MultiKey) it.next();
            if (multi.size() >= 4 && (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0))) && (key2 == null ? multi.getKey(1) == null : key2.equals(multi.getKey(1))) && (key3 == null ? multi.getKey(2) == null : key3.equals(multi.getKey(2))) && (key4 == null ? multi.getKey(3) == null : key4.equals(multi.getKey(3)))) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    //-----------------------------------------------------------------------
    /**
     * Check to ensure that input keys are valid MultiKey objects.
     *
     * @param key the key to check
     */
    protected void checkKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
    }

    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    public Object clone() {
        return new MultiKeyMap((AbstractHashedMap) map.clone());
    }

    /**
     * Puts the key and value into the map, where the key must be a non-null
     * MultiKey object.
     *
     * @param key   the non-null MultiKey object
     * @param value the value to store
     * @return the previous value for the key
     * @throws NullPointerException if the key is null
     * @throws ClassCastException   if the key is not a MultiKey
     */
    public V put(MultiKey<K> key, V value) {
        checkKey(key);
        return map.put(key, value);
    }

    /**
     * Puts all the keys and values into this map.
     * Each key must be non-null and a MultiKey object.
     *
     * @param mapToCopy the map to copy in.
     * @throws NullPointerException if the mapToCopy or any key within is null
     * @throws ClassCastException   if any key is not a MultiKey
     */
    public void putAll(Map<? extends MultiKey<K>, ? extends V> mapToCopy) {
        for (Iterator it = mapToCopy.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            checkKey(key);
        }
        map.putAll(mapToCopy);
    }

    //-----------------------------------------------------------------------
    public MapIterator mapIterator() {
        return map.mapIterator();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public V get(Object key) {
        return map.get(key);
    }

    public V remove(Object key) {
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public Set keySet() {
        return map.keySet();
    }

    public Collection values() {
        return map.values();
    }

    public Set entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return map.equals(obj);
    }

    public int hashCode() {
        return map.hashCode();
    }

    public String toString() {
        return map.toString();
    }

}

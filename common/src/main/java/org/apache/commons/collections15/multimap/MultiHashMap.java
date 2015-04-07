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
package org.apache.commons.collections15.multimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.iterators.EmptyIterator;

/**
 * <code>MultiHashMap</code> is the default implementation of the
 * {@link org.apache.commons.collections15.MultiMap MultiMap} interface.
 * <p/>
 * A <code>MultiMap</code> is like a Map, but with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that key.
 * <p/>
 * This implementation uses an <code>ArrayList</code> as the collection.
 * The internal storage list is made available without cloning via the
 * <code>get(Object)</code> and <code>entrySet()</code> methods.
 * The implementation returns <code>null</code> when there are no values mapped to a key.
 * <p/>
 * For example:
 * <pre>
 * Number key = new Integer(5);
 * MultiMap&lt;Number,String&gt; mhm = new MultiHashMap&lt;Number,String&gt;();
 * mhm.put(key, "A");
 * mhm.put(key, "B");
 * mhm.put(key, "C");
 * Collection&lt;String&gt; coll = mhm.get(key);</pre>
 * <p/>
 * <code>list</code> will be a list containing "A", "B", "C".
 *
 * @author Christopher Berry
 * @author James Strachan
 * @author Steve Downey
 * @author Stephen Colebourne
 * @author Julien Buret
 * @author Matt Hall, John Watkinson, Serhiy Yevtushenko
 * @version $Revision: 1.2 $ $Date: 2006/06/08 15:19:55 $
 * @since Commons Collections 2.0
 */
public class MultiHashMap<K,V> implements MultiMap<K,V>, Serializable, Cloneable {

    // backed values collection
    private transient Collection values = null;

    // compatibility with commons-collection releases 2.0/2.1
    private static final long serialVersionUID = 1943563828307035349L;

    private HashMap<K,Collection<V>> internalMap;

    /**
     * Constructor.
     */
    public MultiHashMap() {
        internalMap = new HashMap<K, Collection<V>>();
    }

    /**
     * Constructor.
     *
     * @param initialCapacity the initial map capacity
     */
    public MultiHashMap(int initialCapacity) {
        internalMap = new HashMap<K, Collection<V>>(initialCapacity);
    }

    /**
     * Constructor.
     *
     * @param initialCapacity the initial map capacity
     * @param loadFactor      the amount 0.0-1.0 at which to resize the map
     */
    public MultiHashMap(int initialCapacity, float loadFactor) {
        internalMap = new HashMap<K, Collection<V>>(initialCapacity, loadFactor);
    }

    /**
     * Constructor that copies the input map creating an independent copy.
     * <p/>
     * The values are not cloned.
     * <p/>
     *
     * @param mapToCopy a Map to copy
     */
    public MultiHashMap(Map<K, V> mapToCopy) {
        // be careful of JDK 1.3 vs 1.4 differences
        internalMap = new HashMap<K, Collection<V>>((int) (mapToCopy.size() * 1.4f));
        putAll(mapToCopy);
    }

    /**
     * Constructor that copies the input MultiMap creating an independent copy.
     * <p/>
     * Each internal collection is also cloned.
     * <p/>
     * NOTE: From Commons Collections 3.1 this method correctly copies a MultiMap
     * to form a truly independent new map.
     *
     * @param mapToCopy a Map to copy
     */
    public MultiHashMap(MultiMap<K,V> mapToCopy) {
        internalMap = new HashMap<K, Collection<V>>((int) (mapToCopy.size() * 1.4f));
        for (Iterator<Map.Entry<K,Collection<V>>> it = mapToCopy.entrySet().iterator(); it.hasNext();) {
            Map.Entry<K,Collection<V>> entry = it.next();
            Collection<V> coll = entry.getValue();
            Collection<V> newColl = createCollection(coll);
            internalMap.put(entry.getKey(), newColl);
        }

    }

    /**
     * Read the object during deserialization.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // This method is needed because the 1.2/1.3 Java deserialisation called
        // put and thus messed up that method

        // default read object
        s.defaultReadObject();

        // problem only with jvm <1.4
        String version = "1.2";
        try {
            version = System.getProperty("java.version");
        } catch (SecurityException ex) {
            // ignore and treat as 1.2/1.3
        }

        if (version.startsWith("1.2") || version.startsWith("1.3")) {
            for (Iterator<Map.Entry<K,Collection<V>>> iterator = entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<K,Collection<V>> entry = iterator.next();
                // put has created a extra collection level, remove it
                internalMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the total size of the map by counting all the values.
     *
     * @return the total size of the map counting all values
     * @since Commons Collections 3.1
     */
    public int totalSize() {
        int total = 0;
        Collection<Collection<V>> values = internalMap.values();
        for (Iterator<Collection<V>> it = values.iterator(); it.hasNext();) {
            Collection<V> coll = it.next();
            total += coll.size();
        }
        return total;
    }

    /**
     * Gets the collection mapped to the specified key.
     * This method is a convenience method to typecast the result of <code>get(key)</code>.
     *
     * @param key the key to retrieve
     * @return the collection mapped to the key, null if no mapping
     * @since Commons Collections 3.1
     */
    public Collection<V> getCollection(Object key) {
        return internalMap.get(key);
    }

    /**
     * Gets the size of the collection mapped to the specified key.
     *
     * @param key the key to get size for
     * @return the size of the collection at the key, zero if key not in map
     * @since Commons Collections 3.1
     */
    public int size(Object key) {
        Collection<V> coll = getCollection(key);
        if (coll == null) {
            return 0;
        }
        return coll.size();
    }

    /**
     * Gets an iterator for the collection mapped to the specified key.
     *
     * @param key the key to get an iterator for
     * @return the iterator of the collection at the key, empty iterator if key not in map
     * @since Commons Collections 3.1
     */
    public Iterator<V> iterator(Object key) {
        Collection<V> coll = getCollection(key);
        if (coll == null) {
            return EmptyIterator.INSTANCE;
        }
        return coll.iterator();
    }

    /**
     * Adds the value to the collection associated with the specified key.
     * <p/>
     * Unlike a normal <code>Map</code> the previous value is not replaced.
     * Instead the new value is added to the collection stored against the key.
     *
     * @param key   the key to store against
     * @param value the value to add to the collection at the key
     * @return the value added if the map changed and null if the map did not change
     */
    public V put(K key, V value) {
        // NOTE:: put is called during deserialization in JDK < 1.4 !!!!!!
        //        so we must have a readObject()
        Collection<V> coll = getCollection(key);
        if (coll == null) {
            coll = createCollection(null);
            internalMap.put(key, coll);
        }
        boolean results = coll.add(value);
        return results ? value : null;
    }

    /**
     * Adds a collection of values to the collection associated with the specified key.
     *
     * @param key    the key to store against
     * @param values the values to add to the collection at the key, null ignored
     * @return true if this map changed
     * @since Commons Collections 3.1
     */
    public boolean putAll(K key, Collection<? extends V> values) {
        if (values == null || values.size() == 0) {
            return false;
        }
        Collection<V> coll = getCollection(key);
        if (coll == null) {
            coll = createCollection(values);
            if (coll.size() == 0) {
                return false;
            }
            internalMap.put(key, coll);
            return true;
        } else {
            return coll.addAll(values);
        }
    }

    /**
     * Checks whether the map contains the value specified.
     * <p/>
     * This checks all collections15 against all keys for the value, and thus could be slow.
     *
     * @param value the value to search for
     * @return true if the map contains the value
     */
    public boolean containsValue(Object value) {
        Set<Map.Entry<K,Collection<V>>> pairs = internalMap.entrySet();

        if (pairs == null) {
            return false;
        }
        Iterator<Map.Entry<K,Collection<V>>> pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            Map.Entry<K,Collection<V>> keyValuePair = pairsIterator.next();
            Collection<V> coll = keyValuePair.getValue();
            if (coll.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the collection at the specified key contains the value.
     *
     * @param value the value to search for
     * @return true if the map contains the value
     * @since Commons Collections 3.1
     */
    public boolean containsValue(Object key, Object value) {
        Collection<V> coll = getCollection(key);
        if (coll == null) {
            return false;
        }
        return coll.contains(value);
    }

    /**
     * Removes a specific value from map.
     * <p/>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p/>
     * If the last value for a key is removed, <code>null</code> will be returned
     * from a subsequant <code>get(key)</code>.
     *
     * @param key  the key to remove from
     * @param item the value to remove
     * @return the value removed (which was passed in), null if nothing removed
     */
    public V remove(Object key, Object item) {
        Collection valuesForKey = getCollection(key);
        if (valuesForKey == null) {
            return null;
        }
        valuesForKey.remove(item);

        // remove the list if it is now empty
        // (saves space, and allows equals to work)
        if (valuesForKey.isEmpty()) {
            remove(key);
        }
        return (V)item;
    }

    /**
     * Clear the map.
     * <p/>
     * This clears each collection in the map, and so may be slow.
     */
    public void clear() {
        // For gc, clear each list in the map
        Set<Map.Entry<K,Collection<V>>> pairs = internalMap.entrySet();
        Iterator<Map.Entry<K,Collection<V>>> pairsIterator = pairs.iterator();
        while (pairsIterator.hasNext()) {
            Map.Entry<K,Collection<V>> keyValuePair = pairsIterator.next();
            Collection<V> coll = keyValuePair.getValue();
            coll.clear();
        }
        internalMap.clear();
    }

    public int size() {
        return internalMap.size();
    }

    public Collection<V> get(Object key) {
        return internalMap.get(key);
    }

    public Collection<V> remove(Object key) {
        return internalMap.remove(key);
    }

    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        for (K key : map.keySet()) {
            put(key, map.get(key));
        }
    }

    public void putAll(MultiMap<? extends K, ? extends V> map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<? extends K, Collection<? extends V>> entry = (Map.Entry<? extends K, Collection<? extends V>>) it.next();
            for (V v : entry.getValue()) {
                put(entry.getKey(), v);
            }
        }
    }

    public Set<K> keySet() {
        return internalMap.keySet();
    }

    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return internalMap.entrySet();
    }

    public Map<K, Collection<V>> map() {
        return internalMap;
    }

    /**
     * Gets a collection containing all the values in the map.
     * <p/>
     * This returns a collection containing the combination of values from all keys.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
        Collection vs = values;
        return vs != null ? vs : (values = new Values<V>());
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class to view the elements.
     */
    private class Values<T> extends AbstractCollection<V> {

        public Iterator<V> iterator() {
            return new ValueIterator<V>();
        }

        public int size() {
            int compt = 0;
            Iterator it = iterator();
            while (it.hasNext()) {
                it.next();
                compt++;
            }
            return compt;
        }

        public void clear() {
            MultiHashMap.this.clear();
        }

    }

    /**
     * Inner iterator to view the elements.
     */
    private class ValueIterator<T> implements Iterator<V> {
        private Iterator<Collection<V>> backedIterator;
        private Iterator<V> tempIterator;

        private ValueIterator() {
            backedIterator = internalMap.values().iterator();
        }

        private boolean searchNextIterator() {
            while (tempIterator == null || tempIterator.hasNext() == false) {
                if (backedIterator.hasNext() == false) {
                    return false;
                }
                tempIterator = backedIterator.next().iterator();
            }
            return true;
        }

        public boolean hasNext() {
            return searchNextIterator();
        }

        public V next() {
            if (searchNextIterator() == false) {
                throw new NoSuchElementException();
            }
            return tempIterator.next();
        }

        public void remove() {
            if (tempIterator == null) {
                throw new IllegalStateException();
            }
            tempIterator.remove();
        }

    }

    //-----------------------------------------------------------------------
    /**
     * Clones the map creating an independent copy.
     * <p/>
     * The clone will shallow clone the collections15 as well as the map.
     *
     * @return the cloned map
     */
    public Object clone() {
        MultiHashMap<K,V> cloned = new MultiHashMap<K, V>();
        for (Iterator<Map.Entry<K,Collection<V>>> it = internalMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<K,Collection<V>> entry = it.next();
            for (V v : entry.getValue()) {
                cloned.put(entry.getKey(), v);
            }
        }
        return cloned;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MultiHashMap) {
            return internalMap.equals(((MultiHashMap)obj).map());
        } else return false;
    }

    public int hashCode() {
        return internalMap.hashCode();
    }

    /**
     * Creates a new instance of the map value Collection container.
     * <p/>
     * This method can be overridden to use your own collection type.
     *
     * @param coll the collection to copy, may be null
     * @return the new collection
     */
    protected Collection<V> createCollection(Collection<? extends V> coll) {
        if (coll == null) {
            return new ArrayList<V>();
        } else {
            return new ArrayList<V>(coll);
        }
    }

    public String toString() {
        return internalMap.toString();
    }

}

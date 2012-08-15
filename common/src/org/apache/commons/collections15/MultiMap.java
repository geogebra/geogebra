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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Defines a map that holds a collection of values against each key.
 * <p/>
 * A <code>MultiMap</code> is a Map with slightly different semantics.
 * Putting a value into the map will add the value to a Collection at that key.
 * Getting a value will return a Collection, holding all the values put to that key.
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
 * <code>coll</code> will be a collection containing "A", "B", "C".
 * <p/>
 * NOTE: Note: this new, generics-friendly version of the MultiMap interface does
 * NOT extend java.util.Map! This is because MultiMap breaks the Map contract in
 * too many ways to allow generics support. However, you can get a live java.util.Map
 * for a MultiMap with the method {@link #map()}.
 *
 * @author Christopher Berry
 * @author James Strachan
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 2.0
 */
public interface MultiMap <K,V> {

    /**
     * Removes a specific value from map.
     * <p/>
     * The item is removed from the collection mapped to the specified key.
     * Other values attached to that key are unaffected.
     * <p/>
     * If the last value for a key is removed, implementations typically
     * return <code>null</code> from a subsequant <code>get(Object)</code>, however
     * they may choose to return an empty collection.
     *
     * @param key  the key to remove from
     * @param item the item to remove
     * @return the value removed (which was passed in), null if nothing removed
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException            if the key or value is of an invalid type
     * @throws NullPointerException          if the key or value is null and null is invalid
     */
    public V remove(Object key, Object item);

    /**
     * Gets the number of values in this map for the given key.
     * <p/>
     * Implementations return the count of keys in the map, or 0 if there are no values for the given key.
     *
     * @return the number of values in this map for the given key.
     */
    int size(Object key);

    //-----------------------------------------------------------------------
    /**
     * Gets the number of keys in this map.
     * <p/>
     * Implementations return the count of keys in the map.
     *
     * @return the number of key-collection mappings in this map
     */
    int size();

    /**
     * Gets the collection of values associated with the specified key.
     * <p/>
     * The returned value will implement <code>Collection</code>. Implementations
     * are free to declare that they return <code>Collection</code> subclasses
     * such as <code>List</code> or <code>Set</code>.
     * <p/>
     * Implementations return <code>null</code> if no values have
     * been mapped to the key.
     * <p/>
     * Implementations may choose to return a clone of the internal collection.
     *
     * @param key the key to retrieve
     * @return the <code>Collection</code> of values, implementations should
     *         return <code>null</code> for no mapping, but may return an empty collection
     * @throws ClassCastException   if the key is of an invalid type
     * @throws NullPointerException if the key is null and null keys are invalid
     */
    Collection<V> get(Object key);

    /**
     * Checks whether the map contains the value specified.
     * <p/>
     * Implementations check all collections15 against all keys for the value.
     *
     * @param value the value to search for
     * @return true if the map contains the value
     * @throws ClassCastException   if the value is of an invalid type
     * @throws NullPointerException if the value is null and null value are invalid
     */
    boolean containsValue(Object value);

    /**
     * Checks whether the map contains the value specified, at the key specified.
     *
     * @param value the value to search for
     * @param key the key against which to search for the value
     * @return true if the map contains the value
     * @throws ClassCastException   if the value is of an invalid type
     * @throws NullPointerException if the value is null and null value are invalid
     */
    boolean containsValue(Object key, Object value);

    /**
     * Adds the value to the collection associated with the specified key.
     * <p/>
     * Unlike a normal <code>Map</code> the previous value is not replaced.
     * Instead the new value is added to the collection stored against the key.
     * The collection may be a <code>List</code>, <code>Set</code> or other
     * collection dependent on implementation.
     *
     * @param key   the key to store against
     * @param value the value to add to the collection at the key
     * @return typically the value added if the map changed and null if the map did not change
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException            if the key or value is of an invalid type
     * @throws NullPointerException          if the key or value is null and null is invalid
     * @throws IllegalArgumentException      if the key or value is invalid
     */
    V put(K key, V value);

    /**
     * Removes all values associated with the specified key.
     * <p/>
     * Implementations typically return <code>null</code> from a subsequent
     * <code>get(Object)</code>, however they may choose to return an empty collection.
     *
     * @param key the key to remove values from
     * @return the <code>Collection</code> of values removed, implementations should
     *         return <code>null</code> for no mapping found, but may return an empty collection
     * @throws UnsupportedOperationException if the map is unmodifiable
     * @throws ClassCastException            if the key is of an invalid type
     * @throws NullPointerException          if the key is null and null keys are invalid
     */
    Collection<V> remove(Object key);

    /**
     * Gets a collection containing all the values in the map.
     * <p/>
     * Inplementations return a collection containing the combination
     * of values from all keys.
     *
     * @return a collection view of the values contained in this map
     */
    Collection<V> values();

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key.
     * @throws ClassCastException   if the key is of an inappropriate type for
     *                              this map (optional).
     * @throws NullPointerException if the key is <tt>null</tt> and this map
     *                              does not permit <tt>null</tt> keys (optional).
     */
    boolean containsKey(Object key);

    // Modification Operations

    // Bulk Operations

    /**
     * Copies all of the mappings from the specified map to this map
     * (optional operation).  The effect of this call is equivalent to that
     * of calling {@link #put(Object,Object) put(k, v)} on this map once
     * for each mapping from key <tt>k</tt> to value <tt>v</tt> in the
     * specified map.  The behavior of this operation is unspecified if the
     * specified map is modified while the operation is in progress.
     *
     * @param t Mappings to be stored in this map.
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
     *                                       not supported by this map.
     * @throws ClassCastException            if the class of a key or value in the
     *                                       specified map prevents it from being stored in this map.
     * @throws IllegalArgumentException      some aspect of a key or value in the
     *                                       specified map prevents it from being stored in this map.
     * @throws NullPointerException          if the specified map is <tt>null</tt>, or if
     *                                       this map does not permit <tt>null</tt> keys or values, and the
     *                                       specified map contains <tt>null</tt> keys or values.
     */
    void putAll(Map<? extends K, ? extends V> t);

    /**
     * Copies all of the mappings from the specified multimap to this multimap
     * (optional operation).  The effect of this call is equivalent to that
     * of calling {@link #put(Object,Object) put(k, v)} on this map once
     * for each mapping from key to collections15 of values in the
     * specified multimap.  The behavior of this operation is unspecified if the
     * specified multimap is modified while the operation is in progress.
     *
     * @param t Mappings to be stored in this map.
     * @throws UnsupportedOperationException if the <tt>putAll</tt> method is
     *                                       not supported by this map.
     * @throws ClassCastException            if the class of a key or value in the
     *                                       specified map prevents it from being stored in this map.
     * @throws IllegalArgumentException      some aspect of a key or value in the
     *                                       specified map prevents it from being stored in this map.
     * @throws NullPointerException          if the specified map is <tt>null</tt>, or if
     *                                       this map does not permit <tt>null</tt> keys or values, and the
     *                                       specified map contains <tt>null</tt> keys or values.
     */
    void putAll(MultiMap<? extends K, ? extends V> t);

    /**
     * Copies all of the values in the given collection in to the multimap against the given key.
     * @param key the key against which to store the values.
     * @param values the collection of values to map to the key.
     */
    boolean putAll(K key, Collection<? extends V> values);

    Iterator<V> iterator(Object key);

    /**
     * Removes all mappings from this map (optional operation).
     *
     * @throws UnsupportedOperationException clear is not supported by this
     *                                       map.
     */
    void clear();


    // Views

    /**
     * Returns a set view of the keys contained in this map.  The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa.  If the map is modified while an iteration over the set is
     * in progress (except through the iterator's own <tt>remove</tt>
     * operation), the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding mapping from
     * the map, via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt> <tt>retainAll</tt>, and <tt>clear</tt> operations.
     * It does not support the add or <tt>addAll</tt> operations.
     *
     * @return a set view of the keys contained in this map.
     */
    Set<K> keySet();

    /**
     * Returns a set view of the mappings contained in this map.  Each element
     * in the returned set is a {@link Map.Entry}.  The set is backed by the
     * map, so changes to the map are reflected in the set, and vice-versa.
     * If the map is modified while an iteration over the set is in progress
     * (except through the iterator's own <tt>remove</tt> operation, or through
     * the <tt>setValue</tt> operation on a map entry returned by the iterator)
     * the results of the iteration are undefined.  The set supports element
     * removal, which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map.
     */
    Set<Map.Entry<K, Collection<V>>> entrySet();

    /**
     * Returns a java.util.Map&lt;K,Collection&lt;V&gt;&gt; for this MultiMap.
     *
     * @return the underlying java.util.Map for this MultiMap.
     */
    Map<K,Collection<V>> map();

    // Comparison and hashing

    /**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two Maps
     * represent the same mappings.  More formally, two maps <tt>t1</tt> and
     * <tt>t2</tt> represent the same mappings if
     * <tt>t1.entrySet().equals(t2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @param o object to be compared for equality with this map.
     * @return <tt>true</tt> if the specified object is equal to this map.
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this map.  The hash code of a map
     * is defined to be the sum of the hashCodes of each entry in the map's
     * entrySet view.  This ensures that <tt>t1.equals(t2)</tt> implies
     * that <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps
     * <tt>t1</tt> and <tt>t2</tt>, as required by the general
     * contract of Object.hashCode.
     *
     * @return the hash code value for this map.
     * @see Map.Entry#hashCode()
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();

}

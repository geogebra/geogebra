// GenericsNote: Converted -- refactored heavily, and now must be a map of String -> ?.
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A case-insensitive <code>Map</code>.
 * <p/>
 * As entries are added to the map, keys are converted to all lowercase. A new
 * key is compared to existing keys by comparing <code>newKey.toLower()</code>
 * to the lowercase values in the current <code>KeySet.</code>
 * <p/>
 * Null keys are supported.
 * <p/>
 * The <code>keySet()</code> method returns all lowercase keys, or nulls.
 * <p/>
 * Example:
 * <pre><code>
 *  Map map = new CaseInsensitiveMap();
 *  map.put("One", "One");
 *  map.put("Two", "Two");
 *  map.put(null, "Three");
 *  map.put("one", "Four");
 * </code></pre>
 * creates a <code>CaseInsensitiveMap</code> with three entries.<br>
 * <code>map.get(null)</code> returns <code>"Three"</code> and <code>map.get("ONE")</code>
 * returns <code>"Four".</code>  The <code>Set</code> returned by <code>keySet()</code>
 * equals <code>{"one", "two", null}.</code>
 *
 * @author Matt Hall, John Watkinson, Commons-Collections team
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class CaseInsensitiveMap <V> extends AbstractHashedMap<String, V> implements Serializable, Cloneable {

    /**
     * Serialisation version
     */
    private static final long serialVersionUID = -7074655917369299456L;

    /**
     * Constructs a new empty map with default size and load factor.
     */
    public CaseInsensitiveMap() {
        super(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_THRESHOLD);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    public CaseInsensitiveMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is less than one
     * @throws IllegalArgumentException if the load factor is less than zero
     */
    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructor copying elements from another map.
     * <p/>
     * Keys will be converted to lower case strings, which may cause
     * some entries to be removed (if string representation of keys differ
     * only by character case).
     *
     * @param map the map to copy
     * @throws NullPointerException if the map is null
     */
    public CaseInsensitiveMap(Map<? extends String, ? extends V> map) {
        super(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Converts keys to lower case.
     * <p/>
     * Returns null if key is null.
     *
     * @param key the key convert
     * @return the converted key
     */
    protected String convertKey(String key) {
        if (key != null) {
            return key.toString().toLowerCase();
        } else {
            return null;
        }
    }

    @Override public V get(Object key) {
        if (!(key instanceof String)) {
            return super.get(key);
        }
        return super.get(convertKey((String) key));
    }

    @Override public V put(String s, V v) {
        return super.put(convertKey(s), v);
    }

    @Override public void putAll(Map<? extends String, ? extends V> map) {
        Set entries = map.entrySet();
        for (Iterator<Entry<? extends String, ? extends V>> iterator = entries.iterator(); iterator.hasNext();) {
            Entry<? extends String, ? extends V> entry = iterator.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Clones the map without cloning the keys or values.
     *
     * @return a shallow clone
     */
    public Object clone() {
        return super.clone();
    }

    /**
     * Write the map out using a custom routine.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        doWriteObject(out);
    }

    /**
     * Read the map in using a custom routine.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        doReadObject(in);
    }

}

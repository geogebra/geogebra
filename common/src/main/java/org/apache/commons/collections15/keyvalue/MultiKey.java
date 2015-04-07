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
package org.apache.commons.collections15.keyvalue;

import java.io.Serializable;
import java.util.Arrays;

/**
 * A <code>MultiKey</code> allows multiple map keys to be merged together.
 * <p/>
 * The purpose of this class is to avoid the need to write code to handle
 * maps of maps. An example might be the need to lookup a filename by
 * key and locale. The typical solution might be nested maps. This class
 * can be used instead by creating an instance passing in the key and locale.
 * <p/>
 * Example usage:
 * <pre>
 * // populate map with data mapping key+locale to localizedText
 * Map map = new HashMap();
 * MultiKey multiKey = new MultiKey(key, locale);
 * map.put(multiKey, localizedText);
 * <p/>
 * // later retireve the localized text
 * MultiKey multiKey = new MultiKey(key, locale);
 * String localizedText = (String) map.get(multiKey);
 * </pre>
 *
 * @author Howard Lewis Ship
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class MultiKey <K> implements Serializable {
    // This class could implement List, but that would confuse it's purpose

    /**
     * Serialisation version
     */
    private static final long serialVersionUID = 4465448607415788805L;

    /**
     * The individual keys
     */
    private final K[] keys;
    /**
     * The cached hashCode
     */
    private final int hashCode;

    /**
     * Constructor taking two keys.
     * <p/>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param keys the keys
     */
    public MultiKey(K... keys) {
        this(keys, true);
    }

    /**
     * Constructor taking an array of keys, optionally choosing whether to clone.
     * <p/>
     * <b>If the array is not cloned, then it must not be modified.</b>
     * <p/>
     * This method is public for performance reasons only, to avoid a clone.
     * The hashcode is calculated once here in this method.
     * Therefore, changing the array passed in would not change the hashcode but
     * would change the equals method, which is a bug.
     * <p/>
     * This is the only fully safe usage of this constructor, as the object array
     * is never made available in a variable:
     * <pre>
     * new MultiKey(new Object[] {...}, false);
     * </pre>
     * <p/>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the MultiKey.
     *
     * @param keys      the array of keys, not null
     * @param makeClone true to clone the array, false to assign it
     * @throws IllegalArgumentException if the key array is null
     * @since Commons Collections 3.1
     */
    public MultiKey(K[] keys, boolean makeClone) {
        super();
        if (keys == null) {
            throw new IllegalArgumentException("The array of keys must not be null");
        }
        if (makeClone) {
            this.keys = keys.clone();
        } else {
            this.keys = keys;
        }

        int total = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) {
                total ^= keys[i].hashCode();
            }
        }
        hashCode = total;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Gets a clone of the array of keys.
     * <p/>
     * The keys should be immutable
     * If they are not then they must not be changed.
     *
     * @return the individual keys
     */
    public K[] getKeys() {
        return keys.clone();
    }

    /**
     * Gets the key at the specified index.
     * <p/>
     * The key should be immutable.
     * If it is not then it must not be changed.
     *
     * @param index the index to retrieve
     * @return the key at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @since Commons Collections 3.1
     */
    public K getKey(int index) {
        return keys[index];
    }

    /**
     * Gets the size of the list of keys.
     *
     * @return the size of the list of keys
     * @since Commons Collections 3.1
     */
    public int size() {
        return keys.length;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Compares this object to another.
     * <p/>
     * To be equal, the other object must be a <code>MultiKey</code> with the
     * same number of keys which are also equal.
     *
     * @param other the other object to compare to
     * @return true if equal
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultiKey) {
            MultiKey otherMulti = (MultiKey) other;
            return Arrays.equals(keys, otherMulti.keys);
        }
        return false;
    }

    /**
     * Gets the combined hash code that is computed from all the keys.
     * <p/>
     * This value is computed once and then cached, so elements should not
     * change their hash codes once created (note that this is the same
     * constraint that would be used if the individual keys elements were
     * themselves {@link java.util.Map Map} keys.
     *
     * @return the hash code
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     * Gets a debugging string version of the key.
     *
     * @return a debugging string
     */
    public String toString() {
        return "MultiKey" + Arrays.asList(keys).toString();
    }

}

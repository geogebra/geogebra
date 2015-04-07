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

import org.apache.commons.collections15.Predicate;

/**
 * Decorates another <code>SortedMap </code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This map exists to provide validation for the decorated map.
 * It is normally created to decorate an empty map.
 * If an object cannot be added to the map, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null keys are added to the map.
 * <pre>SortedMap map = PredicatedSortedSet.decorate(new TreeMap(), NotNullPredicate.INSTANCE, null);</pre>
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class PredicatedSortedMap <K,V> extends PredicatedMap<K, V> implements SortedMap<K, V> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 3359846175935304332L;

    /**
     * Factory method to create a predicated (validating) sorted map.
     * <p/>
     * If there are any elements already in the list being decorated, they
     * are validated.
     *
     * @param map            the map to decorate, must not be null
     * @param keyPredicate   the predicate to validate the keys, null means no check
     * @param valuePredicate the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    public static <K,V> SortedMap<K, V> decorate(SortedMap<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        return new PredicatedSortedMap<K, V>(map, keyPredicate, valuePredicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map            the map to decorate, must not be null
     * @param keyPredicate   the predicate to validate the keys, null means no check
     * @param valuePredicate the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    protected PredicatedSortedMap(SortedMap<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        super(map, keyPredicate, valuePredicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the map being decorated.
     *
     * @return the decorated map
     */
    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap<K, V>) map;
    }

    //-----------------------------------------------------------------------
    public K firstKey() {
        return getSortedMap().firstKey();
    }

    public K lastKey() {
        return getSortedMap().lastKey();
    }

    public Comparator<? super K> comparator() {
        return getSortedMap().comparator();
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> map = getSortedMap().subMap(fromKey, toKey);
        return new PredicatedSortedMap<K, V>(map, keyPredicate, valuePredicate);
    }

    public SortedMap<K, V> headMap(K toKey) {
        SortedMap map = getSortedMap().headMap(toKey);
        return new PredicatedSortedMap<K, V>(map, keyPredicate, valuePredicate);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> map = getSortedMap().tailMap(fromKey);
        return new PredicatedSortedMap<K, V>(map, keyPredicate, valuePredicate);
    }

}

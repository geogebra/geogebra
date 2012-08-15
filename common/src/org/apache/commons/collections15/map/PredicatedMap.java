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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.Predicate;

/**
 * Decorates another <code>Map</code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This map exists to provide validation for the decorated map.
 * It is normally created to decorate an empty map.
 * If an object cannot be added to the map, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null keys are added to the map.
 * <pre>Map map = PredicatedSet.decorate(new HashMap(), NotNullPredicate.INSTANCE, null);</pre>
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class PredicatedMap <K,V> extends AbstractInputCheckedMapDecorator<K, V> implements Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 7412622456128415156L;

    /**
     * The key predicate to use
     */
    protected final Predicate<? super K> keyPredicate;
    /**
     * The value predicate to use
     */
    protected final Predicate<? super V> valuePredicate;

    /**
     * Factory method to create a predicated (validating) map.
     * <p/>
     * If there are any elements already in the list being decorated, they
     * are validated.
     *
     * @param map            the map to decorate, must not be null
     * @param keyPredicate   the predicate to validate the keys, null means no check
     * @param valuePredicate the predicate to validate to values, null means no check
     * @throws IllegalArgumentException if the map is null
     */
    public static <K,V> Map<K, V> decorate(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        return new PredicatedMap<K, V>(map, keyPredicate, valuePredicate);
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
    protected PredicatedMap(Map<K, V> map, Predicate<? super K> keyPredicate, Predicate<? super V> valuePredicate) {
        super(map);
        this.keyPredicate = keyPredicate;
        this.valuePredicate = valuePredicate;

        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            K key = entry.getKey();
            V value = entry.getValue();
            validate(key, value);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     *
     * @param out the output stream
     * @throws IOException
     * @since Commons Collections 3.1
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(map);
    }

    /**
     * Read the map in using a custom routine.
     *
     * @param in the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     * @since Commons Collections 3.1
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        map = (Map<K, V>) in.readObject();
    }

    //-----------------------------------------------------------------------
    /**
     * Validates a key value pair.
     *
     * @param key   the key to validate
     * @param value the value to validate
     * @throws IllegalArgumentException if invalid
     */
    protected void validate(K key, V value) {
        if (keyPredicate != null && keyPredicate.evaluate(key) == false) {
            throw new IllegalArgumentException("Cannot add key - Predicate rejected it");
        }
        if (valuePredicate != null && valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot add value - Predicate rejected it");
        }
    }

    /**
     * Override to validate an object set into the map via <code>setValue</code>.
     *
     * @param value the value to validate
     * @throws IllegalArgumentException if invalid
     * @since Commons Collections 3.1
     */
    protected V checkSetValue(V value) {
        if (valuePredicate.evaluate(value) == false) {
            throw new IllegalArgumentException("Cannot set value - Predicate rejected it");
        }
        return value;
    }

    /**
     * Override to only return true when there is a value transformer.
     *
     * @return true if a value predicate is in use
     * @since Commons Collections 3.1
     */
    protected boolean isSetValueChecking() {
        return (valuePredicate != null);
    }

    //-----------------------------------------------------------------------
    public V put(K key, V value) {
        validate(key, value);
        return map.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        Iterator it = mapToCopy.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            K key = (K) entry.getKey();
            V value = (V) entry.getValue();
            validate(key, value);
        }
        map.putAll(mapToCopy);
    }

}

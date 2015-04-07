// GenericsNote: Converted -- Using a Transformer instead of a Factory is no longer allowed.
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
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.FactoryTransformer;

/**
 * Decorates another <code>Map</code> to create objects in the map on demand.
 * <p/>
 * When the {@link #get(Object)} method is called with a key that does not
 * exist in the map, the factory is used to create the object. The created
 * object will be added to the map using the requested key.
 * <p/>
 * For instance:
 * <pre>
 * Factory factory = new Factory() {
 *     public Object create() {
 *         return new Date();
 *     }
 * }
 * Map lazy = Lazy.map(new HashMap(), factory);
 * Object obj = lazy.get("NOW");
 * </pre>
 * <p/>
 * After the above code is executed, <code>obj</code> will contain
 * a new <code>Date</code> instance. Furthermore, that <code>Date</code>
 * instance is mapped to the "NOW" key in the map.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class LazyMap <K,V> extends AbstractMapDecorator<K, V> implements Map<K, V>, Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 7990956402564206740L;

    /**
     * The factory to use to construct elements
     */
    //protected final Factory<V> factory;

    /**
     * The factory to use to construct elements
     */
    protected final Transformer<K, V> transformer;

    /**
     * Factory method to create a lazily instantiated map.
     *
     * @param map     the map to decorate, must not be null
     * @param factory the factory to use, must not be null
     * @throws IllegalArgumentException if map or factory is null
     */
    public static <K,V> Map<K, V> decorate(Map<K, V> map, Factory<V> factory) {
        return new LazyMap<K, V>(map, factory);
    }

    /**
     * Factory method to create a lazily instantiated map.
     *
     * @param map     		Map to decorate, must not be null
     * @param transformer	Transformer to use, must not be null
     * @throws IllegalArgumentException if map or transformer is null
     */
    public static <K,V> Map<K, V> decorate(Map<K, V> map, Transformer<K, V> transformer) {
        return new LazyMap<K, V>(map, transformer);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param map     the map to decorate, must not be null
     * @param factory the factory to use, must not be null
     * @throws IllegalArgumentException if map or factory is null
     */
    protected LazyMap(Map<K, V> map, Factory<V> factory) {
        super(map);
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        this.transformer = new FactoryTransformer<K, V>(factory);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param map     		Map to decorate, must not be null
     * @param transformer	Transformer to use, must not be null
     * @throws IllegalArgumentException if map or factory is null
     */
    protected LazyMap(Map<K, V> map, Transformer<K, V> transformer) {
        super(map);
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        this.transformer = transformer;
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
    public V get(Object key) {
        // create value for key if key is not currently in the map
        if (map.containsKey(key) == false) {
            V value = this.transformer.transform((K) key);
            map.put((K) key, value);
            return value;
        }
        return map.get(key);
    }

    // no need to wrap keySet, entrySet or values as they are views of
    // existing map entries - you can't do a map-style get on them.
}

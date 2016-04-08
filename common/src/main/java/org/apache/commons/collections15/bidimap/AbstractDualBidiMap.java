/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections15.bidimap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.ResettableIterator;
import org.apache.commons.collections15.collection.AbstractCollectionDecorator;
import org.apache.commons.collections15.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections15.keyvalue.AbstractMapEntryDecorator;


/**
 * Abstract <code>BidiMap</code> implemented using two maps.
 * <p>
 * An implementation can be written simply by implementing the
 * <code>createMap</code> method.
 *
 * @see DualHashBidiMap
 * @see DualTreeBidiMap
 * @since Commons Collections 3.0
 * @version $Id$
 *
 * @author Matthew Hawthorne
 * @author Stephen Colebourne
 */
public abstract class AbstractDualBidiMap<K, V> implements BidiMap<K, V> {

    /**
     * Normal delegate map.
     */
    protected Map<K, V> normalMap;

    /**
     * Reverse delegate map.
     */
    protected Map<V, K> reverseMap;

    /**
     * Inverse view of this map.
     */
    protected BidiMap<V, K> inverseBidiMap = null;

    /**
     * View of the keys.
     */
    protected Set<K> keySet = null;

    /**
     * View of the values.
     */
    protected Collection<V> values = null;

    /**
     * View of the entries.
     */
    protected Set<Map.Entry<K, V>> entrySet = null;

    /**
     * Creates an empty map, initialised by <code>createMap</code>.
     * <p>
     * This constructor remains in place for deserialization.
     * All other usage is deprecated in favour of
     * {@link #AbstractDualBidiMap(Map, Map)}.
     */
    protected AbstractDualBidiMap() {
        super();
    }

    /**
     * Creates an empty map using the two maps specified as storage.
     * <p>
     * The two maps must be a matching pair, normal and reverse.
     * They will typically both be empty.
     * <p>
     * Neither map is validated, so nulls may be passed in.
     * If you choose to do this then the subclass constructor must populate
     * the <code>maps[]</code> instance variable itself.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @since Commons Collections 3.1
     */
    protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap) {
        super();
        this.normalMap = normalMap;
        this.reverseMap = reverseMap;
    }

    /**
     * Constructs a map that decorates the specified maps,
     * used by the subclass <code>createBidiMap</code> implementation.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseBidiMap  the inverse BidiMap
     */
    protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super();
        this.normalMap = normalMap;
        this.reverseMap = reverseMap;
        this.inverseBidiMap = inverseBidiMap;
    }

    /**
     * Creates a new instance of the subclass.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap  the reverse direction map
     * @param inverseMap  this map, which is the inverse in the new map
     * @return the inverse map
     */
    protected abstract BidiMap<V, K> createBidiMap(Map<V, K> normalMap, Map<K, V> reverseMap, BidiMap<K, V> inverseMap);

    // Map delegation
    //-----------------------------------------------------------------------
    public V get(Object key) {
        return normalMap.get(key);
    }

    public int size() {
        return normalMap.size();
    }

    public boolean isEmpty() {
        return normalMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return normalMap.containsKey(key);
    }

    @Override
    public boolean equals(Object obj) {
        return normalMap.equals(obj);
    }

    @Override
    public int hashCode() {
        return normalMap.hashCode();
    }

    @Override
    public String toString() {
        return normalMap.toString();
    }

    // BidiMap changes
    //-----------------------------------------------------------------------
    public V put(K key, V value) {
        if (normalMap.containsKey(key)) {
            reverseMap.remove(normalMap.get(key));
        }
        if (reverseMap.containsKey(value)) {
            normalMap.remove(reverseMap.get(value));
        }
        final V obj = normalMap.put(key, value);
        reverseMap.put(value, key);
        return obj;
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public V remove(Object key) {
        V value = null;
        if (normalMap.containsKey(key)) {
            value = normalMap.remove(key);
            reverseMap.remove(value);
        }
        return value;
    }

    public void clear() {
        normalMap.clear();
        reverseMap.clear();
    }

    public boolean containsValue(Object value) {
        return reverseMap.containsKey(value);
    }

    // BidiMap
    //-----------------------------------------------------------------------
    /**
     * Obtains a <code>MapIterator</code> over the map.
     * The iterator implements <code>ResetableMapIterator</code>.
     * This implementation relies on the entrySet iterator.
     * <p>
     * The setValue() methods only allow a new value to be set.
     * If the value being set is already in the map, an IllegalArgumentException
     * is thrown (as setValue cannot change the size of the map).
     *
     * @return a map iterator
     */
    public MapIterator<K, V> mapIterator() {
        return new BidiMapIterator<K, V>(this);
    }

    public K getKey(Object value) {
        return reverseMap.get(value);
    }

    public K removeValue(Object value) {
        K key = null;
        if (reverseMap.containsKey(value)) {
            key = reverseMap.remove(value);
            normalMap.remove(key);
        }
        return key;
    }

    public BidiMap<V, K> inverseBidiMap() {
        if (inverseBidiMap == null) {
            inverseBidiMap = createBidiMap(reverseMap, normalMap, this);
        }
        return inverseBidiMap;
    }

    // Map views
    //-----------------------------------------------------------------------
    /**
     * Gets a keySet view of the map.
     * Changes made on the view are reflected in the map.
     * The set supports remove and clear but not add.
     *
     * @return the keySet view
     */
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet<K>(this);
        }
        return keySet;
    }

    /**
     * Creates a key set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @param iterator  the iterator to decorate
     * @return the keySet iterator
     */
    protected Iterator<K> createKeySetIterator(Iterator<K> iterator) {
        return new KeySetIterator<K>(iterator, this);
    }

    /**
     * Gets a values view of the map.
     * Changes made on the view are reflected in the map.
     * The set supports remove and clear but not add.
     *
     * @return the values view
     */
    public Set<V> values() {
        if (values == null) {
            values = new Values<V>(this);
        }
        return (Set<V>) values;
    }

    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @param iterator  the iterator to decorate
     * @return the values iterator
     */
    protected Iterator<V> createValuesIterator(Iterator<V> iterator) {
        return new ValuesIterator<V>(iterator, this);
    }

    /**
     * Gets an entrySet view of the map.
     * Changes made on the set are reflected in the map.
     * The set supports remove and clear but not add.
     * <p>
     * The Map Entry setValue() method only allow a new value to be set.
     * If the value being set is already in the map, an IllegalArgumentException
     * is thrown (as setValue cannot change the size of the map).
     *
     * @return the entrySet view
     */
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet<K, V>(this);
        }
        return entrySet;
    }

    /**
     * Creates an entry set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @param iterator  the iterator to decorate
     * @return the entrySet iterator
     */
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
        return new EntrySetIterator<K, V>(iterator, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class View.
     */
    @SuppressWarnings("serial")
    protected static abstract class View<K, V, E> extends AbstractCollectionDecorator<E> {

        /** The parent map */
        protected final AbstractDualBidiMap<K, V> parent;

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param coll  the collection view being decorated
         * @param parent  the parent BidiMap
         */
        protected View(Collection<E> coll, AbstractDualBidiMap<K, V> parent) {
            super(coll);
            this.parent = parent;
        }

        @Override
        public boolean removeAll(Collection<?> coll) {
            if (parent.isEmpty() || coll.isEmpty()) {
                return false;
            }
            boolean modified = false;
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                if (coll.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean retainAll(Collection<?> coll) {
            if (parent.isEmpty()) {
                return false;
            }
            if (coll.isEmpty()) {
                parent.clear();
                return true;
            }
            boolean modified = false;
            Iterator<E> it = iterator();
            while (it.hasNext()) {
                if (coll.contains(it.next()) == false) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public void clear() {
            parent.clear();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class KeySet.
     */
    protected static class KeySet<K> extends View<K, Object, K> implements Set<K> {

        /** Serialization version */
        private static final long serialVersionUID = -7107935777385040694L;

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param parent  the parent BidiMap
         */
        @SuppressWarnings("unchecked")
        protected KeySet(AbstractDualBidiMap<K, ?> parent) {
            super(parent.normalMap.keySet(), (AbstractDualBidiMap<K, Object>) parent);
        }

        @Override
        public Iterator<K> iterator() {
            return parent.createKeySetIterator(super.iterator());
        }

        @Override
        public boolean contains(Object key) {
            return parent.normalMap.containsKey(key);
        }

        @Override
        public boolean remove(Object key) {
            if (parent.normalMap.containsKey(key)) {
                Object value = parent.normalMap.remove(key);
                parent.reverseMap.remove(value);
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class KeySetIterator.
     */
    protected static class KeySetIterator<K> extends AbstractIteratorDecorator<K> {

        /** The parent map */
        protected final AbstractDualBidiMap<K, ?> parent;

        /** The last returned key */
        protected K lastKey = null;

        /** Whether remove is allowed at present */
        protected boolean canRemove = false;

        /**
         * Constructor.
         * @param iterator  the iterator to decorate
         * @param parent  the parent map
         */
        protected KeySetIterator(Iterator<K> iterator, AbstractDualBidiMap<K, ?> parent) {
            super(iterator);
            this.parent = parent;
        }

        @Override
        public K next() {
            lastKey = super.next();
            canRemove = true;
            return lastKey;
        }

        @Override
        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            Object value = parent.normalMap.get(lastKey);
            super.remove();
            parent.reverseMap.remove(value);
            lastKey = null;
            canRemove = false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class Values.
     */
    protected static class Values<V> extends View<Object, V, V> implements Set<V> {

        /** Serialization version */
        private static final long serialVersionUID = 4023777119829639864L;

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param parent  the parent BidiMap
         */
        @SuppressWarnings("unchecked")
        protected Values(AbstractDualBidiMap<?, V> parent) {
            super(parent.normalMap.values(), (AbstractDualBidiMap<Object, V>) parent);
        }

        @Override
        public Iterator<V> iterator() {
            return parent.createValuesIterator(super.iterator());
        }

        @Override
        public boolean contains(Object value) {
            return parent.reverseMap.containsKey(value);
        }

        @Override
        public boolean remove(Object value) {
            if (parent.reverseMap.containsKey(value)) {
                Object key = parent.reverseMap.remove(value);
                parent.normalMap.remove(key);
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class ValuesIterator.
     */
    protected static class ValuesIterator<V> extends AbstractIteratorDecorator<V> {

        /** The parent map */
        protected final AbstractDualBidiMap<Object, V> parent;

        /** The last returned value */
        protected V lastValue = null;

        /** Whether remove is allowed at present */
        protected boolean canRemove = false;

        /**
         * Constructor.
         * @param iterator  the iterator to decorate
         * @param parent  the parent map
         */
        @SuppressWarnings("unchecked")
        protected ValuesIterator(Iterator<V> iterator, AbstractDualBidiMap<?, V> parent) {
            super(iterator);
            this.parent = (AbstractDualBidiMap<Object, V>) parent;
        }

        @Override
        public V next() {
            lastValue = super.next();
            canRemove = true;
            return lastValue;
        }

        @Override
        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            super.remove(); // removes from maps[0]
            parent.reverseMap.remove(lastValue);
            lastValue = null;
            canRemove = false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class EntrySet.
     */
    protected static class EntrySet<K, V> extends View<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {

        /** Serialization version */
        private static final long serialVersionUID = 4040410962603292348L;

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param parent  the parent BidiMap
         */
        protected EntrySet(AbstractDualBidiMap<K, V> parent) {
            super(parent.normalMap.entrySet(), parent);
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return parent.createEntrySetIterator(super.iterator());
        }

        @Override
        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
            Object key = entry.getKey();
            if (parent.containsKey(key)) {
                V value = parent.normalMap.get(key);
                if (value == null ? entry.getValue() == null : value.equals(entry.getValue())) {
                    parent.normalMap.remove(key);
                    parent.reverseMap.remove(value);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Inner class EntrySetIterator.
     */
    protected static class EntrySetIterator<K, V> extends AbstractIteratorDecorator<Map.Entry<K, V>> {

        /** The parent map */
        protected final AbstractDualBidiMap<K, V> parent;

        /** The last returned entry */
        protected Map.Entry<K, V> last = null;

        /** Whether remove is allowed at present */
        protected boolean canRemove = false;

        /**
         * Constructor.
         * @param iterator  the iterator to decorate
         * @param parent  the parent map
         */
        protected EntrySetIterator(Iterator<Map.Entry<K, V>> iterator, AbstractDualBidiMap<K, V> parent) {
            super(iterator);
            this.parent = parent;
        }

        @Override
        public Map.Entry<K, V> next() {
            last = new MapEntry<K, V>(super.next(), parent);
            canRemove = true;
            return last;
        }

        @Override
        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            // store value as remove may change the entry in the decorator (eg.TreeMap)
            Object value = last.getValue();
            super.remove();
            parent.reverseMap.remove(value);
            last = null;
            canRemove = false;
        }
    }

    /**
     * Inner class MapEntry.
     */
    protected static class MapEntry<K, V> extends AbstractMapEntryDecorator<K, V> {

        /** The parent map */
        protected final AbstractDualBidiMap<K, V> parent;

        /**
         * Constructor.
         * @param entry  the entry to decorate
         * @param parent  the parent map
         */
        protected MapEntry(Map.Entry<K, V> entry, AbstractDualBidiMap<K, V> parent) {
            super(entry);
            this.parent = parent;
        }

        @Override
        public V setValue(V value) {
            K key = MapEntry.this.getKey();
            if (parent.reverseMap.containsKey(value) &&
                parent.reverseMap.get(value) != key) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            parent.put(key, value);
            final V oldValue = super.setValue(value);
            return oldValue;
        }
    }

    /**
     * Inner class MapIterator.
     */
    protected static class BidiMapIterator<K, V> implements MapIterator<K, V>, ResettableIterator<K> {

        /** The parent map */
        protected final AbstractDualBidiMap<K, V> parent;

        /** The iterator being wrapped */
        protected Iterator<Map.Entry<K, V>> iterator;

        /** The last returned entry */
        protected Map.Entry<K, V> last = null;

        /** Whether remove is allowed at present */
        protected boolean canRemove = false;

        /**
         * Constructor.
         * @param parent  the parent map
         */
        protected BidiMapIterator(AbstractDualBidiMap<K, V> parent) {
            super();
            this.parent = parent;
            this.iterator = parent.normalMap.entrySet().iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public K next() {
            last = iterator.next();
            canRemove = true;
            return last.getKey();
        }

        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            // store value as remove may change the entry in the decorator (eg.TreeMap)
            V value = last.getValue();
            iterator.remove();
            parent.reverseMap.remove(value);
            last = null;
            canRemove = false;
        }

        public K getKey() {
            if (last == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return last.getKey();
        }

        public V getValue() {
            if (last == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return last.getValue();
        }

        public V setValue(V value) {
            if (last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (parent.reverseMap.containsKey(value) &&
                parent.reverseMap.get(value) != last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            return parent.put(last.getKey(), value);
        }

        public void reset() {
            iterator = parent.normalMap.entrySet().iterator();
            last = null;
            canRemove = false;
        }

        @Override
        public String toString() {
            if (last != null) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            }
            return "MapIterator[]";
        }
    }

}

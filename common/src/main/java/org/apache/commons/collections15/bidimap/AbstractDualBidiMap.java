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
 * <p/>
 * An implementation can be written simply by implementing the
 * <code>createMap</code> method.
 *
 * @author Matthew Hawthorne
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Id: AbstractDualBidiMap.java,v 1.1 2005/10/11 17:05:19 pents90 Exp $
 * @see DualHashBidiMap
 * @see DualTreeBidiMap
 * @since Commons Collections 3.0
 */
public abstract class AbstractDualBidiMap <K,V> implements BidiMap<K, V> {

    /**
     * Delegate maps.  The first map contains standard entries, and the
     * second contains inverses.
     */
    protected transient Map<K, V> forwardMap;
    protected transient Map<V, K> inverseMap;

    /**
     * Inverse view of this map.
     */
    protected transient BidiMap<V, K> inverseBidiMap = null;
    /**
     * View of the keys.
     */
    protected transient Set<K> keySet = null;
    /**
     * View of the values.
     */
    protected transient Set<V> values = null;
    /**
     * View of the entries.
     */
    protected transient Set<Map.Entry<K, V>> entrySet = null;

    /**
     * Creates an empty map, initialised by <code>createMap</code>.
     * <p/>
     * This constructor remains in place for deserialization.
     * All other usage is deprecated in favour of
     * {@link #AbstractDualBidiMap(Map, Map)}.
     *
     * @deprecated should not be used.
     */
    protected AbstractDualBidiMap() {
        super();
        forwardMap = createMap();
        inverseMap = createMap();
    }

    /**
     * Creates an empty map using the two maps specified as storage.
     * <p/>
     * The two maps must be a matching pair, normal and reverse.
     * They will typically both be empty.
     * <p/>
     * Neither map is validated, so nulls may be passed in.
     * If you choose to do this then the subclass constructor must populate
     * the <code>maps[]</code> instance variable itself.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap the reverse direction map
     * @since Commons Collections 3.1
     */
    protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap) {
        super();
        forwardMap = normalMap;
        inverseMap = reverseMap;
    }

    /**
     * Constructs a map that decorates the specified maps,
     * used by the subclass <code>createBidiMap</code> implementation.
     *
     * @param normalMap      the normal direction map
     * @param reverseMap     the reverse direction map
     * @param inverseBidiMap the inverse BidiMap
     */
    protected AbstractDualBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super();
        forwardMap = normalMap;
        inverseMap = reverseMap;
        this.inverseBidiMap = inverseBidiMap;
    }

    /**
     * Creates a new instance of the map used by the subclass to store data.
     * <p/>
     * This design is deeply flawed and has been deprecated.
     * It relied on subclass data being used during a superclass constructor.
     *
     * @return the map to be used for internal storage
     * @deprecated For constructors, use the new two map constructor.
     *             For deserialization, populate the maps array directly in readObject.
     */
    protected Map createMap() {
        return null;
    }

    /**
     * Creates a new instance of the subclass.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap the reverse direction map
     * @param inverseMap this map, which is the inverse in the new map
     * @return the inverse map
     */
    protected abstract <K,V> BidiMap<K, V> createBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseMap);

    // Map delegation
    //-----------------------------------------------------------------------
    public V get(Object key) {
        return forwardMap.get(key);
    }

    public int size() {
        return forwardMap.size();
    }

    public boolean isEmpty() {
        return forwardMap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return forwardMap.containsKey(key);
    }

    public boolean equals(Object obj) {
        return forwardMap.equals(obj);
    }

    public int hashCode() {
        return forwardMap.hashCode();
    }

    public String toString() {
        return forwardMap.toString();
    }

    // BidiMap changes
    //-----------------------------------------------------------------------
    public V put(K key, V value) {
        if (forwardMap.containsKey(key)) {
            inverseMap.remove(forwardMap.get(key));
        }
        if (inverseMap.containsKey(value)) {
            forwardMap.remove(inverseMap.get(value));
        }
        final V obj = forwardMap.put(key, value);
        inverseMap.put(value, key);
        return obj;
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            put((K) entry.getKey(), (V) entry.getValue());
        }
    }

    public V remove(Object key) {
        V value = null;
        if (forwardMap.containsKey(key)) {
            value = forwardMap.remove(key);
            inverseMap.remove(value);
        }
        return value;
    }

    public void clear() {
        forwardMap.clear();
        inverseMap.clear();
    }

    public boolean containsValue(Object value) {
        return inverseMap.containsKey(value);
    }

    // BidiMap
    //-----------------------------------------------------------------------
    /**
     * Obtains a <code>MapIterator</code> over the map.
     * The iterator implements <code>ResetableMapIterator</code>.
     * This implementation relies on the entrySet iterator.
     * <p/>
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
        return inverseMap.get(value);
    }

    public K removeValue(Object value) {
        K key = null;
        if (inverseMap.containsKey(value)) {
            key = inverseMap.remove(value);
            forwardMap.remove(key);
        }
        return key;
    }

    public BidiMap<V, K> inverseBidiMap() {
        if (inverseBidiMap == null) {
            inverseBidiMap = createBidiMap(inverseMap, forwardMap, this);
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
            keySet = new KeySet<K, V>(this);
        }
        return keySet;
    }

    /**
     * Creates a key set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @param iterator the iterator to decorate
     * @return the keySet iterator
     */
    protected Iterator<K> createKeySetIterator(Iterator<K> iterator) {
        return new KeySetIterator<K, V>(iterator, this);
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
            values = new Values<K, V>(this);
        }
        return values;
    }

    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @param iterator the iterator to decorate
     * @return the values iterator
     */
    protected Iterator<V> createValuesIterator(Iterator<V> iterator) {
        return new ValuesIterator<K, V>(iterator, this);
    }

    /**
     * Gets an entrySet view of the map.
     * Changes made on the set are reflected in the map.
     * The set supports remove and clear but not add.
     * <p/>
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
     * @param iterator the iterator to decorate
     * @return the entrySet iterator
     */
    protected Iterator<Map.Entry<K, V>> createEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
        return new EntrySetIterator<K, V>(iterator, this);
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class View.
     */
    protected static abstract class View <K,V,E> extends AbstractCollectionDecorator<E> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param coll   the collection view being decorated
         * @param parent the parent BidiMap
         */
        protected View(Collection<E> coll, AbstractDualBidiMap<K, V> parent) {
            super(coll);
            this.parent = parent;
        }

        public boolean removeAll(Collection<?> coll) {
            if (parent.isEmpty() || coll.isEmpty()) {
                return false;
            }
            boolean modified = false;
            Iterator it = iterator();
            while (it.hasNext()) {
                if (coll.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        public boolean retainAll(Collection<?> coll) {
            if (parent.isEmpty()) {
                return false;
            }
            if (coll.isEmpty()) {
                parent.clear();
                return true;
            }
            boolean modified = false;
            Iterator it = iterator();
            while (it.hasNext()) {
                if (coll.contains(it.next()) == false) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        public void clear() {
            parent.clear();
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Inner class KeySet.
     */
    protected static class KeySet <K,V> extends View<K, V, K> implements Set<K> {

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param parent the parent BidiMap
         */
        protected KeySet(AbstractDualBidiMap<K, V> parent) {
            super(parent.forwardMap.keySet(), parent);
        }

        public Iterator<K> iterator() {
            return parent.createKeySetIterator(super.iterator());
        }

        public boolean contains(Object key) {
            return parent.forwardMap.containsKey(key);
        }

        public boolean remove(Object key) {
            if (parent.forwardMap.containsKey(key)) {
                Object value = parent.forwardMap.remove(key);
                parent.inverseMap.remove(value);
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class KeySetIterator.
     */
    protected static class KeySetIterator <K,V> extends AbstractIteratorDecorator<K> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;
        /**
         * The last returned key
         */
        protected K lastKey = null;
        /**
         * Whether remove is allowed at present
         */
        protected boolean canRemove = false;

        /**
         * Constructor.
         *
         * @param iterator the iterator to decorate
         * @param parent   the parent map
         */
        protected KeySetIterator(Iterator<K> iterator, AbstractDualBidiMap<K, V> parent) {
            super(iterator);
            this.parent = parent;
        }

        public K next() {
            lastKey = super.next();
            canRemove = true;
            return lastKey;
        }

        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            Object value = parent.forwardMap.get(lastKey);
            super.remove();
            parent.inverseMap.remove(value);
            lastKey = null;
            canRemove = false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class Values.
     */
    protected static class Values <K,V> extends View<K, V, V> implements Set<V> {

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param parent the parent BidiMap
         */
        protected Values(AbstractDualBidiMap<K, V> parent) {
            super(parent.forwardMap.values(), parent);
        }

        public Iterator<V> iterator() {
            return parent.createValuesIterator(super.iterator());
        }

        public boolean contains(Object value) {
            return parent.inverseMap.containsKey(value);
        }

        public boolean remove(Object value) {
            if (parent.inverseMap.containsKey(value)) {
                Object key = parent.inverseMap.remove(value);
                parent.forwardMap.remove(key);
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class ValuesIterator.
     */
    protected static class ValuesIterator <K,V> extends AbstractIteratorDecorator<V> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;
        /**
         * The last returned value
         */
        protected V lastValue = null;
        /**
         * Whether remove is allowed at present
         */
        protected boolean canRemove = false;

        /**
         * Constructor.
         *
         * @param iterator the iterator to decorate
         * @param parent   the parent map
         */
        protected ValuesIterator(Iterator<V> iterator, AbstractDualBidiMap<K, V> parent) {
            super(iterator);
            this.parent = parent;
        }

        public V next() {
            lastValue = super.next();
            canRemove = true;
            return lastValue;
        }

        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            super.remove(); // removes from forwardMap
            parent.inverseMap.remove(lastValue);
            lastValue = null;
            canRemove = false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class EntrySet.
     */
    protected static class EntrySet <K,V> extends View<K, V, Map.Entry<K, V>> implements Set<Map.Entry<K, V>> {

        /**
         * Constructs a new view of the BidiMap.
         *
         * @param parent the parent BidiMap
         */
        protected EntrySet(AbstractDualBidiMap<K, V> parent) {
            super(parent.forwardMap.entrySet(), parent);
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return parent.createEntrySetIterator(super.iterator());
        }

        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry entry = (Map.Entry) obj;
            Object key = entry.getKey();
            if (parent.containsKey(key)) {
                Object value = parent.forwardMap.get(key);
                if (value == null ? entry.getValue() == null : value.equals(entry.getValue())) {
                    parent.forwardMap.remove(key);
                    parent.inverseMap.remove(value);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Inner class EntrySetIterator.
     */
    protected static class EntrySetIterator <K,V> extends AbstractIteratorDecorator<Map.Entry<K, V>> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;
        /**
         * The last returned entry
         */
        protected Map.Entry<K, V> last = null;
        /**
         * Whether remove is allowed at present
         */
        protected boolean canRemove = false;

        /**
         * Constructor.
         *
         * @param iterator the iterator to decorate
         * @param parent   the parent map
         */
        protected EntrySetIterator(Iterator<Map.Entry<K, V>> iterator, AbstractDualBidiMap<K, V> parent) {
            super(iterator);
            this.parent = parent;
        }

        public Map.Entry<K, V> next() {
            last = new MapEntry<K, V>(super.next(), parent);
            canRemove = true;
            return last;
        }

        public void remove() {
            if (canRemove == false) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            // store value as remove may change the entry in the decorator (eg.TreeMap)
            Object value = last.getValue();
            super.remove();
            parent.inverseMap.remove(value);
            last = null;
            canRemove = false;
        }
    }

    /**
     * Inner class MapEntry.
     */
    protected static class MapEntry <K,V> extends AbstractMapEntryDecorator<K, V> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;

        /**
         * Constructor.
         *
         * @param entry  the entry to decorate
         * @param parent the parent map
         */
        protected MapEntry(Map.Entry<K, V> entry, AbstractDualBidiMap<K, V> parent) {
            super(entry);
            this.parent = parent;
        }

        public V setValue(V value) {
            K key = MapEntry.this.getKey();
            if (parent.inverseMap.containsKey(value) && parent.inverseMap.get(value) != key) {
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
    protected static class BidiMapIterator <K,V> implements MapIterator<K, V>, ResettableIterator<K> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;
        /**
         * The iterator being wrapped
         */
        protected Iterator<Map.Entry<K, V>> iterator;
        /**
         * The last returned entry
         */
        protected Map.Entry<K, V> last = null;
        /**
         * Whether remove is allowed at present
         */
        protected boolean canRemove = false;

        /**
         * Constructor.
         *
         * @param parent the parent map
         */
        protected BidiMapIterator(AbstractDualBidiMap<K, V> parent) {
            super();
            this.parent = parent;
            this.iterator = parent.forwardMap.entrySet().iterator();
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
            parent.inverseMap.remove(value);
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
            if (parent.inverseMap.containsKey(value) && parent.inverseMap.get(value) != last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            return parent.put(last.getKey(), value);
        }

        public void reset() {
            iterator = parent.forwardMap.entrySet().iterator();
            last = null;
            canRemove = false;
        }

        public String toString() {
            if (last != null) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            } else {
                return "MapIterator[]";
            }
        }
    }

}

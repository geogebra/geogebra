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
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.OrderedMapIterator;
import org.apache.commons.collections15.ResettableIterator;
import org.apache.commons.collections15.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections15.keyvalue.AbstractMapEntry;
import org.apache.commons.collections15.list.UnmodifiableList;

/**
 * Decorates a <code>Map</code> to ensure that the order of addition is retained
 * using a <code>List</code> to maintain order.
 * <p/>
 * The order will be used via the iterators and toArray methods on the views.
 * The order is also returned by the <code>MapIterator</code>.
 * The <code>orderedMapIterator()</code> method accesses an iterator that can
 * iterate both forwards and backwards through the map.
 * In addition, non-interface methods are provided to access the map by index.
 * <p/>
 * If an object is added to the Map for a second time, it will remain in the
 * original position in the iteration.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Henri Yandell
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class ListOrderedMap <K,V> extends AbstractMapDecorator<K, V> implements OrderedMap<K, V>, Serializable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 2728177751851003750L;

    /**
     * Internal list to hold the sequence of objects
     */
    protected final List<K> insertOrder = new ArrayList<K>();

    /**
     * Factory method to create an ordered map.
     * <p/>
     * An <code>ArrayList</code> is used to retain order.
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    public static <K,V> OrderedMap<K, V> decorate(Map<K, V> map) {
        return new ListOrderedMap<K, V>(map);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructs a new empty <code>ListOrderedMap</code> that decorates
     * a <code>HashMap</code>.
     *
     * @since Commons Collections 3.1
     */
    public ListOrderedMap() {
        this(new HashMap<K, V>());
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param map the map to decorate, must not be null
     * @throws IllegalArgumentException if map is null
     */
    protected ListOrderedMap(Map<K, V> map) {
        super(map);
        insertOrder.addAll(getMap().keySet());
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

    // Implement OrderedMap
    //-----------------------------------------------------------------------
    public MapIterator<K, V> mapIterator() {
        return orderedMapIterator();
    }

    public OrderedMapIterator<K, V> orderedMapIterator() {
        return new ListOrderedMapIterator<K, V>(this);
    }

    /**
     * Gets the first key in this map by insert order.
     *
     * @return the first key currently in this map
     * @throws NoSuchElementException if this map is empty
     */
    public K firstKey() {
        if (size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return insertOrder.get(0);
    }

    /**
     * Gets the last key in this map by insert order.
     *
     * @return the last key currently in this map
     * @throws NoSuchElementException if this map is empty
     */
    public K lastKey() {
        if (size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return insertOrder.get(size() - 1);
    }

    /**
     * Gets the next key to the one specified using insert order.
     * This method performs a list search to find the key and is O(n).
     *
     * @param key the key to find previous for
     * @return the next key, null if no match or at start
     */
    public K nextKey(K key) {
        int index = insertOrder.indexOf(key);
        if (index >= 0 && index < size() - 1) {
            return insertOrder.get(index + 1);
        }
        return null;
    }

    /**
     * Gets the previous key to the one specified using insert order.
     * This method performs a list search to find the key and is O(n).
     *
     * @param key the key to find previous for
     * @return the previous key, null if no match or at start
     */
    public K previousKey(K key) {
        int index = insertOrder.indexOf(key);
        if (index > 0) {
            return insertOrder.get(index - 1);
        }
        return null;
    }

    //-----------------------------------------------------------------------
    public V put(K key, V value) {
        if (getMap().containsKey(key)) {
            // re-adding doesn't change order
            return getMap().put(key, value);
        } else {
            // first add, so add to both map and list
            V result = getMap().put(key, value);
            insertOrder.add(key);
            return result;
        }
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            put((K) entry.getKey(), (V) entry.getValue());
        }
    }

    public V remove(Object key) {
        V result = getMap().remove(key);
        insertOrder.remove(key);
        return result;
    }

    public void clear() {
        getMap().clear();
        insertOrder.clear();
    }

    //-----------------------------------------------------------------------
    public Set<K> keySet() {
        return new KeySetView<K, V>(this);
    }

    public Collection<V> values() {
        return new ValuesView<K, V>(this);
    }

    public Set<Map.Entry<K,V>> entrySet() {
        return new EntrySetView<K,V>(this, this.insertOrder);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Returns the Map as a string.
     *
     * @return the Map as a String
     */
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer();
        buf.append('{');
        boolean first = true;
        Iterator it = entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append(key == this ? "(this Map)" : key);
            buf.append('=');
            buf.append(value == this ? "(this Map)" : value);
        }
        buf.append('}');
        return buf.toString();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the key at the specified index.
     *
     * @param index the index to retrieve
     * @return the key at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public K get(int index) {
        return insertOrder.get(index);
    }

    /**
     * Gets the value at the specified index.
     *
     * @param index the index to retrieve
     * @return the key at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public V getValue(int index) {
        return get(insertOrder.get(index));
    }

    /**
     * Gets the index of the specified key.
     *
     * @param key the key to find the index of
     * @return the index, or -1 if not found
     */
    public int indexOf(Object key) {
        return insertOrder.indexOf(key);
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index the index of the object to remove
     * @return the previous value corresponding the <code>key</code>,
     *         or <code>null</code> if none existed
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Object remove(int index) {
        return remove(get(index));
    }

    /**
     * Gets an unmodifiable List view of the keys which changes as the map changes.
     * <p/>
     * The returned list is unmodifiable because changes to the values of
     * the list (using {@link java.util.ListIterator#set(Object)}) will
     * effectively remove the value from the list and reinsert that value at
     * the end of the list, which is an unexpected side effect of changing the
     * value of a list.  This occurs because changing the key, changes when the
     * mapping is added to the map and thus where it appears in the list.
     * <p/>
     * An alternative to this method is to use {@link #keySet()}.
     *
     * @return The ordered list of keys.
     * @see #keySet()
     */
    public List<K> asList() {
        return UnmodifiableList.decorate(insertOrder);
    }

    //-----------------------------------------------------------------------
    static class ValuesView <K,V> extends AbstractCollection<V> {
        private final ListOrderedMap<K, V> parent;

        ValuesView(ListOrderedMap<K, V> parent) {
            super();
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        public void clear() {
            this.parent.clear();
        }

        public Iterator<V> iterator() {
            return new AbstractIteratorDecorator(parent.entrySet().iterator()) {
                public Object next() {
                    return ((Map.Entry) iterator.next()).getValue();
                }
            };
        }
    }

    //-----------------------------------------------------------------------
    static class KeySetView <K,V> extends AbstractSet<K> {
        private final ListOrderedMap<K, V> parent;

        KeySetView(ListOrderedMap<K, V> parent) {
            super();
            this.parent = parent;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean contains(Object value) {
            return this.parent.containsKey(value);
        }

        public void clear() {
            this.parent.clear();
        }

        public Iterator<K> iterator() {
            final Iterator<Map.Entry<K, V>> entryIterator = parent.entrySet().iterator();
            return new Iterator<K>() {
                public K next() {
                    return entryIterator.next().getKey();
                }

                public boolean hasNext() {
                    return entryIterator.hasNext();
                }

                public void remove() {
                    entryIterator.remove();
                }
            };
        }
    }

    //-----------------------------------------------------------------------    
    static class EntrySetView <K,V> extends AbstractSet<Map.Entry<K, V>> {
        private final ListOrderedMap<K, V> parent;
        private final List<K> insertOrder;
        private Set<Map.Entry<K, V>> entrySet;

        public EntrySetView(ListOrderedMap<K, V> parent, List<K> insertOrder) {
            super();
            this.parent = parent;
            this.insertOrder = insertOrder;
        }

        private Set<Map.Entry<K, V>> getEntrySet() {
            if (entrySet == null) {
                entrySet = parent.getMap().entrySet();
            }
            return entrySet;
        }

        public int size() {
            return this.parent.size();
        }

        public boolean isEmpty() {
            return this.parent.isEmpty();
        }

        public boolean contains(Object obj) {
            return getEntrySet().contains(obj);
        }

        public boolean containsAll(Collection<?> coll) {
            return getEntrySet().containsAll(coll);
        }

        public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            if (getEntrySet().contains(obj)) {
                Object key = ((Map.Entry) obj).getKey();
                parent.remove(key);
                return true;
            }
            return false;
        }

        public void clear() {
            this.parent.clear();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return getEntrySet().equals(obj);
        }

        public int hashCode() {
            return getEntrySet().hashCode();
        }

        public String toString() {
            return getEntrySet().toString();
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return new ListOrderedIterator<K, V>(parent, insertOrder);
        }
    }

    //-----------------------------------------------------------------------
    static class ListOrderedIterator <K,V> implements Iterator<Map.Entry<K, V>> {
        private final ListOrderedMap<K, V> parent;
        private K last = null;
        private Iterator<K> listIterator;

        ListOrderedIterator(ListOrderedMap<K, V> parent, List<K> insertOrder) {
            listIterator = insertOrder.iterator();
            this.parent = parent;
        }

        public Map.Entry<K, V> next() {
            last = listIterator.next();
            return new ListOrderedMapEntry<K, V>(parent, last);
        }

        public void remove() {
            listIterator.remove();
            parent.getMap().remove(last);
        }

        public boolean hasNext() {
            return listIterator.hasNext();
        }
    }

    //-----------------------------------------------------------------------
    static class ListOrderedMapEntry <K,V> extends AbstractMapEntry<K, V> {
        private final ListOrderedMap<K, V> parent;

        ListOrderedMapEntry(ListOrderedMap<K, V> parent, K key) {
            super(key, null);
            this.parent = parent;
        }

        public V getValue() {
            return parent.get(key);
        }

        public V setValue(V value) {
            return parent.getMap().put(key, value);
        }
    }

    //-----------------------------------------------------------------------
    static class ListOrderedMapIterator <K,V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {
        private final ListOrderedMap<K, V> parent;
        private ListIterator<K> iterator;
        private K last = null;
        private boolean readable = false;

        ListOrderedMapIterator(ListOrderedMap<K, V> parent) {
            super();
            this.parent = parent;
            this.iterator = parent.insertOrder.listIterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public K next() {
            last = iterator.next();
            readable = true;
            return last;
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public K previous() {
            last = iterator.previous();
            readable = true;
            return last;
        }

        public void remove() {
            if (readable == false) {
                throw new IllegalStateException(AbstractHashedMap.REMOVE_INVALID);
            }
            iterator.remove();
            parent.map.remove(last);
            readable = false;
        }

        public K getKey() {
            if (readable == false) {
                throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);
            }
            return last;
        }

        public V getValue() {
            if (readable == false) {
                throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);
            }
            return parent.get(last);
        }

        public V setValue(V value) {
            if (readable == false) {
                throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);
            }
            return parent.map.put(last, value);
        }

        public void reset() {
            iterator = parent.insertOrder.listIterator();
            last = null;
            readable = false;
        }

        public String toString() {
            if (readable == true) {
                return "Iterator[" + getKey() + "=" + getValue() + "]";
            } else {
                return "Iterator[]";
            }
        }
    }

}

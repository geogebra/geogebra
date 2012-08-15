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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.OrderedBidiMap;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.OrderedMapIterator;
import org.apache.commons.collections15.ResettableIterator;
import org.apache.commons.collections15.SortedBidiMap;
import org.apache.commons.collections15.map.AbstractSortedMapDecorator;

/**
 * Implementation of <code>BidiMap</code> that uses two <code>TreeMap</code> instances.
 * <p/>
 * The setValue() method on iterators will succeed only if the new value being set is
 * not already in the bidimap.
 * <p/>
 * When considering whether to use this class, the {@link TreeBidiMap} class should
 * also be considered. It implements the interface using a dedicated design, and does
 * not store each object twice, which can save on memory use.
 * <p/>
 * NOTE: From Commons Collections 3.1, all subclasses will use <code>TreeMap</code>
 * and the flawed <code>createMap</code> method is ignored.
 *
 * @author Matthew Hawthorne
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Id: DualTreeBidiMap.java,v 1.1 2005/10/11 17:05:19 pents90 Exp $
 * @since Commons Collections 3.0
 */
public class DualTreeBidiMap <K,V> extends AbstractDualBidiMap<K, V> implements SortedBidiMap<K, V>, Serializable {

    /**
     * Ensure serialization compatibility
     */
    private static final long serialVersionUID = 721969328361809L;
    /**
     * The comparator to use
     */
    protected final Comparator<? super K> comparator;

    /**
     * Creates an empty <code>DualTreeBidiMap</code>
     */
    public DualTreeBidiMap() {
        super(new TreeMap<K, V>(), new TreeMap<V, K>());
        this.comparator = null;
    }

    public static <E> DualTreeBidiMap<E, E> createTwoWayBidiMap(Comparator<? super E> comparator) {
        return new DualTreeBidiMap<E, E>(comparator, comparator);
    }

    /**
     * Constructs a <code>DualTreeBidiMap</code> and copies the mappings from
     * specified <code>Map</code>.
     *
     * @param map the map whose mappings are to be placed in this map
     */
    public DualTreeBidiMap(Map<? extends K, ? extends V> map) {
        super(new TreeMap<K, V>(), new TreeMap<V, K>());
        putAll(map);
        this.comparator = null;
    }

    /**
     * Constructs a <code>DualTreeBidiMap</code> using the specified Comparators.
     *
     * @param comparator the Comparator
     */
    public DualTreeBidiMap(Comparator<? super K> comparator, Comparator<? super V> inverseComparator) {
        super(new TreeMap<K, V>(comparator), new TreeMap<V, K>(inverseComparator));
        this.comparator = comparator;
    }

    /**
     * Constructs a <code>DualTreeBidiMap</code> that decorates the specified maps.
     *
     * @param normalMap      the normal direction map
     * @param reverseMap     the reverse direction map
     * @param inverseBidiMap the inverse BidiMap
     */
    protected DualTreeBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseBidiMap) {
        super(normalMap, reverseMap, inverseBidiMap);
        this.comparator = ((SortedMap<K, V>) normalMap).comparator();
    }

    /**
     * Creates a new instance of this object.
     *
     * @param normalMap  the normal direction map
     * @param reverseMap the reverse direction map
     * @param inverseMap the inverse BidiMap
     * @return new bidi map
     */
    protected <K,V> BidiMap<K, V> createBidiMap(Map<K, V> normalMap, Map<V, K> reverseMap, BidiMap<V, K> inverseMap) {
        return new DualTreeBidiMap<K, V>(normalMap, reverseMap, inverseMap);
    }

    //-----------------------------------------------------------------------
    public Comparator<? super K> comparator() {
        return ((SortedMap<K, V>) forwardMap).comparator();
    }

    public K firstKey() {
        return ((SortedMap<K, V>) forwardMap).firstKey();
    }

    public K lastKey() {
        return ((SortedMap<K, V>) forwardMap).lastKey();
    }

    public K nextKey(K key) {
        if (isEmpty()) {
            return null;
        }
        if (forwardMap instanceof OrderedMap) {
            return ((OrderedMap<K, V>) forwardMap).nextKey(key);
        }
        SortedMap sm = (SortedMap) forwardMap;
        Iterator<K> it = sm.tailMap(key).keySet().iterator();
        it.next();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public K previousKey(K key) {
        if (isEmpty()) {
            return null;
        }
        if (forwardMap instanceof OrderedMap) {
            return ((OrderedMap<K, V>) forwardMap).previousKey(key);
        }
        SortedMap<K, V> sm = (SortedMap<K, V>) forwardMap;
        SortedMap<K, V> hm = sm.headMap(key);
        if (hm.isEmpty()) {
            return null;
        }
        return hm.lastKey();
    }

    //-----------------------------------------------------------------------
    /**
     * Obtains an ordered map iterator.
     * <p/>
     * This implementation copies the elements to an ArrayList in order to
     * provide the forward/backward behaviour.
     *
     * @return a new ordered map iterator
     */
    public OrderedMapIterator<K, V> orderedMapIterator() {
        return new BidiOrderedMapIterator<K, V>(this);
    }

    public SortedBidiMap<V, K> inverseSortedBidiMap() {
        return (SortedBidiMap<V, K>) inverseBidiMap();
    }

    public OrderedBidiMap<V, K> inverseOrderedBidiMap() {
        return (OrderedBidiMap<V, K>) inverseBidiMap();
    }

    //-----------------------------------------------------------------------
    public SortedMap<K, V> headMap(K toKey) {
        SortedMap<K, V> sub = ((SortedMap<K, V>) forwardMap).headMap(toKey);
        return new ViewMap(this, sub);
    }

    public SortedMap<K, V> tailMap(K fromKey) {
        SortedMap<K, V> sub = ((SortedMap<K, V>) forwardMap).tailMap(fromKey);
        return new ViewMap<K, V>(this, sub);
    }

    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        SortedMap<K, V> sub = ((SortedMap<K, V>) forwardMap).subMap(fromKey, toKey);
        return new ViewMap<K, V>(this, sub);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Internal sorted map view.
     */
    protected static class ViewMap <K,V> extends AbstractSortedMapDecorator<K, V> {
        /**
         * The parent bidi map.
         */
        final DualTreeBidiMap<K, V> bidi;

        /**
         * Constructor.
         *
         * @param bidi the parent bidi map
         * @param sm   the subMap sorted map
         */
        protected ViewMap(DualTreeBidiMap<K, V> bidi, SortedMap<K, V> sm) {
            // the implementation is not great here...
            // use the forwardMap as the filtered map, but inverseMap as the full map
            // this forces containsValue and clear to be overridden
            super((SortedMap) bidi.createBidiMap(sm, bidi.inverseMap, bidi.inverseBidiMap));
            this.bidi = (DualTreeBidiMap) map;
        }

        public boolean containsValue(Object value) {
            // override as default implementation jumps to [1]
            return bidi.forwardMap.containsValue(value);
        }

        public void clear() {
            // override as default implementation jumps to [1]
            for (Iterator it = keySet().iterator(); it.hasNext();) {
                it.next();
                it.remove();
            }
        }

        public SortedMap<K, V> headMap(K toKey) {
            return new ViewMap<K, V>(bidi, super.headMap(toKey));
        }

        public SortedMap<K, V> tailMap(K fromKey) {
            return new ViewMap<K, V>(bidi, super.tailMap(fromKey));
        }

        public SortedMap<K, V> subMap(K fromKey, K toKey) {
            return new ViewMap<K, V>(bidi, super.subMap(fromKey, toKey));
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Inner class MapIterator.
     */
    protected static class BidiOrderedMapIterator <K,V> implements OrderedMapIterator<K, V>, ResettableIterator<K> {

        /**
         * The parent map
         */
        protected final AbstractDualBidiMap<K, V> parent;
        /**
         * The iterator being decorated
         */
        protected ListIterator<Map.Entry<K, V>> iterator;
        /**
         * The last returned entry
         */
        private Map.Entry<K, V> last = null;

        /**
         * Constructor.
         *
         * @param parent the parent map
         */
        protected BidiOrderedMapIterator(AbstractDualBidiMap<K, V> parent) {
            super();
            this.parent = parent;
            iterator = new ArrayList<Map.Entry<K, V>>(parent.entrySet()).listIterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public K next() {
            last = iterator.next();
            return last.getKey();
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public K previous() {
            last = iterator.previous();
            return last.getKey();
        }

        public void remove() {
            iterator.remove();
            parent.remove(last.getKey());
            last = null;
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
            iterator = new ArrayList<Map.Entry<K, V>>(parent.entrySet()).listIterator();
            last = null;
        }

        public String toString() {
            if (last != null) {
                return "MapIterator[" + getKey() + "=" + getValue() + "]";
            } else {
                return "MapIterator[]";
            }
        }
    }

    // Serialization
    //-----------------------------------------------------------------------
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(forwardMap);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        forwardMap = new TreeMap<K, V>(comparator);
        inverseMap = new TreeMap<V, K>();
        Map<K, V> map = (Map<K, V>) in.readObject();
        putAll(map);
    }

}

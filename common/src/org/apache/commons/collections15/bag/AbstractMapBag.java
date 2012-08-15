// GenericsNote: Converted.
/*
 *  Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.bag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.set.UnmodifiableSet;

/**
 * Abstract implementation of the {@link Bag} interface to simplify the creation
 * of subclass implementations.
 * <p/>
 * Subclasses specify a Map implementation to use as the internal storage.
 * The map will be used to map bag elements to a number; the number represents
 * the number of occurrences of that element in the bag.
 *
 * @author Chuck Burdick
 * @author Michael A. Smith
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Janek Bogucki
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0 (previously DefaultMapBag v2.0)
 */
public abstract class AbstractMapBag <E> implements Bag<E> {

    /**
     * The map to use to store the data
     */
    private transient Map<E, MutableInteger> map;
    /**
     * The current total size of the bag
     */
    private int size;
    /**
     * The modification count for fail fast iterators
     */
    private transient int modCount;
    /**
     * The modification count for fail fast iterators
     */
    private transient Set<E> uniqueSet;

    /**
     * Constructor needed for subclass serialisation.
     */
    protected AbstractMapBag() {
        super();
    }

    /**
     * Constructor that assigns the specified Map as the backing store.
     * The map must be empty and non-null.
     *
     * @param map the map to assign
     */
    protected AbstractMapBag(Map<E, MutableInteger> map) {
        super();
        this.map = map;
    }

    /**
     * Utility method for implementations to access the map that backs
     * this bag. Not intended for interactive use outside of subclasses.
     *
     * @return the map being used by the Bag
     */
    protected Map<E, MutableInteger> getMap() {
        return map;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the number of elements in this bag.
     *
     * @return current size of the bag
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the underlying map is empty.
     *
     * @return true if bag is empty
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns the number of occurrence of the given element in this bag
     * by looking up its count in the underlying map.
     *
     * @param object the object to search for
     * @return the number of occurrences of the object, zero if not found
     */
    public int getCount(E object) {
        MutableInteger count = map.get(object);
        if (count != null) {
            return count.value;
        }
        return 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Determines if the bag contains the given element by checking if the
     * underlying map contains the element as a key.
     *
     * @param object the object to search for
     * @return true if the bag contains the given element
     */
    public boolean contains(Object object) {
        return map.containsKey(object);
    }

    /**
     * Determines if the bag contains the given elements.
     *
     * @param coll the collection to check against
     * @return <code>true</code> if the Bag contains all the collection
     */
    public boolean containsAll(Collection<?> coll) {
        if (coll instanceof Bag) {
            return containsAll((Bag<E>) coll);
        }
        return containsAll(new HashBag(coll));
    }

    /**
     * Returns <code>true</code> if the bag contains all elements in
     * the given collection, respecting cardinality.
     *
     * @param other the bag to check against
     * @return <code>true</code> if the Bag contains all the collection
     */
    boolean containsAll(Bag<E> other) {
        boolean result = true;
        Iterator<E> it = other.uniqueSet().iterator();
        while (it.hasNext()) {
            E current = it.next();
            boolean contains = getCount(current) >= other.getCount(current);
            result = result && contains;
        }
        return result;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over the bag elements.
     * Elements present in the Bag more than once will be returned repeatedly.
     *
     * @return the iterator
     */
    public Iterator<E> iterator() {
        return new BagIterator<E>(this);
    }

    /**
     * Inner class iterator for the Bag.
     */
    static class BagIterator <E> implements Iterator<E> {
        private AbstractMapBag<E> parent;
        private Iterator<Map.Entry<E, MutableInteger>> entryIterator;
        private Map.Entry<E, MutableInteger> current;
        private int itemCount;
        private final int mods;
        private boolean canRemove;

        /**
         * Constructor.
         *
         * @param parent the parent bag
         */
        public BagIterator(AbstractMapBag<E> parent) {
            this.parent = parent;
            this.entryIterator = parent.map.entrySet().iterator();
            this.current = null;
            this.mods = parent.modCount;
            this.canRemove = false;
        }

        public boolean hasNext() {
            return (itemCount > 0 || entryIterator.hasNext());
        }

        public E next() {
            if (parent.modCount != mods) {
                throw new ConcurrentModificationException();
            }
            if (itemCount == 0) {
                current = entryIterator.next();
                itemCount = current.getValue().value;
            }
            canRemove = true;
            itemCount--;
            return current.getKey();
        }

        public void remove() {
            if (parent.modCount != mods) {
                throw new ConcurrentModificationException();
            }
            if (canRemove == false) {
                throw new IllegalStateException();
            }
            MutableInteger mut = current.getValue();
            if (mut.value > 0) {
                mut.value--;
                parent.size--;
            } else {
                entryIterator.remove();
            }
            canRemove = false;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new element to the bag, incrementing its count in the underlying map.
     *
     * @param object the object to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    public boolean add(E object) {
        return add(object, 1);
    }

    /**
     * Adds a new element to the bag, incrementing its count in the map.
     *
     * @param object  the object to search for
     * @param nCopies the number of copies to add
     * @return <code>true</code> if the object was not already in the <code>uniqueSet</code>
     */
    public boolean add(E object, int nCopies) {
        modCount++;
        if (nCopies > 0) {
            MutableInteger mut = map.get(object);
            size += nCopies;
            if (mut == null) {
                map.put(object, new MutableInteger(nCopies));
                return true;
            } else {
                mut.value += nCopies;
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Invokes {@link #add(Object)} for each element in the given collection.
     *
     * @param coll the collection to add
     * @return <code>true</code> if this call changed the bag
     */
    public boolean addAll(Collection<? extends E> coll) {
        boolean changed = false;
        Iterator<? extends E> i = coll.iterator();
        while (i.hasNext()) {
            boolean added = add(i.next());
            changed = changed || added;
        }
        return changed;
    }

    //-----------------------------------------------------------------------
    /**
     * Clears the bag by clearing the underlying map.
     */
    public void clear() {
        modCount++;
        map.clear();
        size = 0;
    }

    /**
     * Removes all copies of the specified object from the bag.
     *
     * @param object the object to remove
     * @return true if the bag changed
     */
    public boolean remove(Object object) {
        MutableInteger mut = map.get(object);
        if (mut == null) {
            return false;
        }
        modCount++;
        map.remove(object);
        size -= mut.value;
        return true;
    }

    /**
     * Removes a specified number of copies of an object from the bag.
     *
     * @param object  the object to remove
     * @param nCopies the number of copies to remove
     * @return true if the bag changed
     */
    public boolean remove(E object, int nCopies) {
        MutableInteger mut = map.get(object);
        if (mut == null) {
            return false;
        }
        if (nCopies <= 0) {
            return false;
        }
        modCount++;
        if (nCopies < mut.value) {
            mut.value -= nCopies;
            map.put(object, mut);
            size -= nCopies;
        } else {
            map.remove(object);
            size -= mut.value;
        }
        return true;
    }

    /**
     * Removes objects from the bag according to their count in the specified collection.
     *
     * @param coll the collection to use
     * @return true if the bag changed
     */
    public boolean removeAll(Collection<?> coll) {
        boolean result = false;
        if (coll != null) {
            Iterator i = coll.iterator();
            while (i.hasNext()) {
                boolean changed = remove((E) i.next(), 1);
                result = result || changed;
            }
        }
        return result;
    }

    /**
     * Remove any members of the bag that are not in the given
     * bag, respecting cardinality.
     *
     * @param coll the collection to retain
     * @return true if this call changed the collection
     */
    public boolean retainAll(Collection<?> coll) {
        if (coll instanceof Bag) {
            return retainAll((Bag<? extends E>) coll);
        }
        return retainAll(new HashBag(coll));
    }

    /**
     * Remove any members of the bag that are not in the given
     * bag, respecting cardinality.
     *
     * @param other the bag to retain
     * @return <code>true</code> if this call changed the collection
     * @see #retainAll(Collection)
     */
    boolean retainAll(Bag<E> other) {
        boolean result = false;
        Bag excess = new HashBag();
        Iterator<E> i = uniqueSet().iterator();
        while (i.hasNext()) {
            E current = i.next();
            int myCount = getCount(current);
            int otherCount = other.getCount(current);
            if (1 <= otherCount && otherCount <= myCount) {
                excess.add(current, myCount - otherCount);
            } else {
                excess.add(current, myCount);
            }
        }
        if (!excess.isEmpty()) {
            result = removeAll(excess);
        }
        return result;
    }

    //-----------------------------------------------------------------------

    /**
     * Mutable integer class for storing the data.
     */
    protected static class MutableInteger {
        /**
         * The value of this mutable.
         */
        protected int value;

        /**
         * Constructor.
         *
         * @param value the initial value
         */
        MutableInteger(int value) {
            this.value = value;
        }

        public boolean equals(Object obj) {
            if (obj instanceof MutableInteger == false) {
                return false;
            }
            return ((MutableInteger) obj).value == value;
        }

        public int hashCode() {
            return value;
        }
    }

    /**
     * Returns an array of all of this bag's elements.
     *
     * @return an array of all of this bag's elements
     */
    public Object[] toArray() {
        Object[] result = new Object[size()];
        int i = 0;
        Iterator<E> it = map.keySet().iterator();
        while (it.hasNext()) {
            E current = it.next();
            for (int index = getCount(current); index > 0; index--) {
                result[i++] = current;
            }
        }
        return result;
    }

    /**
     * Returns an array of all of this bag's elements.
     *
     * @param array the array to populate
     * @return an array of all of this bag's elements
     */
    public Object[] toArray(Object[] array) {
        int size = size();
        if (array.length < size) {
            array = (Object[]) Array.newInstance(array.getClass().getComponentType(), size);
        }

        int i = 0;
        Iterator<E> it = map.keySet().iterator();
        while (it.hasNext()) {
            E current = it.next();
            for (int index = getCount(current); index > 0; index--) {
                array[i++] = current;
            }
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    /**
     * Returns an unmodifiable view of the underlying map's key set.
     *
     * @return the set of unique elements in this bag
     */
    public Set<E> uniqueSet() {
        if (uniqueSet == null) {
            uniqueSet = UnmodifiableSet.decorate(map.keySet());
        }
        return uniqueSet;
    }

    //-----------------------------------------------------------------------
    /**
     * Write the map out using a custom routine.
     *
     * @param out the output stream
     * @throws IOException
     */
    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(map.size());
        for (Iterator<Map.Entry<E, MutableInteger>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<E, MutableInteger> entry = it.next();
            out.writeObject(entry.getKey());
            out.writeInt(entry.getValue().value);
        }
    }

    /**
     * Read the map in using a custom routine.
     *
     * @param map the map to use
     * @param in  the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected void doReadObject(Map<E, MutableInteger> map, ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.map = map;
        int entrySize = in.readInt();
        for (int i = 0; i < entrySize; i++) {
            E obj = (E) in.readObject();
            int count = in.readInt();
            map.put(obj, new MutableInteger(count));
            size += count;
        }
    }
    
    //-----------------------------------------------------------------------
    /**
     * Compares this Bag to another.
     * This Bag equals another Bag if it contains the same number of occurrences of
     * the same elements.
     *
     * @param object the Bag to compare to
     * @return true if equal
     */
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof Bag == false) {
            return false;
        }
        Bag other = (Bag) object;
        if (other.size() != size()) {
            return false;
        }
        for (Iterator<E> it = map.keySet().iterator(); it.hasNext();) {
            E element = it.next();
            if (other.getCount(element) != getCount(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a hash code for the Bag compatible with the definition of equals.
     * The hash code is defined as the sum total of a hash code for each element.
     * The per element hash code is defined as
     * <code>(e==null ? 0 : e.hashCode()) ^ noOccurances)</code>.
     * This hash code is compatible with the Set interface.
     *
     * @return the hash code of the Bag
     */
    public int hashCode() {
        int total = 0;
        for (Iterator<Map.Entry<E, MutableInteger>> it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry<E, MutableInteger> entry = it.next();
            Object element = entry.getKey();
            Integer count = entry.getValue().value;
            total += (element == null ? 0 : element.hashCode()) ^ count;
        }
        return total;
    }

    /**
     * Implement a toString() method suitable for debugging.
     *
     * @return a debugging toString
     */
    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        Iterator<E> it = uniqueSet().iterator();
        while (it.hasNext()) {
            E current = it.next();
            int count = getCount(current);
            buf.append(count);
            buf.append(':');
            buf.append(current);
            if (it.hasNext()) {
                buf.append(',');
            }
        }
        buf.append(']');
        return buf.toString();
    }

}

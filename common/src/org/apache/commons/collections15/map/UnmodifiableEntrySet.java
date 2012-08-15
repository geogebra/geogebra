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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Unmodifiable;
import org.apache.commons.collections15.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections15.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections15.set.AbstractSetDecorator;

/**
 * Decorates a map entry <code>Set</code> to ensure it can't be altered.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableEntrySet <K,V> extends AbstractSetDecorator<Map.Entry<K, V>> implements Unmodifiable {

    /**
     * Factory method to create an unmodifiable set of Map Entry objects.
     *
     * @param set the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <K,V> Set<Map.Entry<K, V>> decorate(Set<Map.Entry<K, V>> set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableEntrySet<K, V>(set);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param set the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    private UnmodifiableEntrySet(Set<Map.Entry<K, V>> set) {
        super(set);
    }

    //-----------------------------------------------------------------------
    public boolean add(Map.Entry<K, V> object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends Map.Entry<K, V>> coll) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException();
    }

    //-----------------------------------------------------------------------
    public Iterator<Map.Entry<K, V>> iterator() {
        return new UnmodifiableEntrySetIterator<K, V>(collection.iterator());
    }

    public Object[] toArray() {
        Object[] array = collection.toArray();
        for (int i = 0; i < array.length; i++) {
            array[i] = new UnmodifiableEntry((Map.Entry) array[i]);
        }
        return array;
    }

    public <T> T[] toArray(T array[]) {
        T[] result = array;
        if (array.length > 0) {
            // we must create a new array to handle multi-threaded situations
            // where another thread could access data before we decorate it
            result = (T[]) Array.newInstance(array.getClass().getComponentType(), 0);
        }
        result = collection.toArray(result);
        Collection<UnmodifiableEntry<K, V>> newCollection = new ArrayList<UnmodifiableEntry<K, V>>();
        for (int i = 0; i < result.length; i++) {
            //            result[i] = new UnmodifiableEntry<K,V>((Map.Entry) result[i]);
            newCollection.add(new UnmodifiableEntry<K, V>((Map.Entry) result[i]));
        }
        result = newCollection.toArray(result);

        // check to see if result should be returned straight
        if (result.length > array.length) {
            return result;
        }

        // copy back into input array to fulfil the method contract
        System.arraycopy(result, 0, array, 0, result.length);
        if (array.length > result.length) {
            array[result.length] = null;
        }
        return array;
    }
    
    //-----------------------------------------------------------------------
    /**
     * Implementation of an entry set iterator.
     */
    final static class UnmodifiableEntrySetIterator <K,V> extends AbstractIteratorDecorator<Map.Entry<K, V>> {

        protected UnmodifiableEntrySetIterator(Iterator<Map.Entry<K, V>> iterator) {
            super(iterator);
        }

        public Map.Entry<K, V> next() {
            Map.Entry entry = (Map.Entry) iterator.next();
            return new UnmodifiableEntry<K, V>(entry);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of a map entry that is unmodifiable.
     */
    final static class UnmodifiableEntry <K,V> extends AbstractMapEntryDecorator<K, V> {

        protected UnmodifiableEntry(Map.Entry<K, V> entry) {
            super(entry);
        }

        public V setValue(V obj) {
            throw new UnsupportedOperationException();
        }
    }

}

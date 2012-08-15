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
package org.apache.commons.collections15.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections15.BoundedCollection;
import org.apache.commons.collections15.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections15.iterators.UnmodifiableIterator;

/**
 * Decorates another <code>List</code> to fix the size preventing add/remove.
 * <p/>
 * The add, remove, clear and retain operations are unsupported.
 * The set method is allowed (as it doesn't change the list size).
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class FixedSizeList <E> extends AbstractSerializableListDecorator<E> implements BoundedCollection<E> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = -2218010673611160319L;

    /**
     * Factory method to create a fixed size list.
     *
     * @param list the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    public static <E> List<E> decorate(List<E> list) {
        return new FixedSizeList<E>(list);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param list the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected FixedSizeList(List<E> list) {
        super(list);
    }

    //-----------------------------------------------------------------------
    public boolean add(E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void add(int index, E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean addAll(Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean addAll(int index, Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public E get(int index) {
        return getList().get(index);
    }

    public int indexOf(Object object) {
        return getList().indexOf(object);
    }

    public Iterator<E> iterator() {
        return UnmodifiableIterator.decorate(getCollection().iterator());
    }

    public int lastIndexOf(Object object) {
        return getList().lastIndexOf(object);
    }

    public ListIterator<E> listIterator() {
        return new FixedSizeListIterator<E>(getList().listIterator(0));
    }

    public ListIterator<E> listIterator(int index) {
        return new FixedSizeListIterator<E>(getList().listIterator(index));
    }

    public E remove(int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean remove(Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean removeAll(Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public boolean retainAll(Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public E set(int index, E object) {
        return getList().set(index, object);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = getList().subList(fromIndex, toIndex);
        return new FixedSizeList<E>(sub);
    }

    /**
     * List iterator that only permits changes via set()
     */
    static class FixedSizeListIterator <E> extends AbstractListIteratorDecorator<E> {
        protected FixedSizeListIterator(ListIterator<E> iterator) {
            super(iterator);
        }

        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }

        public void add(E object) {
            throw new UnsupportedOperationException("List is fixed size");
        }
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

}

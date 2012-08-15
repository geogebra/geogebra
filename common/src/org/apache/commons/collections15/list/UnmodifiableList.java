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

import org.apache.commons.collections15.Unmodifiable;
import org.apache.commons.collections15.iterators.UnmodifiableIterator;
import org.apache.commons.collections15.iterators.UnmodifiableListIterator;

/**
 * Decorates another <code>List</code> to ensure it can't be altered.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableList <E> extends AbstractSerializableListDecorator<E> implements Unmodifiable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 6595182819922443652L;

    /**
     * Factory method to create an unmodifiable list.
     *
     * @param list the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    public static <E> List<E> decorate(List<E> list) {
        if (list instanceof Unmodifiable) {
            return list;
        }
        return new UnmodifiableList<E>(list);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param list the list to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    private UnmodifiableList(List<E> list) {
        super(list);
    }

    //-----------------------------------------------------------------------
    public Iterator<E> iterator() {
        return UnmodifiableIterator.decorate(getCollection().iterator());
    }

    public boolean add(E object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends E> coll) {
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
    public ListIterator<E> listIterator() {
        return UnmodifiableListIterator.decorate(getList().listIterator());
    }

    public ListIterator<E> listIterator(int index) {
        return UnmodifiableListIterator.decorate(getList().listIterator(index));
    }

    public void add(int index, E object) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }

    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    public E set(int index, E object) {
        throw new UnsupportedOperationException();
    }

    public List<E> subList(int fromIndex, int toIndex) {
        List<E> sub = getList().subList(fromIndex, toIndex);
        return new UnmodifiableList<E>(sub);
    }

}

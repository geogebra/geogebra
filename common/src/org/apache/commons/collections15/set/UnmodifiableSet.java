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
package org.apache.commons.collections15.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections15.Unmodifiable;
import org.apache.commons.collections15.iterators.UnmodifiableIterator;

/**
 * Decorates another <code>Set</code> to ensure it can't be altered.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableSet <E> extends AbstractSerializableSetDecorator<E> implements Unmodifiable {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 6499119872185240161L;

    /**
     * Factory method to create an unmodifiable set.
     *
     * @param set the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    public static <E> Set<E> decorate(Set<E> set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableSet<E>(set);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param set the set to decorate, must not be null
     * @throws IllegalArgumentException if set is null
     */
    private UnmodifiableSet(Set<E> set) {
        super(set);
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

}

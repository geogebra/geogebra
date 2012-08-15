// GenericsNote: Converted.
/*
 *  Copyright 1999-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.iterators;

import java.util.Iterator;

import org.apache.commons.collections15.Unmodifiable;

/**
 * Decorates an iterator such that it cannot be modified.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class UnmodifiableIterator <E> implements Iterator<E>, Unmodifiable {

    /**
     * The iterator being decorated
     */
    private Iterator<E> iterator;

    //-----------------------------------------------------------------------
    /**
     * Decorates the specified iterator such that it cannot be modified.
     * <p/>
     * If the iterator is already unmodifiable it is returned directly.
     *
     * @param iterator the iterator to decorate
     * @throws IllegalArgumentException if the iterator is null
     */
    public static <E> Iterator<E> decorate(Iterator<E> iterator) {
        if (iterator == null) {
            throw new IllegalArgumentException("Iterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            return iterator;
        }
        return new UnmodifiableIterator<E>(iterator);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param iterator the iterator to decorate
     */
    private UnmodifiableIterator(Iterator<E> iterator) {
        super();
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public E next() {
        return iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }

}

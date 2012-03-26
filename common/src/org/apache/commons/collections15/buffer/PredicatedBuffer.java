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
package org.apache.commons.collections15.buffer;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.collection.PredicatedCollection;

/**
 * Decorates another <code>Buffer</code> to validate that additions
 * match a specified predicate.
 * <p/>
 * This buffer exists to provide validation for the decorated buffer.
 * It is normally created to decorate an empty buffer.
 * If an object cannot be added to the buffer, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the buffer.
 * <pre>Buffer buffer = PredicatedBuffer.decorate(new UnboundedFifoBuffer(), NotNullPredicate.INSTANCE);</pre>
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 */
public class PredicatedBuffer <E> extends PredicatedCollection<E> implements Buffer<E> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 2307609000539943581L;

    /**
     * Factory method to create a predicated (validating) buffer.
     * <p/>
     * If there are any elements already in the buffer being decorated, they
     * are validated.
     *
     * @param buffer    the buffer to decorate, must not be null
     * @param predicate the predicate to use for validation, must not be null
     * @return a new predicated Buffer
     * @throws IllegalArgumentException if buffer or predicate is null
     * @throws IllegalArgumentException if the buffer contains invalid elements
     */
    public static <E> Buffer<E> decorate(Buffer<E> buffer, Predicate<? super E> predicate) {
        return new PredicatedBuffer(buffer, predicate);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p/>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     *
     * @param buffer    the buffer to decorate, must not be null
     * @param predicate the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if buffer or predicate is null
     * @throws IllegalArgumentException if the buffer contains invalid elements
     */
    protected PredicatedBuffer(Buffer<E> buffer, Predicate<? super E> predicate) {
        super(buffer, predicate);
    }

    /**
     * Gets the buffer being decorated.
     *
     * @return the decorated buffer
     */
    protected Buffer<E> getBuffer() {
        return (Buffer<E>) getCollection();
    }

    //-----------------------------------------------------------------------
    public E get() {
        return getBuffer().get();
    }

    public E remove() {
        return getBuffer().remove();
    }

}

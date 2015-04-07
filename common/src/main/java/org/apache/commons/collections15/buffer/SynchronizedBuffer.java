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
import org.apache.commons.collections15.collection.SynchronizedCollection;

/**
 * Decorates another <code>Buffer</code> to synchronize its behaviour
 * for a multi-threaded environment.
 * <p/>
 * Methods are synchronized, then forwarded to the decorated buffer.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 */
public class SynchronizedBuffer <E> extends SynchronizedCollection<E> implements Buffer<E> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = -6859936183953626253L;

    /**
     * Factory method to create a synchronized buffer.
     *
     * @param buffer the buffer to decorate, must not be null
     * @return a new synchronized Buffer
     * @throws IllegalArgumentException if buffer is null
     */
    public static <E> Buffer<E> decorate(Buffer<E> buffer) {
        return new SynchronizedBuffer(buffer);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     *
     * @param buffer the buffer to decorate, must not be null
     * @throws IllegalArgumentException if the buffer is null
     */
    protected SynchronizedBuffer(Buffer<E> buffer) {
        super(buffer);
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param buffer the buffer to decorate, must not be null
     * @param lock   the lock object to use, must not be null
     * @throws IllegalArgumentException if the buffer is null
     */
    protected SynchronizedBuffer(Buffer<E> buffer, Object lock) {
        super(buffer, lock);
    }

    /**
     * Gets the buffer being decorated.
     *
     * @return the decorated buffer
     */
    protected Buffer<E> getBuffer() {
        return (Buffer<E>) collection;
    }

    //-----------------------------------------------------------------------
    public E get() {
        synchronized (lock) {
            return getBuffer().get();
        }
    }

    public E remove() {
        synchronized (lock) {
            return getBuffer().remove();
        }
    }

}

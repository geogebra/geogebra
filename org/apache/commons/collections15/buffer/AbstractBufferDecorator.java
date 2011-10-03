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
import org.apache.commons.collections15.collection.AbstractCollectionDecorator;

/**
 * Decorates another <code>Buffer</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated buffer.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 */
public abstract class AbstractBufferDecorator <E> extends AbstractCollectionDecorator<E> implements Buffer<E> {

    /**
     * Constructor only used in deserialization, do not use otherwise.
     *
     * @since Commons Collections 3.1
     */
    protected AbstractBufferDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param buffer the buffer to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected AbstractBufferDecorator(Buffer<E> buffer) {
        super(buffer);
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

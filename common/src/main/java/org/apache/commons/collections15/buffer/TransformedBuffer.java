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
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.collection.TransformedCollection;

/**
 * Decorates another <code>Buffer</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Note: This class cannot support generics without breaking the Collection contract.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 */
public class TransformedBuffer <I,O> extends TransformedCollection<I, O> implements Buffer {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = -7901091318986132033L;

    /**
     * Factory method to create a transforming buffer.
     * <p/>
     * If there are any elements already in the buffer being decorated, they
     * are NOT transformed.
     *
     * @param buffer      the buffer to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @return a new transformed Buffer
     * @throws IllegalArgumentException if buffer or transformer is null
     */
    public static <I,O> Buffer<O> decorate(Buffer<I> buffer, Transformer<? super I, ? extends O> transformer) {
        return new TransformedBuffer<I, O>(buffer, transformer);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p/>
     * If there are any elements already in the buffer being decorated, they
     * are NOT transformed.
     *
     * @param buffer      the buffer to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if buffer or transformer is null
     */
    protected TransformedBuffer(Buffer<I> buffer, Transformer<? super I, ? extends O> transformer) {
        super(buffer, transformer);
    }

    /**
     * Gets the decorated buffer.
     *
     * @return the decorated buffer
     */
    protected Buffer<O> getBuffer() {
        return (Buffer<O>) collection;
    }

    //-----------------------------------------------------------------------
    public Object get() {
        return getBuffer().get();
    }

    public Object remove() {
        return getBuffer().remove();
    }

}

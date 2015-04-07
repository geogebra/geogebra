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

import org.apache.commons.collections15.Transformer;

/**
 * Decorates an iterator such that each element returned is transformed.
 *
 * @author James Strachan
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 1.0
 */
public class TransformIterator <I,O> implements Iterator<O> {

    /**
     * The iterator being used
     */
    private Iterator<I> iterator;
    /**
     * The transformer being used
     */
    private Transformer<I, O> transformer;

    //-----------------------------------------------------------------------
    /**
     * Constructs a new <code>TransformIterator</code> that will not function
     * until the {@link #setIterator(Iterator) setIterator} method is
     * invoked.
     */
    public TransformIterator() {
        super();
    }

    /**
     * Constructs a new <code>TransformIterator</code> that won't transform
     * elements from the given iterator.
     *
     * @param iterator the iterator to use
     */
    public TransformIterator(Iterator<I> iterator) {
        super();
        this.iterator = iterator;
    }

    /**
     * Constructs a new <code>TransformIterator</code> that will use the
     * given iterator and transformer.  If the given transformer is null,
     * then objects will not be transformed.
     *
     * @param iterator    the iterator to use
     * @param transformer the transformer to use
     */
    public TransformIterator(Iterator<I> iterator, Transformer<I, O> transformer) {
        super();
        this.iterator = iterator;
        this.transformer = transformer;
    }

    //-----------------------------------------------------------------------
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Gets the next object from the iteration, transforming it using the
     * current transformer. If the transformer is null, no transformation
     * occurs and the object from the iterator is returned directly.
     *
     * @return the next object
     * @throws java.util.NoSuchElementException
     *          if there are no more elements
     */
    public O next() {
        return transform(iterator.next());
    }

    public void remove() {
        iterator.remove();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the iterator this iterator is using.
     *
     * @return the iterator.
     */
    public Iterator<I> getIterator() {
        return iterator;
    }

    /**
     * Sets the iterator for this iterator to use.
     * If iteration has started, this effectively resets the iterator.
     *
     * @param iterator the iterator to use
     */
    public void setIterator(Iterator<I> iterator) {
        this.iterator = iterator;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the transformer this iterator is using.
     *
     * @return the transformer.
     */
    public Transformer<I, O> getTransformer() {
        return transformer;
    }

    /**
     * Sets the transformer this the iterator to use.
     * A null transformer is a no-op transformer.
     *
     * @param transformer the transformer to use
     */
    public void setTransformer(Transformer<I, O> transformer) {
        this.transformer = transformer;
    }

    //-----------------------------------------------------------------------
    /**
     * Transforms the given object using the transformer.
     * If the transformer is null, the original object is returned as-is.
     *
     * @param source the object to transform
     * @return the transformed object
     */
    protected O transform(I source) {
        if (transformer != null) {
            return transformer.transform(source);
        }
        // Generics hack
        Object sourceObj = source;
        return (O) sourceObj;
    }
}

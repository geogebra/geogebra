// GenericsNote: Converted, but only partially type-safe.
/*
 *  Copyright 2001-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.functors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections15.Transformer;

/**
 * Transformer implementation that chains the specified transformers together.
 * <p/>
 * The input object is passed to the first transformer. The transformed result
 * is passed to the second transformer and so on.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class ChainedTransformer <I,O> implements Transformer<I, O>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 3514945074733160196L;

    /**
     * The transformers to call in turn
     */
    private final Transformer[] iTransformers;

    /**
     * Factory method that performs validation and copies the parameter array.
     *
     * @param transformers the transformers to chain, copied, no nulls
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if the transformers array is null
     * @throws IllegalArgumentException if any transformer in the array is null
     */
    public static <I,O> Transformer<I, O> getInstance(Transformer[] transformers) {
        FunctorUtils.validate(transformers);
        if (transformers.length == 0) {
            return NOPTransformer.INSTANCE;
        }
        transformers = FunctorUtils.copy(transformers);
        return new ChainedTransformer<I, O>(transformers);
    }

    /**
     * Create a new Transformer that calls each transformer in turn, passing the
     * result into the next transformer. The ordering is that of the iterator()
     * method on the collection.
     *
     * @param transformers a collection of transformers to chain
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if the transformers collection is null
     * @throws IllegalArgumentException if any transformer in the collection is null
     */
    public static <I,O> Transformer<I, O> getInstance(Collection<Transformer> transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("Transformer collection must not be null");
        }
        if (transformers.size() == 0) {
            return NOPTransformer.INSTANCE;
        }
        // convert to array like this to guarantee iterator() ordering
        Transformer[] cmds = new Transformer[transformers.size()];
        int i = 0;
        for (Iterator<Transformer> it = transformers.iterator(); it.hasNext();) {
            cmds[i++] = it.next();
        }
        FunctorUtils.validate(cmds);
        return new ChainedTransformer<I, O>(cmds);
    }

    /**
     * Factory method that performs validation.
     *
     * @param transformer1 the first transformer, not null
     * @param transformer2 the second transformer, not null
     * @return the <code>chained</code> transformer
     * @throws IllegalArgumentException if either transformer is null
     */
    public static <I,M,O> Transformer<I, O> getInstance(Transformer<I, ? extends M> transformer1, Transformer<? super M, O> transformer2) {
        if (transformer1 == null || transformer2 == null) {
            throw new IllegalArgumentException("Transformers must not be null");
        }
        Transformer[] transformers = new Transformer[]{transformer1, transformer2};
        return new ChainedTransformer<I, O>(transformers);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param transformers the transformers to chain, not copied, no nulls
     */
    public ChainedTransformer(Transformer[] transformers) {
        super();
        iTransformers = transformers;
    }

    /**
     * Transforms the input to result via each decorated transformer
     *
     * @param object the input object passed to the first transformer
     * @return the transformed result
     */
    public O transform(I object) {
        Object intermediate = object;
        for (int i = 0; i < iTransformers.length; i++) {
            intermediate = iTransformers[i].transform(intermediate);
        }
        return (O) intermediate;
    }

    /**
     * Gets the transformers, do not modify the array.
     *
     * @return the transformers
     * @since Commons Collections 3.1
     */
    public Transformer[] getTransformers() {
        return iTransformers;
    }

}

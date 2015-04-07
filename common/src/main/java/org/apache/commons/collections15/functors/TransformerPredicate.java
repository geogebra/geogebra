// GenericsNote: Converted.
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

import org.apache.commons.collections15.FunctorException;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

/**
 * Predicate implementation that returns the result of a transformer.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class TransformerPredicate <T> implements Predicate<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = -2407966402920578741L;

    /**
     * The transformer to call
     */
    private final Transformer<T, Boolean> iTransformer;

    /**
     * Factory to create the predicate.
     *
     * @param transformer the transformer to decorate
     * @return the predicate
     * @throws IllegalArgumentException if the transformer is null
     */
    public static <T> Predicate<T> getInstance(Transformer<T, Boolean> transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("The transformer to call must not be null");
        }
        return new TransformerPredicate<T>(transformer);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param transformer the transformer to decorate
     */
    public TransformerPredicate(Transformer<T, Boolean> transformer) {
        super();
        iTransformer = transformer;
    }

    /**
     * Evaluates the predicate returning the result of the decorated transformer.
     *
     * @param object the input object
     * @return true if decorated transformer returns Boolean.TRUE
     * @throws FunctorException if the transformer returns an invalid type
     */
    public boolean evaluate(T object) {
        Boolean result = iTransformer.transform(object);
        if (result == null) {
            throw new FunctorException("Transformer must return an instanceof Boolean, it was a " + ("null object"));
        }
        return result.booleanValue();
    }

    /**
     * Gets the transformer.
     *
     * @return the transformer
     * @since Commons Collections 3.1
     */
    public Transformer<T, Boolean> getTransformer() {
        return iTransformer;
    }

}

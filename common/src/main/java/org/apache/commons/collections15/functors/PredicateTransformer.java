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

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

/**
 * Transformer implementation that calls a Predicate using the input object
 * and then returns the input.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class PredicateTransformer <T> implements Transformer<T, Boolean>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 5278818408044349346L;

    /**
     * The closure to wrap
     */
    private final Predicate<T> iPredicate;

    /**
     * Factory method that performs validation.
     *
     * @param predicate the predicate to call, not null
     * @return the <code>predicate</code> transformer
     * @throws IllegalArgumentException if the predicate is null
     */
    public static <T> Transformer<T, Boolean> getInstance(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        return new PredicateTransformer<T>(predicate);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicate the predicate to call, not null
     */
    public PredicateTransformer(Predicate<T> predicate) {
        super();
        iPredicate = predicate;
    }

    /**
     * Transforms the input to result by calling a predicate.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public Boolean transform(T input) {
        return (iPredicate.evaluate(input) ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * Gets the predicate.
     *
     * @return the predicate
     * @since Commons Collections 3.1
     */
    public Predicate<T> getPredicate() {
        return iPredicate;
    }

}

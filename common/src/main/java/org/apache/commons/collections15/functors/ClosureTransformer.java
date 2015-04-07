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

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;

/**
 * Transformer implementation that calls a Closure using the input object
 * and then returns the input.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class ClosureTransformer <T> implements Transformer<T, T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 478466901448617286L;

    /**
     * The closure to wrap
     */
    private final Closure<T> iClosure;

    /**
     * Factory method that performs validation.
     *
     * @param closure the closure to call, not null
     * @return the <code>closure</code> transformer
     * @throws IllegalArgumentException if the closure is null
     */
    public static <T> Transformer<T, T> getInstance(Closure<T> closure) {
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new ClosureTransformer<T>(closure);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param closure the closure to call, not null
     */
    public ClosureTransformer(Closure<T> closure) {
        super();
        iClosure = closure;
    }

    /**
     * Transforms the input to result by executing a closure.
     *
     * @param input the input object to transform
     * @return the transformed result
     */
    public T transform(T input) {
        iClosure.execute(input);
        return input;
    }

    /**
     * Gets the closure.
     *
     * @return the closure
     * @since Commons Collections 3.1
     */
    public Closure<T> getClosure() {
        return iClosure;
    }

}

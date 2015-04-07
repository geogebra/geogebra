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
import org.apache.commons.collections15.Predicate;

/**
 * Closure implementation acts as an if statement calling one or other closure
 * based on a predicate.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public class IfClosure <T> implements Closure<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = 3518477308466486130L;

    /**
     * The test
     */
    private final Predicate<? super T> iPredicate;
    /**
     * The closure to use if true
     */
    private final Closure<? super T> iTrueClosure;
    /**
     * The closure to use if false
     */
    private final Closure<? super T> iFalseClosure;

    /**
     * Factory method that performs validation.
     *
     * @param predicate    predicate to switch on
     * @param trueClosure  closure used if true
     * @param falseClosure closure used if false
     * @return the <code>if</code> closure
     * @throws IllegalArgumentException if any argument is null
     */
    public static <T> Closure<T> getInstance(Predicate<? super T> predicate, Closure<? super T> trueClosure, Closure<? super T> falseClosure) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (trueClosure == null || falseClosure == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        return new IfClosure<T>(predicate, trueClosure, falseClosure);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicate    predicate to switch on, not null
     * @param trueClosure  closure used if true, not null
     * @param falseClosure closure used if false, not null
     */
    public IfClosure(Predicate<? super T> predicate, Closure<? super T> trueClosure, Closure<? super T> falseClosure) {
        super();
        iPredicate = predicate;
        iTrueClosure = trueClosure;
        iFalseClosure = falseClosure;
    }

    /**
     * Executes the true or false closure accoring to the result of the predicate.
     *
     * @param input the input object
     */
    public void execute(T input) {
        if (iPredicate.evaluate(input) == true) {
            iTrueClosure.execute(input);
        } else {
            iFalseClosure.execute(input);
        }
    }

    /**
     * Gets the predicate.
     *
     * @return the predicate
     * @since Commons Collections 3.1
     */
    public Predicate<? super T> getPredicate() {
        return iPredicate;
    }

    /**
     * Gets the closure called when true.
     *
     * @return the closure
     * @since Commons Collections 3.1
     */
    public Closure<? super T> getTrueClosure() {
        return iTrueClosure;
    }

    /**
     * Gets the closure called when false.
     *
     * @return the closure
     * @since Commons Collections 3.1
     */
    public Closure<? super T> getFalseClosure() {
        return iFalseClosure;
    }

}

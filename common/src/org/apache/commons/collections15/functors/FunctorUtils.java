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

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

/**
 * Internal utilities for functors.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
class FunctorUtils {

    /**
     * Restricted constructor.
     */
    private FunctorUtils() {
        super();
    }

    /**
     * Clone the predicates to ensure that the internal reference can't be messed with.
     *
     * @param predicates the predicates to copy
     * @return the cloned predicates
     */
    static <T> Predicate<? super T>[] copy(Predicate<? super T>[] predicates) {
        if (predicates == null) {
            return null;
        }
        return (Predicate<? super T>[]) predicates.clone();
    }

    /**
     * Validate the predicates to ensure that all is well.
     *
     * @param predicates the predicates to validate
     */
    static <T> void validate(Predicate<? super T>[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        for (int i = 0; i < predicates.length; i++) {
            if (predicates[i] == null) {
                throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }

    /**
     * Validate the predicates to ensure that all is well.
     *
     * @param predicates the predicates to validate
     */
    static <T> void validateMin2(Predicate<? super T>[] predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate array must not be null");
        }
        if (predicates.length < 2) {
            throw new IllegalArgumentException("At least 2 predicates must be specified in the predicate array, size was " + predicates.length);
        }
        for (int i = 0; i < predicates.length; i++) {
            if (predicates[i] == null) {
                throw new IllegalArgumentException("The predicate array must not contain a null predicate, index " + i + " was null");
            }
        }
    }

    /**
     * Validate the predicates to ensure that all is well.
     *
     * @param predicates the predicates to validate
     * @return predicate array
     */
    static <T> Predicate<? super T>[] validate(Collection<Predicate<? super T>> predicates) {
        if (predicates == null) {
            throw new IllegalArgumentException("The predicate collection must not be null");
        }
        if (predicates.size() < 2) {
            throw new IllegalArgumentException("At least 2 predicates must be specified in the predicate collection, size was " + predicates.size());
        }
        // convert to array like this to guarantee iterator() ordering
        Predicate<? super T>[] preds = new Predicate[predicates.size()];
        int i = 0;
        for (Iterator<Predicate<? super T>> it = predicates.iterator(); it.hasNext();) {
            preds[i] = it.next();
            if (preds[i] == null) {
                throw new IllegalArgumentException("The predicate collection must not contain a null predicate, index " + i + " was null");
            }
            i++;
        }
        return preds;
    }

    /**
     * Clone the closures to ensure that the internal reference can't be messed with.
     *
     * @param closures the closures to copy
     * @return the cloned closures
     */
    static <T> Closure<? super T>[] copy(Closure<? super T>[] closures) {
        if (closures == null) {
            return null;
        }
        return (Closure<? super T>[]) closures.clone();
    }

    /**
     * Validate the closures to ensure that all is well.
     *
     * @param closures the closures to validate
     */
    static <T> void validate(Closure<? super T>[] closures) {
        if (closures == null) {
            throw new IllegalArgumentException("The closure array must not be null");
        }
        for (int i = 0; i < closures.length; i++) {
            if (closures[i] == null) {
                throw new IllegalArgumentException("The closure array must not contain a null closure, index " + i + " was null");
            }
        }
    }

    /**
     * Copy method
     *
     * @param transformers the transformers to copy
     * @return a clone of the transformers
     */
    static <I,O> Transformer<? super I, ? extends O>[] copy(Transformer<? super I, ? extends O>[] transformers) {
        if (transformers == null) {
            return null;
        }
        return (Transformer<? super I, ? extends O>[]) transformers.clone();
    }

    /**
     * Validate method
     *
     * @param transformers the transformers to validate
     */
    static <I,O> void validate(Transformer<? super I, ? extends O>[] transformers) {
        if (transformers == null) {
            throw new IllegalArgumentException("The transformer array must not be null");
        }
        for (int i = 0; i < transformers.length; i++) {
            if (transformers[i] == null) {
                throw new IllegalArgumentException("The transformer array must not contain a null transformer, index " + i + " was null");
            }
        }
    }

}

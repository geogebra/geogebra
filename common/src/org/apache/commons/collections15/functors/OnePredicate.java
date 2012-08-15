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
import java.util.Collection;

import org.apache.commons.collections15.Predicate;

/**
 * Predicate implementation that returns true if only one of the predicates return true.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class OnePredicate <T> implements Predicate<T>, PredicateDecorator<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = -8125389089924745785L;

    /**
     * The array of predicates to call
     */
    private final Predicate<? super T>[] iPredicates;

    /**
     * Factory to create the predicate.
     *
     * @param predicates the predicates to check, cloned, not null
     * @return the <code>any</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     * @throws IllegalArgumentException if any predicate in the array is null
     */
    public static <T> Predicate<T> getInstance(Predicate<? super T>[] predicates) {
        FunctorUtils.validateMin2(predicates);
        predicates = FunctorUtils.copy(predicates);
        return new OnePredicate<T>(predicates);
    }

    /**
     * Factory to create the predicate.
     *
     * @param predicates the predicates to check, cloned, not null
     * @return the <code>one</code> predicate
     * @throws IllegalArgumentException if the predicates array is null
     * @throws IllegalArgumentException if any predicate in the array is null
     * @throws IllegalArgumentException if the predicates array has less than 2 elements
     */
    public static <T> Predicate<T> getInstance(Collection<Predicate<? super T>> predicates) {
        Predicate[] preds = FunctorUtils.validate(predicates);
        return new OnePredicate<T>(preds);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param predicates the predicates to check, not cloned, not null
     */
    public OnePredicate(Predicate<? super T>[] predicates) {
        super();
        iPredicates = predicates;
    }

    /**
     * Evaluates the predicate returning true if only one decorated predicate
     * returns true.
     *
     * @param object the input object
     * @return true if only one decorated predicate returns true
     */
    public boolean evaluate(T object) {
        boolean match = false;
        for (int i = 0; i < iPredicates.length; i++) {
            if (iPredicates[i].evaluate(object)) {
                if (match) {
                    return false;
                }
                match = true;
            }
        }
        return match;
    }

    /**
     * Gets the predicates, do not modify the array.
     *
     * @return the predicates
     * @since Commons Collections 3.1
     */
    public Predicate<? super T>[] getPredicates() {
        return iPredicates;
    }

}

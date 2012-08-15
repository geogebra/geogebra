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

/**
 * Predicate implementation that returns true if the input is the same object
 * as the one stored in this predicate.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 3.0
 */
public final class IdentityPredicate <T> implements Predicate<T>, Serializable {

    /**
     * Serial version UID
     */
    static final long serialVersionUID = -89901658494523293L;


    /**
     * The value to compare to
     */
    private final T iValue;

    /**
     * Factory to create the identity predicate.
     *
     * @param object the object to compare to
     * @return the predicate
     * @throws IllegalArgumentException if the predicate is null
     */
    public static <T> Predicate<T> getInstance(T object) {
        if (object == null) {
            return NullPredicate.INSTANCE;
        }
        return new IdentityPredicate<T>(object);
    }

    /**
     * Constructor that performs no validation.
     * Use <code>getInstance</code> if you want that.
     *
     * @param object the object to compare to
     */
    public IdentityPredicate(T object) {
        super();
        iValue = object;
    }

    /**
     * Evaluates the predicate returning true if the input object is identical to
     * the stored object.
     *
     * @param object the input object
     * @return true if input is the same object as the stored value
     */
    public boolean evaluate(T object) {
        return (iValue == object);
    }

    /**
     * Gets the value.
     *
     * @return the value
     * @since Commons Collections 3.1
     */
    public T getValue() {
        return iValue;
    }

}

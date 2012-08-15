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
package org.apache.commons.collections15.set;

import java.util.Set;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.collection.PredicatedCollection;

/**
 * Decorates another <code>Set</code> to validate that all additions
 * match a specified predicate.
 * <p/>
 * This set exists to provide validation for the decorated set.
 * It is normally created to decorate an empty set.
 * If an object cannot be added to the set, an IllegalArgumentException is thrown.
 * <p/>
 * One usage would be to ensure that no null entries are added to the set.
 * <pre>Set set = PredicatedSet.decorate(new HashSet(), NotNullPredicate.INSTANCE);</pre>
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Paul Jack
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.0
 */
public class PredicatedSet <E> extends PredicatedCollection<E> implements Set<E> {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = -684521469108685117L;

    /**
     * Factory method to create a predicated (validating) set.
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are validated.
     *
     * @param set       the set to decorate, must not be null
     * @param predicate the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if set or predicate is null
     * @throws IllegalArgumentException if the set contains invalid elements
     */
    public static <E> Set<E> decorate(Set<E> set, Predicate<? super E> predicate) {
        return new PredicatedSet<E>(set, predicate);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are validated.
     *
     * @param set       the set to decorate, must not be null
     * @param predicate the predicate to use for validation, must not be null
     * @throws IllegalArgumentException if set or predicate is null
     * @throws IllegalArgumentException if the set contains invalid elements
     */
    protected PredicatedSet(Set<E> set, Predicate<? super E> predicate) {
        super(set, predicate);
    }

    /**
     * Gets the set being decorated.
     *
     * @return the decorated set
     */
    protected Set<E> getSet() {
        return (Set<E>) getCollection();
    }

}

// TODO: Not yet converted.
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

import org.apache.commons.collections15.functors.InstanceofPredicate;

/**
 * Decorates another <code>Set</code> to validate that elements
 * added are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @author Matthew Hawthorne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.0
 */
public class TypedSet {

    /**
     * Factory method to create a typed set.
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are validated.
     *
     * @param set  the set to decorate, must not be null
     * @param type the type to allow into the collection, must not be null
     * @throws IllegalArgumentException if set or type is null
     * @throws IllegalArgumentException if the set contains invalid elements
     */
    public static Set decorate(Set set, Class type) {
        return new PredicatedSet(set, InstanceofPredicate.getInstance(type));
    }

    /**
     * Restrictive constructor.
     */
    protected TypedSet() {
    }

}

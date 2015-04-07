// GenericsNote: Not converted, deprecated.
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
package org.apache.commons.collections15.collection;

import java.util.Collection;

import org.apache.commons.collections15.functors.InstanceofPredicate;

/**
 * Decorates a <code>Collection</code> to validate that elements added are of a specific type.
 * <p/>
 * The validation of additions is performed via an instanceof test against
 * a specified <code>Class</code>. If an object cannot be added to the
 * collection, an IllegalArgumentException is thrown.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Matthew Hawthorne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 * @deprecated Type safe classes are no longer required under 1.5.
 */
public class TypedCollection {

    /**
     * Factory method to create a typed collection.
     * <p/>
     * If there are any elements already in the collection being decorated, they
     * are validated.
     *
     * @param coll the collection to decorate, must not be null
     * @param type the type to allow into the collection, must not be null
     * @return a new typed collection
     * @throws IllegalArgumentException if collection or type is null
     * @throws IllegalArgumentException if the collection contains invalid elements
     */
    public static Collection decorate(Collection coll, Class type) {
        return new PredicatedCollection(coll, InstanceofPredicate.getInstance(type));
    }

    /**
     * Restrictive constructor.
     */
    protected TypedCollection() {
        super();
    }

}

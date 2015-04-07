// GenericsNote: Converted.
/*
 *  Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.commons.collections15;

import java.util.Collection;

/**
 * Defines a collection that is bounded in size.
 * <p/>
 * The size of the collection can vary, but it can never exceed a preset
 * maximum number of elements. This interface allows the querying of details
 * associated with the maximum number of elements.
 *
 * @author Matt Hall, John Watkinson, Herve Quiroz
 * @author Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @see CollectionUtils#isFull
 * @see CollectionUtils#maxSize
 * @since Commons Collections 3.0
 */
public interface BoundedCollection <E> extends Collection<E> {

    /**
     * Returns true if this collection is full and no new elements can be added.
     *
     * @return <code>true</code> if the collection is full
     */
    boolean isFull();

    /**
     * Gets the maximum size of the collection (the bound).
     *
     * @return the maximum number of elements the collection can hold
     */
    int maxSize();

}

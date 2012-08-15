// GenericsNote: Converted.
/*
 *  Copyright 1999-2004 The Apache Software Foundation
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
package org.apache.commons.collections15.iterators;

import java.util.Iterator;

import org.apache.commons.collections15.functors.UniquePredicate;

/**
 * A FilterIterator which only returns "unique" Objects.  Internally,
 * the Iterator maintains a Set of objects it has already encountered,
 * and duplicate Objects are skipped.
 *
 * @author Matt Hall, John Watkinson, Morgan Delagrange
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:24 $
 * @since Commons Collections 2.1
 */
public class UniqueFilterIterator <E> extends FilterIterator<E> {

    //-------------------------------------------------------------------------
    
    /**
     * Constructs a new <code>UniqueFilterIterator</code>.
     *
     * @param iterator the iterator to use
     */
    public UniqueFilterIterator(Iterator<E> iterator) {
        super(iterator, UniquePredicate.getInstance());
    }

}

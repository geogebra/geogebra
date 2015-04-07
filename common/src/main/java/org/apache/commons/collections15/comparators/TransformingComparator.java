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
package org.apache.commons.collections15.comparators;

import java.util.Comparator;

import org.apache.commons.collections15.Transformer;

/**
 * Decorates another Comparator with transformation behavior. That is, the
 * return value from the transform operation will be passed to the decorated
 * {@link Comparator#compare(Object,Object) compare} method.
 *
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @see org.apache.commons.collections15.Transformer
 * @see org.apache.commons.collections15.comparators.ComparableComparator
 * @since Commons Collections 2.0 (?)
 */
public class TransformingComparator <I,O> implements Comparator<I> {

    /**
     * The decorated comparator.
     */
    protected Comparator<O> decorated;
    /**
     * The transformer being used.
     */
    protected Transformer<I, O> transformer;

    /**
     * Constructs an instance with the given Transformer and Comparator.
     *
     * @param transformer what will transform the arguments to <code>compare</code>
     * @param decorated   the decorated Comparator
     */
    public TransformingComparator(Transformer<I, O> transformer, Comparator<O> decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the result of comparing the values from the transform operation.
     *
     * @param obj1 the first object to transform then compare
     * @param obj2 the second object to transform then compare
     * @return negative if obj1 is less, positive if greater, zero if equal
     */
    public int compare(I obj1, I obj2) {
        O value1 = this.transformer.transform(obj1);
        O value2 = this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }

}


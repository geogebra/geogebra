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

import java.util.Comparator;
import java.util.SortedSet;

import org.apache.commons.collections15.Transformer;

/**
 * Decorates another <code>SortedSet</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:39 $
 * @since Commons Collections 3.0
 */
public class TransformedSortedSet extends TransformedSet implements SortedSet {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = -1675486811351124386L;

    /**
     * Factory method to create a transforming sorted set.
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     *
     * @param set         the set to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    public static <I,O> SortedSet<O> decorate(SortedSet<I> set, Transformer<? super I, ? extends O> transformer) {
        return new TransformedSortedSet(set, transformer);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p/>
     * If there are any elements already in the set being decorated, they
     * are NOT transformed.
     *
     * @param set         the set to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if set or transformer is null
     */
    protected TransformedSortedSet(SortedSet set, Transformer transformer) {
        super(set, transformer);
    }

    /**
     * Gets the decorated set.
     *
     * @return the decorated set
     */
    protected SortedSet getSortedSet() {
        return (SortedSet) collection;
    }

    //-----------------------------------------------------------------------
    public Object first() {
        return getSortedSet().first();
    }

    public Object last() {
        return getSortedSet().last();
    }

    public Comparator comparator() {
        return getSortedSet().comparator();
    }

    //-----------------------------------------------------------------------
    public SortedSet subSet(Object fromElement, Object toElement) {
        SortedSet set = getSortedSet().subSet(fromElement, toElement);
        return new TransformedSortedSet(set, transformer);
    }

    public SortedSet headSet(Object toElement) {
        SortedSet set = getSortedSet().headSet(toElement);
        return new TransformedSortedSet(set, transformer);
    }

    public SortedSet tailSet(Object fromElement) {
        SortedSet set = getSortedSet().tailSet(fromElement);
        return new TransformedSortedSet(set, transformer);
    }

}

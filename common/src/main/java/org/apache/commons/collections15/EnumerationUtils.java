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
package org.apache.commons.collections15;

import java.util.Enumeration;
import java.util.List;

import org.apache.commons.collections15.iterators.EnumerationIterator;

/**
 * Provides utility methods for {@link Enumeration} instances.
 *
 * @author Matt Hall, John Watkinson, <a href="mailto:ggregory@seagullsw.com">Gary Gregory</a>
 * @version $Id: EnumerationUtils.java,v 1.1 2005/10/11 17:05:19 pents90 Exp $
 * @since Commons Collections 3.0
 */
public class EnumerationUtils {

    /**
     * EnumerationUtils is not normally instantiated.
     */
    public EnumerationUtils() {
        // no init.
    }

    /**
     * Creates a list based on an enumeration.
     * <p/>
     * <p>As the enumeration is traversed, an ArrayList of its values is
     * created. The new list is returned.</p>
     *
     * @param enumeration the enumeration to traverse, which should not be <code>null</code>.
     * @throws NullPointerException if the enumeration parameter is <code>null</code>.
     */
    public static <E> List<E> toList(Enumeration<E> enumeration) {
        return IteratorUtils.toList(new EnumerationIterator(enumeration));
    }

}

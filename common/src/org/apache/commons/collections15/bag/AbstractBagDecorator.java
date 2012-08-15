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
package org.apache.commons.collections15.bag;

import java.util.Set;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.collection.AbstractCollectionDecorator;

/**
 * Decorates another <code>Bag</code> to provide additional behaviour.
 * <p/>
 * Methods are forwarded directly to the decorated bag.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 3.0
 */
public abstract class AbstractBagDecorator <E> extends AbstractCollectionDecorator<E> implements Bag<E> {

    /**
     * Constructor only used in deserialization, do not use otherwise.
     *
     * @since Commons Collections 3.1
     */
    protected AbstractBagDecorator() {
        super();
    }

    /**
     * Constructor that wraps (not copies).
     *
     * @param bag the bag to decorate, must not be null
     * @throws IllegalArgumentException if list is null
     */
    protected AbstractBagDecorator(Bag<E> bag) {
        super(bag);
    }

    /**
     * Gets the bag being decorated.
     *
     * @return the decorated bag
     */
    protected Bag<E> getBag() {
        return (Bag<E>) getCollection();
    }

    //-----------------------------------------------------------------------
    public int getCount(E object) {
        return getBag().getCount(object);
    }

    public boolean add(E object, int count) {
        return getBag().add(object, count);
    }

    public boolean remove(E object, int count) {
        return getBag().remove(object, count);
    }

    public Set<E> uniqueSet() {
        return getBag().uniqueSet();
    }

}

// GenericsNote: Converted, but unfortunately very little type-safety could be achieved without breaking Collection interface.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.Transformer;

/**
 * Decorates another <code>Collection</code> to transform objects that are added.
 * <p/>
 * The add methods are affected by this class.
 * Thus objects must be removed or searched for using their transformed form.
 * For example, if the transformation converts Strings to Integers, you must
 * use the Integer form to remove objects.
 * <p/>
 * This class is Serializable from Commons Collections 3.1.
 * <p>
 * Note: This class cannot support generics without breaking the Collection contract.
 *
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:20 $
 * @since Commons Collections 3.0
 */
public class TransformedCollection <I,O> extends AbstractSerializableCollectionDecorator {

    /**
     * Serialization version
     */
    private static final long serialVersionUID = 8692300188161871514L;

    /**
     * The transformer to use
     */
    protected final Transformer<? super I, ? extends O> transformer;

    /**
     * Factory method to create a transforming collection.
     * <p/>
     * If there are any elements already in the collection being decorated, they
     * are NOT transformed.
     *
     * @param coll        the collection to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @return a new transformed collection
     * @throws IllegalArgumentException if collection or transformer is null
     */
    public static <I,O> Collection<O> decorate(Collection<I> coll, Transformer<? super I, ? extends O> transformer) {
        return new TransformedCollection<I, O>(coll, transformer);
    }
    
    //-----------------------------------------------------------------------
    /**
     * Constructor that wraps (not copies).
     * <p/>
     * If there are any elements already in the collection being decorated, they
     * are NOT transformed.
     *
     * @param coll        the collection to decorate, must not be null
     * @param transformer the transformer to use for conversion, must not be null
     * @throws IllegalArgumentException if collection or transformer is null
     */
    protected TransformedCollection(Collection<I> coll, Transformer<? super I, ? extends O> transformer) {
        super(coll);
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        this.transformer = transformer;
    }

    /**
     * Transforms an object.
     * <p/>
     * The transformer itself may throw an exception if necessary.
     *
     * @param object the object to transform
     * @return a transformed object
     */
    protected O transform(I object) {
        return transformer.transform(object);
    }

    /**
     * Transforms a collection.
     * <p/>
     * The transformer itself may throw an exception if necessary.
     *
     * @param coll the collection to transform
     * @return a transformed object
     */
    protected Collection<O> transform(Collection<? extends I> coll) {
        List<O> list = new ArrayList<O>(coll.size());
        for (Iterator<? extends I> it = coll.iterator(); it.hasNext();) {
            list.add(transform(it.next()));
        }
        return list;
    }

    //-----------------------------------------------------------------------
    public boolean add(Object object) {
        O transformed = transform((I) object);
        return getCollection().add(transformed);
    }

    /**
     * A better typed version of the add method (although breaks the Collection interface).
     */
    public boolean addTyped(I object) {
        return add(object);
    }

    public boolean addAll(Collection coll) {
        Collection<O> col2 = transform((Collection<? extends I>) coll);
        return getCollection().addAll(col2);
    }

    /**
     * A better typed version of the addAll method (although breaks the Collection interface).
     */
    public boolean addAllTyped(Collection<? extends I> coll) {
        return addAll(coll);
    }

}

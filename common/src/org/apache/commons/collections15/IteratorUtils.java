// GenericsNote: Converted, although getIterator(Object) is unsatisfying.
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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections15.iterators.ArrayIterator;
import org.apache.commons.collections15.iterators.ArrayListIterator;
import org.apache.commons.collections15.iterators.CollatingIterator;
import org.apache.commons.collections15.iterators.EmptyIterator;
import org.apache.commons.collections15.iterators.EmptyListIterator;
import org.apache.commons.collections15.iterators.EmptyMapIterator;
import org.apache.commons.collections15.iterators.EmptyOrderedIterator;
import org.apache.commons.collections15.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections15.iterators.EnumerationIterator;
import org.apache.commons.collections15.iterators.FilterIterator;
import org.apache.commons.collections15.iterators.FilterListIterator;
import org.apache.commons.collections15.iterators.IteratorChain;
import org.apache.commons.collections15.iterators.IteratorEnumeration;
import org.apache.commons.collections15.iterators.ListIteratorWrapper;
import org.apache.commons.collections15.iterators.LoopingIterator;
import org.apache.commons.collections15.iterators.ObjectArrayIterator;
import org.apache.commons.collections15.iterators.ObjectArrayListIterator;
import org.apache.commons.collections15.iterators.ObjectGraphIterator;
import org.apache.commons.collections15.iterators.SingletonIterator;
import org.apache.commons.collections15.iterators.SingletonListIterator;
import org.apache.commons.collections15.iterators.TransformIterator;
import org.apache.commons.collections15.iterators.UnmodifiableIterator;
import org.apache.commons.collections15.iterators.UnmodifiableListIterator;
import org.apache.commons.collections15.iterators.UnmodifiableMapIterator;

/**
 * Provides static utility methods and decorators for {@link Iterator}
 * instances. The implementations are provided in the iterators subpackage.
 * <p/>
 * WARNING: Due to human error certain binary incompatabilities were introduced
 * between Commons Collections 2.1 and 3.0. The class remained source and test
 * compatible, so if you can recompile all your classes and dependencies
 * everything is OK. Those methods which are binary incompatible are marked as
 * such, together with alternate solutions that are binary compatible
 * against versions 2.1.1 and 3.1.
 *
 * @author Stephen Colebourne
 * @author Matt Hall, John Watkinson, Phil Steitz
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 2.1
 */
public class IteratorUtils {
    // validation is done in this class in certain cases because the
    // public classes allow invalid states

    /**
     * An iterator over no elements.
     * <p/>
     * WARNING: This constant is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyIterator.INSTANCE</code> for compatability with Commons Collections 2.1.1.
     */
    public static final ResettableIterator EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;
    /**
     * A list iterator over no elements.
     * <p/>
     * WARNING: This constant is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyListIterator.INSTANCE</code> for compatability with Commons Collections 2.1.1.
     */
    public static final ResettableListIterator EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;
    /**
     * An ordered iterator over no elements.
     */
    public static final OrderedIterator EMPTY_ORDERED_ITERATOR = EmptyOrderedIterator.INSTANCE;
    /**
     * A map iterator over no elements.
     */
    public static final MapIterator EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;
    /**
     * An ordered map iterator over no elements.
     */
    public static final OrderedMapIterator EMPTY_ORDERED_MAP_ITERATOR = EmptyOrderedMapIterator.INSTANCE;

    /**
     * IteratorUtils is not normally instantiated.
     */
    public IteratorUtils() {
    }

    // Empty
    //-----------------------------------------------------------------------
    /**
     * Gets an empty iterator.
     * <p/>
     * This iterator is a valid iterator object that will iterate over
     * nothing.
     * <p/>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyIterator.INSTANCE</code> for compatability with Commons Collections 2.1.1.
     *
     * @return an iterator over nothing
     */
    public static ResettableIterator emptyIterator() {
        return EMPTY_ITERATOR;
    }

    /**
     * Gets an empty list iterator.
     * <p/>
     * This iterator is a valid list iterator object that will iterate
     * over nothing.
     * <p/>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>EmptyListIterator.INSTANCE</code> for compatability with Commons Collections 2.1.1.
     *
     * @return a list iterator over nothing
     */
    public static ResettableListIterator emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    /**
     * Gets an empty ordered iterator.
     * <p/>
     * This iterator is a valid iterator object that will iterate
     * over nothing.
     *
     * @return an ordered iterator over nothing
     */
    public static OrderedIterator emptyOrderedIterator() {
        return EMPTY_ORDERED_ITERATOR;
    }

    /**
     * Gets an empty map iterator.
     * <p/>
     * This iterator is a valid map iterator object that will iterate
     * over nothing.
     *
     * @return a map iterator over nothing
     */
    public static MapIterator emptyMapIterator() {
        return EMPTY_MAP_ITERATOR;
    }

    /**
     * Gets an empty ordered map iterator.
     * <p/>
     * This iterator is a valid map iterator object that will iterate
     * over nothing.
     *
     * @return a map iterator over nothing
     */
    public static OrderedMapIterator emptyOrderedMapIterator() {
        return EMPTY_ORDERED_MAP_ITERATOR;
    }

    // Singleton
    //-----------------------------------------------------------------------
    /**
     * Gets a singleton iterator.
     * <p/>
     * This iterator is a valid iterator object that will iterate over
     * the specified object.
     * <p/>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new SingletonIterator(object)</code> for compatability.
     *
     * @param object the single object over which to iterate
     * @return a singleton iterator over the object
     */
    public static <E> ResettableIterator<E> singletonIterator(E object) {
        return new SingletonIterator<E>(object);
    }

    /**
     * Gets a singleton list iterator.
     * <p/>
     * This iterator is a valid list iterator object that will iterate over
     * the specified object.
     *
     * @param object the single object over which to iterate
     * @return a singleton list iterator over the object
     */
    public static <E> ListIterator<E> singletonListIterator(E object) {
        return new SingletonListIterator<E>(object);
    }

    // Arrays
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over an object array.
     * <p/>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new ArrayIterator(array)</code> for compatability.
     *
     * @param array the array over which to iterate
     * @return an iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(E[] array) {
        return new ObjectArrayIterator<E>(array);
    }

    /**
     * Gets an iterator over an object or primitive array.
     * <p/>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array the array over which to iterate
     * @return an iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException     if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(Object array) {
        return new ArrayIterator<E>(array);
    }

    /**
     * Gets an iterator over the end part of an object array.
     * <p/>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new ArrayIterator(array,start)</code> for compatability.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *                                   than the length of the array
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(E[] array, int start) {
        return new ObjectArrayIterator<E>(array, start);
    }

    /**
     * Gets an iterator over the end part of an object or primitive array.
     * <p/>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero or greater
     *                                   than the length of the array
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(Object array, int start) {
        return new ArrayIterator<E>(array, start);
    }

    /**
     * Gets an iterator over part of an object array.
     * <p/>
     * WARNING: This method is binary incompatible with Commons Collections 2.1 and 2.1.1.
     * Use <code>new ArrayIterator(array,start,end)</code> for compatability.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException  if end is before start
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(E[] array, int start, int end) {
        return new ObjectArrayIterator<E>(array, start, end);
    }

    /**
     * Gets an iterator over part of an object or primitive array.
     * <p/>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException  if end is before start
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(Object array, int start, int end) {
        return new ArrayIterator<E>(array, start, end);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a list iterator over an object array.
     *
     * @param array the array over which to iterate
     * @return a list iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(E[] array) {
        return new ObjectArrayListIterator(array);
    }

    /**
     * Gets a list iterator over an object or primitive array.
     * <p/>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array the array over which to iterate
     * @return a list iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException     if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(Object array) {
        return new ArrayListIterator<E>(array);
    }

    /**
     * Gets a list iterator over the end part of an object array.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(E[] array, int start) {
        return new ObjectArrayListIterator<E>(array, start);
    }

    /**
     * Gets a list iterator over the end part of an object or primitive array.
     * <p/>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(Object array, int start) {
        return new ArrayListIterator<E>(array, start);
    }

    /**
     * Gets a list iterator over part of an object array.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException  if end is before start
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(E[] array, int start, int end) {
        return new ObjectArrayListIterator<E>(array, start, end);
    }

    /**
     * Gets a list iterator over part of an object or primitive array.
     * <p/>
     * This method will handle primitive arrays as well as object arrays.
     * The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException  if end is before start
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(Object array, int start, int end) {
        return new ArrayListIterator<E>(array, start, end);
    }
    
    // Unmodifiable
    //-----------------------------------------------------------------------
    /**
     * Gets an immutable version of an {@link Iterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove} method.
     *
     * @param iterator the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <E> Iterator<E> unmodifiableIterator(Iterator<E> iterator) {
        return UnmodifiableIterator.decorate(iterator);
    }

    /**
     * Gets an immutable version of a {@link ListIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link ListIterator#add} and
     * {@link ListIterator#set} methods.
     *
     * @param listIterator the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <E> ListIterator<E> unmodifiableListIterator(ListIterator<E> listIterator) {
        return UnmodifiableListIterator.decorate(listIterator);
    }

    /**
     * Gets an immutable version of a {@link MapIterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove}, {@link MapIterator#setValue(Object)} methods.
     *
     * @param mapIterator the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <K,V> MapIterator<K, V> unmodifiableMapIterator(MapIterator<K, V> mapIterator) {
        return UnmodifiableMapIterator.decorate(mapIterator);
    }

    // Chained
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that iterates through two {@link Iterator}s
     * one after another.
     *
     * @param iterator1 the first iterators to use, not null
     * @param iterator2 the first iterators to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static <E> Iterator<E> chainedIterator(Iterator<E> iterator1, Iterator<E> iterator2) {
        return new IteratorChain<E>(iterator1, iterator2);
    }

    /**
     * Gets an iterator that iterates through an array of {@link Iterator}s
     * one after another.
     *
     * @param iterators the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null
     */
    public static <E> Iterator<E> chainedIterator(Iterator<E>[] iterators) {
        return new IteratorChain<E>(iterators);
    }

    /**
     * Gets an iterator that iterates through a collections15 of {@link Iterator}s
     * one after another.
     *
     * @param iterators the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException   if the iterators collection contains the wrong object type
     */
    public static <E> Iterator<E> chainedIterator(Collection<Iterator<? extends E>> iterators) {
        return new IteratorChain<E>(iterators);
    }

    // Collated
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in a collection of ordered {@link Iterator}s.
     * <p/>
     * Given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
     * the {@link Iterator#next()} method will return the lesser of
     * <code>A.next()</code> and <code>B.next()</code>.
     * <p/>
     * The comparator is optional. If null is specified then natural order is used.
     *
     * @param comparator the comparator to use, may be null for natural order
     * @param iterator1  the first iterators to use, not null
     * @param iterator2  the first iterators to use, not null
     * @return a combination iterator over the iterators
     * @throws NullPointerException if either iterator is null
     */
    public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Iterator<? extends E> iterator1, Iterator<? extends E> iterator2) {
        return new CollatingIterator<E>(comparator, iterator1, iterator2);
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in an array of {@link Iterator}s.
     * <p/>
     * Given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
     * the {@link Iterator#next()} method will return the lesser of
     * <code>A.next()</code> and <code>B.next()</code> and so on.
     * <p/>
     * The comparator is optional. If null is specified then natural order is used.
     *
     * @param comparator the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators array is null or contains a null
     */
    public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Iterator<? extends E>[] iterators) {
        return new CollatingIterator<E>(comparator, iterators);
    }

    /**
     * Gets an iterator that provides an ordered iteration over the elements
     * contained in a collection of {@link Iterator}s.
     * <p/>
     * Given two ordered {@link Iterator}s <code>A</code> and <code>B</code>,
     * the {@link Iterator#next()} method will return the lesser of
     * <code>A.next()</code> and <code>B.next()</code> and so on.
     * <p/>
     * The comparator is optional. If null is specified then natural order is used.
     *
     * @param comparator the comparator to use, may be null for natural order
     * @param iterators  the iterators to use, not null or empty or contain nulls
     * @return a combination iterator over the iterators
     * @throws NullPointerException if iterators collection is null or contains a null
     * @throws ClassCastException   if the iterators collection contains the wrong object type
     */
    public static <E> Iterator<E> collatedIterator(Comparator<? super E> comparator, Collection<Iterator<? extends E>> iterators) {
        return new CollatingIterator<E>(comparator, iterators);
    }
    
    // Object Graph
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that operates over an object graph.
     * <p/>
     * This iterator can extract multiple objects from a complex tree-like object graph.
     * The iteration starts from a single root object.
     * It uses a <code>Transformer</code> to extract the iterators and elements.
     * Its main benefit is that no intermediate <code>List</code> is created.
     * <p/>
     * For example, consider an object graph:
     * <pre>
     *                 |- Branch -- Leaf
     *                 |         \- Leaf
     *         |- Tree |         /- Leaf
     *         |       |- Branch -- Leaf
     *  Forest |                 \- Leaf
     *         |       |- Branch -- Leaf
     *         |       |         \- Leaf
     *         |- Tree |         /- Leaf
     *                 |- Branch -- Leaf
     *                 |- Branch -- Leaf</pre>
     * The following <code>Transformer</code>, used in this class, will extract all
     * the Leaf objects without creating a combined intermediate list:
     * <pre>
     * public Object transform(Object input) {
     *   if (input instanceof Forest) {
     *     return ((Forest) input).treeIterator();
     *   }
     *   if (input instanceof Tree) {
     *     return ((Tree) input).branchIterator();
     *   }
     *   if (input instanceof Branch) {
     *     return ((Branch) input).leafIterator();
     *   }
     *   if (input instanceof Leaf) {
     *     return input;
     *   }
     *   throw new ClassCastException();
     * }</pre>
     * <p/>
     * Internally, iteration starts from the root object. When next is called,
     * the transformer is called to examine the object. The transformer will return
     * either an iterator or an object. If the object is an Iterator, the next element
     * from that iterator is obtained and the process repeats. If the element is an object
     * it is returned.
     * <p/>
     * Under many circumstances, linking Iterators together in this manner is
     * more efficient (and convenient) than using nested for loops to extract a list.
     *
     * @param root        the root object to start iterating from, null results in an empty iterator
     * @param transformer the transformer to use, see above, null uses no effect transformer
     * @return a new object graph iterator
     * @since Commons Collections 3.1
     */
    public static Iterator objectGraphIterator(Object root, Transformer transformer) {
        return new ObjectGraphIterator(root, transformer);
    }
    
    // Transformed
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that transforms the elements of another iterator.
     * <p/>
     * The transformation occurs during the next() method and the underlying
     * iterator is unaffected by the transformation.
     *
     * @param iterator  the iterator to use, not null
     * @param transform the transform to use, not null
     * @return a new transforming iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <I,O> Iterator<O> transformedIterator(Iterator<I> iterator, Transformer<I, O> transform) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (transform == null) {
            throw new NullPointerException("Transformer must not be null");
        }
        return new TransformIterator<I, O>(iterator, transform);
    }
    
    // Filtered
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that filters another iterator.
     * <p/>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     *
     * @param iterator  the iterator to use, not null
     * @param predicate the predicate to use as a filter, not null
     * @return a new filtered iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <E> Iterator<E> filteredIterator(Iterator<E> iterator, Predicate<? super E> predicate) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterIterator<E>(iterator, predicate);
    }

    /**
     * Gets a list iterator that filters another list iterator.
     * <p/>
     * The returned iterator will only return objects that match the specified
     * filtering predicate.
     *
     * @param listIterator the list iterator to use, not null
     * @param predicate    the predicate to use as a filter, not null
     * @return a new filtered iterator
     * @throws NullPointerException if either parameter is null
     */
    public static <E> ListIterator<E> filteredListIterator(ListIterator<E> listIterator, Predicate<? super E> predicate) {
        if (listIterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        if (predicate == null) {
            throw new NullPointerException("Predicate must not be null");
        }
        return new FilterListIterator<E>(listIterator, predicate);
    }
    
    // Looping
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that loops continuously over the supplied collection.
     * <p/>
     * The iterator will only stop looping if the remove method is called
     * enough times to empty the collection, or if the collection is empty
     * to start with.
     *
     * @param coll the collection to iterate over, not null
     * @return a new looping iterator
     * @throws NullPointerException if the collection is null
     */
    public static <E> ResettableIterator<E> loopingIterator(Collection<E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new LoopingIterator<E>(coll);
    }
    
    // Views
    //-----------------------------------------------------------------------
    /**
     * Gets an iterator that provides an iterator view of the given enumeration.
     *
     * @param enumeration the enumeration to use
     * @return a new iterator
     */
    public static <E> Iterator<E> asIterator(Enumeration<E> enumeration) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        return new EnumerationIterator<E>(enumeration);
    }

    /**
     * Gets an iterator that provides an iterator view of the given enumeration
     * that will remove elements from the specified collection.
     *
     * @param enumeration      the enumeration to use
     * @param removeCollection the collection to remove elements from
     * @return a new iterator
     */
    public static <E> Iterator<E> asIterator(Enumeration<E> enumeration, Collection<E> removeCollection) {
        if (enumeration == null) {
            throw new NullPointerException("Enumeration must not be null");
        }
        if (removeCollection == null) {
            throw new NullPointerException("Collection must not be null");
        }
        return new EnumerationIterator<E>(enumeration, removeCollection);
    }

    /**
     * Gets an enumeration that wraps an iterator.
     *
     * @param iterator the iterator to use, not null
     * @return a new enumeration
     * @throws NullPointerException if iterator is null
     */
    public static <E> Enumeration<E> asEnumeration(Iterator<E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new IteratorEnumeration<E>(iterator);
    }

    /**
     * Gets a list iterator based on a simple iterator.
     * <p/>
     * As the wrapped Iterator is traversed, a LinkedList of its values is
     * cached, permitting all required operations of ListIterator.
     *
     * @param iterator the iterator to use, not null
     * @return a new iterator
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> ListIterator<E> toListIterator(Iterator<E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        return new ListIteratorWrapper<E>(iterator);
    }

    /**
     * Gets an array based on an iterator.
     * <p/>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     *
     * @param iterator the iterator to use, not null
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> E[] toArray(Iterator<E> iterator) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        List<E> list = toList(iterator, 100);
        return (E[]) list.toArray();
    }

    /**
     * Gets an array based on an iterator.
     * <p/>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, this is converted to an array.
     *
     * @param iterator   the iterator to use, not null
     * @param arrayClass the class of array to create
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     * @throws NullPointerException if arrayClass is null
     * @throws ClassCastException   if the arrayClass is invalid
     */
    public static <E> E[] toArray(Iterator<E> iterator, Class<E> arrayClass) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (arrayClass == null) {
            throw new NullPointerException("Array class must not be null");
        }
        List<E> list = toList(iterator, 100);
        return list.toArray((E[]) Array.newInstance(arrayClass, list.size()));
    }

    /**
     * Gets a list based on an iterator.
     * <p/>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     *
     * @param iterator the iterator to use, not null
     * @return a list of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> List<E> toList(Iterator<E> iterator) {
        return toList(iterator, 10);
    }

    /**
     * Gets a list based on an iterator.
     * <p/>
     * As the wrapped Iterator is traversed, an ArrayList of its values is
     * created. At the end, the list is returned.
     *
     * @param iterator      the iterator to use, not null
     * @param estimatedSize the initial size of the ArrayList
     * @return a list of the iterator contents
     * @throws NullPointerException     if iterator parameter is null
     * @throws IllegalArgumentException if the size is less than 1
     */
    public static <E> List<E> toList(Iterator<E> iterator, int estimatedSize) {
        if (iterator == null) {
            throw new NullPointerException("Iterator must not be null");
        }
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        List<E> list = new ArrayList<E>(estimatedSize);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Gets a suitable Iterator for the given object.
     * <p/>
     * This method can handles objects as follows
     * <ul>
     * <li>null - empty iterator
     * <li>Iterator - returned directly
     * <li>Enumeration - wrapped
     * <li>Collection - iterator from collection returned
     * <li>Map - values iterator returned
     * <li>Dictionary - values (elements) enumeration returned as iterator
     * <li>array - iterator over array returned
     * <li>object with iterator() public method accessed by reflection
     * <li>object - singleton iterator
     * </ul>
     *
     * @param obj the object to convert to an iterator
     * @return a suitable iterator, never null
     */
    public static Iterator getIterator(Object obj) {
        if (obj == null) {
            return emptyIterator();

        } else if (obj instanceof Iterator) {
            return (Iterator) obj;

        } else if (obj instanceof Collection) {
            return ((Collection) obj).iterator();

        } else if (obj instanceof Object[]) {
            return new ObjectArrayIterator((Object[]) obj);

        } else if (obj instanceof Enumeration) {
            return new EnumerationIterator((Enumeration) obj);

        } else if (obj instanceof Map) {
            return ((Map) obj).values().iterator();

        } else if (obj instanceof Dictionary) {
            return new EnumerationIterator(((Dictionary) obj).elements());

        } else if (obj.getClass().isArray()) {
            return new ArrayIterator(obj);

        } else {
            try {
                Method method = obj.getClass().getMethod("iterator", null);
                if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                    Iterator it = (Iterator) method.invoke(obj, null);
                    if (it != null) {
                        return it;
                    }
                }
            } catch (Exception ex) {
                // ignore
            }
            return singletonIterator(obj);
        }
    }

}

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
import java.util.Comparator;

import org.apache.commons.collections15.comparators.BooleanComparator;
import org.apache.commons.collections15.comparators.ComparableComparator;
import org.apache.commons.collections15.comparators.ComparatorChain;
import org.apache.commons.collections15.comparators.NullComparator;
import org.apache.commons.collections15.comparators.ReverseComparator;
import org.apache.commons.collections15.comparators.TransformingComparator;

/**
 * Provides convenient static utility methods for <Code>Comparator</Code>
 * objects.
 * <p/>
 * Most of the functionality in this class can also be found in the
 * <code>comparators</code> package. This class merely provides a
 * convenient central place if you have use for more than one class
 * in the <code>comparators</code> subpackage.
 *
 * @author Paul Jack
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:19 $
 * @since Commons Collections 2.1
 */
public class ComparatorUtils {

    /**
     * ComparatorUtils should not normally be instantiated.
     */
    public ComparatorUtils() {
    }

    /**
     * Comparator for natural sort order.
     *
     * @see ComparableComparator#getInstance
     */
    public static final Comparator NATURAL_COMPARATOR = ComparableComparator.getInstance();

    /**
     * Gets a comparator that uses the natural order of the objects.
     *
     * @return a comparator which uses natural order
     */
    public static Comparator naturalComparator() {
        return NATURAL_COMPARATOR;
    }

    /**
     * Gets a comparator that compares using two {@link Comparator}s.
     * <p/>
     * The second comparator is used if the first comparator returns equal.
     *
     * @param comparator1 the first comparator to use, not null
     * @param comparator2 the first comparator to use, not null
     * @return a {@link ComparatorChain} formed from the two comparators
     * @throws NullPointerException if either comparator is null
     * @see ComparatorChain
     */
    public static <T> Comparator<T> chainedComparator(Comparator<T> comparator1, Comparator<T> comparator2) {
        return chainedComparator(new Comparator[]{comparator1, comparator2});
    }

    /**
     * Gets a comparator that compares using an array of {@link Comparator}s, applied
     * in sequence until one returns not equal or the array is exhausted.
     *
     * @param comparators the comparators to use, not null or empty or containing nulls
     * @return a {@link ComparatorChain} formed from the input comparators
     * @throws NullPointerException if comparators array is null or contains a null
     * @see ComparatorChain
     */
    public static <T> Comparator<T> chainedComparator(Comparator<T>[] comparators) {
        ComparatorChain chain = new ComparatorChain();
        for (int i = 0; i < comparators.length; i++) {
            if (comparators[i] == null) {
                throw new NullPointerException("Comparator cannot be null");
            }
            chain.addComparator(comparators[i]);
        }
        return chain;
    }

    /**
     * Gets a comparator that compares using a collection of {@link Comparator}s,
     * applied in (default iterator) sequence until one returns not equal or the
     * collection is exhausted.
     *
     * @param comparators the comparators to use, not null or empty or containing nulls
     * @return a {@link ComparatorChain} formed from the input comparators
     * @throws NullPointerException if comparators collection is null or contains a null
     * @throws ClassCastException   if the comparators collection contains the wrong object type
     * @see ComparatorChain
     */
    public static <T> Comparator<T> chainedComparator(Collection<T> comparators) {
        return chainedComparator((Comparator[]) comparators.toArray(new Comparator[comparators.size()]));
    }

    /**
     * Gets a comparator that reverses the order of the given comparator.
     *
     * @param comparator the comparator to reverse
     * @return a comparator that reverses the order of the input comparator
     * @see ReverseComparator
     */
    public static <T> Comparator<T> reversedComparator(Comparator<T> comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new ReverseComparator(comparator);
    }

    /**
     * Gets a Comparator that can sort Boolean objects.
     * <p/>
     * The parameter specifies whether true or false is sorted first.
     * <p/>
     * The comparator throws NullPointerException if a null value is compared.
     *
     * @param trueFirst when <code>true</code>, sort
     *                  <code>true</code> {@link Boolean}s before
     *                  <code>false</code> {@link Boolean}s.
     * @return a comparator that sorts booleans
     */
    public static Comparator<Boolean> booleanComparator(boolean trueFirst) {
        return BooleanComparator.getBooleanComparator(trueFirst);
    }

    /**
     * Gets a Comparator that controls the comparison of <code>null</code> values.
     * <p/>
     * The returned comparator will consider a null value to be less than
     * any nonnull value, and equal to any other null value.  Two nonnull
     * values will be evaluated with the given comparator.
     *
     * @param comparator the comparator that wants to allow nulls
     * @return a version of that comparator that allows nulls
     * @see NullComparator
     */
    public static <T> Comparator<T> nullLowComparator(Comparator<T> comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new NullComparator(comparator, false);
    }

    /**
     * Gets a Comparator that controls the comparison of <code>null</code> values.
     * <p/>
     * The returned comparator will consider a null value to be greater than
     * any nonnull value, and equal to any other null value.  Two nonnull
     * values will be evaluated with the given comparator.
     *
     * @param comparator the comparator that wants to allow nulls
     * @return a version of that comparator that allows nulls
     * @see NullComparator
     */
    public static <T> Comparator<T> nullHighComparator(Comparator<T> comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new NullComparator(comparator, true);
    }

    /**
     * Gets a Comparator that passes transformed objects to the given comparator.
     * <p/>
     * Objects passed to the returned comparator will first be transformed
     * by the given transformer before they are compared by the given
     * comparator.
     *
     * @param comparator  the sort order to use
     * @param transformer the transformer to use
     * @return a comparator that transforms its input objects before comparing them
     * @see TransformingComparator
     */
    public static <I,O> Comparator<O> transformedComparator(Comparator<I> comparator, Transformer<I, O> transformer) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        return new TransformingComparator(transformer, comparator);
    }

    /**
     * Returns the smaller of the given objects according to the given
     * comparator, returning the second object if the comparator
     * returns equal.
     *
     * @param o1         the first object to compare
     * @param o2         the second object to compare
     * @param comparator the sort order to use
     * @return the smaller of the two objects
     */
    public static <T> T min(T o1, T o2, Comparator<T> comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        int c = comparator.compare(o1, o2);
        return (c < 0) ? o1 : o2;
    }

    /**
     * Returns the larger of the given objects according to the given
     * comparator, returning the second object if the comparator
     * returns equal.
     *
     * @param o1         the first object to compare
     * @param o2         the second object to compare
     * @param comparator the sort order to use
     * @return the larger of the two objects
     */
    public static <T> T max(T o1, T o2, Comparator<T> comparator) {
        if (comparator == null) {
            comparator = NATURAL_COMPARATOR;
        }
        int c = comparator.compare(o1, o2);
        return (c > 0) ? o1 : o2;
    }

}

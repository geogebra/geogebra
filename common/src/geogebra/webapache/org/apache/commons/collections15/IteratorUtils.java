package org.apache.commons.collections15;

import java.util.Iterator;

import org.apache.commons.collections15.iterators.UnmodifiableIterator;


public class IteratorUtils {

    // Unmodifiable
    //-----------------------------------------------------------------------
    /**
     * Gets an immutable version of an {@link Iterator}. The returned object
     * will always throw an {@link UnsupportedOperationException} for
     * the {@link Iterator#remove} method.
     *
     * @param iterator  the iterator to make immutable
     * @return an immutable version of the iterator
     */
    public static <E> Iterator<E> unmodifiableIterator(Iterator<E> iterator) {
        return UnmodifiableIterator.decorate(iterator);
    }

}

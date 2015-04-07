/*
 * Created on Apr 2, 2006
 *
 * Copyright (c) 2006, the JUNG Project and the Regents of the University 
 * of California
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * http://jung.sourceforge.net/license.txt for a description.
 */
package edu.uci.ics.jung.graph.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;


/**
 * An implementation of <code>Collection</code> that stores exactly
 * 2 non-null objects and is not mutable.  They respect <code>equals</code>
 * and may be used as indices or map keys.<p>
 * Note that they do not protect from malevolent behavior: if one or another
 * object in the tuple is mutable, then it can be changed with the usual bad
 * effects.
 */
@SuppressWarnings("serial")
public final class Pair<T> implements Collection<T>, Serializable
{
    private T first;
    private T second;

    /**
     * Creates a <code>Pair</code> from the specified elements.
     * @param value1 the first value in the new <code>Pair</code>
     * @param value2 the second value in the new <code>Pair</code>
     * @throws IllegalArgumentException if either argument is null
     */
    public Pair(T value1, T value2) 
    {
    	if(value1 == null || value2 == null) 
    		throw new IllegalArgumentException("Pair cannot contain null values");
        first = value1;
        second = value2;
    }
    
    /**
     * Creates a Pair from the passed Collection.
     * The size of the Collection must be 2.
     * @param values the elements of the new <code>Pair</code>
     * @throws IllegalArgumentException if the input collection is null,
     * contains null values, or has != 2 elements.
     */
    public Pair(Collection<? extends T> values) 
    {
        if (values == null)
            throw new IllegalArgumentException("Input collection cannot be null");
    	if (values.size() == 2)
        {
            if(values.contains(null)) 
                throw new IllegalArgumentException("Pair cannot contain null values");
            Iterator<? extends T> iter = values.iterator();
            first = iter.next();
            second = iter.next();
       }
        else
            throw new IllegalArgumentException("Pair may only be created from a Collection of exactly 2 elements");
        
    }
    
    /**
     * Creates a <code>Pair</code> from the passed array.
     * The size of the array must be 2.
     * @throws IllegalArgumentException if the input array is null,
     * contains null values, or has != 2 elements.
     */
    public Pair(T[] values)
    {
        if (values == null)
            throw new IllegalArgumentException("Input array cannot be null");
        if (values.length == 2)
        {
            if(values[0] == null || values[1] == null) 
                throw new IllegalArgumentException("Pair cannot contain null values");
            first = values[0];
            second = values[1];
        }
        else
            throw new IllegalArgumentException("Pair may only be created from an " +
            		"array of 2 elements");
    }

    /**
     * Returns the first element.
     */
    public T getFirst() 
    {
        return first;
    }
    
    /**
     * Returns the second element.
     */
    public T getSecond() 
    {
        return second;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals( Object o ) {
        if (o == this)
            return true;

        if (o instanceof Pair) {
            Pair otherPair = (Pair) o;
            Object otherFirst = otherPair.getFirst();
            Object otherSecond = otherPair.getSecond();
            return 
            	(this.first  == otherFirst  || 
            			(this.first != null  && this.first.equals(otherFirst)))   
            			&&
                (this.second == otherSecond || 
                		(this.second != null && this.second.equals(otherSecond)));
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() 
    {
    	int hashCode = 1;
	    hashCode = 31*hashCode + (first==null ? 0 : first.hashCode());
	    hashCode = 31*hashCode + (second==null ? 0 : second.hashCode());
    	return hashCode;
    }
    
    @Override
    public String toString()
    {
        return "<" + first.toString() + ", " + second.toString() + ">";
    }

    public boolean add(T o) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
    }

    public void clear() {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
    }

    public boolean contains(Object o) {
        return (first == o || first.equals(o) || second == o || second.equals(o));
    }

    public boolean containsAll(Collection<?> c) {
        if (c.size() > 2)
            return false;
        Iterator<?> iter = c.iterator();
        Object c_first = iter.next();
        Object c_second = iter.next();
        return this.contains(c_first) && this.contains(c_second);
    }

    public boolean isEmpty() {
        return false;
    }

    public Iterator<T> iterator() {
        return new PairIterator();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Pairs cannot be mutated");
    }

    public int size() {
        return 2;
    }

    public Object[] toArray() {
        Object[] to_return = new Object[2];
        to_return[0] = first;
        to_return[1] = second;
        return to_return;
    }

    @SuppressWarnings("unchecked")
    public <S> S[] toArray(S[] a) {
        S[] to_return = a;
        if (a.length < 2){
        	// TODO check
        	//to_return = (S[])java.lang.reflect.Array.newInstance(type, 2);
        	Object[] arr = new Object[2];
        	to_return = (S[]) arr;
        }
        to_return[0] = (S)first;
        to_return[1] = (S)second;
        
        if (to_return.length > 2)
            to_return[2] = null;
        return to_return;
    }
    
    private class PairIterator implements Iterator<T>
    {
        int position;
        
        private PairIterator()
        {
            position = 0;
        }

        public boolean hasNext()
        {
            return position < 2;
        }

        public T next()
        {
            position++;
            if (position == 1)
                return first;
            else if (position == 2)
                return second;
            else
                return null;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Pairs cannot be mutated");
        }
    }
}



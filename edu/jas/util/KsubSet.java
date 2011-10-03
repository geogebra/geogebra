/*
 * $Id: KsubSet.java 3067 2010-04-10 11:07:46Z kredel $
 */

package edu.jas.util;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * K-Subset with iterator.
 * @author Heinz Kredel
 */
public class KsubSet<E> implements Iterable<List<E>> {


    /** 
     * data structure.
     */
    public final List<E> set;
    public final int k;


    /**
     * KsubSet constructor.
     * @param set generating set.
     * @param k size of subsets.
     */
    public KsubSet(List<E> set, int k) {
        if ( set == null ) {
            throw new IllegalArgumentException("null set not allowed");
        }
        this.set = set;
        if ( k < 0 || k > set.size() ) {
            throw new IllegalArgumentException("k out of range");
        }
        this.k = k;
    }


    /**
     * Get an iterator over subsets.
     * @return an iterator.
     */
    public Iterator<List<E>> iterator() {
        if ( k == 0 ) {
            return new ZeroSubSetIterator<E>(set);
        }
        if ( k == 1 ) {
            return new OneSubSetIterator<E>(set);
        }
        return new KsubSetIterator<E>(set,k);
    }

}


/**
 * Power set iterator.
 * @author Heinz Kredel
 */
class KsubSetIterator<E> implements Iterator<List<E>> {


    /** 
     * data structure.
     */
    public final List<E> set;
    public final int k;
    final List<E> rest;
    private E current;
    private Iterator<List<E>> recIter;
    private Iterator<E> iter;


    /**
     * KsubSetIterator constructor.
     * @param set generating set.
     * @param k subset size.
     */
    public KsubSetIterator(List<E> set, int k) {
        if ( set == null || set.size() == 0 ) {
            throw new IllegalArgumentException("null or empty set not allowed");
        }
        this.set = set;
        if ( k < 2 || k > set.size() ) {
            throw new IllegalArgumentException("k out of range");
        }
        this.k = k;
        iter = set.iterator();
        current = iter.next();
        //System.out.println("current = " + current);
        rest = new LinkedList<E>(set);
        rest.remove(0);
        //System.out.println("rest = " + rest);
        if ( k == 2 ) {
           recIter = new OneSubSetIterator<E>( rest ); 
        } else {
           recIter = new KsubSetIterator<E>( rest, k-1 ); 
        }
    }


    /** 
     * Test for availability of a next subset.
     * @return true if the iteration has more subsets, else false.
     */
    public boolean hasNext() {
        return recIter.hasNext() || ( iter.hasNext() && rest.size() >= k ) ;
    }


    /** 
     * Get next subset.
     * @return next subset.
     */
    public List<E> next() {
        if ( recIter.hasNext() ) {
            List<E> next = new LinkedList<E>( recIter.next() );
            next.add( 0, current );
            return next;
        }
        if ( iter.hasNext() ) {
            current = iter.next();
            //System.out.println("current = " + current);
            rest.remove(0);
            //System.out.println("rest = " + rest);
            if ( rest.size() < k-1 ) {
                throw new RuntimeException("invalid call of next()");
            }
            if ( k == 2 ) {
                recIter = new OneSubSetIterator<E>( rest ); 
            } else {
                recIter = new KsubSetIterator<E>( rest, k-1 ); 
            }
            return this.next(); // retry
        } else {
            throw new RuntimeException("invalid call of next()");
        }
    }


    /** 
     * Remove the last subset returned from underlying set if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove subsets");
    }

}


/**
 * One-subset iterator.
 * @author Heinz Kredel
 */
class OneSubSetIterator<E> implements Iterator<List<E>> {


    /** 
     * data structure.
     */
    public final List<E> set;
    private Iterator<E> iter;


    /**
     * OneSubSetIterator constructor.
     * @param set generating set.
     */
    public OneSubSetIterator(List<E> set) {
        this.set = set;
        if ( set == null || set.size() == 0 ) {
            iter = null;
            return;
        }
        iter = set.iterator();
    }


    /** 
     * Test for availability of a next subset.
     * @return true if the iteration has more subsets, else false.
     */
    public boolean hasNext() {
        if ( iter == null ) {
            return false;
        }
        return iter.hasNext();
    }


    /** 
     * Get next subset.
     * @return next subset.
     */
    public List<E> next() {
        List<E> next = new LinkedList<E>();
        next.add( iter.next() );
        return next;
    }


    /** 
     * Remove the last subset returned from underlying set if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove subsets");
    }

}


/**
 * Zero-subset iterator.
 * @author Heinz Kredel
 */
class ZeroSubSetIterator<E> implements Iterator<List<E>> {


    /** 
     * data structure.
     */
    private boolean hasNext;


    /**
     * ZeroSubSetIterator constructor.
     * @param set generating set (ignored).
     */
    public ZeroSubSetIterator(List<E> set) {
        hasNext = true;
    }


    /** 
     * Test for availability of a next subset.
     * @return true if the iteration has more subsets, else false.
     */
    public boolean hasNext() {
        return hasNext;
    }


    /** 
     * Get next subset.
     * @return next subset.
     */
    public List<E> next() {
        List<E> next = new LinkedList<E>();
        hasNext = false;
        return next;
    }


    /** 
     * Remove the last subset returned from underlying set if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove subsets");
    }

}

/*
 * $Id: PowerSet.java 2477 2009-03-08 12:18:23Z kredel $
 */

package edu.jas.util;

//import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Power set with iterator.
 * @author Heinz Kredel
 */
public class PowerSet<E> implements Iterable<List<E>> {


    /** 
     * data structure.
     */
    public final List<E> set;


    /**
     * PowerSet constructor.
     * @param set generating set.
     */
    public PowerSet(List<E> set) {
        this.set = set;
    }


    /**
     * get an iterator over subsets.
     * @return an iterator.
     */
    public Iterator<List<E>> iterator() {
        return new PowerSetIterator<E>(set);
    }

}


/**
 * Power set iterator.
 * @author Heinz Kredel
 */
class PowerSetIterator<E> implements Iterator<List<E>> {


    /** 
     * data structure.
     */
    public final List<E> set;
    final List<E> rest;
    final E current;
    private PowerSetIterator<E> recIter;
    enum Mode { copy, extend, first, done };
    Mode mode; 


    /**
     * PowerSetIterator constructor.
     * @param set generating set.
     */
    public PowerSetIterator(List<E> set) {
        this.set = set;
        if ( set == null || set.size() == 0 ) {
            current = null;
            recIter = null;
            rest = null;
            mode = Mode.first;
            return;
        }
        mode = Mode.copy; 
        current = set.get(0);
        rest = new LinkedList<E>(set);
        rest.remove(0);
        recIter = new PowerSetIterator<E>( rest );
    }


    /** 
     * Test for availability of a next subset.
     * @return true if the iteration has more subsets, else false.
     */
    public boolean hasNext() {
        if ( mode == Mode.first ) {
            return true;
        }
        if ( recIter == null ) {
            return false;
        }
        return recIter.hasNext() || mode == Mode.copy;
    }


    /** 
     * Get next subset.
     * @return next subset.
     */
    public List<E> next() {
        if ( mode == Mode.first ) {
            mode = Mode.done;
            List<E> first = new LinkedList<E>();
            return first;
        }
        if ( mode == Mode.extend ) {
           if ( recIter.hasNext() ) {
               List<E> next = new LinkedList<E>( recIter.next() );
               next.add( current );
               return next;
           }
        } 
        if ( mode == Mode.copy ) {
           if ( recIter.hasNext() ) {
                return recIter.next();
           } else {
                mode = Mode.extend;
                recIter = new PowerSetIterator<E>( rest );
                return this.next();
           }
        }
        return null;
    }


    /** 
     * Remove the last subset returned from underlying set if allowed.
     */
    public void remove() {
        throw new UnsupportedOperationException("cannnot remove subsets");
    }

}

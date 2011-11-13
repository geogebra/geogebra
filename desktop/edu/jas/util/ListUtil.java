/*
 * $Id: ListUtil.java 3049 2010-03-20 15:08:52Z kredel $
 */

package edu.jas.util;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import edu.jas.structure.Element;
import edu.jas.structure.UnaryFunctor;


/**
 * List utilities.
 * For example map functor on list elements.
 * @author Heinz Kredel
 */

public class ListUtil {


    private static final Logger logger = Logger.getLogger(ListUtil.class);
    // private static boolean debug = logger.isDebugEnabled();


    /**
     * Map a unary function to the list.
     * @param f evaluation functor.
     * @return new list elements f(list(i)).
     */
    public static <C extends Element<C>,D extends Element<D>>
           List<D> map(List<C> list, UnaryFunctor<C,D> f) {
        if ( list == null ) {
            return (List<D>)null;
        }
        List<D> nl;
        if ( list instanceof ArrayList ) {
            nl = new ArrayList<D>( list.size() );
        } else if ( list instanceof LinkedList ) {
            nl = new LinkedList<D>();
        } else {
            throw new RuntimeException("list type not implemented");
        }
        for ( C c : list ) {
            D n = f.eval( c );
            nl.add( n );
        }
        return nl;
    }


    /**
     * Tuple from lists.
     * @param A list of lists.
     * @return new list with tuples (a_1,...,an) with ai in Ai, i=0,...,length(A)-1.
     */
    public static <C> List<List<C>> tupleFromList(List<List<C>> A) {
        if ( A == null ) {
            return null;
        }
        List<List<C>> T = new ArrayList<List<C>>( A.size() );
        if ( A.size() == 0 ) {
            return T;
        }
        if ( A.size() == 1 ) {
            List<C> Ap = A.get(0);
            for ( C a : Ap ) {
               List<C> Tp = new ArrayList<C>(1);
               Tp.add(a);
               T.add( Tp );
            }
            return T;
        }
        List<List<C>> Ap = new ArrayList<List<C>>( A );
        List<C> f = Ap.remove( 0 );
        List<List<C>> Tp = tupleFromList( Ap );
        //System.out.println("Tp = " + Tp);
        for ( C a : f ) {
            for ( List<C> tp : Tp ) {
                List<C> ts = new ArrayList<C>();
                ts.add(a);
                ts.addAll(tp);
                T.add( ts );
            }
        }
        return T;
    }
}

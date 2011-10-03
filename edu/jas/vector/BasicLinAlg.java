/*
 * $Id: BasicLinAlg.java 1710 2008-02-24 17:35:31Z kredel $
 */

package edu.jas.vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Basic linear algebra methods.
 * Implements Basic linear algebra computations and tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class BasicLinAlg<C extends RingElem<C>> {

    private static final Logger logger = Logger.getLogger(BasicLinAlg.class);
    //private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public BasicLinAlg() {
    }


    /**
     * Scalar product of vectors of polynomials.
     * @param r a polynomial list.
     * @param F a polynomial list.
     * @return the scalar product of r and F.
     */

    public GenPolynomial<C> 
           scalarProduct(List<GenPolynomial<C>> r, 
                         List<GenPolynomial<C>> F) {  
        GenPolynomial<C> sp = null;
        Iterator<GenPolynomial<C>> it = r.iterator();
        Iterator<GenPolynomial<C>> jt = F.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenPolynomial<C> pi = it.next();
            GenPolynomial<C> pj = jt.next();
            if ( pi == null || pj == null ) {
               continue;
            }
            if ( sp == null ) {
                sp = pi.multiply(pj);
            } else {
                sp = sp.sum( pi.multiply(pj) );
            }
        }
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }


    /**
     * product of vector and matrix of polynomials.
     * @param r a polynomial list.
     * @param F a polynomial matrix.
     * @return the scalar product of r and F.
     */

    public List<GenPolynomial<C>> 
           scalarProduct(List<GenPolynomial<C>> r, ModuleList<C> F) {  
        List<GenPolynomial<C>> ZZ = null;
        Iterator<GenPolynomial<C>> it = r.iterator();
        Iterator<List<GenPolynomial<C>>> jt = F.list.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenPolynomial<C> pi = it.next();
            List<GenPolynomial<C>> vj = jt.next();
            List<GenPolynomial<C>> Z = scalarProduct( pi, vj );
            //System.out.println("pi" + pi);
            //System.out.println("vj" + vj);
            // System.out.println("scalarProduct" + Z);
            if ( ZZ == null ) {
                ZZ = Z;
            } else {
                ZZ = vectorAdd(ZZ,Z);
            }
        }
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("scalarProduct wrong sizes");
        }
        if ( logger.isDebugEnabled() ) {
            logger.debug("scalarProduct" + ZZ);
        }
        return ZZ;
    }


    /**
     * Addition of vectors of polynomials.
     * @param a a polynomial list.
     * @param b a polynomial list.
     * @return a+b, the vector sum of a and b.
     */

    public List<GenPolynomial<C>> 
           vectorAdd(List<GenPolynomial<C>> a, List<GenPolynomial<C>> b) {  
        if ( a == null ) {
            return b;
        }
        if ( b == null ) {
            return a;
        }
        List<GenPolynomial<C>> V = new ArrayList<GenPolynomial<C>>( a.size() );
        Iterator<GenPolynomial<C>> it = a.iterator();
        Iterator<GenPolynomial<C>> jt = b.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenPolynomial<C> pi = it.next();
            GenPolynomial<C> pj = jt.next();
            GenPolynomial<C> p = pi.sum( pj );
            V.add( p );
        }
        //System.out.println("vectorAdd" + V);
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("vectorAdd wrong sizes");
        }
        return V;
    }


    /**
     * test vector of zero polynomials.
     * @param a a polynomial list.
     * @return true, if all polynomial in a are zero, else false.
     */
    public boolean 
           isZero(List<GenPolynomial<C>> a) {  
        if ( a == null ) {
            return true;
        }
        for ( GenPolynomial<C> pi : a ) {
            if ( pi == null ) {
                continue;
            }
            if ( ! pi.isZERO() ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Scalar product of polynomial with vector of polynomials.
     * @param p a polynomial.
     * @param F a polynomial list.
     * @return the scalar product of p and F.
     */

    public List<GenPolynomial<C>> 
           scalarProduct(GenPolynomial<C> p, List<GenPolynomial<C>> F) {  
        List<GenPolynomial<C>> V = new ArrayList<GenPolynomial<C>>( F.size() );
        for ( GenPolynomial<C> pi : F ) {
            if ( p != null ) {
               pi = p.multiply( pi );
            } else {
               pi = null;
            }
            V.add( pi );
        }
        return V;
    }


    /**
     * Scalar product of vector of polynomials with polynomial.
     * @param F a polynomial list.
     * @param p a polynomial.
     * @return the scalar product of F and p.
     */

    public List<GenPolynomial<C>> 
        scalarProduct(List<GenPolynomial<C>> F, GenPolynomial<C> p) {  
        List<GenPolynomial<C>> V = new ArrayList<GenPolynomial<C>>( F.size() );
        for ( GenPolynomial<C> pi : F ) {
            if ( pi != null ) {
               pi = pi.multiply( p );
            }
            V.add( pi );
        }
        return V;
    }

}

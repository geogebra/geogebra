/*
 * $Id: SolvableBasicLinAlg.java 1710 2008-02-24 17:35:31Z kredel $
 */

package edu.jas.vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;


/**
 * Basic linear algebra for solvable polynomials.
 * Implements basic linear algebra computations and tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableBasicLinAlg<C extends RingElem<C>> {

    private static final Logger logger = Logger.getLogger(SolvableBasicLinAlg.class);
    //private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public SolvableBasicLinAlg() {
    }


    /**
     * Scalar product of vectors of polynomials.
     * @param r a polynomial list.
     * @param F a polynomial list.
     * @return the left scalar product of r and F.
     */
    public GenSolvablePolynomial<C> 
           leftScalarProduct(List<GenSolvablePolynomial<C>> r, 
                             List<GenSolvablePolynomial<C>> F) {  
        GenSolvablePolynomial<C> sp = null;
        Iterator<GenSolvablePolynomial<C>> it = r.iterator();
        Iterator<GenSolvablePolynomial<C>> jt = F.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            GenSolvablePolynomial<C> pj = jt.next();
            if ( pi == null || pj == null ) {
               continue;
            }
            if ( sp == null ) {
                sp = pi.multiply(pj);
            } else {
                sp = (GenSolvablePolynomial<C>)sp.sum( pi.multiply(pj) );
            }
        }
        if ( it.hasNext() || jt.hasNext() ) {
            logger.error("scalarProduct wrong sizes");
        }
        return sp;
    }


    /**
     * Product of vector and matrix of polynomials.
     * @param r a polynomial list.
     * @param F a polynomial list.
     * @return the left scalar product of r and F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftScalarProduct(List<GenSolvablePolynomial<C>> r, 
                             ModuleList<C> F) {  
        List<GenSolvablePolynomial<C>> ZZ = null;
        Iterator<GenSolvablePolynomial<C>> it = r.iterator();
        Iterator<List<GenPolynomial<C>>> jt = F.list.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            List<GenSolvablePolynomial<C>> vj = (List/*<GenSolvablePolynomial<C>>*/)jt.next();
            List<GenSolvablePolynomial<C>> Z = leftScalarProduct( pi, vj );
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
     * Product of vector and matrix of polynomials.
     * @param r a polynomial list.
     * @param F a polynomial list.
     * @return the right scalar product of r and F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightScalarProduct(List<GenSolvablePolynomial<C>> r, 
                              ModuleList<C> F) {  
        List<GenSolvablePolynomial<C>> ZZ = null;
        Iterator<GenSolvablePolynomial<C>> it = r.iterator();
        Iterator<List<GenPolynomial<C>>> jt = F.list.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            List<GenSolvablePolynomial<C>> vj = (List/*<GenSolvablePolynomial<C>>*/)jt.next();
            List<GenSolvablePolynomial<C>> Z = leftScalarProduct( vj, pi ); // order
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
    public List<GenSolvablePolynomial<C>>
           vectorAdd(List<GenSolvablePolynomial<C>> a, 
                     List<GenSolvablePolynomial<C>> b) {  
        if ( a == null ) {
            return b;
        }
        if ( b == null ) {
            return a;
        }
        List<GenSolvablePolynomial<C>> V 
            = new ArrayList<GenSolvablePolynomial<C>>( a.size() );
        Iterator<GenSolvablePolynomial<C>> it = a.iterator();
        Iterator<GenSolvablePolynomial<C>> jt = b.iterator();
        while ( it.hasNext() && jt.hasNext() ) {
            GenSolvablePolynomial<C> pi = it.next();
            GenSolvablePolynomial<C> pj = jt.next();
            GenSolvablePolynomial<C> p = (GenSolvablePolynomial<C>)pi.sum( pj );
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
           isZero(List<GenSolvablePolynomial<C>> a) {  
        if ( a == null ) {
            return true;
        }
        for ( GenSolvablePolynomial<C> pi : a ) {
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
     * @return the left scalar product of p and F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftScalarProduct(GenSolvablePolynomial<C> p, 
                             List<GenSolvablePolynomial<C>> F) {  
        List<GenSolvablePolynomial<C>> V 
            = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        for ( GenSolvablePolynomial<C> pi : F ) {
            pi = p.multiply( pi );
            V.add( pi );
        }
        return V;
    }


    /**
     * Scalar product of vector of polynomials with polynomial.
     * @param F a polynomial list.
     * @param p a polynomial.
     * @return the left scalar product of F and p.
     */
    public List<GenSolvablePolynomial<C>> 
        leftScalarProduct(List<GenSolvablePolynomial<C>> F,
                          GenSolvablePolynomial<C> p) {  
        List<GenSolvablePolynomial<C>> V 
            = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        for ( GenSolvablePolynomial<C> pi : F ) {
            if ( pi != null ) {
               pi = pi.multiply( p );
            }
            V.add( pi );
        }
        return V;
    }

}

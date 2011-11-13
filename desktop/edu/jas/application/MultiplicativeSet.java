/*
 * $Id: MultiplicativeSet.java 2824 2009-09-23 18:02:32Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.ExpVector;

import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Multiplicative set of polynomials.
 * a, b in M implies a*b in M, 1 in M.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class MultiplicativeSet<C extends GcdRingElem<C>> implements Serializable {


    private static final Logger logger = Logger.getLogger(MultiplicativeSet.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * Data structure.
     */
    public final List<GenPolynomial<C>> mset;


    /**
     * Polynomial ring factory.
     */
    public final GenPolynomialRing<C> ring;


    /**
     * MultiplicativeSet constructor. Constructs an empty multiplicative set.
     * @param ring polynomial ring factory for coefficients.
     */
    public MultiplicativeSet(GenPolynomialRing<C> ring) {
        this(ring, new ArrayList<GenPolynomial<C>>());
        if (ring == null) {
            throw new RuntimeException("only for non null rings");
        }
    }


    /**
     * MultiplicativeSet constructor. 
     * @param ring polynomial ring factory for coefficients.
     * @param ms a list of non-zero polynomials.
     */
    protected MultiplicativeSet(GenPolynomialRing<C> ring, List<GenPolynomial<C>> ms) {
        if (ms == null || ring == null) {
            throw new RuntimeException("only for non null parts");
        }
        this.ring = ring;
        mset = ms;
    }


    /**
     * toString.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MultiplicativeSet" + mset;
    }


    /**
     * Equals.
     * @param ob an Object.
     * @return true if this is equal to o, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object ob) {
        MultiplicativeSet<C> c = null;
        try {
            c = (MultiplicativeSet<C>) ob;
        } catch (ClassCastException e) {
            return false;
        }
        if (c == null) {
            return false;
        }
        if (!ring.equals(c.ring)) {
            return false;
        }
        return mset.equals(c.mset);
    }


    /**
     * Hash code for this condition.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = ring.hashCode();
        h = h << 17;
        h += mset.hashCode();
        return h;
    }


    /**
     * Is set.
     * @return true if this is the empty set, else false.
     */
    public boolean isEmpty() {
        return mset.size() == 0;
    }


    /**
     * Test if a polynomial is contained in this multiplicative set.
     * @param c polynomial searched in mset.
     * @return true, if c = prod_{m in mset} m, else false
     */
    public boolean contains(GenPolynomial<C> c) {
        if (c == null || c.isZERO()) { 
            return false;
        }
        if (c.isConstant()) { 
            return true;
        }
        if (mset.size() == 0) {
            return false;
        }
        GenPolynomial<C> d = c;
        for (GenPolynomial<C> n : mset) {
            // System.out.println("mset n = " + n);
            if (n.isONE()) { // do not use 1
                continue;
            }
            GenPolynomial<C> q, r;
            do {
                GenPolynomial<C>[] qr = d.divideAndRemainder(n);
                q = qr[0];
                r = qr[1];
                // System.out.println("q = " + q + ", r = " + r + ", d = " + d +
                // ", n = " + n);
                if (r != null && !r.isZERO()) {
                    continue;
                }
                if (q != null && q.isConstant()) {
                    return true;
                }
                d = q;
            } while (r.isZERO() && !d.isConstant());
        }
        return d.isConstant(); // false
    }


    /**
     * Test if a list of polynomials is contained in multiplicative set.
     * @param L list of polynomials to be searched in mset.
     * @return true, if all c in L are in mset, else false
     */
    public boolean contains(List<GenPolynomial<C>> L) {
        if (L == null || L.size() == 0) {
            return true;
        }
        for (GenPolynomial<C> c : L) {
            if (!contains(c)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Add polynomial to mset. 
     * @param cc polynomial to be added to mset.
     * @return new multiplicative set.
     * <b>Note:</b> must be overridden in sub-classes.
     */
    public MultiplicativeSet<C> add(GenPolynomial<C> cc) {
        if (cc == null || cc.isZERO() || cc.isConstant()) { 
            return this;
        }
        if ( ring.coFac.isField() ) {
            cc = cc.monic();
        }
        List<GenPolynomial<C>> list;
        if (mset.size() == 0) { 
            list =new ArrayList<GenPolynomial<C>>(1);
            list.add(cc);
            return new MultiplicativeSet<C>(ring,list);
        }
        GenPolynomial<C> c = removeFactors(cc);
        if ( c.isConstant() ) { 
            logger.info("skipped unit or constant = " + c);
            return this;
        }
        if ( ring.coFac.isField() ) {
            c = c.monic();
        }
        if (mset.size() == 0) {
            logger.info("added to empty mset = " + c);
        } else {
            logger.info("added to mset = " + c);
        }
        list = new ArrayList<GenPolynomial<C>>(mset);
        list.add(c);
        return new MultiplicativeSet<C>(ring,list);
    }


    /**
     * Replace polynomial list of mset. 
     * @param L polynomial list to replace mset.
     * @return new multiplicative set.
     * <b>Note:</b> must be overridden in sub-classes.
     */
    public MultiplicativeSet<C> replace(List<GenPolynomial<C>> L) {
        MultiplicativeSet<C> ms = new MultiplicativeSet<C>(ring);
        if (L == null || L.size() == 0) { 
            return ms;
        }
        for ( GenPolynomial<C> p : L ) {
            ms = ms.add(p);
        }
        return ms;
    }


    /**
     * Remove factors by mset factors division. 
     * @param cc polynomial to be removed factors from mset.
     * @return quotient polynomial.
     */
    public GenPolynomial<C> removeFactors(GenPolynomial<C> cc) {
        if (cc == null || cc.isZERO() || cc.isConstant()) { 
            return cc;
        }
        if (mset.size() == 0) { 
            return cc;
        }
        GenPolynomial<C> c = cc;
        for (GenPolynomial<C> n : mset) {
            if (n.isConstant()) { // do not use 1, should not be in mset
                continue;
            }
            GenPolynomial<C> q, r;
            do {
                GenPolynomial<C>[] qr = c.divideAndRemainder(n);
                q = qr[0];
                r = qr[1];
                if (r != null && !r.isZERO()) {
                    continue;
                }
                if (q != null && q.isConstant()) {
                    return q;
                }
                c = q;
            } while (r.isZERO() && !c.isConstant());
        }
        return c;
    }


    /**
     * Remove factors by mset factors division. 
     * @param L list of polynomial to be removed factors from mset.
     * @return quotient polynomial list.
     */
    public List<GenPolynomial<C>> removeFactors(List<GenPolynomial<C>> L) {
        if (L == null || L.size() == 0) { 
            return L;
        }
        if (mset.size() == 0) {
            return L;
        }
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>(L.size());
        for (GenPolynomial<C> p : L) {
            p = removeFactors(p);
            // nono, really: if ( !p.isConstant() ) {
            M.add(p);
        }
        return M;
    }

}

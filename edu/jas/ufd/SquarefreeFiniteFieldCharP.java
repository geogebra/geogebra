/*
 * $Id: SquarefreeFiniteFieldCharP.java 3198 2010-06-26 20:10:41Z kredel $
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for finite coefficient fields of characteristic p.
 * @author Heinz Kredel
 */

public class SquarefreeFiniteFieldCharP<C extends GcdRingElem<C>> extends SquarefreeFieldCharP<C> {


    private static final Logger logger = Logger.getLogger(SquarefreeFiniteFieldCharP.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public SquarefreeFiniteFieldCharP(RingFactory<C> fac) {
        super(fac);
        // isFinite() predicate now present
        if ( !fac.isFinite() ) {
            throw new IllegalArgumentException("fac must be finite"); 
        }
    }


    /* --------- char-th roots --------------------- */

    /**
     * Characteristics root of a coefficient. <b>Note:</b> not needed at the
     * moment.
     * @param p coefficient.
     * @return [p -&gt; k] if exists k with e=k*charactristic(c) and c = p**e,
     *         else null.
     */
    public SortedMap<C, Long> rootCharacteristic(C p) {
        if (p == null) {
            throw new RuntimeException(this.getClass().getName() + " p == null");
        }
        // already checked in constructor:
        //java.math.BigInteger c = p.factory().characteristic();
        //if ( c.signum() == 0 ) {
        //    return null;
        //}
        SortedMap<C, Long> root = new TreeMap<C, Long>();
        if (p.isZERO()) {
            return root;
        }
        // true for finite fields:
        root.put(p, 1L);
        return root;
    }


    /**
     * Characteristics root of a coefficient.
     * @param c coefficient.
     * @return r with r**p == c, if such an r exists, else null.
     */
    public C coeffRootCharacteristic(C c) {
        if (c == null || c.isZERO()) {
            return c;
        }
        C r = c;
        if (aCoFac == null && qCoFac == null) {
            // case ModInteger: c**p == c
            return r;
        }
        if (aCoFac != null) {
            // case AlgebraicNumber<ModInteger>: r = c**(p**(d-1)), r**p == c
            long d = aCoFac.totalExtensionDegree();
            //System.out.println("d = " + d);
            if (d <= 1) {
                return r;
            }
            BigInteger p = new BigInteger(aCoFac.characteristic());
            BigInteger q = Power.<BigInteger> positivePower(p, d - 1);
            //System.out.println("p**(d-1) = " + q);
            r = Power.<C> positivePower(r, q.getVal());
            //System.out.println("r**q = " + r);
            return r;
        }
        if (qCoFac != null) {
            throw new RuntimeException("case QuotientRing not yet implemented");
        }
        return r;
    }


    /**
     * Characteristics root of a polynomial. <b>Note:</b> call only in
     * recursion.
     * @param P polynomial.
     * @return [p -&gt; k] if exists k with e=k*charactristic(P) and P = p**e,
     *         else null.
     */
    public SortedMap<GenPolynomial<C>, Long> rootCharacteristic(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if (c.signum() == 0) {
            return null;
        }
        SortedMap<GenPolynomial<C>, Long> root = new TreeMap<GenPolynomial<C>, Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P, 1L);
            return root;
        }
        SortedMap<GenPolynomial<C>, Long> sf = squarefreeFactors(P);
        if (logger.isInfoEnabled()) {
            logger.info("sf = " + sf);
        }
        // better: test if sf.size() == 1 // not ok
        Long k = null;
        for (GenPolynomial<C> p : sf.keySet()) {
            if (p.isConstant()) {
                //System.out.println("p,const = " + p);
                continue;
            }
            Long e = sf.get(p);
            java.math.BigInteger E = new java.math.BigInteger(e.toString());
            java.math.BigInteger r = E.remainder(c);
            if (!r.equals(java.math.BigInteger.ZERO)) {
                //System.out.println("r = " + r);
                return null;
            }
            if (k == null) {
                k = e;
            } else if (k.compareTo(e) >= 0) {
                k = e;
            }
        }
        // now c divides all exponents
        Long cl = c.longValue();
        GenPolynomial<C> rp = P.ring.getONE();
        for (GenPolynomial<C> q : sf.keySet()) {
            Long e = sf.get(q);
            if (q.isConstant()) { // ensure p-th root
                C qc = q.leadingBaseCoefficient();
                //System.out.println("qc,const = " + qc + ", e = " + e);
                if (e > 1L) {
                    qc = Power.<C> positivePower(qc, e);
                    e = 1L;
                }
                C qr = coeffRootCharacteristic(qc);
                //System.out.println("qr,const = " + qr);
                q = P.ring.getONE().multiply(qr);
                root.put(q, 1L);
                continue;
            }
            if (e > k) {
                long ep = e / cl;
                q = Power.<GenPolynomial<C>> positivePower(q, ep);
            }
            rp = rp.multiply(q);
        }
        if (k != null) {
            k = k / cl;
            root.put(rp, k);
        }
        //System.out.println("sf,root = " + root);
        return root;
    }


    /**
     * GenPolynomial char-th root univariate polynomial. 
     * Base coefficient type must be
     * finite field, that is ModInteger or AlgebraicNumber&lt;ModInteger&gt;
     * etc.
     * @param P GenPolynomial.
     * @return char-th_rootOf(P), or null if no char-th root.
     */
    @Override
    public GenPolynomial<C> baseRootCharacteristic(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<C> d = pfac.getZERO().clone();
        for (Monomial<C> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            ExpVector e = ExpVector.create(1, 0, fl);
            // for m.c exists a char-th root, since finite field
            C r = coeffRootCharacteristic(m.c);
            d.doPutToMap(e, r);
        }
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial with polynomial coefficients.
     * @param P recursive univariate GenPolynomial.
     * @return char-th_rootOf(P), or null if P is no char-th root.
     */
    @Override
    public GenPolynomial<GenPolynomial<C>> recursiveUnivariateRootCharacteristic(
            GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate polynomials");
        }
        RingFactory<GenPolynomial<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<GenPolynomial<C>> d = pfac.getZERO().clone();
        for (Monomial<GenPolynomial<C>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<GenPolynomial<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
               logger.info("sm,rec = " + sm);
            }
            GenPolynomial<C> r = rf.getONE();
            for (GenPolynomial<C> rp : sm.keySet()) {
                long gl = sm.get(rp);
                if (gl > 1) {
                    rp = Power.<GenPolynomial<C>> positivePower(rp, gl);
                }
                r = r.multiply(rp);
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            //System.out.println("put-root r = " + r + ", e = " + e);
            d.doPutToMap(e, r);
        }
        return d;
    }

}

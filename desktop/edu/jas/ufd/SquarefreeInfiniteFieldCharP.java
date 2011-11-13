/*
 * $Id: SquarefreeInfiniteFieldCharP.java 3208 2010-07-04 19:08:44Z kredel $
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for infinite coefficient fields of characteristic p.
 * @author Heinz Kredel
 */

public class SquarefreeInfiniteFieldCharP<C extends GcdRingElem<C>> extends SquarefreeFieldCharP<Quotient<C>> {


    private static final Logger logger = Logger.getLogger(SquarefreeInfiniteFieldCharP.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * GCD engine for infinite ring of characteristic p base coefficients.
     */
    protected final SquarefreeAbstract<C> rengine;



    /**
     * Constructor.
     */
    public SquarefreeInfiniteFieldCharP(RingFactory<Quotient<C>> fac) {
        super(fac);
        // isFinite() predicate now present
        if ( fac.isFinite() ) {
            throw new IllegalArgumentException("fac must be in-finite"); 
        }
        QuotientRing<C> qfac = (QuotientRing<C>) fac;
        GenPolynomialRing<C> rfac = qfac.ring;
        rengine = SquarefreeFactory.<C>getImplementation(rfac);
        //rengine = new SquarefreeFiniteFieldCharP<C>(rfac.coFac);
        //rengine = new SquarefreeInfiniteRingCharP<C>( rfac.coFac );
    }


    /* --------- quotient char-th roots --------------------- */


    /**
     * Squarefree factors of a Quotient.
     * @param P Quotient.
     * @return [p_1 -&gt; e_1,...,p_k - &gt; e_k] with P = prod_{i=1, ..., k}
     *         p_i**e_k.
     */
    public SortedMap<Quotient<C>, Long> squarefreeFactors(Quotient<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<Quotient<C>, Long> factors = new TreeMap<Quotient<C>, Long>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P, 1L);
            return factors;
        }
        GenPolynomial<C> num = P.num;
        GenPolynomial<C> den = P.den;
        QuotientRing<C> pfac = P.ring;
        GenPolynomial<C> one = pfac.ring.getONE();
        if (!num.isONE()) {
            SortedMap<GenPolynomial<C>, Long> nfac = rengine.squarefreeFactors(num);
            //System.out.println("nfac = " + nfac);
            for (GenPolynomial<C> nfp : nfac.keySet()) {
                Quotient<C> nf = new Quotient<C>(pfac, nfp);
                factors.put(nf, nfac.get(nfp));
            }
        }
        if (den.isONE()) {
            if (factors.size() == 0) {
                factors.put(P, 1L);
            }
            return factors;
        }
        SortedMap<GenPolynomial<C>, Long> dfac = rengine.squarefreeFactors(den);
        //System.out.println("dfac = " + dfac);
        for (GenPolynomial<C> dfp : dfac.keySet()) {
            Quotient<C> df = new Quotient<C>(pfac, one, dfp);
            factors.put(df, dfac.get(dfp));
        }
        if (factors.size() == 0) {
            factors.put(P, 1L);
        }
        return factors;
    }


    /**
     * Characteristics root of a Quotient.
     * @param P Quotient.
     * @return [p -&gt; k] if exists k with e=charactristic(P)*k and P = p**e,
     *         else null.
     */
    public SortedMap<Quotient<C>, Long> rootCharacteristic(Quotient<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if (c.signum() == 0) {
            return null;
        }
        SortedMap<Quotient<C>, Long> root = new TreeMap<Quotient<C>, Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P, 1L);
            return root;
        }
        SortedMap<Quotient<C>, Long> sf = squarefreeFactors(P);
        if (sf == null || sf.size() == 0) {
            return null;
        }
        if ( logger.isInfoEnabled() ) {
            logger.info("sf,quot = " + sf);
        }
        // better: test if sf.size() == 2 // no, since num and den factors 
        Long k = null;
        Long cl = c.longValue();
        for (Quotient<C> p : sf.keySet()) {
            //System.out.println("p = " + p);
            if (p.isConstant()) { // todo: check for non-constants in coefficients
                continue;
            }
            Long e = sf.get(p);
            long E = e.longValue();
            long r = E % cl;
            if (r != 0) {
                //System.out.println("r = " + r);
                return null;
            }
            if (k == null) {
                k = e;
            } else if (k >= e) {
                k = e;
            }
        }
        if (k == null) {
            k = 1L; //return null;
        }
        // now c divides all exponents of non constant elements
        Quotient<C> rp = P.ring.getONE();
        for (Quotient<C> q : sf.keySet()) {
            Long e = sf.get(q);
            //System.out.println("q = " + q + ", e = " + e);
            if (e >= k) {
                e = e / cl;
                //q = Power.<Quotient<C>> positivePower(q, e);
                root.put(q, e);
            } else { // constant case
                root.put(q, e);
            }
        }
        //System.out.println("root = " + root);
        return root;
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P univariate GenPolynomial with Quotient coefficients.
     * @return char-th_rootOf(P), or null, if P is no char-th root.
     */
    public GenPolynomial<Quotient<C>> rootCharacteristic(GenPolynomial<Quotient<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // go to recursion
            GenPolynomialRing<Quotient<C>> cfac = pfac.contract(1);
            GenPolynomialRing<GenPolynomial<Quotient<C>>> rfac = new GenPolynomialRing<GenPolynomial<Quotient<C>>>(
                    cfac, 1);
            GenPolynomial<GenPolynomial<Quotient<C>>> Pr = PolyUtil.<Quotient<C>> recursive(rfac, P);
            GenPolynomial<GenPolynomial<Quotient<C>>> Prc = recursiveUnivariateRootCharacteristic(Pr);
            if (Prc == null) {
                return null;
            }
            GenPolynomial<Quotient<C>> D = PolyUtil.<Quotient<C>> distribute(pfac, Prc);
            return D;
        }
        RingFactory<Quotient<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<Quotient<C>> d = pfac.getZERO().clone();
        for (Monomial<Quotient<C>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<Quotient<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("sm,root = " + sm);
            }
            Quotient<C> r = rf.getONE();
            for (Quotient<C> rp : sm.keySet()) {
                long gl = sm.get(rp);
                if (gl > 1) {
                    rp = Power.<Quotient<C>> positivePower(rp, gl);
                }
                r = r.multiply(rp);
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        logger.info("sm,root,d = " + d);
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial. 
     * @param P GenPolynomial.
     * @return char-th_rootOf(P).
     */
    @Override
    public GenPolynomial<Quotient<C>> baseRootCharacteristic(GenPolynomial<Quotient<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<Quotient<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate polynomials");
        }
        RingFactory<Quotient<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<Quotient<C>> d = pfac.getZERO().clone();
        for (Monomial<Quotient<C>> m : P) {
            //System.out.println("m = " + m);
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<Quotient<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("sm,base,root = " + sm);
            }
            Quotient<C> r = rf.getONE();
            for (Quotient<C> rp : sm.keySet()) {
                //System.out.println("rp = " + rp);
                long gl = sm.get(rp);
                //System.out.println("gl = " + gl);
                Quotient<C> re = rp;
                if (gl > 1) {
                    re = Power.<Quotient<C>> positivePower(rp, gl);
                }
                //System.out.println("re = " + re);
                r = r.multiply(re); 
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("sm,base,d = " + d);
        }
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial with polynomial coefficients.
     * @param P recursive univariate GenPolynomial.
     * @return char-th_rootOf(P), or null if P is no char-th root.
     */
    @Override
    public GenPolynomial<GenPolynomial<Quotient<C>>> recursiveUnivariateRootCharacteristic(
            GenPolynomial<GenPolynomial<Quotient<C>>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<Quotient<C>>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate recursive polynomials");
        }
        RingFactory<GenPolynomial<Quotient<C>>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<GenPolynomial<Quotient<C>>> d = pfac.getZERO().clone();
        for (Monomial<GenPolynomial<Quotient<C>>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            GenPolynomial<Quotient<C>> r = rootCharacteristic(m.c);
            if (r == null) {
                return null;
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        return d;
    }

}

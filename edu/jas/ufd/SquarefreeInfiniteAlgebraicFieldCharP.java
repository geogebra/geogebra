/*
 * $Id: SquarefreeInfiniteAlgebraicFieldCharP.java 3217 2010-07-06 18:31:44Z kredel $
 */

package edu.jas.ufd;


import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.gb.Reduction;
import edu.jas.gb.ReductionSeq;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.application.Ideal;


/**
 * Squarefree decomposition for algebraic extensions of infinite coefficient fields 
 * of characteristic p &gt; 0.
 * @author Heinz Kredel
 */

public class SquarefreeInfiniteAlgebraicFieldCharP<C extends GcdRingElem<C>> 
             extends SquarefreeFieldCharP<AlgebraicNumber<C>> {


    private static final Logger logger = Logger.getLogger(SquarefreeInfiniteAlgebraicFieldCharP.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * GCD engine for infinite ring of characteristic p base coefficients.
     */
    protected final SquarefreeAbstract<C> rengine;



    /**
     * Constructor.
     */
    public SquarefreeInfiniteAlgebraicFieldCharP(RingFactory<AlgebraicNumber<C>> fac) {
        super(fac);
        // isFinite() predicate now present
        if ( fac.isFinite() ) {
            throw new IllegalArgumentException("fac must be in-finite"); 
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) fac;
        GenPolynomialRing<C> rfac = afac.ring;
        //System.out.println("rfac = " + rfac);
        //System.out.println("rfac = " + rfac.coFac);
        rengine = SquarefreeFactory.<C>getImplementation(rfac);
        //System.out.println("rengine = " + rengine);
    }


    /* --------- algebraic number char-th roots --------------------- */

    /**
     * Squarefree factors of a AlgebraicNumber.
     * @param P AlgebraicNumber.
     * @return [p_1 -&gt; e_1,...,p_k - &gt; e_k] with P = prod_{i=1, ..., k}
     *         p_i**e_k.
     */
    public SortedMap<AlgebraicNumber<C>, Long> squarefreeFactors(AlgebraicNumber<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        SortedMap<AlgebraicNumber<C>, Long> factors = new TreeMap<AlgebraicNumber<C>, Long>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.put(P, 1L);
            return factors;
        }
        GenPolynomial<C> an = P.val;
        AlgebraicNumberRing<C> pfac = P.ring;
        GenPolynomial<C> one = pfac.ring.getONE();
        if (!an.isONE()) {
            //System.out.println("an = " + an);
            //System.out.println("rengine = " + rengine);
            SortedMap<GenPolynomial<C>, Long> nfac = rengine.squarefreeFactors(an);
            //System.out.println("nfac = " + nfac);
            for (GenPolynomial<C> nfp : nfac.keySet()) {
                AlgebraicNumber<C> nf = new AlgebraicNumber<C>(pfac, nfp);
                factors.put(nf, nfac.get(nfp));
            }
        }
        if (factors.size() == 0) {
            factors.put(P, 1L);
        }
        return factors;
    }


    /**
     * Characteristics root of a AlgebraicNumber.
     * @param P AlgebraicNumber.
     * @return [p -&gt; k] if exists k with e=charactristic(P)*k and P = p**e,
     *         else null.
     */
    public SortedMap<AlgebraicNumber<C>, Long> rootCharacteristic(AlgebraicNumber<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        java.math.BigInteger c = P.ring.characteristic();
        if (c.signum() == 0) {
            return null;
        }
        SortedMap<AlgebraicNumber<C>, Long> root = new TreeMap<AlgebraicNumber<C>, Long>();
        if (P.isZERO()) {
            return root;
        }
        if (P.isONE()) {
            root.put(P, 1L);
            return root;
        }
        // generate system of equations
        AlgebraicNumberRing<C> afac = P.ring;
        long deg = afac.modul.degree(0);
        int d = (int)deg;
        String[] vn = GenPolynomialRing.newVars("c",d);
        GenPolynomialRing<AlgebraicNumber<C>> pfac = new GenPolynomialRing<AlgebraicNumber<C>>(afac,d,vn);
        List<GenPolynomial<AlgebraicNumber<C>>> uv = (List<GenPolynomial<AlgebraicNumber<C>>>) pfac.univariateList();
        GenPolynomial<AlgebraicNumber<C>> cp = pfac.getZERO();
        GenPolynomialRing<C> apfac = afac.ring;
        long i = 0;
        for ( GenPolynomial<AlgebraicNumber<C>> pa : uv ) {
            GenPolynomial<C> ca = apfac.univariate(0,i++);
            GenPolynomial<AlgebraicNumber<C>> pb = pa.multiply( new AlgebraicNumber<C>(afac,ca) );
            cp = cp.sum(pb);
        }
        GenPolynomial<AlgebraicNumber<C>> cpp = Power.<GenPolynomial<AlgebraicNumber<C>>>positivePower(cp,c);
        if ( logger.isInfoEnabled() ) {
            logger.info("cp^p = " + cpp);
            logger.info("P    = " + P);
        }
        GenPolynomialRing<C> ppfac = new GenPolynomialRing<C>(apfac.coFac,pfac);
        List<GenPolynomial<C>> gl = new ArrayList<GenPolynomial<C>>();
        for (Monomial<AlgebraicNumber<C>> m : cpp) {
            ExpVector f = m.e;
            AlgebraicNumber<C> a = m.c;
            //System.out.println("m = " + m);
            GenPolynomial<C> ap = a.val;
            GenPolynomial<C> g = ppfac.getZERO();
            for ( Monomial<C> ma : ap ) {
                ExpVector e = ma.e;
                C cc = ma.c;
                C pc = P.val.coefficient(e);
                GenPolynomial<C> r = new GenPolynomial<C>(ppfac,cc,f);
                r = r.subtract(pc);
                //System.out.println("r = " + r);
                gl.add(r);
            }
        }
        //System.out.println("gl = " + gl);
        // solve system of equations and construct result
        Reduction<C> red = new ReductionSeq<C>();
        gl = red.irreducibleSet(gl);
        Ideal<C> L = new Ideal<C>(ppfac, gl, true);
        int z = L.commonZeroTest();
        if (z < 0) { // no solution
            return null;
        }
        if ( logger.isInfoEnabled() ) {
            logger.info("solution = " + gl);
        }
        GenPolynomial<C> car = apfac.getZERO();
        for ( GenPolynomial<C> pl : gl ) {
            if ( pl.length() <= 1 ) {
                continue;
            }
            if ( pl.length() > 2 ) {
                throw new RuntimeException("dim > 0 not implemented " + pl);
            }
            //System.out.println("pl = " + pl);
            ExpVector e = pl.leadingExpVector();
            int[] v = e.dependencyOnVariables();
            if (v == null || v.length == 0) {
                continue;
            }
            int vi = v[0];
            //System.out.println("vi = " + vi);
            GenPolynomial<C> ca = apfac.univariate(0,deg-1-vi);
            //System.out.println("ca = " + ca);
            C tc = pl.trailingBaseCoefficient();
            tc = tc.negate();
            if ( e.maxDeg() == c.longValue() ) {  // p-th root of tc ...
                //SortedMap<C, Long> br = rengine.rootCharacteristic(tc);
                SortedMap<C, Long> br = rengine.squarefreeFactors(tc);
                //System.out.println("br = " + br);
                if ( br != null && br.size() > 0 ) {
                    C cc = apfac.coFac.getONE();
                    for ( C bc : br.keySet() ) {
                        long ll = br.get(bc);
                        if ( ll % c.longValue() == 0L ) {
                            long fl = ll / c.longValue();
                            cc = cc.multiply( Power.<C> positivePower(bc,fl) );
                        } else { // fail ?
                            cc = cc.multiply( bc );
                        }
                    }
                    //System.out.println("cc = " + cc);
                    tc = cc;
                }
            }
            ca = ca.multiply(tc);
            car = car.sum(ca);
        }
        AlgebraicNumber<C> rr = new AlgebraicNumber<C>(afac,car);
        if ( logger.isInfoEnabled() ) {
            logger.info("solution AN = " + rr);
            //System.out.println("rr = " + rr);
        }
        root.put(rr, 1L);
        return root;
    }


    /**
     * GenPolynomial char-th root main variable.
     * @param P univariate GenPolynomial with AlgebraicNumber coefficients.
     * @return char-th_rootOf(P), or null, if P is no char-th root.
     */
    public GenPolynomial<AlgebraicNumber<C>> rootCharacteristic(GenPolynomial<AlgebraicNumber<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // go to recursion
            GenPolynomialRing<AlgebraicNumber<C>> cfac = pfac.contract(1);
            GenPolynomialRing<GenPolynomial<AlgebraicNumber<C>>> rfac = new GenPolynomialRing<GenPolynomial<AlgebraicNumber<C>>>(
                    cfac, 1);
            GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> Pr = PolyUtil.<AlgebraicNumber<C>> recursive(rfac, P);
            GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> Prc = recursiveUnivariateRootCharacteristic(Pr);
            if (Prc == null) {
                return null;
            }
            GenPolynomial<AlgebraicNumber<C>> D = PolyUtil.<AlgebraicNumber<C>> distribute(pfac, Prc);
            return D;
        }
        RingFactory<AlgebraicNumber<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for ModInteger polynomials " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<AlgebraicNumber<C>> d = pfac.getZERO().clone();
        for (Monomial<AlgebraicNumber<C>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<AlgebraicNumber<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("sm_alg,root = " + sm);
            }
            AlgebraicNumber<C> r = rf.getONE();
            for (AlgebraicNumber<C> rp : sm.keySet()) {
                long gl = sm.get(rp);
                if (gl > 1) {
                    rp = Power.<AlgebraicNumber<C>> positivePower(rp, gl);
                }
                r = r.multiply(rp);
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        logger.info("sm_alg,root,d = " + d);
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial. 
     * @param P GenPolynomial.
     * @return char-th_rootOf(P).
     */
    @Override
    public GenPolynomial<AlgebraicNumber<C>> baseRootCharacteristic(GenPolynomial<AlgebraicNumber<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate polynomials");
        }
        RingFactory<AlgebraicNumber<C>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<AlgebraicNumber<C>> d = pfac.getZERO().clone();
        for (Monomial<AlgebraicNumber<C>> m : P) {
            //System.out.println("m = " + m);
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            SortedMap<AlgebraicNumber<C>, Long> sm = rootCharacteristic(m.c);
            if (sm == null) {
                return null;
            }
            if (logger.isInfoEnabled()) {
                logger.info("sm_alg,base,root = " + sm);
            }
            AlgebraicNumber<C> r = rf.getONE();
            for (AlgebraicNumber<C> rp : sm.keySet()) {
                //System.out.println("rp = " + rp);
                long gl = sm.get(rp);
                //System.out.println("gl = " + gl);
                AlgebraicNumber<C> re = rp;
                if (gl > 1) {
                    re = Power.<AlgebraicNumber<C>> positivePower(rp, gl);
                }
                //System.out.println("re = " + re);
                r = r.multiply(re); 
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        if (logger.isInfoEnabled()) {
            logger.info("sm_alg,base,d = " + d);
        }
        return d;
    }


    /**
     * GenPolynomial char-th root univariate polynomial with polynomial coefficients.
     * @param P recursive univariate GenPolynomial.
     * @return char-th_rootOf(P), or null if P is no char-th root.
     */
    @Override
    public GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> recursiveUnivariateRootCharacteristic(
                         GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<AlgebraicNumber<C>>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // basePthRoot not possible by return type
            throw new RuntimeException(P.getClass().getName() + " only for univariate recursive polynomials");
        }
        RingFactory<GenPolynomial<AlgebraicNumber<C>>> rf = pfac.coFac;
        if (rf.characteristic().signum() != 1) {
            // basePthRoot not possible
            throw new RuntimeException(P.getClass().getName() + " only for char p > 0 " + rf);
        }
        long mp = rf.characteristic().longValue();
        GenPolynomial<GenPolynomial<AlgebraicNumber<C>>> d = pfac.getZERO().clone();
        for (Monomial<GenPolynomial<AlgebraicNumber<C>>> m : P) {
            ExpVector f = m.e;
            long fl = f.getVal(0);
            if (fl % mp != 0) {
                return null;
            }
            fl = fl / mp;
            GenPolynomial<AlgebraicNumber<C>> r = rootCharacteristic(m.c);
            if (r == null) {
                return null;
            }
            ExpVector e = ExpVector.create(1, 0, fl);
            d.doPutToMap(e, r);
        }
        return d;
    }

}

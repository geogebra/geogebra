/*
 * $Id: FactorModular.java 2979 2010-01-18 22:23:20Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.Modular;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.Power;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModularRingFactory;


/**
 * Modular coefficients factorization algorithms.
 * This class implements factorization methods for polynomials over (prime) modular integers.
 * @author Heinz Kredel
 */

public class FactorModular<MOD extends GcdRingElem<MOD> & Modular> extends FactorAbsolute<MOD> {


    private static final Logger logger = Logger.getLogger(FactorModular.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * No argument constructor, do not use. 
     */
    private FactorModular() {
        this( (RingFactory<MOD>) (Object) new ModLongRing(13,true) ); // hack, 13 unimportant
    }


    /**
     * Constructor. 
     * @param cfac coefficient ring factory.
     */
    public FactorModular(RingFactory<MOD> cfac) {
        super(cfac);
    }


    /**
     * GenPolynomial base distinct degree factorization.
     * @param P squarefree and monic GenPolynomial.
     * @return [e_1 -&gt; p_1, ..., e_k -&gt; p_k] with P = prod_{i=1,...,k} p_i and
     *         p_i has only irreducible factors of degree e_i.
     */
    public SortedMap<Long, GenPolynomial<MOD>> baseDistinctDegreeFactors(GenPolynomial<MOD> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        SortedMap<Long, GenPolynomial<MOD>> facs = new TreeMap<Long, GenPolynomial<MOD>>();
        if (P.isZERO()) {
            return facs;
        }
        GenPolynomialRing<MOD> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        ModularRingFactory<MOD> mr = (ModularRingFactory<MOD>) pfac.coFac;
        java.math.BigInteger m = mr.getIntegerModul().getVal();
        //if (m.longValue() == 2L) {
        //    logger.warn(this.getClass().getName() + " case p = 2 not implemented");
        //}
        GenPolynomial<MOD> x = pfac.univariate(0);
        GenPolynomial<MOD> h = x;
        GenPolynomial<MOD> f = P;
        GenPolynomial<MOD> g;
        Power<GenPolynomial<MOD>> pow = new Power<GenPolynomial<MOD>>(pfac);
        long d = 0;
        while (d + 1 <= f.degree(0) / 2) {
            d++;
            h = pow.modPower(h, m, f);
            g = engine.gcd(h.subtract(x), f);
            if (!g.isONE()) {
                facs.put(d, g);
                f = f.divide(g);
            }
        }
        if (!f.isONE()) {
            d = f.degree(0);
            facs.put(d, f);
        }
        return facs;
    }


    /**
     * GenPolynomial base equal degree factorization.
     * @param P squarefree and monic GenPolynomial.
     * @param deg such that P has only irreducible factors of degree deg.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    public List<GenPolynomial<MOD>> baseEqualDegreeFactors(GenPolynomial<MOD> P, long deg) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        List<GenPolynomial<MOD>> facs = new ArrayList<GenPolynomial<MOD>>();
        if (P.isZERO()) {
            return facs;
        }
        GenPolynomialRing<MOD> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        if (P.degree(0) == deg) {
            facs.add(P);
            return facs;
        }
        ModularRingFactory<MOD> mr = (ModularRingFactory<MOD>) pfac.coFac;
        java.math.BigInteger m = mr.getIntegerModul().getVal();
        //System.out.println("m = " + m);
        boolean p2 = false;
        if (m.equals(java.math.BigInteger.valueOf(2L))) {
            p2 = true;
            //throw new RuntimeException(this.getClass().getName() + " case p = 2 not implemented");
        }
        GenPolynomial<MOD> one = pfac.getONE();
        GenPolynomial<MOD> t = pfac.univariate(0,1L);
        GenPolynomial<MOD> r;
        GenPolynomial<MOD> h;
        GenPolynomial<MOD> f = P;
        //GreatestCommonDivisor<MOD> engine = GCDFactory.<MOD> getImplementation(pfac.coFac);
        Power<GenPolynomial<MOD>> pow = new Power<GenPolynomial<MOD>>(pfac);
        GenPolynomial<MOD> g = null;
        int degi = (int) deg; //f.degree(0);
        //System.out.println("deg = " + deg);
        BigInteger di = Power.<BigInteger> positivePower(new BigInteger(m), deg);
        //System.out.println("di = " + di);
        java.math.BigInteger d = di.getVal(); //.longValue()-1;
        //System.out.println("d = " + d);
        d = d.shiftRight(1); // divide by 2
        do {
            if ( p2 ) {
                h = t;
                for ( int i = 1; i < degi; i++ ) {
                    h = t.sum( h.multiply(h) );
                    h = h.remainder(f);
                }
                t = t.multiply( pfac.univariate(0,2L) );
                //System.out.println("h = " + h);
            } else {
                r = pfac.random(17, degi, 2 * degi, 1.0f);
                if (r.degree(0) >= f.degree(0)) {
                    r = r.remainder(f);
                }
                r = r.monic();
                //System.out.println("r = " + r);
                h = pow.modPower(r, d, f).subtract(one);
                degi++;
            }
            g = engine.gcd(h, f);
            //System.out.println("g = " + g);
        } while (g.degree(0) == 0 || g.degree(0) == f.degree(0));
        f = f.divide(g);
        facs.addAll(baseEqualDegreeFactors(f, deg));
        facs.addAll(baseEqualDegreeFactors(g, deg));
        return facs;
    }


    /**
     * GenPolynomial base factorization of a squarefree polynomial.
     * @param P squarefree and monic! GenPolynomial.
     * @return [p_1,...,p_k] with P = prod_{i=1,...,r} p_i.
     */
    @Override
    public List<GenPolynomial<MOD>> baseFactorsSquarefree(GenPolynomial<MOD> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P == null");
        }
        List<GenPolynomial<MOD>> factors = new ArrayList<GenPolynomial<MOD>>();
        if (P.isZERO()) {
            return factors;
        }
        if (P.isONE()) {
            factors.add(P);
            return factors;
        }
        GenPolynomialRing<MOD> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        if (!P.leadingBaseCoefficient().isONE()) {
            throw new RuntimeException("ldcf(P) != 1: " + P);
        }
        SortedMap<Long, GenPolynomial<MOD>> dfacs = baseDistinctDegreeFactors(P);
        if ( debug ) {
            logger.info("dfacs    = " + dfacs);
            //System.out.println("dfacs    = " + dfacs);
        }
        for (Long e : dfacs.keySet()) {
            GenPolynomial<MOD> f = dfacs.get(e);
            List<GenPolynomial<MOD>> efacs = baseEqualDegreeFactors(f, e);
            if ( debug ) {
               logger.info("efacs " + e + "   = " + efacs);
               //System.out.println("efacs " + e + "   = " + efacs);
            }
            factors.addAll(efacs);
        }
        //System.out.println("factors  = " + factors);
        factors = PolyUtil.<MOD>monic(factors);
        SortedSet<GenPolynomial<MOD>> ss = new TreeSet<GenPolynomial<MOD>>(factors);
        //System.out.println("sorted   = " + ss);
        factors.clear();
        factors.addAll(ss);
        return factors;
    }

}

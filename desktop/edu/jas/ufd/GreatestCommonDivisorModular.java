/*
 * $Id: GreatestCommonDivisorModular.java 2958 2010-01-01 17:43:39Z kredel $
 */

package edu.jas.ufd;


import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.ModularRingFactory;
import edu.jas.arith.BigInteger;
import edu.jas.arith.Modular;
import edu.jas.arith.ModLongRing;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.PrimeList;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;


/**
 * Greatest common divisor algorithms with modular computation and chinese
 * remainder algorithm.
 * @author Heinz Kredel
 */

public class GreatestCommonDivisorModular<MOD extends GcdRingElem<MOD> & Modular> 
        extends GreatestCommonDivisorAbstract<BigInteger> {
 

    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorModular.class);


    private final boolean debug = logger.isInfoEnabled();


    /*
     * Modular gcd algorithm to use.
     */
    protected final GreatestCommonDivisorAbstract<MOD> mufd;


    /*
     * Integer gcd algorithm for fall back.
     */
    protected final GreatestCommonDivisorAbstract<BigInteger> iufd = new GreatestCommonDivisorSubres<BigInteger>();


    /**
     * Constructor to set recursive algorithm. Use modular evaluation GCD
     * algorithm.
     */
    public GreatestCommonDivisorModular() {
        this(false);
    }


    /**
     * Constructor to set recursive algorithm.
     * @param simple , true if the simple PRS should be used.
     */
    public GreatestCommonDivisorModular(boolean simple) {
        if (simple) {
            mufd = new GreatestCommonDivisorSimple<MOD>();
        } else {
            mufd = new GreatestCommonDivisorModEval<MOD>();
        }
    }


    /**
     * Univariate GenPolynomial greatest comon divisor. Delegate to subresultant
     * baseGcd, should not be needed.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<BigInteger> baseGcd(GenPolynomial<BigInteger> P, GenPolynomial<BigInteger> S) {
        return iufd.baseGcd(P, S);
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor. Delegate to
     * subresultant recursiveGcd, should not be needed.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<GenPolynomial<BigInteger>> recursiveUnivariateGcd(
            GenPolynomial<GenPolynomial<BigInteger>> P, GenPolynomial<GenPolynomial<BigInteger>> S) {
        return iufd.recursiveUnivariateGcd(P, S);
    }


    /**
     * GenPolynomial greatest comon divisor, modular algorithm.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    @Override
    public GenPolynomial<BigInteger> gcd(GenPolynomial<BigInteger> P, GenPolynomial<BigInteger> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenPolynomialRing<BigInteger> fac = P.ring;
        // special case for univariate polynomials
        if (fac.nvar <= 1) {
            GenPolynomial<BigInteger> T = baseGcd(P, S);
            return T;
        }
        long e = P.degree(0);
        long f = S.degree(0);
        GenPolynomial<BigInteger> q;
        GenPolynomial<BigInteger> r;
        if (f > e) {
            r = P;
            q = S;
            long g = f;
            f = e;
            e = g;
        } else {
            q = P;
            r = S;
        }
        r = r.abs();
        q = q.abs();
        // compute contents and primitive parts
        BigInteger a = baseContent(r);
        BigInteger b = baseContent(q);
        // gcd of coefficient contents
        BigInteger c = gcd(a, b); // indirection
        r = divide(r, a); // indirection
        q = divide(q, b); // indirection
        if (r.isONE()) {
            return r.multiply(c);
        }
        if (q.isONE()) {
            return q.multiply(c);
        }
        // compute normalization factor
        BigInteger ac = r.leadingBaseCoefficient();
        BigInteger bc = q.leadingBaseCoefficient();
        BigInteger cc = gcd(ac, bc); // indirection
        // compute norms
        BigInteger an = r.maxNorm();
        BigInteger bn = q.maxNorm();
        BigInteger n = (an.compareTo(bn) < 0 ? bn : an);
        n = n.multiply(cc).multiply(n.fromInteger(2));
        // compute degree vectors
        ExpVector rdegv = r.degreeVector();
        ExpVector qdegv = q.degreeVector();
        //compute factor coefficient bounds
        BigInteger af = an.multiply(PolyUtil.factorBound(rdegv));
        BigInteger bf = bn.multiply(PolyUtil.factorBound(qdegv));
        BigInteger cf = (af.compareTo(bf) < 0 ? bf : af);
        cf = cf.multiply(cc.multiply(cc.fromInteger(8)));
        //initialize prime list and degree vector
        PrimeList primes = new PrimeList();
        int pn = 10; //primes.size();
        ExpVector wdegv = rdegv.subst(0, rdegv.getVal(0) + 1);
        // +1 seems to be a hack for the unlucky prime test
        ModularRingFactory<MOD> cofac;
        ModularRingFactory<MOD> cofacM = null;
        GenPolynomial<MOD> qm;
        GenPolynomial<MOD> rm;
        GenPolynomialRing<MOD> mfac;
        GenPolynomialRing<MOD> rfac = null;
        int i = 0;
        BigInteger M = null;
        BigInteger cfe = null;
        GenPolynomial<MOD> cp = null;
        GenPolynomial<MOD> cm = null;
        GenPolynomial<BigInteger> cpi = null;
        if (debug) {
            logger.debug("c = " + c);
            logger.debug("cc = " + cc);
            logger.debug("n  = " + n);
            logger.debug("cf = " + cf);
            logger.info("wdegv = " + wdegv);
        }
        for (java.math.BigInteger p : primes) {
            //System.out.println("next run ++++++++++++++++++++++++++++++++++");
            if ( p.longValue() == 2L ) { // skip 2
                continue;
            }
            if (++i >= pn) {
                logger.error("prime list exhausted, pn = " + pn);
                return iufd.gcd(P, S);
                //throw new RuntimeException("prime list exhausted");
            }
            // initialize coefficient factory and map normalization factor
            if ( ModLongRing.MAX_LONG.compareTo( p ) > 0 ) {
                cofac = (ModularRingFactory) new ModLongRing(p, true);
            } else {
                cofac = (ModularRingFactory) new ModIntegerRing(p, true);
            }
            MOD nf = cofac.fromInteger(cc.getVal());
            if (nf.isZERO()) {
                continue;
            }
            // initialize polynomial factory and map polynomials
            mfac = new GenPolynomialRing<MOD>(cofac, fac.nvar, fac.tord, fac.getVars());
            qm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, q);
            if (!qm.degreeVector().equals(qdegv)) {
                continue;
            }
            rm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, r);
            if (!rm.degreeVector().equals(rdegv)) {
                continue;
            }
            if (debug) {
                logger.info("cofac = " + cofac.getIntegerModul());
            }
            // compute modular gcd
            cm = mufd.gcd(rm, qm);
            // test for constant g.c.d
            if (cm.isConstant()) {
                logger.debug("cm, constant = " + cm);
                return fac.getONE().multiply(c);
                //return cm.abs().multiply( c );
            }
            // test for unlucky prime
            ExpVector mdegv = cm.degreeVector();
            if (wdegv.equals(mdegv)) { // TL = 0
                // prime ok, next round
                if (M != null) {
                    if (M.compareTo(cfe) > 0) {
                        System.out.println("M > cfe: " + M + " > " + cfe);
                        // continue; // why should this be required?
                    }
                }
            } else { // TL = 3
                boolean ok = false;
                if (wdegv.multipleOf(mdegv)) { // TL = 2 // EVMT(wdegv,mdegv)
                    M = null; // init chinese remainder
                    ok = true; // prime ok
                }
                if (mdegv.multipleOf(wdegv)) { // TL = 1 // EVMT(mdegv,wdegv)
                    continue; // skip this prime
                }
                if (!ok) {
                    M = null; // discard chinese remainder and previous work
                    continue; // prime not ok
                }
            }
            //--wdegv = mdegv;
            // prepare chinese remainder algorithm
            cm = cm.multiply(nf);
            if (M == null) {
                // initialize chinese remainder algorithm
                M = new BigInteger(p);
                cofacM = cofac;
                rfac = mfac;
                cp = cm;
                wdegv = wdegv.gcd(mdegv); //EVGCD(wdegv,mdegv);
                cfe = cf;
                for (int k = 0; k < wdegv.length(); k++) {
                    cfe = cfe.multiply(new BigInteger(wdegv.getVal(k) + 1));
                }
            } else {
                // apply chinese remainder algorithm
                MOD mi = cofac.fromInteger(M.getVal());
                mi = mi.inverse(); // mod p
                M = M.multiply(new BigInteger(p));
                if ( ModLongRing.MAX_LONG.compareTo( M.getVal() ) > 0 ) {
                    cofacM = (ModularRingFactory) new ModLongRing(M.getVal());
                } else {
                    cofacM = (ModularRingFactory) new ModIntegerRing(M.getVal());
                }
                rfac = new GenPolynomialRing<MOD>(cofacM, fac.nvar, fac.tord, fac.getVars());
                cp = PolyUtil.<MOD> chineseRemainder(rfac, cp, mi, cm);
            }
            // test for completion
            if (n.compareTo(M) <= 0) {
                break;
            }
            // must use integer.sumNorm
            cpi = PolyUtil.<MOD>integerFromModularCoefficients(fac, cp);
            BigInteger cmn = cpi.sumNorm();
            cmn = cmn.multiply(cmn.fromInteger(4));
            //if ( cmn.compareTo( M ) <= 0 ) {
            // does not work: only if cofactors are also considered?
            // break;
            //}
            if (i % 2 != 0 && !cp.isZERO()) {
                // check if done on every second prime
                GenPolynomial<BigInteger> x;
                x = PolyUtil.<MOD>integerFromModularCoefficients(fac, cp);
                x = basePrimitivePart(x);
                if (!PolyUtil.<BigInteger> basePseudoRemainder(q, x).isZERO()) {
                    continue;
                }
                if (!PolyUtil.<BigInteger> basePseudoRemainder(r, x).isZERO()) {
                    continue;
                }
                logger.info("done on exact division, #primes = " + i);
                break;
            }
        }
        if (debug) {
            logger.info("done on M = " + M + ", #primes = " + i);
        }
        // remove normalization
        q = PolyUtil.<MOD>integerFromModularCoefficients(fac, cp);
        q = basePrimitivePart(q);
        return q.abs().multiply(c);
    }

}

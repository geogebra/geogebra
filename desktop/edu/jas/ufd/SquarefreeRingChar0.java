/*
 * $Id: SquarefreeRingChar0.java 3209 2010-07-04 19:14:31Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Squarefree decomposition for coefficient rings of characteristic 0.
 * @author Heinz Kredel
 */

public class SquarefreeRingChar0<C extends GcdRingElem<C>> extends SquarefreeAbstract<C> /*implements Squarefree<C>*/{


    private static final Logger logger = Logger.getLogger(SquarefreeRingChar0.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Factory for ring of characteristic 0 coefficients.
     */
    protected final RingFactory<C> coFac;


    /**
     * Constructor.
     */
    public SquarefreeRingChar0(RingFactory<C> fac) {
        super( GCDFactory.<C> getProxy(fac) );
        if (fac.isField()) {
            throw new IllegalArgumentException("fac is a field: use SquarefreeFieldChar0");
        }
        if (fac.characteristic().signum() != 0) {
            throw new IllegalArgumentException("characterisic(fac) must be zero");
        }
        coFac = fac;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " with " + engine + " over " + coFac;
    }


    /**
     * GenPolynomial polynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<C> baseSquarefreePart(GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        GenPolynomial<C> pp = engine.basePrimitivePart(P);
        if (pp.isConstant()) {
            return pp;
        }
        GenPolynomial<C> d = PolyUtil.<C> baseDeriviative(pp);
        d = engine.basePrimitivePart(d);
        GenPolynomial<C> g = engine.baseGcd(pp, d);
        g = engine.basePrimitivePart(g);
        GenPolynomial<C> q = PolyUtil.<C> basePseudoDivide(pp, g);
        q = engine.basePrimitivePart(q);
        return q;
    }


    /**
     * GenPolynomial polynomial squarefree factorization.
     * @param A GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    @Override
    public SortedMap<GenPolynomial<C>, Long> baseSquarefreeFactors(GenPolynomial<C> A) {
        SortedMap<GenPolynomial<C>, Long> sfactors = new TreeMap<GenPolynomial<C>, Long>();
        if (A == null || A.isZERO()) {
            return sfactors;
        }
        if (A.isConstant()) {
            sfactors.put(A, 1L);
            return sfactors;
        }
        GenPolynomialRing<C> pfac = A.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        C ldbcf = A.leadingBaseCoefficient();
        if (!ldbcf.isONE()) {
            C cc = engine.baseContent(A);
            A = A.divide(cc);
            GenPolynomial<C> f1 = pfac.getONE().multiply(cc);
            //System.out.println("gcda sqf f1 = " + f1);
            sfactors.put(f1, 1L);
        }
        GenPolynomial<C> T0 = A;
        GenPolynomial<C> Tp;
        GenPolynomial<C> T = null;
        GenPolynomial<C> V = null;
        long k = 0L;
        boolean init = true;
        while (true) {
            if (init) {
                if (T0.isConstant() || T0.isZERO()) {
                    break;
                }
                Tp = PolyUtil.<C> baseDeriviative(T0);
                T = engine.baseGcd(T0, Tp);
                T = engine.basePrimitivePart(T);
                V = PolyUtil.<C> basePseudoDivide(T0, T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT  = " + T);
                //System.out.println("iV  = " + V);
                k = 0L;
                init = false;
            }
            if (V.isConstant()) {
                break;
            }
            k++;
            GenPolynomial<C> W = engine.baseGcd(T, V);
            W = engine.basePrimitivePart(W);
            GenPolynomial<C> z = PolyUtil.<C> basePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> basePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            if (z.degree(0) > 0) {
                if (ldbcf.isONE() && !z.leadingBaseCoefficient().isONE()) {
                    z = engine.basePrimitivePart(z);
                    logger.info("z,pp = " + z);
                }
                sfactors.put(z, k);
            }
        }
        return sfactors;
    }


    /**
     * GenPolynomial recursive univariate polynomial greatest squarefree
     * divisor.
     * @param P recursive univariate GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<GenPolynomial<C>> recursiveUnivariateSquarefreePart(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException(this.getClass().getName() + " only for multivariate polynomials");
        }
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) pfac.coFac;
        // squarefree content
        GenPolynomial<GenPolynomial<C>> pp = P;
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        Pc = engine.basePrimitivePart(Pc);
        //System.out.println("Pc,bPP = " + Pc);
        if (!Pc.isONE()) {
            pp = PolyUtil.<C> coefficientPseudoDivide(pp, Pc);
            //System.out.println("pp,sqp = " + pp);
            GenPolynomial<C> Pr = squarefreePart(Pc);
            Pr = engine.basePrimitivePart(Pr);
            //System.out.println("Pr,bPP = " + Pr);
        }
        if (pp.leadingExpVector().getVal(0) < 1) {
            //System.out.println("pp = " + pp);
            //System.out.println("Pc = " + Pc);
            return pp.multiply(Pc);
        }
        GenPolynomial<GenPolynomial<C>> d = PolyUtil.<C> recursiveDeriviative(pp);
        //System.out.println("d = " + d);
        GenPolynomial<GenPolynomial<C>> g = engine.recursiveUnivariateGcd(pp, d);
        //System.out.println("g,rec = " + g);
        g = engine.baseRecursivePrimitivePart(g);
        //System.out.println("g,bPP = " + g);
        GenPolynomial<GenPolynomial<C>> q = PolyUtil.<C> recursivePseudoDivide(pp, g);
        q = engine.baseRecursivePrimitivePart(q);
        //System.out.println("q,bPP = " + q);
        return q.multiply(Pc);
    }


    /**
     * GenPolynomial recursive univariate polynomial squarefree factorization.
     * @param P recursive univariate GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    @Override
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveUnivariateSquarefreeFactors(
            GenPolynomial<GenPolynomial<C>> P) {
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> sfactors = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>();
        if (P == null || P.isZERO()) {
            return sfactors;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if (pfac.nvar > 1) {
            // recursiveContent not possible by return type
            throw new RuntimeException(this.getClass().getName() + " only for univariate polynomials");
        }
        // if base coefficient ring is a field, make monic
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) pfac.coFac;
        C bcc = engine.baseRecursiveContent(P);
        if (!bcc.isONE()) {
            GenPolynomial<C> lc = cfac.getONE().multiply(bcc);
            GenPolynomial<GenPolynomial<C>> pl = pfac.getONE().multiply(lc);
            sfactors.put(pl, 1L);
            P = PolyUtil.<C> baseRecursiveDivide(P, bcc);
        }
        // factors of content
        GenPolynomial<C> Pc = engine.recursiveContent(P);
        if (logger.isInfoEnabled()) {
            logger.info("Pc = " + Pc);
        }
        Pc = engine.basePrimitivePart(Pc);
        //System.out.println("Pc,PP = " + Pc);
        if (!Pc.isONE()) {
            P = PolyUtil.<C> coefficientPseudoDivide(P, Pc);
        }
        SortedMap<GenPolynomial<C>, Long> rsf = squarefreeFactors(Pc);
        if (logger.isInfoEnabled()) {
            logger.info("rsf = " + rsf);
        }
        // add factors of content
        for (GenPolynomial<C> c : rsf.keySet()) {
            if (!c.isONE()) {
                GenPolynomial<GenPolynomial<C>> cr = pfac.getONE().multiply(c);
                Long rk = rsf.get(c);
                sfactors.put(cr, rk);
            }
        }

        // factors of recursive polynomial
        GenPolynomial<GenPolynomial<C>> T0 = P;
        GenPolynomial<GenPolynomial<C>> Tp;
        GenPolynomial<GenPolynomial<C>> T = null;
        GenPolynomial<GenPolynomial<C>> V = null;
        long k = 0L;
        boolean init = true;
        while (true) {
            if (init) {
                if (T0.isConstant() || T0.isZERO()) {
                    break;
                }
                Tp = PolyUtil.<C> recursiveDeriviative(T0);
                T = engine.recursiveUnivariateGcd(T0, Tp);
                T = engine.baseRecursivePrimitivePart(T);
                V = PolyUtil.<C> recursivePseudoDivide(T0, T);
                //System.out.println("iT0 = " + T0);
                //System.out.println("iTp = " + Tp);
                //System.out.println("iT = " + T);
                //System.out.println("iV = " + V);
                k = 0L;
                init = false;
            }
            if (V.isConstant()) {
                break;
            }
            k++;
            GenPolynomial<GenPolynomial<C>> W = engine.recursiveUnivariateGcd(T, V);
            W = engine.baseRecursivePrimitivePart(W);
            GenPolynomial<GenPolynomial<C>> z = PolyUtil.<C> recursivePseudoDivide(V, W);
            //System.out.println("W = " + W);
            //System.out.println("z = " + z);
            V = W;
            T = PolyUtil.<C> recursivePseudoDivide(T, V);
            //System.out.println("V = " + V);
            //System.out.println("T = " + T);
            //was: if ( z.degree(0) > 0 ) {
            if (!z.isONE() && !z.isZERO()) {
                z = engine.baseRecursivePrimitivePart(z);
                sfactors.put(z, k);
            }
        }
        return sfactors;
    }


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    @Override
    public GenPolynomial<C> squarefreePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreePart(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<C> Pc = engine.recursiveContent(Pr);
        Pr = PolyUtil.<C> coefficientPseudoDivide(Pr, Pc);
        GenPolynomial<C> Ps = squarefreePart(Pc);
        GenPolynomial<GenPolynomial<C>> PP = recursiveUnivariateSquarefreePart(Pr);
        GenPolynomial<GenPolynomial<C>> PS = PP.multiply(Ps);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, PS);
        return D;
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    @Override
    public SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return baseSquarefreeFactors(P);
        }
        SortedMap<GenPolynomial<C>, Long> sfactors = new TreeMap<GenPolynomial<C>, Long>();
        if (P.isZERO()) {
            return sfactors;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> PP = recursiveUnivariateSquarefreeFactors(Pr);

        for (Map.Entry<GenPolynomial<GenPolynomial<C>>, Long> m : PP.entrySet()) {
            Long i = m.getValue();
            GenPolynomial<GenPolynomial<C>> Dr = m.getKey();
            GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
            sfactors.put(D, i);
        }
        return sfactors;
    }


    /**
     * Coefficients squarefree factorization.
     * @param P coefficient.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public SortedMap<C, Long> squarefreeFactors(C P) {
	throw new RuntimeException("method not implemented");
    }

}

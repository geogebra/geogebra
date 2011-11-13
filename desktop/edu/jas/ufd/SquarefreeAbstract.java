/*
 * $Id: SquarefreeAbstract.java 3208 2010-07-04 19:08:44Z kredel $
 */

package edu.jas.ufd;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Abstract squarefree decomposition class.
 * @author Heinz Kredel
 */

public abstract class SquarefreeAbstract<C extends GcdRingElem<C>> implements Squarefree<C> {


    /**
     * GCD engine for respective base coefficients.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Constructor.
     */
    public SquarefreeAbstract(GreatestCommonDivisorAbstract<C> engine) {
        this.engine = engine;
    }


    /**
     * GenPolynomial polynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public abstract GenPolynomial<C> baseSquarefreePart(GenPolynomial<C> P);


    /**
     * GenPolynomial polynomial squarefree factorization.
     * @param A GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<GenPolynomial<C>, Long> baseSquarefreeFactors(GenPolynomial<C> A);


    /**
     * GenPolynomial recursive polynomial greatest squarefree divisor.
     * @param P recursive univariate GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public abstract GenPolynomial<GenPolynomial<C>> recursiveUnivariateSquarefreePart(
            GenPolynomial<GenPolynomial<C>> P);


    /**
     * GenPolynomial recursive univariate polynomial squarefree factorization.
     * @param P recursive univariate GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveUnivariateSquarefreeFactors(
            GenPolynomial<GenPolynomial<C>> P);


    /**
     * GenPolynomial greatest squarefree divisor.
     * @param P GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public abstract GenPolynomial<C> squarefreePart(GenPolynomial<C> P);


    /**
     * GenPolynomial test if is squarefree.
     * @param P GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isSquarefree(GenPolynomial<C> P) {
        GenPolynomial<C> S = squarefreePart(P);
        boolean f = P.equals(S);
        if (!f) {
            System.out.println("\nisSquarefree: " + f);
            System.out.println("S  = " + S);
            System.out.println("P  = " + P);
        }
        return f;
    }


    /**
     * GenPolynomial list test if squarefree.
     * @param L list of GenPolynomial.
     * @return true if each P in L is squarefree, else false.
     */
    public boolean isSquarefree(List<GenPolynomial<C>> L) {
        if ( L == null || L.isEmpty() ) {
            return true;
        }
        for ( GenPolynomial<C> P : L ) {
            if (! isSquarefree(P) ) {
                return false;
            }
        }
        return true;
    }


    /**
     * Recursive GenPolynomial test if is squarefree.
     * @param P recursive univariate GenPolynomial.
     * @return true if P is squarefree, else false.
     */
    public boolean isRecursiveSquarefree(GenPolynomial<GenPolynomial<C>> P) {
        GenPolynomial<GenPolynomial<C>> S = recursiveUnivariateSquarefreePart(P);
        boolean f = P.equals(S);
        if (!f) {
            System.out.println("\nisSquarefree: " + f);
            System.out.println("S = " + S);
            System.out.println("P = " + P);
        }
        return f;
    }


    /**
     * GenPolynomial squarefree factorization.
     * @param P GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<GenPolynomial<C>, Long> squarefreeFactors(GenPolynomial<C> P);


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a and each b in B is
     *         squarefree. B does not contain zero or constant polynomials.
     */
    public List<GenPolynomial<C>> coPrimeSquarefree(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenPolynomial<C>> S = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> g : A) {
            SortedMap<GenPolynomial<C>, Long> sm = squarefreeFactors(g);
            S.addAll(sm.keySet());
        }
        List<GenPolynomial<C>> B = engine.coPrime(S);
        return B;
    }


    /**
     * GenPolynomial squarefree and co-prime list.
     * @param a polynomial.
     * @param P squarefree co-prime list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for non-constant a
     *         there exists b in P with b|a. B does not contain zero or constant
     *         polynomials.
     */
    public List<GenPolynomial<C>> coPrimeSquarefree(GenPolynomial<C> a, List<GenPolynomial<C>> P) {
        if (a == null || a.isZERO() || a.isConstant()) {
            return P;
        }
        SortedMap<GenPolynomial<C>, Long> sm = squarefreeFactors(a);
        List<GenPolynomial<C>> B = P;
        for ( GenPolynomial<C> f : sm.keySet() ) {
            B = engine.coPrime(f,B);
        }
        return B;
    }


    /**
     * Test if list of GenPolynomials is squarefree and co-prime.
     * @param B list of GenPolynomials.
     * @return true, if for all b != c in B gcd(b,c) = 1 and 
     *          each b in B is squarefree, else false. 
     */
    public boolean isCoPrimeSquarefree(List<GenPolynomial<C>> B) {
        if (B == null || B.isEmpty()) {
            return true;
        }
        if ( !engine.isCoPrime(B) ) {
            return false;
        }
        return isSquarefree(B);
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1,...,p_k].
     * @return true if P = prod_{i=1,...,r} p_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, List<GenPolynomial<C>> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : F) {
            t = t.multiply(f);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            System.out.println("\nfactorization(list): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
        }
        return f;
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isFactorization(GenPolynomial<C> P, SortedMap<GenPolynomial<C>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<C> t = P.ring.getONE();
        for (GenPolynomial<C> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<C> g = Power.<GenPolynomial<C>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            //System.out.println("P = " + P);
            //System.out.println("t = " + t);
            P = P.monic();
            t = t.monic();
            f = P.equals(t) || P.equals(t.negate());
            if (f) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F = " + F);
            System.out.println("P = " + P);
            System.out.println("t = " + t);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * GenPolynomial is (squarefree) factorization.
     * @param P GenPolynomial.
     * @param F = [p_1 -&gt; e_1, ..., p_k -&gt; e_k].
     * @return true if P = prod_{i=1,...,k} p_i**e_i, else false.
     */
    public boolean isRecursiveFactorization(GenPolynomial<GenPolynomial<C>> P,
            SortedMap<GenPolynomial<GenPolynomial<C>>, Long> F) {
        if (P == null || F == null) {
            throw new IllegalArgumentException("P and F may not be null");
        }
        if (P.isZERO() && F.size() == 0) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> t = P.ring.getONE();
        for (GenPolynomial<GenPolynomial<C>> f : F.keySet()) {
            Long E = F.get(f);
            long e = E.longValue();
            GenPolynomial<GenPolynomial<C>> g = Power.<GenPolynomial<GenPolynomial<C>>> positivePower(f, e);
            t = t.multiply(g);
        }
        boolean f = P.equals(t) || P.equals(t.negate());
        if (!f) {
            //System.out.println("P = " + P);
            //System.out.println("t = " + t);
            GenPolynomialRing<C> cf = (GenPolynomialRing<C>)P.ring.coFac;
            GreatestCommonDivisorAbstract<C> engine = GCDFactory.getProxy(cf.coFac);
            GenPolynomial<GenPolynomial<C>> Pp = engine.recursivePrimitivePart(P);
            Pp = PolyUtil.<C>monic(Pp);
            GenPolynomial<GenPolynomial<C>> tp = engine.recursivePrimitivePart(t);
            tp = PolyUtil.<C>monic(tp);
            f = Pp.equals(tp) || Pp.equals(tp.negate());
            if (f) {
                return f;
            }
            System.out.println("\nfactorization(map): " + f);
            System.out.println("F  = " + F);
            System.out.println("P  = " + P);
            System.out.println("t  = " + t);
            System.out.println("Pp = " + Pp);
            System.out.println("tp = " + tp);
            //RuntimeException e = new RuntimeException("fac-map");
            //e.printStackTrace();
            //throw e;
        }
        return f;
    }


    /**
     * GenPolynomial recursive polynomial greatest squarefree divisor.
     * @param P recursive GenPolynomial.
     * @return squarefree(pp(P)).
     */
    public GenPolynomial<GenPolynomial<C>> recursiveSquarefreePart(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null || P.isZERO()) {
            return P;
        }
        if (P.ring.nvar <= 1) {
            return recursiveUnivariateSquarefreePart(P);
        }
        // distributed polynomials squarefree part
        GenPolynomialRing<GenPolynomial<C>> rfac = P.ring;
        RingFactory<GenPolynomial<C>> rrfac = rfac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rrfac;
        GenPolynomialRing<C> dfac = cfac.extend(rfac.nvar);
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, P);
        GenPolynomial<C> Dd = squarefreePart(Pd);
        // convert to recursive
        GenPolynomial<GenPolynomial<C>> C = PolyUtil.<C> recursive(rfac, Dd);
        return C;
    }


    /**
     * GenPolynomial recursive polynomial squarefree factorization.
     * @param P recursive GenPolynomial.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public SortedMap<GenPolynomial<GenPolynomial<C>>, Long> recursiveSquarefreeFactors(
            GenPolynomial<GenPolynomial<C>> P) {
        SortedMap<GenPolynomial<GenPolynomial<C>>, Long> factors;
        factors = new TreeMap<GenPolynomial<GenPolynomial<C>>, Long>();
        if (P == null || P.isZERO()) {
            return factors;
        }
        if (P.ring.nvar <= 1) {
            return recursiveUnivariateSquarefreeFactors(P);
        }
        // distributed polynomials squarefree part
        GenPolynomialRing<GenPolynomial<C>> rfac = P.ring;
        RingFactory<GenPolynomial<C>> rrfac = rfac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rrfac;
        GenPolynomialRing<C> dfac = cfac.extend(rfac.nvar);
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, P);
        SortedMap<GenPolynomial<C>, Long> dfacs = squarefreeFactors(Pd);
        // convert to recursive
        for (Map.Entry<GenPolynomial<C>, Long> Dm : dfacs.entrySet()) {
            GenPolynomial<C> Dd = Dm.getKey();
            Long e = Dm.getValue();
            GenPolynomial<GenPolynomial<C>> C = PolyUtil.<C> recursive(rfac, Dd);
            factors.put(C, e);
        }
        return factors;
    }


    /**
     * Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param D sorted map [d_1 -> e_1, ..., d_k -> e_k] with d_i squarefree.
     * @return [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] with A/prod(D) = A0 + sum( sum ( Aij/di^j ) ) with deg(Aij) < deg(di).
     */
    public List<List<GenPolynomial<C>>> basePartialFraction(GenPolynomial<C> A, SortedMap<GenPolynomial<C>,Long> D) {
        if ( D == null || A == null ) {
            throw new IllegalArgumentException("null A or D not allowed");
        }
        List<List<GenPolynomial<C>>> pf = new ArrayList<List<GenPolynomial<C>>>( D.size()+1 );
        if ( D.size() == 0 ) {
            return pf;
        }
        //List<GenPolynomial<C>> fi;
        if ( A.isZERO() ) {
            for ( GenPolynomial<C> d : D.keySet() ) {
                long e = D.get(d);
                int e1 = (int)e + 1;
                List<GenPolynomial<C>> fi = new ArrayList<GenPolynomial<C>>(e1);
                for ( int i = 0; i < e1; i++ ) {
                    fi.add(A);
                }
                pf.add(fi);
            }
            List<GenPolynomial<C>> fi = new ArrayList<GenPolynomial<C>>(1);
            fi.add(A);
            pf.add(0,fi);
            return pf;
        }
        // A != 0, D != empty
        List<GenPolynomial<C>> Dp = new ArrayList<GenPolynomial<C>>( D.size() );
        for ( GenPolynomial<C> d : D.keySet() ) {
            long e = D.get(d);
            GenPolynomial<C> f = Power.<GenPolynomial<C>> positivePower(d, e);
            Dp.add(f);
        }
        List<GenPolynomial<C>> F = engine.basePartialFraction(A,Dp);
        //System.out.println("fraction list = " + F.size());
        GenPolynomial<C> A0 = F.remove(0);
        List<GenPolynomial<C>> fi = new ArrayList<GenPolynomial<C>>(1);
        fi.add(A0);
        pf.add(fi);
        int i = 0;
        for ( GenPolynomial<C> d : D.keySet() ) { // assume fixed sequence order
            long e = D.get(d);
            int ei = (int)e;
            GenPolynomial<C> gi = F.get(i); // assume fixed sequence order
            List<GenPolynomial<C>> Fi = engine.basePartialFraction(gi,d,ei);
            pf.add(Fi);
            i++;
        }
        return pf;
    }


    /**
     * Test for Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param D sorted map [d_1 -> e_1, ..., d_k -> e_k] with d_i squarefree.
     * @param F a list of lists [ [Ai0, Ai1,..., Aie_i], i=0,...,k ] 
     * @return true, if A/prod(D) = A0 + sum( sum ( Aij/di^j ) ),
               else false.
     */
    public boolean isBasePartialFraction(GenPolynomial<C> A, SortedMap<GenPolynomial<C>,Long> D, List<List<GenPolynomial<C>>> F) {
        if ( D == null || A == null || F == null ) {
            throw new IllegalArgumentException("null A, D or F not allowed");
        }
        if ( D.isEmpty() && F.isEmpty() ) {
            return true;
        }
        if ( D.isEmpty() || F.isEmpty() ) {
            return false;
        }
        List<GenPolynomial<C>> Dp = new ArrayList<GenPolynomial<C>>( D.size() );
        for ( GenPolynomial<C> d : D.keySet() ) {
            long e = D.get(d);
            GenPolynomial<C> f = Power.<GenPolynomial<C>> positivePower(d, e);
            Dp.add(f);
        }
        List<GenPolynomial<C>> fi = F.get(0);
        if ( fi.size() != 1 ) {
            System.out.println("size(fi) != 1 " + fi);
            return false;
        }
        boolean t;
        GenPolynomial<C> A0 = fi.get(0);
        //System.out.println("A0 = " + A0);
        List<GenPolynomial<C>> Qp = new ArrayList<GenPolynomial<C>>(D.size()+1);
        Qp.add(A0);

//         List<GenPolynomial<C>> Fp = engine.basePartialFraction(A,Dp);
//         System.out.println("fraction list = " + F.size());
//         t = engine.isBasePartialFraction(A,Dp,Fp);
//         if ( ! t ) {
//             System.out.println("not recursion isPartFrac = " + Fp);
//             return false;
//         }
//         GenPolynomial<C> A0p = Fp.remove(0);
//         if ( ! A0.equals(A0p) ) {
//             System.out.println("A0 != A0p " + A0p);
//             return false;
//         }

        int i = 0;
        for ( GenPolynomial<C> d : D.keySet() ) { // assume fixed sequence order
            long e = D.get(d);
            int ei = (int)e;
            List<GenPolynomial<C>> Fi = F.get(i+1); // assume fixed sequence order

//            GenPolynomial<C> pi = Fp.get(i);        // assume fixed sequence order
//             t = engine.isBasePartialFraction(pi,d,ei,Fi);
//             if ( ! t ) {
//                 System.out.println("not isPartFrac exp = " + pi + ", d = " + d + ", e = " + ei);
//                 System.out.println("not isPartFrac exp = " + Fi);
//                 return false;
//             }

            GenPolynomial<C> qi = engine.basePartialFractionValue(d,ei,Fi);
            Qp.add(qi);

//             t = qi.equals(pi);
//             if ( ! t ) {
//                 System.out.println("not isPartFrac exp = " + pi + ", d = " + d + ", e = " + ei + ", qi = " + qi);
//             }

            i++;
        }

        t = engine.isBasePartialFraction(A,Dp,Qp);
        if ( ! t ) {
            System.out.println("not final isPartFrac " + Qp);
        }
        return t;
    }


    /**
     * Coefficients greatest squarefree divisor.
     * @param P coefficient.
     * @return squarefree part of P.
     */
    public C squarefreePart(C P) {
        if (P == null) {
            return null;
        }
        // just for the moment:
        C s = null;
        SortedMap<C, Long> factors = squarefreeFactors(P);
        //logger.info("sqfPart,factors = " + factors);
        System.out.println("sqfPart,factors = " + factors);
        for (C sp : factors.keySet()) {
            if ( s == null ) {
                s = sp;
            } else {
                s = s.multiply(sp);
            }
        }
        return s;
    }


    /**
     * Coefficients squarefree factorization.
     * @param P coefficient.
     * @return [p_1 -> e_1, ..., p_k -> e_k] with P = prod_{i=1,...,k} p_i^{e_i}
     *         and p_i squarefree.
     */
    public abstract SortedMap<C, Long> squarefreeFactors(C P); 
    /* not possible:
    {
        if (P == null) {
            return null;
        }
        SortedMap<C, Long> factors = new TreeMap<C, Long>();
        SquarefreeAbstract<C> reng = SquarefreeFactory.getImplementation((RingFactory<C>) P.factory());
            System.out.println("fcp,reng = " + reng);
            SortedMap<C, Long> rfactors = reng.squarefreeFactors(P);
            for (C c : rfactors.keySet()) {
                if (!c.isONE()) {
                    C cr = (C) (Object) c;
                    Long rk = rfactors.get(c);
                    factors.put(cr, rk);
                }
            }

        return factors;
    }
    */

}

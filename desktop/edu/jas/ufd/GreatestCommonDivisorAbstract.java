/*
 * $Id: GreatestCommonDivisorAbstract.java 3079 2010-04-19 20:53:23Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Greatest common divisor algorithms.
 * @author Heinz Kredel
 */

public abstract class GreatestCommonDivisorAbstract<C extends GcdRingElem<C>> implements
        GreatestCommonDivisor<C> {


    private static final Logger logger = Logger.getLogger(GreatestCommonDivisorAbstract.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName();
    }


    /**
     * GenPolynomial base coefficient content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public C baseContent(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        C d = null;
        for (C c : P.getMap().values()) {
            if (d == null) {
                d = c;
            } else {
                d = d.gcd(c);
            }
            if (d.isONE()) {
                return d;
            }
        }
        if ( d.signum() < 0 ) {
            d = d.negate();
        }
        return d;
    }


    /**
     * GenPolynomial base coefficient primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> basePrimitivePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = baseContent(P);
        if (d.isONE()) {
            return P;
        }
        GenPolynomial<C> pp = P.divide(d);
        if (debug) {
            GenPolynomial<C> p = pp.multiply(d);
            if (!p.equals(P)) {
                throw new RuntimeException("pp(p)*cont(p) != p: ");
            }
        }
        return pp;
    }


    /**
     * Univariate GenPolynomial greatest common divisor. Uses sparse
     * pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<C> baseGcd(GenPolynomial<C> P, GenPolynomial<C> S);


    /**
     * GenPolynomial recursive content.
     * @param P recursive GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> recursiveContent(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P.ring.getZEROCoefficient();
        }
        GenPolynomial<C> d = null;
        for (GenPolynomial<C> c : P.getMap().values()) {
            if (d == null) {
                d = c;
            } else {
                d = gcd(d, c); // go to recursion
            }
            if (d.isONE()) {
                return d;
            }
        }
        return d.abs();
    }


    /**
     * GenPolynomial recursive primitive part.
     * @param P recursive GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<GenPolynomial<C>> recursivePrimitivePart(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomial<C> d = recursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> pp = PolyUtil.<C> recursiveDivide(P, d);
        return pp;
    }


    /**
     * GenPolynomial base recursive content.
     * @param P recursive GenPolynomial.
     * @return baseCont(P).
     */
    public C baseRecursiveContent(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            GenPolynomialRing<C> cf = (GenPolynomialRing<C>) P.ring.coFac;
            return cf.coFac.getZERO();
        }
        C d = null;
        for (GenPolynomial<C> c : P.getMap().values()) {
            C cc = baseContent(c);
            if (d == null) {
                d = cc;
            } else {
                d = gcd(d, cc);
            }
            if (d.isONE()) {
                return d;
            }
        }
        if ( d.signum() < 0 ) {
            d = d.negate();
        }
        return d;
    }


    /**
     * GenPolynomial base recursive primitive part.
     * @param P recursive GenPolynomial.
     * @return basePP(P).
     */
    public GenPolynomial<GenPolynomial<C>> baseRecursivePrimitivePart(GenPolynomial<GenPolynomial<C>> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        C d = baseRecursiveContent(P);
        if (d.isONE()) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> pp = PolyUtil.<C> baseRecursiveDivide(P, d);
        return pp;
    }


    /**
     * GenPolynomial recursive greatest common divisor. Uses pseudoRemainder for
     * remainder.
     * @param P recursive GenPolynomial.
     * @param S recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> recursiveGcd(GenPolynomial<GenPolynomial<C>> P,
            GenPolynomial<GenPolynomial<C>> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        if (P.ring.nvar <= 1) {
            return recursiveUnivariateGcd(P, S);
        }
        // distributed polynomials gcd
        GenPolynomialRing<GenPolynomial<C>> rfac = P.ring;
        RingFactory<GenPolynomial<C>> rrfac = rfac.coFac;
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) rrfac;
        GenPolynomialRing<C> dfac = cfac.extend(rfac.nvar);
        GenPolynomial<C> Pd = PolyUtil.<C> distribute(dfac, P);
        GenPolynomial<C> Sd = PolyUtil.<C> distribute(dfac, S);
        GenPolynomial<C> Dd = gcd(Pd, Sd);
        // convert to recursive
        GenPolynomial<GenPolynomial<C>> C = PolyUtil.<C> recursive(rfac, Dd);
        return C;
    }


    /**
     * Univariate GenPolynomial recursive greatest common divisor. Uses
     * pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public abstract GenPolynomial<GenPolynomial<C>> recursiveUnivariateGcd(GenPolynomial<GenPolynomial<C>> P,
            GenPolynomial<GenPolynomial<C>> S);


    /**
     * GenPolynomial content.
     * @param P GenPolynomial.
     * @return cont(P).
     */
    public GenPolynomial<C> content(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            // baseContent not possible by return type
            throw new RuntimeException(this.getClass().getName()
                    + " use baseContent for univariate polynomials");

        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<C> D = recursiveContent(Pr);
        return D;
    }


    /**
     * GenPolynomial primitive part.
     * @param P GenPolynomial.
     * @return pp(P).
     */
    public GenPolynomial<C> primitivePart(GenPolynomial<C> P) {
        if (P == null) {
            throw new RuntimeException(this.getClass().getName() + " P != null");
        }
        if (P.isZERO()) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            return basePrimitivePart(P);
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> PP = recursivePrimitivePart(Pr);

        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, PP);
        return D;
    }


    /**
     * GenPolynomial division. Indirection to GenPolynomial method.
     * @param a GenPolynomial.
     * @param b coefficient.
     * @return a/b.
     */
    public GenPolynomial<C> divide(GenPolynomial<C> a, C b) {
        if (b == null || b.isZERO()) {
            throw new RuntimeException(this.getClass().getName() + " division by zero");

        }
        if (a == null || a.isZERO()) {
            return a;
        }
        return a.divide(b);
    }


    /**
     * Coefficient greatest common divisor. Indirection to coefficient method.
     * @param a coefficient.
     * @param b coefficient.
     * @return gcd(a,b).
     */
    public C gcd(C a, C b) {
        if (b == null || b.isZERO()) {
            return a;
        }
        if (a == null || a.isZERO()) {
            return b;
        }
        return a.gcd(b);
    }


    /**
     * GenPolynomial greatest common divisor.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> gcd(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return P;
        }
        if (P == null || P.isZERO()) {
            return S;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            GenPolynomial<C> T = baseGcd(P, S);
            return T;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> Sr = PolyUtil.<C> recursive(rfac, S);
        GenPolynomial<GenPolynomial<C>> Dr = recursiveUnivariateGcd(Pr, Sr);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
        return D;
    }


    /**
     * GenPolynomial least common multiple.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return lcm(P,S).
     */
    public GenPolynomial<C> lcm(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        GenPolynomial<C> C = gcd(P, S);
        GenPolynomial<C> A = P.multiply(S);
        return PolyUtil.<C> basePseudoDivide(A, C);
    }


    /**
     * GenPolynomial resultant.
     * The input polynomials are considered as univariate polynomials in the main variable. 
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return res(P,S).
     * @see edu.jas.ufd.GreatestCommonDivisorSubres#recursiveResultant
     */
    public GenPolynomial<C> resultant(GenPolynomial<C> P, GenPolynomial<C> S) {
        if (S == null || S.isZERO()) {
            return S;
        }
        if (P == null || P.isZERO()) {
            return P;
        }
        // hack for now:
        GreatestCommonDivisorSubres<C> ufd_sr = new GreatestCommonDivisorSubres<C>();
        GenPolynomialRing<C> pfac = P.ring;
        if (pfac.nvar <= 1) {
            GenPolynomial<C> T = ufd_sr.baseResultant(P, S);
            return T;
        }
        GenPolynomialRing<C> cfac = pfac.contract(1);
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, 1);

        GenPolynomial<GenPolynomial<C>> Pr = PolyUtil.<C> recursive(rfac, P);
        GenPolynomial<GenPolynomial<C>> Sr = PolyUtil.<C> recursive(rfac, S);

        GenPolynomial<GenPolynomial<C>> Dr = ufd_sr.recursiveResultant(Pr, Sr);
        GenPolynomial<C> D = PolyUtil.<C> distribute(pfac, Dr);
        return D;
    }


    /**
     * GenPolynomial co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a. B does not contain zero or
     *         constant polynomials.
     */
    public List<GenPolynomial<C>> coPrime(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>(A.size());
        // make a coprime to rest of list
        GenPolynomial<C> a = A.get(0);
        //System.out.println("a = " + a);
        if (!a.isZERO() && !a.isConstant()) {
            for (int i = 1; i < A.size(); i++) {
                GenPolynomial<C> b = A.get(i);
                GenPolynomial<C> g = gcd(a, b).abs();
                if (!g.isONE()) {
                    a = PolyUtil.<C> basePseudoDivide(a, g);
                    b = PolyUtil.<C> basePseudoDivide(b, g);
                    GenPolynomial<C> gp = gcd(a, g).abs();
                    while (!gp.isONE()) {
                        a = PolyUtil.<C> basePseudoDivide(a, gp);
                        g = PolyUtil.<C> basePseudoDivide(g, gp);
                        B.add(g); // gcd(a,g) == 1
                        g = gp;
                        gp = gcd(a, gp).abs();
                    }
                    if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                        B.add(g); // gcd(a,g) == 1
                    }
                }
                if (!b.isZERO() && !b.isConstant()) {
                    B.add(b); // gcd(a,b) == 1
                }
            }
        } else {
            B.addAll(A.subList(1, A.size()));
        }
        a = a.abs();
        // make rest coprime
        B = coPrime(B);
        //System.out.println("B = " + B);
        //System.out.println("red(a) = " + a);
        if (!a.isZERO() && !a.isConstant() /*&& !B.contains(a)*/) {
            B.add(a);
        }
        return B;
    }


    /**
     * GenPolynomial co-prime list.
     * @param A list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for all non-constant
     *         a in A there exists b in B with b|a. B does not contain zero or
     *         constant polynomials.
     */
    public List<GenPolynomial<C>> coPrimeRec(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return A;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>();
        // make a co-prime to rest of list
        for (GenPolynomial<C> a : A) {
            //System.out.println("a = " + a);
            B = coPrime(a, B);
            //System.out.println("B = " + B);
        }
        return B;
    }


    /**
     * GenPolynomial co-prime list.
     * @param a GenPolynomial.
     * @param P co-prime list of GenPolynomials.
     * @return B with gcd(b,c) = 1 for all b != c in B and for non-constant a
     *         there exists b in P with b|a. B does not contain zero or constant
     *         polynomials.
     */
    public List<GenPolynomial<C>> coPrime(GenPolynomial<C> a, List<GenPolynomial<C>> P) {
        if (a == null || a.isZERO() || a.isConstant()) {
            return P;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>(P.size() + 1);
        // make a coprime to elements of the list P
        for (int i = 0; i < P.size(); i++) {
            GenPolynomial<C> b = P.get(i);
            GenPolynomial<C> g = gcd(a, b).abs();
            if (!g.isONE()) {
                a = PolyUtil.<C> basePseudoDivide(a, g);
                b = PolyUtil.<C> basePseudoDivide(b, g);
                // make g co-prime to new a, g is co-prime to c != b in P, B
                GenPolynomial<C> gp = gcd(a, g).abs();
                while (!gp.isONE()) {
                    a = PolyUtil.<C> basePseudoDivide(a, gp);
                    g = PolyUtil.<C> basePseudoDivide(g, gp);
                    if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                        B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                    }
                    g = gp;
                    gp = gcd(a, gp).abs();
                }
                // make new g co-prime to new b
                gp = gcd(b, g).abs();
                while (!gp.isONE()) {
                    b = PolyUtil.<C> basePseudoDivide(b, gp);
                    g = PolyUtil.<C> basePseudoDivide(g, gp);
                    if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                        B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                    }
                    g = gp;
                    gp = gcd(b, gp).abs();
                }
                if (!g.isZERO() && !g.isConstant() /*&& !B.contains(g)*/) {
                    B.add(g); // gcd(a,g) == 1 and gcd(g,c) == 1 for c != b in P, B
                }
            }
            if (!b.isZERO() && !b.isConstant() /*&& !B.contains(b)*/) {
                B.add(b); // gcd(a,b) == 1 and gcd(b,c) == 1 for c != b in P, B
            }
        }
        if (!a.isZERO() && !a.isConstant() /*&& !B.contains(a)*/) {
            B.add(a);
        }
        return B;
    }


    /**
     * GenPolynomial test for co-prime list.
     * @param A list of GenPolynomials.
     * @return true if gcd(b,c) = 1 for all b != c in B, else false.
     */
    public boolean isCoPrime(List<GenPolynomial<C>> A) {
        if (A == null || A.isEmpty()) {
            return true;
        }
        if (A.size() == 1) {
            return true;
        }
        for (int i = 0; i < A.size(); i++) {
            GenPolynomial<C> a = A.get(i);
            for (int j = i + 1; j < A.size(); j++) {
                GenPolynomial<C> b = A.get(j);
                GenPolynomial<C> g = gcd(a, b);
                if (!g.isONE()) {
                    System.out.println("not co-prime, a: " + a);
                    System.out.println("not co-prime, b: " + b);
                    System.out.println("not co-prime, g: " + g);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * GenPolynomial test for co-prime list of given list.
     * @param A list of GenPolynomials.
     * @param P list of co-prime GenPolynomials.
     * @return true if isCoPrime(P) and for all a in A exists p in P with p | a,
     *         else false.
     */
    public boolean isCoPrime(List<GenPolynomial<C>> P, List<GenPolynomial<C>> A) {
        if (!isCoPrime(P)) {
            return false;
        }
        if (A == null || A.isEmpty()) {
            return true;
        }
        for (GenPolynomial<C> q : A) {
            if (q.isZERO() || q.isConstant()) {
                continue;
            }
            boolean divides = false;
            for (GenPolynomial<C> p : P) {
                GenPolynomial<C> a = PolyUtil.<C> basePseudoRemainder(q, p);
                if (a.isZERO()) { // p divides q
                    divides = true;
                    break;
                }
            }
            if (!divides) {
                System.out.println("no divisor for: " + q);
                return false;
            }
        }
        return true;
    }


    /**
     * Univariate GenPolynomial extended greatest common divisor. Uses sparse
     * pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return [ gcd(P,S), a, b ] with a*P + b*S = gcd(P,S).
     */
    public GenPolynomial<C>[] baseExtendedGcd(GenPolynomial<C> P, GenPolynomial<C> S) {
        //return P.egcd(S);
        GenPolynomial<C>[] hegcd = baseHalfExtendedGcd(P,S);
        GenPolynomial<C>[] ret = (GenPolynomial<C>[]) new GenPolynomial[3];
        ret[0] = hegcd[0];
        ret[1] = hegcd[1];
        GenPolynomial<C> x = hegcd[0].subtract( hegcd[1].multiply(P) );
        GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(x, S);
        // assert qr[1].isZERO() 
        ret[2] = qr[0];
        return ret;
    }


    /**
     * Univariate GenPolynomial half extended greatest comon divisor.
     * Uses sparse pseudoRemainder for remainder.  
     * @param S GenPolynomial.
     * @return [ gcd(P,S), a ] with a*P + b*S = gcd(P,S).
     */
    public GenPolynomial<C>[] baseHalfExtendedGcd(GenPolynomial<C> P, GenPolynomial<C> S) {
        //if ( P == null ) {
        //    throw new IllegalArgumentException("null P not allowed");
        //}
        GenPolynomial<C>[] ret = (GenPolynomial<C>[]) new GenPolynomial[2];
        ret[0] = null;
        ret[1] = null;
        if ( S == null || S.isZERO() ) {
            ret[0] = P;
            ret[1] = P.ring.getONE();
            return ret;
        }
        if ( P == null || P.isZERO() ) {
            ret[0] = S;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        if ( P.ring.nvar != 1 ) {
             throw new RuntimeException(this.getClass().getName()
                                      + " not univariate polynomials " + P.ring);
        }
        GenPolynomial<C> q = P; 
        GenPolynomial<C> r = S;
        GenPolynomial<C> c1 = P.ring.getONE().clone();
        GenPolynomial<C> d1 = P.ring.getZERO().clone();
        while ( !r.isZERO() ) {
            GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(q, r); 
                                    //q.divideAndRemainder(r);
            q = qr[0];
            GenPolynomial<C> x = c1.subtract( q.multiply(d1) );
            c1 = d1; 
            d1 = x; 
            q = r;
            r = qr[1];
        }
        // normalize ldcf(q) to 1, i.e. make monic
        C g = q.leadingBaseCoefficient();
        if ( g.isUnit() ) {
            C h = g.inverse();
            q = q.multiply( h );
            c1 = c1.multiply( h );
        }
        //assert ( ((c1.multiply(P)).remainder(S).equals(q) )); 
        ret[0] = q;
        ret[1] = c1;
        return ret;
    }


    /**
     * Univariate GenPolynomial greatest common divisor diophantine version. 
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @param c univariate GenPolynomial.
     * @return [ a, b ] with a*P + b*S = c and deg(a) < deg(S).
     */
    public GenPolynomial<C>[] baseGcdDiophant(GenPolynomial<C> P, GenPolynomial<C> S, GenPolynomial<C> c) {
        GenPolynomial<C>[] egcd = baseExtendedGcd(P,S);
        GenPolynomial<C> g = egcd[0];
        GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(c, g);
        if ( !qr[1].isZERO() ) {
            throw new RuntimeException("not solvable, r = " + qr[1] + ", c = " + c + ", g = " + g);
        }
        GenPolynomial<C> q = qr[0];
        GenPolynomial<C> a = egcd[1].multiply(q);
        GenPolynomial<C> b = egcd[2].multiply(q);
        if ( !a.isZERO() && a.degree(0) >= S.degree(0) ) {
            qr = PolyUtil.<C> basePseudoQuotientRemainder(a, S);
            a = qr[1];
            b = b.sum( P.multiply( qr[0] ) );
        }
        GenPolynomial<C>[] ret = (GenPolynomial<C>[]) new GenPolynomial[2];
        ret[0] = a;
        ret[1] = b;

        if ( true ) {
            return ret;
        }

        GenPolynomial<C> y = ret[0].multiply(P).sum( ret[1].multiply(S) );
        if ( !y.equals(c) ) {
            System.out.println("P  = " + P);
            System.out.println("S  = " + S);
            System.out.println("c  = " + c);
            System.out.println("a  = " + a);
            System.out.println("b  = " + b);
            System.out.println("y  = " + y);
            throw new RuntimeException("not diophant, x = " + y.subtract(c));
        }

        return ret;
    }


    /**
     * Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return [ A0, Ap, As ] with A/(P*S) = A0 + Ap/P + As/S with deg(Ap) < deg(P) and deg(As) < deg(S).
     */
    public GenPolynomial<C>[] basePartialFraction(GenPolynomial<C> A, GenPolynomial<C> P, GenPolynomial<C> S) {
        GenPolynomial<C>[] ret = (GenPolynomial<C>[]) new GenPolynomial[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        GenPolynomial<C> ps = P.multiply(S);
        GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(A, ps);
        ret[0] = qr[0];
        GenPolynomial<C> r = qr[1];
        GenPolynomial<C>[] diop = baseGcdDiophant(S,P,r); // switch arguments

//         GenPolynomial<C> x = diop[0].multiply(S).sum( diop[1].multiply(P) );
//         if ( !x.equals(r) ) {
//             System.out.println("r  = " + r);
//             System.out.println("x  = " + x);
//             throw new RuntimeException("not partial fraction, x = " + x);
//         }

        ret[1] = diop[0];
        ret[2] = diop[1];
        if ( ret[1].degree(0) >= P.degree(0) ) {
            qr = PolyUtil.<C> basePseudoQuotientRemainder(ret[1], P);
            ret[0] = ret[0].sum( qr[0] );
            ret[1] = qr[1];
        }
        if ( ret[2].degree(0) >= S.degree(0) ) {
            qr = PolyUtil.<C> basePseudoQuotientRemainder(ret[2], S);
            ret[0] = ret[0].sum( qr[0] );
            ret[2] = qr[1];
        }
        return ret;
    }


    /**
     * Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param P univariate GenPolynomial.
     * @param e exponent for P.
     * @return [ F0, F1, ..., Fe ] with A/(P^e) = sum( Fi / P^i ) with deg(Fi) < deg(P).
     */
    public List<GenPolynomial<C>> basePartialFraction(GenPolynomial<C> A, GenPolynomial<C> P, int e) {
        if ( A == null || P == null || e == 0 ) {
            throw new IllegalArgumentException("null A, P or e = 0 not allowed");
        }
        List<GenPolynomial<C>> pf = new ArrayList<GenPolynomial<C>>( e );
        if ( A.isZERO() ) {
            for ( int i = 0; i < e; i++ ) {
                 pf.add(A);
            }
            return pf;
        }
        if ( e == 1 ) {
            GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(A, P);
            pf.add(qr[0]);
            pf.add(qr[1]);
            return pf;
        }
        GenPolynomial<C> a = A;
        for ( int j = e; j > 0; j-- ) {
            GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(a, P);
            a = qr[0];
            pf.add(0,qr[1]);
        }
        pf.add(0,a);
        return pf;
    }


    /**
     * Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param D list of co-prime univariate GenPolynomials.
     * @return [ A0, A1,..., An ] with A/prod(D) = A0 + sum( Ai/Di ) with deg(Ai) < deg(Di).
     */
    public List<GenPolynomial<C>> basePartialFraction(GenPolynomial<C> A, List<GenPolynomial<C>> D) {
        if ( D == null || A == null ) {
            throw new IllegalArgumentException("null A or D not allowed");
        }
        List<GenPolynomial<C>> pf = new ArrayList<GenPolynomial<C>>( D.size()+1 );
        if ( A.isZERO() || D.size() == 0 ) {
            pf.add(A);
            for ( int i = 0; i < D.size(); i++ ) {
                 pf.add(A);
            }
            return pf;
        }
        List<GenPolynomial<C>> Dp = new ArrayList<GenPolynomial<C>>( D.size()-1 );
        GenPolynomial<C> P = A.ring.getONE();
        GenPolynomial<C> d1 = null;
        for ( GenPolynomial<C> d : D ) {
            if ( d1 == null ) {
                d1 = d;
            } else {
                P = P.multiply(d);
                Dp.add(d);
            }
        }
        GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(A, P.multiply(d1));
        GenPolynomial<C> A0 = qr[0];
        GenPolynomial<C> r = qr[1];
        if ( D.size() == 1 ) {
            pf.add(A0);
            pf.add(r);
            return pf;
        }
        GenPolynomial<C>[] diop = baseGcdDiophant(P,d1,r); // switch arguments
        GenPolynomial<C> A1 = diop[0];
        GenPolynomial<C> S = diop[1];
        List<GenPolynomial<C>> Fr = basePartialFraction(S,Dp);
        A0 = A0.sum( Fr.remove(0) ); 
        pf.add(A0);
        pf.add(A1);
        pf.addAll(Fr);
        return pf;
    }


    /**
     * Test for Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param D list of (co-prime) univariate GenPolynomials.
     * @param F list of univariate GenPolynomials from a partial fraction computation.
     * @return true if A/prod(D) = F0 + sum( Fi/Di ) with deg(Fi) < deg(Di), Fi in F, 
               else false.
     */
    public boolean isBasePartialFraction(GenPolynomial<C> A, List<GenPolynomial<C>> D, List<GenPolynomial<C>> F) {
        if ( D == null || A == null || F == null ) {
            throw new IllegalArgumentException("null A, F or D not allowed");
        }
        if ( D.size() != F.size()-1 ) {
            return false;
        }
        // A0*prod(D) + sum( Ai * Dip ), Dip = prod(D,j!=i)
        GenPolynomial<C> P = A.ring.getONE();
        for ( GenPolynomial<C> d : D ) {
                P = P.multiply(d);
        }
        List<GenPolynomial<C>> Fp = new ArrayList<GenPolynomial<C>>( F );
        GenPolynomial<C> A0 = Fp.remove(0).multiply(P);
        //System.out.println("A0 = " + A0);
        int j = 0;
        for ( GenPolynomial<C> Fi : Fp ) {
            P = A.ring.getONE();
            int i = 0;
            for ( GenPolynomial<C> d : D ) {
                if ( i != j ) {
                    P = P.multiply(d);
                }
                i++;
            }
            //System.out.println("Fi = " + Fi);
            //System.out.println("P  = " + P);
            A0 = A0.sum( Fi.multiply(P) );
            //System.out.println("A0 = " + A0);
            j++;
        }
        boolean t = A.equals(A0);
        if ( ! t ) {
            System.out.println("not isPartFrac = " + A0);
        }
        return t;
    }


    /**
     * Test for Univariate GenPolynomial partial fraction decomposition. 
     * @param A univariate GenPolynomial.
     * @param P univariate GenPolynomial.
     * @param e exponent for P.
     * @param F list of univariate GenPolynomials from a partial fraction computation.
     * @return true if A/(P^e) = F0 + sum( Fi / P^i ) with deg(Fi) < deg(P), Fi in F, 
               else false.
     */
    public boolean isBasePartialFraction(GenPolynomial<C> A, GenPolynomial<C> P, int e, List<GenPolynomial<C>> F) {
        if ( A == null || P == null || F == null || e == 0 ) {
            throw new IllegalArgumentException("null A, P, F or e = 0 not allowed");
        }
        GenPolynomial<C> A0 = basePartialFractionValue(P,e,F);
//         A.ring.getZERO();
//         for ( GenPolynomial<C> Fi : F ) {
//             A0 = A0.multiply(P);
//             A0 = A0.sum(Fi);
//             //System.out.println("A0 = " + A0);
//         }
        boolean t = A.equals(A0);
        if ( ! t ) {
            System.out.println("not isPartFrac = " + A0);
        }
        return t;
    }


    /**
     * Test for Univariate GenPolynomial partial fraction decomposition. 
     * @param P univariate GenPolynomial.
     * @param e exponent for P.
     * @param F list of univariate GenPolynomials from a partial fraction computation.
     * @return (F0 + sum( Fi / P^i )) * P^e.
     */
    public GenPolynomial<C> basePartialFractionValue(GenPolynomial<C> P, int e, List<GenPolynomial<C>> F) {
        if ( P == null || F == null || e == 0 ) {
            throw new IllegalArgumentException("null P, F or e = 0 not allowed");
        }
        GenPolynomial<C> A0 = P.ring.getZERO();
        for ( GenPolynomial<C> Fi : F ) {
            A0 = A0.multiply(P);
            A0 = A0.sum(Fi);
            //System.out.println("A0 = " + A0);
        }
        return A0;
    }

}

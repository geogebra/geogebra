/*
 * $Id: HenselUtil.java 3128 2010-05-13 18:02:25Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.BitSet;

import org.apache.log4j.Logger;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;

import edu.jas.arith.BigInteger;
import edu.jas.arith.Modular;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.Monomial;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModularRingFactory;
import edu.jas.structure.UnaryFunctor;

import edu.jas.util.ListUtil;


/**
 * Hensel utilities for ufd.
 * @author Heinz Kredel
 */

public class HenselUtil {


    private static final Logger logger = Logger.getLogger(HenselUtil.class);


    private static boolean debug = true || logger.isDebugEnabled();


    /**
     * Modular Hensel lifting algorithm on coefficients. Let p =
     * A.ring.coFac.modul() = B.ring.coFac.modul() and assume C == A*B mod p
     * with ggt(A,B) == 1 mod p and S A + T B == 1 mod p. See Algorithm 6.1. in
     * Geddes et.al.. Linear version, as it does not lift S A + T B == 1 mod
     * p^{e+1}.
     * @param C GenPolynomial
     * @param A GenPolynomial
     * @param B other GenPolynomial
     * @param S GenPolynomial
     * @param T GenPolynomial
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1,Am,Bm] = lift(C,A,B), with C = A1 * B1 mod p^e, Am = A1 mod p^e, Bm = B1 mod p^e .
     */
    @SuppressWarnings("unchecked")
    public static <MOD extends GcdRingElem<MOD> & Modular>
      HenselApprox<MOD> liftHensel(GenPolynomial<BigInteger> C, BigInteger M, 
                                   GenPolynomial<MOD> A, GenPolynomial<MOD> B, 
                                   GenPolynomial<MOD> S, GenPolynomial<MOD> T) throws NoLiftingException {
        if (C == null || C.isZERO()) {
            return new HenselApprox<MOD>(C,C,A,B);
        }
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<MOD> pfac = A.ring;
        RingFactory<MOD> p = pfac.coFac;
        RingFactory<MOD> q = p;
        ModularRingFactory<MOD> P = (ModularRingFactory<MOD>) p;
        ModularRingFactory<MOD> Q = (ModularRingFactory<MOD>) q;
        BigInteger Qi = Q.getIntegerModul();
        BigInteger M2 = M.multiply(M.fromInteger(2));
        BigInteger Mq = Qi;

        // normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        MOD a = A.leadingBaseCoefficient();
        if (!a.isONE()) { // A = A.monic();
            A = A.divide(a);
            S = S.multiply(a);
        }
        MOD b = B.leadingBaseCoefficient();
        if (!b.isONE()) { // B = B.monic();
            B = B.divide(b);
            T = T.multiply(b);
        }
        MOD ci = P.fromInteger(c.getVal());
        A = A.multiply(ci);
        B = B.multiply(ci);
        T = T.divide(ci);
        S = S.divide(ci);
        Ai = PolyUtil.integerFromModularCoefficients(fac, A);
        Bi = PolyUtil.integerFromModularCoefficients(fac, B);
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.doPutToMap(ea, c);
        Bi.doPutToMap(eb, c);

        // polynomials mod p
        GenPolynomial<MOD> Ap;
        GenPolynomial<MOD> Bp;
        GenPolynomial<MOD> A1p = A;
        GenPolynomial<MOD> B1p = B;
        GenPolynomial<MOD> Ep;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;

        while (Mq.compareTo(M2) < 0) {
            // compute E=(C-AB)/q over the integers
            E = C.subtract(Ai.multiply(Bi));
            if (E.isZERO()) {
                //System.out.println("leaving on zero error");
                logger.info("leaving on zero error");
                break;
            }
            try {
                E = E.divide(Qi);
            } catch (RuntimeException e) {
                // useful in debuging
                //System.out.println("C  = " + C );
                //System.out.println("Ai = " + Ai );
                //System.out.println("Bi = " + Bi );
                //System.out.println("E  = " + E );
                //System.out.println("Qi = " + Qi );
                throw e;
            }
            // E mod p
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(pfac, E);
            //logger.info("Ep = " + Ep);

            // construct approximation mod p
            Ap = S.multiply(Ep); // S,T ++ T,S
            Bp = T.multiply(Ep);
            GenPolynomial<MOD>[] QR;
            QR = Ap.divideAndRemainder(B);
            GenPolynomial<MOD> Qp;
            GenPolynomial<MOD> Rp;
            Qp = QR[0];
            Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum(A.multiply(Qp));

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac, A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac, B1p);
            Ea1 = Ea.multiply(Qi);
            Eb1 = Eb.multiply(Qi);

            Ea = Ai.sum(Eb1); // Eb1 and Ea1 are required
            Eb = Bi.sum(Ea1); //--------------------------
            assert (Ea.degree(0) + Eb.degree(0) <= C.degree(0));
            //if ( Ea.degree(0)+Eb.degree(0) > C.degree(0) ) { // debug
            //   throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            //}

            // prepare for next iteration
            Mq = Qi;
            Qi = Q.getIntegerModul().multiply(P.getIntegerModul());
            // Q = new ModIntegerRing(Qi.getVal());
            if ( ModLongRing.MAX_LONG.compareTo( Qi.getVal() ) > 0 ) {
                Q = (ModularRingFactory) new ModLongRing(Qi.getVal());
            } else {
                Q = (ModularRingFactory) new ModIntegerRing(Qi.getVal());
            }
            Ai = Ea;
            Bi = Eb;
        }
        GreatestCommonDivisorAbstract<BigInteger> ufd = new GreatestCommonDivisorPrimitive<BigInteger>();

        // remove normalization
        BigInteger ai = ufd.baseContent(Ai);
        Ai = Ai.divide(ai);
        BigInteger bi = null;
        try {
            bi = c.divide(ai);
            Bi = Bi.divide(bi); // divide( c/a )
        } catch (RuntimeException e) {
            //System.out.println("C  = " + C );
            //System.out.println("Ai = " + Ai );
            //System.out.println("Bi = " + Bi );
            //System.out.println("c  = " + c );
            //System.out.println("ai = " + ai );
            //System.out.println("bi = " + bi );
            //System.out.println("no exact lifting possible " + e);
            throw new NoLiftingException("no exact lifting possible " +e);
        }
        return new HenselApprox<MOD>(Ai,Bi,A1p,B1p);
    }


    /**
     * Modular Hensel lifting algorithm on coefficients. Let p =
     * A.ring.coFac.modul() = B.ring.coFac.modul() and assume C == A*B mod p
     * with ggt(A,B) == 1 mod p. See algorithm 6.1. in Geddes et.al. and
     * algorithms 3.5.{5,6} in Cohen. 
     * @param C GenPolynomial
     * @param A GenPolynomial
     * @param B other GenPolynomial
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked")
    public static <MOD extends GcdRingElem<MOD> & Modular>
      HenselApprox<MOD> liftHensel(GenPolynomial<BigInteger> C, BigInteger M, GenPolynomial<MOD> A, GenPolynomial<MOD> B) throws NoLiftingException {
        if (C == null || C.isZERO()) {
            return new HenselApprox<MOD>(C,C,A,B);
        }
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // one Hensel step on part polynomials
        GenPolynomial<MOD>[] gst = A.egcd(B);
        if (!gst[0].isONE()) {
            throw new NoLiftingException("A and B not coprime, gcd = " + gst[0] + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<MOD> s = gst[1];
        GenPolynomial<MOD> t = gst[2];
        HenselApprox<MOD> ab = HenselUtil.<MOD> liftHensel(C, M, A, B, s, t);
        return ab;
    }


    /**
     * Modular quadratic Hensel lifting algorithm on coefficients. Let p =
     * A.ring.coFac.modul() = B.ring.coFac.modul() and assume C == A*B mod p
     * with ggt(A,B) == 1 mod p and S A + T B == 1 mod p. See algorithm 6.1. in
     * Geddes et.al. and algorithms 3.5.{5,6} in Cohen. Quadratic version, as it
     * also lifts S A + T B == 1 mod p^{e+1}.
     * @param C GenPolynomial
     * @param A GenPolynomial
     * @param B other GenPolynomial
     * @param S GenPolynomial
     * @param T GenPolynomial
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked")
    public static <MOD extends GcdRingElem<MOD> & Modular>
      HenselApprox<MOD> liftHenselQuadratic(GenPolynomial<BigInteger> C, BigInteger M,
                                            GenPolynomial<MOD> A, GenPolynomial<MOD> B, 
                                            GenPolynomial<MOD> S, GenPolynomial<MOD> T) throws NoLiftingException {
        if (C == null || C.isZERO()) {
            return new HenselApprox<MOD>(C,C,A,B);
        }
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<MOD> pfac = A.ring;
        RingFactory<MOD> p = pfac.coFac;
        RingFactory<MOD> q = p;
        ModularRingFactory<MOD> P = (ModularRingFactory<MOD>) p;
        ModularRingFactory<MOD> Q = (ModularRingFactory<MOD>) q;
        BigInteger Qi = Q.getIntegerModul();
        BigInteger M2 = M.multiply(M.fromInteger(2));
        BigInteger Mq = Qi;
        GenPolynomialRing<MOD> qfac;
        qfac = new GenPolynomialRing<MOD>(Q, pfac);

        // normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        MOD a = A.leadingBaseCoefficient();
        if (!a.isONE()) { // A = A.monic();
            A = A.divide(a);
            S = S.multiply(a);
        }
        MOD b = B.leadingBaseCoefficient();
        if (!b.isONE()) { // B = B.monic();
            B = B.divide(b);
            T = T.multiply(b);
        }
        MOD ci = P.fromInteger(c.getVal());
        A = A.multiply(ci);
        B = B.multiply(ci);
        T = T.divide(ci);
        S = S.divide(ci);
        Ai = PolyUtil.integerFromModularCoefficients(fac, A);
        Bi = PolyUtil.integerFromModularCoefficients(fac, B);
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.doPutToMap(ea, c);
        Bi.doPutToMap(eb, c);

        // polynomials mod p
        GenPolynomial<MOD> Ap;
        GenPolynomial<MOD> Bp;
        GenPolynomial<MOD> A1p = A;
        GenPolynomial<MOD> B1p = B;
        GenPolynomial<MOD> Ep;
        GenPolynomial<MOD> Sp = S;
        GenPolynomial<MOD> Tp = T;

        // polynomials mod q
        GenPolynomial<MOD> Aq;
        GenPolynomial<MOD> Bq;
        GenPolynomial<MOD> Eq;

        // polynomials over the integers
        GenPolynomial<BigInteger> E;
        GenPolynomial<BigInteger> Ea;
        GenPolynomial<BigInteger> Eb;
        GenPolynomial<BigInteger> Ea1;
        GenPolynomial<BigInteger> Eb1;
        GenPolynomial<BigInteger> Si;
        GenPolynomial<BigInteger> Ti;

        Si = PolyUtil.integerFromModularCoefficients(fac, S);
        Ti = PolyUtil.integerFromModularCoefficients(fac, T);

        Aq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ai);
        Bq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Bi);

        while (Mq.compareTo(M2) < 0) {
            // compute E=(C-AB)/q over the integers
            E = C.subtract(Ai.multiply(Bi));
            if (E.isZERO()) {
                //System.out.println("leaving on zero error");
                logger.info("leaving on zero error");
                break;
            }
            E = E.divide(Qi);
            // E mod p
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(qfac, E);
            //logger.info("Ep = " + Ep + ", qfac = " + qfac);
            if (Ep.isZERO()) {
                //System.out.println("leaving on zero error");
                //??logger.info("leaving on zero error Ep");
                //??break;
            }

            // construct approximation mod p
            Ap = Sp.multiply(Ep); // S,T ++ T,S
            Bp = Tp.multiply(Ep);
            GenPolynomial<MOD>[] QR;
            //logger.info("Ap = " + Ap + ", Bp = " + Bp + ", fac(Ap) = " + Ap.ring);
            QR = Ap.divideAndRemainder(Bq);
            GenPolynomial<MOD> Qp;
            GenPolynomial<MOD> Rp;
            Qp = QR[0];
            Rp = QR[1];
            //logger.info("Qp = " + Qp + ", Rp = " + Rp);
            A1p = Rp;
            B1p = Bp.sum(Aq.multiply(Qp));

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac, A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac, B1p);
            Ea1 = Ea.multiply(Qi);
            Eb1 = Eb.multiply(Qi);
            Ea = Ai.sum(Eb1); // Eb1 and Ea1 are required
            Eb = Bi.sum(Ea1); //--------------------------
            assert (Ea.degree(0) + Eb.degree(0) <= C.degree(0));
            //if ( Ea.degree(0)+Eb.degree(0) > C.degree(0) ) { // debug
            //   throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            //}
            Ai = Ea;
            Bi = Eb;

            // gcd representation factors error --------------------------------
            // compute E=(1-SA-TB)/q over the integers
            E = fac.getONE();
            E = E.subtract(Si.multiply(Ai)).subtract(Ti.multiply(Bi));
            E = E.divide(Qi);
            // E mod q
            Ep = PolyUtil.<MOD> fromIntegerCoefficients(qfac, E);
            //logger.info("Ep2 = " + Ep);

            // construct approximation mod q
            Ap = Sp.multiply(Ep); // S,T ++ T,S
            Bp = Tp.multiply(Ep);
            QR = Bp.divideAndRemainder(Aq); // Ai == A mod p ?
            Qp = QR[0];
            Rp = QR[1];
            B1p = Rp;
            A1p = Ap.sum(Bq.multiply(Qp));

            if (false && debug) {
                Eq = A1p.multiply(Aq).sum(B1p.multiply(Bq)).subtract(Ep);
                if (!Eq.isZERO()) {
                    System.out.println("A*A1p+B*B1p-Ep2 != 0 " + Eq);
                    throw new RuntimeException("A*A1p+B*B1p-Ep2 != 0 mod " + Q.getIntegerModul());
                }
            }

            // construct q-adic approximation, convert to integer
            Ea = PolyUtil.integerFromModularCoefficients(fac, A1p);
            Eb = PolyUtil.integerFromModularCoefficients(fac, B1p);
            Ea1 = Ea.multiply(Qi);
            Eb1 = Eb.multiply(Qi);
            Ea = Si.sum(Ea1); // Eb1 and Ea1 are required
            Eb = Ti.sum(Eb1); //--------------------------
            Si = Ea;
            Ti = Eb;

            // prepare for next iteration
            Mq = Qi;
            Qi = Q.getIntegerModul().multiply(Q.getIntegerModul());
            if ( ModLongRing.MAX_LONG.compareTo( Qi.getVal() ) > 0 ) {
                Q = (ModularRingFactory) new ModLongRing(Qi.getVal());
            } else {
                Q = (ModularRingFactory) new ModIntegerRing(Qi.getVal());
            }
            //Q = new ModIntegerRing(Qi.getVal());
            //System.out.println("Q = " + Q + ", from Q = " + Mq);

            qfac = new GenPolynomialRing<MOD>(Q, pfac);

            Aq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ai);
            Bq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Bi);
            Sp = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Si);
            Tp = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ti);
            if (false && debug) {
                E = Ai.multiply(Si).sum(Bi.multiply(Ti));
                Eq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, E);
                if (!Eq.isONE()) {
                    System.out.println("Ai*Si+Bi*Ti=1 " + Eq);
                    throw new RuntimeException("Ai*Si+Bi*Ti != 1 mod " + Q.getIntegerModul());
                }
            }
        }
        GreatestCommonDivisorAbstract<BigInteger> ufd = new GreatestCommonDivisorPrimitive<BigInteger>();

        // remove normalization if possible
        BigInteger ai = ufd.baseContent(Ai);
        Ai = Ai.divide(ai);
        BigInteger bi = null;
        try {
            bi = c.divide(ai);
            Bi = Bi.divide(bi); // divide( c/a )
        } catch (RuntimeException e) {
            //System.out.println("C  = " + C );
            //System.out.println("Ai = " + Ai );
            //System.out.println("Bi = " + Bi );
            //System.out.println("c  = " + c );
            //System.out.println("ai = " + ai );
            //System.out.println("bi = " + bi );
            //System.out.println("no exact lifting possible " + e);
            throw new NoLiftingException("no exact lifting possible " +e);
        }
        return new HenselApprox<MOD>(Ai,Bi,A1p,B1p);
    }


    /**
     * Modular quadratic Hensel lifting algorithm on coefficients. Let p =
     * A.ring.coFac.modul() = B.ring.coFac.modul() and assume C == A*B mod p
     * with ggt(A,B) == 1 mod p. See algorithm 6.1. in Geddes et.al. and
     * algorithms 3.5.{5,6} in Cohen. Quadratic version.
     * @param C GenPolynomial
     * @param A GenPolynomial
     * @param B other GenPolynomial
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked")
    public static <MOD extends GcdRingElem<MOD> & Modular>
      HenselApprox<MOD> liftHenselQuadratic(GenPolynomial<BigInteger> C, BigInteger M, 
                                            GenPolynomial<MOD> A, GenPolynomial<MOD> B) throws NoLiftingException {
        if (C == null || C.isZERO()) {
            return new HenselApprox<MOD>(C,C,A,B);
        }
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // one Hensel step on part polynomials
        GenPolynomial<MOD>[] gst = A.egcd(B);
        if (!gst[0].isONE()) {
            throw new NoLiftingException("A and B not coprime, gcd = " + gst[0] + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<MOD> s = gst[1];
        GenPolynomial<MOD> t = gst[2];
        HenselApprox<MOD> ab = HenselUtil.<MOD> liftHenselQuadratic(C, M, A, B, s, t);
        return ab;
    }


    /**
     * Modular Hensel lifting algorithm on coefficients. Let p =
     * A.ring.coFac.modul() = B.ring.coFac.modul() and assume C == A*B mod p
     * with ggt(A,B) == 1 mod p. See algorithm 6.1. in Geddes et.al. and
     * algorithms 3.5.{5,6} in Cohen. Quadratic version.
     * @param C GenPolynomial
     * @param A GenPolynomial
     * @param B other GenPolynomial
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked")
    public static <MOD extends GcdRingElem<MOD> & Modular>
      HenselApprox<MOD> liftHenselQuadraticFac(GenPolynomial<BigInteger> C, BigInteger M,
                                               GenPolynomial<MOD> A, GenPolynomial<MOD> B) throws NoLiftingException {
        if (C == null || C.isZERO()) {
            throw new RuntimeException("C must be nonzero");
        }
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // one Hensel step on part polynomials
        GenPolynomial<MOD>[] gst = A.egcd(B);
        if (!gst[0].isONE()) {
            throw new NoLiftingException("A and B not coprime, gcd = " + gst[0] + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<MOD> s = gst[1];
        GenPolynomial<MOD> t = gst[2];
        HenselApprox<MOD> ab = HenselUtil.<MOD> liftHenselQuadraticFac(C, M, A, B, s, t);
        return ab;
    }


    /**
     * Modular Hensel lifting algorithm on coefficients. Let p =
     * A.ring.coFac.modul() = B.ring.coFac.modul() and assume C == A*B mod p
     * with ggt(A,B) == 1 mod p and S A + T B == 1 mod p. See algorithm 6.1. in
     * Geddes et.al. and algorithms 3.5.{5,6} in Cohen. Quadratic version, as it
     * also lifts S A + T B == 1 mod p^{e+1}.
     * @param C primitive GenPolynomial
     * @param A GenPolynomial
     * @param B other GenPolynomial
     * @param S GenPolynomial
     * @param T GenPolynomial
     * @param M bound on the coefficients of A1 and B1 as factors of C.
     * @return [A1,B1] = lift(C,A,B), with C = A1 * B1.
     */
    @SuppressWarnings("unchecked")
    public static <MOD extends GcdRingElem<MOD> & Modular>
      HenselApprox<MOD> liftHenselQuadraticFac(GenPolynomial<BigInteger> C, BigInteger M,
                                               GenPolynomial<MOD> A, GenPolynomial<MOD> B, 
                                               GenPolynomial<MOD> S, GenPolynomial<MOD> T) throws NoLiftingException {
        //System.out.println("*** version for factorization *** ");
        GenPolynomial<BigInteger>[] AB = (GenPolynomial<BigInteger>[]) new GenPolynomial[2];
        if (C == null || C.isZERO()) {
            throw new RuntimeException("C must be nonzero");
        }
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // setup factories
        GenPolynomialRing<MOD> pfac = A.ring;
        RingFactory<MOD> p = pfac.coFac;
        RingFactory<MOD> q = p;
        ModularRingFactory<MOD> P = (ModularRingFactory<MOD>) p;
        ModularRingFactory<MOD> Q = (ModularRingFactory<MOD>) q;
        BigInteger PP = Q.getIntegerModul();
        BigInteger Qi = PP;
        BigInteger M2 = M.multiply(M.fromInteger(2));
        if (debug) {
            //System.out.println("M2 =  " + M2);
            logger.debug("M2 =  " + M2);
        }
        BigInteger Mq = Qi;
        GenPolynomialRing<MOD> qfac;
        qfac = new GenPolynomialRing<MOD>(Q, pfac); // mod p
        GenPolynomialRing<MOD> mfac;
        BigInteger Mi = Q.getIntegerModul().multiply(Q.getIntegerModul());
        ModularRingFactory<MOD> Qmm;
        // = new ModIntegerRing(Mi.getVal());
        if ( ModLongRing.MAX_LONG.compareTo( Mi.getVal() ) > 0 ) {
            Qmm = (ModularRingFactory) new ModLongRing(Mi.getVal());
        } else {
            Qmm = (ModularRingFactory) new ModIntegerRing(Mi.getVal());
        }
        mfac = new GenPolynomialRing<MOD>(Qmm, qfac); // mod p^e
        MOD Qm = Qmm.fromInteger(Qi.getVal());

        // partly normalize c and a, b factors, assert p is prime
        GenPolynomial<BigInteger> Ai;
        GenPolynomial<BigInteger> Bi;
        BigInteger c = C.leadingBaseCoefficient();
        C = C.multiply(c); // sic
        MOD a = A.leadingBaseCoefficient();
        if (!a.isONE()) { // A = A.monic();
            A = A.divide(a);
            S = S.multiply(a);
        }
        MOD b = B.leadingBaseCoefficient();
        if (!b.isONE()) { // B = B.monic();
            B = B.divide(b);
            T = T.multiply(b);
        }
        MOD ci = P.fromInteger(c.getVal());
        if (ci.isZERO()) {
            System.out.println("c =  " + c);
            System.out.println("P =  " + P);
            throw new RuntimeException("c mod p == 0 not meaningful");
        }
        // mod p
        A = A.multiply(ci);
        S = S.divide(ci);
        B = B.multiply(ci);
        T = T.divide(ci);
        Ai = PolyUtil.integerFromModularCoefficients(fac, A);
        Bi = PolyUtil.integerFromModularCoefficients(fac, B);
        // replace leading base coefficients
        ExpVector ea = Ai.leadingExpVector();
        ExpVector eb = Bi.leadingExpVector();
        Ai.doPutToMap(ea, c);
        Bi.doPutToMap(eb, c);

        // polynomials mod p
        GenPolynomial<MOD> Ap;
        GenPolynomial<MOD> Bp;
        GenPolynomial<MOD> A1p = A;
        GenPolynomial<MOD> B1p = B;
        GenPolynomial<MOD> Sp = S;
        GenPolynomial<MOD> Tp = T;

        // polynomials mod q
        GenPolynomial<MOD> Aq;
        GenPolynomial<MOD> Bq;

        // polynomials mod p^e
        GenPolynomial<MOD> Cm;
        GenPolynomial<MOD> Am;
        GenPolynomial<MOD> Bm;
        GenPolynomial<MOD> Em;
        GenPolynomial<MOD> Emp;
        GenPolynomial<MOD> Sm;
        GenPolynomial<MOD> Tm;
        GenPolynomial<MOD> Ema;
        GenPolynomial<MOD> Emb;
        GenPolynomial<MOD> Ema1;
        GenPolynomial<MOD> Emb1;

        // polynomials over the integers
        GenPolynomial<BigInteger> Ei;
        GenPolynomial<BigInteger> Si;
        GenPolynomial<BigInteger> Ti;

        Si = PolyUtil.integerFromModularCoefficients(fac, S);
        Ti = PolyUtil.integerFromModularCoefficients(fac, T);

        Aq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ai);
        Bq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Bi);

        // polynomials mod p^e
        Cm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, C);
        Am = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Ai);
        Bm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Bi);
        Sm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Si);
        Tm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Ti);
        //System.out.println("Cm =  " + Cm);
        //System.out.println("Am =  " + Am);
        //System.out.println("Bm =  " + Bm);
        //System.out.println("Ai =  " + Ai);
        //System.out.println("Bi =  " + Bi);
        //System.out.println("mfac =  " + mfac);

        while (Mq.compareTo(M2) < 0) {
            // compute E=(C-AB)/p mod p^e
            if (debug) {
                //System.out.println("mfac =  " + Cm.ring);
                logger.debug("mfac =  " + Cm.ring);
            }
            Em = Cm.subtract(Am.multiply(Bm));
            //System.out.println("Em =  " + Em);
            if (Em.isZERO()) {
                if (C.subtract(Ai.multiply(Bi)).isZERO()) {
                    //System.out.println("leaving on zero error");
                    logger.info("leaving on zero error");
                    break;
                }
            }
            // Em = Em.divide( Qm );
            Ei = PolyUtil.integerFromModularCoefficients(fac, Em);
            Ei = Ei.divide(Qi);
            //System.out.println("Ei =  " + Ei);

            // Ei mod p
            Emp = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ei);
            //            Emp = PolyUtil.<MOD>fromIntegerCoefficients(qfac,
            //               PolyUtil.integerFromModularCoefficients(fac,Em) ); 
            //System.out.println("Emp =  " + Emp);
            //logger.info("Emp = " + Emp);
            if (Emp.isZERO()) {
                if (C.subtract(Ai.multiply(Bi)).isZERO()) {
                    //System.out.println("leaving on zero error Emp");
                    logger.info("leaving on zero error Emp");
                    break;
                }
            }

            // construct approximation mod p
            Ap = Sp.multiply(Emp); // S,T ++ T,S 
            Bp = Tp.multiply(Emp);
            GenPolynomial<MOD>[] QR = null;
            QR = Ap.divideAndRemainder(Bq); // Bq !
            GenPolynomial<MOD> Qp = QR[0];
            GenPolynomial<MOD> Rp = QR[1];
            A1p = Rp;
            B1p = Bp.sum(Aq.multiply(Qp)); // Aq !
            //System.out.println("A1p =  " + A1p);
            //System.out.println("B1p =  " + B1p);

            // construct q-adic approximation
            Ema = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(fac, A1p));
            Emb = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(fac, B1p));
            //System.out.println("Ema =  " + Ema);
            //System.out.println("Emb =  " + Emb);
            Ema1 = Ema.multiply(Qm);
            Emb1 = Emb.multiply(Qm);
            Ema = Am.sum(Emb1); // Eb1 and Ea1 are required
            Emb = Bm.sum(Ema1); //--------------------------
            assert (Ema.degree(0) + Emb.degree(0) <= Cm.degree(0));
            //if ( Ema.degree(0)+Emb.degree(0) > Cm.degree(0) ) { // debug
            //   throw new RuntimeException("deg(A)+deg(B) > deg(C)");
            //}
            Am = Ema;
            Bm = Emb;
            Ai = PolyUtil.integerFromModularCoefficients(fac, Am);
            Bi = PolyUtil.integerFromModularCoefficients(fac, Bm);
            //System.out.println("Am =  " + Am);
            //System.out.println("Bm =  " + Bm);
            //System.out.println("Ai =  " + Ai);
            //System.out.println("Bi =  " + Bi + "\n");

            // gcd representation factors error --------------------------------
            // compute E=(1-SA-TB)/p mod p^e
            Em = mfac.getONE();
            Em = Em.subtract(Sm.multiply(Am)).subtract(Tm.multiply(Bm));
            //System.out.println("Em  =  " + Em);
            // Em = Em.divide( Pm );

            Ei = PolyUtil.integerFromModularCoefficients(fac, Em);
            Ei = Ei.divide(Qi);
            //System.out.println("Ei =  " + Ei);
            // Ei mod p
            Emp = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ei);
            //Emp = PolyUtil.<MOD>fromIntegerCoefficients(qfac,
            //               PolyUtil.integerFromModularCoefficients(fac,Em) );
            //System.out.println("Emp =  " + Emp);

            // construct approximation mod p
            Ap = Sp.multiply(Emp); // S,T ++ T,S // Ep Eqp
            Bp = Tp.multiply(Emp); // Ep Eqp
            QR = Bp.divideAndRemainder(Aq); // Ap Aq ! // Ai == A mod p ?
            Qp = QR[0];
            Rp = QR[1];
            B1p = Rp;
            A1p = Ap.sum(Bq.multiply(Qp));
            //System.out.println("A1p =  " + A1p);
            //System.out.println("B1p =  " + B1p);

            // construct p^e-adic approximation
            Ema = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(fac, A1p));
            Emb = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(fac, B1p));
            Ema1 = Ema.multiply(Qm);
            Emb1 = Emb.multiply(Qm);
            Ema = Sm.sum(Ema1); // Emb1 and Ema1 are required
            Emb = Tm.sum(Emb1); //--------------------------
            Sm = Ema;
            Tm = Emb;
            Si = PolyUtil.integerFromModularCoefficients(fac, Sm);
            Ti = PolyUtil.integerFromModularCoefficients(fac, Tm);
            //System.out.println("Sm =  " + Sm);
            //System.out.println("Tm =  " + Tm);
            //System.out.println("Si =  " + Si);
            //System.out.println("Ti =  " + Ti + "\n");

            // prepare for next iteration
            Qi = Q.getIntegerModul().multiply(Q.getIntegerModul());
            Mq = Qi;
            //lmfac = mfac;
            // Q = new ModIntegerRing(Qi.getVal());
            if ( ModLongRing.MAX_LONG.compareTo( Qi.getVal() ) > 0 ) {
                Q = (ModularRingFactory) new ModLongRing(Qi.getVal());
            } else {
                Q = (ModularRingFactory) new ModIntegerRing(Qi.getVal());
            }
            qfac = new GenPolynomialRing<MOD>(Q, pfac);
            BigInteger Qmmi = Qmm.getIntegerModul().multiply(Qmm.getIntegerModul());
            //Qmm = new ModIntegerRing(Qmmi.getVal());
            if ( ModLongRing.MAX_LONG.compareTo( Qmmi.getVal() ) > 0 ) {
                Qmm = (ModularRingFactory) new ModLongRing(Qmmi.getVal());
            } else {
                Qmm = (ModularRingFactory) new ModIntegerRing(Qmmi.getVal());
            }
            mfac = new GenPolynomialRing<MOD>(Qmm, qfac);
            Qm = Qmm.fromInteger(Qi.getVal());

            Cm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, C);
            Am = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Ai);
            Bm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Bi);
            Sm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Si);
            Tm = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Ti);

            assert (isHenselLift(C, Mi, PP, Ai, Bi));
            Mi = Mi.fromInteger(Qmm.getIntegerModul().getVal());

            Aq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ai);
            Bq = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Bi);
            Sp = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Si);
            Tp = PolyUtil.<MOD> fromIntegerCoefficients(qfac, Ti);

            //System.out.println("Am =  " + Am);
            //System.out.println("Bm =  " + Bm);
            //System.out.println("Sm =  " + Sm);
            //System.out.println("Tm =  " + Tm);
            //System.out.println("mfac =  " + mfac);
            //System.out.println("Qmm = " + Qmm + ", M2   =  " + M2 + ", Mq   =  " + Mq + "\n");
        }
        //System.out.println("*Ai =  " + Ai);
        //System.out.println("*Bi =  " + Bi);

        Em = Cm.subtract(Am.multiply(Bm));
        if (!Em.isZERO()) {
            System.out.println("Em =  " + Em);
            //throw new NoLiftingException("no exact lifting possible");
        }
        // remove normalization not possible when not exact factorization
        GreatestCommonDivisorAbstract<BigInteger> ufd = new GreatestCommonDivisorPrimitive<BigInteger>();
        // remove normalization if possible
        BigInteger ai = ufd.baseContent(Ai);
        Ai = Ai.divide(ai); // Ai=pp(Ai)
        BigInteger[] qr = c.divideAndRemainder(ai);
        BigInteger bi = null;
        boolean exact = true;
        if (qr[1].isZERO()) {
            bi = qr[0];
            try {
                Bi = Bi.divide(bi); // divide( c/a )
            } catch (RuntimeException e) {
                System.out.println("*catch: no exact factorization: " + bi + ", e = " + e);
                exact = false;
            }
        } else {
            System.out.println("*remainder: no exact factorization: q = " + qr[0] + ", r = " + qr[1]);
            exact = false;
        }
        if (!exact) {
            System.out.println("*Ai =  " + Ai);
            System.out.println("*ai =  " + ai);
            System.out.println("*Bi =  " + Bi);
            System.out.println("*bi =  " + bi);
            System.out.println("*c  =  " + c);
            throw new NoLiftingException("no exact lifting possible");
        }
        return new HenselApprox<MOD>(Ai,Bi,Aq,Bq);
    }


    /**
     * Modular Hensel lifting test. Let p be a prime number and assume C ==
     * prod_{0,...,n-1} g_i mod p with ggt(g_i,g_j) == 1 mod p for i != j.
     * @param C GenPolynomial
     * @param G = [g_0,...,g_{n-1}] list of GenPolynomial
     * @param M bound on the coefficients of g_i as factors of C.
     * @param p prime number.
     * @return true if C = prod_{0,...,n-1} g_i mod p^e, else false.
     */
    public static //<MOD extends GcdRingElem<MOD> & Modular> 
      boolean isHenselLift(GenPolynomial<BigInteger> C, BigInteger M, BigInteger p, List<GenPolynomial<BigInteger>> G) {
        if (C == null || C.isZERO()) {
            return false;
        }
        GenPolynomialRing<BigInteger> pfac = C.ring;
        ModIntegerRing pm = new ModIntegerRing(p.getVal(), true);
        GenPolynomialRing<ModInteger> mfac = new GenPolynomialRing<ModInteger>(pm, pfac);

        // check mod p
        GenPolynomial<ModInteger> cl = mfac.getONE();
        GenPolynomial<ModInteger> hlp;
        for (GenPolynomial<BigInteger> hl : G) {
            //System.out.println("hl       = " + hl);
            hlp = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, hl);
            //System.out.println("hl mod p = " + hlp);
            cl = cl.multiply(hlp);
        }
        GenPolynomial<ModInteger> cp = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, C);
        if (!cp.equals(cl)) {
            System.out.println("Hensel precondition wrong!");
            System.out.println("cl    = " + cl);
            System.out.println("cp    = " + cp);
            System.out.println("cp-cl = " + cp.subtract(cl));
            System.out.println("M = " + M + ", p = " + p);
            return false;
        }

        // check mod p^e 
        BigInteger mip = p;
        while (mip.compareTo(M) < 0) {
            mip = mip.multiply(mip); // p
        }
        // mip = mip.multiply(p);
        pm = new ModIntegerRing(mip.getVal(), false);
        mfac = new GenPolynomialRing<ModInteger>(pm, pfac);
        cl = mfac.getONE();
        for (GenPolynomial<BigInteger> hl : G) {
            //System.out.println("hl         = " + hl);
            hlp = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, hl);
            //System.out.println("hl mod p^e = " + hlp);
            cl = cl.multiply(hlp);
        }
        cp = PolyUtil.<ModInteger> fromIntegerCoefficients(mfac, C);
        if (!cp.equals(cl)) {
            System.out.println("Hensel post condition wrong!");
            System.out.println("cl    = " + cl);
            System.out.println("cp    = " + cp);
            System.out.println("cp-cl = " + cp.subtract(cl));
            System.out.println("M = " + M + ", p = " + p + ", p^e = " + mip);
            return false;
        }
        return true;
    }


    /**
     * Modular Hensel lifting test. Let p be a prime number and assume C == A *
     * B mod p with ggt(A,B) == 1 mod p.
     * @param C GenPolynomial
     * @param A GenPolynomial
     * @param B GenPolynomial
     * @param M bound on the coefficients of A and B as factors of C.
     * @param p prime number.
     * @return true if C = A * B mod p**e, else false.
     */
    public static //<MOD extends GcdRingElem<MOD> & Modular>
      boolean isHenselLift(GenPolynomial<BigInteger> C, BigInteger M, BigInteger p, 
                           GenPolynomial<BigInteger> A, GenPolynomial<BigInteger> B) {
        List<GenPolynomial<BigInteger>> G = new ArrayList<GenPolynomial<BigInteger>>(2);
        G.add(A);
        G.add(B);
        return isHenselLift(C, M, p, G);
    }


    /**
     * Modular Hensel lifting test. Let p be a prime number and assume C == A *
     * B mod p with ggt(A,B) == 1 mod p.
     * @param C GenPolynomial
     * @param Ha Hensel approximation.
     * @param M bound on the coefficients of A and B as factors of C.
     * @param p prime number.
     * @return true if C = A * B mod p^e, else false.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      boolean isHenselLift(GenPolynomial<BigInteger> C, BigInteger M, BigInteger p, HenselApprox<MOD> Ha) {
        List<GenPolynomial<BigInteger>> G = new ArrayList<GenPolynomial<BigInteger>>(2);
        G.add(Ha.A);
        G.add(Ha.B);
        return isHenselLift(C, M, p, G);
    }


    /**
     * Constructing and lifting algorithm for extended Euclidean relation. 
     * Computation modulo p^k, unused and unnecessary.
     * Let p = A.ring.coFac.modul() and assume ggt(A,B) == 1 mod p.
     * @param A modular GenPolynomial
     * @param B modular GenPolynomial
     * @param k desired approximation exponent p^k.
     * @return [s,t] with s A + t B = 1 mod p^k.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      GenPolynomial<MOD>[] liftExtendedEuclideanP2k(GenPolynomial<MOD> A, GenPolynomial<MOD> B, long k) throws NoLiftingException {
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<MOD> fac = A.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // start with extended Euclid mod p
        GenPolynomial<MOD>[] gst = A.egcd(B);
        if (!gst[0].isONE()) {
            throw new NoLiftingException("A and B not coprime, gcd = " + gst[0] + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<MOD> S = gst[1];
        GenPolynomial<MOD> T = gst[2];
        System.out.println("\nS = " + S);
        System.out.println("T = " + T);

        // setup integer polynomial ring
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac); 
        GenPolynomial<BigInteger> one = ifac.getONE();
        GenPolynomial<BigInteger> Ai = PolyUtil.integerFromModularCoefficients(ifac, A);
        GenPolynomial<BigInteger> Bi = PolyUtil.integerFromModularCoefficients(ifac, B);
        GenPolynomial<BigInteger> Si = PolyUtil.integerFromModularCoefficients(ifac, S);
        GenPolynomial<BigInteger> Ti = PolyUtil.integerFromModularCoefficients(ifac, T);
        //System.out.println("Ai = " + Ai);
        //System.out.println("Bi = " + Bi);
        //System.out.println("Si = " + Si);
        //System.out.println("Ti = " + Ti);

        // approximate mod p^i
        ModularRingFactory<MOD> mcfac = (ModularRingFactory<MOD>) fac.coFac;
        BigInteger p = mcfac.getIntegerModul();
        BigInteger modul = p;
        GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(mcfac,fac);
        for ( int i = 0; i < k; i++ ) {
            // e = 1 - s a - t b in Z[x]
            GenPolynomial<BigInteger> e = one.subtract(Si.multiply(Ai)).subtract(Ti.multiply(Bi));
            System.out.println("\ne = " + e);
            e = e.divide(modul);
            // move to in Z_{p^i}[x]
            GenPolynomial<MOD> c = PolyUtil.<MOD> fromIntegerCoefficients(mfac, e);
            System.out.println("c = " + c + ": " + c.ring.coFac);
            GenPolynomial<MOD> s = S.multiply(c);
            GenPolynomial<MOD> t = T.multiply(c);
            //System.out.println("s = " + s + ": " + s.ring.coFac);
            //System.out.println("t = " + t + ": " + t.ring.coFac);

            GenPolynomial<MOD>[] QR = s.divideAndRemainder(B);
            GenPolynomial<MOD> q = QR[0];
            s = QR[1];
            t = t.sum(q.multiply(A));
            //System.out.println("s = " + s + ": " + s.ring.coFac);
            //System.out.println("t = " + t + ": " + t.ring.coFac);

            BigInteger m = modul;
            // setup ring mod p^i
            modul = modul.multiply(p);
            if ( ModLongRing.MAX_LONG.compareTo( modul.getVal() ) > 0 ) {
                mcfac = (ModularRingFactory) new ModLongRing(modul.getVal());
            } else {
                mcfac = (ModularRingFactory) new ModIntegerRing(modul.getVal());
            }
            System.out.println("mcfac = " + mcfac);
            mfac = new GenPolynomialRing<MOD>(mcfac,fac);
            //System.out.println("mfac  = " + mfac);

            MOD mo = mcfac.fromInteger(m.getVal());
            //System.out.println("mo    = " + mo);

            S = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(ifac, S));
            T = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(ifac, T));
            s = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(ifac, s));
            t = PolyUtil.<MOD> fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(ifac, t));
            //System.out.println("S = " + S + ": " + S.ring.coFac);
            //System.out.println("T = " + T + ": " + T.ring.coFac);
            //System.out.println("s = " + s + ": " + s.ring.coFac);
            //System.out.println("t = " + t + ": " + t.ring.coFac);
            S = S.sum(s.multiply(mo));
            T = T.sum(t.multiply(mo));
            //System.out.println("S = " + S + ": " + S.ring.coFac);
            //System.out.println("T = " + T + ": " + T.ring.coFac);
            Si = PolyUtil.integerFromModularCoefficients(ifac, S);
            Ti = PolyUtil.integerFromModularCoefficients(ifac, T);
            A = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Ai);
            B = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Bi);
            //System.out.println("Si = " + Si);
            //System.out.println("Ti = " + Ti);
            //System.out.println("A  = " + A + ": " + A.ring.coFac);
            //System.out.println("B  = " + B + ": " + B.ring.coFac);
        }
        GenPolynomial<MOD>[] rel = (GenPolynomial<MOD>[]) new GenPolynomial[2];
        rel[0] = S;
        rel[1] = T;
//         rel[2] = A;
//         rel[3] = B;
        return rel;
    }


    /**
     * Constructing and lifting algorithm for extended Euclidean relation. 
     * Let p = A.ring.coFac.modul() and assume ggt(A,B) == 1 mod p.
     * @param A modular GenPolynomial
     * @param B modular GenPolynomial
     * @param k desired approximation exponent p^k.
     * @return [s,t] with s A + t B = 1 mod p^k.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      GenPolynomial<MOD>[] liftExtendedEuclidean(GenPolynomial<MOD> A, GenPolynomial<MOD> B, long k) throws NoLiftingException {
        if (A == null || A.isZERO() || B == null || B.isZERO()) {
            throw new RuntimeException("A and B must be nonzero");
        }
        GenPolynomialRing<MOD> fac = A.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        // start with extended Euclidean relation mod p
        GenPolynomial<MOD>[] gst = A.egcd(B);
        if (!gst[0].isONE()) {
            throw new NoLiftingException("A and B not coprime, gcd = " + gst[0] + ", A = " + A + ", B = " + B);
        }
        GenPolynomial<MOD> S = gst[1];
        GenPolynomial<MOD> T = gst[2];
        //System.out.println("\nS = " + S + ": " + S.ring.coFac);
        //System.out.println("T = " + T + ": " + S.ring.coFac);

        // setup integer polynomial ring
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac); 
        GenPolynomial<BigInteger> one = ifac.getONE();
        GenPolynomial<BigInteger> Ai = PolyUtil.integerFromModularCoefficients(ifac, A);
        GenPolynomial<BigInteger> Bi = PolyUtil.integerFromModularCoefficients(ifac, B);
        GenPolynomial<BigInteger> Si = PolyUtil.integerFromModularCoefficients(ifac, S);
        GenPolynomial<BigInteger> Ti = PolyUtil.integerFromModularCoefficients(ifac, T);
        //System.out.println("Ai = " + Ai);
        //System.out.println("Bi = " + Bi);
        //System.out.println("Si = " + Si);
        //System.out.println("Ti = " + Ti);

        // approximate mod p^i
        ModularRingFactory<MOD> mcfac = (ModularRingFactory<MOD>) fac.coFac;
        BigInteger p = mcfac.getIntegerModul();
        BigInteger modul = p;
        GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(mcfac,fac);
        for ( int i = 0; i < k; i++ ) {
            // e = 1 - s a - t b in Z[x]
            GenPolynomial<BigInteger> e = one.subtract(Si.multiply(Ai)).subtract(Ti.multiply(Bi));
            //System.out.println("\ne = " + e);
            if ( e.isZERO() ) {
                logger.info("leaving on zero error");
                break;
            }
            e = e.divide(modul);
            // move to Z_p[x] and compute next approximation 
            GenPolynomial<MOD> c = PolyUtil.<MOD> fromIntegerCoefficients(fac, e);
            //System.out.println("c = " + c + ": " + c.ring.coFac);
            GenPolynomial<MOD> s = S.multiply(c);
            GenPolynomial<MOD> t = T.multiply(c);
            //System.out.println("s = " + s + ": " + s.ring.coFac);
            //System.out.println("t = " + t + ": " + t.ring.coFac);

            GenPolynomial<MOD>[] QR = s.divideAndRemainder(B); // watch for ordering 
            GenPolynomial<MOD> q = QR[0];
            s = QR[1];
            t = t.sum(q.multiply(A));
            //System.out.println("s = " + s + ": " + s.ring.coFac);
            //System.out.println("t = " + t + ": " + t.ring.coFac);

            GenPolynomial<BigInteger> si = PolyUtil.integerFromModularCoefficients(ifac, s);
            GenPolynomial<BigInteger> ti = PolyUtil.integerFromModularCoefficients(ifac, t);
            //System.out.println("si = " + si);
            //System.out.println("ti = " + si);
            // add approximation to solution
            Si = Si.sum(si.multiply(modul));
            Ti = Ti.sum(ti.multiply(modul));
            //System.out.println("Si = " + Si);
            //System.out.println("Ti = " + Ti);
            modul = modul.multiply(p);
            //System.out.println("modul = " + modul);
        }
        // setup ring mod p^i
        if ( ModLongRing.MAX_LONG.compareTo( modul.getVal() ) > 0 ) {
            mcfac = (ModularRingFactory) new ModLongRing(modul.getVal());
        } else {
            mcfac = (ModularRingFactory) new ModIntegerRing(modul.getVal());
        }
        //System.out.println("mcfac = " + mcfac);
        mfac = new GenPolynomialRing<MOD>(mcfac,fac);
        S = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Si);
        T = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Ti);
        //System.out.println("S = " + S + ": " + S.ring.coFac);
        //System.out.println("T = " + T + ": " + T.ring.coFac);
        GenPolynomial<MOD>[] rel = (GenPolynomial<MOD>[]) new GenPolynomial[2];
        rel[0] = S;
        rel[1] = T;
        return rel;
    }


    /**
     * Constructing and lifting algorithm for extended Euclidean relation. 
     * Let p = A_i.ring.coFac.modul() and assume ggt(A_i,A_j) == 1 mod p, i != j.
     * @param A list of modular GenPolynomials
     * @param k desired approximation exponent p^k.
     * @return [s_0,...,s_n-1] with sum_i s_i * B_i = 1 mod p^k, with B_i = prod_{i!=j} A_j.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      List<GenPolynomial<MOD>> liftExtendedEuclidean(List<GenPolynomial<MOD>> A, long k) throws NoLiftingException {
        if (A == null || A.size() == 0) {
            throw new RuntimeException("A must be non null and non empty");
        }
        GenPolynomialRing<MOD> fac = A.get(0).ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        GenPolynomial<MOD> zero = fac.getZERO();
        int r = A.size();
        List<GenPolynomial<MOD>> Q = new ArrayList<GenPolynomial<MOD>>(r);
        for ( int i = 0; i < r; i++ ) {
            Q.add(zero);
        }
        //System.out.println("A = " + A);
        Q.set(r-2,A.get(r-1));
        for ( int j = r-3; j >= 0; j-- ) {
            GenPolynomial<MOD> q = A.get(j+1).multiply(Q.get(j+1));
            Q.set(j,q);
        }
        //System.out.println("Q = " + Q);
        List<GenPolynomial<MOD>> B = new ArrayList<GenPolynomial<MOD>>(r+1);
        List<GenPolynomial<MOD>> lift = new ArrayList<GenPolynomial<MOD>>(r);
        for ( int i = 0; i < r; i++ ) {
            B.add(zero);
            lift.add(zero);
        }
        GenPolynomial<MOD> one = fac.getONE();
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac);
        B.add(0,one);
        //System.out.println("B(0) = " + B.get(0));
        GenPolynomial<MOD> b = one;
        for ( int j = 0; j < r-1; j++ ) {
            //System.out.println("Q("+(j)+") = " + Q.get(j));
            //System.out.println("A("+(j)+") = " + A.get(j));
            //System.out.println("B("+(j)+") = " + B.get(j));
            List<GenPolynomial<MOD>> S = liftDiophant(Q.get(j),A.get(j),B.get(j),k); 
            //System.out.println("\nSb = " + S);
            b = S.get(0);
            GenPolynomial<MOD> bb = PolyUtil.<MOD> fromIntegerCoefficients(fac, PolyUtil.integerFromModularCoefficients(ifac, b));
            B.set(j+1,bb);
            lift.set(j,S.get(1));
            //System.out.println("B("+(j+1)+") = " + B.get(j+1));
            if ( debug ) {
                logger.info("lift("+j+") = " + lift.get(j));
            }
        }
        //System.out.println("liftb = " + lift);
        lift.set(r-1,b);
        if ( debug ) {
            logger.info("lift("+(r-1)+") = " + b);
        }
        //System.out.println("B("+(r-1)+") = " + B.get(r-1) + " : " +  B.get(r-1).ring.coFac + ", b = " +  b + " : " +  b.ring.coFac);
        //System.out.println("B = " + B);
        //System.out.println("liftb = " + lift);
        return lift;
    }


    /**
     * Modular diophantine equation solution and lifting algorithm. 
     * Let p = A_i.ring.coFac.modul() and assume ggt(A,B) == 1 mod p. 
     * @param A modular GenPolynomial
     * @param B modular GenPolynomial
     * @param C modular GenPolynomial
     * @param k desired approximation exponent p^k.
     * @return [s, t] with s A' + t B' = C mod p^k, with A' = B, B' = A.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      List<GenPolynomial<MOD>> liftDiophant(GenPolynomial<MOD> A, GenPolynomial<MOD> B, GenPolynomial<MOD> C, long k) 
                                            throws NoLiftingException {
        List<GenPolynomial<MOD>> sol = new ArrayList<GenPolynomial<MOD>>();
        GenPolynomialRing<MOD> fac = A.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        //System.out.println("C = " + C);
        GenPolynomial<MOD> zero = fac.getZERO();
        for ( int i = 0; i < 2; i++ ) {
            sol.add(zero);
        }
        for ( Monomial<MOD> m : C ) {
            //System.out.println("monomial = " + m);
            long e = m.e.getVal(0);
            List<GenPolynomial<MOD>> S = liftDiophant(A,B,e,k);
            //System.out.println("Se = " + S);
            MOD a = m.c;
            fac = S.get(0).ring;
            a = fac.coFac.fromInteger( a.getSymmetricInteger().getVal() );
            int i = 0;
            for ( GenPolynomial<MOD> d : S ) {
                //System.out.println("d = " + d);
                d = d.multiply(a);
                d = sol.get(i).sum(d);
                //System.out.println("d = " + d);
                sol.set(i++,d);
            }
        }
        if ( debug ) {
            GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac);
            A = PolyUtil.<MOD> fromIntegerCoefficients(fac, PolyUtil.integerFromModularCoefficients(ifac, A));
            B = PolyUtil.<MOD> fromIntegerCoefficients(fac, PolyUtil.integerFromModularCoefficients(ifac, B));
            C = PolyUtil.<MOD> fromIntegerCoefficients(fac, PolyUtil.integerFromModularCoefficients(ifac, C));
            GenPolynomial<MOD> y = B.multiply(sol.get(0)).sum(A.multiply(sol.get(1)) );
            if ( !y.equals(C) ) {
                System.out.println("A = " + A + ", B = " + B);
                System.out.println("s1 = " + sol.get(0) + ", s2 = " + sol.get(1));
                System.out.println("A*r1 + B*r2 = " + y + " : " + fac.coFac);
            }
        }
        return sol;
    }


    /**
     * Modular diophantine equation solution and lifting algorithm. 
     * Let p = A_i.ring.coFac.modul() and assume ggt(A,B) == 1 mod p. 
     * @param A modular GenPolynomial
     * @param B modular GenPolynomial
     * @param e exponent for x^e
     * @param k desired approximation exponent p^k.
     * @return [s, t] with s A' + t B' = x^e mod p^k, with A' = B, B' = A.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      List<GenPolynomial<MOD>> liftDiophant(GenPolynomial<MOD> A, GenPolynomial<MOD> B, long e, long k) 
                                            throws NoLiftingException {
        List<GenPolynomial<MOD>> sol = new ArrayList<GenPolynomial<MOD>>();
        GenPolynomialRing<MOD> fac = A.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac);
        GenPolynomial<MOD>[] lee = liftExtendedEuclidean(B,A,k);
        GenPolynomial<MOD> s1 = lee[0];
        GenPolynomial<MOD> s2 = lee[1];
        fac = s1.ring;
        A = PolyUtil.<MOD> fromIntegerCoefficients(fac, PolyUtil.integerFromModularCoefficients(ifac, A));
        B = PolyUtil.<MOD> fromIntegerCoefficients(fac, PolyUtil.integerFromModularCoefficients(ifac, B));

//      this is the wrong sequence:
//         GenPolynomial<MOD> xe = fac.univariate(0,e);
//         GenPolynomial<MOD> q = s1.multiply(xe);
//         GenPolynomial<MOD>[] QR = q.divideAndRemainder(B);
//         q = QR[0];
//         GenPolynomial<MOD> r1 = QR[1];
//         GenPolynomial<MOD> r2 = s2.multiply(xe).sum( q.multiply(A) );

        GenPolynomial<MOD> xe = fac.univariate(0,e);
        GenPolynomial<MOD> q = s1.multiply(xe);
        GenPolynomial<MOD>[] QR = q.divideAndRemainder(A);
        q = QR[0];
        GenPolynomial<MOD> r1 = QR[1];
        GenPolynomial<MOD> r2 = s2.multiply(xe).sum( q.multiply(B) );
        //System.out.println("r1 = " + r1 + ", r2 = " + r2);
        sol.add(r1);
        sol.add(r2);
        if ( debug ) {
            GenPolynomial<MOD> y = B.multiply(r1).sum(A.multiply(r2));
            if ( !y.equals(xe) ) {
                System.out.println("A = " + A + ", B = " + B);
                System.out.println("r1 = " + r1 + ", r2 = " + r2);
                System.out.println("A*r1 + B*r2 = " + y);
            }
        }
        return sol;
    }


    /**
     * Modular extended Euclidean relation lifting test. 
     * @param A list of GenPolynomials
     * @param S = [s_0,...,s_{n-1}] list of GenPolynomial
     * @return true if prod_{0,...,n-1} s_i * B _i = 1 mod p^e, with B_i = prod_{i!=j} A_j, else false.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular> 
      boolean isExtendedEuclideanLift(List<GenPolynomial<MOD>> A, List<GenPolynomial<MOD>> S) {
        GenPolynomialRing<MOD> fac = A.get(0).ring;
        List<GenPolynomial<MOD>> B = new ArrayList<GenPolynomial<MOD>>(A.size());
        int i = 0;
        for ( GenPolynomial<MOD> ai : A ) {
             GenPolynomial<MOD> b = fac.getONE();
             int j = 0;
             for ( GenPolynomial<MOD> aj : A ) {
                 if ( i != j /*!ai.equals(aj)*/ ) {
                     b = b.multiply(aj);
                 }
                 j++;
             }
             //System.out.println("b = " + b);
             B.add(b);
             i++;
        }
        //System.out.println("B = " + B);
        // check mod p^e 
        GenPolynomial<MOD> t = fac.getZERO();
        i = 0;
        for ( GenPolynomial<MOD> a : B ) {
            GenPolynomial<MOD> s = a.multiply( S.get(i++) );
            t = t.sum(s);
        }
        if (!t.isONE()) {
            System.out.println("no ee lift!");
            System.out.println("A = " + A);
            System.out.println("B = " + B);
            System.out.println("S = " + S);
            System.out.println("t = " + t);
            return false;
        }
        return true;
    }


    /**
     * Modular Hensel lifting algorithm on coefficients. 
     * Let p = f_i.ring.coFac.modul() and 
     * assume C == prod_{0,...,n-1} f_i mod p with ggt(f_i,f_j) == 1 mod p for i != j
     * @param C monic integer polynomial
     * @param F = [f_0,...,f_{n-1}] list of monic modular polynomials.
     * @param k approximation exponent.
     * @return [g_0,...,g_{n-1}] with C = prod_{0,...,n-1} g_i mod p^e.
     */
    public static <MOD extends GcdRingElem<MOD> & Modular>
      List<GenPolynomial<MOD>> liftHenselMonic(GenPolynomial<BigInteger> C, List<GenPolynomial<MOD>> F, long k) throws NoLiftingException {
        if (C == null || C.isZERO() || F == null || F.size() == 0) {
            throw new RuntimeException("C must be nonzero and F must be nonempty");
        }
        GenPolynomialRing<BigInteger> fac = C.ring;
        if (fac.nvar != 1) { // todo assert
            throw new RuntimeException("polynomial ring not univariate");
        }
        List<GenPolynomial<MOD>> lift = new ArrayList<GenPolynomial<MOD>>(F.size());
        GenPolynomialRing<MOD> pfac = F.get(0).ring;
        RingFactory<MOD> pcfac = pfac.coFac;
        ModularRingFactory<MOD> PF = (ModularRingFactory<MOD>) pcfac;
        BigInteger P = PF.getIntegerModul();
        int n = F.size();
        if (n == 1) { // lift F_0, this case will probably never be used
            GenPolynomial<MOD> f = F.get(0);
            ModularRingFactory<MOD> mcfac;
            if ( ModLongRing.MAX_LONG.compareTo( P.getVal() ) > 0 ) {
                mcfac = (ModularRingFactory) new ModLongRing(P.getVal());
            } else {
                mcfac = (ModularRingFactory) new ModIntegerRing(P.getVal());
            }
            GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(mcfac, fac);
            f = PolyUtil.fromIntegerCoefficients(mfac, PolyUtil.integerFromModularCoefficients(fac, f));
            lift.add( f );
            return lift;
        }
//         if (n == 2) { // only one step
//             HenselApprox<MOD> ab = HenselUtil.<MOD> liftHenselQuadratic(C, M, F.get(0), F.get(1));
//             lift.add(ab.Am);
//             lift.add(ab.Bm);
//             return lift;
//         }

        // setup integer polynomial ring
        GenPolynomialRing<BigInteger> ifac = new GenPolynomialRing<BigInteger>(new BigInteger(),fac); 
        GenPolynomial<BigInteger> one = ifac.getONE();
        List<GenPolynomial<BigInteger>> Fi = PolyUtil.integerFromModularCoefficients(ifac, F);
        //System.out.println("Fi = " + Fi);

        List<GenPolynomial<MOD>> S = liftExtendedEuclidean(F, k+1); // lift works for any k, TODO: use this
        //System.out.println("Sext = " + S);
        if ( debug ) {
            logger.info("EE lift = " + S);
            // adjust coefficients
            List<GenPolynomial<MOD>> Sx = PolyUtil.fromIntegerCoefficients(pfac, PolyUtil.integerFromModularCoefficients(ifac, S) );
            try {
                boolean il = HenselUtil.<MOD>isExtendedEuclideanLift(F,Sx);
                //System.out.println("islift = " + il);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        List<GenPolynomial<BigInteger>> Si = PolyUtil.integerFromModularCoefficients(ifac, S);
        //System.out.println("Si = " + Si);
        //System.out.println("C = " + C);

        // approximate mod p^i
        ModularRingFactory<MOD> mcfac = PF;
        BigInteger p = mcfac.getIntegerModul();
        BigInteger modul = p;
        GenPolynomialRing<MOD> mfac = new GenPolynomialRing<MOD>(mcfac,fac);
        List<GenPolynomial<MOD>> Sp = PolyUtil.fromIntegerCoefficients(mfac, Si);
        //System.out.println("Sp = " + Sp);
        for ( int i = 0; i < k; i++ ) {
            GenPolynomial<BigInteger> e = fac.getONE();
            for ( GenPolynomial<BigInteger> fi : Fi ) {
                e = e.multiply( fi );
            }
            e = C.subtract(e);
            //System.out.println("\ne = " + e);
            if ( e.isZERO() ) {
                logger.info("leaving on zero error");
                break;
            }
            try {
                 e = e.divide(modul);
            } catch ( RuntimeException ex ) {
                ex.printStackTrace();
                throw ex;
            }
            //System.out.println("e = " + e);
            // move to in Z_p[x]
            GenPolynomial<MOD> c = PolyUtil.<MOD> fromIntegerCoefficients(mfac, e);
            //System.out.println("c = " + c + ": " + c.ring.coFac);

            List<GenPolynomial<MOD>> s = new ArrayList<GenPolynomial<MOD>>(S.size());
            int j = 0;
            for ( GenPolynomial<MOD> f : Sp ) {
                f = f.multiply(c);
                //System.out.println("f = " + f + " : " + f.ring.coFac);
                //System.out.println("F,i = " + F.get(j) + " : " + F.get(j).ring.coFac);
                f = f.remainder( F.get(j++) );
                //System.out.println("f = " + f + " : " + f.ring.coFac);
                s.add(f);
            }
            //System.out.println("s = " + s);
            List<GenPolynomial<BigInteger>> si = PolyUtil.integerFromModularCoefficients(ifac, s);
            //System.out.println("si = " + si);

            List<GenPolynomial<BigInteger>> Fii = new ArrayList<GenPolynomial<BigInteger>>(F.size());
            j = 0;
            for ( GenPolynomial<BigInteger> f : Fi ) {
                f = f.sum( si.get(j++).multiply(modul) );
                Fii.add(f);
            }
            //System.out.println("Fii = " + Fii);
            Fi = Fii;
            modul = modul.multiply(p);
            if ( i >= k-1 ) {
                logger.info("e != 0 for k = " + k);
            }
        }
        // setup ring mod p^i
        if ( ModLongRing.MAX_LONG.compareTo( modul.getVal() ) > 0 ) {
            mcfac = (ModularRingFactory) new ModLongRing(modul.getVal());
        } else {
            mcfac = (ModularRingFactory) new ModIntegerRing(modul.getVal());
        }
        //System.out.println("mcfac = " + mcfac);
        mfac = new GenPolynomialRing<MOD>(mcfac,fac);
        lift = PolyUtil.<MOD> fromIntegerCoefficients(mfac, Fi);
        //System.out.println("lift = " + lift + ": " + lift.get(0).ring.coFac);
        return lift;
    }

}

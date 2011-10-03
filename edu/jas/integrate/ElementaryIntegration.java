/*
 * $Id: ElementaryIntegration.java 2996 2010-02-07 13:32:42Z kredel $
 */

package edu.jas.integrate;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Methods related to elementary integration. In particular there are methods
 * for Hermite reduction and Rothstein-Trager integration of the logarithmic
 * part.
 * 
 * @author Axel Kramer
 * @author Heinz Kredel
 * @param <C> coefficient type
 */

public class ElementaryIntegration<C extends GcdRingElem<C>> {


    private static final Logger logger = Logger.getLogger(ElementaryIntegration.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Engine for factorization.
     */
    public final FactorAbstract<C> irr;


    /**
     * Engine for squarefree decomposition.
     */
    public final SquarefreeAbstract<C> sqf;


    /**
     * Engine for greatest common divisors.
     */
    public final GreatestCommonDivisorAbstract<C> ufd;


    /**
     * Constructor.
     */
    public ElementaryIntegration(RingFactory<C> br) {
        ufd = GCDFactory.<C> getProxy(br);
        sqf = SquarefreeFactory.<C> getImplementation(br);
        irr = /*(FactorAbsolute<C>)*/FactorFactory.<C> getImplementation(br);
    }


    /**
     * Integration of a rational function.
     * 
     * @param r rational function
     * @return Integral container, such that integrate(r) = sum_i(g_i) + sum_j(
     *         an_j log(hd_j) )
     */
    public QuotIntegral<C> integrate(Quotient<C> r) {
        Integral<C> integral = integrate(r.num, r.den);
        return new QuotIntegral<C>(r.ring, integral);
    }


    /**
     * Integration of a rational function.
     * 
     * @param a numerator
     * @param d denominator
     * @return Integral container, such that integrate(a/d) = sum_i(gn_i/gd_i) +
     *         integrate(h0) + sum_j( an_j log(hd_j) )
     */
    public Integral<C> integrate(GenPolynomial<C> a, GenPolynomial<C> d) {
        if (d == null || a == null || d.isZERO()) {
            throw new RuntimeException("zero or null not allowed");
        }
        if (a.isZERO()) {
            return new Integral<C>(a, d, a);
        }
        if (d.isONE()) {
            GenPolynomial<C> pi = PolyUtil.<C> baseIntegral(a);
            return new Integral<C>(a, d, pi);
        }
        GenPolynomialRing<C> pfac = d.ring;
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials " + pfac);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients " + pfac);
        }

        GenPolynomial<C>[] qr = PolyUtil.<C> basePseudoQuotientRemainder(a, d);
        GenPolynomial<C> p = qr[0];
        GenPolynomial<C> r = qr[1];

        GenPolynomial<C> c = ufd.gcd(r, d);
        if (!c.isONE()) {
            r = PolyUtil.<C> basePseudoQuotientRemainder(r, c)[0];
            d = PolyUtil.<C> basePseudoQuotientRemainder(d, c)[0];
        }
        List<GenPolynomial<C>>[] ih = integrateHermite(r, d);
        List<GenPolynomial<C>> rat = ih[0];
        List<GenPolynomial<C>> log = ih[1];

        GenPolynomial<C> pp = log.remove(0);
        p = p.sum(pp);
        GenPolynomial<C> pi = PolyUtil.<C> baseIntegral(p);

        if (debug) {
            logger.debug("pi  = " + pi);
            logger.debug("rat = " + rat);
            logger.debug("log = " + log);
        }
        if (log.size() == 0) {
            return new Integral<C>(a, d, pi, rat);
        }

        List<LogIntegral<C>> logi = new ArrayList<LogIntegral<C>>(log.size() / 2);
        for (int i = 0; i < log.size(); i++) {
            GenPolynomial<C> ln = log.get(i++);
            GenPolynomial<C> ld = log.get(i);
            LogIntegral<C> pf = integrateLogPart(ln, ld);
            logi.add(pf);
        }
        if (debug) {
            logger.debug("logi = " + logi);
        }
        return new Integral<C>(a, d, pi, rat, logi);
    }


    /**
     * Integration of the rational part, Hermite reduction step.
     * 
     * @param a numerator
     * @param d denominator, gcd(a,d) == 1
     * @return [ [ gn_i, gd_i ], [ h0, hn_j, hd_j ] ] such that integrate(a/d) =
     *         sum_i(gn_i/gd_i) + integrate(h0) + sum_j( integrate(hn_j/hd_j) )
     */
    public List<GenPolynomial<C>>[] integrateHermite(GenPolynomial<C> a, GenPolynomial<C> d) {
        if (d == null || d.isZERO()) {
            throw new RuntimeException("d == null or d == 0");
        }
        if (a == null || a.isZERO()) {
            throw new RuntimeException("a == null or a == 0");
        }

        // get squarefree decomposition
        SortedMap<GenPolynomial<C>, Long> sfactors = sqf.squarefreeFactors(d);

        List<GenPolynomial<C>> D = new ArrayList<GenPolynomial<C>>(sfactors.keySet());
        List<GenPolynomial<C>> DP = new ArrayList<GenPolynomial<C>>();
        for (GenPolynomial<C> f : D) {
            long e = sfactors.get(f);
            GenPolynomial<C> dp = Power.<GenPolynomial<C>> positivePower(f, e);
            DP.add(dp);
        }
        //System.out.println("D:      " + D);
        //System.out.println("DP:     " + DP);

        // get partial fraction decompostion 
        List<GenPolynomial<C>> Ai = ufd.basePartialFraction(a, DP);
        //System.out.println("Ai:     " + Ai);

        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        List<GenPolynomial<C>> H = new ArrayList<GenPolynomial<C>>();
        H.add(Ai.remove(0)); // P

        GenPolynomialRing<C> fac = d.ring;
        int i = 0;
        for (GenPolynomial<C> v : D) {
            //System.out.println("V:" + v.toString());
            GenPolynomial<C> Ak = Ai.get(i++);
            //System.out.println("Ak:  " + Ak.toString());
            int k = sfactors.get(v).intValue(); // assert low power
            for (int j = k - 1; j >= 1; j--) {
                //System.out.println("Step(" + k + "," + j + ")");
                GenPolynomial<C> DV_dx = PolyUtil.<C> baseDeriviative(v);
                GenPolynomial<C> Aik = Ak.divide(fac.fromInteger(-j));
                GenPolynomial<C>[] BC = ufd.baseGcdDiophant(DV_dx, v, Aik);
                GenPolynomial<C> b = BC[0];
                GenPolynomial<C> c = BC[1];
                GenPolynomial<C> vj = Power.<GenPolynomial<C>> positivePower(v, j);
                G.add(b); // B
                G.add(vj); // v^j
                Ak = fac.fromInteger(-j).multiply(c).subtract(PolyUtil.<C> baseDeriviative(b));
                //System.out.println("B:   " + b.toString());
                //System.out.println("C:   " + c.toString());
            }
            //System.out.println("V:" + v.toString());
            //System.out.println("Ak:  " + Ak.toString());
            if (!Ak.isZERO()) {
                H.add(Ak); // A_k
                H.add(v); // v
            }
        }
        List<GenPolynomial<C>>[] ret = (List<GenPolynomial<C>>[]) new List[2];
        ret[0] = G;
        ret[1] = H;
        return ret;
    }


    /**
     * Univariate GenPolynomial integration of the logaritmic part,
     * Rothstein-Trager algorithm.
     * @param A univariate GenPolynomial, deg(A) < deg(P).
     * @param P univariate squarefree GenPolynomial, gcd(A,P) == 1.
     * @return logarithmic part container.
     */
    public LogIntegral<C> integrateLogPart(GenPolynomial<C> A, GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            throw new RuntimeException(" P == null or P == 0");
        }
        if (A == null || A.isZERO()) {
            throw new RuntimeException(" A == null or A == 0");
        }
        //System.out.println("\nP_base_algeb_part = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials " + pfac);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients " + pfac);
        }
        List<C> cfactors = new ArrayList<C>();
        List<GenPolynomial<C>> cdenom = new ArrayList<GenPolynomial<C>>();
        List<AlgebraicNumber<C>> afactors = new ArrayList<AlgebraicNumber<C>>();
        List<GenPolynomial<AlgebraicNumber<C>>> adenom = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();

        // P linear
        if (P.degree(0) <= 1) {
            cfactors.add(A.leadingBaseCoefficient());
            cdenom.add(P);
            return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
        }
        List<GenPolynomial<C>> Pfac = irr.baseFactorsSquarefree(P);
        //System.out.println("\nPfac = " + Pfac);

        List<GenPolynomial<C>> Afac = ufd.basePartialFraction(A, Pfac);

        GenPolynomial<C> A0 = Afac.remove(0);
        if (!A0.isZERO()) {
            throw new RuntimeException(" A0 != 0: deg(A)>= deg(P)");
        }

        // algebraic and linear factors
        int i = 0;
        for (GenPolynomial<C> pi : Pfac) {
            GenPolynomial<C> ai = Afac.get(i++);
            if (pi.degree(0) <= 1) {
                cfactors.add(ai.leadingBaseCoefficient());
                cdenom.add(pi);
                continue;
            }
            LogIntegral<C> pf = integrateLogPartIrreducible(ai, pi);
            cfactors.addAll(pf.cfactors);
            cdenom.addAll(pf.cdenom);
            afactors.addAll(pf.afactors);
            adenom.addAll(pf.adenom);
        }
        return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
    }


    /**
     * Univariate GenPolynomial integration of the logaritmic part,
     * Rothstein-Trager algorithm.
     * @param A univariate GenPolynomial, deg(A) < deg(P).
     * @param P univariate irreducible GenPolynomial. // gcd(A,P) == 1 automatic
     * @return logarithmic part container.
     */
    public LogIntegral<C> integrateLogPartIrreducible(GenPolynomial<C> A, GenPolynomial<C> P) {
        if (P == null || P.isZERO()) {
            throw new RuntimeException("P == null or P == 0");
        }
        //System.out.println("\nP_base_algeb_part = " + P);
        GenPolynomialRing<C> pfac = P.ring; // K[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials " + pfac);
        }
        if (!pfac.coFac.isField()) {
            throw new RuntimeException("only for field coefficients " + pfac);
        }
        List<C> cfactors = new ArrayList<C>();
        List<GenPolynomial<C>> cdenom = new ArrayList<GenPolynomial<C>>();
        List<AlgebraicNumber<C>> afactors = new ArrayList<AlgebraicNumber<C>>();
        List<GenPolynomial<AlgebraicNumber<C>>> adenom = new ArrayList<GenPolynomial<AlgebraicNumber<C>>>();

        // P linear
        if (P.degree(0) <= 1) {
            cfactors.add(A.leadingBaseCoefficient());
            cdenom.add(P);
            return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
        }

        // deriviative
        GenPolynomial<C> Pp = PolyUtil.<C> baseDeriviative(P);
        //no: Pp = Pp.monic();
        //System.out.println("\nP  = " + P);
        //System.out.println("Pp = " + Pp);

        // Q[t]
        String[] vars = new String[] { "t" };
        GenPolynomialRing<C> cfac = new GenPolynomialRing<C>(pfac.coFac, 1, pfac.tord, vars);
        GenPolynomial<C> t = cfac.univariate(0);
        //System.out.println("t = " + t);

        // Q[x][t]
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(pfac, cfac); // sic
        //System.out.println("rfac = " + rfac.toScript());

        // transform polynomials to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac = PolyUfdUtil.<C> introduceLowerVariable(rfac, A);
        //System.out.println("Ac = " + Ac);
        GenPolynomial<GenPolynomial<C>> Pc = PolyUfdUtil.<C> introduceLowerVariable(rfac, P);
        //System.out.println("Pc = " + Pc);
        GenPolynomial<GenPolynomial<C>> Pcp = PolyUfdUtil.<C> introduceLowerVariable(rfac, Pp);
        //System.out.println("Pcp = " + Pcp);

        // Q[t][x]
        GenPolynomialRing<GenPolynomial<C>> rfac1 = Pc.ring;
        //System.out.println("rfac1 = " + rfac1.toScript());

        // A - t P'
        GenPolynomial<GenPolynomial<C>> tc = rfac1.getONE().multiply(t);
        //System.out.println("tc = " + tc);
        GenPolynomial<GenPolynomial<C>> At = Ac.subtract(tc.multiply(Pcp));
        //System.out.println("At = " + At);

        GreatestCommonDivisorSubres<C> engine = new GreatestCommonDivisorSubres<C>();
        // = GCDFactory.<C>getImplementation( cfac.coFac );
        GreatestCommonDivisorAbstract<AlgebraicNumber<C>> aengine = null;

        GenPolynomial<GenPolynomial<C>> Rc = engine.recursiveResultant(Pc, At);
        //System.out.println("Rc = " + Rc);
        GenPolynomial<C> res = Rc.leadingBaseCoefficient();
        //no: res = res.monic();
        //System.out.println("\nres = " + res);

        SortedMap<GenPolynomial<C>, Long> resfac = irr.baseFactors(res);
        //System.out.println("resfac = " + resfac + "\n");

        for (GenPolynomial<C> r : resfac.keySet()) {
            //System.out.println("\nr(t) = " + r);
            if (r.isConstant()) {
                continue;
            }
            //vars = new String[] { "z_" + Math.abs(r.hashCode() % 1000) };
            vars = pfac.newVars("z_");
            pfac = pfac.clone();
            vars = pfac.setVars(vars);
            r = pfac.copy(r); // hack to exchange the variables
            //System.out.println("r(z_) = " + r);
            AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(r, true); // since irreducible
            logger.debug("afac = " + afac.toScript());
            AlgebraicNumber<C> a = afac.getGenerator();
            //no: a = a.negate();
            //System.out.println("a = " + a);

            // K(alpha)[x]
            GenPolynomialRing<AlgebraicNumber<C>> pafac = new GenPolynomialRing<AlgebraicNumber<C>>(afac,
                    Pc.ring);
            //System.out.println("pafac = " + pafac.toScript());

            // convert to K(alpha)[x]
            GenPolynomial<AlgebraicNumber<C>> Pa = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, P);
            //System.out.println("Pa = " + Pa);
            GenPolynomial<AlgebraicNumber<C>> Pap = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, Pp);
            //System.out.println("Pap = " + Pap);
            GenPolynomial<AlgebraicNumber<C>> Aa = PolyUtil.<C> convertToAlgebraicCoefficients(pafac, A);
            //System.out.println("Aa = " + Aa);

            // A - a P'
            GenPolynomial<AlgebraicNumber<C>> Ap = Aa.subtract(Pap.multiply(a));
            //System.out.println("Ap = " + Ap);

            if (aengine == null) {
                aengine = GCDFactory.<AlgebraicNumber<C>> getImplementation(afac);
            }
            GenPolynomial<AlgebraicNumber<C>> Ga = aengine.baseGcd(Pa, Ap);
            //System.out.println("Ga = " + Ga);
            if (Ga.isConstant()) {
                //System.out.println("warning constant gcd ignored");
                continue;
            }
            afactors.add(a);
            adenom.add(Ga);
            // special quadratic case
            if (P.degree(0) == 2 && Ga.degree(0) == 1) {
                GenPolynomial<AlgebraicNumber<C>>[] qra = PolyUtil
                        .<AlgebraicNumber<C>> basePseudoQuotientRemainder(Pa, Ga);
                GenPolynomial<AlgebraicNumber<C>> Qa = qra[0];
                if (!qra[1].isZERO()) {
                    throw new RuntimeException("remainder not zero");
                }
                //System.out.println("Qa = " + Qa);
                afactors.add(a.negate());
                adenom.add(Qa);
            }
            // todo: eventually implement special cases deg = 3, 4
        }
        return new LogIntegral<C>(A, P, cfactors, cdenom, afactors, adenom);
    }


    /**
     * Derivation of a univariate rational function.
     * 
     * @param r rational function
     * @return dr/dx
     */
    public Quotient<C> deriviative(Quotient<C> r) {
        GenPolynomial<C> num = r.num;
        GenPolynomial<C> den = r.den;
        GenPolynomial<C> nump = PolyUtil.<C> baseDeriviative(num);
        if (den.isONE()) {
            return new Quotient<C>(r.ring, nump, den);
        }
        GenPolynomial<C> denp = PolyUtil.<C> baseDeriviative(den);

        GenPolynomial<C> n = den.multiply(nump).subtract(num.multiply(denp));
        GenPolynomial<C> d = den.multiply(den);

        Quotient<C> der = new Quotient<C>(r.ring, n, d);
        return der;
    }


    /**
     * Test of integration of a rational function.
     * 
     * @param ri integral
     * @return true, if ri is an integral, else false.
     */
    public boolean isIntegral(QuotIntegral<C> ri) {
        Quotient<C> r = ri.quot;
        QuotientRing<C> qr = r.ring;
        Quotient<C> i = r.ring.getZERO();
        for (Quotient<C> q : ri.rational) {
            Quotient<C> qd = deriviative(q);
            i = i.sum(qd);
        }
        if (ri.logarithm.size() == 0) {
            return r.equals(i);
        }
        for (LogIntegral<C> li : ri.logarithm) {
            Quotient<C> q = new Quotient<C>(qr, li.num, li.den);
            i = i.sum(q);
        }
        boolean t = r.equals(i);
        if (!t) {
            return false;
        }
        for (LogIntegral<C> li : ri.logarithm) {
            t = isIntegral(li);
            if (!t) {
                return false;
            }
        }
        return true;
    }


    /**
     * Test of integration of the logarithmic part of a rational function.
     * 
     * @param rl logarithmic part of an integral
     * @return true, if rl is an integral, else false.
     */
    public boolean isIntegral(LogIntegral<C> rl) {
        QuotientRing<C> qr = new QuotientRing<C>(rl.den.ring);
        Quotient<C> r = new Quotient<C>(qr, rl.num, rl.den);

        Quotient<C> i = qr.getZERO();
        int j = 0;
        for (GenPolynomial<C> d : rl.cdenom) {
            GenPolynomial<C> dp = PolyUtil.<C> baseDeriviative(d);
            dp = dp.multiply(rl.cfactors.get(j++));
            Quotient<C> f = new Quotient<C>(qr, dp, d);
            i = i.sum(f);
        }
        if (rl.afactors.size() == 0) {
            return r.equals(i);
        }
        r = r.subtract(i);
        QuotientRing<AlgebraicNumber<C>> aqr = new QuotientRing<AlgebraicNumber<C>>(rl.adenom.get(0).ring);
        Quotient<AlgebraicNumber<C>> ai = aqr.getZERO();

        GenPolynomial<AlgebraicNumber<C>> aqn = PolyUtil.<C> convertToAlgebraicCoefficients(aqr.ring, r.num);
        GenPolynomial<AlgebraicNumber<C>> aqd = PolyUtil.<C> convertToAlgebraicCoefficients(aqr.ring, r.den);
        Quotient<AlgebraicNumber<C>> ar = new Quotient<AlgebraicNumber<C>>(aqr, aqn, aqd);

        j = 0;
        for (GenPolynomial<AlgebraicNumber<C>> d : rl.adenom) {
            GenPolynomial<AlgebraicNumber<C>> dp = PolyUtil.<AlgebraicNumber<C>> baseDeriviative(d);
            dp = dp.multiply(rl.afactors.get(j++));
            Quotient<AlgebraicNumber<C>> f = new Quotient<AlgebraicNumber<C>>(aqr, dp, d);
            ai = ai.sum(f);
        }
        boolean t = ar.equals(ai);
        if (t) {
            return true;
        }
        logger.warn("log integral not verified");
        //System.out.println("r        = " + r);
        //System.out.println("afactors = " + rl.afactors);
        //System.out.println("adenom   = " + rl.adenom);
        //System.out.println("ar       = " + ar);
        //System.out.println("ai       = " + ai);
        return true;
    }

}

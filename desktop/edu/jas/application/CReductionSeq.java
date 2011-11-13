/*
 * $Id: CReductionSeq.java 2824 2009-09-23 18:02:32Z kredel $
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;


/**
 * Polynomial parametric ring reduction sequential use algorithm. Implements
 * normalform, condition construction and polynomial determination.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */
public class CReductionSeq<C extends GcdRingElem<C>>
       /* extends ReductionAbstract<C> */
       /* implements CReduction<C> */ {


    private static final Logger logger = Logger.getLogger(CReductionSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    private final boolean info = logger.isInfoEnabled();


    /**
     * Greatest common divisor engine.
     */
    protected final GreatestCommonDivisor<C> engine;


    /**
     * Polynomial coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Flag if top-reduction only should be used.
     */
    protected boolean top = true; // false;


    /**
     * Constructor.
     * @param rf coefficient factory.
     */
    public CReductionSeq(RingFactory<C> rf) {
        cofac = rf;
        // System.out.println("cofac = " + cofac);
        engine = GCDFactory.<C> getImplementation(cofac);
    }


    /**
     * S-Polynomial.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    public ColorPolynomial<C> SPolynomial(ColorPolynomial<C> Ap, ColorPolynomial<C> Bp) {
        if (Bp == null || Bp.isZERO()) {
            return Bp;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }

        Map.Entry<ExpVector, GenPolynomial<C>> ma = Ap.red.leadingMonomial();
        Map.Entry<ExpVector, GenPolynomial<C>> mb = Bp.red.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f); // EVLCM(e,f);
        ExpVector e1 = g.subtract(e); // EVDIF(g,e);
        ExpVector f1 = g.subtract(f); // EVDIF(g,f);

        GenPolynomial<C> a = ma.getValue();
        GenPolynomial<C> b = mb.getValue();

        GenPolynomial<C> c = engine.gcd(a, b);
        if (!c.isONE()) {
            // System.out.println("gcd =s " + c);
            a = a.divide(c);
            b = b.divide(c);
        }

        ColorPolynomial<C> App = Ap.multiply(b, e1);
        ColorPolynomial<C> Bpp = Bp.multiply(a, f1);
        ColorPolynomial<C> Cp = App.subtract(Bpp);
        return Cp;
    }


    /**
     * Is top reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<ColorPolynomial<C>> P, ColorPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        for (ColorPolynomial<C> p : P) {
            if (p == null) {
                return false;
            }
            ExpVector f = p.leadingExpVector();
            if (f == null) {
                return false;
            }
            if (e == null) {
                return false;
            }
            mt = e.multipleOf(f); // EVMT( e, p.leadingExpVector() );
            if (mt) {
                return true;
            }
        }
        return false;
    }


    /**
     * Is reducible.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is reducible with respect to Pp.
     */
    public boolean isReducible(List<ColorPolynomial<C>> Pp, ColorPolynomial<C> Ap) {
        return !isNormalform(Pp, Ap);
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public boolean isNormalform(List<ColorPolynomial<C>> Pp, ColorPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        if (Ap == null || Ap.isZERO()) {
            return true;
        }
        int l;
        ColorPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new ColorPolynomial[l];
            // P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        ColorPolynomial<C>[] p = new ColorPolynomial[l];
        Map.Entry<ExpVector, GenPolynomial<C>> m;
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].red.leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                j++;
            }
        }
        l = j;
        boolean mt = false;
        for (ExpVector e : Ap.red.getMap().keySet()) {
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]); // EVMT( e, htl[i] );
                if (mt) {
                   System.out.println("not normalform " + Ap + ", P[i] = " + P[i]);
                   return false;
                }
            }
            if ( top ) {
               return true;
            }
        }
        for (ExpVector e : Ap.white.getMap().keySet()) {
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]); // EVMT( e, htl[i] );
                if (mt) {
                   System.out.println("not normalform " + Ap + ", P[i] = " + P[i]);
                   return false;
                }
            }
            if ( top ) {
               return true;
            }
        }
        return true;
    }


    /**
     * Is in Normalform.
     * @param Pp polynomial list.
     * @return true if each Ap in Pp is in normalform with respect to Pp\{Ap}.
     */
    public boolean isNormalform(List<ColorPolynomial<C>> Pp) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        ColorPolynomial<C> Ap;
        List<ColorPolynomial<C>> P = new LinkedList<ColorPolynomial<C>>(Pp);
        int s = P.size();
        for (int i = 0; i < s; i++) {
            Ap = P.remove(i);
            if (!isNormalform(P, Ap)) {
                return false;
            }
            P.add(Ap);
        }
        return true;
    }


    /**
     * Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @param cond condition for these polynomials.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public ColorPolynomial<C> normalform(Condition<C> cond, List<ColorPolynomial<C>> Pp,
            ColorPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        Map.Entry<ExpVector, GenPolynomial<C>> m;
        int l;
        ColorPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new ColorPolynomial[l];
            // P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        Object[] lbc = new Object[l]; // want C[]
        ColorPolynomial<C>[] p = new ColorPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            if (P[i] == null) {
                continue;
            }
            p[i] = P[i];
            m = p[i].red.leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        GenPolynomial<C> a;
        boolean mt = false;
        GenPolynomial<GenPolynomial<C>> zero = p[0].red.ring.getZERO();
        ColorPolynomial<C> R = new ColorPolynomial<C>(zero, zero, zero);

        // ColorPolynomial<C> T = null;
        ColorPolynomial<C> Q = null;
        ColorPolynomial<C> S = Ap;
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            Condition.Color col = cond.color(a);
            if (col == Condition.Color.GREEN) { // move to green terms
                GenPolynomial<GenPolynomial<C>> g = S.green.sum(a, e);
                GenPolynomial<GenPolynomial<C>> r = S.red;
                GenPolynomial<GenPolynomial<C>> w = S.white;
                if (S.red.isZERO()) {
                    w = w.subtract(a, e);
                } else { // only in minimalGB
                    logger.info("green_red = " + zero.sum(a, e));
                    r = r.subtract(a, e);
                }
                S = new ColorPolynomial<C>(g, r, w);
                continue;
            }
            if (col == Condition.Color.WHITE) { // refine condition
                // System.out.println("white = " + zero.sum(a,e));
                // return S; // return for new case distinction
            }
            // System.out.println("NF, e = " + e);
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]); // EVMT( e, htl[i] );
                if (mt)
                    break;
            }
            if (!mt) {
                // logger.debug("irred");
                if (top) {
                    return S;
                }
                R = R.sum(a, e);
                S = S.subtract(a, e);
                // System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]); // EVDIF( e, htl[i] );
                // logger.info("red div = " + e);
                GenPolynomial<C> c = (GenPolynomial<C>) lbc[i];
                GenPolynomial<C> g = engine.gcd(a, c);
                if (!g.isONE()) {
                    // System.out.println("gcd = " + g);
                    a = a.divide(g);
                    c = c.divide(g);
                }
                S = S.multiply(c);
                R = R.multiply(c);
                Q = p[i].multiply(a, e);
                S = S.subtract(Q);
            }
        }
        return R;
    }


    /*
     * -------- coloring and condition stuff ------------------------------
     */

    /**
     * Case distinction conditions of parametric polynomial list. The returned
     * condition determines the polynomial list.
     * @param L list of parametric polynomials.
     * @return list of conditions as case distinction.
     */
    public List<Condition<C>> caseDistinction(List<GenPolynomial<GenPolynomial<C>>> L) {
        List<Condition<C>> cd = new ArrayList<Condition<C>>();
        if (L == null || L.size() == 0) {
            return cd;
        }
        for (GenPolynomial<GenPolynomial<C>> A : L) {
            if (A != null && !A.isZERO()) {
                cd = caseDistinction(cd, A);
            }
        }
        // System.out.println("cd = " + cd);
        return cd;
    }


    /**
     * Case distinction conditions of parametric polynomial list.
     * @param cd a list of conditions.
     * @param A a parametric polynomial.
     * @return list of conditions as case distinction extending the conditions
     *         in cd.
     */
    public List<Condition<C>> caseDistinction(List<Condition<C>> cd,
            GenPolynomial<GenPolynomial<C>> A) {
        if (A == null || A.isZERO()) {
            return cd;
        }
        if (cd == null) {
            cd = new ArrayList<Condition<C>>();
        }
        if (cd.size() == 0) { // construct empty condition
            RingFactory<GenPolynomial<C>> crfac = A.ring.coFac;
            GenPolynomialRing<C> cfac = (GenPolynomialRing<C>) crfac;
            Condition<C> sc = new Condition<C>(cfac);
            cd.add(sc);
        }
        GenPolynomial<GenPolynomial<C>> Ap;
        GenPolynomial<GenPolynomial<C>> Bp;

        List<Condition<C>> C = new ArrayList<Condition<C>>( /* leer! */);
        for (Condition<C> cond : cd) {
            // System.out.println("caseDist: " + cond);
            Condition<C> cz = cond;
            Ap = A;
            while (!Ap.isZERO()) {
                GenPolynomial<C> c = Ap.leadingBaseCoefficient();
                Bp = Ap.reductum();
                //System.out.println("to color: " + c);
                switch (cz.color(c)) {
                case GREEN:
                    // System.out.println("color green: " + c);
                    Ap = Bp;
                    continue;
                case RED:
                    // System.out.println("color red: " + c);
                    C.add(cz);
                    // wrong: return C;
                    Ap = A.ring.getZERO();
                    continue;
                    // break;
                case WHITE:
                default:
                    // System.out.println("color white: " + c);
                    Condition<C> nc = cz.extendNonZero(c);
                    if (nc != null) { // no contradiction
                        if (!cz.equals(nc)) {
                            C.add(nc);
                        } else {
                            cz = null;
                            Ap = A.ring.getZERO();
                            continue;
                        }
                    } else {
                        System.out.println("this should not be printed " + c);
                    }
                    Condition<C> ez = cz.extendZero(c);
                    if (ez != null) {
                        cz = ez;
                    } else { // contradiction
                        cz = null;
                        Ap = A.ring.getZERO();
                        continue;
                    }
                    Ap = Bp;
                }
            }
            // System.out.println("cond cz: " + cz);
            if (cz == null || cz.isContradictory() || C.contains(cz)) {
                // System.out.println("not added entry " + cz);
            } else {
                C.add(cz);
            }
        }
        // System.out.println("C = " + C);
        return C;
    }


    /**
     * Case distinction conditions of parametric polynomial list.
     * @param A a parametric polynomial.
     * @param cond a condition.
     * @return list of case distinction conditions.
     */
    public List<Condition<C>> caseDistinction(Condition<C> cond,
            GenPolynomial<GenPolynomial<C>> A) {
        List<Condition<C>> cd = new ArrayList<Condition<C>>();
        if (A == null || A.isZERO()) {
            return cd;
        }
        cd.add(cond);
        cd = caseDistinction(cd, A);
        if (info) {
            StringBuffer s = new StringBuffer("extending condition: " + cond + "\n");
            s.append("case distinctions: [ \n");
            for (Condition<C> c : cd) {
                s.append(c.toString() + "\n");
            }
            s.append("]");
            logger.info(s.toString());
        }
        return cd;
    }


    /**
     * Determine polynomial list.
     * @param H polynomial list.
     * @return new determined list of colored systems.
     */
    public List<ColoredSystem<C>> determine(List<GenPolynomial<GenPolynomial<C>>> H) {
        if (H == null || H.size() == 0) {
            List<ColoredSystem<C>> CS = new ArrayList<ColoredSystem<C>>();
            return CS;
        }
        //System.out.println("of determine     = " + H);
        Collections.reverse(H);
        List<Condition<C>> cd = caseDistinction(H);
        //System.out.println("case Distinction = " + cd);
        //System.out.println("of determine     = " + H);
        return determine(cd, H);
    }


    /**
     * Determine polynomial list.
     * @param H polynomial list.
     * @param cd case distiction, a condition list.
     * @return new determined list of colored systems.
     */
    public List<ColoredSystem<C>> determine(List<Condition<C>> cd,
            List<GenPolynomial<GenPolynomial<C>>> H) {
        List<ColoredSystem<C>> CS = new ArrayList<ColoredSystem<C>>();
        if (H == null || H.size() == 0) {
            return CS;
        }
        for (Condition<C> cond : cd) {
            logger.info("determine wrt cond = " + cond);
            if (cond.zero.isONE()) { // should not happen
                System.out.println("ideal is one = " + cond.zero);
                // continue; // can treat all coeffs as green
            }
            // if ( cond.isEmpty() ) { // do not use this code
            // continue; // can skip condition (?)
            // }
            List<ColorPolynomial<C>> S = cond.determine(H);
            ColoredSystem<C> cs = new ColoredSystem<C>(cond, S);
            CS.add(cs);
        }
        return CS;
    }

}

/*
 * $Id: RReductionSeq.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RegularRingElem;


/**
 * Polynomial Regular ring Reduction sequential use algorithm. Implements
 * normalform and boolean closure stuff.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class RReductionSeq<C extends RegularRingElem<C>> extends ReductionAbstract<C>
        implements RReduction<C> {


    private static final Logger logger = Logger.getLogger(RReductionSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public RReductionSeq() {
    }


    /**
     * Is top reducible. Condition is a b != 0, for a=ldcf(A) and b=ldcf(B) and
     * lt(B) | lt(A) for some B in F.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    @Override
    public boolean isTopReducible(List<GenPolynomial<C>> P, GenPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        C a = A.leadingBaseCoefficient();
        a = a.idempotent();
        for (GenPolynomial<C> p : P) {
            mt = e.multipleOf(p.leadingExpVector());
            if (mt) {
                C b = p.leadingBaseCoefficient();
                //C r = a.multiply( b );
                //C r = a.multiply( b.idempotent() );
                C r = a.idempotentAnd(b);
                mt = !r.isZERO();
                if (mt) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Is strong top reducible. Condition is idempotent(a) == idempotent(b), for
     * a=ldcf(A) and b=ldcf(B) and lt(B) | lt(A) for some B in F.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is string top reducible with respect to P.
     */
    public boolean isStrongTopReducible(List<GenPolynomial<C>> P, GenPolynomial<C> A) {
        if (P == null || P.isEmpty()) {
            return false;
        }
        if (A == null || A.isZERO()) {
            return false;
        }
        boolean mt = false;
        ExpVector e = A.leadingExpVector();
        C a = A.leadingBaseCoefficient();
        a = a.idempotent();
        for (GenPolynomial<C> p : P) {
            mt = e.multipleOf(p.leadingExpVector());
            if (mt) {
                C b = p.leadingBaseCoefficient();
                mt = a.equals(b.idempotent());
                if (mt) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Is in Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return true if Ap is in normalform with respect to Pp.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isNormalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return true;
        }
        if (Ap == null || Ap.isZERO()) {
            return true;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RegularRingElem[l]; // want <C>
        GenPolynomial<C>[] p = new GenPolynomial[l];
        Map.Entry<ExpVector, C> m;
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            if (P[i] == null) {
                continue;
            }
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        boolean mt = false;
        Map<ExpVector, C> Am = Ap.getMap();
        for (ExpVector e : Am.keySet()) {
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    C a = Am.get(e);
                    //C r = a.multiply( lbc[i] );
                    //C r = a.idempotent().multiply( lbc[i].idempotent() );
                    C r = a.idempotentAnd(lbc[i]);
                    mt = !r.isZERO();
                    if (mt) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Normalform using r-reduction.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return r-nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[]) new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        //System.out.println("l = " + l);
        Map.Entry<ExpVector, C> m;
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RegularRingElem[l]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[]) new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            if (P[i] == null) {
                continue;
            }
            p[i] = P[i].abs();
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e, f;
        C a, b;
        C r = null;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    //r = a.multiply( lbc[i] );
                    //r = a.idempotent().multiply( lbc[i].idempotent() );
                    r = a.idempotentAnd(lbc[i]);
                    //System.out.println("r = " + r);
                    mt = !r.isZERO(); // && mt
                    if (mt) {
                        b = a.divide(lbc[i]);
                        if (b.isZERO()) { // strange case in regular rings
                            System.out.println("b == zero: r = " + r);
                            continue;
                        }
                        f = e.subtract(htl[i]);
                        //logger.info("red div = " + f);
                        Q = p[i].multiply(b, f);
                        S = S.subtract(Q); // not ok with reductum
                        f = S.leadingExpVector();
                        if (!e.equals(f)) {
                            a = Ap.ring.coFac.getZERO();
                            break;
                        }
                        a = S.leadingBaseCoefficient();
                    }
                }
            }
            if (!a.isZERO()) { //! mt ) { 
                //logger.debug("irred");
                R = R.sum(a, e);
                S = S.reductum();
            }
        }
        return R; //.abs(); // not monic if not boolean closed
    }


    /**
     * GB criterium 4. Use only for commutative polynomial rings. <b>Note:</b>
     * Experimental version for r-Groebner bases.
     * @param A polynomial.
     * @param B polynomial.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    @Override
    public boolean criterion4(GenPolynomial<C> A, GenPolynomial<C> B, ExpVector e) {
        if (logger.isInfoEnabled()) {
            if (!A.ring.equals(B.ring)) {
                logger.error("rings equal");
            }
            if (A instanceof GenSolvablePolynomial || B instanceof GenSolvablePolynomial) {
                logger.error("GBCriterion4 not applicabable to SolvablePolynomials");
                return true;
            }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ei.sum(ej);
        // boolean t =  g == e ;
        ExpVector h = g.subtract(e);
        int s = h.signum();
        if (s == 0) { // disjoint ht
            C a = A.leadingBaseCoefficient();
            C b = B.leadingBaseCoefficient();
            C d = a.multiply(b);
            if (d.isZERO()) { // a guess
                //System.out.println("d1 = " + d + ", a = " + a + ", b = " + b);
                return false; // can skip pair
            }
        }
        return true; //! ( s == 0 );
    }


    /**
     * GB criterium 4. Use only for commutative polynomial rings. <b>Note:</b>
     * Experimental version for r-Groebner bases.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    @Override
    public boolean criterion4(GenPolynomial<C> A, GenPolynomial<C> B) {
        if (logger.isInfoEnabled()) {
            if (A instanceof GenSolvablePolynomial || B instanceof GenSolvablePolynomial) {
                logger.error("GBCriterion4 not applicabable to SolvablePolynomials");
                return true;
            }
        }
        ExpVector ei = A.leadingExpVector();
        ExpVector ej = B.leadingExpVector();
        ExpVector g = ei.sum(ej);
        ExpVector e = ei.lcm(ej);
        //        boolean t =  g == e ;
        ExpVector h = g.subtract(e);
        int s = h.signum();
        if (s == 0) { // disjoint ht
            C a = A.leadingBaseCoefficient();
            C b = B.leadingBaseCoefficient();
            C d = a.multiply(b);
            if (d.isZERO()) { // a guess
                return false; // can skip pair
            }
        }
        return true; //! ( s == 0 );
    }


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return Ap - row*Pp = nf(Pp,Ap) , the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> row,
            List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        //throw new RuntimeException("not jet implemented");
        int l;
        GenPolynomial<C>[] P;
        synchronized (Pp) {
            l = Pp.size();
            P = (GenPolynomial<C>[]) new GenPolynomial[l];
            //P = Pp.toArray();
            for (int i = 0; i < Pp.size(); i++) {
                P[i] = Pp.get(i);
            }
        }
        //System.out.println("l = " + l);
        Map.Entry<ExpVector, C> m;
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RegularRingElem[l]; // want <C>
        GenPolynomial<C>[] p = (GenPolynomial<C>[]) new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e, f;
        C a;
        C r = null;
        boolean mt = false;
        GenPolynomial<C> fac = null;
        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> R = Ap.ring.getZERO();
        GenPolynomial<C> Q = null;
        GenPolynomial<C> S = Ap;
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt) {
                    //r = a.idempotent().multiply( lbc[i].idempotent() );
                    //r = a.multiply( lbc[i] );
                    r = a.idempotentAnd(lbc[i]);
                    //System.out.println("r = " + r);
                    mt = !r.isZERO(); // && mt
                    if (mt) {
                        a = a.divide(lbc[i]);
                        if (a.isZERO()) { // strange case in regular rings
                            System.out.println("b == zero: r = " + r);
                            continue;
                        }
                        f = e.subtract(htl[i]);
                        //logger.info("red div = " + f);
                        Q = p[i].multiply(a, f);
                        S = S.subtract(Q); // not ok with reductum

                        fac = row.get(i);
                        if (fac == null) {
                            fac = zero.sum(a, f);
                        } else {
                            fac = fac.sum(a, f);
                        }
                        row.set(i, fac);

                        f = S.leadingExpVector();
                        if (!e.equals(f)) {
                            a = Ap.ring.coFac.getZERO();
                            break;
                        }
                        a = S.leadingBaseCoefficient();
                    }
                }
            }
            if (!a.isZERO()) { //! mt ) { 
                //logger.debug("irred");
                R = R.sum(a, e);
                S = S.reductum();
            }
        }
        return R; //.abs(); not for recording 
    }


    /**
     * Irreducible set. May not be boolean closed.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P.
     */
    @Override
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp) {
        ArrayList<GenPolynomial<C>> P = new ArrayList<GenPolynomial<C>>();
        if (Pp == null) {
            return null;
        }
        for (GenPolynomial<C> a : Pp) {
            if (!a.isZERO()) {
                P.add(a);
            }
        }
        int l = P.size();
        if (l <= 1)
            return P;

        int irr = 0;
        ExpVector e;
        ExpVector f;
        GenPolynomial<C> a;
        Iterator<GenPolynomial<C>> it;
        logger.debug("irr = ");
        while (irr != l) {
            //it = P.listIterator(); 
            //a = P.get(0); //it.next();
            a = P.remove(0);
            e = a.leadingExpVector();
            a = normalform(P, a);
            // no not make monic because of boolean closure
            logger.debug(String.valueOf(irr));
            if (a.isZERO()) {
                l--;
                if (l <= 1) {
                    return P;
                }
            } else {
                f = a.leadingExpVector();
                if (e.equals(f)) {
                    // lbcf(a) eventually shorter
                    // correct since longer coeffs can reduce shorter coeffs
                    irr++;
                } else {
                    irr = 0;
                }
                P.add(a);
            }
        }
        //System.out.println();
        return P;
    }


    /*
     * -------- boolean closure stuff -----------------------------------------
     */

    /**
     * Is boolean closed, test if A == idempotent(ldcf(A)) A.
     * @param A polynomial.
     * @return true if A is boolean closed, else false.
     */
    public boolean isBooleanClosed(GenPolynomial<C> A) {
        if (A == null || A.isZERO()) {
            return true;
        }
        C a = A.leadingBaseCoefficient();
        C i = a.idempotent();
        GenPolynomial<C> B = A.multiply(i);
        // better run idemAnd on coefficients
        if (A.equals(B)) {
            return true;
        }
        return false;
    }


    /**
     * Is boolean closed, test if all A in F are boolean closed.
     * @param F polynomial list.
     * @return true if F is boolean closed, else false.
     */
    public boolean isBooleanClosed(List<GenPolynomial<C>> F) {
        if (F == null || F.size() == 0) {
            return true;
        }
        for (GenPolynomial<C> a : F) {
            if (a == null || a.isZERO()) {
                continue;
            }
            //System.out.println("a = " + a);
            if (!isBooleanClosed(a)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Is reduced boolean closed, test if all A in F are boolean closed or br(A)
     * reduces to zero.
     * @param F polynomial list.
     * @return true if F is boolean closed, else false.
     */
    public boolean isReducedBooleanClosed(List<GenPolynomial<C>> F) {
        if (F == null || F.size() == 0) {
            return true;
        }
        GenPolynomial<C> b;
        GenPolynomial<C> r;
        for (GenPolynomial<C> a : F) {
            //System.out.println("a = " + a);
            if (a == null) {
                continue;
            }
            while (!a.isZERO()) {
                if (!isBooleanClosed(a)) {
                    b = booleanClosure(a);
                    b = normalform(F, b);
                    if (!b.isZERO()) { //F.contains(r)
                        return false;
                    }
                }
                r = booleanRemainder(a);
                r = normalform(F, r);
                if (!r.isZERO()) { //F.contains(r)
                    return false;
                }
                //System.out.println("r = " + r);
                a = r;
            }
        }
        return true;
    }


    /**
     * Boolean closure, compute idempotent(ldcf(A)) A.
     * @param A polynomial.
     * @return bc(A).
     */
    public GenPolynomial<C> booleanClosure(GenPolynomial<C> A) {
        if (A == null || A.isZERO()) {
            return A;
        }
        C a = A.leadingBaseCoefficient();
        C i = a.idempotent();
        GenPolynomial<C> B = A.multiply(i);
        return B;
    }


    /**
     * Boolean remainder, compute idemComplement(ldcf(A)) A.
     * @param A polynomial.
     * @return br(A).
     */
    public GenPolynomial<C> booleanRemainder(GenPolynomial<C> A) {
        if (A == null || A.isZERO()) {
            return A;
        }
        C a = A.leadingBaseCoefficient();
        C i = a.idemComplement();
        GenPolynomial<C> B = A.multiply(i);
        return B;
    }


    /**
     * Boolean closure, compute BC(A) for all A in F.
     * @param F polynomial list.
     * @return bc(F).
     */
    public List<GenPolynomial<C>> booleanClosure(List<GenPolynomial<C>> F) {
        if (F == null || F.size() == 0) {
            return F;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>(F.size());
        for (GenPolynomial<C> a : F) {
            if (a == null) {
                continue;
            }
            while (!a.isZERO()) {
                GenPolynomial<C> b = booleanClosure(a);
                B.add(b);
                a = booleanRemainder(a);
            }
        }
        return B;
    }


    /**
     * Reduced boolean closure, compute BC(A) for all A in F.
     * @param F polynomial list.
     * @return red(bc(F)) = bc(red(F)).
     */
    public List<GenPolynomial<C>> reducedBooleanClosure(List<GenPolynomial<C>> F) {
        if (F == null || F.size() == 0) {
            return F;
        }
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>(F);
        GenPolynomial<C> a;
        GenPolynomial<C> b;
        GenPolynomial<C> c;
        int len = B.size();
        for (int i = 0; i < len; i++) { // not B.size(), it changes
            a = B.remove(0);
            if (a == null) {
                continue;
            }
            while (!a.isZERO()) {
                //System.out.println("a = " + a);
                b = booleanClosure(a);
                //System.out.println("b = " + b);
                b = booleanClosure(normalform(B, b));
                if (b.isZERO()) {
                    break;
                }
                B.add(b); // adds as last
                c = a.subtract(b); // = BR(a mod B)
                //System.out.println("c = " + c);
                c = normalform(B, c);
                a = c;
            }
        }
        return B;
    }


    /**
     * Reduced boolean closure, compute BC(A) modulo F.
     * @param A polynomial.
     * @param F polynomial list.
     * @return red(bc(A)).
     */
    public List<GenPolynomial<C>> reducedBooleanClosure(List<GenPolynomial<C>> F,
            GenPolynomial<C> A) {
        List<GenPolynomial<C>> B = new ArrayList<GenPolynomial<C>>();
        if (A == null || A.isZERO()) {
            return B;
        }
        GenPolynomial<C> a = A;
        GenPolynomial<C> b;
        GenPolynomial<C> c;
        while (!a.isZERO()) {
            //System.out.println("a = " + a);
            b = booleanClosure(a);
            //System.out.println("b = " + b);
            b = booleanClosure(normalform(F, b));
            if (b.isZERO()) {
                break;
            }
            B.add(b); // adds as last
            c = a.subtract(b); // = BR(a mod F) 
            //System.out.println("c = " + c);
            c = normalform(F, c);
            //System.out.println("c = " + c);
            a = c;
        }
        return B;
    }

}

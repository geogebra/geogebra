/*
 * $Id: RGroebnerBasePseudoSeq.java 2505 2009-03-22 19:53:50Z kredel $
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RegularRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;


/**
 * Regular ring Groebner Base with pseudo reduction sequential algorithm.
 * Implements R-Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class RGroebnerBasePseudoSeq<C extends RegularRingElem<C>> extends
        RGroebnerBaseSeq<C> {


    private static final Logger logger = Logger.getLogger(RGroebnerBasePseudoSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Greatest common divisor engine for coefficient content and primitive
     * parts.
     */
    protected final GreatestCommonDivisorAbstract<C> engine;


    /**
     * Pseudo reduction engine.
     */
    protected final RPseudoReduction<C> red;


    /**
     * Coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf coefficient ring factory.
     */
    public RGroebnerBasePseudoSeq(RingFactory<C> rf) {
        this(new RPseudoReductionSeq<C>(), rf);
    }


    /**
     * Constructor.
     * @param red R-pseudo-Reduction engine
     * @param rf coefficient ring factory.
     */
    public RGroebnerBasePseudoSeq(RPseudoReduction<C> red, RingFactory<C> rf) {
        super(red);
        this.red = red;
        cofac = rf;
        engine = (GreatestCommonDivisorAbstract<C>) GCDFactory.<C> getImplementation(rf);
        // not used: engine =
        // (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getProxy( rf );
    }


    /**
     * R-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a R-Groebner base of F.
     */
    @Override
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        if (F == null) {
            return F;
        }
        /* boolean closure */
        List<GenPolynomial<C>> bcF = red.reducedBooleanClosure(F);
        logger.info("#bcF-#F = " + (bcF.size() - F.size()));
        F = bcF;
        /* normalize input */
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedRPairlist<C> pairlist = null;
        for (GenPolynomial<C> p : F) {
            if (!p.isZERO()) {
                p = engine.basePrimitivePart(p); // not monic, no field
                p = p.abs();
                if (p.isConstant() && p.leadingBaseCoefficient().isFull()) {
                    G.clear();
                    G.add(p);
                    return G; // since boolean closed and no threads are activated
                }
                G.add(p); // G.add( 0, p ); //reverse list
                if (pairlist == null) {
                    pairlist = new OrderedRPairlist<C>(modv, p.ring);
                }
                // putOne not required
                pairlist.put(p);
            }
        }
        if (G.size() <= 1) {
            return G; // since boolean closed and no threads are activated
        }
        /* loop on critical pairs */
        Pair<C> pair;
        GenPolynomial<C> pi;
        GenPolynomial<C> pj;
        GenPolynomial<C> S;
        // GenPolynomial<C> D;
        GenPolynomial<C> H;
        List<GenPolynomial<C>> bcH;
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            // System.out.println("pair = " + pair);
            if (pair == null)
                continue;

            pi = pair.pi;
            pj = pair.pj;
            if (logger.isDebugEnabled()) {
                logger.info("pi    = " + pi);
                logger.info("pj    = " + pj);
            }
            if (!red.moduleCriterion(modv, pi, pj)) {
                continue;
            }

            // S-polynomial -----------------------
            // Criterion3(), Criterion4() not applicable
            S = red.SPolynomial(pi, pj);
            if (S.isZERO()) {
                pair.setZero();
                continue;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ht(S) = " + S.leadingExpVector());
            }

            H = red.normalform(G, S);
            if (H.isZERO()) {
                pair.setZero();
                continue;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }
            H = engine.basePrimitivePart(H);
            H = H.abs(); // not monic, no field
            if (H.isConstant() && H.leadingBaseCoefficient().isFull()) {
                // mostly useless
                G.clear();
                G.add(H);
                return G; // not boolean closed ok, no threads are activated
            }
            if (logger.isDebugEnabled()) {
                logger.debug("H = " + H);
            }
            if (!H.isZERO()) {
                // logger.info("Sred = " + H);
                // len = G.size();
                bcH = red.reducedBooleanClosure(G, H);
                // logger.info("#bcH = " + bcH.size());
                // G.addAll( bcH );
                for (GenPolynomial<C> h : bcH) {
                    h = engine.basePrimitivePart(h);
                    h = h.abs(); // monic() not ok, since no field
                    logger.info("bc(Sred) = " + h);
                    G.add(h);
                    pairlist.put(h);
                }
                if (debug) {
                    if (!pair.getUseCriterion3() || !pair.getUseCriterion4()) {
                        logger.info("H != 0 but: " + pair);
                    }
                }
            }
        }
        logger.debug("#sequential list = " + G.size());
        G = minimalGB(G);
        // G = red.irreducibleSet(G); // not correct since not boolean closed
        logger.info("pairlist #put = " + pairlist.putCount() + " #rem = "
                + pairlist.remCount()
        // + " #total = " + pairlist.pairCount()
                );
        return G;
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    @Override
    public List<GenPolynomial<C>> minimalGB(List<GenPolynomial<C>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>(Gp.size());
        for (GenPolynomial<C> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                a = a.abs(); // already positive in GB
                G.add(a);
            }
        }
        // remove top reducible polynomials
        logger.info("minGB start with " + G.size());
        GenPolynomial<C> a, b;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            b = a;
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // try to drop polynomial
                List<GenPolynomial<C>> ff;
                ff = new ArrayList<GenPolynomial<C>>(G);
                ff.addAll(F);
                a = red.normalform(ff, a);
                if (a.isZERO()) {
                    if (false && !isGB(ff)) { // is really required, but why?
                        logger.info("minGB not dropped " + b);
                        F.add(b);
                    } else {
                        if (debug) {
                            logger.debug("minGB dropped " + b);
                        }
                    }
                } else { // happens
                    logger.info("minGB not zero " + a);
                    F.add(a);
                }
            } else { // not top reducible, keep polynomial
                F.add(a);
            }
        }
        G = F;
        // reduce remaining polynomials
        int len = G.size();
        int el = 0;
        while (el < len) {
            el++;
            a = G.remove(0);
            b = a;
            a = red.normalform(G, a);
            a = engine.basePrimitivePart(a); // not a.monic() since no field
            a = a.abs();
            if (red.isBooleanClosed(a)) {
                List<GenPolynomial<C>> ff;
                ff = new ArrayList<GenPolynomial<C>>(G);
                ff.add(a);
                if (true || isGB(ff)) {
                    if (debug) {
                        logger.debug("minGB reduced " + b + " to " + a);
                    }
                    G.add(a);
                } else {
                    logger.info("minGB not reduced " + b + " to " + a);
                    G.add(b);
                }
                continue;
            } else {
                logger.info("minGB not boolean closed " + a);
                G.add(b); // do not reduce
            }
        }
        /* stratify: collect polynomials with equal leading terms */
        ExpVector e, f;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        List<GenPolynomial<C>> ff;
        ff = new ArrayList<GenPolynomial<C>>(G);
        for (int i = 0; i < ff.size(); i++) {
            a = ff.get(i);
            if (a == null || a.isZERO()) {
                continue;
            }
            e = a.leadingExpVector();
            for (int j = i + 1; j < ff.size(); j++) {
                b = ff.get(j);
                if (b == null || b.isZERO()) {
                    continue;
                }
                f = b.leadingExpVector();
                if (e.equals(f)) {
                    // System.out.println("minGB e == f: " + a + ", " + b);
                    a = a.sum(b);
                    ff.set(j, null);
                }
            }
            F.add(a);
        }
        if (true || isGB(F)) {
            G = F;
        } else {
            logger.info("minGB not stratified " + F);
        }
        logger.info("minGB end   with " + G.size());
        return G;
    }


    /*
     * Minimal ordered Groebner basis. 
     * @param Gp a Groebner base. 
     * @return a reduced Groebner base of Gp. 
     * @todo use primitivePart
     */
    List<GenPolynomial<C>> minimalGBtesting(List<GenPolynomial<C>> Gp) {
        if (Gp == null || Gp.size() <= 1) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>(Gp.size());
        for (GenPolynomial<C> a : Gp) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            // wg monic do not return G;
        }
        // remove top reducible polynomials
        GenPolynomial<C> a, b;
        List<GenPolynomial<C>> F;
        List<GenPolynomial<C>> bcH;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            b = a;
            if (red.isTopReducible(G, a) || red.isTopReducible(F, a)) {
                // drop polynomial
                if (true || debug) {
                    List<GenPolynomial<C>> ff;
                    ff = new ArrayList<GenPolynomial<C>>(G);
                    ff.addAll(F);
                    a = red.normalform(ff, a);
                    if (!a.isZERO()) {
                        System.out.println("minGB nf(a) != 0 " + a);
                        bcH = red.reducedBooleanClosure(G, a);
                        if (bcH.size() > 1) { // never happend so far
                            System.out.println("minGB not bc: bcH size = " + bcH.size());
                            F.add(b); // do not replace, stay with b
                        } else {
                            // System.out.println("minGB add bc(a): a = " + a + ",
                            // bc(a) = " + bcH.get(0));
                            F.add(b); // do not replace, stay with b
                            // F.addAll( bcH );
                        }
                    } else {
                        if (!isGB(ff)) {
                            System.out.println("minGB not dropped " + b);
                            F.add(b);
                        } else {
                            System.out.println("minGB dropped " + b);
                        }
                    }
                }
            } else { // not top reducible, keep polynomial
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            // wg monic return G;
        }
        // reduce remaining polynomials
        int len = G.size();
        int el = 0;
        // System.out.println("minGB reducing " + len);
        while (el < len) {
            el++;
            a = G.remove(0);
            b = a;
            // System.out.println("minGB doing " + el + ", a = " + a);
            a = red.normalform(G, a);
            // not bc:
            a = engine.basePrimitivePart(a); // not a.monic() since no field
            if (red.isBooleanClosed(a)) {
                List<GenPolynomial<C>> ff;
                ff = new ArrayList<GenPolynomial<C>>(G);
                ff.add(a);
                if (isGB(ff)) {
                    System.out.println("minGB reduced " + b + " to " + a);
                    G.add(a);
                } else {
                    System.out.println("minGB not reduced " + b + " to " + a);
                    G.add(b);
                }
                continue;
            }
            System.out.println("minGB not bc: a = " + a + "\n BC(a) = "
                    + red.booleanClosure(a) + ", BR(a) = " + red.booleanRemainder(a));
            bcH = red.reducedBooleanClosure(G, a);
            if (bcH.size() > 1) {
                System.out.println("minGB not bc: bcH size = " + bcH.size());
                G.add(b); // do not reduce
            } else {
                // G.addAll( bcH );
                G.add(b); // do not reduce
                for (GenPolynomial<C> h : bcH) {
                    h = engine.basePrimitivePart(h);
                    h = h.abs(); // monic() not ok, since no field
                    // G.add( h );
                }
            }
        }
        // make abs if possible
        F = new ArrayList<GenPolynomial<C>>(G.size());
        for (GenPolynomial<C> p : G) {
            a = p.abs();
            F.add(a);
        }
        G = F;

        if (false) {
            return G;
        }

        // stratify: collect polynomials with equal leading terms
        ExpVector e, f;
        F = new ArrayList<GenPolynomial<C>>(G.size());
        for (int i = 0; i < G.size(); i++) {
            a = G.get(i);
            if (a == null || a.isZERO()) {
                continue;
            }
            e = a.leadingExpVector();
            for (int j = i + 1; j < G.size(); j++) {
                b = G.get(j);
                if (b == null || b.isZERO()) {
                    continue;
                }
                f = b.leadingExpVector();
                if (e.equals(f)) {
                    // System.out.println("minGB e == f: " + a + ", " + b);
                    a = a.sum(b);
                    G.set(j, null);
                }
            }
            F.add(a);
        }
        G = F;

        // info on boolean algebra element blocks
        Map<C, List<GenPolynomial<C>>> bd = new TreeMap<C, List<GenPolynomial<C>>>();
        for (GenPolynomial<C> p : G) {
            C cf = p.leadingBaseCoefficient();
            cf = cf.idempotent();
            List<GenPolynomial<C>> block = bd.get(cf);
            if (block == null) {
                block = new ArrayList<GenPolynomial<C>>();
            }
            block.add(p);
            bd.put(cf, block);
        }
        System.out.println("\nminGB bd:");
        for (C k : bd.keySet()) {
            System.out.println("\nkey = " + k + ":");
            System.out.println("val = " + bd.get(k));
        }
        System.out.println();
        //
        return G;
    }

}

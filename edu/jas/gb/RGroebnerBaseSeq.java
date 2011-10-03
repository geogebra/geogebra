/*
 * $Id: RGroebnerBaseSeq.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RegularRingElem;


/**
 * Regular ring Groebner Base sequential algorithm. Implements R-Groebner bases
 * and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class RGroebnerBaseSeq<C extends RegularRingElem<C>> extends
        GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(RGroebnerBaseSeq.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    protected RReduction<C> red; // shadow super.red 


    /**
     * Constructor.
     */
    public RGroebnerBaseSeq() {
        this(new RReductionSeq<C>());
    }


    /**
     * Constructor.
     * @param red R-Reduction engine
     */
    public RGroebnerBaseSeq(RReduction<C> red) {
        super(red);
        this.red = red;
        assert super.red == this.red;
    }


    /**
     * R-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a R-Groebner base, else false.
     */
    @Override
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {
        if (F == null) {
            return true;
        }
        if (!red.isBooleanClosed(F)) {
            if (true || debug) {
                System.out.println("not boolean closed");
            }
            return false;
        }
        GenPolynomial<C> pi, pj, s, h;
        for (int i = 0; i < F.size(); i++) {
            pi = F.get(i);
            for (int j = i + 1; j < F.size(); j++) {
                pj = F.get(j);
                if (!red.moduleCriterion(modv, pi, pj)) {
                    continue;
                }
                // red.criterion4 not applicable
                s = red.SPolynomial(pi, pj);
                if (s.isZERO()) {
                    continue;
                }
                s = red.normalform(F, s);
                if (!s.isZERO()) {
                    if (debug) {
                        System.out.println("p" + i + " = " + pi);
                        System.out.println("p" + j + " = " + pj);
                        System.out.println("s-pol = " + red.SPolynomial(pi, pj));
                        System.out.println("s-pol(" + i + "," + j + ") != 0: " + s);
                        //System.out.println("red = " + red.getClass().getName());
                    }
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * R-Groebner base using pairlist class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a R-Groebner base of F.
     */
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        /* boolean closure */
        List<GenPolynomial<C>> bcF = red.reducedBooleanClosure(F);
        logger.info("#bcF-#F = " + (bcF.size() - F.size()));
        F = bcF;
        /* normalize input */
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        OrderedRPairlist<C> pairlist = null;
        for (GenPolynomial<C> p : F) {
            if (!p.isZERO()) {
                p = p.monic(); //p.abs(); // not monic, monic if boolean closed
                if (p.isONE()) {
                    G.clear();
                    G.add(p);
                    return G; // since boolean closed and no threads are activated
                }
                G.add(p); //G.add( 0, p ); //reverse list
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
        //GenPolynomial<C> D;
        GenPolynomial<C> H;
        List<GenPolynomial<C>> bcH;
        //int len = G.size();
        //System.out.println("len = " + len);
        while (pairlist.hasNext()) {
            pair = pairlist.removeNext();
            //System.out.println("pair = " + pair);
            if (pair == null)
                continue;

            pi = pair.pi;
            pj = pair.pj;
            if (logger.isDebugEnabled()) {
                logger.debug("pi    = " + pi);
                logger.debug("pj    = " + pj);
            }
            if (!red.moduleCriterion(modv, pi, pj)) {
                continue;
            }

            // S-polynomial -----------------------
            // Criterion3() Criterion4() not applicable
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
            //H = H.monic(); // only for boolean closed H
            if (logger.isDebugEnabled()) {
                logger.debug("ht(H) = " + H.leadingExpVector());
            }

            if (H.isONE()) { // mostly useless
                G.clear();
                G.add(H);
                return G; // not boolean closed ok, since no threads are activated
            }
            if (logger.isDebugEnabled()) {
                logger.debug("H = " + H);
            }
            if (!H.isZERO()) {
                logger.info("Sred = " + H);
                //len = G.size();
                bcH = red.reducedBooleanClosure(G, H);
                logger.info("#bcH = " + bcH.size());
                for (GenPolynomial<C> h : bcH) {
                    h = h.monic(); // monic() ok, since boolean closed
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
        //G = red.irreducibleSet(G);
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
                // already positive a = a.abs();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            //wg monic do not return G;
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
                    if (!a.isZERO()) { // happens
                        logger.info("minGB not zero " + a);
                        bcH = red.reducedBooleanClosure(G, a);
                        if (bcH.size() > 1) { // never happend so far
                            System.out.println("minGB not bc: bcH size = " + bcH.size());
                            F.add(b); // do not replace, stay with b
                        } else {
                            //System.out.println("minGB add bc(a): a = " + a + ", bc(a) = " + bcH.get(0));
                            F.addAll(bcH);
                        }
                    } else {
                        //System.out.println("minGB dropped " + b);
                    }
                }
            } else {
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
        while (el < len) {
            a = G.remove(0);
            b = a;
            //System.out.println("doing " + a.length());
            a = red.normalform(G, a);
            bcH = red.reducedBooleanClosure(G, a);
            if (bcH.size() > 1) {
                System.out.println("minGB not bc: bcH size = " + bcH.size());
                G.add(b); // do not reduce
            } else {
                G.addAll(bcH);
            }
            el++;
        }
        // make monic if possible
        F = new ArrayList<GenPolynomial<C>>(G.size());
        for (GenPolynomial<C> p : G) {
            a = p.monic().abs();
            if (p.length() != a.length()) {
                System.out.println("minGB not bc: #p != #a: a = " + a + ", p = " + p);
                a = p; // dont make monic for now
            }
            F.add(a);
        }
        G = F;

        /* stratify: collect polynomials with equal leading terms */
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
                    //System.out.println("minGB e == f: " + a + ", " + b);
                    a = a.sum(b);
                    G.set(j, null);
                }
            }
            F.add(a);
        }
        G = F;

        /* info on boolean algebra element blocks 
        Map<C,List<GenPolynomial<C>>> bd = new TreeMap<C,List<GenPolynomial<C>>>();
        for ( GenPolynomial<C> p : G ) { 
            C cf = p.leadingBaseCoefficient();
            cf = cf.idempotent();
            List<GenPolynomial<C>> block = bd.get( cf );
            if ( block == null ) {
               block = new ArrayList<GenPolynomial<C>>();
            }
            block.add( p ); 
            bd.put( cf, block );
        }
        System.out.println("\nminGB bd:");
        for( C k: bd.keySet() ) {
           System.out.println("\nkey = " + k + ":");
           System.out.println("val = " + bd.get(k));
        }
        System.out.println();
        */
        return G;
    }

}

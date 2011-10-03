/*
 * $Id: ComprehensiveGroebnerBaseSeq.java 3163 2010-06-03 19:06:31Z kredel $
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBasePseudoSeq;
import edu.jas.gb.GBFactory;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.ufd.SquarefreeFactory;
import edu.jas.ufd.Squarefree;
import edu.jas.ufd.SquarefreeAbstract;


/**
 * Comprehensive Groebner Base sequential algorithm. Implements faithful comprehensive
 * Groebner bases via Groebner systems and CGB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ComprehensiveGroebnerBaseSeq<C extends GcdRingElem<C>>
/* extends GroebnerBaseAbstract<GenPolynomial<C>> */{


    private static final Logger logger = Logger
            .getLogger(ComprehensiveGroebnerBaseSeq.class);


    private static final boolean debug = logger.isDebugEnabled();


    /**
     * Squarefree for coefficient content and primitive
     * parts.
     */
    protected final SquarefreeAbstract<C> engine;


    /**
     * Flag if gcd engine should be used.
     */
    private final boolean notFaithfull = false;


    /**
     * Comprehensive reduction engine.
     */
    protected final CReductionSeq<C> cred;


    /**
     * Polynomial coefficient ring factory.
     */
    protected final RingFactory<C> cofac;


    /**
     * Constructor.
     * @param rf base coefficient ring factory.
     */
    public ComprehensiveGroebnerBaseSeq(RingFactory<C> rf) {
        this(new CReductionSeq<C>(rf), rf);
    }


    /**
     * Constructor.
     * @param red C-pseudo-Reduction engine
     * @param rf base coefficient ring factory.
     */
    @SuppressWarnings("unchecked")
    public ComprehensiveGroebnerBaseSeq(CReductionSeq<C> red, RingFactory<C> rf) {
        // super(null); // red not possible since type of type
        cred = red;
        cofac = rf;
        // selection for C but used for R:
        //engine e = GCDFactory.<C> getImplementation(cofac);
        engine = SquarefreeFactory.<C>getImplementation(rf);
    }


    /**
     * Comprehensive-Groebner base test.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isGB(List<GenPolynomial<GenPolynomial<C>>> F) {
        return isGB(0, F);
    }


    /**
     * Comprehensive-Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isGB(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        // return isGBcol( modv, F );
        return isGBsubst(modv, F);
    }


    /**
     * Comprehensive-Groebner base test using colored systems.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isGBcol(List<GenPolynomial<GenPolynomial<C>>> F) {
        return isGBcol(0, F);
    }


    /**
     * Comprehensive-Groebner base test using colored systems.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isGBcol(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.size() == 0) {
            return true;
        }
        List<ColoredSystem<C>> CS = cred.determine(F);
        return isGBsys(modv, CS);
    }


    /**
     * Comprehensive-Groebner system test.
     * @param CS list of colored systems.
     * @return true, if CS is a Comprehensive-Groebner system, else false.
     */
    // @Override
    public boolean isGBsys(List<ColoredSystem<C>> CS) {
        return isGBsys(0, CS);
    }


    /**
     * Comprehensive-Groebner system test.
     * @param modv module variable number.
     * @param CS list of colored systems.
     * @return true, if CS is a Comprehensive-Groebner system, else false.
     */
    // @Override
    public boolean isGBsys(int modv, List<ColoredSystem<C>> CS) {
        if (CS == null || CS.size() == 0) {
            return true;
        }
        ColorPolynomial<C> p, q, h, hp;
        for (ColoredSystem<C> cs : CS) {
            if (true || debug) {
                if (!cs.isDetermined()) {
                    System.out.println("not determined, cs = " + cs);
                    return false;
                }
                if (!cs.checkInvariant()) {
                    System.out.println("not invariant, cs = " + cs);
                    return false;
                }
            }
            Condition<C> cond = cs.condition;
            List<ColorPolynomial<C>> S = cs.list;
            int k = S.size();
            for (int j = 0; j < k; j++) {
                p = S.get(j);
                for (int l = j + 1; l < k; l++) {
                    q = S.get(l);
                    h = cred.SPolynomial(p, q);
                    // System.out.println("spol(a,b) = " + h);
                    h = cred.normalform(cond, S, h);
                    // System.out.println("NF(spol(a,b)) = " + h);
                    if (true || debug) {
                        if (!cred.isNormalform(S, h)) {
                            System.out.println("not normalform, h = " + h);
                            System.out.println("cs = " + cs);
                            return false;
                        }
                    }
                    if (!h.isZERO()) {
                        hp = cond.reDetermine(h);
                        if (!hp.isZERO()) {
                            System.out.println("p = " + p);
                            System.out.println("q = " + q);
                            System.out.println("not zero:   NF(spol(p,q))  = " + h);
                            System.out.println("redetermine(NF(spol(p,q))) = " + hp);
                            System.out.println("cs = " + cs);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Comprehensive-Groebner base test using substitution.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isGBsubst(List<GenPolynomial<GenPolynomial<C>>> F) {
        return isGBsubst(0, F);
    }


    /**
     * Comprehensive-Groebner base test using substitution.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isGBsubst(int modv, List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null || F.size() == 0) {
            return true;
        }
        GenPolynomial<GenPolynomial<C>> f = F.get(0); // assert non Zero
        GenPolynomialRing<GenPolynomial<C>> cf = f.ring;

        List<ColoredSystem<C>> CS = cred.determine(F);
        if (logger.isDebugEnabled()) {
            logger.info("determined polynomials =\n" + CS);
        }
        // substitute zero conditions into parameter coefficients and test
        for (ColoredSystem<C> cs : CS) {
            Ideal<C> id = cs.condition.zero;
            ResidueRing<C> r = new ResidueRing<C>(id);
            GenPolynomialRing<Residue<C>> rf = new GenPolynomialRing<Residue<C>>(r, cf);
            List<GenPolynomial<Residue<C>>> list = PolyUtilApp.<C> toResidue(rf, F);
            //GroebnerBase<Residue<C>> bb = new GroebnerBasePseudoSeq<Residue<C>>(r);
            GroebnerBase<Residue<C>> bb = GBFactory.getImplementation(r);
            boolean t = bb.isGB(list);
            if (!t) {
                System.out.println("test condition = " + cs.condition);
                System.out.println("no GB for residue coefficients = " + list);
                return false;
            }
        }

        // substitute random ideal into parameter coefficients and test
        GenPolynomialRing<C> ccf = (GenPolynomialRing<C>) cf.coFac;
        int nv = ccf.nvar - 2;
        if (nv < 1) {
            nv = 1;
        }
        List<GenPolynomial<C>> il = new ArrayList<GenPolynomial<C>>();
        int i = 0;
        int j = 1;
        while (i < nv) {
            j++;
            GenPolynomial<C> p = ccf.random(j + 1);
            // System.out.println("p = " + p);
            if (p.isConstant()) {
                continue;
            }
            if (p.isZERO()) {
                continue;
            }
            p = engine.squarefreePart(p);
            il.add(p);
            i++;
        }
        logger.info("random ideal = " + il);
        Ideal<C> id = new Ideal<C>(ccf, il);
        ResidueRing<C> r = new ResidueRing<C>(id);
        GenPolynomialRing<Residue<C>> rf = new GenPolynomialRing<Residue<C>>(r, cf);
        List<GenPolynomial<Residue<C>>> list = PolyUtilApp.<C> toResidue(rf, F);
        logger.info("random residue = " + r.ideal.getList());
        //GroebnerBase<Residue<C>> bb = new GroebnerBasePseudoSeq<Residue<C>>(r);
        GroebnerBase<Residue<C>> bb = GBFactory.getImplementation(r);
        boolean t = bb.isGB(list);
        if (!t) {
            System.out.println("no GB for residue coefficients = " + list);
            return false;
        }
        return true;
    }


    /**
     * Comprehensive-Groebner system test.
     * @param F Groebner system.
     * @return true, if F is a Comprehensive-Groebner system, else false.
     */
    // @Override
    public boolean isGBsys(GroebnerSystem<C> F) {
        return isGBsys(0, F.list);
    }


    /**
     * Comprehensive-Groebner base test.
     * @param F Groebner system.
     * @return true, if F is a Comprehensive-Groebner base, else false.
     */
    // @Override
    public boolean isCGB(GroebnerSystem<C> F) {
        return isGB( F.getCGB() );
    }


    /**
     * Comprehensive-Groebner system and base test.
     * @param F Groebner system.
     * @return true, if F is a Comprehensive-Groebner system and base, else false.
     */
    // @Override
    public boolean isGB(GroebnerSystem<C> F) {
        return isGBsys(0, F.list) && isGB(F.getCGB());
    }


    /**
     * Comprehensive Groebner base system using pairlist class.
     * @param F polynomial list.
     * @return GBsys(F) a Comprehensive Groebner system of F.
     */
    // @Override
    // @SuppressWarnings("unchecked")
    public GroebnerSystem<C> GBsys(List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null) {
            return null;
        }
        List<ColoredSystem<C>> CSp = new ArrayList<ColoredSystem<C>>();
        if (F.size() == 0) {
            return new GroebnerSystem<C>( CSp );
        }
        // extract coefficient factory
        GenPolynomial<GenPolynomial<C>> f = F.get(0);
        GenPolynomialRing<GenPolynomial<C>> fac = f.ring;
        // determine polynomials
        List<ColoredSystem<C>> CS = cred.determine(F);
        // System.out.println("CS = " + CS);
        // CS.remove(0); // empty colored system
        if (logger.isInfoEnabled()) {
            logger.info("determined polynomials =\n" + CS);
        }

        // setup pair lists
        List<ColoredSystem<C>> CSs = new ArrayList<ColoredSystem<C>>();
        ColoredSystem<C> css;
        for (ColoredSystem<C> cs : CS) {
            OrderedCPairlist<C> pairlist = new OrderedCPairlist<C>(fac);
            for (ColorPolynomial<C> p : cs.list) {
                // System.out.println("p = " + p);
                pairlist.put(p);
            }
            css = new ColoredSystem<C>(cs.condition, cs.list, pairlist);
            CSs.add(css);
        }

        // main loop
        List<ColoredSystem<C>> CSb = new ArrayList<ColoredSystem<C>>();
        List<ColoredSystem<C>> ncs;
        List<ColoredSystem<C>> CSh, CSbh;
        ColoredSystem<C> cs;
        List<ColorPolynomial<C>> G;
        OrderedCPairlist<C> pairlist;
        Condition<C> cond;
        int si = 0;
        while (CSs.size() > 0) {
            cs = CSs.get(0); // remove(0);
            si++;
            logger.info("poped GBsys number    " + si + " with condition = "
                    + cs.condition);
            logger.info("poped GBsys (remaining " + (CSs.size() - 1)
                    + ") with pairlist  = " + cs.pairlist);
            if ( !cs.isDetermined() ) {
                cs = cs.reDetermine();
            }
            pairlist = cs.pairlist;
            G = cs.list;
            cond = cs.condition;
            // logger.info( pairlist.toString() );

            CPair<C> pair;
            ColorPolynomial<C> pi;
            ColorPolynomial<C> pj;
            ColorPolynomial<C> S;
            // GenPolynomial<GenPolynomial<C>> H;
            ColorPolynomial<C> H;
            while (pairlist.hasNext()) {
                pair = pairlist.removeNext();
                if (pair == null)
                    continue;

                pi = pair.pi;
                pj = pair.pj;
                if (debug) {
                    logger.info("pi    = " + pi);
                    logger.info("pj    = " + pj);
                }

                S = cred.SPolynomial(pi, pj);
                if (S.isZERO()) {
                    pair.setZero();
                    continue;
                }
                if (debug) {
                    // logger.info("ht(S) = " + S.leadingExpVector() );
                    logger.info("S = " + S);
                }

                H = cred.normalform(cond, G, S);
                if (H.isZERO()) {
                    pair.setZero();
                    continue;
                }
                if (debug) {
                    logger.info("ht(H) = " + H.leadingExpVector());
                }

                H = H.abs();
                if (debug) {
                    logger.debug("H = " + H);
                }
                logger.info("H = " + H);
                if (!H.isZERO()) {
                    CSh = new ArrayList<ColoredSystem<C>>();
                    ncs = determineAddPairs(cs, H);
                    if (ncs == null || ncs.size() == 0) {
                        continue;
                    }
                    cs = ncs.remove(0); // remove other?
                    pairlist = cs.pairlist;
                    G = cs.list;
                    cond = cs.condition;
                    logger.info("replaced main branch = " + cond);
                    logger.info("#new systems       = " + ncs.size());
                    int yi = CSs.size();
                    for (ColoredSystem<C> x : ncs) {
                        if ( !x.isDetermined() ) {
                            x = x.reDetermine();
                        }
                        CSs = x.addToList(CSs);
                    }
                    logger.info("#new systems added = " + (CSs.size()-yi));
                }
            }
            // all s-pols reduce to zero in this branch
            if ( !cs.isDetermined() ) {
                cs = cs.reDetermine();
            }
            CSb.add(cs);
            CSs.remove(0);
            logger.info("done with = " + cs.condition);
        }
        // all branches done
        CSh = new ArrayList<ColoredSystem<C>>();
        for (ColoredSystem<C> x : CSb) {
            // System.out.println("G = " + x.list );
            if ( !x.isDetermined() ) {
                x = x.reDetermine();
            }
            cs = minimalGB(x);
            // System.out.println("min(G) = " + cs.list );
            if ( !cs.isDetermined() ) {
                cs = cs.reDetermine();
            }
            // cs = new ColoredSystem<C>( x.condition, G, x.pairlist );
            CSh.add(cs);
            logger.info("#sequential done = " + x.condition);
            logger.info(x.pairlist.toString());
        }
        CSb = new ArrayList<ColoredSystem<C>>(CSh);
        return new GroebnerSystem<C>( CSb );
    }


    /**
     * Determine polynomial relative to a condition of a colored system and add
     * pairs.
     * @param cs a colored system.
     * @param A color polynomial.
     * @return list of colored systems, the conditions extending the condition
     *         of cs.
     */
    public List<ColoredSystem<C>> determineAddPairs(ColoredSystem<C> cs,
                                                    ColorPolynomial<C> A) {
        List<ColoredSystem<C>> NCS = new ArrayList<ColoredSystem<C>>();
        if (A == null || A.isZERO()) {
            // NCS.add( cs );
            return NCS;
        }
        List<ColorPolynomial<C>> S = cs.list;
        Condition<C> cond = cs.condition; // .clone(); done in Condition
                                            // itself
        OrderedCPairlist<C> pl = cs.pairlist;

        List<ColorPolynomial<C>> Sp;
        ColorPolynomial<C> nz;
        ColoredSystem<C> NS;
        // if ( A.isDetermined() ) { ... } // dont use this
        // System.out.println("to determine = " + A);
        GenPolynomial<GenPolynomial<C>> Ap = A.getPolynomial();
        List<Condition<C>> cd = cred.caseDistinction(cond, Ap);
        logger.info("# cases = " + cd.size());
        for (Condition<C> cnd : cd) {
            //nz = cnd.determine(Ap);
            nz = cnd.reDetermine(A);
            if (nz == null || nz.isZERO()) {
                logger.info("zero determined nz = " + nz);
                Sp = new ArrayList<ColorPolynomial<C>>(S);
                OrderedCPairlist<C> PL = pl.clone();
                NS = new ColoredSystem<C>(cnd, Sp, PL);
                try {
                    if ( !NS.isDetermined() ) {
                        NS = NS.reDetermine();
                    }
                } catch(RuntimeException e) {
                    System.out.println("Contradiction in NS_0 = " + NS);
                    //e.printStackTrace();
                    continue;
                }
                NCS = NS.addToList(NCS);
                continue;
            }
            if (S.contains(nz)) {
                System.out.println("*** S.contains(nz) ***");
                continue;
            }
            logger.info("new determined nz = " + nz);
            Sp = new ArrayList<ColorPolynomial<C>>(S);
            Sp.add(nz);
            OrderedCPairlist<C> PL = pl.clone();
            PL.put(nz);
            NS = new ColoredSystem<C>(cnd, Sp, PL);
            try {
                if ( !NS.isDetermined() ) {
                    NS = NS.reDetermine();
                }
            } catch(RuntimeException e) {
                System.out.println("Contradiction in NS = " + NS);
                //e.printStackTrace();
                continue;
            }
            NCS = NS.addToList(NCS);
        }
        // System.out.println("new determination = " + NCS);
        return NCS;
    }


    /**
     * Comprehensive Groebner base via Groebner system.
     * @param F polynomial list.
     * @return GB(F) a Comprehensive Groebner base of F.
     */
    // @Override
    // @SuppressWarnings("unchecked")
    public List<GenPolynomial<GenPolynomial<C>>> GB(List<GenPolynomial<GenPolynomial<C>>> F) {
        if (F == null) {
            return F;
        }
        // compute Groebner system
        GroebnerSystem<C> gs = GBsys(F);
        // System.out.println("\n\nGBsys = " + gs);
        return gs.getCGB();
    }


    /**
     * Minimal ordered Groebner basis.
     * @param cs colored system.
     * @return a reduced Groebner base of Gp.
     */
    // @Override
    public ColoredSystem<C> minimalGB(ColoredSystem<C> cs) {
        // List<ColorPolynomial<C>> Gp ) {
        if (cs == null || cs.list == null || cs.list.size() <= 1) {
            return cs;
        }
        // remove zero polynomials
        List<ColorPolynomial<C>> G = new ArrayList<ColorPolynomial<C>>(cs.list.size());
        for (ColorPolynomial<C> a : cs.list) {
            if (a != null && !a.isZERO()) { // always true in GB()
                // already positive a = a.abs();
                G.add(a);
            }
        }
        if (G.size() <= 1) {
            return new ColoredSystem<C>(cs.condition, G, cs.pairlist);
        }
        // System.out.println("G check " + G);
        // remove top reducible polynomials
        Condition<C> cond = cs.condition;
        ColorPolynomial<C> a, b;
        List<ColorPolynomial<C>> F;
        F = new ArrayList<ColorPolynomial<C>>(G.size());
        while (G.size() > 0) {
            a = G.remove(0);
            b = a;
            // System.out.println("check " + b);
            if (false) {
                if (a.red.leadingBaseCoefficient().isConstant()) { // dont drop
                                                                    // these
                    F.add(a);
                    continue;
                }
            }
            if (cred.isTopReducible(G, a) || cred.isTopReducible(F, a)) {
                // drop polynomial
                if (false) {
                    // System.out.println("trying to drop " + a);
                    List<ColorPolynomial<C>> ff;
                    ff = new ArrayList<ColorPolynomial<C>>(G);
                    ff.addAll(F);
                    a = cred.normalform(cond, ff, a);
                    try {
                        a = cond.reDetermine(a);
                    } catch (RuntimeException ignored) {
                    }
                    if (!a.isZERO()) {
                        if (false || debug) {
                            System.out.println("error, nf(a) != 0 " + b + ", " + a);
                        }
                        F.add(b);
                    }
                }
            } else {
                F.add(a);
            }
        }
        G = F;
        if (G.size() <= 1) {
            return new ColoredSystem<C>(cs.condition, G, cs.pairlist);
        }
        // reduce remaining polynomials
        int len = G.size();
        int i = 0;
        while (i < len) {
            a = G.remove(0);
            b = a;
            ExpVector e = a.red.leadingExpVector();
            // System.out.println("reducing " + a);
            a = cred.normalform(cond, G, a); // unchanged by top reduction
            // System.out.println("reduced " + a);
            try {
                a = cond.reDetermine(a);
            } catch (RuntimeException ignored) {
            }
            ExpVector f = a.red.leadingExpVector();
            // a = ##engine.basePrimitivePart(a); //a.monic(); was not required
            // a = a.abs();
            // a = red.normalform( F, a );
            if (e.equals(f)) {
                G.add(a); // adds as last
            } else { // should not happen
                if (false) {
                    System.out.println("error, nf(a) not determined " + b + ", " + a);
                }
                G.add(b); // adds as last
            }
            i++;
        }
        return new ColoredSystem<C>(cs.condition, G, cs.pairlist);
    }

}

/*
 * $Id: Ideal.java 3205 2010-07-04 15:47:10Z kredel $
 */

package edu.jas.application;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import edu.jas.gb.ExtendedGB;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBasePartial;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.GroebnerBaseSeqPairSeq;
import edu.jas.gb.GBFactory;
import edu.jas.gb.Reduction;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.OptimizedPolynomialList;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderOptimization;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.NotInvertibleException;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.FactorAbstract;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.PolyUfdUtil;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;


/**
 * Ideal implements some methods for ideal arithmetic, for example intersection,
 * quotient and zero and positive dimensional ideal decomposition.
 * @author Heinz Kredel
 */
public class Ideal<C extends GcdRingElem<C>> implements Comparable<Ideal<C>>, Serializable, Cloneable {


    private static final Logger logger = Logger.getLogger(Ideal.class);


    private final boolean debug = true || logger.isDebugEnabled();


    /**
     * The data structure is a PolynomialList.
     */
    protected PolynomialList<C> list;


    /**
     * Indicator if list is a Groebner Base.
     */
    protected boolean isGB;


    /**
     * Indicator if test has been performed if this is a Groebner Base.
     */
    protected boolean testGB;


    /**
     * Indicator if list has optimized term order.
     */
    protected boolean isTopt;


    /**
     * Groebner base engine.
     */
    protected final GroebnerBaseAbstract<C> bb;


    /**
     * Reduction engine.
     */
    protected final Reduction<C> red;


    /**
     * Squarefree decomposition engine.
     */
    protected final SquarefreeAbstract<C> engine;


    /**
     * Constructor.
     * @param ring polynomial ring
     */
    public Ideal(GenPolynomialRing<C> ring) {
        this(ring, new ArrayList<GenPolynomial<C>>());
    }


    /**
     * Constructor.
     * @param ring polynomial ring
     * @param F list of polynomials
     */
    public Ideal(GenPolynomialRing<C> ring, List<GenPolynomial<C>> F) {
        this(new PolynomialList<C>(ring, F));
    }


    /**
     * Constructor.
     * @param ring polynomial ring
     * @param F list of polynomials
     * @param gb true if F is known to be a Groebner Base, else false
     */
    public Ideal(GenPolynomialRing<C> ring, List<GenPolynomial<C>> F, boolean gb) {
        this(new PolynomialList<C>(ring, F), gb);
    }


    /**
     * Constructor.
     * @param ring polynomial ring
     * @param F list of polynomials
     * @param gb true if F is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     */
    public Ideal(GenPolynomialRing<C> ring, List<GenPolynomial<C>> F, boolean gb, boolean topt) {
        this(new PolynomialList<C>(ring, F), gb, topt);
    }


    /**
     * Constructor.
     * @param list polynomial list
     */
    public Ideal(PolynomialList<C> list) {
        this(list, false);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public Ideal(PolynomialList<C> list, GroebnerBaseAbstract<C> bb, Reduction<C> red) {
        this(list, false, bb, red);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     */
    public Ideal(PolynomialList<C> list, boolean gb) {
        //this(list, gb, new GroebnerBaseSeqPairSeq<C>(), new ReductionSeq<C>());
        this(list, gb, GBFactory.getImplementation(list.ring.coFac) );
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     */
    public Ideal(PolynomialList<C> list, boolean gb, boolean topt) {
        //this(list, gb, topt, new GroebnerBaseSeqPairSeq<C>(), new ReductionSeq<C>());
        this(list, gb, topt, GBFactory.getImplementation(list.ring.coFac) );
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public Ideal(PolynomialList<C> list, boolean gb, GroebnerBaseAbstract<C> bb, Reduction<C> red) {
        this(list, gb, false, bb, red);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param bb Groebner Base engine
     */
    public Ideal(PolynomialList<C> list, boolean gb, GroebnerBaseAbstract<C> bb) {
        this(list, gb, false, bb, bb.red);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     * @param bb Groebner Base engine
     */
    public Ideal(PolynomialList<C> list, boolean gb, boolean topt, GroebnerBaseAbstract<C> bb) {
        this(list, gb, topt, bb, bb.red);
    }


    /**
     * Constructor.
     * @param list polynomial list
     * @param gb true if list is known to be a Groebner Base, else false
     * @param topt true if term order is optimized, else false
     * @param bb Groebner Base engine
     * @param red Reduction engine
     */
    public Ideal(PolynomialList<C> list, boolean gb, boolean topt, 
                 GroebnerBaseAbstract<C> bb, Reduction<C> red) {
        if (list == null || list.list == null) {
            throw new IllegalArgumentException("list and list.list may not be null");
        }
        this.list = list;
        this.isGB = gb;
        this.isTopt = topt;
        this.testGB = (gb ? true : false); // ??
        this.bb = bb;
        this.red = red;
        this.engine = SquarefreeFactory.<C> getImplementation(list.ring.coFac);
    }


    /**
     * Clone this.
     * @return a copy of this.
     */
    @Override
    public Ideal<C> clone() {
        return new Ideal<C>(list.clone(), isGB, isTopt, bb, red);
    }


    /**
     * Get the List of GenPolynomials.
     * @return list.list
     */
    public List<GenPolynomial<C>> getList() {
        return list.list;
    }


    /**
     * Get the GenPolynomialRing.
     * @return list.ring
     */
    public GenPolynomialRing<C> getRing() {
        return list.ring;
    }


    /**
     * Get the zero ideal.
     * @return ideal(0)
     */
    public Ideal<C> getZERO() {
        List<GenPolynomial<C>> z = new ArrayList<GenPolynomial<C>>(0);
        PolynomialList<C> pl = new PolynomialList<C>(getRing(), z);
        return new Ideal<C>(pl, true, isTopt, bb, red);
    }


    /**
     * Get the one ideal.
     * @return ideal(1)
     */
    public Ideal<C> getONE() {
        List<GenPolynomial<C>> one = new ArrayList<GenPolynomial<C>>(1);
        one.add(list.ring.getONE());
        PolynomialList<C> pl = new PolynomialList<C>(getRing(), one);
        return new Ideal<C>(pl, true, isTopt, bb, red);
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return list.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        return list.toScript();
    }


    /**
     * Comparison with any other object. Note: If both ideals are not Groebner
     * Bases, then false may be returned even the ideals are equal.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof Ideal)) {
            logger.warn("equals no Ideal");
            return false;
        }
        Ideal<C> B = null;
        try {
            B = (Ideal<C>) b;
        } catch (ClassCastException ignored) {
            return false;
        }
        //if ( isGB && B.isGB ) {
        //   return list.equals( B.list ); requires also monic polys
        //} else { // compute GBs ?
        return this.contains(B) && B.contains(this);
        //}
    }


    /**
     * Ideal list comparison.
     * @param L other Ideal.
     * @return compareTo() of polynomial lists.
     */
    public int compareTo(Ideal<C> L) {
        return list.compareTo(L.list);
    }


    /**
     * Hash code for this ideal.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = list.hashCode();
        if (isGB) {
            h = h << 1;
        }
        if (testGB) {
            h += 1;
        }
        return h;
    }


    /**
     * Test if ZERO ideal.
     * @return true, if this is the 0 ideal, else false
     */
    public boolean isZERO() {
        return list.isZERO();
    }


    /**
     * Test if ONE is contained in the ideal.
     * To test for a proper ideal use <code>! id.isONE()</code>.
     * @return true, if this is the 1 ideal, else false
     */
    public boolean isONE() {
        return list.isONE();
    }


    /**
     * Optimize the term order.
     */
    public void doToptimize() {
        if (isTopt) {
            return;
        }
        list = TermOrderOptimization.<C> optimizeTermOrder(list);
        isTopt = true;
        if (isGB) {
            isGB = false;
            doGB();
        }
        return;
    }


    /**
     * Test if this is a Groebner base.
     * @return true, if this is a Groebner base, else false
     */
    public boolean isGB() {
        if (testGB) {
            return isGB;
        }
        logger.warn("isGB computing");
        isGB = bb.isGB(getList());
        testGB = true;
        return isGB;
    }


    /**
     * Do Groebner Base. compute the Groebner Base for this ideal.
     */
    public void doGB() {
        if (isGB && testGB) {
            return;
        }
        //logger.warn("GB computing");
        List<GenPolynomial<C>> G = getList();
        logger.info("GB computing = " + G);
        G = bb.GB(G);
        if (isTopt) {
            List<Integer> perm = ((OptimizedPolynomialList<C>) list).perm;
            list = new OptimizedPolynomialList<C>(perm, getRing(), G);
        } else {
            list = new PolynomialList<C>(getRing(), G);
        }
        isGB = true;
        testGB = true;
        return;
    }


    /**
     * Groebner Base. Get a Groebner Base for this ideal.
     * @return GB(this)
     */
    public Ideal<C> GB() {
        if (isGB) {
            return this;
        }
        doGB();
        return this;
    }


    /**
     * Ideal containment. Test if B is contained in this ideal. Note: this is
     * eventually modified to become a Groebner Base.
     * @param B ideal
     * @return true, if B is contained in this, else false
     */
    public boolean contains(Ideal<C> B) {
        if (B == null || B.isZERO()) {
            return true;
        }
        return contains(B.getList());
    }


    /**
     * Ideal containment. Test if b is contained in this ideal. Note: this is
     * eventually modified to become a Groebner Base.
     * @param b polynomial
     * @return true, if b is contained in this, else false
     */
    public boolean contains(GenPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return true;
        }
        if (this.isONE()) {
            return true;
        }
        if (this.isZERO()) {
            return false;
        }
        if (!isGB) {
            doGB();
        }
        GenPolynomial<C> z;
        z = red.normalform(getList(), b);
        if (z == null || z.isZERO()) {
            return true;
        }
        return false;
    }


    /**
     * Ideal containment. Test if each b in B is contained in this ideal. Note:
     * this is eventually modified to become a Groebner Base.
     * @param B list of polynomials
     * @return true, if each b in B is contained in this, else false
     */
    public boolean contains(List<GenPolynomial<C>> B) {
        if (B == null || B.size() == 0) {
            return true;
        }
        if (this.isONE()) {
            return true;
        }
        if (!isGB) {
            doGB();
        }
        for (GenPolynomial<C> b : B) {
            if (b == null) {
                continue;
            }
            GenPolynomial<C> z = red.normalform(getList(), b);
            if (!z.isZERO()) {
                //System.out.println("contains nf(b) != 0: " + b);
                return false;
            }
        }
        return true;
    }


    /**
     * Summation. Generators for the sum of ideals. Note: if both ideals are
     * Groebner bases, a Groebner base is returned.
     * @param B ideal
     * @return ideal(this+B)
     */
    public Ideal<C> sum(Ideal<C> B) {
        if (B == null || B.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return B;
        }
        int s = getList().size() + B.getList().size();
        List<GenPolynomial<C>> c;
        c = new ArrayList<GenPolynomial<C>>(s);
        c.addAll(getList());
        c.addAll(B.getList());
        Ideal<C> I = new Ideal<C>(getRing(), c, false);
        if (isGB && B.isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Summation. Generators for the sum of ideal and a polynomial. Note: if
     * this ideal is a Groebner base, a Groebner base is returned.
     * @param b polynomial
     * @return ideal(this+{b})
     */
    public Ideal<C> sum(GenPolynomial<C> b) {
        if (b == null || b.isZERO()) {
            return this;
        }
        int s = getList().size() + 1;
        List<GenPolynomial<C>> c;
        c = new ArrayList<GenPolynomial<C>>(s);
        c.addAll(getList());
        c.add(b);
        Ideal<C> I = new Ideal<C>(getRing(), c, false);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Summation. Generators for the sum of this ideal and a list of
     * polynomials. Note: if this ideal is a Groebner base, a Groebner base is
     * returned.
     * @param L list of polynomials
     * @return ideal(this+L)
     */
    public Ideal<C> sum(List<GenPolynomial<C>> L) {
        if (L == null || L.isEmpty()) {
            return this;
        }
        int s = getList().size() + L.size();
        List<GenPolynomial<C>> c = new ArrayList<GenPolynomial<C>>(s);
        c.addAll(getList());
        c.addAll(L);
        Ideal<C> I = new Ideal<C>(getRing(), c, false);
        if (isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Product. Generators for the product of ideals. Note: if both ideals are
     * Groebner bases, a Groebner base is returned.
     * @param B ideal
     * @return ideal(this*B)
     */
    public Ideal<C> product(Ideal<C> B) {
        if (B == null || B.isZERO()) {
            return B;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = getList().size() * B.getList().size();
        List<GenPolynomial<C>> c;
        c = new ArrayList<GenPolynomial<C>>(s);
        for (GenPolynomial<C> p : getList()) {
            for (GenPolynomial<C> q : B.getList()) {
                q = p.multiply(q);
                c.add(q);
            }
        }
        Ideal<C> I = new Ideal<C>(getRing(), c, false);
        if (isGB && B.isGB) {
            I.doGB();
        }
        return I;
    }


    /**
     * Intersection. Generators for the intersection of ideals.
     * Using an iterative algorithm.
     * @param Bl list of ideals
     * @return ideal(cap_i B_i), a Groebner base
     */
    public Ideal<C> intersect(List<Ideal<C>> Bl) {
        if (Bl == null || Bl.size() == 0) {
            return getZERO();
        }
        Ideal<C> I = null;
        for (Ideal<C> B : Bl) {
            if (I == null) {
                I = B;
                continue;
            }
            if (I.isONE()) {
                return I;
            }
            I = I.intersect(B);
        }
        return I;
    }


    /**
     * Intersection. Generators for the intersection of ideals.
     * @param B ideal
     * @return ideal(this \cap B), a Groebner base
     */
    public Ideal<C> intersect(Ideal<C> B) {
        if (B == null || B.isZERO()) { // (0)
            return B;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = getList().size() + B.getList().size();
        List<GenPolynomial<C>> c;
        c = new ArrayList<GenPolynomial<C>>(s);
        List<GenPolynomial<C>> a = getList();
        List<GenPolynomial<C>> b = B.getList();

        GenPolynomialRing<C> tfac = getRing().extend(1);
        // term order is also adjusted
        for (GenPolynomial<C> p : a) {
            p = p.extend(tfac, 0, 1L); // t*p
            c.add(p);
        }
        for (GenPolynomial<C> p : b) {
            GenPolynomial<C> q = p.extend(tfac, 0, 1L);
            GenPolynomial<C> r = p.extend(tfac, 0, 0L);
            p = r.subtract(q); // (1-t)*p
            c.add(p);
        }
        logger.warn("intersect computing GB");
        List<GenPolynomial<C>> g = bb.GB(c);
        if (debug) {
            logger.debug("intersect GB = " + g);
        }
        Ideal<C> E = new Ideal<C>(tfac, g, true);
        Ideal<C> I = E.intersect(getRing());
        return I;
    }


    /**
     * Intersection. Generators for the intersection of a ideal with a
     * polynomial ring. The polynomial ring of this ideal must be a contraction
     * of R and the TermOrder must be an elimination order.
     * @param R polynomial ring
     * @return ideal(this \cap R)
     */
    public Ideal<C> intersect(GenPolynomialRing<C> R) {
        if (R == null) {
            throw new IllegalArgumentException("R may not be null");
        }
        int d = getRing().nvar - R.nvar;
        if (d <= 0) {
            return this;
        }
        List<GenPolynomial<C>> H = new ArrayList<GenPolynomial<C>>(getList().size());
        for (GenPolynomial<C> p : getList()) {
            Map<ExpVector, GenPolynomial<C>> m = null;
            m = p.contract(R);
            if (debug) {
                logger.debug("intersect contract m = " + m);
            }
            if (m.size() == 1) { // contains one power of variables
                for (ExpVector e : m.keySet()) {
                    if (e.isZERO()) {
                        H.add(m.get(e));
                    }
                }
            }
        }
        GenPolynomialRing<C> tfac = getRing().contract(d);
        if (tfac.equals(R)) { // check 
            return new Ideal<C>(R, H, isGB, isTopt);
        }
        logger.info("tfac, R = " + tfac + ", " + R);
        // throw new RuntimeException("contract(this) != R");
        return new Ideal<C>(R, H); // compute GB
    }


    /**
     * Eliminate. Generators for the intersection of a ideal with a polynomial
     * ring. The polynomial rings must have variable names.
     * @param R polynomial ring
     * @return ideal(this \cap R)
     */
    public Ideal<C> eliminate(GenPolynomialRing<C> R) {
        if (R == null) {
            throw new IllegalArgumentException("R may not be null");
        }
        if (list.ring.equals(R)) {
            return this;
        }
        String[] ename = R.getVars();
        Ideal<C> I = eliminate(ename);
        return I.intersect(R);
    }


    /**
     * Eliminate. Preparation of generators for the intersection of a ideal with
     * a polynomial ring.
     * @param ename variables for the elimination ring.
     * @return ideal(this) in K[ename,{vars \ ename}])
     */
    public Ideal<C> eliminate(String[] ename) {
        //System.out.println("ename = " + Arrays.toString(ename));
        if (ename == null) {
            throw new IllegalArgumentException("ename may not be null");
        }
        String[] aname = getRing().getVars();
        //System.out.println("aname = " + Arrays.toString(aname));
        if (aname == null) {
            throw new IllegalArgumentException("aname may not be null");
        }

        GroebnerBasePartial<C> bbp = new GroebnerBasePartial<C>(bb, null);
        String[] rname = bbp.remainingVars(aname, ename);
        //System.out.println("rname = " + Arrays.toString(rname));
        PolynomialList<C> Pl = null;
        if (rname.length == 0) {
            if (Arrays.equals(aname, ename)) {
                return this;
            } else {
                Pl = bbp.partialGB(getList(), ename); // normal GB
            }
        } else {
            Pl = bbp.elimPartialGB(getList(), rname, ename); // reversed!
        }
        //System.out.println("Pl = " + Pl);
        if (debug) {
            logger.debug("elimination GB = " + Pl);
        }
        Ideal<C> I = new Ideal<C>(Pl, true);
        return I;
    }


    /**
     * Quotient. Generators for the ideal quotient.
     * @param h polynomial
     * @return ideal(this : h), a Groebner base
     */
    public Ideal<C> quotient(GenPolynomial<C> h) {
        if (h == null) { // == (0)
            return this;
        }
        if (h.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        List<GenPolynomial<C>> H;
        H = new ArrayList<GenPolynomial<C>>(1);
        H.add(h);
        Ideal<C> Hi = new Ideal<C>(getRing(), H, true);

        Ideal<C> I = this.intersect(Hi);

        List<GenPolynomial<C>> Q;
        Q = new ArrayList<GenPolynomial<C>>(I.getList().size());
        for (GenPolynomial<C> q : I.getList()) {
            q = q.divide(h); // remainder == 0
            Q.add(q);
        }
        return new Ideal<C>(getRing(), Q, true /*false?*/);
    }


    /**
     * Quotient. Generators for the ideal quotient.
     * @param H ideal
     * @return ideal(this : H), a Groebner base
     */
    public Ideal<C> quotient(Ideal<C> H) {
        if (H == null) { // == (0)
            return this;
        }
        if (H.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        Ideal<C> Q = null;
        for (GenPolynomial<C> h : H.getList()) {
            Ideal<C> Hi = this.quotient(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }


    /**
     * Infinite quotient. Generators for the infinite ideal quotient.
     * @param h polynomial
     * @return ideal(this : h<sup>s</sup>), a Groebner base
     */
    public Ideal<C> infiniteQuotientRab(GenPolynomial<C> h) {
        if (h == null || h.isZERO()) { // == (0)
            return getONE();
        }
        if (h.isONE()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        Ideal<C> I = this.GB(); // should be already
        List<GenPolynomial<C>> a = I.getList();
        List<GenPolynomial<C>> c;
        c = new ArrayList<GenPolynomial<C>>(a.size() + 1);

        GenPolynomialRing<C> tfac = getRing().extend(1);
        // term order is also adjusted
        for (GenPolynomial<C> p : a) {
            p = p.extend(tfac, 0, 0L); // p
            c.add(p);
        }
        GenPolynomial<C> q = h.extend(tfac, 0, 1L);
        GenPolynomial<C> r = tfac.getONE(); // h.extend( tfac, 0, 0L );
        GenPolynomial<C> hs = q.subtract(r); // 1 - t*h // (1-t)*h
        c.add(hs);
        logger.warn("infiniteQuotientRab computing GB ");
        List<GenPolynomial<C>> g = bb.GB(c);
        if (debug) {
            logger.info("infiniteQuotientRab    = " + tfac + ", c = " + c);
            logger.info("infiniteQuotientRab GB = " + g);
        }
        Ideal<C> E = new Ideal<C>(tfac, g, true);
        Ideal<C> Is = E.intersect(getRing());
        return Is;
    }


    /**
     * Infinite quotient exponent.
     * @param h polynomial
     * @param Q quotient this : h^\infinity
     * @return s with Q = this : h<sup>s</sup>
     */
    public int infiniteQuotientExponent(GenPolynomial<C> h, Ideal<C> Q) {
        int s = 0;
        if (h == null) { // == 0
            return s;
        }
        if (h.isZERO() || h.isONE()) {
            return s;
        }
        if (this.isZERO() || this.isONE()) {
            return s;
        }
        //see below: if (this.contains(Q)) {
        //    return s;
        //}
        GenPolynomial<C> p = getRing().getONE();
        for (GenPolynomial<C> q : Q.getList()) {
            if (this.contains(q)) {
                continue;
            }
            //System.out.println("q = " + q + ", p = " + p + ", s = " + s);
            GenPolynomial<C> qp = q.multiply(p);
            while (!this.contains(qp)) {
                p = p.multiply(h);
                s++;
                qp = q.multiply(p);
            }
        }
        return s;
    }


    /**
     * Infinite quotient. Generators for the infinite ideal quotient.
     * @param h polynomial
     * @return ideal(this : h<sup>s</sup>), a Groebner base
     */
    public Ideal<C> infiniteQuotient(GenPolynomial<C> h) {
        if (h == null) { // == (0)
            return this;
        }
        if (h.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = 0;
        Ideal<C> I = this.GB(); // should be already
        GenPolynomial<C> hs = h;
        Ideal<C> Is = I;

        boolean eq = false;
        while (!eq) {
            Is = I.quotient(hs);
            Is = Is.GB(); // should be already
            logger.info("infiniteQuotient s = " + s);
            eq = Is.contains(I); // I.contains(Is) always
            if (!eq) {
                I = Is;
                s++;
                // hs = hs.multiply( h );
            }
        }
        return Is;
    }


    /**
     * Radical membership test.
     * @param h polynomial
     * @return true if h is contained in the radical of ideal(this), else false.
     */
    public boolean isRadicalMember(GenPolynomial<C> h) {
        if (h == null) { // == (0)
            return true;
        }
        if (h.isZERO()) {
            return true;
        }
        if (this.isZERO()) {
            return true;
        }
        Ideal<C> x = infiniteQuotientRab(h);
        if (debug) {
            logger.debug("infiniteQuotientRab = " + x);
        }
        return x.isONE();
    }


    /**
     * Infinite quotient. Generators for the infinite ideal quotient.
     * @param h polynomial
     * @return ideal(this : h<sup>s</sup>), a Groebner base
     */
    public Ideal<C> infiniteQuotientOld(GenPolynomial<C> h) {
        if (h == null) { // == (0)
            return this;
        }
        if (h.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        int s = 0;
        Ideal<C> I = this.GB(); // should be already
        GenPolynomial<C> hs = h;

        boolean eq = false;
        while (!eq) {
            Ideal<C> Is = I.quotient(hs);
            Is = Is.GB(); // should be already
            logger.debug("infiniteQuotient s = " + s);
            eq = Is.contains(I); // I.contains(Is) always
            if (!eq) {
                I = Is;
                s++;
                hs = hs.multiply(h);
            }
        }
        return I;
    }


    /**
     * Infinite Quotient. Generators for the ideal infinite quotient.
     * @param H ideal
     * @return ideal(this : H<sup>s</sup>), a Groebner base
     */
    public Ideal<C> infiniteQuotient(Ideal<C> H) {
        if (H == null) { // == (0)
            return this;
        }
        if (H.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        Ideal<C> Q = null;
        for (GenPolynomial<C> h : H.getList()) {
            Ideal<C> Hi = this.infiniteQuotient(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }


    /**
     * Infinite Quotient. Generators for the ideal infinite quotient.
     * @param H ideal
     * @return ideal(this : H<sup>s</sup>), a Groebner base
     */
    public Ideal<C> infiniteQuotientRab(Ideal<C> H) {
        if (H == null) { // == (0)
            return this;
        }
        if (H.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return this;
        }
        Ideal<C> Q = null;
        for (GenPolynomial<C> h : H.getList()) {
            Ideal<C> Hi = this.infiniteQuotientRab(h);
            if (Q == null) {
                Q = Hi;
            } else {
                Q = Q.intersect(Hi);
            }
        }
        return Q;
    }


    /**
     * Normalform for element.
     * @param h polynomial
     * @return normalform of h with respect to this
     */
    public GenPolynomial<C> normalform(GenPolynomial<C> h) {
        if (h == null) {
            return h;
        }
        if (h.isZERO()) {
            return h;
        }
        if (this.isZERO()) {
            return h;
        }
        GenPolynomial<C> r;
        r = red.normalform(list.list, h);
        return r;
    }


    /**
     * Normalform for list of elements.
     * @param L polynomial list
     * @return list of normalforms of the elements of L with respect to this
     */
    public List<GenPolynomial<C>> normalform(List<GenPolynomial<C>> L) {
        if (L == null) {
            return L;
        }
        if (L.size() == 0) {
            return L;
        }
        if (this.isZERO()) {
            return L;
        }
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>(L.size());
        for (GenPolynomial<C> h : L) {
            GenPolynomial<C> r = normalform(h);
            if (r != null && !r.isZERO()) {
                M.add(r);
            }
        }
        return M;
    }


    /**
     * Inverse for element modulo this ideal.
     * @param h polynomial
     * @return inverse of h with respect to this, if defined
     */
    public GenPolynomial<C> inverse(GenPolynomial<C> h) {
        if (h == null || h.isZERO()) {
            throw new RuntimeException(" zero not invertible");
        }
        if (this.isZERO()) {
            throw new NotInvertibleException(" zero ideal");
        }
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(1 + list.list.size());
        F.add(h);
        F.addAll(list.list);
        //System.out.println("F = " + F);
        ExtendedGB<C> x = bb.extGB(F);
        List<GenPolynomial<C>> G = x.G;
        //System.out.println("G = " + G);
        GenPolynomial<C> one = null;
        int i = -1;
        for (GenPolynomial<C> p : G) {
            i++;
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                one = p;
                break;
            }
        }
        if (one == null) {
            throw new NotInvertibleException(" h = " + h);
        }
        List<GenPolynomial<C>> row = x.G2F.get(i); // != -1
        GenPolynomial<C> g = row.get(0);
        if (g == null || g.isZERO()) {
            throw new NotInvertibleException(" h = " + h);
        }
        // adjust g to get g*h == 1
        GenPolynomial<C> f = g.multiply(h);
        GenPolynomial<C> k = red.normalform(list.list, f);
        if (!k.isONE()) {
            C lbc = k.leadingBaseCoefficient();
            lbc = lbc.inverse();
            g = g.multiply(lbc);
        }
        if (debug) {
            //logger.info("inv G = " + G);
            //logger.info("inv G2F = " + x.G2F);
            //logger.info("inv row "+i+" = " + row);
            //logger.info("inv h = " + h);
            //logger.info("inv g = " + g);
            //logger.info("inv f = " + f);
            f = g.multiply(h);
            k = red.normalform(list.list, f);
            logger.info("inv k = " + k);
            if (!k.isUnit()) {
                throw new NotInvertibleException(" k = " + k);
            }
        }
        return g;
    }


    /**
     * Test if element is a unit modulo this ideal.
     * @param h polynomial
     * @return true if h is a unit with respect to this, else false
     */
    public boolean isUnit(GenPolynomial<C> h) {
        if (h == null || h.isZERO()) {
            return false;
        }
        if (this.isZERO()) {
            return false;
        }
        List<GenPolynomial<C>> F = new ArrayList<GenPolynomial<C>>(1 + list.list.size());
        F.add(h);
        F.addAll(list.list);
        List<GenPolynomial<C>> G = bb.GB(F);
        for (GenPolynomial<C> p : G) {
            if (p == null) {
                continue;
            }
            if (p.isUnit()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Radical approximation. Squarefree generators for the ideal.
     * @return squarefree(this), a Groebner base
     */
    public Ideal<C> squarefree() {
        if (this.isZERO()) {
            return this;
        }
        Ideal<C> R = this;
        Ideal<C> Rp = null;
        List<GenPolynomial<C>> li, ri;
        while (true) {
            li = R.getList();
            ri = new ArrayList<GenPolynomial<C>>(li); //.size() );
            for (GenPolynomial<C> h : li) {
                GenPolynomial<C> r = engine.squarefreePart(h);
                ri.add(r);
            }
            Rp = new Ideal<C>(R.getRing(), ri, false);
            Rp.doGB();
            if (R.equals(Rp)) {
                break;
            }
            R = Rp;
        }
        return R;
    }


    /**
     * Ideal common zero test.
     * @return -1, 0 or 1 if dimension(this) &eq; -1, 0 or &ge; 1.
     */
    public int commonZeroTest() {
        if (this.isZERO()) {
            return 1;
        }
        if (!isGB) {
            doGB();
        }
        if (this.isONE()) {
            return -1;
        }
        if (this.list.ring.nvar <= 0) {
            return -1;
        }
        //int uht = 0;
        Set<Integer> v = new HashSet<Integer>(); // for non reduced GBs
        // List<GenPolynomial<C>> Z = this.list.list;
        for (GenPolynomial<C> p : getList()) {
            ExpVector e = p.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] u = e.dependencyOnVariables();
            if (u == null) {
                continue;
            }
            if (u.length == 1) {
                //uht++;
                v.add(u[0]);
            }
        }
        if (this.list.ring.nvar == v.size()) {
            return 0;
        }
        return 1;
    }


    /**
     * Univariate head term degrees.
     * @return a list of the degrees of univariate head terms.
     */
    public List<Long> univariateDegrees() {
        List<Long> ud = new ArrayList<Long>();
        if (this.isZERO()) {
            return ud;
        }
        if (!isGB) {
            doGB();
        }
        if (this.isONE()) {
            return ud;
        }
        if (this.list.ring.nvar <= 0) {
            return ud;
        }
        //int uht = 0;
        Map<Integer, Long> v = new TreeMap<Integer, Long>(); // for non reduced GBs
        for (GenPolynomial<C> p : getList()) {
            ExpVector e = p.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] u = e.dependencyOnVariables();
            if (u == null) {
                continue;
            }
            if (u.length == 1) {
                //uht++;
                Long d = v.get(u[0]);
                if (d == null) {
                    v.put(u[0], e.getVal(u[0]));
                }
            }
        }
        for (int i = 0; i < this.list.ring.nvar; i++) {
            Long d = v.get(i);
            ud.add(d);
        }
        //Collections.reverse(ud);
        return ud;
    }


    /**
     * Ideal dimension.
     * @return a dimension container (dim,maxIndep,list(maxIndep),vars).
     */
    public Dimension dimension() {
        int t = commonZeroTest();
        Set<Integer> S = new HashSet<Integer>();
        Set<Set<Integer>> M = new HashSet<Set<Integer>>();
        if (t <= 0) {
            return new Dimension(t, S, M, this.list.ring.getVars());
        }
        int d = 0;
        Set<Integer> U = new HashSet<Integer>();
        for (int i = 0; i < this.list.ring.nvar; i++) {
            U.add(i);
        }
        M = dimension(S, U, M);
        for (Set<Integer> m : M) {
            int dp = m.size();
            if (dp > d) {
                d = dp;
                S = m;
            }
        }
        return new Dimension(d, S, M, this.list.ring.getVars());
    }


    /**
     * Ideal dimension.
     * @param S is a set of independent variables.
     * @param U is a set of variables of unknown status.
     * @param M is a list of maximal sets of independent variables.
     * @return a list of maximal sets of independent variables, eventually
     *         containing S.
     */
    protected Set<Set<Integer>> dimension(Set<Integer> S, Set<Integer> U, Set<Set<Integer>> M) {
        Set<Set<Integer>> MP = M;
        Set<Integer> UP = new HashSet<Integer>(U);
        for (Integer j : U) {
            UP.remove(j);
            Set<Integer> SP = new HashSet<Integer>(S);
            SP.add(j);
            if (!containsHT(SP, getList())) {
                MP = dimension(SP, UP, MP);
            }
        }
        boolean contained = false;
        for (Set<Integer> m : MP) {
            if (m.containsAll(S)) {
                contained = true;
                break;
            }
        }
        if (!contained) {
            MP.add(S);
        }
        return MP;
    }


    /**
     * Ideal head term containment test.
     * @param G list of polynomials.
     * @param H index set.
     * @return true, if the vaiables of the head terms of each polynomial in G
     *         are contained in H, else false.
     */
    protected boolean containsHT(Set<Integer> H, List<GenPolynomial<C>> G) {
        Set<Integer> S = null;
        for (GenPolynomial<C> p : G) {
            if (p == null) {
                continue;
            }
            ExpVector e = p.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] v = e.dependencyOnVariables();
            if (v == null) {
                continue;
            }
            //System.out.println("v = " + Arrays.toString(v));
            if (S == null) { // revert indices
                S = new HashSet<Integer>(H.size());
                int r = e.length() - 1;
                for (Integer i : H) {
                    S.add(r - i);
                }
            }
            if (contains(v, S)) { // v \subset S
                return true;
            }
        }
        return false;
    }


    /**
     * Set containment. is v \subset H.
     * @param v index array.
     * @param H index set.
     * @return true, if each element of v is contained in H, else false .
     */
    protected boolean contains(int[] v, Set<Integer> H) {
        for (int i = 0; i < v.length; i++) {
            if (!H.contains(v[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Construct univariate polynomials of minimal degree in all variables in
     * zero dimensional ideal(G).
     * @return list of univariate polynomial of minimal degree in each variable
     *         in ideal(G)
     */
    public List<GenPolynomial<C>> constructUnivariate() {
        List<GenPolynomial<C>> univs = new ArrayList<GenPolynomial<C>>();
        for (int i = list.ring.nvar - 1; i >= 0; i--) {
            GenPolynomial<C> u = constructUnivariate(i);
            univs.add(u);
        }
        return univs;
    }


    /**
     * Construct univariate polynomial of minimal degree in variable i in zero
     * dimensional ideal(G).
     * @param i variable index.
     * @return univariate polynomial of minimal degree in variable i in ideal(G)
     */
    public GenPolynomial<C> constructUnivariate(int i) {
        return constructUnivariate(i, getList());
    }


    /**
     * Construct univariate polynomial of minimal degree in variable i of a zero
     * dimensional ideal(G).
     * @param i variable index.
     * @param G list of polynomials, a monic reduced Gr&ouml;bner base of a zero
     *            dimensional ideal.
     * @return univariate polynomial of minimal degree in variable i in ideal(G)
     */
    public GenPolynomial<C> constructUnivariate(int i, List<GenPolynomial<C>> G) {
        if (G == null || G.size() == 0) {
            throw new IllegalArgumentException("G may not be null or empty");
        }
        List<Long> ud = univariateDegrees();
	if ( ud == null || ud.size() <= i ) {
            //logger.info("univ pol, ud = " + ud);
            throw new IllegalArgumentException("ideal(G) not zero dimensional " + ud);
	}
        int ll = 0;
        Long di = ud.get(i);
        if (di != null) {
            ll = (int) (long) di;
        } else {
            throw new IllegalArgumentException("ideal(G) not zero dimensional");
        }
        GenPolynomialRing<C> pfac = G.get(0).ring;
        RingFactory<C> cfac = pfac.coFac;
        String var = pfac.getVars()[pfac.nvar - 1 - i];
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(cfac, 1, new TermOrder(TermOrder.INVLEX),
                new String[] { var });
        GenPolynomial<C> pol = ufac.getZERO();

        GenPolynomialRing<C> cpfac = new GenPolynomialRing<C>(cfac, ll, new TermOrder(TermOrder.INVLEX));
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cpfac, pfac);
        GenPolynomial<GenPolynomial<C>> P = rfac.getZERO();
        for (int k = 0; k < ll; k++) {
            GenPolynomial<GenPolynomial<C>> Pp = rfac.univariate(i, k);
            GenPolynomial<C> cp = cpfac.univariate(cpfac.nvar - 1 - k);
            Pp = Pp.multiply(cp);
            P = P.sum(Pp);
        }
        GenPolynomial<C> X;
        GenPolynomial<C> XP;
        // solve system of linear equations for the coefficients of the univariate polynomial
        List<GenPolynomial<C>> ls;
        int z = -1;
        do {
            //System.out.println("ll  = " + ll);
            GenPolynomial<GenPolynomial<C>> Pp = rfac.univariate(i, ll);
            GenPolynomial<C> cp = cpfac.univariate(cpfac.nvar - 1 - ll);
            Pp = Pp.multiply(cp);
            P = P.sum(Pp);
            X = pfac.univariate(i, ll);
            XP = red.normalform(G, X);
            GenPolynomial<GenPolynomial<C>> XPp = PolyUtil.<C> toRecursive(rfac, XP);
            GenPolynomial<GenPolynomial<C>> XPs = XPp.sum(P);
            ls = new ArrayList<GenPolynomial<C>>(XPs.getMap().values());
            ls = red.irreducibleSet(ls);
            //System.out.println("ls = " + ls);
            Ideal<C> L = new Ideal<C>(cpfac, ls, true);
            z = L.commonZeroTest();
            if (z != 0) {
                ll++;
                cpfac = cpfac.extend(1);
                rfac = new GenPolynomialRing<GenPolynomial<C>>(cpfac, pfac);
                P = PolyUtil.<C> extendCoefficients(rfac, P, 0, 0L);
                XPp = PolyUtil.<C> extendCoefficients(rfac, XPp, 0, 1L);
                P = P.sum(XPp);
            }
        } while (z != 0); // && ll <= 5 && !XP.isZERO()
        // construct result polynomial
        pol = ufac.univariate(0, ll);
        for (GenPolynomial<C> pc : ls) {
            ExpVector e = pc.leadingExpVector();
            if (e == null) {
                continue;
            }
            int[] v = e.dependencyOnVariables();
            if (v == null || v.length == 0) {
                continue;
            }
            int vi = v[0];
            C tc = pc.trailingBaseCoefficient();
            tc = tc.negate();
            GenPolynomial<C> pi = ufac.univariate(0, ll - 1 - vi);
            pi = pi.multiply(tc);
            pol = pol.sum(pi);
        }
        if (logger.isInfoEnabled()) {
            logger.info("univ pol = " + pol);
        }
        return pol;
    }


    /**
     * Zero dimensional radical decompostition. See Seidenbergs lemma 92, and
     * BWK lemma 8.13.
     * @return intersection of radical ideals G_i with ideal(this) subseteq
     *         cap_i( ideal(G_i) )
     */
    public List<IdealWithUniv<C>> zeroDimRadicalDecomposition() {
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        if (this.isZERO()) {
            return dec;
        }
        IdealWithUniv<C> iwu = new IdealWithUniv<C>(this, new ArrayList<GenPolynomial<C>>());
        dec.add(iwu);
        if (this.isONE()) {
            return dec;
        }
        for (int i = list.ring.nvar - 1; i >= 0; i--) {
            List<IdealWithUniv<C>> part = new ArrayList<IdealWithUniv<C>>();
            for (IdealWithUniv<C> id : dec) {
                //System.out.println("id = " + id + ", i = " + i);
                GenPolynomial<C> u = id.ideal.constructUnivariate(i);
                SortedMap<GenPolynomial<C>, Long> facs = engine.baseSquarefreeFactors(u);
                if (facs.size() == 1 && facs.get(facs.firstKey()) == 1L) {
                    List<GenPolynomial<C>> iup = new ArrayList<GenPolynomial<C>>();
                    iup.addAll(id.upolys);
                    iup.add(u);
                    IdealWithUniv<C> Ipu = new IdealWithUniv<C>(id.ideal, iup);
                    part.add(Ipu);
                    continue; // irreducible
                }
                if (logger.isInfoEnabled()) {
                    logger.info("squarefree facs = " + facs);
                }
                GenPolynomialRing<C> mfac = id.ideal.list.ring;
                int j = mfac.nvar - 1 - i;
                for (GenPolynomial<C> p : facs.keySet()) {
                    // make p multivariate
                    GenPolynomial<C> pm = p.extendUnivariate(mfac, j);
                    // mfac.parse( p.toString() );
                    //stem.out.println("pm = " + pm);
                    Ideal<C> Ip = id.ideal.sum(pm);
                    List<GenPolynomial<C>> iup = new ArrayList<GenPolynomial<C>>();
                    iup.addAll(id.upolys);
                    iup.add(p);
                    IdealWithUniv<C> Ipu = new IdealWithUniv<C>(Ip, iup);
                    if (debug) {
                        logger.info("ideal with squarefree facs = " + Ipu);
                    }
                    part.add(Ipu);
                }
            }
            dec = part;
            part = new ArrayList<IdealWithUniv<C>>();
        }
        return dec;
    }


    /**
     * Test for Zero dimensional radical. See Seidenbergs lemma 92, and BWK
     * lemma 8.13.
     * @return true if this is an zero dimensional radical ideal, else false
     */
    public boolean isZeroDimRadical() {
        if (this.isZERO()) {
            return false;
        }
        if (this.isONE()) {
            return false; // not 0-dim
        }
        for (int i = list.ring.nvar - 1; i >= 0; i--) {
            GenPolynomial<C> u = constructUnivariate(i);
            boolean t = engine.isSquarefree(u);
            if (!t) {
                System.out.println("not squarefree " + engine.squarefreePart(u) + ", " + u);
                return false;
            }
        }
        return true;
    }


    /**
     * Zero dimensional ideal irreducible decompostition. 
     * See algorithm DIRGZD of BGK 1986 and also PREDEC of the Gr&ouml;bner bases book 1993.
     * @return intersection H, of ideals G_i with ideal(this) subseteq
     *         cap_i( ideal(G_i) ) and each ideal G_i has only irreducible minimal
     *         univariate polynomials and the G_i are pairwise co-prime.
     */
    public List<IdealWithUniv<C>> zeroDimDecomposition() {
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        if (this.isZERO()) {
            return dec;
        }
        IdealWithUniv<C> iwu = new IdealWithUniv<C>(this, new ArrayList<GenPolynomial<C>>());
        dec.add(iwu);
        if (this.isONE()) {
            return dec;
        }
        FactorAbstract<C> ufd = FactorFactory.<C> getImplementation(list.ring.coFac);
        for (int i = list.ring.nvar - 1; i >= 0; i--) {
            List<IdealWithUniv<C>> part = new ArrayList<IdealWithUniv<C>>();
            for (IdealWithUniv<C> id : dec) {
                GenPolynomial<C> u = id.ideal.constructUnivariate(i);
                SortedMap<GenPolynomial<C>, Long> facs = ufd.baseFactors(u);
                if (facs.size() == 1 && facs.get(facs.firstKey()) == 1L) {
                    List<GenPolynomial<C>> iup = new ArrayList<GenPolynomial<C>>();
                    iup.addAll(id.upolys);
                    iup.add(u);
                    IdealWithUniv<C> Ipu = new IdealWithUniv<C>(id.ideal, iup);
                    part.add(Ipu);
                    continue; // irreducible
                }
                if (debug) {
                    logger.info("irreducible facs = " + facs);
                }
                GenPolynomialRing<C> mfac = id.ideal.list.ring;
                int j = mfac.nvar - 1 - i;
                for (GenPolynomial<C> p : facs.keySet()) {
                    // make p multivariate
                    GenPolynomial<C> pm = p.extendUnivariate(mfac, j);
                    // mfac.parse( p.toString() );
                    //System.out.println("pm = " + pm);
                    Ideal<C> Ip = id.ideal.sum(pm);
                    List<GenPolynomial<C>> iup = new ArrayList<GenPolynomial<C>>();
                    iup.addAll(id.upolys);
                    iup.add(p);
                    IdealWithUniv<C> Ipu = new IdealWithUniv<C>(Ip, iup);
                    part.add(Ipu);
                }
            }
            dec = part;
            part = new ArrayList<IdealWithUniv<C>>();
        }
        return dec;
    }


    /**
     * Zero dimensional ideal irreducible decompostition extension.
     * One step decomposition via a minimal univariate polynomial in the lowest variable, 
     * used after each normalPosition step.
     * @param upol list of univariate polynomials
     * @param og list of other generators for the ideal
     * @return intersection of ideals G_i with ideal(this) subseteq cap_i(
     *         ideal(G_i) ) and all minimal univariate polynomials of all G_i are irreducible
     */
    public List<IdealWithUniv<C>> zeroDimDecompositionExtension(List<GenPolynomial<C>> upol,
            List<GenPolynomial<C>> og) {
        if (upol == null || upol.size() + 1 != list.ring.nvar) {
            throw new IllegalArgumentException("univariate polynomial list not correct " + upol);
        }
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        if (this.isZERO()) {
            return dec;
        }
        IdealWithUniv<C> iwu = new IdealWithUniv<C>(this, upol);
        if (this.isONE()) {
            dec.add(iwu);
            return dec;
        }
        FactorAbstract<C> ufd = FactorFactory.<C> getImplementation(list.ring.coFac);
        int i = list.ring.nvar - 1;
        //IdealWithUniv<C> id = new IdealWithUniv<C>(this,upol);
        GenPolynomial<C> u = this.constructUnivariate(i);
        SortedMap<GenPolynomial<C>, Long> facs = ufd.baseFactors(u);
        if (facs.size() == 1 && facs.get(facs.firstKey()) == 1L) {
            List<GenPolynomial<C>> iup = new ArrayList<GenPolynomial<C>>();
            iup.add(u); // new polynomial first
            iup.addAll(upol);
            IdealWithUniv<C> Ipu = new IdealWithUniv<C>(this, iup, og);
            dec.add(Ipu);
            return dec;
        }
        if (true) {
            logger.info("irreducible facs = " + facs);
        }
        GenPolynomialRing<C> mfac = list.ring;
        int j = mfac.nvar - 1 - i;
        for (GenPolynomial<C> p : facs.keySet()) {
            // make p multivariate
            GenPolynomial<C> pm = p.extendUnivariate(mfac, j);
            //System.out.println("pm = " + pm);
            Ideal<C> Ip = this.sum(pm);
            List<GenPolynomial<C>> iup = new ArrayList<GenPolynomial<C>>();
            iup.add(p); // new polynomial first
            iup.addAll(upol);
            IdealWithUniv<C> Ipu = new IdealWithUniv<C>(Ip, iup, og);
            dec.add(Ipu);
        }
        return dec;
    }


    /**
     * Test for zero dimensional ideal decompostition.
     * @param L intersection of ideals G_i with ideal(G) subseteq cap_i(
     *            ideal(G_i) ) and all minimal univariate polynomials of all G_i are irreducible
     * @return true if L is a zero dimensional irreducible decomposition of this, else false
     */
    public boolean isZeroDimDecomposition(List<IdealWithUniv<C>> L) {
        if (L == null || L.size() == 0) {
            if (this.isZERO()) {
                return true;
            } else {
                return false;
            }
        }
        // add lower variables if L contains ideals from field extensions
        GenPolynomialRing<C> ofac = list.ring;
        int r = ofac.nvar;
        int rp = L.get(0).ideal.list.ring.nvar;
        int d = rp - r;
        //System.out.println("d = " + d);
        Ideal<C> Id = this;
        if (d > 0) { 
            GenPolynomialRing<C> nfac = ofac.extendLower(d);
            //System.out.println("nfac = " + nfac);
            List<GenPolynomial<C>> elist = new ArrayList<GenPolynomial<C>>(list.list.size());
            for (GenPolynomial<C> p : getList()) {
                //System.out.println("p = " + p);
                GenPolynomial<C> q = p.extendLower(nfac, 0, 0L);
                //System.out.println("q = "  + q);
                elist.add(q);
            }
            Id = new Ideal<C>(nfac, elist, isGB, isTopt);
        }
        // test if this is contained in the intersection
        for (IdealWithUniv<C> I : L) {
            boolean t = I.ideal.contains(Id);
            if (!t) {
                System.out.println("not contained " + this + " in " + I.ideal);
                return false;
            }
        }
        // test if all univariate polynomials are contained in the respective ideal
        //List<GenPolynomial<C>> upprod = new ArrayList<GenPolynomial<C>>(rp);
        for (IdealWithUniv<C> I : L) {
            GenPolynomialRing<C> mfac = I.ideal.list.ring;
            int i = 0;
            for (GenPolynomial<C> p : I.upolys) {
                GenPolynomial<C> pm = p.extendUnivariate(mfac, i++);
                //System.out.println("pm = " + pm + ", p = " + p);
                boolean t = I.ideal.contains(pm);
                if (!t) {
                    System.out.println("not contained " + pm + " in " + I.ideal);
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Compute normal position for variables i and j.
     * @param i first variable index
     * @param j second variable index
     * @param og other generators for the ideal
     * @return this + (z - x_j - t x_i) in the ring C[z, x_1, ..., x_r]
     */
    public IdealWithUniv<C> normalPositionFor(int i, int j, List<GenPolynomial<C>> og) {
        // extend variables by one
        GenPolynomialRing<C> ofac = list.ring;
        if (ofac.tord.getEvord() != TermOrder.INVLEX) {
            throw new RuntimeException("invalid term order for normalPosition " + ofac.tord);
        }
        GenPolynomialRing<C> nfac = ofac.extendLower(1);
        List<GenPolynomial<C>> elist = new ArrayList<GenPolynomial<C>>(list.list.size() + 1);
        for (GenPolynomial<C> p : getList()) {
            GenPolynomial<C> q = p.extendLower(nfac, 0, 0L);
            //System.out.println("q = "  + q);
            elist.add(q);
        }
        List<GenPolynomial<C>> ogen = new ArrayList<GenPolynomial<C>>();
        if (og != null && og.size() > 0) {
            for (GenPolynomial<C> p : og) {
                GenPolynomial<C> q = p.extendLower(nfac, 0, 0L);
                //System.out.println("q = "  + q);
                ogen.add(q);
            }
        }
        Ideal<C> I = new Ideal<C>(nfac, elist, true);
        //System.out.println("I = "  + I);
        int ip = list.ring.nvar - 1 - i;
        int jp = list.ring.nvar - 1 - j;
        GenPolynomial<C> xi = nfac.univariate(ip);
        GenPolynomial<C> xj = nfac.univariate(jp);
        GenPolynomial<C> z = nfac.univariate(nfac.nvar - 1);
        // compute GBs until value of t is OK
        Ideal<C> Ip;
        GenPolynomial<C> zp;
        int t = 0;
        do {
            t--;
            // zp = z - ( xj - xi * t )
            zp = z.subtract(xj.subtract(xi.multiply(nfac.fromInteger(t))));
            zp = zp.monic();
            Ip = I.sum(zp);
            //System.out.println("Ip = " + Ip);
            if (-t % 5 == 0) {
                logger.info("normal position, t = " + t);
            }
        } while (!Ip.isNormalPositionFor(i + 1, j + 1));
        if (debug) {
            logger.info("normal position = " + Ip);
        }
        ogen.add(zp);
        IdealWithUniv<C> Ips = new IdealWithUniv<C>(Ip, null, ogen);
        return Ips;
    }


    /**
     * Test if this ideal is in normal position for variables i and j.
     * @param i first variable index
     * @param j second variable index
     * @return true if this is in normal position with respect to i and j
     */
    public boolean isNormalPositionFor(int i, int j) {
        // called in extended ring!
        int ip = list.ring.nvar - 1 - i;
        int jp = list.ring.nvar - 1 - j;
        boolean iOK = false;
        boolean jOK = false;
        for (GenPolynomial<C> p : getList()) {
            ExpVector e = p.leadingExpVector();
            int[] dov = e.dependencyOnVariables();
            //System.out.println("dov = " + Arrays.toString(dov));
            if (dov.length == 0) {
                throw new IllegalArgumentException("ideal dimension is not zero");
            }
            if (dov[0] == ip) {
                if (e.totalDeg() != 1) {
                    return false;
                } else {
                    iOK = true;
                }
            } else if (dov[0] == jp) {
                if (e.totalDeg() != 1) {
                    return false;
                } else {
                    jOK = true;
                }
            }
            if (iOK && jOK) {
                return true;
            }
        }
        return iOK && jOK;
    }


    /**
     * Normal position index, separate for polynomials with more than 2 variables. See also 
     * <a href="http://krum.rz.uni-mannheim.de/mas/src/masring/DIPDEC0.mi.html">mas.masring.DIPDEC0#DIGISR</a>
     * @return (i,j) for non-normal variables
     */
    public int[] normalPositionIndex2Vars() {
        int[] np = null;
        int i = -1;
        int j = -1;
        for (GenPolynomial<C> p : getList()) {
            ExpVector e = p.leadingExpVector();
            int[] dov = e.dependencyOnVariables();
            //System.out.println("dov_head = " + Arrays.toString(dov));
            if (dov.length == 0) {
                throw new IllegalArgumentException("ideal dimension is not zero " + p);
            }
            // search bi-variate head terms
            if (dov.length >= 2) {
                i = dov[0];
                j = dov[1];
                break;
            }
            int n = dov[0];
            GenPolynomial<C> q = p.reductum();
            e = q.degreeVector();
            dov = e.dependencyOnVariables();
            //System.out.println("dov_red  = " + Arrays.toString(dov));
            int k = Arrays.binarySearch(dov, n);
            int len = 2;
            if (k >= 0) {
                len = 3;
            }
            // search bi-variate reductas
            if (dov.length >= len) {
                switch (k) {
                case 0:
                    i = dov[1];
                    j = dov[2];
                    break;
                case 1:
                    i = dov[0];
                    j = dov[2];
                    break;
                case 2:
                    i = dov[0];
                    j = dov[1];
                    break;
                default:
                    i = dov[0];
                    j = dov[1];
                    break;
                }
                break;
            }
        }
        if (i < 0 || j < 0) {
            return np;
        }
        // adjust index
        i = list.ring.nvar - 1 - i;
        j = list.ring.nvar - 1 - j;
        np = new int[] { j, i }; // reverse
        logger.info("normalPositionIndex2Vars, np = " + Arrays.toString(np));
        return np;
    }


    /**
     * Normal position index, separate multiple univariate polynomials. See also
     * <a href="http://krum.rz.uni-mannheim.de/mas/src/masring/DIPDEC0.mi.html">mas.masring.DIPDEC0#DIGISM</a>
     * @return (i,j) for non-normal variables
     */
    public int[] normalPositionIndexUnivars() {
        int[] np = null; //new int[] { -1, -1 };
        int i = -1;
        int j = -1;
        // search multiple univariate polynomials with degree &gt;= 2
        for (GenPolynomial<C> p : getList()) {
            ExpVector e = p.degreeVector();
            int[] dov = e.dependencyOnVariables();
            long t = e.totalDeg(); // lt(p) would be enough
            //System.out.println("dov_univ = " + Arrays.toString(dov));
            if (dov.length == 0) {
                throw new IllegalArgumentException("ideal dimension is not zero");
            }
            if (dov.length == 1 && t >= 2L) {
                if (i == -1) {
                    i = dov[0];
                } else if (j == -1) {
                    j = dov[0];
                    if (i > j) {
                        int x = i;
                        i = j;
                        j = x;
                    }
                }
            }
            if (i >= 0 && j >= 0) {
                break;
            }
        }
        if (i < 0 || j < 0) {
            // search polynomials with univariate head term and degree &gt;= 2
            for (GenPolynomial<C> p : getList()) {
                ExpVector e = p.leadingExpVector();
                long t = e.totalDeg();
                if (t >= 2) {
                    e = p.degreeVector();
                    int[] dov = e.dependencyOnVariables();
                    //System.out.println("dov_univ2 = " + Arrays.toString(dov));
                    if (dov.length == 0) {
                        throw new IllegalArgumentException("ideal dimension is not zero");
                    }
                    if (dov.length >= 2) {
                        i = dov[0];
                        j = dov[1];
                    }
                }
                if (i >= 0 && j >= 0) {
                    break;
                }
            }
        }
        if (i < 0 || j < 0) {
            return np;
        }
        // adjust index
        i = list.ring.nvar - 1 - i;
        j = list.ring.nvar - 1 - j;
        np = new int[] { j, i }; // reverse
        logger.info("normalPositionIndexUnivars, np = " + Arrays.toString(np));
        return np;
    }


    /**
     * Zero dimensional ideal decompostition for real roots. See algorithm
     * mas.masring.DIPDEC0#DINTSR.
     * @return intersection of ideals G_i with ideal(this) subseteq
     *         cap_i( ideal(G_i) ) and each G_i contains at most bi-variate
     *         polynomials and all univariate minimal polynomials are
     *         irreducible
     */
    public List<IdealWithUniv<C>> zeroDimRootDecomposition() {
        List<IdealWithUniv<C>> dec = zeroDimDecomposition();
        if (this.isZERO()) {
            return dec;
        }
        if (this.isONE()) {
            return dec;
        }
        List<IdealWithUniv<C>> rdec = new ArrayList<IdealWithUniv<C>>();
        while (dec.size() > 0) {
            IdealWithUniv<C> id = dec.remove(0);
            int[] ri = id.ideal.normalPositionIndex2Vars();
            if (ri == null || ri.length != 2) {
                rdec.add(id);
            } else {
                IdealWithUniv<C> I = id.ideal.normalPositionFor(ri[0], ri[1], id.others);
                List<IdealWithUniv<C>> rd = I.ideal.zeroDimDecompositionExtension(id.upolys, I.others);
                //System.out.println("r_rd = " + rd);
                dec.addAll(rd);
            }
        }
        return rdec;
    }


    /**
     * Zero dimensional ideal prime decompostition. See algorithm
     * mas.masring.DIPDEC0#DINTSS.
     * @return intersection of ideals G_i with ideal(this) subseteq cap_i(
     *         ideal(G_i) ) and each G_i is a prime ideal
     */
    public List<IdealWithUniv<C>> zeroDimPrimeDecomposition() {
        List<IdealWithUniv<C>> pdec = zeroDimPrimeDecompositionFE();
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        for (IdealWithUniv<C> Ip : pdec) {
            if (Ip.ideal.getRing().nvar == getRing().nvar) { // no field extension
                dec.add(Ip);
            } else { // remove field extension
                // add other generators for performance
                Ideal<C> Id = Ip.ideal;
                if (Ip.others != null) {
                    //System.out.println("adding Ip.others = " + Ip.others);
                    List<GenPolynomial<C>> pp = new ArrayList<GenPolynomial<C>>();
                    pp.addAll(Id.getList());
                    pp.addAll(Ip.others);
                    Id = new Ideal<C>(Id.getRing(), pp);
                }
                Ideal<C> Is = Id.eliminate(getRing());
                //System.out.println("Is = " + Is);
                int s = Ip.upolys.size() - getRing().nvar; // skip field ext univariate polys
                List<GenPolynomial<C>> upol = Ip.upolys.subList(s, Ip.upolys.size());
                IdealWithUniv<C> Iu = new IdealWithUniv<C>(Is, upol);
                //,Ip.others); used above and must be ignored here 
                dec.add(Iu);
            }
        }
        return dec;
    }


    /**
     * Zero dimensional ideal prime decompostition, with field extension. See
     * algorithm mas.masring.DIPDEC0#DINTSS.
     * @return intersection of ideals G_i with ideal(this) subseteq cap_i(
     *         ideal(G_i) ) and each G_i is a prime ideal with eventually
     *         containing field extension variables
     */
    public List<IdealWithUniv<C>> zeroDimPrimeDecompositionFE() {
        List<IdealWithUniv<C>> dec = zeroDimRootDecomposition();
        if (this.isZERO()) {
            return dec;
        }
        if (this.isONE()) {
            return dec;
        }
        List<IdealWithUniv<C>> rdec = new ArrayList<IdealWithUniv<C>>();
        while (dec.size() > 0) {
            IdealWithUniv<C> id = dec.remove(0);
            int[] ri = id.ideal.normalPositionIndexUnivars();
            if (ri == null || ri.length != 2) {
                rdec.add(id);
            } else {
                IdealWithUniv<C> I = id.ideal.normalPositionFor(ri[0], ri[1], id.others);
                List<IdealWithUniv<C>> rd = I.ideal.zeroDimDecompositionExtension(id.upolys, I.others);
                //System.out.println("rd = " + rd);
                dec.addAll(rd);
            }
        }
        return rdec;
    }


    /**
     * Zero dimensional ideal associated primary ideal. See algorithm
     * mas.masring.DIPIDEAL#DIRLPI.
     * @param P prime ideal associated to this
     * @return primary ideal of this with respect to the associated pime ideal P
     */
    public Ideal<C> primaryIdeal(Ideal<C> P) {
        Ideal<C> Qs = P;
        Ideal<C> Q;
        int e = 0;
        do {
            Q = Qs;
            e++;
            Qs = Q.product(P);
        } while (Qs.contains(this));
        boolean t;
        Ideal<C> As;
        do {
            As = this.sum(Qs);
            t = As.contains(Q);
            if (!t) {
                Q = Qs;
                e++;
                Qs = Q.product(P);
            }
        } while (!t);
        logger.info("exponent = " + e);
        return As;
    }


    /**
     * Zero dimensional ideal primary decompostition.
     * @return list of primary components of primary ideals G_i (pairwise co-prime) 
     *         with ideal(this) = cap_i( ideal(G_i) ) together with the associated primes
     */
    public List<PrimaryComponent<C>> zeroDimPrimaryDecomposition() {
        List<IdealWithUniv<C>> pdec = zeroDimPrimeDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("prim decomp = " + pdec);
        }
        return zeroDimPrimaryDecomposition(pdec);
    }


    /**
     * Zero dimensional ideal elimination to original ring.
     * @param pdec list of prime ideals G_i
     * @return intersection of pairwise co-prime prime ideals G_i in the ring of this with
     *         ideal(this) = cap_i( ideal(G_i) )
     */
    public List<IdealWithUniv<C>> zeroDimElimination(List<IdealWithUniv<C>> pdec) {
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        if (this.isZERO()) {
            return dec;
        }
        if (this.isONE()) {
            dec.add(pdec.get(0));
            return dec;
        }
        List<IdealWithUniv<C>> qdec = new ArrayList<IdealWithUniv<C>>();
        for (IdealWithUniv<C> Ip : pdec) {
            //System.out.println("Ip = " + Ip);
            List<GenPolynomial<C>> epol = new ArrayList<GenPolynomial<C>>();
            epol.addAll(Ip.ideal.getList());
            GenPolynomialRing<C> mfac = Ip.ideal.list.ring;
            int j = 0;
            // add univariate polynomials for performance
            for (GenPolynomial<C> p : Ip.upolys) {
                GenPolynomial<C> pm = p.extendUnivariate(mfac, j++);
                if (j != 1) { // skip double
                    epol.add(pm);
                }
            }
            // add other generators for performance
            if (Ip.others != null) {
                epol.addAll(Ip.others);
            }
            Ideal<C> Ipp = new Ideal<C>(mfac, epol);
            // logger.info("eliminate_1 = " + Ipp);
            TermOrder to = null;
            if (mfac.tord.getEvord() != TermOrder.IGRLEX) {
                List<GenPolynomial<C>> epols = new ArrayList<GenPolynomial<C>>();
                to = new TermOrder(TermOrder.IGRLEX);
                GenPolynomialRing<C> smfac 
                   = new GenPolynomialRing<C>(mfac.coFac, mfac.nvar, to, mfac.getVars());
                for (GenPolynomial<C> p : epol) {
                    GenPolynomial<C> pm = smfac.copy(p);
                    epols.add(pm.monic());
                }
                //epol = epols; 
                Ipp = new Ideal<C>(smfac, epols);
            }
            epol = red.irreducibleSet(Ipp.getList());
            Ipp = new Ideal<C>(Ipp.getRing(), epol);
            if (logger.isInfoEnabled()) {
                logger.info("eliminate = " + Ipp);
            }
            Ideal<C> Is = Ipp.eliminate(list.ring);
            //System.out.println("Is = " + Is);
            if (to != null && !Is.list.ring.equals(list.ring)) {
                List<GenPolynomial<C>> epols = new ArrayList<GenPolynomial<C>>();
                for (GenPolynomial<C> p : Is.getList()) {
                    GenPolynomial<C> pm = list.ring.copy(p);
                    epols.add(pm);
                }
                Is = new Ideal<C>(list.ring, epols);
                //System.out.println("Is = " + Is);
            }
            int k = Ip.upolys.size() - list.ring.nvar;
            List<GenPolynomial<C>> up = new ArrayList<GenPolynomial<C>>();
            for (int i = 0; i < list.ring.nvar; i++) {
                up.add(Ip.upolys.get(i + k));
            }
            IdealWithUniv<C> Ie = new IdealWithUniv<C>(Is, up);
            qdec.add(Ie);
        }
        return qdec;
    }


    /**
     * Zero dimensional ideal primary decompostition.
     * @param pdec list of prime ideals G_i with no field extensions
     * @return list of primary components of primary ideals G_i (pairwise co-prime) 
     *         with ideal(this) = cap_i( ideal(G_i) ) together with the associated primes
     */
    public List<PrimaryComponent<C>> zeroDimPrimaryDecomposition(List<IdealWithUniv<C>> pdec) {
        List<PrimaryComponent<C>> dec = new ArrayList<PrimaryComponent<C>>();
        if (this.isZERO()) {
            return dec;
        }
        if (this.isONE()) {
            PrimaryComponent<C> pc = new PrimaryComponent<C>(pdec.get(0).ideal, pdec.get(0));
            dec.add(pc);
            return dec;
        }
        for (IdealWithUniv<C> Ip : pdec) {
            Ideal<C> Qs = this.primaryIdeal(Ip.ideal);
            PrimaryComponent<C> pc = new PrimaryComponent<C>(Qs, Ip);
            dec.add(pc);
        }
        return dec;
    }


    /**
     * Test for primary ideal decompostition.
     * @param L list of primary components G_i
     * @return true if ideal(this) == cap_i( ideal(G_i) )
     */
    public boolean isPrimaryDecomposition(List<PrimaryComponent<C>> L) {
        // test if this is contained in the intersection
        for (PrimaryComponent<C> I : L) {
            boolean t = I.primary.contains(this);
            if (!t) {
                System.out.println("not contained " + this + " in " + I);
                return false;
            }
        }
        Ideal<C> isec = null;
        for (PrimaryComponent<C> I : L) {
            if (isec == null) {
                isec = I.primary;
            } else {
                isec = isec.intersect(I.primary);
            }
        }
        return this.contains(isec);
    }


    /**
     * Ideal extension.
     * @param vars list of variables for a polynomial ring for extension
     * @return ideal G, with coefficients in QuotientRing(GenPolynomialRing<C>(vars))
     */
    public IdealWithUniv<Quotient<C>> extension(String[] vars) {
        GenPolynomialRing<C> fac = getRing();
        GenPolynomialRing<C> efac = new GenPolynomialRing<C>(fac.coFac, vars.length, fac.tord, vars);
        IdealWithUniv<Quotient<C>> ext = extension(efac);
        return ext;
    }


    /**
     * Ideal extension.
     * @param efac polynomial ring for extension
     * @return ideal G, with coefficients in QuotientRing(efac)
     */
    public IdealWithUniv<Quotient<C>> extension(GenPolynomialRing<C> efac) {
        QuotientRing<C> qfac = new QuotientRing<C>(efac);
        IdealWithUniv<Quotient<C>> ext = extension(qfac);
        return ext;
    }


    /**
     * Ideal extension.
     * @param qfac quotient polynomial ring for extension
     * @return ideal G, with coefficients in qfac
     */
    public IdealWithUniv<Quotient<C>> extension(QuotientRing<C> qfac) {
        GenPolynomialRing<C> fac = getRing();
        GenPolynomialRing<C> efac = qfac.ring;
        String[] rvars = GroebnerBasePartial.remainingVars(fac.getVars(), efac.getVars());
        //System.out.println("rvars = " + Arrays.toString(rvars));

        GroebnerBasePartial<C> bbp = new GroebnerBasePartial<C>();
        //wrong: OptimizedPolynomialList<C> pgb = bbp.partialGB(getList(),rvars);
        OptimizedPolynomialList<C> pgb = bbp.elimPartialGB(getList(), rvars, efac.getVars());
        if (logger.isInfoEnabled()) {
            logger.info("rvars = " + Arrays.toString(rvars));
            logger.info("partialGB = " + pgb);
        }

        GenPolynomialRing<GenPolynomial<C>> rfac 
            = new GenPolynomialRing<GenPolynomial<C>>(efac, rvars.length, fac.tord, rvars);
        List<GenPolynomial<C>> list = pgb.list;
        List<GenPolynomial<GenPolynomial<C>>> rpgb = PolyUtil.<C> recursive(rfac, list);
        //System.out.println("rfac = " + rfac);
        GenPolynomialRing<Quotient<C>> qpfac = new GenPolynomialRing<Quotient<C>>(qfac, rfac);
        List<GenPolynomial<Quotient<C>>> qpgb = PolyUfdUtil.<C> quotientFromIntegralCoefficients(qpfac, rpgb);
        //System.out.println("qpfac = " + qpfac);

        // compute f 
        GreatestCommonDivisor<C> ufd = GCDFactory.getImplementation(fac.coFac);
        GenPolynomial<C> f = null; // qfac.ring.getONE();
        for (GenPolynomial<GenPolynomial<C>> p : rpgb) {
            if (f == null) {
                f = p.leadingBaseCoefficient();
            } else {
                f = ufd.lcm(f, p.leadingBaseCoefficient());
            }
        }
        //SquarefreeAbstract<C> sqf = SquarefreeFactory.getImplementation(fac.coFac);
        //not required: f = sqf.squarefreePart(f);
        GenPolynomial<GenPolynomial<C>> fp = rfac.getONE().multiply(f);
        GenPolynomial<Quotient<C>> fq = PolyUfdUtil.<C> quotientFromIntegralCoefficients(qpfac, fp);
        if (logger.isInfoEnabled()) {
            logger.info("extension f = " + f);
            logger.info("ext = " + qpgb);
        }
        List<GenPolynomial<Quotient<C>>> upols = new ArrayList<GenPolynomial<Quotient<C>>>(0);
        List<GenPolynomial<Quotient<C>>> opols = new ArrayList<GenPolynomial<Quotient<C>>>(1);
        opols.add(fq);

        qpgb = PolyUtil.<Quotient<C>> monic(qpgb);
        Ideal<Quotient<C>> ext = new Ideal<Quotient<C>>(qpfac, qpgb);
        IdealWithUniv<Quotient<C>> extu = new IdealWithUniv<Quotient<C>>(ext, upols, opols);
        return extu;
    }


    /**
     * Ideal contraction and permutation.
     * @param eideal extension ideal of this.
     * @return contraction ideal of eideal in this polynomial ring
     */
    public IdealWithUniv<C> permContraction(IdealWithUniv<Quotient<C>> eideal) {
        return Ideal.<C> permutation(getRing(), Ideal.<C> contraction(eideal));
    }


    /**
     * Ideal contraction.
     * @param eid extension ideal of this.
     * @return contraction ideal of eid in distributed polynomial ring
     */
    public static <C extends GcdRingElem<C>> IdealWithUniv<C> contraction(IdealWithUniv<Quotient<C>> eid) {
        Ideal<Quotient<C>> eideal = eid.ideal;
        List<GenPolynomial<Quotient<C>>> qgb = eideal.getList();
        QuotientRing<C> qfac = (QuotientRing<C>) eideal.getRing().coFac;
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(qfac.ring, eideal.getRing());
        GenPolynomialRing<C> dfac = qfac.ring.extend(eideal.getRing().getVars());
        TermOrder to = new TermOrder(qfac.ring.tord.getEvord());
        dfac = new GenPolynomialRing<C>(dfac.coFac, dfac.nvar, to, dfac.getVars());
        //System.out.println("qfac = " + qfac);
        //System.out.println("rfac = " + rfac);
        //System.out.println("dfac = " + dfac);
	// convert polynomials
        List<GenPolynomial<GenPolynomial<C>>> cgb = PolyUfdUtil.<C> integralFromQuotientCoefficients(rfac,qgb);
        List<GenPolynomial<C>> dgb = PolyUtil.<C> distribute(dfac, cgb);
        Ideal<C> cont = new Ideal<C>(dfac, dgb);
	// convert other polynomials
        List<GenPolynomial<C>> opols = new ArrayList<GenPolynomial<C>>();
        if (eid.others != null && eid.others.size() > 0) {
            List<GenPolynomial<GenPolynomial<C>>> orpol 
                = PolyUfdUtil.<C> integralFromQuotientCoefficients(rfac, eid.others);
            List<GenPolynomial<C>> opol = PolyUtil.<C> distribute(dfac, orpol);
            opols.addAll(opol);
        }
	// convert univariate polynomials
        List<GenPolynomial<C>> upols = new ArrayList<GenPolynomial<C>>(0);
        int i = 0;
        for (GenPolynomial<Quotient<C>> p : eid.upolys) {
	    GenPolynomial<Quotient<C>> pm = p.extendUnivariate(eideal.getRing(), i++);
            //System.out.println("pm = " + pm + ", p = " + p);
            GenPolynomial<GenPolynomial<C>> urpol = PolyUfdUtil.<C> integralFromQuotientCoefficients(rfac, pm);
            GenPolynomial<C> upol = PolyUtil.<C> distribute(dfac, urpol);
            upols.add(upol);
            //System.out.println("upol = " + upol);
	}
        // compute f 
        GreatestCommonDivisor<C> ufd = GCDFactory.getImplementation(qfac.ring.coFac);
        GenPolynomial<C> f = null; // qfac.ring.getONE();
        for (GenPolynomial<GenPolynomial<C>> p : cgb) {
            if (f == null) {
                f = p.leadingBaseCoefficient();
            } else {
                f = ufd.lcm(f, p.leadingBaseCoefficient());
            }
        }
        GenPolynomial<GenPolynomial<C>> fp = rfac.getONE().multiply(f);
        f = PolyUtil.<C> distribute(dfac, fp);
        if (logger.isInfoEnabled()) {
            logger.info("contraction f = " + f);
            logger.info("cont = " + cont);
        }
        opols.add(f);
        if (f.isONE()) {
            IdealWithUniv<C> cf = new IdealWithUniv<C>(cont, upols, opols);
            return cf;
        }
        // compute ideal quotient by f
        Ideal<C> Q = cont.infiniteQuotientRab(f);
        IdealWithUniv<C> Qu = new IdealWithUniv<C>(Q, upols, opols);
        return Qu;
    }


    /**
     * Ideal permutation.
     * @param oring polynomial ring to which variables are back permuted.
     * @param Cont ideal to be permuted
     * @return permutation of cont in polynomial ring oring
     */
    public static <C extends GcdRingElem<C>> IdealWithUniv<C> permutation(GenPolynomialRing<C> oring,
            IdealWithUniv<C> Cont) {
        Ideal<C> cont = Cont.ideal;
        GenPolynomialRing<C> dfac = cont.getRing();
        // (back) permutation of variables
        String[] ovars = oring.getVars();
        String[] dvars = dfac.getVars();
        //System.out.println("ovars = " + Arrays.toString(ovars));
        //System.out.println("dvars = " + Arrays.toString(dvars));
        if (Arrays.equals(ovars, dvars)) { // nothing to do
            return Cont;
        }
        List<Integer> perm = GroebnerBasePartial.getPermutation(dvars, ovars);
        //System.out.println("perm  = " + perm);
        GenPolynomialRing<C> pfac = TermOrderOptimization.<C> permutation(perm, cont.getRing());
        if (logger.isInfoEnabled()) {
            logger.info("pfac = " + pfac);
        }
        List<GenPolynomial<C>> ppolys = TermOrderOptimization.<C> permutation(perm, pfac, cont.getList());
        //System.out.println("ppolys = " + ppolys);
        cont = new Ideal<C>(pfac, ppolys);
        if (logger.isDebugEnabled()) {
            logger.info("perm cont = " + cont);
        }
        List<GenPolynomial<C>> opolys = TermOrderOptimization.<C> permutation(perm, pfac, Cont.others);
        //System.out.println("opolys = " + opolys);
        List<GenPolynomial<C>> upolys = TermOrderOptimization.<C> permutation(perm, pfac, Cont.upolys);
        //System.out.println("opolys = " + opolys);
        IdealWithUniv<C> Cu = new IdealWithUniv<C>(cont, upolys, opolys);
        return Cu;
    }


    /**
     * Ideal radical.
     * @return the radical ideal of this
     */
    public Ideal<C> radical() {
        List<IdealWithUniv<C>> rdec = radicalDecomposition();
        List<Ideal<C>> dec = new ArrayList<Ideal<C>>(rdec.size());
        for (IdealWithUniv<C> ru : rdec) {
            dec.add(ru.ideal);
        }
        Ideal<C> R = intersect(dec);
        return R;
    }


    /**
     * Ideal radical decompostition.
     * @return intersection of ideals G_i with radical(this) eq cap_i(
     *         ideal(G_i) ) and each G_i is a radical ideal and the G_i are pairwise co-prime
     */
    public List<IdealWithUniv<C>> radicalDecomposition() {
        // check dimension
        int z = commonZeroTest();
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        List<GenPolynomial<C>> ups = new ArrayList<GenPolynomial<C>>();
        // dimension -1
        if (z < 0) {
            IdealWithUniv<C> id = new IdealWithUniv<C>(this, ups);
            dec.add(id); // see GB book
            return dec;
        }
        // dimension 0
        if (z == 0) {
            dec = zeroDimRadicalDecomposition();
            return dec;
        }
        // dimension > 0
        if (this.isZERO()) {
            return dec;
        }
        Dimension dim = dimension();
        if (logger.isInfoEnabled()) {
            logger.info("dimension = " + dim);
        }

        // shortest maximal independent set
        Set<Set<Integer>> M = dim.M;
        Set<Integer> min = null;
        for (Set<Integer> m : M) {
            if (min == null) {
                min = m;
                continue;
            }
            if (m.size() < min.size()) {
                min = m;
            }
        }
        //System.out.println("min = " + min);
        String[] mvars = new String[min.size()];
        int j = 0;
        for (Integer i : min) {
            mvars[j++] = dim.v[i];
        }
        if (logger.isInfoEnabled()) {
            logger.info("extension for variables = " + Arrays.toString(mvars));
        }
        // reduce to dimension zero
        IdealWithUniv<Quotient<C>> Ext = extension(mvars);
        if (logger.isInfoEnabled()) {
            logger.info("extension = " + Ext);
        }

        List<IdealWithUniv<Quotient<C>>> edec = Ext.ideal.zeroDimRadicalDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("0-dim radical decomp = " + edec);
        }
        // remove field extensions are not required
        // reconstruct dimension
        for (IdealWithUniv<Quotient<C>> ep : edec) {
            IdealWithUniv<C> cont = permContraction(ep);
            //System.out.println("cont = " + cont);
            dec.add(cont);
        }
        IdealWithUniv<C> extcont = permContraction(Ext);
        //System.out.println("extcont = " + extcont);

        // get f
        List<GenPolynomial<C>> ql = extcont.others;
        if (ql.size() == 0) { // should not happen
            return dec;
        }
        GenPolynomial<C> fx = ql.get(0);
        //System.out.println("cont(Ext) fx = " + fx + ", " + fx.ring);
        if (fx.isONE()) {
            return dec;
        }
        Ideal<C> T = sum(fx);
        //System.out.println("T.rec = " + T.getList());
        if (T.isONE()) {
            logger.info("1 in ideal for " + fx);
            return dec;
        }
        if (logger.isInfoEnabled()) {
            logger.info("radical decomp ext-cont fx = " + fx);
            logger.info("recursion radical decomp T = " + T);
        }
        // recursion:
        List<IdealWithUniv<C>> Tdec = T.radicalDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("recursion radical decomp = " + Tdec);
        }
        dec.addAll(Tdec);
        return dec;
    }


    /**
     * Ideal irreducible decompostition.
     * @return intersection of ideals G_i with ideal(this) subseteq cap_i(
     *         ideal(G_i) ) and each G_i is an ideal with irreducible univariate
     *         polynomials (after extension to a zero dimensional ideal) 
     *         and the G_i are pairwise co-prime
     */
    public List<IdealWithUniv<C>> decomposition() {
        // check dimension
        int z = commonZeroTest();
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        List<GenPolynomial<C>> ups = new ArrayList<GenPolynomial<C>>();
        // dimension -1
        if (z < 0) {
            //IdealWithUniv<C> id = new IdealWithUniv<C>(this, ups);
            //dec.add(id); see GB book
            return dec;
        }
        // dimension 0
        if (z == 0) {
            dec = zeroDimDecomposition();
            return dec;
        }
        // dimension > 0
        if (this.isZERO()) {
            return dec;
        }
        Dimension dim = dimension();
        if (logger.isInfoEnabled()) {
            logger.info("dimension = " + dim);
        }

        // shortest maximal independent set
        Set<Set<Integer>> M = dim.M;
        Set<Integer> min = null;
        for (Set<Integer> m : M) {
            if (min == null) {
                min = m;
                continue;
            }
            if (m.size() < min.size()) {
                min = m;
            }
        }
        //System.out.println("min = " + min);
        String[] mvars = new String[min.size()];
        int j = 0;
        for (Integer i : min) {
            mvars[j++] = dim.v[i];
        }
        if (logger.isInfoEnabled()) {
            logger.info("extension for variables = " + Arrays.toString(mvars));
        }
        // reduce to dimension zero
        IdealWithUniv<Quotient<C>> Ext = extension(mvars);
        if (logger.isInfoEnabled()) {
            logger.info("extension = " + Ext);
        }

        List<IdealWithUniv<Quotient<C>>> edec = Ext.ideal.zeroDimDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("0-dim irred decomp = " + edec);
        }
        // remove field extensions are not required
        // reconstruct dimension
        for (IdealWithUniv<Quotient<C>> ep : edec) {
            IdealWithUniv<C> cont = permContraction(ep);
            //System.out.println("cont = " + cont);
            dec.add(cont);
        }
        IdealWithUniv<C> extcont = permContraction(Ext);
        //System.out.println("extcont = " + extcont);

        // get f
        List<GenPolynomial<C>> ql = extcont.others;
        if (ql.size() == 0) { // should not happen
            return dec;
        }
        GenPolynomial<C> fx = ql.get(0);
        //System.out.println("cont(Ext) fx = " + fx + ", " + fx.ring);
        if (fx.isONE()) {
            return dec;
        }
        Ideal<C> T = sum(fx);
        //System.out.println("T.rec = " + T.getList());
        if (T.isONE()) {
            logger.info("1 in ideal for " + fx);
            return dec;
        }
        if (logger.isInfoEnabled()) {
            logger.info("radical decomp ext-cont fx = " + fx);
            logger.info("recursion radical decomp T = " + T);
        }
        // recursion:
        List<IdealWithUniv<C>> Tdec = T.decomposition();
        if (logger.isInfoEnabled()) {
            logger.info("recursion irred decomposition = " + Tdec);
        }
        dec.addAll(Tdec);
        return dec;
    }


    /**
     * Ideal prime decompostition.
     * @return intersection of ideals G_i with ideal(this) subseteq cap_i(
     *         ideal(G_i) ) and each G_i is a prime ideal and the G_i are pairwise co-prime
     */
    public List<IdealWithUniv<C>> primeDecomposition() {
        // check dimension
        int z = commonZeroTest();
        List<IdealWithUniv<C>> dec = new ArrayList<IdealWithUniv<C>>();
        List<GenPolynomial<C>> ups = new ArrayList<GenPolynomial<C>>();
        // dimension -1
        if (z < 0) {
            //IdealWithUniv<C> id = new IdealWithUniv<C>(this, ups);
            //dec.add(id); see GB book
            return dec;
        }
        // dimension 0
        if (z == 0) {
            dec = zeroDimPrimeDecomposition();
            return dec;
        }
        // dimension > 0
        if (this.isZERO()) {
            return dec;
        }
        Dimension dim = dimension();
        if (logger.isInfoEnabled()) {
            logger.info("dimension = " + dim);
        }

        // shortest maximal independent set
        Set<Set<Integer>> M = dim.M;
        Set<Integer> min = null;
        for (Set<Integer> m : M) {
            if (min == null) {
                min = m;
                continue;
            }
            if (m.size() < min.size()) {
                min = m;
            }
        }
        //System.out.println("min = " + min);
        String[] mvars = new String[min.size()];
        int j = 0;
        for (Integer i : min) {
            mvars[j++] = dim.v[i];
        }
        if (logger.isInfoEnabled()) {
            logger.info("extension for variables = " + Arrays.toString(mvars));
        }
        // reduce to dimension zero
        IdealWithUniv<Quotient<C>> Ext = extension(mvars);
        if (logger.isInfoEnabled()) {
            logger.info("extension = " + Ext);
        }
        List<IdealWithUniv<Quotient<C>>> edec = Ext.ideal.zeroDimPrimeDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("0-dim prime decomp = " + edec);
        }
        // remove field extensions, already done
        // reconstruct dimension
        for (IdealWithUniv<Quotient<C>> ep : edec) {
            IdealWithUniv<C> cont = permContraction(ep);
            //System.out.println("cont = " + cont);
            dec.add(cont);
        }
        // get f
        IdealWithUniv<C> extcont = permContraction(Ext);
        //System.out.println("extcont = " + extcont);
        List<GenPolynomial<C>> ql = extcont.others;
        if (ql.size() == 0) { // should not happen
            return dec;
        }
        GenPolynomial<C> fx = ql.get(0);
        //System.out.println("cont(Ext) fx = " + fx + ", " + fx.ring);
        if (fx.isONE()) {
            return dec;
        }
        // compute exponent not required
        Ideal<C> T = sum(fx);
        //System.out.println("T.rec = " + T.getList());
        if (T.isONE()) {
            logger.info("1 in ideal for " + fx);
            return dec;
        }
        if (logger.isInfoEnabled()) {
            logger.info("radical decomp ext-cont fx = " + fx);
            logger.info("recursion radical decomp T = " + T);
        }
        // recursion:
        List<IdealWithUniv<C>> Tdec = T.primeDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("recursion prime decomp = " + Tdec);
        }
        dec.addAll(Tdec);
        return dec;
    }


    /**
     * Test for ideal decompostition.
     * @param L intersection of ideals G_i with ideal(G) eq cap_i(ideal(G_i) )
     * @return true if L is a decomposition of this, else false
     */
    public boolean isDecomposition(List<IdealWithUniv<C>> L) {
        if (L == null || L.size() == 0) {
            if (this.isZERO()) {
                return true;
            } else {
                return false;
            }
        }
        GenPolynomialRing<C> ofac = list.ring;
        int r = ofac.nvar;
        int rp = L.get(0).ideal.list.ring.nvar;
        int d = rp - r;
        //System.out.println("d = " + d);
        Ideal<C> Id = this;
        if (d > 0) { // add lower variables
            GenPolynomialRing<C> nfac = ofac.extendLower(d);
            //System.out.println("nfac = " + nfac);
            List<GenPolynomial<C>> elist = new ArrayList<GenPolynomial<C>>(list.list.size());
            for (GenPolynomial<C> p : getList()) {
                //System.out.println("p = " + p);
                GenPolynomial<C> q = p.extendLower(nfac, 0, 0L);
                //System.out.println("q = "  + q);
                elist.add(q);
            }
            Id = new Ideal<C>(nfac, elist, isGB, isTopt);
        }

        // test if this is contained in the intersection
        for (IdealWithUniv<C> I : L) {
            boolean t = I.ideal.contains(Id);
            if (!t) {
                System.out.println("not contained " + this + " in " + I.ideal);
                return false;
            }
        }
        //         // test if all univariate polynomials are contained in the respective ideal
        //         for (IdealWithUniv<C> I : L) {
        //             GenPolynomialRing<C> mfac = I.ideal.list.ring;
        //             int i = 0;
        //             for (GenPolynomial<C> p : I.upolys) {
        //                 GenPolynomial<C> pm = p.extendUnivariate(mfac, i++);
        //                 //System.out.println("pm = " + pm + ", p = " + p);
        //                 boolean t = I.ideal.contains(pm);
        //                 if (!t) {
        //                     System.out.println("not contained " + pm + " in " + I.ideal);
        //                     return false;
        //                 }
        //             }
        //         }
        return true;
    }


    /**
     * Ideal primary decompostition.
     * @return list of primary components of primary ideals G_i (pairwise co-prime) 
     *         with ideal(this) = cap_i( ideal(G_i) ) together with the associated primes
     */
    public List<PrimaryComponent<C>> primaryDecomposition() {
        // check dimension
        int z = commonZeroTest();
        List<PrimaryComponent<C>> dec = new ArrayList<PrimaryComponent<C>>();
        List<GenPolynomial<C>> ups = new ArrayList<GenPolynomial<C>>();
        // dimension -1
        if (z < 0) {
            //IdealWithUniv<C> id = new IdealWithUniv<C>(this, ups);
            //PrimaryComponent<C> pc = new PrimaryComponent<C>(this, id);
            //dec.add(pc); see GB book
            return dec;
        }
        // dimension 0
        if (z == 0) {
            dec = zeroDimPrimaryDecomposition();
            return dec;
        }
        // dimension > 0
        if (this.isZERO()) {
            return dec;
        }
        Dimension dim = dimension();
        if (logger.isInfoEnabled()) {
            logger.info("dimension = " + dim);
        }

        // shortest maximal independent set
        Set<Set<Integer>> M = dim.M;
        Set<Integer> min = null;
        for (Set<Integer> m : M) {
            if (min == null) {
                min = m;
                continue;
            }
            if (m.size() < min.size()) {
                min = m;
            }
        }
        //System.out.println("min = " + min);
        String[] mvars = new String[min.size()];
        int j = 0;
        for (Integer i : min) {
            mvars[j++] = dim.v[i];
        }
        if (logger.isInfoEnabled()) {
            logger.info("extension for variables = " + Arrays.toString(mvars));
        }
        // reduce to dimension zero
        IdealWithUniv<Quotient<C>> Ext = extension(mvars);
        if (logger.isInfoEnabled()) {
            logger.info("extension = " + Ext);
        }

        List<PrimaryComponent<Quotient<C>>> edec = Ext.ideal.zeroDimPrimaryDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("0-dim primary decomp = " + edec);
        }
        // remove field extensions, already done
        // reconstruct dimension
        List<GenPolynomial<Quotient<C>>> upq = new ArrayList<GenPolynomial<Quotient<C>>>();
        for (PrimaryComponent<Quotient<C>> ep : edec) {
            IdealWithUniv<Quotient<C>> epu = new IdealWithUniv<Quotient<C>>(ep.primary, upq);
            IdealWithUniv<C> contq = permContraction(epu);
            IdealWithUniv<C> contp = permContraction(ep.prime);
            PrimaryComponent<C> pc = new PrimaryComponent<C>(contq.ideal, contp);
            //System.out.println("pc = " + pc);
            dec.add(pc);
        }

        // get f
        IdealWithUniv<C> extcont = permContraction(Ext);
        if (debug) {
            logger.info("cont(Ext) = " + extcont);
        }
        List<GenPolynomial<C>> ql = extcont.others;
        if (ql.size() == 0) { // should not happen
            return dec;
        }
        GenPolynomial<C> fx = ql.get(0);
        //System.out.println("cont(Ext) fx = " + fx + ", " + fx.ring);
        if (fx.isONE()) {
            return dec;
        }
        // compute exponent
        int s = this.infiniteQuotientExponent(fx, extcont.ideal);
        if (s == 0) {
            logger.info("exponent is 0 ");
            return dec;
        }
        if (s > 1) {
            fx = Power.<GenPolynomial<C>> positivePower(fx, s);
        }
        if (debug) {
            logger.info("exponent fx = " + s + ", fx^s = " + fx);
        }

        Ideal<C> T = sum(fx);
        //System.out.println("T.rec = " + T.getList());
        if (T.isONE()) {
            logger.info("1 in ideal for " + fx);
            return dec;
        }
        if (logger.isInfoEnabled()) {
            logger.info("radical decomp ext-cont fx = " + fx);
            logger.info("recursion radical decomp T = " + T);
        }
        // recursion:
        List<PrimaryComponent<C>> Tdec = T.primaryDecomposition();
        if (logger.isInfoEnabled()) {
            logger.info("recursion primary decomp = " + Tdec);
        }
        dec.addAll(Tdec);
        return dec;
    }

}

/*
 * $Id: ColoredSystem.java 2828 2009-09-27 12:30:52Z kredel $
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for a condition, a corresponding colored polynomial list and a
 * Groebner base pair list.
 * @param <C> coefficient type
 */
public class ColoredSystem<C extends GcdRingElem<C>> implements Cloneable {


    private static final Logger logger = Logger.getLogger(ColoredSystem.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * Condition determinig this colored system.
     */
    public final Condition<C> condition;


    /**
     * Colored polynomials of this system.
     */
    public final List<ColorPolynomial<C>> list;


    /**
     * Groebner base pair list of this system.
     */
    public final OrderedCPairlist<C> pairlist;


    /**
     * Constructor for a colored polynomial system.
     * @param cond a condition.
     * @param S a list of colored polynomials.
     */
    public ColoredSystem(Condition<C> cond, List<ColorPolynomial<C>> S) {
        this(cond, S, null);
    }


    /**
     * Constructor for a colored polynomial system.
     * @param cond a condition.
     * @param S a list of colored polynomials.
     * @param pl a ordered pair list.
     */
    public ColoredSystem(Condition<C> cond, List<ColorPolynomial<C>> S,
            OrderedCPairlist<C> pl) {
        this.condition = cond;
        this.list = S;
        this.pairlist = pl;
    }


    /**
     * Clone this colored polynomial system.
     * @return a clone of this.
     */
    @Override
    public ColoredSystem<C> clone() {
        return new ColoredSystem<C>(condition, list, pairlist.clone());
    }


    /**
     * Add to list of colored systems.
     * This is added to the list of colored systems, 
     * if a system with the same condition is not already contained.
     * @param L a list of colored systems.
     * @return L.add(this) if this not in L, else L.
     */
    public List<ColoredSystem<C>> addToList(List<ColoredSystem<C>> L) {
        List<ColoredSystem<C>> S = new ArrayList<ColoredSystem<C>>(L.size()+1);
        boolean contained = false;
        for (ColoredSystem<C> x : L) {
            if (condition.equals(x.condition) && list.equals(x.list)) {
                logger.info("replaced system = " + x.condition);
                S.add(this);
                contained = true;
            } else { // copy existing
                // System.out.println("kept system = " + x);
                S.add(x);
            }
        }
        if (!contained) {
            S.add(this);
        }
        return S;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("ColoredSystem: \n");
        if (list.size() > 0) {
            s.append("polynomial ring : " + list.get(0).green.ring + "\n");
        } else {
            s.append("parameter polynomial ring : " + condition.zero.list.ring + "\n");
        }
        s.append("conditions == 0 : " + getConditionZero() + "\n");
        s.append("conditions != 0 : " + getConditionNonZero() + "\n");
        if (debug) {
            s.append("green coefficients:\n" + getGreenCoefficients() + "\n");
            s.append("red coefficients:\n" + getRedCoefficients() + "\n");
        }
        s.append("colored polynomials:\n" + list + "\n");
        s.append("uncolored polynomials:\n" + getPolynomialList() + "\n");
        if (debug) {
            s.append("essential polynomials:\n" + getEssentialPolynomialList() + "\n");
        }
        if (pairlist != null) {
            s.append(pairlist.toString() + "\n");
        }
        return s.toString();
    }


    /**
     * Is this colored system equal to other.
     * @param c other colored system.
     * @return true, if this is equal to other, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object c) {
        ColoredSystem<C> cs = null;
        try {
            cs = (ColoredSystem<C>) c;
        } catch (ClassCastException e) {
            return false;
        }
        if (cs == null) {
            return false;
        }
        boolean t = (condition.equals(cs.condition) && list.equals(cs.list));
        if (!t) {
            return t;
        }
        // now t == true
        t = pairlist.equals(cs.pairlist);
        if (!t) {
            System.out.println("pairlists not equal " + pairlist + ", " + cs.pairlist);
        }
        return true; // if lists are equal ignore pairlists
    }


    /**
     * Hash code for this colored system.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = condition.hashCode();
        h = h << 17;
        h += list.hashCode();
        // h = h << 11;
        // h += pairlist.hashCode();
        return h;
    }


    /**
     * Get zero condition.
     * @return condition.zero.
     */
    public List<GenPolynomial<C>> getConditionZero() {
        return condition.zero.getList();
    }


    /**
     * Get non zero condition.
     * @return condition.nonZero.
     */
    public List<GenPolynomial<C>> getConditionNonZero() {
        return condition.nonZero.mset;
    }


    /**
     * Get list of red coefficients of polynomials.
     * @return list of all red coefficients of polynomials.
     */
    public List<GenPolynomial<C>> getRedCoefficients() {
        Set<GenPolynomial<C>> F = new HashSet<GenPolynomial<C>>();
        for (ColorPolynomial<C> s : list) {
            F.addAll(s.red.getMap().values());
        }
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>(F);
        return M;
    }


    /**
     * Get list of green coefficients of polynomials.
     * @return list of all green coefficients of polynomials.
     */
    public List<GenPolynomial<C>> getGreenCoefficients() {
        Set<GenPolynomial<C>> F = new HashSet<GenPolynomial<C>>();
        for (ColorPolynomial<C> s : list) {
            F.addAll(s.green.getMap().values());
        }
        List<GenPolynomial<C>> M = new ArrayList<GenPolynomial<C>>(F);
        return M;
    }


    /**
     * Get list of full polynomials.
     * @return list of all full polynomials.
     */
    public List<GenPolynomial<GenPolynomial<C>>> getPolynomialList() {
        List<GenPolynomial<GenPolynomial<C>>> F = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        for (ColorPolynomial<C> s : list) {
            F.add(s.getPolynomial());
        }
        return F;
    }


    /**
     * Get list of essential polynomials.
     * @return list of all essential polynomials.
     */
    public List<GenPolynomial<GenPolynomial<C>>> getEssentialPolynomialList() {
        List<GenPolynomial<GenPolynomial<C>>> F = new ArrayList<GenPolynomial<GenPolynomial<C>>>();
        for (ColorPolynomial<C> s : list) {
            F.add(s.getEssentialPolynomial());
        }
        return F;
    }


    /**
     * Check invariants. Check if all polynomials are determined and if the
     * color of all coefficients is correct with respect to the condition.
     * @return true, if all invariants are met, else false.
     */
    public boolean checkInvariant() {
        if (!isDetermined()) {
            return false;
        }
        if (!condition.isDetermined(list)) {
            return false;
        }
        // Condition<C> cond = condition;
        for (ColorPolynomial<C> s : list) {
            if (!s.checkInvariant()) {
                System.out.println("notInvariant " + s);
                System.out.println("condition:   " + condition);
                return false;
            }
            for (GenPolynomial<C> g : s.green.getMap().values()) {
                if (condition.color(g) != Condition.Color.GREEN) {
                    System.out.println("notGreen   " + g);
                    System.out.println("condition: " + condition);
                    System.out.println("colors:    " + s);
                    return false;
                }
            }
            for (GenPolynomial<C> r : s.red.getMap().values()) {
                if (condition.color(r) != Condition.Color.RED) {
                    System.out.println("notRed     " + r);
                    System.out.println("condition: " + condition);
                    System.out.println("colors:    " + s);
                    return false;
                }
            }
            for (GenPolynomial<C> w : s.white.getMap().values()) {
                if (condition.color(w) != Condition.Color.WHITE) {
                    // System.out.println("notWhite " + w);
                    // System.out.println("condition: " + condition);
                    // System.out.println("colors: " + s);
                    continue; // no error
                    // return false;
                }
            }
        }
        return true;
    }


    /**
     * Is this colored system completely determined.
     * @return true, if each ColorPolynomial is determined, else false.
     */
    public boolean isDetermined() {
        for (ColorPolynomial<C> s : list) {
            if (s.isZERO()) {
                continue;
            }
            if (!s.isDetermined()) {
                System.out.println("not simple determined " + s);
                System.out.println("condition:            " + condition);
                return false;
            }
            if (!condition.isDetermined(s)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Re determine colorings of polynomials.
     * @return colored system with re determined colored polynomials.
     */
    public ColoredSystem<C> reDetermine() {
        if (condition == null || condition.zero.isONE()) {
            return this;
        }
        List<ColorPolynomial<C>> Sn = new ArrayList<ColorPolynomial<C>>(list.size());
        for (ColorPolynomial<C> c : list) {
            ColorPolynomial<C> a = condition.reDetermine(c);
            // if ( !a.isZERO() ) {
            Sn.add(a); // must also add zeros
            // }
        }
        return new ColoredSystem<C>(condition, Sn, pairlist);
    }

}

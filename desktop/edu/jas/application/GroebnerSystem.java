/*
 * $Id: GroebnerSystem.java 2828 2009-09-27 12:30:52Z kredel $
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jas.poly.PolynomialList;
import edu.jas.poly.OrderedPolynomialList;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.structure.GcdRingElem;


/**
 * Container for a Groebner system. 
 * It contains a list of colored systems and a
 * list of parametric polynomials representing the 
 * corresponding comprehensive Groebner base.
 * @param <C> coefficient type
 */
public class GroebnerSystem<C extends GcdRingElem<C>> {


    private static final Logger logger = Logger.getLogger(GroebnerSystem.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * List of colored systems.
     */
    public final List<ColoredSystem<C>> list;


    /**
     * List of conditions for this Groebner system.
     */
    protected List<Condition<C>> conds;


    /**
     * Comprehensive Groebner base for this Groebner system.
     */
    protected PolynomialList<GenPolynomial<C>> cgb;


    /**
     * Constructor for a Groebner system.
     * @param S a list of colored systems.
     */
    public GroebnerSystem(List<ColoredSystem<C>> S) {
        this.list = S;
        this.conds = null;
        this.cgb = null;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("GroebnerSystem: \n");
        boolean first = true;
        for (ColoredSystem<C> cs : list) {
            if ( first ) {
               first = false;
            } else {
               sb.append("\n");
            }
            sb.append( cs.toString() );
        }
        sb.append("Conditions:\n");
        first = true;
        for ( Condition<C> cond : getConditions() ) {
            if ( first ) {
                first = false;
            } else {
                sb.append("\n");
            }
            sb.append( cond.toString() );
        }
        sb.append("\n");
        if ( cgb == null ) {
           sb.append("Comprehensive Groebner Base not jet computed\n");
        } else {
           sb.append("Comprehensive Groebner Base:\n");
           first = true;
           for ( GenPolynomial<GenPolynomial<C>> p : getCGB() ) {
               if ( first ) {
                  first = false;
               } else {
                  sb.append(",\n");
               }
               sb.append( p.toString() );
           }
           sb.append("\n");
        }
        return sb.toString();
    }


    /**
     * Is this Groebner system equal to other.
     * @param c other Groebner system.
     * @return true, if this is equal to other, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object c) {
        GroebnerSystem<C> cs = null;
        try {
            cs = (GroebnerSystem<C>) c;
        } catch (ClassCastException e) {
            return false;
        }
        if (cs == null) {
            return false;
        }
        boolean t = list.equals(cs.list);
        return t;
    }


    /**
     * Hash code for this colored system.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = list.hashCode();
        return h;
    }


    /**
     * Check invariants. Check if all colored systems are determined and 
     * all invariants are met.
     * @return true, if all invariants are met, else false.
     */
    public boolean checkInvariant() {
        for (ColoredSystem<C> s : list) {
            if (!s.checkInvariant()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Is each colored system completely determined.
     * @return true, if each ColoredSystem is determined, else false.
     */
    public boolean isDetermined() {
        for (ColoredSystem<C> s : list) {
            if (!s.isDetermined()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Get list of conditions determining this Groebner system. 
     * @return list of determining conditions.
     */
    public List<Condition<C>> getConditions() {
        if ( conds != null ) {
           return conds;
        }
        List<Condition<C>> cd = new ArrayList<Condition<C>>( list.size() );
        for (ColoredSystem<C> cs : list) {
            cd.add(cs.condition);
        }
        conds = cd;
        return conds;
    }


    /**
     * Get comprehensive Groebner base. 
     * @return the comprehensive Groebner base for this Groebner system.
     */
    public List<GenPolynomial<GenPolynomial<C>>> getCGB() {
        if ( cgb != null ) {
           return cgb.list;
        }
        // assure conditions are collected
        List<Condition<C>> unused = getConditions();
        // combine for CGB
        Set<GenPolynomial<GenPolynomial<C>>> Gs 
           = new HashSet<GenPolynomial<GenPolynomial<C>>>();
        for (ColoredSystem<C> cs : list) {
            if (debug) {
                if (!cs.isDetermined()) {
                    System.out.println("not determined, cs = " + cs);
                }
                if (!cs.checkInvariant()) {
                    System.out.println("not invariant, cs = " + cs);
                }
            }
            for (ColorPolynomial<C> p : cs.list) {
                GenPolynomial<GenPolynomial<C>> f = p.getPolynomial();
                Gs.add(f);
            }
        }
        List<GenPolynomial<GenPolynomial<C>>> G 
            = new ArrayList<GenPolynomial<GenPolynomial<C>>>(Gs);
        GenPolynomialRing<GenPolynomial<C>> ring = null;
        if ( G.size() > 0 ) {
           ring = G.get(0).ring;
        }
        cgb = new OrderedPolynomialList<GenPolynomial<C>>(ring,G);
        return G;
    }

}

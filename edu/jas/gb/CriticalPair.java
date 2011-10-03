/*
 * $Id: CriticalPair.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.io.Serializable;

import edu.jas.structure.RingElem;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;


/**
 * Serializable subclass to hold critical pairs of polynomials.
 * Used also to manage reduction status of the pair.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */
public class CriticalPair<C extends RingElem<C> > 
             implements Serializable {

    public final ExpVector e;
    public final GenPolynomial<C> pi;
    public final GenPolynomial<C> pj;
    public final int i;
    public final int j;
    protected volatile boolean inReduction;
    protected volatile GenPolynomial<C> reductum;
    //public final ExpVector sugar;


    /**
     * CriticalPair constructor.
     * @param e lcm(lt(pi),lt(pj).
     * @param pi polynomial i.
     * @param pj polynomial j.
     * @param i index of pi.
     * @param j index pf pj.
     */
    public CriticalPair(ExpVector e,
                        GenPolynomial<C> pi, GenPolynomial<C> pj, 
                        int i, int j) {
        this.e = e;
        this.pi = pi; 
        this.pj = pj; 
        this.i = i; 
        this.j = j;
        inReduction = false; 
        reductum = null;
    }


    /**
     * toString.
     */
    @Override
     public String toString() {
        StringBuffer s = new StringBuffer("pair( ");
        s.append(e + "," + i);
        if ( pi != null ) {
           s.append("{" + pi.length() + "}");
        }
        s.append("," + j);
        if ( pj != null ) {
           s.append("{" + pj.length() + "}");
        }
        if ( inReduction ) {
           s.append("," + inReduction);
        } 
        if ( reductum != null ) {
           s.append("," + reductum.leadingExpVector());
        } 
        s.append(" )");
        return s.toString();
    }


    /**
     * Set in reduction status.
     * inReduction is set to true.
     */
    public void setInReduction() {
        if ( inReduction ) {
           throw new RuntimeException("already in reduction " + this);
        }
        inReduction = true;
    }


    /**
     * Get in reduction status.
     * @return true if the polynomial is currently in reduction, else false.
     */
    public boolean getInReduction() {
        return inReduction;
    }


    /**
     * Get reduced polynomial.
     * @return the reduced polynomial or null if not done.
     */
    public GenPolynomial<C> getReductum() {
        return reductum;
    }


    /**
     * Set reduced polynomial.
     * @param r the reduced polynomial.
     */
    public void setReductum(GenPolynomial<C> r) {
        if ( r == null ) {
           throw new RuntimeException("reduction null not allowed " + this);
        }
        inReduction = false;
        reductum = r;
    }


    /**
     * Is reduced to zero.
     * @return true if the S-polynomial of this CriticalPair 
     *         was reduced to ZERO, else false.
     */
    public boolean isZERO() {
        if ( reductum == null ) { // not jet done
            return false;
        }
        return reductum.isZERO();
    }


    /**
     * Is reduced to one.
     * @return true if the S-polynomial of this CriticalPair was 
     *         reduced to ONE, else false.
     */
    public boolean isONE() {
        if ( reductum == null ) { // not jet done
            return false;
        }
        return reductum.isONE();
    }

}

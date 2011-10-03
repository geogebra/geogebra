/*
 * $Id: AbelianGroupElem.java 1708 2008-02-24 17:28:36Z kredel $
 */

package edu.jas.structure;


/**
 * Abelian group element interface.
 * Defines the additive methods.
 * @param <C> element type
 * @author Heinz Kredel
 */

public interface AbelianGroupElem<C extends AbelianGroupElem<C>> 
         extends Element<C> {


    /**
     * Test if this is zero.
     * @return true if this is 0, else false.
     */
    public boolean isZERO();


    /**
     * Signum.
     * @return the sign of this.
     */
    public int signum();


    /**
     * Sum of this and S.
     * @param S
     * @return this + S.
     */
    public C sum(C S);
    //public <T extends C> T sum(T S);


    /**
     * Subtract S from this.
     * @param S
     * @return this - S.
     */
    public C subtract(C S);


    /**
     * Negate this.
     * @return - this.
     */
    public C negate();


    /**
     * Absolute value of this.
     * @return |this|.
     */
    public C abs();

}

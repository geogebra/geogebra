/*
 * $Id: RegularRingElem.java 1708 2008-02-24 17:28:36Z kredel $
 */

package edu.jas.structure;


/**
 * Regular ring element interface.
 * Defines idempotent operations and idempotent tests.
 * @param <C> regular ring element type
 * @author Heinz Kredel
 */

public interface RegularRingElem<C extends RegularRingElem<C>> 
                 extends GcdRingElem<C> {


    /* Get component. 
     * Not possible to define in interface.
     * @param i index of component.
     * @return val(i).
    public C get(int i);
     */


    /** Is regular ring element full. 
     * @return If every component is non zero, 
     *         then true is returned, else false.
     */
    public boolean isFull();


    /** Is idempotent. 
     * @return If this is a idempotent element then true is returned, else false.
     */
    public boolean isIdempotent();


    /** Idempotent.  
     * @return S with this*S = this.
     */
    public C idempotent();


    /** Regular ring element idempotent complement.  
     * @return 1-this.idempotent().
     */
    public C idemComplement();


    /** Regular ring element idempotent and.  
     * @param S Product.
     * @return this.idempotent() and S.idempotent().
     */
    public C idempotentAnd(C S);


    /** Regular ring element idempotent or.  
     * @param S Product.
     * @return this.idempotent() or S.idempotent().
     */
    public C idempotentOr(C S);


    /** Regular ring element fill with idempotent.  
     * @param S Product.
     * @return fill this with S.idempotent().
     */
    public C fillIdempotent(C S);


    /** Regular ring element fill with one.  
     * @return fill this with one.
     */
    public C fillOne();

}

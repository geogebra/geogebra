/*
 * $Id: PowerSeries.java 2727 2009-07-09 20:26:27Z kredel $
 */

package edu.jas.ps;


import edu.jas.structure.BinaryFunctor;
import edu.jas.structure.RingElem;
import edu.jas.structure.Selector;
import edu.jas.structure.UnaryFunctor;


/**
 * Power series interface. Adds methods specific to power series.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface PowerSeries<C extends RingElem<C>> {


    /**
     * Leading coefficient.
     * @return first coefficient.
     */
    public C leadingCoefficient();


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public PowerSeries<C> reductum();


    /**
     * Coefficient.
     * @return i-th coefficient.
     */
    public C coefficient(int i);


    /**
     * Prepend a new leading coefficient.
     * @return new power series.
     */
    public PowerSeries<C> prepend(C c);


    /**
     * Shift coefficients.
     * @param k shift index.
     * @return new power series with coefficient(i) = old.coefficient(i+k).
     */
    public UnivPowerSeries<C> shift(int k);


    /**
     * Select elements.
     * @return new power series.
     */
    public PowerSeries<C> select(Selector<? super C> sel);


    /**
     * Map a unary function to this power series.
     * @return new power series. <D extends RingElem<D>>
     */
    public PowerSeries<C> map(UnaryFunctor<? super C, C> f);


    /**
     * Map a binary function to elements of this and another power series.
     * @return new power series. , D extends RingElem<D>
     */
    public <C2 extends RingElem<C2>> PowerSeries<C> zip(BinaryFunctor<? super C, ? super C2, C> f,
            PowerSeries<C2> ps);


    /**
     * Differentiate.
     * @return differentiate(this).
     */
    public PowerSeries<C> differentiate();


    /**
     * Integrate with given constant.
     * @return integrate(this).
     */
    public PowerSeries<C> integrate(final C c);

}

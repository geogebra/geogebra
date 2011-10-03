/*
 * $Id: PowerSeriesMap.java 2153 2008-10-02 19:30:16Z kredel $
 */

package edu.jas.ps;


import edu.jas.structure.RingElem;


/**
 * Power series map interface.
 * Defines method for mapping of power series.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface PowerSeriesMap<C extends RingElem<C>> {


    /*
     * Map.
     * @return new power series resulting from mapping elements of ps.
    public PowerSeries<C> map(PowerSeries<C> ps);
     */


    /**
     * Map.
     * @return new power series resulting from mapping elements of ps.
     */
    public UnivPowerSeries<C> map(UnivPowerSeries<C> ps);

}

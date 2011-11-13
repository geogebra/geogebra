/*
 * $Id: ModularRingFactory.java 2934 2009-12-29 14:34:30Z kredel $
 */

package edu.jas.structure;


import edu.jas.arith.Modular;
import edu.jas.arith.BigInteger;


/**
 * Modular ring factory interface. Defines chinese remainder method and get
 * modul method.
 * @author Heinz Kredel
 */

public interface ModularRingFactory<C extends RingElem<C> & Modular> extends RingFactory<C> {


    /**
     * Return the BigInteger modul for the factory.
     * @return a BigInteger of this.modul.
     */
    public BigInteger getIntegerModul();


    /**
     * Chinese remainder algorithm. Assert c.modul >= a.modul and c.modul *
     * a.modul = this.modul.
     * @param c modular.
     * @param ci inverse of c.modul in ring of a.
     * @param a other ModLong.
     * @return S, with S mod c.modul == c and S mod a.modul == a.
     */
    public C chineseRemainder(C c, C ci, C a);

}

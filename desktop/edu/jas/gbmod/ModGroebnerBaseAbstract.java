/*
 * $Id: ModGroebnerBaseAbstract.java 3190 2010-06-26 20:10:33Z kredel $
 */

package edu.jas.gbmod;

import java.util.List;

//import org.apache.log4j.Logger;

import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;

import edu.jas.gb.GroebnerBase;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.GBFactory;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolynomialList;

import edu.jas.vector.ModuleList;


/**
 * Module Groebner Bases abstract class.
 * Implements Groebner bases and GB test.
 * @author Heinz Kredel
 */

public class ModGroebnerBaseAbstract<C extends GcdRingElem<C>> 
       implements ModGroebnerBase<C> {

    //private static final Logger logger = Logger.getLogger(ModGroebnerBase.class);


/**
 * Used Groebner base algorithm.
 */
    protected final GroebnerBase<C> bb;


/**
 * Constructor.
 */
    public ModGroebnerBaseAbstract() {
        bb = GBFactory.getImplementation();
    }


/**
 * Constructor.
 */
    public ModGroebnerBaseAbstract(RingFactory<C> cf) {
        bb = GBFactory.getImplementation(cf);
    }


/**
 * Module Groebner base test.
 */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        return bb.isGB(modv,F);
    }


/**
 * isGB.
 * @param M a module basis.
 * @return true, if M is a Groebner base, else false.
 */
    public boolean isGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return bb.isGB(modv,F.list);
    }


/**
 * Groebner base using pairlist class.
 */
    public List<GenPolynomial<C>> 
             GB(int modv, List<GenPolynomial<C>> F) {  
        return bb.GB(modv,F);
    }


/**
 * GB.
 * @param M a module basis.
 * @return GB(M), a Groebner base of M.
 */
    public ModuleList<C> GB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }

        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols;
        List<GenPolynomial<C>> G = bb.GB(modv,F.list);
        F = new PolynomialList<C>(F.ring,G);
        N = F.getModuleList(modv);
        return N;
    }

}

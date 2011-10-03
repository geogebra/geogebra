/*
 * $Id: ModSolvableGroebnerBaseAbstract.java 2416 2009-02-07 13:24:32Z kredel $
 */

package edu.jas.gbmod;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


import edu.jas.structure.RingElem;

import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.vector.ModuleList;


/**
 * Module solvable Groebner Bases class.
 * Implements module solvable Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class ModSolvableGroebnerBaseAbstract<C extends RingElem<C>> 
       implements ModSolvableGroebnerBase<C> {

    private static final Logger logger = Logger.getLogger(ModSolvableGroebnerBase.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Used Solvable Groebner base algorithm.
     */
    protected final SolvableGroebnerBase<C> sbb;


    /**
     * Constructor.
     */
    public ModSolvableGroebnerBaseAbstract() {
        sbb = new SolvableGroebnerBaseSeq<C>();
    }


    /**
     * Module left Groebner base test.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean 
           isLeftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.isLeftGB(modv,F);
    }


    /**
     * Module left Groebner base test.
     * @param M a module basis.
     * @return true, if M is a left Groebner base, else false.
     */
    public boolean 
           isLeftGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        return sbb.isLeftGB(modv,F.castToSolvableList());
    }


    /**
     * Left Groebner base using pairlist class.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return leftGB(F) a left Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.leftGB(modv,F);
    }

    /**
     * Left Groebner base using pairlist class.
     * @param M a module basis.
     * @return leftGB(M) a left Groebner base for M.
     */
    public ModuleList<C> 
           leftGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        if ( debug ) {
           logger.info("F left +++++++++++++++++++ \n" + F);
        }
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = sbb.leftGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        if ( debug ) {
           logger.info("G left +++++++++++++++++++ \n" + F);
        }
        N = F.getModuleList(modv);
        return N;
    }



    /**
     * Module twosided Groebner base test.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a twosided Groebner base, else false.
     */
    public boolean 
           isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.isTwosidedGB(modv,F);
    }

    /**
     * Module twosided Groebner base test.
     * @param M a module basis.
     * @return true, if M is a twosided Groebner base, else false.
     */
    public boolean 
           isTwosidedGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        return sbb.isTwosidedGB(modv,F.castToSolvableList());
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return tsGB(F) a twosided Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.twosidedGB(modv,F);
    }

    /**
     * Twosided Groebner base using pairlist class.
     * @param M a module basis.
     * @return tsGB(M) a twosided Groebner base for M.
     */
    public ModuleList<C> 
           twosidedGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        PolynomialList<C> F = M.getPolynomialList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)F.ring;
        int modv = M.cols;
        List<GenSolvablePolynomial<C>> G 
            = sbb.twosidedGB(modv,F.castToSolvableList());
        F = new PolynomialList<C>(sring,G);
        N = F.getModuleList(modv);
        return N;
    }


    /**
     * Module right Groebner base test.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean 
           isRightGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        return sbb.isRightGB(modv,F);
    }


    /**
     * Module right Groebner base test.
     * @param M a module basis.
     * @return true, if M is a right Groebner base, else false.
     */
    public boolean 
           isRightGB(ModuleList<C> M) {  
        if ( M == null || M.list == null ) {
            return true;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return true;
        }
        int modv = M.cols; // > 0  
        PolynomialList<C> F = M.getPolynomialList();
        //System.out.println("F test = " + F);
        return sbb.isRightGB( modv, F.castToSolvableList() );
    }


    /**
     * Right Groebner base using pairlist class.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return rightGB(F) a right Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(int modv, List<GenSolvablePolynomial<C>> F) {  
        if ( modv == 0 ) {
           return sbb.rightGB(modv,F);
        }
        throw new RuntimeException("modv != 0 not jet implemented");
        // return sbb.rightGB(modv,F);
    }


    /**
     * Right Groebner base using pairlist class.
     * @param M a module basis.
     * @return rightGB(M) a right Groebner base for M.
     */
    public ModuleList<C> 
           rightGB(ModuleList<C> M) {  
        ModuleList<C> N = M;
        if ( M == null || M.list == null ) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        if ( debug ) {
           logger.info("M ====================== \n" + M);
        }
        List<List<GenSolvablePolynomial<C>>> mlist = M.castToSolvableList();
        GenSolvablePolynomialRing<C> sring 
            = (GenSolvablePolynomialRing<C>)M.ring;
        GenSolvablePolynomialRing<C> rring = sring.reverse(true); //true
        sring = rring.reverse(true); // true

        List<List<GenSolvablePolynomial<C>>> nlist = 
            new ArrayList<List<GenSolvablePolynomial<C>>>( M.rows );
        for ( List<GenSolvablePolynomial<C>> row : mlist ) {
            List<GenSolvablePolynomial<C>> nrow = 
                new ArrayList<GenSolvablePolynomial<C>>( row.size() );
            for ( GenSolvablePolynomial<C> elem : row ) {
                GenSolvablePolynomial<C> nelem 
                   = (GenSolvablePolynomial<C>)elem.reverse(rring);
                nrow.add( nelem );
            }
            nlist.add( nrow );
        }
        ModuleList<C> rM = new ModuleList<C>( rring, nlist );
        if ( debug ) {
           logger.info("rM -------------------- \n" + rM);
        }
        ModuleList<C> rMg = leftGB( rM );
        if ( debug ) {
           logger.info("rMg -------------------- \n" + rMg);
        }

        mlist = rMg.castToSolvableList();
        nlist = new ArrayList<List<GenSolvablePolynomial<C>>>( rMg.rows );
        for ( List<GenSolvablePolynomial<C>> row : mlist ) {
            List<GenSolvablePolynomial<C>> nrow = 
                new ArrayList<GenSolvablePolynomial<C>>( row.size() );
            for ( GenSolvablePolynomial<C> elem : row ) {
                GenSolvablePolynomial<C> nelem 
                   = (GenSolvablePolynomial<C>)elem.reverse(sring);
                nrow.add( nelem );
            }
            nlist.add( nrow );
        }
        ModuleList<C> Mg = new ModuleList<C>( sring, nlist );
        if ( debug ) {
           logger.info("Mg -------------------- \n" + Mg);
        }
        return Mg;
    }

}

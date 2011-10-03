/*
 * $Id: ModSolvableGroebnerBase.java 2414 2009-02-07 13:04:36Z kredel $
 */

package edu.jas.gbmod;

import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.structure.RingElem;

import edu.jas.vector.ModuleList;


/**
 * Module solvable Groebner Bases interface.
 * Defines modull solvabe Groebner bases and GB test.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface ModSolvableGroebnerBase<C extends RingElem<C>> {


    /**
     * Module left Groebner base test.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean 
           isLeftGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Module left Groebner base test.
     * @param M a module basis.
     * @return true, if M is a left Groebner base, else false.
     */
    public boolean 
           isLeftGB(ModuleList<C> M);


    /**
     * Left Groebner base using pairlist class.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return leftGB(F) a left Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Left Groebner base using pairlist class.
     * @param M a module basis.
     * @return leftGB(M) a left Groebner base for M.
     */
    public ModuleList<C> 
           leftGB(ModuleList<C> M);
 

    /**
     * Module twosided Groebner base test.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a twosided Groebner base, else false.
     */
    public boolean 
           isTwosidedGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Module twosided Groebner base test.
     * @param M a module basis.
     * @return true, if M is a twosided Groebner base, else false.
     */
    public boolean 
           isTwosidedGB(ModuleList<C> M);


    /**
     * Twosided Groebner base using pairlist class.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return tsGB(F) a twosided Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Twosided Groebner base using pairlist class.
     * @param M a module basis.
     * @return tsGB(M) a twosided Groebner base for M.
     */
    public ModuleList<C> 
           twosidedGB(ModuleList<C> M);


    /**
     * Module right Groebner base test.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean 
           isRightGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Module right Groebner base test.
     * @param M a module basis.
     * @return true, if M is a right Groebner base, else false.
     */
    public boolean 
           isRightGB(ModuleList<C> M);


    /**
     * Right Groebner base using pairlist class.
     * @param modv number of modul variables.
     * @param F a module basis.
     * @return rightGB(F) a right Groebner base for F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Right Groebner base using pairlist class.
     * @param M a module basis.
     * @return rightGB(M) a right Groebner base for M.
     */
    public ModuleList<C> 
           rightGB(ModuleList<C> M);

}

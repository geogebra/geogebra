/*
 * $Id: SolvableGroebnerBase.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;


import java.util.List;

import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RingElem;


/**
 * Solvable Groebner Bases interface.
 * Defines methods for left, right and twosided Groebner bases 
 * and left, right and twosided GB tests.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */

public interface SolvableGroebnerBase<C extends RingElem<C>> {


    /**
     * Left Groebner base test.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(List<GenSolvablePolynomial<C>> F);


    /**
     * Left Groebner base test.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Twosided Groebner base test.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(List<GenSolvablePolynomial<C>> Fp);


    /**
     * Twosided Groebner base test.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(int modv, 
                                List<GenSolvablePolynomial<C>> Fp);


    /**
     * Right Groebner base test.
     * @param F solvable polynomial list.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(List<GenSolvablePolynomial<C>> F);


    /**
     * Right Groebner base test.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(int modv, List<GenSolvablePolynomial<C>> F);


    /**
     * Left Groebner base using pairlist class.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
               leftGB(List<GenSolvablePolynomial<C>> F);


    /**
     * Left Groebner base using pairlist class.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftGB(int modv, 
                  List<GenSolvablePolynomial<C>> F);


    /** 
     * Solvable Extended Groebner base using critical pair class.
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C>  
           extLeftGB( List<GenSolvablePolynomial<C>> F );


    /**
     * Solvable Extended Groebner base using critical pair class.
     * @param modv module variable number.
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C> 
           extLeftGB( int modv, 
                      List<GenSolvablePolynomial<C>> F );


    /**
     * Left minimal ordered groebner basis.
     * @param Gp a left Groebner base.
     * @return leftGBmi(F) a minimal left Groebner base of Gp.
     */
    public List<GenSolvablePolynomial<C>> 
           leftMinimalGB(List<GenSolvablePolynomial<C>> Gp);


    /**
     * Twosided Groebner base using pairlist class.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
           twosidedGB(List<GenSolvablePolynomial<C>> Fp);

    /**
     * Twosided Groebner base using pairlist class.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
           twosidedGB(int modv, 
                      List<GenSolvablePolynomial<C>> Fp);


    /**
     * Right Groebner base using opposite ring left GB.
     * @param F solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(List<GenSolvablePolynomial<C>> F);


    /**
     * Right Groebner base using opposite ring left GB.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(int modv, 
                   List<GenSolvablePolynomial<C>> F);


    /**
     * Test if left reduction matrix.
     * @param exgb an SolvableExtendedGB container.
     * @return true, if exgb contains a left reduction matrix, else false.
     */
    public boolean
           isLeftReductionMatrix(SolvableExtendedGB<C> exgb);  


    /**
     * Test if left reduction matrix.
     * @param F a solvable polynomial list.
     * @param G a left Groebner base.
     * @param Mf a possible left reduction matrix.
     * @param Mg a possible left reduction matrix.
     * @return true, if Mg and Mf are left reduction matrices, else false.
     */
    public boolean
           isLeftReductionMatrix(List<GenSolvablePolynomial<C>> F, 
                                 List<GenSolvablePolynomial<C>> G,
                                 List<List<GenSolvablePolynomial<C>>> Mf,  
                                 List<List<GenSolvablePolynomial<C>>> Mg);  

}

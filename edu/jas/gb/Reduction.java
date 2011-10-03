/*
 * $Id: Reduction.java 3187 2010-06-16 22:07:38Z kredel $
 */

package edu.jas.gb;

import java.util.List;

import java.io.Serializable;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial Reduction interface.
 * Defines S-Polynomial, normalform, criterion 4, module criterion
 * and irreducible set.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface Reduction<C extends RingElem<C>> 
                 extends Serializable {


    /**
     * S-Polynomial.
     * @param Ap polynomial.
     * @param Bp polynomial.
     * @return spol(Ap,Bp) the S-polynomial of Ap and Bp.
     */
    public GenPolynomial<C> SPolynomial(GenPolynomial<C> Ap, 
                                        GenPolynomial<C> Bp);


    /**
     * S-Polynomial with recording.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return Spol(Ap, Bp), the S-Polynomial for Ap and Bp.
     */
    public GenPolynomial<C> 
           SPolynomial(List<GenPolynomial<C>> S,
                       int i,
                       GenPolynomial<C> Ap, 
                       int j,
                       GenPolynomial<C> Bp);


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the module S-polynomial(i,j) is required.
     */
    public boolean moduleCriterion(int modv, 
                                   GenPolynomial<C> A, 
                                   GenPolynomial<C> B);


    /**
     * Module criterium.
     * @param modv number of module variables.
     * @param ei ExpVector.
     * @param ej ExpVector.
     * @return true if the module S-polynomial(i,j) is required.
     */
    public boolean moduleCriterion(int modv, ExpVector ei, ExpVector ej);


    /**
     * GB criterium 4.
     * Use only for commutative polynomial rings.
     * @param A polynomial.
     * @param B polynomial.
     * @param e = lcm(ht(A),ht(B))
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
                              GenPolynomial<C> B, 
                              ExpVector e);


    /**
     * GB criterium 4.
     * Use only for commutative polynomial rings.
     * @param A polynomial.
     * @param B polynomial.
     * @return true if the S-polynomial(i,j) is required, else false.
     */
    public boolean criterion4(GenPolynomial<C> A, 
                              GenPolynomial<C> B);


    /**
     * Is top reducible.
     * Condition is lt(B) | lt(A) for some B in F.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is top reducible with respect to P.
     */
    public boolean isTopReducible(List<GenPolynomial<C>> P, 
                                  GenPolynomial<C> A);


    /**
     * Is reducible.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is reducible with respect to P.
     */
    public boolean isReducible(List<GenPolynomial<C>> P, 
                               GenPolynomial<C> A);


    /**
     * Is in Normalform.
     * @param A polynomial.
     * @param P polynomial list.
     * @return true if A is in normalform with respect to P.
     */
    public boolean isNormalform(List<GenPolynomial<C>> P, 
                                GenPolynomial<C> A);


    /**
     * Is in Normalform.
     * @param Pp polynomial list.
     * @return true if each A in Pp is in normalform with respect to Pp\{A}.
     */
    public boolean isNormalform( List<GenPolynomial<C>> Pp );


    /**
     * Normalform.
     * @param A polynomial.
     * @param P polynomial list.
     * @return nf(A) with respect to P.
     */
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> P, 
                                       GenPolynomial<C> A);


    /**
     * Normalform Set.
     * @param Ap polynomial list.
     * @param Pp polynomial list.
     * @return list of nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenPolynomial<C>> normalform(List<GenPolynomial<C>> Pp, 
                                             List<GenPolynomial<C>> Ap);


    /**
     * Normalform with recording.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    public GenPolynomial<C> 
           normalform(List<GenPolynomial<C>> row,
                      List<GenPolynomial<C>> Pp, 
                      GenPolynomial<C> Ap);


    /**
     * Irreducible set.
     * @param Pp polynomial list.
     * @return a list P of polynomials which are in normalform wrt. P and with ideal(Pp) = ideal(P).
     */
    public List<GenPolynomial<C>> irreducibleSet(List<GenPolynomial<C>> Pp);



    /**
     * Is reduction of normal form.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @param Np nf(Pp,Ap), a normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */

    public boolean 
           isReductionNF(List<GenPolynomial<C>> row,
                         List<GenPolynomial<C>> Pp, 
                         GenPolynomial<C> Ap,
                         GenPolynomial<C> Np);

}

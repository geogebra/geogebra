/*
 * $Id: Syzygy.java 2414 2009-02-07 13:04:36Z kredel $
 */

package edu.jas.gbmod;

import java.util.List;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.structure.RingElem;

import edu.jas.vector.ModuleList;
import edu.jas.vector.GenVector;

/**
 * Syzygy interface.
 * Defines Syzygy computations and tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public interface Syzygy<C extends RingElem<C>>  {


    /**
     * Syzygy module from Groebner base.
     * F must be a Groebner base.
     * @param F a Groebner base.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> 
           zeroRelations(List<GenPolynomial<C>> F);


    /**
     * Syzygy module from Groebner base.
     * F must be a Groebner base.
     * @param modv number of module variables.
     * @param F a Groebner base.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> 
           zeroRelations(int modv, List<GenPolynomial<C>> F);


    /**
     * Syzygy module from Groebner base.
     * v must be a Groebner base.
     * @param modv number of module variables.
     * @param v a Groebner base.
     * @return syz(v), a basis for the module of syzygies for v.
     */
    public List<List<GenPolynomial<C>>> 
           zeroRelations(int modv, GenVector<GenPolynomial<C>> v);


    /**
     * Syzygy module from module Groebner base.
     * M must be a module Groebner base.
     * @param M a module Groebner base.
     * @return syz(M), a basis for the module of syzygies for M.
     */
    public ModuleList<C> 
          zeroRelations(ModuleList<C> M);


    /**
     * Test if sysygy.
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of syzygies for F, else false.
     */
    public boolean 
           isZeroRelation(List<List<GenPolynomial<C>>> Z, 
                          List<GenPolynomial<C>> F);


    /**
     * Test if sysygy of modules.
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of syzygies for F, else false.
     */
    public boolean 
           isZeroRelation(ModuleList<C> Z, ModuleList<C> F);


    /**
     * Resolution of a module.
     * Only with direct GBs.
     * @param M a module list of a Groebner basis.
     * @return a resolution of M.
     */
    public List<ResPart<C>>
           resolution(ModuleList<C> M);


    /**
     * Resolution of a polynomial list.
     * Only with direct GBs.
     * @param F a polynomial list of a Groebner basis.
     * @return a resolution of F.
     */
    public List // <ResPart<C>|ResPolPart<C>>
           resolution(PolynomialList<C> F);


    /**
     * Resolution of a polynomial list.
     * @param F a polynomial list of an arbitrary basis.
     * @return a resolution of F.
     */
    public List // <ResPart<C>|ResPolPart<C>>
           resolutionArbitrary(PolynomialList<C> F);


    /**
     * Resolution of a module.
     * @param M a module list of an arbitrary basis.
     * @return a resolution of M.
     */
    public List<ResPart<C>>
           resolutionArbitrary(ModuleList<C> M);


    /**
     * Syzygy module from arbitrary base.
     * @param F a polynomial list.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> 
           zeroRelationsArbitrary(List<GenPolynomial<C>> F);


    /**
     * Syzygy module from arbitrary base.
     * @param modv number of module variables.
     * @param F a polynomial list.
     * @return syz(F), a basis for the module of syzygies for F.
     */
    public List<List<GenPolynomial<C>>> 
           zeroRelationsArbitrary(int modv, List<GenPolynomial<C>> F);


    /**
     * Syzygy module from arbitrary module base.
     * @param M an arbitrary module base.
     * @return syz(M), a basis for the module of syzygies for M.
     */
    public ModuleList<C> 
           zeroRelationsArbitrary(ModuleList<C> M); 

}

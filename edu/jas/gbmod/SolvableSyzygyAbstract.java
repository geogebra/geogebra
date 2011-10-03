/*
 * $Id: SolvableSyzygyAbstract.java 2416 2009-02-07 13:24:32Z kredel $
 */

package edu.jas.gbmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;

import edu.jas.gb.Reduction;
import edu.jas.gb.ReductionSeq;
import edu.jas.gb.SolvableExtendedGB;
import edu.jas.gb.SolvableGroebnerBase;
import edu.jas.gb.SolvableGroebnerBaseSeq;
import edu.jas.gb.SolvableReduction;
import edu.jas.gb.SolvableReductionSeq;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;
import edu.jas.poly.ExpVector;

import edu.jas.vector.ModuleList;
import edu.jas.vector.SolvableBasicLinAlg;


/**
 * Syzygy class for solvable polynomials.
 * Implements Syzygy computations and tests.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class SolvableSyzygyAbstract<C extends RingElem<C>> 
             implements SolvableSyzygy<C> {

    private static final Logger logger = Logger.getLogger(SolvableSyzygyAbstract.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Solvable reduction engine.
     */
    protected SolvableReduction<C> sred;


    /**
     * Reduction engine.
     */
    protected Reduction<C> red;


    /**
     * Linear algebra engine.
     */
    protected SolvableBasicLinAlg<C> sblas;


    /**
     * Constructor.
     */
    public SolvableSyzygyAbstract() {
        red = new ReductionSeq<C>();
        sred = new SolvableReductionSeq<C>();
        sblas = new SolvableBasicLinAlg<C>();
    }


    /**
     * Left syzygy for left Groebner base.
     * @param F a Groebner base.
     * @return leftSyz(F), a basis for the left module of syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> 
           leftZeroRelations(List<GenSolvablePolynomial<C>> F) {  
        return leftZeroRelations(0,F);
    }


    /**
     * Left syzygy for left Groebner base.
     * @param modv number of module variables.
     * @param F a Groebner base.
     * @return leftSyz(F), a basis for the left module of syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>>
        leftZeroRelations(int modv, 
                          List<GenSolvablePolynomial<C>> F) {  
        List<List<GenSolvablePolynomial<C>>> Z 
            = new ArrayList<List<GenSolvablePolynomial<C>>>();
        ArrayList<GenSolvablePolynomial<C>> S 
            = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        for ( int i = 0; i < F.size(); i++ ) {
            S.add( null );
        }
        GenSolvablePolynomial<C> pi, pj, s, h, zero;
        zero = null;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            if ( pi != null && zero == null ) {
                zero = pi.ring.getZERO();
            }
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                //logger.info("p"+i+", p"+j+" = " + pi + ", " +pj);

                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                    continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) continue;
                ArrayList<GenSolvablePolynomial<C>> row 
                    = (ArrayList<GenSolvablePolynomial<C>>)S.clone();

                s = sred.leftSPolynomial( row, i, pi, j, pj );
                //logger.info("row = " + row);
                if ( s.isZERO() ) {
                    Z.add( row );
                    continue;
                }

                h = sred.leftNormalform( row, F, s );
                if ( ! h.isZERO() ) {
                    throw new RuntimeException("Syzygy no leftGB");
                }
                if ( logger.isDebugEnabled() ) {
                    logger.info("row = " + row);
                }
                Z.add( row );
            }
        }
        // set null to zero
        for ( List<GenSolvablePolynomial<C>> vr : Z ) {
            for ( int j = 0; j < vr.size(); j++ ) {
                if ( vr.get(j) == null ) {
                    vr.set(j,zero);
                }
            }
        }
        return Z;
    }


    /**
     * Left syzygy for left module Groebner base.
     * @param M a Groebner base.
     * @return leftSyz(M), a basis for the left module of syzygies for M.
     */
    public ModuleList<C> 
           leftZeroRelations(ModuleList<C> M) {  
        ModuleList<C> N = null;
        if ( M == null || M.list == null) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        GenSolvablePolynomial<C> zero 
           = (GenSolvablePolynomial<C>)M.ring.getZERO();
        //logger.info("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G 
            = leftZeroRelations(modv,F.castToSolvableList());
        if ( G == null ) {
            return N;
        }
        List<List<GenSolvablePolynomial<C>>> Z 
            = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for ( int i = 0; i < G.size(); i++ ) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List<GenSolvablePolynomial<C>> Zi = new ArrayList<GenSolvablePolynomial<C>>();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for ( int j = 0; j < Gi.size(); j++ ) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenSolvablePolynomial<C> p = Gi.get(j);
                if ( p != null ) {
                    Map<ExpVector,GenPolynomial<C>> r = p.contract( M.ring );
                   //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                   if ( r.size() == 0 ) {
                       Zi.add(zero); 
                   } else if ( r.size() == 1 ) {
                       GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>)(r.values().toArray())[0];
                       Zi.add(vi); 
                   } else { // will not happen
                       throw new RuntimeException("Map.size() > 1 = " + r.size());
                   }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add( Zi );
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>)M.ring,Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Test if left syzygy.
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of left syzygies for F, else false.
     */
    public boolean 
           isLeftZeroRelation(List<List<GenSolvablePolynomial<C>>> Z, 
                              List<GenSolvablePolynomial<C>> F) {  
        for ( List<GenSolvablePolynomial<C>> row : Z ) {
            GenSolvablePolynomial<C> p = sblas.leftScalarProduct(row,F);
            if ( p == null ) { 
               continue;
            }
            if ( ! p.isZERO() ) {
                logger.info("is not ZeroRelation = " + p);
                return false;
            }
        }
        return true;
    }


    /**
     * Test if right syzygy.
     * @param Z list of sysygies.
     * @param F a polynomial list.
     * @return true, if Z is a list of right syzygies for F, else false.
     */
    public boolean 
           isRightZeroRelation(List<List<GenSolvablePolynomial<C>>> Z, 
                               List<GenSolvablePolynomial<C>> F) {  
        boolean isit = true;
        for ( List<GenSolvablePolynomial<C>> row : Z ) {
            GenSolvablePolynomial<C> p = sblas.leftScalarProduct(F,row); // param order
            if ( p == null ) { 
               continue;
            }
            if ( ! p.isZERO() ) {
                logger.info("is not ZeroRelation = " + p);
                isit = false;
                return false;
            }
        }
        return isit; //true;
    }


    /**
     * Test if left sysygy of modules
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of left syzygies for F, else false.
     */
    public boolean 
           isLeftZeroRelation(ModuleList<C> Z, 
                              ModuleList<C> F) {  
        if ( Z == null || Z.list == null ) {
            return true;
        }
        for ( List<GenSolvablePolynomial<C>> row : Z.castToSolvableList() ) {
            List<GenSolvablePolynomial<C>> zr = sblas.leftScalarProduct(row,F);
            if ( ! sblas.isZero(zr) ) {
                logger.info("is not ZeroRelation (" + zr.size() + ") = " + zr);
                return false;
            }
        }
        return true;
    }


    /**
     * Test if right sysygy of modules
     * @param Z list of sysygies.
     * @param F a module list.
     * @return true, if Z is a list of right syzygies for F, else false.
     */
    public boolean 
           isRightZeroRelation(ModuleList<C> Z, 
                               ModuleList<C> F) {  
        if ( Z == null || Z.list == null ) {
            return true;
        }
        for ( List<GenSolvablePolynomial<C>> row : Z.castToSolvableList() ) {
            List<GenSolvablePolynomial<C>> zr = sblas.rightScalarProduct(row,F);
            if ( ! sblas.isZero(zr) ) {
                logger.info("is not ZeroRelation (" + zr.size() + ") = " + zr);
                return false;
            }
        }
        return true;
    }


    /**
     * Resolution of a module.
     * Only with direct GBs.
     * @param M a module list of a Groebner basis.
     * @return a resolution of M.
     */
    public List<SolvResPart<C>> 
           resolution(ModuleList<C> M) {  
        List<SolvResPart<C>> R = new ArrayList<SolvResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM;
        ModuleList<C> Z;
        ModSolvableGroebnerBase<C> msbb = new ModSolvableGroebnerBaseAbstract<C>();
        while (true) {
          GM = msbb.leftGB(MM);
          Z = leftZeroRelations(GM);
          R.add( new SolvResPart<C>(MM,GM,Z) );
          if ( Z == null || Z.list == null || Z.list.size() == 0 ) {
              break;
          }
          MM = Z;
        }
        return R;
    }


    /**
     * Resolution of a polynomial list.
     * Only with direct GBs.
     * @param F a polynomial list of a Groebner basis.
     * @return a resolution of F.
     */
    public List // <SolvResPart<C>|SolvResPolPart<C>> 
           resolution(PolynomialList<C> F) {  
        List<List<GenSolvablePolynomial<C>>> Z;
        ModuleList<C> Zm;
        List<GenSolvablePolynomial<C>> G;
        PolynomialList<C> Gl;
        SolvableGroebnerBase<C> sbb = new SolvableGroebnerBaseSeq<C>();

        G = sbb.leftGB( F.castToSolvableList() );
        Z = leftZeroRelations( G );
        Gl = new PolynomialList<C>((GenSolvablePolynomialRing<C>)F.ring, G);
        Zm = new ModuleList<C>((GenSolvablePolynomialRing<C>)F.ring, Z);

        List R = resolution(Zm);
        R.add( 0, new SolvResPolPart<C>( F, Gl, Zm ) );
        return R;
    }


    /**
     * Resolution of a module.
     * @param M a module list of an arbitrary basis.
     * @return a resolution of M.
     */
    public List<SolvResPart<C>> 
           resolutionArbitrary(ModuleList<C> M) {  
        List<SolvResPart<C>> R = new ArrayList<SolvResPart<C>>();
        ModuleList<C> MM = M;
        ModuleList<C> GM = null;
        ModuleList<C> Z;
        //ModSolvableGroebnerBase<C> msbb = new ModSolvableGroebnerBaseAbstract<C>();
        while (true) {
            //GM = msbb.leftGB(MM);
          Z = leftZeroRelationsArbitrary(MM);
          R.add( new SolvResPart<C>(MM,GM,Z) );
          if ( Z == null || Z.list == null || Z.list.size() == 0 ) {
              break;
          }
          MM = Z;
        }
        return R;
    }


    /**
     * Resolution of a polynomial list.
     * @param F a polynomial list of an arbitrary basis.
     * @return a resolution of F.
     */
    public List // <SolvResPart<C>|SolvResPolPart<C>> 
           resolutionArbitrary(PolynomialList<C> F) {  
        List<List<GenSolvablePolynomial<C>>> Z;
        ModuleList<C> Zm;
        //List<GenSolvablePolynomial<C>> G;
        PolynomialList<C> Gl = null;
        //SolvableGroebnerBase<C> sbb = new SolvableGroebnerBaseSeq<C>();

        //G = sbb.leftGB( F.castToSolvableList() );
        Z = leftZeroRelationsArbitrary( F.castToSolvableList() );
        //Gl = new PolynomialList<C>((GenSolvablePolynomialRing<C>)F.ring, G);
        Zm = new ModuleList<C>((GenSolvablePolynomialRing<C>)F.ring, Z);

        List R = resolutionArbitrary(Zm);
        R.add( 0, new SolvResPolPart<C>( F, Gl, Zm ) );
        return R;
    }


    /**
     * Left syzygy module from arbitrary base.
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of left syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> 
           leftZeroRelationsArbitrary(List<GenSolvablePolynomial<C>> F) {  
        return leftZeroRelationsArbitrary(0,F);
    }


    /**
     * Left syzygy module from arbitrary base.
     * @param modv number of module variables.
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of left syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> 
        leftZeroRelationsArbitrary(int modv, List<GenSolvablePolynomial<C>> F) {  
        if ( F == null ) {
            return null; //leftZeroRelations( modv, F );
        }
        if ( F.size() <= 1 ) {
            return leftZeroRelations( modv, F );
        }
        final int lenf = F.size(); 
        SolvableGroebnerBaseSeq<C> sgb = new SolvableGroebnerBaseSeq<C>();
        SolvableExtendedGB<C> exgb = sgb.extLeftGB( F );
        if ( debug ) {
           logger.info("exgb = " + exgb);
        }
        if ( ! sgb.isLeftReductionMatrix(exgb) ) {
           logger.error("is reduction matrix ? false");
        }
        List<GenSolvablePolynomial<C>> G = exgb.G;
        List<List<GenSolvablePolynomial<C>>> G2F = exgb.G2F;
        List<List<GenSolvablePolynomial<C>>> F2G = exgb.F2G;

        List<List<GenSolvablePolynomial<C>>> sg = leftZeroRelations( modv, G );
        GenSolvablePolynomialRing<C> ring = G.get(0).ring;
        ModuleList<C> S = new ModuleList<C>( ring, sg );
        if ( debug ) {
           logger.info("syz = " + S);
        }
        if ( ! isLeftZeroRelation(sg,G) ) {
           logger.error("is syzygy ? false");
        }

        List<List<GenSolvablePolynomial<C>>> sf;
        sf = new ArrayList<List<GenSolvablePolynomial<C>>>( sg.size() );
        //List<GenPolynomial<C>> row;

        for ( List<GenSolvablePolynomial<C>> r : sg ) {
            Iterator<GenSolvablePolynomial<C>> it = r.iterator();
            Iterator<List<GenSolvablePolynomial<C>>> jt = G2F.iterator();

            List<GenSolvablePolynomial<C>> rf;
            rf = new ArrayList<GenSolvablePolynomial<C>>( lenf );
            for ( int m = 0; m < lenf; m++ ) {
                rf.add( ring.getZERO() );
            }
            while ( it.hasNext() && jt.hasNext() ) {
               GenSolvablePolynomial<C> si = it.next();
               List<GenSolvablePolynomial<C>> ai = jt.next();
               //System.out.println("si = " + si);
               //System.out.println("ai = " + ai);
               if ( si == null || ai == null ) {
                  continue;
               }
               List<GenSolvablePolynomial<C>> pi = sblas.leftScalarProduct(si,ai);
               //System.out.println("pi = " + pi);
               rf = sblas.vectorAdd( rf, pi );
            }
            if ( it.hasNext() || jt.hasNext() ) {
               logger.error("leftZeroRelationsArbitrary wrong sizes");
            }
            //System.out.println("\nrf = " + rf + "\n");
            sf.add( rf );
        }
        if ( ! isLeftZeroRelation(sf,F) ) {
           logger.error("is partial syz sf ? false");
        }

        List<List<GenSolvablePolynomial<C>>> M;
        M = new ArrayList<List<GenSolvablePolynomial<C>>>( lenf );
        for ( List<GenSolvablePolynomial<C>> r : F2G ) {
            Iterator<GenSolvablePolynomial<C>> it = r.iterator();
            Iterator<List<GenSolvablePolynomial<C>>> jt = G2F.iterator();

            List<GenSolvablePolynomial<C>> rf;
            rf = new ArrayList<GenSolvablePolynomial<C>>( lenf );
            for ( int m = 0; m < lenf; m++ ) {
                rf.add( ring.getZERO() );
            }
            while ( it.hasNext() && jt.hasNext() ) {
               GenSolvablePolynomial<C> si = it.next();
               List<GenSolvablePolynomial<C>> ai = jt.next();
               //System.out.println("si = " + si);
               //System.out.println("ai = " + ai);
               if ( si == null || ai == null ) {
                  continue;
               }
               //wrong: List<GenSolvablePolynomial<C>> pi = sblas.leftScalarProduct(ai,si);
               List<GenSolvablePolynomial<C>> pi = sblas.leftScalarProduct(si,ai);
               //System.out.println("pi = " + pi);
               rf = sblas.vectorAdd( rf, pi );
            }
            if ( it.hasNext() || jt.hasNext() ) {
               logger.error("zeroRelationsArbitrary wrong sizes");
            }
            //System.out.println("\nMg Mf = " + rf + "\n");
            M.add( rf );
        }
        //ModuleList<C> ML = new ModuleList<C>( ring, M );
        //System.out.println("syz ML = " + ML);
        // debug only:
        //List<GenSolvablePolynomial<C>> F2 = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        /* not true in general
        for ( List<GenSolvablePolynomial<C>> rr: M ) {
            GenSolvablePolynomial<C> rrg = sblas.leftScalarProduct( F, rr );
            F2.add( rrg );
        }
        PolynomialList<C> pF = new PolynomialList<C>( ring, F );
        PolynomialList<C> pF2 = new PolynomialList<C>( ring, F2 );
        if ( ! pF.equals( pF2 ) ) {
           logger.error("is FAB = F ? false");
           //System.out.println("pF  = " + pF.list.size());
           //System.out.println("pF2 = " + pF2.list.size());
        }
        */
        int sflen = sf.size();
        List<List<GenSolvablePolynomial<C>>> M2;
        M2 = new ArrayList<List<GenSolvablePolynomial<C>>>( lenf );
        int i = 0;
        for ( List<GenSolvablePolynomial<C>> ri: M ) {
            List<GenSolvablePolynomial<C>> r2i;
            r2i = new ArrayList<GenSolvablePolynomial<C>>( ri.size() );
            int j = 0;
            for ( GenSolvablePolynomial<C> rij : ri ) {
                GenSolvablePolynomial<C> p = null;
                if ( i == j ) {
                    p = (GenSolvablePolynomial<C>)ring.getONE().subtract( rij );
                } else {
                    if ( rij != null ) {
                       p = (GenSolvablePolynomial<C>)rij.negate();
                    }
                }
                r2i.add( p );
                j++;
            }
            M2.add( r2i );
            if ( ! sblas.isZero( r2i ) ) {
                sf.add( r2i );
            }
            i++;
        }
        ModuleList<C> M2L = new ModuleList<C>( ring, M2 );
        if ( debug ) {
           logger.debug("syz M2L = " + M2L);
        }

        if ( debug ) {
           ModuleList<C> SF = new ModuleList<C>( ring, sf );
           logger.debug("syz sf = " + SF);
           logger.debug("#syz " + sflen + ", " + sf.size());
        }
        if ( ! isLeftZeroRelation(sf,F) ) {
           logger.error("is syz sf ? false");
        }
        return sf;
    }


    /**
     * Left syzygy for arbitrary left module base.
     * @param M an arbitrary base.
     * @return leftSyz(M), a basis for the left module of syzygies for M.
     */
    public ModuleList<C> 
           leftZeroRelationsArbitrary(ModuleList<C> M) {  
        ModuleList<C> N = null;
        if ( M == null || M.list == null) {
            return N;
        }
        if ( M.rows == 0 || M.cols == 0 ) {
            return N;
        }
        GenSolvablePolynomial<C> zero 
           = (GenSolvablePolynomial<C>)M.ring.getZERO();
        //logger.info("zero = " + zero);

        //ModuleList<C> Np = null;
        PolynomialList<C> F = M.getPolynomialList();
        int modv = M.cols; // > 0  
        logger.info("modv = " + modv);
        List<List<GenSolvablePolynomial<C>>> G 
            = leftZeroRelationsArbitrary(modv,F.castToSolvableList());
        if ( G == null ) {
            return N;
        }
        List<List<GenSolvablePolynomial<C>>> Z 
            = new ArrayList<List<GenSolvablePolynomial<C>>>();
        for ( int i = 0; i < G.size(); i++ ) {
            List<GenSolvablePolynomial<C>> Gi = G.get(i);
            List<GenSolvablePolynomial<C>> Zi = new ArrayList<GenSolvablePolynomial<C>>();
            // System.out.println("\nG("+i+") = " + G.get(i));
            for ( int j = 0; j < Gi.size(); j++ ) {
                //System.out.println("\nG("+i+","+j+") = " + Gi.get(j));
                GenSolvablePolynomial<C> p = Gi.get(j);
                if ( p != null ) {
                    Map<ExpVector,GenPolynomial<C>> r = p.contract( M.ring );
                   //System.out.println("map("+i+","+j+") = " + r + ", size = " + r.size() );
                   if ( r.size() == 0 ) {
                       Zi.add(zero); 
                   } else if ( r.size() == 1 ) {
                       GenSolvablePolynomial<C> vi = (GenSolvablePolynomial<C>)(r.values().toArray())[0];
                       Zi.add(vi); 
                   } else { // will not happen
                       throw new RuntimeException("Map.size() > 1 = " + r.size());
                   }
                }
            }
            //System.out.println("\nZ("+i+") = " + Zi);
            Z.add( Zi );
        }
        N = new ModuleList<C>((GenSolvablePolynomialRing<C>)M.ring,Z);
        //System.out.println("\n\nN = " + N);
        return N;
    }


    /**
     * Right syzygy module from arbitrary base.
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> 
           rightZeroRelationsArbitrary(List<GenSolvablePolynomial<C>> F) {  
        return rightZeroRelationsArbitrary(0,F);
    }


    /**
     * Right syzygy module from arbitrary base.
     * @param modv number of module variables.
     * @param F a solvable polynomial list.
     * @return syz(F), a basis for the module of right syzygies for F.
     */
    public List<List<GenSolvablePolynomial<C>>> 
        rightZeroRelationsArbitrary(int modv, List<GenSolvablePolynomial<C>> F) {  
        GenSolvablePolynomialRing<C> ring = null;
        for ( GenSolvablePolynomial<C> p : F ) {
            if ( p != null ) {
                ring = p.ring;
                break;
            }
        }
        List<List<GenSolvablePolynomial<C>>> Z;
        if ( ring == null ) { // all null
           Z = new ArrayList<List<GenSolvablePolynomial<C>>>(1);
           Z.add(F);
           return Z;
        }
        GenSolvablePolynomialRing<C> rring = ring.reverse(true);
        GenSolvablePolynomial<C> q;
        List<GenSolvablePolynomial<C>> rF;
           rF = new ArrayList<GenSolvablePolynomial<C>>( F.size() );
        for ( GenSolvablePolynomial<C> p : F ) {
            if ( p != null ) {
               q = (GenSolvablePolynomial<C>)p.reverse(rring);
               rF.add( q );
            }
        }
        if ( true || debug ) {
           PolynomialList<C> pl = new PolynomialList<C>(rring,rF);
           //logger.info("reversed problem = " + pl);
           System.out.println("reversed problem = " + pl);
        }
        List<List<GenSolvablePolynomial<C>>> rZ = leftZeroRelationsArbitrary(modv,rF);
        if ( debug ) {
           boolean isit = isLeftZeroRelation(rZ,rF);
           logger.debug("isLeftZeroRelation = " + isit);
        }
        GenSolvablePolynomialRing<C> oring = rring.reverse(true);
        if ( debug ) {
           logger.debug("ring == oring: " + ring.equals(oring) );
        }
        ring = oring;
        Z = new ArrayList<List<GenSolvablePolynomial<C>>>( rZ.size() );
        for ( List<GenSolvablePolynomial<C>> z : rZ ) {
            if ( z == null ) {
               continue;
            }
            List<GenSolvablePolynomial<C>> s;
                s = new ArrayList<GenSolvablePolynomial<C>>( z.size() );
            for ( GenSolvablePolynomial<C> p : z ) {
               if ( p != null ) {
                  q = (GenSolvablePolynomial<C>)p.reverse(ring);
                  s.add( q );
               }
            }
            Z.add( s );
        }
        return Z;
    }

}


/**
 * Container for module resolution components.
 * @param <C> coefficient type
 */
class SolvResPart<C extends RingElem<C>> implements Serializable {

    public final ModuleList<C> module;
    public final ModuleList<C> GB;
    public final ModuleList<C> syzygy;

    /**
     * SolvResPart.
     * @param m a module list.
     * @param g a module list GB.
     * @param z a syzygy module list.
     */
    public SolvResPart(ModuleList<C> m, ModuleList<C> g, ModuleList<C> z) {
        module = m;
        GB = g;
        syzygy = z;
    }


    /**
     * toString.
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("SolvResPart(\n");
        s.append("module = " + module);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }
}


/**
 * Container for polynomial resolution components.
 * @param <C> coefficient type
 */
class SolvResPolPart<C extends RingElem<C>> implements Serializable {

    public final PolynomialList<C> ideal;
    public final PolynomialList<C> GB;
    public final ModuleList<C> syzygy;

/**
 * SolvResPolPart.
 * @param m a polynomial list.
 * @param g a polynomial list GB.
 * @param z a syzygy module list.
 */
    public SolvResPolPart(PolynomialList<C> m, PolynomialList<C> g, 
                          ModuleList<C> z) {
        ideal = m;
        GB = g;
        syzygy = z;
    }


/**
 * toString.
 */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("SolvResPolPart(\n");
        s.append("ideal = " + ideal);
        s.append("\n GB = " + GB);
        s.append("\n syzygy = " + syzygy);
        s.append(")");
        return s.toString();
    }

}


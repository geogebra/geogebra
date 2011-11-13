/*
 * $Id: GroebnerBaseAbstract.java 3190 2010-06-26 20:10:33Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;
import edu.jas.poly.ExpVector;
import edu.jas.vector.BasicLinAlg;


/**
 * Groebner Bases abstract class.
 * Implements common Groebner bases and GB test methods.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class GroebnerBaseAbstract<C extends RingElem<C>> 
                      implements GroebnerBase<C> {

    private static final Logger logger = Logger.getLogger(GroebnerBaseAbstract.class);
    private final boolean debug = logger.isDebugEnabled();


    /**
     * Reduction engine.
     */
    public final Reduction<C> red;


    /**
     * linear algebra engine.
     */
    public final BasicLinAlg<C> blas;


    /**
     * Constructor.
     */
    public GroebnerBaseAbstract() {
        this( new ReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red Reduction engine
     */
    public GroebnerBaseAbstract(Reduction<C> red) {
        this.red = red;
        blas = new BasicLinAlg<C>();
    }


    /**
     * Groebner base test.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(List<GenPolynomial<C>> F) {  
        return isGB(0,F);
    }


    /**
     * Groebner base test.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return true, if F is a Groebner base, else false.
     */
    public boolean isGB(int modv, List<GenPolynomial<C>> F) {  
        if ( F == null ) {
           return true;
        }
        GenPolynomial<C> pi, pj, s, h;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                if ( ! red.criterion4( pi, pj ) ) { 
                   continue;
                }
                s = red.SPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = red.normalform( F, s );
                if ( ! h.isZERO() ) {
                   System.out.println("pi = " + pi + ", pj = " + pj);
                   System.out.println("s  = " + s  + ", h = " + h);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Groebner base using pairlist class.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    public List<GenPolynomial<C>> 
             GB( List<GenPolynomial<C>> F ) {  
        return GB(0,F);
    }


    /** 
     * Extended Groebner base using critical pair class.
     * @param F polynomial list.
     * @return a container for a Groebner base G of F together with back-and-forth transformations.
     */
    public ExtendedGB<C>  
                  extGB( List<GenPolynomial<C>> F ) {
        return extGB(0,F); 
    }


    /**
     * Extended Groebner base using critical pair class.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return a container for a Groebner base G of F together with back-and-forth transformations.
     */
    public ExtendedGB<C> 
           extGB( int modv, 
                  List<GenPolynomial<C>> F ) {
        throw new RuntimeException("extGB not implemented in " 
                                   + this.getClass().getSimpleName());
    }


    /**
     * Minimal ordered Groebner basis.
     * @param Gp a Groebner base.
     * @return a reduced Groebner base of Gp.
     */
    public List<GenPolynomial<C>> 
                minimalGB(List<GenPolynomial<C>> Gp) {  
        if ( Gp == null || Gp.size() <= 1 ) {
            return Gp;
        }
        // remove zero polynomials
        List<GenPolynomial<C>> G
            = new ArrayList<GenPolynomial<C>>( Gp.size() );
        for ( GenPolynomial<C> a : Gp ) { 
            if ( a != null && !a.isZERO() ) { // always true in GB()
               // already positive a = a.abs();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }
        // remove top reducible polynomials
        GenPolynomial<C> a;
        List<GenPolynomial<C>> F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            if ( red.isTopReducible(G,a) || red.isTopReducible(F,a) ) {
               // drop polynomial 
               if ( debug ) {
                  System.out.println("dropped " + a);
                  List<GenPolynomial<C>> ff;
                  ff = new ArrayList<GenPolynomial<C>>( G );
                  ff.addAll(F);
                  a = red.normalform( ff, a );
                  if ( !a.isZERO() ) {
                     System.out.println("error, nf(a) " + a);
                  }
               }
            } else {
                F.add(a);
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }
        // reduce remaining polynomials
        int len = G.size();
        int i = 0;
        while ( i < len ) {
            a = G.remove(0);
            //System.out.println("doing " + a.length());
            a = red.normalform( G, a );
            //a = red.normalform( F, a );
            G.add( a ); // adds as last
            i++;
        }
        return G;
    }


    /**
     * Test if reduction matrix.
     * @param exgb an ExtendedGB container.
     * @return true, if exgb contains a reduction matrix, else false.
     */
    public boolean
           isReductionMatrix(ExtendedGB<C> exgb) {  
        if ( exgb == null ) {
            return true;
        }
        return isReductionMatrix(exgb.F,exgb.G,exgb.F2G,exgb.G2F);
    }


    /**
     * Test if reduction matrix.
     * @param F a polynomial list.
     * @param G a Groebner base.
     * @param Mf a possible reduction matrix.
     * @param Mg a possible reduction matrix.
     * @return true, if Mg and Mf are reduction matrices, else false.
     */
    public boolean
           isReductionMatrix(List<GenPolynomial<C>> F, 
                             List<GenPolynomial<C>> G,
                             List<List<GenPolynomial<C>>> Mf,  
                             List<List<GenPolynomial<C>>> Mg) {  
        // no more check G and Mg: G * Mg[i] == 0
        // check F and Mg: F * Mg[i] == G[i]
        int k = 0;
        for ( List<GenPolynomial<C>> row : Mg ) {
            boolean t = red.isReductionNF( row, F, G.get( k ), null );  
            if ( ! t ) {
               logger.error("F isReductionMatrix s, k = " + F.size() + ", " + k);
               return false;
            }
            k++;
        }
        // check G and Mf: G * Mf[i] == F[i]
        k = 0;
        for ( List<GenPolynomial<C>> row : Mf ) {
            boolean t = red.isReductionNF( row, G, F.get( k ), null );  
            if ( ! t ) {
               logger.error("G isReductionMatrix s, k = " + G.size() + ", " + k);
               return false;
            }
            k++;
        }
        return true;
    }


    /**
     * Normalize M.
     * Make all rows the same size and make certain column elements zero.
     * @param M a reduction matrix.
     * @return normalized M.
     */
    public List<List<GenPolynomial<C>>> 
           normalizeMatrix(int flen, List<List<GenPolynomial<C>>> M) {  
        if ( M == null ) {
            return M;
        }
        if ( M.size() == 0 ) {
            return M;
        }
        List<List<GenPolynomial<C>>> N = new ArrayList<List<GenPolynomial<C>>>();
        List<List<GenPolynomial<C>>> K = new ArrayList<List<GenPolynomial<C>>>();
        int len = M.get(  M.size()-1 ).size(); // longest row
        // pad / extend rows
        for ( List<GenPolynomial<C>> row : M ) {
            List<GenPolynomial<C>> nrow = new ArrayList<GenPolynomial<C>>( row );
            for ( int i = row.size(); i < len; i++ ) {
                nrow.add( null );
            }
            N.add( nrow );
        }
        // System.out.println("norm N fill = " + N);
        // make zero columns
        int k = flen;
        for ( int i = 0; i < N.size(); i++ ) { // 0
            List<GenPolynomial<C>> row = N.get( i );
            if ( debug ) {
               logger.info("row = " + row);
            }
            K.add( row );
            if ( i < flen ) { // skip identity part
               continue;
            }
            List<GenPolynomial<C>> xrow;
            GenPolynomial<C> a;
            //System.out.println("norm i = " + i);
            for ( int j = i+1; j < N.size(); j++ ) {
                List<GenPolynomial<C>> nrow = N.get( j );
                //System.out.println("nrow j = " +j + ", " + nrow);
                if ( k < nrow.size() ) { // always true
                   a = nrow.get( k );
                   //System.out.println("k, a = " + k + ", " + a);
                   if ( a != null && !a.isZERO() ) {
                      xrow = blas.scalarProduct( a, row);
                      xrow = blas.vectorAdd(xrow,nrow);
                      //System.out.println("xrow = " + xrow);
                      N.set( j, xrow );
                   }
                }
            }
            k++;
        }
        //System.out.println("norm K reduc = " + K);
        // truncate 
        N.clear();
        for ( List<GenPolynomial<C>> row: K ) {
            List<GenPolynomial<C>> tr = new ArrayList<GenPolynomial<C>>();
            for ( int i = 0; i < flen; i++ ) {
                tr.add( row.get(i) );
            }
            N.add( tr );
        }
        K = N;
        //System.out.println("norm K trunc = " + K);
        return K;
    }


    /**
     * Minimal extended groebner basis.
     * @param Gp a Groebner base.
     * @param M a reduction matrix, is modified.
     * @return a (partially) reduced Groebner base of Gp in a container.
     */
    public ExtendedGB<C> 
        minimalExtendedGB(int flen,
                          List<GenPolynomial<C>> Gp,
                          List<List<GenPolynomial<C>>> M) {  
        if ( Gp == null ) {
        return null; //new ExtendedGB<C>(null,Gp,null,M);
        }
        if ( Gp.size() <= 1 ) {
           return new ExtendedGB<C>(null,Gp,null,M);
        }
        List<GenPolynomial<C>> G;
        List<GenPolynomial<C>> F;
        G = new ArrayList<GenPolynomial<C>>( Gp );
        F = new ArrayList<GenPolynomial<C>>( Gp.size() );

        List<List<GenPolynomial<C>>> Mg;
        List<List<GenPolynomial<C>>> Mf;
        Mg = new ArrayList<List<GenPolynomial<C>>>( M.size() );
        Mf = new ArrayList<List<GenPolynomial<C>>>( M.size() );
        List<GenPolynomial<C>> row;
        for ( List<GenPolynomial<C>> r : M ) {
            // must be copied also
            row = new ArrayList<GenPolynomial<C>>( r );
            Mg.add( row );
        }
        row = null;

        GenPolynomial<C> a;
        ExpVector e;        
        ExpVector f;        
        GenPolynomial<C> p;
        boolean mt;
        ListIterator<GenPolynomial<C>> it;
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> jx = new ArrayList<Integer>();
        int k = 0;
        //System.out.println("flen, Gp, M = " + flen + ", " + Gp.size() + ", " + M.size() );
        while ( G.size() > 0 ) {
            a = G.remove(0);
            e = a.leadingExpVector();

            it = G.listIterator();
            mt = false;
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt =  e.multipleOf( f );
            }
            it = F.listIterator();
            while ( it.hasNext() && ! mt ) {
               p = it.next();
               f = p.leadingExpVector();
               mt =  e.multipleOf( f );
            }
            //System.out.println("k, mt = " + k + ", " + mt);
            if ( ! mt ) {
               F.add( a );
               ix.add( k );
            } else { // drop polynomial and corresponding row and column
               // F.add( a.ring.getZERO() );
               jx.add( k );
            }
            k++;
        }
        if ( debug ) {
           logger.debug("ix, #M, jx = " + ix + ", " + Mg.size() + ", " + jx);
        }
        int fix = -1; // copied polys
        // copy Mg to Mf as indicated by ix
        for ( int i = 0; i < ix.size(); i++ ) {
            int u = ix.get(i); 
            if ( u >= flen && fix == -1 ) {
               fix = Mf.size();
            }
            //System.out.println("copy u, fix = " + u + ", " + fix);
            if ( u >= 0 ) {
               row = Mg.get( u );
               Mf.add( row );
            }
        }
        if ( F.size() <= 1 || fix == -1 ) {
           return new ExtendedGB<C>(null,F,null,Mf);
        }
        // must return, since extended normalform has not correct order of polys
        /*
        G = F;
        F = new ArrayList<GenPolynomial<C>>( G.size() );
        List<GenPolynomial<C>> temp;
        k = 0;
        final int len = G.size();
        while ( G.size() > 0 ) {
            a = G.remove(0);
            if ( k >= fix ) { // dont touch copied polys
               row = Mf.get( k );
               //System.out.println("doing k = " + k + ", " + a);
               // must keep order, but removed polys missing
               temp = new ArrayList<GenPolynomial<C>>( len );
               temp.addAll( F );
               temp.add( a.ring.getZERO() ); // ??
               temp.addAll( G );
               //System.out.println("row before = " + row);
               a = red.normalform( row, temp, a );
               //System.out.println("row after  = " + row);
            }
            F.add( a );
            k++;
        }
        // does Mf need renormalization?
        */
        return new ExtendedGB<C>(null,F,null,Mf);
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    public void terminate() {
        logger.info("terminate not implemented");
    }


    /**
     * Cancel ThreadPool.
     */
    public int cancel() {
        logger.info("cancel not implemented");
        return 0;
    }

}



/*
 * $Id: SolvableGroebnerBaseAbstract.java 3074 2010-04-14 21:49:20Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.GenSolvablePolynomialRing;
import edu.jas.poly.PolynomialList;

import edu.jas.structure.RingElem;


/**
 * Solvable Groebner Bases abstract class.
 * Implements common left, right and twosided Groebner bases 
 * and left, right and twosided GB tests.
 * @param <C> coefficient type
 * @author Heinz Kredel.
 */

public abstract class SolvableGroebnerBaseAbstract<C extends RingElem<C>> 
       implements SolvableGroebnerBase<C> {

    private static final Logger logger = Logger.getLogger(SolvableGroebnerBaseAbstract.class);
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
     * Constructor.
     */
    public SolvableGroebnerBaseAbstract() {
        this( new ReductionSeq<C>(), new SolvableReductionSeq<C>() );
    }


    /**
     * Constructor.
     * @param red Reduction engine
     * @param sred Solvable reduction engine
     */
    public SolvableGroebnerBaseAbstract(Reduction<C> red,
                                        SolvableReduction<C> sred) {
        this.red = red;
        this.sred = sred;
    }


    /**
     * Left Groebner base test.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(List<GenSolvablePolynomial<C>> F) {  
        return isLeftGB(0,F);
    }


    /**
     * Left Groebner base test.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return true, if F is a left Groebner base, else false.
     */
    public boolean isLeftGB(int modv, 
                            List<GenSolvablePolynomial<C>> F) {  
        GenSolvablePolynomial<C> pi, pj, s, h;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.leftSPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = sred.leftNormalform( F, s );
                if ( ! h.isZERO() ) {
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Twosided Groebner base test.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(List<GenSolvablePolynomial<C>> Fp) {  
        return isTwosidedGB(0,Fp);
    }


    /**
     * Twosided Groebner base test.
     * @param modv number of module variables.
     * @param Fp solvable polynomial list.
     * @return true, if Fp is a two-sided Groebner base, else false.
     */
    public boolean isTwosidedGB(int modv, 
                                List<GenSolvablePolynomial<C>> Fp) {
        if ( Fp == null || Fp.size() == 0 ) { // 0 not 1
            return true;
        }
        GenSolvablePolynomialRing<C> fac = Fp.get(0).ring; // assert != null
        //List<GenSolvablePolynomial<C>> X = generateUnivar( modv, Fp );
        List<GenSolvablePolynomial<C>> X = fac.univariateList( modv );
        List<GenSolvablePolynomial<C>> F 
            = new ArrayList<GenSolvablePolynomial<C>>( Fp.size() * (1+X.size()) );
        F.addAll( Fp );
        GenSolvablePolynomial<C> p, x, pi, pj, s, h;
        for ( int i = 0; i < Fp.size(); i++ ) {
            p = Fp.get(i);
            for ( int j = 0; j < X.size(); j++ ) {
                x = X.get(j);
                p = p.multiply( x );
                F.add( p );
            }
        }
        //System.out.println("F to check = " + F);
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.leftSPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                h = sred.leftNormalform( F, s );
                if ( ! h.isZERO() ) {
                   logger.info("is not TwosidedGB: " + h);
                   return false;
                }
            }
        }
        return true;
    }


    /**
     * Right Groebner base test.
     * @param F solvable polynomial list.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(List<GenSolvablePolynomial<C>> F) {
        return isRightGB(0,F);
    }


    /**
     * Right Groebner base test.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return true, if F is a right Groebner base, else false.
     */
    public boolean isRightGB(int modv, List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomial<C> pi, pj, s, h;
        for ( int i = 0; i < F.size(); i++ ) {
            pi = F.get(i);
            //System.out.println("pi right = " + pi);
            for ( int j = i+1; j < F.size(); j++ ) {
                pj = F.get(j);
                //System.out.println("pj right = " + pj);
                if ( ! red.moduleCriterion( modv, pi, pj ) ) {
                   continue;
                }
                // if ( ! red.criterion4( pi, pj ) ) { continue; }
                s = sred.rightSPolynomial( pi, pj );
                if ( s.isZERO() ) {
                   continue;
                }
                //System.out.println("s right = " + s);
                h = sred.rightNormalform( F, s );
                if ( ! h.isZERO() ) {
                   logger.info("isRightGB non zero h = " + h);
                   return false;
                } else {
                    //logger.info("isRightGB zero h = " + h);
                }
            }
        }
        return true;
    }


    /**
     * Left Groebner base using pairlist class.
     * @param F solvable polynomial list.
     * @return leftGB(F) a left Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           leftGB(List<GenSolvablePolynomial<C>> F) {  
        return leftGB(0,F);
    }


    /** 
     * Solvable Extended Groebner base using critical pair class.
     * @param F solvable polynomial list.
     * @return a container for an extended left Groebner base of F.
     */
    public SolvableExtendedGB<C>  
           extLeftGB( List<GenSolvablePolynomial<C>> F ) {
        return extLeftGB(0,F); 
    }


    /**
     * Left minimal ordered groebner basis.
     * @param Gp a left Groebner base.
     * @return leftGBmi(F) a minimal left Groebner base of Gp.
     */
    public List<GenSolvablePolynomial<C>> 
               leftMinimalGB(List<GenSolvablePolynomial<C>> Gp) {  
        ArrayList<GenSolvablePolynomial<C>> G 
           = new ArrayList<GenSolvablePolynomial<C>>();
        ListIterator<GenSolvablePolynomial<C>> it = Gp.listIterator();
        for ( GenSolvablePolynomial<C> a: Gp ) { 
            // a = (SolvablePolynomial) it.next();
            if ( a.length() != 0 ) { // always true
               // already monic a = a.monic();
               G.add( a );
            }
        }
        if ( G.size() <= 1 ) {
           return G;
        }

        ExpVector e;        
        ExpVector f;        
        GenSolvablePolynomial<C> a, p;
        ArrayList<GenSolvablePolynomial<C>> F 
           = new ArrayList<GenSolvablePolynomial<C>>();
        boolean mt;

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
            if ( ! mt ) {
                F.add( a );
            } else {
                // System.out.println("dropped " + a.length());
            }
        }
        G = F;
        if ( G.size() <= 1 ) {
           return G;
        }

        F = new ArrayList<GenSolvablePolynomial<C>>();
        while ( G.size() > 0 ) {
            a = G.remove(0);
            // System.out.println("doing " + a.length());
            a = sred.leftNormalform( G, a );
            a = sred.leftNormalform( F, a );
            F.add( a );
        }
        return F;
    }


    /**
     * Twosided Groebner base using pairlist class.
     * @param Fp solvable polynomial list.
     * @return tsGB(Fp) a twosided Groebner base of Fp.
     */
    public List<GenSolvablePolynomial<C>> 
               twosidedGB(List<GenSolvablePolynomial<C>> Fp) {  
        return twosidedGB(0,Fp);
    }


    /**
     * Right Groebner base using opposite ring left GB.
     * @param F solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(List<GenSolvablePolynomial<C>> F) {  
        return rightGB(0,F);
    }


    /**
     * Right Groebner base using opposite ring left GB.
     * @param modv number of module variables.
     * @param F solvable polynomial list.
     * @return rightGB(F) a right Groebner base of F.
     */
    public List<GenSolvablePolynomial<C>> 
           rightGB(int modv, 
                   List<GenSolvablePolynomial<C>> F) {
        GenSolvablePolynomialRing<C> ring = null;
        for ( GenSolvablePolynomial<C> p : F ) {
            if ( p != null ) {
                ring = p.ring;
                break;
            }
        }
        if ( ring == null ) {
            return F;
        }
        GenSolvablePolynomialRing<C> rring = ring.reverse(true); //true
        //ring = rring.reverse(true); // true
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
           logger.info("reversed problem = " + pl);
        }
        List<GenSolvablePolynomial<C>> rG = leftGB( modv, rF );
        if ( true || debug ) {
            //PolynomialList<C> pl = new PolynomialList<C>(rring,rG);
            //logger.info("reversed GB = " + pl);
            long t = System.currentTimeMillis();
            boolean isit = isLeftGB( rG );
            t = System.currentTimeMillis() - t;
            logger.info("is left GB = " + isit + ", in " + t + " milliseconds");
        }
        ring = rring.reverse(true); // true
        List<GenSolvablePolynomial<C>> G = new ArrayList<GenSolvablePolynomial<C>>(rG.size());
        for ( GenSolvablePolynomial<C> p : rG ) {
            if ( p != null ) {
               q = (GenSolvablePolynomial<C>)p.reverse(ring);
               G.add( q );
            }
        }
        if ( true || debug ) {
            //PolynomialList<C> pl = new PolynomialList<C>(ring,G);
            //logger.info("GB = " + pl);
            long t = System.currentTimeMillis();
            boolean isit = isRightGB( G );
            t = System.currentTimeMillis() - t;
            logger.info("is right GB = " + isit + ", in " + t + " milliseconds");
        }
        return G;
    }


    /**
     * Test if left reduction matrix.
     * @param exgb an SolvableExtendedGB container.
     * @return true, if exgb contains a left reduction matrix, else false.
     */
    public boolean
           isLeftReductionMatrix(SolvableExtendedGB<C> exgb) {  
        if ( exgb == null ) {
            return true;
        }
        return isLeftReductionMatrix(exgb.F,exgb.G,exgb.F2G,exgb.G2F);
    }


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
                                 List<List<GenSolvablePolynomial<C>>> Mg) {  
        // no more check G and Mg: G * Mg[i] == 0
        // check F and Mg: F * Mg[i] == G[i]
        int k = 0;
        for ( List<GenSolvablePolynomial<C>> row : Mg ) {
            boolean t = sred.isLeftReductionNF( row, F, G.get( k ), null );  
            if ( ! t ) {
               logger.error("F isLeftReductionMatrix s, k = " + F.size() + ", " + k);
               return false;
            }
            k++;
        }
        // check G and Mf: G * Mf[i] == F[i]
        k = 0;
        for ( List<GenSolvablePolynomial<C>> row : Mf ) {
            boolean t = sred.isLeftReductionNF( row, G, F.get( k ), null );  
            if ( ! t ) {
               logger.error("G isLeftReductionMatrix s, k = " + G.size() + ", " + k);
               return false;
            }
            k++;
        }
        return true;
    }

}

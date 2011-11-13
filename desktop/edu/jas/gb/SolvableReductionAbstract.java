/*
 * $Id: SolvableReductionAbstract.java 2412 2009-02-07 12:17:54Z kredel $
 */

package edu.jas.gb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;
import edu.jas.poly.GenSolvablePolynomial;

import edu.jas.structure.RingElem;


/**
 * Solvable polynomial Reduction abstract class.
 * Implements common left, right S-Polynomial, left normalform and 
 * left irreducible set.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public abstract class SolvableReductionAbstract<C extends RingElem<C>>
                      implements SolvableReduction<C> {

    private static final Logger logger = Logger.getLogger(SolvableReductionAbstract.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * Constructor.
     */
    public SolvableReductionAbstract() {
    }


    /**
     * Left S-Polynomial.
     * @param Ap solvable polynomial.
     * @param Bp solvable polynomial.
     * @return left-spol(Ap,Bp) the left S-polynomial of Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
           leftSPolynomial(GenSolvablePolynomial<C> Ap, 
                           GenSolvablePolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
           if ( Bp == null || Bp.isZERO() ) {
               if ( Ap != null ) {
                  return Ap.ring.getZERO(); 
               } else {
                  return null;
               }
           }
           if ( Ap == null || Ap.isZERO() ) {
              return Bp.ring.getZERO(); 
           }
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("rings not equal"); 
           }
        }
        Map.Entry<ExpVector,C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector,C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        GenSolvablePolynomial<C> App = Ap.multiplyLeft( b, e1 );
        GenSolvablePolynomial<C> Bpp = Bp.multiplyLeft( a, f1 );
        GenSolvablePolynomial<C> Cp = (GenSolvablePolynomial<C>) App.subtract(Bpp);
        return Cp;
    }


    /**
     * S-Polynomial with recording.
     * @param S recording matrix, is modified.
     * @param i index of Ap in basis list.
     * @param Ap a polynomial.
     * @param j index of Bp in basis list.
     * @param Bp a polynomial.
     * @return leftSpol(Ap, Bp), the left S-Polynomial for Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
        leftSPolynomial(List<GenSolvablePolynomial<C>> S,
                        int i,
                        GenSolvablePolynomial<C> Ap, 
                        int j,
                        GenSolvablePolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
            if ( Bp == null || Bp.isZERO() ) {
                throw new RuntimeException("Spol B is zero");
            }
            if ( Ap == null || Ap.isZERO() ) {
                throw new RuntimeException("Spol A is zero");
            }
            if ( ! Ap.ring.equals( Bp.ring ) ) { 
                logger.error("rings not equal"); 
            }
        }
        Map.Entry<ExpVector,C> ma = Ap.leadingMonomial();
        Map.Entry<ExpVector,C> mb = Bp.leadingMonomial();

        ExpVector e = ma.getKey();
        ExpVector f = mb.getKey();

        ExpVector g  = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        C a = ma.getValue();
        C b = mb.getValue();

        GenSolvablePolynomial<C> App = Ap.multiplyLeft( b, e1 );
        GenSolvablePolynomial<C> Bpp = Bp.multiplyLeft( a, f1 );
        GenSolvablePolynomial<C> Cp = (GenSolvablePolynomial<C>)App.subtract(Bpp);

        GenSolvablePolynomial<C> zero = Ap.ring.getZERO();
        GenSolvablePolynomial<C> As = (GenSolvablePolynomial<C>)zero.sum( b.negate(), e1 );
        GenSolvablePolynomial<C> Bs = (GenSolvablePolynomial<C>)zero.sum( a, f1 );
        S.set( i, As );
        S.set( j, Bs );
        return Cp;
    }


    /**
     * Left Normalform Set.
     * @param Ap solvable polynomial list.
     * @param Pp solvable polynomial list.
     * @return list of left-nf(a) with respect to Pp for all a in Ap.
     */
    public List<GenSolvablePolynomial<C>> 
           leftNormalform(List<GenSolvablePolynomial<C>> Pp, 
                          List<GenSolvablePolynomial<C>> Ap) {  
        if ( Pp == null || Pp.isEmpty() ) {
           return Ap;
        }
        if ( Ap == null || Ap.isEmpty() ) {
           return Ap;
        }
        ArrayList<GenSolvablePolynomial<C>> red 
           = new ArrayList<GenSolvablePolynomial<C>>();
        for ( GenSolvablePolynomial<C> A : Ap ) {
            A = leftNormalform( Pp, A );
            red.add( A );
        }
        return red;
    }


    /**
     * Left irreducible set.
     * @param Pp solvable polynomial list.
     * @return a list P of solvable polynomials which are in normalform wrt. P.
     */
    public List<GenSolvablePolynomial<C>> 
           leftIrreducibleSet(List<GenSolvablePolynomial<C>> Pp) {  
        ArrayList<GenSolvablePolynomial<C>> P 
           = new ArrayList<GenSolvablePolynomial<C>>();
        for ( GenSolvablePolynomial<C> a : Pp ) {
            if ( a.length() != 0 ) {
               a = (GenSolvablePolynomial<C>)a.monic();
               P.add( a );
            }
        }
        int l = P.size();
        if ( l <= 1 ) return P;

        int irr = 0;
        ExpVector e;        
        ExpVector f;        
        GenSolvablePolynomial<C> a;
        Iterator<GenSolvablePolynomial<C>> it;
        logger.debug("irr = ");
        while ( irr != l ) {
            it = P.listIterator(); 
            a = it.next();
            P.remove(0);
            e = a.leadingExpVector();
            a = leftNormalform( P, a );
            logger.debug(String.valueOf(irr));
            if ( a.length() == 0 ) { l--;
               if ( l <= 1 ) { return P; }
            } else {
               f = a.leadingExpVector();
               if (  f .signum() == 0 ) { 
                  P = new ArrayList<GenSolvablePolynomial<C>>(); 
                  P.add( (GenSolvablePolynomial<C>)a.monic() ); 
                  return P;
               }    
               if ( e.equals( f ) ) {
                  irr++;
               } else {
                  irr = 0; a = (GenSolvablePolynomial<C>)a.monic();
               }
               P.add( a );
            }
        }
        //System.out.println();
        return P;
    }


    /**
     * Is reduction of normal form.
     * @param row recording matrix, is modified.
     * @param Pp a solvable polynomial list for reduction.
     * @param Ap a solvable polynomial.
     * @param Np nf(Pp,Ap), a left normal form of Ap wrt. Pp.
     * @return true, if Np + sum( row[i]*Pp[i] ) == Ap, else false.
     */

    public boolean 
           isLeftReductionNF(List<GenSolvablePolynomial<C>> row,
                             List<GenSolvablePolynomial<C>> Pp, 
                             GenSolvablePolynomial<C> Ap,
                             GenSolvablePolynomial<C> Np) {
        if ( row == null && Pp == null ) {
            if ( Ap == null ) {
               return Np == null;
         }
            return Ap.equals(Np);
        }
        if ( row == null && Pp != null ) {
            return false;
        }
        if ( row != null && Pp == null ) {
            return false;
        }
        if ( row.size() != Pp.size() ) {
            return false;
        }
        GenSolvablePolynomial<C> t = Np;
        GenSolvablePolynomial<C> r;
        GenSolvablePolynomial<C> p;
        for ( int m = 0; m < Pp.size(); m++ ) {
            r = row.get(m);
            p = Pp.get(m);
            if ( r != null && p != null ) {
               if ( t == null ) {
                  t = r.multiply(p);
               } else {
                  t = (GenSolvablePolynomial<C>)t.sum( r.multiply(p) );
               }
            }
            //System.out.println("r = " + r );
            //System.out.println("p = " + p );
        }
        if ( debug ) {
           logger.info("t = " + t );
           logger.info("a = " + Ap );
        }
        if ( t == null ) {
           if ( Ap == null ) {
              return true;
           } else {
              return Ap.isZERO();
           }
        } else {
           t = (GenSolvablePolynomial<C>)t.subtract( Ap );
           return t.isZERO();
        }
    }


    /**
     * Right S-Polynomial.
     * @param Ap solvable polynomial.
     * @param Bp solvable polynomial.
     * @return right-spol(Ap,Bp) the right S-polynomial of Ap and Bp.
     */
    public GenSolvablePolynomial<C> 
           rightSPolynomial(GenSolvablePolynomial<C> Ap, 
                            GenSolvablePolynomial<C> Bp) {  
        if ( logger.isInfoEnabled() ) {
           if ( Bp == null || Bp.isZERO() ) {
               if ( Ap != null ) {
                  return Ap.ring.getZERO(); 
               } else {
                  return null;
               }
           }
           if ( Ap == null || Ap.isZERO() ) {
              return Bp.ring.getZERO(); 
           }
           if ( ! Ap.ring.equals( Bp.ring ) ) { 
              logger.error("rings not equal"); 
           }
        }
        ExpVector e = Ap.leadingExpVector();
        ExpVector f = Bp.leadingExpVector();

        ExpVector g = e.lcm(f);
        ExpVector e1 = g.subtract(e);
        ExpVector f1 = g.subtract(f);

        GenSolvablePolynomial<C> App = Ap.multiply( e1 );
        GenSolvablePolynomial<C> Bpp = Bp.multiply( f1 );

        C a = App.leadingBaseCoefficient();
        C b = Bpp.leadingBaseCoefficient();
        App = App.multiply( b );
        Bpp = Bpp.multiply( a );

        GenSolvablePolynomial<C> Cp = (GenSolvablePolynomial<C>) App.subtract(Bpp);
        return Cp;
    }


}

/*
 * $Id: Roots.java 2915 2009-12-24 16:35:34Z kredel $
 */

package edu.jas.arith;

//import java.util.Random;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.math.MathContext;


import edu.jas.structure.Power;


/**
 * Root computation algorithms.
 * Roots for BigInteger and BigDecimals.
 * @author Heinz Kredel
 */
public class Roots {


    /** Integer n-th root.  
     * Uses BigDecimal and newton iteration.
     * R is the n-th root of A.
     * @param A big integer.
     * @param n long.
     * @return the n-th root of A.
     */
    public static BigInteger root(BigInteger A, int n) {
        if ( n == 1 ) {
            return A;
        }
        if ( n == 2 ) {
            return sqrt(A);
        }
        if ( n < 1 ) {
            throw new RuntimeException("negative root not defined");
        }
        if ( A == null || A.isZERO() || A.isONE() ) {
           return A;
        }
        // ensure enough precision
        int s = A.val.bitLength();
        MathContext mc = new MathContext( s ); 
        //System.out.println("mc = " + mc);
        // newton iteration
        BigDecimal Ap = new BigDecimal( A.val, mc );
        //System.out.println("Ap = " + Ap);
        BigDecimal N = new BigDecimal( n, mc ); 
        BigDecimal ninv = new BigDecimal( 1.0/n, mc ); 
        BigDecimal nsub = BigDecimal.ONE.subtract( ninv ); 
        BigDecimal P, R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        while ( true ) {
            P = Power.positivePower( R, n-1 );
            R1 = Ap.divide( P.multiply(N) ); 
            R1 = R.multiply( nsub ).sum( R1 );
            d = R.subtract(R1).abs();
            R = R1;
            if ( d.compareTo(BigDecimal.ONE) <= 0 ) {
                //System.out.println("d  = " + d);
                break;
            }
        }
        java.math.BigInteger RP = R.val.toBigInteger(); 
        return new BigInteger(RP);
    }


    /** Integer square root.  
     * Uses BigDecimal and newton iteration.
     * R is the square root of A.
     * @param A big integer.
     * @return the square root of A.
     */
    public static BigInteger sqrt(BigInteger A) {
        if ( A == null || A.isZERO() || A.isONE() ) {
           return A;
        }
        // ensure enough precision
        int s = A.val.bitLength();
        MathContext mc = new MathContext( s ); 
        //System.out.println("mc = " + mc);
        // newton iteration
        BigDecimal Ap = new BigDecimal( A.val, mc );
        //System.out.println("Ap = " + Ap);
        BigDecimal ninv = new BigDecimal( 0.5, mc ); 
        BigDecimal R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        while ( true ) {
            R1 = R.sum( Ap.divide( R ) );
            R1 = R1.multiply(ninv); // div n
            d = R.subtract(R1).abs();
            R = R1;
            if ( d.compareTo(BigDecimal.ONE) <= 0 ) {
                //System.out.println("d  = " + d);
                break;
            }
        }
        java.math.BigInteger RP = R.val.toBigInteger(); 
        return new BigInteger(RP);
    }


    /** Integer square root.  
     * Uses BigInteger only.
     * R is the square root of A.
     * @param A big integer.
     * @return the square root of A.
     */
    public static BigInteger sqrtInt(BigInteger A) {
        if ( A == null || A.isZERO() || A.isONE() ) {
           return A;
        }
        int s = A.signum();
        if ( s < 0 ) {
            throw new RuntimeException("root of negative not defined");
        }
        if ( s == 0 ) {
            return A;
        }
        BigInteger R, R1, d;
        int log2 = A.val.bitLength();
        //System.out.println("A = " + A + ", log2 = " + log2);
        int rootlog2 = log2 - log2 / 2;
        R = new BigInteger( A.val.shiftRight(rootlog2) );
        //System.out.println("R = " + R + ", rootlog2 = " + rootlog2);
        d = R;
        while ( !d.isZERO() ) {
            d = new BigInteger( d.val.shiftRight(1) ); // div 2
            R1 = R.sum(d);
            s = A.compareTo( R1.multiply(R1) );
            if ( s == 0 ) {
                return R1;
            }
            if ( s > 0 ) {
                R = R1;
            }
            //System.out.println("R1 = " + R1);
            //System.out.println("d  = " + d);
        }
        while ( true ) {
            R1 = R.sum( BigInteger.ONE );
            //System.out.println("R1 = " + R1);
            s = A.compareTo( R1.multiply(R1) );
            if ( s == 0 ) {
                return R1;
            }
            if ( s > 0 ) {
                R = R1;
            }
            if ( s < 0 ) {
                return R;
            }
        }
        //return R;
    }


    /** Square root.  
     * R is the square root of A.
     * @param A big decimal.
     * @return the square root of A.
     */
    public static BigDecimal sqrt(BigDecimal A) {
        if ( A == null || A.isZERO() || A.isONE() ) {
           return A;
        }
        // for small A use root of inverse
        if ( A.compareTo( BigDecimal.ONE ) < 0 ) {
            BigDecimal Ap = A.inverse();
            Ap = sqrt(Ap);
            return Ap.inverse();
        }
        MathContext mc = A.context;
        // newton iteration
        BigDecimal Ap = new BigDecimal( A.val, mc );
        BigDecimal ninv = new BigDecimal( 0.5, mc ); 
        BigDecimal R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        while ( true ) {
            R1 = R.sum( Ap.divide( R ) );
            R1 = R1.multiply(ninv); // div n
            d = R.subtract(R1).abs();
            R = R1;
            if ( d.compareTo(BigDecimal.ONE) <= 0 ) {
                //System.out.println("d  = " + d);
                break;
            }
        }
        return R;
    }


    /** N-th root.  
     * R is the n-th root of A.
     * @param A big decimal.
     * @param n long.
     * @return the n-th root of A.
     */
    public static BigDecimal root(BigDecimal A, int n) {
        if ( n == 1 ) {
            return A;
        }
        if ( n == 2 ) {
            return sqrt(A);
        }
        if ( n < 1 ) {
            throw new RuntimeException("negative root not defined");
        }
        if ( A == null || A.isZERO() || A.isONE() ) {
           return A;
        }
        // for small A use root of inverse
        if ( A.compareTo( BigDecimal.ONE ) <= 0 ) {
            BigDecimal Ap = A.inverse();
            Ap = root(Ap,n);
            return Ap.inverse();
        }
        // ensure enough precision
        MathContext mc = A.context; 
        // newton iteration
        BigDecimal Ap = A;
        BigDecimal N = new BigDecimal( n, mc ); 
        BigDecimal ninv = new BigDecimal( 1.0/n, mc ); 
        BigDecimal nsub = new BigDecimal( 1.0, mc ); // because of precision
        nsub = nsub.subtract( ninv ); 
        BigDecimal P, R1, R = Ap.multiply(ninv); // initial guess
        BigDecimal d;
        while ( true ) {
            P = Power.positivePower( R, n-1 );
            R1 = Ap.divide( P.multiply(N) ); 
            R1 = R.multiply( nsub ).sum( R1 );
            d = R.subtract(R1).abs();
            R = R1;
            //System.out.println("d  = " + d);
            if ( d.compareTo( BigDecimal.ONE ) <= 0 ) {
                //System.out.println("d  = " + d);
                break;
            }
        }
        return R;
    }

}

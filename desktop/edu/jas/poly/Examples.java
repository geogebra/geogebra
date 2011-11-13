/*
 * $Id: Examples.java 1976 2008-08-03 09:56:01Z kredel $
 */

package edu.jas.poly;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;

/**
 * Examples for polynomials usage.
 * @author Heinz Kredel.
 */

public class Examples {

/**
 * main.
 */
   public static void main (String[] args) {
       //example0();
       /*
       example1();
       example2();
       example3();
       example4();
       example5();
       */
       //example6();
       example7();
       example7();
       //example8();
       //example9();
       //example10();
       //example11();
       //example12();
   }


/**
 * example0.
 * for PPPJ 2006.
 */
public static void example0() {
        BigInteger z = new BigInteger();

        TermOrder to = new TermOrder();
        String[] vars = new String[] { "x1", "x2", "x3" };
        GenPolynomialRing<BigInteger> ring;
        ring = new GenPolynomialRing<BigInteger>(z,3,to,vars);
        System.out.println("ring = " + ring);

        GenPolynomial<BigInteger> pol;
        pol = ring.parse( "3 x1^2 x3^4 + 7 x2^5 - 61" );
        System.out.println("pol = " + pol);
        System.out.println("pol = " + pol.toString(ring.getVars()));

        GenPolynomial<BigInteger> one;
        one = ring.parse( "1" );
        System.out.println("one = " + one);
        System.out.println("one = " + one.toString(ring.getVars()));

        GenPolynomial<BigInteger> p;
        p = pol.subtract(pol);
        System.out.println("p = " + p);
        System.out.println("p = " + p.toString(ring.getVars()));

        p = pol.multiply(pol);
        System.out.println("p = " + p);
        System.out.println("p = " + p.toString(ring.getVars()));
}


/**
 * example1.
 * random polynomial with rational coefficients.
 * Q[x_1,...x_7]
 */
public static void example1() {
       System.out.println("\n\n example 1");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);
       GenPolynomialRing<BigRational> fac;
                fac = new GenPolynomialRing<BigRational>(cfac,7);
       //System.out.println("fac = " + fac);
       System.out.println("fac = " + fac);

       GenPolynomial<BigRational> a = fac.random(10);
       System.out.println("a = " + a);
}


/**
 * example2.
 * random polynomial with coefficients of rational polynomials.
 * Q[x_1,...x_7][y_1,...,y_3]
 */
public static void example2() {
       System.out.println("\n\n example 2");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);
       GenPolynomialRing<BigRational> fac;
                fac = new GenPolynomialRing<BigRational>(cfac,7);
       System.out.println("fac = " + fac);

       GenPolynomialRing<GenPolynomial<BigRational>> gfac;
               gfac = new GenPolynomialRing<GenPolynomial<BigRational>>(fac,3);
       System.out.println("gfac = " + gfac);

       GenPolynomial<GenPolynomial<BigRational>> a = gfac.random(10);
       System.out.println("a = " + a);
}


/**
 * example3.
 * random rational algebraic number.
 * Q(alpha)
 */
public static void example3() {
       System.out.println("\n\n example 3");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);

       GenPolynomialRing<BigRational> mfac;
          mfac = new GenPolynomialRing<BigRational>( cfac, 1 );
       System.out.println("mfac = " + mfac);

       GenPolynomial<BigRational> modul = mfac.random(8).monic();
          // assume !mo.isUnit()
       System.out.println("modul = " + modul);

       AlgebraicNumberRing<BigRational> fac;
          fac = new AlgebraicNumberRing<BigRational>( modul );
       System.out.println("fac = " + fac);

       AlgebraicNumber< BigRational > a = fac.random(15);
       System.out.println("a = " + a);
}

   protected static long getPrime() {
       long prime = 2; //2^60-93; // 2^30-35; //19; knuth (2,390)
       for ( int i = 1; i < 60; i++ ) {
           prime *= 2;
       }
       prime -= 93;
       //System.out.println("prime = " + prime);
       return prime;
   }


/**
 * example4.
 * random modular algebraic number.
 * Z_p(alpha)
 */
public static void example4() {
       System.out.println("\n\n example 4");

       long prime = getPrime();
       ModIntegerRing cfac = new ModIntegerRing(prime);
       System.out.println("cfac = " + cfac);

       GenPolynomialRing<ModInteger> mfac;
          mfac = new GenPolynomialRing<ModInteger>( cfac, 1 );
       System.out.println("mfac = " + mfac);

       GenPolynomial<ModInteger> modul = mfac.random(8).monic();
          // assume !modul.isUnit()
       System.out.println("modul = " + modul);

       AlgebraicNumberRing<ModInteger> fac;
          fac = new AlgebraicNumberRing<ModInteger>( modul );
       System.out.println("fac = " + fac);

       AlgebraicNumber< ModInteger > a = fac.random(12);
       System.out.println("a = " + a);
}


/**
 * example5.
 * random solvable polynomial with rational coefficients.
 * Q{x_1,...x_6, {x_2 * x_1 = x_1 x_2 +1, ...} } 
 */
public static void example5() {
       System.out.println("\n\n example 5");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);
       GenSolvablePolynomialRing<BigRational> sfac;
                sfac = new GenSolvablePolynomialRing<BigRational>(cfac,6);
       //System.out.println("sfac = " + sfac);

       WeylRelations<BigRational> wl = new WeylRelations<BigRational>(sfac);
       wl.generate();
       System.out.println("sfac = " + sfac);

       GenSolvablePolynomial<BigRational> a = sfac.random(5);
       System.out.println("a = " + a);
       System.out.println("a = " + a.toString( sfac.vars ) );

       GenSolvablePolynomial<BigRational> b = a.multiply(a);
       System.out.println("b = " + b);
       System.out.println("b = " + b.toString( sfac.vars ) );

       System.out.println("sfac = " + sfac);
}


/**
 * example6.
 * Fateman benchmark: p = (x+y+z)^20; q = p * (p+1)
 * Z[z,y,x]
 */
public static void example6() {
       System.out.println("\n\n example 6");

       BigInteger cfac = new BigInteger();
       System.out.println("cfac = " + cfac);

       TermOrder to = new TermOrder( TermOrder.INVLEX );
       System.out.println("to   = " + to);

       GenPolynomialRing<BigInteger> fac;
       fac = new GenPolynomialRing<BigInteger>(cfac,3,to);
       System.out.println("fac = " + fac);
                fac.setVars( new String[]{ "z", "y", "x" } );
       System.out.println("fac = " + fac);

       GenPolynomial<BigInteger> x = fac.univariate(0);
       GenPolynomial<BigInteger> y = fac.univariate(1);
       GenPolynomial<BigInteger> z = fac.univariate(2);

       System.out.println("x = " + x);
       System.out.println("x = " + x.toString( fac.vars ) );
       System.out.println("y = " + y);
       System.out.println("y = " + y.toString( fac.vars ) );
       System.out.println("z = " + z);
       System.out.println("z = " + z.toString( fac.vars ) );

       GenPolynomial<BigInteger> p = x.sum(y).sum(z).sum( fac.getONE());
       BigInteger f = cfac.fromInteger(10000000001L);
       //       p = p.multiply( f );
       System.out.println("p = " + p);
       System.out.println("p = " + p.toString( fac.vars ) );

       GenPolynomial<BigInteger> q = p;
       for ( int i = 1; i < 20; i++ ) {
           q = q.multiply(p);
       }
       //System.out.println("q = " + q.toString( fac.vars ) );
       System.out.println("q = " + q.length());

       GenPolynomial<BigInteger> q1 = q.sum( fac.getONE() );

       GenPolynomial<BigInteger> q2;
       long t = System.currentTimeMillis();
       q2 = q.multiply(q1); 
       t = System.currentTimeMillis() - t;

       System.out.println("q2 = " + q2.length());
       System.out.println("time = " + t + " ms");
}


/**
 * example7.
 * Fateman benchmark: p = (x+y+z)^20; q = p * (p+1)
 * Q[z,y,x]
 */
public static void example7() {
       System.out.println("\n\n example 7");

       BigRational cfac = new BigRational();
       System.out.println("cfac = " + cfac);

       TermOrder to = new TermOrder( TermOrder.INVLEX );
       System.out.println("to   = " + to);

       GenPolynomialRing<BigRational> fac;
       fac = new GenPolynomialRing<BigRational>(cfac,3,to);
       System.out.println("fac = " + fac);
                fac.setVars( new String[]{ "z", "y", "x" } );
       System.out.println("fac = " + fac);

       long mi = 1L;
       //long mi = Integer.MAX_VALUE;
       GenPolynomial<BigRational> x = fac.univariate(0,mi);
       GenPolynomial<BigRational> y = fac.univariate(1,mi);
       GenPolynomial<BigRational> z = fac.univariate(2,mi);

       // System.out.println("x = " + x);
       System.out.println("x = " + x.toString( fac.vars ) );
       // System.out.println("y = " + y);
       System.out.println("y = " + y.toString( fac.vars ) );
       // System.out.println("z = " + z);
       System.out.println("z = " + z.toString( fac.vars ) );

       GenPolynomial<BigRational> p = x.sum(y).sum(z).sum( fac.getONE());
       BigRational f = cfac.fromInteger(10000000001L);
           //f = f.multiply( f );
       //p = p.multiply( f );
       // System.out.println("p = " + p);
       System.out.println("p = " + p.toString( fac.vars ) );

       int mpow = 20;
       System.out.println("mpow = " + mpow);
       GenPolynomial<BigRational> q = p;
       for ( int i = 1; i < mpow; i++ ) {
           q = q.multiply(p);
       }
       //System.out.println("q = " + q.toString( fac.vars ) );
       System.out.println("len(q) = " + q.length());
       System.out.println("deg(q) = " + q.degree());

       GenPolynomial<BigRational> q1 = q.sum( fac.getONE() );

       GenPolynomial<BigRational> q2;
       long t = System.currentTimeMillis();
       q2 = q.multiply(q1); 
       t = System.currentTimeMillis() - t;

       System.out.println("len(q2)    = " + q2.length());
       System.out.println("deg(q2)    = " + q2.degree());
       System.out.println("LeadEV(q2) = " + q2.leadingExpVector());
       System.out.println("time       = " + t + " ms");
}


/**
 * example8.
 * Chebyshev polynomials
 *
 *  T(0) = 1
 *  T(1) = x
 *  T(n) = 2x * T(n-1) - T(n-2)
 */
public static void example8() {
    int m = 10;
    BigInteger fac = new BigInteger();
    String[] var = new String[]{ "x" };

    GenPolynomialRing<BigInteger> ring
        = new GenPolynomialRing<BigInteger>(fac,1,var);

    List<GenPolynomial<BigInteger>> T 
       = new ArrayList<GenPolynomial<BigInteger>>(m);

    GenPolynomial<BigInteger> t, one, x, x2, x2a, x2b;

    one = ring.getONE();
    x   = ring.univariate(0);
    x2  = ring.parse("2 x");
    x2a = x.multiply( fac.fromInteger(2) );
    x2b = x.multiply( new BigInteger(2) );
    x2 = x2b;

    T.add( one );
    T.add( x );
    for ( int n = 2; n < m; n++ ) {
        t = x2.multiply( T.get(n-1) ).subtract( T.get(n-2) );
        T.add( t );
    }
    for ( int n = 0 /*m-2*/; n < m; n++ ) {
        System.out.println("T["+n+"] = " + T.get(n) ); //.toString(var) );
    }
}


/**
 * example9.
 * Legendre polynomials
 *
 *  P(0) = 1
 *  P(1) = x
 *  P(n) = 1/n [ (2n-1) * x * P(n-1) - (n-1) * P(n-2) ]
 */
 // P(n+1) = 1/(n+1) [ (2n+1) * x * P(n) - n * P(n-1) ]
public static void example9() {
    int n = 10;

    BigRational fac = new BigRational();
    String[] var = new String[]{ "x" };

    GenPolynomialRing<BigRational> ring
        = new GenPolynomialRing<BigRational>(fac,1,var);

    List<GenPolynomial<BigRational>> P 
       = new ArrayList<GenPolynomial<BigRational>>(n);

    GenPolynomial<BigRational> t, one, x, xc, xn;
    BigRational n21, nn;

    one = ring.getONE();
    x   = ring.univariate(0);

    P.add( one );
    P.add( x );
    for ( int i = 2; i < n; i++ ) {
        n21 = new BigRational( 2*i-1 );
        xc = x.multiply( n21 );
        t = xc.multiply( P.get(i-1) );
        nn = new BigRational( i-1 );
        xc = P.get(i-2).multiply( nn );
        t = t.subtract( xc );
        nn = new BigRational(1,i);
        t = t.multiply( nn );
        P.add( t );
    }
    for ( int i = 0; i < n; i++ ) {
        System.out.println("P["+i+"] = " + P.get(i).toString(var) );
        System.out.println();
    }
}


/**
 * example10.
 * Hermite polynomials
 *
 *  H(0) = 1
 *  H(1) = 2 x
 *  H(n) = 2 * x * H(n-1) - 2 * (n-1) * H(n-2)
 */
 // H(n+1) = 2 * x * H(n) - 2 * n * H(n-1)
public static void example10() {
    int n = 100;

    BigInteger fac = new BigInteger();
    String[] var = new String[]{ "x" };

    GenPolynomialRing<BigInteger> ring
        = new GenPolynomialRing<BigInteger>(fac,1,var);

    List<GenPolynomial<BigInteger>> H 
       = new ArrayList<GenPolynomial<BigInteger>>(n);

    GenPolynomial<BigInteger> t, one, x2, xc, x;
    BigInteger n2, nn;

    one = ring.getONE();
    x   = ring.univariate(0);
    n2 = new BigInteger(2);
    x2 = x.multiply( n2 );
    H.add( one );
    H.add( x2 );
    for ( int i = 2; i < n; i++ ) {
        t = x2.multiply( H.get(i-1) );
        nn = new BigInteger( 2*(i-1) );
        xc = H.get(i-2).multiply( nn );
        t = t.subtract( xc );
        H.add( t );
    }
    for ( int i = n-1; i < n; i++ ) {
        System.out.println("H["+i+"] = " + H.get(i).toString(var) );
        System.out.println();
    }
}


/**
 * example11.
 * degree matrix;
 *
 */
public static void example11() {
    int n = 50;
    BigRational fac = new BigRational();
    GenPolynomialRing<BigRational> ring
           = new GenPolynomialRing<BigRational>(fac,n);
    System.out.println("ring = " + ring + "\n");

    GenPolynomial<BigRational> p = ring.random(5,3,6,0.5f);
    System.out.println("p = " + p + "\n");

    List<GenPolynomial<BigInteger>> dem 
        = TermOrderOptimization.<BigRational>degreeMatrix( p );

    System.out.println("dem = " + dem + "\n");

    List<GenPolynomial<BigRational>> polys 
        = new ArrayList<GenPolynomial<BigRational>>();
    polys.add( p );
    for ( int i = 0; i < 5; i++ ) {
        polys.add( ring.random(5,3,6,0.1f) );
    }
    System.out.println("polys = " + polys + "\n");

    dem = TermOrderOptimization.<BigRational>degreeMatrix( polys );
    System.out.println("dem = " + dem + "\n");

    List<Integer> perm;
    perm = TermOrderOptimization.optimalPermutation( dem );
    System.out.println("perm = " + perm + "\n");

    List<GenPolynomial<BigInteger>> pdem; 
    pdem = TermOrderOptimization.<GenPolynomial<BigInteger>>listPermutation( perm, dem );
    System.out.println("pdem = " + pdem + "\n");

    GenPolynomialRing<BigRational> pring;
    pring = TermOrderOptimization.<BigRational>permutation( perm, ring );
    System.out.println("ring  = " + ring);
    System.out.println("pring = " + pring + "\n");

    List<GenPolynomial<BigRational>> ppolys;
    ppolys = TermOrderOptimization.<BigRational>permutation( perm, pring, polys );
    System.out.println("ppolys = " + ppolys + "\n");

    dem = TermOrderOptimization.<BigRational>degreeMatrix( ppolys );
    //System.out.println("pdem = " + dem + "\n");

    perm = TermOrderOptimization.optimalPermutation( dem );
    //System.out.println("pperm = " + perm + "\n");
    int i = 0;
    for ( Integer j : perm ) {
        if ( i != (int)j ) {
           System.out.println("error = " + i + " != " + j + "\n");
        }
        i++;
    }

    OptimizedPolynomialList<BigRational> op;
    op = TermOrderOptimization.<BigRational>optimizeTermOrder( ring, polys );
    System.out.println("op:\n" + op);
    if ( ! op.equals( new PolynomialList<BigRational>(pring,ppolys) ) ) {
       System.out.println("error = " + "\n" + op);
    }
}


/**
 * example12.
 * type games.
 */
public static void example12() {
       System.out.println("\n\n example 12");

       BigRational t1 = new BigRational();
       System.out.println("t1 = " + t1);

       BigInteger t2 = new BigInteger();
       System.out.println("t2 = " + t2);

       System.out.println("t1.isAssignableFrom(t2) = " 
            + t1.getClass().isAssignableFrom(t2.getClass()));
       System.out.println("t2.isAssignableFrom(t1) = " 
            + t2.getClass().isAssignableFrom(t1.getClass()));

       GenPolynomialRing<BigInteger> t3 
          = new GenPolynomialRing<BigInteger>(t2,3);
       System.out.println("t3 = " + t3);

       GenSolvablePolynomialRing<BigInteger> t4 
          = new GenSolvablePolynomialRing<BigInteger>(t2,3);
       System.out.println("t4 = " + t4);

       System.out.println("t3.isAssignableFrom(t4) = " 
            + t3.getClass().isAssignableFrom(t4.getClass()));
       System.out.println("t4.isAssignableFrom(t3) = " 
            + t4.getClass().isAssignableFrom(t3.getClass()));


       GenPolynomialRing<BigRational> t5 
          = new GenPolynomialRing<BigRational>(t1,3);
       System.out.println("t5 = " + t5);

       System.out.println("t3.isAssignableFrom(t5) = " 
            + t3.getClass().isAssignableFrom(t5.getClass()));
       System.out.println("t5.isAssignableFrom(t3) = " 
            + t5.getClass().isAssignableFrom(t3.getClass()));
}

}

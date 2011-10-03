/*
 * $Id: Examples.java 1265 2007-07-29 10:22:22Z kredel $
 */

package edu.jas.vector;

//import java.util.ArrayList;
//import java.util.List;

//import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
//import edu.jas.arith.ModInteger;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;


/**
 * Examples for basic linear algebra.
 * @author Heinz Kredel.
 */

public class Examples {

/**
 * main.
 */
   public static void main (String[] args) {
       example1();
       //example2();
   }


/**
 * example1.
 */
public static void example1() {
       System.out.println("\n\n example 1");

       BigInteger cfac;
       GenPolynomialRing<BigInteger> fac;
       QuotientRing<BigInteger> efac;
       GenPolynomialRing<Quotient<BigInteger>> qfac;
       GenMatrixRing<GenPolynomial<Quotient<BigInteger>>> mfac;

       cfac = new BigInteger();
       System.out.println("cfac = " + cfac);

       fac = new GenPolynomialRing<BigInteger>(cfac,2);
       System.out.println(" fac = " + fac);

       efac = new QuotientRing<BigInteger>( fac );
       System.out.println("efac = " + efac);

       String[] v = new String[] {"x", "y", "z" };
       qfac = new GenPolynomialRing<Quotient<BigInteger>>( efac, 3, v );
       System.out.println("qfac = " + qfac);

       mfac = new GenMatrixRing<GenPolynomial<Quotient<BigInteger>>>( qfac, 3, 3 );
       System.out.println("mfac = " + mfac);

       GenPolynomial<Quotient<BigInteger>> p;
       p = qfac.random(3,4,2,0.3f);
       System.out.println("\np = " + p);


       GenMatrix<GenPolynomial<Quotient<BigInteger>>> m;
       m = mfac.random(3,0.4f);
       System.out.println("\nm = " + m);

   }


}

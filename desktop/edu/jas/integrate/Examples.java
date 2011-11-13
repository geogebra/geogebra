/*
 * $Id: Examples.java 2877 2009-11-15 17:17:40Z kredel $
 */

package edu.jas.integrate;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;
import edu.jas.ufd.GCDFactory;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.SquarefreeAbstract;
import edu.jas.ufd.SquarefreeFactory;
import edu.jas.ufd.FactorFactory;
import edu.jas.ufd.FactorAbsolute;
import edu.jas.ufd.PartialFraction;
import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;

/**
 * Examples related to elementary integration. 
 * 
 * @author Axel Kramer
 * @author Heinz Kredel
 */

public class Examples {


  /**
   * Main program.
   * 
   * @param args
   */
  public static void main(String[] args) {
      example1();
      example2();
      example3();
  }


  /**
   * Example rationals.
   */
  public static void example1() {

    BigRational br = new BigRational(0);
    String[] vars = new String[] { "x" };
    GenPolynomialRing<BigRational> fac;
    fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(
        TermOrder.INVLEX), vars);

    ElementaryIntegration<BigRational> eIntegrator = new ElementaryIntegration<BigRational>(
        br);

    GenPolynomial<BigRational> a = fac.parse("x^7 - 24 x^4 - 4 x^2 + 8 x - 8");
    System.out.println("A: " + a.toString());
    GenPolynomial<BigRational> d = fac.parse("x^8 + 6 x^6 + 12 x^4 + 8 x^2");
    System.out.println("D: " + d.toString());
    GenPolynomial<BigRational> gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    List<GenPolynomial<BigRational>>[] ret = eIntegrator.integrateHermite(a, d);
    System.out.println("Result: " + ret[0] + " , " + ret[1]);

    System.out.println("-----");

    a = fac.parse("10 x^2 - 63 x + 29");
    System.out.println("A: " + a.toString());
    d = fac.parse("x^3 - 11 x^2 + 40 x -48");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrateHermite(a, d);
    System.out.println("Result: " + ret[0] + " , " + ret[1]);

    System.out.println("-----");

    a = fac.parse("x+3");
    System.out.println("A: " + a.toString());
    d = fac.parse("x^2 - 3 x - 40");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrateHermite(a, d);
    System.out.println("Result: " + ret[0] + " , " + ret[1]);

    System.out.println("-----");

    a = fac.parse("10 x^2+12 x + 20");
    System.out.println("A: " + a.toString());
    d = fac.parse("x^3 - 8");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrateHermite(a, d);
    System.out.println("Result: " + ret[0] + " , " + ret[1]);

    System.out.println("------------------------------------------------------\n");
    ComputerThreads.terminate();
  }


  /**
   * Example rational plus logarithm.
   */
  public static void example2() {

    BigRational br = new BigRational(0);
    String[] vars = new String[] { "x" };
    GenPolynomialRing<BigRational> fac;
    fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(
        TermOrder.INVLEX), vars);

    ElementaryIntegration<BigRational> eIntegrator = new ElementaryIntegration<BigRational>(
        br);

    GenPolynomial<BigRational> a = fac.parse("x^7 - 24 x^4 - 4 x^2 + 8 x - 8");
    System.out.println("A: " + a.toString());
    GenPolynomial<BigRational> d = fac.parse("x^8 + 6 x^6 + 12 x^4 + 8 x^2");
    System.out.println("D: " + d.toString());
    GenPolynomial<BigRational> gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    Integral<BigRational> ret = eIntegrator.integrate(a, d);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("10 x^2 - 63 x + 29");
    System.out.println("A: " + a.toString());
    d = fac.parse("x^3 - 11 x^2 + 40 x -48");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrate(a, d);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("x+3");
    System.out.println("A: " + a.toString());
    d = fac.parse("x^2 - 3 x - 40");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrate(a, d);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("10 x^2+12 x + 20");
    System.out.println("A: " + a.toString());
    d = fac.parse("x^3 - 8");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrate(a, d);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("1");
    System.out.println("A: " + a.toString());
    d = fac.parse("(x**5 + x - 7)");
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrate(a, d);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("1");
    d = fac.parse("(x**5 + x - 7)");
    a = a.sum(d);
    System.out.println("A: " + a.toString());
    System.out.println("D: " + d.toString());
    gcd = a.gcd(d);
    System.out.println("GCD: " + gcd.toString());
    ret = eIntegrator.integrate(a, d);
    System.out.println("Result: " + ret);

    System.out.println("-----");
    ComputerThreads.terminate();
  }


  /**
   * Example quotients with rational plus logarithm.
   */
  public static void example3() {

    BigRational br = new BigRational(0);
    String[] vars = new String[] { "x" };
    GenPolynomialRing<BigRational> fac;
    fac = new GenPolynomialRing<BigRational>(br, vars.length, new TermOrder(
        TermOrder.INVLEX), vars);

    QuotientRing<BigRational> qfac = new QuotientRing<BigRational>(fac);

    ElementaryIntegration<BigRational> eIntegrator = new ElementaryIntegration<BigRational>(br);

    GenPolynomial<BigRational> a = fac.parse("x^7 - 24 x^4 - 4 x^2 + 8 x - 8");
    GenPolynomial<BigRational> d = fac.parse("x^8 + 6 x^6 + 12 x^4 + 8 x^2");
    Quotient<BigRational> q = new Quotient<BigRational>(qfac,a,d);
    System.out.println("q =  " + q);
    QuotIntegral<BigRational> ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("10 x^2 - 63 x + 29");
    d = fac.parse("x^3 - 11 x^2 + 40 x -48");
    q = new Quotient<BigRational>(qfac,a,d);
    System.out.println("q =  " + q);
    ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("x+3");
    d = fac.parse("x^2 - 3 x - 40");
    q = new Quotient<BigRational>(qfac,a,d);
    System.out.println("q =  " + q);
    ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("10 x^2+12 x + 20");
    d = fac.parse("x^3 - 8");
    q = new Quotient<BigRational>(qfac,a,d);
    System.out.println("q =  " + q);
    ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("1");
    d = fac.parse("(x**5 + x - 7)");
    q = new Quotient<BigRational>(qfac,a,d);
    System.out.println("q =  " + q);
    ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    a = fac.parse("1");
    d = fac.parse("(x**5 + x - 7)");
    a = a.sum(d);
    q = new Quotient<BigRational>(qfac,a,d);
    System.out.println("q =  " + q);
    ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);

    System.out.println("-----");

    Quotient<BigRational> qi = qfac.random(7);
    //qi = qi.sum( qfac.random(5) );
    q = eIntegrator.deriviative(qi);
    System.out.println("qi =  " + qi);
    System.out.println("q  =  " + q);
    ret = eIntegrator.integrate(q);
    System.out.println("Result: " + ret);
    boolean t = eIntegrator.isIntegral(ret);
    System.out.println("isIntegral = " + t);

    System.out.println("-----");
    ComputerThreads.terminate();
  }


}

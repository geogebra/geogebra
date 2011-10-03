/*
 * $Id: ExamplesPartialFraction.java 2996 2010-02-07 13:32:42Z kredel $
 */

package edu.jas.ufd;


import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.TermOrder;


/**
 * Examples related to partial fraction decomposition.
 * 
 * @author Heinz Kredel
 */

public class ExamplesPartialFraction {


    /**
     * Main program.
     * 
     * @param args
     */
    public static void main(String[] args) {
        example11();
        example12();
        example13();
        example14();
        // BasicConfigurator.configure();
        example15();
        example16();
        example17();
        ComputerThreads.terminate();
    }


    /**
     * example11. Rothstein-Trager algorithm.
     */
    public static void example11() {
        System.out.println("\n\nexample 11");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^2 - 2 )
        GenPolynomial<BigRational> D = pfac.parse("x^2 - 2");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFractionIrreducible(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example12. Rothstein-Trager algorithm.
     */
    public static void example12() {
        System.out.println("\n\nexample 12");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^3 + x )
        GenPolynomial<BigRational> D = pfac.parse("x^3 + x");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example13. Rothstein-Trager algorithm.
     */
    public static void example13() {
        System.out.println("\n\nexample 13");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^6 - 5 x^4 + 5 x^2 + 4 )
        GenPolynomial<BigRational> D = pfac.parse("x^6 - 5 x^4 + 5 x^2 + 4");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example14. Rothstein-Trager algorithm.
     */
    public static void example14() {
        System.out.println("\n\nexample 14");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^4 + 4 )
        GenPolynomial<BigRational> D = pfac.parse("x^4 + 4");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example15. Rothstein-Trager algorithm.
     */
    public static void example15() {
        System.out.println("\n\nexample 15");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^3 - 2 )
        GenPolynomial<BigRational> D = pfac.parse("x^3 - 2");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example16. Rothstein-Trager algorithm.
     */
    public static void example16() {
        System.out.println("\n\nexample 16");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x - 1 ) ( x - 2 ) ( x - 3 ) 
        GenPolynomial<BigRational> D = pfac.parse("( x - 1 ) * ( x - 2 ) * ( x - 3 )");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example17. Absolute factorization of example15.
     */
    public static void example17() {
        System.out.println("\n\nexample 17");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^3 - 2 )
        GenPolynomial<BigRational> D = pfac.parse("x^3 - 2");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = (FactorRational) FactorFactory.getImplementation(cfac);

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFractionIrreducibleAbsolute(N, D);
        System.out.println("\nintegral " + F);
    }

}

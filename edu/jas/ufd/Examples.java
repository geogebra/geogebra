/*
 * $Id: Examples.java 2996 2010-02-07 13:32:42Z kredel $
 */

package edu.jas.ufd;


import java.util.SortedMap;

import org.apache.log4j.BasicConfigurator;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;
import edu.jas.arith.BigRational;
import edu.jas.kern.ComputerThreads;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;


/**
 * Examples for ufd and elementaty integration usage.
 * @author Heinz Kredel.
 */

public class Examples {


    /**
     * main.
     */
    public static void main(String[] args) {
        //BasicConfigurator.configure();
        //// no go: example6();
        //BasicConfigurator.configure();
        //example9();
        BasicConfigurator.configure();
        example10();
        ComputerThreads.terminate();
    }


    /**
     * example6. Partial fraction decomposition.
     */
    public static void example6() {
        System.out.println("\n\nexample 6");
        // http://www.apmaths.uwo.ca/~rcorless/AM563/NOTES/Nov_16_95/node13.html

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // ( 7 x^6 + 1 ) /  ( x^7 + x + 1 )
        GenPolynomial<BigRational> D = pfac.parse("x^7 + x + 1");
        GenPolynomial<BigRational> N = PolyUtil.<BigRational> baseDeriviative(D);

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);
    }


    /**
     * example9. Rothstein-Trager and absolute factorization algorithm.
     */
    public static void example9() {
        System.out.println("\n\nexample 9");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);
        String[] alpha = new String[] { "alpha" };
        String[] vars = new String[] { "x" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, vars);

        // 1 / ( x^5 + x - 7 ) 
        GenPolynomial<BigRational> D = pfac.parse("( x^5 + x - 7 )");
        GenPolynomial<BigRational> N = pfac.getONE();

        FactorRational engine = new FactorRational();

        PartialFraction<BigRational> F = engine.baseAlgebraicPartialFraction(N, D);
        System.out.println("\nintegral " + F);

        //PartialFraction<BigRational> Fa = engine.baseAlgebraicPartialFractionIrreducibleAbsolute(N,D);
        //System.out.println("\nintegral_a " + Fa);

    }


    /**
     * example10. factorization in Q(sqrt(2))(x)(sqrt(x))[y].
     */
    public static void example10() {
        System.out.println("\n\nexample 10");

        TermOrder to = new TermOrder(TermOrder.INVLEX);
        BigRational cfac = new BigRational(1);

        String[] var_w2 = new String[] { "w2" };
        GenPolynomialRing<BigRational> pfac = new GenPolynomialRing<BigRational>(cfac, 1, to, var_w2);
        System.out.println("pfac   = " + pfac.toScript());

        GenPolynomial<BigRational> w2 = pfac.parse(" w2^2 - 2 ");
        System.out.println("w2     = " + w2);

        AlgebraicNumberRing<BigRational> a2fac = new AlgebraicNumberRing<BigRational>(w2, true);
        System.out.println("a2fac  = " + a2fac.toScript());

        String[] var_x = new String[] { "x" };
        GenPolynomialRing<AlgebraicNumber<BigRational>> apfac = new GenPolynomialRing<AlgebraicNumber<BigRational>>(
                a2fac, 1, to, var_x);
        System.out.println("apfac  = " + apfac.toScript());

        QuotientRing<AlgebraicNumber<BigRational>> qfac = new QuotientRing<AlgebraicNumber<BigRational>>(
                apfac);
        System.out.println("qfac   = " + qfac.toScript());

        String[] var_wx = new String[] { "wx" };
        GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>> pqfac = new GenPolynomialRing<Quotient<AlgebraicNumber<BigRational>>>(
                qfac, 1, to, var_wx);
        System.out.println("pqfac  = " + pqfac.toScript());

        GenPolynomial<Quotient<AlgebraicNumber<BigRational>>> wx = pqfac.parse(" wx^2 - { x } ");
        System.out.println("wx     = " + wx);

        AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>> axfac = new AlgebraicNumberRing<Quotient<AlgebraicNumber<BigRational>>>(
                wx, true);
        System.out.println("axfac  = " + axfac.toScript());

        String[] var_y = new String[] { "y" };
        GenPolynomialRing<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> apqfac = new GenPolynomialRing<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>(
                axfac, 1, to, var_y);
        System.out.println("apqfac = " + apqfac.toScript());

        //  ( y^2 - x ) * ( y^2 - 2 ), need {} for recursive coefficients
        GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> f;
        f = apqfac.parse(" ( y^2 - { { x } } ) * ( y^2 - 2 )^2 ");
        System.out.println("f      = " + f);

        FactorAbstract<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>> engine = FactorFactory
                .getImplementation(axfac);
        System.out.println("engine = " + engine);

        SortedMap<GenPolynomial<AlgebraicNumber<Quotient<AlgebraicNumber<BigRational>>>>, Long> F = engine
                .factors(f);
        System.out.println("factors(f) = " + F);
    }

}

/*
 * $Id: PolyUfdUtil.java 3198 2010-06-26 20:10:41Z kredel $
 */

package edu.jas.ufd;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.application.Quotient;
import edu.jas.application.QuotientRing;

import edu.jas.arith.BigInteger;
import edu.jas.arith.Modular;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.ModLong;
import edu.jas.arith.ModLongRing;

import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.PolyUtil;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModularRingFactory;
import edu.jas.structure.UnaryFunctor;

import edu.jas.util.ListUtil;


/**
 * Polynomial ufd utilities, like conversion between different representations
 * and Hensel lifting.
 * @author Heinz Kredel
 */

public class PolyUfdUtil {


    private static final Logger logger = Logger.getLogger(PolyUfdUtil.class);


    private static boolean debug = logger.isDebugEnabled();


    /**
     * Integral polynomial from rational function coefficients. Represent as
     * polynomial with integral polynomial coefficients by multiplication with
     * the lcm of the numerators of the rational function coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with rational function coefficients to be converted.
     * @return polynomial with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> integralFromQuotientCoefficients(
            GenPolynomialRing<GenPolynomial<C>> fac, GenPolynomial<Quotient<C>> A) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().clone();
        if (A == null || A.isZERO()) {
            return B;
        }
        GenPolynomial<C> c = null;
        GenPolynomial<C> d;
        GenPolynomial<C> x;
        GreatestCommonDivisor<C> ufd = new GreatestCommonDivisorSubres<C>();
        int s = 0;
        // lcm of denominators
        for (Quotient<C> y : A.getMap().values()) {
            x = y.den;
            // c = lcm(c,x)
            if (c == null) {
                c = x;
                s = x.signum();
            } else {
                d = ufd.gcd(c, x);
                c = c.multiply(x.divide(d));
            }
        }
        if (s < 0) {
            c = c.negate();
        }
        for (Map.Entry<ExpVector, Quotient<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            Quotient<C> a = y.getValue();
            // p = n*(c/d)
            GenPolynomial<C> b = c.divide(a.den);
            GenPolynomial<C> p = a.num.multiply(b);
            //B = B.sum( p, e ); // inefficient
            B.doPutToMap(e, p);
        }
        return B;
    }


    /**
     * Integral polynomial from rational function coefficients. Represent as
     * polynomial with integral polynomial coefficients by multiplication with
     * the lcm of the numerators of the rational function coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomial with rational function coefficients to be
     *            converted.
     * @return list of polynomials with integral polynomial coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<GenPolynomial<C>>> integralFromQuotientCoefficients(
            GenPolynomialRing<GenPolynomial<C>> fac, Collection<GenPolynomial<Quotient<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenPolynomial<GenPolynomial<C>>> list = new ArrayList<GenPolynomial<GenPolynomial<C>>>(L.size());
        for (GenPolynomial<Quotient<C>> p : L) {
            list.add(integralFromQuotientCoefficients(fac, p));
        }
        return list;
    }


    /**
     * Rational function from integral polynomial coefficients. Represent as
     * polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with integral polynomial coefficients to be
     *            converted.
     * @return polynomial with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<Quotient<C>> quotientFromIntegralCoefficients(
            GenPolynomialRing<Quotient<C>> fac, GenPolynomial<GenPolynomial<C>> A) {
        GenPolynomial<Quotient<C>> B = fac.getZERO().clone();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<Quotient<C>> cfac = fac.coFac;
        QuotientRing<C> qfac = (QuotientRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<C>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            Quotient<C> p = new Quotient<C>(qfac, a); // can not be zero
            if (p != null && !p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * Rational function from integral polynomial coefficients. Represent as
     * polynomial with type Quotient<C> coefficients.
     * @param fac result polynomial factory.
     * @param L list of polynomials with integral polynomial coefficients to be
     *            converted.
     * @return list of polynomials with type Quotient<C> coefficients.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<Quotient<C>>> quotientFromIntegralCoefficients(
            GenPolynomialRing<Quotient<C>> fac, Collection<GenPolynomial<GenPolynomial<C>>> L) {
        if (L == null) {
            return null;
        }
        List<GenPolynomial<Quotient<C>>> list = new ArrayList<GenPolynomial<Quotient<C>>>(L.size());
        for (GenPolynomial<GenPolynomial<C>> p : L) {
            list.add(quotientFromIntegralCoefficients(fac, p));
        }
        return list;
    }


    /**
     * From BigInteger coefficients. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients, e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigInteger&gt; coefficients to
     *            be converted.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends RingElem<C>> GenPolynomial<GenPolynomial<C>> fromIntegerCoefficients(
            GenPolynomialRing<GenPolynomial<C>> fac, GenPolynomial<GenPolynomial<BigInteger>> A) {
        GenPolynomial<GenPolynomial<C>> B = fac.getZERO().clone();
        if (A == null || A.isZERO()) {
            return B;
        }
        RingFactory<GenPolynomial<C>> cfac = fac.coFac;
        GenPolynomialRing<C> rfac = (GenPolynomialRing<C>) cfac;
        for (Map.Entry<ExpVector, GenPolynomial<BigInteger>> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            GenPolynomial<BigInteger> a = y.getValue();
            GenPolynomial<C> p = PolyUtil.<C> fromIntegerCoefficients(rfac, a);
            if (p != null && !p.isZERO()) {
                //B = B.sum( p, e ); // inefficient
                B.doPutToMap(e, p);
            }
        }
        return B;
    }


    /**
     * From BigInteger coefficients. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients, e.g. ModInteger or BigRational.
     * @param fac result polynomial factory.
     * @param L polynomial list with GenPolynomial&lt;BigInteger&gt;
     *            coefficients to be converted.
     * @return polynomial list with polynomials with type GenPolynomial&lt;C&gt;
     *         coefficients.
     */
    public static <C extends RingElem<C>> List<GenPolynomial<GenPolynomial<C>>> fromIntegerCoefficients(
            GenPolynomialRing<GenPolynomial<C>> fac, List<GenPolynomial<GenPolynomial<BigInteger>>> L) {
        List<GenPolynomial<GenPolynomial<C>>> K = null;
        if (L == null) {
            return K;
        }
        K = new ArrayList<GenPolynomial<GenPolynomial<C>>>(L.size());
        if (L.size() == 0) {
            return K;
        }
        for (GenPolynomial<GenPolynomial<BigInteger>> a : L) {
            GenPolynomial<GenPolynomial<C>> b = fromIntegerCoefficients(fac, a);
            K.add(b);
        }
        return K;
    }


    /**
     * Introduce lower variable. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients.
     * @param rfac result polynomial factory.
     * @param A polynomial to be extended.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> introduceLowerVariable(
            GenPolynomialRing<GenPolynomial<C>> rfac, GenPolynomial<C> A) {
        if (A == null || rfac == null) {
            return null;
        }
        GenPolynomial<GenPolynomial<C>> Pc = rfac.getONE().multiply(A);
        if (Pc.isZERO()) {
            return Pc;
        }
        Pc = PolyUtil.<C> switchVariables(Pc);
        return Pc;
    }


    /**
     * From AlgebraicNumber coefficients. Represent as polynomial with type
     * GenPolynomial&lt;C&gt; coefficients, e.g. ModInteger or BigRational.
     * @param rfac result polynomial factory.
     * @param A polynomial with AlgebraicNumber coefficients to be converted.
     * @param k for (y-k x) substitution.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<GenPolynomial<C>> substituteFromAlgebraicCoefficients(
            GenPolynomialRing<GenPolynomial<C>> rfac, GenPolynomial<AlgebraicNumber<C>> A, long k) {
        if (A == null || rfac == null) {
            return null;
        }
        if (A.isZERO()) {
            return rfac.getZERO();
        }
        // setup x - k alpha
        GenPolynomialRing<AlgebraicNumber<C>> apfac = A.ring;
        GenPolynomial<AlgebraicNumber<C>> x = apfac.univariate(0);
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) A.ring.coFac;
        AlgebraicNumber<C> alpha = afac.getGenerator();
        AlgebraicNumber<C> ka = afac.fromInteger(k);
        GenPolynomial<AlgebraicNumber<C>> s = x.subtract(ka.multiply(alpha)); // x - k alpha
        // substitute, convert and switch
        GenPolynomial<AlgebraicNumber<C>> B = PolyUtil.<AlgebraicNumber<C>> substituteMain(A, s);
        GenPolynomial<GenPolynomial<C>> Pc = PolyUtil.<C> fromAlgebraicCoefficients(rfac, B); // Q[alpha][x]
        Pc = PolyUtil.<C> switchVariables(Pc); // Q[x][alpha]
        return Pc;
    }


    /**
     * Convert to AlgebraicNumber coefficients. Represent as polynomial with
     * AlgebraicNumber<C> coefficients, C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with GenPolynomial&lt;BigInteger&gt; coefficients to
     *            be converted.
     * @param k for (y-k x) substitution.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<AlgebraicNumber<C>> substituteConvertToAlgebraicCoefficients(
            GenPolynomialRing<AlgebraicNumber<C>> pfac, GenPolynomial<C> A, long k) {
        if (A == null || pfac == null) {
            return null;
        }
        if (A.isZERO()) {
            return pfac.getZERO();
        }
        // convert to Q(alpha)[x]
        GenPolynomial<AlgebraicNumber<C>> B = PolyUtil.<C> convertToAlgebraicCoefficients(pfac, A);
        // setup x _+_ k alpha for back substitution
        GenPolynomial<AlgebraicNumber<C>> x = pfac.univariate(0);
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        AlgebraicNumber<C> alpha = afac.getGenerator();
        AlgebraicNumber<C> ka = afac.fromInteger(k);
        GenPolynomial<AlgebraicNumber<C>> s = x.sum(ka.multiply(alpha)); // x + k alpha
        // substitute
        GenPolynomial<AlgebraicNumber<C>> N = PolyUtil.<AlgebraicNumber<C>> substituteMain(B, s);
        return N;
    }


    /**
     * Norm of a polynomial with AlgebraicNumber coefficients.
     * @param A polynomial from GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @param k for (y - k x) substitution.
     * @return norm(A) = res_x(A(x,y),m(x)) in GenPolynomialRing&lt;C&gt;.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> norm(GenPolynomial<AlgebraicNumber<C>> A, long k) {
        if (A == null) {
            return null;
        }
        GenPolynomialRing<AlgebraicNumber<C>> pfac = A.ring; // Q(alpha)[x]
        if (pfac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        GenPolynomial<C> agen = afac.modul;
        GenPolynomialRing<C> cfac = afac.ring;
        if (A.isZERO()) {
            return cfac.getZERO();
        }
        AlgebraicNumber<C> ldcf = A.leadingBaseCoefficient();
        if (!ldcf.isONE()) {
            A = A.monic();
        }
        GenPolynomialRing<GenPolynomial<C>> rfac = new GenPolynomialRing<GenPolynomial<C>>(cfac, pfac);

        // transform minimal polynomial to bi-variate polynomial
        GenPolynomial<GenPolynomial<C>> Ac = PolyUfdUtil.<C> introduceLowerVariable(rfac, agen);
        //System.out.println("Ac = " + Ac);

        // transform to bi-variate polynomial, 
        // switching varaible sequence from Q[alpha][x] to Q[X][alpha]
        GenPolynomial<GenPolynomial<C>> Pc = PolyUfdUtil.<C> substituteFromAlgebraicCoefficients(rfac, A, k);
        Pc = PolyUtil.<C> monic(Pc);
        //System.out.println("Pc = " + Pc);

        GreatestCommonDivisorSubres<C> engine = new GreatestCommonDivisorSubres<C>( /*cfac.coFac*/);
        // = (GreatestCommonDivisorAbstract<C>)GCDFactory.<C>getImplementation( cfac.coFac );

        GenPolynomial<GenPolynomial<C>> Rc = engine.recursiveResultant(Pc, Ac);
        //System.out.println("Rc = " + Rc);
        GenPolynomial<C> res = Rc.leadingBaseCoefficient();
        res = res.monic();
        return res;
    }


    /**
     * Norm of a polynomial with AlgebraicNumber coefficients.
     * @param A polynomial from GenPolynomial&lt;AlgebraicNumber&lt;C&gt;&gt;.
     * @return norm(A) = resultant_x( A(x,y), m(x) ) in K[y].
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> norm(GenPolynomial<AlgebraicNumber<C>> A) {
        return norm(A, 0L);
    }


    /**
     * Ensure that the field property is determined.
     * Checks if modul is irreducible and modifies the algebraic number ring. 
     * @param afac algebraic number ring.
     */
    public static <C extends GcdRingElem<C>> 
      void ensureFieldProperty(AlgebraicNumberRing<C> afac) {
        if ( afac.getField() != -1 ) {
           return;
        }
        if ( !afac.ring.coFac.isField() ) {
           afac.setField(false);
           return;
        }
        Factorization<C> mf = FactorFactory.<C>getImplementation(afac.ring);
        if ( mf.isIrreducible(afac.modul) ) {
           afac.setField(true);
        } else {
           afac.setField(false);
        }
    }


    /**
     * Kronecker substitution. Substitute x_i by x**d**(i-1) to construct a
     * univariate polynomial.
     * @param A polynomial to be converted.
     * @return a univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> substituteKronecker(GenPolynomial<C> A) {
        if (A == null) {
            return A;
        }
        long d = A.degree() + 1L;
        return substituteKronecker(A, d);
    }


    /**
     * Kronecker substitution. Substitute x_i by x**d**(i-1) to construct a
     * univariate polynomial.
     * @param A polynomial to be converted.
     * @return a univariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> substituteKronecker(GenPolynomial<C> A, long d) {
        if (A == null) {
            return A;
        }
        RingFactory<C> cfac = A.ring.coFac;
        GenPolynomialRing<C> ufac = new GenPolynomialRing<C>(cfac, 1);
        GenPolynomial<C> B = ufac.getZERO().clone();
        if (A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long f = 0L;
            long h = 1L;
            for (int i = 0; i < e.length(); i++) {
                long j = e.getVal(i) * h;
                f += j;
                h *= d;
            }
            ExpVector g = ExpVector.create(1, 0, f);
            B.doPutToMap(g, a);
        }
        return B;
    }


    /**
     * Kronecker substitution. Substitute x_i by x**d**(i-1) to construct a
     * univariate polynomials.
     * @param A list of polynomials to be converted.
     * @return a list of univariate polynomials.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<C>> substituteKronecker(
            List<GenPolynomial<C>> A, int d) {
        if (A == null || A.get(0) == null) {
            return null;
        }
        return ListUtil.<GenPolynomial<C>, GenPolynomial<C>> map(A, new SubstKronecker<C>(d));
    }


    /**
     * Kronecker back substitution. Substitute x**d**(i-1) to x_i to construct a
     * multivariate polynomial.
     * @param A polynomial to be converted.
     * @param fac result polynomial factory.
     * @return a multivariate polynomial.
     */
    public static <C extends GcdRingElem<C>> GenPolynomial<C> backSubstituteKronecker(
            GenPolynomialRing<C> fac, GenPolynomial<C> A, long d) {
        if (A == null) {
            return A;
        }
        if (fac == null) {
            throw new IllegalArgumentException("null factory not allowed ");
        }
        int n = fac.nvar;
        GenPolynomial<C> B = fac.getZERO().clone();
        if (A.isZERO()) {
            return B;
        }
        for (Map.Entry<ExpVector, C> y : A.getMap().entrySet()) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            long f = e.getVal(0);
            ExpVector g = ExpVector.create(n);
            for (int i = 0; i < n; i++) {
                long j = f % d;
                f /= d;
                g = g.subst(i, j);
            }
            B.doPutToMap(g, a);
        }
        return B;
    }


    /**
     * Kronecker back substitution. Substitute x**d**(i-1) to x_i to construct a
     * multivariate polynomials.
     * @param A list of polynomials to be converted.
     * @param fac result polynomial factory.
     * @return a list of multivariate polynomials.
     */
    public static <C extends GcdRingElem<C>> List<GenPolynomial<C>> backSubstituteKronecker(
            GenPolynomialRing<C> fac, List<GenPolynomial<C>> A, long d) {
        return ListUtil.<GenPolynomial<C>, GenPolynomial<C>> map(A, new BackSubstKronecker<C>(fac, d));
    }

}


/**
 * Kronecker substitutuion functor.
 */
class SubstKronecker<C extends GcdRingElem<C>> implements UnaryFunctor<GenPolynomial<C>, GenPolynomial<C>> {


    final long d;


    public SubstKronecker(long d) {
        this.d = d;
    }


    public GenPolynomial<C> eval(GenPolynomial<C> c) {
        if (c == null) {
            return null;
        } else {
            return PolyUfdUtil.<C> substituteKronecker(c, d);
        }
    }
}


/**
 * Kronecker back substitutuion functor.
 */
class BackSubstKronecker<C extends GcdRingElem<C>> implements
        UnaryFunctor<GenPolynomial<C>, GenPolynomial<C>> {


    final long d;


    final GenPolynomialRing<C> fac;


    public BackSubstKronecker(GenPolynomialRing<C> fac, long d) {
        this.d = d;
        this.fac = fac;
    }


    public GenPolynomial<C> eval(GenPolynomial<C> c) {
        if (c == null) {
            return null;
        } else {
            return PolyUfdUtil.<C> backSubstituteKronecker(fac, c, d);
        }
    }
}

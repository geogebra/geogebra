/*
 * $Id: PolyUtil.java 3050 2010-03-20 15:11:34Z kredel $
 */

package edu.jas.poly;

import java.util.Map;
//import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.arith.BigDecimal;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.arith.BigComplex;
import edu.jas.arith.Modular;

import edu.jas.structure.Element;
import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModularRingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.structure.Power;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;

import edu.jas.util.ListUtil;


/**
 * Polynomial utilities, for example
 * conversion between different representations, evaluation and interpolation.
 * @author Heinz Kredel
 */

public class PolyUtil {


    private static final Logger logger = Logger.getLogger(PolyUtil.class);
    private static boolean debug = logger.isDebugEnabled();


    /**
     * Recursive representation. 
     * Represent as polynomial in i variables with coefficients in n-i variables.
     * Works for arbitrary term orders.
     * @param <C> coefficient type.
     * @param rfac recursive polynomial ring factory.
     * @param A polynomial to be converted.
     * @return Recursive represenations of this in the ring rfac.
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<GenPolynomial<C>> 
        recursive( GenPolynomialRing<GenPolynomial<C>> rfac, 
                   GenPolynomial<C> A ) {

        GenPolynomial<GenPolynomial<C>> B = rfac.getZERO().clone();
        if ( A.isZERO() ) {
           return B;
        }
        int i = rfac.nvar;
        GenPolynomial<C> zero = rfac.getZEROCoefficient();
        Map<ExpVector,GenPolynomial<C>> Bv = B.val; //getMap();
        for ( Map.Entry<ExpVector,C> y: A.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            C a = y.getValue();
            ExpVector f = e.contract(0,i);
            ExpVector g = e.contract(i,e.length()-i);
            GenPolynomial<C> p = Bv.get(f);
            if ( p == null ) {
                p = zero;
            }
            p = p.sum( a, g );
            Bv.put( f, p );
        }
        return B;
    }


    /**
     * Distribute a recursive polynomial to a generic polynomial. 
     * Works for arbitrary term orders.
     * @param <C> coefficient type.
     * @param dfac combined polynomial ring factory of coefficients and this.
     * @param B polynomial to be converted.
     * @return distributed polynomial.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<C> 
        distribute( GenPolynomialRing<C> dfac,
                    GenPolynomial<GenPolynomial<C>> B) {
        GenPolynomial<C> C = dfac.getZERO().clone();
        if ( B.isZERO() ) { 
            return C;
        }
        Map<ExpVector,C> Cm = C.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: B.getMap().entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> A = y.getValue();
            for ( Map.Entry<ExpVector,C> x: A.val.entrySet() ) {
                ExpVector f = x.getKey();
                C b = x.getValue();
                ExpVector g = e.combine(f);
                assert ( Cm.get(g) != null );
                //if ( Cm.get(g) != null ) { // todo assert, done
                //   throw new RuntimeException("PolyUtil debug error");
                //}
                Cm.put( g, b );
            }
        }
        return C;
    }


    /**
     * Recursive representation. 
     * Represent as polynomials in i variables with coefficients in n-i variables.
     * Works for arbitrary term orders.
     * @param <C> coefficient type.
     * @param rfac recursive polynomial ring factory.
     * @param L list of polynomials to be converted.
     * @return Recursive represenations of the list in the ring rfac.
     */
    public static <C extends RingElem<C>> 
        List<GenPolynomial<GenPolynomial<C>>> 
        recursive( GenPolynomialRing<GenPolynomial<C>> rfac, 
                   List<GenPolynomial<C>> L ) {
        return ListUtil.<GenPolynomial<C>,GenPolynomial<GenPolynomial<C>>>map( 
                                                    L, 
                                                    new DistToRec<C>(rfac) );
    }


    /**
     * Distribute a recursive polynomial list to a generic polynomial list. 
     * Works for arbitrary term orders.
     * @param <C> coefficient type.
     * @param dfac combined polynomial ring factory of coefficients and this.
     * @param L list of polynomials to be converted.
     * @return distributed polynomial list.
     */
    public static <C extends RingElem<C>>
        List<GenPolynomial<C>> 
        distribute( GenPolynomialRing<C> dfac,
                    List<GenPolynomial<GenPolynomial<C>>> L) {
        return ListUtil.<GenPolynomial<GenPolynomial<C>>,GenPolynomial<C>>map( 
                                                    L, 
                                                    new RecToDist<C>(dfac) );
    }


    /**
     * BigInteger from ModInteger coefficients, symmetric. 
     * Represent as polynomial with BigInteger coefficients by 
     * removing the modules and making coefficients symmetric to 0.
     * @param fac result polynomial factory.
     * @param A polynomial with ModInteger coefficients to be converted.
     * @return polynomial with BigInteger coefficients.
     */
    public static <C extends RingElem<C> & Modular> 
        GenPolynomial<BigInteger> 
        integerFromModularCoefficients( GenPolynomialRing<BigInteger> fac,
                                        GenPolynomial<C> A ) {
        return PolyUtil.<C,BigInteger>map(fac,A, new ModSymToInt<C>() );
    }



    /**
     * BigInteger from ModInteger coefficients, symmetric. 
     * Represent as polynomial with BigInteger coefficients by 
     * removing the modules and making coefficients symmetric to 0.
     * @param fac result polynomial factory.
     * @param L list of polynomials with ModInteger coefficients to be converted.
     * @return list of polynomials with BigInteger coefficients.
     */
    public static <C extends RingElem<C> & Modular> 
        List<GenPolynomial<BigInteger>> 
        integerFromModularCoefficients( final GenPolynomialRing<BigInteger> fac,
                                        List<GenPolynomial<C>> L ) {
        return ListUtil.<GenPolynomial<C>,GenPolynomial<BigInteger>>map( L, 
                        new UnaryFunctor<GenPolynomial<C>,GenPolynomial<BigInteger>>() {
                            public GenPolynomial<BigInteger> eval(GenPolynomial<C> c) {
                                return PolyUtil.<C>integerFromModularCoefficients(fac,c);
                            }
                        }
                                                              );
    }


    /**
     * BigInteger from ModInteger coefficients, positive. 
     * Represent as polynomial with BigInteger coefficients by 
     * removing the modules.
     * @param fac result polynomial factory.
     * @param A polynomial with ModInteger coefficients to be converted.
     * @return polynomial with BigInteger coefficients.
     */
    public static <C extends RingElem<C> & Modular>
        GenPolynomial<BigInteger> 
        integerFromModularCoefficientsPositive( GenPolynomialRing<BigInteger> fac,
                                                GenPolynomial<C> A ) {
        return PolyUtil.<C,BigInteger>map(fac,A, new ModToInt<C>() );
    }


    /**
     * BigInteger from BigRational coefficients. 
     * Represent as polynomial with BigInteger coefficients by 
     * multiplication with the lcm of the numerators of the 
     * BigRational coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with BigRational coefficients to be converted.
     * @return polynomial with BigInteger coefficients.
     */
    public static GenPolynomial<BigInteger> 
        integerFromRationalCoefficients( GenPolynomialRing<BigInteger> fac,
                                         GenPolynomial<BigRational> A ) {
        if ( A == null || A.isZERO() ) {
           return fac.getZERO();
        }
        java.math.BigInteger c = null;
        int s = 0;
        // lcm of denominators
        for ( BigRational y: A.val.values() ) {
            java.math.BigInteger x = y.denominator();
            // c = lcm(c,x)
            if ( c == null ) {
               c = x; 
               s = x.signum();
            } else {
               java.math.BigInteger d = c.gcd( x );
               c = c.multiply( x.divide( d ) );
            }
        }
        if ( s < 0 ) {
           c = c.negate();
        }
        return PolyUtil.<BigRational,BigInteger>map(fac,A, new RatToInt( c ) );
    }


    /**
     * BigInteger from BigRational coefficients. Represent as polynomial with
     * BigInteger coefficients by multiplication with the gcd of the numerators
     * and the lcm of the denominators of the BigRational coefficients.
     * <br /><b>Author:</b> Axel Kramer
     * @param fac result polynomial factory.
     * @param A polynomial with BigRational coefficients to be converted.
     * @return Object[] with 3 entries: [0]->gcd [1]->lcm and 
               [2]->polynomial with BigInteger coefficients.
     */
    public static Object[] 
        integerFromRationalCoefficientsFactor(GenPolynomialRing<BigInteger> fac, 
                                              GenPolynomial<BigRational> A) {
        Object[] result = new Object[3];
        if (A == null || A.isZERO()) {
           result[0] = java.math.BigInteger.ONE;
           result[1] = java.math.BigInteger.ZERO;
           result[2] = fac.getZERO();
           return result;
        }
        java.math.BigInteger gcd = null;
        java.math.BigInteger lcm = null;
        int sLCM = 0;
        int sGCD = 0;
        // lcm of denominators
        for (BigRational y : A.val.values()) {
            java.math.BigInteger numerator = y.numerator();
            java.math.BigInteger denominator = y.denominator();
            // lcm = lcm(lcm,x)
            if (lcm == null) {
                lcm = denominator;
                sLCM = denominator.signum();
            } else {
                java.math.BigInteger d = lcm.gcd(denominator);
                lcm = lcm.multiply(denominator.divide(d));
            }
            // gcd = gcd(gcd,x)
            if (gcd == null) {
                gcd = numerator;
                sGCD = numerator.signum();
            } else {
                gcd = gcd.gcd(numerator);
            }
        }
        if (sLCM < 0) {
            lcm = lcm.negate();
        }
        if (sGCD < 0) {
            gcd = gcd.negate();
        }
        result[0] = gcd;
        result[1] = lcm;
        result[2] = PolyUtil.<BigRational, BigInteger> map(fac, A, new RatToIntFactor(gcd, lcm));
        return result;
    }


    /**
     * BigInteger from BigRational coefficients. 
     * Represent as list of polynomials with BigInteger coefficients by 
     * multiplication with the lcm of the numerators of the 
     * BigRational coefficients of each polynomial.
     * @param fac result polynomial factory.
     * @param L list of polynomials with BigRational coefficients to be converted.
     * @return polynomial list with BigInteger coefficients.
     */
    public static List<GenPolynomial<BigInteger>> 
        integerFromRationalCoefficients( GenPolynomialRing<BigInteger> fac,
                                         List<GenPolynomial<BigRational>> L ) {
        return ListUtil.<GenPolynomial<BigRational>,GenPolynomial<BigInteger>>map( 
                                                    L, 
                                                    new RatToIntPoly(fac) );
    }


    /**
     * From BigInteger coefficients. 
     * Represent as polynomial with type C coefficients,
     * e.g. ModInteger or BigRational.
     * @param <C> coefficient type.
     * @param fac result polynomial factory.
     * @param A polynomial with BigInteger coefficients to be converted.
     * @return polynomial with type C coefficients.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<C> 
        fromIntegerCoefficients( GenPolynomialRing<C> fac,
                                 GenPolynomial<BigInteger> A ) {
        return PolyUtil.<BigInteger,C>map(fac,A, new FromInteger<C>(fac.coFac) );
    }


    /**
     * From BigInteger coefficients. 
     * Represent as list of polynomials with type C coefficients,
     * e.g. ModInteger or BigRational.
     * @param <C> coefficient type.
     * @param fac result polynomial factory.
     * @param L list of polynomials with BigInteger coefficients to be converted.
     * @return list of polynomials with type C coefficients.
     */
    public static <C extends RingElem<C>>
        List<GenPolynomial<C>> 
        fromIntegerCoefficients( GenPolynomialRing<C> fac,
                                 List<GenPolynomial<BigInteger>> L ) {
        return ListUtil.<GenPolynomial<BigInteger>,GenPolynomial<C>>map( L, 
                                                   new FromIntegerPoly<C>(fac) );
    }


    /**
     * Convert to decimal coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with Rational coefficients to be converted.
     * @return polynomial with BigDecimal coefficients.
     */
    public static <C extends RingElem<C> & Rational> 
        GenPolynomial<BigDecimal> 
        decimalFromRational( GenPolynomialRing<BigDecimal> fac,
                             GenPolynomial<C> A ) {
        return PolyUtil.<C,BigDecimal>map(fac,A, new RatToDec<C>() );
    }


    /**
     * Convert to complex decimal coefficients.
     * @param fac result polynomial factory.
     * @param A polynomial with complex Rational coefficients to be converted.
     * @return polynomial with Complex BigDecimal coefficients.
     */
    public static <C extends RingElem<C> & Rational> 
        GenPolynomial<Complex<BigDecimal>> 
        complexDecimalFromRational( GenPolynomialRing<Complex<BigDecimal>> fac,
                                    GenPolynomial<Complex<C>> A ) {
        return PolyUtil.<Complex<C>,Complex<BigDecimal>>map(fac,A, new CompRatToDec<C>(fac.coFac) );
    }


    /**
     * Real part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with real part of the coefficients.
     */
    public static GenPolynomial<BigRational> 
        realPart( GenPolynomialRing<BigRational> fac,
                  GenPolynomial<BigComplex> A ) {
        return PolyUtil.<BigComplex,BigRational>map(fac,A, new RealPart() );
    }


    /**
     * Imaginary part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with imaginary part of coefficients.
     */
    public static GenPolynomial<BigRational> 
        imaginaryPart( GenPolynomialRing<BigRational> fac,
                       GenPolynomial<BigComplex> A ) {
        return PolyUtil.<BigComplex,BigRational>map(fac,A, new ImagPart() );
    }


    /**
     * Real part.
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with real part of the coefficients.
     */
    public static <C extends RingElem<C>>
    GenPolynomial<C> realPartFromComplex(GenPolynomialRing<C> fac, GenPolynomial<Complex<C>> A) {
        return PolyUtil.<Complex<C>, C> map(fac, A, new RealPartComplex<C>());
    }


    /**
     * Imaginary part.
     * @param fac result polynomial factory.
     * @param A polynomial with BigComplex coefficients to be converted.
     * @return polynomial with imaginary part of coefficients.
     */
    public static <C extends RingElem<C>>
    GenPolynomial<C> imaginaryPartFromComplex(GenPolynomialRing<C> fac, GenPolynomial<Complex<C>> A) {
        return PolyUtil.<Complex<C>, C> map(fac, A, new ImagPartComplex<C>());
    }


    /**
     * Complex from real polynomial. 
     * @param fac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with Complex<C> coefficients.
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<Complex<C>>
        toComplex( GenPolynomialRing<Complex<C>> fac,
                    GenPolynomial<C> A ) {
        return PolyUtil.<C,Complex<C>>map(fac,A, new ToComplex<C>(fac.coFac) );
    }


    /**
     * Complex from rational real part. 
     * @param fac result polynomial factory.
     * @param A polynomial with BigRational coefficients to be converted.
     * @return polynomial with BigComplex coefficients.
     */
    public static GenPolynomial<BigComplex> 
        complexFromRational( GenPolynomialRing<BigComplex> fac,
                             GenPolynomial<BigRational> A ) {
        return PolyUtil.<BigRational,BigComplex>map(fac,A, new RatToCompl() );
    }


    /**
     * From AlgebraicNumber coefficients. 
     * Represent as polynomial with type GenPolynomial&lt;C&gt; coefficients,
     * e.g. ModInteger or BigRational.
     * @param rfac result polynomial factory.
     * @param A polynomial with AlgebraicNumber coefficients to be converted.
     * @return polynomial with type GenPolynomial&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<GenPolynomial<C>> 
      fromAlgebraicCoefficients( GenPolynomialRing<GenPolynomial<C>> rfac,
                                 GenPolynomial<AlgebraicNumber<C>> A ) {
        return PolyUtil.<AlgebraicNumber<C>,GenPolynomial<C>>map(rfac,A, new AlgToPoly<C>() );
    }


    /**
     * Convert to AlgebraicNumber coefficients. 
     * Represent as polynomial with AlgebraicNumber<C> coefficients,
     * C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<AlgebraicNumber<C>> 
      convertToAlgebraicCoefficients( GenPolynomialRing<AlgebraicNumber<C>> pfac,
                                      GenPolynomial<C> A ) {
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        return PolyUtil.<C,AlgebraicNumber<C>>map(pfac,A, new CoeffToAlg<C>(afac) );
    }


    /**
     * Convert to recursive AlgebraicNumber coefficients. 
     * Represent as polynomial with recursive AlgebraicNumber<C> coefficients,
     * C is e.g. ModInteger or BigRational.
     * @param depth recursion depth of AlgebraicNumber coefficients.
     * @param pfac result polynomial factory.
     * @param A polynomial with C coefficients to be converted.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<AlgebraicNumber<C>> 
        convertToRecAlgebraicCoefficients( int depth,
                                           GenPolynomialRing<AlgebraicNumber<C>> pfac,
                                           GenPolynomial<C> A ) {
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        return PolyUtil.<C,AlgebraicNumber<C>>map(pfac,A, new CoeffToRecAlg<C>(depth,afac) );
    }


    /**
     * Convert to AlgebraicNumber coefficients. 
     * Represent as polynomial with AlgebraicNumber<C> coefficients,
     * C is e.g. ModInteger or BigRational.
     * @param pfac result polynomial factory.
     * @param A recursive polynomial with GenPolynomial&lt;BigInteger&gt; 
     * coefficients to be converted.
     * @return polynomial with AlgebraicNumber&lt;C&gt; coefficients.
     */
    public static <C extends GcdRingElem<C>>
      GenPolynomial<AlgebraicNumber<C>> 
      convertRecursiveToAlgebraicCoefficients( GenPolynomialRing<AlgebraicNumber<C>> pfac,
                                               GenPolynomial<GenPolynomial<C>> A ) {
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) pfac.coFac;
        return PolyUtil.<GenPolynomial<C>,AlgebraicNumber<C>>map(pfac,A, new PolyToAlg<C>(afac) );
    }


    /**
     * Complex from algebraic coefficients. 
     * @param fac result polynomial factory.
     * @param A polynomial with AlgebraicNumber coefficients Q(i) to be converted.
     * @return polynomial with Complex coefficients.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<Complex<C>> 
        complexFromAlgebraic( GenPolynomialRing<Complex<C>> fac,
                              GenPolynomial<AlgebraicNumber<C>> A ) {
        ComplexRing<C> cfac = (ComplexRing<C>) fac.coFac;
        return PolyUtil.<AlgebraicNumber<C>,Complex<C>>map(fac,A, new AlgebToCompl<C>(cfac) );
    }


    /**
     * AlgebraicNumber from complex coefficients. 
     * @param fac result polynomial factory over Q(i).
     * @param A polynomial with Complex coefficients to be converted.
     * @return polynomial with AlgebraicNumber coefficients.
     */
    public static <C extends GcdRingElem<C>>
        GenPolynomial<AlgebraicNumber<C>> 
        algebraicFromComplex( GenPolynomialRing<AlgebraicNumber<C>> fac,
                              GenPolynomial<Complex<C>> A ) {
        AlgebraicNumberRing<C> afac = (AlgebraicNumberRing<C>) fac.coFac;
        return PolyUtil.<Complex<C>,AlgebraicNumber<C>>map(fac,A, new ComplToAlgeb<C>(afac) );
    }


    /** ModInteger chinese remainder algorithm on coefficients.
     * @param fac GenPolynomial<ModInteger> result factory 
     * with A.coFac.modul*B.coFac.modul = C.coFac.modul.
     * @param A GenPolynomial<ModInteger>.
     * @param B other GenPolynomial<ModInteger>.
     * @param mi inverse of A.coFac.modul in ring B.coFac.
     * @return S = cra(A,B), with S mod A.coFac.modul == A 
     *                       and S mod B.coFac.modul == B. 
     */
    public static <C extends RingElem<C> & Modular>
        GenPolynomial<C> 
        chineseRemainder( GenPolynomialRing<C> fac,
                          GenPolynomial<C> A,
                          C mi,
                          GenPolynomial<C> B ) {
        ModularRingFactory<C> cfac = (ModularRingFactory<C>) fac.coFac; // get RingFactory
        GenPolynomial<C> S = fac.getZERO().clone(); 
        GenPolynomial<C> Ap = A.clone(); 
        SortedMap<ExpVector,C> av = Ap.val; //getMap();
        SortedMap<ExpVector,C> bv = B.getMap();
        SortedMap<ExpVector,C> sv = S.val; //getMap();
        C c = null;
        for ( ExpVector e : bv.keySet() ) {
            C x = av.get( e );
            C y = bv.get( e ); // assert y != null
            if ( x != null ) {
               av.remove( e );
               c = cfac.chineseRemainder(x,mi,y);
               if ( ! c.isZERO() ) { // 0 cannot happen
                   sv.put( e, c );
               }
            } else {
               //c = cfac.fromInteger( y.getVal() );
               c = cfac.chineseRemainder(A.ring.coFac.getZERO(),mi,y);
               if ( ! c.isZERO() ) { // 0 cannot happen
                  sv.put( e, c ); // c != null
               }
            }
        }
        // assert bv is empty = done
        for ( ExpVector e : av.keySet() ) { // rest of av
            C x = av.get( e ); // assert x != null
            //c = cfac.fromInteger( x.getVal() );
            c = cfac.chineseRemainder(x,mi,B.ring.coFac.getZERO());
            if ( ! c.isZERO() ) { // 0 cannot happen
               sv.put( e, c ); // c != null
            }
        }
        return S;
    }


    /**
     * GenPolynomial monic, i.e. leadingBaseCoefficient == 1.
     * If leadingBaseCoefficient is not invertible returns this unmodified.
     * @param <C> coefficient type.
     * @param p recursive GenPolynomial<GenPolynomial<C>>.
     * @return monic(p).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           monic( GenPolynomial<GenPolynomial<C>> p ) {
        if ( p == null || p.isZERO() ) {
            return p;
        }
        C lc = p.leadingBaseCoefficient().leadingBaseCoefficient();
        if ( !lc.isUnit() ) {
           return p;
        }
        C lm = lc.inverse();
        GenPolynomial<C> L = p.ring.coFac.getONE();
        L = L.multiply(lm);
        return p.multiply(L);
    }


    /**
     * Polynomial list monic. 
     * @param <C> coefficient type.
     * @param L list of polynomials with field coefficients.
     * @return list of polynomials with leading coefficient 1.
     */
    public static <C extends RingElem<C>>
        List<GenPolynomial<C>> monic( List<GenPolynomial<C>> L ) {
        return ListUtil.<GenPolynomial<C>,GenPolynomial<C>>map( L, 
                        new UnaryFunctor<GenPolynomial<C>,GenPolynomial<C>>() {
                            public GenPolynomial<C> eval(GenPolynomial<C> c) {
                                if ( c == null ) {
                                     return null;
                                } else {
                                     return c.monic();
                                }
                            }
                        }
                                                              );
    }


    /**
     * Polynomial list leading exponent vectors. 
     * @param <C> coefficient type.
     * @param L list of polynomials.
     * @return list of leading exponent vectors.
     */
    public static <C extends RingElem<C>>
        List<ExpVector> leadingExpVector( List<GenPolynomial<C>> L ) {
        return ListUtil.<GenPolynomial<C>,ExpVector>map( L, 
                        new UnaryFunctor<GenPolynomial<C>,ExpVector>() {
                            public ExpVector eval(GenPolynomial<C> c) {
                                if ( c == null ) {
                                     return null;
                                } else {
                                     return c.leadingExpVector();
                                }
                            }
                        }
                                                              );
    }


    /**
     * Extend coefficient variables. 
     * Extend all coefficient ExpVectors by i elements and multiply by x_j^k.
     * @param pfac extended polynomial ring factory (by i variables in the coefficients).
     * @param j index of variable to be used for multiplication.
     * @param k exponent for x_j.
     * @return extended polynomial.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<GenPolynomial<C>> extendCoefficients(GenPolynomialRing<GenPolynomial<C>> pfac, 
                                                           GenPolynomial<GenPolynomial<C>> A, int j, long k) {
        GenPolynomial<GenPolynomial<C>> Cp = pfac.getZERO().clone();
        if ( A.isZERO() ) { 
           return Cp;
        }
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)pfac.coFac;
        GenPolynomialRing<C> acfac = (GenPolynomialRing<C>)A.ring.coFac;
        int i = cfac.nvar - acfac.nvar;
        Map<ExpVector,GenPolynomial<C>> CC = Cp.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> y: A.val.entrySet() ) {
            ExpVector e = y.getKey();
            GenPolynomial<C> a = y.getValue();
            GenPolynomial<C> f = a.extend(cfac,j,k);
            CC.put( e, f );
        }
        return Cp;
    }


    /**
     * To recursive representation. 
     * Represent as polynomial in i+r variables with coefficients in i variables.
     * Works for arbitrary term orders.
     * @param <C> coefficient type.
     * @param rfac recursive polynomial ring factory.
     * @param A polynomial to be converted.
     * @return Recursive represenations of this in the ring rfac.
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<GenPolynomial<C>> 
        toRecursive( GenPolynomialRing<GenPolynomial<C>> rfac, 
                     GenPolynomial<C> A ) {

        GenPolynomial<GenPolynomial<C>> B = rfac.getZERO().clone();
        if ( A.isZERO() ) {
           return B;
        }
        int i = rfac.nvar;
        GenPolynomial<C> zero = rfac.getZEROCoefficient();
        GenPolynomial<C> one  = rfac.getONECoefficient();
        Map<ExpVector,GenPolynomial<C>> Bv = B.val; //getMap();
        for ( Monomial<C> m: A ) {
            ExpVector e = m.e;
            C a = m.c;
            GenPolynomial<C> p = one.multiply(a);
            Bv.put( e, p );
        }
        return B;
    }


    /**
     * GenPolynomial coefficient wise remainder.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param s nonzero coefficient.
     * @return coefficient wise remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> baseRemainderPoly( GenPolynomial<C> P, 
                                               C s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        GenPolynomial<C> h = P.ring.getZERO().clone();
        Map<ExpVector,C> hm = h.val; //getMap();
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey(); 
            C a = m.getValue(); 
            C x = a.remainder(s);
            hm.put(f,x);
        }
        return h;
    }


    /**
     * GenPolynomial sparse pseudo remainder.
     * For univariate polynomials.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> basePseudoRemainder( GenPolynomial<C> P, 
                                                 GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P.ring.getZERO();
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( f.multipleOf(e) ) {
                C a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                C x = a.remainder(c);
                if ( x.isZERO() ) {
                   C y = a.divide(c);
                   h = S.multiply( y, f ); // coeff a
                } else {
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo divide.
     * For univariate polynomials or exact division.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return quotient with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> basePseudoDivide( GenPolynomial<C> P, 
                                              GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        if ( P.isZERO() || S.isONE() ) {
            return P;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P;
        GenPolynomial<C> q = S.ring.getZERO().clone();

        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( f.multipleOf(e) ) {
                C a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                C x = a.remainder(c);
                if ( x.isZERO() ) {
                   C y = a.divide(c);
                   q = q.sum( y, f );
                   h = S.multiply( y, f ); // coeff a
                } else {
                   q = q.sum( a, f );
                   q = q.multiply( c );
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenPolynomial pseudo quotient and remainder.
     * For univariate polynomials or exact division.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param S nonzero GenPolynomial.
     * @return [ quotient, remainder ] with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#divide(edu.jas.poly.GenPolynomial).
     */
    @SuppressWarnings("unchecked")
    public static <C extends RingElem<C>>
        GenPolynomial<C>[] basePseudoQuotientRemainder( GenPolynomial<C> P, 
                                                        GenPolynomial<C> S ) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        GenPolynomial<C>[] ret = new GenPolynomial[2];
        ret[0] = null;
        ret[1] = null;
        if ( P.isZERO() || S.isONE() ) {
            ret[0] = P;
            ret[1] = S.ring.getZERO();
            return ret;
        }
        C c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = P;
        GenPolynomial<C> q = S.ring.getZERO().clone();

        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( f.multipleOf(e) ) {
                C a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                C x = a.remainder(c);
                if ( x.isZERO() ) {
                   C y = a.divide(c);
                   q = q.sum( y, f );
                   h = S.multiply( y, f ); // coeff a
                } else {
                   q = q.sum( a, f );
                   q = q.multiply( c );
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * Division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param s GenPolynomial.
     * @return this/s.
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursiveDivide( GenPolynomial<GenPolynomial<C>> P, 
                            GenPolynomial<C> s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero " + P + ", " + s);
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( s.isONE() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> p = P.ring.getZERO().clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> pv = p.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m1 : P.getMap().entrySet() ) {
            GenPolynomial<C> c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            GenPolynomial<C> c = PolyUtil.<C>basePseudoDivide(c1,s);
            if ( !c.isZERO() ) {
               pv.put( e1, c ); // or m1.setValue( c )
            } else {
               System.out.println("pu, c1 = " + c1);
               System.out.println("pu, s  = " + s);
               System.out.println("pu, c  = " + c);
               throw new RuntimeException("something is wrong");
            }
        }
        return p;
    }


    /**
     * GenPolynomial base divide.
     * For recursive polynomials.
     * Division by coefficient ring element.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param s coefficient.
     * @return this/s.
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           baseRecursiveDivide( GenPolynomial<GenPolynomial<C>> P, C s ) {
        if ( s == null || s.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero " + P + ", " + s);
        }
        if ( P.isZERO() ) {
            return P;
        }
        if ( s.isONE() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> p = P.ring.getZERO().clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> pv = p.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m1 : P.getMap().entrySet() ) {
            GenPolynomial<C> c1 = m1.getValue();
            ExpVector e1 = m1.getKey();
            GenPolynomial<C> c = PolyUtil.<C>coefficientBasePseudoDivide(c1,s);
            if ( !c.isZERO() ) {
               pv.put( e1, c ); // or m1.setValue( c )
            } else {
               System.out.println("pu, c1 = " + c1);
               System.out.println("pu, s  = " + s);
               System.out.println("pu, c  = " + c);
               throw new RuntimeException("something is wrong");
            }
        }
        return p;
    }


    /**
     * GenPolynomial sparse pseudo remainder.
     * For recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return remainder with ldcf(S)<sup>m'</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursivePseudoRemainder( GenPolynomial<GenPolynomial<C>> P, 
                                     GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P.ring.getZERO();
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( f.multipleOf(e) ) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                GenPolynomial<C> x = c; //test basePseudoRemainder(a,c);
                if ( x.isZERO() ) {
                   GenPolynomial<C> y = PolyUtil.<C>basePseudoDivide(a,c);
                   h = S.multiply( y, f ); // coeff a
                } else {
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param S nonzero recursive GenPolynomial.
     * @return quotient with ldcf(S)<sup>m</sup> P = quotient * S + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursivePseudoDivide( GenPolynomial<GenPolynomial<C>> P, 
                                  GenPolynomial<GenPolynomial<C>> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(P.getClass().getName()
                                       + " division by zero");
        }
        if ( S.ring.nvar != 1 ) {
           // ok if exact division
           // throw new RuntimeException(this.getClass().getName()
           //                            + " univariate polynomials only");
        }
        if ( P == null || P.isZERO() ) {
            return P;
        }
        if ( S.isONE() ) {
            return P;
        }
        GenPolynomial<C> c = S.leadingBaseCoefficient();
        ExpVector e = S.leadingExpVector();
        GenPolynomial<GenPolynomial<C>> h;
        GenPolynomial<GenPolynomial<C>> r = P; 
        GenPolynomial<GenPolynomial<C>> q = S.ring.getZERO().clone();
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( f.multipleOf(e) ) {
                GenPolynomial<C> a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                GenPolynomial<C> x = PolyUtil.<C>basePseudoRemainder(a,c);
                if ( x.isZERO() ) {
                   GenPolynomial<C> y = PolyUtil.<C>basePseudoDivide(a,c);
                   q = q.sum( y, f );
                   h = S.multiply( y, f ); // coeff a
                } else {
                   q = q.sum( a, f );
                   q = q.multiply( c );
                   r = r.multiply( c );    // coeff ac
                   h = S.multiply( a, f ); // coeff ac
                }
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return q;
    }


    /**
     * GenPolynomial pseudo divide.
     * For recursive polynomials.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @param s nonzero GenPolynomial.
     * @return quotient with ldcf(s)<sup>m</sup> P = quotient * s + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           coefficientPseudoDivide( GenPolynomial<GenPolynomial<C>> P, 
                                    GenPolynomial<C> s) {
        if ( s == null || s.isZERO() ) {
           throw new RuntimeException(" division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        GenPolynomial<GenPolynomial<C>> p = P.ring.getZERO().clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> pv = p.val;
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : P.getMap().entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> c1 = m.getValue();
            GenPolynomial<C> c = basePseudoDivide(c1,s);
            if ( false ) {
                GenPolynomial<C> x = c1.remainder(s);
                if ( !x.isZERO() ) {
                   System.out.println("divide x = " + x);
                   throw new RuntimeException(" no exact division: " + c1 + "/" + s);
                }
            }
            if ( c.isZERO() ) {
               System.out.println(" no exact division: " + c1 + "/" + s);
               //throw new RuntimeException(" no exact division: " + c1 + "/" + s);
            } else {
               pv.put( e, c ); // or m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenPolynomial pseudo divide.
     * For polynomials.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @param s nonzero coefficient.
     * @return quotient with ldcf(s)<sup>m</sup> P = quotient * s + remainder.
     * @see edu.jas.poly.GenPolynomial#remainder(edu.jas.poly.GenPolynomial).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> coefficientBasePseudoDivide( GenPolynomial<C> P, C s) {
        if ( s == null || s.isZERO() ) {
           throw new RuntimeException(" division by zero");
        }
        if ( P.isZERO() ) {
            return P;
        }
        GenPolynomial<C> p = P.ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector e = m.getKey();
            C c1 = m.getValue();
            C c = c1.divide(s);
            if ( false ) {
                C x = c1.remainder(s);
                if ( !x.isZERO() ) {
                   System.out.println("divide x = " + x);
                   throw new RuntimeException(" no exact division: " + c1 + "/" + s);
                }
            }
            if ( c.isZERO() ) {
               System.out.println(" no exact division: " + c1 + "/" + s);
               //throw new RuntimeException(" no exact division: " + c1 + "/" + s);
            } else {
               pv.put( e, c ); // or m1.setValue( c )
            }
        }
        return p;
    }


    /**
     * GenPolynomial polynomial derivative main variable.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @return deriviative(P).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           baseDeriviative( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        GenPolynomial<C> d = pfac.getZERO().clone();
        Map<ExpVector,C> dm = d.val; //getMap();
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               C a = m.getValue(); 
               C x = a.multiply(cf);
               if ( x != null && !x.isZERO() ) {
                  ExpVector e = ExpVector.create( 1, 0, fl-1L );  
                  dm.put(e,x);
               }
            }
        }
        return d; 
    }


    /**
     * GenPolynomial polynomial integral main variable.
     * @param <C> coefficient type.
     * @param P GenPolynomial.
     * @return integral(P).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<C> 
           baseIntegral( GenPolynomial<C> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<C> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        RingFactory<C> rf = pfac.coFac;
        GenPolynomial<C> d = pfac.getZERO().clone();
        Map<ExpVector,C> dm = d.val; //getMap();
        for ( Map.Entry<ExpVector,C> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            fl++;
            C cf = rf.fromInteger( fl );
            C a = m.getValue(); 
            C x = a.divide(cf);
            if ( x != null && !x.isZERO() ) {
                ExpVector e = ExpVector.create( 1, 0, fl );  
                dm.put(e,x);
            }
        }
        return d; 
    }


    /**
     * GenPolynomial recursive polynomial derivative main variable.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial.
     * @return deriviative(P).
     */
    public static <C extends RingElem<C>>
           GenPolynomial<GenPolynomial<C>> 
           recursiveDeriviative( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null || P.isZERO() ) {
            return P;
        }
        GenPolynomialRing<GenPolynomial<C>> pfac = P.ring;
        if ( pfac.nvar > 1 ) { 
           // baseContent not possible by return type
           throw new RuntimeException(P.getClass().getName()
                     + " only for univariate polynomials");
        }
        GenPolynomialRing<C> pr = (GenPolynomialRing<C>)pfac.coFac;
        RingFactory<C> rf = pr.coFac;
        GenPolynomial<GenPolynomial<C>> d = pfac.getZERO().clone();
        Map<ExpVector,GenPolynomial<C>> dm = d.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : P.getMap().entrySet() ) {
            ExpVector f = m.getKey();  
            long fl = f.getVal(0);
            if ( fl > 0 ) {
               C cf = rf.fromInteger( fl );
               GenPolynomial<C> a = m.getValue(); 
               GenPolynomial<C> x = a.multiply(cf);
               if ( x != null && !x.isZERO() ) {
                  ExpVector e = ExpVector.create( 1, 0, fl-1L );  
                  dm.put(e,x);
               }
            }
        }
        return d; 
    }


    /**
     * Factor coefficient bound.
     * See SACIPOL.IPFCB: the product of all maxNorms of potential factors
     * is less than or equal to 2**b times the maxNorm of A.
     * @param e degree vector of a GenPolynomial A.
     * @return 2**b.
     */
    public static BigInteger factorBound(ExpVector e) {
        int n = 0;
        java.math.BigInteger p = java.math.BigInteger.ONE;
        java.math.BigInteger v;
        if ( e == null || e.isZERO() ) {
           return BigInteger.ONE;
        }
        for ( int i = 0; i < e.length(); i++ ) {
            if ( e.getVal(i) > 0 ) {
               n += ( 2*e.getVal(i) - 1 );
               v = new java.math.BigInteger( "" + (e.getVal(i) - 1) );
               p = p.multiply( v );
            }
        }
        n += ( p.bitCount() + 1 ); // log2(p)
        n /= 2;
        v = new java.math.BigInteger( "" + 2 );
        v = v.shiftLeft( n );
        BigInteger N = new BigInteger( v );
        return N;
    }


    /**
     * Evaluate at main variable. 
     * @param <C> coefficient type.
     * @param cfac coefficent polynomial ring factory.
     * @param A polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( x_1, ..., x_{n-1}, a ).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C> 
        evaluateMain( GenPolynomialRing<C> cfac, 
                      GenPolynomial<GenPolynomial<C>> A,
                      C a ) {
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        if ( A.ring.nvar != 1 ) { // todo assert
           throw new RuntimeException("evaluateMain no univariate polynomial");
        }
        if ( a == null || a.isZERO() ) {
           return A.trailingBaseCoefficient();
        }
        // assert decending exponents, i.e. compatible term order
        Map<ExpVector,GenPolynomial<C>> val = A.getMap();
        GenPolynomial<C> B = null;
        long el1 = -1; // undefined
        long el2 = -1;
        for ( ExpVector e : val.keySet() ) {
            el2 = e.getVal(0);
            if ( B == null /*el1 < 0*/ ) { // first turn
               B = val.get( e );
            } else {
               for ( long i = el2; i < el1; i++ ) {
                   B = B.multiply( a );
               }
               B = B.sum( val.get( e ) );
            }
            el1 = el2;
        }
        for ( long i = 0; i < el2; i++ ) {
            B = B.multiply( a );
        }
        return B;
    }


    /**
     * Evaluate at main variable. 
     * @param <C> coefficient type.
     * @param cfac coefficent ring factory.
     * @param A univariate polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( a ).
     */
    public static <C extends RingElem<C>> 
        C 
        evaluateMain( RingFactory<C> cfac, 
                      GenPolynomial<C> A,
                      C a ) {
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        if ( A.ring.nvar != 1 ) { // todo assert
           throw new RuntimeException("evaluateMain no univariate polynomial");
        }
        if ( a == null || a.isZERO() ) {
           return A.trailingBaseCoefficient();
        }
        // assert decreasing exponents, i.e. compatible term order
        Map<ExpVector,C> val = A.getMap();
        C B = null;
        long el1 = -1; // undefined
        long el2 = -1;
        for ( ExpVector e : val.keySet() ) {
            el2 = e.getVal(0);
            if ( B == null /*el1 < 0*/ ) { // first turn
               B = val.get( e );
            } else {
               for ( long i = el2; i < el1; i++ ) {
                   B = B.multiply( a );
               }
               B = B.sum( val.get( e ) );
            }
            el1 = el2;
        }
        for ( long i = 0; i < el2; i++ ) {
            B = B.multiply( a );
        }
        return B;
    }


    /**
     * Evaluate at main variable. 
     * @param <C> coefficient type.
     * @param cfac coefficent ring factory.
     * @param L list of univariate polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return list( A( a ) ) for A in L.
     */
    public static <C extends RingElem<C>> 
        List<C> 
        evaluateMain( RingFactory<C> cfac, 
                      List<GenPolynomial<C>> L,
                      C a ) {
        return ListUtil.<GenPolynomial<C>,C>map( L, new EvalMain<C>(cfac,a) );
    }


    /**
     * Evaluate at k-th variable. 
     * @param <C> coefficient type.
     * @param cfac coefficient polynomial ring in k variables 
     *        C[x_1, ..., x_k] factory.
     * @param rfac coefficient polynomial ring 
     *        C[x_1, ..., x_{k-1}] [x_k] factory,
     *        a recursive polynomial ring in 1 variable with 
     *        coefficients in k-1 variables.
     * @param nfac polynomial ring in n-1 varaibles
     *        C[x_1, ..., x_{k-1}] [x_{k+1}, ..., x_n] factory,
     *        a recursive polynomial ring in n-k+1 variables with 
     *        coefficients in k-1 variables.
     * @param dfac polynomial ring in n-1 variables.
     *        C[x_1, ..., x_{k-1}, x_{k+1}, ..., x_n] factory.
     * @param A polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( x_1, ..., x_{k-1}, a, x_{k+1}, ..., x_n).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C>
        evaluate( GenPolynomialRing<C> cfac,
                  GenPolynomialRing<GenPolynomial<C>> rfac, 
                  GenPolynomialRing<GenPolynomial<C>> nfac, 
                  GenPolynomialRing<C> dfac,
                  GenPolynomial<C> A,
                  C a ) {
        if ( rfac.nvar != 1 ) { // todo assert
           throw new RuntimeException("evaluate coefficient ring not univariate");
        }
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        Map<ExpVector,GenPolynomial<C>> Ap = A.contract(cfac);
        GenPolynomialRing<C> rcf = (GenPolynomialRing<C>)rfac.coFac;
        GenPolynomial<GenPolynomial<C>> Ev = nfac.getZERO().clone();
        Map<ExpVector,GenPolynomial<C>> Evm = Ev.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : Ap.entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> b = m.getValue();
            GenPolynomial<GenPolynomial<C>> c = recursive( rfac, b );
            GenPolynomial<C> d = evaluateMain(rcf,c,a);
            if ( d != null && !d.isZERO() ) {
               Evm.put(e,d);
            }
        }
        GenPolynomial<C> B = distribute(dfac,Ev);
        return B;
    }


    /**
     * Evaluate at first (lowest) variable. 
     * @param <C> coefficient type.
     * @param cfac coefficient polynomial ring in first variable 
     *        C[x_1] factory.
     * @param dfac polynomial ring in n-1 variables.
     *        C[x_2, ..., x_n] factory.
     * @param A polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( a, x_2, ..., x_n).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C>
        evaluateFirst( GenPolynomialRing<C> cfac,
                       GenPolynomialRing<C> dfac,
                       GenPolynomial<C> A,
                       C a ) {
        if ( A == null || A.isZERO() ) {
           return dfac.getZERO();
        }
        Map<ExpVector,GenPolynomial<C>> Ap = A.contract(cfac);
        //RingFactory<C> rcf = cfac.coFac; // == dfac.coFac

        GenPolynomial<C> B = dfac.getZERO().clone();
        Map<ExpVector,C> Bm = B.val; //getMap();

        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : Ap.entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> b = m.getValue();
            C d = evaluateMain(cfac.coFac,b,a);
            if ( d != null && !d.isZERO() ) {
               Bm.put(e,d);
            }
        }
        return B;
    }


    /**
     * Evaluate at first (lowest) variable. 
     * @param <C> coefficient type.
     * Could also be called evaluateFirst(), but type erasure of A parameter
     * does not allow same name.
     * @param cfac coefficient polynomial ring in first variable 
     *        C[x_1] factory.
     * @param dfac polynomial ring in n-1 variables.
     *        C[x_2, ..., x_n] factory.
     * @param A recursive polynomial to be evaluated.
     * @param a value to evaluate at.
     * @return A( a, x_2, ..., x_n).
     */
    public static <C extends RingElem<C>> 
        GenPolynomial<C>
        evaluateFirstRec( GenPolynomialRing<C> cfac,
                          GenPolynomialRing<C> dfac,
                          GenPolynomial<GenPolynomial<C>> A,
                          C a ) {
        if ( A == null || A.isZERO() ) {
           return dfac.getZERO();
        }
        Map<ExpVector,GenPolynomial<C>> Ap = A.getMap();
        GenPolynomial<C> B = dfac.getZERO().clone();
        Map<ExpVector,C> Bm = B.val; //getMap();
        for ( Map.Entry<ExpVector,GenPolynomial<C>> m : Ap.entrySet() ) {
            ExpVector e = m.getKey();
            GenPolynomial<C> b = m.getValue();
            C d = evaluateMain(cfac.coFac,b,a);
            if ( d != null && !d.isZERO() ) {
               Bm.put(e,d);
            }
        }
        return B;
    }


    /**
     * Evaluate all variables. 
     * @param <C> coefficient type.
     * @param cfac coefficient ring factory.
     * @param dfac polynomial ring in n variables.
     *        C[x_1, x_2, ..., x_n] factory.
     * @param A polynomial to be evaluated.
     * @param a = ( a_1, a_2, ..., a_n) a tuple of values to evaluate at.
     * @return A( a_1, a_2, ..., a_n).
     */
    public static <C extends RingElem<C>> 
        C
        evaluateAll( RingFactory<C> cfac,
                     GenPolynomialRing<C> dfac,
                     GenPolynomial<C> A,
                     List<C> a ) {
        if ( A == null || A.isZERO() ) {
           return cfac.getZERO();
        }
        if ( a == null || a.size() != dfac.nvar ) {
           throw new RuntimeException("evaluate tuple size not equal to number of variables");
        }
        if ( dfac.nvar == 0 ) {
            return A.trailingBaseCoefficient();
        }
        if ( dfac.nvar == 1 ) {
            return evaluateMain(cfac,A,a.get(0));
        }
        C b = cfac.getZERO();
        GenPolynomial<C> Ap = A;
        for ( int k = 0; k < dfac.nvar-1; k++ ) {
            C ap = a.get(k);
            GenPolynomialRing<C> c1fac = new GenPolynomialRing<C>(cfac,1);
            GenPolynomialRing<C> cnfac = new GenPolynomialRing<C>(cfac,dfac.nvar-1-k);
            GenPolynomial<C> Bp = evaluateFirst(c1fac,cnfac,Ap,ap);
            if ( Bp.isZERO() ) {
                return b;
            }
            Ap = Bp;
            //System.out.println("Ap = " + Ap);
        }
        C ap = a.get(dfac.nvar-1);
        b = evaluateMain(cfac,Ap,ap);
        return b;
    }


    /**
     * Substitute main variable. 
     * @param A univariate polynomial.
     * @param s polynomial for substitution.
     * @return polynomial A(x <- s).
     */
    public static <C extends RingElem<C>>
      GenPolynomial<C> substituteMain( GenPolynomial<C> A, GenPolynomial<C> s ) {
        return substituteUnivariate(A,s);
    }


    /**
     * Substitute univariate polynomial. 
     * @param f univariate polynomial.
     * @param t polynomial for substitution.
     * @return polynomial A(x <- t).
     */
    public static <C extends RingElem<C>>
      GenPolynomial<C> substituteUnivariate( GenPolynomial<C> f, GenPolynomial<C> t ) {
        if (f == null||t == null) {
            return null;
        }
        GenPolynomialRing<C> fac = f.ring;
        if (fac.nvar > 1 || t.ring.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        if (f.isZERO() || f.isConstant()) {
            return f;
        }
        // assert decending exponents, i.e. compatible term order
        Map<ExpVector, C> val = f.getMap();
        GenPolynomial<C> s = null;
        long el1 = -1; // undefined
        long el2 = -1;
        for (ExpVector e : val.keySet()) {
            el2 = e.getVal(0);
            if (s == null /*el1 < 0*/) { // first turn
                s = fac.getZERO().sum(val.get(e));
            } else {
                for (long i = el2; i < el1; i++) {
                    s = s.multiply(t);
                }
                s = s.sum(val.get(e));
            }
            el1 = el2;
        }
        for (long i = 0; i < el2; i++) {
            s = s.multiply(t);
        }
        //System.out.println("s = " + s);
        return s;
    }


    /**
     * Taylor series for polynomial.
     * @param f univariate polynomial.
     * @param a expansion point.
     * @return Taylor series (a polynomial) of f at a.
     */
    public static <C extends RingElem<C>> GenPolynomial<C> seriesOfTaylor(GenPolynomial<C> f, C a) {
        if (f == null) {
            return null;
        }
        GenPolynomialRing<C> fac = f.ring;
        if (fac.nvar > 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        if (f.isZERO() || f.isConstant()) {
            return f;
        }
        GenPolynomial<C> s = fac.getZERO();
        C fa = PolyUtil.<C> evaluateMain(fac.coFac, f, a);
        s = s.sum(fa);
        long n = 1;
        long i = 0;
        GenPolynomial<C> g = PolyUtil.<C> baseDeriviative(f);
        GenPolynomial<C> p = fac.getONE();
        while (!g.isZERO()) {
            i++;
            n *= i;
            fa = PolyUtil.<C> evaluateMain(fac.coFac, g, a);
            GenPolynomial<C> q = fac.univariate(0, i); //p;
            q = q.multiply(fa);
            q = q.divide(fac.fromInteger(n));
            s = s.sum(q);
            g = PolyUtil.<C> baseDeriviative(g);
        }
        //System.out.println("s = " + s);
        return s;
    }


    /** ModInteger interpolate on first variable.
     * @param <C> coefficient type.
     * @param fac GenPolynomial<C> result factory.
     * @param A GenPolynomial<C>.
     * @param M GenPolynomial<C> interpolation modul of A.
     * @param mi inverse of M(am) in ring fac.coFac.
     * @param B evaluation of other GenPolynomial<C>.
     * @param am evaluation point (interpolation modul) of B, i.e. P(am) = B.
     * @return S, with S mod M == A and S(am) == B.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<GenPolynomial<C>> 
        interpolate( GenPolynomialRing<GenPolynomial<C>> fac,
                     GenPolynomial<GenPolynomial<C>> A,
                     GenPolynomial<C> M,
                     C mi,
                     GenPolynomial<C> B, 
                     C am ) {
        GenPolynomial<GenPolynomial<C>> S = fac.getZERO().clone(); 
        GenPolynomial<GenPolynomial<C>> Ap = A.clone(); 
        SortedMap<ExpVector,GenPolynomial<C>> av = Ap.val; //getMap();
        SortedMap<ExpVector,C> bv = B.getMap();
        SortedMap<ExpVector,GenPolynomial<C>> sv = S.val; //getMap();
        GenPolynomialRing<C> cfac = (GenPolynomialRing<C>)fac.coFac; 
        RingFactory<C> bfac = cfac.coFac; 
        GenPolynomial<C> c = null;
        for ( ExpVector e : bv.keySet() ) {
            GenPolynomial<C> x = av.get( e );
            C y = bv.get( e ); // assert y != null
            if ( x != null ) {
               av.remove( e );
               c = PolyUtil.<C>interpolate(cfac,x,M,mi,y,am);
               if ( ! c.isZERO() ) { // 0 cannot happen
                   sv.put( e, c );
               }
            } else {
               c = PolyUtil.<C>interpolate(cfac,cfac.getZERO(),M,mi,y,am);
               if ( ! c.isZERO() ) { // 0 cannot happen
                  sv.put( e, c ); // c != null
               }
            }
        }
        // assert bv is empty = done
        for ( ExpVector e : av.keySet() ) { // rest of av
            GenPolynomial<C> x = av.get( e ); // assert x != null
            c = PolyUtil.<C>interpolate(cfac,x,M,mi,bfac.getZERO(),am);
            if ( ! c.isZERO() ) { // 0 cannot happen
               sv.put( e, c ); // c != null
            }
        }
        return S;
    }


    /** Univariate polynomial interpolation.
     * @param <C> coefficient type.
     * @param fac GenPolynomial<C> result factory.
     * @param A GenPolynomial<C>.
     * @param M GenPolynomial<C> interpolation modul of A.
     * @param mi inverse of M(am) in ring fac.coFac.
     * @param a evaluation of other GenPolynomial<C>.
     * @param am evaluation point (interpolation modul) of a, i.e. P(am) = a.
     * @return S, with S mod M == A and S(am) == a.
     */
    public static <C extends RingElem<C>>
        GenPolynomial<C> 
        interpolate( GenPolynomialRing<C> fac,
                     GenPolynomial<C> A,
                     GenPolynomial<C> M,
                     C mi,
                     C a, 
                     C am ) {
        GenPolynomial<C> s; 
        C b = PolyUtil.<C>evaluateMain( fac.coFac, A, am ); 
                              // A mod a.modul
        C d = a.subtract( b ); // a-A mod a.modul
        if ( d.isZERO() ) {
           return A;
        }
        b = d.multiply( mi ); // b = (a-A)*mi mod a.modul
        // (M*b)+A mod M = A mod M = 
        // (M*mi*(a-A)+A) mod a.modul = a mod a.modul
        s = M.multiply( b );
        s = s.sum( A );
        return s;
    }


    /**
     * Recursive GenPolynomial switch varaible blocks.
     * @param <C> coefficient type.
     * @param P recursive GenPolynomial in R[X,Y].
     * @return this in R[Y,X].
     */
    public static <C extends RingElem<C>>
      GenPolynomial<GenPolynomial<C>> 
      switchVariables( GenPolynomial<GenPolynomial<C>> P ) {
        if ( P == null ) {
            throw new IllegalArgumentException("P == null");
        }
        GenPolynomialRing<GenPolynomial<C>> rfac1 = P.ring;
        GenPolynomialRing<C> cfac1 = (GenPolynomialRing<C>) rfac1.coFac;
        GenPolynomialRing<C> cfac2 = new GenPolynomialRing<C>(cfac1.coFac,rfac1);
        GenPolynomial<C> zero = cfac2.getZERO(); 
        GenPolynomialRing<GenPolynomial<C>> rfac2 
            = new GenPolynomialRing<GenPolynomial<C>>(cfac2,cfac1);
        GenPolynomial<GenPolynomial<C>> B = rfac2.getZERO().clone(); 
        if ( P.isZERO() ) {
            return B;
        }
        for ( Monomial<GenPolynomial<C>> mr : P ) {
            GenPolynomial<C> cr = mr.c;
            for ( Monomial<C> mc : cr ) {
                GenPolynomial<C> c = zero.sum(mc.c,mr.e);
                B = B.sum(c,mc.e);
            }
        }
        return B;
    }


    /**
     * Maximal degree in the coefficient polynomials.
     * @param <C> coefficient type.
     * @return maximal degree in the coefficients.
     */
    public static <C extends RingElem<C>>
           long 
           coeffMaxDegree(GenPolynomial<GenPolynomial<C>> A) {
        if ( A.isZERO() ) {
           return 0; // 0 or -1 ?;
        }
        long deg = 0;
        for ( GenPolynomial<C> a : A.getMap().values() ) {
            long d = a.degree();
            if ( d > deg ) {
               deg = d;
            }
        }
        return deg;
    }


    /**
     * Map a unary function to the coefficients.
     * @param ring result polynomial ring factory.
     * @param p polynomial.
     * @param f evaluation functor.
     * @return new polynomial with coefficients f(p(e)).
     */
    public static <C extends RingElem<C>, D extends RingElem<D>>
           GenPolynomial<D> map(GenPolynomialRing<D> ring,
                                GenPolynomial<C> p,
                                UnaryFunctor<C,D> f) {
        GenPolynomial<D> n = ring.getZERO().clone(); 
        SortedMap<ExpVector,D> nv = n.val;
        for ( Monomial<C> m : p ) {
            D c = f.eval( m.c );
            if ( c != null && !c.isZERO() ) {
                nv.put( m.e, c );
            }
        }
        return n;
    }

}



/**
 * Conversion of distributive to recursive representation.
 */
class DistToRec<C extends RingElem<C>> 
               implements UnaryFunctor<GenPolynomial<C>,GenPolynomial<GenPolynomial<C>>> {
    GenPolynomialRing<GenPolynomial<C>> fac;
    public DistToRec(GenPolynomialRing<GenPolynomial<C>> fac) {
        this.fac = fac;
    }
    public GenPolynomial<GenPolynomial<C>> eval(GenPolynomial<C> c) {
        if ( c == null ) {
            return fac.getZERO();
        } else {
            return PolyUtil.<C>recursive( fac, c );
        }
    }
}


/**
 * Conversion of recursive to distributive representation.
 */
class RecToDist<C extends RingElem<C>> 
    implements UnaryFunctor<GenPolynomial<GenPolynomial<C>>,GenPolynomial<C>> {
    GenPolynomialRing<C> fac;
    public RecToDist(GenPolynomialRing<C> fac) {
        this.fac = fac;
    }
    public GenPolynomial<C> eval(GenPolynomial<GenPolynomial<C>> c) {
        if ( c == null ) {
            return fac.getZERO();
        } else {
            return PolyUtil.<C>distribute( fac, c );
        }
    }
}


/**
 * BigRational numerator functor.
 */
class RatNumer implements UnaryFunctor<BigRational,BigInteger> {
    public BigInteger eval(BigRational c) {
        if ( c == null ) {
            return new BigInteger();
        } else {
            return new BigInteger( c.numerator() );
        }
    }
}


/**
 * Conversion of symmetric ModInteger to BigInteger functor.
 */
class ModSymToInt<C extends RingElem<C> & Modular> implements UnaryFunctor<C,BigInteger> {
    public BigInteger eval(C c) {
        if ( c == null ) {
            return new BigInteger();
        } else {
            return c.getSymmetricInteger();
        } 
    }
}


/**
 * Conversion of ModInteger to BigInteger functor.
 */
class ModToInt<C extends RingElem<C> & Modular> implements UnaryFunctor<C,BigInteger> {
    public BigInteger eval(C c) {
        if ( c == null ) {
            return new BigInteger();
        } else {
            return c.getInteger();
        }
    }
}


/**
 * Conversion of BigRational to BigInteger with division by lcm functor.
 * result = num*(lcm/denom).
 */
class RatToInt implements UnaryFunctor<BigRational,BigInteger> {
    java.math.BigInteger lcm;
    public RatToInt(java.math.BigInteger lcm) {
        this.lcm = lcm; //.getVal();
    }
    public BigInteger eval(BigRational c) {
        if ( c == null ) {
            return new BigInteger();
        } else {
            // p = num*(lcm/denom)
            java.math.BigInteger b = lcm.divide( c.denominator() );
            return new BigInteger( c.numerator().multiply( b ) );
        }
    }
}


/**
 * Conversion of BigRational to BigInteger. 
 * result = (num/gcd)*(lcm/denom).
 */
class RatToIntFactor implements UnaryFunctor<BigRational, BigInteger> {
    final java.math.BigInteger lcm;
    final java.math.BigInteger gcd;

    public RatToIntFactor(java.math.BigInteger gcd, java.math.BigInteger lcm) {
        this.gcd = gcd;
        this.lcm = lcm; // .getVal();
    }

    public BigInteger eval(BigRational c) {
        if (c == null) {
            return new BigInteger();
        } else {
            if (gcd.equals(java.math.BigInteger.ONE)) {
                // p = num*(lcm/denom)
                java.math.BigInteger b = lcm.divide(c.denominator());
                return new BigInteger(c.numerator().multiply(b));
            } else {
                // p = (num/gcd)*(lcm/denom)
                java.math.BigInteger a = c.numerator().divide(gcd);
                java.math.BigInteger b = lcm.divide(c.denominator());
                return new BigInteger(a.multiply(b));
            }
        }
    }
}


/**
 * Conversion of Rational to BigDecimal.
 * result = decimal(r).
 */
class RatToDec<C extends Element<C> & Rational> implements UnaryFunctor<C,BigDecimal> {
    public BigDecimal eval(C c) {
        if ( c == null ) {
            return new BigDecimal();
        } else {
            return new BigDecimal(c.getRational());
        }
    }
}


/**
 * Conversion of Complex Rational to Complex BigDecimal.
 * result = decimal(r).
 */
class CompRatToDec<C extends RingElem<C> & Rational> implements UnaryFunctor<Complex<C>,Complex<BigDecimal>> {
    ComplexRing<BigDecimal> ring;
    public CompRatToDec(RingFactory<Complex<BigDecimal>> ring) {
        this.ring = (ComplexRing<BigDecimal>) ring;
    }
    public Complex<BigDecimal> eval(Complex<C> c) {
        if ( c == null ) {
            return ring.getZERO();
        } else {
            BigDecimal r = new BigDecimal( c.getRe().getRational() );
            BigDecimal i = new BigDecimal( c.getIm().getRational() );
            return new Complex<BigDecimal>(ring,r,i);
        }
    }
}


/**
 * Conversion from BigInteger functor.
 */
class FromInteger<D extends RingElem<D>> implements UnaryFunctor<BigInteger,D> {
    RingFactory<D> ring;
    public FromInteger(RingFactory<D> ring) {
        this.ring = ring;
    }
    public D eval(BigInteger c) {
        if ( c == null ) {
            return ring.getZERO();
        } else {
            return ring.fromInteger( c.getVal() );
        }
    }
}


/**
 * Conversion from GenPolynomial<BigInteger> functor.
 */
class FromIntegerPoly<D extends RingElem<D>> 
      implements UnaryFunctor<GenPolynomial<BigInteger>,GenPolynomial<D>> {

    GenPolynomialRing<D> ring;
    FromInteger<D> fi;


    public FromIntegerPoly(GenPolynomialRing<D> ring) {
        if ( ring == null ) {
            throw new IllegalArgumentException("ring must not be null");
        }
        this.ring = ring;
        fi = new FromInteger<D>(ring.coFac);
    }


    public GenPolynomial<D> eval(GenPolynomial<BigInteger> c) {
        if ( c == null ) {
            return ring.getZERO();
        } else {
            return PolyUtil.<BigInteger,D>map( ring, c, fi );
        }
    }
}


/**
 * Conversion from GenPolynomial<BigRational> to GenPolynomial<BigInteger> functor.
 */
class RatToIntPoly
    implements UnaryFunctor<GenPolynomial<BigRational>,GenPolynomial<BigInteger>> {

    GenPolynomialRing<BigInteger> ring;

    public RatToIntPoly(GenPolynomialRing<BigInteger> ring) {
        if ( ring == null ) {
            throw new IllegalArgumentException("ring must not be null");
        }
        this.ring = ring;
    }

    public GenPolynomial<BigInteger> eval(GenPolynomial<BigRational> c) {
        if ( c == null ) {
            return ring.getZERO();
        } else {
            return PolyUtil.integerFromRationalCoefficients( ring, c );
        }
    }
}


/**
 * Real part functor.
 */
class RealPart implements UnaryFunctor<BigComplex,BigRational> {
    public BigRational eval(BigComplex c) {
        if ( c == null ) {
            return new BigRational();
        } else {
            return c.getRe();
        }
    }
}


/**
 * Imaginary part functor.
 */
class ImagPart implements UnaryFunctor<BigComplex,BigRational> {
    public BigRational eval(BigComplex c) {
        if ( c == null ) {
            return new BigRational();
        } else {
            return c.getIm();
        }
    }
}


/**
 * Real part functor.
 */
class RealPartComplex<C extends RingElem<C>> implements UnaryFunctor<Complex<C>, C> {
    public C eval(Complex<C> c) {
        if (c == null) {
            return null;
        } else {
            return c.getRe();
        }
    }
}


/**
 * Imaginary part functor.
 */
class ImagPartComplex<C extends RingElem<C>> implements UnaryFunctor<Complex<C>, C> {
    public C eval(Complex<C> c) {
        if (c == null) {
            return null;
        } else {
            return c.getIm();
        }
    }
}


/**
 * Rational to complex functor.
 */
class ToComplex<C extends RingElem<C>> implements UnaryFunctor<C,Complex<C>> {
    final protected ComplexRing<C> cfac;

    @SuppressWarnings("unchecked")
    public ToComplex(RingFactory<Complex<C>> fac) {
        if ( fac == null ) {
            throw new IllegalArgumentException("fac must not be null");
        }
        cfac = (ComplexRing<C>)fac;
    }

    public Complex<C> eval(C c) {
        if ( c == null ) {
            return cfac.getZERO();
        } else {
            return new Complex<C>(cfac, c );
        }
    }
}


/**
 * Rational to complex functor.
 */
class RatToCompl implements UnaryFunctor<BigRational,BigComplex> {
    public BigComplex eval(BigRational c) {
        if ( c == null ) {
            return new BigComplex();
        } else {
            return new BigComplex( c );
        }
    }
}


/**
 * Algebraic to generic complex functor.
 */
class AlgebToCompl<C extends GcdRingElem<C>> implements UnaryFunctor<AlgebraicNumber<C>,Complex<C>> {
    final protected ComplexRing<C> cfac;
    public AlgebToCompl(ComplexRing<C> fac) {
        if ( fac == null ) {
            throw new IllegalArgumentException("fac must not be null");
        }
        cfac = fac;
    }
    public Complex<C> eval(AlgebraicNumber<C> a) {
        if ( a == null || a.isZERO() ) { // should not happen
            return cfac.getZERO();
        } else if ( a.isONE() ) {
            return cfac.getONE();
        } else {
            GenPolynomial<C> p = a.getVal();
            C real = cfac.ring.getZERO();
            C imag = cfac.ring.getZERO();
            for ( Monomial<C> m : p ) {
                if ( m.exponent().getVal(0) == 1L ) {
                    imag = m.coefficient();
                } else if ( m.exponent().getVal(0) == 0L ) {
                    real = m.coefficient();
                } else {
                    throw new IllegalArgumentException("unexpected monomial " + m);
                }
            }
            //Complex<C> c = new Complex<C>(cfac,real,imag);
            return new Complex<C>(cfac,real,imag);
        }
    }
}


/**
 * Ceneric complex to algebraic number functor.
 */
class ComplToAlgeb<C extends GcdRingElem<C>> implements UnaryFunctor<Complex<C>,AlgebraicNumber<C>> {
    final protected AlgebraicNumberRing<C> afac;
    final protected AlgebraicNumber<C> I;
    public ComplToAlgeb(AlgebraicNumberRing<C> fac) {
        if ( fac == null ) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
        I = afac.getGenerator();
    }
    public AlgebraicNumber<C> eval(Complex<C> c) {
        if ( c == null || c.isZERO() ) { // should not happen
            return afac.getZERO();
        } else if ( c.isONE() ) {
            return afac.getONE();
        } else if ( c.isIMAG() ) {
            return I;
        } else {
            return I.multiply(c.getIm()).sum(c.getRe());
        }
    }
}


/**
 * Algebraic to polynomial functor.
 */
class AlgToPoly<C extends GcdRingElem<C>> 
               implements UnaryFunctor<AlgebraicNumber<C>,GenPolynomial<C>> {
    public GenPolynomial<C> eval(AlgebraicNumber<C> c) {
        if ( c == null ) {
            return null;
        } else {
            return c.val;
        }
    }
}


/**
 * Polynomial to algebriac functor.
 */
class PolyToAlg<C extends GcdRingElem<C>> 
               implements UnaryFunctor<GenPolynomial<C>,AlgebraicNumber<C>> {

    final protected AlgebraicNumberRing<C> afac;

    public PolyToAlg(AlgebraicNumberRing<C> fac) {
        if ( fac == null ) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
    }

    public AlgebraicNumber<C> eval(GenPolynomial<C> c) {
        if ( c == null ) {
            return afac.getZERO();
        } else {
            return new AlgebraicNumber<C>(afac,c);
        }
    }
}


/**
 * Coefficient to algebriac functor.
 */
class CoeffToAlg<C extends GcdRingElem<C>> 
                implements UnaryFunctor<C,AlgebraicNumber<C>> {

    final protected AlgebraicNumberRing<C> afac;
    final protected GenPolynomial<C> zero;

    public CoeffToAlg(AlgebraicNumberRing<C> fac) {
        if ( fac == null ) {
            throw new IllegalArgumentException("fac must not be null");
        }
        afac = fac;
        GenPolynomialRing<C> pfac = afac.ring;
        zero = pfac.getZERO();
    }

    public AlgebraicNumber<C> eval(C c) {
        if ( c == null ) {
            return afac.getZERO();
        } else {
            return new AlgebraicNumber<C>(afac,zero.sum(c));
        }
    }
}


/**
 * Coefficient to recursive algebriac functor.
 */
class CoeffToRecAlg<C extends GcdRingElem<C>> 
                implements UnaryFunctor<C,AlgebraicNumber<C>> {

    final protected List<AlgebraicNumberRing<C>> lfac;
    final int depth;

    @SuppressWarnings("unchecked")
    public CoeffToRecAlg(int depth, AlgebraicNumberRing<C> fac) {
        if ( fac == null ) {
            throw new IllegalArgumentException("fac must not be null");
        }
        AlgebraicNumberRing<C> afac = fac;
        this.depth = depth;
        lfac = new ArrayList<AlgebraicNumberRing<C>>(depth);
        lfac.add(fac);
        for ( int i = 1; i < depth; i++ ) {
            RingFactory<C> rf = afac.ring.coFac;
            if ( ! (rf instanceof AlgebraicNumberRing) ) {
                throw new IllegalArgumentException("fac depth to low");
            }
            afac = (AlgebraicNumberRing<C>) (Object)rf;
            lfac.add(afac);
        }
    }

    @SuppressWarnings("unchecked")
    public AlgebraicNumber<C> eval(C c) {
        if ( c == null ) {
            return lfac.get(0).getZERO();
        } 
        C ac = c;
        AlgebraicNumberRing<C> af = lfac.get( lfac.size()-1 );
        GenPolynomial<C> zero = af.ring.getZERO();
        AlgebraicNumber<C> an = new AlgebraicNumber<C>(af,zero.sum(ac));
        for ( int i = lfac.size()-2; i >= 0; i-- ) {
            af = lfac.get(i);
            zero = af.ring.getZERO();
            ac = (C) (Object) an;
            an = new AlgebraicNumber<C>(af,zero.sum(ac));
        }
        return an;
    }
}


/**
 * Evaluate main variable functor.
 */
class EvalMain<C extends RingElem<C>> 
    implements UnaryFunctor<GenPolynomial<C>,C> {
    final RingFactory<C> cfac;
    final C a;
    public EvalMain(RingFactory<C> cfac, C a) {
    this.cfac = cfac;
    this.a = a;
    }

    public C eval(GenPolynomial<C> c) {
        if ( c == null ) {
            return null;
        } else {
            return PolyUtil.<C>evaluateMain(cfac,c,a);
        }
    }
}

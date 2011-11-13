
/*
 * $Id: GenGcdPolynomial.java 2921 2009-12-25 17:06:56Z kredel $
 */

package edu.jas.poly;

import java.util.Map;
//import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.log4j.Logger;

import edu.jas.structure.GcdRingElem;
import edu.jas.poly.ExpVector;


/**
 * GenGcdPolynomial generic polynomials implementing RingElem.
 * n-variate ordered polynomials over C.
 * Objects of this class are intended to be immutable.
 * The implementation is based on TreeMap respectively SortedMap 
 * from exponents to coefficients.
 * Only the coefficients are modeled with generic types,
 * the exponents are fixed to ExpVector with long entries 
 * (this will eventually be changed in the future).
 * C can also be a non integral domain, e.g. a ModInteger, 
 * i.e. it may contain zero divisors, since multiply() does now check for zeros.
 * @author Heinz Kredel
 */

public class GenGcdPolynomial<C extends GcdRingElem<C> > 
    extends GenPolynomial<C> {


    /** The factory for the polynomial ring. 
     */
    public final GenGcdPolynomialRing< C > gring;


    /** The data structure for polynomials. 
     */
    //protected final SortedMap<ExpVector,C> val; // do not change to TreeMap


    private static final Logger logger = Logger.getLogger(GenGcdPolynomial.class);
    private static final boolean debug = logger.isDebugEnabled();


    // protected GenPolynomial() { ring = null; val = null; } // don't use


    /**
     * Private constructor for GenPolynomial.
     * @param r polynomial ring factory.
     * @param t TreeMap with correct ordering.
     */
    private GenGcdPolynomial(GenGcdPolynomialRing<C> r, TreeMap<ExpVector,C> t) {
        super(r,t);
        gring = r;
    }


    /**
     * Constructor for zero GenPolynomial.
     * @param r polynomial ring factory.
     */
    public GenGcdPolynomial(GenGcdPolynomialRing< C > r) {
        this(r, new TreeMap<ExpVector,C>( r.tord.getDescendComparator() ) );
    }


    /**
     * Constructor for GenGcdPolynomial c * x<sup>e</sup>.
     * @param r polynomial ring factory.
     * @param c coefficient.
     * @param e exponent.
     */
    public GenGcdPolynomial(GenGcdPolynomialRing< C > r, C c, ExpVector e) {
        this(r);
        if ( ! c.isZERO() ) {
           val.put(e,c);
        }
    }


    /**
     * Constructor for GenGcdPolynomial.
     * @param r polynomial ring factory.
     * @param v the SortedMap of some other polynomial.
     */
    protected GenGcdPolynomial(GenGcdPolynomialRing< C > r, 
                               SortedMap<ExpVector,C> v) {
        this(r);
        val.putAll( v ); // assume no zero coefficients
    }


    /**
     * Clone this GenPolynomial.
     * @see java.lang.Object#clone()
     */
    @Override
     public GenGcdPolynomial<C> clone() {
        //return ring.copy(this);
        return new GenGcdPolynomial<C>(gring,this.val);
    }


    /**
     * GenGcdPolynomial summation. 
     * @param S GenPolynomial.
     * @return this+S.
     */
    //public <T extends GenPolynomial<C>> T sum(T /*GenPolynomial<C>*/ S) {
    public GenGcdPolynomial<C> sum(GenGcdPolynomial<C> S) {
        return new GenGcdPolynomial<C>(gring, super.sum(S).val );
    }


    /**
     * GenGcdPolynomial addition. 
     * This method is not very efficient, since this is copied.
     * @param a coefficient.
     * @param e exponent.
     * @return this + a x<sup>e</sup>.
     */
    @Override
     public GenGcdPolynomial<C> sum(C a, ExpVector e) {
        return new GenGcdPolynomial<C>(gring, super.sum(a,e).val );
    }


    /**
     * GenGcdPolynomial subtraction. 
     * @param S GenPolynomial.
     * @return this-S.
     */
    public GenGcdPolynomial<C> subtract(GenGcdPolynomial<C> S) {
        return new GenGcdPolynomial<C>(gring, super.subtract(S).val );
    }


    /**
     * GenGcdPolynomial subtraction. 
     * This method is not very efficient, since this is copied.
     * @param a coefficient.
     * @param e exponent.
     * @return this - a x<sup>e</sup>.
     */
    @Override
     public GenGcdPolynomial<C> subtract(C a, ExpVector e) {
        return new GenGcdPolynomial<C>(gring, super.subtract(a,e).val );
    }


    /**
     * GenGcdPolynomial negation. 
     * @return -this.
     */
    @Override
     public GenGcdPolynomial<C> negate() {
        return new GenGcdPolynomial<C>(gring, super.negate().val );
    }


    /**
     * GenPolynomial absolute value, i.e. leadingCoefficient &gt; 0.
     * @return abs(this).
     */
    @Override
     public GenGcdPolynomial<C> abs() {
        return new GenGcdPolynomial<C>(gring, super.abs().val );
    }


    /**
     * GenGcdPolynomial multiplication. 
     * @param S GenPolynomial.
     * @return this*S.
     */
    public GenGcdPolynomial<C> multiply(GenGcdPolynomial<C> S) {
        return new GenGcdPolynomial<C>(gring, super.multiply(S).val );
    }


    /**
     * GenGcdPolynomial multiplication. 
     * Product with coefficient ring element.
     * @param s coefficient.
     * @return this*s.
     */
    @Override
     public GenGcdPolynomial<C> multiply(C s) {
        return new GenGcdPolynomial<C>(gring, super.multiply(s).val );
    }


    /**
     * GenGcdPolynomial monic, i.e. leadingCoefficient == 1.
     * If leadingCoefficient is not invertible returns this unmodified.
     * @return monic(this).
     */
    @Override
     public GenGcdPolynomial<C> monic() {
        return new GenGcdPolynomial<C>(gring, super.monic().val );
    }


    /**
     * GenGcdPolynomial multiplication. 
     * Product with ring element and exponent vector.
     * @param s coefficient.
     * @param e exponent.
     * @return this * s x<sup>e</sup>.
     */
    @Override
     public GenGcdPolynomial<C> multiply(C s, ExpVector e) {
        return new GenGcdPolynomial<C>(gring, super.multiply(s,e).val );
    }


    /**
     * GenGcdPolynomial multiplication. 
     * Product with exponent vector.
     * @param e exponent (!= null).
     * @return this * x<sup>e</sup>.
     */
    @Override
     public GenGcdPolynomial<C> multiply(ExpVector e) {
        return new GenGcdPolynomial<C>(gring, super.multiply(e).val );
    }


    /**
     * GenPolynomial multiplication. 
     * Product with 'monomial'.
     * @param m 'monomial'.
     * @return this * m.
     */
    @Override
     public GenGcdPolynomial<C> multiply(Map.Entry<ExpVector,C> m) {
        return new GenGcdPolynomial<C>(gring, super.multiply(m).val );
    }


    /**
     * GenPolynomial division. 
     * Division by coefficient ring element.
     * Fails, if exact division is not possible.
     * @param s coefficient.
     * @return this/s.
     */
    @Override
     public GenPolynomial<C> divide(C s) {
        if ( s == null || s.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        if ( this.isZERO() ) {
            return this;
        }
        GenPolynomial<C> p = ring.getZERO().clone(); 
        SortedMap<ExpVector,C> pv = p.val;
        for ( Map.Entry<ExpVector,C> m : val.entrySet() ) {
            ExpVector e = m.getKey();
            C c1 = m.getValue();
            C c = c1.divide(s);
            if ( true ) {
                C x = c1.remainder(s);
                if ( !x.isZERO() ) {
                   System.out.println("divide x = " + x);
                   throw new RuntimeException(this.getClass().getName()
                                + " no exact division: " + c1 + "/" + s);
                }
            }
            if ( c.isZERO() ) {
               throw new RuntimeException(this.getClass().getName()
                                + " no exact division: " + c1 + "/" + s);
            }
            pv.put( e, c ); // or m1.setValue( c )
        }
        return p;
    }


    /**
     * GenPolynomial division with remainder.
     * Fails, if exact division by leading base coefficient is not possible.
     * Meaningful only for univariate polynomials over fields, but works 
     * in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return [ quotient , remainder ] with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#basePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    @Override
     public GenPolynomial<C>[] divideAndRemainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            throw new RuntimeException(this.getClass().getName()
                                       + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if ( ! c.isUnit() ) {
           throw new RuntimeException(this.getClass().getName()
                                       + " lbcf not invertible " + c);
        }
        C ci = c.inverse();
     assert (ring.nvar == S.ring.nvar);
        ExpVector e = S.leadingExpVector();
        //System.out.println("e = " + e);
        GenPolynomial<C> h;
        GenPolynomial<C> q = ring.getZERO().clone();
        GenPolynomial<C> r = this.clone(); 
        //GenPolynomial<C> rx; 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            //System.out.println("f = " + f);
            if ( f.multipleOf(e) ) {
                C a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                //logger.info("red div = " + e);
                //C ax = a;
                a = a.multiply( ci );
                q = q.sum( a, f );
                h = S.multiply( a, f );
                //rx = r;
                r = r.subtract( h );
            } else {
                break;
            }
        }
        //System.out.println("q = " + q + ", r = " +r);
        GenPolynomial<C>[] ret = (GenPolynomial<C>[]) new GenPolynomial[2];
        ret[0] = q;
        ret[1] = r;
        return ret;
    }


    /**
     * GenPolynomial division.
     * Fails, if exact division by leading base coefficient is not possible.
     * Meaningful only for univariate polynomials over fields, but works 
     * in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return quotient with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#basePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    @Override
     public GenPolynomial<C> divide(GenPolynomial<C> S) {
        return divideAndRemainder(S)[0];
    }


    /**
     * GenPolynomial remainder.
     * Fails, if exact division by leading base coefficient is not possible.
     * Meaningful only for univariate polynomials over fields, but works 
     * in any case.
     * @param S nonzero GenPolynomial with invertible leading coefficient.
     * @return remainder with this = quotient * S + remainder.
     * @see edu.jas.poly.PolyUtil#basePseudoRemainder(edu.jas.poly.GenPolynomial,edu.jas.poly.GenPolynomial).
     */
    @Override
     public GenPolynomial<C> remainder(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        C c = S.leadingBaseCoefficient();
        if ( ! c.isUnit() ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " lbc not invertible " + c);
        }
        C ci = c.inverse();
     assert (ring.nvar == S.ring.nvar);
        ExpVector e = S.leadingExpVector();
        GenPolynomial<C> h;
        GenPolynomial<C> r = this.clone(); 
        while ( ! r.isZERO() ) {
            ExpVector f = r.leadingExpVector();
            if ( f.multipleOf(e) ) {
                C a = r.leadingBaseCoefficient();
                f =  f.subtract( e );
                //logger.info("red div = " + e);
                a = a.multiply( ci );
                h = S.multiply( a, f );
                r = r.subtract( h );
            } else {
                break;
            }
        }
        return r;
    }


    /**
     * GenPolynomial greatest common divisor.
     * Only for univariate polynomials over fields.
     * @param S GenPolynomial.
     * @return gcd(this,S).
     */
    @Override
     public GenPolynomial<C> gcd(GenPolynomial<C> S) {
        if ( S == null || S.isZERO() ) {
            return this;
        }
        if ( this.isZERO() ) {
            return S;
        }
        if ( ring.nvar != 1 /*|| !ring.coFac.isField()*/ ) {
           //throw new RuntimeException(this.getClass().getName()
           //                       + " not univariate polynomials" + ring);
           //System.out.println("this = " + this);
           //System.out.println("S    = " + S);
           return gring.engine.gcd(this,S);
        }
        GenPolynomial<C> x;
        GenPolynomial<C> q = this;
        GenPolynomial<C> r = S;
        while ( !r.isZERO() ) {
            x = q.remainder(r);
            q = r;
            r = x;
        }
        return q.monic(); // normalize
    }


    /**
     * GenPolynomial extended greatest comon divisor.
     * Only for univariate polynomials over fields.
     * @param S GenPolynomial.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    @Override
     public GenPolynomial<C>[] egcd(GenPolynomial<C> S) {
        GenPolynomial<C>[] ret = (GenPolynomial<C>[]) new GenPolynomial[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if ( S == null || S.isZERO() ) {
            ret[0] = this;
            return ret;
        }
        if ( this.isZERO() ) {
            ret[0] = S;
            return ret;
        }
        if ( ring.nvar != 1 ) {
           throw new RuntimeException(this.getClass().getName()
                                      + " not univariate polynomials" + ring);
        }
        //System.out.println("this = " + this + ", S = " + S);
        GenPolynomial<C>[] qr;
        GenPolynomial<C> q = this; 
        GenPolynomial<C> r = S;
        GenPolynomial<C> c1 = ring.getONE().clone();
        GenPolynomial<C> d1 = ring.getZERO().clone();
        GenPolynomial<C> c2 = ring.getZERO().clone();
        GenPolynomial<C> d2 = ring.getONE().clone();
        GenPolynomial<C> x1;
        GenPolynomial<C> x2;
        while ( !r.isZERO() ) {
            qr = q.divideAndRemainder(r);
            q = qr[0];
            x1 = c1.subtract( q.multiply(d1) );
            x2 = c2.subtract( q.multiply(d2) );
            c1 = d1; c2 = d2;
            d1 = x1; d2 = x2;
            q = r;
            r = qr[1];
        }
        // normalize ldcf(q) to 1, i.e. make monic
        C g = q.leadingBaseCoefficient();
        if ( g.isUnit() ) {
            C h = g.inverse();
            q = q.multiply( h );
            c1 = c1.multiply( h );
            c2 = c2.multiply( h );
        }        
     //assert ( ((c1.multiply(this)).sum( c2.multiply(S)).equals(q) )); 
        //if ( c1.isZERO() ) {
        //   System.out.println("this = " + this + "\n S = " + S);
        //   System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        //} 
        ret[0] = q;
        ret[1] = c1;
        ret[2] = c2;
        return ret;
    }


    /**
     * GenGcdPolynomial inverse.
     * Required by RingElem.
     * Throws not implemented exception.
     */
    @Override
     public GenGcdPolynomial<C> inverse() {
        return new GenGcdPolynomial<C>(gring, super.inverse().val );
    }


    /**
     * GenGcdPolynomial modular inverse.
     * Only for univariate polynomials over fields.
     * @param m GenGcdPolynomial.
     * @return a with with a*this = 1 mod m.
     */
    public GenGcdPolynomial<C> modInverse(GenGcdPolynomial<C> m) {
        return new GenGcdPolynomial<C>(gring, super.modInverse(m).val );
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend all ExpVectors by i elements and multiply by x_j^k.
     * @param pfac extended polynomial ring factory (by i variables).
     * @param j index of variable to be used for multiplication.
     * @param k exponent for x_j.
     * @return extended polynomial.
     */
    public GenGcdPolynomial<C> extend(GenGcdPolynomialRing<C> pfac, int j, long k) {
        return new GenGcdPolynomial<C>(gring, super.extend(pfac,j,k).val );
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * remove i elements of each ExpVector.
     * @param pfac contracted polynomial ring factory (by i variables).
     * @return Map of exponents and contracted polynomials.
     * <b>Note:</b> could return SortedMap
     */
    public Map<ExpVector,GenGcdPolynomial<C>> contract(GenGcdPolynomialRing<C> pfac) {
        GenGcdPolynomial<C> zero = new GenGcdPolynomial<C>(gring, pfac.getZERO().val );
        TermOrder t = new TermOrder( TermOrder.INVLEX );
        Map<ExpVector,GenGcdPolynomial<C>> B
            = new TreeMap<ExpVector,GenGcdPolynomial<C>>( t.getAscendComparator() );
        if ( this.isZERO() ) { 
           return B;
     }
        int i = ring.nvar - pfac.nvar;
        Map<ExpVector,C> A = val;
        for ( Map.Entry<ExpVector,C> y: A.entrySet() ) {
            ExpVector e = y.getKey();
            //System.out.println("e = " + e);
            C a = y.getValue();
            //System.out.println("a = " + a);
            ExpVector f = e.contract(0,i);
            ExpVector g = e.contract(i,e.length()-i);
            //System.out.println("e = " + e + ", f = " + f + ", g = " + g );
            GenGcdPolynomial<C> p = B.get(f);
            if ( p == null ) {
                p = zero;
            }
            p = p.sum( a, g );
            B.put( f, p );
        }
        return B;
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return polynomial with reversed variables.
     */
    public GenGcdPolynomial<C> reverse(GenGcdPolynomialRing<C> oring) {
        return new GenGcdPolynomial<C>(gring, super.reverse(oring).val );
    }


}

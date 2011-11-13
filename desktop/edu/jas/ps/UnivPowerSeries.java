/*
 * $Id: UnivPowerSeries.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.ps;


import edu.jas.structure.BinaryFunctor;
import edu.jas.structure.RingElem;
import edu.jas.structure.Selector;
import edu.jas.structure.UnaryFunctor;


/**
 * Univariate power series implementation. Uses inner classes and lazy evaluated
 * generating function for coefficients. All ring element methods use lazy
 * evaluation except where noted otherwise. Eager evaluated methods are
 * <code>toString()</code>, <code>compareTo()</code>,
 * <code>equals()</code>, <code>evaluate()</code>, or they use the
 * <code>order()</code> method, like <code>signum()</code>,
 * <code>abs()</code>, <code>divide()</code>, <code>remainder()</code>
 * and <code>gcd()</code>.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class UnivPowerSeries<C extends RingElem<C>> implements RingElem<UnivPowerSeries<C>>, PowerSeries<C> {


    /**
     * Power series ring factory.
     */
    public final UnivPowerSeriesRing<C> ring;


    /**
     * Data structure / generating function for coeffcients. Cannot be final
     * because of fixPoint, must be accessible in factory.
     */
    /*package*/Coefficients<C> lazyCoeffs;


    /**
     * Truncation of computations.
     */
    private int truncate = 11;


    /**
     * Order of power series.
     */
    private int order = -1; // == unknown


    /**
     * Private constructor.
     */
    private UnivPowerSeries() {
        throw new RuntimeException("do not use no-argument constructor");
    }


    /**
     * Package constructor. Use in fixPoint only, must be accessible in factory.
     * @param ring power series ring.
     */
    /*package*/UnivPowerSeries(UnivPowerSeriesRing<C> ring) {
        this.ring = ring;
        this.lazyCoeffs = null;
    }


    /**
     * Constructor.
     * @param ring power series ring.
     * @param lazyCoeffs generating function for coefficients.
     */
    public UnivPowerSeries(UnivPowerSeriesRing<C> ring, Coefficients<C> lazyCoeffs) {
        if (lazyCoeffs == null || ring == null) {
            throw new IllegalArgumentException("null not allowed: ring = " + ring + ", lazyCoeffs = "
                    + lazyCoeffs);
        }
        this.ring = ring;
        this.lazyCoeffs = lazyCoeffs;
        this.truncate = ring.truncate;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public UnivPowerSeriesRing<C> factory() {
        return ring;
    }


    /**
     * Clone this power series.
     * @see java.lang.Object#clone()
     */
    @Override
    public UnivPowerSeries<C> clone() {
        //return ring.copy(this);
        return new UnivPowerSeries<C>(ring, lazyCoeffs);
    }


    /**
     * String representation of power series.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toString(truncate);
    }


    /**
     * To String with given truncate.
     * @return string representation of this to given truncate.
     */
    public String toString(int truncate) {
        StringBuffer sb = new StringBuffer();
        UnivPowerSeries<C> s = this;
        String var = ring.var;
        //System.out.println("cache = " + s.coeffCache);
        for (int i = 0; i < truncate; i++) {
            C c = s.coefficient(i);
            int si = c.signum();
            if (si != 0) {
                if (si > 0) {
                    if (sb.length() > 0) {
                        sb.append(" + ");
                    }
                } else {
                    c = c.negate();
                    sb.append(" - ");
                }
                if (!c.isONE() || i == 0) {
                    sb.append(c.toString());
                    if (i > 0) {
                        sb.append(" * ");
                    }
                }
                if (i == 0) {
                    //skip; sb.append(" ");
                } else if (i == 1) {
                    sb.append(var);
                } else {
                    sb.append(var + "^" + i);
                }
                //sb.append(c.toString() + ", ");
            }
            //System.out.println("cache = " + s.coeffCache);
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        sb.append(" + BigO(" + var + "^" + truncate + ")");
        //sb.append("...");
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer sb = new StringBuffer("");
        UnivPowerSeries<C> s = this;
        String var = ring.var;
        //System.out.println("cache = " + s.coeffCache);
        for (int i = 0; i < truncate; i++) {
            C c = s.coefficient(i);
            int si = c.signum();
            if (si != 0) {
                if (si > 0) {
                    if (sb.length() > 0) {
                        sb.append(" + ");
                    }
                } else {
                    c = c.negate();
                    sb.append(" - ");
                }
                if (!c.isONE() || i == 0) {
                    sb.append(c.toScript());
                    if (i > 0) {
                        sb.append(" * ");
                    }
                }
                if (i == 0) {
                    //skip; sb.append(" ");
                } else if (i == 1) {
                    sb.append(var);
                } else {
                    sb.append(var + "**" + i);
                }
                //sb.append(c.toString() + ", ");
            }
            //System.out.println("cache = " + s.coeffCache);
        }
        if (sb.length() == 0) {
            sb.append("0");
        }
        // sb.append("," + truncate + "");
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /**
     * Get coefficient.
     * @param index number of requested coefficient.
     * @return coefficient at index.
     */
    public C coefficient(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("negative index not allowed");
        }
        //System.out.println("cache = " + coeffCache);
        return lazyCoeffs.get(index);
    }


    /**
     * Leading base coefficient.
     * @return first coefficient.
     */
    public C leadingCoefficient() {
        return coefficient(0);
    }


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public UnivPowerSeries<C> reductum() {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                return coefficient(i + 1);
            }
        });
    }


    /**
     * Prepend a new leading coefficient.
     * @param h new coefficient.
     * @return new power series.
     */
    public UnivPowerSeries<C> prepend(final C h) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                if (i == 0) {
                    return h;
                } else {
                    return coefficient(i - 1);
                }
            }
        });
    }


    /**
     * Shift coefficients.
     * @param k shift index.
     * @return new power series with coefficient(i) = old.coefficient(i-k).
     */
    public UnivPowerSeries<C> shift(final int k) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                if (i - k < 0) {
                    return ring.coFac.getZERO();
                } else {
                    return coefficient(i - k);
                }
            }
        });
    }


    /**
     * Select coefficients.
     * @param sel selector functor.
     * @return new power series with selected coefficients.
     */
    public UnivPowerSeries<C> select(final Selector<? super C> sel) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                C c = coefficient(i);
                if (sel.select(c)) {
                    return c;
                } else {
                    return ring.coFac.getZERO();
                }
            }
        });
    }


    /**
     * Shift select coefficients. Not selected coefficients are removed from the
     * result series.
     * @param sel selector functor.
     * @return new power series with shifted selected coefficients.
     */
    public UnivPowerSeries<C> shiftSelect(final Selector<? super C> sel) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            int pos = 0;


            @Override
            public C generate(int i) {
                C c;
                if (i > 0) {
                    c = get((i - 1));
                }
                c = null;
                do {
                    c = coefficient(pos++);
                } while (!sel.select(c));
                return c;
            }
        });
    }


    /**
     * Map a unary function to this power series.
     * @param f evaluation functor.
     * @return new power series with coefficients f(this(i)).
     */
    public UnivPowerSeries<C> map(final UnaryFunctor<? super C, C> f) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                return f.eval(coefficient(i));
            }
        });
    }


    /**
     * Map a binary function to this and another power series.
     * @param f evaluation functor with coefficients f(this(i),other(i)).
     * @param ps other power series.
     * @return new power series.
     */
    public <C2 extends RingElem<C2>> UnivPowerSeries<C> zip(final BinaryFunctor<? super C, ? super C2, C> f,
            final PowerSeries<C2> ps) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                return f.eval(coefficient(i), ps.coefficient(i));
            }
        });
    }


    /**
     * Sum of two power series.
     * @param ps other power series.
     * @return this + ps.
     */
    public UnivPowerSeries<C> sum(UnivPowerSeries<C> ps) {
        return zip(new Sum<C>(), ps);
    }


    /**
     * Subtraction of two power series.
     * @param ps other power series.
     * @return this - ps.
     */
    public UnivPowerSeries<C> subtract(UnivPowerSeries<C> ps) {
        return zip(new Subtract<C>(), ps);
    }


    /**
     * Multiply by coefficient.
     * @param c coefficient.
     * @return this * c.
     */
    public UnivPowerSeries<C> multiply(C c) {
        return map(new Multiply<C>(c));
    }


    /**
     * Negate.
     * @return - this.
     */
    public UnivPowerSeries<C> negate() {
        return map(new Negate<C>());
    }


    /**
     * Absolute value.
     * @return abs(this).
     */
    public UnivPowerSeries<C> abs() {
        if (signum() < 0) {
            return negate();
        } else {
            return this;
        }
        // return map( new Abs<C>() );
    }


    /**
     * Evaluate at given point.
     * @return ps(c).
     */
    public C evaluate(C e) {
        C v = coefficient(0);
        C p = e;
        for (int i = 1; i < truncate; i++) {
            C c = coefficient(i).multiply(p);
            v = v.sum(c);
            p = p.multiply(e);
        }
        return v;
    }


    /**
     * Order.
     * @return index of first non zero coefficient.
     */
    public int order() {
        if (order < 0) { // compute it
            for (int i = 0; i <= truncate; i++) {
                if (!coefficient(i).isZERO()) {
                    order = i;
                    return order;
                }
            }
            order = truncate + 1;
        }
        return order;
    }


    /**
     * Truncate.
     * @return truncate index of power series.
     */
    public int truncate() {
        return truncate;
    }


    /**
     * Set truncate.
     * @param t new truncate index.
     * @return old truncate index of power series.
     */
    public int setTruncate(int t) {
        if (t < 0) {
            throw new RuntimeException("negative truncate not allowed");
        }
        int ot = truncate;
        truncate = t;
        return ot;
    }


    /**
     * Signum.
     * @return sign of first non zero coefficient.
     */
    public int signum() {
        return coefficient(order()).signum();
    }


    /**
     * Compare to. <b>Note: </b> compare only up to truncate.
     * @return sign of first non zero coefficient of this-ps.
     */
    //JAVA6only: @Override
    public int compareTo(UnivPowerSeries<C> ps) {
        int m = order();
        int n = ps.order();
        int pos = (m <= n) ? m : n;
        int s = 0;
        do {
            s = coefficient(pos).compareTo(ps.coefficient(pos));
            pos++;
        } while (s == 0 && pos <= truncate);
        return s;
    }


    /**
     * Is power series zero. <b>Note: </b> compare only up to truncate.
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return (compareTo(ring.ZERO) == 0);
    }


    /**
     * Is power series one. <b>Note: </b> compare only up to truncate.
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return (compareTo(ring.ONE) == 0);
    }


    /**
     * Comparison with any other object. <b>Note: </b> compare only up to
     * truncate.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof UnivPowerSeries)) {
            return false;
        }
        UnivPowerSeries<C> a = null;
        try {
            a = (UnivPowerSeries<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (a == null) {
            return false;
        }
        return compareTo(a) == 0;
    }


    /**
     * Hash code for this polynomial. <b>Note: </b> only up to truncate.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 0;
        //h = ( ring.hashCode() << 27 );
        //h += val.hashCode();
        for (int i = 0; i <= truncate; i++) {
            h += coefficient(i).hashCode();
            h = (h << 23);
        };
        return h;
    }


    /**
     * Is unit.
     * @return true, if this power series is invertible, else false.
     */
    public boolean isUnit() {
        return leadingCoefficient().isUnit();
    }


    /**
     * Multiply by another power series.
     * @return this * ps.
     */
    public UnivPowerSeries<C> multiply(final UnivPowerSeries<C> ps) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                C c = null; //fac.getZERO();
                for (int k = 0; k <= i; k++) {
                    C m = coefficient(k).multiply(ps.coefficient(i - k));
                    if (c == null) {
                        c = m;
                    } else {
                        c = c.sum(m);
                    }
                }
                return c;
            }
        });
    }


    /**
     * Inverse power series.
     * @return ps with this * ps = 1.
     */
    public UnivPowerSeries<C> inverse() {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                C d = leadingCoefficient().inverse(); // may fail
                if (i == 0) {
                    return d;
                }
                C c = null; //fac.getZERO();
                for (int k = 0; k < i; k++) {
                    C m = get(k); // cached value
                    m = coefficient(i - k).multiply(m);
                    if (c == null) {
                        c = m;
                    } else {
                        c = c.sum(m);
                    }
                }
                c = c.multiply(d.negate());
                return c;
            }
        });
    }


    /**
     * Divide by another power series.
     * @return this / ps.
     */
    public UnivPowerSeries<C> divide(UnivPowerSeries<C> ps) {
        if (ps.isUnit()) {
            return multiply(ps.inverse());
        }
        int m = order();
        int n = ps.order();
        if (m < n) {
            return ring.getZERO();
        }
        if (!ps.coefficient(n).isUnit()) {
            throw new RuntimeException("division by non unit coefficient " + ps.coefficient(n) + ", n = " + n);
        }
        // now m >= n
        UnivPowerSeries<C> st, sps, q, sq;
        if (m == 0) {
            st = this;
        } else {
            st = this.shift(-m);
        }
        if (n == 0) {
            sps = ps;
        } else {
            sps = ps.shift(-n);
        }
        q = st.multiply(sps.inverse());
        if (m == n) {
            sq = q;
        } else {
            sq = q.shift(m - n);
        }
        return sq;
    }


    /**
     * Power series remainder.
     * @param ps nonzero power series with invertible leading coefficient.
     * @return remainder with this = quotient * ps + remainder.
     */
    public UnivPowerSeries<C> remainder(UnivPowerSeries<C> ps) {
        int m = order();
        int n = ps.order();
        if (m >= n) {
            return ring.getZERO();
        }
        return this;
    }


    /**
     * Differentiate.
     * @return differentiate(this).
     */
    public UnivPowerSeries<C> differentiate() {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                C v = coefficient(i + 1);
                v = v.multiply(ring.coFac.fromInteger(i + 1));
                return v;
            }
        });
    }


    /**
     * Integrate with given constant.
     * @param c integration constant.
     * @return integrate(this).
     */
    public UnivPowerSeries<C> integrate(final C c) {
        return new UnivPowerSeries<C>(ring, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                if (i == 0) {
                    return c;
                }
                C v = coefficient(i - 1);
                v = v.divide(ring.coFac.fromInteger(i));
                return v;
            }
        });
    }


    /**
     * Power series greatest common divisor.
     * @param ps power series.
     * @return gcd(this,ps).
     */
    public UnivPowerSeries<C> gcd(UnivPowerSeries<C> ps) {
        if (ps.isZERO()) {
            return this;
        }
        if (this.isZERO()) {
            return ps;
        }
        int m = order();
        int n = ps.order();
        int ll = (m < n) ? m : n;
        return ring.getONE().shift(ll);
    }


    /**
     * Power series extended greatest common divisor. <b>Note:</b> not
     * implemented.
     * @param S power series.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    //SuppressWarnings("unchecked")
    public UnivPowerSeries<C>[] egcd(UnivPowerSeries<C> S) {
        throw new RuntimeException("egcd for power series not implemented");
    }

}


/* arithmetic method functors */


/**
 * Internal summation functor.
 */
class Sum<C extends RingElem<C>> implements BinaryFunctor<C, C, C> {


    public C eval(C c1, C c2) {
        return c1.sum(c2);
    }
}


/**
 * Internal subtraction functor.
 */
class Subtract<C extends RingElem<C>> implements BinaryFunctor<C, C, C> {


    public C eval(C c1, C c2) {
        return c1.subtract(c2);
    }
}


/**
 * Internal scalar multiplication functor.
 */
class Multiply<C extends RingElem<C>> implements UnaryFunctor<C, C> {


    C x;


    public Multiply(C x) {
        this.x = x;
    }


    public C eval(C c) {
        return c.multiply(x);
    }
}


/**
 * Internal negation functor.
 */
class Negate<C extends RingElem<C>> implements UnaryFunctor<C, C> {


    public C eval(C c) {
        return c.negate();
    }
}


/* only for sequential access:
class Abs<C extends RingElem<C>> implements UnaryFunctor<C,C> {
        int sign = 0;
        public C eval(C c) {
            int s = c.signum();
            if ( s == 0 ) {
               return c;
            }
            if ( sign > 0 ) {
               return c;
            } else if ( sign < 0 ) {
               return c.negate();
            }
            // first non zero coefficient:
            sign = s;
            if ( s > 0 ) {
               return c;
            }
            return c.negate();
        }
}
*/

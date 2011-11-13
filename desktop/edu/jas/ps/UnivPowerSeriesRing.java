/*
 * $Id: UnivPowerSeriesRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.ps;


import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.Monomial;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Univariate power series ring implementation. Uses lazy evaluated generating
 * function for coefficients.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class UnivPowerSeriesRing<C extends RingElem<C>> implements RingFactory<UnivPowerSeries<C>> {


    /**
     * A default random sequence generator.
     */
    protected final static Random random = new Random();


    /**
     * Default truncate.
     */
    public final static int DEFAULT_TRUNCATE = 11;


    /**
     * Truncate.
     */
    int truncate;


    /**
     * Default variable name.
     */
    public final static String DEFAULT_NAME = "x";


    /**
     * Variable name.
     */
    String var;


    /**
     * Coefficient ring factory.
     */
    public final RingFactory<C> coFac;


    /**
     * The constant power series 1 for this ring.
     */
    public final UnivPowerSeries<C> ONE;


    /**
     * The constant power series 0 for this ring.
     */
    public final UnivPowerSeries<C> ZERO;


    /**
     * No argument constructor.
     */
    private UnivPowerSeriesRing() {
        throw new RuntimeException("do not use no-argument constructor");
        //coFac = null;
        //ONE = null;
        //ZERO = null;
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     */
    public UnivPowerSeriesRing(RingFactory<C> coFac) {
        this(coFac, DEFAULT_TRUNCATE, DEFAULT_NAME);
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     * @param truncate index of truncation.
     */
    public UnivPowerSeriesRing(RingFactory<C> coFac, int truncate) {
        this(coFac, truncate, DEFAULT_NAME);
    }


    /**
     * Constructor.
     * @param coFac coefficient ring factory.
     * @param name of the variable.
     */
    public UnivPowerSeriesRing(RingFactory<C> coFac, String name) {
        this(coFac, DEFAULT_TRUNCATE, name);
    }


    /**
     * Constructor.
     * @param cofac coefficient ring factory.
     * @param truncate index of truncation.
     * @param name of the variable.
     */
    public UnivPowerSeriesRing(RingFactory<C> cofac, int truncate, String name) {
        this.coFac = cofac;
        this.truncate = truncate;
        this.var = name;
        this.ONE = new UnivPowerSeries<C>(this, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                if (i == 0) {
                    return coFac.getONE();
                } else {
                    return coFac.getZERO();
                }
            }
        });
        this.ZERO = new UnivPowerSeries<C>(this, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                return coFac.getZERO();
            }
        });
    }


    /**
     * Fixed point construction.
     * @param map a mapping of power series.
     * @return fix point wrt map.
     */
    // Cannot be a static method because a power series ring is required.
    public UnivPowerSeries<C> fixPoint(PowerSeriesMap<C> map) {
        UnivPowerSeries<C> ps1 = new UnivPowerSeries<C>(this);
        UnivPowerSeries<C> ps2 = map.map(ps1);
        ps1.lazyCoeffs = ps2.lazyCoeffs;
        return ps2;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String scf = coFac.getClass().getSimpleName();
        sb.append(scf + "((" + var + "))");
        return sb.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("PS(");
        String f = null;
        try {
            f = ((RingElem<C>) coFac).toScriptFactory(); // sic
        } catch (Exception e) {
            f = coFac.toScript();
        }
        s.append(f + ",\"" + var + "\"," + truncate + ")");
        return s.toString();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object B) {
        if (!(B instanceof UnivPowerSeriesRing)) {
            return false;
        }
        UnivPowerSeriesRing<C> a = null;
        try {
            a = (UnivPowerSeriesRing<C>) B;
        } catch (ClassCastException ignored) {
        }
        if (var.equals(a.var)) {
            return true;
        }
        return false;
    }


    /**
     * Hash code for this .
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h = 0;
        h = (var.hashCode() << 27);
        h += truncate;
        return h;
    }


    /**
     * Get the zero element.
     * @return 0 as UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> getZERO() {
        return ZERO;
    }


    /**
     * Get the one element.
     * @return 1 as UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> getONE() {
        return ONE;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<UnivPowerSeries<C>> generators() {
        List<C> rgens = coFac.generators();
        List<UnivPowerSeries<C>> gens = new ArrayList<UnivPowerSeries<C>>(rgens.size());
        for (final C cg : rgens) {
            UnivPowerSeries<C> g = new UnivPowerSeries<C>(this, new Coefficients<C>() {


                @Override
                public C generate(int i) {
                    if (i == 0) {
                        return cg;
                    } else {
                        return coFac.getZERO();
                    }
                }
            });
            gens.add(g);
        }
        gens.add(ONE.shift(1));
        return gens;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return false;
    }


    /**
     * Get the power series of the exponential function.
     * @return exp(x) as UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> getEXP() {
        return fixPoint(new PowerSeriesMap<C>() {


            public UnivPowerSeries<C> map(UnivPowerSeries<C> e) {
                return e.integrate(coFac.getONE());
            }
        });
    }


    /**
     * Get the power series of the sinus function.
     * @return sin(x) as UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> getSIN() {
        return fixPoint(new PowerSeriesMap<C>() {


            public UnivPowerSeries<C> map(UnivPowerSeries<C> s) {
                return s.negate().integrate(coFac.getONE()).integrate(coFac.getZERO());
            }
        });
    }


    /**
     * Get the power series of the cosinus function.
     * @return cos(x) as UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> getCOS() {
        return fixPoint(new PowerSeriesMap<C>() {


            public UnivPowerSeries<C> map(UnivPowerSeries<C> c) {
                return c.negate().integrate(coFac.getZERO()).integrate(coFac.getONE());
            }
        });
    }


    /**
     * Get the power series of the tangens function.
     * @return tan(x) as UnivPowerSeries<C>.
     */
    public UnivPowerSeries<C> getTAN() {
        return fixPoint(new PowerSeriesMap<C>() {


            public UnivPowerSeries<C> map(UnivPowerSeries<C> t) {
                return t.multiply(t).sum(getONE()).integrate(coFac.getZERO());
            }
        });
    }


    /**
     * Solve an ordinary differential equation. y' = f(y) with y(0) = c.
     * @param f a UnivPowerSeries<C>.
     * @param c integration constant.
     * @return f.integrate(c).
     */
    public UnivPowerSeries<C> solveODE(final UnivPowerSeries<C> f, final C c) {
        return f.integrate(c);
    }


    /**
     * Is commuative.
     * @return true, if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return coFac.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return coFac.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return false.
     */
    public boolean isField() {
        return false;
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return coFac.characteristic();
    }


    /**
     * Get a (constant) UnivPowerSeries&lt;C&gt; from a long value.
     * @param a long.
     * @return a UnivPowerSeries&lt;C&gt;.
     */
    public UnivPowerSeries<C> fromInteger(long a) {
        return ONE.multiply(coFac.fromInteger(a));
    }


    /**
     * Get a (constant) UnivPowerSeries&lt;C&gt; from a java.math.BigInteger.
     * @param a BigInteger.
     * @return a UnivPowerSeries&lt;C&gt;.
     */
    public UnivPowerSeries<C> fromInteger(java.math.BigInteger a) {
        return ONE.multiply(coFac.fromInteger(a));
    }


    /**
     * Get a UnivPowerSeries&lt;C&gt; from a GenPolynomial&lt;C&gt;.
     * @param a GenPolynomial&lt;C&gt;.
     * @return a UnivPowerSeries&lt;C&gt;.
     */
    public UnivPowerSeries<C> fromPolynomial(GenPolynomial<C> a) {
        if (a == null || a.isZERO()) {
            return ZERO;
        }
        if (a.isONE()) {
            return ONE;
        }
        if (a.ring.nvar != 1) {
            throw new RuntimeException("only for univariate polynomials");
        }
        HashMap<Integer, C> cache = new HashMap<Integer, C>(a.length());
        //Iterator<Monomial<C>> it = a.monomialIterator();
        for (Monomial<C> m : a) {
            //while ( it.hasNext() ) {
            //Monomial<C> m = it.next();
            long e = m.exponent().getVal(0);
            cache.put((int) e, m.coefficient());
        }
        return new UnivPowerSeries<C>(this, new Coefficients<C>(cache) {
            @Override
            public C generate(int i) {
                // cached coefficients returned by get
                return coFac.getZERO();
            }
        });
    }


    /**
     * Generate a random power series with k = 5, d = 0.7.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random() {
        return random(5, 0.7f, random);
    }


    /**
     * Generate a random power series with d = 0.7.
     * @param k bitsize of random coefficients.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(int k) {
        return random(k, 0.7f, random);
    }


    /**
     * Generate a random power series with d = 0.7.
     * @param k bitsize of random coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(int k, Random rnd) {
        return random(k, 0.7f, rnd);
    }


    /**
     * Generate a random power series.
     * @param k bitsize of random coefficients.
     * @param d density of non-zero coefficients.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(int k, float d) {
        return random(k, d, random);
    }


    /**
     * Generate a random power series.
     * @param k bitsize of random coefficients.
     * @param d density of non-zero coefficients.
     * @param rnd is a source for random bits.
     * @return a random power series.
     */
    public UnivPowerSeries<C> random(final int k, final float d, final Random rnd) {
        return new UnivPowerSeries<C>(this, new Coefficients<C>() {


            @Override
            public C generate(int i) {
                // cached coefficients returned by get
                C c;
                float f = rnd.nextFloat();
                if (f < d) {
                    c = coFac.random(k, rnd);
                } else {
                    c = coFac.getZERO();
                }
                return c;
            }
        });
    }


    /**
     * Copy power series.
     * @param c a power series.
     * @return a copy of c.
     */
    public UnivPowerSeries<C> copy(UnivPowerSeries<C> c) {
        return new UnivPowerSeries<C>(this, c.lazyCoeffs);
    }


    /**
     * Parse a power series. <b>Note:</b> not implemented.
     * @param s String.
     * @return power series from s.
     */
    public UnivPowerSeries<C> parse(String s) {
        throw new RuntimeException("parse for power series not implemented");
    }


    /**
     * Parse a power series. <b>Note:</b> not implemented.
     * @param r Reader.
     * @return next power series from r.
     */
    public UnivPowerSeries<C> parse(Reader r) {
        throw new RuntimeException("parse for power series not implemented");
    }

}

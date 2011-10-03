/*
 * $Id: ComplexAlgebraicRing.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.root;


import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.jas.arith.Rational;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.Complex;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.Power;
import edu.jas.structure.RingFactory;


/**
 * Complex algebraic number factory class based on GenPolynomial with RingElem
 * interface. Objects of this class are immutable with the exception of the
 * isolating intervals.
 * @author Heinz Kredel
 */

public class ComplexAlgebraicRing<C extends GcdRingElem<C> & Rational>
/*extends AlgebraicNumberRing<C>*/
implements RingFactory<ComplexAlgebraicNumber<C>> {


    /**
     * Representing AlgebraicNumberRing.
     */
    public final AlgebraicNumberRing<Complex<C>> algebraic;


    /**
     * Isolating rectangle for a complex root. <b>Note: </b> interval may shrink
     * eventually.
     */
    /*package*/Rectangle<C> root;


    /**
     * Precision of the isolating rectangle for a complex root.
     */
    public final Complex<C> eps;


    /**
     * Complex root computation engine.
     */
    public final ComplexRootsSturm<C> engine;


    /**
     * The constructor creates a ComplexAlgebraicNumber factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial&lt;C&gt;.
     * @param root isolating rectangle for a complex root.
     */
    public ComplexAlgebraicRing(GenPolynomial<Complex<C>> m, Rectangle<C> root) {
        algebraic = new AlgebraicNumberRing<Complex<C>>(m);
        this.root = root;
        engine = new ComplexRootsSturm<C>(m.ring.coFac);
        if (m.ring.characteristic().signum() > 0) {
            throw new RuntimeException("characteristic not zero");
        }
        Complex<C> e = m.ring.coFac.fromInteger(10L);
        e = e.inverse();
        // e = Power.positivePower(e,BigDecimal.DEFAULT_PRECISION);
        e = Power.positivePower(e, 9); //BigDecimal.DEFAULT_PRECISION);
        eps = e;
    }


    /**
     * The constructor creates a ComplexAlgebraicNumber factory object from a
     * GenPolynomial objects module.
     * @param m module GenPolynomial&lt;C&gt;.
     * @param root isolating rectangle for a complex root.
     * @param isField indicator if m is prime.
     */
    public ComplexAlgebraicRing(GenPolynomial<Complex<C>> m, Rectangle<C> root, boolean isField) {
        algebraic = new AlgebraicNumberRing<Complex<C>>(m, isField);
        this.root = root;
        engine = new ComplexRootsSturm<C>(m.ring.coFac);
        if (m.ring.characteristic().signum() > 0) {
            throw new RuntimeException("characteristic not zero");
        }
        Complex<C> e = m.ring.coFac.fromInteger(10L);
        e = e.inverse();
        e = Power.positivePower(e, 9); //BigDecimal.DEFAULT_PRECISION);
        eps = e;
    }


    /**
     * Get the module part.
     * @return modul. public GenPolynomial<C> getModul() { return
     *         algebraic.modul; }
     */


    /**
     * Set a refined rectangle for the complex root. <b>Note: </b> rectangle may
     * shrink eventually.
     * @param v rectangle.
     */
    public synchronized void setRoot(Rectangle<C> v) {
        // assert v is contained in root
        this.root = v;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return algebraic.isFinite();
    }


    /**
     * Copy ComplexAlgebraicNumber element c.
     * @param c
     * @return a copy of c.
     */
    public ComplexAlgebraicNumber<C> copy(ComplexAlgebraicNumber<C> c) {
        return new ComplexAlgebraicNumber<C>(this, c.number);
    }


    /**
     * Get the zero element.
     * @return 0 as ComplexAlgebraicNumber.
     */
    public ComplexAlgebraicNumber<C> getZERO() {
        return new ComplexAlgebraicNumber<C>(this, algebraic.getZERO());
    }


    /**
     * Get the one element.
     * @return 1 as ComplexAlgebraicNumber.
     */
    public ComplexAlgebraicNumber<C> getONE() {
        return new ComplexAlgebraicNumber<C>(this, algebraic.getONE());
    }


    /**
     * Get the generating element.
     * @return alpha as ComplexAlgebraicNumber.
     */
    public ComplexAlgebraicNumber<C> getGenerator() {
        return new ComplexAlgebraicNumber<C>(this, algebraic.getGenerator());
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<ComplexAlgebraicNumber<C>> generators() {
        List<ComplexAlgebraicNumber<C>> gens = new ArrayList<ComplexAlgebraicNumber<C>>(2);
        gens.add(getONE());
        gens.add(getGenerator());
        return gens;
    }


    /**
     * Query if this ring is commutative.
     * @return true if this ring is commutative, else false.
     */
    public boolean isCommutative() {
        return algebraic.isCommutative();
    }


    /**
     * Query if this ring is associative.
     * @return true if this ring is associative, else false.
     */
    public boolean isAssociative() {
        return algebraic.isAssociative();
    }


    /**
     * Query if this ring is a field.
     * @return true if algebraic is prime, else false.
     */
    public boolean isField() {
        return algebraic.isField();
    }


    /**
     * Characteristic of this ring.
     * @return characteristic of this ring.
     */
    public java.math.BigInteger characteristic() {
        return algebraic.characteristic();
    }


    /**
     * Get a ComplexAlgebraicNumber element from a BigInteger value.
     * @param a BigInteger.
     * @return a ComplexAlgebraicNumber.
     */
    public ComplexAlgebraicNumber<C> fromInteger(java.math.BigInteger a) {
        return new ComplexAlgebraicNumber<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get a ComplexAlgebraicNumber element from a long value.
     * @param a long.
     * @return a ComplexAlgebraicNumber.
     */
    public ComplexAlgebraicNumber<C> fromInteger(long a) {
        return new ComplexAlgebraicNumber<C>(this, algebraic.fromInteger(a));
    }


    /**
     * Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ComplexAlgebraicRing[ " + algebraic.modul.toString() + " in " + root + " | isField="
                + algebraic.isField() + " :: " + algebraic.ring.toString() + " ]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return "ComplexN( " + algebraic.modul.toScript() + ", " + root.toScript()
        //+ ", " + algebraic.isField() 
                //+ ", " + algebraic.ring.toScript() 
                + " )";
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    // not jet working
    public boolean equals(Object b) {
        if (!(b instanceof ComplexAlgebraicRing)) {
            return false;
        }
        ComplexAlgebraicRing<C> a = null;
        try {
            a = (ComplexAlgebraicRing<C>) b;
        } catch (ClassCastException e) {
        }
        if (a == null) {
            return false;
        }
        return algebraic.equals(a.algebraic) && root.equals(a.root);
    }


    /**
     * Hash code for this ComplexAlgebraicNumber.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * algebraic.hashCode() + root.hashCode();
    }


    /**
     * ComplexAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @return a random integer mod modul.
     */
    public ComplexAlgebraicNumber<C> random(int n) {
        return new ComplexAlgebraicNumber<C>(this, algebraic.random(n));
    }


    /**
     * ComplexAlgebraicNumber random.
     * @param n such that 0 &le; v &le; (2<sup>n</sup>-1).
     * @param rnd is a source for random bits.
     * @return a random integer mod modul.
     */
    public ComplexAlgebraicNumber<C> random(int n, Random rnd) {
        return new ComplexAlgebraicNumber<C>(this, algebraic.random(n, rnd));
    }


    /**
     * Parse ComplexAlgebraicNumber from String.
     * @param s String.
     * @return ComplexAlgebraicNumber from s.
     */
    public ComplexAlgebraicNumber<C> parse(String s) {
        return new ComplexAlgebraicNumber<C>(this, algebraic.parse(s));
    }


    /**
     * Parse ComplexAlgebraicNumber from Reader.
     * @param r Reader.
     * @return next ComplexAlgebraicNumber from r.
     */
    public ComplexAlgebraicNumber<C> parse(Reader r) {
        return new ComplexAlgebraicNumber<C>(this, algebraic.parse(r));
    }

}

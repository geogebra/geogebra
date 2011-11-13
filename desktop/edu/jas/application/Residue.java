/*
 * $Id: Residue.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.application;

import edu.jas.poly.GenPolynomial;
import edu.jas.poly.PolyUtil;

import edu.jas.kern.PrettyPrint;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;


/**
 * Residue ring element based on GenPolynomial with RingElem interface.
 * Objects of this class are (nearly) immutable.
 * @author Heinz Kredel
 */
public class Residue<C extends GcdRingElem<C> > 
             implements GcdRingElem< Residue<C> > {


    /** Residue class factory data structure. 
     */
    public final ResidueRing<C> ring;


    /** Value part of the element data structure. 
     */
    public final GenPolynomial<C> val;


    /** Flag to remember if this residue element is a unit.
     * -1 is unknown, 1 is unit, 0 not a unit.
     */
    protected int isunit = -1; // initially unknown


    /** The constructor creates a Residue object 
     * from a ring factory. 
     * @param r ring factory.
     */
    public Residue(ResidueRing<C> r) {
        this( r, r.ring.getZERO(), 0 );
    }


    /** The constructor creates a Residue object 
     * from a ring factory and a polynomial. 
     * @param r ring factory.
     * @param a polynomial list.
     */
    public Residue(ResidueRing<C> r, GenPolynomial<C> a) {
        this( r, a, -1 );
    }


    /** The constructor creates a Residue object 
     * from a ring factory, a polynomial and an indicator if a is a unit. 
     * @param r ring factory.
     * @param a polynomial list.
     * @param u isunit indicator, -1, 0, 1.
     */
    public Residue(ResidueRing<C> r, GenPolynomial<C> a, int u) {
        ring = r;
        val = ring.ideal.normalform( a ); //.monic() no go
        switch ( u ) {
        case 0:  isunit = u;
                 break;
        case 1:  isunit = u;
                 break;
        default: isunit = -1;
        }
        if ( val.isONE() ) {
           isunit = 1;
        }
        if ( val.isZERO() ) {
           isunit = 0;
        }
    }

    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ResidueRing<C> factory() {
        return ring;
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Residue<C> clone() {
        return new Residue<C>( ring, val, isunit );
    }
   

    /** Is Residue zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        // ??return val.equals( ring.ring.getZERO() );
        return val.isZERO();
    }


    /** Is Residue one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        // ?? return val.equals( ring.ring.getONE() );
        return val.isONE();
    }


    /** Is Residue unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( isunit > 0 ) {
            return true;
        } 
        if ( isunit == 0 ) {
            return false;
        } 
        // not jet known
        boolean u = ring.ideal.isUnit( val );
        if ( u ) {
           isunit = 1;
        } else {
           isunit = 0;
        }
        return ( u );
    }


    /** Is Residue a constant. 
     * @return true if this.val is a constant polynomial, else false.
     */
    public boolean isConstant() {
        return val.isConstant();
    }


    /** Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if ( PrettyPrint.isTrue() ) {
           return val.toString( ring.ring.getVars() );
        } else {
           return "Residue[ " + val.toString() 
                   + " mod " + ring.toString() + " ]";
        }
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return val.toScript(); 
//         return "PolyResidue( " + val.toScript() 
//                         + ", " + ring.toScript() + " )";
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return factory().toScript();
    }


    /** Residue comparison.  
     * @param b Residue.
     * @return sign(this-b), 0 means that this and b are equivalent in this residue class ring.
     */
    //JAVA6only: @Override
    public int compareTo(Residue<C> b) {
        GenPolynomial<C> v = b.val;
        if ( ! ring.equals( b.ring ) ) {
           v = ring.ideal.normalform( v );
        }
        return val.compareTo( v );
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     * @return true means that this and b are equivalent in this residue class ring.
     */
    @Override
    @SuppressWarnings("unchecked") 
    public boolean equals(Object b) {
        if ( ! ( b instanceof Residue ) ) {
           return false;
        }
        Residue<C> a = null;
        try {
            a = (Residue<C>) b;
        } catch (ClassCastException e) {
        }
        if ( a == null ) {
            return false;
        }
        return ( 0 == compareTo( a ) );
    }


    /** Hash code for this local.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
       int h;
       h = ring.hashCode();
       h = 37 * h + val.hashCode();
       return h;
    }


    /** Residue absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public Residue<C> abs() {
        return new Residue<C>( ring, val.abs(), isunit );
    }


    /** Residue summation.
     * @param S Residue.
     * @return this+S.
     */
    public Residue<C> sum(Residue<C> S) {
        return new Residue<C>( ring, val.sum( S.val ) );
    }


    /** Residue negate.
     * @return -this.
     * @see edu.jas.structure.RingElem#negate()
     */
    public Residue<C> negate() {
        return new Residue<C>( ring, val.negate(), isunit );
    }


    /** Residue signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /** Residue subtraction.
     * @param S Residue.
     * @return this-S.
     */
    public Residue<C> subtract(Residue<C> S) {
        return new Residue<C>( ring, val.subtract( S.val ) );
    }


    /** Residue division.
     * @param S Residue.
     * @return this/S.
     */
    public Residue<C> divide(Residue<C> S) {
        if ( ring.isField() ) {
            return multiply( S.inverse() );
        }
        GenPolynomial<C> x = PolyUtil.<C>basePseudoDivide( val, S.val );
        return new Residue<C>( ring, x );
    }


    /** Residue inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S = 1/this if defined. 
     */
    public Residue<C> inverse() {
        GenPolynomial<C> x = ring.ideal.inverse( val );
        return new Residue<C>( ring, x, 1 );
    }


    /** Residue remainder.
     * @param S Residue.
     * @return this - (this/S)*S.
     */
    public Residue<C> remainder(Residue<C> S) {
        //GenPolynomial<C> x = val.remainder( S.val );
        GenPolynomial<C> x = PolyUtil.<C>basePseudoRemainder( val, S.val );
        return new Residue<C>( ring, x );
    }


    /** Residue multiplication.
     * @param S Residue.
     * @return this*S.
     */
    public Residue<C> multiply(Residue<C> S) {
        GenPolynomial<C> x = val.multiply( S.val );
        int i = -1;
        if ( isunit == 1 && S.isunit == 1 ) {
           i = 1;
        } else if ( isunit == 0 || S.isunit == 0 ) {
           i = 0;
        }
        return new Residue<C>( ring, x, i );
    }

 
    /** Residue monic.
     * @return this with monic value part.
     */
    public Residue<C> monic() {
        return new Residue<C>( ring, val.monic(), isunit );
    }


    /**
     * Greatest common divisor.
     * @param b other element.
     * @return gcd(this,b).
     */
    public Residue<C> gcd(Residue<C> b) {
        GenPolynomial<C> x = ring.engine.gcd( val, b.val );
        int i = -1; // gcd might become a unit
        if ( x.isONE() ) {
            i = 1;
        } else {
           System.out.println("Residue gcd = " + x);
        }
        if ( isunit == 1 && b.isunit == 1 ) {
           i = 1;
        }
        return new Residue<C>( ring, x, i );
    }


    /**
     * Extended greatest common divisor.
     * <b>Note: </b>Not implemented, throws RuntimeException.
     * @param b other element.
     * @return [ gcd(this,b), c1, c2 ] with c1*this + c2*b = gcd(this,b).
     */
    public Residue<C>[] egcd(Residue<C> b) {
        throw new RuntimeException("egcd not implemented " + this.getClass().getName());
    }
}

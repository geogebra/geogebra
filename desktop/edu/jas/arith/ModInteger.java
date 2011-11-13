/*
 * $Id: ModInteger.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.arith;

import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.NotInvertibleException;


/**
 * ModInteger class with RingElem interface
 * and with the familiar SAC method names.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 * @see java.math.BigInteger
 */

public final class ModInteger implements GcdRingElem<ModInteger>, Modular {


    /** ModIntegerRing reference. 
     */
    public final ModIntegerRing ring;


    /** Value part of the element data structure. 
     */
    protected final java.math.BigInteger val;


    /** The constructor creates a ModInteger object 
     * from a ModIntegerRing and a value part. 
     * @param m ModIntegerRing.
     * @param a math.BigInteger.
     */
    public ModInteger(ModIntegerRing m, java.math.BigInteger a) {
        ring = m;
        val = a.mod(ring.modul);
    }


    /** The constructor creates a ModInteger object 
     * from a ModIntegerRing and a long value part. 
     * @param m ModIntegerRing.
     * @param a long.
     */
    public ModInteger(ModIntegerRing m, long a) {
        this( m, new java.math.BigInteger( String.valueOf(a) ) );
    }


    /** The constructor creates a ModInteger object 
     * from a ModIntegerRing and a String value part. 
     * @param m ModIntegerRing.
     * @param s String.
     */
    public ModInteger(ModIntegerRing m, String s) {
        this( m, new java.math.BigInteger( s.trim() ) );
    }


    /** The constructor creates a 0 ModInteger object 
     * from a given ModIntegerRing. 
     * @param m ModIntegerRing.
     */
    public ModInteger(ModIntegerRing m) {
        this(m,java.math.BigInteger.ZERO);
    }


    /** Get the value part. 
     * @return val.
     */
    public java.math.BigInteger getVal() {
        return val;
    }


    /** Get the module part. 
     * @return modul.
     */
    public java.math.BigInteger getModul() {
        return ring.modul;
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public ModIntegerRing factory() {
        return ring;
    }


    /** Get the symmetric value part. 
     * @return val with -modul/2 <= val < modul/2.
     */
    public java.math.BigInteger getSymmetricVal() {
        if ( val.add( val ).compareTo( ring.modul ) > 0 ) {
            // val > m/2 as 2*val > m, make symmetric to 0
            return val.subtract( ring.modul );
        }
        return val;
    }


    /**
     * Return a BigInteger from this Element.
     * @return a BigInteger of this.
     */
    public BigInteger getInteger() {
        return new BigInteger( val );
    }


    /**
     * Return a symmetric BigInteger from this Element.
     * @return a symmetric BigInteger of this.
     */
    public BigInteger getSymmetricInteger() {
        java.math.BigInteger v = val;
        if ( val.add( val ).compareTo( ring.modul ) > 0 ) {
            // val > m/2 as 2*val > m, make symmetric to 0
            v = val.subtract( ring.modul );
        }
        return new BigInteger( v );
    }


    /**  Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public ModInteger clone() {
        return new ModInteger( ring, val );
    }


    /** Is ModInteger number zero. 
     * @return If this is 0 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isZERO()
     */
    public boolean isZERO() {
        return val.equals( java.math.BigInteger.ZERO );
    }


    /** Is ModInteger number one. 
     * @return If this is 1 then true is returned, else false.
     * @see edu.jas.structure.RingElem#isONE()
     */
    public boolean isONE() {
        return val.equals( java.math.BigInteger.ONE );
    }


    /** Is ModInteger number a unit. 
     * @return If this is a unit then true is returned, else false.
     * @see edu.jas.structure.RingElem#isUnit()
     */
    public boolean isUnit() {
        if ( isZERO() ) {
           return false;
        }
        if ( ring.isField() ) {
           return true;
        }
        java.math.BigInteger g = ring.modul.gcd( val ).abs();
        return ( g.equals( java.math.BigInteger.ONE ) );
    }


    /**  Get the String representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return val.toString();
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        return toString();
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


    /** ModInteger comparison.  
     * @param b ModInteger.
     * @return sign(this-b).
     */
    //JAVA6only: @Override
    public int compareTo(ModInteger b) {
        java.math.BigInteger v = b.val;
        if ( ring != b.ring ) {
            v = v.mod( ring.modul );
        }
        return val.compareTo( v );
    }


    /** ModInteger comparison.
     * @param A  ModInteger.
     * @param B  ModInteger.
     * @return sign(this-b).
     */
    public static int MICOMP(ModInteger A, ModInteger B) {
        if ( A == null ) return -B.signum();
        return A.compareTo(B);
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object b) {
        if ( ! ( b instanceof ModInteger ) ) {
            return false;
        }
        return (0 == compareTo( (ModInteger)b) );
    }


    /** Hash code for this ModInteger.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        //return 37 * val.hashCode();
        return val.hashCode();
    }


    /** ModInteger absolute value.
     * @return the absolute value of this.
     * @see edu.jas.structure.RingElem#abs()
     */
    public ModInteger abs() {
        return new ModInteger( ring, val.abs() );
    }


    /** ModInteger absolute value.
     * @param A ModInteger.
     * @return the absolute value of A.
     */
    public static ModInteger MIABS(ModInteger A) {
        if ( A == null ) return null;
        return A.abs();
    }


    /** ModInteger negative. 
     * @see edu.jas.structure.RingElem#negate()
     * @return -this.
     */
    public ModInteger negate() {
        return new ModInteger( ring, val.negate() );
    }


    /** ModInteger negative.
     * @param A ModInteger.
     * @return -A.
     */
    public static ModInteger MINEG(ModInteger A) {
        if ( A == null ) return null;
        return A.negate();
    }


    /** ModInteger signum.
     * @see edu.jas.structure.RingElem#signum()
     * @return signum(this).
     */
    public int signum() {
        return val.signum();
    }


    /** ModInteger signum.
     * @param A ModInteger
     * @return signum(A).
     */
    public static int MISIGN(ModInteger A) {
        if ( A == null ) return 0;
        return A.signum();
    }


    /** ModInteger subtraction.
     * @param S ModInteger. 
     * @return this-S.
     */
    public ModInteger subtract(ModInteger S) {
        return new ModInteger( ring, val.subtract( S.val ) );
    }


    /** ModInteger subtraction.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A-B.
     */
    public static ModInteger MIDIF(ModInteger A, ModInteger B) {
        if ( A == null ) return B.negate();
        return A.subtract(B);
    }


    /** ModInteger divide.
     * @param S ModInteger.
     * @return this/S.
     */
    public ModInteger divide(ModInteger S) {
        try {
            return multiply( S.inverse() );
        } catch (NotInvertibleException e) {
            try {
                if ( val.remainder( S.val ).equals( java.math.BigInteger.ZERO ) ) {
                   return new ModInteger( ring, val.divide( S.val ) );
                }
                throw new NotInvertibleException(e.getCause());
            } catch (ArithmeticException a) {
                throw new NotInvertibleException(a.getCause());
            }
        }
    }


    /** ModInteger quotient.
     * @param A ModInteger. 
     * @param B ModInteger.
     * @return A/B.
     */
    public static ModInteger MIQ(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.divide(B);
    }


    /** ModInteger inverse.  
     * @see edu.jas.structure.RingElem#inverse()
     * @throws NotInvertibleException if the element is not invertible.
     * @return S with S=1/this if defined. 
     */
    public ModInteger inverse() /*throws NotInvertibleException*/ {
        try {
            return new ModInteger( ring, val.modInverse( ring.modul ));
        } catch (ArithmeticException e) {
            throw new NotInvertibleException(e.getCause());
        }
    }


    /** ModInteger inverse.  
     * @param A is a non-zero integer.  
     * @see edu.jas.structure.RingElem#inverse()
     * @return S with S=1/A if defined.
     */
    public static ModInteger MIINV(ModInteger A) {
        if ( A == null ) return null;
        return A.inverse();
    }


    /** ModInteger remainder.
     * @param S ModInteger.
     * @return remainder(this,S).
     */
    public ModInteger remainder(ModInteger S) {
        if ( S == null || S.isZERO()) {
           throw new RuntimeException(this.getClass().getName()
                                      + " division by zero");
        }
        if ( S.isONE()) {
           return ring.getZERO();
        }
        if ( S.isUnit() ) {
           return ring.getZERO();
        }
        return new ModInteger( ring, val.remainder( S.val ) );
    }


    /** ModInteger remainder.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A - (A/B)*B.
     */
    public static ModInteger MIREM(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.remainder(B);
    }


    /** ModInteger multiply.
     * @param S ModInteger.
     * @return this*S.
     */
    public ModInteger multiply(ModInteger S) {
        return new ModInteger( ring, val.multiply( S.val ) );
    }


    /** ModInteger product.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A*B.
     */
    public static ModInteger MIPROD(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.multiply(B);
    }


    /** ModInteger summation.
     * @param S ModInteger.
     * @return this+S.
     */
    public ModInteger sum(ModInteger S) {
        return new ModInteger( ring, val.add( S.val ) );
    }


    /** ModInteger summation.
     * @param A ModInteger.
     * @param B ModInteger.
     * @return A+B.
     */
    public static ModInteger MISUM(ModInteger A, ModInteger B) {
        if ( A == null ) return null;
        return A.sum(B);
    }


    /** ModInteger greatest common divisor.  
     * @param S ModInteger.
     * @return gcd(this,S).
     */
    public ModInteger gcd(ModInteger S) {
        if ( S.isZERO() ) {
           return this;
        }
        if ( isZERO() ) {
           return S;
        }
        if ( isUnit() || S.isUnit() ) {
           return ring.getONE();
        }
        return new ModInteger( ring, val.gcd( S.val ) );
    }


    /**
     * ModInteger extended greatest common divisor.
     * @param S ModInteger.
     * @return [ gcd(this,S), a, b ] with a*this + b*S = gcd(this,S).
     */
    public ModInteger[] egcd(ModInteger S) {
        ModInteger[] ret = new ModInteger[3];
        ret[0] = null;
        ret[1] = null;
        ret[2] = null;
        if ( S == null || S.isZERO() ) {
           ret[0] = this;
           return ret;
        }
        if ( isZERO() ) {
           ret[0] = S;
           return ret;
        }
        if ( this.isUnit() || S.isUnit() ) {
           ret[0] = ring.getONE();
           if ( this.isUnit() && S.isUnit() ) {
              ModInteger half = ring.fromInteger(2).inverse();
              ret[1] = this.inverse().multiply(half);
              ret[2] = S.inverse().multiply(half);
              return ret;
           }
           if ( this.isUnit() ) {
              // oder inverse(S-1)?
              ret[1] = this.inverse();
              ret[2] = ring.getZERO();
              return ret;
           }
           // if ( S.isUnit() ) {
              // oder inverse(this-1)?
              ret[1] = ring.getZERO();
              ret[2] = S.inverse();
              return ret;
           //}
        }
        //System.out.println("this = " + this + ", S = " + S);
        java.math.BigInteger[] qr;
        java.math.BigInteger q = this.val; 
        java.math.BigInteger r = S.val;
        java.math.BigInteger c1 = BigInteger.ONE.val;
        java.math.BigInteger d1 = BigInteger.ZERO.val;
        java.math.BigInteger c2 = BigInteger.ZERO.val;
        java.math.BigInteger d2 = BigInteger.ONE.val;
        java.math.BigInteger x1;
        java.math.BigInteger x2;
        while ( !r.equals(java.math.BigInteger.ZERO) ) {
            qr = q.divideAndRemainder(r);
            q = qr[0];
            x1 = c1.subtract( q.multiply(d1) );
            x2 = c2.subtract( q.multiply(d2) );
            c1 = d1; c2 = d2;
            d1 = x1; d2 = x2;
            q = r;
            r = qr[1];
        }
        //System.out.println("q = " + q + "\n c1 = " + c1 + "\n c2 = " + c2);
        ret[0] = new ModInteger(ring,q); 
        ret[1] = new ModInteger(ring,c1);
        ret[2] = new ModInteger(ring,c2);
        return ret;
    }

}

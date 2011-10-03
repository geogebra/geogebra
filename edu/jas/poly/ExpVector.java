/*
 * $Id: ExpVector.java 3212 2010-07-05 12:54:49Z kredel $
 */

package edu.jas.poly;

import java.util.Random;
import java.util.Vector;
//import java.io.Serializable;

import edu.jas.structure.AbelianGroupElem;
import edu.jas.structure.AbelianGroupFactory;


/**
 * ExpVector implements exponent vectors for polynomials.
 * Exponent vectors are implemented as arrays of Java elementary types, 
 * like long, int, short and byte.
 * ExpVector provides also the familiar MAS static method names.
 * The implementation is only tested for nonnegative exponents
 * but should work also for negative exponents.
 * Objects of this class are intended to be immutable, but 
 * exponents can be set (during construction);
 * also the hash code is only computed once, when needed. 
 * The different storage unit implementations are <code>ExpVectorLong</code>
 * <code>ExpVectorInteger</code>, <code>ExpVectorShort</code>
 * and <code>ExpVectorByte</code>.
 * The static factory methods <code>create()</code> of <code>ExpVector</code>
 * select the respective storage unit.
 * The selection of the desired storage unit is internally done via the static variable 
 * <code>storunit</code>. This varaible should not be changed dynamically.
 * @author Heinz Kredel
 */

public abstract class ExpVector implements AbelianGroupElem<ExpVector>
                                           /*Cloneable, Serializable*/ {


    /**
     * Stored hash code.
     */
    protected int hash = 0;


    /**
     * Random number generator.
     */
    private final static Random random = new Random();


    /**
     * Storage representation of exponent arrays.
     */
    public static enum StorUnit { LONG, INT, SHORT, BYTE };


    /**
     * Used storage representation of exponent arrays.
     * <b>Note:</b> Set this only statically and not dynamically. 
     */
    public final static StorUnit storunit = StorUnit.LONG;


    /**
     * Constructor for ExpVector.
     */
    public ExpVector() {
       hash = 0;
    }


    /**
     * Factory constructor for ExpVector.
     * @param n length of exponent vector.
     */
    public static ExpVector create(int n) {
        switch ( storunit ) {
        case INT:   return new ExpVectorInteger(n);
        case LONG:  return new ExpVectorLong(n);
        case SHORT: return new ExpVectorShort(n);
        case BYTE:  return new ExpVectorByte(n);
        default:    return new ExpVectorInteger(n);
        }
    }


    /**
     * Factory constructor for ExpVector.
     * Sets exponent i to e.
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public static ExpVector create(int n, int i, long e) {
        switch ( storunit ) {
        case INT:   return new ExpVectorInteger(n, i, e);
        case LONG:  return new ExpVectorLong(n, i, e);
        case SHORT: return new ExpVectorShort(n, i, e);
        case BYTE:  return new ExpVectorByte(n, i, e);
        default:    return new ExpVectorInteger(n, i, e);
        }
    }


    /**
     * Internal factory constructor for ExpVector.
     * Sets val.
     * @param v internal representation array.
     */
    public static ExpVector create(long[] v) {
        switch ( storunit ) {
        case INT:   return new ExpVectorInteger(v);
        case LONG:  return new ExpVectorLong(v);
        case SHORT: return new ExpVectorShort(v);
        case BYTE:  return new ExpVectorByte(v);
        default:    return new ExpVectorInteger(v);
        }
    }


    /**
     * Factory constructor for ExpVector.
     * Converts a String representation to an ExpVector.
     * Accepted format = (1,2,3,4,5,6,7).
     * @param s String representation.
     */
    public static ExpVector create(String s) {
        switch ( storunit ) {
        case INT:   return new ExpVectorInteger(s);
        case LONG:  return new ExpVectorLong(s);
        case SHORT: return new ExpVectorShort(s);
        case BYTE:  return new ExpVectorByte(s);
        default:    return new ExpVectorInteger(s);
        }
    }


    /**
     * Get the corresponding element factory.
     * @return factory for this Element.
     * @see edu.jas.structure.Element#factory()
     */
    public AbelianGroupFactory<ExpVector> factory() {
        //return null;
        throw new RuntimeException("no factory implemented for ExpVector");
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     * <b>Note: </b> returns true because of finite set of values in each index.
     */
    public boolean isFinite() {
        return true;
    }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public abstract ExpVector clone();


    /**
     * Get the exponent vector. 
     * @return val.
     */
    /*package*/ abstract long[] getVal();


    /**
     * Get the exponent at position i. 
     * @param i position.
     * @return val[i].
     */
    public abstract long getVal(int i);


    /**
     * Set the exponent at position i to e. 
     * @param i
     * @param e
     * @return old val[i].
     */
    protected abstract long setVal(int i, long e);


    /**
     * Get the length of this exponent vector. 
     * @return val.length.
     */
    public abstract int length();


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend this by i elements and set val[j] to e.
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    public abstract ExpVector extend(int i, int j, long e);


    /**
     * Extend lower variables. 
     * Extend this by i lower elements and set val[j] to e.
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    public abstract ExpVector extendLower(int i, int j, long e);


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract this to len elements.
     * @param i position of first element to be copied.
     * @param len new length.
     * @return contracted exponent vector.
     */
    public abstract ExpVector contract(int i, int len);


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return reversed exponent vector.
     */
    public abstract ExpVector reverse();


    /**
     * Reverse j variables. Used e.g. in opposite rings.
     * Reverses the first j-1 variables, the rest is unchanged.
     * @param j index of first variable not reversed.
     * @return reversed exponent vector.
     */
    public abstract ExpVector reverse(int j);


    /**
     * Combine with ExpVector. 
     * Combine this with the other ExpVector V.
     * @param V the other exponent vector.
     * @return combined exponent vector.
     */
    public abstract ExpVector combine( ExpVector V );


    /** Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("(");
        for (int i = 0; i < length(); i++ ) {
            s.append( getVal(i) );
            if ( i < length()-1 ) {
               s.append(",");
            }
        }
        s.append(")");
        return s.toString();
    }


    /** Get the string representation with variable names.
     * @param vars names of variables.
     * @see java.lang.Object#toString()
     */
    public String toString(String[] vars) {
        String s = "";
        boolean pit;
        int r = length();
        if ( r != vars.length ) {
            return toString();
        }
        if ( r == 0 ) {
            return s;
        }
        long vi;
        for (int i = r-1; i > 0; i-- ) {
            vi = getVal( i );
            if ( vi != 0 ) { 
                s += vars[ r-1-i ];
                if ( vi != 1 ) {
                    s += "^" + vi;
                }
                pit = false;
                for ( int j = i-1; j >= 0; j-- ) {
                    if ( getVal(j) != 0 ) {
                       pit = true;
                    }
                }
                if ( pit ) s += " * ";
            }
        }
        vi = getVal( 0 );
        if ( vi != 0 ) { 
            s += vars[ r-1 ];
            if ( vi != 1 ) {
                s += "^" + vi + "";
            }
        }
        return s; 
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        return toScript( stdVars()  );
    }


    /** Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    // @Override
    public String toScript(String[] vars) {
        // Python case
        int r = length();
        if ( r != vars.length ) {
            return toString();
        }
        StringBuffer s = new StringBuffer();
        boolean pit;
        long vi;
        for ( int i = r-1; i > 0; i-- ) {
            vi = getVal( i );
            if ( vi != 0 ) { 
                s.append( vars[ r-1-i ] );
                if ( vi != 1 ) {
                    s.append("**" + vi);
                }
                pit = false;
                for ( int j = i-1; j >= 0; j-- ) {
                    if ( getVal(j) != 0 ) {
                       pit = true;
                    }
                }
                if ( pit ) { 
                    s.append(" * ");
                }
            }
        }
        vi = getVal( 0 );
        if ( vi != 0 ) { 
            s.append( vars[ r-1 ] );
            if ( vi != 1 ) {
                s.append("**" + vi);
            }
        }
        return s.toString();
    }


    /** Get a scripting compatible string representation of the factory.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.Element#toScriptFactory()
     */
    //JAVA6only: @Override
    public String toScriptFactory() {
        // Python case
        return "ExpVector()";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object B ) { 
        if ( ! (B instanceof ExpVector) ) {
            return false;
        }
        ExpVector b = (ExpVector)B;
        int t = this.invLexCompareTo( b );
        //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
        return (0 == t);
    }


    /** hashCode.
     * Optimized for small exponents, i.e. &le; 2<sup>4</sup>
     * and small number of variables, i.e. &le; 8.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() { 
        if ( hash == 0 ) {
           for (int i = 0; i < length(); i++ ) {
               hash = hash<<4 + getVal(i);
           }
           if ( hash == 0 ) {
              hash = 1;
           }
        }
        return hash;
    }


    /** Is ExpVector zero. 
     * @return If this has all elements 0 then true is returned, else false.
     */
    public boolean isZERO() { 
        return ( 0 == this.signum() );
    }


    /**
     * Standard variable names.
     * Generate standard names for variables, 
     * i.e. x0 to x(n-1). 
     * @return standard names.
     */
    public String[] stdVars() {
        return STDVARS("x",length());
    }


    /**
     * Generate variable names.
     * Generate names for variables, 
     * i.e. prefix0 to prefix(n-1). 
     * @param prefix name prefix.
     * @return standard names.
     */
    public String[] stdVars(String prefix) {
        return STDVARS(prefix,length());
    }


    /**
     * Standard variable names.
     * Generate standard names for variables, 
     * i.e. x0 to x(n-1). 
     * @param n size of names array
     * @return standard names.
     */
    public static String[] STDVARS(int n) {
        return STDVARS("x",n);
    }


    /**
     * Generate variable names.
     * Generate names for variables from given prefix.
     * i.e. prefix0 to prefix(n-1). 
     * @param n size of names array.
     * @param prefix name prefix.
     * @return vatiable names.
     */
    public static String[] STDVARS(String prefix, int n) {
        String[] vars = new String[ n ];
        if ( prefix == null || prefix.length() == 0 ) {
            prefix = "x";
        }
        for ( int i = 0; i < n; i++) {
            vars[i] = prefix + i; //(n-1-i);
        }
        return vars;
    }


    /**
     * ExpVector absolute value.
     * @param U
     * @return abs(U).
     */
    public static ExpVector EVABS( ExpVector U ) {
        return U.abs();
    }


    /**
     * ExpVector absolute value.
     * @return abs(this).
     */
    public abstract ExpVector abs();


    /**
     * ExpVector negate.
     * @param U
     * @return -U.
     */
    public static ExpVector EVNEG( ExpVector U ) {
        return U.negate();
    }


    /**
     * ExpVector negate.
     * @return -this.
     */
    public abstract ExpVector negate( );


    /**
     * ExpVector summation.
     * @param U
     * @param V
     * @return U+V.
     */
    public static ExpVector EVSUM( ExpVector U, ExpVector V ) {
        return U.sum(V);
    }


    /**
     * ExpVector summation.
     * @param V
     * @return this+V.
     */
    public abstract ExpVector sum( ExpVector V );


    /**
     * ExpVector difference.
     * Result may have negative entries.
     * @param U
     * @param V
     * @return U-V.
     */
    public static ExpVector EVDIF( ExpVector U, ExpVector V ) {
        return U.subtract(V);
    }


    /**
     * ExpVector subtract.
     * Result may have negative entries.
     * @param V
     * @return this-V.
     */
    public abstract ExpVector subtract( ExpVector V );


    /**
     * ExpVector substitution.
     * Clone and set exponent to d at position i.
     * @param U
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public static ExpVector EVSU( ExpVector U, int i, long d ) {
        return U.subst(i,d);
    }


    /**
     * ExpVector substitution.
     * Clone and set exponent to d at position i.
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public ExpVector subst( int i, long d ) {
        ExpVector V = (ExpVector)this.clone();
        long e = V.setVal( i, d );
        return V;
        //return EVSU(this, i, d);
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @return random ExpVector.
     */
    public static ExpVector EVRAND( int r, long k, float q ) {
        return EVRAND(r,k,q,random);
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return random ExpVector.
     */
    public static ExpVector EVRAND( int r, long k, float q, Random rnd ) {
        long[] w = new long[r];
        long e;
        float f;
        for (int i = 0; i < w.length; i++ ) {
            f = rnd.nextFloat(); 
            if ( f > q ) { 
                e = 0; 
            } else { 
                e = rnd.nextLong() % k; 
                if ( e < 0 ) {
                    e = -e;
                } 
            }
            w[i] = e;
        }
        return create( w );
        //return new ExpVector( w );
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @return random ExpVector.
     */
    public static ExpVector random( int r, long k, float q ) {
        return EVRAND(r,k,q,random);
    }


    /**
     * Generate a random ExpVector.
     * @param r length of new ExpVector. 
     * @param k maximal degree in each exponent.
     * @param q density of nozero exponents.
     * @param rnd is a source for random bits.
     * @return random ExpVector.
     */
    public static ExpVector random( int r, long k, float q, Random rnd ) {
        return EVRAND(r,k,q,rnd);
    }


    /**
     * ExpVector sign.
     * @param U
     * @return 0 if U is zero, -1 if some entry is negative, 
     1 if no entry is negativ and at least one entry is positive.
    */
    public static int EVSIGN( ExpVector U ) {
        return U.signum();
    }


    /**
     * ExpVector signum.
     * @return 0 if this is zero, -1 if some entry is negative, 
     *  1 if no entry is negative and at least one entry is positive.
     */
    public abstract int signum();


    /**
     * ExpVector total degree.
     * @param U
     * @return sum of all exponents.
     */
    public static long EVTDEG( ExpVector U ) {
        return U.totalDeg();
    }


    /**
     * ExpVector total degree.
     * @return sum of all exponents.
     */
    public abstract long totalDeg();


    /**
     * ExpVector maximal degree.
     * @param U
     * @return maximal exponent.
     */
    public static long EVMDEG( ExpVector U ) {
        return U.maxDeg();
    }


    /**
     * ExpVector maximal degree.
     * @return maximal exponent.
     */
    public abstract long maxDeg();


    /**
     * ExpVector weighted degree.
     * @param w weights.
     * @param U
     * @return weighted sum of all exponents.
     */
    public static long EVWDEG( long[][] w, ExpVector U ) {
        return U.weightDeg( w );
    }


    /**
     * ExpVector weighted degree.
     * @param w weights.
     * @return weighted sum of all exponents.
     */
    public abstract long weightDeg( long[][] w );


    /**
     * ExpVector least common multiple.
     * @param U
     * @param V
     * @return component wise maximum of U and V.
     */
    public static ExpVector EVLCM( ExpVector U, ExpVector V ) {
        return U.lcm(V);
    }


    /**
     * ExpVector least common multiple.
     * @param V
     * @return component wise maximum of this and V.
     */
    public abstract ExpVector lcm( ExpVector V );


    /**
     * ExpVector greatest common divisor.
     * @param U
     * @param V
     * @return component wise minimum of U and V.
     */
    public static ExpVector EVGCD( ExpVector U, ExpVector V ) {
        return U.gcd(V);
    }


    /**
     * ExpVector greatest common divisor.
     * @param V
     * @return component wise minimum of this and V.
     */
    public abstract ExpVector gcd( ExpVector V );


    /**
     * ExpVector dependency on variables.
     * @param U
     * @return array of indices where U has positive exponents.
     */
    public static int[] EVDOV( ExpVector U ) {
        return U.dependencyOnVariables();
    }


    /**
     * ExpVector dependency on variables.
     * @return array of indices where val has positive exponents.
     */
    public abstract int[] dependencyOnVariables();


    /**
     * ExpVector multiple test.
     * Test if U is component wise greater or equal to V.
     * @param U
     * @param V
     * @return true if U is a multiple of V, else false.
     */
    public static boolean EVMT( ExpVector U, ExpVector V ) {
        return U.multipleOf(V);
    }


    /**
     * ExpVector multiple test.
     * Test if this is component wise greater or equal to V.
     * @param V
     * @return true if this is a multiple of V, else false.
     */
    public abstract boolean multipleOf( ExpVector V );


    /**
     * ExpVector divides test.
     * Test if V is component wise greater or equal to this.
     * @param V
     * @return true if this divides V, else false.
     */
    public boolean divides( ExpVector V ) {
        return V.multipleOf(this);
        //return EVMT(V, this);
    }


    /**
     * ExpVector compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    //JAVA6only: @Override
    public int compareTo( ExpVector V ) {
        return this.invLexCompareTo(V);
    }


    /**
     * Inverse lexicographical compare.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVILCP( ExpVector U, ExpVector V ) {
        return U.invLexCompareTo(V);
    }


    /**
     * ExpVector inverse lexicographical compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int invLexCompareTo( ExpVector V );


    /**
     * Inverse lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVILCP( ExpVector U, ExpVector V, int begin, int end ) {
        return U.invLexCompareTo(V,begin,end);
    }


    /**
     * ExpVector inverse lexicographical compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int invLexCompareTo( ExpVector V, int begin, int end );


    /**
     * Inverse graded lexicographical compare.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIGLC( ExpVector U, ExpVector V ) {
        return U.invGradCompareTo(V);
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int invGradCompareTo( ExpVector V );


    /**
     * Inverse graded lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIGLC( ExpVector U, ExpVector V, int begin, int end ) {
        return U.invGradCompareTo(V,begin,end);
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int invGradCompareTo( ExpVector V, int begin, int end );


    /**
     * Reverse inverse lexicographical compare.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVRILCP( ExpVector U, ExpVector V ) {
        return U.revInvLexCompareTo(V);
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int revInvLexCompareTo( ExpVector V );


    /**
     * Reverse inverse lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVRILCP( ExpVector U, ExpVector V, int begin, int end ) {
        return U.revInvLexCompareTo(V,begin,end);
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int revInvLexCompareTo( ExpVector V, int begin, int end );


    /**
     * Reverse inverse graded lexicographical compare.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVRIGLC( ExpVector U, ExpVector V ) {
        return U.revInvGradCompareTo(V);
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int revInvGradCompareTo( ExpVector V );


    /**
     * Reverse inverse graded lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVRIGLC( ExpVector U, ExpVector V, int begin, int end ) {
        return U.revInvGradCompareTo(V,begin,end);
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int revInvGradCompareTo( ExpVector V, int begin, int end );


    /**
     * Inverse weighted lexicographical compare.
     * @param w weight array.
     * @param U
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIWLC( long[][] w, ExpVector U, ExpVector V ) {
        return U.invWeightCompareTo(w,V);
    }


    /**
     * ExpVector inverse weighted lexicographical compareTo.
     * @param w weight array.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int invWeightCompareTo( long[][] w, ExpVector V );


    /**
     * Inverse weighted lexicographical compare part.
     * Compare entries between begin and end (-1).
     * @param w weight array.
     * @param U
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public static int EVIWLC( long[][] w, ExpVector U, ExpVector V, int begin, int end ) {
        return U.invWeightCompareTo(w,V,begin,end);
    }


    /**
     * ExpVector inverse weighted lexicographical compareTo.
     * @param w weight array.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public abstract int invWeightCompareTo( long[][] w, ExpVector V, int begin, int end );

}

/*
 * $Id: ExpVectorShort.java 3090 2010-04-26 20:28:34Z kredel $
 */

package edu.jas.poly;

import java.util.Random;
import java.util.Arrays;
import java.util.Vector;
//import java.io.Serializable;

import edu.jas.structure.AbelianGroupElem;


/**
 * ExpVectorShort implements exponent vectors for polynomials using 
 * arrays of short as storage unit.
 * This class is used by ExpVector internally, there is no need to use this class directly.
 * @see ExpVector
 * @author Heinz Kredel
 */

public class ExpVectorShort extends ExpVector 
                   /*implements AbelianGroupElem<ExpVectorShort>*/ {


    /**
     * The data structure is an array of longs.
     */
    /*package*/ final short[] val;


    /**
     * Largest short.
     */
    public static long maxShort = (long)Short.MAX_VALUE / 2;


    /**
     * Smallest short.
     */
    public static long minShort = (long)Short.MIN_VALUE / 2;


    /**
     * Constructor for ExpVector.
     * @param n length of exponent vector.
     */
    public ExpVectorShort(int n) {
        this( new short[n] );
    }

    
    /**
     * Constructor for ExpVector.
     * Sets exponent i to e.
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public ExpVectorShort(int n, int i, short e) {
        this( n );
        val[i] = e;
    }


    /**
     * Constructor for ExpVector.
     * Sets exponent i to e.
     * @param n length of exponent vector.
     * @param i index of exponent to be set.
     * @param e exponent to be set.
     */
    public ExpVectorShort(int n, int i, long e) {
        this( n );
        if ( e >= maxShort || e <= minShort ) {
            throw new IllegalArgumentException("exponent to large: "+e);
        }
        val[i] = (short)e;
    }


    /**
     * Internal constructor for ExpVector.
     * Sets val.
     * @param v internal representation array.
     */
    protected ExpVectorShort(short[] v) {
        super();
        val = v;
    }


    /**
     * Constructor for ExpVector.
     * Sets val, converts from long array.
     * @param v long representation array.
     */
    public ExpVectorShort(long[] v) {
        this( v.length );
        for ( int i = 0; i < v.length; i++ ) {
            if ( v[i] >= maxShort || v[i] <= minShort ) {
                throw new IllegalArgumentException("exponent to large: "+v[i]);
            }
            val[i] = (short)v[i];
        }
    }


    /**
     * Constructor for ExpVector.
     * Converts a String representation to an ExpVector.
     * Accepted format = (1,2,3,4,5,6,7).
     * @param s String representation.
     */
    public ExpVectorShort(String s) throws NumberFormatException {
        super();
        // first format = (1,2,3,4,5,6,7)
        Vector<Short> exps = new Vector<Short>();
        s = s.trim();
        int b = s.indexOf('(');
        int e = s.indexOf(')',b+1);
        String teil;
        int k;
        short a;
        if ( b >= 0 && e >= 0 ) {
            b++;
            while ( ( k = s.indexOf(',',b) ) >= 0 ) {
                teil = s.substring(b,k);
                a = Short.parseShort( teil );
                exps.add( new Short( a ) ); 
                b = k + 1;
            }
            if ( b <= e ) {
                teil = s.substring(b,e);
                a = Short.parseShort( teil );
                exps.add( new Short( a ) ); 
            }
            int length = exps.size();
            val = new short[ length ];
            for ( int j = 0; j < length; j++ ) {
                val[j] = exps.elementAt(j).shortValue();
            }
        } else {
        // not implemented
        val = null;
        // length = -1;
        //Vector names = new Vector();
        //vars = s;
        }
    }


    /** Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public ExpVectorShort clone() {
        short[] w = new short[ val.length ];
        System.arraycopy(val,0,w,0,val.length);
        return new ExpVectorShort( w );
    }


    /**
     * Get the exponent vector. 
     * @return val as long.
     */
    /*package*/ long[] getVal() {
     long v[] = new long[val.length];
     for ( int i = 0; i < val.length; i++ ) {
         v[i] = val[i];
     }
        return v;
    } 


    /**
     * Get the exponent at position i. 
     * @param i position.
     * @return val[i].
     */
    public long getVal(int i) {
        return val[i];
    } 


    /**
     * Set the exponent at position i to e. 
     * @param i
     * @param e
     * @return old val[i].
     */
    protected long setVal(int i, long e) {
        short x = val[i];
        if ( e >= maxShort || e <= minShort ) {
           throw new IllegalArgumentException("exponent to large: "+e);
        }
        val[i] = (short)e;
        hash = 0; // beware of race condition
        return x;
    } 


    /**
     * Set the exponent at position i to e. 
     * @param i
     * @param e
     * @return old val[i].
     */
    protected short setVal(int i, short e) {
        short x = val[i];
        val[i] = e;
        hash = 0; // beware of race condition
        return x;
    } 


    /**
     * Get the length of this exponent vector. 
     * @return val.length.
     */
    public int length() {
        return val.length; 
    } 


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend this by i elements and set val[j] to e.
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    public ExpVectorShort extend(int i, int j, long e) {
        short[] w = new short[ val.length + i ];
        System.arraycopy(val,0,w,i,val.length);
        if ( j >= i ) {
           throw new RuntimeException("i "+i+" <= j "+j+" invalid");
        }
        if ( e >= maxShort || e <= minShort ) {
           throw new IllegalArgumentException("exponent to large: "+e);
        }
        w[j] = (short)e;
        return new ExpVectorShort( w );
    }


    /**
     * Extend lower variables. 
     * Extend this by i lower elements and set val[j] to e.
     * @param i number of elements to extend.
     * @param j index of element to be set.
     * @param e new exponent for val[j].
     * @return extended exponent vector.
     */
    public ExpVectorShort extendLower(int i, int j, long e) {
        short[] w = new short[ val.length + i ];
        System.arraycopy(val,0,w,0,val.length);
        if ( j >= i ) {
           throw new RuntimeException("i "+i+" <= j "+j+" invalid");
        }
        w[ val.length + j ] = (short)e;
        return new ExpVectorShort( w );
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract this to len elements.
     * @param i position of first element to be copied.
     * @param len new length.
     * @return contracted exponent vector.
     */
    public ExpVectorShort contract(int i, int len) {
        if ( i+len > val.length ) {
           throw new RuntimeException("len "+len+" > val.len "+val.length);
        }
        short[] w = new short[ len ];
        System.arraycopy(val,i,w,0,len);
        return new ExpVectorShort( w );
    }


    /**
     * Reverse variables. Used e.g. in opposite rings.
     * @return reversed exponent vector.
     */
    public ExpVectorShort reverse() {
        short[] w = new short[ val.length ];
        for ( int i = 0; i < val.length; i++ ) {
            w[i] = val[ val.length - 1 - i ];
        }
        return new ExpVectorShort( w );
    }


    /**
     * Reverse j variables. Used e.g. in opposite rings.
     * Reverses the first j-1 variables, the rest is unchanged.
     * @param j index of first variable not reversed.
     * @return reversed exponent vector.
     */
    public ExpVectorShort reverse(int j) {
        if ( j <= 0 || j > val.length ) {
           return this;
        }
        short[] w = new short[ val.length ];
        for ( int i = 0; i < j; i++ ) {
            w[i] = val[ j - 1 - i ];
        }
        // copy rest
        for ( int i = j; i < val.length; i++ ) {
            w[i] = val[ i ];
        }
        return new ExpVectorShort( w );
    }


    /**
     * Combine with ExpVector. 
     * Combine this with the other ExpVector V.
     * @param V the other exponent vector.
     * @return combined exponent vector.
     */
    public ExpVectorShort combine( ExpVector V ) {
        if ( V == null || V.length() == 0 ) {
            return this;
        }
        ExpVectorShort Vi = (ExpVectorShort)V;
        if ( val.length == 0 ) {
            return Vi;
        }
        short[] w = new short[ val.length + Vi.val.length ];
        System.arraycopy(val,0,w,0,val.length);
        System.arraycopy(Vi.val,0,w,val.length,Vi.val.length);
        return new ExpVectorShort( w );
    }


    /** Get the string representation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString()+":short";
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object B ) { 
        if ( ! (B instanceof ExpVectorShort) ) {
            return false;
        }
        ExpVectorShort b = (ExpVectorShort)B;
        int t = this.invLexCompareTo( b );
        //System.out.println("equals: this = " + this + " B = " + B + " t = " + t);
        return (0 == t);
    }


    /**
     * ExpVector absolute value.
     * @return abs(this).
     */
    public ExpVectorShort abs() {
        short[] u = val; 
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] >= 0L ) {
               w[i] = u[i];
            } else {
          w[i] = (short) ( - u[i] );
            }
        }
        return new ExpVectorShort( w );
        //return EVABS(this);
    }


    /**
     * ExpVector negate.
     * @return -this.
     */
    public ExpVectorShort negate( ) {
        short[] u = val; 
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++ ) {
            w[i] = (short) ( - u[i] );
        }
        return new ExpVectorShort( w );
     // return EVNEG(this);
    }


    /**
     * ExpVector summation.
     * @param V
     * @return this+V.
     */
    public ExpVectorShort sum( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++ ) {
            w[i] = (short) ( u[i] + v[i] );
        }
        return new ExpVectorShort( w );
     // return EVSUM(this, V);
    }


    /**
     * ExpVector subtract.
     * Result may have negative entries.
     * @param V
     * @return this-V.
     */
    public ExpVectorShort subtract( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++ ) { 
            w[i] = (short) ( u[i] - v[i] );
        }
        return new ExpVectorShort( w );
        //return EVDIF(this, V);
    }


    /**
     * ExpVector substitution.
     * Clone and set exponent to d at position i.
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public ExpVectorShort subst( int i, short d ) {
        ExpVectorShort V = (ExpVectorShort)this.clone();
        long e = V.setVal( i, d );
        return V;
        //return EVSU(this, i, d);
    }


    /**
     * ExpVector substitution.
     * Clone and set exponent to d at position i.
     * @param i position.
     * @param d new exponent.
     * @return substituted ExpVector.
     */
    public ExpVectorShort subst( int i, long d ) {
        ExpVectorShort V = (ExpVectorShort)this.clone();
        long e = V.setVal( i, d );
        return V;
        //return EVSU(this, i, d);
    }


    /**
     * ExpVector signum.
     * @return 0 if this is zero, -1 if some entry is negative, 
     *  1 if no entry is negative and at least one entry is positive.
     */
    public int signum( ) {
        int t = 0;
        short[] u = val; 
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] < 0 ) {
                return -1;
            }
            if ( u[i] > 0 ) {
                t = 1;
            }
        }
        return t;
        //return EVSIGN(this);
    }


    /**
     * ExpVector total degree.
     * @return sum of all exponents.
     */
    public long totalDeg( ) {
        long t = 0;
        short[] u = val; // U.val;
        for (int i = 0; i < u.length; i++ ) {
            t += u[i];
        }
        return t;
        //return EVTDEG(this);
    }


    /**
     * ExpVector maximal degree.
     * @return maximal exponent.
     */
    public long maxDeg( ) {
        long t = 0;
        short[] u = val; 
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > t ) {
               t = u[i];
            }
        }
        return t;
        //return EVMDEG(this);
    }


    /**
     * ExpVector weighted degree.
     * @param w weights.
     * @return weighted sum of all exponents.
     */
    public long weightDeg( long[][] w ) {
        if ( w == null || w.length == 0 ) { 
            return totalDeg(); // assume weight 1 
        }
        long t = 0;
        short[] u = val; 
        for ( int j = 0; j < w.length; j++ ) {
            long[] wj = w[j];
            for (int i = 0; i < u.length; i++ ) {
                t += wj[i] * u[i];
            }
        }
        return t;
     //return EVWDEG( w, this );
    }


    /**
     * ExpVector least common multiple.
     * @param V
     * @return component wise maximum of this and V.
     */
    public ExpVectorShort lcm( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++ ) {
            w[i] = ( u[i] >= v[i] ? u[i] : v[i] );
        }
        return new ExpVectorShort( w );
        //return EVLCM(this, V);
    }


    /**
     * ExpVector greatest common divisor.
     * @param V
     * @return component wise minimum of this and V.
     */
    public ExpVectorShort gcd( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        short[] w = new short[u.length];
        for (int i = 0; i < u.length; i++ ) {
            w[i] = ( u[i] <= v[i] ? u[i] : v[i] );
        }
        return new ExpVectorShort( w );
        //return EVGCD(this, V);
    }


    /**
     * ExpVector dependency on variables.
     * @return array of indices where val has positive exponents.
     */
    public int[] dependencyOnVariables() {
        short[] u = val;
        int l = 0;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > 0 ) {
                l++;
            }
        }
        int[] dep = new int[ l ];
        if ( l == 0 ) {
            return dep;
        }
        int j = 0;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > 0 ) {
                dep[j] = i; j++;
            }
        }
        return dep;
    }


    /**
     * ExpVector multiple test.
     * Test if this is component wise greater or equal to V.
     * @param V
     * @return true if this is a multiple of V, else false.
     */
    public boolean multipleOf( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        boolean t = true;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] < v[i] ) { 
                return false;
            }
        }
        return t;
        //return EVMT(this, V);
    }


    /**
     * ExpVector compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    //@Override
    public int compareTo( ExpVector V ) {
        return this.invLexCompareTo(V);
    }


    /**
     * ExpVector inverse lexicographical compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int invLexCompareTo( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        for (int i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) return 1;
            if ( u[i] < v[i] ) return -1;
        }
        return t;
        //return EVILCP(this, V);
    }


    /**
     * ExpVector inverse lexicographical compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int invLexCompareTo( ExpVector V, int begin, int end  ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        for (int i = begin; i < end; i++ ) {
            if ( u[i] > v[i] ) return 1;
            if ( u[i] < v[i] ) return -1;
        }
        return t;
        //return EVILCP(this, V, begin, end);
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int invGradCompareTo( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        int i;
        for ( i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) { 
                t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
                t = -1; break; 
            }
        }
        if ( t == 0 ) { 
            return t;
        }
        long up = 0; 
        long vp = 0; 
        for (int j = i; j < u.length; j++ ) {
            up += u[j]; 
            vp += v[j]; 
        }
        if ( up > vp ) { 
            t = 1; 
        } else { 
            if ( up < vp ) { 
                t = -1; 
            }
        }
        return t;
        //return EVIGLC(this, V);
    }


    /**
     * ExpVector inverse graded lexicographical compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int invGradCompareTo( ExpVector V, int begin, int end ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        int i;
        for ( i = begin; i < end; i++ ) {
            if ( u[i] > v[i] ) { 
                t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
                t = -1; break; 
            }
        }
        if ( t == 0 ) {
            return t;
        }
        long up = 0; 
        long vp = 0; 
        for (int j = i; j < end; j++ ) {
            up += u[j]; 
            vp += v[j]; 
        }
        if ( up > vp ) { 
            t = 1; 
        } else { 
            if ( up < vp ) { 
                t = -1; 
            }
        }
        return t;
        //return EVIGLC(this, V, begin, end);
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int revInvLexCompareTo( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        for (int i = u.length-1; i >= 0; i-- ) {
            if ( u[i] > v[i] ) return 1;
            if ( u[i] < v[i] ) return -1;
        }
        return t;
        //return EVRILCP(this, V);
    }


    /**
     * ExpVector reverse inverse lexicographical compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int revInvLexCompareTo( ExpVector V, int begin, int end ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        for (int i = end-1; i >= begin; i-- ) {
            if ( u[i] > v[i] ) return 1;
            if ( u[i] < v[i] ) return -1;
        }
        return t;
        //return EVRILCP(this, V, begin, end);
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int revInvGradCompareTo( ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        int i;
        for ( i = u.length-1; i >= 0; i-- ) {
            if ( u[i] > v[i] ) { 
                t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
                t = -1; break; 
            }
        }
        if ( t == 0 ) { 
            return t;
        }
        long up = 0; 
        long vp = 0; 
        for (int j = i; j >= 0; j-- ) {
            up += u[j]; 
            vp += v[j]; 
        }
        if ( up > vp ) { 
            t = 1; 
        } else { 
            if ( up < vp ) { 
                t = -1; 
            }
        }
        return t;
        //return EVRIGLC(this, V);
    }


    /**
     * ExpVector reverse inverse graded compareTo.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int revInvGradCompareTo( ExpVector V, int begin, int end ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        int i;
        for ( i = end-1; i >= begin; i-- ) {
            if ( u[i] > v[i] ) { 
                t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
                t = -1; break; 
            }
        }
        if ( t == 0 ) {
            return t;
        }
        long up = 0; 
        long vp = 0; 
        for (int j = i; j >= begin; j-- ) {
            up += u[j]; 
            vp += v[j]; 
        }
        if ( up > vp ) { 
            t = 1; 
        } else { 
            if ( up < vp ) { 
                t = -1; 
            }
        }
        return t;
        //return EVRIGLC(this, V, begin, end);
    }


    /**
     * ExpVector inverse weighted lexicographical compareTo.
     * @param w weight array.
     * @param V
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int invWeightCompareTo( long[][] w, ExpVector V ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        int i;
        for ( i = 0; i < u.length; i++ ) {
            if ( u[i] > v[i] ) { 
                t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
                t = -1; break; 
            }
        }
        if ( t == 0 ) {
            return t;
        }
        for ( int k = 0; k < w.length; k++ ) {
            long[] wk = w[k];
            long up = 0; 
            long vp = 0; 
            for (int j = i; j < u.length; j++ ) {
                up += wk[j] * u[j]; 
                vp += wk[j] * v[j]; 
            }
            if ( up > vp ) { 
                return 1;
            } else if ( up < vp ) { 
                return -1;
            }
        }
        return t;
        //return EVIWLC(w, this, V);
    }


    /**
     * ExpVector inverse weighted lexicographical compareTo.
     * @param w weight array.
     * @param V
     * @param begin
     * @param end
     * @return 0 if U == V, -1 if U &lt; V, 1 if U &gt; V.
     */
    public int invWeightCompareTo( long[][] w, ExpVector V, int begin, int end ) {
        short[] u = val; 
        short[] v = ((ExpVectorShort)V).val;
        int t = 0;
        int i;
        for ( i = begin; i < end; i++ ) {
            if ( u[i] > v[i] ) { 
                t = 1; break; 
            }
            if ( u[i] < v[i] ) { 
                t = -1; break; 
            }
        }
        if ( t == 0 ) {
            return t;
        }
        for ( int k = 0; k < w.length; k++ ) {
            long[] wk = w[k];
            long up = 0; 
            long vp = 0; 
            for (int j = i; j < end; j++ ) {
                up += wk[j] * u[j]; 
                vp += wk[j] * v[j]; 
            }
            if ( up > vp ) { 
                return 1;
            } else if ( up < vp ) { 
                return -1;
            }
        }
        return t;
        //return EVIWLC(w, this, V, begin, end);
    }

}

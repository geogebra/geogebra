/*
 * $Id: ExpVectorPair.java 1888 2008-07-12 13:37:34Z kredel $
 */

package edu.jas.poly;

import java.io.Serializable;


/**
 * ExpVectorPair 
 * implements pairs of exponent vectors for S-polynomials.
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */


public class ExpVectorPair implements Serializable {

    private final ExpVector e1;
    private final ExpVector e2;


    /**
     * Constructors for ExpVectorPair.
     * @param e first part.
     * @param f second part.
     */
    public ExpVectorPair(ExpVector e, ExpVector f) {
        e1 = e;
        e2 = f;
    }


    /**
     * @return first part.
     */
    public ExpVector getFirst() {
        return e1;
    } 


    /**
     * @return second part.
     */
    public ExpVector getSecond() {
        return e2;
    } 


    /**
     * toString.
     */
    @Override
     public String toString() {
        StringBuffer s = new StringBuffer("ExpVectorPair[");
        s.append(e1.toString());
        s.append(",");
        s.append(e2.toString());
        s.append("]");
        return s.toString();
    }


    /**
     * equals.
     * @param B other.
     * @return true, if this == b, else false.
     */
    @Override
     public boolean equals(Object B) { 
       if ( ! (B instanceof ExpVectorPair) ) return false;
       return equals( (ExpVectorPair)B );
    }


    /**
     * equals.
     * @param b other.
     * @return true, if this == b, else false.
     */
    public boolean equals(ExpVectorPair b) { 
       boolean t = e1.equals( b.getFirst() ); 
       t = t && e2.equals( b.getSecond() ); 
       return t;
    }


    /** hash code.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
       return (e1.hashCode() << 16) + e2.hashCode();
    }

        
    /**
     * isMultiple.
     * @param p other.
     * @return true, if this is a multiple of b, else false.
     */
    public boolean isMultiple(ExpVectorPair p) {
       boolean w =  e1.multipleOf( p.getFirst() );
       if ( !w ) {
           return w;
       }
       w =  e2.multipleOf( p.getSecond() );
       if ( !w ) {
           return w;
       }
       return true;
    }

}

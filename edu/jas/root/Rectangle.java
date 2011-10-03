/*
 * $Id: Rectangle.java 3026 2010-03-07 18:53:23Z kredel $
 */

package edu.jas.root;


import edu.jas.arith.BigDecimal;
import edu.jas.arith.BigRational;
import edu.jas.arith.Rational;
import edu.jas.structure.Complex;
import edu.jas.structure.ComplexRing;
import edu.jas.structure.ElemFactory;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Rectangle. For example isolating rectangle for complex roots.
 * @param <C> coefficient type.
 * @author Heinz Kredel
 */
public class Rectangle<C extends RingElem<C> & Rational > {


    /**
     * rectangle corners.
     */
    public final Complex<C>[] corners;


    /**
     * Constructor.
     * @param c array of corners.
     */
    @SuppressWarnings("unchecked")
    public Rectangle(Complex<C>[] c) {
        if (c.length < 5) {
            corners = (Complex<C>[]) new Complex[5];
            for (int i = 0; i < 4; i++) {
                corners[i] = c[i];
            }
        } else {
            corners = c;
        }
        if (corners[4] == null) {
            corners[4] = corners[0];
        }
    }


    /**
     * Constructor.
     * @param nw corner.
     * @param sw corner.
     * @param se corner.
     * @param ne corner.
     */
    @SuppressWarnings("unchecked")
    public Rectangle(Complex<C> nw, Complex<C> sw, Complex<C> se, Complex<C> ne) {
        this( (Complex<C>[]) new Complex[] { nw, sw, se, ne } );
    }


    /**
     * String representation of Rectangle.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + corners[0] + ", " + corners[1] + ", " + corners[2] + ", " + corners[3] + "]";
        //return centerApprox() + " = [" + corners[0] + ", " + corners[1] + ", " + corners[2] + ", " + corners[3] + "]";
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Rectangle.
     */
    public String toScript() {
        // Python case
        return "(" + corners[0] + ", " + corners[1] + ", " + corners[2] + ", " + corners[3] + ")";
    }


    /**
     * Get north west corner.
     * @return north west corner of this rectangle.
     */
    public Complex<C> getNW() {
        return corners[0];
    }


    /**
     * Get south west corner.
     * @return south west corner of this rectangle.
     */
    public Complex<C> getSW() {
        return corners[1];
    }


    /**
     * Get south east corner.
     * @return south east corner of this rectangle.
     */
    public Complex<C> getSE() {
        return corners[2];
    }


    /**
     * Get north east corner.
     * @return north east corner of this rectangle.
     */
    public Complex<C> getNE() {
        return corners[3];
    }


    /**
     * Exchange NW corner.
     * @param c new NW corner.
     * @return rectangle with north west corner c of this rectangle.
     */
    public Rectangle<C> exchangeNW(Complex<C> c) {
        Complex<C> d = getSE();
        Complex<C> sw = new Complex<C>(c.factory(),c.getRe(),d.getIm());
        Complex<C> ne = new Complex<C>(c.factory(),d.getRe(),c.getIm());
        return new Rectangle<C>(c,sw,d,ne);
    }


    /**
     * Exchange SW corner.
     * @param c new SW corner.
     * @return rectangle with south west corner c of this rectangle.
     */
    public Rectangle<C> exchangeSW(Complex<C> c) {
        Complex<C> d = getNE();
        Complex<C> nw = new Complex<C>(c.factory(),c.getRe(),d.getIm());
        Complex<C> se = new Complex<C>(c.factory(),d.getRe(),c.getIm());
        return new Rectangle<C>(nw,c,se,d);
    }


    /**
     * Exchange SE corner.
     * @param c new SE corner.
     * @return rectangle with south east corner c of this rectangle.
     */
    public Rectangle<C> exchangeSE(Complex<C> c) {
        Complex<C> d = getNW();
        Complex<C> sw = new Complex<C>(c.factory(),d.getRe(),c.getIm());
        Complex<C> ne = new Complex<C>(c.factory(),c.getRe(),d.getIm());
        return new Rectangle<C>(d,sw,c,ne);
    }


    /**
     * Exchange NE corner.
     * @param c new NE corner.
     * @return rectangle with north east corner c of this rectangle.
     */
    public Rectangle<C> exchangeNE(Complex<C> c) {
        Complex<C> d = getSW();
        Complex<C> nw = new Complex<C>(c.factory(),d.getRe(),c.getIm());
        Complex<C> se = new Complex<C>(c.factory(),c.getRe(),d.getIm());
        return new Rectangle<C>(nw,d,se,c);
    }


    /**
     * Contains a point.
     * @param c point.
     * @return true if c is contained in this rectangle, else false.
     */
    public boolean contains(Complex<C> c) {
        Complex<C> ll = getSW();
        Complex<C> ur = getSW();
        return c.getRe().compareTo(ll.getRe()) < 0 ||
               c.getIm().compareTo(ll.getIm()) < 0 || 
               c.getRe().compareTo(ur.getRe()) > 0 || 
               c.getIm().compareTo(ur.getIm()) > 0;
    }


    /**
     * Random point of recatangle.
     * @return a random point contained in this rectangle.
     */
    public Complex<C> randomPoint() {
        Complex<C> sw = getSW();
        Complex<C> se = getSE();
        Complex<C> nw = getNW();
        Complex<C> r = sw.factory().random(13);
        C dr = se.getRe().subtract(sw.getRe()); // >= 0
        C di = nw.getIm().subtract(sw.getIm()); // >= 0
        C rr = r.getRe().abs();
        C ri = r.getIm().abs();
        C one = ((RingFactory<C>)dr.factory()).getONE();
        if ( !rr.isZERO() ) {
            if ( rr.compareTo(one) > 0 ) {
                rr = rr.inverse();
            }
        }
        if ( !ri.isZERO() ) {
            if ( ri.compareTo(one) > 0 ) {
                ri = ri.inverse();
            }
        }
        // 0 <= rr, ri <= 1
        rr = rr.multiply(dr);
        ri = ri.multiply(di);
        Complex<C> rp = new Complex<C>(sw.factory(),rr,ri);
        //System.out.println("rp = " + rp);
        rp = sw.sum(rp);
        return rp;
    }


    /**
     * Clone this.
     * @see java.lang.Object#clone()
     */
    @Override
    public Rectangle<C> clone() {
        return new Rectangle<C>(corners);
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object b) {
        if (!(b instanceof Rectangle)) {
            return false;
        }
        Rectangle<C> a = null;
        try {
            a = (Rectangle<C>) b;
        } catch (ClassCastException e) {
        }
        for (int i = 0; i < 4; i++) {
            if (!corners[i].equals(a.corners[i])) {
                return false;
            }
        }
        return true;
    }


    /**
     * Hash code for this Rectangle.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hc = 0;
        for (int i = 0; i < 3; i++) {
            hc += 37 * corners[i].hashCode();
        }
        return 37 * hc + corners[3].hashCode();
    }


    /**
     * Complex center.
     * @return r + i m of the center.
     */
    public Complex<C> getCenter() {
        C r = corners[2].getRe().subtract(corners[1].getRe());
        C m = corners[0].getIm().subtract(corners[1].getIm());
        ElemFactory<C> rf = r.factory();
        C two = rf.fromInteger(2);
        r = r.divide(two);
        m = m.divide(two);
        r = corners[1].getRe().sum(r);
        m = corners[1].getIm().sum(m);
        return new Complex<C>(corners[0].factory(),r,m);
    }


    /**
     * Complex of BigRational approximation of center.
     * @return r + i m as rational approximation of the center.
     */
    public Complex<BigRational> getRationalCenter() {
        Complex<C> cm = getCenter();
        BigRational rs = cm.getRe().getRational(); 
        BigRational ms = cm.getIm().getRational(); 
        ComplexRing<BigRational> cf = new ComplexRing<BigRational>(rs.factory());
        Complex<BigRational> c = new Complex<BigRational>(cf,rs,ms);
        return c;
    }


    /**
     * Complex of BigDecimal approximation of center.
     * @return r + i m as decimal approximation of the center.
     */
    public Complex<BigDecimal> getDecimalCenter() {
        Complex<BigRational> rc = getRationalCenter();
        BigDecimal rd = new BigDecimal(rc.getRe());
        BigDecimal md = new BigDecimal(rc.getIm());
        ComplexRing<BigDecimal> cf = new ComplexRing<BigDecimal>(rd.factory());
        Complex<BigDecimal> c = new Complex<BigDecimal>(cf,rd,md);
        return c;
    }


    /**
     * Approximation of center.
     * @return r + i m as string of decimal approximation of the center.
     */
    public String centerApprox() {
        Complex<BigDecimal> c = getDecimalCenter();
        StringBuffer s = new StringBuffer();
        s.append("[ ");
        s.append(c.getRe().toString());
        s.append(" i ");
        s.append(c.getIm().toString());
        s.append(" ]");
        return s.toString();
    }


    /**
     * Length.
     * @return |ne-sw|**2;
     */
    public C length() {
        Complex<C> m = corners[3].subtract(corners[1]);
        return m.norm().getRe();
    }


    /**
     * Rational Length.
     * @return rational(|ne-sw|**2);
     */
    public BigRational rationalLength() {
        BigRational r = new BigRational(length().toString());
        return r;
    }

}

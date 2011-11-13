/*
 * $Id: GenVectorModul.java 3211 2010-07-05 12:54:22Z kredel $
 */

package edu.jas.vector;


//import java.io.IOException;
import java.io.Reader; //import java.io.StringReader;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.math.BigInteger;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.ModulFactory;

import edu.jas.util.StringUtil;


/**
 * GenVectorModul implements a generic vector factory with RingElem entries.
 * Vectors of n columns over C.
 * @author Heinz Kredel
 */

public class GenVectorModul<C extends RingElem<C>> implements ModulFactory<GenVector<C>, C> {


    private static final Logger logger = Logger.getLogger(GenVectorModul.class);


    public final RingFactory<C> coFac;


    public final int cols;


    public final GenVector<C> ZERO;


    public final List<GenVector<C>> BASIS;


    private final static Random random = new Random();


    public final static float DEFAULT_DENSITY = 0.5f;


    private final float density = DEFAULT_DENSITY;


    /**
     * Constructor for GenVectorModul.
     */
    @SuppressWarnings("unchecked")
    public GenVectorModul(RingFactory<C> b, int s) {
        coFac = b;
        cols = s;
        ArrayList<C> z = new ArrayList<C>(cols);
        for (int i = 0; i < cols; i++) {
            z.add(coFac.getZERO());
        }
        ZERO = new GenVector<C>(this, z);
        BASIS = new ArrayList<GenVector<C>>(cols);
        List<C> cgens = coFac.generators();
        ArrayList<C> v;
        for (int i = 0; i < cols; i++) {
            for (C g : cgens) {
                v = (ArrayList<C>) z.clone();
                v.set(i, g);
                BASIS.add(new GenVector<C>(this, v));
            }
        }
    }


    /**
     * Get the String representation as RingElem.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(coFac.getClass().getSimpleName());
        s.append("[" + cols + "]");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this ElemFactory.
     * @see edu.jas.structure.ElemFactory#toScript()
     */
    //JAVA6only: @Override
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer("Vec(");
        String f = null;
        try {
            f = ((RingElem<C>) coFac).toScriptFactory(); // sic
        } catch (Exception e) {
            f = coFac.toScript();
        }
        s.append(f + "," + cols + " )");
        return s.toString();
    }


    /**
     * getZERO.
     * @return ZERO.
     */
    public GenVector<C> getZERO() {
        return ZERO;
    }


    /**
     * Get a list of the generating elements.
     * @return list of generators for the algebraic structure.
     * @see edu.jas.structure.ElemFactory#generators()
     */
    public List<GenVector<C>> generators() {
        return BASIS;
    }


    /**
     * Is this structure finite or infinite.
     * @return true if this structure is finite, else false.
     * @see edu.jas.structure.ElemFactory#isFinite()
     */
    public boolean isFinite() {
        return coFac.isFinite();
    }


    /**
     * Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (!(other instanceof GenVectorModul)) {
            return false;
        }
        GenVectorModul omod = (GenVectorModul) other;
        if (cols != omod.cols) {
            return false;
        }
        if (!coFac.equals(omod.coFac)) {
            return false;
        }
        return true;
    }


    /**
     * Hash code for this vector module.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = cols;
        h = 37 * h + coFac.hashCode();
        return h;
    }


    /**
     * Get the vector for a.
     * @param a long
     * @return vector corresponding to a.
     */
    public GenVector<C> fromInteger(long a) {
        C c = coFac.fromInteger(a);
        return BASIS.get(0).scalarMultiply(c);
    }


    /**
     * Get the vector for a.
     * @param a long
     * @return vector corresponding to a.
     */
    public GenVector<C> fromInteger(BigInteger a) {
        C c = coFac.fromInteger(a);
        return BASIS.get(0).scalarMultiply(c);
    }


    /**
     * From List of coefficients.
     * @param v list of coefficients.
     */
    public GenVector<C> fromList(List<C> v) {
        if (v == null) {
            return ZERO;
        }
        if (v.size() > cols) {
            throw new RuntimeException("size v > cols " + cols + " < " + v);
        }
        List<C> r = new ArrayList<C>(cols);
        r.addAll(v);
        // pad with zeros if required:
        for (int i = r.size(); i < cols; i++) {
            r.add(coFac.getZERO());
        }
        return new GenVector<C>(this, r);
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     */
    public GenVector<C> random(int k) {
        return random(k, density, random);
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param q density of nozero coefficients.
     */
    public GenVector<C> random(int k, float q) {
        return random(k, q, random);
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param random is a source for random bits.
     * @return a random element.
     */
    public GenVector<C> random(int k, Random random) {
        return random(k, density, random);
    }


    /**
     * Random vector.
     * @param k size of random coefficients.
     * @param q density of nozero coefficients.
     * @param random is a source for random bits.
     * @return a random element.
     */
    public GenVector<C> random(int k, float q, Random random) {
        List<C> r = new ArrayList<C>(cols);
        for (int i = 0; i < cols; i++) {
            if (random.nextFloat() < q) {
                r.add(coFac.random(k));
            } else {
                r.add(coFac.getZERO());
            }
        }
        return new GenVector<C>(this, r);
    }


    /**
     * copy vector.
     */
    public GenVector<C> copy(GenVector<C> c) {
        if (c == null) {
            return c;
        } else {
            return c.clone();
        }
        //return new GenVector<C>( this, c.val );//clone val
    }


    /**
     * parse a vector from a String. Syntax: [ c, ..., c ]
     */
    public GenVector<C> parse(String s) {
        int i = s.indexOf("[");
        if (i >= 0) {
            s = s.substring(i + 1);
        }
        i = s.indexOf("]");
        if (i >= 0) {
            s = s.substring(0, i);
        }
        List<C> vec = new ArrayList<C>(cols);
        String e;
        C c;
        do {
            i = s.indexOf(",");
            if (i >= 0) {
                e = s.substring(0, i);
                s = s.substring(i + 1);
                c = coFac.parse(e);
                vec.add(c);
            }
        } while (i >= 0);
        if (s.trim().length() > 0) {
            c = coFac.parse(s);
            vec.add(c);
        }
        return new GenVector<C>(this, vec);
        //throw new RuntimeException("parse not jet implemented");
        //return ZERO;
    }


    /**
     * parse a vector from a Reader.
     */
    public GenVector<C> parse(Reader r) {
        String s = StringUtil.nextPairedString(r, '[', ']');
        return parse(s);
        //throw new RuntimeException("parse not jet implemented");
        //return ZERO;
    }

}

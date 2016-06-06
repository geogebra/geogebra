package org.geogebra.common.kernel.discrete.tsp.impl;

/**
 * centralised place for possible micro-optimisations.
 */
public final class Maths {

    /**
     * fast inverse square root.
     * originally from quake 3:
     * http://en.wikipedia.org/wiki/Fast_inverse_square_root
     *
     * works by making a clever guess as to the starting point
     * for newton's method. 1 pass is a good approximation.
     */
    public static double invSqrt(double x) {
        final double xhalf = 0.5d*x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6eb50c7b537a9L - (i >> 1);
        x = Double.longBitsToDouble(i);
        x *= (1.5d - xhalf*x*x); // pass 1
        x *= (1.5d - xhalf*x*x); // pass 2
        x *= (1.5d - xhalf*x*x); // pass 3               
        x *= (1.5d - xhalf*x*x); // pass 4
        return x;
    }
    
    public static final double sqrt(final double d) {
        //return Math.sqrt(d);
        //return sqrtnat(d); // no diff (jni overhead.)
	return d*invSqrt(d); // ~10% faster than Math.sqrt.
    }

}

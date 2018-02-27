package org.geogebra.ggbjdk.java.awt.geom.utils;

public class GWTMath {
	
	// from https://groups.google.com/forum/#!topic/google-web-toolkit-contributors/I50Ry-x8ur0
	public static double IEEEremainder(double f1, double f2) {
		double r = Math.abs(f1 % f2);
		if (Double.isNaN(r) || r == f2 || r <= Math.abs(f2) / 2.0) {
			return r;
		}
		return Math.signum(f1) * (r - f2);
	}

}

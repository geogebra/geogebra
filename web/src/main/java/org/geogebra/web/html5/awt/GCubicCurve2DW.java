package org.geogebra.web.html5.awt;

public class GCubicCurve2DW implements org.geogebra.common.awt.GCubicCurve2D {

	org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D.Double impl;

	public GCubicCurve2DW() {
		impl = new org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D.Double();
	}

	public int solveCubic(double[] eqn, double[] dest) {
		return org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D.solveCubic(eqn,
		        dest);
	}

}

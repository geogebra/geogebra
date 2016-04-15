package org.geogebra.web.html5.awt;

import org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D;

public class GCubicCurve2DW implements org.geogebra.common.awt.GCubicCurve2D {

	org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D.Double impl;

	public GCubicCurve2DW() {
		impl = new CubicCurve2D.Double();
	}

	public int solveCubic(double[] eqn, double[] dest) {
		return CubicCurve2D.solveCubic(eqn,
		        dest);
	}

}

package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GCubicCurve2D;
import org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D;

public class GCubicCurve2DW implements GCubicCurve2D {

	CubicCurve2D.Double impl;

	public GCubicCurve2DW() {
		impl = new CubicCurve2D.Double();
	}

	public int solveCubic(double[] eqn, double[] dest) {
		return CubicCurve2D.solveCubic(eqn,
		        dest);
	}

}

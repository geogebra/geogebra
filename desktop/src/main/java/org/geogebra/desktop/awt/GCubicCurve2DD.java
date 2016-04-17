package org.geogebra.desktop.awt;

import java.awt.geom.CubicCurve2D;

public class GCubicCurve2DD implements org.geogebra.common.awt.GCubicCurve2D {

	CubicCurve2D impl;

	public GCubicCurve2DD() {
		impl = new CubicCurve2D.Double();
	}

	public int solveCubic(double[] eqn, double[] dest) {
		return CubicCurve2D.solveCubic(eqn, dest);
	}
}

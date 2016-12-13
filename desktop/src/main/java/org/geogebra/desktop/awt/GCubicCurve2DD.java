package org.geogebra.desktop.awt;

import java.awt.geom.CubicCurve2D;

import org.geogebra.common.awt.GCubicCurve2D;

public class GCubicCurve2DD implements GCubicCurve2D {

	public int solveCubic(double[] eqn, double[] dest) {
		return CubicCurve2D.solveCubic(eqn, dest);
	}
}

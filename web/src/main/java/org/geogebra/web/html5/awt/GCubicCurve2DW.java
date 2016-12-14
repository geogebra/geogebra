package org.geogebra.web.html5.awt;

import org.geogebra.common.awt.GCubicCurve2D;
import org.geogebra.ggbjdk.java.awt.geom.CubicCurve2D;

public class GCubicCurve2DW implements GCubicCurve2D {


	public GCubicCurve2DW() {
	}

	public int solveCubic(double[] eqn, double[] dest) {
		return CubicCurve2D.solveCubic(eqn,
		        dest);
	}

}

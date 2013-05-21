package geogebra.html5.awt;

public class GCubicCurve2DW extends geogebra.common.awt.GCubicCurve2D {
	
	geogebra.html5.openjdk.awt.geom.CubicCurve2D.Double impl;

	public GCubicCurve2DW(){
		impl = new geogebra.html5.openjdk.awt.geom.CubicCurve2D.Double();
	}
	
	@Override
	public int solveCubic(double[] eqn, double[] dest) {
		return geogebra.html5.openjdk.awt.geom.CubicCurve2D.solveCubic(eqn, dest);
	}

}

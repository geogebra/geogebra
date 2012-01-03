package geogebra.awt;

public class CubicCurve2D extends geogebra.common.awt.CubicCurve2D{
	
	java.awt.geom.CubicCurve2D impl;

	public CubicCurve2D(){
		impl = new java.awt.geom.CubicCurve2D.Double();
	}
	
	@Override
	public int solveCubic(double[] eqn, double[] dest) {
		return java.awt.geom.CubicCurve2D.solveCubic(eqn, dest);
	}
}

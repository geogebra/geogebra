package geogebra.web.awt;

public class CubicCurve2D extends geogebra.common.awt.CubicCurve2D {
	
	geogebra.web.kernel.gawt.CubicCurve2D.Double impl;

	public CubicCurve2D(){
		impl = new geogebra.web.kernel.gawt.CubicCurve2D.Double();
	}
	
	@Override
	public int solveCubic(double[] eqn, double[] dest) {
		return geogebra.web.kernel.gawt.CubicCurve2D.solveCubic(eqn, dest);
	}

}

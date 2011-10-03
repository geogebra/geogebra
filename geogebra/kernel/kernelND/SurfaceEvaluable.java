package geogebra.kernel.kernelND;

import geogebra.kernel.Matrix.Coords;

public interface SurfaceEvaluable {

	/**
	 * @param v n-dimensional vector
	 * @return point for parameters v
	 */
	public Coords evaluatePoint(double u, double v);
	
	public double getMinParameter(int i);
	public double getMaxParameter(int i);
}

package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.Matrix.Coords3;
import geogebra.common.kernel.Matrix.CoordsFloat3;

/**
 * Surface with parametric equation z=f(x1,x2,...,xn)
 * @author Mathieu
 */
public interface SurfaceEvaluable {
	
	/**
	 * level of detail (speed/quality)
	 */
	public enum LevelOfDetail {
		/** level of detail : speed */
		SPEED, 
		/** level of detail : quality */
		QUALITY
	}

	/**
	 * @param u first parameter
	 * @param v second parameter
	 * @return point for parameters u, v
	 */
	public Coords evaluatePoint(double u, double v);
	
	/**
	 * @param u first parameter
	 * @param v second parameter
	 * @param point point set for parameters u, v
	 */
	public void evaluatePoint(double u, double v, Coords3 point);
	
	/**
	 * 
	 * @param u
	 *            first parameter
	 * @param v
	 *            second parameter
	 * @param normal
	 *            normal. WARNING: the normal may not have norm=1
	 * @return true if the normal is defined
	 */
	public boolean evaluateNormal(double u, double v, Coords3 normal);

	/**
	 * @param i index of parameter
	 * @return minimal value for i-th parameter
	 */
	public double getMinParameter(int i);
	/**
	 * @param i index of parameter
	 * @return maximal value for i-th parameter
	 */
	public double getMaxParameter(int i);
	
	/**
	 * set derivatives (if not already done)
	 */
	public void setDerivatives();
	
	/**
	 * @return the level of detail (for drawing)
	 */
	public LevelOfDetail getLevelOfDetail();
	
	/**
	 * set the level of detail (for drawing)
	 * @param lod level of detail
	 */
	public void setLevelOfDetail(LevelOfDetail lod);
}

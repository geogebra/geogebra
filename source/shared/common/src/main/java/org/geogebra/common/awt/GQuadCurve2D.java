package org.geogebra.common.awt;

/**
 * Quadratic curve.
 */
public interface GQuadCurve2D extends GShape {

	void setCurve(double[] parpoints, int i);

	/**
	 * Set curve from 3 points
	 * @param x1 start point's x-coordinate
	 * @param y1 start point's y-coordinate
	 * @param controlX control point's x-coordinate
	 * @param controlY control point's y-coordinate
	 * @param x2 end point's x-coordinate
	 * @param y2 end point's y-coordinate
	 */
	void setCurve(double x1, double y1, double controlX,
			double controlY, double x2, double y2);

}

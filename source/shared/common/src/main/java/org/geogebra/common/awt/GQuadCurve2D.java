package org.geogebra.common.awt;

/**
 * Quadratic curve.
 */
public interface GQuadCurve2D extends GShape {

	/**
	 * Sets the location of the end points and control points of this
	 * <code>GQuadCurve2D</code> to the <code>double</code> coordinates at
	 * the specified offset in the specified array.
	 * @param coords the array containing coordinate values
	 * @param offset the index into the array from which to start
	 *          getting the coordinate values and assigning them to this
	 *          <code>GQuadCurve2D</code>
	 */
	void setCurve(double[] coords, int offset);

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

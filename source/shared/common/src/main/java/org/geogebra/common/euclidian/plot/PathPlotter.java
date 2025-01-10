package org.geogebra.common.euclidian.plot;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.matrix.CoordSys;

/**
 * interface where the curve will plot
 * 
 * @author mathieu
 *
 */
public interface PathPlotter {

	/**
	 * Calls gp.lineTo(x, y) resp. gp.moveTo(x, y) only if the current point is
	 * not already at this position.
	 * 
	 * @param pos
	 *            point coordinates
	 * @param lineTo
	 *            says if we want line / move
	 */
	public void drawTo(double[] pos, SegmentType lineTo);

	/**
	 * Calls gp.lineTo(x, y) only if the current point is not already at this
	 * position.
	 * 
	 * @param pos
	 *            point coordinates
	 */
	public void lineTo(double[] pos);

	/**
	 * Calls gp.moveTo(x, y) only if the current point is not already at this
	 * position.
	 * 
	 * @param pos
	 *            point coordinates
	 */
	public void moveTo(double[] pos);

	/**
	 * Corner-style lineto to the first point
	 */
	public void corner();

	/**
	 * Like lineto, but avoid corners
	 * 
	 * @param pos
	 *            endpoint of added segment
	 */
	public void corner(double[] pos);

	/**
	 * draw first point
	 * 
	 * @param pos
	 *            point position
	 * @param moveToAllowed
	 *            type of move allowed
	 */
	public void firstPoint(double[] pos, Gap moveToAllowed);

	/**
	 * 
	 * @return 2D/3D double array
	 */
	public double[] newDoubleArray();

	/**
	 * copy coords from MyPoint to double[]
	 * 
	 * @param point
	 *            point
	 * @param ret
	 *            double values
	 * @param transformSys
	 *            coordinate system of 2D points
	 * @return true if coords are on the view
	 */
	public boolean copyCoords(MyPoint point, double[] ret,
			CoordSys transformSys);

	/**
	 * end the plotting
	 */
	public void endPlot();

	/**
	 * @param transformSys
	 *            coordinate system of 2D points
	 * @return whether all of the points in the plane will be visible with this
	 *         plotter
	 */
	public boolean supports(CoordSys transformSys);

}

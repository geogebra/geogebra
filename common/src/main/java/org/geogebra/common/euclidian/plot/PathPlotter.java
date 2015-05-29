package org.geogebra.common.euclidian.plot;

import org.geogebra.common.euclidian.plot.CurvePlotter.Gap;
import org.geogebra.common.kernel.MyPoint;

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
	public void drawTo(double[] pos, boolean lineTo);

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

	public void corner();

	public void corner(double[] pos);

	/**
	 * draw first point
	 * 
	 * @param pos
	 *            point position
	 * @param moveToAllowed
	 *            type of move allowed
	 */
	public void firstPoint(double pos[], Gap moveToAllowed);

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
	 * @return true if coords are on the view
	 */
	public boolean copyCoords(MyPoint point, double[] ret);

	/**
	 * end the plotting
	 */
	public void endPlot();

}

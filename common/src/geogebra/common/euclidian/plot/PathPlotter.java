package geogebra.common.euclidian.plot;

import geogebra.common.euclidian.plot.CurvePlotter.Gap;


/**
 * interface where the curve will plot
 * @author mathieu
 *
 */
public interface PathPlotter {

	
	/**
	 * Calls gp.lineTo(x, y) resp. gp.moveTo(x, y) only if the current point is
	 * not already at this position.
	 * @param x x coord
	 * @param y y coord
	 * @param lineTo says if we want line / move
	 */
	public void drawTo(double[] pos, boolean lineTo);
	
	public void lineTo(double[] pos);
	
	public void moveTo(double[] pos);
	
	
	public void corner();
	
	public void corner(double[] pos);
	

	/**
	 * draw first point
	 * @param pos point position
	 * @param moveToAllowed type of move allowed
	 */
	public void firstPoint(double pos[], Gap moveToAllowed);
	


}

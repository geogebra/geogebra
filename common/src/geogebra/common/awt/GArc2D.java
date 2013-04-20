package geogebra.common.awt;

/**
 * Wrapper for java.awt.geom.Arc2D compatible classes
 */
public abstract class GArc2D implements GRectangularShape{
	/** open type (just the arc)*/
	public final static int OPEN = 0;
	/** chord type (include line connecting ends)*/
	public final static int CHORD = 1;
	/** pie type (include lines connecting with center + the area)*/
	public final static int PIE = 2;

	/**
	 * 
	 * @param x x-coord of top left corner
	 * @param y y-coord of top left corner
	 * @param width width
	 * @param height height
	 * @param angleStart start angle
	 * @param angleEnd end angle
	 * @param type type (OPEN or PIE)
	 */
	public abstract void setArc(double x, double y, double width, double height, double angleStart,
			double angleEnd, int type);
	/**
	 * @return start point
	 */
	public abstract GPoint2D getStartPoint();
	/**
	 * @return end point
	 */
	public abstract GPoint2D getEndPoint();
	/**
	 * 
	 * @param centerX x-coord of center
	 * @param centerY y-coord of center
	 * @param radius circle radius
	 * @param angleStart start angle
	 * @param angleEnd end angle
	 * @param type type (OPEN or PIE)
	 */
	public abstract void setArcByCenter(double centerX, double centerY, double radius, double angleStart,
			double angleEnd, int type);

}

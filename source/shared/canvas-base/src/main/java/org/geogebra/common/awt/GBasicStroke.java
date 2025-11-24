package org.geogebra.common.awt;

/**
 * Stroke style.
 */
public interface GBasicStroke {
	public static final int CAP_BUTT = 0; // Java & GWT
	public static final int CAP_ROUND = 1; // Java & GWT
	public static final int CAP_SQUARE = 2; // Java & GWT
	public static final int JOIN_MITER = 0; // Java
	public static final int JOIN_ROUND = 1; // Java
	public static final int JOIN_BEVEL = 2; // Java

	/**
	 * Contour of given shape as a shape.
	 * @param shape source shape
	 * @param capacity initial number of points
	 * @return stroke shape
	 */
	GShape createStrokedShape(GShape shape, int capacity);

	/**
	 * @return end cap type
	 */
	int getEndCap();

	/**
	 * @return mitre limit
	 */
	double getMiterLimit();

	/**
	 * @return line join type
	 */
	int getLineJoin();

	/**
	 * @return line width in pixels
	 */
	double getLineWidth();

	/**
	 * @return dashed line pattern
	 */
	double[] getDashArray();

}

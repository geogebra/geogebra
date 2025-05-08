package org.geogebra.common.awt;

import org.geogebra.common.annotation.MissingDoc;

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

	@MissingDoc
	int getEndCap();

	@MissingDoc
	double getMiterLimit();

	@MissingDoc
	int getLineJoin();

	@MissingDoc
	double getLineWidth();

	@MissingDoc
	double[] getDashArray();

}

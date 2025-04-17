package org.geogebra.common.awt;

public interface GEllipse2DDouble extends GRectangularShape {

	/**
	 * @param xUL left
	 * @param yUL top 
	 * @param width width
	 * @param height height
	 */
	void setFrame(double xUL, double yUL, double width, double height);

	/**
	 * @param centerX x-coordinate of the center
	 * @param centerY y-coordinate of the center
	 * @param cornerX x-coordinate of a bounding box corner
	 * @param cornerY y-coordinate of the same corner
	 */
	void setFrameFromCenter(double centerX, double centerY,
			double cornerX, double cornerY);

}

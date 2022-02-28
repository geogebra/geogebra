package org.geogebra.common.util.shape;

/**
 * Size object.
 */
public class Size {

	private double width;
	private double height;

	/**
	 * Create a size object.
	 * @param width width
	 * @param height height
	 */
	public Size(double width, double height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Return the width
	 * @return width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Return the height
	 * @return height
	 */
	public double getHeight() {
		return height;
	}
}

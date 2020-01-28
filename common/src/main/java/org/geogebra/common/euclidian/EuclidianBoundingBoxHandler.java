package org.geogebra.common.euclidian;

/**
 * handlers type for bounding box
 * 
 * @author csilla
 *
 */
public enum EuclidianBoundingBoxHandler {

	/**
	 * top right corner handler of bounding box
	 */
	TOP_RIGHT(1, -1),
	/**
	 * bottom right corner handler of bounding box
	 */
	BOTTOM_RIGHT(1, 1),
	/**
	 * bottom left corner handler of bounding box
	 */
	BOTTOM_LEFT(-1, 1),
	/**
	 * top left corner handler of bounding box
	 */
	TOP_LEFT(-1, -1),
	/**
	 * left side handler of bounding box
	 */
	LEFT(-1, 0),
	/**
	 * right side handler of bounding box
	 */
	RIGHT(1, 0),
	/**
	 * top side handler of bounding box
	 */
	TOP(0, -1),
	/**
	 * bottom side handler of bounding box
	 */
	BOTTOM(0, 1),
	/**
	 * rotation handler of bounding box
	 */
	ROTATION(0, 0),
	/**
	 * undefined handler
	 */
	UNDEFINED(0, 0);

	private int dx;
	private int dy;

	private EuclidianBoundingBoxHandler(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public boolean isDiagonal() {
		return dx != 0 && dy != 0;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

}

package org.geogebra.common.euclidian;

/**
 * handlers type for bounding box
 * 
 * @author csilla
 *
 */
public enum EuclidianBoundingBoxHandler implements ShapeManipulationHandler {

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
	 * mind map: add node to the top
	 */
	ADD_TOP(0, -1),
	/**
	 * mind map: add node to the right
	 */
	ADD_RIGHT(1, 0),
	/**
	 * mind map: add node to the bottom
	 */
	ADD_BOTTOM(0, 1),
	/**
	 * mind map: add node to the left
	 */
	ADD_LEFT(-1, 0),
	/**
	 * undefined handler
	 */
	UNDEFINED(0, 0);

	private final int dx;
	private final int dy;

	EuclidianBoundingBoxHandler(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public boolean isDiagonal() {
		return dx != 0 && dy != 0;
	}

	@Override
	public boolean isAddHandler() {
		return this == ADD_TOP
				|| this == ADD_RIGHT
				|| this == ADD_BOTTOM
				|| this == ADD_LEFT;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

}

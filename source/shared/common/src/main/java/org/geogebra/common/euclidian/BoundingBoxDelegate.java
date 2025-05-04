package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

/**
 * Bounding box delegate.
 */
public interface BoundingBoxDelegate {
	/**
	 * Create all handlers.
	 */
	void createHandlers();

	/**
	 * @return single corner handler
	 */
	GShape createCornerHandler();

	/**
	 * @return single side handler
	 */
	GShape createSideHandler();

	/**
	 * Draw in graphics.
	 * @param g2 graphics
	 */
	void draw(GGraphics2D g2);

	/**
	 * Check if side of bounding box is hit.
	 * @param x x-coordinate in pixels
	 * @param y y-coordinate in pixels
	 * @param hitThreshold hit threshold
	 * @return whether side was hit.
	 */
	boolean hitSideOfBoundingBox(int x, int y, int hitThreshold);

	/**
	 * Update the shape of given handler.
	 * @param handlerIndex handler index
	 * @param x x-coordinate of handler's center
	 * @param y y-coordinate of handler's center
	 */
	void setHandlerFromCenter(int handlerIndex, double x, double y);
}

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

/**
 * Bounding box delegate.
 */
public interface BoundingBoxDelegate {
	void createHandlers();

	GShape createCornerHandler();

	GShape createSideHandler();

	void draw(GGraphics2D g2);

	boolean hitSideOfBoundingBox(int x, int y, int hitThreshold);

	void setHandlerFromCenter(int handlerIndex, double x, double y);
}

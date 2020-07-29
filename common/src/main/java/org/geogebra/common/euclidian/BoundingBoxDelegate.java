package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;

public interface BoundingBoxDelegate {
	void createHandlers();

	GShape createHandler();

	void draw(GGraphics2D g2);

	boolean hitSideOfBoundingBox(int x, int y, int hitThreshold);

	void setHandlerFromCenter(int handlerIndex, double x, double y);
}

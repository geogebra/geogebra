package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;

/**
 * Simple bounding box with no handlers.
 */
public class SingleBoundingBox extends BoundingBox<GEllipse2DDouble> {

	public SingleBoundingBox(GColor color) {
		setColor(color);
	}

	@Override
	protected void createHandlers() {
		// no handlers
	}

	@Override
	protected GEllipse2DDouble createCornerHandler() {
		return null;
	}

	@Override
	protected GEllipse2DDouble createSideHandler() {
		return null;
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawRectangle(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}
}

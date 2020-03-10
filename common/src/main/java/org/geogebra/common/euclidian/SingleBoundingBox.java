package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;

public class SingleBoundingBox extends BoundingBox<GEllipse2DDouble> {

	@Override
	protected void createHandlers() {
		// no handlers
	}

	@Override
	protected GEllipse2DDouble createHandler() {
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

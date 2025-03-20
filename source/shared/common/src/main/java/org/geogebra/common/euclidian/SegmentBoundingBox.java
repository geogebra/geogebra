package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;

public class SegmentBoundingBox extends BoundingBox<GEllipse2DDouble> {

	@Override
	public void draw(GGraphics2D g2) {
		drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	@Override
	protected void createHandlers() {
		initHandlers(2);
	}

	@Override
	protected GEllipse2DDouble createCornerHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	protected GEllipse2DDouble createSideHandler() {
		return null;
	}

	/**
	 * @param i
	 *            handler index
	 * @param x
	 *            screen x-coord
	 * @param y
	 *            screen y-coord
	 */
	public void setHandlerFromCenter(int i, double x, double y) {
		handlers.get(i).setFrameFromCenter(x, y, x + HANDLER_RADIUS, y + HANDLER_RADIUS);
	}

	@Override
	public EuclidianCursor getCursor(EuclidianBoundingBoxHandler nrHandler) {
		return EuclidianCursor.DRAG;
	}
}

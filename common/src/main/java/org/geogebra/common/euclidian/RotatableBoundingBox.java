package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * Bounding box for a single element that rotates together with the geo.
 */
public class RotatableBoundingBox implements BoundingBoxDelegate {

	private final MediaBoundingBox box;

	public RotatableBoundingBox(MediaBoundingBox box) {
		this.box = box;
	}

	@Override
	public void createHandlers() {
		box.initHandlers(9);
	}

	@Override
	public GEllipse2DDouble createHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	public void draw(GGraphics2D g2) {
		box.drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	@Override
	public void setHandlerFromCenter(int handlerIndex, double x, double y) {
		((GEllipse2DDouble) box.handlers.get(handlerIndex))
				.setFrameFromCenter(x, y, x + BoundingBox.HANDLER_RADIUS,
						y + BoundingBox.HANDLER_RADIUS);
	}

}

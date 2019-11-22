package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.factories.AwtFactory;

public class MultiBoundingBox extends BoundingBox<GEllipse2DDouble> {
	private static final int ROTATION_HANDLER_DISTANCE = 25;
	protected int nrHandlers = 8;

	public MultiBoundingBox(boolean hasRotationHandler) {
		nrHandlers = hasRotationHandler ? 9 : 8;
	}

	@Override
	protected void createHandlers() {
		initHandlers(nrHandlers);
		createBoundingBoxHandlers();
	}

	private void createBoundingBoxHandlers() {

		// corner handlers
		setHandlerFromCenter(0, rectangle.getX(), rectangle.getY());
		setHandlerFromCenter(1, rectangle.getX(), rectangle.getMaxY());
		setHandlerFromCenter(2, rectangle.getMaxX(), rectangle.getMaxY());
		setHandlerFromCenter(3, rectangle.getMaxX(), rectangle.getY());

		// side handlers
		double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
		double centerY = (rectangle.getMinY() + rectangle.getMaxY()) / 2;
		// top
		setHandlerFromCenter(4, centerX, rectangle.getMinY());
		// left
		setHandlerFromCenter(5, rectangle.getMinX(), centerY);
		// bottom
		setHandlerFromCenter(6, centerX, rectangle.getMaxY());
		// right
		setHandlerFromCenter(7, rectangle.getMaxX(), centerY);
		if (nrHandlers == 9) {
			// rotation handler
			setHandlerFromCenter(8, centerX, rectangle.getMinY() - ROTATION_HANDLER_DISTANCE);
		}

	}

	@Override
	public void draw(GGraphics2D g2) {
		// draw bounding box
		drawRectangle(g2);
		if (nrHandlers == 9) {
			GLine2D line = AwtFactory.getPrototype().newLine2D();
			double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
			line.setLine(centerX, rectangle.getMinY(),
					centerX,
					rectangle.getMinY() - ROTATION_HANDLER_DISTANCE);
			g2.setColor(getColor());
			g2.draw(line);
		}
		drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		if (rectangle == null) {
			return false;
		}
		return hitRectangle(x, y, hitThreshold) || hitRotationHandlerLine(x, y, hitThreshold);
	}

	private boolean hitRotationHandlerLine(int x, int y, int hitThreshold) {
		double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
		return nrHandlers == 9 && onSegment(centerX,
				rectangle.getMinY(), x, y, centerX,
				rectangle.getMinY() - ROTATION_HANDLER_DISTANCE, hitThreshold);
	}

	@Override
	protected GEllipse2DDouble createHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	private void setHandlerFromCenter(int i, double x, double y) {
		handlers.get(i).setFrameFromCenter(x, y, x + HANDLER_RADIUS,
				y + HANDLER_RADIUS);
	}

}

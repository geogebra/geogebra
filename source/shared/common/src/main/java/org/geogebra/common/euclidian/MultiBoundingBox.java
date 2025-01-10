package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GLine2D;
import org.geogebra.common.factories.AwtFactory;

public class MultiBoundingBox extends BoundingBox<GEllipse2DDouble> {

	private final boolean hasRotationHandler;

	public MultiBoundingBox(boolean hasRotationHandler) {
		this.hasRotationHandler = hasRotationHandler;
	}

	@Override
	protected void createHandlers() {
		initHandlers(hasRotationHandler ? 9 : 8);
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
		if (hasRotationHandler) {
			// rotation handler
			setHandlerFromCenter(8, centerX, rectangle.getMinY() - ROTATION_HANDLER_DISTANCE);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		// draw bounding box
		drawRectangle(g2);
		if (hasRotationHandler) {
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
		return hitRectangle(x, y, 2 * hitThreshold)
				|| hitRotationHandlerLine(x, y, 2 * hitThreshold);
	}

	private boolean hitRotationHandlerLine(int x, int y, int hitThreshold) {
		double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
		return hasRotationHandler && onSegment(centerX,
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

	/**
	 * check if intersection point is on segment Threshold includes line
	 * thickness.
	 */
	private static boolean onSegment(double segStartX, double segStartY, int hitX, int hitY,
			double segEndX, double segEndY, int hitThreshold) {
		return onSegmentCoord(segStartX, hitX, segEndX, hitThreshold)
				&& onSegmentCoord(segStartY, hitY, segEndY, hitThreshold);
	}

	private static boolean onSegmentCoord(double segStartX, int hitX, double segEndX,
			int hitThreshold) {
		return hitX <= Math.max(segStartX, segEndX) + hitThreshold
				&& hitX >= Math.min(segStartX, segEndX) - hitThreshold;
	}
}

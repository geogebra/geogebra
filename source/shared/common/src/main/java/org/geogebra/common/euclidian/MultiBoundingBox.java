package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.factories.AwtFactory;

public class MultiBoundingBox extends BoundingBox<GShape> {

	private final boolean hasRotationHandler;

	/**
	 * Constructor
	 * @param hasRotationHandler - true, if box has rotation handler
	 * @param rotationImage - icon of rotation handler
	 */
	public MultiBoundingBox(boolean hasRotationHandler, MyImage rotationImage) {
		this.hasRotationHandler = hasRotationHandler;
		setRotationHandlerImage(rotationImage);
	}

	@Override
	protected void createHandlers() {
		initHandlers(4, hasRotationHandler ? 5 : 4);
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
			setHandlerFromCenter(8, centerX, rectangle.getMaxY() + ROTATION_HANDLER_DISTANCE);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		// draw bounding box
		drawRectangle(g2);
		drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		if (rectangle == null) {
			return false;
		}
		return hitRectangle(x, y, 2 * hitThreshold);
	}

	@Override
	protected GEllipse2DDouble createCornerHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	protected GRectangle2D createSideHandler() {
		return AwtFactory.getPrototype().newRectangle();
	}

	private void setHandlerFromCenter(int handlerIndex, double x, double y) {
		GShape handler = handlers.get(handlerIndex);
		if (isRotationHandler(handlerIndex)) {
			((GEllipse2DDouble) handlers.get(handlerIndex)).setFrameFromCenter(x, y,
					x + ROTATION_HANDLER_RADIUS, y + ROTATION_HANDLER_RADIUS);
		} else if (isCornerHandler(handler)) {
			((GEllipse2DDouble) handlers.get(handlerIndex)).setFrameFromCenter(x, y,
					x + HANDLER_RADIUS, y + HANDLER_RADIUS);
		} else if (isSideHandler(handler)) {
			int width = handlerIndex % 2 == 0 ? SIDE_HANDLER_WIDTH : SIDE_HANDLER_HEIGHT;
			int height = handlerIndex % 2 == 0 ? SIDE_HANDLER_HEIGHT : SIDE_HANDLER_WIDTH;
			((GRectangle2D) handlers.get(handlerIndex)).setFrame(x - width, y - height,
					width * 2, height * 2);
		}
	}
}

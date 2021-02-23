package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;

public class MindMapBoundingBox extends MediaBoundingBox {

	private static final GEllipse2DDouble CIRCLE = AwtFactory.getPrototype().newEllipse2DDouble();

	private static final int PLUS_DISTANCE = 22;
	private static final int PLUS_RADIUS = 12;
	private static final int PLUS_LENGTH = 6;

	private static final EuclidianBoundingBoxHandler[] ADD_HANDLERS = {
			EuclidianBoundingBoxHandler.ADD_TOP,
			EuclidianBoundingBoxHandler.ADD_RIGHT,
			EuclidianBoundingBoxHandler.ADD_BOTTOM,
			EuclidianBoundingBoxHandler.ADD_LEFT
	};

	private EuclidianBoundingBoxHandler focusedHandler;

	@Override
	protected void createHandlers() {
		initHandlers(8);
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawRectangle(g2);
		drawHandlers(g2);
		for (int i = 0; i < ADD_HANDLERS.length; i++) {
			drawPlus(
					g2,
					focusedHandler == ADD_HANDLERS[i],
					corners[4 + i].x + ADD_HANDLERS[i].getDx() * PLUS_DISTANCE,
					corners[4 + i].y + ADD_HANDLERS[i].getDy() * PLUS_DISTANCE
			);
		}
	}

	@Override
	public EuclidianBoundingBoxHandler getHitHandler(int x, int y, int hitThreshold) {
		for (int i = 0; i < ADD_HANDLERS.length; i++) {
			double plusX = corners[4 + i].x + ADD_HANDLERS[i].getDx() * PLUS_DISTANCE;
			double plusY = corners[4 + i].y + ADD_HANDLERS[i].getDy() * PLUS_DISTANCE;

			if (Math.hypot(x - plusX, y - plusY) < PLUS_RADIUS) {
				return focusedHandler = ADD_HANDLERS[i];
			}
		}

		return focusedHandler = super.getHitHandler(x, y, hitThreshold);
	}

	@Override
	public EuclidianCursor getCursor(EuclidianBoundingBoxHandler handler) {
		if (handler.isAddHandler()) {
			return EuclidianCursor.DRAG;
		}

		return super.getCursor(handler);
	}

	private void drawPlus(GGraphics2D g2, boolean focused, double x, double y) {
		if (focused) {
			g2.setColor(color);
		} else {
			g2.setColor(GColor.LIGHT_GRAY);
		}
		CIRCLE.setFrameFromCenter(x, y, x - PLUS_RADIUS, y - PLUS_RADIUS);
		g2.fill(CIRCLE);
		g2.setColor(GColor.WHITE);
		g2.translate(x, y);
		g2.drawLine(-PLUS_LENGTH, 0, PLUS_LENGTH, 0);
		g2.drawLine(0, -PLUS_LENGTH, 0, +PLUS_LENGTH);
		g2.translate(-x, -y);
	}
}

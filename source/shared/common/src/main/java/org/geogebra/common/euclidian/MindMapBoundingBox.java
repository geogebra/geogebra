package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoMindMapNode;

public class MindMapBoundingBox extends MediaBoundingBox {

	private static final GEllipse2DDouble CIRCLE = AwtFactory.getPrototype().newEllipse2DDouble();

	private static final int PLUS_DISTANCE = 22;
	private static final int PLUS_RADIUS = 12;
	private static final int PLUS_LENGTH = 6;

	private static final EuclidianBoundingBoxHandler[] ADD_HANDLERS = {
			EuclidianBoundingBoxHandler.ADD_TOP,
			EuclidianBoundingBoxHandler.ADD_LEFT,
			EuclidianBoundingBoxHandler.ADD_BOTTOM,
			EuclidianBoundingBoxHandler.ADD_RIGHT
	};

	private final EuclidianView view;
	private final GeoMindMapNode node;

	/**
	 * @param view view
	 * @param node mind-map node
	 * @param rotationImage rotation icon
	 */
	public MindMapBoundingBox(EuclidianView view, GeoMindMapNode node,
			MyImage rotationImage) {
		super(rotationImage);
		this.view = view;
		this.node = node;
	}

	@Override
	protected void createHandlers() {
		initHandlers(4, 4);
	}

	@Override
	public void draw(GGraphics2D g2) {
		drawRectangle(g2);
		drawHandlers(g2);
		for (int i = 0; i < ADD_HANDLERS.length; i++) {
			if (node.getAlignment() != null && i == node.getAlignment().ordinal()) {
				continue;
			}

			drawPlus(
					g2,
					view.getHitHandler() == ADD_HANDLERS[i],
					corners[4 + i].x + ADD_HANDLERS[i].getDx() * PLUS_DISTANCE,
					corners[4 + i].y + ADD_HANDLERS[i].getDy() * PLUS_DISTANCE
			);
		}
	}

	@Override
	public ShapeManipulationHandler getHitHandler(int x, int y, int hitThreshold) {
		for (int i = 0; i < ADD_HANDLERS.length; i++) {
			double plusX = corners[4 + i].x + ADD_HANDLERS[i].getDx() * PLUS_DISTANCE;
			double plusY = corners[4 + i].y + ADD_HANDLERS[i].getDy() * PLUS_DISTANCE;

			if (Math.hypot(x - plusX, y - plusY) < PLUS_RADIUS) {
				return ADD_HANDLERS[i];
			}
		}

		return super.getHitHandler(x, y, hitThreshold);
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
			g2.setColor(secondaryColor);
		} else {
			g2.setColor(GColor.MIND_MAP_PLUS_INACTIVE);
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

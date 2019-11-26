package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;

public class CropBox extends BoundingBox<GGeneralPath> {
	private static final int CROP_HANDLERS = 8;
	private GBasicStroke outlineStroke = AwtFactory.getPrototype().newBasicStroke(6.0f,
			GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND);
	private GBasicStroke handlerStroke = AwtFactory.getPrototype().newBasicStroke(4.0f,
			GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND);

	@Override
	protected void createHandlers() {
		initHandlers(CROP_HANDLERS);
		createhandlers();
	}

	private void createhandlers() {
		handlers.get(0).moveTo(rectangle.getX(), rectangle.getY() + 10);
		handlers.get(0).lineTo(rectangle.getX(), rectangle.getY());
		handlers.get(0).lineTo(rectangle.getX() + 10, rectangle.getY());
		handlers.get(1).moveTo(rectangle.getX(), rectangle.getMaxY() - 10);
		handlers.get(1).lineTo(rectangle.getX(), rectangle.getMaxY());
		handlers.get(1).lineTo(rectangle.getX() + 10, rectangle.getMaxY());
		handlers.get(2).moveTo(rectangle.getMaxX() - 10, rectangle.getMaxY());
		handlers.get(2).lineTo(rectangle.getMaxX(), rectangle.getMaxY());
		handlers.get(2).lineTo(rectangle.getMaxX(), rectangle.getMaxY() - 10);
		handlers.get(3).moveTo(rectangle.getMaxX(), rectangle.getY() + 10);
		handlers.get(3).lineTo(rectangle.getMaxX(), rectangle.getY());
		handlers.get(3).lineTo(rectangle.getMaxX() - 10, rectangle.getY());
		// side handlers
		double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
		double centerY = (rectangle.getMinY() + rectangle.getMaxY()) / 2;
		handlers.get(4).moveTo(centerX - 5, rectangle.getMinY());
		handlers.get(4).lineTo(centerX + 5, rectangle.getMinY());
		handlers.get(5).moveTo(rectangle.getMinX(), centerY - 5);
		handlers.get(5).lineTo(rectangle.getMinX(), centerY + 5);
		handlers.get(6).moveTo(centerX - 5, rectangle.getMaxY());
		handlers.get(6).lineTo(centerX + 5, rectangle.getMaxY());
		handlers.get(7).moveTo(rectangle.getMaxX(), centerY - 5);
		handlers.get(7).lineTo(rectangle.getMaxX(), centerY + 5);
	}

	@Override
	public void draw(GGraphics2D g2) {
		// draw bounding box
		drawRectangle(g2);

		if (handlers != null && !handlers.isEmpty()) {
			g2.setColor(GColor.WHITE);
			g2.setStroke(outlineStroke);
			for (int i = 0; i < CROP_HANDLERS; i++) {
				g2.draw(handlers.get(i));
			}
			g2.setStroke(handlerStroke);
			g2.setColor(GColor.BLACK);
			for (int i = 0; i < CROP_HANDLERS; i++) {
				g2.draw(handlers.get(i));
			}
		}
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return rectangle != null && hitRectangle(x, y, 2 * hitThreshold);
	}

	@Override
	public boolean isCropBox() {
		return true;
	}

	@Override
	protected GGeneralPath createHandler() {
		return AwtFactory.getPrototype().newGeneralPath();
	}

}

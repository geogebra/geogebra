package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.factories.AwtFactory;

public class CropBox implements BoundingBoxDelegate {
	private static final int CROP_HANDLERS = 8;
	private GBasicStroke outlineStroke = AwtFactory.getPrototype().newBasicStroke(6.0f,
			GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND);
	private GBasicStroke handlerStroke = AwtFactory.getPrototype().newBasicStroke(4.0f,
			GBasicStroke.CAP_SQUARE, GBasicStroke.JOIN_ROUND);
	private MediaBoundingBox box;

	public CropBox(MediaBoundingBox box) {
		this.box = box;
	}

	public void createHandlers() {
		box.initHandlers(CROP_HANDLERS);
		createhandlers();
	}

	private void createhandlers() {
		GRectangle2D rectangle = box.rectangle;
		getHandler(0).moveTo(rectangle.getX(), rectangle.getY() + 10);
		getHandler(0).lineTo(rectangle.getX(), rectangle.getY());
		getHandler(0).lineTo(rectangle.getX() + 10, rectangle.getY());
		getHandler(1).moveTo(rectangle.getX(), rectangle.getMaxY() - 10);
		getHandler(1).lineTo(rectangle.getX(), rectangle.getMaxY());
		getHandler(1).lineTo(rectangle.getX() + 10, rectangle.getMaxY());
		getHandler(2).moveTo(rectangle.getMaxX() - 10, rectangle.getMaxY());
		getHandler(2).lineTo(rectangle.getMaxX(), rectangle.getMaxY());
		getHandler(2).lineTo(rectangle.getMaxX(), rectangle.getMaxY() - 10);
		getHandler(3).moveTo(rectangle.getMaxX(), rectangle.getY() + 10);
		getHandler(3).lineTo(rectangle.getMaxX(), rectangle.getY());
		getHandler(3).lineTo(rectangle.getMaxX() - 10, rectangle.getY());
		// side handlers
		double centerX = (rectangle.getMinX() + rectangle.getMaxX()) / 2;
		double centerY = (rectangle.getMinY() + rectangle.getMaxY()) / 2;
		getHandler(4).moveTo(centerX - 5, rectangle.getMinY());
		getHandler(4).lineTo(centerX + 5, rectangle.getMinY());
		getHandler(5).moveTo(rectangle.getMinX(), centerY - 5);
		getHandler(5).lineTo(rectangle.getMinX(), centerY + 5);
		getHandler(6).moveTo(centerX - 5, rectangle.getMaxY());
		getHandler(6).lineTo(centerX + 5, rectangle.getMaxY());
		getHandler(7).moveTo(rectangle.getMaxX(), centerY - 5);
		getHandler(7).lineTo(rectangle.getMaxX(), centerY + 5);
	}

	private GGeneralPath getHandler(int i) {
		return (GGeneralPath) box.handlers.get(i);
	}

	@Override
	public void draw(GGraphics2D g2) {
		// draw bounding box
		box.drawRectangle(g2);

		if (box.handlers != null && !box.handlers.isEmpty()) {
			g2.setColor(GColor.WHITE);
			g2.setStroke(outlineStroke);
			for (int i = 0; i < CROP_HANDLERS; i++) {
				g2.draw(getHandler(i));
			}
			g2.setStroke(handlerStroke);
			g2.setColor(GColor.BLACK);
			for (int i = 0; i < CROP_HANDLERS; i++) {
				g2.draw(getHandler(i));
			}
		}
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return box.rectangle != null && box.hitRectangle(x, y, 2 * hitThreshold);
	}

	@Override
	public void setTransform(GAffineTransform directTransform) {

	}

	@Override
	public GGeneralPath createHandler() {
		return AwtFactory.getPrototype().newGeneralPath();
	}

}

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GGraphics2D;

public class MeasurementToolBoundingBox extends MediaBoundingBox {

	@Override
	protected void drawLineToRotateHandler(GGraphics2D g2) {
		double xPos = corners[0].getX() + (corners[3].getX() - corners[0].getX()) / 2;
		double yPos = corners[0].getY() + (corners[3].getY() - corners[0].getY()) / 2;
		g2.drawLine((int) xPos, (int) yPos,
				(int) corners[8].getX(), (int) corners[8].getY());
	}

	@Override
	protected void updateHandlers() {
		double width = geo.getWidth();
		double height = geo.getHeight();
		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, height);
		setHandlerTransformed(2, width, height);
		setHandlerTransformed(3, width, 0);
		setHandlerTransformed(8, width / 2, -BoundingBox.ROTATION_HANDLER_DISTANCE);
	}
}

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineText;

public class TextBoundingBox extends BoundingBox<GEllipse2DDouble> {

	private GeoInlineText text;

	private GPoint2D[] corners = new GPoint2D[9];
	private int rotationHandlerx;
	private int rotationHandlery;

	private GAffineTransform transform;

	@Override
	protected void createHandlers() {
		initHandlers(9);
	}

	@Override
	protected GEllipse2DDouble createHandler() {
		return AwtFactory.getPrototype().newEllipse2DDouble();
	}

	@Override
	public void updateFrom(GeoElement geo) {
		super.updateFrom(geo);
		text = (GeoInlineText) geo;
	}

	@Override
	public void draw(GGraphics2D g2) {
		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
				GBasicStroke.JOIN_MITER));
		g2.setColor(color);

		if (corners[0] != null) {
			for (int i = 0; i < 4; i++) {
				g2.drawLine((int) corners[i].getX(), (int) corners[i].getY(),
						(int) corners[(i + 1) % 4].getX(), (int) corners[(i + 1) % 4].getY());
			}
			g2.drawLine((int) corners[4].getX(), (int) corners[4].getY(),
					rotationHandlerx, rotationHandlery);
		}

		drawHandlers(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	private void setHandlerFromCenter(int i, double x, double y) {
		handlers.get(i).setFrameFromCenter(x, y, x + HANDLER_RADIUS,
				y + HANDLER_RADIUS);
	}

	private void setHandlerTransformed(int i, double x, double y) {
		corners[i] = transform.transform(new GPoint2D.Double(x, y), null);
		setHandlerFromCenter(i, corners[i].getX(), corners[i].getY());
	}

	private void updateHandlers() {
		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, 1);
		setHandlerTransformed(2, 1, 1);
		setHandlerTransformed(3, 1, 0);
		setHandlerTransformed(4, 0.5, 0);
		setHandlerTransformed(5, 0, 0.5);
		setHandlerTransformed(6, 0.5, 1);
		setHandlerTransformed(7, 1, 0.5);

		rotationHandlerx = (int) (corners[4].getX()
				+ Math.sin(text.getAngle()) * ROTATION_HANDLER_DISTANCE);
		rotationHandlery = (int) (corners[4].getY()
				- Math.cos(text.getAngle()) * ROTATION_HANDLER_DISTANCE);

		setHandlerFromCenter(8, rotationHandlerx, rotationHandlery);
	}

	public void setTransform(GAffineTransform transform) {
		this.transform = transform;
		updateHandlers();
	}
}

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactory;

/**
 * Bounding box for a single element that rotates together with the geo.
 */
public class RotatableBoundingBox implements BoundingBoxDelegate {

	private GPoint2D[] corners = new GPoint2D[9];
	private GAffineTransform transform;
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
		g2.setStroke(AwtFactory.getPrototype().newBasicStroke(2.0f, GBasicStroke.CAP_BUTT,
				GBasicStroke.JOIN_MITER));
		g2.setColor(box.color);

		if (corners[0] != null) {
			for (int i = 0; i < 4; i++) {
				g2.drawLine((int) corners[i].getX(), (int) corners[i].getY(),
						(int) corners[(i + 1) % 4].getX(), (int) corners[(i + 1) % 4].getY());
			}
			if (box.showHandlers()) {
				g2.drawLine((int) corners[4].getX(), (int) corners[4].getY(),
						(int) corners[8].getX(), (int) corners[8].getY());
			}
		}
		if (box.showHandlers()) {
			box.drawHandlers(g2);
		}
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	private void setHandlerFromCenter(int i, double x, double y) {
		((GEllipse2DDouble) box.handlers.get(i)).setFrameFromCenter(x, y, x + BoundingBox.HANDLER_RADIUS,
				y + BoundingBox.HANDLER_RADIUS);
	}

	private void setHandlerTransformed(int i, double x, double y) {
		corners[i] = transform.transform(new GPoint2D(x, y), null);
		setHandlerFromCenter(i, corners[i].getX(), corners[i].getY());
	}

	private void updateHandlers() {
		double width = box.geo.getWidth();
		double height = box.geo.getHeight();

		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, height);
		setHandlerTransformed(2, width, height);
		setHandlerTransformed(3, width, 0);
		setHandlerTransformed(4, width / 2, 0);
		setHandlerTransformed(5, 0, height / 2);
		setHandlerTransformed(6, width / 2, height);
		setHandlerTransformed(7, width, height / 2);
		setHandlerTransformed(8, width / 2, - BoundingBox.ROTATION_HANDLER_DISTANCE);
	}

	/**
	 * Set the transform and update the handlers
	 * @param transform GAffineTransform
	 */
	public void setTransform(GAffineTransform transform) {
		this.transform = transform;
		updateHandlers();
	}
}

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.RectangleTransformable;

/**
 * Bounding box for a single element that rotates together with the geo.
 */
public class RotatableBoundingBox extends BoundingBox<GEllipse2DDouble> {

	private RectangleTransformable geo;

	private GPoint2D[] corners = new GPoint2D[9];

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
		this.geo = (RectangleTransformable) geo;
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
			if (showHandlers()) {
				g2.drawLine((int) corners[4].getX(), (int) corners[4].getY(),
						(int) corners[8].getX(), (int) corners[8].getY());
			}
		}
		if (showHandlers()) {
			drawHandlers(g2);
		}
	}

	private boolean showHandlers() {
		return !((GeoElement) geo).hasGroup();
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return false;
	}

	@Override
	public EuclidianCursor getCursor(EuclidianBoundingBoxHandler handler) {
		if (handler == EuclidianBoundingBoxHandler.ROTATION) {
			return EuclidianCursor.ROTATION;
		}

		// evil hackery to get closest rotation handler
		int cursorIndex = (int) Math.round(4 * (Math.atan2(handler.getDx(), handler.getDy())
				- geo.getAngle()) / Math.PI) % 4;

		// I'd need a proper number theoretic remainder, but I have to make do with
		// Computer Science modulo (there is Math.floorMod in java8)
		switch ((4 + cursorIndex) % 4) {
			case 0:
				return EuclidianCursor.RESIZE_NS;
			case 1:
				return EuclidianCursor.RESIZE_NWSE;
			case 2:
				return EuclidianCursor.RESIZE_EW;
			case 3:
				return EuclidianCursor.RESIZE_NESW;
			default:
				return EuclidianCursor.DEFAULT;
		}
	}

	private void setHandlerFromCenter(int i, double x, double y) {
		handlers.get(i).setFrameFromCenter(x, y, x + HANDLER_RADIUS,
				y + HANDLER_RADIUS);
	}

	private void setHandlerTransformed(int i, double x, double y) {
		corners[i] = transform.transform(new GPoint2D(x, y), null);
		setHandlerFromCenter(i, corners[i].getX(), corners[i].getY());
	}

	private void updateHandlers() {
		double width = geo.getWidth();
		double height = geo.getHeight();

		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, height);
		setHandlerTransformed(2, width, height);
		setHandlerTransformed(3, width, 0);
		setHandlerTransformed(4, width / 2, 0);
		setHandlerTransformed(5, 0, height / 2);
		setHandlerTransformed(6, width / 2, height);
		setHandlerTransformed(7, width, height / 2);
		setHandlerTransformed(8, width / 2, -ROTATION_HANDLER_DISTANCE);
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

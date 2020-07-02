package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.RectangleTransformable;

public class MediaBoundingBox extends BoundingBox<GShape> {

	protected RectangleTransformable geo;
	protected GAffineTransform transform;
	protected GPoint2D[] corners = new GPoint2D[9];
	BoundingBoxDelegate delegate;

	public MediaBoundingBox() {
		delegate = new RotatableBoundingBox(this);
	}

	@Override
	protected void createHandlers() {
		delegate.createHandlers();
	}

	@Override
	protected GShape createHandler() {
		return delegate.createHandler();
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
			if (showHandlers() && !isCropBox()) {
				g2.drawLine((int) corners[4].getX(), (int) corners[4].getY(),
						(int) corners[8].getX(), (int) corners[8].getY());
			}
		}
		if (showHandlers()) {
			delegate.draw(g2);
		}
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return delegate.hitSideOfBoundingBox(x, y, hitThreshold);
	}

	@Override
	public void setTransform(GAffineTransform directTransform) {
		this.transform = directTransform;
		updateHandlers();
	}

	private void setHandlerTransformed(int handlerIndex, double x, double y) {
		corners[handlerIndex] = transform.transform(new GPoint2D(x, y), null);
		delegate.setHandlerFromCenter(handlerIndex,
				corners[handlerIndex].getX(), corners[handlerIndex].getY());
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
		if (!isCropBox()) {
			setHandlerTransformed(8, width / 2, -BoundingBox.ROTATION_HANDLER_DISTANCE);
		}
	}

	/**
	 * @param crop true for crop mode, false for resize
	 */
	public void setCropMode(boolean crop) {
		if (crop != isCropBox()) {
			delegate = crop ? new CropBox(this) : new RotatableBoundingBox(this);
			delegate.createHandlers();
			updateHandlers();
		}
	}

	@Override
	public boolean isCropBox() {
		return delegate instanceof CropBox;
	}

	@Override
	public void updateFrom(GeoElement geo) {
		super.updateFrom(geo);
		this.geo = (RectangleTransformable) geo;
	}

	protected boolean showHandlers() {
		return !((GeoElement) geo).hasGroup();
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
}

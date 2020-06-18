package org.geogebra.common.euclidian;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.RectangleTransformable;

public class MediaBoundingBox extends BoundingBox<GShape> {

	BoundingBoxDelegate delegate;
	protected RectangleTransformable geo;

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
		delegate.draw(g2);
	}

	@Override
	public boolean hitSideOfBoundingBox(int x, int y, int hitThreshold) {
		return delegate.hitSideOfBoundingBox(x, y, hitThreshold);
	}

	@Override
	public void setTransform(GAffineTransform directTransform) {
		delegate.setTransform(directTransform);
	}

	public void setCropMode(boolean crop) {
		delegate = crop ? new CropBox(this) : new RotatableBoundingBox(this);
		delegate.createHandlers();
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

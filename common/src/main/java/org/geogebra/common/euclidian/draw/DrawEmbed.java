package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.debug.Log;

/**
 * Drawable for embedded apps
 */
public class DrawEmbed extends Drawable implements DrawWidget {

	private BoundingBox boundingBox;
	private GRectangle2D bounds;
	private double originalRatio = 1;

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            embedded applet
	 */
	public DrawEmbed(EuclidianView ev, GeoEmbed geo) {
		this.view = ev;
		this.geo = geo;
		if (getEmbedManager() != null) {
			getEmbedManager().add(this);
		}
		update();
	}

	private EmbedManager getEmbedManager() {
		return view.getApplication().getEmbedManager();
	}

	@Override
	public void update() {
		if (getEmbedManager() != null) {
			getEmbedManager().update(this);
		}
		setMetrics();
	}

	private void setMetrics() {
		bounds = AwtFactory.getPrototype().newRectangle2D();
		bounds.setFrame(getLeft(), getTop(),
				getRight() - getLeft(), getBottom() - getTop());
		if (boundingBox != null) {
			boundingBox.setRectangle(bounds);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (hitBoundingBox(x, y, hitThreshold)) {
			// TODO geo.setLastHitType(HitType.ON_BOUNDARY);
			return false;
		}
		if (bounds == null) {
			return false;
		}
		// TODO geo.setLastHitType(HitType.ON_FILLING);
		return bounds.contains(x, y) && geo.isVisible();
	}

	private boolean hitBoundingBox(int hitX, int hitY, int hitThreshold) {
		return getBoundingBox() != null && getBoundingBox().getRectangle() != null
				&& getBoundingBox() == view.getBoundingBox()
				&& getBoundingBox().getRectangle().intersects(hitX - hitThreshold,
						hitY - hitThreshold, 2 * hitThreshold, 2 * hitThreshold)
				&& getBoundingBox().hitSideOfBoundingBox(hitX, hitY, hitThreshold);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(bounds);
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;

	}

	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new BoundingBox(false);
			setMetrics();
		}
		return boundingBox;
	}

	@Override
	public int getTop() {
		return getView().toScreenCoordY(((GeoEmbed) geo).getCorner(2).getInhomY());
	}

	@Override
	public int getLeft() {
		return getView().toScreenCoordX(((GeoEmbed) geo).getCorner(0).getInhomX());
	}

	public int getRight() {
		return getView().toScreenCoordX(((GeoEmbed) geo).getCorner(1).getInhomX());
	}

	public int getBottom() {
		return getView().toScreenCoordY(((GeoEmbed) geo).getCorner(0).getInhomY());
	}

	public int getEmbedID() {
		return ((GeoEmbed) geo).getEmbedID();
	}
	
	private void updateOriginalRatio() {
		double width = getWidth();
		double height = getHeight();
		originalRatio = height / width;
	}

	@Override
	public int getWidth() {
		return getBottom() - getTop();
	}

	@Override
	public int getHeight() {
		return getBottom() - getTop();
	}

	@Override
	public void updateByBoundingBoxResize(AbstractEvent e, EuclidianBoundingBoxHandler handler) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}

		getBoundingBox().resize(this, e, handler);
	}

	@Override
	public void setWidth(int newWidth) {

		GeoPoint corner = ((GeoEmbed) geo).getCorner(1);
		Log.error(
				newWidth + "," + (((GeoEmbed) geo).getCorner(0).getInhomX()
						+ newWidth / view.getXscale()));
		corner.setCoords(((GeoEmbed) geo).getCorner(0).getInhomX() + newWidth / view.getXscale(),
				corner.getInhomY(), 1);
		corner.updateCoords();
	}

	@Override
	public void setHeight(int newHeight) {
		GeoPoint corner = ((GeoEmbed) geo).getCorner(0);
		corner.setCoords(corner.getInhomX(),
				corner.getInhomY() - (newHeight - getHeight()) / view.getYscale(), 1);
		corner = ((GeoEmbed) geo).getCorner(1);
		corner.setCoords(corner.getInhomX(),
				corner.getInhomY() - (newHeight - getHeight()) / view.getYscale(), 1);
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getOriginalRatio() {
		return this.originalRatio;
	}

	@Override
	public void resetRatio() {
		this.originalRatio = Double.NaN;
	}

}

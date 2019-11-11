package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App.ExportType;

/**
 * Drawable for embedded apps
 */
public class DrawEmbed extends Drawable implements DrawWidget, RemoveNeeded {
	private BoundingBox boundingBox;
	private GRectangle2D bounds;
	private double originalRatio = Double.NaN;
	private GeoEmbed geoEmbed;
	private final static int EMBED_SIZE_THRESHOLD = 100;

	/**
	 * @param ev
	 *            view
	 * @param geo
	 *            embedded applet
	 */
	public DrawEmbed(EuclidianView ev, GeoEmbed geo) {
		this.view = ev;
		this.geo = geo;
		this.geoEmbed = geo;
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
				getWidth(), getHeight());
		if (boundingBox != null) {
			boundingBox.setRectangle(bounds);
		}
	}

	@Override
	public double getWidthThreshold() {
		return EMBED_SIZE_THRESHOLD;
	}

	@Override
	public double getHeightThreshold() {
		return EMBED_SIZE_THRESHOLD;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (view.getApplication().getExportType() == ExportType.NONE) {
			return;
		}
		MyImage preview = view.getApplication().getEmbedManager().getPreview(this);

		// if (preview != null) {
		g2.setColor(GColor.BLACK);
		int sx = getWidth();
		int sy = getHeight();
		g2.drawRect(getLeft(), getTop(), sx, sy);
		g2.saveTransform();

		double s = Math.min(sx, sy);
		g2.translate(getLeft() + Math.max((sx - s) / 2, 0), getTop() + Math.max((sy - s) / 2, 0));
		g2.scale(s / preview.getWidth(), s / preview.getHeight());

		g2.drawImage(preview, 0, 0);
		g2.restoreTransform();

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
		return getBoundingBox() != null && getBoundingBox() == view.getBoundingBox()
				&& getBoundingBox().hit(hitX, hitY, hitThreshold);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(bounds);
	}

	@Override
	public GRectangle getBounds() {
		return bounds == null ? null : bounds.getBounds();
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = createBoundingBox(false, false);
			setMetrics();
		}
		boundingBox.updateFrom(geo);
		return boundingBox;
	}

	@Override
	public int getTop() {
		return getView().toScreenCoordY(geoEmbed.getCorner(2).getInhomY());
	}

	@Override
	public int getLeft() {
		return getView().toScreenCoordX(geoEmbed.getCorner(0).getInhomX());
	}

	/**
	 * @return embed ID
	 */
	public int getEmbedID() {
		return geoEmbed.getEmbedID();
	}
	
	private void updateOriginalRatio() {
		double width = getWidth();
		double height = getHeight();
		originalRatio = height / width;
	}

	@Override
	public int getWidth() {
		return (int) Math.round(getView().toScreenCoordXd(geoEmbed.getCorner(1).getInhomX())
				- getView().toScreenCoordXd(geoEmbed.getCorner(0).getInhomX()));
	}

	@Override
	public int getHeight() {
		return (int) Math.round(getView().toScreenCoordYd(geoEmbed.getCorner(0).getInhomY())
				- getView().toScreenCoordYd(geoEmbed.getCorner(2).getInhomY()));
	}

	@Override
	public void updateByBoundingBoxResize(GPoint2D p,
			EuclidianBoundingBoxHandler handler) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}
		getBoundingBox().resize(this, p, handler);
	}

	@Override
	public void setWidth(int newWidth) {
		double contentWidth = geoEmbed.getContentWidth() * newWidth / getWidth();
		geoEmbed.setContentWidth(contentWidth);
		GeoPointND corner = geoEmbed.getCorner(1);
		corner.setCoords(geoEmbed.getCorner(0).getInhomX() + newWidth / view.getXscale(),
				corner.getInhomY(), 1);
		corner.updateCoords();
	}

	@Override
	public void setHeight(int newHeight) {
		double contentHeight = geoEmbed.getContentHeight() * newHeight / getHeight();
		geoEmbed.setContentHeight(contentHeight);
		GeoPointND corner = geoEmbed.getCorner(0);
		corner.setCoords(corner.getInhomX(),
				corner.getInhomY() - (newHeight - getHeight()) / view.getYscale(), 1);
		corner = geoEmbed.getCorner(1);
		corner.setCoords(corner.getInhomX(),
				corner.getInhomY() - (newHeight - getHeight()) / view.getYscale(), 1);
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		geoEmbed.setAbsoluteScreenLoc(x, y);
	}

	@Override
	public double getOriginalRatio() {
		return this.originalRatio;
	}

	@Override
	public void resetRatio() {
		this.originalRatio = Double.NaN;
	}

	/**
	 * @return emvbeded applet as geo
	 */
	public GeoEmbed getGeoEmbed() {
		return geoEmbed;
	}

	@Override
	public void remove() {
		view.getApplication().getEmbedManager().remove(this);
	}

	@Override
	public boolean isFixedRatio() {
		return false;
	}
}

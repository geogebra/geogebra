package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EmbedManager;
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

	private final EmbedManager embedManager;
	private GRectangle2D bounds;
	private double originalRatio = Double.NaN;
	private GeoEmbed geoEmbed;
	private final static int EMBED_SIZE_THRESHOLD = 100;
	private MyImage preview;

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
		embedManager = view.getApplication().getEmbedManager();
		update();
		if (embedManager != null) {
			preview = embedManager.getPreview(this);
		}
	}

	@Override
	public void update() {
		if (geoEmbed.getEmbedID() >= 0 && embedManager != null) {
			embedManager.add(this);
		}
		if (embedManager != null) {
			embedManager.update(this);
		}

		bounds = AwtFactory.getPrototype().newRectangle2D();
		bounds.setFrame(getLeft(), getTop(), getWidth(), getHeight());
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
			view.embed(g2, this);
			return;
		}

		int sx = getWidth();
		int sy = getHeight();
		g2.setColor(GColor.WHITE);
		g2.fillRect(getLeft(), getTop(), sx, sy);
		g2.setColor(GColor.BLACK);
		g2.drawRect(getLeft(), getTop(), sx, sy);

		int s = Math.min(sx, sy);
		int iconLeft = getLeft() + Math.max((sx - s) / 2, 0);
		int iconTop = getTop() + Math.max((sy - s) / 2, 0);
		g2.drawImage(preview, iconLeft, iconTop, s, s);
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
		return view.getBoundingBox() != null && geo.isSelected()
				&& view.getBoundingBox().hit(hitX, hitY, hitThreshold);
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
	public int getTop() {
		return getView().toScreenCoordY(geoEmbed.getCorner(2).getInhomY());
	}

	@Override
	public int getLeft() {
		return getView().toScreenCoordX(geoEmbed.getCorner(0).getInhomX());
	}

	@Override
	public int getEmbedID() {
		return geoEmbed.getEmbedID();
	}

	@Override
	public boolean isBackground() {
		return geoEmbed.isBackground();
	}

	@Override
	public void setBackground(boolean b) {
		geoEmbed.setBackground(b);
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
	public void fromPoints(ArrayList<GPoint2D> pts) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}
		BoundingBox.resize(this, pts.get(0), pts.get(1));
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
	 * @return embedded applet as geo
	 */
	public GeoEmbed getGeoEmbed() {
		return geoEmbed;
	}

	@Override
	public void remove() {
		if (embedManager != null) {
			embedManager.remove(this);
		}
	}

	@Override
	public boolean isFixedRatio() {
		return false;
	}
}

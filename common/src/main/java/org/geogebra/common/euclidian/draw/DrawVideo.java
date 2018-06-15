package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianBoundingBoxHandler;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;

/**
 * Drawable class for GeoVideo
 *
 * It uses HTML representation.
 *
 * @author laszlo
 *
 */
public class DrawVideo extends Drawable implements DrawWidget {
	private GeoVideo video;
	private App app;
	private GRectangle bounds;
	private int left;
	private int top;
	private BoundingBox boundingBox;
	private double originalRatio = Double.NaN;


	/** Threshold correction for resizing handler capturing */
	public static final int HANDLER_THRESHOLD = -4;

	/**
	 * @param view
	 *            The euclidian view.
	 * @param geo
	 *            The GeoElement that represents the video content.
	 */
	public DrawVideo(EuclidianView view, GeoVideo geo) {
		this.view = view;
		this.video = geo;
		this.geo = geo;
		this.app = geo.getKernel().getApplication();
		setMetrics();
	}

	@Override
	public void update() {
		app.getGuiManager().updateVideo(video);
		setMetrics();
	}

	private void setMetrics() {
		int width = video.getWidth();
		int height = video.getHeight();
		left = video.getAbsoluteScreenLocX();
		top = video.getAbsoluteScreenLocY();

		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
		if (boundingBox != null) {
			boundingBox.setRectangle(bounds);
		}
	}

	private boolean isPreviewNeeded() {
		return app.getVideoManager().isPreviewOnly();
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (!isPreviewNeeded()) {
			return;
		}
		MyImage preview = video.getPreview();
		if (preview != null) {
			g2.saveTransform();
			double sx = video.getWidth();
			sx /= preview.getWidth();
			double sy = video.getHeight();
			sy /= preview.getHeight();
			g2.translate(left, top);
			g2.scale(sx, sy);
			g2.drawImage(preview, 0, 0);
			g2.restoreTransform();
		}
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (hitBoundingBox(x, y, hitThreshold)) {
			video.setLastHitType(HitType.ON_BOUNDARY);
			return false;
		}
		if (bounds == null) {
			return false;
		}
		video.setLastHitType(HitType.ON_FILLING);
		return bounds.contains(x, y) && video.isVisible();
	}

	private boolean hitBoundingBox(int hitX, int hitY, int hitThreshold) {
		return getBoundingBox() != null && getBoundingBox().getRectangle() != null
				&& getBoundingBox() == view.getBoundingBox()
				&& getBoundingBox().getRectangle().intersects(
						hitX - hitThreshold, hitY - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold)
				&& getBoundingBox().hitSideOfBoundingBox(hitX, hitY, hitThreshold);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(bounds);
	}

	@Override
	public GeoElement getGeoElement() {
		return video;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		if (geo.isGeoVideo()) {
			this.geo = geo;
			video = (GeoVideo) geo;
		}
	}

	@Override
	public BoundingBox getBoundingBox() {
		if (boundingBox == null) {
			boundingBox = new BoundingBox(false);
			setMetrics();
		}
		return video.isBackground() ? boundingBox : null;
	}

	private void updateOriginalRatio() {
		double width = video.getWidth();
		double height = video.getHeight();
		originalRatio = height / width;
	}

	@Override
	public void updateByBoundingBoxResize(AbstractEvent e,
			EuclidianBoundingBoxHandler handler) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}

		getBoundingBox().resize(this, e, handler);
	}

	@Override
	public GRectangle getBounds() {
		return bounds;
	}

	public void setWidth(int newWidth) {
		video.setWidth(newWidth);
	}

	public void setHeight(int newHeight) {
		video.setWidth(newHeight);
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public void setAbsoluteScreenLoc(int x, int y) {
		video.setAbsoluteScreenLoc(x, y);
	}

	public double getOriginalRatio() {
		return originalRatio;
	}

	public int getWidth() {
		return video.getWidth();
	}

	public int getHeight() {
		return video.getHeight();
	}

	public void resetRatio() {
		originalRatio = Double.NaN;
	}

}

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.main.App;
import org.geogebra.common.media.MediaFormat;

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
	private double originalRatio = Double.NaN;
	private final static int VIDEO_SIZE_THRESHOLD = 100;

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
		video.zoomIfNeeded();
		if (app.getVideoManager() != null) {
			app.getVideoManager().updatePlayer(video);
		}
		setMetrics();
	}

	@Override
	public double getWidthThreshold() {
		return VIDEO_SIZE_THRESHOLD;
	}

	@Override
	public double getHeightThreshold() {
		return VIDEO_SIZE_THRESHOLD;
	}

	private void setMetrics() {
		int width = video.getWidth();
		int height = video.getHeight();
		left = video.getScreenLocX(view);
		top = video.getScreenLocY(view);

		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
	}

	private boolean isPreviewNeeded() {
		return app.getVideoManager() != null && app.getVideoManager().isPreviewOnly();
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
		return view.getBoundingBox() != null && geo.isSelected()
				&& view.getBoundingBox().hit(hitX, hitY, hitThreshold);
	}

	@Override
	public boolean isInside(GRectangle rect) {
		return rect.contains(bounds);
	}

	@Override
	public GeoElement getGeoElement() {
		return video;
	}

	private void updateOriginalRatio() {
		double width = video.getWidth();
		double height = video.getHeight();
		originalRatio = height / width;
	}

	@Override
	public void fromPoints(ArrayList<GPoint2D> pts) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}
		BoundingBox.resize(this, pts.get(0), pts.get(1));
	}

	@Override
	public GRectangle getBounds() {
		return bounds;
	}

	@Override
	public void setWidth(int newWidth) {
		video.setWidth(newWidth);
	}

	@Override
	public void setHeight(int newHeight) {
		video.setHeight(newHeight);
	}

	@Override
	public int getLeft() {
		return left;
	}

	@Override
	public int getTop() {
		return top;
	}

	@Override
	public void setAbsoluteScreenLoc(int x, int y) {
		video.setAbsoluteScreenLoc(x, y);
	}

	@Override
	public double getOriginalRatio() {
		return originalRatio;
	}

	@Override
	public int getWidth() {
		return video.getWidth();
	}

	@Override
	public int getHeight() {
		return video.getHeight();
	}

	@Override
	public void resetRatio() {
		originalRatio = Double.NaN;
	}

	@Override
	public boolean isFixedRatio() {
		return video.getFormat() != MediaFormat.VIDEO_YOUTUBE;
	}

}

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
public class DrawVideo extends Drawable {
	private GeoVideo video;
	private App app;
	private GRectangle bounds;
	private int left;
	private int top;
	private BoundingBox boundingBox;
	private double originalRatio = Double.NaN;
	private final static int VIDEO_SIZE_THRESHOLD = 100;

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
	}

	@Override
	public void update() {
		app.getGuiManager().updateVideo(video);
		int width = video.getWidth();
		int height = video.getHeight();
		left = video.getAbsoluteScreenLocX();
		top = video.getAbsoluteScreenLocY();

		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
		if (bounds != null) {
			getBoundingBox().setRectangle(bounds);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (video.isPlaying()) {
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
		}
		return video.isPlaying() ? null : boundingBox;
	}

	private void updateOriginalRatio() {
		double width, height;
		width = video.getWidth();
		height = video.getHeight();
		originalRatio = height / width;
	}

	@Override
	public void updateByBoundingBoxResize(AbstractEvent e,
			EuclidianBoundingBoxHandler handler) {
		if (Double.isNaN(originalRatio)) {
			updateOriginalRatio();
		}
		int eventX = e.getX();
		int eventY = e.getY();

		int newWidth = 1;
		int newHeight = 1;

		switch (handler) {
		case TOP_RIGHT:
			newWidth = eventX - left;
			if (newWidth <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			newHeight = (int) (originalRatio * newWidth);
			video.setAbsoluteScreenLoc(left,
					top - newHeight + video.getHeight());
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			update();
			break;

		case BOTTOM_RIGHT:
			newWidth = eventX - left;
			if (newWidth <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			newHeight = (int) (originalRatio * newWidth);
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			update();
			break;

		case TOP_LEFT:
			newWidth = video.getWidth() + left - eventX;
			if (newWidth <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			newHeight = (int) (originalRatio * newWidth);
			video.setAbsoluteScreenLoc(eventX,
					top - newHeight + video.getHeight());
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			update();
			break;

		case BOTTOM_LEFT:
			newWidth = video.getWidth() + left - eventX;
			if (newWidth <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			newHeight = (int) (originalRatio * newWidth);
			video.setAbsoluteScreenLoc(eventX, top);
			video.setWidth(newWidth);
			video.setHeight(newHeight);
			update();
			break;

		case RIGHT:
			newWidth = eventX - left;
			if (newWidth <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			video.setWidth(newWidth);
			originalRatio = Double.NaN;
			update();
			break;

		case LEFT:
			newWidth = video.getWidth() + left - eventX;
			if (newWidth <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			video.setAbsoluteScreenLoc(eventX, top);
			video.setWidth(newWidth);
			originalRatio = Double.NaN;
			update();
			break;

		case TOP:
			newHeight = video.getHeight() + top - eventY;
			if (newHeight <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			video.setAbsoluteScreenLoc(left, eventY);
			video.setHeight(newHeight);
			originalRatio = Double.NaN;
			update();
			break;

		case BOTTOM:
			newHeight = eventY - top;
			if (newHeight <= VIDEO_SIZE_THRESHOLD) {
				return;
			}
			video.setHeight(newHeight);
			originalRatio = Double.NaN;
			update();
			break;
		case UNDEFINED:
		default:
			break;
		}
	}

	@Override
	public GRectangle getBounds() {
		return bounds;
	}
}

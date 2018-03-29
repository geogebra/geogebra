package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.BoundingBox;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
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
		int left = video.labelOffsetX;
		int top = video.labelOffsetY;
		int width = video.getWidth();
		int height = video.getHeight();
		bounds = AwtFactory.getPrototype().newRectangle(left, top, width, height);
	}

	@Override
	public void draw(GGraphics2D g2) {
		// Video is not drawn to canvas.
	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		return bounds.contains(x, y) && video.isVisible();
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
		// TODO implement this.
		return null;
	}

}

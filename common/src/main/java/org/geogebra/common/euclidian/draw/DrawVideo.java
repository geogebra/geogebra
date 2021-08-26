package org.geogebra.common.euclidian.draw;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.RemoveNeeded;
import org.geogebra.common.kernel.geos.GeoVideo;
import org.geogebra.common.kernel.geos.GeoWidget;
import org.geogebra.common.main.App;
import org.geogebra.common.media.MediaFormat;
import org.geogebra.common.media.VideoManager;

/**
 * Drawable class for GeoVideo
 *
 * It uses HTML representation.
 *
 * @author laszlo
 *
 */
public class DrawVideo extends DrawWidget implements RemoveNeeded {

	private final GeoVideo video;
	private final App app;

	/**
	 * @param view
	 *            The euclidian view.
	 * @param geo
	 *            The GeoElement that represents the video content.
	 */
	public DrawVideo(EuclidianView view, GeoVideo geo) {
		super(view, geo, geo.getFormat() != MediaFormat.VIDEO_YOUTUBE);
		this.video = geo;
		this.app = geo.getKernel().getApplication();

		updateBounds();
	}

	@Override
	public void update() {
		getGeoElement().zoomIfNeeded();
		updateBounds();

		if (app.getVideoManager() != null) {
			app.getVideoManager().updatePlayer(this);
		}
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (view.getApplication().getExportType() == App.ExportType.NONE) {
			view.embed(g2, this);
			return;
		}

		MyImage preview = video.getPreview();
		if (preview != null) {
			g2.saveTransform();
			double sx = video.getWidth();
			sx /= preview.getWidth();
			double sy = video.getHeight();
			sy /= preview.getHeight();
			g2.transform(getTransform());
			g2.scale(sx, sy);
			g2.drawImage(preview, 0, 0);
			g2.restoreTransform();
		}
	}

	@Override
	public GeoWidget getGeoElement() {
		return video;
	}

	@Override
	public int getEmbedID() {
		return -1;
	}

	@Override
	public boolean isBackground() {
		return video.isBackground();
	}

	@Override
	public void setBackground(boolean b) {
		video.setBackground(b);
	}

	public GeoVideo getVideo() {
		return video;
	}

	@Override
	public void remove() {
		VideoManager videoManager = app.getVideoManager();
		if (videoManager != null) {
			videoManager.removePlayer(this);
		}
	}
}

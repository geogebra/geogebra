package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.GeoClass;

/**
 * 
 * GeoElement to represent a video object.
 * 
 * @author laszlo
 *
 */
public class GeoVideo extends GeoAudio {
	/**
	 * Test video URL.
	 */
	public static final String TEST_URL = "https://www.youtube.com/embed/8MPbR6Cbwi4";
	private static final int VIDEO_WIDTH = 420;
	private static final int VIDEO_HEIGHT = 345;
	private boolean changed = false;
	/**
	 * Constructor.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoVideo(Construction c) {
		super(c);
		setWidth(VIDEO_WIDTH);
		setHeight(VIDEO_HEIGHT);
	}

	/**
	 * Constructor.
	 * 
	 * @param c
	 *            the construction.
	 * @param url
	 *            the URL of the video.
	 */
	public GeoVideo(Construction c, String url) {
		super(c, url);
		setLabel("video");
		setWidth(VIDEO_WIDTH);
		setHeight(VIDEO_HEIGHT);
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.VIDEO;
	}

	@Override
	public GeoElement copy() {
		GeoVideo ret = new GeoVideo(cons);
		ret.setSrc(getSrc());
		return ret;
	}

	@Override
	public void set(GeoElementND geo) {
		if (!geo.isGeoVideo()) {
			return;
		}
		setSrc(((GeoVideo) geo).getSrc());
		changed = true;
	}

	@Override
	public void setSrc(String url) {
		super.setSrc(url);
		changed = true;
	}

	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		changed = true;
	}

	@Override
	public void setHeight(int height) {
		super.setHeight(height);
		changed = true;
	}

	@Override
	public boolean isGeoAudio() {
		return false;
	}

	@Override
	public boolean isGeoVideo() {
		return true;
	}

	@Override
	public boolean isPlaying() {
		if (!hasVideoManager()) {
			return false;
		}

		return app.getVideoManager().isPlaying(this);
	}

	private boolean hasVideoManager() {
		return app.getVideoManager() != null;
	}

	/**
	 * 
	 * @return if any relevant property has changed.
	 */
	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}

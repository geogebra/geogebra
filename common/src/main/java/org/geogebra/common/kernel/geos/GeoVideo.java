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
	 * Constructor.
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoVideo(Construction c) {
		super(c);
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

}

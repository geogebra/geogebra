package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.media.MediaFormat;

/**
 * Geo class for mp4 format video files.
 * 
 * @author laszlo
 *
 */
public class GeoMP4Video extends GeoVideo {

	/**
	 * 
	 * @param c
	 *            the construction.
	 */
	public GeoMP4Video(Construction c) {
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
	public GeoMP4Video(Construction c, String url) {
		super(c, url);
	}

	@Override
	public MediaFormat getFormat() {
		return MediaFormat.MP4;
	}
}

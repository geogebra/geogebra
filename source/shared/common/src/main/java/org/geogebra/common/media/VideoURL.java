/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.media;

/**
 * Data class for results of video URL check.
 * 
 * @author laszlo
 *
 */
public class VideoURL {
	private String url;
	private boolean valid;
	private MediaFormat format;

	/**
	 * Constructor
	 * 
	 * @param url
	 *            The video URL
	 * @param valid
	 *            if the URL is valid.
	 * @param format
	 *            Video format.
	 */
	protected VideoURL(String url, boolean valid, MediaFormat format) {
		this.url = url;
		this.valid = valid;
		this.format = format;
	}

	/**
	 * 
	 * @return true if the URL points to a valid video.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * 
	 * @return the media format of the URL.
	 */
	public MediaFormat getFormat() {
		return format;
	}

	/**
	 * Creates object with a valid result.
	 * 
	 * @param url
	 *            the URL of the video.
	 * @param format
	 *            {@link MediaFormat}
	 * @return an {@link VideoURL} instance.
	 */
	public static VideoURL createOK(String url, MediaFormat format) {
		return new VideoURL(url, true, format);
	}

	/**
	 * Creates object with an invalid result.
	 * 
	 * @param url
	 *            the URL of the video.
	 * 
	 * @param format
	 *            {@link MediaFormat}
	 * @return an {@link VideoURL} instance.
	 */
	public static VideoURL createError(String url, MediaFormat format) {
		return new VideoURL(url, false, format);
	}

	/**
	 * 
	 * @return the URL of the video.
	 */
	public String getUrl() {
		return url;
	}
}
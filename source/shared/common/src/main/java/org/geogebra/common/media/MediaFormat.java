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

/** Specifies media format */
public enum MediaFormat {
	/** No media format at all */
	NONE("none"),

	/** audio format supported by HTML5 audio tag */
	AUDIO_HTML5("html5audio"),

	/** video format supported by HTML5 video tag */
	VIDEO_HTML5("html5video"),

	/** YouTube video format */
	VIDEO_YOUTUBE("youtube"),

	/** Video from Mebis site */
	VIDEO_MEBIS("mebis");
	private final String name;

	MediaFormat(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * 
	 * @param key
	 *            the name of the format.
	 * @return the corresponding enum.
	 */
	public static MediaFormat get(String key) {
		if ("html5audio".equals(key)) {
			return MediaFormat.AUDIO_HTML5;
		} else if ("html5video".equals(key)) {
			return MediaFormat.VIDEO_HTML5;
		} else if ("youtube".equals(key)) {
			return MediaFormat.VIDEO_YOUTUBE;
		}
		if ("mebis".equals(key)) {
			return MediaFormat.VIDEO_MEBIS;
		}
		return MediaFormat.NONE;
	}
}
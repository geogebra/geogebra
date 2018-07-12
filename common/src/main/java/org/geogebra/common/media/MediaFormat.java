package org.geogebra.common.media;

/** Specifies media format */
public enum MediaFormat {
	/** No media format at all */
	NONE,

	/** audio format supported by HTML5 audio tag */
	AUDIO_HTML5,

	/** video format supported by HTML5 video tag */
	VIDEO_HTML5,

	/** YouTube video format */
	VIDEO_YOUTUBE,

	/** Video from Mebis site */
	VIDEO_MEBIS
}
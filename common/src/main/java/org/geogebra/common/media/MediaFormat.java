package org.geogebra.common.media;

/** Specifies media format */
public enum MediaFormat {
	/** No media format at all */
	NONE,

	/** Any audio format that html5 audio tag can play. */
	AUDIO_HTML5,

	/** YouTube video format */
	YOUTUBE,

	/** MP4 video format */
	MP4
}
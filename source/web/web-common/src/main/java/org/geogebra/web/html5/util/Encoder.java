package org.geogebra.web.html5.util;

/**
 * Animation encoder.
 * TODO remove, use FrameCollectorW directly
 */
public interface Encoder {

	/**
	 * Add a frame.
	 * @param url data URL of the frame
	 */
	void addFrame(String url);

	/**
	 * @param width width in px
	 * @param height height in px
	 * @return base64 string
	 */
	String finish(int width, int height);

}

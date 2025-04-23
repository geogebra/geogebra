package org.geogebra.web.html5.util;

/**
 * Animation encoder.
 */
public interface Encoder {

	void addFrame(String url);

	/**
	 * @param width width in px
	 * @param height height in px
	 * @return base64 string
	 */
	String finish(int width, int height);

}

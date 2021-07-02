package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.html5.export.WebMVideo;

import elemental2.dom.Blob;
import elemental2.dom.URL;

/**
 * Wrapper class for the Whammy.js library.
 *
 * adapted from GifShot class
 */
public class WebMEncoderW implements Encoder {

	/**
	 * Milliseconds between frames.
	 */
	private final int frameDelay;
	private final String filename;

	private final List<String> images;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 */
	public WebMEncoderW(int frameDelay, String filename) {
		this.frameDelay = frameDelay;
		this.filename = filename;
		this.images = new ArrayList<>();
		ExportLoader.onWhammyLoaded(() -> { /* preload while the images are being prepared */ });
	}

	/**
	 * @param url
	 *            adds a new frame
	 */
	@Override
	public void addFrame(String url) {
		images.add(url);
	}

	@Override
	public String finish(int width, int height) {
		ExportLoader.onWhammyLoaded(() ->
				finish(images, filename, frameDelay * 0.001));
		return null;
	}

	private void finish(List<String> images, String filename, double delaySeconds) {
		WebMVideo encoder = new WebMVideo(1 / delaySeconds);

		for (String image : images) {
			encoder.add(image);
		}

		Blob blob = encoder.compile();
		String url = URL.createObjectURL(blob);

		Browser.downloadURL(url, filename);
	}
}

package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.html5.export.WebMVideo;

import elemental2.dom.Blob;
import elemental2.dom.URL;

/**
 * Wrapper class for the Whammy.js library.
 *
 * adapted from GifShot class
 */
public class WebMEncoderW implements FrameCollectorW {

	/**
	 * Milliseconds between frames.
	 */
	private final int frameDelay;
	private final StringConsumer consumer;

	private final List<String> images;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 */
	public WebMEncoderW(int frameDelay, StringConsumer consumer) {
		this.frameDelay = frameDelay;
		this.consumer = consumer;
		this.images = new ArrayList<>();
		ExportLoader.onWhammyLoaded(() -> { /* preload while the images are being prepared */ });
	}

	@Override
	public void addFrame(EuclidianViewWInterface view,
			double exportScale) {
		String url = view.getExportImageDataUrl(exportScale, false,
				App.ExportType.WEBP, false);
		images.add(url);
	}

	@Override
	public void finish(int width, int height) {
		ExportLoader.onWhammyLoaded(() ->
				finish(images, frameDelay * 0.001));
	}

	private void finish(List<String> images, double delaySeconds) {
		WebMVideo encoder = new WebMVideo(1 / delaySeconds);

		for (String image : images) {
			encoder.add(image);
		}

		Blob blob = encoder.compile();
		String url = URL.createObjectURL(blob);

		consumer.consume(url);
	}
}

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

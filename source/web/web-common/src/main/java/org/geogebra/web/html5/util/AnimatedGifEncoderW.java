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
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.export.ExportLoader;
import org.geogebra.web.html5.export.Gifshot;

import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Wrapper class for the gif.js library.
 * 
 * @author bencze
 * @author Laszlo Gal - GifShot integration
 */
public class AnimatedGifEncoderW implements FrameCollectorW {

	/**
	 * Milliseconds between frames.
	 */
	private final int frameDelay;
	private final StringConsumer filename;

	private final List<String> gifs;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 */
	public AnimatedGifEncoderW(int frameDelay, StringConsumer filename) {
		this.frameDelay = frameDelay;
		this.filename = filename;
		this.gifs = new ArrayList<>();
		ExportLoader.onGifshotLoaded(() -> { /* preload while the images are being prepared */ });
	}

	@Override
	public void addFrame(EuclidianViewWInterface view,
			double exportScale) {
		String url = view.getExportImageDataUrl(exportScale, false,
				App.ExportType.PNG, false);
		gifs.add(url);
	}

	/**
	 * Finishes the internal gif object and starts rendering.
	 */
	@Override
	public void finish(int width, int height) {
		ExportLoader.onGifshotLoaded(() ->
				finish(gifs, filename, width, height, frameDelay * 0.001));
	}

	private void finish(List<String> urls,
			StringConsumer consumer, double width, double height, double delaySeconds) {

		JsPropertyMap<Object> settings = JsPropertyMap.of();
		settings.set("images", JsArray.asJsArray(urls.toArray()));
		settings.set("gifWidth", width);
		settings.set("gifHeight", height);
		settings.set("interval", delaySeconds);

		ExportLoader.getGifshot().createGIF(settings, (obj) -> {
			Gifshot.GifshotResult res = Js.uncheckedCast(obj);
			if (Js.isFalsy(res.error)) {
				consumer.consume(res.image);
			} else {
				Log.error(res);
			}
		});
	}
}

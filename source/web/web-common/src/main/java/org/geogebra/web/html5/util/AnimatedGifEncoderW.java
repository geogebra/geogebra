package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.export.ExportLoader;

import elemental2.core.JsArray;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Wrapper class for the gif.js library.
 * 
 * @author bencze
 * @author Laszlo Gal - GifShot integration
 */
public class AnimatedGifEncoderW implements Encoder {

	/**
	 * Milliseconds between frames.
	 */
	private final int frameDelay;
	private final String filename;

	private final List<String> gifs;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 */
	public AnimatedGifEncoderW(int frameDelay, String filename) {
		this.frameDelay = frameDelay;
		this.filename = filename;
		this.gifs = new ArrayList<>();
		ExportLoader.onGifshotLoaded(() -> { /* preload while the images are being prepared */ });
	}

	/**
	 * @param url
	 *            adds a new frame
	 */
	@Override
	public void addFrame(String url) {
		gifs.add(url);
	}

	/**
	 * Finishes the internal gif object and starts rendering.
	 */
	@Override
	public String finish(int width, int height) {
		ExportLoader.onGifshotLoaded(() ->
				finish(gifs, filename, width, height, frameDelay * 0.001));
		return null;
	}

	private void finish(List<String> urls,
			String filename, double width, double height, double delaySeconds) {

		JsPropertyMap<Object> settings = JsPropertyMap.of();
		settings.set("images", JsArray.asJsArray(urls.toArray()));
		settings.set("gifWidth", width);
		settings.set("gifHeight", height);
		settings.set("interval", delaySeconds);

		ExportLoader.getGifshot().createGIF(settings, (obj) -> {
			if (Js.isFalsy(obj.error)) {
				Browser.exportImage(obj.image, filename);
			} else {
				Log.error(obj);
			}
		});
	}
}

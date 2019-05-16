package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

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
	private int frameDelay;

	protected boolean jsLoaded;
	private List<String> gifs;
	protected boolean finished;
	private String filename;
	private boolean repeat;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 * @param repeat
	 *            true to repeat the animation
	 */
	public AnimatedGifEncoderW(int frameDelay, boolean repeat,
			String filename) {
		jsLoaded = false;
		this.frameDelay = frameDelay;
		this.filename = filename;
		gifs = new ArrayList<>();
		this.repeat = repeat;
		initialize();
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
		finished = true;
		if (!jsLoaded) {
			return null;
		}

		JavaScriptObject urls = createJsArrayString(gifs);

		// will return null as GIF is created in webworker
		return finish(urls, filename, width, height, repeat,
				frameDelay * 0.001);

	}

	private static native String finish(JavaScriptObject urls,
			String filename, double width, double height, boolean repeat,
			double delaySeconds) /*-{

		//console.log(urls);

		$wnd.gifshot
				.createGIF(
						{
							'images' : urls,
							'gifWidth' : width,
							'gifHeight' : height,
							'interval' : delaySeconds
						},
						function(obj) {
							if (!obj.error) {
								var image = obj.image;

								//console.log(image);

								@org.geogebra.web.html5.Browser::exportImage(Ljava/lang/String;Ljava/lang/String;)(image, filename);
							} else {
								console.log("error", obj);
							}
						});

		return "";
	}-*/;

	/**
	 * Load JS and clear state.
	 */
	public void initialize() {
		Log.debug("gifshot.image.min.js loading");
		JavaScriptInjector
				.inject(GuiResourcesSimple.INSTANCE.gifShotJs());
		this.jsLoaded = true;
		gifs.clear();
	}

	private static JsArrayString createJsArrayString(List<String> list) {
		JsArrayString jsArray = (JsArrayString) JavaScriptObject.createArray();
		for (String string : list) {
			jsArray.push(string);
		}
		return jsArray;
	}
}

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
public class AnimatedGifEncoderW {

	/**
	 * Reference to the gif object created internally.
	 */
	private JavaScriptObject internal;

	/**
	 * Milliseconds between frames.
	 */
	private int frameDelay;

	protected boolean jsLoaded;
	private List<String> gifs;

	protected boolean finished;

	private String filename;

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
		initialize();
	}

	/**
	 * @param url
	 *            adds a new frame
	 */
	public void addFrame(String url) {
		gifs.add(url);
	}

	/**
	 * Finishes the internal gif object and starts rendering.
	 */
	public String finish() {
		finished = true;
		if (!jsLoaded) {
			return "";
		}

		JavaScriptObject urls = createJsArrayString(gifs);
		return finish(urls, filename);
	}

	private static native String finish(JavaScriptObject urls,
			String filename) /*-{

		//console.log(urls);

		$wnd.gifshot
				.createGIF(
						{
							'images' : urls
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

	public void initialize() {
		finished = false;
		// GWT.runAsync(new RunAsyncCallback() {
		// public void onSuccess() {
		Log.debug("gifshot.image.min.js loading success");
				JavaScriptInjector
						.inject(GuiResourcesSimple.INSTANCE.gifShotJs());
				AnimatedGifEncoderW.this.jsLoaded = true;
				if (finished) {
					JavaScriptObject urls = createJsArrayString(gifs);
					finish(urls, filename);

				}
		// }

		// public void onFailure(Throwable reason) {
		// Log.debug("gifsot.image.min.js loading failure");
		// }
		// });
		gifs.clear();
	}

	private static JsArrayString createJsArrayString(List<String> list) {
		JsArrayString jsArray = (JsArrayString) JsArrayString.createArray();
		for (String string : list) {
			jsArray.push(string);
		}
		return jsArray;
	}
}

package org.geogebra.web.html5.util;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.resources.JavaScriptInjector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Wrapper class for the Whammy.js library.
 *
 * adapted from GifShot class
 */
public class WebMEncoderW implements Encoder {

	/**
	 * Milliseconds between frames.
	 */
	private int frameDelay;

	protected boolean jsLoaded;
	private List<String> images;
	protected boolean finished;
	private String filename;
	private boolean repeat;

	/**
	 * @param frameDelay
	 *            delay between the frames in milliseconds
	 * @param repeat
	 *            true to repeat the animation
	 */
	public WebMEncoderW(int frameDelay, boolean repeat,
			String filename) {
		jsLoaded = false;
		this.frameDelay = frameDelay;
		this.filename = filename;
		images = new ArrayList<>();
		this.repeat = repeat;
		initialize();
	}

	/**
	 * @param url
	 *            adds a new frame
	 */
	@Override
	public void addFrame(String url) {
		images.add(url);
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

		JavaScriptObject urls = createJsArrayString(images);
		return finish(urls, filename, width, height, repeat,
				frameDelay * 0.001);
	}

	private static native String finish(JavaScriptObject urls,
			String filename, double width, double height, boolean repeat,
			double delaySeconds) /*-{

		//console.log(urls);

		// pass framerate
		var encoder = new $wnd.WebMGL.Video(1 / delaySeconds);

		for (var i = 0; i < urls.length; i++) {
			encoder.add(urls[i]);
		}

		var blob = encoder.compile();

		var a = document.createElement('a');
		document.body.appendChild(a);
		var url = $wnd.URL.createObjectURL(blob);
		a.href = url;
		a.download = filename;
		a.click();

		//@org.geogebra.web.html5.Browser::exportImage(Ljava/lang/String;Ljava/lang/String;)(image, filename);

		return url;
	}-*/;

	/**
	 * Load JS and clear state.
	 */
	public void initialize() {
		Log.debug("whammy.min.js loading");
		JavaScriptInjector
				.inject(GuiResourcesSimple.INSTANCE.whammyJs());
		this.jsLoaded = true;
		images.clear();
	}

	private static JsArrayString createJsArrayString(List<String> list) {
		JsArrayString jsArray = (JsArrayString) JavaScriptObject.createArray();
		for (String string : list) {
			jsArray.push(string);
		}
		return jsArray;
	}
}

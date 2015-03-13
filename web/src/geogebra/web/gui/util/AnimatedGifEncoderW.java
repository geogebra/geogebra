package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.html5.js.JavaScriptInjector;
import geogebra.web.export.GifShotResources;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.ImageElement;

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
	
	/**
	 * @param frameDelay delay between the frames in milliseconds
	 * @param repeat true to repeat the animation
	 */
	public AnimatedGifEncoderW(int frameDelay, boolean repeat) {
		jsLoaded = false;
		this.frameDelay = frameDelay;
		gifs = new ArrayList<String>();
    }
	
	/**
	 * @param image adds a new frame
	 */
	public void addFrame(ImageElement image) {
		// addFrame(internal, image, frameDelay);
		gifs.add(image.getSrc());
	}
	
	/**
	 * Finishes the internal gif object and starts rendering.
	 */
	public void finish() {
		if (!jsLoaded) {
			return;
		}

		JavaScriptObject urls = createJsArrayString(gifs);
		finish(urls);
	}

	private static native void finish(JavaScriptObject urls) /*-{

		gifshot.createGIF({
			'images' : urls
		}, function(obj) {
			if (!obj.error) {
				var image = obj.image, animatedImage = document
						.createElement('img');
				animatedImage.src = image;
				document.body.appendChild(animatedImage);
			}
		});
	}-*/;
	

	public void initialize() {
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				App.debug("gifsot.image.min.js loading success");
				JavaScriptInjector.inject(GifShotResources.INSTANCE.gifShotJs());
				AnimatedGifEncoderW.this.jsLoaded = true;
			}

			public void onFailure(Throwable reason) {
				App.debug("gifsot.image.min.js loading failure");
			}
		});
	}

	private static JsArrayString createJsArrayString(List<String> list) {
		JsArrayString jsArray = (JsArrayString) JsArrayString.createArray();
		for (String string : list) {
			jsArray.push(string);
		}
		return jsArray;
	}
}

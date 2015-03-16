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
	
	/**
	 * @param frameDelay delay between the frames in milliseconds
	 * @param repeat true to repeat the animation
	 */
	public AnimatedGifEncoderW(int frameDelay, boolean repeat) {
		jsLoaded = false;
		this.frameDelay = frameDelay;
		gifs = new ArrayList<String>();
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
	public void finish() {
		finished = true;
		if (!jsLoaded) {
			return;
		}

		JavaScriptObject urls = createJsArrayString(gifs);
		finish(urls);
	}

	private static native void finish(JavaScriptObject urls) /*-{

		$wnd.gifshot.createGIF({
			'images' : urls
		}, function(obj) {
			if (!obj.error) {
				var image = obj.image, animatedImage = document
						.createElement('img');
				animatedImage.src = image;
				var title = "anim.gif"
				if ($wnd.navigator.msSaveBlob) {
					//works for chrome and internet explorer
					$wnd.navigator.msSaveBlob(animatedImage, title);
				} else {
					//works for firefox
					var a = $doc.createElement("a");
					$doc.body.appendChild(a);
					a.style = "display: none";
					a.href = animatedImage.src;
					a.download = title;
					a.click();
					//		        window.URL.revokeObjectURL(url);
				}

			}
		});
	}-*/;
	

	public void initialize() {
		finished = false;
		GWT.runAsync(new RunAsyncCallback() {
			public void onSuccess() {
				App.debug("gifsot.image.min.js loading success");
				JavaScriptInjector.inject(GifShotResources.INSTANCE.gifShotJs());
				AnimatedGifEncoderW.this.jsLoaded = true;
				if (finished) {
					JavaScriptObject urls = createJsArrayString(gifs);
					finish(urls);

				}
			}

			public void onFailure(Throwable reason) {
				App.debug("gifsot.image.min.js loading failure");
			}
		});
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

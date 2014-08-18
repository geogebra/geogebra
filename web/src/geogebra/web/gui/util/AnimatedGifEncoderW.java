package geogebra.web.gui.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.ImageElement;

/**
 * Wrapper class for the gif.js library.
 * @author bencze
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
	
	/**
	 * @param frameDelay delay between the frames in milliseconds
	 * @param repeat true to repeat the animation
	 */
	public AnimatedGifEncoderW(int frameDelay, boolean repeat) {
		this.frameDelay = frameDelay;
		internal = createObject(repeat ? 0 : -1);
    }
	
	/**
	 * @param image adds a new frame
	 */
	public void addFrame(ImageElement image) {
		addFrame(internal, image, frameDelay);
	}
	
	/**
	 * Finishes the internal gif object and starts rendering.
	 */
	public void finish() {
		finish(internal);
	}

	private native JavaScriptObject createObject(int rep) /*-{
		var obj = new $wnd.GIF({
  			workers: 2,
  			quality: 10,
  			repeat: rep,
  			background: '#fff' // background should be white
  			//transparent: '#fff'
  			});
			//$wnd.console.log(obj);
			return obj;
	}-*/;

	
	private static native void addFrame(JavaScriptObject obj, JavaScriptObject imageElement, int frameDelay) /*-{
		//$wnd.console.log(obj);
		//$wnd.console.log(imageElement);
		//$wnd.console.log(frameDelay);
		if (obj.options.width === null) {
			obj.options.width = imageElement.width;
			obj.options.height = imageElement.height;
		}
		try {
			obj.addFrame(imageElement, {delay: frameDelay, copy:true});
		} catch (e) {
			$wnd.console.log(e.stack);
			throw e;
		}
	}-*/;
	
	private static native void finish(JavaScriptObject obj) /*-{
		obj.on('finished', function(blob) {
			$wnd.console.log(blob);
			$wnd.console.log(URL.createObjectURL(blob));
			  $wnd.open(URL.createObjectURL(blob));
			});
		try {
			obj.render();
		} catch(e) {
			$wnd.console.log(e.stack);
		}
	}-*/;
	
}

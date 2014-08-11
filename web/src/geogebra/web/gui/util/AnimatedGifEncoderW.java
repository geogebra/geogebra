package geogebra.web.gui.util;

import geogebra.html5.gawt.BufferedImage;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Wrapper class around gif.js
 * @author bencze
 */
public class AnimatedGifEncoderW {
	
	private JavaScriptObject internal;
	
	private int frameDelay;
	private String fileName;
	
	/**
	 * @param frameDelay delay
	 * @param repeat true to repeat gif
	 * @param fileName 
	 */
	public AnimatedGifEncoderW(int frameDelay, boolean repeat, String fileName) {
		this.frameDelay = frameDelay;
	    this.fileName = fileName;
		internal = createObject(repeat ? 0 : -1);
    }
	
	/**
	 * @param image adds a new frame
	 */
	public void addFrame(BufferedImage image) {
		addFrame(internal, image.getCanvas().getElement(), frameDelay);
	}
	
	/**
	 * Finishes the image
	 */
	public void finish() {
		finish(internal);
	}

	private native JavaScriptObject createObject(int rep) /*-{
		return new $wnd.GIF({
  			workers: 2,
  			quality: 10,
  			repeat: rep
			});
	}-*/;

	
	private static native void addFrame(JavaScriptObject obj, Element image, int frameDelay) /*-{
		obj.addFrame(image, {delay: frameDelay});
	}-*/;
	
	private static native void finish(JavaScriptObject obj) /*-{
		// testing method for now
		obj.on('finished', function(blob) {
			$wnd.console.log('finished');
			$wnd.console.log(URL.createObjectURL(blob));
			  $wnd.open(URL.createObjectURL(blob));
			});
		gif.render();
	}-*/;
	
}

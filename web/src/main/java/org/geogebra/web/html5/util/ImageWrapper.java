package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

public class ImageWrapper {

	ImageElement img;

	public ImageWrapper(ImageElement imageElement) {
		this.img = imageElement;
	}

	/**
	 * @param imageManager
	 * 
	 *            Images in gwt event system not loaded when src added, only
	 *            when attached to dom. So we must hack it.
	 */
	public void attachNativeLoadHandler(ImageManagerW imageManager) {
		addNativeLoadHandler(img, imageManager);
	}

	private native void addNativeLoadHandler(Element img,
	        ImageManagerW imageManager) /*-{
		img
				.addEventListener(
						"load",
						function() {
							imageManager.@org.geogebra.web.html5.util.ImageManagerW::checkIfAllLoaded()();
						});
		img
				.addEventListener(
						"error",
						function() {
							img.src = imageManager.@org.geogebra.web.html5.util.ImageManagerW::getErrorURL()();
						});
	}-*/;

	public ImageElement getElement() {
		return img;
	}

	public static native void nativeon(ImageElement img, String event,
	        ImageLoadCallback callback) /*-{
		img.addEventListener(event, function() {
			callback.@org.geogebra.web.html5.util.ImageLoadCallback::onLoad()();
		});
	}-*/;

	/**
	 * Native event handler
	 * 
	 * @param event
	 * @param callback
	 */
	public void on(String event, ImageLoadCallback callback) {
		nativeon(getElement(), event, callback);
	}

}

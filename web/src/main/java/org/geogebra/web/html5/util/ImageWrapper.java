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
	 *            Images in gwt event system not loaded when src added, only
	 *            when attached to dom. So we must hack it.
	 */
	public void attachNativeLoadHandler(final ImageManagerW imageManager,
			ImageLoadCallback callback) {
		nativeon(img, "load", callback);
		nativeon(img, "error", new ImageLoadCallback() {

			@Override
			public void onLoad() {
				getElement().setSrc(imageManager.getErrorURL());
			}
		});
	}

	/**
	 * @return wrapped element
	 */
	public ImageElement getElement() {
		return img;
	}

	/**
	 * @param img
	 *            image
	 * @param event
	 *            event name
	 * @param callback
	 *            callback
	 */
	public static native void nativeon(Element img, String event,
	        ImageLoadCallback callback) /*-{
		img
				.addEventListener(
						event,
						function() {
							callback.@org.geogebra.web.html5.util.ImageLoadCallback::onLoad()();
						});
	}-*/;

	/**
	 * Native event handler
	 * 
	 * @param event
	 *            event name
	 * @param callback
	 *            callback
	 */
	public void on(String event, ImageLoadCallback callback) {
		nativeon(getElement(), event, callback);
	}

}

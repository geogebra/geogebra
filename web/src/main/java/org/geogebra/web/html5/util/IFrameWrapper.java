package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;

public class IFrameWrapper {

	IFrameElement iframe;

	public IFrameWrapper(IFrameElement IFrameElement) {
		this.iframe = IFrameElement;
	}

	/**
	 * @param imageManager
	 *            Images in gwt event system not loaded when src added, only
	 *            when attached to dom. So we must hack it.
	 */
	public void attachNativeLoadHandler(ImageManagerW imageManager,
			ImageLoadCallback callback) {
		addNativeLoadHandler(iframe, imageManager, callback);
	}

	private native void addNativeLoadHandler(Element img,
			ImageManagerW imageManager, ImageLoadCallback callback) /*-{
		img
				.addEventListener(
						"load",
						function() {
							callback.@org.geogebra.web.html5.util.ImageLoadCallback::onLoad()();
						});
		img
				.addEventListener(
						"error",
						function() {
							img.src = imageManager.@org.geogebra.web.html5.util.ImageManagerW::getErrorURL()();
						});
	}-*/;

	public IFrameElement getElement() {
		return iframe;
	}

	public static native void nativeon(IFrameElement img, String event,
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

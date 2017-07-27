package org.geogebra.web.web.gui.toolbarpanel;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;

/**
 * Callback that prevents header to be resized during animation.
 * 
 * @author laszlo
 */
public class HeaderAnimationCallback implements AnimationCallback {

	private final Header header;

	/**
	 * @param header
	 *            to set.
	 */
	HeaderAnimationCallback(Header header) {
		this.header = header;
	}

	public void onLayout(Layer layer, double progress) {
		this.header.setAnimating(true);
	}

	public void onAnimationComplete() {
		this.header.setAnimating(false);
		this.header.updateStyle();
	}
}
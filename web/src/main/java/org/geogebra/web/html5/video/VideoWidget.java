package org.geogebra.web.html5.video;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget to wrap HTML5 video tag
 *
 * @author laszlo
 *
 */
public class VideoWidget extends Widget {
	private Element elem;

	/**
	 * Constuctor
	 */
	public VideoWidget() {
		elem = DOM.createElement("video");
		setElement(elem);
	}

	/**
	 * Constuctor
	 *
	 *
	 * @param src
	 *            source of the video
	 */
	public VideoWidget(String src) {
		this();
		setSrc(src);
	}

	/**
	 * @param src
	 *            Source of the video.
	 */
	public void setSrc(String src) {
		elem.setAttribute("src", src);
	}

	/**
	 * Sets the width of the video
	 *
	 * @param width
	 *            to set.
	 */
	public void setWidth(int width) {
		elem.setAttribute("width", width + "px");
	}

	/**
	 * Sets the height of the video
	 *
	 * @param height
	 *            to set.
	 */

	public void setHeight(int height) {
		elem.setAttribute("height", height + "px");
	}

	/**
	 * Play video tag
	 */
	public void play() {
		play(elem);
	}

	/**
	 * Pause video tag
	 */
	public void pause() {
		pause(elem);
	}

	private native void play(JavaScriptObject player) /*-{
		player.play();
	}-*/;

	private native void pause(JavaScriptObject player) /*-{
		player.pause();
	}-*/;

	private void switchAttribute(String name, boolean b) {
		if (b) {
			elem.setAttribute(name, "");
		} else {
			elem.removeAttribute(name);
		}
	}

	/**
	 * Set controls to be displayed or not.
	 *
	 * @param b
	 *            to set
	 */
	public void setControls(boolean b) {
		switchAttribute("controls", b);
	}

	/**
	 * Set autoplay to be displayed or not.
	 *
	 * @param b
	 *            to set
	 */
	public void setAutoplay(boolean b) {
		switchAttribute("autoplay", b);
	}
}


package org.geogebra.web.full.main.video;

import org.geogebra.common.util.ExternalAccess;

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
public class HTML5VideoWidget extends Widget {
	private Element elem;
	private VideoListener listener;

	/**
	 * Listener to video element
	 *
	 */
	public interface VideoListener {

		/**
		 * Called when video is loaded
		 * 
		 * @param width
		 *            the original width of the video.
		 * @param height
		 *            the original height of the video.
		 */
		void onLoad(int width, int height);

		/**
		 * Called when something is went wrong.
		 */
		void onError();
	}
	
	/**
	 * Constructor
	 * 
	 * @param listener
	 *            Video listener.
	 */
	public HTML5VideoWidget(VideoListener listener) {
		this.listener = listener;
		elem = DOM.createElement("video");
		setElement(elem);
		addHandlers(elem);
	}

	private native void addHandlers(JavaScriptObject video) /*-{
		var that = this;
		video.oncanplaythrough = function() {
			that.@org.geogebra.web.full.main.video.HTML5VideoWidget::listenerOnLoad(II)(video.videoWidth, video.videoHeight);
		}

		video.onerror = function() {
			that.@org.geogebra.web.full.main.video.HTML5VideoWidget::listenerOnError()();
		}
	}-*/;

	/**
	 * Constructor
	 *
	 *
	 * @param src
	 *            source of the video
	 * @param listener
	 *            Video listener.
	 */
	public HTML5VideoWidget(String src, VideoListener listener) {
		this(listener);
		setSrc(src);
	}

	/**
	 * @param src
	 *            Source of the video.
	 */
	public void setSrc(String src) {
		elem.setAttribute("src", src);
		load(elem); // needed for Safari https://stackoverflow.com/a/49794011
	}

	private native void load(Element video) /*-{
		video.load();
	}-*/;

	/**
	 * Sets the width of the video
	 *
	 * @param width
	 *            to set.
	 */
	public void setWidth(int width) {
		if (width < 0) {
			return;
		}

		elem.setAttribute("width", width + "px");
	}

	/**
	 * Sets the height of the video
	 *
	 * @param height
	 *            to set.
	 */

	public void setHeight(int height) {
		if (height < 0) {
			return;
		}

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

	@ExternalAccess
	private void listenerOnLoad(int width, int height) {
		if (listener != null) {
			listener.onLoad(width, height);
		}
	}

	@ExternalAccess
	private void listenerOnError() {
		if (listener != null) {
			listener.onError();
		}
	}
}


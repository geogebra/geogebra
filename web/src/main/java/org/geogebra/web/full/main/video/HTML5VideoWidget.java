package org.geogebra.web.full.main.video;

import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.Widget;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLVideoElement;
import jsinterop.base.Js;

/**
 * Widget to wrap HTML5 video tag
 *
 * @author laszlo
 *
 */
public class HTML5VideoWidget extends Widget {
	private HTMLVideoElement elem;
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
		elem = (HTMLVideoElement) DomGlobal.document.createElement("video");
		setElement(Js.<Element>uncheckedCast(elem));
		addHandlers(elem);
	}

	private void addHandlers(HTMLVideoElement video) {
		video.oncanplaythrough = (evt) -> {
			listenerOnLoad(video.videoWidth, video.videoHeight);
			return null;
		};

		video.onerror = (evt) -> {
			listenerOnError();
			return null;
		};
	}

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
		elem.load(); // needed for Safari https://stackoverflow.com/a/49794011
	}

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
		elem.play();
	}

	/**
	 * Pause video tag
	 */
	public void pause() {
		elem.pause();
	}

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

	private void listenerOnLoad(int width, int height) {
		if (listener != null) {
			listener.onLoad(width, height);
		}
	}

	private void listenerOnError() {
		if (listener != null) {
			listener.onError();
		}
	}
}


package org.geogebra.web.html5.util;

import com.google.gwt.dom.client.Element;

/**
 * @author gabor
 * 
 *         Embedelement for embed elements
 *
 */
public class EmbedElement extends Element {

	static final String TAG = "embed";

	/**
	 * @param element
	 *            Assert, that the given {@link Element} is compatible with this
	 *            class and automatically typecast it.
	 * @return
	 */
	public static EmbedElement as(Element element) {
		// assert element.getTagName().equalsIgnoreCase(TAG);
		return (EmbedElement) element;
	}

	protected EmbedElement() {
	}

	/**
	 * @param msg
	 *            to pNaCl embed
	 */
	public native final void postMessage(String msg) /*-{
		this.postMessage(msg);
	}-*/;

}

package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.Image;

/**
 * Image that prevents dragging by default
 */
public class NoDragImage extends Image {

	/**
	 * @param uri
	 *            URL
	 * @param width
	 *            width in pixels
	 */
	public NoDragImage(String uri, int width) {
		this(uri);
		this.setWidth(width + "px");
	}

	/**
	 * @param uri
	 *            URI
	 */
	public NoDragImage(String uri) {
		super(uri);
		this.getElement().setAttribute("draggable", "false");
	}
}

package org.geogebra.web.html5.gui.util;

import com.google.gwt.user.client.ui.Image;

public class NoDragImage extends Image {

	public NoDragImage(String uri, int width) {
		this(uri);
		this.setWidth(width + "px");
	}

	public NoDragImage(String uri) {
		super(uri);
		this.getElement().setAttribute("draggable", "false");
	}

	public NoDragImage(String uri, Integer size, boolean isHeight) {
		this(uri);
		if (isHeight) {
			this.setHeight(size + "px");
		} else {
			this.setWidth(size + "px");
		}
	}
}

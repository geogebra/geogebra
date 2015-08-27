package org.geogebra.web.html5.gui;

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
}

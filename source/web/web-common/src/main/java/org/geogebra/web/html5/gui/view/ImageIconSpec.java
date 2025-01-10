package org.geogebra.web.html5.gui.view;

import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.client.Element;

public class ImageIconSpec implements IconSpec {
	private SVGResource image;

	public ImageIconSpec(SVGResource image) {
		this.image = image;
	}

	public SVGResource getImage() {
		return image;
	}

	@Override
	public Element toElement() {
		return new NoDragImage(getImage(), 24).getElement();
	}

	@Override
	public IconSpec withFill(String color) {
		image = image.withFill(color);
		return this;
	}
}

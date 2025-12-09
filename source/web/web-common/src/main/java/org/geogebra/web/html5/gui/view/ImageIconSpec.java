/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

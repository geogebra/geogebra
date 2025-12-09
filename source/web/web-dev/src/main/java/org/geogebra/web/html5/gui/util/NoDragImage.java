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

package org.geogebra.web.html5.gui.util;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ImageResource;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Image;

/**
 * Image that prevents dragging by default
 */
public class NoDragImage extends Image implements HasResource {

	/**
	 * @param uri
	 *            URL
	 * @param width
	 *            width in pixels
	 */
	public NoDragImage(String uri, int width) {
		this(uri);
		setWidth(width);
	}

	/**
	 * @param uri
	 *            URL
	 * @param width
	 *            in px
	 * @param height
	 *            in px
	 */
	public NoDragImage(ResourcePrototype uri, int width, int height) {
		this(safeURI(uri));
		setWidth(width);
		if (height > 0) {
			setHeight(height);
		}
	}

	/**
	 * @param uri
	 *            URL
	 * @param size
	 *            in px
	 */
	public NoDragImage(ResourcePrototype uri, int size) {
		this(uri, size, size);
	}

	/**
	 * @param uri
	 *            URI
	 */
	public NoDragImage(String uri) {
		super(uri);
		this.getElement().setAttribute("draggable", "false");
	}

	/**
	 * 
	 * @param res
	 *            SVG or PNG resource
	 * @return safe URI
	 */
	public static String safeURI(ResourcePrototype res) {
		if (res instanceof ImageResource) {
			return ((ImageResource) res).getSafeUri().asString();
		}
		if (res instanceof SVGResource) {
			return ((SVGResource) res).getSafeUri().asString();
		}
		return "";
	}

	public void setResource(ResourcePrototype res) {
		this.setUrl(NoDragImage.safeURI(res));
	}

	/**
	 * Sets the width of the image.
	 *
	 * @param width width in pixels
	 */
	public void setWidth(int width) {
		setWidth(width + "px");
	}

	/**
	 * Sets the height of the image.
	 *
	 * @param height height in pixels
	 */
	public void setHeight(int height) {
		setHeight(height + "px");
	}

	/**
	 * Mark image as presentation-only by setting empty alt.
	 * Preferable to aria role because of wider support.
	 */
	public void setPresentation() {
		getElement().setAttribute("alt", "");
	}
}

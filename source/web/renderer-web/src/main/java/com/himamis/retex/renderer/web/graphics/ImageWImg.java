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

package com.himamis.retex.renderer.web.graphics;

import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;

import elemental2.dom.HTMLImageElement;

public class ImageWImg implements Image {
	private final int width;
	private final int height;
	private final HTMLImageElement image;

	/**
	 * @param img image
	 * @param width width in pixels
	 * @param height height in pixels
	 */
	public ImageWImg(HTMLImageElement img, int width, int height) {
		this.image = img;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Graphics2DInterface createGraphics2D() {
		return null;
	}

	public HTMLImageElement getImage() {
		return image;
	}
}

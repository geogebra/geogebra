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

package org.geogebra.common.awt;

/**
 * Image that can be used as a canvas.
 */
public interface GBufferedImage {

	public int TYPE_INT_ARGB = 2;

	/**
	 * @return width in pixels
	 */
	int getWidth();

	/**
	 * @return height in pixels
	 */
	int getHeight();

	/**
	 * @return graphics object with the same dimensions as this image.
	 */
	GGraphics2D createGraphics();

	/**
	 * @param x left
	 * @param y top
	 * @param w width
	 * @param h height
	 * @return rectangular section of this image
	 */
	GBufferedImage getSubimage(int x, int y, int w, int h);

	/**
	 * Flush all related resources.
	 */
	void flush();

	/**
	 * 
	 * @return image as base64 PNG with "data:image/png;base64," prefix (or can
	 *         be null)
	 */
	String getBase64();

}

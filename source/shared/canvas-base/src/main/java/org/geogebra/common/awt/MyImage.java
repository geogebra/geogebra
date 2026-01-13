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
 * Wrapper for images that can be either bitmap or SVG
 * 
 * see MyImageD and MyImageW for implementations
 * 
 * @author michael
 *
 */
public interface MyImage {

	/**
	 * @return width in pixels
	 */
	int getWidth();

	/**
	 * @return height in pixels
	 */
	int getHeight();

	/**
	 * @return whether this is an SVG
	 */
	boolean isSVG();

	/**
	 * @return graphics objects for painting into this image
	 */
	GGraphics2D createGraphics();

	/**
	 * @return convert to base64
	 */
	String toLaTeXStringBase64();

	/**
	 * @return SVG content
	 */
	String getSVG();

	/**
	 * @return whether the platform-dependent implementation is valid
	 */
	boolean hasNonNullImplementation();

	/**
	 * Tries to tint the image with given color and return a result.
	 * Works for the preset SVGs that we use in buttons, may work with other SVGs.
	 *
	 * @param color color
	 * @param onLoad called when the tinted image is ready (only applies to Web)
	 * @return null if this image is not an SVG image (i.e., isSVG() returns false),
	 *     a tinted version of this SVG, or null if tinting for some reason failed.
	 */
	default MyImage tintedSVG(GColor color, Runnable onLoad) {
		return null;
	}
}

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

	int getWidth();

	int getHeight();

	boolean isSVG();

	GGraphics2D createGraphics();

	String toLaTeXStringBase64();

	/**
	 * Tries to tint the image with given color and return a result.
	 * Works for the preset SVGs that we use in buttons, may work with other SVGs.
	 *
	 * @param color color
	 * @param onLoad called when the tinted image is ready (only applies to Web)
	 * @return null if this image is not an SVG image (i.e., isSVG() returns false),
	 *     a tinted version of this SVG, or null if tinting for some reason failed.
	 */
	MyImage tintedSVG(GColor color, Runnable onLoad);
}

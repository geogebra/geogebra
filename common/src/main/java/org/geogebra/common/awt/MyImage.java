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
	 * Returns null on platforms that don't support SVGs.
	 * @param color color
	 * @param onLoad called when tinted image ready (needed in Web)
	 * @return tinted copy of this image if possible, null if not
	 */
	MyImage tintedSVG(GColor color, Runnable onLoad);
}

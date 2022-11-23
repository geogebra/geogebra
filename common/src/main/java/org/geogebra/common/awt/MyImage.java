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
	 * @param color color
	 * @param onLoad called when tinted image ready (needed in Web)
	 * @return tinted copy of this image
	 */
	default MyImage tint(GColor color, Runnable onLoad) {
		return this;
	}
}

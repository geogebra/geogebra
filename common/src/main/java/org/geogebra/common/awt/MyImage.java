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

}

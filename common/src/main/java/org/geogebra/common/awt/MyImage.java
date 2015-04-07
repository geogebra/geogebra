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

	GBufferedImage getSubimage(int x, int y, int width, int height);

	GGraphics2D createGraphics();

}

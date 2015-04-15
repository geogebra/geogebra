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

	void drawSubimage(int x, int y, int width, int height, GGraphics2D g, int posX, int posY);

	GGraphics2D createGraphics();

}

package org.geogebra.common.awt;

public interface GBufferedImage {

	public int TYPE_INT_ARGB = 2;

	int getWidth();

	int getHeight();

	GGraphics2D createGraphics();

	GBufferedImage getSubimage(int i, int j, int size, int size2);

}

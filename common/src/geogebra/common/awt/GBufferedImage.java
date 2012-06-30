package geogebra.common.awt;

public interface GBufferedImage {

	public int TYPE_INT_ARGB = 2;

	int getWidth();

	int getHeight();

	geogebra.common.awt.Graphics2D createGraphics();

}

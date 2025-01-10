package org.geogebra.common.awt;

public interface GBufferedImage {

	public int TYPE_INT_ARGB = 2;

	int getWidth();

	int getHeight();

	GGraphics2D createGraphics();

	GBufferedImage getSubimage(int x, int y, int w, int h);

	void flush();

	/**
	 * 
	 * @return image as base64 PNG with "data:image/png;base64," prefix (or can
	 *         be null)
	 */
	String getBase64();

}

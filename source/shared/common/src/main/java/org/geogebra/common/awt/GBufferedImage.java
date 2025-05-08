package org.geogebra.common.awt;

import org.geogebra.common.annotation.MissingDoc;

/**
 * Image that can be used as a canvas.
 */
public interface GBufferedImage {

	public int TYPE_INT_ARGB = 2;

	/**
	 * @return width in pixels
	 */
	int getWidth();

	/**
	 * @return height in pixels
	 */
	int getHeight();

	/**
	 * @return graphics object with the same dimensions as this image.
	 */
	GGraphics2D createGraphics();

	/**
	 * @param x left
	 * @param y top
	 * @param w width
	 * @param h height
	 * @return rectangular section of this image
	 */
	GBufferedImage getSubimage(int x, int y, int w, int h);

	@MissingDoc
	void flush();

	/**
	 * 
	 * @return image as base64 PNG with "data:image/png;base64," prefix (or can
	 *         be null)
	 */
	String getBase64();

}

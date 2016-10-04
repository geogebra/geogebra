package org.geogebra.desktop.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.geogebra.desktop.awt.GGraphics2DD;

public class ImageResizer {

	/**
	 * Resizes the image to the given size.
	 * 
	 * @param srcImage
	 *            the image to be resized
	 * @param width
	 *            width of the resized image
	 * @param height
	 *            height of the resized image
	 */
	public static BufferedImage resizeImage(BufferedImage srcImage, int width,
			int height) {
		BufferedImage resizedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImage.createGraphics();

		GGraphics2DD.setAntialiasing(g2);

		g2.drawImage(srcImage, 0, 0, width, height, null);

		g2.dispose();

		return resizedImage;
	}
}

/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
	 * @return resized image
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

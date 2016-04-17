package org.geogebra.desktop.awt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;

public class GBufferedImageD implements GBufferedImage {
	public int TYPE_INT_ARGB = BufferedImage.TYPE_INT_ARGB;
	private BufferedImage impl;

	public GBufferedImageD(int width, int height, int imageType) {
		impl = new BufferedImage(width, height, imageType);
	}

	public GBufferedImageD(BufferedImage image) {
		impl = image;
	}

	public int getWidth() {
		return impl.getWidth();
	}

	public int getHeight() {
		return impl.getHeight();
	}

	public static BufferedImage getAwtBufferedImage(
			GBufferedImage im) {
		if (im == null)
			return null;
		return ((GBufferedImageD) im).impl;
	}

	public GGraphics2D createGraphics() {
		return new GGraphics2DD(
(Graphics2D) impl.getGraphics());
	}

	public GBufferedImage getSubimage(int i, int j, int size, int size2) {
		return new GBufferedImageD(impl.getSubimage(i, j, size,
				size2));
	}

	/**
	 * 
	 * @return ARGB pixel data
	 */
	public int[] getData() {
		return ((DataBufferInt) impl.getRaster().getDataBuffer()).getData();
	}

}

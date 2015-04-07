package org.geogebra.desktop.awt;

import java.awt.image.DataBufferInt;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;

public class GBufferedImageD implements org.geogebra.common.awt.GBufferedImage {
	public int TYPE_INT_ARGB = java.awt.image.BufferedImage.TYPE_INT_ARGB;
	private java.awt.image.BufferedImage impl;

	public GBufferedImageD(int width, int height, int imageType) {
		impl = new java.awt.image.BufferedImage(width, height, imageType);
		// TODO Auto-generated constructor stub
	}

	public GBufferedImageD(java.awt.image.BufferedImage image) {
		impl = image;
	}

	public int getWidth() {
		return impl.getWidth();
	}

	public int getHeight() {
		return impl.getHeight();
	}

	public static java.awt.image.BufferedImage getAwtBufferedImage(
			org.geogebra.common.awt.GBufferedImage im) {
		if (im == null)
			return null;
		return ((GBufferedImageD) im).impl;
	}

	public GGraphics2D createGraphics() {
		return new org.geogebra.desktop.awt.GGraphics2DD(
				(java.awt.Graphics2D) impl.getGraphics());
	}

	public GBufferedImage getSubimage(int i, int j, int size, int size2) {
		return new org.geogebra.desktop.awt.GBufferedImageD(impl.getSubimage(i, j, size,
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

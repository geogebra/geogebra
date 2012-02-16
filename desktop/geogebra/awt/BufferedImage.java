package geogebra.awt;

import geogebra.common.awt.Graphics2D;

public class BufferedImage implements geogebra.common.awt.BufferedImage {
	public int TYPE_INT_ARGB = java.awt.image.BufferedImage.TYPE_INT_ARGB;
	private java.awt.image.BufferedImage impl;
	
	public BufferedImage(int width, int height, int imageType) {
		impl = new java.awt.image.BufferedImage(width, height, imageType);
		// TODO Auto-generated constructor stub
	}

	public BufferedImage(java.awt.image.BufferedImage image) {
		impl = image;
	}
	public int getWidth() {
		return impl.getWidth();
	}

	public int getHeight() {
		return impl.getHeight();
	}
	
	public static java.awt.image.BufferedImage getAwtBufferedImage(
			geogebra.common.awt.BufferedImage im){
		if(im==null)
			return null;
		return ((BufferedImage)im).impl;
	}

	public Graphics2D createGraphics() {
		return new geogebra.awt.Graphics2D((java.awt.Graphics2D)impl.getGraphics());
	}

	// this is important in the Web version
	public boolean isLoaded() {
		return true;
	}
}

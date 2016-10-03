package org.geogebra.desktop.awt;

import java.awt.Image;

import org.geogebra.common.awt.GImage;

public class GGenericImageD implements GImage {
	private Image impl;

	public GGenericImageD(Image im) {
		this.impl = im;
	}

	public static Image getAwtImage(GImage img) {
		if (img instanceof GGenericImageD)
			return ((GGenericImageD) img).impl;
		if (img instanceof GBufferedImageD)
			GBufferedImageD.getAwtBufferedImage((GBufferedImageD) img);
		return null;
	}

	public int getWidth() {
		return impl.getWidth(null);
	}

	public int getHeight() {
		return impl.getHeight(null);
	}
}

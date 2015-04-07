package org.geogebra.desktop.awt;

import java.awt.Image;

public class GGenericImageD implements org.geogebra.common.awt.GImage {
	private java.awt.Image impl;

	public GGenericImageD(Image im) {
		this.impl = im;
	}

	public static java.awt.Image getAwtImage(org.geogebra.common.awt.GImage img) {
		if (img instanceof GGenericImageD)
			return ((GGenericImageD) img).impl;
		if (img instanceof GBufferedImageD)
			GBufferedImageD.getAwtBufferedImage((GBufferedImageD) img);
		return null;
	}
}

package org.geogebra.desktop.awt;

import java.awt.Image;

import org.geogebra.common.awt.GImage;
import org.geogebra.common.awt.GImageObserver;

public class GGenericImageD implements GImage {
	private Image impl;

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

	public int getWidth(GImageObserver im) {
		return impl.getWidth(null);
	}

	public int getHeight(GImageObserver im) {
		return impl.getHeight(null);
	}
}

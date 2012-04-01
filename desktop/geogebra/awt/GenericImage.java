package geogebra.awt;

import java.awt.Image;

public class GenericImage implements geogebra.common.awt.Image {
	private java.awt.Image impl;

	public GenericImage(Image im) {
		this.impl = im;
	}
	
	public static java.awt.Image getAwtImage(geogebra.common.awt.Image img){
		if(img instanceof GenericImage)
			return ((GenericImage)img).impl;
		if(img instanceof BufferedImage)
			BufferedImage.getAwtBufferedImage((BufferedImage)img);
		return null;
	}
}

package geogebra.web.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.dom.client.Context2d;

import geogebra.common.awt.Graphics2D;
import geogebra.common.main.AbstractApplication;

public class BufferedImage implements geogebra.common.awt.BufferedImage {
	
	private geogebra.web.kernel.gawt.BufferedImage impl;
	
	public BufferedImage(int width, int height, int imageType) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType);
	}

	public BufferedImage(geogebra.common.awt.BufferedImage fillImage) {
	    impl = getGawtImage(fillImage);
    }

	public BufferedImage(ImageElement imageElement) {
	    impl = new geogebra.web.kernel.gawt.BufferedImage(imageElement);
    }

	public int getWidth() {
		return impl.getWidth();
	}

	
	public int getHeight() {
		return impl.getHeight();
	}

	public Graphics2D createGraphics() {

		Canvas cv = Canvas.createIfSupported();
		cv.setCoordinateSpaceWidth(impl.getWidth());
		cv.setCoordinateSpaceHeight(impl.getHeight());
		Context2d c2d = cv.getContext2d();
		c2d.drawImage(impl.getImageElement(),0,0);
		return new geogebra.web.awt.Graphics2D(cv);
    }

	public BufferedImage getSubimage(int xInt, int yInt, int xInt2, int yInt2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	public static geogebra.web.kernel.gawt.BufferedImage getGawtImage(geogebra.common.awt.BufferedImage img) {
		if(!(img instanceof BufferedImage))
			return null;
		return ((BufferedImage)img).impl;
	    
    }

}

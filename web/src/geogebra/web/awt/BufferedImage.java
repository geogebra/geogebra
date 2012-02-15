package geogebra.web.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.dom.client.Context2d;

import geogebra.common.awt.Graphics2D;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

public class BufferedImage implements geogebra.common.awt.BufferedImage {

	// impl can be null here, but impl.img cannot be null
	private geogebra.web.kernel.gawt.BufferedImage impl = null;

	public BufferedImage(int width, int height, int imageType, boolean opaque) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType, opaque);
	}

	public BufferedImage(int width, int height, int imageType) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType);
	}

	// this constructor clones the fillImage
	public BufferedImage(geogebra.common.awt.BufferedImage fillImage) {
	    impl = getGawtImage(fillImage);
	    if (impl != null)
		    impl = impl.cloneDeep();
	    else
			Application.debug("BufferedImage (web) called with empty BufferedImage (web)");
    }

	// this constructor wraps the imageElement
	public BufferedImage(ImageElement imageElement) {
		if (imageElement != null)
			impl = new geogebra.web.kernel.gawt.BufferedImage(imageElement);
		else
			Application.debug("BufferedImage (web) called with null ImageElement");
    }

	public int getWidth() {
		if (impl == null)
			return 0;
		return impl.getWidth();
	}

	public int getHeight() {
		if (impl == null)
			return 0;
		return impl.getHeight();
	}

	public Graphics2D createGraphics() {
		return new geogebra.web.awt.Graphics2D(impl.getCanvas());
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

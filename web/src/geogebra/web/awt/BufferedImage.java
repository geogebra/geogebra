package geogebra.web.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;

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

	public BufferedImage(ImageData imageData) {
	    impl = new geogebra.web.kernel.gawt.BufferedImage(imageData);
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
	    Context2d ctx = impl.getCanvas().getContext2d(); // TODO Auto-generated
	    ImageData imageData = ctx.getImageData(xInt, yInt, xInt2, yInt2);
	    return new BufferedImage(imageData);
    }

	public static geogebra.web.kernel.gawt.BufferedImage getGawtImage(geogebra.common.awt.BufferedImage img) {
		if(!(img instanceof BufferedImage))
			return null;
		return ((BufferedImage)img).impl;
    }

	public boolean isLoaded() {
		return impl.isLoaded();
	}
	
	public ImageElement getImageElement() {
		return impl.getImageElement();
	}

	public CanvasElement getCanvasElement() {
	    return impl.getCanvas().getCanvasElement();
    }
}

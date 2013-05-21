package geogebra.html5.awt;

import geogebra.common.awt.GGraphics2D;
import geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;

public class GBufferedImageW implements geogebra.common.awt.GBufferedImage {

	// impl can be null here, but impl.img cannot be null
	private geogebra.html5.gawt.BufferedImage impl = null;

	public GBufferedImageW(int width, int height, int imageType, boolean opaque) {
		impl = new geogebra.html5.gawt.BufferedImage(width,height,imageType, opaque);
	}

	public GBufferedImageW(int width, int height, int imageType) {
		impl = new geogebra.html5.gawt.BufferedImage(width,height,imageType);
	}

	public GBufferedImageW(Canvas canvas, int width, int height, int imageType, boolean opaque) {
		impl = new geogebra.html5.gawt.BufferedImage(canvas,width,height,imageType,opaque);
	}

	// this constructor clones the fillImage
	public GBufferedImageW(geogebra.common.awt.GBufferedImage fillImage) {
	    impl = getGawtImage(fillImage);
	    if (impl != null)
		    impl = impl.cloneDeep();
	    else
			App.debug("BufferedImage (web) called with empty BufferedImage (web)");
    }

	// this constructor wraps the imageElement
	public GBufferedImageW(ImageElement imageElement) {
		if (imageElement != null)
			impl = new geogebra.html5.gawt.BufferedImage(imageElement);
		else
			App.debug("BufferedImage (web) called with null ImageElement");
    }

	public GBufferedImageW(ImageData imageData) {
	    impl = new geogebra.html5.gawt.BufferedImage(imageData);
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

	public GGraphics2D createGraphics() {
		return new geogebra.html5.awt.GGraphics2DW(impl.getCanvas());
    }

	public GBufferedImageW getSubimage(int xInt, int yInt, int xInt2, int yInt2) {
	    Context2d ctx = impl.getCanvas().getContext2d(); // TODO Auto-generated
	    ImageData imageData = ctx.getImageData(xInt, yInt, xInt2, yInt2);
	    return new GBufferedImageW(imageData);
    }

	public static geogebra.html5.gawt.BufferedImage getGawtImage(geogebra.common.awt.GBufferedImage img) {
		if(!(img instanceof GBufferedImageW))
			return null;
		return ((GBufferedImageW)img).impl;
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
	
	public Canvas getCanvas() {
		return impl.getCanvas();
	}
}

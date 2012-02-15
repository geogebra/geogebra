package geogebra.web.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.dom.client.Context2d;

import geogebra.common.awt.Graphics2D;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

public class BufferedImage implements geogebra.common.awt.BufferedImage {
	
	private geogebra.web.kernel.gawt.BufferedImage impl;

	private Canvas impl2 = null; // this is used when drawn on the BufferedImage by a Graphics2D

	public BufferedImage(int width, int height, int imageType, boolean opaque) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType, opaque);
	}

	public BufferedImage(int width, int height, int imageType) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType);
	}

	public BufferedImage(geogebra.common.awt.BufferedImage fillImage) {
	    impl = getGawtImage(fillImage);
	    if (impl == null) {
			Application.debug("BufferedImage (web) called with empty BufferedImage (web)");
	    	return;
	    }
	    impl = impl.cloneDeep();
    }

	public BufferedImage(ImageElement imageElement) {
		if (imageElement != null) {
			impl = new geogebra.web.kernel.gawt.BufferedImage(imageElement);
		} else {
			impl = null;
			Application.debug("BufferedImage (web) called with null ImageElement");
		}
    }

	private void syncImplementations() {
		if (impl2 == null)
			return;
		impl = new geogebra.web.kernel.gawt.BufferedImage(impl2);
	}

	public int getWidth() {
		// because impl2 and impl are always the same size, syncing is not needed here
		//syncImplementations();
		if (impl == null)
			return 0;
		return impl.getWidth();
	}

	public int getHeight() {
		// because impl2 and impl are always the same size, syncing is not needed here
		//syncImplementations();
		if (impl == null)
			return 0;
		return impl.getHeight();
	}

	public Graphics2D createGraphics() {
		return new geogebra.web.awt.Graphics2D(getCanvas(this));
    }

	public BufferedImage getSubimage(int xInt, int yInt, int xInt2, int yInt2) {
	    AbstractApplication.debug("implementation needed"); // TODO Auto-generated
	    return null;
    }

	public static geogebra.web.kernel.gawt.BufferedImage getGawtImage(geogebra.common.awt.BufferedImage img) {
		if(!(img instanceof BufferedImage))
			return null;
		((BufferedImage)img).syncImplementations(); // impl defined!
		return ((BufferedImage)img).impl;
    }

	public static Canvas getCanvas(geogebra.common.awt.BufferedImage img) {
		if(!(img instanceof BufferedImage))
			return null;

		if (((BufferedImage)img).impl2 == null) {
			Canvas cv = Canvas.createIfSupported();
			cv.setCoordinateSpaceWidth(((BufferedImage)img).getWidth());
			cv.setCoordinateSpaceHeight(((BufferedImage)img).getHeight());
			Context2d c2d = cv.getContext2d();
			if (((BufferedImage)img).impl != null)
				c2d.drawImage(((BufferedImage)img).impl.getImageElement(),0,0);
			((BufferedImage)img).impl2 = cv;
		}
		return ((BufferedImage)img).impl2;
	}
}

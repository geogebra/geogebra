package geogebra.web.awt;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.dom.client.Context2d;

import geogebra.common.awt.Graphics2D;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

public class BufferedImage implements geogebra.common.awt.BufferedImage {
	
	private geogebra.web.kernel.gawt.BufferedImage impl = null;

	private Canvas impl2 = null; // this is used when drawn on the BufferedImage by a Graphics2D

	public BufferedImage(int width, int height, int imageType, boolean opaque) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType, opaque);
	}

	public BufferedImage(int width, int height, int imageType) {
		impl = new geogebra.web.kernel.gawt.BufferedImage(width,height,imageType);
	}

	public BufferedImage(geogebra.common.awt.BufferedImage fillImage) {
		impl2 = getCanvasImage(fillImage);
	    impl = getGawtImage(fillImage);

	    if (impl == null)
			Application.debug("BufferedImage (web) called with empty BufferedImage (web)");
    }

	public BufferedImage(ImageElement imageElement) {
		if (imageElement != null)
			impl = new geogebra.web.kernel.gawt.BufferedImage(imageElement);
		else
			Application.debug("BufferedImage (web) called with null ImageElement");
    }

	public BufferedImage(Canvas cv) {
		if (cv != null) {
			impl2 = cv;
			impl = new geogebra.web.kernel.gawt.BufferedImage(cv);
		} else {
			Application.debug("BufferedImage (web) called with null Canvas");
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
		return impl.getWidth();
	}

	
	public int getHeight() {
		// because impl2 and impl are always the same size, syncing is not needed here
		//syncImplementations();
		return impl.getHeight();
	}

	public Graphics2D createGraphics() {
		if (impl2 == null) {
			Canvas cv = Canvas.createIfSupported();
			cv.setCoordinateSpaceWidth(impl.getWidth());
			cv.setCoordinateSpaceHeight(impl.getHeight());
			Context2d c2d = cv.getContext2d();
			c2d.drawImage(impl.getImageElement(),0,0);
			impl2 = cv;
		}
		return new geogebra.web.awt.Graphics2D(impl2);
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

	public static Canvas getCanvasImage(geogebra.common.awt.BufferedImage img) {
		if(!(img instanceof BufferedImage))
			return null;
		return ((BufferedImage)img).impl2;
	}
}

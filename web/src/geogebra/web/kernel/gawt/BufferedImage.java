package geogebra.web.kernel.gawt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

public class BufferedImage {
	
	ImageElement img = null;

	public BufferedImage(int width, int height, int imageType, boolean opaque) {

		img = ImageElement.as(DOM.createImg());
		img.setWidth(width);
		img.setHeight(height);

		CanvasElement nc = CanvasElement.createObject().cast();
		nc.setWidth(width);
		nc.setHeight(height);

		/* Problem: how to create transparent/opaque canvas

		Canvas nc = Canvas.createIfSupported();
		nc.setCoordinateSpaceWidth(width);
		nc.setCoordinateSpaceHeight(height);

		if (opaque) {
			Context2d c2d = nc.getContext2d();
			c2d.setStrokeStyle("rgba(255,255,255,1.0)");
			c2d.setFillStyle("rgba(255,255,255,1.0)");
			c2d.fillRect(0, 0, width, height);
		} else {
			Context2d c2d = nc.getContext2d();
			c2d.setStrokeStyle("rgba(255,255,255,0.0)");
			c2d.setFillStyle("rgba(255,255,255,0.0)");
			c2d.setGlobalCompositeOperation(Context2d.Composite.COPY);
			//c2d.setGlobalAlpha(0.0);
			c2d.fillRect(0, 0, width, height);
			c2d.setGlobalCompositeOperation(Context2d.Composite.SOURCE_OVER);
		}
		*/

		img.setSrc(nc.toDataUrl());
	}

	public BufferedImage(int width, int height, int imageType) {
		this(width, height, imageType, false);
    }

	public BufferedImage(ImageElement imageElement) {
	    img = imageElement;
    }

	public int getWidth() {
	   return img.getWidth();
    }
	
	public int getHeight() {
		return img.getHeight();
	}

	public ImageElement getImageElement() {
	   return img;
    }
	
	
}

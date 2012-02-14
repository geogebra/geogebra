package geogebra.web.kernel.gawt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

import geogebra.web.main.Application;

public class BufferedImage {
	
	ImageElement img = null;

	public BufferedImage(int width, int height, int imageType, boolean opaque) {

		img = ImageElement.as(DOM.createImg());
		img.setWidth(width);
		img.setHeight(height);

		
		/*if (opaque) {
			Canvas nc = Canvas.createIfSupported();
			nc.setCoordinateSpaceWidth(width);
			nc.setCoordinateSpaceHeight(height);

			Context2d c2d = nc.getContext2d();
			com.google.gwt.canvas.dom.client.FillStrokeStyle fss = c2d.getStrokeStyle();
			com.google.gwt.canvas.dom.client.FillStrokeStyle fsf = c2d.getFillStyle();
			c2d.setStrokeStyle("rgba(255,255,255,1.0)");
			c2d.setFillStyle("rgba(255,255,255,1.0)");
			c2d.fillRect(0, 0, width, height);
			c2d.setStrokeStyle(fss);
			c2d.setFillStyle(fsf);
			img.setSrc(nc.toDataUrl());
		} else {*/
			CanvasElement nc = CanvasElement.createObject().cast();
			nc.setWidth(width);
			nc.setHeight(height);

			img.setSrc(nc.toDataUrl());
		/*}*/
	}

	public BufferedImage(int width, int height, int imageType) {
		this(width, height, imageType, false);
    }

	public BufferedImage(ImageElement imageElement) {
		if (imageElement != null)// This should not called with null 
			img = imageElement;
		else
			Application.debug("BufferedImage (gawt) called with null");
    }

	public BufferedImage(Canvas cv) {
		if (cv != null) {// This should not called with null
			img = ImageElement.as(DOM.createImg());
			img.setWidth(cv.getCoordinateSpaceWidth());
			img.setHeight(cv.getCoordinateSpaceHeight());
			img.setSrc(cv.toDataUrl());
		} else {
			Application.debug("BufferedImage (gawt) called with null");
		}
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

	public BufferedImage cloneDeep() {
		return new BufferedImage((ImageElement)img.cloneNode(true));
	}
}

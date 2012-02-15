package geogebra.web.kernel.gawt;

import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

import geogebra.web.main.Application;

public class BufferedImage {

	ImageElement img = null; // necessary

	Canvas canv = null; // not necessary, but if present, this is the main one

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

	// this clones this bufferedimage!
	public BufferedImage(Canvas cv) {
		if (cv != null) {// This should not called with null
			canv = Canvas.createIfSupported();
			canv.setCoordinateSpaceWidth(cv.getCoordinateSpaceWidth());
			canv.setCoordinateSpaceHeight(cv.getCoordinateSpaceHeight());
			Context2d c2d = canv.getContext2d();
			c2d.putImageData(
					cv.getContext2d().getImageData(0, 0,
							cv.getCoordinateSpaceWidth(),
							cv.getCoordinateSpaceHeight()),
					0,0);
			img = getImageElement();
		} else {
			Application.debug("BufferedImage (gawt) called with null Canvas");
		}
	}

	public int getWidth() {
		if (canv == null)
			return img.getWidth();
		else
			return canv.getCoordinateSpaceWidth();
    }

	public int getHeight() {
		if (canv == null)
			return img.getHeight();
		else
			return canv.getCoordinateSpaceHeight();
	}

	public ImageElement getImageElement() {
		if (canv != null) {
			img = ImageElement.as(DOM.createImg());
			img.setWidth(canv.getCoordinateSpaceWidth());
			img.setHeight(canv.getCoordinateSpaceHeight());
			img.setSrc(canv.toDataUrl());
		}
		return img;
    }

	public BufferedImage cloneDeep() {
		if (canv != null) {
			return new BufferedImage(canv);
		} else {
			return new BufferedImage((ImageElement)img.cloneNode(true));
		}
	}

	public Canvas getCanvas() {
		if (canv == null) {
			canv = Canvas.createIfSupported();
			canv.setCoordinateSpaceWidth(getWidth());
			canv.setCoordinateSpaceHeight(getHeight());
			Context2d c2d = canv.getContext2d();
			c2d.drawImage(img,0,0);
		}
		return canv;
	}
}

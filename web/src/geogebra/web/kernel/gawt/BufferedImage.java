package geogebra.web.kernel.gawt;

import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

public class BufferedImage {

	ImageElement img = null; // necessary

	Canvas canv = null; // not necessary, but if present, this is the main one

	public BufferedImage(int width, int height, int imageType, boolean opaque) {
		this(null, width, height, imageType, opaque);
	}

	public BufferedImage(Canvas canvas, int width, int height, int imageType, boolean opaque) {

		if (canvas == null)
			canv = Canvas.createIfSupported();
		else
			canv = canvas;

		canv.setCoordinateSpaceWidth(width);
		canv.setCoordinateSpaceHeight(height);
		canv.setWidth(width+"px");
		canv.setHeight(height+"px");

		if (opaque) {
			Context2d c2d = canv.getContext2d();
			//com.google.gwt.canvas.dom.client.FillStrokeStyle fss = c2d.getStrokeStyle();
			//com.google.gwt.canvas.dom.client.FillStrokeStyle fsf = c2d.getFillStyle();
			c2d.setGlobalCompositeOperation(Context2d.Composite.COPY);
			c2d.setStrokeStyle(CssColor.make("rgba(255,255,255,1.0)"));
			c2d.setFillStyle(CssColor.make("rgba(255,255,255,1.0)"));
			c2d.fillRect(0, 0, width, height);
			c2d.fill();
			//c2d.setStrokeStyle(fss);
			//c2d.setFillStyle(fsf);
		}

		img = getImageElement();
	}

	public BufferedImage(int width, int height, int imageType) {
		this(width, height, imageType, false);
    }

	public BufferedImage(ImageElement imageElement) {
		if (imageElement != null)// This should not called with null 
			img = imageElement;
		else
			AppW.debug("BufferedImage (gawt) called with null");
    }

	// this clones this bufferedimage!
	public BufferedImage(Canvas cv) {
		if (cv != null) {// This should not called with null
			canv = Canvas.createIfSupported();
			canv.setCoordinateSpaceWidth(cv.getCoordinateSpaceWidth());
			canv.setCoordinateSpaceHeight(cv.getCoordinateSpaceHeight());
			canv.setWidth(cv.getCanvasElement().getWidth()+"px");
			canv.setHeight(cv.getCanvasElement().getHeight()+"px");
			Context2d c2d = canv.getContext2d();
			c2d.putImageData(
					cv.getContext2d().getImageData(0, 0,
							cv.getCoordinateSpaceWidth(),
							cv.getCoordinateSpaceHeight()),
					0,0);
			img = getImageElement();
		} else {
			AppW.debug("BufferedImage (gawt) called with null Canvas");
		}
	}

	public BufferedImage(ImageData imageData) {
		canv = Canvas.createIfSupported();
		canv.setCoordinateSpaceWidth(imageData.getWidth());
		canv.setCoordinateSpaceHeight(imageData.getHeight());
		canv.setWidth(imageData.getWidth()+"px");
		canv.setHeight(imageData.getHeight()+"px");
		canv.getContext2d().putImageData(imageData, 0, 0);
		img = getImageElement();
    }

	public int getWidth() {
		if (canv == null)
			return img.getWidth();
		return canv.getCoordinateSpaceWidth();
		// programmers should make sure that
		// canv.getCoordinateSpaceWidth() == canv.getCanvasElement().getWidth() 
    }

	public int getHeight() {
		if (canv == null)
			return img.getHeight();
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
		if (canv != null)
			return new BufferedImage(canv);
		return new BufferedImage((ImageElement)img.cloneNode(true));
	}

	public Canvas getCanvas() {
		if (canv == null) {
			canv = Canvas.createIfSupported();
			canv.setCoordinateSpaceWidth(getWidth());
			canv.setCoordinateSpaceHeight(getHeight());
			canv.setWidth(getWidth()+"px");
			canv.setHeight(getWidth()+"px");
			Context2d c2d = canv.getContext2d();
			c2d.drawImage(img,0,0);
		}
		return canv;
	}

	public boolean isLoaded() {
		return img.getPropertyBoolean("complete");
	}
}

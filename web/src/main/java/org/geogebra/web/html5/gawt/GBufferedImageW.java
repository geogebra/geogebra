package org.geogebra.web.html5.gawt;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.main.App;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

public class GBufferedImageW implements org.geogebra.common.awt.GBufferedImage {

	ImageElement img = null; // necessary

	Canvas canv = null; // not necessary, but if present, this is the main one

	public GBufferedImageW(int width, int height, int imageType, boolean opaque) {
		this(null, width, height, imageType, opaque);
	}

	public GBufferedImageW(Canvas canvas, int width, int height, int imageType,
	        boolean opaque) {

		if (canvas == null)
			canv = makeCanvas();
		else
			canv = canvas;

		canv.setCoordinateSpaceWidth(width);
		canv.setCoordinateSpaceHeight(height);
		canv.setWidth(width + "px");
		canv.setHeight(height + "px");

		if (opaque) {
			Context2d c2d = canv.getContext2d();
			// com.google.gwt.canvas.dom.client.FillStrokeStyle fss =
			// c2d.getStrokeStyle();
			// com.google.gwt.canvas.dom.client.FillStrokeStyle fsf =
			// c2d.getFillStyle();
			c2d.setGlobalCompositeOperation(Context2d.Composite.COPY);
			c2d.setStrokeStyle(CssColor.make("rgba(255,255,255,1.0)"));
			c2d.setFillStyle(CssColor.make("rgba(255,255,255,1.0)"));
			c2d.fillRect(0, 0, width, height);
			c2d.fill();
			// c2d.setStrokeStyle(fss);
			// c2d.setFillStyle(fsf);
		}

		// img = getImageElement();
	}

	public GBufferedImageW(int width, int height, int imageType) {
		this(width, height, imageType, false);
	}

	public GBufferedImageW(ImageElement imageElement) {
		if (imageElement != null)// This should not called with null
			img = imageElement;
		else
			App.debug("BufferedImage (gawt) called with null");
	}

	// this clones this bufferedimage!
	public GBufferedImageW(Canvas cv) {
		if (cv != null) {// This should not called with null
			canv = makeCanvas();
			canv.setCoordinateSpaceWidth(cv.getCoordinateSpaceWidth());
			canv.setCoordinateSpaceHeight(cv.getCoordinateSpaceHeight());
			canv.setWidth(cv.getCanvasElement().getWidth() + "px");
			canv.setHeight(cv.getCanvasElement().getHeight() + "px");
			Context2d c2d = canv.getContext2d();
			c2d.putImageData(
			        cv.getContext2d().getImageData(0, 0,
			                cv.getCoordinateSpaceWidth(),
			                cv.getCoordinateSpaceHeight()), 0, 0);
			// img = getImageElement();
		} else {
			App.debug("BufferedImage (gawt) called with null Canvas");
		}
	}

	public GBufferedImageW(ImageData imageData) {
		canv = makeCanvas();
		canv.setCoordinateSpaceWidth(imageData.getWidth());
		canv.setCoordinateSpaceHeight(imageData.getHeight());
		canv.setWidth(imageData.getWidth() + "px");
		canv.setHeight(imageData.getHeight() + "px");
		canv.getContext2d().putImageData(imageData, 0, 0);
		// img = getImageElement();
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

	public boolean hasCanvas() {
		return canv != null;
	}

	public GBufferedImageW cloneDeep() {
		if (canv != null)
			return new GBufferedImageW(canv);
		return new GBufferedImageW((ImageElement) img.cloneNode(true));
	}

	public Canvas getCanvas() {
		if (canv == null) {
			canv = makeCanvas();
			canv.setCoordinateSpaceWidth(img.getWidth());
			canv.setCoordinateSpaceHeight(img.getHeight());
			canv.setWidth(getWidth() + "px");
			canv.setHeight(getWidth() + "px");
			Context2d c2d = canv.getContext2d();
			c2d.drawImage(img, 0, 0);
		}
		return canv;
	}

	private Canvas makeCanvas() {
		return Canvas.createIfSupported();
	}

	public boolean isLoaded() {
		return img == null || img.getPropertyBoolean("complete");
	}

	@Override
	public GGraphics2D createGraphics() {
		return new org.geogebra.web.html5.awt.GGraphics2DW(getCanvas(), true);
	}

	@Override
	public GBufferedImage getSubimage(int x, int y, int width, int height) {
		Context2d ctx = getCanvas().getContext2d();
		ImageData imageData = ctx.getImageData(x, y, width, height);
		return new GBufferedImageW(imageData);
	}

	public ImageData getImageData() {
		return getCanvas().getContext2d().getImageData(0, 0, getWidth(),
		        getHeight());
	}
}

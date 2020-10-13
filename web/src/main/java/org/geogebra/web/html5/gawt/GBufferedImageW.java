package org.geogebra.web.html5.gawt;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;

/**
 * Image that can be used for drawing.
 */
public class GBufferedImageW implements GBufferedImage {
	/** img element */
	ImageElement img = null; // necessary

	/** Canvas */
	Canvas canv = null; // not necessary, but if present, this is the main one

	private double pixelRatio;

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param pixelRatio
	 *            pixel ratio
	 * @param opaque
	 *            whether to use white background
	 */
	public GBufferedImageW(int width, int height, double pixelRatio,
			boolean opaque) {
		this(null, width, height, pixelRatio, opaque);
	}

	/**
	 * @param canvas
	 *            canvas
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param pixelRatio
	 *            CSS pixel ratio
	 * @param opaque
	 *            whether white background should be used
	 */
	public GBufferedImageW(Canvas canvas, int width, int height,
			double pixelRatio, boolean opaque) {
		this.pixelRatio = pixelRatio;

		if (canvas == null) {
			canv = makeCanvas();
		} else {
			canv = canvas;
		}
		if (canv != null) {
			canv.setCoordinateSpaceWidth((int) (width * pixelRatio));
			canv.setCoordinateSpaceHeight((int) (height * pixelRatio));
			canv.setWidth(width + "px");
			canv.setHeight(height + "px");
			Context2d c2d = canv.getContext2d();
			if (opaque) {
				c2d.setGlobalCompositeOperation(Context2d.Composite.COPY);
				c2d.setStrokeStyle(CssColor.make("rgba(255,255,255,1.0)"));
				c2d.setFillStyle(CssColor.make("rgba(255,255,255,1.0)"));
				c2d.fillRect(0, 0, width, height);
			}
			if (pixelRatio != 1) {
				c2d.scale(pixelRatio, pixelRatio);
			}
		}
		// img = getImageElement();
	}

	/**
	 * New image using with a blank canvas
	 * 
	 * @param width
	 *            canvas width
	 * @param height
	 *            canvas height
	 * @param pixelRatio
	 *            CSS pixel ratio
	 */
	public GBufferedImageW(int width, int height, double pixelRatio) {
		this(width, height, pixelRatio, false);
	}

	/**
	 * @param imageElement
	 *            img element
	 */
	public GBufferedImageW(ImageElement imageElement) {
		if (imageElement != null) {
			img = imageElement;
		} else {
			Log.debug("BufferedImage (gawt) called with null");
		}
	}

	/**
	 * New image using clone of the canvas
	 * 
	 * @param cv
	 *            canvas with original image
	 */
	public GBufferedImageW(Canvas cv) {
		if (cv != null) { // This should not be called with null
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
			Log.debug("BufferedImage (gawt) called with null Canvas");
		}
	}

	/**
	 * @param imageData
	 *            image data
	 */
	public GBufferedImageW(ImageData imageData) {
		canv = makeCanvas();
		canv.setCoordinateSpaceWidth(imageData.getWidth());
		canv.setCoordinateSpaceHeight(imageData.getHeight());
		canv.setWidth(imageData.getWidth() + "px");
		canv.setHeight(imageData.getHeight() + "px");
		canv.getContext2d().putImageData(imageData, 0, 0);
		// img = getImageElement();
	}

	@Override
	public int getWidth() {
		if (canv == null) {
			return img.getWidth();
		}
		return canv.getCoordinateSpaceWidth();
		// programmers should make sure that
		// canv.getCoordinateSpaceWidth() == canv.getCanvasElement().getWidth()
	}

	@Override
	public int getHeight() {
		if (canv == null) {
			return img.getHeight();
		}
		return canv.getCoordinateSpaceHeight();
	}

	/**
	 * Return content as image, convert canvas to img if necessary.
	 * 
	 * @return image element
	 */
	public ImageElement getImageElement() {
		if (canv != null) {
			img = ImageElement.as(DOM.createImg());
			img.setWidth(canv.getCoordinateSpaceWidth());
			img.setHeight(canv.getCoordinateSpaceHeight());
			img.setSrc(canv.toDataUrl());
		}
		return img;
	}

	/**
	 * @return whether canvas is initialized
	 */
	public boolean hasCanvas() {
		return canv != null;
	}

	/**
	 * @return create a clone using a new canvas / img element
	 */
	public GBufferedImageW cloneDeep() {
		if (canv != null) {
			return new GBufferedImageW(canv);
		}
		return new GBufferedImageW((ImageElement) img.cloneNode(true));
	}

	/**
	 * @return canvas
	 */
	public Canvas getCanvas() {
		if (canv == null) {
			canv = makeCanvas();
			if (canv != null) {
				canv.setCoordinateSpaceWidth(img.getWidth());
				canv.setCoordinateSpaceHeight(img.getHeight());
				canv.setWidth(getWidth() + "px");
				canv.setHeight(getWidth() + "px");
				Context2d c2d = canv.getContext2d();
				c2d.drawImage(img, 0, 0);
			}
		}
		return canv;
	}

	private static Canvas makeCanvas() {
		return Canvas.createIfSupported();
	}

	/**
	 * @return whether this is canvas or loaded img
	 */
	public boolean isLoaded() {
		return img == null || img.getPropertyBoolean("complete");
	}

	@Override
	public GGraphics2D createGraphics() {
		GGraphics2DW g2 = new GGraphics2DW(getCanvas(), true);
		g2.setDevicePixelRatio(this.pixelRatio);
		return g2;
	}

	@Override
	public GBufferedImage getSubimage(int x, int y, int width, int height) {
		Context2d ctx = getCanvas().getContext2d();
		ImageData imageData = ctx.getImageData(x, y, width, height);
		return new GBufferedImageW(imageData);
	}

	/**
	 * @return image data of the canvas
	 */
	public ImageData getImageData() {
		return getCanvas().getContext2d().getImageData(0, 0, getWidth(),
		        getHeight());
	}

	@Override
	public void flush() {
		// nothing to flush
	}

	@Override
	public String getBase64() {
		if (img == null) {
			Log.error("img null");
			return null;
		}
		return img.getSrc();
	}

	/**
	 * @return pixel ratio of this image
	 */
	public double getPixelRatio() {
		return pixelRatio;
	}

}

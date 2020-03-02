package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;

/**
 * HTML5 implementation of image: wrapper around Image element
 *
 */
public final class MyImageW implements MyImage {

	private ImageElement img;
	private Canvas canv;
	private int width = 0;
	private int height = 0;
	private boolean svg;

	/**
	 * @param im
	 *            image element
	 * @param isSVG
	 *            whether this is a SVG
	 */
	public MyImageW(ImageElement im, boolean isSVG) {
		this.img = im;
		this.svg = isSVG;

		width = img.getWidth();
		height = img.getHeight();
		if (width == 0 || height == 0) {
			// hack for IE10/11/12
			// can't work out SVG height unless it's attached to the DOM
			Document.get().getBody().appendChild(img);
			width = img.getOffsetWidth();
			height = img.getOffsetHeight();
			Document.get().getBody().removeChild(img);
		}
	}

	@Override
	public int getWidth() {
		if (width == 0) {
			width = img.getWidth();
		}
		return width;
	}

	@Override
	public int getHeight() {
		if (height == 0) {
			height = img.getHeight();
		}
		return height;
	}

	@Override
	public boolean isSVG() {
		return svg;
	}

	private Canvas getCanvas() {
		if (canv == null) {
			canv = Canvas.createIfSupported();
			canv.setCoordinateSpaceWidth(img.getWidth());
			canv.setCoordinateSpaceHeight(img.getHeight());
			canv.setWidth(getWidth() + "px");
			canv.setHeight(getHeight() + "px");
			Context2d c2d = canv.getContext2d();
			c2d.drawImage(img, 0, 0);
		}
		return canv;
	}

	@Override
	public GGraphics2D createGraphics() {
		return new GGraphics2DW(getCanvas(), true);
	}

	/**
	 * @return image element
	 */
	public ImageElement getImage() {
		return img;
	}

	@Override
	public String toLaTeXStringBase64() {
		return "\\imagebasesixtyfour{" + getWidth() + "}{" + getHeight() + "}{"
				+ img.getSrc() + "}";
	}
}

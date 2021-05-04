package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.client.Canvas;

import elemental2.dom.CSSProperties;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

/**
 * HTML5 implementation of image: wrapper around Image element
 *
 */
public final class MyImageW implements MyImage {

	private HTMLImageElement img;
	private HTMLCanvasElement canv;
	private int width;
	private int height;
	private boolean svg;

	/**
	 * @param im
	 *            image element
	 * @param isSVG
	 *            whether this is a SVG
	 */
	public MyImageW(HTMLImageElement im, boolean isSVG) {
		this.img = im;
		this.svg = isSVG;

		width = img.width;
		height = img.height;
		if (width == 0 || height == 0) {
			// hack for IE10/11/12
			// can't work out SVG height unless it's attached to the DOM
			DomGlobal.document.body.append(img);
			width = img.offsetWidth;
			height = img.offsetHeight;
			DomGlobal.document.body.append(img);
		}
	}

	@Override
	public int getWidth() {
		if (width == 0) {
			width = img.width;
		}
		return width;
	}

	@Override
	public int getHeight() {
		if (height == 0) {
			height = img.height;
		}
		return height;
	}

	@Override
	public boolean isSVG() {
		return svg;
	}

	private HTMLCanvasElement getCanvas() {
		if (canv == null) {
			canv = Js.uncheckedCast(DomGlobal.document.createElement("canvas"));
			canv.width = img.width;
			canv.height = img.height;
			canv.style.width = CSSProperties.WidthUnionType.of(getWidth() + "px");
			canv.style.height = CSSProperties.HeightUnionType.of(getHeight() + "px");
			CanvasRenderingContext2D c2d = Js.uncheckedCast(canv.getContext("2d"));
			c2d.drawImage(img, 0, 0);
		}
		return canv;
	}

	@Override
	public GGraphics2D createGraphics() {
		return new GGraphics2DW(Canvas.wrap(Js.uncheckedCast(getCanvas())), true);
	}

	/**
	 * @return image element
	 */
	public HTMLImageElement getImage() {
		return img;
	}

	@Override
	public String toLaTeXStringBase64() {
		return "\\imagebasesixtyfour{" + getWidth() + "}{" + getHeight() + "}{"
				+ img.src + "}";
	}
}

package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.geogebra.web.resources.SVGResourcePrototype;
import org.gwtproject.canvas.client.Canvas;

import com.himamis.retex.renderer.web.graphics.JLMContext2d;

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

	private final HTMLImageElement img;
	private HTMLCanvasElement canv;
	private int width;
	private int height;
	private final boolean svg;

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
			getSizesFromDOM();
		}
	}

	/*
	 * hack for IE10/11/12
	 * can't work out SVG height unless it's attached to the DOM
	 */
	private void getSizesFromDOM() {
		DomGlobal.document.body.append(img);
		width = img.offsetWidth;
		height = img.offsetHeight;
		DomGlobal.document.body.removeChild(img);
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

	@Override
	public MyImage tintedSVG(GColor color, Runnable onLoad) {
		if (!svg) {
			return null;
		}
		try {
			String dataUrl = img.src;
			String svg = DomGlobal.atob(dataUrl.substring(dataUrl.indexOf(",") + 1));
			String svgRes = SVGResourcePrototype.createFilled(color.toString(), svg);
			HTMLImageElement img = Js.uncheckedCast(DomGlobal.document.createElement("img"));
			img.addEventListener("load", evt -> onLoad.run());
			img.src = StringUtil.svgMarker + DomGlobal.btoa(svgRes);
			return new MyImageW(img, true);
		} catch (RuntimeException ex) {
			return null;
		}
	}

	/**
	 * Draw this image in context.
	 * @param context graphics context
	 * @param x horizontal offset
	 * @param y vertical offset
	 */
	public void render(JLMContext2d context, int x, int y) {
		context.drawImage(img, x, y);
	}
}

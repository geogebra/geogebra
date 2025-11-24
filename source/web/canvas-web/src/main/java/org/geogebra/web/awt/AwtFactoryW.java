package org.geogebra.web.awt;

import org.geogebra.common.awt.GAlphaComposite;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GGradientPaint;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.ggbjdk.factories.AwtFactoryHeadless;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;

import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Creates AWT wrappers for web
 *
 */
public class AwtFactoryW extends AwtFactoryHeadless {

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			double pixelRatio) {
		return new GBufferedImageW(pixelWidth, pixelHeight, pixelRatio);
	}

	@Override
	public GBufferedImage createBufferedImage(int width, int height,
			boolean transparency) {
		return new GBufferedImageW(width, height, 1.0f, !transparency);
	}

	@Override
	public GDimension newDimension(int width, int height) {
		return new Dimension(width, height);
	}

	@Override
	public GTextLayout newTextLayout(String string, GFont fontLine,
			GFontRenderContext frc) {
		return new GTextLayoutW(string, fontLine, (GFontRenderContextW) frc);
	}

	@Override
	public GAlphaComposite newAlphaComposite(double alpha) {
		return new GAlphaCompositeW(alpha);
	}

	@Override
	public GGradientPaint newGradientPaint(double x, double y, GColor bg2,
			double x2, double i, GColor bg) {
		return new GGradientPaintW(x, y, bg2, x2, i, bg);
	}

	@Override
	public GFont newFont(String name, int style, int size) {
		return new GFontW(name, style, size);
	}

	@Override
	public GPaint newTexturePaint(GBufferedImage subimage, GRectangle rect) {
		return new GTexturePaintW((GBufferedImageW) subimage, rect);
	}

	@Override
	public GPaint newTexturePaint(MyImage subimage, GRectangle rect) {
		return new GTexturePaintW(
				new GBufferedImageW(((MyImageW) subimage).getImage()), rect);
	}

	@Override
	public GBufferedImage newBufferedImage(int pixelWidth, int pixelHeight,
			GGraphics2D g2) {
		return newBufferedImage(pixelWidth, pixelHeight,
				((GGraphics2DWI) g2).getDevicePixelRatio());
	}

	@Override
	public GGraphics2DW getSVGGraphics(int width, int height) {
		Canvas2Svg canvas2svg = new Canvas2Svg(width, height);
		CanvasRenderingContext2D ctx = Js.uncheckedCast(canvas2svg);
		return new GGraphics2DW(ctx);
	}

	@Override
	public GGraphics2DW getPDFGraphics(int width, int height) {
		Canvas2Pdf.PdfContext canvas2pdf = new Canvas2Pdf.PdfContext(width, height,
				JsPropertyMap.of("verticalFlip", true));
		CanvasRenderingContext2D ctx = Js.uncheckedCast(canvas2pdf);
		return new GGraphics2DW(ctx);
	}

}

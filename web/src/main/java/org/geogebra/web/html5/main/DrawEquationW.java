package org.geogebra.web.html5.main;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.awt.GDimensionW;
import org.geogebra.web.html5.awt.GGraphics2DW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style.Unit;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.web.DrawingFinishedCallback;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.graphics.ColorW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;

/**
 * Web LaTeX helper class
 */
public class DrawEquationW extends DrawEquation {


 

	private static Object initJLaTeXMath = null;

	@Override
	public GDimension drawEquation(App app1, GeoElementND geo,
			final GGraphics2D g2,
	        int x, int y, String latexString0, GFont font, boolean serif,
	        final GColor fgColor, GColor bgColor, boolean useCache,
			boolean updateAgain, final Runnable callback) {

			String eqstring = latexString0;

		TeXIcon icon = createIcon(eqstring, convertColor(fgColor), font,
				font.getLaTeXStyle(serif),
				null, null, app1);

			Graphics2DW g3 = new Graphics2DW(((GGraphics2DW) g2).getContext());
			g3.setDrawingFinishedCallback(new DrawingFinishedCallback() {

				@Override
				public void onDrawingFinished() {
					((GGraphics2DW) g2).updateCanvasColor();
					if (callback != null) {
						callback.run();
					}

				}
			});
			icon.paintIcon(new HasForegroundColor() {
				@Override
				public Color getForegroundColor() {
					return FactoryProvider.INSTANCE.getGraphicsFactory()
							.createColor(fgColor.getRed(), fgColor.getGreen(),
									fgColor.getBlue());
				}
			}, g3, x, y);
			((GGraphics2DW) g2).updateCanvasColor();
			g3.maybeNotifyDrawingFinishedCallback();
			return new GDimensionW(icon.getIconWidth(), icon.getIconHeight());

	}

	private static void ensureJLMFactoryExists() {
		if (FactoryProvider.INSTANCE == null) {
			FactoryProvider.INSTANCE = new FactoryProviderGWT();
		}
	}

	/*
	 * needed for avoid the pixelated appearance of LaTeX texts at printing
	 */
	private static double printScale = 1;

	/**
	 * @param t
	 *            print scale
	 */
	public static void setPrintScale(double t) {
		printScale = t;
	}
	/**
	 * @param geo
	 *            geo element, used for color and app
	 * @param text0
	 *            LaTeX text
	 * @param c0
	 *            canvas (may be null)
	 * @param fontSize
	 *            font size
	 * @return canvas
	 */
	public static Canvas paintOnCanvas(GeoElementND geo, String text0,
			Canvas c0,
			int fontSize) {
		if (geo == null) {
			return c0 == null ? Canvas.createIfSupported() : c0;
		}
		AppW app = ((AppW) geo.getKernel().getApplication());
		final GColor fgColor = app.has(Feature.AV_TEXT_BLACK) ? GColor.BLACK
				: geo.getAlgebraColor();
		return paintOnCanvas(app, text0, c0, fontSize, fgColor);
	}

	public static Canvas paintOnCanvasOutput(GeoElementND geo, String text0,
			Canvas c0, int fontSize) {
		if (geo == null) {
			return c0 == null ? Canvas.createIfSupported() : c0;
		}
		AppW app = ((AppW) geo.getKernel().getApplication());
		final GColor fgColor = geo.getAlgebraColor();
		return paintOnCanvas(app, text0, c0, fontSize, fgColor);
	}
	/**
	 * @param app
	 *            has access to LaTeX and pixel ratio
	 * @param text0
	 *            LaTeX text
	 * @param c0
	 *            canvas (may be null)
	 * @param fontSize
	 *            font size
	 * @return canvas
	 */
	public static Canvas paintOnCanvas(AppW app, String text0,
			Canvas c0, int fontSize, final GColor fgColor) {

		Canvas c = c0;
		if (c == null) {
			c = Canvas.createIfSupported();
		} else {
			c.getContext2d().fillRect(0, 0, c.getCoordinateSpaceWidth(),
					c.getCoordinateSpaceHeight());
		}
		JLMContext2d ctx = (JLMContext2d) c.getContext2d();

		app.getDrawEquation().checkFirstCall(app);
		GFont font = AwtFactory.getPrototype().newFont("geogebra", GFont.PLAIN,
				fontSize - 3);
		TeXIcon icon = app.getDrawEquation().createIcon(
				"\\mathsf{\\mathrm {" + text0 + "}}",
				app.getDrawEquation().convertColor(fgColor),
				font, font.getLaTeXStyle(false),
				null, null, app);
		Graphics2DInterface g3 = new Graphics2DW(ctx);

		double ratio = app.getPixelRatio() * printScale;
		int width = Math.min(icon.getIconWidth(), 20000);
		c.setCoordinateSpaceWidth((int) (width * ratio));
		c.setCoordinateSpaceHeight((int) (icon.getIconHeight() * ratio));
		c.getElement().getStyle().setWidth(width, Unit.PX);
		c.getElement().getStyle().setHeight(icon.getIconHeight(), Unit.PX);

		// c.getElement().getStyle().setMargin(4, Unit.PX);
		ctx.scale2(ratio, ratio);

		icon.paintIcon(new HasForegroundColor() {
			@Override
			public Color getForegroundColor() {
				return FactoryProvider.INSTANCE.getGraphicsFactory()
						.createColor(fgColor.getRed(), fgColor.getGreen(),
								fgColor.getBlue());
			}
		}, g3, 0, 0);
		return c;
	}



	@Override
	public GDimension measureEquation(App app, GeoElement geo0, int minValue,
			int minValue2, String text, GFont font, boolean serif) {
		return this.measureEquationJLaTeXMath(app, geo0, 0, 0, text, font,
				serif, null, null);
	}

	@Override
	public void checkFirstCall(App app) {
		ensureJLMFactoryExists();
		if (initJLaTeXMath == null) {

			StringBuilder initJLM = DrawEquation.getJLMCommands();
			initJLaTeXMath = new TeXFormula(initJLM.toString());
		}

	}

	@Override
	public Color convertColor(GColor color) {
		return new ColorW(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	protected Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret) {
		// TODO Auto-generated method stub
		return null;
	}
}

package org.geogebra.web.html5.main;

import java.util.function.Supplier;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.geom.Dimension;
import org.geogebra.web.html5.awt.GGraphics2DW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.style.shared.Unit;

import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.web.FactoryProviderGWT;
import com.himamis.retex.renderer.web.graphics.ColorW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;
import com.himamis.retex.renderer.web.graphics.JLMContext2d;
import com.himamis.retex.renderer.web.graphics.JLMContextHelper;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Web LaTeX helper class
 */
public class DrawEquationW extends DrawEquation {
	/*
	 * needed for avoid the pixelated appearance of LaTeX texts at printing
	 */
	private static double printScale = 1;
	private final Supplier<Double> pixelRatio;

	public DrawEquationW(Supplier<Double> pixelRatio) {
		this.pixelRatio = pixelRatio;
	}

	@Override
	public GDimension drawEquation(App app1, GeoElementND geo,
			final GGraphics2D g2, int x, int y, String latexString0, GFont font,
			boolean serif, final GColor fgColor, GColor bgColor,
			boolean useCache, boolean updateAgain, final Runnable callback) {

		String eqstring = latexString0;

		TeXIcon icon = createIcon(eqstring, convertColor(fgColor), font,
				font.getLaTeXStyle(serif), null, null);

		Graphics2DW g3 = new Graphics2DW(((GGraphics2DW) g2).getContext());
		g3.setDrawingFinishedCallback(async -> {
			g2.updateCanvasColor();
			if (callback != null) {
				callback.run();
			}
		});
		icon.paintIcon(() -> convertColor(fgColor), g3, x, y);
		g2.updateCanvasColor();
		g3.maybeNotifyDrawingFinishedCallback(false);
		return new Dimension(icon.getIconWidth(), icon.getIconHeight());

	}

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
			Canvas c0, int fontSize) {
		Canvas c = makeCleanCanvas(c0);
		if (geo != null) {
			DrawEquationW current =
					(DrawEquationW) geo.getKernel().getApplication().getDrawEquation();
			current.paintOnCleanCanvas(text0, c, fontSize, GColor.BLACK, needsSerif(geo));
		}
		return c;
	}

	/**
	 * @param geo
	 *            element
	 * @param text0
	 *            text
	 * @param c0
	 *            canvas or null
	 * @param fontSize
	 *            font size
	 * @return canvas
	 */
	public static Canvas paintOnCanvasOutput(GeoElementND geo, String text0,
			Canvas c0, int fontSize) {
		final GColor fgColor = geo.getAlgebraColor();
		Canvas c = makeCleanCanvas(c0);
		DrawEquationW current = (DrawEquationW) geo.getKernel().getApplication().getDrawEquation();
		current.paintOnCleanCanvas(text0, c, fontSize, fgColor, needsSerif(geo));
		return c;
	}

	/**
	 * @param geo element
	 * @return whether serif is needed
	 */
	public static boolean needsSerif(GeoElementND geo) {
		boolean serif = false;
		if (geo instanceof TextProperties) {
			serif = ((TextProperties) geo).isSerifFont();
		}
		return serif;
	}

	/**
	 * @param old old canvas
	 * @return cleaned old canvas or a new instance
	 */
	public static Canvas makeCleanCanvas(Canvas old) {
		Canvas c = old;
		if (c == null) {
			c = Canvas.createIfSupported();
		} else {
			c.getContext2d().fillRect(0, 0, c.getCoordinateSpaceWidth(),
					c.getCoordinateSpaceHeight());
		}
		return c;
	}

	/**
	 * @param text0
	 *            LaTeX text
	 * @param c
	 *            canvas
	 * @param fontSize
	 *            font size
	 * @param fgColor
	 *            color
	 * @param serif
	 *            whether to use serif font
	 * @return graphics
	 */
	public Graphics2DW paintOnCleanCanvas(String text0, @NonNull Canvas c,
			int fontSize, final GColor fgColor, boolean serif) {
		JLMContext2d ctx = JLMContextHelper.as(c.getContext2d());

		checkFirstCall();
		TeXIcon icon = createIcon(text0,
				convertColor(fgColor), fontSize,
				serif ? 0 : TeXFont.SANSSERIF);
		Graphics2DW g3 = new Graphics2DW(ctx);

		double ratio = pixelRatio.get() * printScale;
		double width = roundUp(Math.min(icon.getIconWidth(), 20000), ratio);
		double height = roundUp(icon.getIconHeight(), ratio);
		c.setCoordinateSpaceWidth((int) (width * ratio));
		c.setCoordinateSpaceHeight((int) (height * ratio));
		c.getElement().getStyle().setWidth(width, Unit.PX);
		c.getElement().getStyle().setHeight(height, Unit.PX);

		// c.getElement().getStyle().setMargin(4, Unit.PX);
		ctx.scale2(ratio, ratio);

		icon.paintIcon(() -> convertColor(fgColor), g3, 0, 0);
		return g3;
	}

	private static double roundUp(int w, double ratio) {

		return Math.ceil(w * ratio) / ratio;
	}

	@Override
	public GDimension measureEquation(App app, String text,
			GFont font, boolean serif) {
		return this.measureEquationJLaTeXMath(app, text, font, serif, null,
				null);
	}

	@Override
	public void checkFirstCall() {
		FactoryProviderGWT.ensureLoaded();
		DrawEquation.checkFirstCallStatic();
	}

	@Override
	public Color convertColor(GColor color) {
		return new ColorW(color.getRed(), color.getGreen(), color.getBlue());
	}

	@Override
	public Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret) {
		// TODO JLaTeXMathCache uses
		// import java.lang.ref.Reference;
		// import java.lang.ref.ReferenceQueue;
		// import java.lang.ref.SoftReference;
		// so can't use this method in web at the moment
		return null;
	}
}

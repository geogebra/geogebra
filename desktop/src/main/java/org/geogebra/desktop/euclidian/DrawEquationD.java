package org.geogebra.desktop.euclidian;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.desktop.graphics.ColorD;
import com.himamis.retex.renderer.desktop.graphics.Graphics2DD;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.cache.JLaTeXMathCache;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.Image;

public class DrawEquationD extends DrawEquation {

	boolean drawEquationJLaTeXMathFirstCall = true;

	private Object initJLaTeXMath;





	public void checkFirstCall(App app) {
		if (drawEquationJLaTeXMathFirstCall) { // first call

			drawEquationJLaTeXMathFirstCall = false;

			// initialise definitions
			if (initJLaTeXMath == null) {

				StringBuilder initJLM = getJLMCommands();
				FactoryProvider.INSTANCE = new FactoryProviderDesktop();
				initJLaTeXMath = new TeXFormula(initJLM.toString());
			}

			// make sure cache doesn't get too big
			JLaTeXMathCache.setMaxCachedObjects(100);

			// disable \magnification{factor} (makes Algebra View not work)
			// now removed from JLaTeXMath
			// DefaultTeXFont.enableMagnification(false);

		}

	}

	final public GDimension drawEquation(final App app,
			final GeoElementND geo, final GGraphics2D g2, final int x,
			final int y, final String text, final GFont font,
			final boolean serif, final GColor fgColor, final GColor bgColor,
			final boolean useCache, boolean updateAgain, Runnable callback) {


		GDimension d = drawEquation(app, geo,
				new Graphics2DD(GGraphics2DD.getAwtGraphics(g2)), x, y, text,
				font, serif, ColorD.get(GColorD.getAwtColor(fgColor)),
				ColorD.get(GColorD.getAwtColor(bgColor)), useCache, null, null);
		if (callback != null) {
			callback.run();
		}
		return d;
	}

	/**
	 * Draw a LaTeX image in the cell icon. Drawing is done twice. First draw
	 * gives the needed size of the image. Second draw renders the image with
	 * the correct dimensions.
	 * 
	 * @param app
	 *            needed for
	 *            {@link #drawEquationJLaTeXMath(AppD, GeoElement, Graphics2D, int, int, String, GFont, boolean, Color, Color, boolean, Integer, Float)}
	 * @param latexIcon
	 *            the LaTeX String will be drawn there
	 * @param latex
	 *            the LaTeX String to be drawn
	 * @param font
	 * @param serif
	 * @param fgColor
	 *            foreground color
	 * @param bgColor
	 *            background color
	 */
	public void drawLatexImageIcon(final AppD app, ImageIcon latexIcon,
			final String latex, final Font font, final boolean serif,
			final Color fgColor, final Color bgColor) {

		// Create image with dummy size, then draw into it to get the correct
		// size
		BufferedImage image = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		GDimension d = new GDimensionD();
		d = drawEquation(app, null, new GGraphics2DD(g2image), 0, 0, latex,
				new GFontD(font), serif, new GColorD(fgColor),
				new GColorD(bgColor), true, false,
				null);

		// Now use this size and draw again to get the final image
		image = new BufferedImage(d.getWidth(), d.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		d = drawEquation(app, null, new GGraphics2DD(g2image), 0, 0, latex,
				new GFontD(font), serif, new GColorD(fgColor),
				new GColorD(bgColor), true, false,
				null);

		latexIcon.setImage(image);
	}

	@Override
	public GDimension measureEquation(App app, GeoElement geo0, int minValue,
			int minValue2, String text, GFont font, boolean serif) {

		return this.measureEquationJLaTeXMath(app, geo0, 0, 0, text, font,
				serif, null, null);
	}

	@Override
	public com.himamis.retex.renderer.share.platform.graphics.Color convertColor(
			GColor color) {
		return ColorD.get(GColorD.getAwtColor(color));
	}

	@Override
	protected Image getCachedDimensions(String text, GeoElementND geo,
			com.himamis.retex.renderer.share.platform.graphics.Color fgColor,
			GFont font, int style, int[] ret) {
		Object key = null;
		// if geoText != null then keep track of which key goes with the
		// GeoText
		// so that we can remove it from the cache if it changes
		// eg for a (regular) dynamic LaTeX text eg "\sqrt{"+a+"}"
		if (geo == null) {
			key = JLaTeXMathCache.getCachedTeXFormula(text,
					TeXConstants.STYLE_DISPLAY, style,
					font.getSize() + 3 /* font size */,
					1 /*
						 * inset around the label
						 */, fgColor);
		} else {
			key = geo.getLaTeXCache().getCachedLaTeXKey(text,
					font.getSize() + 3, style, fgColor);
		}

		int[] ret2 = JLaTeXMathCache.getCachedTeXFormulaDimensions(key);
		ret[0] = ret2[0];
		ret[1] = ret2[1];
		return JLaTeXMathCache.getCachedTeXFormulaImage(key);
	}

}

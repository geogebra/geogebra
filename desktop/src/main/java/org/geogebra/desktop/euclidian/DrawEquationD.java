package org.geogebra.desktop.euclidian;

import java.awt.Color;
import java.awt.Dimension;
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
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.awt.GDimensionD;
import org.geogebra.desktop.awt.GFontD;
import org.geogebra.desktop.awt.GGraphics2DD;
import org.geogebra.desktop.main.AppD;

import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.desktop.graphics.ColorD;
import com.himamis.retex.renderer.desktop.graphics.Graphics2DD;
import com.himamis.retex.renderer.share.DefaultTeXFont;
import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.cache.JLaTeXMathCache;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

public class DrawEquationD extends DrawEquation {

	boolean drawEquationJLaTeXMathFirstCall = true;

	private Object initJLaTeXMath;

	/**
	 * Renders LaTeX equation using JLaTeXMath
	 * 
	 * @param app
	 * @param g2
	 * @param x
	 * @param y
	 * @param text
	 * @param font
	 * @param serif
	 * @param fgColor
	 * @param bgColor
	 * @return dimension of rendered equation
	 */
	final public Dimension drawEquationJLaTeXMath(final AppD app,
			final GeoElementND geo, final Graphics2D g2, final int x,
			final int y, final String text, final GFont font,
			final boolean serif,
			final Color fgColor, final Color bgColor, final boolean useCache,
			final Integer maxWidth, final Float lineSpace) {
		// TODO uncomment when \- works
		// text=addPossibleBreaks(text);

		int width = -1;
		int height = -1;
		// int depth = 0;

		checkFirstCall(app);

		int style = font.getLaTeXStyle(serif);

		// if we're exporting, we want to draw it full resolution
		if (app.isExporting() || !useCache) {

			// Application.debug("creating new icon for: "+text);
			TeXFormula formula;
			TeXIcon icon;

			try {
				formula = new TeXFormula(text);

				if (maxWidth == null) {
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							font.getSize() + 3, style, ColorD.get(fgColor));
				} else {
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							font.getSize() + 3, TeXConstants.UNIT_CM,
							maxWidth.intValue(), TeXConstants.ALIGN_LEFT,
							TeXConstants.UNIT_CM, lineSpace.floatValue());
				}
			} catch (final MyError e) {
				// e.printStackTrace();
				// Application.debug("MyError LaTeX parse exception:
				// "+e.getMessage()+"\n"+text);
				// Write error message to Graphics View

				formula = TeXFormula.getPartialTeXFormula(text);
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, ColorD.get(fgColor));

				formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
						TeXConstants.UNIT_CM, 4f, TeXConstants.ALIGN_LEFT,
						TeXConstants.UNIT_CM, 0.5f);

			} catch (final Exception e) {
				// e.printStackTrace();
				// Application.debug("LaTeX parse exception:
				// "+e.getMessage()+"\n"+text);
				// Write error message to Graphics View
				try {
					formula = TeXFormula.getPartialTeXFormula(text);

					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							font.getSize() + 3, style, ColorD.get(fgColor));
				} catch (Exception e2) {
					Log.debug("LaTeX parse exception: " + e.getMessage() + "\n"
							+ text);
					formula = TeXFormula.getPartialTeXFormula(
							"\text{" + app.getLocalization()
									.getError("CAS.GeneralErrorMessage") + "}");
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							font.getSize() + 3, style, ColorD.get(fgColor));
				}
			}
			icon.setInsets(new Insets(1, 1, 1, 1));
			HasForegroundColor fg = new HasForegroundColor() {

				public com.himamis.retex.renderer.share.platform.graphics.Color getForegroundColor() {
					return ColorD.get(fgColor);
				}

			};

			icon.paintIcon(fg, new Graphics2DD(g2), x, y);

			return new Dimension(icon.getIconWidth(), icon.getIconHeight());

		}

		Object key = null;
		Image im = null;
		try {
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
							 */, ColorD.get(fgColor));
			} else {
				key = geo.getLaTeXCache().getCachedLaTeXKey(text,
						font.getSize() + 3, style, ColorD.get(fgColor));
			}

			im = JLaTeXMathCache.getCachedTeXFormulaImage(key);

			final int ret[] = JLaTeXMathCache
					.getCachedTeXFormulaDimensions(key);
			width = ret[0];
			height = ret[1];
			// depth = ret[2];

		} catch (final Exception e) {
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			final TeXFormula formula = TeXFormula.getPartialTeXFormula(text);
			im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, new ColorD(Color.black),
					new ColorD(Color.white));
		}

		new Graphics2DD(g2).drawImage(im, x, y);

		if (width == -1) {
			width = im.getWidth();
		}
		if (height == -1) {
			height = im.getHeight();
		}

		return new Dimension(width, height);
	}

	final public Dimension measureEquationJLaTeXMath(final AppD app,
			final GeoElement geo, final int x, final int y, final String text,
			final GFont font, final boolean serif,
			final Integer maxWidth, final Float lineSpace) {

		checkFirstCall(app);
		Color fgColor = Color.BLACK;
		int style = font.getLaTeXStyle(serif);

		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			if (maxWidth == null) {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, ColorD.get(fgColor));
			} else {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, TeXConstants.UNIT_CM,
						maxWidth.intValue(), TeXConstants.ALIGN_LEFT,
						TeXConstants.UNIT_CM, lineSpace.floatValue());
			}
		} catch (final MyError e) {
			// e.printStackTrace();
			// Application.debug("MyError LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, ColorD.get(fgColor));

			formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
					TeXConstants.UNIT_CM, 4f, TeXConstants.ALIGN_LEFT,
					TeXConstants.UNIT_CM, 0.5f);

		} catch (final Exception e) {
			// e.printStackTrace();
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, ColorD.get(fgColor));

		}
		icon.setInsets(new Insets(1, 1, 1, 1));

		return new Dimension(icon.getIconWidth(), icon.getIconHeight());

	}

	private void checkFirstCall(AppD app) {
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
			DefaultTeXFont.enableMagnification(false);

		}

	}

	final public GDimension drawEquation(final App app,
			final GeoElementND geo, final GGraphics2D g2, final int x,
			final int y, final String text, final GFont font,
			final boolean serif, final GColor fgColor, final GColor bgColor,
			final boolean useCache, boolean updateAgain, Runnable callback) {

		Dimension d = drawEquation((AppD) app, geo,
				GGraphics2DD.getAwtGraphics(g2), x, y,
				text, font, serif, fgColor, bgColor, useCache, null, null);

		if (callback != null) {
			callback.run();
		}
		return new GDimensionD(d);
	}

	final public static Dimension drawEquation(final AppD app,
			final GeoElementND geo, final Graphics2D g2, final int x,
			final int y, final String text, final GFont font,
			final boolean serif, final GColor fgColor, final GColor bgColor,
			final boolean useCache, final Integer maxWidth,
			final Float lineSpace) {

		return app.getDrawEquation().drawEquationJLaTeXMath(app, geo, g2, x, y,
				text, font, serif, GColorD.getAwtColor(fgColor),
				GColorD.getAwtColor(bgColor), useCache,
				maxWidth, lineSpace);
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
			int minValue2, String text, GFont font, boolean b) {

		return new GDimensionD(this.measureEquationJLaTeXMath((AppD) app, geo0,
				0, 0, text, font, b, null, null));
	}

}

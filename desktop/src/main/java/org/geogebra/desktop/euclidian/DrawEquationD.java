package org.geogebra.desktop.euclidian;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.geogebra.common.euclidian.DrawEquation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.Language;
import org.geogebra.desktop.main.AppD;
import org.scilab.forge.jlatexmath.AlphabetRegistration;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.WebStartAlphabetRegistration;
import org.scilab.forge.jlatexmath.cache.JLaTeXMathCache;
import org.scilab.forge.jlatexmath.dynamic.DynamicAtom;

public class DrawEquationD extends DrawEquation {
	private final JLabel jl = new JLabel();
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
			final GeoElement geo, final Graphics2D g2, final int x,
			final int y, final String text,
			final org.geogebra.common.awt.GFont font, final boolean serif,
			final Color fgColor, final Color bgColor, final boolean useCache,
			final Integer maxWidth, final Float lineSpace) {
		// TODO uncomment when \- works
		// text=addPossibleBreaks(text);

		int width = -1;
		int height = -1;
		// int depth = 0;

		if (drawEquationJLaTeXMathFirstCall) { // first call

			drawEquationJLaTeXMathFirstCall = false;

			// initialise definitions
			if (initJLaTeXMath == null) {

				StringBuilder initJLM = getJLMCommands();
				initJLaTeXMath = new TeXFormula(initJLM.toString());
			}

			// make sure cache doesn't get too big
			JLaTeXMathCache.setMaxCachedObjects(100);

			// disable \magnification{factor} (makes Algebra View not work)
			DefaultTeXFont.enableMagnification(false);

			for (Language l : Language.values()) {

				if (l.testChar != null) {
					final Font testFont = app.getFontCanDisplayAwt(l.testChar,
							true, Font.PLAIN, 12);
					if (testFont != null) {
						TeXFormula
								.registerExternalFont(Character.UnicodeBlock
										.of(l.testChar.charAt(0)), testFont
										.getFontName());
						// Application.debug("LaTeX font registering: "+l.name+" "+testFont.getFontName());
					}

				}
			}

			// Arabic is in standard Java fonts, so we don't need to search for
			// a font
			TeXFormula.registerExternalFont(
					Character.UnicodeBlock.of('\u0681'), "Sans Serif", "Serif");
			// Korean is in standard Java fonts, so we don't need to search for
			// a font
			TeXFormula.registerExternalFont(
					Character.UnicodeBlock.of('\uB458'), "Sans Serif", "Serif");
			// Japanese is in standard Java fonts, so we don't need to search
			// for
			// a font
			TeXFormula.registerExternalFont(
					Character.UnicodeBlock.of('\u30ea'), "Sans Serif", "Serif");

			// Other codeblocks (currency, symbols etc)
			TeXFormula.registerExternalFont(
					Character.UnicodeBlock.of('\u20a0'), "Sans Serif", "Serif");
			TeXFormula.registerExternalFont(
					Character.UnicodeBlock.of('\u2600'), "Sans Serif", "Serif");
			TeXFormula.registerExternalFont(
					Character.UnicodeBlock.of('\u2700'), "Sans Serif", "Serif");

			try {
				WebStartAlphabetRegistration
						.register(AlphabetRegistration.JLM_GREEK);
				WebStartAlphabetRegistration
						.register(AlphabetRegistration.JLM_CYRILLIC);
				// URLAlphabetRegistration.register(new
				// URL(app.getCodeBase()+"jlm_greek.jar"),
				// "greek",URLAlphabetRegistration.JLM_GREEK);
				// URLAlphabetRegistration.register(new
				// URL(app.getCodeBase()+"jlm_cyrillic.jar"),
				// "cyrillic",URLAlphabetRegistration.JLM_CYRILLIC);
			} catch (final Exception e) {
				e.printStackTrace();
			}
			final LatexConvertorFactory factory = new LatexConvertorFactory(
					app.getKernel());
			DynamicAtom.setExternalConverterFactory(factory);

		}

		int style = 0;
		if (font.isBold()) {
			style = style | TeXFormula.BOLD;
		}
		if (font.isItalic()) {
			style = style | TeXFormula.ITALIC;
		}
		if (!serif) {
			style = style | TeXFormula.SANSSERIF;
		}

		// if we're exporting, we want to draw it full resolution
		// if it's a \jlmDynamic text, we don't want to add it to the cache
		if (app.isExporting() || (text.indexOf("\\jlmDynamic") > -1)
				|| !useCache) {

			// Application.debug("creating new icon for: "+text);
			TeXFormula formula;
			TeXIcon icon;

			try {
				formula = new TeXFormula(text);

				if (maxWidth == null) {
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							font.getSize() + 3, style, fgColor);
				} else {
					icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
							font.getSize() + 3, TeXConstants.UNIT_CM,
							maxWidth.intValue(), TeXConstants.ALIGN_LEFT,
							TeXConstants.UNIT_CM, lineSpace.floatValue());
				}
			} catch (final MyError e) {
				// e.printStackTrace();
				// Application.debug("MyError LaTeX parse exception: "+e.getMessage()+"\n"+text);
				// Write error message to Graphics View

				formula = TeXFormula.getPartialTeXFormula(text);
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);

				formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
						TeXConstants.UNIT_CM, 4f, TeXConstants.ALIGN_LEFT,
						TeXConstants.UNIT_CM, 0.5f);

				// Rectangle rec = drawMultiLineText(e.getMessage()+"\n"+text,
				// x, y + g2.getFont().getSize(), g2);
				// return new Dimension(rec.width, rec.height);
			} catch (final Exception e) {
				// e.printStackTrace();
				// Application.debug("LaTeX parse exception: "+e.getMessage()+"\n"+text);
				// Write error message to Graphics View

				formula = TeXFormula.getPartialTeXFormula(text);
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);

				// Rectangle rec = drawMultiLineText(e.getMessage()+"\n"+text,
				// x, y + g2.getFont().getSize(), g2);
				// return new Dimension(rec.width, rec.height);
			}
			icon.setInsets(new Insets(1, 1, 1, 1));

			jl.setForeground(fgColor);
			icon.paintIcon(jl, g2, x, y);
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
						font.getSize() + 3 /* font size */, 1 /*
																 * inset around
																 * the label
																 */, fgColor);
			} else {
				key = geo.getLaTeXCache().getCachedLaTeXKey(text,
						font.getSize() + 3, style,
						new org.geogebra.desktop.awt.GColorD(fgColor));
			}

			im = JLaTeXMathCache.getCachedTeXFormulaImage(key);

			final int ret[] = JLaTeXMathCache
					.getCachedTeXFormulaDimensions(key);
			width = ret[0];
			height = ret[1];
			// depth = ret[2];

		} catch (final Exception e) {
			// Application.debug("LaTeX parse exception: "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			final TeXFormula formula = TeXFormula.getPartialTeXFormula(text);
			im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, Color.black, Color.white);

			// Rectangle rec = drawMultiLineText(e.getMessage()+"\n"+text, x, y
			// + g2.getFont().getSize(), g2);
			// return new Dimension(rec.width, rec.height);

		}

		g2.drawImage(im, x, y, null);

		if (width == -1) {
			width = im.getWidth(null);
		}
		if (height == -1) {
			height = im.getHeight(null);
		}

		return new Dimension(width, height);
	}



	public void setUseJavaFontsForLaTeX(final App app, final boolean b) {
		if (b != app.useJavaFontsForLaTeX) {
			app.useJavaFontsForLaTeX = b;
			final String serifFont = b ? "Serif" : null;
			final String sansSerifFont = b ? "Sans Serif" : null;
			TeXFormula.registerExternalFont(Character.UnicodeBlock.BASIC_LATIN,
					sansSerifFont, serifFont);
			JLaTeXMathCache.clearCache();
			app.getKernel().notifyRepaint();
		}
	}

	final public org.geogebra.common.awt.GDimension drawEquation(final App app,
			final GeoElement geo, final org.geogebra.common.awt.GGraphics2D g2,
			final int x, final int y, final String text,
			final org.geogebra.common.awt.GFont font, final boolean serif,
			final org.geogebra.common.awt.GColor fgColor,
			final org.geogebra.common.awt.GColor bgColor, final boolean useCache,
 boolean updateAgain, Runnable callback) {
		Dimension d = drawEquation((AppD) app, geo,
				org.geogebra.desktop.awt.GGraphics2DD.getAwtGraphics(g2), x, y, text, font,
 serif, fgColor, bgColor, useCache, null, null);
		if (callback != null) {
			callback.run();
		}
		return new org.geogebra.desktop.awt.GDimensionD(d);
	}

	final public static Dimension drawEquation(final AppD app,
			final GeoElement geo, final Graphics2D g2, final int x,
			final int y, final String text,
			final org.geogebra.common.awt.GFont font, final boolean serif,
			final org.geogebra.common.awt.GColor fgColor,
			final org.geogebra.common.awt.GColor bgColor, final boolean useCache,
			final Integer maxWidth, final Float lineSpace) {
		// if (useJLaTeXMath)
		return app.getDrawEquation().drawEquationJLaTeXMath(app, geo, g2, x, y,
				text, font, serif, org.geogebra.desktop.awt.GColorD.getAwtColor(fgColor),
				org.geogebra.desktop.awt.GColorD.getAwtColor(bgColor), useCache, maxWidth,
				lineSpace);
		// else return drawEquationHotEqn(app, g2, x, y, text, font, fgColor,
		// bgColor);
	}

	/**
	 * Draw a LaTeX image in the cell icon. Drawing is done twice. First draw
	 * gives the needed size of the image. Second draw renders the image with
	 * the correct dimensions.
	 * 
	 * @param app
	 *            needed for
	 *            {@link #drawEquationJLaTeXMath(AppD, GeoElement, Graphics2D, int, int, String, org.geogebra.common.awt.GFont, boolean, Color, Color, boolean, Integer, Float)}
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

		org.geogebra.common.awt.GDimension d = new org.geogebra.desktop.awt.GDimensionD();
		d = drawEquation(app, null, new org.geogebra.desktop.awt.GGraphics2DD(g2image), 0,
				0, latex, new org.geogebra.desktop.awt.GFontD(font), serif,
				new org.geogebra.desktop.awt.GColorD(fgColor),
				new org.geogebra.desktop.awt.GColorD(bgColor), true, false,
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
		d = drawEquation(app, null, new org.geogebra.desktop.awt.GGraphics2DD(g2image), 0,
				0, latex, new org.geogebra.desktop.awt.GFontD(font), serif,
				new org.geogebra.desktop.awt.GColorD(fgColor),
				new org.geogebra.desktop.awt.GColorD(bgColor), true, false,
				null);

		latexIcon.setImage(image);
	}

}

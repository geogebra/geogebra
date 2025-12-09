/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

/**
 * Cross platform helper for equation rendering
 */
public abstract class DrawEquation implements DrawEquationI {
	private static Object initJLaTeXMath;

	/**
	 * @return \newcommand definitions for GeoGebra specific commands and do
	 *         other initialization
	 */
	public static StringBuilder getJLMCommands() {
		StringBuilder initJLM = new StringBuilder();

		HashMap<String, GColor> ggbCols = GeoGebraColorConstants
				.getGeoGebraColors();

		Iterator<Entry<String, GColor>> it = ggbCols.entrySet().iterator();

		// add commands eg \red{text}
		while (it.hasNext()) {
			Entry<String, GColor> colPair = it.next();

			String colStr = colPair.getKey();

			// can't have command eg \grey2
			if (!Character.isDigit(colStr.charAt(colStr.length() - 1))) {
				GColor col = colPair.getValue();

				// eg
				// initJLM.append("\\newcommand{\\red}[1]{\\textcolor{255,0,0}{#1}}
				// ");
				initJLM.append("\\newcommand{\\");
				initJLM.append(colStr);
				initJLM.append("}[1]{\\textcolor{");
				initJLM.append(col.getRed());
				initJLM.append(',');
				initJLM.append(col.getGreen());
				initJLM.append(',');
				initJLM.append(col.getBlue());
				initJLM.append("}{#1}} ");

			}
		}
		return initJLM;
	}

	/**
	 * Make sure GGB specific commands are defined; idempotent.
	 */
	protected static void checkFirstCallStatic() {
		if (initJLaTeXMath == null) {
			StringBuilder initJLM = DrawEquation.getJLMCommands();
			initJLaTeXMath = new TeXFormula(initJLM.toString());
		}
	}

	/**
	 * Renders LaTeX equation using JLaTeXMath
	 * 
	 * @param app
	 *            application
	 * @param geo
	 *            geo
	 * @param g2
	 *            graphics
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param serif
	 *            true for serif
	 * @param fgColor
	 *            foreground color
	 * @param bgColor
	 *            background color
	 * @param useCache
	 *            true to cache
	 * @param maxWidth
	 *            width for multiline align
	 * @param lineSpace
	 *            line space in centimeters
	 * @return dimensions of result
	 */
	final public GDimension drawEquation(final App app, final GeoElementND geo,
			final Graphics2DInterface g2, final int x, final int y,
			final String text, final GFont font, final boolean serif,
			final Color fgColor, final Color bgColor,
			boolean useCache, final Integer maxWidth,
			final Double lineSpace) {
		// TODO uncomment when \- works
		// text=addPossibleBreaks(text);

		int width = -1;
		int height = -1;
		// int depth = 0;

		int style = getLaTeXStyle(font, serif);

		// if we're exporting, we want to draw it full resolution
		if (app.isExporting() || !useCache) {
			TeXIcon icon = createIcon(text, fgColor, font, style, maxWidth,
					lineSpace);

			HasForegroundColor fg = new HasForegroundColor() {

				@Override
				public Color getForegroundColor() {
					return fgColor;
				}

			};

			icon.paintIcon(fg, g2, x, y);

			return AwtFactory.getPrototype().newDimension(icon.getIconWidth(),
					icon.getIconHeight());
		}

		Image im = null;
		try {
			final int[] ret = new int[2];
			checkFirstCall();
			im = getCachedDimensions(text, geo, fgColor, font, style, ret);

			width = ret[0];
			height = ret[1];
			// depth = ret[2];

		} catch (final Exception e) {
			Log.debug(e);
			// Write error message to Graphics View
			checkFirstCall();

			try {
				final TeXFormula formula = TeXFormula
						.getPartialTeXFormula(text);
				im = TeXFormula.asImage(formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style), convertColor(GColor.BLACK),
						convertColor(GColor.WHITE), getPixelRatio());

				// toJavaString() to help diagnose non-printable characters
				Log.warn("latex syntax error\n" + text + "\n"
						+ StringUtil.toJavaString(e.getMessage()));

			} catch (Exception e2) {

				final TeXFormula formula = TeXFormula
						.getPartialTeXFormula("\\textcolor{red}{?}");
				im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, convertColor(GColor.BLACK),
						convertColor(GColor.WHITE));

				// toJavaString() to help diagnose non-printable characters
				Log.error(
						"serious latex error\n" + text + "\n"
								+ StringUtil.toJavaString(e.getMessage()));
				Log.debug(e2);
			}
		}
		g2.scale(1 / getPixelRatio(), 1 / getPixelRatio());
		g2.drawImage(im, (int) (x * getPixelRatio()), (int) (y * getPixelRatio()));
		g2.scale(getPixelRatio(), getPixelRatio());
		if (width == -1) {
			width = im.getWidth();
		}
		if (height == -1) {
			height = im.getHeight();
		}

		return AwtFactory.getPrototype().newDimension(width, height);
	}

	public double getPixelRatio() {
		return 1;
	}

	/**
	 * @param text
	 *            LaTeX
	 * @param fgColor
	 *            text color
	 * @param font
	 *            font
	 * @param style
	 *            combines TeXFormula.BOLD, TeXFormula.ITALIC,
	 *            TeXFormula.SANSSERIF
	 * @param maxWidth
	 *            width for aligned multirow
	 * @param lineSpace
	 *            space between lines
	 * @return rendered LaTeX
	 */
	public TeXIcon createIcon(String text, Color fgColor, GFont font, int style,
			Integer maxWidth, Double lineSpace) {
		return createIcon(text, fgColor, font.getSize() + 3, style);
	}

	/**
	 * @param text text
	 * @param fgColor text color
	 * @param fontSize font size in LaTeX font (should be GGB font size + 3)
	 * @param style latex font style
	 * @return icon
	 */
	public TeXIcon createIcon(String text, Color fgColor, int fontSize, int style) {
		checkFirstCall();
		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			// if (maxWidth == null) {
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					fontSize, style, fgColor);
			// } else {
			// icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
			// font.getSize() + 3, TeXLength.Unit.CM,
			// maxWidth.intValue(), TeXConstants.Align.LEFT,
			// TeXLength.Unit.CM, lineSpace.doubleValue());
			// }
		} catch (final Error e) {
			Log.debug(e);
			Log.debug("MyError LaTeX parse exception:" + e.getMessage() + "\n"
					+ text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula("?");
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					fontSize, style, fgColor);

			// formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
			// TeXLength.Unit.CM, 4f, TeXConstants.Align.LEFT,
			// TeXLength.Unit.CM, 0.5f);
			formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 4,
					TeXConstants.Align.LEFT);

		} catch (final Exception e) {
			Log.debug(e);
			Log.debug(
					"LaTeX parse exception1: " + e.getMessage() + "\n" + text);
			// Write error message to Graphics View
			try {
				formula = TeXFormula.getPartialTeXFormula(text);

				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						fontSize, style, fgColor);
			} catch (Exception e2) {
				// e2.printStackTrace();
				Log.debug("LaTeX parse exception2: " + e2.getMessage() + "\n"
						+ text);
				formula = TeXFormula
						.getPartialTeXFormula("?");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						fontSize, style, fgColor);
			}
		}
		icon.setInsets(new Insets(1, 1, 1, 1));
		return icon;
	}

	/**
	 * @param app
	 *            application
	 * @param text
	 *            text
	 * @param font
	 *            font
	 * @param serif
	 *            true for serif
	 * @param maxWidth
	 *            width for alignment
	 * @param lineSpace
	 *            line space in centimeters
	 * @return dimensions of result
	 */
	final public GDimension measureEquationJLaTeXMath(final App app,
			final String text,
			final GFont font, final boolean serif, final Integer maxWidth,
			final Double lineSpace) {

		checkFirstCall();
		GColor fgColor = GColor.BLACK;
		int style = getLaTeXStyle(font, serif);

		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			// if (maxWidth == null) {
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, convertColor(fgColor));
			// } else {
			// icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
			// font.getSize() + 3, TeXLength.Unit.CM,
			// maxWidth.intValue(), TeXConstants.Align.LEFT,
			// TeXLength.Unit.CM, lineSpace.doubleValue());
			// }
		} catch (final MyError e) {
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, convertColor(fgColor));

			// formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 15,
			// TeXLength.Unit.CM, 4f, TeXConstants.Align.LEFT,
			// TeXLength.Unit.CM, 0.5f);
			formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 4f,
					TeXConstants.Align.LEFT);

		} catch (final Exception e) {
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, convertColor(fgColor));

		}
		icon.setInsets(new Insets(1, 1, 1, 1));

		return AwtFactory.getPrototype().newDimension(icon.getIconWidth(),
				icon.getIconHeight());
	}

	/**
	 * @param serif
	 *            whether this is serif font
	 * @return style as required by JLaTeXMath
	 */
	public int getLaTeXStyle(GFont font, boolean serif) {
		int style = 0;
		if (font.isBold()) {
			style = style | TeXFont.BOLD;
		}
		if (font.isItalic()) {
			style = style | TeXFont.ITALIC;
		}
		if (!serif) {
			style = style | TeXFont.SANSSERIF;
		}

		return style;
	}
}

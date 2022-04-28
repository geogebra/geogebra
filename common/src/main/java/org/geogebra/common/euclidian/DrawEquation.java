package org.geogebra.common.euclidian;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.renderer.share.TeXConstants;
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

		int style = font.getLaTeXStyle(serif);

		// if we're exporting, we want to draw it full resolution
		if (app.isExporting() || !useCache) {

			// Application.debug("creating new icon for: "+text);
			TeXIcon icon = createIcon(text, fgColor, font, style, maxWidth,
					lineSpace, app);

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
			checkFirstCall(app);
			im = getCachedDimensions(text, geo, fgColor, font, style, ret);

			width = ret[0];
			height = ret[1];
			// depth = ret[2];

		} catch (final Exception e) {
			Log.debug(e);
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View
			checkFirstCall(app);

			try {
				final TeXFormula formula = TeXFormula
						.getPartialTeXFormula(text);
				im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, convertColor(GColor.BLACK),
						convertColor(GColor.WHITE));

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

		g2.drawImage(im, x, y);

		if (width == -1) {
			width = im.getWidth();
		}
		if (height == -1) {
			height = im.getHeight();
		}

		return AwtFactory.getPrototype().newDimension(width, height);
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
	 * @param app
	 *            application
	 * @return rendered LaTeX
	 */
	public TeXIcon createIcon(String text, Color fgColor, GFont font, int style,
			Integer maxWidth, Double lineSpace, App app) {
		checkFirstCall(app);
		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			// if (maxWidth == null) {
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, fgColor);
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
					font.getSize() + 3, style, fgColor);

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
						font.getSize() + 3, style, fgColor);
			} catch (Exception e2) {
				// e2.printStackTrace();
				Log.debug("LaTeX parse exception2: " + e2.getMessage() + "\n"
						+ text);
				formula = TeXFormula
						.getPartialTeXFormula("?");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);
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

		checkFirstCall(app);
		GColor fgColor = GColor.BLACK;
		int style = font.getLaTeXStyle(serif);

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
			// e.printStackTrace();
			// Application.debug("MyError LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
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
			// e.printStackTrace();
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, convertColor(fgColor));

		}
		icon.setInsets(new Insets(1, 1, 1, 1));

		return AwtFactory.getPrototype().newDimension(icon.getIconWidth(),
				icon.getIconHeight());

	}
}

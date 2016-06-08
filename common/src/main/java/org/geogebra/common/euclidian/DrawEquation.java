package org.geogebra.common.euclidian;


import java.util.HashMap;
import java.util.Iterator;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.renderer.share.TeXConstants;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXIcon;
//import com.himamis.retex.renderer.share.cache.JLaTeXMathCache;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.HasForegroundColor;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Insets;

public abstract class DrawEquation {


	public static void appendFractionStart(StringBuilder sb, StringTemplate tpl) {
			sb.append(" \\frac{ ");
	}

	public static void appendFractionMiddle(StringBuilder sb, StringTemplate tpl) {
			sb.append(" }{ ");
	}

	public static void appendFractionEnd(StringBuilder sb, StringTemplate tpl) {
		sb.append(" } ");
	}

	public static void appendInfinity(StringBuilder sb, StringTemplate tpl) {
			sb.append(" \\infty ");
	}

	public static void appendMinusInfinity(StringBuilder sb, StringTemplate tpl) {
			sb.append(" - \\infty ");
	}

	public static void appendNegation(StringBuilder sb, StringTemplate tpl) {
			sb.append("-");
	}

	/**
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
	 * @return dimensions of result
	 */
	public abstract GDimension drawEquation(App app, GeoElementND geo,
			GGraphics2D g2, int x, int y, String text,
 GFont font,
			boolean serif, GColor fgColor,
			GColor bgColor, boolean useCache, boolean updateAgain,
			Runnable callback);

	public static StringBuilder getJLMCommands() {
		StringBuilder initJLM = new StringBuilder();

		// used in ExpressionNode
		// (for serializing GeoGebra expressions to LaTeX)
		initJLM.append("\\DeclareMathOperator{\\sech}{sech} ");
		initJLM.append("\\DeclareMathOperator{\\csch}{csch} ");
		initJLM.append("\\DeclareMathOperator{\\erf}{erf} ");
		initJLM.append("\\DeclareMathOperator{\\sgn}{sgn} ");
		initJLM.append("\\DeclareMathOperator{\\round}{round} ");
		initJLM.append("\\DeclareMathOperator{\\Ci}{Ci} ");
		initJLM.append("\\DeclareMathOperator{\\Si}{Si} ");
		initJLM.append("\\DeclareMathOperator{\\Ei}{Ei} ");
		initJLM.append("\\DeclareMathOperator{\\acosh}{acosh} ");
		initJLM.append("\\DeclareMathOperator{\\asinh}{asinh} ");
		initJLM.append("\\DeclareMathOperator{\\atanh}{atanh} ");
		initJLM.append("\\DeclareMathOperator{\\real}{real} ");
		initJLM.append("\\DeclareMathOperator{\\imaginary}{imaginary} ");
		initJLM.append("\\DeclareMathOperator{\\fractionalPart}{fractionalPart} ");
		initJLM.append("\\DeclareMathOperator{\\round}{round} ");
		initJLM.append("\\newcommand{\\space}[0]{\\ } ");
		initJLM.append("\\newcommand{\\dollar}[0]{\\$} ");

		// #4068 so that we can use \questeq in Java and HTML5
		initJLM.append("\\newcommand{\\questeq}[0]{ \\stackrel{ \\small ?}{=} } ");
		initJLM.append("\\newcommand{\\pcdot}{\\space} ");

		HashMap<String, GColor> ggbCols = GeoGebraColorConstants
				.getGeoGebraColors();

		Iterator<String> it = ggbCols.keySet().iterator();

		// add commands eg \red{text}
		// same commands added to MathQuillGGB
		while (it.hasNext()) {
			String colStr = it.next();

			// can't have command eg \grey2
			if (!Character.isDigit(colStr.charAt(colStr.length() - 1))) {
				GColor col = ggbCols.get(colStr);

				// eg
				// initJLM.append("\\newcommand{\\red}[1]{\\textcolor{255,0,0}{#1}} ");
				initJLM.append("\\newcommand{\\");
				initJLM.append(colStr);
				initJLM.append("}[1]{\\textcolor{");
				initJLM.append(col.getRed());
				initJLM.append(',');
				initJLM.append(col.getGreen());
				initJLM.append(',');
				initJLM.append(col.getBlue());
				initJLM.append("}{#1}} ");

				// generate JavaScript code for MathQuillGGB
				// System.out.println("LatexCmds."+colStr+" = bind(Style, '\\\\"+colStr+"', '<span style=\"color:#"+StringUtil.toHexString(col)+"\"></span>');");

			}
		}
		return initJLM;
	}


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
	final public GDimension drawEquation(final App app, final GeoElementND geo,
			final Graphics2DInterface g2, final int x, final int y,
			final String text,
			final GFont font, final boolean serif,
			final com.himamis.retex.renderer.share.platform.graphics.Color fgColor,
			final com.himamis.retex.renderer.share.platform.graphics.Color bgColor,
			final boolean useCache, final Integer maxWidth,
			final Float lineSpace) {
		// TODO uncomment when \- works
		// text=addPossibleBreaks(text);

		int width = -1;
		int height = -1;
		// int depth = 0;



		int style = font.getLaTeXStyle(serif);

		// if we're exporting, we want to draw it full resolution
		if (app.isExporting() || !useCache) {

			// Application.debug("creating new icon for: "+text);
			TeXIcon icon = createIcon(text, fgColor, font, style,
					maxWidth, lineSpace, app);

			HasForegroundColor fg = new HasForegroundColor() {

				public com.himamis.retex.renderer.share.platform.graphics.Color getForegroundColor() {
					return fgColor;
				}

			};

			icon.paintIcon(fg, g2, x, y);

			return AwtFactory.prototype.newDimension(icon.getIconWidth(),
					icon.getIconHeight());

		}


		Image im = null;
		try {
			final int ret[] = new int[2];
			checkFirstCall(app);
			im = getCachedDimensions(text, geo, fgColor, font, style, ret);

			width = ret[0];
			height = ret[1];
			// depth = ret[2];

		} catch (final Exception e) {
			// Application.debug("LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View
			checkFirstCall(app);
			final TeXFormula formula = TeXFormula.getPartialTeXFormula(text);
			im = formula.createBufferedImage(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, convertColor(GColor.BLACK),
					convertColor(GColor.WHITE));
		}

		g2.drawImage(im, x, y);

		if (width == -1) {
			width = im.getWidth();
		}
		if (height == -1) {
			height = im.getHeight();
		}

		return AwtFactory.prototype.newDimension(width, height);
	}

	public TeXIcon createIcon(String text, Color fgColor, GFont font,
			int style, Integer maxWidth,
			Float lineSpace, App app) {
		checkFirstCall(app);
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
			// Application.debug("MyError LaTeX parse exception:
			// "+e.getMessage()+"\n"+text);
			// Write error message to Graphics View

			formula = TeXFormula.getPartialTeXFormula(text);
			icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
					font.getSize() + 3, style, fgColor);

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
						font.getSize() + 3, style, fgColor);
			} catch (Exception e2) {
				Log.debug("LaTeX parse exception: " + e.getMessage() + "\n"
						+ text);
				formula = TeXFormula
						.getPartialTeXFormula(
								"\\text{"
								+ app.getLocalization().getError(
										"CAS.GeneralErrorMessage")
								+ "}");
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, fgColor);
			}
		}
		icon.setInsets(new Insets(1, 1, 1, 1));
		return icon;
	}

	protected abstract Image getCachedDimensions(String text, GeoElementND geo,
			Color fgColor, GFont font, int style, int[] ret);

	public abstract void checkFirstCall(App app);

	public abstract Color convertColor(GColor bLACK);

	public abstract GDimension measureEquation(App app, GeoElement geo0,
			int minValue,
			int minValue2, String text, GFont font, boolean b);

	final public GDimension measureEquationJLaTeXMath(final App app,
			final GeoElement geo, final int x, final int y, final String text,
			final GFont font, final boolean serif, final Integer maxWidth,
			final Float lineSpace) {

		checkFirstCall(app);
		GColor fgColor = GColor.BLACK;
		int style = font.getLaTeXStyle(serif);

		TeXFormula formula;
		TeXIcon icon;

		try {
			formula = new TeXFormula(text);

			if (maxWidth == null) {
				icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY,
						font.getSize() + 3, style, convertColor(fgColor));
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
					font.getSize() + 3, style, convertColor(fgColor));

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
					font.getSize() + 3, style, convertColor(fgColor));

		}
		icon.setInsets(new Insets(1, 1, 1, 1));

		return AwtFactory.prototype.newDimension(icon.getIconWidth(),
				icon.getIconHeight());

	}
}

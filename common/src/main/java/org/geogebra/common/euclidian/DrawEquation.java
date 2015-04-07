package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.Unicode;

public abstract class DrawEquation {

	/*
	 * make sure we always return LATEX (or MATHML, not currently used) so that
	 * eg FractionText will work when part of another text and not return eg
	 * (1)/(3)
	 */
	private static StringType checkStringType(StringTemplate tpl) {
		switch (tpl.getStringType()) {
		case LATEX:
			return StringType.LATEX;

		case MATHML:
			return StringType.MATHML;

		default:
			// should really be app.getPreferredFormulaRenderingType()
			return StringType.LATEX;
		}
	}

	public static void appendFormulaStart(StringBuilder sb, StringTemplate tpl) {
		final StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case MATHML:
			sb.append("<apply>");
			break;
		}
	}

	public static void appendFractionStart(StringBuilder sb, StringTemplate tpl) {

		StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case LATEX:
			sb.append(" \\frac{ ");
			break;

		case MATHML:
			sb.append("<divide/><cn>");
			break;

		default:
			sb.append("(");
		}

	}

	public static void appendFractionMiddle(StringBuilder sb, StringTemplate tpl) {
		StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case LATEX:
			sb.append(" }{ ");
			break;

		case MATHML:
			sb.append("</cn><cn>");

			break;

		default:
			sb.append(")/(");
		}
	}

	public static void appendFractionEnd(StringBuilder sb, StringTemplate tpl) {
		StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case LATEX:
			sb.append(" } ");
			break;

		case MATHML:
			sb.append("</cn></apply>");
			break;

		default:
			sb.append(")");
		}
	}

	public static void appendInfinity(StringBuilder sb, StringTemplate tpl) {
		StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case LATEX:
			sb.append(" \\infty ");
			break;

		case MATHML:
			sb.append("<infinity/>");
			break;

		default:
			sb.append(Unicode.Infinity);
		}
	}

	public static void appendMinusInfinity(StringBuilder sb, StringTemplate tpl) {
		StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case LATEX:
			sb.append(" - \\infty ");
			break;

		case MATHML:
			sb.append("<apply><minus/><infinity/></apply>");
			break;

		default:
			sb.append('-');
			sb.append(Unicode.Infinity);
		}
	}

	public static void appendNegation(StringBuilder sb, StringTemplate tpl) {
		final StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case MATHML:
			sb.append("<minus/>");
			break;

		default:
			sb.append("-");
		}
	}

	public static void appendNumber(StringBuilder sb, StringTemplate tpl,
			String num) {
		StringType stringType = checkStringType(tpl);

		switch (stringType) {
		case MATHML:
			sb.append("<cn>");
			sb.append(num);
			sb.append("</cn>");
			break;

		default:
			sb.append(num);
		}
	}

	/**
	 * @param app
	 *            application
	 * @param b
	 *            true to use Java fonts
	 */
	public abstract void setUseJavaFontsForLaTeX(App app, boolean b);

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
	public abstract org.geogebra.common.awt.GDimension drawEquation(App app,
			GeoElement geo, org.geogebra.common.awt.GGraphics2D g2, int x, int y,
			String text, org.geogebra.common.awt.GFont font, boolean serif,
			org.geogebra.common.awt.GColor fgColor,
			org.geogebra.common.awt.GColor bgColor, boolean useCache,
			boolean updateAgain);

}

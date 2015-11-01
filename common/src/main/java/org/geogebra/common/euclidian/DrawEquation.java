package org.geogebra.common.euclidian;

import java.util.HashMap;
import java.util.Iterator;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoGebraColorConstants;

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
	public abstract org.geogebra.common.awt.GDimension drawEquation(App app,
			GeoElement geo, org.geogebra.common.awt.GGraphics2D g2, int x, int y,
			String text, org.geogebra.common.awt.GFont font, boolean serif,
			org.geogebra.common.awt.GColor fgColor,
			org.geogebra.common.awt.GColor bgColor, boolean useCache,
			boolean updateAgain, Runnable callback);

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

		// #4068 so that we can use \questeq in Java and HTML5
		initJLM.append("\\newcommand{\\questeq}[0]{ \\stackrel{ \\small ?}{=} } ");

		HashMap<String, org.geogebra.common.awt.GColor> ggbCols = GeoGebraColorConstants
				.getGeoGebraColors();

		Iterator<String> it = ggbCols.keySet().iterator();

		// add commands eg \red{text}
		// same commands added to MathQuillGGB
		while (it.hasNext()) {
			String colStr = it.next();

			// can't have command eg \grey2
			if (!Character.isDigit(colStr.charAt(colStr.length() - 1))) {
				org.geogebra.common.awt.GColor col = ggbCols.get(colStr);

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

}

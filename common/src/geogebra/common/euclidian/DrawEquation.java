package geogebra.common.euclidian;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.util.Unicode;

public abstract class DrawEquation {

	public static void appendFractionStart(StringBuilder sb, StringTemplate tpl) {
		switch (tpl.getStringType()) {
		case LATEX:
			sb.append(" \\frac{ ");
			break;

		case MATHML:
			sb.append("<apply><divide/><cn>");
			break;

		default:
			sb.append("(");
		}

	}

	public static void appendFractionMiddle(StringBuilder sb, StringTemplate tpl) {
		switch (tpl.getStringType()) {
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
		switch (tpl.getStringType()) {
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
		switch (tpl.getStringType()) {
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
		switch (tpl.getStringType()) {
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

	public static void appendNumber(StringBuilder sb, StringTemplate tpl,
			String num) {
		switch (tpl.getStringType()) {
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
	 * @param app application
	 * @param b true to use Java fonts
	 */
	public abstract void setUseJavaFontsForLaTeX(App app, boolean b);

	/**
	 * @param app application
	 * @param geo geo
	 * @param g2 graphics
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param text text
	 * @param font font
	 * @param serif true for serif
	 * @param fgColor foreground color
	 * @param bgColor background color
	 * @param useCache true to cache
	 * @return dimensions of result
	 */
	public abstract geogebra.common.awt.GDimension drawEquation(App app,
			GeoElement geo, geogebra.common.awt.GGraphics2D g2, int x, int y, String text,
			geogebra.common.awt.GFont font, boolean serif, geogebra.common.awt.GColor fgColor, geogebra.common.awt.GColor bgColor,
			boolean useCache); 



}

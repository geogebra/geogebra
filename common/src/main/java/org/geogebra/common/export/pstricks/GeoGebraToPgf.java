package org.geogebra.common.export.pstricks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.DrawPoint;
import org.geogebra.common.export.UnicodeTeX;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoAngleLines;
import org.geogebra.common.kernel.algos.AlgoAnglePoints;
import org.geogebra.common.kernel.algos.AlgoAngleVector;
import org.geogebra.common.kernel.algos.AlgoAngleVectors;
import org.geogebra.common.kernel.algos.AlgoBoxPlot;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.algos.AlgoSlope;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.cas.AlgoIntegralFunctions;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoRayND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Generates PGF/Tikz string representation of current view.
 * 
 * 
 */
public abstract class GeoGebraToPgf extends GeoGebraExport {
	private static final int FORMAT_LATEX = 0;
	private static final int FORMAT_PLAIN_TEX = 1;
	private static final int FORMAT_CONTEXT = 2;
	private static final int FORMAT_BEAMER = 3;
	private int functionIdentifier = 0;
	private boolean forceGnuplot = false;
	private boolean gnuplotWarning = false;
	private boolean hatchWarning = false;

	/**
	 * @param app
	 *            application
	 */
	public GeoGebraToPgf(App app) {
		super(app);
	}

	@Override
	public void generateAllCode() {

		format = frame.getFormat();
		forceGnuplot = frame.getGnuplot();
		// init unit variables
		xunit = frame.getXUnit();
		yunit = frame.getYUnit();
		// Initialize new StringBuilder for Pstricks code
		// and CustomColor
		code = new StringBuilder();
		codePoint = new StringBuilder();
		codePreamble = new StringBuilder();

		codeFilledObject = new StringBuilder();
		codeBeginDoc = new StringBuilder();
		customColor = new HashMap<>();
		if (format == GeoGebraToPgf.FORMAT_LATEX) {
			codePreamble.append("\\documentclass[");
			codePreamble.append(frame.getFontSize());
			codePreamble.append("pt]{article}\n");
			codePreamble.append("\\usepackage{pgf,tikz");
			codePreamble.append(",pgfplots}\n");
			codePreamble.append("\\pgfplotsset{compat=1.15");

			codePreamble.append(
					"}\n\\usepackage{mathrsfs}\n\\usetikzlibrary{arrows}\n\\pagestyle{empty}\n");
			codeBeginDoc.append(
					"\\begin{tikzpicture}[line cap=round,line join=round,>=triangle 45,");

			addScale(codeBeginDoc);

			codeBeginDoc.append("]\n");
		} else if (format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
			codePreamble.append("%Uncomment next line if XeTeX is used\n");
			codePreamble.append("%\\def\\pgfsysdriver{pgfsys-xetex.def}\n\n");
			codePreamble.append("\\input pgf.tex\n\\input tikz.tex\n");
			codePreamble.append("\\usetikzlibrary{arrows}\n");
			codePreamble.append("\\baselineskip=");
			codePreamble.append(frame.getFontSize());
			codePreamble.append("pt\n\\hsize=6.3truein\n\\vsize=8.7truein\n");

			codeBeginDoc.append(
					"\\tikzpicture[line cap=round,line join=round,>=triangle 45,x=");
			codeBeginDoc.append(xunit);
			codeBeginDoc.append("cm,y=");
			codeBeginDoc.append(yunit);
			codeBeginDoc.append("cm]\n");
		} else if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
			codePreamble
					.append("\\setupbodyfont[" + frame.getFontSize() + "pt]\n");
			codePreamble.append("\\usemodule[tikz]\n\\usemodule[pgf]\n");
			codePreamble.append(
					"\\usetikzlibrary[arrows]\n\\setuppagenumbering[location=]\n");

			codeBeginDoc.append("\\startTEXpage\n\\starttikzpicture[");
			codeBeginDoc.append("line cap=round,line join=round,>=triangle 45,x=");
			codeBeginDoc.append(xunit);
			codeBeginDoc.append("cm,y=");
			codeBeginDoc.append(yunit);
			codeBeginDoc.append("cm]\n");
		} else if (format == GeoGebraToPgf.FORMAT_BEAMER) {
			codePreamble.append("\\documentclass[" + frame.getFontSize()
					+ "pt]{beamer}\n"
					+ "\\usepackage{pgf,tikz}\n\\usetikzlibrary{arrows}\n\\pagestyle{empty}\n");
			codeBeginDoc.append("\\begin{frame}\n");
			codeBeginDoc.append(
					"\\begin{tikzpicture}[line cap=round,line join=round,>=triangle 45,x=");
			codeBeginDoc.append(xunit);
			codeBeginDoc.append("cm,y=");
			codeBeginDoc.append(yunit);
			codeBeginDoc.append("cm]\n");
		}
		if (format == FORMAT_BEAMER) {
			format = FORMAT_LATEX;
		}

		if (euclidianView.getShowXaxis() || euclidianView.getShowYaxis()) {
			drawNiceAxesGrid();
		} else if (euclidianView.getShowGrid()) {
			drawGrid();
		}
		// Clipping
		codeFilledObject.append("\\clip");
		writePoint(xmin, ymin, codeFilledObject);
		codeFilledObject.append(" rectangle ");
		writePoint(xmax, ymax, codeFilledObject);
		codeFilledObject.append(";\n");

		/*
		 * get all objects from construction and "draw" them by creating PGF
		 * code
		 */

		drawAllElements();
		/*
		 * Object [] geos =
		 * kernel.getConstruction().getGeoSetConstructionOrder().toArray(); for
		 * (int i=0;i<geos.length;i++){ GeoElement g = (GeoElement)(geos[i]);
		 * drawGeoElement(g,false); }
		 */
		// add code for Points and Labels
		if (codePoint.length() != 0 && format == GeoGebraToPgf.FORMAT_LATEX) {
			codePoint.insert(0, "\\begin{scriptsize}\n");
			codePoint.append("\\end{scriptsize}\n");

		}

		// add code for Points and Labels
		code.append(codePoint);
		// Close Environment tikzpicture
		if (format == GeoGebraToPgf.FORMAT_LATEX) {
			if (euclidianView.getShowXaxis()
					|| euclidianView.getShowYaxis()) {
				code.append("\\end{axis}\n");
			}

			code.append("\\end{tikzpicture}\n");
			if (isBeamer) {
				code.append("\\end{frame}\n");
			}
			code.append("\\end{document}");

			codeBeginDoc.insert(0, "\\begin{document}\n");
		} else if (format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
			code.append("\\endtikzpicture\n\\bye\n");

		} else if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
			code.append("\\stoptikzpicture\n\\stopTEXpage\n\\stoptext");
			codeBeginDoc.insert(0, "\\starttext\n");
		}
		/*
		 * String formatFont=resizeFont(app.getFontSize()); if
		 * (null!=formatFont){ codeBeginPic.insert(0,formatFont+"\n");
		 * code.append("}\n"); }
		 */
		code.insert(0, codeFilledObject + "");
		code.insert(0, codeBeginDoc + "");
		code.insert(0, codePreamble + "");
		frame.write(code);
	}

	@Override
	protected void drawLocus(GeoLocus g) {
		ArrayList<MyPoint> ll = g.getPoints();
		Iterator<MyPoint> it = ll.iterator();
		startBeamer(code);
		code.append("\\draw");
		String s = lineOptionCode(g, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
		}
		code.append(s);
		boolean first = true;
		boolean out = false;
		while (it.hasNext()) {
			MyPoint mp = it.next();
			double x = mp.x;
			double y = mp.y;
			boolean b = mp.getLineTo();
			if (x > xmin && x < xmax && y > ymin && y < ymax) {
				if (b && !first) {
					code.append(" -- ");
				} else if (first) {
					first = false;
				}
				writePoint(x, y, code);
				out = false;
			} else if (!first && mp.getLineTo() && !out) {
				out = true;
				code.append(" -- ");
				writePoint(x, y, code);
			}

			else {
				first = true;
				out = false;
			}
		}
		code.append(";\n");
		endBeamer(code);
	}

	@Override
	protected void drawBoxPlot(GeoNumeric geo) {
		AlgoBoxPlot algo = ((AlgoBoxPlot) geo.getParentAlgorithm());
		double y = algo.getA().getDouble();
		double height = algo.getB().getDouble();
		double[] lf = algo.getLeftBorders();
		double min = lf[0];

		startBeamer(codeFilledObject);
		codeFilledObject.append("\\draw ");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
		}
		codeFilledObject.append(s);
		// Min vertical bar
		writePoint(min, y - height, codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(min, y + height, codeFilledObject);
		codeFilledObject.append(" ");
		// Max vertical bar
		double max = lf[4];
		writePoint(max, y - height, codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(max, y + height, codeFilledObject);
		codeFilledObject.append(" ");
		// Med vertical bar
		double med = lf[2];
		writePoint(med, y - height, codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(med, y + height, codeFilledObject);
		// Min-q1 horizontal
		codeFilledObject.append(" ");
		writePoint(min, y, codeFilledObject);
		codeFilledObject.append("-- ");
		double q1 = lf[1];
		writePoint(q1, y, codeFilledObject);
		// q3-max
		codeFilledObject.append(" ");
		double q3 = lf[3];
		writePoint(q3, y, codeFilledObject);
		codeFilledObject.append("-- ");
		writePoint(max, y, codeFilledObject);
		codeFilledObject.append(";\n");
		if (isBeamer) {
			codeFilledObject.append("  ");
		}
		// Rectangle q1-q3
		codeFilledObject.append("\\draw");
		if (s.length() != 0) {
			codeFilledObject.append(s);
		}
		writePoint(q1, y - height, codeFilledObject);
		codeFilledObject.append(" rectangle ");
		writePoint(q3, y + height, codeFilledObject);
		codeFilledObject.append(";\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawSumTrapezoidal(GeoNumeric geo) {
		AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) geo
				.getParentAlgorithm();
		int n = algo.getIntervals();
		double[] y = algo.getValues();
		double[] x = algo.getLeftBorder();
		// Trapezoidal sum
		startBeamer(codeFilledObject);
		for (int i = 0; i < n; i++) {

			codeFilledObject.append("\\draw");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				codeFilledObject.append("[" + s + "] ");
			}
			writePoint(x[i], 0, codeFilledObject);
			codeFilledObject.append(" -- ");
			writePoint(x[i + 1], 0, codeFilledObject);
			codeFilledObject.append(" -- ");
			writePoint(x[i + 1], y[i + 1], codeFilledObject);
			codeFilledObject.append(" -- ");
			writePoint(x[i], y[i], codeFilledObject);
			codeFilledObject.append(" -- cycle;\n");
			if (i != n - 1 && isBeamer) {
				codeFilledObject.append("  ");
			}
		}
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawSumUpperLower(GeoNumeric geo) {
		AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) geo
				.getParentAlgorithm();
		int n = algo.getIntervals();
		double step = algo.getStep();
		double[] y = algo.getValues();
		double[] x = algo.getLeftBorder();
		startBeamer(codeFilledObject);
		for (int i = 0; i < n; i++) {
			codeFilledObject.append("\\draw");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				codeFilledObject.append("[" + s + "] ");
			}
			writePoint(x[i], 0, codeFilledObject);
			codeFilledObject.append(" rectangle ");
			writePoint(x[i] + step, y[i], codeFilledObject);
			codeFilledObject.append(";\n");
			if (i != -1 && isBeamer) {
				codeFilledObject.append("  ");
			}
		}
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawIntegralFunctions(GeoNumeric geo) {
		// command: \draw[option]{[domain=a:b,samples=..] plot(\x,{f(\x)})
		// }--(b,g(b)) -- {[domain=b:a,samples=..] plot(\x,{g(\x)}) }--(a,f(a))
		// --cycle;
		AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo
				.getParentAlgorithm();

		// function f
		GeoFunction f = algo.getF();

		String value = f.toValueString(getStringTemplate());
		value = killSpace(StringUtil.toLaTeXString(value, true));
		boolean plotWithGnuplot = warningFunc(value, "tan(")
				|| warningFunc(value, "cosh(") || warningFunc(value, "acosh(")
				|| warningFunc(value, "asinh(") || warningFunc(value, "atanh(")
				|| warningFunc(value, "sinh(") || warningFunc(value, "tanh(");
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\draw");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			codeFilledObject.append("[" + s + "] ");
		}
		codeFilledObject.append("{");
		// between a and b
		double a = algo.getA().getDouble();
		double b = algo.getB().getDouble();

		if (plotWithGnuplot) {
			codeFilledObject.append(" plot[raw gnuplot, id=func");
			codeFilledObject.append(functionIdentifier);
			functionIdentifier++;
			codeFilledObject.append("] function{set samples 100; set xrange [");
			codeFilledObject.append(a);
			codeFilledObject.append(":");
			codeFilledObject.append(b);
			codeFilledObject.append("]; plot ");
			value = value.replaceAll("\\^", "**");
			codeFilledObject.append(value);
			codeFilledObject.append("}");
		} else {
			codeFilledObject.append("[");
			codeFilledObject.append("smooth,samples=50,domain=");
			codeFilledObject.append(a);
			codeFilledObject.append(":");
			codeFilledObject.append(b);
			codeFilledObject.append("] plot");
			codeFilledObject.append("(\\x,{");
			value = replaceX(value, "\\x");
			codeFilledObject.append(value);
			codeFilledObject.append("})");
		}
		// function g
		GeoFunction g = algo.getG();
		codeFilledObject.append("} -- ");
		writePoint(b, g.value(b), codeFilledObject);
		codeFilledObject.append(" {");
		value = g.toValueString(getStringTemplate());
		value = killSpace(StringUtil.toLaTeXString(value, true));
		plotWithGnuplot = warningFunc(value, "tan(")
				|| warningFunc(value, "cosh(") || warningFunc(value, "acosh(")
				|| warningFunc(value, "asinh(") || warningFunc(value, "atanh(")
				|| warningFunc(value, "sinh(") || warningFunc(value, "tanh(");
		if (plotWithGnuplot) {
			codeFilledObject.append("-- plot[raw gnuplot, id=func");
			codeFilledObject.append(functionIdentifier);
			functionIdentifier++;

			codeFilledObject.append(
					"] function{set parametric ; set samples 100; set trange [");
			codeFilledObject.append(a);
			codeFilledObject.append(":");
			codeFilledObject.append(b);
			codeFilledObject.append("]; plot ");
			String variable = format(b + a) + "-t";

			codeFilledObject.append(variable);
			codeFilledObject.append(",");
			value = replaceX(value, variable);
			value = value.replaceAll("\\^", "**");
			codeFilledObject.append(value);
			codeFilledObject.append("}");
		} else {
			codeFilledObject.append("[");
			codeFilledObject.append("smooth,samples=50,domain=");
			codeFilledObject.append(b);
			codeFilledObject.append(":");
			codeFilledObject.append(a);
			codeFilledObject.append("] -- plot");
			codeFilledObject.append("(\\x,{");
			value = replaceX(value, "\\x");
			codeFilledObject.append(value);
			codeFilledObject.append("})");
		}
		codeFilledObject.append("} -- ");
		writePoint(a, f.value(a), codeFilledObject);
		codeFilledObject.append(" -- cycle;\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawIntegral(GeoNumeric geo) {
		// command: \plot[option]
		// plot[domain]{\pstplot{a}{b}{f(x)}\lineto(b,0)\lineto(a,0)\closepath}
		AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo
				.getParentAlgorithm();
		// function f
		GeoFunction f = algo.getFunction();
		// between a and b
		double a = algo.getA().getDouble();
		double b = algo.getB().getDouble();
		if (a == Double.NEGATIVE_INFINITY) {
			a = xmin;
		}
		if (b == Double.POSITIVE_INFINITY) {
			b = xmax;
		}
		String value = f.toValueString(getStringTemplate());
		value = killSpace(StringUtil.toLaTeXString(value, true));
		boolean plotWithGnuplot = warningFunc(value, "tan(")
				|| warningFunc(value, "cosh(") || warningFunc(value, "acosh(")
				|| warningFunc(value, "asinh(") || warningFunc(value, "atanh(")
				|| warningFunc(value, "sinh(") || warningFunc(value, "tanh(");
		startBeamer(codeFilledObject);
		if (!isLatexFunction(f.toValueString(StringTemplate.noLocalDefault))) {
			double af = xmin;
			double bf = xmax;
			if (f.hasInterval()) {
				af = f.getIntervalMin();
				bf = f.getIntervalMax();
			}
			f.setInterval(a, b);
			drawFunction(f, code, true, geo);
			f.setInterval(af, bf);
			if (f.isEuclidianVisible()) {
				drawFunction(f, code, false, geo);
			}
		} else {
			codeFilledObject.append("\\draw");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				codeFilledObject.append("[");
				codeFilledObject.append(s);
			}

			if (plotWithGnuplot) {
				if (s.length() != 0) {
					codeFilledObject.append("]");
				}
				codeFilledObject.append(" plot[raw gnuplot, id=func");
				codeFilledObject.append(functionIdentifier);
				functionIdentifier++;
				codeFilledObject
						.append("] function{set samples 100; set xrange [");
				codeFilledObject.append(a);
				codeFilledObject.append(":");
				codeFilledObject.append(b);
				codeFilledObject.append("]; plot ");
				value = value.replaceAll("\\^", "**");
				codeFilledObject.append(value);
				codeFilledObject.append("}");
			} else {
				if (s.length() != 0) {
					codeFilledObject.append(", ");
				} else {
					codeFilledObject.append("[");
				}
				codeFilledObject.append("smooth,samples=50,domain=");
				codeFilledObject.append(a);
				codeFilledObject.append(":");
				codeFilledObject.append(b);
				codeFilledObject.append("] plot");
				codeFilledObject.append("(\\x,{");
				value = replaceX(value, "\\x");
				codeFilledObject.append(value);
				codeFilledObject.append("})");
			}
			codeFilledObject.append(" -- ");
			writePoint(b, 0, codeFilledObject);
			codeFilledObject.append(" -- ");
			writePoint(a, 0, codeFilledObject);
			codeFilledObject.append(" -- cycle;\n");
			endBeamer(codeFilledObject);
		}
	}

	@Override
	protected void drawSlope(GeoNumeric geo) {
		int slopeTriangleSize = geo.getSlopeTriangleSize();
		double rwHeight = geo.getValue() * slopeTriangleSize;
		double height = euclidianView.getYscale() * rwHeight;
		double[] coords = new double[2];
		if (Math.abs(height) > Float.MAX_VALUE) {
			return;
		}
		// get point on line g
		((AlgoSlope) geo.getParentAlgorithm()).getInhomPointOnLine(coords);
		// draw slope triangle
		double x = coords[0];
		double y = coords[1];
		double xright = x + slopeTriangleSize;
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\draw");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			codeFilledObject.append("[" + s + "] ");
		}
		writePoint(x, y, codeFilledObject);
		codeFilledObject.append(" -- ");
		writePoint(xright, y, codeFilledObject);
		codeFilledObject.append(" -- ");
		writePoint(xright, y + rwHeight, codeFilledObject);
		codeFilledObject.append(";\n");
		// draw Label
		double xLabelHor = (x + xright) / 2;
		double yLabelHor = y - ((euclidianView.getFont().getSize() + 2)
				/ euclidianView.getYscale());
		GColor geocolor = geo.getObjectColor();
		codePoint.append("\\draw[color=");
		colorCode(geocolor, codePoint);
		codePoint.append("] ");
		writePoint(xLabelHor, yLabelHor, codePoint);
		codePoint.append(" node[anchor=south west] {");
		codePoint.append(slopeTriangleSize);
		codePoint.append("};\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawAngle(GeoAngle geo) {
		AlgoElement algo = geo.getParentAlgorithm();
		GeoPointND vertex, point;
		GeoVectorND v;
		GeoPoint tempPoint = new GeoPoint(construction);
		tempPoint.setCoords(0.0, 0.0, 1.0);
		double[] firstVec = new double[2];
		double[] m = new double[2];
		// angle defines with three points
		if (algo instanceof AlgoAnglePoints) {
			AlgoAnglePoints pa = (AlgoAnglePoints) algo;
			vertex = pa.getB();
			point = pa.getA();
			vertex.getInhomCoords(m);
			// first vec
			Coords coords = point.getInhomCoordsInD3();
			firstVec[0] = coords.getX() - m[0];
			firstVec[1] = coords.getY() - m[1];
		}
		// angle between two vectors
		else if (algo instanceof AlgoAngleVectors) {
			AlgoAngleVectors va = (AlgoAngleVectors) algo;
			v = va.getv();
			// vertex
			vertex = v.getStartPoint();
			if (vertex == null) {
				vertex = tempPoint;
			}
			vertex.getInhomCoords(m);
			// first vec
			v.getInhomCoords(firstVec);
		}
		// angle between two lines
		else if (algo instanceof AlgoAngleLines) {
			AlgoAngleLines la = (AlgoAngleLines) algo;
			vertex = tempPoint;
			la.updateDrawInfo(m, firstVec, null);
		}
		// angle of a single vector or a single point
		else if (algo instanceof AlgoAngleVector) {
			AlgoAngleVector va = (AlgoAngleVector) algo;
			GeoVec3D vec = va.getVec3D();
			if (vec instanceof GeoVector) {
				v = (GeoVector) vec;
				// vertex
				vertex = v.getStartPoint();
				if (vertex == null) {
					vertex = tempPoint;
				}
				vertex.getInhomCoords(m);
			} else if (vec instanceof GeoPoint) {
				vertex = tempPoint;
				// vertex
				vertex.getInhomCoords(m);
			}
			firstVec[0] = 1;
			firstVec[1] = 0;

		}
		// Fix bug with Slider
		tempPoint.remove();
		// ///////////////

		double angSt = Math.atan2(firstVec[1], firstVec[0]);

		double angExt = geo.getRawAngle();
		if (angExt > Math.PI * 2) {
			angExt -= Math.PI * 2;
		}

		// if (geo.getAngleStyle() == GeoAngle.ANGLE_ISCLOCKWISE) {
		// angSt += angExt;
		// angExt = 2.0 * Math.PI - angExt;
		// }

		if (geo.getAngleStyle() == AngleStyle.NOTREFLEX) {
			if (angExt > Math.PI) {
				angSt += angExt;
				angExt = 2.0 * Math.PI - angExt;
			}
		}

		if (geo.getAngleStyle() == AngleStyle.ISREFLEX) {
			if (angExt < Math.PI) {
				angSt += angExt;
				angExt = 2.0 * Math.PI - angExt;
			}
		}

		angExt += angSt;
		int arcSize = geo.getArcSize();
		double r = arcSize / euclidianView.getXscale();
		// if angle=90 and decoration=little square
		if (drawAngleAs(geo, EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE)) {
			r = r / Math.sqrt(2);
			double[] x = new double[8];
			x[0] = m[0] + r * Math.cos(angSt);
			x[1] = m[1] + r * Math.sin(angSt);
			x[2] = m[0]
					+ r * Math.sqrt(2) * Math.cos(angSt + Kernel.PI_HALF / 2);
			x[3] = m[1]
					+ r * Math.sqrt(2) * Math.sin(angSt + Kernel.PI_HALF / 2);
			x[4] = m[0] + r * Math.cos(angSt + Kernel.PI_HALF);
			x[5] = m[1] + r * Math.sin(angSt + Kernel.PI_HALF);
			x[6] = m[0];
			x[7] = m[1];
			startBeamer(codeFilledObject);
			codeFilledObject.append("\\draw");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				codeFilledObject.append("[" + s + "] ");
			}
			for (int i = 0; i < 4; i++) {
				writePoint(x[2 * i], x[2 * i + 1], codeFilledObject);
				codeFilledObject.append(" -- ");
			}
			codeFilledObject.append("cycle; \n");
			endBeamer(codeFilledObject);
		}
		// draw arc for the angle
		else {
			// set arc in real world coords
			double angStDeg = Math.toDegrees(angSt) % 360;
			double angEndDeg = Math.toDegrees(angExt) % 360;
			if (angStDeg > angEndDeg) {
				angStDeg = angStDeg - 360;
			}
			startBeamer(codeFilledObject);
			codeFilledObject.append("\\draw [shift={");
			writePoint(m[0], m[1], codeFilledObject);
			codeFilledObject.append("}");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				codeFilledObject.append("," + s + "] ");
			} else {
				codeFilledObject.append("] ");
			}
			codeFilledObject.append("(0,0) -- (");
			codeFilledObject.append(format(angStDeg));
			codeFilledObject.append(":");
			codeFilledObject.append(format(r));
			codeFilledObject.append(") arc (");
			codeFilledObject.append(format(angStDeg));
			codeFilledObject.append(":");
			codeFilledObject.append(format(angEndDeg));
			codeFilledObject.append(":");
			codeFilledObject.append(format(r));
			codeFilledObject.append(") -- cycle;\n");
			endBeamer(codeFilledObject);

			// draw the dot if angle= 90 and decoration=dot
			if (drawAngleAs(geo, EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT)) {
				double diameter = geo.getLineThickness()
						/ euclidianView.getXscale();
				double radius = arcSize / euclidianView.getXscale() / 1.7;
				double labelAngle = (angSt + angExt) / 2.0;
				double x1 = m[0] + radius * Math.cos(labelAngle);
				double x2 = m[1] + radius * Math.sin(labelAngle);
				// draw an ellipse
				// command: \draw (0,0) circle diameter
				startBeamer(code);
				code.append("\\fill");
				s = lineOptionCode(geo, true);
				if (s.length() != 0) {
					code.append("[" + s + "] ");
				}
				writePoint(x1, x2, code);
				code.append(" circle (");
				code.append(format(diameter / 2));
				code.append(");\n");
				endBeamer(code);
			}
		}
		int deco = geo.getDecorationType();
		if (deco != GeoElementND.DECORATION_NONE) {
			startBeamer(code);
			markAngle(geo, r, m, angSt, angExt);
			endBeamer(code);
		}
	}

	@Override
	protected void drawArrowArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd, double r, boolean anticlockwise) {
		double angStDeg = Math.toDegrees(angSt) % 360;
		double angEndDeg = Math.toDegrees(angEnd) % 360;
		if (angStDeg > angEndDeg) {
			angStDeg -= 360;
		}
		code.append("\\draw [shift={");
		writePoint(vertex[0], vertex[1], code);
		code.append("},-");
		if (anticlockwise) {
			code.append(">");
		} else {
			code.append("<");
		}
		String s = lineOptionCode(geo, false);
		if (s.length() != 0) {
			code.append("," + s + "] ");
		} else {
			code.append("] ");
		}
		code.append("(");
		code.append(format(angStDeg));
		code.append(":");
		code.append(format(r));
		code.append(") arc (");
		code.append(format(angStDeg));
		code.append(":");
		code.append(format(angEndDeg));
		code.append(":");
		code.append(format(r));
		code.append(");\n");
	}

	@Override
	protected void drawArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd, double r) {
		double angStDeg = Math.toDegrees(angSt) % 360;
		double angEndDeg = Math.toDegrees(angEnd) % 360;
		if (angStDeg > angEndDeg) {
			angStDeg -= 360;
		}
		if (isBeamer) {
			code.append("  ");
		}
		code.append("\\draw [shift={");
		writePoint(vertex[0], vertex[1], code);
		code.append("}");
		String s = lineOptionCode(geo, false);
		if (s.length() != 0) {
			code.append("," + s + "] ");
		} else {
			code.append("] ");
		}
		code.append("(");
		code.append(format(angStDeg));
		code.append(":");
		code.append(format(r));
		code.append(") arc (");
		code.append(format(angStDeg));
		code.append(":");
		code.append(format(angEndDeg));
		code.append(":");
		code.append(format(r));
		code.append(");\n");
	}

	@Override
	protected void drawTick(GeoAngle geo, double[] vertex, double angle0) {
		double cos = Math.cos(angle0);
		double sin = Math.sin(-angle0);
		double radius = geo.getArcSize();
		double diff = 2.5 + geo.getLineThickness() / 4d;
		double x1 = euclidianView
				.toRealWorldCoordX(vertex[0] + (radius - diff) * cos);
		double x2 = euclidianView
				.toRealWorldCoordX(vertex[0] + (radius + diff) * cos);
		double y1 = euclidianView.toRealWorldCoordY(vertex[1]
				+ (radius - diff) * sin * euclidianView.getScaleRatio());
		double y2 = euclidianView.toRealWorldCoordY(vertex[1]
				+ (radius + diff) * sin * euclidianView.getScaleRatio());
		if (isBeamer) {
			code.append("  ");
		}
		code.append("\\draw");
		String s = lineOptionCode(geo, false);
		if (s.length() != 0) {
			code.append("[" + s + "] ");
		}
		writePoint(x1, y1, code);
		code.append(" -- ");
		writePoint(x2, y2, code);
		code.append(";\n");
	}

	@Override
	protected void drawSlider(GeoNumeric geo) {
		boolean horizontal = geo.isSliderHorizontal();
		double max = geo.getIntervalMax();
		double min = geo.getIntervalMin();
		double value = geo.getValue();
		double width = geo.getSliderWidth();
		double x = geo.getSliderX();
		double y = geo.getSliderY();
		// start point of horizontal line for slider
		if (geo.isAbsoluteScreenLocActive()) {
			x = euclidianView.toRealWorldCoordX(x);
			y = euclidianView.toRealWorldCoordY(y);
			width = horizontal ? width / euclidianView.getXscale()
					: width / euclidianView.getYscale();
		}
		// create point for slider
		GeoPoint geoPoint = new GeoPoint(construction);
		geoPoint.setObjColor(geo.getObjectColor());
		String label = StringUtil.toLaTeXString(geo.getLabelDescription(),
				true);
		geoPoint.setLabel(label);
		double param = (value - min) / (max - min);
		geoPoint.setPointSize(2 + (geo.getLineThickness() + 1) / 3);
		geoPoint.setLabelVisible(geo.isLabelVisible());
		if (horizontal) {
			geoPoint.setCoords(x + width * param, y, 1.0);
		} else {
			geoPoint.setCoords(x, y + width * param, 1.0);
		}
		DrawPoint drawPoint = new DrawPoint(euclidianView, geoPoint);
		drawPoint.setGeoElement(geo);
		if (geo.isLabelVisible()) {
			if (horizontal) {
				drawPoint.xLabel -= 15;
				drawPoint.yLabel -= 5;
			} else {
				drawPoint.xLabel += 5;
				drawPoint.yLabel += 2 * geoPoint.getPointSize() + 4;
			}
		}
		drawGeoPoint(geoPoint);
		drawLabel(geoPoint, drawPoint);
		// Bug fixed with Slider
		geoPoint.remove();

		// draw Line or Slider
		startBeamer(code);
		code.append("\\draw");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
			code.append(s);
		}
		writePoint(x, y, code);
		code.append(" -- ");
		if (horizontal) {
			x += width;
		} else {
			y += width;
		}
		writePoint(x, y, code);
		code.append(";\n");
		endBeamer(code);
	}

	@Override
	protected void drawPolygon(GeoPolygon geo) {
		// command: \pspolygon[par](x0,y0)....(xn,yn)
		double alpha = geo.getAlphaValue();
		if (alpha == 0.0f && geo.getFillType() == FillType.IMAGE) {
			return;
		}
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\fill");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
			codeFilledObject.append(s);
		}
		GeoPointND[] points = geo.getPoints();
		for (int i = 0; i < points.length; i++) {
			Coords coords = points[i].getCoordsInD2();
			double x = coords.getX(), y = coords.getY(), z = coords.getZ();
			x = x / z;
			y = y / z;
			writePoint(x, y, codeFilledObject);
			codeFilledObject.append(" -- ");
		}
		codeFilledObject.append("cycle;\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawText(GeoText geo) {
		boolean isLatex = geo.isLaTeX();
		String st = geo.getTextString();
		int style = geo.getFontStyle();
		int size = (int) (geo.getFontSizeMultiplier() * getApp().getFontSize());
		GeoPointND gp;
		double x, y;
		// compute location of text
		if (geo.isAbsoluteScreenLocActive()) {
			x = geo.getAbsoluteScreenLocX();
			y = geo.getAbsoluteScreenLocY();
		} else {
			gp = geo.getStartPoint();
			if (gp == null) {
				x = (int) euclidianView.getXZero();
				y = (int) euclidianView.getYZero();
			} else {
				if (!gp.isDefined()) {
					return;
				}
				x = euclidianView.toScreenCoordX(gp.getInhomX());
				y = euclidianView.toScreenCoordY(gp.getInhomY());
			}
			x += geo.labelOffsetX;
			y += geo.labelOffsetY;
		}
		x = euclidianView.toRealWorldCoordX(x);
		y = euclidianView
				.toRealWorldCoordY(y - euclidianView.getFont().getSize());
		int id = st.indexOf("\n");
		// One line
		if (id == -1 || isLatex) {
			startBeamer(code);
			code.append("\\draw ");
			// Color
			GColor geocolor = geo.getObjectColor();
			if (!geocolor.equals(GColor.BLACK)) {
				code.append("[color=");
				colorCode(geocolor, code);
				code.append("]");
			}
			writePoint(x, y, code);
			code.append(" node[anchor=north west] {");
			addText(st, isLatex, style);
			code.append("};\n");
			endBeamer(code);
		}
		// MultiLine
		else {
			StringBuilder sb = new StringBuilder();

			GFont font = AwtFactory.getPrototype().newFont(
					geo.isSerifFont() ? "Serif" : "SansSerif", style, size);
			int width = getWidth(st, sb, font);

			startBeamer(code);
			code.append("\\draw ");

			// Color
			GColor geocolor = geo.getObjectColor();
			if (!geocolor.equals(GColor.BLACK)) {
				code.append("[color=");
				colorCode(geocolor, code);
				code.append("]");
			}
			writePoint(x, y, code);
			code.append(" node[anchor=north west] {");
			code.append("\\parbox{");
			code.append(format(
					width * (xmax - xmin) * xunit / euclidianView.getWidth()
							+ 1));
			code.append(" cm}{");
			addText(new String(sb), isLatex, style);
			code.append("}};\n");
			endBeamer(code);
		}
	}

	private void addText(String st0, boolean isLatex, int style) {
		String st = st0;

		if (isLatex) {
			st = st.replaceAll("\n", " ");
		}

		if (format == FORMAT_LATEX) {
			if (isLatex) {
				if (!st.startsWith("$")) {
					code.append("$");
				}
				for (int i = 0; i < st.length(); i++) {
					char uCode = st.charAt(i);
					if (UnicodeTeX.getMap().containsKey(uCode)) {
						addTextPackage();
						st = st.replaceAll("\\" + uCode,
								"\\\\" + UnicodeTeX.getMap().get(uCode));
					}
				}
			}
			// Replace all backslash symbol with \textbackslash
			else {
				st = st.replaceAll("\\\\", "\\\\textbackslash ");
				for (int i = 0; i < st.length(); i++) {
					char uCode = st.charAt(i);
					if (UnicodeTeX.getMap().containsKey(uCode)) {
						addTextPackage();
						st = st.replaceAll("\\" + uCode, "\\$\\\\"
								+ UnicodeTeX.getMap().get(uCode) + "\\$");
					}
				}
				st = st.replace("$\\euro$", "euro");
			}
			switch (style) {
			default:
				// do nothing
				break;
			case 1:
				if (isLatex) {
					code.append("\\mathbf{");
				} else {
					code.append("\\textbf{");
				}
				break;
			case 2:
				if (isLatex) {
					code.append("\\mathit{");
				} else {
					code.append("\\textit{");
				}
				break;
			case 3:
				if (isLatex) {
					code.append("\\mathit{\\mathbf{");
				} else {
					code.append("\\textit{\\textbf{");
				}
				break;
			}
			code.append(st);
			switch (style) {
			default:
				// do nothing
				break;
			case 1:
			case 2:
				code.append("}");
				break;
			case 3:
				code.append("}}");
				break;
			}
			if (isLatex && !st.endsWith("$")) {
				code.append("$");
			}
		} else if (format == FORMAT_CONTEXT) {
			if (isLatex) {
				code.append("$");
			}

			switch (style) {
			default:
				// do nothing
				break;
			case 1:
				code.append("{\\bf ");
				break;
			case 2:
				code.append("{\\em ");
				break;
			case 3:
				code.append("{\\em \\bf");
				break;
			}
			code.append(st);
			switch (style) {
			default:
				// do nothing
				break;
			case 1:
			case 2:
			case 3:
				code.append("}");
				break;
			}
			if (isLatex && !st.endsWith("$")) {
				code.append("$");
			}
		} else if (format == FORMAT_PLAIN_TEX) {
			if (isLatex && !st.endsWith("$")) {
				code.append("$");
			}
			switch (style) {
			default:
				// do nothing
				break;
			case 1:
				code.append("\\bf{");
				break;
			case 2:
				code.append("\\it{ ");
				break;
			case 3:
				code.append("\\it{\\bf{");
				break;
			}
			code.append(st);
			switch (style) {
			default:
				// do nothing
				break;
			case 1:
			case 2:
				code.append("}");
				break;
			case 3:
				code.append("}}");
				break;
			}
			if (isLatex && !st.endsWith("$")) {
				code.append("$");
			}
		}
	}

	@Override
	protected void drawGeoConicPart(GeoConicPart geo) {

		double startAngle = geo.getParameterStart();
		double endAngle = geo.getParameterEnd();
		// Get all coefficients form the transform matrix
		GAffineTransform af = geo.getAffineTransform();
		double m11 = af.getScaleX();
		double m22 = af.getScaleY();
		double m12 = af.getShearX();
		double m21 = af.getShearY();
		double tx = af.getTranslateX();
		double ty = af.getTranslateY();
		if (startAngle > endAngle) {
			startAngle = startAngle - Math.PI * 2;
		}

		// Sector command: \draw[shift={(x0,y0)},par] (0,0) --
		// (startAngle:radius) arc (angleStart:EndAngle:radius) -- cycle
		// Arc command: \draw[shift={(x0,y0)},par] (startAngle:radius) arc
		// (startAngle:endAngle:radius)
		startBeamer(code);
		code.append("\\draw [shift={");
		writePoint(tx, ty, code);
		code.append("}");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "," + s + "] ";
			code.append(s);
		} else {
			code.append("]");
		}
		double r1 = geo.getHalfAxes()[0];
		double r2 = geo.getHalfAxes()[1];
		if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
			code.append(" (0,0) -- ");
			StringBuilder sb1 = new StringBuilder();
			sb1.append(format(r1));
			sb1.append("*cos(\\t r)");
			StringBuilder sb2 = new StringBuilder();
			sb2.append(format(r2));
			sb2.append("*sin(\\t r)");
			code.append(" plot[domain=");
			code.append(format(startAngle));
			code.append(":");
			code.append(format(endAngle));
			code.append(",variable=\\t]({");
			code.append(format(m11));
			code.append("*");
			code.append(sb1);
			code.append("+");
			code.append(format(m12));
			code.append("*");
			code.append(sb2);
			code.append("},{");
			code.append(format(m21));
			code.append("*");
			code.append(sb1);
			code.append("+");
			code.append(format(m22));
			code.append("*");
			code.append(sb2);
			code.append("})");
			code.append(" -- cycle ;\n");
		} else if (geo
				.getConicPartType() == GeoConicNDConstants.CONIC_PART_ARC) {
			StringBuilder sb1 = new StringBuilder();
			sb1.append(format(r1));
			sb1.append("*cos(\\t r)");
			StringBuilder sb2 = new StringBuilder();
			sb2.append(format(r2));
			sb2.append("*sin(\\t r)");
			code.append(" plot[domain=");
			code.append(format(startAngle));
			code.append(":");
			code.append(format(endAngle));
			code.append(",variable=\\t]({");
			code.append(format(m11));
			code.append("*");
			code.append(sb1);
			code.append("+");
			code.append(format(m12));
			code.append("*");
			code.append(sb2);
			code.append("},{");
			code.append(format(m21));
			code.append("*");
			code.append(sb1);
			code.append("+");
			code.append(format(m22));
			code.append("*");
			code.append(sb2);
			code.append("});\n");
		}
		endBeamer(code);
	}

	private void drawFunction(GeoFunction geo, StringBuilder sb,
			boolean integral, GeoNumeric geo1) {
		Function f = geo.getFunction();
		if (null == f) {
			return;
		}
		String value = f.toValueString(getStringTemplate());
		value = killSpace(StringUtil.toLaTeXString(value, true));

		boolean plotWithGnuplot = warningFunc(value, "cosh(")
				|| warningFunc(value, "acosh(") || warningFunc(value, "asinh(")
				|| warningFunc(value, "atanh(") || warningFunc(value, "sinh(")
				|| warningFunc(value, "tanh(");

		boolean[] v = hasFractionalOrTrigoExponent(f.getExpression());
		if (v[0]) {
			if (!plotWithGnuplot) {
				addWarningGnuplot();
			}
			if (v[1]) {
				value = value.replaceAll("\\*180/pi", "");
			}
		}
		double a = xmin;
		double b = xmax;
		if (geo.hasInterval()) {
			a = Math.max(a, geo.getIntervalMin());
			b = Math.min(b, geo.getIntervalMax());
		}
		double xrangemax = a, xrangemin = a;
		while (xrangemax < b) {
			xrangemin = firstDefinedValue(geo, a, b);
			// Application.debug("xrangemin "+xrangemin);
			if (xrangemin == b) {
				break;
			}
			xrangemax = maxDefinedValue(geo, xrangemin, b);
			// Application.debug("xrangemax "+xrangemax);
			startBeamer(sb);
			if (forceGnuplot) {
				if (!isLatexFunction(
						f.toValueString(StringTemplate.noLocalDefault))
						|| f.toValueString(StringTemplate.noLocalDefault)
								.toLowerCase().contains('\u212f' + "^")) {
					drawNoLatexFunction(geo, sb, xrangemax, xrangemin, integral,
							geo1);
				} else {
					value = value.replaceAll("\\*180/pi", "");
					drawGnuPlot(geo, sb, value, xrangemax, xrangemin);
				}
			} else {
				if (!isLatexFunction(
						f.toValueString(StringTemplate.noLocalDefault))
						|| isTrigInv(
								f.toValueString(StringTemplate.noLocalDefault))
						|| f.toValueString(StringTemplate.noLocalDefault)
								.toLowerCase().contains('\u212f' + "^")) {
					drawNoLatexFunction(geo, sb, xrangemax, xrangemin, integral,
							geo1);
				} else {
					drawPgfStandard(geo, sb, value, xrangemax, xrangemin);
				}
			}
			endBeamer(sb);
			xrangemax += PRECISION_XRANGE_FUNCTION;
			a = xrangemax;
		}
	}

	/**
	 * @param en
	 *            expression
	 * @return {fractional, trig}
	 */
	protected static boolean[] hasFractionalOrTrigoExponent(ExpressionNode en) {
		boolean[] v = { false, false };
		if (en == null || en.getOperation() == Operation.NO_OPERATION) {
			return v;
		}
		Operation op = en.getOperation();
		if (op == Operation.POWER) {
			ExpressionNode le = en.getRightTree();
			if (le.isNumberValue()) {
				if (le.toValueString(StringTemplate.xmlTemplate).contains("sin")
						|| le.toValueString(StringTemplate.xmlTemplate)
								.contains("cos")
						|| le.toValueString(StringTemplate.xmlTemplate)
								.contains("tan")) {
					v[1] = true;
				}
				double val1 = le.evaluateDouble();
				v[0] = !DoubleUtil.isInteger(val1);
				return v;
			}
			op = le.getOperation();
			v[0] = op == Operation.DIVIDE;
			return v;
		}
		if (!hasFractionalOrTrigoExponent(en.getRightTree())[0]) {
			return hasFractionalOrTrigoExponent(en.getLeftTree());
		}
		v[0] = true;
		return v;
	}

	private static boolean isTrigInv(String s) {
		return s.toLowerCase().contains("atan(")
				|| s.toLowerCase().contains("acos(")
				|| s.toLowerCase().contains("asin(");

	}

	private void drawPgfStandard(GeoFunction geo, StringBuilder sb,
			String value0, double xrangemax, double xrangemin) {
		sb.append("\\draw");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			sb.append("[");
			sb.append(s);
			sb.append(",");
		} else {
			sb.append("[");
		}
		sb.append("smooth,samples=100,domain=");
		sb.append(xrangemin);
		sb.append(":");
		sb.append(xrangemax);
		sb.append("] plot");
		sb.append("(\\x,{");
		String value = replaceX(value0, "(\\x)");
		sb.append(value);
		sb.append("});\n");
	}

	private void drawGnuPlot(GeoFunction geo, StringBuilder sb, String value,
			double xrangemax, double xrangemin) {
		sb.append("\\draw");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			sb.append("[");
			sb.append(s);
			sb.append("]");
		}
		sb.append(" plot[raw gnuplot, id=func");
		sb.append(functionIdentifier);
		functionIdentifier++;
		sb.append("] function{set samples 100; set xrange [");
		sb.append(xrangemin + 0.1);
		sb.append(":");
		sb.append(xrangemax - 0.1);
		sb.append("]; plot ");
		String valueGnu = value.replaceAll("\\^", "**");
		sb.append(valueGnu);
		sb.append("};\n");
	}

	private void drawNoLatexFunction(GeoFunction geo, StringBuilder sb,
			double xrangemax, double xrangemin, boolean integral,
			GeoNumeric geo1) {

		String template = getLineTemplate(geo);
		String cycle = "";
		String close = "";
		if (integral) {
			close = "(" + format(geo.getIntervalMax()) + ",0) -- ("
					+ format(geo.getIntervalMin()) + ",0) -- ";
			sb.append("\\draw[" + lineOptionCode(geo1, true) + "]");
			template = " (%0,%1) -- (%2,%3) --";
			cycle = "cycle;\n";
		}

		StringBuilder lineBuilder = drawNoLatexFunction(geo, xrangemax,
				xrangemin, 400, template);
		sb.append(lineBuilder.toString());
		sb.append(close);
		sb.append(cycle);
	}

	@Override
	protected String getLineTemplate(GeoElementND geo) {
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "]";
		}
		return "\\draw" + s + " (%0,%1) -- (%2,%3);\n";
	}

	@Override
	protected void drawSingleCurveCartesian(GeoCurveCartesian geo,
			boolean trasparency) {
		// \parametricplot[algebraic=true,linecolor=red]
		// {-3.14}{3.14}{cos(3*t)|sin(2*t)}
		// Only done using gnuplot
		// add Warning
		addWarningGnuplot();

		drawSingleCurve(geo, code);

	}

	private void drawSingleCurve(GeoCurveCartesian geo, StringBuilder sb) {
		// boolean isClosed=geo.isClosedPath();
		String fx = geo.getFunX(getStringTemplate());
		fx = killSpace(StringUtil.toLaTeXString(fx, true));
		fx = fx.replaceAll("\\^", "**");
		String fy = geo.getFunY(getStringTemplate());
		fy = killSpace(StringUtil.toLaTeXString(fy, true));
		fy = fy.replaceAll("\\^", "**");
		// It seems that only for the parametric curve are not required grades
		fx = fx.replaceAll("\\*180/pi", "");
		fy = fy.replaceAll("\\*180/pi", "");
		String variable = geo.getVarString(getStringTemplate());
		boolean warning = !("t".equals(variable));
		if (warning) {
			code.append(
					"% WARNING: You have to use the special variable t in parametric plot");
		}
		startBeamer(sb);
		sb.append("\\draw");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			sb.append("[");
			sb.append(s);
		}
		if (s.length() != 0) {
			sb.append(", ");
		} else {
			sb.append("[");
		}
		double start = geo.getMinParameter();
		double end = geo.getMaxParameter();
		sb.append("smooth,samples=100,domain=");
		sb.append(start);
		sb.append(":");
		sb.append(end);
		sb.append("] plot[parametric] function{");
		sb.append(fx);
		sb.append(",");
		sb.append(fy);
		sb.append("};\n");
		endBeamer(sb);
	}

	@Override
	protected void drawFunction(GeoFunction geo) {
		drawFunction(geo, code, false, null);
	}

	/**
	 * This method replace the letter "x" by the String substitute
	 * 
	 * @param name
	 *            The function
	 */
	private static String replaceX(String name, String substitute) {
		StringBuilder sb = new StringBuilder(name);
		// If the expression starts with minus -
		// Insert a "0" (Bug from TikZ /PGF)
		if (name.length() > 0 && name.charAt(0) == '-') {
			sb.insert(0, "0");
		}
		int i = 0;
		while (i < sb.length()) {
			char before = '1';
			char after = '1';
			char character = sb.charAt(i);
			if (character == 'x') {
				if (i > 0) {
					before = sb.charAt(i - 1);
				}
				if (i < sb.length() - 1) {
					after = sb.charAt(i + 1);
				}
				int id1 = "1234567890^ +-*/%()\t".indexOf(after);
				int id2 = "1234567890^ +-*/%()\t".indexOf(before);
				if (id1 != -1 && id2 != -1) {
					sb.deleteCharAt(i);
					sb.insert(i, substitute);
					i += substitute.length() - 1;
				}
			}
			i++;
		}
		return new String(sb);
	}

	/**
	 * We have to rewrite the function - kill spaces - add character * when
	 * needed - rename several functions (done in #ExpressionNode.toString()) -
	 * rename constants
	 */
	private static String killSpace(String name) {
		// 2 x +3 ----> 2*x+3
		StringBuilder sb = new StringBuilder();
		boolean operand = false;
		boolean space = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if ("*/+-".indexOf(c) != -1) {
				sb.append(c);
				operand = true;
				space = false;
			} else if (c == ' ') {
				if (!operand) {
					space = true;
				} else {
					space = false;
					operand = false;
				}
			} else {
				if (space && !name.contains("If")) {
					sb.append("*");
				}
				sb.append(c);
				space = false;
				operand = false;
			}
		}

		renameFunc(sb, Unicode.EULER_STRING, Math.E + "");
		renameFunc(sb, "\\pi", Math.PI + "");
		return new String(sb);
	}

	/**
	 * Some Functions are not supported by PGF. This method write a warning in
	 * preamble
	 * 
	 * @param sb
	 *            The complete Function
	 * @param nameFunc
	 *            The Function unsupported
	 */
	private boolean warningFunc(String sb, String nameFunc) {
		if (forceGnuplot) {
			return true;
		}
		int ind = sb.indexOf(nameFunc);
		if (ind != -1) {
			addWarningGnuplot();
			return true;
		}
		return false;
	}

	private void addWarningGnuplot() {
		if (gnuplotWarning) {
			return;
		}
		gnuplotWarning = true;
		codePreamble.append(" \n%<<<<<<<WARNING>>>>>>>\n");
		codePreamble.append(
				"% PGF/Tikz doesn't support the following mathematical functions:\n");
		codePreamble.append("% cosh, acosh, sinh, asinh, tanh, atanh,\n");
		codePreamble.append("% x^r with r not integer\n\n");
		codePreamble.append("% Plotting will be done using GNUPLOT\n");
		codePreamble.append(
				"% GNUPLOT must be installed and you must allow Latex to call external\n");
		codePreamble.append("% programs by adding the following option to your compiler\n");
		codePreamble.append("% shell-escape    OR    enable-write18 \n");
		codePreamble.append("% Example: pdflatex --shell-escape file.tex \n\n");
	}

	private void addWarningHatch() {
		if (hatchWarning) {
			return;
		}
		hatchWarning = true;
		codePreamble.append(" \n\n%<<<<<<<WARNING>>>>>>>\n");
		codePreamble
				.append("% PGF/Tikz doesn't support hatch filling very well\n");
		codePreamble.append("% Use PStricks for a perfect hatching export\n\n");
	}

	@Override
	protected void drawGeoVector(GeoVector geo) {
		GeoPointND pointStart = geo.getStartPoint();
		double x1, y1;
		if (null == pointStart) {
			x1 = 0;
			y1 = 0;
		} else {
			Coords c = pointStart.getCoords();
			x1 = c.getX() / c.getZ();
			y1 = c.getY() / c.getZ();
		}
		double[] coord = new double[3];
		geo.getCoords(coord);
		double x2 = coord[0] + x1;
		double y2 = coord[1] + y1;
		startBeamer(code);
		code.append("\\draw [->");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			code.append(",");
			code.append(s);
		}
		code.append("] ");
		writePoint(x1, y1, code);
		code.append(" -- ");
		writePoint(x2, y2, code);
		code.append(";\n");
		endBeamer(code);
	}

	private void drawCircle(GeoConicND geo) {
		StringBuilder build = new StringBuilder();
		if (xunit == yunit) {
			// draw a circle
			// command: \draw[options](x_center,y_center) circle (R cm)
			double x = geo.getTranslationVector().getX();
			double y = geo.getTranslationVector().getY();
			double r = geo.getHalfAxes()[0];

			startBeamer(build);
			build.append("\\draw");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				s = " [" + s + "] ";
			}
			build.append(s);
			writePoint(x, y, build);
			build.append(" circle (");
			String tmpr = format(r * xunit);
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpr) != 0) {
				build.append(tmpr);
			} else {
				build.append(r);
			}
			build.append("cm);\n");
			endBeamer(build);
		} else {
			// draw an ellipse
			// command: \draw[options](x_center,y_center) ellipse (XRadius cm
			// and YRadius cm)
			double x1 = geo.getTranslationVector().getX();
			double y1 = geo.getTranslationVector().getY();
			startBeamer(build);
			build.append("\\draw");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				s = " [" + s + "] ";
			}
			build.append(s);
			writePoint(x1, y1, build);
			double r1 = geo.getHalfAxes()[0];
			double r2 = geo.getHalfAxes()[1];
			build.append(" ellipse (");
			build.append(format(r1 * xunit));
			build.append("cm and ");
			build.append(format(r2 * yunit));
			build.append("cm);\n");
			endBeamer(build);
		}
		if (geo.getAlphaValue() > 0.0f) {
			codeFilledObject.append(build);
		} else {
			code.append(build);
		}
	}

	@Override
	protected void drawGeoConic(GeoConicND geo) {
		// just need one eigenvector to give angle
		// assume getZ() is zero (check done earlier)
		Coords ev0 = euclidianView.getCoordsForView(geo.getEigenvec3D(0));
		double eigenvecX = ev0.getX();
		double eigenvecY = ev0.getY();
		double angle = Math.toDegrees(Math.atan2(eigenvecY, eigenvecX));

		Coords mp = geo.getMidpoint3D();
		double x1 = mp.getX();
		double y1 = mp.getY();
		double r1 = geo.getHalfAxes()[0];
		double r2 = geo.getHalfAxes()[1];

		switch (geo.getType()) {
		default:
			// do nothing
			break;
		// if conic is a circle
		case GeoConicNDConstants.CONIC_CIRCLE:
			drawCircle(geo);
			break;
		// if conic is an ellipse
		case GeoConicNDConstants.CONIC_ELLIPSE:
			// command: \draw[rotate
			// around={angle:center},lineOptions](x_center,y_center) ellipse (R1
			// and R2)

			startBeamer(code);
			code.append("\\draw [rotate around={");
			code.append(format(angle));
			code.append(":");
			writePoint(x1, y1, code);
			code.append("}");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				code.append(",");
				code.append(s);
			}
			code.append("] ");
			writePoint(x1, y1, code);

			code.append(" ellipse (");
			code.append(format(r1 * xunit));
			code.append("cm and ");
			code.append(format(r2 * yunit));
			code.append("cm);\n");
			endBeamer(code);
			break;

		// if conic is a parabola
		case GeoConicNDConstants.CONIC_PARABOLA:
			// command: \draw[rotate
			// around={angle:center},xshift=x1,yshift=y1,lineOptions]
			// plot(\x,\x^2/2/p);
			// parameter of the parabola

			// calculate the x range to draw the parabola
			double x0 = Math.max(Math.abs(x1 - xmin), Math.abs(x1 - xmax));
			x0 = Math.max(x0, Math.abs(y1 - ymin));
			x0 = Math.max(x0, Math.abs(y1 - ymax));

			// avoid sqrt by choosing x = k*p with
			// i = 2*k is quadratic number
			// make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
			double p = geo.p;
			x0 = 4 * x0 / p;
			int i = 4;
			int k2 = 16;
			while (k2 < x0) {
				i += 2;
				k2 = i * i;
			}
			// x0 = k2/2 * p; // x = k*p
			x0 = i * p; // y = sqrt(2k p^2) = i p
			angle -= 90;

			startBeamer(code);
			code.append("\\draw [samples=50,rotate around={");
			code.append(format(angle));
			code.append(":");
			writePoint(x1, y1, code);
			code.append("},xshift=");
			code.append(format(x1 * xunit));
			code.append("cm,yshift=");
			code.append(format(y1 * yunit));
			code.append("cm");
			s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				code.append(",");
				code.append(s);
			}
			code.append(",domain=");
			code.append(-x0);
			code.append(":");
			code.append(x0);
			code.append(")] plot (\\x,{(\\x)^2/2/");
			code.append(p);
			code.append("});\n");
			endBeamer(code);
			break;
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			// command: \draw[domain=-1:1,rotate
			// around={angle:center},xshift=x1,yshift=y1,lineOptions]
			// plot({a(1+\x^2)/(1-\x^2)},2b\x/(1-\x^2));

			startBeamer(code);
			code.append("\\draw [samples=50,domain=-0.99:0.99,rotate around={");
			code.append(format(angle));
			code.append(":");
			writePoint(x1, y1, code);
			code.append("},xshift=");
			code.append(format(x1 * xunit));
			code.append("cm,yshift=");
			code.append(format(y1 * yunit));
			code.append("cm");
			s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				code.append(",");
				code.append(s);
			}
			r1 = geo.getHalfAxes()[0];
			r2 = geo.getHalfAxes()[1];
			code.append("] plot ({");
			code.append(format(r1));
			code.append("*(1+(\\x)^2)/(1-(\\x)^2)},{");
			code.append(format(r2));
			code.append("*2*(\\x)/(1-(\\x)^2)});\n");

			if (isBeamer) {
				code.append("  ");
			}
			code.append("\\draw [samples=50,domain=-0.99:0.99,rotate around={");
			code.append(format(angle));
			code.append(":");
			writePoint(x1, y1, code);
			code.append("},xshift=");
			code.append(format(x1 * xunit));
			code.append("cm,yshift=");
			code.append(format(y1 * yunit));
			code.append("cm");
			s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				code.append(",");
				code.append(s);
			}
			code.append("] plot ({");
			code.append(format(r1));
			code.append("*(-1-(\\x)^2)/(1-(\\x)^2)},{");
			code.append(format(r2));
			code.append("*(-2)*(\\x)/(1-(\\x)^2)});\n");
			endBeamer(code);
			break;
		}
	}

	/**
	 * This will generate the Tikz code to draw the GeoPoint gp into the
	 * StringBuilder PointCode
	 * 
	 * @param gp
	 *            The choosen GeoPoint
	 */
	@Override
	protected void drawGeoPoint(GeoPointND gp) {
		if (frame.getExportPointSymbol()) {
			double[] A = new double[3];

			// assume 2D (3D check done earlier)
			gp.getInhomCoords(A);

			double x = A[0];
			double y = A[1];

			GColor dotcolor = gp.getObjectColor();
			double dotsize = gp.getPointSize();
			int dotstyle = gp.getPointStyle();

			if (dotstyle == -1) { // default
				dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
			}

			startBeamer(codePoint);

			if (dotstyle == EuclidianStyleConstants.POINT_STYLE_CIRCLE) {
				codePoint.append("\\draw [color=");
				colorCode(dotcolor, codePoint);
				codePoint.append("] ");
				writePoint(x, y, codePoint);
				codePoint.append(" circle (");
				codePoint.append(dotsize / 2);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_CROSS) {
				codePoint.append("\\draw [color=");
				colorCode(dotcolor, codePoint);
				codePoint.append("] ");
				writePoint(x, y, codePoint);
				codePoint.append("-- ++(-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt) -- ++(");
				codePoint.append(dotsize);
				codePoint.append("pt,");
				codePoint.append(dotsize);
				codePoint.append("pt) ++(-");
				codePoint.append(dotsize);
				codePoint.append("pt,0) -- ++(");
				codePoint.append(dotsize);
				codePoint.append("pt,-");
				codePoint.append(dotsize);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND) {
				codePoint.append("\\draw [color=");
				colorCode(dotcolor, codePoint);
				codePoint.append("] ");
				writePoint(x, y, codePoint);
				codePoint.append(" ++(-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,");
				codePoint.append(dotsize / 2);
				codePoint.append("pt)--++(");

				codePoint.append(dotsize / 2);
				codePoint.append("pt,-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt)--++(-");

				codePoint.append(dotsize / 2);
				codePoint.append("pt,-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt)--++(-");

				codePoint.append(dotsize / 2);
				codePoint.append("pt,");
				codePoint.append(dotsize / 2);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND) {
				codePoint.append("\\draw [fill=");
				colorCode(dotcolor, codePoint);
				codePoint.append("] ");
				writePoint(x, y, codePoint);
				codePoint.append(" ++(-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,");
				codePoint.append(dotsize / 2);
				codePoint.append("pt)--++(");

				codePoint.append(dotsize / 2);
				codePoint.append("pt,-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt)--++(-");

				codePoint.append(dotsize / 2);
				codePoint.append("pt,-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt)--++(-");

				codePoint.append(dotsize / 2);
				codePoint.append("pt,");
				codePoint.append(dotsize / 2);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_PLUS) {
				codePoint.append("\\draw [color=");
				colorCode(dotcolor, codePoint);
				codePoint.append("] ");
				writePoint(x, y, codePoint);
				codePoint.append("-- ++(-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(dotsize);
				codePoint.append("pt,0 pt) ++(-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt,-");
				codePoint.append(dotsize / 2);
				codePoint.append("pt) -- ++(0 pt,");
				codePoint.append(dotsize);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST) {
				double radius = 3 * dotsize / 4;
				codePoint.append("\\draw [fill=");
				colorCode(dotcolor, codePoint);
				codePoint.append(",shift={");
				writePoint(x, y, codePoint);
				codePoint.append("},rotate=270] (0,0)");

				codePoint.append(" ++(0 pt,");
				codePoint.append(radius);
				codePoint.append("pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,-");
				codePoint.append(radius / 2 * 3);

				codePoint.append("pt)--++(-");
				codePoint.append(format(radius * Math.sqrt(3)));
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,");
				codePoint.append(3 * radius / 2);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH) {
				double radius = 3 * dotsize / 4;
				codePoint.append("\\draw [fill=");
				colorCode(dotcolor, codePoint);
				codePoint.append(",shift={");
				writePoint(x, y, codePoint);
				codePoint.append("}] (0,0)");

				codePoint.append(" ++(0 pt,");
				codePoint.append(radius);
				codePoint.append("pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,-");
				codePoint.append(radius / 2 * 3);

				codePoint.append("pt)--++(-");
				codePoint.append(format(radius * Math.sqrt(3)));
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,");
				codePoint.append(3 * radius / 2);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH) {
				double radius = 3 * dotsize / 4;
				codePoint.append("\\draw [fill=");
				colorCode(dotcolor, codePoint);
				codePoint.append(",shift={");
				writePoint(x, y, codePoint);
				codePoint.append("},rotate=180] (0,0)");

				codePoint.append(" ++(0 pt,");
				codePoint.append(radius);
				codePoint.append("pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,-");
				codePoint.append(radius / 2 * 3);

				codePoint.append("pt)--++(-");
				codePoint.append(format(radius * Math.sqrt(3)));
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,");
				codePoint.append(3 * radius / 2);
				codePoint.append("pt);\n");
			} else if (dotstyle == EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST) {
				double radius = 3 * dotsize / 4;
				codePoint.append("\\draw [fill=");
				colorCode(dotcolor, codePoint);
				codePoint.append(",shift={");
				writePoint(x, y, codePoint);
				codePoint.append("},rotate=90] (0,0)");

				codePoint.append(" ++(0 pt,");
				codePoint.append(radius);
				codePoint.append("pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,-");
				codePoint.append(radius / 2 * 3);

				codePoint.append("pt)--++(-");
				codePoint.append(format(radius * Math.sqrt(3)));
				codePoint.append("pt,0 pt) -- ++(");
				codePoint.append(format(radius / 2 * Math.sqrt(3)));
				codePoint.append("pt,");
				codePoint.append(3 * radius / 2);
				codePoint.append("pt);\n");
			}

			// default is the circle point style
			else {
				codePoint.append("\\draw [fill=");
				colorCode(dotcolor, codePoint);
				codePoint.append("] ");
				writePoint(x, y, codePoint);
				codePoint.append(" circle (");
				codePoint.append(dotsize / 2);
				codePoint.append("pt);\n");
			}
			endBeamer(codePoint);
		}
		// In case of trimmed intersection
		if (gp.getShowTrimmedIntersectionLines()) {
			AlgoElement algo = gp.getParentAlgorithm();

			if (algo instanceof AlgoIntersectAbstract) {
				double x1 = euclidianView.toScreenCoordXd(gp.getInhomX());
				double y1 = euclidianView.toScreenCoordYd(gp.getInhomY());
				double x2 = euclidianView.toScreenCoordXd(gp.getInhomX()) + 30;
				double y2 = euclidianView.toScreenCoordYd(gp.getInhomY()) + 30;
				x1 = euclidianView.toRealWorldCoordX(x1);
				x2 = euclidianView.toRealWorldCoordX(x2);
				y1 = euclidianView.toRealWorldCoordY(y1);
				y2 = euclidianView.toRealWorldCoordY(y2);

				StringBuilder s = new StringBuilder();
				if (format == GeoGebraToPgf.FORMAT_LATEX) {
					s.append("\\begin{scope}\n");
				} else if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
					s.append("\\startscope\n");
				} else if (format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
					s.append("\\scope\n");
				}
				s.append("\\clip (");
				s.append(format(x1));
				s.append(",");
				s.append(format(y1));
				double r1 = Math.abs(x2 - x1);
				double r2 = Math.abs(y2 - y1);
				s.append(") ellipse (");
				s.append(format(r1));
				s.append("cm and ");
				s.append(format(r2));
				s.append("cm);\n");
				// Latex format
				String end = "\\end{scope}\n";
				if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
					end = "\\stopscope\n";
				} else if (format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
					end = "\\endscope\n";
				}
				boolean fill1 = false;
				GeoElement[] geos = algo.getInput();
				boolean draw = !geos[0].isEuclidianVisible();
				if (draw) {
					fill1 = geos[0].isFillable()
							&& geos[0].getAlphaValue() > 0.0f;
					if (fill1) {
						codeFilledObject.append(s);
					} else {
						code.append(s);
					}
					drawGeoElement(geos[0], false, true);
				}
				if (geos.length > 1 && !geos[1].isEuclidianVisible()) {
					boolean fill2 = geos[1].isFillable()
							&& (geos[1].getAlphaValue() > 0.0f);
					if (draw) {
						if (fill1 == fill2) {
							drawGeoElement(geos[1], false, true);
							if (fill1) {
								codeFilledObject.append(end);
							} else {
								code.append(end);
							}
						} else {
							if (fill1) {
								codeFilledObject.append(end);
							} else {
								code.append(end);
							}
							if (fill2) {
								codeFilledObject.append(s);
							} else {
								code.append(s);
							}
							drawGeoElement(geos[1], false, true);
							if (fill2) {
								codeFilledObject.append(end);
							} else {
								code.append(end);
							}
						}
					} else {
						if (fill2) {
							codeFilledObject.append(s);
						} else {
							code.append(s);
						}
						drawGeoElement(geos[1], false, true);
						if (fill2) {
							codeFilledObject.append(end);
						} else {
							code.append(end);
						}
					}
				} else if (draw) {
					if (fill1) {
						codeFilledObject.append(end);
					} else {
						code.append(end);
					}
				}
			}
		}
	}

	/**
	 * Generate the PGF/tikZ code to draw an infinite line
	 */

	@Override
	protected void drawGeoLine(GeoLine geo) {
		double a = geo.getX();
		double b = geo.getY();
		double c = geo.getZ();
		startBeamer(code);
		/*
		 * To prevent "dimension too large" prblem with TikZ Eg: \draw [line
		 * width=1.6pt,color=qqcctt,domain=-2.097:9.585]
		 * plot(\x,{(-7.657--2.392*\x)/-0.007});
		 * 
		 * We allow a factor of 40 between the coefficient director and the
		 * window height Else we say it's vertical.
		 */
		double heightScreen = frame.textYmaxValue() - frame.textYminValue();
		if (Math.abs(a / b / heightScreen) > 40) {
			b = 0;
		}

		if (b != 0) {
			code.append("\\draw [");
			String option = lineOptionCode(geo, true);
			if (option.length() != 0) {
				code.append(option);
				code.append(",");
			}
			code.append("domain=");
			code.append(format(xmin));
			code.append(":");
			code.append(format(xmax));
			code.append("] plot(\\x,{(-");
			code.append(format(c));
			code.append("-");
			code.append(format(a));
			code.append("*\\x)/");
			String tmpy = format(b);
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpy) != 0) {
				code.append(tmpy);
			} else {
				code.append(b);
			}
			code.append("});\n");
		} else if (b == 0) {
			code.append("\\draw ");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				s = "[" + s + "] ";
			}
			code.append(s);
			writePoint(-c / a, ymin, code);
			code.append(" -- ");
			writePoint(-c / a, ymax, code);
			code.append(";\n");
		}
		endBeamer(code);
	}

	/**
	 * This will generate the Tikz code to draw the GeoSegment geo into the
	 * StringBuilder code
	 * 
	 * @param geo
	 *            The choosen GeoPoint
	 */

	@Override
	protected void drawGeoSegment(GeoSegmentND geo) {
		double[] A = new double[3];
		double[] B = new double[3];
		GeoPointND pointStart = geo.getStartPoint();
		GeoPointND pointEnd = geo.getEndPoint();
		pointStart.getInhomCoords(A);
		pointEnd.getInhomCoords(B);

		startBeamer(code);
		code.append("\\draw ");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
		}
		code.append(s);

		// assume 2D (3D check done earlier)
		writePoint(A[0], A[1], code);
		code.append("-- ");
		writePoint(B[0], B[1], code);
		code.append(";\n");
		int deco = geo.getDecorationType();
		if (deco != GeoElementND.DECORATION_NONE) {
			mark(A, B, deco, geo);
		}
		endBeamer(code);
	}

	@Override
	protected void drawLine(double x1, double y1, double x2, double y2,
			GeoElementND geo) {
		if (isBeamer) {
			code.append("  ");
		}
		code.append("\\draw ");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
		}
		code.append(s);
		writePoint(x1, y1, code);
		code.append(" -- ");
		writePoint(x2, y2, code);
		code.append(";\n");
	}

	@Override
	protected void drawGeoRay(GeoRayND geo) {

		GeoPointND pointStart = geo.getStartPoint();
		double x1 = pointStart.getInhomX();
		double y1 = pointStart.getInhomY();

		Coords equation = geo
				.getCartesianEquationVector(euclidianView.getMatrix());

		double a = equation.getX();
		double b = equation.getY();
		double c = equation.getZ();

		double inf = xmin, sup = xmax;
		if (b > 0) {
			inf = x1;
		} else {
			sup = x1;
		}
		startBeamer(code);
		if (b != 0) {
			code.append("\\draw [");
			String option = lineOptionCode(geo, true);
			if (option.length() != 0) {
				code.append(option);
				code.append(",");
			}
			code.append("domain=");
			code.append(inf);
			code.append(":");
			code.append(sup);
			code.append("] plot(\\x,{(-");
			code.append(format(c));
			code.append("-");
			code.append(format(a));
			code.append("*\\x)/");
			String tmpy = format(b);
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpy) != 0) {
				code.append(tmpy);
			} else {
				code.append(b);
			}
			code.append("});\n");
		} else if (b == 0) {
			if (a < 0) {
				sup = ymax;
			} else {
				sup = ymin;
			}
			code.append("\\draw ");
			String s = lineOptionCode(geo, true);
			if (s.length() != 0) {
				s = "[" + s + "] ";
			}
			code.append(s);
			writePoint(x1, y1, code);
			code.append(" -- ");
			writePoint(x1, sup, code);
			code.append(";\n");
		}
		endBeamer(code);
	}

	@Override
	protected void drawLabel(GeoElementND geo, DrawableND drawGeo0) {
		DrawableND drawGeo = drawGeo0;
		if (geo != null && geo.isLabelVisible()
				&& geo.getLabelDescription() != null) {
			String name = geo.getLabelDescription();
			if (geo.getLabelMode() == GeoElementND.LABEL_CAPTION) {
				String nameSym = name;
				for (int i = 0; i < name.length(); i++) {
					char uCode = name.charAt(i);
					if (UnicodeTeX.getMap().containsKey(uCode)) {
						nameSym = nameSym.replaceAll("\\" + uCode, "\\$\\\\"
								+ UnicodeTeX.getMap().get(uCode) + "\\$");
					}
				}
				nameSym = nameSym.replace("$\\euro$", "euro");
				name = nameSym;
				if (name.contains("_")) {
					name = "$" + name + "$";
				}
			} else {
				name = "$" + StringUtil.toLaTeXString(geo.getLabelDescription(),
						true) + "$";
			}
			if (name.indexOf(Unicode.DEGREE_STRING) != -1) {
				if (format == GeoGebraToPgf.FORMAT_LATEX) {
					name = name.replaceAll(Unicode.DEGREE_STRING,
							"\\\\textrm{\\\\degre}");
					if (codePreamble.indexOf("\\degre") == -1) {
						codePreamble.append(
								"\\newcommand{\\degre}{\\ensuremath{^\\circ}}\n");
					}
				} else if (format == GeoGebraToPgf.FORMAT_CONTEXT
						|| format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
					name = name.replaceAll(Unicode.DEGREE_STRING,
							"{}^{\\\\circ}");
				}
			}
			if (null == drawGeo) {
				drawGeo = euclidianView.getDrawableFor(geo);
			}
			if (drawGeo == null) {
				return;
			}
			double xLabel = drawGeo.getxLabel();
			double yLabel = drawGeo.getyLabel();
			xLabel = euclidianView.toRealWorldCoordX(Math.round(xLabel));
			yLabel = euclidianView.toRealWorldCoordY(Math.round(yLabel));
			GColor geocolor = geo.getObjectColor();
			startBeamer(codePoint);
			int width = (int) Math.ceil(StringUtil.getPrototype()
					.estimateLength(StringUtil
							.toLaTeXString(geo.getLabelDescription(), true),
							euclidianView.getFont()));
			int height = (int) Math.ceil(StringUtil.getPrototype()
					.estimateHeight(StringUtil
							.toLaTeXString(geo.getLabelDescription(), true),
							euclidianView.getFont()));
			double[] translation = new double[2];
			translation[0] = euclidianView.getXZero() + width / 2.0;
			translation[1] = euclidianView.getYZero() - height / 2.0;
			translation[0] = euclidianView.toRealWorldCoordX(translation[0]);
			translation[1] = euclidianView.toRealWorldCoordY(translation[1]);
			codePoint.append("\\draw[color=");
			colorCode(geocolor, codePoint);
			codePoint.append("] ");
			writePoint(xLabel + translation[0], yLabel + translation[1],
					codePoint);
			codePoint.append(" node {");
			codePoint.append(name);
			codePoint.append("};\n");
			endBeamer(codePoint);
		}
	}

	/**
	 * Generate the PGF/TikZ code for the Grid
	 */

	private void drawGrid() {
		// resizeFont(codeBeginDoc);
		GColor gridCol = euclidianView.getGridColor();
		double[] GridDist = euclidianView.getGridDistances();
		int gridLine = euclidianView.getGridLineStyle();
		codeBeginDoc.append("\\draw [color=");
		colorCode(gridCol, codeBeginDoc);
		codeBeginDoc.append(",");
		linestyleCode(gridLine, codeBeginDoc);
		codeBeginDoc.append(", xstep=");
		codeBeginDoc.append(sci2dec(GridDist[0] * xunit));
		codeBeginDoc.append("cm,ystep=");
		codeBeginDoc.append(sci2dec(GridDist[1] * yunit));
		codeBeginDoc.append("cm] ");
		writePoint(xmin, ymin, codeBeginDoc);
		codeBeginDoc.append(" grid ");
		writePoint(xmax, ymax, codeBeginDoc);
		codeBeginDoc.append(";\n");
	}

	private void addScale(StringBuilder sb) {
		sb.append("x=");
		sb.append(xunit);
		sb.append("cm,y=");
		sb.append(yunit);
		sb.append("cm");
	}

	/**
	 * https://de.sharelatex.com/learn/pgfplots_package
	 */
	private void drawNiceAxesGrid() {
		codeBeginDoc.append("\\begin{axis}[\n");

		// codeBeginDoc.append("x=1cm,y=1cm,");
		addScale(codeBeginDoc);

		// ignore y-axis setting (assume same as x)
		String position = euclidianView.getDrawBorderAxes()[0] ? "edge"
				: "middle";
		codeBeginDoc.append(",\naxis lines=" + position + ",\n");

		// codeBeginDoc.append(",\ngrid=both,\n");

		if (euclidianView
				.getGridLineStyle() != EuclidianStyleConstants.LINE_TYPE_FULL) {
			codeBeginDoc.append("grid style=dashed,\n");
		}

		if (euclidianView.getShowGrid()) {
			// horizontal grid
			codeBeginDoc.append("ymajorgrids=true,\n");
			// vertical grid
			codeBeginDoc.append("xmajorgrids=true,\n");
		}

		codeBeginDoc.append("xmin=");
		codeBeginDoc.append(xmin);

		codeBeginDoc.append(",\nxmax=");
		codeBeginDoc.append(xmax);

		codeBeginDoc.append(",\nymin=");
		codeBeginDoc.append(ymin);

		codeBeginDoc.append(",\nymax=");
		codeBeginDoc.append(ymax);

		// eg xtick={-8,-7,...,8}");
		codeBeginDoc.append(",\nxtick={");
		double tickStepX = euclidianView.getGridDistances()[0];
		double startX = MyMath.nextMultiple(xmin, tickStepX);
		double endX = MyMath.nextMultiple(xmax + Kernel.STANDARD_PRECISION, tickStepX) - tickStepX;

		codeBeginDoc.append(startX);
		codeBeginDoc.append(',');
		codeBeginDoc.append(startX + tickStepX);
		codeBeginDoc.append(",...,");
		codeBeginDoc.append(endX);

		// eg ytick={-4,-3,...,4");
		codeBeginDoc.append("},\nytick={");

		double tickStepY = euclidianView.getGridDistances()[1];
		double startY = MyMath.nextMultiple(ymin, tickStepY);
		double endY = MyMath.nextMultiple(ymax + Kernel.STANDARD_PRECISION, tickStepY) - tickStepY;
		codeBeginDoc.append(startY);
		codeBeginDoc.append(',');
		codeBeginDoc.append(startY + tickStepY);
		codeBeginDoc.append(",...,");
		codeBeginDoc.append(endY);

		codeBeginDoc.append("},]\n");
	}

	/**
	 * A util method adds point coordinates to a StringBuilder
	 * 
	 * @param x
	 *            X point
	 * @param y
	 *            Y Point
	 * @param sb
	 *            The StringBuilder code
	 */
	private void writePoint(double x, double y, StringBuilder sb) {
		sb.append("(");
		sb.append(format(x));
		sb.append(",");
		sb.append(format(y));
		sb.append(")");
	}

	/**
	 * @param geo
	 *            element
	 * @param transparency
	 *            whether to use transparency
	 * @return line options
	 */
	public String lineOptionCode(GeoElementND geo, boolean transparency) {
		StringBuilder sb = new StringBuilder();
		int linethickness = geo.getLineThickness();
		int linestyle = geo.getLineType();

		Info info = new Info(geo);

		boolean coma = false;

		// removed: default is different in GeoGebra vs Tikz
		// if (linethickness != EuclidianStyleConstants.DEFAULT_LINE_THICKNESS)
		// {
		// coma needed
		coma = true;
		// bracket needed
		sb.append("line width=");
		sb.append(format(linethickness / 2.0 * 0.8));
		sb.append("pt");

		if (linestyle != EuclidianStyleConstants.DEFAULT_LINE_TYPE) {
			if (coma) {
				sb.append(",");
			} else {
				coma = true;
			}
			linestyleCode(linestyle, sb);
		}
		if (!info.getLinecolor().equals(GColor.BLACK)) {
			if (coma) {
				sb.append(",");
			} else {
				coma = true;
			}
			if (transparency && geo.isFillable()
					&& info.getFillType() == FillType.IMAGE) {
				sb.append("pattern ");
			}
			sb.append("color=");
			colorCode(info.getLinecolor(), sb);
		}
		if (transparency && geo.isFillable()) {
			switch (info.getFillType()) {
			default:
			case STANDARD:
				if (info.getAlpha() > 0.0f) {
					if (coma) {
						sb.append(",");
					} else {
						coma = true;
					}
					sb.append("fill=");
					colorCode(info.getLinecolor(), sb);
					sb.append(",fill opacity=");
					sb.append(info.getAlpha());
				}
				break;
			case SYMBOLS:
			case CROSSHATCHED:
			case CHESSBOARD:
			case HONEYCOMB:
			case DOTTED:
			case BRICK:
			case HATCH:
				addWarningHatch();
				if (coma) {
					sb.append(",");
				} else {
					coma = true;
				}
				sb.append("fill=");
				colorCode(info.getLinecolor(), sb);
				sb.append(",pattern=");
				if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
					if (codePreamble
							.indexOf("usetikzlibrary{patterns}") == -1) {
						codePreamble.append("\\usetikzlibrary{patterns}\n");
					}
				} else {
					if (codePreamble
							.indexOf("usetikzlibrary[patterns]") == -1) {
						codePreamble.append("\\usetikzlibrary[patterns]\n");
					}
				}
				double angle = info.getAngle();
				if (info.getFillType() == FillType.DOTTED) {
					sb.append("dots");
				} else {
					if (angle < 20) {
						sb.append("horizontal lines");
					} else if (angle < 70) {
						sb.append("north east lines");
					} else if (angle < 110) {
						sb.append("vertical lines");
					} else if (angle < 160) {
						sb.append("north west lines");
					} else {
						sb.append("horizontal lines");
					}
				}
				sb.append(",pattern color=");
				colorCode(info.getLinecolor(), sb);
				break;
			}
		}
		return new String(sb);
	}

	/**
	 * Append the line style parameters to the StringBuilder sb
	 */
	private void linestyleCode(int linestyle, StringBuilder sb) {
		switch (linestyle) {
		default:
			// do nothing
			break;
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			sb.append("dotted");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			// sb.append("dash pattern=off 4pt on 4pt");
			sb.append("dash pattern=on ");
			int size = resizePt(4);
			sb.append(size);
			sb.append("pt off ");
			sb.append(size);
			sb.append("pt");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			// sb.append("dash pattern=off 8pt on 8pt");
			sb.append("dash pattern=on ");
			int size8 = resizePt(8);
			sb.append(size8);
			sb.append("pt off ");
			sb.append(size8);
			sb.append("pt");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			// sb.append("dash pattern=on 1 pt off 4pt on 8pt off 4 pt");
			sb.append("dash pattern=on ");
			int size1 = resizePt(1);
			int size4 = resizePt(4);
			size8 = resizePt(8);
			sb.append(size1);
			sb.append("pt off ");
			sb.append(size4);
			sb.append("pt on ");
			sb.append(size8);
			sb.append("pt off ");
			sb.append(4);
			sb.append("pt");
			break;
		}
	}

	/**
	 * Append the name color to StringBuilder sb It will create a custom color,
	 * if this color hasn't be defined yet
	 * 
	 * @param c0
	 *            The Choosen color
	 * @param sb
	 *            The StringBuilder where the color has to be added
	 */
	@Override
	protected void colorCode(GColor c0, StringBuilder sb) {
		if (frame.isGrayscale()) {
			if (c0.equals(GColor.BLACK)) {
				sb.append("black");
				return;
			}
			String colorname = "";
			int red = c0.getRed();
			int green = c0.getGreen();
			int blue = c0.getBlue();
			int grayscale = (red + green + blue) / 3;
			GColor c = GColor.newColor(grayscale, grayscale, grayscale);
			if (customColor.containsKey(c)) {
				colorname = customColor.get(c).toString();
			} else {
				if (c.equals(GColor.BLACK)) {
					sb.append("black");
					return;
				} else if (c.equals(GColor.RED)) {
					sb.append("red");
					return;
				} else if (c.equals(GColor.BLUE)) {
					sb.append("blue");
					return;
				} else if (c.equals(GColor.GREEN)) {
					sb.append("green");
					return;
				}
				colorname = createCustomColor(red, green, blue);
				// Example: \definecolor{orange}{rgb}{1,0.5,0}
				if (format == GeoGebraToPgf.FORMAT_LATEX
						|| format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
					codeBeginDoc.insert(0,
							"\\definecolor{" + colorname + "}{rgb}{"
									+ format(grayscale / 255d) + ","
									+ format(grayscale / 255d) + ","
									+ format(grayscale / 255d) + "}\n");
					customColor.put(c, colorname);
				} else if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
					codeBeginDoc.insert(0,
							"\\definecolor[" + colorname + "][r="
									+ format(grayscale / 255d) + ",g="
									+ format(grayscale / 255d) + ",b="
									+ format(grayscale / 255d) + "]\n");
					customColor.put(c, colorname);

				}
			}
			if (c.equals(GColor.BLACK)) {
				sb.append("black");
				return;
			}
			sb.append(colorname);
		} else {
			if (c0.equals(GColor.BLACK)) {
				sb.append("black");
				return;
			}
			String colorname = "";
			if (customColor.containsKey(c0)) {
				colorname = customColor.get(c0).toString();
			} else {
				int red = c0.getRed();
				int green = c0.getGreen();
				int blue = c0.getBlue();
				colorname = createCustomColor(red, green, blue);
				// Example: \definecolor{orange}{rgb}{1,0.5,0}
				if (format == GeoGebraToPgf.FORMAT_LATEX
						|| format == GeoGebraToPgf.FORMAT_PLAIN_TEX) {
					codeBeginDoc.insert(0,
							"\\definecolor{" + colorname + "}{rgb}{"
									+ format(red / 255d) + ","
									+ format(green / 255d) + ","
									+ format(blue / 255d) + "}\n");
					customColor.put(c0, colorname);
				} else if (format == GeoGebraToPgf.FORMAT_CONTEXT) {
					codeBeginDoc.insert(0, "\\definecolor[" + colorname + "][r="
							+ format(red / 255d) + ",g=" + format(green / 255d)
							+ ",b=" + format(blue / 255d) + "]\n");
					customColor.put(c0, colorname);

				}
			}
			sb.append(colorname);
		}
	}

	/**
	 * Export Implicit plot for polynom degree greater than 2
	 */
	@Override
	protected void drawImplicitPoly(GeoImplicit geo) {
		code.append(
				"\n%WARNING: PGF/Tikz and Gnuplot don't support implicit curves\n");
		code.append("%Rather try PSTricks export\n");
		code.append("%Cannot draw ");
		code.append(getImplicitExpr(geo));
		code.append("\n\n");
	}

	@Override
	protected void drawPolyLine(GeoPolyLine geo) {
		GeoPointND[] path = geo.getPoints();
		if (path.length < 2) {
			return;
		}
		startBeamer(code);
		StringBuilder str = new StringBuilder();
		str.append("\\draw ");
		String s = lineOptionCode(geo, true);
		if (s.length() != 0) {
			s = "[" + s + "] ";
		}
		str.append(s);
		for (int i = 0; i < path.length; i++) {
			Coords coords = path[i].getInhomCoords();
			double x1 = coords.getX();
			double y1 = coords.getY();
			writePoint(x1, y1, str);
			if (i != path.length - 1) {
				str.append("-- ");
			}
		}
		str.append(";\n");
		String s1 = str.toString();
		s1 = s1.replaceAll("-- \\(\\?,\\?\\)--", ";\n \\\\draw " + s);
		code.append(s1);
		endBeamer(code);
	}

	@Override
	protected StringTemplate getStringTemplate() {
		return StringTemplate.get(StringType.PGF);
	}

	/*
	 * @Override protected GGraphics2D createGraphics(FunctionalNVar ef,
	 * Inequality inequality, EuclidianView euclidianView2){ return new
	 * MyGraphicsPgf(ef, inequality, euclidianView2); }
	 */
	@Override
	protected void drawHistogramOrBarChartBox(double[] y, double[] x,
			int length, double width, GeoNumeric g) {
		startBeamer(codeFilledObject);
		String command = g.getDefinition(StringTemplate.noLocalDefault);
		if (command.contains("Binomial") && command.contains("true")) {
			codeFilledObject.append("\\draw");
			String s = lineOptionCode(g, true);
			if (s.length() != 0) {
				codeFilledObject.append("[" + s + "] ");
			}
			writePoint(x[0] + width / 2, 0, codeFilledObject);
			codeFilledObject.append(" -- ");
			writePoint(x[0] + width / 2, y[0], codeFilledObject);
			codeFilledObject.append(";\n");
			for (int i = 0; i < length - 1; i++) {
				codeFilledObject.append("\\draw");
				s = lineOptionCode(g, true);
				if (s.length() != 0) {
					codeFilledObject.append("[" + s + "] ");
				}
				writePoint(x[i] + width / 2, y[i], codeFilledObject);
				codeFilledObject.append(" -- ");
				writePoint(x[i + 1] + width / 2, y[i], codeFilledObject);
				codeFilledObject.append(";\n");
				codeFilledObject.append("\\draw");
				s = lineOptionCode(g, true);
				if (s.length() != 0) {
					codeFilledObject.append("[" + s + "] ");
				}
				writePoint(x[i + 1] + width / 2, y[i], codeFilledObject);
				codeFilledObject.append(" -- ");
				writePoint(x[i + 1] + width / 2, y[i + 1], codeFilledObject);
				codeFilledObject.append(";\n");
			}
		} else {
			for (int i = 0; i < length; i++) {
				barNumber = i + 1;
				codeFilledObject.append("\\draw");
				String s = lineOptionCode(g, true);
				if (s.length() != 0) {
					codeFilledObject.append("[" + s + "] ");
				}
				writePoint(x[i], 0, codeFilledObject);
				codeFilledObject.append(" rectangle ");
				if (x.length == length) {
					writePoint(x[i] + width, y[i], codeFilledObject);
				} else {
					writePoint(x[i + 1], y[i], codeFilledObject);
				}
				codeFilledObject.append(";\n");
			}
		}
		endBeamer(codeFilledObject);

	}

	@Override
	protected boolean fillSpline(GeoCurveCartesian[] curves) {
		String liopco = lineOptionCode(curves[0], true);
		if (!liopco.contains("fill")) {
			return false;
		}
		StringBuilder fill = new StringBuilder();

		liopco = "[" + liopco + "]";
		String template = "\\pgflineto{\\pgfxy(%0,%1)}\n";
		double p = curves[0].getMinParameter();
		double y = curves[0].getFunY().value(curves[0].getMinParameter());
		double yprec = y;
		if (Math.abs(y) < 0.001) {
			y = yprec = 0;
		}
		double xprec = curves[0].getFunX().value(curves[0].getMinParameter());
		double x = xprec;
		fill.append("\\pgfmoveto{\\pgfxy(" + x + "," + y + ")}");
		for (int i = 0; i < curves.length; i++) {
			p = curves[i].getMinParameter();
			y = curves[i].getFunY().value(curves[i].getMinParameter());
			yprec = y;
			if (Math.abs(y) < 0.001) {
				y = yprec = 0;
			}
			double step = (curves[i].getMaxParameter()
					- curves[i].getMinParameter()) / 200;
			xprec = curves[i].getFunX().value(curves[i].getMinParameter());
			x = xprec;
			for (; p <= curves[i].getMaxParameter(); p += step) {
				y = curves[i].getFunY().value(p);
				x = curves[i].getFunX().value(p);
				if (Math.abs(y) < 0.001) {
					y = 0;
				}
				if (Math.abs(x) < 0.001) {
					x = 0;
				}
				fill.append(StringUtil.format(template, xprec, yprec, x, y));
				yprec = y;
				xprec = x;
			}
		}
		fill.append("\\draw" + liopco + "(" + x + "," + y + ") circle(0pt);\n");
		code.append(fill);
		return true;
	}

	/**
	 * @param s
	 *            shape
	 * @param ineq
	 *            inequality
	 * @param geo
	 *            function
	 * @param ds
	 *            view parameters
	 */
	public void superFill(GShape s, Inequality ineq, FunctionalNVar geo,
			double[] ds) {
		((GeoElement) geo).setLineType(ineq.getBorder().lineType);
		switch (ineq.getType()) {
		default:
			// do nothing
			break;
		case INEQUALITY_CONIC:
			GeoConicND conic = ineq.getConicBorder();
			if (conic.getType() == GeoConicNDConstants.CONIC_ELLIPSE
					|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
				((GeoElement) conic)
						.setObjColor(((GeoElement) geo).getObjectColor());
				conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
				((GeoElement) conic)
						.setAlphaValue(((GeoElement) geo).getAlphaValue());
				((GeoElement) conic).setHatchingAngle(
						(int) ((GeoElement) geo).getHatchingAngle());
				((GeoElement) conic).setHatchingDistance(
						((GeoElement) geo).getHatchingDistance());
				((GeoElement) conic)
						.setFillType(((GeoElement) geo).getFillType());
				drawGeoConic(conic);
				break;
			}
		case INEQUALITY_PARAMETRIC_Y:
		case INEQUALITY_PARAMETRIC_X:
		case INEQUALITY_1VAR_X:
		case INEQUALITY_1VAR_Y:
		case INEQUALITY_LINEAR:
			double[] coords = new double[2];
			double zeroY = ds[5] * ds[3];
			double zeroX = ds[4] * (-ds[0]);
			GPathIterator path = s.getPathIterator(null);
			code.append("\\draw[");
			code.append(lineOptionCode((GeoElement) geo, true));
			code.append("]");
			double precX = Integer.MAX_VALUE;
			double precY = Integer.MAX_VALUE;
			while (!path.isDone()) {
				path.currentSegment(coords);
				if (coords[0] == precX && coords[1] == precY) {
					code.delete(code.length() - 2, code.length());
					code.append(";\n\\draw[");
					code.append(lineOptionCode((GeoElement) geo, true));
					code.append("]");
				} else {
					double x1 = (coords[0] - zeroX) / ds[4];
					double y1 = -(coords[1] - zeroY) / ds[5];
					if (y1 > ymax) {
						y1 = ymax;
					}
					if (y1 < ymin) {
						y1 = ymin;
					}
					code.append("(");
					code.append(format(x1));
					code.append(",");
					code.append(format(y1));
					code.append(")--");
				}
				precX = coords[0];
				precY = coords[1];
				path.next();
			}
			int i = code.lastIndexOf(")");
			code.delete(i + 1, code.length());
			code.append(";\n");
			break;
		}
	}
}
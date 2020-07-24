/*
This file is part of GeoGebra.
This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.export.pstricks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import org.geogebra.common.kernel.algos.AlgoSlope;
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
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Greek;
import com.himamis.retex.editor.share.util.Unicode;

/*
 import org.mozilla.javascript.Context;
 import org.mozilla.javascript.Scriptable;
 import org.mozilla.javascript.ScriptableObject; */
/**
 * @author Andy Zhu
 */

public abstract class GeoGebraToAsymptote extends GeoGebraExport {

	private boolean eurosym = false; // Use euro symbol
	private boolean compact = false; // compact code
	private boolean compactcse5 = false; // use cse5 code
	private boolean grayscale = false; // black-and-white vs color
	private boolean dotColors = false;
	private boolean pairName = false; // refer to pairs by a name
	// Indexes number of parabolas and hyperbolas and arcs and functions
	private int parabolaCount = 0; // number of functions used by parabolas
	private int hyperbolaCount = 0; // number of functions used by hyperbolas
	private int arcCount = 0; // number of arcs drawn
	private int functionCount = 0; // number of functions drawn
	private int implicitFuncCount = 0; // number of implicit functions drawn
	private int fillType = 0; // FILL_OPACITY, etc
	private int fontsize; // font size
	// Code for beginning of picture, for points, for Colors, and for background
	// fill
	private StringBuilder codeBeginPic;
	private StringBuilder codePointDecl;
	private StringBuilder codeColors;
	private StringBuilder codeEndDoc;
	// Contains list of points
	private ArrayList<GeoPoint> pointList;
	// Maps unicode expressions to text equivalents
	private Map<String, String> pairNameTable;
	// Maps function return expressions to function #
	private Map<String, Integer> implicitPolyTable;
	// use the following packages for Asymptote and LaTeX commands
	// importContour = false, importMath = false, importGraph = false,
	// usepackage_amssymb = false, usepackage_amsmath = false,
	// usepackage_mathrsfs = false;
	private Set<String> usepackage;
	/** packages to import */
	public Set<String> importpackage;
	/** whether to fill inequality */
	public boolean fillInequality = false;

	/**
	 * @param app
	 *            application
	 */
	public GeoGebraToAsymptote(final App app) {
		super(app);
	}

	/**
	 * generateAllCode: generate Asymptote output by assembling snippets and
	 * sanitizing
	 */
	@Override
	public void generateAllCode() {

		// reset global variables
		parabolaCount = 0;
		hyperbolaCount = 0;
		arcCount = 0;
		functionCount = 0;
		implicitFuncCount = 0;
		fillType = 0;
		/*
		 * importContour = false; importMath = false; importGraph = false;
		 * usepackage_amssymb = false; usepackage_amsmath = false;
		 * usepackage_mathrsfs = false;
		 */
		usepackage = new TreeSet<>();
		importpackage = new TreeSet<>();
		pointList = new ArrayList<>(); // list of pairs, for cse5
		pairNameTable = new HashMap<>(); // map of coordinates ->
														// point's name
		implicitPolyTable = new HashMap<>(); // function(x,y)
															// return value to
															// function #
		// map of rgb -> alphabet pen names
		customColor = new HashMap<>();

		// retrieve flags from frame
		format = frame.getFormat();
		compact = frame.getAsyCompact() || frame.getAsyCompactCse5();
		compactcse5 = frame.getAsyCompactCse5();
		fillType = frame.getFillType();
		fontsize = frame.getFontSize();
		grayscale = frame.isGrayscale();
		pairName = frame.getUsePairNames();
		dotColors = frame.getKeepDotColors();

		// initialize unit variables, scale ratio = yunit/xunit;
		xunit = frame.getXUnit();
		yunit = frame.getYUnit();

		// initialize new StringBuilders for Asymptote code
		// overall output
		code = new StringBuilder();
		// beginning statements/comments
		codePreamble = new StringBuilder();
		// beginning statements/comments
		codeBeginPic = new StringBuilder();
		// definition of pairs, for cse5 mode
		codePointDecl = new StringBuilder();
		// pens corresponding to certain rgb values
		codeColors = new StringBuilder();
		// dots and labels
		codePoint = new StringBuilder();
		// all major geometric constructions
		codeFilledObject = new StringBuilder();
		// axes, grid, and so forth
		codeBeginPic = new StringBuilder();
		// ending code, odds and ends
		codeEndDoc = new StringBuilder();

		// generate point list
		if (pairName) {
			for (int step = 0; step < construction.steps(); step++) {
				GeoElementND[] geos = construction.getConstructionElement(step)
						.getGeoElements();
				for (int j = 0; j < geos.length; j++) {
					GeoElementND g = geos[j];
					if (g.isEuclidianVisible() && g.isGeoPoint()) {
						pointList.add((GeoPoint) g);
					}
				}
			}
		}

		// In cse5, initialize pair definitions.
		initPointDeclarations();

		// get all objects from construction and "draw" by creating Asymptote
		// code
		// **Run this before generating other code in case it causes other
		// changes
		// such as which packages should be imported.**
		drawAllElements();

		// Write preamble. If compact option unchecked, include liberal
		// documentation.
		if (!compact) {
			codePreamble.append(" /* Geogebra to Asymptote conversion, ");
			// userscripts.org/scripts/show/72997
			codePreamble.append("documentation at artofproblemsolving.com/Wiki ");
			codePreamble.append("go to User:Azjps/geogebra */\n");
		}
		importpackage.add("graph");
		for (String s : importpackage) {
			codePreamble.append("import " + s + "; ");
		}
		for (String s : usepackage) {
			codePreamble.append("usepackage(\"" + s + "\"); ");
		}
		/*
		 * if (usepackage_amssymb) codePreamble.append(
		 * "usepackage(\"amssymb\"); "); if (usepackage_amsmath)
		 * codePreamble.append("usepackage(\"amsmath\"); "); if (importContour)
		 * codePreamble.append("import contour; "); if (importMath)
		 * codePreamble.append("import math; ");
		 */
		codePreamble.append("size(" + format(frame.getLatexWidth()) + "cm); ");
		initUnitAndVariable();

		// Draw grid
		if (euclidianView.getShowGrid() && frame.getShowAxes()) {
			drawGrid();
		}
		// Draw axis
		if ((euclidianView.getShowXaxis() || euclidianView.getShowYaxis())
				&& frame.getShowAxes()) {
			drawAxis();
		}

		// Clip frame
		codeEndDoc.append(
				"\nclip((xmin,ymin)--(xmin,ymax)--(xmax,ymax)--(xmax,ymin)--cycle); ");
		// Background color
		if (!euclidianView.getBackgroundCommon().equals(GColor.WHITE)) {
			if (!compact) {
				codeEndDoc.append("\n");
			}
			codeEndDoc.append("shipout(bbox(");
			colorCode(euclidianView.getBackgroundCommon(), codeEndDoc);
			codeEndDoc.append(",Fill)); ");
		}
		// Re-scale
		if (format(yunit).compareTo(format(xunit)) != 0) {
			if (!compact) {
				codeEndDoc.append("\n /* re-scale y/x */\n");
			}
			packSpaceBetween(codeEndDoc, "currentpicture", "=",
					"yscale(" + format(yunit / xunit) + ")", "*",
					"currentpicture; ");
		}
		if (!compact) {
			codeEndDoc.append("\n /* end of picture */");
		}

		// add code for Points and Labels
		code.append("\n");
		if (!compact) {
			code.append(" /* dots and labels */");
		}
		code.append(codePoint);

		/*
		 * String formatFont=resizeFont(app.getFontSize()); if
		 * (null!=formatFont){ codeBeginPic.insert(0,formatFont+"\n");
		 * code.append("}\n"); }
		 */// Order: TODO
			// Preamble, Colors, Points, Fills, Pic, Objects, regular code,
			// EndDoc
		if (!compact) {
			code.insert(0, " /* draw figures */");
		}
		code.insert(0, "\n");
		code.insert(0, codeBeginPic);
		code.insert(0, codeFilledObject);
		if (codeFilledObject.length() != 0) {
			code.insert(0, "\n");
		}
		code.insert(0, codePointDecl);
		if (!compact) {
			code.insert(0, codeColors);
		} else if (codeColors.length() != 0) {
			code.insert(0, "\npen" + codeColors.substring(1) + "; ");
		}
		code.insert(0, codePreamble);
		code.append(codeEndDoc); // clip frame, background fill, re-scaling

		// code to temporarily remove pi from code, other unicode issues
		convertUnicodeToText(code);

		frame.write(code);
	}

	@Override
	protected void drawLocus(GeoLocus geo) {
		ArrayList<MyPoint> ll = geo.getPoints();
		Iterator<MyPoint> it = ll.iterator();
		boolean first = true, first2 = true; // whether to write join operators
												// afterwards

		if (!compact) {
			code.append(" /* locus construction */\n");
		}
		startDraw();
		while (it.hasNext()) {
			MyPoint mp = it.next();
			if (mp.x > xmin && mp.x < xmax && mp.y > ymin && mp.y < ymax) {
				String x = format(mp.x), y = format(mp.y);
				if (first && first2) {
					code.append("(");
					first = false;
					first2 = false;
				} else if (first) { // don't draw connecting line
					code.append("^^(");
					first = false;
				} else if (mp.getLineTo()) {
					code.append("--(");
				} else {
					code.append("^^(");
				}
				code.append(x + "," + y + ")");
			} else {
				first = true;
			}
		}
		endDraw(geo);
	}

	@Override
	protected void drawBoxPlot(GeoNumeric geo) {
		AlgoBoxPlot algo = ((AlgoBoxPlot) geo.getParentAlgorithm());
		double y = algo.getA().getDouble();
		double height = algo.getB().getDouble();
		double[] lf = algo.getLeftBorders();
		double min = lf[0];
		double q1 = lf[1];
		double med = lf[2];
		double q3 = lf[3];
		double max = lf[4];

		// Min vertical bar
		drawLine(min, y - height, min, y + height, geo);
		// Max vertical bar
		drawLine(max, y - height, max, y + height, geo);
		// Med vertical bar
		drawLine(med, y - height, med, y + height, geo);
		// Min-q1 horizontal
		drawLine(min, y, q1, y, geo);
		// q3-max
		drawLine(q3, y, max, y, geo);

		// Rectangle q1-q3
		startTransparentFill(codeFilledObject);
		codeFilledObject.append("box(");
		addPoint(format(q1), format(y - height), codeFilledObject);
		codeFilledObject.append(",");
		addPoint(format(q3), format(y + height), codeFilledObject);
		codeFilledObject.append(")");
		endTransparentFill(geo, codeFilledObject);
	}

	@Override
	protected void drawHistogramOrBarChartBox(double[] y, double[] x,
			int length, double width, GeoNumeric g) {
		String command = g.getDefinition(StringTemplate.noLocalDefault);
		if (command.contains("Binomial") && command.contains("true")) {
			startTransparentFill(codeFilledObject);
			codeFilledObject.append("(" + format(x[0] + width / 2));
			codeFilledObject.append(",0) -- (");
			codeFilledObject.append(format(x[0] + width / 2));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[0]) + ")");
			endTransparentFill(g, codeFilledObject);
			for (int i = 0; i < length - 1; i++) {
				startTransparentFill(codeFilledObject);
				codeFilledObject.append("(" + format(x[i] + width / 2));
				codeFilledObject.append("," + format(y[i]) + ") -- (");
				codeFilledObject.append(format(x[i + 1] + width / 2));
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i]) + ")");
				endTransparentFill(g, codeFilledObject);
				startTransparentFill(codeFilledObject);
				codeFilledObject.append("(" + format(x[i + 1] + width / 2));
				codeFilledObject.append("," + format(y[i]) + ") -- (");
				codeFilledObject.append(format(x[i + 1] + width / 2));
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i + 1]) + ")");
				endTransparentFill(g, codeFilledObject);
			}
		} else {
			for (int i = 0; i < length; i++) {
				barNumber = i + 1;
				startTransparentFill(codeFilledObject);
				codeFilledObject.append("box((");
				codeFilledObject.append(format(x[i]));
				codeFilledObject.append(",0),(");
				if (x.length == length) {
					codeFilledObject.append(format(x[i] + width));
				} else {
					codeFilledObject.append(format(x[i + 1]));
				}
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i]));
				codeFilledObject.append("))");
				endTransparentFill(g, codeFilledObject);
			}
		}
	}

	@Override
	protected void drawSumTrapezoidal(GeoNumeric geo) {
		AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) geo
				.getParentAlgorithm();
		int n = algo.getIntervals();
		double[] y = algo.getValues();
		double[] x = algo.getLeftBorder();
		for (int i = 0; i < n; i++) {
			startTransparentFill(codeFilledObject);
			codeFilledObject.append("(");
			codeFilledObject.append(format(x[i]));
			codeFilledObject.append(",0)--(");
			codeFilledObject.append(format(x[i + 1]));
			codeFilledObject.append(",0)--(");
			codeFilledObject.append(format(x[i + 1]));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[i + 1]));
			codeFilledObject.append(")--(");
			codeFilledObject.append(format(x[i]));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[i]));
			codeFilledObject.append(")--cycle");
			endTransparentFill(geo, codeFilledObject);
		}
	}

	@Override
	protected void drawSumUpperLower(GeoNumeric geo) {
		AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) geo
				.getParentAlgorithm();
		int n = algo.getIntervals();
		double step = algo.getStep();
		double[] y = algo.getValues();
		double[] x = algo.getLeftBorder();

		for (int i = 0; i < n; i++) {
			startTransparentFill(codeFilledObject);
			codeFilledObject.append("box((");
			codeFilledObject.append(format(x[i]));
			codeFilledObject.append(",0),(");
			codeFilledObject.append(format(x[i] + step));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[i]));
			codeFilledObject.append("))");
			endTransparentFill(geo, codeFilledObject);
		}
	}

	@Override
	protected void drawIntegralFunctions(GeoNumeric geo) {
		importpackage.add("graph");

		AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo
				.getParentAlgorithm();
		GeoFunction f = algo.getF(), // function f
				g = algo.getG(); // function g

		// String Expression of f and g
		String valueF = f.toValueString(getStringTemplate()),
				valueG = g.toValueString(getStringTemplate());
		valueF = parseFunction(valueF);
		valueG = parseFunction(valueG);
		// String expressions for f(a) and g(b)
		// String fa = format(f.evaluate(a));
		// String gb = format(g.evaluate(b));

		if (!compact) {
			codeFilledObject.append("\n");
		}

		// write functions for f and g if they do not already exist.
		int indexFunc = -1;
		String tempFunctionCountF = "f" + Integer.toString(functionCount + 1);
		String returnCode = "(real x){return " + valueF + ";} ";
		// search for previous occurrences of function
		// TODO Hashtable rewrite?
		if (compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if (indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",
						indexFunc);
				tempFunctionCountF = codeFilledObject
						.substring(indexFuncStart + 1, indexFunc);
			}
		}
		// write function
		if (indexFunc == -1) {
			functionCount++;
			packSpaceBetween(codeFilledObject, "real f" + functionCount,
					"(real x)", "{", "return " + valueF + ";", "} ");
		}

		indexFunc = -1;
		String tempFunctionCountG = "f" + Integer.toString(functionCount + 1);
		returnCode = "(real x){return " + valueG + ";} ";
		// search for previous occurrences of function
		if (compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if (indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",
						indexFunc);
				tempFunctionCountG = codeFilledObject
						.substring(indexFuncStart + 1, indexFunc);
			}
		} // write function
		if (indexFunc == -1) {
			functionCount++;
			packSpaceBetween(codeFilledObject, "real f" + functionCount,
					"(real x)", "{", "return " + valueG + ";", "} ");
		}
		// double a and b
		double a = algo.getA().getDouble(), b = algo.getB().getDouble();
		// String output for a and b
		String sa = format(a), sb = format(b);
		// draw graphs of f and g
		startTransparentFill(codeFilledObject);
		packSpaceBetween(codeFilledObject, "graph(" + tempFunctionCountF + ",",
				sa + ",", sb + ")", "--", "graph(" + tempFunctionCountG + ",",
				sb + ",", sa + ")", "--cycle");
		endTransparentFill(geo, codeFilledObject);
	}

	@Override
	protected void drawIntegral(GeoNumeric geo) {
		importpackage.add("graph");

		AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo
				.getParentAlgorithm();
		GeoFunction f = algo.getFunction(); // function f between a and b
		String a = format(algo.getA().getDouble());
		String b = format(algo.getB().getDouble());
		if (algo.getA().getDouble() == Double.NEGATIVE_INFINITY) {
			a = format(xmin);
		}
		if (algo.getB().getDouble() == Double.POSITIVE_INFINITY) {
			b = format(xmax);
		}
		String value = f.toValueString(getStringTemplate());
		value = parseFunction(value);
		if (!isLatexFunction(f.toValueString(StringTemplate.noLocalDefault))) {
			double af = xmin;
			double bf = xmax;
			if (f.hasInterval()) {
				af = f.getIntervalMin();
				bf = f.getIntervalMax();
			}
			f.setInterval(algo.getA().getDouble(), algo.getB().getDouble());
			drawFunction(f, true, geo, true);
			drawFunction(f, true, geo, false);
			f.setInterval(af, bf);
			if (f.isEuclidianVisible()) {
				drawFunction(f, false, geo, false);
			}
		} else {
			int indexFunc = -1;
			String tempFunctionCount = "f"
					+ Integer.toString(functionCount + 1);
			String returnCode = "(real x){return (" + value + ");} ";
			// search for previous occurrences of function
			if (compact) {
				indexFunc = codeFilledObject.indexOf(returnCode);
				if (indexFunc != -1) {
					// retrieve name of previously used function
					int indexFuncStart = codeFilledObject.lastIndexOf(" ",
							indexFunc);
					tempFunctionCount = codeFilledObject
							.substring(indexFuncStart + 1, indexFunc);
				}
			} // write function
			if (indexFunc == -1) {
				functionCount++;
				if (!compact) {
					codeFilledObject.append("\n");
				}
				codeFilledObject.append("real f");
				codeFilledObject.append(functionCount);
				packSpace(codeFilledObject, "(real x)");
				codeFilledObject.append("{return ");
				codeFilledObject.append(value);
				codeFilledObject.append(";} ");
			}

			startTransparentFill(codeFilledObject);
			codeFilledObject.append("graph(");
			codeFilledObject.append(tempFunctionCount);
			codeFilledObject.append(",");
			codeFilledObject.append(a);
			codeFilledObject.append(",");
			codeFilledObject.append(b);
			codeFilledObject.append(")--");
			addPoint(b, "0", codeFilledObject);
			codeFilledObject.append("--");
			addPoint(a, "0", codeFilledObject);
			codeFilledObject.append("--cycle");
			endTransparentFill(geo, codeFilledObject);
		}
	}

	@Override
	protected void drawSlope(GeoNumeric geo) { // TODO: label bug?
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

		startTransparentFill(codeFilledObject);
		addPoint(format(x), format(y), codeFilledObject);
		codeFilledObject.append("--");
		addPoint(format(xright), format(y), codeFilledObject);
		codeFilledObject.append("--");
		addPoint(format(xright), format(y + rwHeight), codeFilledObject);
		codeFilledObject.append("--cycle");
		endTransparentFill(geo, codeFilledObject);

		// draw Label
		double xLabelHor = (x + xright) / 2;
		double yLabelHor = y - ((euclidianView.getFont().getSize() + 2)
				/ euclidianView.getYscale());
		GColor geocolor = geo.getObjectColor();

		if (!compact) {
			codePoint.append("\n");
		}
		packSpaceAfter(codePoint, "label(\"$" + slopeTriangleSize + "$\",",
				"(" + format(xLabelHor) + ",", format(yLabelHor) + "),", "NE",
				"*");
		if (compact) {
			codePoint.append("lsf");
		} else {
			codePoint.append("labelscalefactor");
		}
		if (!geocolor.equals(GColor.BLACK)) {
			codePoint.append(",");
			colorCode(geocolor, codePoint);
		}
		codePoint.append("); ");
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
		tempPoint.remove();

		double angSt = Math.atan2(firstVec[1], firstVec[0]);

		// double angExt = geo.getValue();
		double angExt = geo.getRawAngle();
		if (angExt > Math.PI * 2) {
			angExt -= Math.PI * 2;
		}

		// if (geo.getAngleStyle() == GeoAngle.ANGLE_ISCLOCKWISE) {
		// angSt += angExt;
		// angExt = 2.0*Math.PI-angExt;
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

		// StringBuilder tempsb = new StringBuilder();
		startTransparentFill(codeFilledObject);
		// if right angle and decoration is a little square
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

			for (int i = 0; i < 4; i++) {
				addPoint(format(x[2 * i]), format(x[2 * i + 1]),
						codeFilledObject);
				codeFilledObject.append("--");
			}
			codeFilledObject.append("cycle");

			// transparent fill options
			endTransparentFill(geo, codeFilledObject);
		} else { // draw arc for the angle.
			codeFilledObject.append("arc(");
			addPoint(format(m[0]), format(m[1]), codeFilledObject);
			codeFilledObject.append(",");
			codeFilledObject.append(format(r));
			codeFilledObject.append(",");
			codeFilledObject.append(format(Math.toDegrees(angSt)));
			codeFilledObject.append(",");
			codeFilledObject.append(format(Math.toDegrees(angExt)));
			codeFilledObject.append(")--(");
			codeFilledObject.append(format(m[0]));
			codeFilledObject.append(",");
			codeFilledObject.append(format(m[1]));
			codeFilledObject.append(")--cycle");
			// transparent fill options
			endTransparentFill(geo, codeFilledObject);

			// draw the [circular?] dot if right angle and decoration is dot
			if (drawAngleAs(geo, EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT)) {
				double diameter = geo.getLineThickness()
						/ euclidianView.getXscale();
				double radius = arcSize / euclidianView.getXscale() / 1.7;
				double labelAngle = (angSt + angExt) / 2.0;
				double x1 = m[0] + radius * Math.cos(labelAngle);
				double x2 = m[1] + radius * Math.sin(labelAngle);

				startDraw();
				if (compactcse5) {
					code.append("CR(");
				} else {
					code.append("circle(");
				}
				addPoint(format(x1), format(x2), code);
				code.append(",");
				code.append(format(diameter));
				code.append(")");
				endDraw(geo);
			}
			if (geo.getDecorationType() != GeoElementND.DECORATION_NONE) {
				markAngle(geo, r, m, angSt, angExt);
			}
		}
	}

	@Override
	protected void drawArrowArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd0, double r, boolean anticlockwise) {
		startDraw();
		code.append("arc(");
		addPoint(format(vertex[0]), format(vertex[1]), code);
		code.append(",");
		code.append(format(r));
		code.append(",");
		code.append(format(Math.toDegrees(angSt)));
		code.append(",");
		// The arrow head goes away from the line.
		// Arrow Winset=0.25, see PStricks spec for arrows
		double arrowHeight = (geo.getLineThickness() * 0.8 + 3) * 1.4 * 3 / 4;
		double angle = Math.asin(arrowHeight / 2 / euclidianView.getXscale() / r);
		double angEnd = angEnd0 - angle;
		code.append(format(Math.toDegrees(angEnd)));
		code.append(")");
		if (lineOptionCode(geo, true) != null) {
			packSpaceAfter(code, ",");
			code.append(lineOptionCode(geo, true));
		} // TODO: resize?
		if (anticlockwise) {
			code.append(",EndArcArrow(6)");
		} else {
			code.append(",BeginArcArrow(6)");
		}
		code.append("); ");
	}

	// angSt, angEnd in degrees. r = radius.
	@Override
	protected void drawArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd, double r) {
		startDraw();
		code.append("arc(");
		addPoint(format(vertex[0]), format(vertex[1]), code);
		code.append(",");
		code.append(format(r));
		code.append(",");
		code.append(format(Math.toDegrees(angSt)));
		code.append(",");
		code.append(format(Math.toDegrees(angEnd)));
		code.append(")");
		endDraw(geo);
	}

	@Override
	protected void drawTick(GeoAngle geo, double[] vertex, double angle0) {
		double cos = Math.cos(angle0);
		double sin = Math.sin(-angle0);
		double radius = geo.getArcSize();
		double diff = 2.5 + geo.getLineThickness() / 4d;
		double x1 = euclidianView.toRealWorldCoordX(
				vertex[0] + (radius - diff) * cos);
		double x2 = euclidianView.toRealWorldCoordX(
				vertex[0] + (radius + diff) * cos);
		double y1 = euclidianView.toRealWorldCoordY(vertex[1] + (radius - diff)
				* sin * euclidianView.getScaleRatio());
		double y2 = euclidianView.toRealWorldCoordY(vertex[1] + (radius + diff)
				* sin * euclidianView.getScaleRatio());

		startDraw();
		addPoint(format(x1), format(y1), code);
		code.append("--");
		addPoint(format(x2), format(y2), code);
		endDraw(geo);
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

		geoPoint.remove();

		// draw Line for Slider
		startDraw();
		addPoint(format(x), format(y), code);
		code.append("--");
		if (horizontal) {
			x += width;
		} else {
			y += width;
		}
		addPoint(format(x), format(y), code);
		endDraw(geo);
	}

	@Override
	protected void drawPolygon(GeoPolygon geo) {
		GeoPointND[] points = geo.getPoints();
		// StringBuilder tempsb = new StringBuilder();

		startTransparentFill(codeFilledObject);
		for (int i = 0; i < points.length; i++) {
			Coords coords = points[i].getCoordsInD2();
			double x = coords.getX(), y = coords.getY(), z = coords.getZ();
			x = x / z;
			y = y / z;
			addPoint(format(x), format(y), codeFilledObject);
			codeFilledObject.append("--");
		}
		codeFilledObject.append("cycle");
		endTransparentFill(geo, codeFilledObject);
	}

	@Override
	protected void drawText(GeoText geo) {
		boolean isLatex = geo.isLaTeX();
		String st = geo.getTextStringSafe();
		if (isLatex) {
			st = StringUtil.toLaTeXString(st, true);
		}
		// try to replace euro symbol
		if (st.contains("\u20ac")) {
			st = st.replace("\\u20ac", "\\\\euro{}");
			if (!eurosym) {
				codePreamble.append("usepackage(\"eurosym\"); ");
			}
		}
		GColor geocolor = geo.getObjectColor();
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
		boolean comma = false;

		// One line
		if (id == -1 || isLatex) {
			if (!compact) {
				code.append("\n");
			}
			code.append("label(\"");
			addText(st, isLatex, style);
			code.append("\",");
			addPoint(format(x), format(y), code);
			code.append(",SE*");
			if (compact) {
				code.append("lsf");
			} else {
				code.append("labelscalefactor");
			}
			if (!geocolor.equals(GColor.BLACK)) { // color
				code.append(",");
				comma = true;
				colorCode(geocolor, code);
			}
			if (size != getApp().getFontSize()) { // fontsize
				if (!comma) {
					code.append(",");
				} else {
					packSpace(code, "+");
				}
				code.append("fontsize(");
				code.append(fontsize + (size - getApp().getFontSize()));
				code.append(")");
			} else if (compactcse5) { // use default font pen for cse5
				if (!comma) {
					code.append(",");
				} else {
					packSpace(code, "+");
				}
				code.append("fp");
			}
			code.append("); ");
		}
		// MultiLine
		else {
			StringBuilder sb = new StringBuilder();
			GFont font = AwtFactory.getPrototype().newFont(
					geo.isSerifFont() ? "Serif" : "SansSerif", style, size);
			int width = getWidth(st, sb, font);

			if (!compact) {
				code.append("\n");
			}
			code.append("label(\"$");
			code.append("\\parbox{");
			code.append(format(
					width * (xmax - xmin) * xunit / euclidianView.getWidth()
							+ 1));
			code.append(" cm}{");
			addText(sb.toString(), isLatex, style);
			code.append("}$\",");
			addPoint(format(x), format(y), code);
			code.append(",SE*");
			if (compact) {
				code.append("lsf");
			} else {
				code.append("labelscalefactor");
			}
			if (!geocolor.equals(GColor.BLACK)) { // color
				code.append(",");
				comma = true;
				colorCode(geocolor, code);
			}
			if (size != getApp().getFontSize()) { // fontsize
				if (!comma) {
					code.append(",");
				} else {
					packSpace(code, "+");
				}
				code.append("fontsize(");
				code.append(fontsize + (size - getApp().getFontSize()));
				code.append(")");
			} else if (compactcse5) { // use default font pen for cse5
				if (!comma) {
					code.append(",");
				} else {
					packSpace(code, "+");
				}
				code.append("fp");
			}
			code.append("); ");
		}
	}

	@Override
	protected void drawGeoConicPart(GeoConicPart geo) {
		StringBuilder tempsb = new StringBuilder();
		double r1 = geo.getHalfAxes()[0], r2 = geo.getHalfAxes()[1];
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
			startAngle -= Math.PI * 2;
		}
		// Fill if: SECTOR and fill type not set to FILL_NONE
		if (m11 == 1 && m22 == 1 && m12 == 0 && m21 == 0) {
			if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR
					&& fillType != ExportSettings.FILL_NONE) {
				startTransparentFill(tempsb);
			} else {
				startDraw(tempsb);
			}
			tempsb.append("shift(");
			addPoint(format(tx), format(ty), tempsb);
			tempsb.append(")*xscale(");
			tempsb.append(format(r1));
			tempsb.append(")*yscale(");
			tempsb.append(format(r2));
			tempsb.append(")*arc((0,0),1,");
			tempsb.append(format(Math.toDegrees(startAngle)));
			tempsb.append(",");
			tempsb.append(format(Math.toDegrees(endAngle)));
			tempsb.append(")");
		} else {
			StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
			sb1.append(format(r1));
			sb1.append("*cos(t)");
			sb2.append(format(r2));
			sb2.append("*sin(t)");

			arcCount++;
			if (!compact) {
				tempsb.append("\n");
			}
			tempsb.append("pair arc");
			tempsb.append(arcCount);
			packSpace(tempsb, "(real t)");
			tempsb.append("{return (");
			tempsb.append(format(m11));
			tempsb.append("*");
			tempsb.append(sb1);
			tempsb.append("+");
			tempsb.append(format(m12));
			tempsb.append("*");
			tempsb.append(sb2);
			tempsb.append("+");
			tempsb.append(format(tx));
			tempsb.append(",");
			tempsb.append(format(m21));
			tempsb.append("*");
			tempsb.append(sb1);
			tempsb.append("+");
			tempsb.append(format(m22));
			tempsb.append("*");
			tempsb.append(sb2);
			tempsb.append("+");
			tempsb.append(format(ty));
			tempsb.append(");} ");

			if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR
					&& fillType != ExportSettings.FILL_NONE) {
				startTransparentFill(tempsb);
			} else {
				startDraw(tempsb);
			}
			tempsb.append("graph(arc");
			tempsb.append(arcCount);
			tempsb.append(",");
			tempsb.append(format(startAngle));
			tempsb.append(",");
			tempsb.append(format(endAngle));
			tempsb.append(")");
		}
		if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
			tempsb.append("--");
			addPoint(format(tx), format(ty), tempsb);
			tempsb.append("--cycle");
			if (fillType == ExportSettings.FILL_NONE) {
				endDraw(geo, tempsb);
			} else {
				endTransparentFill(geo, tempsb);
			}
		} else {
			endDraw(geo, tempsb);
		}

		if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR
				&& fillType != ExportSettings.FILL_NONE) {
			codeFilledObject.append(tempsb);
		} else {
			code.append(tempsb);
		}
	}

	@Override
	protected void drawSingleCurveCartesian(GeoCurveCartesian geo,
			boolean trasparency) {
		importpackage.add("graph");

		// boolean isClosed=geo.isClosedPath();
		String fx = parseFunction(geo.getFunX(getStringTemplate()));
		String fy = parseFunction(geo.getFunY(getStringTemplate()));
		String variable = parseFunction(geo.getVarString(getStringTemplate()));
		// boolean warning=!("t".equals(variable));

		int indexFunc = -1;
		String tempFunctionCount = "f" + Integer.toString(functionCount + 1);
		String returnCode = "(real " + variable + "){return (" + fx + "," + fy
				+ ");} ";
		// search for previous occurrences of function
		if (compact) {
			indexFunc = codeFilledObject.indexOf(returnCode);
			if (indexFunc != -1) {
				// retrieve name of previously used function
				int indexFuncStart = codeFilledObject.lastIndexOf(" ",
						indexFunc);
				tempFunctionCount = codeFilledObject
						.substring(indexFuncStart + 1, indexFunc);
			} else if (code.indexOf(returnCode) != -1) {
				indexFunc = code.indexOf(returnCode);
				int indexFuncStart = code.lastIndexOf(" ", indexFunc);
				tempFunctionCount = code.substring(indexFuncStart + 1,
						indexFunc);
				indexFunc = code.indexOf(returnCode);
			}
		} // write function
		if (indexFunc == -1) {
			functionCount++;
			if (!compact) {
				code.append("\n");
			}
			code.append("pair f");
			code.append(functionCount);
			packSpace(code, "(real " + variable + ")");
			code.append("{return (");
			code.append(fx);
			code.append(",");
			code.append(fy);
			code.append(");} ");
		}

		startDraw();
		double start = geo.getMinParameter(), end = geo.getMaxParameter();
		code.append("graph(");
		code.append(tempFunctionCount);
		code.append(",");
		code.append(format(start));
		code.append(",");
		code.append(format(end));
		code.append(")");
		endDraw(geo);
	}

	@Override
	protected void drawFunction(GeoFunction geo) {
		drawFunction(geo, false, null, false);
	}

	/**
	 * @param geo
	 *            function
	 * @param integral
	 *            whether to fill below
	 * @param geo1
	 *            used for color
	 * @param contour
	 *            whether to show contour
	 */
	protected void drawFunction(GeoFunction geo, boolean integral,
			GeoNumeric geo1, boolean contour) {
		importpackage.add("graph");
		Function f = geo.getFunction();
		if (f == null) {
			return;
		}
		String value = f.toValueString(getStringTemplate());
		value = parseFunction(value); // killSpace(StringUtil.toLaTeXString(value,true));
		value = value.replace("\\\\pi", "pi");
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

			int indexFunc = -1;
			String tempFunctionCount = null;
			String returnCode = null;
			if (!isLatexFunction(
					f.toValueString(StringTemplate.noLocalDefault))) {
				StringBuilder sb = new StringBuilder();
				StringBuilder lineBuilder;
				if (integral) {
					sb.append("path p" + (++functionCount) + ";\n");
					sb.append("p" + functionCount + "=");
					String template = "(%0,%1) -- (%2,%3) -- ";
					lineBuilder = drawNoLatexFunction(geo, xrangemax, xrangemin,
							400, template);
					lineBuilder.append(
							"(" + format(geo.getIntervalMax()) + ",0) -- ("
									+ format(geo.getIntervalMin()) + ",0) -- ");
					StringBuilder color = new StringBuilder();
					colorCode(geo1.getObjectColor(), color);
					String str = "cycle;\ndraw(p" + functionCount + "," + color;
					if (!contour) {
						str = "cycle;\nfill(p" + functionCount + "," + color;
						str += "+opacity(0.05)";
					}
					lineBuilder.append(str);
					lineBuilder.append(");\n");
					sb.append(lineBuilder);
					lineBuilder = sb;
				} else {
					colorCode(geo.getObjectColor(), sb);
					String template = "draw( (%0,%1) -- (%2,%3)," + sb
							+ "+linewidth(" + geo.getLineThickness() + "));\n";
					lineBuilder = drawNoLatexFunction(geo, xrangemax, xrangemin,
							400, template);
				}

				code.append(lineBuilder.toString() + ";\n");
			} else {
				tempFunctionCount = "f" + Integer.toString(functionCount + 1);
				returnCode = "(real x){return " + value + ";} ";
				// search for previous occurrences of function
				if (compact) {
					indexFunc = codeFilledObject.indexOf(returnCode);
					if (indexFunc != -1) {
						// retrieve name of previously used function
						int indexFuncStart = codeFilledObject.lastIndexOf(" ",
								indexFunc);
						tempFunctionCount = codeFilledObject
								.substring(indexFuncStart + 1, indexFunc);
					} else if (code.indexOf(returnCode) != -1) {
						indexFunc = code.indexOf(returnCode);
						int indexFuncStart = code.lastIndexOf(" ", indexFunc);
						tempFunctionCount = code.substring(indexFuncStart + 1,
								indexFunc);
						indexFunc = code.indexOf(returnCode);
					}
				} // write function
				if (indexFunc == -1) {
					functionCount++;
					if (!compact) {
						code.append("\n");
					}
					code.append("real ");
					code.append(tempFunctionCount);
					packSpace(code, "(real x)");
					code.append("{return ");
					code.append(value);
					code.append(";} ");
				}
				startDraw();

				code.append("graph(");
				code.append(tempFunctionCount);
				code.append(",");
				// add/subtract 0.01 to prevent 1/x, log(x) undefined behavior
				code.append(format(xrangemin + 0.01));
				code.append(",");
				code.append(format(xrangemax - 0.01));
				code.append(")");
				endDraw(geo);
			}
			// ? recycled code of sorts?*/
			xrangemax += PRECISION_XRANGE_FUNCTION;
			a = xrangemax;

		}
	}

	// draw vector with EndArrow(6)
	@Override
	protected void drawGeoVector(GeoVector geo) {
		GeoPointND pointStart = geo.getStartPoint();
		String x1, y1;
		if (pointStart == null) {
			x1 = "0";
			y1 = "0";
		} else {
			Coords c = pointStart.getCoords();
			x1 = format(c.getX() / c.getZ());
			y1 = format(c.getY() / c.getZ());
		}
		double[] coord = new double[3];
		geo.getCoords(coord);
		String x2 = format(
				coord[0] + kernel.getAlgebraProcessor().evaluateToDouble(x1));
		String y2 = format(
				coord[1] + kernel.getAlgebraProcessor().evaluateToDouble(y1));

		if (!compact) {
			code.append("\n");
		}
		if (compactcse5) {
			code.append("D(");
		} else {
			code.append("draw(");
		}
		addPoint(x1, y1, code);
		code.append("--");
		addPoint(x2, y2, code);
		if (lineOptionCode(geo, true) != null) {
			code.append(",");
			if (!compact) {
				code.append(" ");
			}
			code.append(lineOptionCode(geo, true));
		}
		code.append(",EndArrow(6)); ");
	}

	private void drawCircle(GeoConicND geo) {
		StringBuilder tempsb = new StringBuilder();
		boolean nofill = geo.getAlphaValue() < 0.05;

		if (xunit == yunit) {
			// draw a circle
			double x = geo.getTranslationVector().getX();
			double y = geo.getTranslationVector().getY();
			double r = geo.getHalfAxes()[0];
			String tmpr = format(r); // removed *xunit, unsure of function

			if (nofill) {
				if (!compact) {
					tempsb.append("\n");
				}
				if (compactcse5) {
					tempsb.append("D(CR(");
				} else {
					tempsb.append("draw(circle(");
				}
			} else {
				startTransparentFill(tempsb);
				if (compactcse5) {
					tempsb.append("CR(");
				} else {
					tempsb.append("circle(");
				}
			}
			addPoint(format(x), format(y), tempsb);
			packSpaceAfter(tempsb, ",");
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpr) != 0) {
				tempsb.append(tmpr);
			} else {
				tempsb.append(r);
			}
			tempsb.append(")");
			if (nofill) {
				endDraw(geo, tempsb);
			} else {
				endTransparentFill(geo, tempsb);
			}
		} else {
			// draw an ellipse by scaling a circle
			double x1 = geo.getTranslationVector().getX();
			double y1 = geo.getTranslationVector().getY();
			double r1 = geo.getHalfAxes()[0];
			double r2 = geo.getHalfAxes()[1];

			if (nofill) {
				if (!compact) {
					tempsb.append("\n");
				}
				if (compactcse5) {
					tempsb.append("D(");
				} else {
					tempsb.append("draw(");
				}
			} else {
				startTransparentFill(tempsb);
			}
			tempsb.append("shift(");
			addPoint(format(x1), format(y1), tempsb);
			packSpaceBetween(tempsb, ")", "*", "scale(" + format(r1) + ",",
					format(r2) + ")*unitcircle");
			if (nofill) {
				endDraw(geo, tempsb);
			} else {
				endTransparentFill(geo, tempsb);
			}
		}

		if (nofill) {
			code.append(tempsb);
		} else {
			codeFilledObject.append(tempsb);
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
		// if conic is a circle
		default:
			// do nothing
			break;
		case GeoConicNDConstants.CONIC_CIRCLE:
			drawCircle(geo);
			break;
		// if conic is an ellipse
		case GeoConicNDConstants.CONIC_ELLIPSE:

			// use scale operator to draw ellipse
			if (compactcse5) {
				if (fillInequality) {
					code.append("filldraw(shift(");
				} else {
					code.append("D(shift(");
				}
			} else if (fillInequality) {
				code.append("filldraw(shift(");
			} else {
				code.append("draw(shift(");
			}
			addPoint(format(x1), format(y1), code);
			code.append(")*rotate(");
			code.append(format(angle));
			code.append(")*xscale(");
			code.append(format(r1));
			code.append(")*yscale(");
			code.append(format(r2));
			code.append(")*unitcircle");
			if (fillInequality) {
				code.append(",pattern(\"hatch\"),border);\n");
			}
			endDraw(geo);
			break;

		// if conic is a parabola
		case GeoConicNDConstants.CONIC_PARABOLA:

			// calculate the x range to draw the parabola
			double x0 = Math.max(Math.abs(x1 - xmin), Math.abs(x1 - xmax));
			x0 = Math.max(x0, Math.abs(y1 - ymin));
			x0 = Math.max(x0, Math.abs(y1 - ymax));
			double p = geo.p;
			// avoid sqrt by choosing x = k*p with
			// i = 2*k is quadratic number
			// make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
			x0 = 4 * x0 / p;
			int i = 4, k2 = 16;
			while (k2 < x0) {
				i += 2;
				k2 = i * i;
			}
			// x0 = k2/2 * p; // x = k*p
			x0 = i * p; // y = sqrt(2k p^2) = i p
			angle -= 90;

			// write real parabola (real x) function
			parabolaCount++;
			if (!compact) {
				code.append("\n");
			}
			code.append("real p");
			if (!compact) {
				code.append("arabola");
			}
			code.append(parabolaCount);
			packSpace(code, "(real x)");
			code.append("{return x^2/2/");
			if (compact) {
				code.append(format(p));
			} else {
				code.append(p);
			}
			code.append(";} ");

			// use graph to plot parabola
			if (!compact) {
				code.append("\n");
			}
			if (compactcse5) {
				code.append("D(shift(");
			} else {
				code.append("draw(shift(");
			}
			addPoint(format(x1), format(y1), code);
			code.append(")*rotate(");
			code.append(format(angle));
			code.append(")*graph(p");
			if (!compact) {
				code.append("arabola");
			}
			code.append(parabolaCount);
			code.append(",");
			code.append(format(-x0));
			code.append(",");
			code.append(format(x0));
			code.append(")");
			endDraw(geo);

			if (!compact) {
				code.append("/* parabola construction */");
			}
			break;

		case GeoConicNDConstants.CONIC_HYPERBOLA:
			// parametric: (a(1+t^2)/(1-t^2), 2bt/(1-t^2))

			hyperbolaCount++;
			if (!compact) {
				code.append("\n");
			}
			if (!compact) {
				code.append("pair hyperbolaLeft");
			} else {
				code.append("pair hl");
			}
			code.append(hyperbolaCount);
			packSpace(code, "(real t)");
			code.append("{return (");
			code.append(format(r1));
			code.append("*(1+t^2)/(1-t^2),");
			code.append(format(r2));
			code.append("*2*t/(1-t^2));} ");
			if (!compact) {
				code.append("pair hyperbolaRight");
			} else {
				code.append("pair hr");
			}
			code.append(hyperbolaCount);
			packSpace(code, "(real t)");
			code.append("{return (");
			code.append(format(r1));
			code.append("*(-1-t^2)/(1-t^2),");
			code.append(format(r2));
			code.append("*(-2)*t/(1-t^2));} ");

			// use graph to plot both halves of hyperbola
			if (!compact) {
				code.append("\n");
			}
			if (compactcse5) {
				code.append("D(shift(");
			} else {
				code.append("draw(shift(");
			}
			addPoint(format(x1), format(y1), code);
			code.append(")*rotate(");
			code.append(format(angle));
			if (!compact) {
				code.append(")*graph(hyperbolaLeft");
			} else {
				code.append(")*graph(hl");
			}
			code.append(hyperbolaCount);
			code.append(",-0.99,0.99)"); // arbitrary to approach (-1,1)
			endDraw(geo);

			if (compactcse5) {
				code.append("D(shift(");
			} else {
				code.append("draw(shift(");
			}
			addPoint(format(x1), format(y1), code);
			code.append(")*rotate(");
			code.append(format(angle));
			if (!compact) {
				code.append(")*graph(hyperbolaRight");
			} else {
				code.append(")*graph(hr");
			}
			code.append(hyperbolaCount);
			code.append(",-0.99,0.99)");
			endDraw(geo);

			if (!compact) {
				code.append("/* hyperbola construction */");
			}

			break;
		}
	}

	// draws dot
	@Override
	protected void drawGeoPoint(GeoPointND gp) {
		if (frame.getExportPointSymbol()) {
			double[] A = new double[3];

			// assume 2D (3D check done earlier)
			gp.getInhomCoords(A);

			double x = A[0];
			double y = A[1];
			gp.getNameDescription();
			int dotstyle = gp.getPointStyle();
			if (dotstyle == -1) { // default
				dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
			} // draw special dot styles
			if (dotstyle != EuclidianStyleConstants.POINT_STYLE_DOT) {
				drawSpecialPoint(gp);
			} else { // plain dot style
				if (!compact) {
					codePoint.append("\n");
				}
				if (compactcse5) {
					codePoint.append("D(");
				} else {
					codePoint.append("dot(");
				}
				addPoint(format(x), format(y), codePoint);
				pointOptionCode(gp, codePoint);
				codePoint.append("); ");
			}
		}
	}

	/**
	 * Draws a point with a special point style (usually uses draw() or
	 * filldraw() command).
	 * 
	 * @param geo
	 *            GeoPoint with style not equal to the standard dot style.
	 */
	protected void drawSpecialPoint(GeoPointND geo) {
		// radius = dotsize (pt) * (2.54 cm)/(72 pt per inch) * XUnit / cm
		double dotsize = geo.getPointSize();
		double radius = dotsize * (2.54 / 72) * (frame.getXUnit());
		int dotstyle = geo.getPointStyle();
		if (dotstyle == -1) { // default
			dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
		}
		double[] A = new double[3];

		geo.getInhomCoords(A);

		if (A[2] != 0) {
			Log.error("can't export 3D Point" + geo.getLabelSimple());
			return;
		}

		double x = A[0];
		double y = A[1];
		GColor dotcolor = geo.getObjectColor();

		switch (dotstyle) {
		case EuclidianStyleConstants.POINT_STYLE_CROSS:
			startDraw();
			code.append("shift((" + format(x) + "," + format(y) + "))*");
			code.append("scale(");
			code.append(format(radius));
			code.append(")*(expi(pi/4)--expi(5*pi/4)");
			if (compactcse5) {
				// operator
				code.append("--(0,0)--");
			} else {
				code.append("^^");
			}
			code.append("expi(3*pi/4)--expi(7*pi/4))");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_CIRCLE:
			// use dot(..,UnFill(0)) command in lieu of filldraw
			if (!compactcse5) {
				codePoint.append("dot(");
				addPoint(format(x), format(y), codePoint);
				// 4.0 slightly arbitrary. 6.0 should be corrective factor, but
				// too small.
				pointOptionCode(geo, codePoint, geo.getPointSize() / 4.0);
				codePoint.append(",UnFill(0)); ");
			}
			// use filldraw(CR) for cse5
			else {
				startDraw();
				// if(compactcse5)
				code.append("CR((");
				// else
				// code.append("circle((");
				code.append(format(x) + "," + format(y) + "),");
				code.append(format(radius));
				code.append(")");
				endPoint(dotcolor);
			}
			break;
		case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
			startDraw();
			code.append("shift((" + format(x) + "," + format(y) + "))*");
			code.append("scale(");
			code.append(format(radius));
			code.append(")*((1,0)--(0,1)--(-1,0)--(0,-1)--cycle)");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
			if (!compact) {
				code.append("\n");
			}
			packSpaceBetween(
					"fill(shift((" + format(x) + "," + format(y) + "))", "*",
					"scale(" + format(radius) + ")", "*",
					"((1,0)--(0,1)--(-1,0)--(0,-1)--cycle)");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_PLUS:
			startDraw();
			packSpaceBetween("shift((" + format(x) + "," + format(y) + "))",
					"*", "scale(" + format(radius) + ")", "*",
					"((0,1)--(0,-1)");
			if (compactcse5) {
				// operator
				code.append("--(0,0)--");
			} else {
				code.append("^^");
			}
			code.append("(1,0)--(-1,0))");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
			if (!compact) {
				code.append("\n");
			}
			packSpaceBetween(
					"fill(shift((" + format(x) + "," + format(y) + "))", "*",
					"scale(" + format(radius) + ")", "*",
					"((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
			if (!compact) {
				code.append("\n");
			}
			packSpaceBetween(
					"fill(shift((" + format(x) + "," + format(y) + "))", "*",
					"rotate(90)", "*", "scale(" + format(radius) + ")", "*",
					"((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
			if (!compact) {
				code.append("\n");
			}
			packSpaceBetween(
					"fill(shift((" + format(x) + "," + format(y) + "))", "*",
					"rotate(270)", "*", "scale(" + format(radius) + ")", "*",
					"((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
			endPoint(dotcolor);
			break;
		case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
			if (!compact) {
				code.append("\n");
			}
			packSpaceBetween(
					"fill(shift((" + format(x) + "," + format(y) + "))", "*",
					"rotate(180)", "*", "scale(" + format(radius) + ")", "*",
					"((1,0)--expi(2*pi/3)--expi(4*pi/3)--cycle)");
			endPoint(dotcolor);
			break;
		default:
			break;
		}
		if (!compact) {
			code.append("/* special point */");
		}
	}

	// draws line
	@Override
	protected void drawGeoLine(GeoLine geo) {
		double x = geo.getX(), y = geo.getY(), z = geo.getZ();

		if (y != 0) {
			startDraw();
			// new evaluation: [-x/y]*[xmin or xmax]-(z/y)
			packSpaceAfter(code, "(xmin,");
			code.append(format(-x / y));
			code.append("*xmin");
			if (z / y < 0 || format(-z / y).equals("0")) {
				packSpace(code, "+");
			}
			code.append(format(-z / y));
			code.append(")");
			// String tmpy=format(y);
			// if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			// else code.append(y);
			packSpaceAfter(code, "--(xmax,");
			code.append(format(-x / y));
			code.append("*xmax");
			if (z / y < 0 || format(-z / y).equals("0")) {
				packSpace(code, "+");
			}
			code.append(format(-z / y));
			// if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
			// else code.append(y);
			code.append(")");
			endDraw(geo);
		} else { // vertical line
			if (!compact) {
				code.append("\n");
			}
			if (compactcse5) {
				code.append("D((");
			} else {
				code.append("draw((");
			}
			String s = format(-z / x);
			code.append(s);
			code.append(",ymin)--(");
			code.append(s);
			code.append(",ymax)");
			endDraw(geo);
		}
		if (!compact) {
			code.append("/* line */");
		}
	}

	// draws segment
	@Override
	protected void drawGeoSegment(GeoSegmentND geo) {
		double[] A = new double[3], B = new double[3];
		GeoPointND pointStart = geo.getStartPoint();
		GeoPointND pointEnd = geo.getEndPoint();
		pointStart.getInhomCoords(A);
		pointEnd.getInhomCoords(B);

		// assume 2D (3D check done earlier)
		String x1 = format(A[0]), y1 = format(A[1]), x2 = format(B[0]),
				y2 = format(B[1]);
		int deco = geo.getDecorationType();

		if (!compact) {
			code.append("\n");
		}
		if (!compactcse5) {
			code.append("draw(");
		} else {
			code.append("D(");
		}
		addPoint(x1, y1, code);
		code.append("--");
		addPoint(x2, y2, code);
		endDraw(geo);

		if (deco != GeoElementND.DECORATION_NONE) {
			mark(A, B, deco, geo);
		}
	}

	@Override
	protected void drawLine(double x1, double y1, double x2, double y2,
			GeoElementND geo) {
		String sx1 = format(x1);
		String sy1 = format(y1);
		String sx2 = format(x2);
		String sy2 = format(y2);

		startDraw();
		addPoint(sx1, sy1, code);
		code.append("--");
		addPoint(sx2, sy2, code);
		endDraw(geo);
	}

	@Override
	protected void drawGeoRay(GeoRayND geo) {
		GeoPointND pointStart = geo.getStartPoint();
		double x1 = pointStart.getInhomX();
		String y1 = format(pointStart.getInhomY());

		Coords equation = geo
				.getCartesianEquationVector(euclidianView.getMatrix());

		double x = equation.getX();
		double y = equation.getY();
		double z = equation.getZ();

		double yEndpoint; // records explicitly y-coordinate of endpoint
		// String tmpy = format(y);
		double inf = xmin, sup = xmax; // determine left and right bounds on x
										// to draw ray
		if (y > 0) {
			inf = x1;
			yEndpoint = (-z - x * inf) / y;
		} else {
			sup = x1;
			yEndpoint = (-z - x * sup) / y;
		}

		// format: draw((inf,f(inf))--(xmax,f(xmax)));
		// OR: draw((xmin,f(xmin))--(sup,f(sup)));
		// old evaluation: (-(z)-(x)*[inf or sup])/y
		// new evaluation: [-x/y]*[inf or sup]-(z/y)
		startDraw();
		if (y != 0) { // non-vertical line
			if (y > 0) {
				addPoint(format(inf), format(yEndpoint), code);
				code.append("--");
				packSpaceAfter(code, "(xmax,");
				code.append(format(-x / y));
				code.append("*xmax");
				if (z / y < 0 || format(-z / y).equals("0")) {
					packSpace(code, "+");
				}
				code.append(format(-z / y));
				// code.append(")/");
				// if (Double.parseDouble(tmpy)!=0) code.append(tmpy);
				// else code.append(y);
				code.append(")");
			} else {
				addPoint(format(sup), format(yEndpoint), code);
				code.append("--");
				packSpaceAfter(code, "(xmin,");
				code.append(format(-x / y));
				code.append("*xmin");
				if (z / y < 0 || format(-z / y).equals("0")) {
					packSpace(code, "+");
				}
				code.append(format(-z / y));
				// code.append("/");
				// if (Double.parseDouble(tmpy) != 0) code.append(tmpy);
				// else code.append(y);
				code.append(")");
			}
			endDraw(geo);
		} else {
			addPoint(format(x1), y1, code);
			code.append("--(");
			code.append(format(x1));
			packSpaceAfter(code, ",");
			if (-x > 0) {
				code.append("ymax");
			} else {
				code.append("ymin");
			}
			code.append(")");
			endDraw(geo);
		}
		if (!compact) {
			code.append("/* ray */");
		}
	}

	@Override
	protected void drawImplicitPoly(GeoImplicit geo) {
		// credit: help from Art of Problem Solving user fedja
		importpackage.add("contour"); // importContour = true; flag for preamble
										// to import contour package
		// two-variable implicit function expression
		String expression = getImplicitExpr(geo);
		if (expression == null) {
			Log.warn("implicit plot not supported for non-polynomials: "
					+ geo.toValueString(StringTemplate.defaultTemplate));
			return;
		}
		String polynomial = parseFunction(expression)
				.replace("\\\\pi", "pi");
		implicitFuncCount++;
		int implicitFuncName = implicitFuncCount;

		// if compact, retrieve previous instance of implicit polynomial
		// expression, if exists
		if (!compact || !implicitPolyTable.containsKey(polynomial)) {
			if (compact) {
				if (implicitPolyTable.isEmpty()) {
					implicitPolyTable.put(polynomial, 1);
				} else {
					implicitFuncName = implicitPolyTable.size() + 1;
					implicitPolyTable.put(polynomial, implicitFuncName);
				}
			}

			// write implicitf# (real x, real y) is implicit polynomial function
			// of two variables
			if (!compact) {
				code.append("\n");
			}
			code.append("real implicitf");
			code.append(implicitFuncName);
			packSpace("(real x, real y)", "{");
			code.append("return " + polynomial);
			packSpaceAfter(";");
			code.append("} ");
		} else {
			implicitFuncName = implicitPolyTable.get(polynomial);
		}

		startDraw(); // code: draw(contour(f, (xmin,ymin), (xmax,ymax), new
						// real[]{0}, 500));
		code.append("contour(implicitf");
		code.append(implicitFuncName);
		packSpaceBetween(code, ",", "(xmin,ymin),", "(xmax,ymax),",
				"new real[]{0},", "500)");
		endDraw(geo.toGeoElement());
	}

	@Override
	protected void drawPolyLine(GeoPolyLine geo) {
		GeoPointND[] points = geo.getPoints();
		StringBuilder str = new StringBuilder();
		startDraw(str); // connect (by join --) all points within one draw
						// statement
		for (int i = 0; i < points.length; i++) {
			Coords coords = points[i].getInhomCoords();
			double x = coords.getX(), y = coords.getY();
			addPoint(format(x), format(y), str);
			if (i != points.length - 1) {
				str.append("--");
			}
		}
		endDraw(geo, str);
		String s = str.toString();
		StringBuilder sb = new StringBuilder();
		if (lineOptionCode(geo, true) != null) {
			packSpaceAfter(sb, ",");
			sb.append(lineOptionCode(geo, true));
		}
		sb.append("); ");
		StringBuilder sa = new StringBuilder();
		if (!compact) {
			sa.append("\n");
		}
		if (compactcse5) {
			sa.append("D(");
		} else {
			sa.append("draw(");
		}

		s = s.replace("--\\(\\?,\\?\\)--", sb.toString() + sa.toString());
		code.append(s);
	}

	private void initUnitAndVariable() {
		// Initaialze units, dot style, dot size ....
		/*
		 * codeBeginPic.append("\\psset{xunit=");
		 * codeBeginPic.append(sci2dec(xunit));
		 * codeBeginPic.append("cm,yunit=");
		 * codeBeginPic.append(sci2dec(yunit));
		 * codeBeginPic.append("cm,algebraic=true,dotstyle=o,dotsize=");
		 * codeBeginPic.append(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		 * codeBeginPic.append("pt 0"); codeBeginPic.append(",linewidth=");
		 * codeBeginPic
		 * .append(format(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS
		 * /2*0.8)); codeBeginPic.append("pt,arrowsize=3pt 2,arrowinset=0.25}\n"
		 * );
		 */

		if (!compact) {
			codePreamble.append(
					"\nreal labelscalefactor = 0.5; /* changes label-to-point distance */");
			codePreamble.append("\npen dps = linewidth(0.7) + fontsize(");
			codePreamble.append(fontsize);
			codePreamble.append("); defaultpen(dps); /* default pen style */ ");
			if (!frame.getKeepDotColors()) {
				codePreamble
						.append("\npen dotstyle = black; /* point style */ \n");
			}
		} else if (!compactcse5) {
			codePreamble
					.append("real lsf=0.5; pen dps=linewidth(0.7)+fontsize(");
			codePreamble.append(fontsize);
			codePreamble.append("); defaultpen(dps); ");
			if (!frame.getKeepDotColors()) {
				codePreamble.append("pen ds=black; ");
			}
		} else {
			codePreamble.append(
					"real lsf=0.5; pathpen=linewidth(0.7); pointpen=black; pen fp=fontsize(");
			codePreamble.append(fontsize);
			codePreamble.append("); pointfontpen=fp; ");
		}
		boolean[] positiveOnly = euclidianView.getPositiveAxes();
		double assignMinX = xmin;
		double assignMinY = ymin;
		if (positiveOnly[0]) {
			assignMinX = -0.1;
		}
		if (positiveOnly[1]) {
			assignMinY = -0.1;
		}
		packSpaceBetween(codePreamble, "real xmin", "=",
				format(assignMinX) + ",", "xmax", "=", format(xmax) + ",",
				"ymin", "=", format(assignMinY) + ",", "ymax", "=",
				format(ymax) + "; ");
		if (!compact) {
			codePreamble.append(" /* image dimensions */\n");
		} else { /* codePreamble.append("\n"); */
		}
	}

	// Generate list of pairs for cse5 code to use
	private void initPointDeclarations() {
		if (!pairName) {
			return;
		}
		Iterator<GeoPoint> it = pointList.iterator();
		boolean comma = false; // flag for determining whether to add comma
		// pre-defined pair names in base module plain. Do not re-write to save
		// hassle
		String[] predefinedNames = { "N", "S", "E", "W", "NE", "SE", "NW", "SW",
				"NNE", "NNW", "SSE", "SSW", "ENE", "WNW", "ESE", "WSW", "left",
				"right", "up", "down" };

		while (it.hasNext()) {
			GeoPoint gp = it.next();
			if (gp.getPointStyle() == EuclidianStyleConstants.POINT_STYLE_DOT
					|| gp.getPointStyle() == EuclidianStyleConstants.POINT_STYLE_CIRCLE) {
				double x = gp.getX(), y = gp.getY(), z = gp.getZ();
				x /= z;
				y /= z;
				String pairString = "(" + format(x) + "," + format(y) + ")";
				String pointName = gp.getLabel(getStringTemplate());
				boolean isVariable = true;

				// Note: if problem with point name, simply discard and move on.
				// check if characters of point names are valid, namely
				// alphanumeric or underscore
				for (int i = 0; i < pointName.length(); i++) {
					if (!Character.isLetterOrDigit(pointName.charAt(i))
							&& pointName.charAt(i) != '_') {
						isVariable = false;
					}
				}

				// check that point names don't re-write basic asymptote pairs
				for (int i = 0; i < predefinedNames.length; i++) {
					if (pointName.equals(predefinedNames[i])) {
						isVariable = false;
					}
				}

				// store pairString -> pairName, write asy declaration pair
				// pairName = pairString;
				if (!pairNameTable.containsKey(pairString) && isVariable) {
					if (comma) {
						codePointDecl.append(", ");
					} else {
						comma = true;
					}
					pairNameTable.put(pairString, pointName);
					codePointDecl.append(pointName);
					packSpace(codePointDecl, "=");
					codePointDecl.append(pairString);
				}
			}
		}
		if (comma) {
			codePointDecl.insert(0, "\npair ");
			codePointDecl.append("; ");
		}
	}

	// if label is visible, draw it
	@Override
	protected void drawLabel(GeoElementND geo, DrawableND drawGeo0) {
		if (geo != null && geo.isLabelVisible()
				&& geo.getLabelDescription() != null) {
			String name;
			if (geo.getLabelMode() == GeoElementND.LABEL_CAPTION) {
				name = convertUnicodeToText(geo.getLabelDescription())
						.replace("\\$", "dollar");
				if (name.contains("_")) {
					name = "$" + name + "$";
				}
			} else if (compactcse5) {
				name = StringUtil.toLaTeXString(geo.getLabelDescription(),
						true);
				name = convertUnicodeToLatex(name);
			} else {
				name = "$" + StringUtil.toLaTeXString(geo.getLabelDescription(),
						true) + "$";
				name = convertUnicodeToLatex(name);
			}
			if (name.indexOf(Unicode.DEGREE_STRING) != -1) {
				name = name.replace(Unicode.DEGREE_STRING, "^\\\\circ");
			}
			DrawableND drawGeo = drawGeo0;
			if (drawGeo == null) {
				drawGeo = euclidianView.getDrawableFor(geo);
			}
			if (drawGeo == null) {
				return;
			}
			double xLabel = drawGeo.getxLabel();
			double yLabel = drawGeo.getyLabel();
			xLabel = euclidianView.toRealWorldCoordX(Math.round(xLabel));
			yLabel = euclidianView.toRealWorldCoordY(Math.round(yLabel));

			if (!compact) {
				codePoint.append("\n");
			}
			if (compactcse5
					&& geo.getLabelMode() != GeoElementND.LABEL_CAPTION) {
				codePoint.append("MP(\"");
			} else {
				codePoint.append("label(\"");
			}
			codePoint.append(name);
			packSpaceBetween(codePoint, "\",", "(");
			codePoint.append(format(xLabel));
			codePoint.append(",");
			codePoint.append(format(yLabel));
			codePoint.append("),");
			if (!compact) {
				codePoint.append(" ");
			}
			codePoint.append("NE");
			packSpace(codePoint, "*");
			if (compact) {
				codePoint.append("lsf");
			}
			if (!compact) {
				codePoint.append("labelscalefactor");
			}

			GColor geocolor = geo.getObjectColor();

			// check if label is of point
			boolean isPointLabel = (geocolor.equals(GColor.BLUE)
					|| colorEquals(geocolor, GColor.newColor(124, 124, 255))) // xdxdff
					// is of the form "A" or "$A$"
					&& (((name.length() == 1)
							&& Character.isUpperCase(name.charAt(0)))
							|| (((name.length() == 3) && name.charAt(0) == '$'
									&& name.charAt(2) == '$' && Character
											.isUpperCase(name.charAt(1)))));
			isPointLabel = isPointLabel || geo.isGeoPoint();
			// replaced with pointfontpen:
			// if(compactcse5) {
			// codePoint.append(",fp");
			// }
			if (isPointLabel && !frame.getKeepDotColors()) {
				// configurable or default black?
				// temp empty
			} else if (!geocolor.equals(GColor.BLACK)) {
				if (compactcse5) {
					codePoint.append(",fp+");
				} else {
					codePoint.append(",");
				}
				colorCode(geocolor, codePoint);
			}
			codePoint.append("); ");
		}
	}

	/**
	 * Returns whether or not c1 and c2 are equivalent colors, when rounded to
	 * the nearest hexadecimal integer.
	 * 
	 * @param c1
	 *            The first Color object.
	 * @param c2
	 *            The second Color object to compare with.
	 * @return Whether c1 and c2 are equivalent colors, to rounding.
	 */
	boolean colorEquals(GColor c1, GColor c2) {
		return format(c1.getRed() / 255d).equals(format(c2.getRed() / 255d))
				&& format(c1.getGreen() / 255d)
						.equals(format(c2.getGreen() / 255d))
				&& format(c1.getBlue() / 255d)
						.equals(format(c2.getBlue() / 255d));
	}

	// Draw the grid
	private void drawGrid() {
		GColor GridCol = euclidianView.getGridColor();
		double[] GridDist = euclidianView.getGridDistances();
		boolean GridBold = euclidianView.getGridIsBold();
		int GridLine = euclidianView.getGridLineStyle();

		if (!compact) {
			// draws grid using Asymptote loops
			codeBeginPic
					.append("\n /* draw grid of horizontal/vertical lines */");
			codeBeginPic.append("\npen gridstyle = ");
			if (GridBold) {
				codeBeginPic.append("linewidth(1.0)");
			} else {
				codeBeginPic.append("linewidth(0.7)");
			}
			codeBeginPic.append(" + ");
			colorCode(GridCol, codeBeginPic);
			if (GridLine != EuclidianStyleConstants.LINE_TYPE_FULL) {
				codeBeginPic.append(" + ");
				linestyleCode(GridLine, codeBeginPic);
			}
			codeBeginPic.append("; real gridx = ");
			codeBeginPic.append(format(GridDist[0]));
			codeBeginPic.append(", gridy = ");
			codeBeginPic.append(format(GridDist[1]));
			codeBeginPic.append("; /* grid intervals */"
					+ "\nfor(real i = ceil(xmin/gridx)*gridx; "
					+ "i <= floor(xmax/gridx)*gridx; i += gridx)");
			codeBeginPic.append("\n draw((i,ymin)--(i,ymax), gridstyle);");
			codeBeginPic.append("\nfor(real i = ceil(ymin/gridy)*gridy; "
					+ "i <= floor(ymax/gridy)*gridy; i += gridy)");
			codeBeginPic.append("\n draw((xmin,i)--(xmax,i), gridstyle);");
			codeBeginPic.append("\n /* end grid */ \n");
			return;
		} else if (!compactcse5) {
			codeBeginPic.append(
					"\n/*grid*/ "); /*
									 * //// COMMENTED CODE - explicitly draw
									 * grid using for loops. ////
									 * codeBeginPic.append ("pen gs=");
									 * if(GridBold) codeBeginPic
									 * .append("linewidth(1.0)"); else
									 * codeBeginPic.append( "linewidth(0.7)");
									 * codeBeginPic.append("+"); ColorCode
									 * (GridCol,codeBeginPic); if(GridLine !=
									 * EuclidianStyleConstants .LINE_TYPE_FULL)
									 * { codeBeginPic.append("+");
									 * LinestyleCode(GridLine, codeBeginPic); }
									 * codeBeginPic.append("; "); codeBeginPic
									 * .append("real gx=" + format(GridDist[0])
									 * + ",gy=" + format(GridDist[1]) + "; ");
									 * codeBeginPic.append(
									 * "\nfor(real i=ceil(xmin/gx)*gx;" +
									 * "i<=floor(xmax/gx)*gx;i+=gx)" );
									 * codeBeginPic.append(
									 * " draw((i,ymin)--(i,ymax),gs);" );
									 * codeBeginPic.append(
									 * " for(real i=ceil(ymin/gy)*gy;" +
									 * "i<=floor(ymax/gy)*gy;i+=gy)" );
									 * codeBeginPic.append(
									 * " draw((xmin,i)--(xmax,i),gs); " );
									 * 
									 * // USE math module defined method
									 * grid(Nx, Ny): real gx=1,gy=1;
									 * add(scale(gx,gy)*shift (floor(xmin
									 * /gx),floor(ymin/gy) )*grid(ceil
									 * (xmax-xmin)+1,ceil(
									 * ymax-ymin)+1,gridpen)); } else { // with
									 * cse5 shorthands if(GridBold) codeBeginPic
									 * .append("linewidth(1.0)"); else
									 * codeBeginPic.append( "linewidth(0.7)");
									 * codeBeginPic.append("+"); ColorCode
									 * (GridCol,codeBeginPic); if(GridLine !=
									 * EuclidianStyleConstants .LINE_TYPE_FULL)
									 * { codeBeginPic.append("+");
									 * LinestyleCode(GridLine, codeBeginPic); }
									 * codeBeginPic. append("; real gx=");
									 * codeBeginPic
									 * .append(format(GridDist[0]));
									 * codeBeginPic.append(",gy="); codeBeginPic
									 * .append(format(GridDist[1]));
									 * codeBeginPic.append(
									 * ";\nfor(real i=ceil(xmin/gx)*gx;" +
									 * "i<=floor(xmax/gx)*gx;i+=gx)" );
									 * codeBeginPic.append(
									 * " D((i,ymin)--(i,ymax),gs);" );
									 * codeBeginPic.append(
									 * " for(real i=ceil(ymin/gy)*gy;" +
									 * "i<=floor(ymax/gy)*gy;i+=gy)" );
									 * codeBeginPic.append(
									 * " D((xmin,i)--(xmax,i),gs); " ); }
									 */
		}
		importpackage.add("math");
		codeBeginPic.append("real gx=" + format(GridDist[0]) + ",gy="
				+ format(GridDist[1]) + "; ");
		codeBeginPic.append("add(scale(gx,gy)*shift(floor(xmin/gx),floor(ymin/gy))");
		codeBeginPic.append("*grid(ceil(xmax-xmin)+1,ceil(ymax-ymin)+1,");
		if (GridBold) {
			codeBeginPic.append("linewidth(1.0)");
		} else {
			codeBeginPic.append("linewidth(0.7)");
		}
		codeBeginPic.append("+");
		colorCode(GridCol, codeBeginPic);
		if (GridLine != EuclidianStyleConstants.LINE_TYPE_FULL) {
			codeBeginPic.append("+");
			linestyleCode(GridLine, codeBeginPic);
		}
		codeBeginPic.append(")); ");
	}

	// Draws Axis presuming shown
	// TODO low priority: improve modularity of this function, repeated code for
	// xaxis/yaxis.
	// note: may shift around relative positions of certain labels.
	private void drawAxis() {
		boolean xAxis = euclidianView.getShowXaxis();
		boolean yAxis = euclidianView.getShowYaxis();
		boolean bx = euclidianView.getShowAxesNumbers()[0];
		boolean by = euclidianView.getShowAxesNumbers()[1];
		String Dx = format(euclidianView.getAxesNumberingDistances()[0]);
		String Dy = format(euclidianView.getAxesNumberingDistances()[1]);
		String[] label = euclidianView.getAxesLabels(false);
		String[] units = euclidianView.getAxesUnitLabels();
		int axisStyle = euclidianView.getAxesLineStyle();
		int[] tickStyle = euclidianView.getAxesTickStyles();
		GColor axisColor = euclidianView.getAxesColor();
		boolean axisBold = (axisStyle & 2) == EuclidianStyleConstants.AXES_BOLD;

		String lx = "", ly = ""; // axis labels
		if (label[0] != null) {
			lx = "$" + StringUtil.toLaTeXString(label[0], true) + "$";
		}
		if (label[1] != null) {
			ly = "$" + StringUtil.toLaTeXString(label[1], true) + "$";
		/*
		 * follow format: void xaxis(picture pic=currentpicture, Label L="",
		 * axis axis=YZero, real xmin=-infinity, real xmax=infinity, pen
		 * p=currentpen, ticks ticks=NoTicks, arrowbar arrow=None, bool
		 * above=false);
		 */
		}

		// Note: code for xaxis and yaxis duplicated twice.
		// When making changes, be sure to update both.
		if (xAxis || yAxis) {
			codeBeginPic.append("\n"); // create initial label
			codeBeginPic.append("Label laxis; laxis.p");
			packSpace(codeBeginPic, "=");
			codeBeginPic.append("fontsize(" + fontsize + "); ");
			if (!bx || !by) { // implement no number shown
				if (!compact) {
					codeBeginPic.append("\n");
				}
				codeBeginPic.append("string blank(real x) {return \"\";} ");
			}
			if (bx || by) { // implement unit labels
				if (units[0] != null && !units[0].equals("")) {
					codeBeginPic.append("string ");
					if (compact) {
						codeBeginPic.append("xlbl");
					} else {
						codeBeginPic.append("xaxislabel");
					}
					packSpace(codeBeginPic, "(real x)");
					packSpaceAfter(codeBeginPic, "{");

					// asymptote code for pi labels:
					// string xlbl(real x){string s; int n=round(2*x/pi);
					// if(abs(n-2*x/pi) > 1e-3) return string(x);
					// if(abs(n)>2) s=string(round((n%2+1)*x/pi)); if(n%2==0)
					// return "$"+s+"\pi$"; return "$"+s+"\pi/2$";}

					// unit label is pi: format -1pi, -1pi/2, 0pi, 1pi/2, 1pi
					if (units[0].equals(Unicode.PI_STRING)) {
						// create labeling function for special labels if n =
						// -1,0,1
						packSpaceBetween(codeBeginPic, "string s; ", "int n",
								"=", "round(2*x/pi); ");
						if (!compact) {
							codeBeginPic.append("\n");
						}
						packSpaceBetween(codeBeginPic, "if(abs(n-2*x/pi)", ">",
								"1e-3) return string(x); ");
						if (!compact) {
							codeBeginPic.append("\n");
						}
						packSpaceBetween(codeBeginPic, "if(abs(n)", ">",
								"2) s = string(round((n%2", "+", "1)*x/pi)); ");
						if (!compact) {
							codeBeginPic.append("\n");
						}
						packSpaceBetween(codeBeginPic, "if(n%2", "==",
								"0) return \"$\"+s+\"\\pi$\"; ");
						// codeBeginPic.append("int n=round(x/pi); ");
						// codeBeginPic.append("if(n==-1) return \"$-\\pi$\";
						// ");
						// codeBeginPic.append("if(n==1) return \"$\\pi$\"; ");
						// codeBeginPic.append("if(n==0) return \"$0$\"; ");
					}
					codeBeginPic.append("return \"$\"");
					packSpace(codeBeginPic, "+");
					// unit label is pi
					if (units[0].equals(Unicode.PI_STRING)) {
						packSpaceBetween(codeBeginPic, "s", "+", "\"\\pi/2");
					} else if (units[0].equals(Unicode.DEGREE_STRING)) {
						packSpaceBetween(codeBeginPic, "string(x)", "+",
								"\"^\\circ");
					} else {
						codeBeginPic.append("string(x)");
						packSpace(codeBeginPic, "+");
						codeBeginPic.append("\"\\,\\mathrm{" + units[0] + "}");
					}
					codeBeginPic.append("$\";} ");
				}
				if (units[1] != null && !units[1].equals("")) {
					codeBeginPic.append("string ");
					if (compact) {
						codeBeginPic.append("ylbl");
					} else {
						codeBeginPic.append("yaxislabel");
					}
					packSpace(codeBeginPic, "(real x)");
					packSpaceAfter(codeBeginPic, "{");

					// asymptote code for pi labels:
					// string ylbl(real x){string s; int n=round(2*x/pi);
					// if(abs(n-2*x/pi) > 1e-3) return string(x);
					// if(abs(n)>2) s=string(round((n%2+1)*x/pi)); if(n%2==0)
					// return "$"+s+"\pi$"; return "$"+s+"\pi/2$";}

					// unit label is pi: format -1pi, -1pi/2, 0pi, 1pi/2, 1pi
					if (units[1].equals(Unicode.PI_STRING)) {
						// create labeling function for special labels if n =
						// -1,0,1
						packSpaceBetween(codeBeginPic, "string s; ", "int n",
								"=", "round(2*x/pi); ");
						if (!compact) {
							codeBeginPic.append("\n");
						}
						packSpaceBetween(codeBeginPic, "if(abs(n-2*x/pi)", ">",
								"1e-3) return string(x); ");
						if (!compact) {
							codeBeginPic.append("\n");
						}
						packSpaceBetween(codeBeginPic, "if(abs(n)", ">",
								"2) s = string(round((n%2", "+", "1)*x/pi)); ");
						if (!compact) {
							codeBeginPic.append("\n");
						}
						packSpaceBetween(codeBeginPic, "if(n%2", "==",
								"0) return \"$\"+s+\"\\pi$\"; ");
						// codeBeginPic.append("int n=round(x/pi); ");
						// codeBeginPic.append("if(n==-1) return \"$-\\pi$\";
						// ");
						// codeBeginPic.append("if(n==1) return \"$\\pi$\"; ");
						// codeBeginPic.append("if(n==0) return \"$0$\"; ");
					}
					codeBeginPic.append("return \"$\"");
					packSpace(codeBeginPic, "+");
					// unit label is pi
					if (units[1].equals(Unicode.PI_STRING)) {
						packSpaceBetween(codeBeginPic, "s", "+", "\"\\pi/2");
					} else if (units[1].equals(Unicode.DEGREE_STRING)) {
						packSpaceBetween(codeBeginPic, "string(x)", "+",
								"\"^\\circ");
					} else {
						codeBeginPic.append("string(x)");
						packSpace(codeBeginPic, "+");
						// put units in text form
						codeBeginPic.append("\"\\,\\mathrm{" + units[1] + "}");
					}
					codeBeginPic.append("$\";} ");
				}
			}
			codeBeginPic.append("\n");
		}
		if (xAxis) {
			codeBeginPic.append("xaxis(");
			if (label[0] != null) {
				packSpaceBetween(codeBeginPic, "\"" + lx + "\",");
			}
			packSpaceBetween(codeBeginPic, "xmin,", "xmax"); // non-fixed axes?
																// TODO: remove
																// if !compact?
																// priority:
																// minor
			// axis pen style
			if (axisColor != GColor.BLACK) {
				codeBeginPic.append(",");
				// catch for other options not changing.
				if (compactcse5) {
					codeBeginPic.append("pathpen+");
				} else {
					codeBeginPic.append("defaultpen+");
				}
				colorCode(axisColor, codeBeginPic);
				if (axisBold) {
					codeBeginPic.append("+linewidth(1.2)");
				}
			} else if (axisBold) {
				codeBeginPic.append(",linewidth(1.2)");
			}
			packSpaceAfter(codeBeginPic, ",");
			if (tickStyle[0] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR) {
				packSpaceAfter(codeBeginPic, "Ticks(laxis,");
				if (!bx) {
					packSpaceAfter(codeBeginPic, "blank,");
				} else if (units[0] != null && !units[0].equals("")) {
					if (compact) {
						packSpaceAfter(codeBeginPic, "xlbl,");
					} else {
						packSpaceAfter(codeBeginPic, "xaxislabel,");
					}
				}
				// Step=Dx, Size=2, NoZero
				packSpaceBetween(codeBeginPic, "Step", "=", Dx + ",", "Size",
						"=", "2");
				if (yAxis) {
					packSpaceBetween(codeBeginPic, ",", "NoZero");
				}
				codeBeginPic.append(")");
			} else if (tickStyle[0] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR) {
				packSpaceAfter(codeBeginPic, "Ticks(laxis,");
				if (!bx) {
					packSpaceAfter(codeBeginPic, "blank,");
				} else if (units[0] != null && !units[0].equals("")) {
					if (compact) {
						packSpaceAfter(codeBeginPic, "xlbl,");
					} else {
						packSpaceAfter(codeBeginPic, "xaxislabel,");
					}
				}
				// n=2, Step=Dx, Size=2, size=1, NoZero
				packSpaceBetween(codeBeginPic, "n", "=", "2,", "Step", "=",
						Dx + ",", "Size", "=", "2,", "size", "=", "1");
				codeBeginPic.append(")");
			}
			packSpaceBetween(codeBeginPic, ",", "above", "=", "true); ");
		}
		if (xAxis && yAxis && !compact) {
			codeBeginPic.append("\n");
		}
		if (yAxis) {
			codeBeginPic.append("yaxis(");
			if (label[1] != null) {
				packSpaceAfter(codeBeginPic, "\"" + ly + "\",");
			}
			packSpaceBetween(codeBeginPic, "ymin,", "ymax"); // non-fixed axes?

			// axis pen style
			if (axisColor != GColor.BLACK) {
				if (compactcse5) {
					codeBeginPic.append(",pathpen+");
				} else {
					codeBeginPic.append(",defaultpen+");
				}
				colorCode(axisColor, codeBeginPic);
				if (axisBold) {
					codeBeginPic.append("+linewidth(1.2)");
				}
			} else if (axisBold) {
				codeBeginPic.append(",linewidth(1.2)");
			}
			packSpaceAfter(codeBeginPic, ",");
			if (tickStyle[1] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR) {
				packSpaceAfter(codeBeginPic, "Ticks(laxis,");
				if (!by) {
					packSpaceAfter(codeBeginPic, "blank,");
				} else if (units[1] != null && !units[1].equals("")) {
					if (compact) {
						packSpaceAfter(codeBeginPic, "ylbl,");
					} else {
						packSpaceAfter(codeBeginPic, "yaxislabel,");
					}
				}
				// Step=Dy, Size=2, NoZero
				packSpaceBetween(codeBeginPic, "Step", "=", Dy + ",", "Size",
						"=", "2");
				if (xAxis) {
					packSpaceBetween(codeBeginPic, ",", "NoZero");
				}
				codeBeginPic.append(")");
			} else if (tickStyle[1] == EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR) {
				packSpaceAfter(codeBeginPic, "Ticks(laxis,");
				if (!by) {
					packSpaceAfter(codeBeginPic, "blank,");
				} else if (units[1] != null && !units[1].equals("")) {
					if (compact) {
						packSpaceAfter(codeBeginPic, "ylbl,");
					} else {
						packSpaceAfter(codeBeginPic, "yaxislabel,");
					}
				}
				// n=2, Step=Dy, Size=2, size=1, NoZero
				packSpaceBetween(codeBeginPic, "n", "=", "2,", "Step", "=",
						Dy + ",", "Size", "=", "2,", "size", "=", "1");
				codeBeginPic.append(")");
			}
			packSpaceBetween(codeBeginPic, ",", "above", "=", "true); ");
		}
		if ((xAxis || yAxis) && !compact) {
			codeBeginPic.append("/* draws axes; NoZero hides '0' label */ ");
		}
		drawArrows(axisStyle, axisBold);
	}

	private void drawArrows(int axisStyle, boolean axisBold) {
		boolean axisLeftArrow = (axisStyle
				& 4) == EuclidianStyleConstants.AXES_LEFT_ARROW;
		boolean axisRightArrow = (axisStyle
				& 1) == EuclidianStyleConstants.AXES_RIGHT_ARROW;
		String arrow = null;
		String pt = "6";
		if (axisBold) {
			pt = "9";
		}
		if (axisRightArrow && axisLeftArrow) {
			arrow = "Arrows(" + pt + "),";
			codeBeginPic.insert(codeBeginPic.indexOf("above") - 1, arrow);
			codeBeginPic.insert(codeBeginPic.lastIndexOf("above") - 1, arrow);
		} else {
			if (axisRightArrow) {
				arrow = "EndArrow(" + pt + "),";
				codeBeginPic.insert(codeBeginPic.indexOf("above") - 1, arrow);
				codeBeginPic.insert(codeBeginPic.lastIndexOf("above") - 1,
						arrow);
			}
			if (axisLeftArrow) {
				arrow = "BeginArrow(" + pt + "),";
				codeBeginPic.insert(codeBeginPic.indexOf("above") - 1, arrow);
				codeBeginPic.insert(codeBeginPic.lastIndexOf("above") - 1,
						arrow);
			}
		}
	}

	// Returns point style code with size dotsize. Includes comma.
	private void pointOptionCode(GeoPointND geo, StringBuilder sb,
			double dotsize) {
		GColor dotcolor = geo.getObjectColor();
		int dotstyle = geo.getPointStyle();
		if (dotstyle == -1) { // default
			dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
		}
		boolean comma = false; // add comma

		if (dotsize != EuclidianStyleConstants.DEFAULT_POINT_SIZE) {
			// comma needed
			comma = true;
			sb.append(",linewidth(");
			// Note: Asymptote magnifies default dotsizes by a scale of 6 x
			// linewidth,
			// but it does not magnify passed-in arguments. So the dotsize here
			// is approximately of the correct size.
			sb.append(format(dotsize));
			sb.append("pt)");
		}
		if (!dotcolor.equals(GColor.BLACK) && frame.getKeepDotColors()) {
			if (comma) {
				packSpace(sb, "+");
			} else {
				sb.append(",");
			}
			comma = true;

			colorCode(dotcolor, sb);
		} else if (!frame.getKeepDotColors() && !compactcse5) {
			if (comma) {
				packSpace(sb, "+");
			} else {
				sb.append(",");
			}
			comma = true;

			/* cse5 has pointpen attribute */
			if (!compact) {
				sb.append("dotstyle");
			} else if (!compactcse5) {
				sb.append("ds");
			}
		}
		// catch mistake
		if (dotstyle != EuclidianStyleConstants.POINT_STYLE_DOT) {
			if (comma) {
				packSpace(sb, "+");
			} else {
				sb.append(",");
			}
			comma = true;
			sb.append("invisible");
		}
	}

	// Returns point style code. Includes comma.
	private void pointOptionCode(GeoPointND geo, StringBuilder sb) {
		pointOptionCode(geo, sb, geo.getPointSize());
	}

	// Line style code; does not include comma.
	private String lineOptionCode(GeoElementND geo, boolean transparency) {
		StringBuilder sb = new StringBuilder();
		int linethickness = geo.getLineThickness();
		int linestyle = geo.getLineType();

		Info info = new Info(geo);

		boolean noPlus = true;
		// if (linethickness != EuclidianStyleConstants.DEFAULT_LINE_THICKNESS)
		// {
		// first parameter
		noPlus = false;
		sb.append("linewidth(");
		sb.append(format(linethickness / 2.0 * 0.8));
		sb.append(")");

		if (linestyle != EuclidianStyleConstants.DEFAULT_LINE_TYPE) {
			if (!noPlus) {
				packSpace(sb, "+");
			} else {
				noPlus = false;
			}
			linestyleCode(linestyle, sb);
		}
		if (!info.getLinecolor().equals(GColor.BLACK)) {
			if (!noPlus) {
				packSpace(sb, "+");
			} else {
				noPlus = false;
			}
			colorCode(info.getLinecolor(), sb);
		}
		if (transparency && geo.isFillable() && info.getAlpha() > 0.0f) {
			/*
			 * TODO: write opacity code? if (!noPlus) packSpace("+",sb); else
			 * noPlus = false; sb.append("fillcolor="); ColorCode(linecolor,sb);
			 * sb.append(",fillstyle=solid,opacity=");
			 * sb.append(geo.getAlphaValue());
			 */
		}
		if (noPlus) {
			return null;
		}
		return sb.toString();
	}

	// Append the linestyle to PSTricks code
	private static void linestyleCode(int linestyle, StringBuilder sb) {
		// note: removed 'pt' from linetype commands, seems to work better.
		switch (linestyle) {
		default:
			// do nothing
			break;
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			sb.append("dotted");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			sb.append("linetype(\"");
			// int size = resizePt(3);
			int size = 2;
			sb.append(size);
			sb.append(" ");
			sb.append(size);
			sb.append("\")");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			sb.append("linetype(\"");
			// size = resizePt(6);
			size = 4;
			sb.append(size);
			sb.append(" ");
			sb.append(size);
			sb.append("\")");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			sb.append("linetype(\"");
			// int size1 = resizePt(2);
			// int size2 = resizePt(8);
			// int size3 = resizePt(10);
			int size1 = 0, size2 = 3, size3 = 4;
			sb.append(size1);
			sb.append(" ");
			sb.append(size2);
			sb.append(" ");
			sb.append(size3);
			sb.append(" ");
			sb.append(size2);
			sb.append("\")");
			break;
		}
	}

	// Append the name color to StringBuilder sb
	@Override
	protected void colorCode(GColor c0, StringBuilder sb) {
		int red = c0.getRed(), green = c0.getGreen(), blue = c0.getBlue();
		if (grayscale) {
			String colorname = "";
			int grayscale1 = (red + green + blue) / 3;
			GColor c = GColor.newColor(grayscale1, grayscale1, grayscale1);
			if (customColor.containsKey(c)) {
				colorname = customColor.get(c).toString();
			} else {
				// Not compact:
				// "pen XXXXXX = rgb(0,0,0); pen YYYYYY = rgb(1,1,1);"
				// Compact:
				// "pen XXXXXX = rgb(0,0,0), YYYYYY = rgb(1,1,1);"
				colorname = createCustomColor(grayscale1, grayscale1,
						grayscale1);
				if (!compact) {
					codeColors.append("pen ");
				} else {
					codeColors.append(", ");
				}
				codeColors.append(colorname);
				packSpace(codeColors, "=");
				codeColors.append("rgb(" + format(grayscale1 / 255d) + ","
						+ format(grayscale1 / 255d) + ","
						+ format(grayscale1 / 255d) + ")");
				if (!compact) {
					codeColors.append("; ");
				}
				customColor.put(c, colorname);
			}
			if (c.equals(GColor.BLACK)) {
				sb.append("black");
			} else if (c.equals(GColor.GRAY)) {
				sb.append("gray");
			} else if (c.equals(GColor.WHITE)) {
				sb.append("white");
			} else {
				sb.append(colorname);
			}
		} else {
			if (c0.equals(GColor.BLACK)) {
				sb.append("black");
			} else if (c0.equals(GColor.GRAY)) {
				sb.append("gray");
			} else if (c0.equals(GColor.WHITE)) {
				sb.append("white");
			} else if (c0.equals(GColor.RED)) {
				sb.append("red");
			} else if (c0.equals(GColor.GREEN)) {
				sb.append("green");
			} else if (c0.equals(GColor.BLUE)) {
				sb.append("blue");
			} else if (c0.equals(GColor.YELLOW)) {
				sb.append("yellow");
			} else {
				String colorname = "";
				if (customColor.containsKey(c0)) {
					colorname = customColor.get(c0).toString();
				} else {
					colorname = createCustomColor(red, green, blue);
					if (!compact) {
						codeColors.append("pen ");
					} else {
						codeColors.append(", ");
					}
					codeColors.append(colorname);
					packSpace(codeColors, "=");
					codeColors.append("rgb(" + format(red / 255d) + ","
							+ format(green / 255d) + "," + format(blue / 255d)
							+ ")");
					if (!compact) {
						codeColors.append("; ");
					}
					customColor.put(c0, colorname);
				}
				sb.append(colorname);
			}
		}
	}

	/**
	 * Equivalent to ColorCode, but dampens color based upon opacity. Appends
	 * the pen to codeColor.
	 * 
	 * @param c
	 *            The original color before transparency.
	 * @param opacity
	 *            Double value from 0 to 1, with 0 being completely transparent.
	 * @param sb
	 *            StringBuilder to attach code to.
	 */
	protected void colorLightCode(GColor c, double opacity, StringBuilder sb) {
		// new Color object so that c is not overriden.
		GColor tempc;
		int red = c.getRed(), green = c.getGreen(), blue = c.getBlue();
		red = (int) (255 * (1 - opacity) + red * opacity);
		green = (int) (255 * (1 - opacity) + green * opacity);
		blue = (int) (255 * (1 - opacity) + blue * opacity);
		if (grayscale) {
			String colorname = "";
			int grayscale1 = (red + green + blue) / 3;
			tempc = GColor.newColor(grayscale1, grayscale1, grayscale1);
			if (customColor.containsKey(tempc)) {
				colorname = customColor.get(tempc).toString();
			} else {
				colorname = createCustomColor(grayscale1, grayscale1,
						grayscale1);
				if (!compact) {
					codeColors.append("pen ");
				} else {
					codeColors.append(", ");
				}
				codeColors.append(colorname);
				packSpace(codeColors, "=");
				codeColors.append("rgb(" + format(grayscale1 / 255d) + ","
						+ format(grayscale1 / 255d) + ","
						+ format(grayscale1 / 255d) + ")");
				if (!compact) {
					codeColors.append("; ");
				}
				customColor.put(tempc, colorname);
			}
			if (tempc.equals(GColor.BLACK)) {
				sb.append("black");
			} else if (tempc.equals(GColor.GRAY)) {
				sb.append("gray");
			} else if (tempc.equals(GColor.WHITE)) {
				sb.append("white");
			} else {
				sb.append(colorname);
			}
		} else {
			tempc = GColor.newColor(red, green, blue);
			if (tempc.equals(GColor.BLACK)) {
				sb.append("black");
			} else if (tempc.equals(GColor.GRAY)) {
				sb.append("gray");
			} else if (tempc.equals(GColor.WHITE)) {
				sb.append("white");
			} else if (tempc.equals(GColor.RED)) {
				sb.append("red");
			} else if (tempc.equals(GColor.GREEN)) {
				sb.append("green");
			} else if (tempc.equals(GColor.BLUE)) {
				sb.append("blue");
			} else if (tempc.equals(GColor.YELLOW)) {
				sb.append("yellow");
			} else {
				String colorname = "";
				if (customColor.containsKey(tempc)) {
					colorname = customColor.get(tempc).toString();
				} else {
					colorname = createCustomColor(red, green, blue);
					if (!compact) {
						codeColors.append("pen ");
					} else {
						codeColors.append(", ");
					}
					codeColors.append(colorname);
					packSpace(codeColors, "=");
					codeColors.append("rgb(" + format(red / 255d) + ","
							+ format(green / 255d) + "," + format(blue / 255d)
							+ ")");
					if (!compact) {
						codeColors.append("; ");
					}
					customColor.put(tempc, colorname);
				}
				sb.append(colorname);
			}
		}
	}

	/*
	 * // Resize text Keep the ratio between font size and picture height
	 * private String resizeFont(int fontSize){ int
	 * latexFont=frame.getFontSize(); double
	 * height_geogebra=euclidianView.getHeight()/30; double
	 * height_latex=frame.getLatexHeight(); double
	 * ratio=height_latex/height_geogebra; int
	 * theoric_size=(int)Math.round(ratio*fontSize); String st=null;
	 * switch(latexFont){ case 10: if (theoric_size<=5) st="\\tiny{"; else if
	 * (theoric_size<=7) st="\\scriptsize{"; else if (theoric_size<=8)
	 * st="\\footnotesize{"; else if (theoric_size<=9) st="\\small{"; else if
	 * (theoric_size<=10) ; else if (theoric_size<=12) st="\\large{"; else if
	 * (theoric_size<=14) st="\\Large{"; else if (theoric_size<=17)
	 * st="\\LARGE{"; else if (theoric_size<=20) st="\\huge{"; else
	 * st="\\Huge{"; break; case 11: if (theoric_size<=6) st="\\tiny{"; else if
	 * (theoric_size<=8) st="\\scriptsize{"; else if (theoric_size<=9)
	 * st="\\footnotesize{"; else if (theoric_size<=10) st="\\small{"; else if
	 * (theoric_size<=11) ; else if (theoric_size<=12) st="\\large{"; else if
	 * (theoric_size<=14) st="\\Large{"; else if (theoric_size<=17)
	 * st="\\LARGE{"; else if (theoric_size<=20) st="\\huge{"; else
	 * st="\\Huge{"; break; case 12: if (theoric_size<=6) st="\\tiny{"; else if
	 * (theoric_size<=8) st="\\scriptsize{"; else if (theoric_size<=10)
	 * st="\\footnotesize{"; else if (theoric_size<=11) st="\\small{"; else if
	 * (theoric_size<=12) ; else if (theoric_size<=14) st="\\large{"; else if
	 * (theoric_size<=17) st="\\Large{"; else if (theoric_size<=20)
	 * st="\\LARGE{"; else if (theoric_size<=25) st="\\huge{"; else
	 * st="\\Huge{"; break; } return st; }
	 */
	// private void defineTransparency(){}

	private void addText(String st0, boolean isLatex, int style) {
		if (isLatex) {
			code.append("$");
		}
		String st = st0;

		if (isLatex) {
			st = st.replaceAll("\n", " ");
		}

		if (isLatex && st.charAt(0) == '$') {
			st = st.substring(1);
		}

		// use packages
		if (isLatex) {
			/*
			 * too many commands to check, here's a partial list of more common
			 * ones: \begin \text \substack \tfrac \dfrac \cfrac \iint \iiint
			 * \iiiint \boldsymbol \pmb \dots \dddot \ddddot
			 */
			if (st.indexOf("\\") != -1) {
				usepackage.add("amsmath");
			}
			if (st.indexOf("\\mathbb") != -1 || st.indexOf("\\mathfrak") != -1) {
				usepackage.add("amssymb");
			}
			if (st.indexOf("\\mathscr") != -1) {
				usepackage.add("mathrsfs");
			}
		}

		// Convert Unicode symbols
		if (isLatex) {
			st = convertUnicodeToLatex(st);
		} else {
			st = convertUnicodeToText(st);
			// Strip dollar signs. Questionable! TODO
			st = st.replace("\\$", "dollar ");
			// Replace all backslash symbol with \textbackslash, except for
			// newlines
			st = st.replace("\\\\", "\\\\textbackslash ").replace(
					"\\\\textbackslash \\\\textbackslash ", "\\\\\\\\ ");
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
		/*
		 * if (!geocolor.equals(Color.BLACK)){ ColorCode2(geocolor,code);
		 * code.append("{"); } // Colors moved to drawText()
		 * 
		 * if (size!=app.getFontSize()) { String formatFont=resizeFont(size); if
		 * (null!=formatFont) code.append(formatFont); }
		 */

		// strip final '$'
		code.append(st.substring(0, st.length() - 1));
		if (!isLatex || st.charAt(st.length() - 1) != '$') {
			code.append(st.charAt(st.length() - 1));
		}

		// if (size!=app.getFontSize()) code.append("}");
		// if (!geocolor.equals(Color.BLACK)) code.append("}");

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
		if (isLatex) {
			code.append("$");
		}
	}

	/**
	 * Append spaces between list s to code if not in compact mode.
	 * 
	 * @param s
	 *            A string which can have spaces around it.
	 */
	protected void packSpaceBetween(String... s) {
		packSpaceBetween(code, s);
	}

	/**
	 * Append spaces between list s to sb if not in compact mode.
	 * 
	 * @param sb
	 *            The StringBuilder to which s is attached.
	 * @param s
	 *            A string which can have spaces around it.
	 */
	protected void packSpaceBetween(StringBuilder sb, String... s) {
		sb.append(s[0]);
		for (int i = 1; i < s.length; i++) {
			if (!compact) {
				sb.append(" ");
				sb.append(s[i]);
			} else {
				sb.append(s[i]);
			}
		}
	}

	/**
	 * Append spaces after s to code if not in compact mode.
	 * 
	 * @param s
	 *            A string which can have spaces around it.
	 */
	protected void packSpaceAfter(String... s) {
		packSpaceAfter(code, s);
	}

	/**
	 * Append spaces after s to sb if not in compact mode.
	 * 
	 * @param sb
	 *            The StringBuilder to which s is attached.
	 * @param s
	 *            A string which can have spaces around it.
	 */
	protected void packSpaceAfter(StringBuilder sb, String... s) {
		packSpaceBetween(sb, s);
		if (!compact) {
			sb.append(" ");
		}
	}

	/**
	 * Append space around s to code if not in compact mode.
	 * 
	 * @param s
	 *            A string which can have spaces around it.
	 */
	protected void packSpace(String... s) {
		packSpace(code, s);
	}

	/**
	 * Append spaces about s to sb if not in compact mode.
	 * 
	 * @param sb
	 *            The StringBuilder to which s is attached.
	 * @param s
	 *            A string which can have spaces around it.
	 */
	protected void packSpace(StringBuilder sb, String... s) {
		if (!compact) {
			sb.append(" ");
		}
		packSpaceAfter(sb, s);
	}

	/**
	 * Default version of startDraw, appends the start of a draw() command to
	 * StringBuilder code.
	 * 
	 */
	protected void startDraw() {
		startDraw(code);
	}

	/**
	 * Appends the opening of a draw() command to sb.
	 * 
	 * @param sb
	 *            Code to attach to.
	 */
	protected void startDraw(StringBuilder sb) {
		if (!compact) {
			sb.append("\n");
		}
		if (compactcse5) {
			sb.append("D(");
		} else {
			sb.append("draw(");
		}
	}

	/**
	 * Appends line style code to end of StringBuilder code.
	 * 
	 * @param geo
	 *            contains line style code.
	 */
	protected void endDraw(GeoElementND geo) {
		endDraw(geo, code);
	}

	/**
	 * Appends line style code to end of StringBuilder code.
	 * 
	 * @param geo
	 *            contains line style code.
	 * @param sb
	 *            code to attach to.
	 */
	protected void endDraw(GeoElementND geo, StringBuilder sb) {
		if (fillInequality) {
			return;
		}
		if (lineOptionCode(geo, true) != null) {
			packSpaceAfter(sb, ",");
			sb.append(lineOptionCode(geo, true));
		}
		sb.append("); ");
	}

	/**
	 * Begins an object drawn by the filldraw() command.
	 * 
	 * @param sb
	 *            StringBuilder to which code added.
	 */
	protected void startTransparentFill(StringBuilder sb) {
		if (!compact) {
			sb.append("\n");
		}
		if (fillType != ExportSettings.FILL_NONE) {
			sb.append("filldraw(");
		} else if (compactcse5) {
			sb.append("D(");
		} else {
			sb.append("draw(");
		}
	}

	/**
	 * Closes an object drawn by the filldraw() command.
	 * 
	 * @param geo
	 *            Object that can be filled.
	 * @param sb
	 *            StringBuilder to which code added.
	 */
	protected void endTransparentFill(GeoElement geo, StringBuilder sb) {

		Info info = new Info(geo);
		// transparent fill options
		if (fillType == ExportSettings.FILL_OPAQUE) {
			packSpaceAfter(sb, ",");
			if (info.getAlpha() >= 0.9) {
				colorCode(info.getLinecolor(), sb);
			} else {
				sb.append("invisible");
			}
		}
		// use opacity(alpha value) pen
		else if (fillType == ExportSettings.FILL_OPACITY_PEN) {
			packSpaceAfter(sb, ",");
			colorCode(info.getLinecolor(), sb);
			packSpace(sb, "+");
			sb.append("opacity(");
			sb.append(info.getAlpha());
			sb.append(")");
		} else if (fillType == ExportSettings.FILL_LAYER) {
			packSpaceAfter(sb, ",");
			colorLightCode(info.getLinecolor(), info.getAlpha(), sb);
		}
		if (lineOptionCode(geo, true) != null) {
			packSpaceAfter(sb, ",");
			sb.append(lineOptionCode(geo, true));
		}
		sb.append("); ");
	}

	/**
	 * For use with drawSpecialPoint() function, appends dot styles
	 * 
	 * @param c
	 *            color
	 */
	protected void endPoint(GColor c) {
		if (!c.equals(GColor.BLACK) && dotColors) {
			code.append(",");
			if (!compact) {
				code.append(" ");
			}
			colorCode(c, code);
		}
		code.append("); ");
	}

	/**
	 * Adds a point in the format "(s1,s2)" to sb.
	 * 
	 * @param s1
	 *            format(x-coordinate)
	 * @param s2
	 *            format(y-coordinate)
	 * @param sb
	 *            StringBuilder object to append code to.
	 */
	protected void addPoint(String s1, String s2, StringBuilder sb) {
		String pairString = "(" + s1 + "," + s2 + ")";
		if (pairName && pairNameTable.containsKey(pairString)) {
			sb.append(pairNameTable.get(pairString));
		// retrieves point name from codePointDecl
		// using string manipulations, unsafe
		// int locPair = codePointDecl.indexOf("(" + s1 + "," + s2 + ")");
		// if(locPair != -1 && compact) {
		// String name = codePointDecl.substring(0,locPair);
		// int locNameStart = name.lastIndexOf(" ")+1;
		// int locNameEnd = name.lastIndexOf("=");
		// name = codePointDecl.substring(locNameStart,locNameEnd);
		// sb.append(name);
		// return;
		// }
		// else {
		// String name = codePointDecl.substring(0,locPair); // temporary re-use
		// int locNameStart = Math.max(name.lastIndexOf(", ")+2,
		// name.lastIndexOf("pair ")+5);
		// int locNameEnd = name.lastIndexOf("=");
		// name = codePointDecl.substring(locNameStart,locNameEnd);
		// sb.append(name);
		// return;
		// }
		} else {
			sb.append(pairString);
		}
	}

	/**
	 * Adds a point in the format "(s1,s2)" to sb.
	 * 
	 * @param x
	 *            real value of x-coordinate
	 * @param y
	 *            real value of y-coordinate
	 * @param sb
	 *            StringBuilder object to append code to.
	 */
	protected void addPoint(double x, double y, StringBuilder sb) {
		addPoint(format(x), format(y), sb);
	}

	/**
	 * Converts unicode expressions ("\u03c0") to plain text ("pi").
	 * 
	 * @param sb
	 *            StringBuilder with code.
	 * @return Updated StringBuilder;
	 */
	protected StringBuilder convertUnicodeToText(StringBuilder sb) {
		// import unicode;
		String tempc = sb.toString();
		tempc = convertUnicodeToText(tempc);
		// override sb with tempc
		sb.delete(0, sb.length());
		sb.append(tempc);
		return sb;
	}

	/**
	 * Converts unicode expressions ("\u03c0") to plain text ("pi").
	 * 
	 * @param s
	 *            Text to convert unicode symbols to text. Is not modified.
	 * @return Converted string.
	 */
	protected String convertUnicodeToText(String s) {
		// import unicode;
		String s1 = s;
		Iterator<Character> it = UnicodeTeX.getMap().keySet().iterator();
		while (it.hasNext()) {
			char skey = it.next();
			s1 = s1.replace(skey + "", UnicodeTeX.getMap().get(skey) + " ");
		}
		return s1.replace(Unicode.DEGREE_STRING, "o ")
				// degree symbol
				.replace("\u212f", "e ").replace("\u00b2", "2 ")
				.replace("\u00b3", "3 ").replace("pi \\)", "pi\\)"); // eliminate
																		// unsightly
																		// spaces
	}

	/**
	 * Converts unicode expressions ("\u03c0") to LaTeX expressions ("\pi").
	 * 
	 * @param s
	 *            Text to convert unicode symbols to LaTeX. Is not modified.
	 * @return Converted string.
	 */
	protected String convertUnicodeToLatex(String s) {
		// import unicode;
		String s1 = s;
		Iterator<Character> it = UnicodeTeX.getMap().keySet().iterator();
		// look up unicodeTable conversions and replace with LaTeX commands
		while (it.hasNext()) {
			char skey = it.next();
			s1 = s1.replace(skey + "",
					"\\\\" + UnicodeTeX.getMap().get(skey) + " ");
		}

		// strip dollar signs
		/*
		 * int locDollar = 0; while((locDollar = s1.indexOf('$',locDollar+1)) !=
		 * -1) { if(locDollar != 0 && locDollar != s1.length() &&
		 * s1.charAt(locDollar-1) != '\\') s1 = s1.substring(0,locDollar) + "\\"
		 * + s1.substring(locDollar); }
		 */

		StringBuilder sb = new StringBuilder();
		// ignore first and last characters
		// TODO check if odd number of dollar signs? No catch-all fix ..
		sb.append(s1.charAt(0));
		for (int i = 1; i < s1.length() - 1; i++) {
			if (s1.charAt(i - 1) == '\\'
					&& (i == 1 || s1.charAt(i - 2) != '\\')) {
				sb.append(s1.charAt(i));
				continue;
			} else if (s1.charAt(i) == '$') {
				sb.append("\\$");
			} else {
				sb.append(s1.charAt(i));
			}
		}
		if (s1.length() > 1) {
			sb.append(s1.charAt(s1.length() - 1));
		}
		s1 = sb.toString();

		return s1.replace(Unicode.DEGREE_STRING, "^\\\\circ").replace("\u212f", " e")
				.replace("\u00b2", "^2").replace("\u00b3", "^3")
				.replace("\\\\questeq", "\\\\stackrel{?}{=}");
	}

	/**
	 * Formats a function string.
	 * 
	 * @param s
	 *            Code containing function.
	 * @return Parsed function string compatible with programming languages.
	 */
	protected String parseFunction(String s) {
		// Unicode?
		return killSpace(StringUtil.toLaTeXString(s, true));
	}

	/*
	 * Rewrite the function: TODO Kill spaces Add character * when needed: 2 x
	 * +3 ----> 2*x+3 Rename several functions: log(x) ---> ln(x) ceil(x) --->
	 * ceiling(x) exp(x) ---> 2.71828^(x)
	 */
	private static String killSpace(String name) {
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
				if (space) {
					sb.append("*");
				}
				sb.append(c);
				space = false;
				operand = false;
			}
		}

		// following needs cleanup
		// rename functions log, ceil and exp
		renameFunc(sb, "\\\\pi", "pi");
		renameFunc(sb, "EXP(", "exp(");
		renameFunc(sb, "ln(", "log(");
		// integers
		renameFunc(sb, "ceiling(", "ceil(");
		renameFunc(sb, "CEILING(", "ceil(");
		renameFunc(sb, "FLOOR(", "floor(");
		// de-capitalize trigonometric/hyperbolics
		renameFunc(sb, "SIN(", "sin(");
		renameFunc(sb, "COS(", "cos(");
		renameFunc(sb, "TAN(", "tan(");
		renameFunc(sb, "ASIN(", "asin(");
		renameFunc(sb, "ACOS(", "acos(");
		renameFunc(sb, "ATAN(", "atan(");
		renameFunc(sb, "SINH(", "sinh(");
		renameFunc(sb, "COSH(", "cosh(");
		renameFunc(sb, "TANH(", "tanh(");
		renameFunc(sb, "ASINH(", "asinh(");
		renameFunc(sb, "ACOSH(", "acosh(");
		renameFunc(sb, "ATANH(", "atanh(");

		// for exponential in new Geogebra version.
		renameFunc(sb, Unicode.EULER_STRING, "2.718"); /* 2.718281828 */

		for (Greek greek : Greek.values()) {

			String latexNameNoBackslash = greek.getLaTeX();
			String latexName = "\\" + latexNameNoBackslash;

			// temporary code: may be redundant, fail-safe
			renameFunc(sb, greek.unicode + "", latexNameNoBackslash);

			renameFunc(sb, latexName, latexNameNoBackslash);
		}

		return sb.toString();
	}

	@Override
	protected StringTemplate getStringTemplate() {
		// Asymptote doesn't understand E notation ie 3E-10
		return StringTemplate.fullFigures(StringType.PSTRICKS);
	}

	/**
	 * @param geo
	 *            element
	 * @return stroke style
	 */
	public String penStyle(GeoElement geo) {
		StringBuilder sb = new StringBuilder();
		switch (geo.getLineType()) {
		default:
		case EuclidianStyleConstants.DEFAULT_LINE_TYPE:
			sb.append("solid+");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			sb.append("longdashed+");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			sb.append("dashed+");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			sb.append("dashdotted+");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			sb.append("Dotted+");
			break;
		}
		return sb.toString();
	}

	@Override
	protected boolean isLatexFunction(String s) {
		// used if there are other non-latex
		return !s.toLowerCase().contains("csc(")
				&& !s.toLowerCase().contains("csch(")
				&& !s.toLowerCase().contains("sec(")
				&& !s.toLowerCase().contains("cot(")
				&& !s.toLowerCase().contains("coth(")
				&& !s.toLowerCase().contains("sech(")
				&& !s.toLowerCase().contains("if");
	}

	@Override
	protected String format(double d) {
		return super.format(d).replace("E", "e");
	}

	@Override
	protected boolean fillSpline(GeoCurveCartesian[] curves) {
		if (curves[0].getAlphaValue() == 0
				&& FillType.STANDARD == curves[0].getFillType()) {
			return false;
		}
		String liopco = lineOptionCode(curves[0], true);
		if (liopco == null) {
			liopco = "";
		} else {
			liopco = "," + liopco;
		}
		for (int i = 0; i < curves.length; i++) {
			drawSingleCurveCartesian(curves[i], false);
		}
		StringBuilder fill = new StringBuilder();
		fill.append("\nfill(");

		double p;
		double y;
		double x;

		for (int i = 0; i < curves.length; i++) {
			p = curves[i].getMinParameter();
			y = curves[i].getFunY().value(curves[i].getMinParameter());
			if (Math.abs(y) < 0.001) {
				y = 0;
			}
			double step = (curves[i].getMaxParameter()
					- curves[i].getMinParameter()) / 200;
			for (; p <= curves[i].getMaxParameter(); p += step) {
				y = curves[i].getFunY().value(p);
				x = curves[i].getFunX().value(p);
				if (Math.abs(y) < 0.001) {
					y = 0;
				}
				if (Math.abs(x) < 0.001) {
					x = 0;
				}
				fill.append("(" + x + "," + y + ") -- ");
			}
		}
		fill.append("cycle" + liopco + ");");
		code.append(fill);
		return true;
	}

	/**
	 * @param s
	 *            shape
	 * @param ineq
	 *            inequality
	 * @param geo
	 *            element
	 * @param ds
	 *            bounds, see getViewBoundsForGeo
	 */
	public void superFill(GShape s, Inequality ineq, FunctionalNVar geo,
			double[] ds) {
		importpackage.add("patterns");
		GColor c = ((GeoElement) geo).getObjectColor();
		int lineType = ((GeoElement) geo).getLineType();
		((GeoElement) geo).setLineType(ineq.getBorder().lineType);
		code.append("\npen border=" + penStyle((GeoElement) geo));
		colorCode(c, code);
		((GeoElement) geo).setLineType(lineType);
		code.append(";\npen fillstyle=" + penStyle((GeoElement) geo));
		colorCode(c, code);
		if (((GeoElement) geo).getFillType() != FillType.STANDARD) {
			code.append(";\nadd(\"hatch\",hatch(2mm,NW,fillstyle));\n");
		} else {
			code.append(";\nadd(\"hatch\",hatch(0.5mm,NW,fillstyle));\n");
		}
		switch (ineq.getType()) {
		default:
			// do nothing
			break;
		case INEQUALITY_CONIC:
			GeoConicND conic = ineq.getConicBorder();
			if (conic.getType() == GeoConicNDConstants.CONIC_ELLIPSE
					|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
				conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
				((GeoElement) conic)
						.setObjColor(((GeoElement) geo).getObjectColor());
				conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
				((GeoElement) conic)
						.setAlphaValue(((GeoElement) geo).getAlphaValue());
				conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
				((GeoElement) conic).setHatchingAngle(
						(int) ((GeoElement) geo).getHatchingAngle());
				((GeoElement) conic).setHatchingDistance(
						((GeoElement) geo).getHatchingDistance());
				((GeoElement) conic)
						.setFillType(((GeoElement) geo).getFillType());
				fillInequality = true;
				drawGeoConic(conic);
				fillInequality = false;
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
			code.append("filldraw(");
			double precX = Integer.MAX_VALUE;
			double precY = Integer.MAX_VALUE;
			while (!path.isDone()) {
				path.currentSegment(coords);

				if (coords[0] == precX && coords[1] == precY) {
					code.append("cycle,pattern(\"hatch\"),border);\n");
					code.append("filldraw(");

				} else {
					code.append("(");
					code.append(format((coords[0] - zeroX) / ds[4]));
					code.append(",");
					code.append(format(-(coords[1] - zeroY) / ds[5]));
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
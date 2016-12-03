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
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoAngleLines;
import org.geogebra.common.kernel.algos.AlgoAnglePoints;
import org.geogebra.common.kernel.algos.AlgoAngleVector;
import org.geogebra.common.kernel.algos.AlgoAngleVectors;
import org.geogebra.common.kernel.algos.AlgoBoxPlot;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFunctionAreaSums;
import org.geogebra.common.kernel.algos.AlgoIntersectAbstract;
import org.geogebra.common.kernel.algos.AlgoSlope;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.cas.AlgoIntegralDefinite;
import org.geogebra.common.kernel.cas.AlgoIntegralFunctions;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.FillType;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoTransferFunction;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.GStringTokenizer;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;

/**
 * @author Le Coq loic
 */

public abstract class GeoGebraToPstricks extends GeoGebraExport {
	private boolean eurosym = false;
	private static final int FORMAT_BEAMER = 1;
	private StringBuilder codeBeginPic;

	/**
	 * Constructor for GeoGeBra export
	 * 
	 * @param app
	 *            GeoGeBra Application
	 */
	public GeoGebraToPstricks(App app) {
		super(app);
	}

	@Override
	public void generateAllCode() {

		format = frame.getFormat();
		// init unit variables
		try {
			xunit = frame.getXUnit();
			yunit = frame.getYUnit();
		} catch (NullPointerException e2) {
			xunit = 1;
			yunit = 1;
		}
		// scaleratio=yunit/xunit;
		// Initialize new StringBuilder for Pstricks code
		// and CustomColor
		code = new StringBuilder();
		codePoint = new StringBuilder();
		codePreamble = new StringBuilder();

		codeFilledObject = new StringBuilder();
		codeBeginDoc = new StringBuilder();
		codeBeginPic = new StringBuilder();
		customColor = new HashMap<GColor, String>();
		if (format == GeoGebraToPstricks.FORMAT_BEAMER) {
			codePreamble.append("\\documentclass[" + frame.getFontSize()
					+ "pt]{beamer}\n");
		} else {
			codePreamble.append("\\documentclass[" + frame.getFontSize()
					+ "pt]{article}\n");
		}
		codePreamble.append("\\usepackage{pstricks-add}\n\\pagestyle{empty}\n");
		codeBeginDoc.append("\\begin{document}\n");
		if (format == GeoGebraToPstricks.FORMAT_BEAMER) {
			codeBeginDoc.append("\\begin{frame}\n");
		}

		initUnitAndVariable();
		// Environment pspicture
		codeBeginPic.append("\\begin{pspicture*}(");
		codeBeginPic.append(format(xmin));
		codeBeginPic.append(",");
		codeBeginPic.append(format(ymin));
		codeBeginPic.append(")(");
		codeBeginPic.append(format(xmax));
		codeBeginPic.append(",");
		codeBeginPic.append(format(ymax));
		codeBeginPic.append(")\n");
		if (euclidianView.getShowGrid()) {
			drawGrid();
		}

		// Draw axis
		if (euclidianView.getShowXaxis() || euclidianView.getShowYaxis()) {
			drawAxis();
		}

		/*
		 * get all objects from construction and "draw" them by creating
		 * pstricks code
		 */

		drawAllElements();
		/*
		 * Object [] geos =
		 * kernel.getConstruction().getGeoSetConstructionOrder().toArray(); for
		 * (int i=0;i<geos.length;i++){ GeoElement g = (GeoElement)(geos[i]);
		 * drawGeoElement(g,false); //
		 * System.out.println(g+" "+beamerSlideNumber); }
		 */

		// add code for Points and Labels
		if (codePoint.length() != 0) {
			codePoint.insert(0, "\\begin{scriptsize}\n");
			codePoint.append("\\end{scriptsize}\n");

		}
		code.append(codePoint);
		// Close Environment pspicture
		code.append("\\end{pspicture*}\n");
		/*
		 * String formatFont=resizeFont(app.getFontSize()); if
		 * (null!=formatFont){ codeBeginPic.insert(0,formatFont+"\n");
		 * code.append("}\n"); }
		 */
		code.insert(0, codeFilledObject + "");
		code.insert(0, codeBeginPic + "");
		code.insert(0, codeBeginDoc + "");
		code.insert(0, codePreamble + "");
		if (format == GeoGebraToPstricks.FORMAT_BEAMER) {
			code.append("\\end{frame}\n");
		}
		code.append("\\end{document}");
		frame.write(code);

	}

	@Override
	protected void drawLocus(GeoLocus g) {
		ArrayList<MyPoint> ll = g.getPoints();
		Iterator<MyPoint> it = ll.iterator();
		startBeamer(code);
		code.append("\\pscustom");
		code.append(lineOptionCode(g, true));
		code.append("{");
		boolean first = true;
		boolean out = false;
		while (it.hasNext()) {
			MyPoint mp = it.next();
			if (mp.x > xmin && mp.x < xmax && mp.y > ymin && mp.y < ymax) {
				String x = format(mp.x);
				String y = format(mp.y);
				boolean b = mp.lineTo;
				if (first) {
					code.append("\\moveto(");
					first = false;
				} else if (b)
					code.append("\\lineto(");
				else
					code.append("\\moveto(");
				code.append(x);
				code.append(",");
				code.append(y);
				code.append(")\n");
				out = false;
			} else if (!first && mp.lineTo && !out) {
				out = true;
				String x = format(mp.x);
				String y = format(mp.y);
				code.append("\\lineto(");
				code.append(x);
				code.append(",");
				code.append(y);
				code.append(")\n");
			} else {
				first = true;
				out = false;
			}
		}
		code.append("}\n");
		endBeamer(code);
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
		startBeamer(code);
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
		endBeamer(code);
		// Rectangle q1-q3
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\psframe");
		codeFilledObject.append(lineOptionCode(geo, true));
		codeFilledObject.append("(");
		codeFilledObject.append(format(q1));
		codeFilledObject.append(",");
		codeFilledObject.append(y - height);
		codeFilledObject.append(")(");
		codeFilledObject.append(format(q3));
		codeFilledObject.append(",");
		codeFilledObject.append(format(y + height));
		codeFilledObject.append(")\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawSumTrapezoidal(GeoNumeric geo) {
		AlgoFunctionAreaSums algo = (AlgoFunctionAreaSums) geo
				.getParentAlgorithm();
		int n = algo.getIntervals();
		double[] y = algo.getValues();
		double[] x = algo.getLeftBorder();
		startBeamer(codeFilledObject);
		for (int i = 0; i < n; i++) {
			codeFilledObject.append("\\pspolygon");
			codeFilledObject.append(lineOptionCode(geo, true));
			codeFilledObject.append("(");
			codeFilledObject.append(format(x[i]));
			codeFilledObject.append(",0)(");
			codeFilledObject.append(format(x[i + 1]));
			codeFilledObject.append(",0)(");
			codeFilledObject.append(format(x[i + 1]));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[i + 1]));
			codeFilledObject.append(")(");
			codeFilledObject.append(format(x[i]));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[i]));
			codeFilledObject.append(")\n");
			if (i != n - 1 && isBeamer)
				codeFilledObject.append("  ");
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
			codeFilledObject.append("\\psframe");
			codeFilledObject.append(lineOptionCode(geo, true));
			codeFilledObject.append("(");
			codeFilledObject.append(format(x[i]));
			codeFilledObject.append(",0)(");
			codeFilledObject.append(format(x[i] + step));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[i]));
			codeFilledObject.append(")\n");
			if (i != n - 1 && isBeamer)
				codeFilledObject.append("  ");
		}
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawIntegralFunctions(GeoNumeric geo) {
		// command:
		// \pscustom[option]{\pstplot{a}{b}{f(x)}\lineto(b,g(b))\pstplot{b}{a}{g(x)}
		// \lineto(a,f(a))\closepath}
		AlgoIntegralFunctions algo = (AlgoIntegralFunctions) geo
				.getParentAlgorithm();
		// function f
		GeoFunction f = algo.getF();
		// function g
		GeoFunction g = algo.getG();
		// double a and b
		double a = algo.getA().getDouble();
		double b = algo.getB().getDouble();
		// String output for a and b
		String sa = format(a);
		String sb = format(b);
		// String Expression of f and g
		String valueF = f.toValueString(getStringTemplate());
		valueF = killSpace(StringUtil.toLaTeXString(valueF, true));
		String valueG = g.toValueString(getStringTemplate());
		valueG = killSpace(StringUtil.toLaTeXString(valueG, true));
		// String expressions for f(a) and g(b)
		String fa = format(f.evaluate(a));
		String gb = format(g.evaluate(b));
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\pscustom");
		codeFilledObject.append(lineOptionCode(geo, true));
		codeFilledObject.append("{\\psplot{");
		codeFilledObject.append(sa);
		codeFilledObject.append("}{");
		codeFilledObject.append(sb);
		codeFilledObject.append("}{");
		codeFilledObject.append(valueF);
		codeFilledObject.append("}\\lineto(");
		codeFilledObject.append(sb);
		codeFilledObject.append(",");
		codeFilledObject.append(gb);
		codeFilledObject.append(")\\psplot{");
		codeFilledObject.append(sb);
		codeFilledObject.append("}{");
		codeFilledObject.append(sa);
		codeFilledObject.append("}{");
		codeFilledObject.append(valueG);
		codeFilledObject.append("}\\lineto(");
		codeFilledObject.append(sa);
		codeFilledObject.append(",");
		codeFilledObject.append(fa);
		codeFilledObject.append(")\\closepath}\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawIntegral(GeoNumeric geo) {
		// command:
		// \pscutom[option]{\pstplot{a}{b}{f(x)}\lineto(b,0)\lineto(a,0)\closepath}
		AlgoIntegralDefinite algo = (AlgoIntegralDefinite) geo
				.getParentAlgorithm();
		// function f
		GeoFunction f = algo.getFunction();
		// between a and b
		String a = format(algo.getA().getDouble());
		String b = format(algo.getB().getDouble());
		String value = f.toValueString(getStringTemplate());
		value = killSpace(StringUtil.toLaTeXString(value, true));
		if (a.substring(a.length() - 1).equals("" + Unicode.INFINITY)) {
			a = format(xmin);
		}
		if (b.substring(b.length() - 1).equals("" + Unicode.INFINITY)) {
			b = format(xmax);
		}
		startBeamer(codeFilledObject);
		if (!isLatexFunction(f.toValueString(StringTemplate.noLocalDefault))) {
			double af = xmin;
			double bf = xmax;
			if (f.hasInterval()) {
				af = f.getIntervalMin();
				bf = f.getIntervalMax();
			}
			f.setInterval(kernel.getAlgebraProcessor().evaluateToDouble(a),
					kernel.getAlgebraProcessor().evaluateToDouble(b));
			code.append("\\pscustom");
			f.setInterval(algo.getA().getDouble(), algo.getB().getDouble());
			drawFunction(f, true, geo);
			f.setInterval(af, bf);
			if (f.isEuclidianVisible()) {
				drawFunction(f, false, geo);
			}
			/*
			 * if (f.isEuclidianVisible()) { String liop = LineOptionCode(geo,
			 * true); GColor col = geo.getObjectColor();
			 * geo.setObjColor(f.getObjectColor()); String liopf =
			 * LineOptionCode(f, true); if (!"".equals(liopf)) { int beginf =
			 * liopf.indexOf("linecolor"); int endf = liopf.indexOf(",",
			 * beginf); if (endf < 0 && beginf >-1) { endf = liopf.indexOf("]",
			 * beginf); } else{ if (beginf<0){ liopf="linecolor=black";
			 * beginf=0; endf=liopf.length(); } } liopf =
			 * liopf.substring(beginf, endf); liop = liop.replace("]", "," +
			 * liopf + "]"); } else { int beginl = liop.indexOf("linecolor");
			 * int endl = liop.indexOf(",", beginl); liop =
			 * liop.replace(liop.substring(beginl, endl), ""); }
			 * code.append(liop); geo.setObjColor(col); } else {
			 * code.append(LineOptionCode(geo, true)); } code.append("{");
			 * drawFunction(f); x=algo.getB().getDouble(); if
			 * (algo.getB().getDouble() > xmax) { String last =
			 * code.substring(code.lastIndexOf("line") + 4); x =
			 * Double.parseDouble(last.split(",")[1].split("\\(")[1]); }
			 * code.append("\\psline(" + x + "," + 0 + ")\n");
			 * code.append("\\psline(" + algo.getA().getDouble() + "," + 0 +
			 * ")\n"); code.append("\\closepath}\n"); f.setInterval(af, bf); if
			 * (f.isEuclidianVisible()) { code.append("\\psline" +
			 * LineOptionCode(geo, true) + "(" + x + "," + 0 + ")\n");
			 * code.append("\\psline" + LineOptionCode(geo, true) + "(" +
			 * algo.getA().getDouble() + "," + 0 + ")\n"); drawFunction(f); }
			 */
		} else {

			codeFilledObject.append("\\pscustom");
			codeFilledObject.append(lineOptionCode(geo, true));
			codeFilledObject.append("{\\psplot{");
			codeFilledObject.append(a);
			codeFilledObject.append("}{");

			codeFilledObject.append(b);
			codeFilledObject.append("}{");
			codeFilledObject.append(value);
			codeFilledObject.append("}\\lineto(");
			codeFilledObject.append(b);
			codeFilledObject.append(",0)\\lineto(");
			codeFilledObject.append(a);
			codeFilledObject.append(",0)\\closepath}\n");
		}
		endBeamer(codeFilledObject);
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
		float x = (float) coords[0];
		float y = (float) coords[1];
		float xright = x + slopeTriangleSize;
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\pspolygon");
		codeFilledObject.append(lineOptionCode(geo, true));
		codeFilledObject.append("(");
		codeFilledObject.append(format(x));
		codeFilledObject.append(",");
		codeFilledObject.append(format(y));
		codeFilledObject.append(")");
		codeFilledObject.append("(");
		codeFilledObject.append(format(xright));
		codeFilledObject.append(",");
		codeFilledObject.append(format(y));
		codeFilledObject.append(")");
		codeFilledObject.append("(");
		codeFilledObject.append(format(xright));
		codeFilledObject.append(",");
		codeFilledObject.append(format(y + rwHeight));
		codeFilledObject.append(")");
		codeFilledObject.append("\n");
		endBeamer(codeFilledObject);
		// draw Label
		float xLabelHor = (x + xright) / 2;
		float yLabelHor = y
				- (float) ((euclidianView.getFont().getSize() + 2) / euclidianView
						.getYscale());
		GColor geocolor = geo.getObjectColor();
		startBeamer(codePoint);
		codePoint.append("\\rput[bl](");
		codePoint.append(format(xLabelHor));
		codePoint.append(",");
		codePoint.append(format(yLabelHor));
		codePoint.append("){");
		if (!geocolor.equals(GColor.BLACK)) {
			codePoint.append("\\");
			colorCode(geocolor, codePoint);
			codePoint.append("{");
		}
		codePoint.append(slopeTriangleSize);
		if (!geocolor.equals(GColor.BLACK)) {
			codePoint.append("}");
		}
		codePoint.append("}\n");
		endBeamer(codePoint);
	}

	@Override
	protected void drawAngle(GeoAngle geo) {
		int arcSize = geo.getArcSize();
		AlgoElement algo = geo.getParentAlgorithm();
		GeoPointND vertex, point;
		GeoVectorND v;
		GeoLine line, line2;
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
			if (vertex == null)
				vertex = tempPoint;
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
				if (vertex == null)
					vertex = tempPoint;
				vertex.getInhomCoords(m);
			} else if (vec instanceof GeoPoint) {
				point = (GeoPoint) vec;
				vertex = tempPoint;
				// vertex
				vertex.getInhomCoords(m);
			}
			firstVec[0] = 1;
			firstVec[1] = 0;

		}
		tempPoint.remove(); // Michael Borcherds 2008-08-20

		double angSt = Math.atan2(firstVec[1], firstVec[0]);

		// Michael Borcherds 2007-10-21 BEGIN
		// double angExt = geo.getValue();
		double angExt = geo.getRawAngle();
		if (angExt > Math.PI * 2)
			angExt -= Math.PI * 2;

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

		// if (geo.changedReflexAngle()) {
		// angSt = angSt - angExt;
		// }
		// Michael Borcherds 2007-10-21 END

		angExt += angSt;
		double r = arcSize / euclidianView.getXscale();
		// if angle=90degrees and decoration=little square
		if (Kernel.isEqual(geo.getValue(), Kernel.PI_HALF)
				&& geo.isEmphasizeRightAngle()
				&& euclidianView.getRightAngleStyle() == EuclidianStyleConstants.RIGHT_ANGLE_STYLE_SQUARE) {
			r = r / Math.sqrt(2);
			double[] x = new double[8];
			x[0] = m[0] + r * Math.cos(angSt);
			x[1] = m[1] + r * Math.sin(angSt);
			x[2] = m[0] + r * Math.sqrt(2)
					* Math.cos(angSt + Kernel.PI_HALF / 2);
			x[3] = m[1] + r * Math.sqrt(2)
					* Math.sin(angSt + Kernel.PI_HALF / 2);
			x[4] = m[0] + r * Math.cos(angSt + Kernel.PI_HALF);
			x[5] = m[1] + r * Math.sin(angSt + Kernel.PI_HALF);
			x[6] = m[0];
			x[7] = m[1];

			// command: \pspolygon[par](x0,y0)....(xn,yn)
			startBeamer(codeFilledObject);
			codeFilledObject.append("\\pspolygon");
			codeFilledObject.append(lineOptionCode(geo, true));
			for (int i = 0; i < 4; i++) {
				codeFilledObject.append("(");
				codeFilledObject.append(format(x[2 * i]));
				codeFilledObject.append(",");
				codeFilledObject.append(format(x[2 * i + 1]));
				codeFilledObject.append(")");
			}
			codeFilledObject.append("\n");
			endBeamer(codeFilledObject);
		}
		// draw arc for the angle
		else {
			// set arc in real world coords
			GColor geocolor = geo.getObjectColor();
			startBeamer(code);
			if (!geocolor.equals(GColor.BLACK)) {
				code.append("\\pscustom");
				code.append(lineOptionCode(geo, true));
				code.append("{\n");
			}
			code.append("\\parametricplot{");
			code.append(angSt);
			code.append("}{");
			code.append(angExt);
			code.append("}{");
			code.append(format(r));
			code.append("*cos(t)+");
			code.append(format(m[0]));
			code.append("|");
			code.append(format(r));
			code.append("*sin(t)+");
			code.append(format(m[1]));
			code.append("}\n");
			if (!geocolor.equals(GColor.BLACK)) {
				code.append("\\lineto(");
				code.append(format(m[0]));
				code.append(",");
				code.append(format(m[1]));
				code.append(")\\closepath}\n");
			}
			endBeamer(code);
			/*
			 * startBeamer(code); code.append("\\pscustom");
			 * code.append(LineOptionCode(geo, true));
			 * code.append("{\\parametricplot{"); code.append(angSt);
			 * code.append("}{"); code.append(angExt); code.append("}{");
			 * code.append(format(r)); code.append("*cos(t)+");
			 * code.append(format(m[0])); code.append("|");
			 * code.append(format(r)); code.append("*sin(t)+");
			 * code.append(format(m[1])); code.append("}");
			 * code.append("\\lineto("); code.append(format(m[0]));
			 * code.append(","); code.append(format(m[1]));
			 * code.append(")\\closepath}\n"); endBeamer(code);
			 */
			// draw the dot if angle= 90 and decoration=dot
			if (Kernel.isEqual(geo.getValue(), Kernel.PI_HALF)
					&& geo.isEmphasizeRightAngle()
					&& euclidianView.getRightAngleStyle() == EuclidianStyleConstants.RIGHT_ANGLE_STYLE_DOT) {
				double diameter = geo.getLineThickness() / euclidianView.getXscale();
				double radius = arcSize / euclidianView.getXscale() / 1.7;
				double labelAngle = (angSt + angExt) / 2.0;
				double x1 = m[0] + radius * Math.cos(labelAngle);
				double x2 = m[1] + radius * Math.sin(labelAngle);
				// draw an ellipse
				// command: \psellipse(0,0)(20.81,-10.81)}
				startBeamer(code);
				code.append("\\psellipse*");
				code.append(lineOptionCode(geo, true));
				code.append("(");
				code.append(format(x1));
				code.append(",");
				code.append(format(x2));
				code.append(")(");
				code.append(format(diameter));
				code.append(",");
				code.append(format(diameter));
				code.append(")\n");
				endBeamer(code);
			}
		}
		int deco = geo.getDecorationType();
		if (deco != GeoElement.DECORATION_NONE) {
			startBeamer(code);
			markAngle(geo, r, m, angSt, angExt);
			endBeamer(code);
		}
	}

	@Override
	protected void drawArrowArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd, double r, boolean anticlockwise) {
		// The arrow head goes away from the line.
		// Arrow Winset=0.25, see PStricks spec for arrows
		double arrowHeight = (geo.getLineThickness() * 0.8 + 3) * 1.4 * 3 / 4;
		double angle = Math.asin(arrowHeight / 2 / euclidianView.getXscale()
				/ r);
		angEnd = angEnd - angle;
		startBeamer(code);
		code.append("\\psellipticarc");
		code.append(lineOptionCode(geo, false));
		if (anticlockwise)
			code.append("{->}(");
		else
			code.append("{<-}(");
		code.append(format(vertex[0]));
		code.append(",");
		code.append(format(vertex[1]));
		code.append(")(");
		code.append(format(r));
		code.append(",");
		code.append(format(r));
		code.append("){");
		code.append(format(Math.toDegrees(angSt)));
		code.append("}{");
		code.append(format(Math.toDegrees(angEnd)));
		code.append("}\n");
		endBeamer(code);
	}

	@Override
	protected void drawArc(GeoAngle geo, double[] vertex, double angSt,
			double angEnd, double r) {
		if (isBeamer)
			code.append("  ");
		code.append("\\parametricplot");
		code.append(lineOptionCode(geo, false));
		code.append("{");
		code.append(angSt);
		code.append("}{");
		code.append(angEnd);
		code.append("}{");
		code.append(format(r));
		code.append("*cos(t)+");
		code.append(format(vertex[0]));
		code.append("|");
		code.append(format(r));
		code.append("*sin(t)+");
		code.append(format(vertex[1]));
		code.append("}\n");
	}

	@Override
	protected void drawTick(GeoAngle geo, double[] vertex, double angle) {
		angle = -angle;
		double radius = geo.getArcSize();
		double diff = 2.5 + geo.getLineThickness() / 4d;
		double x1 = euclidianView.toRealWorldCoordX(vertex[0] + (radius - diff)
				* Math.cos(angle));
		double x2 = euclidianView.toRealWorldCoordX(vertex[0] + (radius + diff)
				* Math.cos(angle));
		double y1 = euclidianView.toRealWorldCoordY(vertex[1] + (radius - diff)
				* Math.sin(angle) * euclidianView.getScaleRatio());
		double y2 = euclidianView.toRealWorldCoordY(vertex[1] + (radius + diff)
				* Math.sin(angle) * euclidianView.getScaleRatio());
		if (isBeamer)
			code.append("  ");
		code.append("\\psline");
		code.append(lineOptionCode(geo, false));
		code.append("(");
		code.append(format(x1));
		code.append(",");
		code.append(format(y1));
		code.append(")(");
		code.append(format(x2));
		code.append(",");
		code.append(format(y2));
		code.append(")\n");

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
			width = horizontal ? width / euclidianView.getXscale() : width
					/ euclidianView.getYscale();
		}
		// create point for slider
		GeoPoint geoPoint = new GeoPoint(construction);
		geoPoint.setObjColor(geo.getObjectColor());
		String label = StringUtil
				.toLaTeXString(geo.getLabelDescription(), true);
		geoPoint.setLabel(label);
		double param = (value - min) / (max - min);
		geoPoint.setPointSize(2 + (geo.getLineThickness() + 1) / 3);
		geoPoint.setLabelVisible(geo.isLabelVisible());
		if (horizontal)
			geoPoint.setCoords(x + width * param, y, 1.0);
		else
			geoPoint.setCoords(x, y + width * param, 1.0);
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

		geoPoint.remove(); // Michael Borcherds 2008-08-20
		startBeamer(code);
		// draw Line for Slider
		code.append("\\psline");
		code.append(lineOptionCode(geo, true));
		code.append("(");
		code.append(format(x));
		code.append(",");
		code.append(format(y));
		code.append(")(");
		if (horizontal)
			x += width;
		else
			y += width;
		code.append(format(x));
		code.append(",");
		code.append(format(y));
		code.append(")\n");
		endBeamer(code);
	}

	@Override
	protected void drawPolygon(GeoPolygon geo) {
		// command: \pspolygon[par](x0,y0)....(xn,yn)
		float alpha = geo.getAlphaValue();
		if (alpha == 0.0f && geo.getFillType() == FillType.IMAGE)
			return;
		startBeamer(codeFilledObject);
		codeFilledObject.append("\\pspolygon");
		codeFilledObject.append(lineOptionCode(geo, true));
		GeoPointND[] points = geo.getPoints();
		for (int i = 0; i < points.length; i++) {
			Coords coords = points[i].getCoordsInD2();
			double x = coords.getX(), y = coords.getY(), z = coords.getZ();
			x = x / z;
			y = y / z;
			codeFilledObject.append("(");
			codeFilledObject.append(format(x));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y));
			codeFilledObject.append(")");
		}
		codeFilledObject.append("\n");
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawText(GeoText geo) {
		boolean isLatex = geo.isLaTeX();
		String st = geo.getTextString();
		GColor geocolor = geo.getObjectColor();
		int style = geo.getFontStyle();
		int size = (int) (geo.getFontSizeMultiplier() * getApp().getFontSize());
		GeoPoint gp;
		double x, y;
		// compute location of text
		if (geo.isAbsoluteScreenLocActive()) {
			x = geo.getAbsoluteScreenLocX();
			y = geo.getAbsoluteScreenLocY();
		} else {
			gp = (GeoPoint) geo.getStartPoint();
			if (gp == null) {
				x = (int) euclidianView.getXZero();
				y = (int) euclidianView.getYZero();
			} else {
				if (!gp.isDefined()) {
					return;
				}
				x = euclidianView.toScreenCoordX(gp.inhomX);
				y = euclidianView.toScreenCoordY(gp.inhomY);
			}
			x += geo.labelOffsetX;
			y += geo.labelOffsetY;
		}
		x = euclidianView.toRealWorldCoordX(x);
		y = euclidianView.toRealWorldCoordY(y
				- euclidianView.getFont().getSize());
		int id = st.indexOf("\n");
		startBeamer(code);
		// One line
		if (id == -1) {
			code.append("\\rput[tl](");
			code.append(format(x));
			code.append(",");
			code.append(format(y));
			code.append("){");
			addText(st, isLatex, style, geocolor);
			code.append("}\n");
		}
		// MultiLine
		else {
			StringBuilder sb = new StringBuilder();
			GStringTokenizer stk = new GStringTokenizer(st, '\n');
			int width = 0;
			GFont font = AwtFactory.getPrototype().newFont(
					geo.isSerifFont() ? "Serif" : "SansSerif", style, size);
			while (stk.hasMoreTokens()) {
				String line = stk.nextToken();
				width = Math.max(width,
						(int) Math.ceil(StringUtil.getPrototype()
								.estimateLength(line, font)));
				sb.append(line);
				if (stk.hasMoreTokens())
					sb.append(" \\\\ ");
			}
			code.append("\\rput[lt](");
			code.append(format(x));
			code.append(",");
			code.append(format(y));
			code.append("){\\parbox{");
			code.append(format(width * (xmax - xmin) * xunit
					/ euclidianView.getWidth() + 1));
			code.append(" cm}{");
			addText(new String(sb), isLatex, style, geocolor);
			code.append("}}\n");
		}
		endBeamer(code);
	}

	@Override
	protected void drawGeoConicPart(GeoConicPart geo) {
		double r1 = geo.getHalfAxes()[0];
		double r2 = geo.getHalfAxes()[1];
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
		startBeamer(code);
		// Sector command:
		// \pscustom[options]{\parametricplot{startAngle}{endAngle}{x+r*cos(t),y+r*sin(t)}\lineto(x,y)\closepath}
		if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
			code.append("\\pscustom");
			code.append(lineOptionCode(geo, true));
			code.append("{\\parametricplot{");
		} else if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_ARC) {
			code.append("\\parametricplot");
			code.append(lineOptionCode(geo, true));
			code.append("{");
		}
		if (startAngle > endAngle) {
			startAngle -= Math.PI * 2;
		}
		StringBuilder sb1 = new StringBuilder();
		sb1.append(format(r1));
		sb1.append("*cos(t)");
		StringBuilder sb2 = new StringBuilder();
		sb2.append(format(r2));
		sb2.append("*sin(t)");
		code.append(startAngle);
		code.append("}{");
		code.append(endAngle);
		code.append("}{");
		code.append(format(m11));
		code.append("*");
		code.append(sb1);
		code.append("+");
		code.append(format(m12));
		code.append("*");
		code.append(sb2);
		code.append("+");
		code.append(format(tx));
		code.append("|");
		code.append(format(m21));
		code.append("*");
		code.append(sb1);
		code.append("+");
		code.append(format(m22));
		code.append("*");
		code.append(sb2);
		code.append("+");
		code.append(format(ty));
		code.append("}");
		if (geo.getConicPartType() == GeoConicNDConstants.CONIC_PART_SECTOR) {
			code.append("\\lineto(");
			code.append(format(tx));
			code.append(",");
			code.append(format(ty));
			code.append(")\\closepath}");
		}
		code.append("\n");
		// }
		endBeamer(code);
	}

	@Override
	protected void drawSingleCurveCartesian(GeoCurveCartesian geo,
			boolean trasparency) {
		double start = geo.getMinParameter();
		double end = geo.getMaxParameter();
		// boolean isClosed=geo.isClosedPath();
		String fx = geo.getFunX(getStringTemplate());
		fx = killSpace(StringUtil.toLaTeXString(fx, true));
		String fy = geo.getFunY(getStringTemplate());
		fy = killSpace(StringUtil.toLaTeXString(fy, true));
		String variable = geo.getVarString(getStringTemplate());
		boolean warning = !(variable.equals("t"));
		startBeamer(code);
		if (warning)
			code.append("% WARNING: You have to use the special variable t in parametric plot");
		code.append("\\parametricplot");
		code.append(lineOptionCode(geo, trasparency));
		int index = code.lastIndexOf("]");
		if (index == code.length() - 1) {
			code.deleteCharAt(index);
			code.append("]{");
		} else
			code.append("{");
		code.append(start);
		code.append("}{");
		code.append(end);
		code.append("}{");
		code.append(fx);
		code.append("|");
		code.append(fy);
		code.append("}\n");
		endBeamer(code);
	}

	@Override
	protected void drawFunction(GeoFunction geo) {
		drawFunction(geo, false, null);
	}

	protected void drawFunction(GeoFunction geo, boolean integral,
			GeoNumeric geo1) {
		// line contains the row that define function
		StringBuilder line = new StringBuilder();
		Function f = geo.getFunction();
		if (null == f)
			return;
		String value = f.toValueString(getStringTemplate());
		value = killSpace(StringUtil.toLaTeXString(value, true));
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
			if (xrangemin == b)
				break;
			xrangemax = maxDefinedValue(geo, xrangemin, b);
			// Application.debug("xrangemax "+xrangemax);
			startBeamer(code);
			line.append("\\psplot");
			// loc contains style, size etc.
			// is used in the case of non latex function, to assign lines style
			String liopco = lineOptionCode(geo, true);
			int index = liopco.lastIndexOf("]");
			if (index != -1 && index == liopco.length() - 1) {
				liopco = liopco.substring(0, liopco.length() - 1);
				liopco += ",plotpoints=200]{";
			} else
				liopco += "[plotpoints=200]{";
			line.append(liopco);

			line.append(xrangemin);
			line.append("}{");
			line.append(xrangemax);
			line.append("}{");
			line.append(value);
			line.append("}\n");
			xrangemax += PRECISION_XRANGE_FUNCTION;
			String s = line.toString();

			// if is'n latex function draws the function as a set of lines
			if (!isLatexFunction(f.toValueString(StringTemplate.noLocalDefault))) {
				liopco = liopco.replace(",plotpoints=200]{", "]");
				liopco = liopco.replace("[plotpoints=200]{", "");
				String pre = "";
				String post = "";
				String template = "\\psline" + liopco + "(%0,%1)(%2,%3)\n";
				StringBuilder lineBuilder = drawNoLatexFunction(geo, xrangemax,
						xrangemin, 200, template);
				s = lineBuilder.toString();
				if (integral) {
					code.append(lineOptionCode(geo1, true));
					pre = "{";
					String end = s.substring(s.lastIndexOf("("));
					post = "(" + b + "," + f.evaluate(b) + ")(" + b + ",0)\n";
					post += "(" + b + ",0)(" + a + ",0)\n";
					post += "(" + a + ",0)(" + a + "," + f.evaluate(a) + ")}\n";

				}
				s = pre + s + post;
				code.append(s);

			} else {
				code.append(line);
			}
			a = xrangemax;
			endBeamer(code);
		}
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
				if (!operand)
					space = true;
				else {
					space = false;
					operand = false;
				}
			} else {
				if (space && !name.contains("If"))
					sb.append("*");
				sb.append(c);
				space = false;
				operand = false;
			}
		}

		// for exponential in new Geogbra version.
		renameFunc(sb, Unicode.EULER_STRING, "2.718281828");

		renameFunc(sb, "\\pi", "PI");
		return new String(sb);
	}

	@Override
	protected void drawGeoVector(GeoVector geo) {
		GeoPointND pointStart = geo.getStartPoint();
		String x1, y1;
		if (null == pointStart) {
			x1 = "0";
			y1 = "0";
		} else {
			Coords c = pointStart.getCoords();
			x1 = format(c.getX() / c.getZ());
			y1 = format(c.getY() / c.getZ());
		}
		double[] coord = new double[3];
		geo.getCoords(coord);
		String x2 = format(coord[0]
				+ kernel.getAlgebraProcessor().evaluateToDouble(x1));
		String y2 = format(coord[1]
				+ kernel.getAlgebraProcessor().evaluateToDouble(y1));
		startBeamer(code);
		code.append("\\psline");
		code.append(lineOptionCode(geo, true));
		code.append("{->}(");
		code.append(x1);
		code.append(",");
		code.append(y1);
		code.append(")(");
		code.append(x2);
		code.append(",");
		code.append(y2);
		code.append(")\n");
		endBeamer(code);
	}

	private void drawCircle(GeoConic geo) {
		StringBuilder s = new StringBuilder();
		if (xunit == yunit) {
			// draw a circle
			// command: \pscircle[options](x_center,y_center){Radius)}
			double x = geo.getTranslationVector().getX();
			double y = geo.getTranslationVector().getY();
			double r = geo.getHalfAxes()[0];
			startBeamer(s);
			s.append("\\pscircle");
			s.append(lineOptionCode(geo, true));
			s.append("(");
			s.append(format(x));
			s.append(",");
			s.append(format(y));
			s.append("){");
			String tmpr = format(r * xunit);
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpr) != 0)
				s.append(tmpr);
			else
				s.append(r);
			s.append("}\n");
			endBeamer(s);
		} else {
			// draw an ellipse
			// command: \psellipse(0,0)(20.81,-10.81)}
			double x1 = geo.getTranslationVector().getX();
			double y1 = geo.getTranslationVector().getY();
			double r1 = geo.getHalfAxes()[0];
			double r2 = geo.getHalfAxes()[1];
			startBeamer(s);
			s.append("\\psellipse");
			s.append(lineOptionCode(geo, true));
			s.append("(");
			s.append(format(x1));
			s.append(",");
			s.append(format(y1));
			s.append(")(");
			s.append(format(r1));
			s.append(",");
			s.append(format(r2));
			s.append(")\n");
			endBeamer(s);
		}
		if (geo.getAlphaValue() > 0.0f)
			codeFilledObject.append(s);
		else
			code.append(s);
	}

	@Override
	protected void drawGeoConic(GeoConic geo) {
		switch (geo.getType()) {
		// if conic is a circle
		case GeoConicNDConstants.CONIC_CIRCLE:
			drawCircle(geo);
			break;
		// if conic is an ellipse
		case GeoConicNDConstants.CONIC_ELLIPSE:
			// command:
			// \rput{angle}(x_center,y_center){\psellipse(0,0)(20.81,-10.81)}
			GAffineTransform at = geo.getAffineTransform();
			double eigenvecX = at.getScaleX();
			double eigenvecY = at.getShearY();
			double x1 = geo.getTranslationVector().getX();
			double y1 = geo.getTranslationVector().getY();
			double r1 = geo.getHalfAxes()[0];
			double r2 = geo.getHalfAxes()[1];
			double angle = Math.toDegrees(Math.atan2(eigenvecY, eigenvecX));
			startBeamer(code);
			code.append("\\rput{");
			code.append(format(angle));
			code.append("}(");
			code.append(format(x1));
			code.append(",");
			code.append(format(y1));
			code.append("){\\psellipse");
			code.append(lineOptionCode(geo, true));
			code.append("(0,0)(");
			code.append(format(r1));
			code.append(",");
			code.append(format(r2));
			code.append(")}\n");
			endBeamer(code);
			break;

		// if conic is a parabola
		case GeoConicNDConstants.CONIC_PARABOLA:
			// command:
			// \rput{angle_rotation}(x_origin,y_origin){\pstplot{xmin}{xmax}{x^2/2/p}}

			// parameter of the parabola
			double p = geo.p;
			at = geo.getAffineTransform();
			// first eigenvec
			eigenvecX = at.getScaleX();
			eigenvecY = at.getShearY();
			// vertex
			x1 = geo.getTranslationVector().getX();
			y1 = geo.getTranslationVector().getY();

			// calculate the x range to draw the parabola
			double x0 = Math.max(Math.abs(x1 - xmin), Math.abs(x1 - xmax));
			x0 = Math.max(x0, Math.abs(y1 - ymin));
			x0 = Math.max(x0, Math.abs(y1 - ymax));

			// avoid sqrt by choosing x = k*p with
			// i = 2*k is quadratic number
			// make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
			x0 = 4 * x0 / p;
			int i = 4;
			int k2 = 16;
			while (k2 < x0) {
				i += 2;
				k2 = i * i;
			}
			// x0 = k2/2 * p; // x = k*p
			x0 = i * p; // y = sqrt(2k p^2) = i p
			angle = Math.toDegrees(Math.atan2(eigenvecY, eigenvecX)) - 90;
			startBeamer(code);
			code.append("\\rput{");
			code.append(format(angle));
			code.append("}(");
			code.append(format(x1));
			code.append(",");
			code.append(format(y1));
			code.append("){\\psplot");
			code.append(lineOptionCode(geo, true));
			code.append("{");
			code.append(format(-x0));
			code.append("}{");
			code.append(format(x0));
			code.append("}");
			code.append("{x^2/2/");
			code.append(format(p));
			code.append("}}\n");
			endBeamer(code);
			break;
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			// command:
			// \rput{angle_rotation}(x_origin,y_origin){\parametric{-1}{1}{a(1+t^2)/(1-t^2)|2bt/(1-t^2)}
			at = geo.getAffineTransform();
			eigenvecX = at.getScaleX();
			eigenvecY = at.getShearY();
			x1 = geo.getTranslationVector().getX();
			y1 = geo.getTranslationVector().getY();
			r1 = geo.getHalfAxes()[0];
			r2 = geo.getHalfAxes()[1];
			angle = Math.toDegrees(Math.atan2(eigenvecY, eigenvecX));
			startBeamer(code);
			code.append("\\rput{");
			code.append(format(angle));
			code.append("}(");
			code.append(format(x1));
			code.append(",");
			code.append(format(y1));
			code.append("){\\parametricplot");
			code.append(lineOptionCode(geo, true));
			code.append("{-0.99}{0.99}{");
			code.append(format(r1));
			code.append("*(1+t^2)/(1-t^2)|");
			code.append(format(r2));
			code.append("*2*t/(1-t^2)");
			code.append("}}\n");

			code.append("\\rput{");
			code.append(format(angle));
			code.append("}(");
			code.append(format(x1));
			code.append(",");
			code.append(format(y1));
			code.append("){\\parametricplot");
			code.append(lineOptionCode(geo, true));
			code.append("{-0.99}{0.99}{");
			code.append(format(r1));
			code.append("*(-1-t^2)/(1-t^2)|");
			code.append(format(r2));
			code.append("*(-2)*t/(1-t^2)");
			code.append("}}\n");
			endBeamer(code);
			break;
		}
	}

	@Override
	protected void drawGeoPoint(GeoPoint gp) {
		if (frame.getExportPointSymbol()) {
			startBeamer(codePoint);
			double x = gp.getX();
			double y = gp.getY();
			double z = gp.getZ();
			x = x / z;
			y = y / z;
			codePoint.append("\\psdots");
			PointOptionCode(gp);
			codePoint.append("(");
			codePoint.append(format(x));
			codePoint.append(",");
			codePoint.append(format(y));
			codePoint.append(")\n");
			endBeamer(codePoint);
		}
		// In case of trimmed intersection
		if (gp.getShowTrimmedIntersectionLines()) {
			AlgoElement algo = gp.getParentAlgorithm();

			if (algo instanceof AlgoIntersectAbstract) {
				GeoElement[] geos = algo.getInput();

				double x1 = euclidianView.toScreenCoordXd(gp.getInhomX());
				double y1 = euclidianView.toScreenCoordYd(gp.getInhomY());
				double x2 = euclidianView.toScreenCoordXd(gp.getInhomX()) + 30;
				double y2 = euclidianView.toScreenCoordYd(gp.getInhomY()) + 30;
				x1 = euclidianView.toRealWorldCoordX(x1);
				x2 = euclidianView.toRealWorldCoordX(x2);
				y1 = euclidianView.toRealWorldCoordY(y1);
				y2 = euclidianView.toRealWorldCoordY(y2);
				double r1 = Math.abs(x2 - x1);
				double r2 = Math.abs(y2 - y1);
				StringBuilder s = new StringBuilder(
						"\\psclip{\\psellipse[linestyle=none](");
				s.append(format(x1));
				s.append(",");
				s.append(format(y1));
				s.append(")(");
				s.append(format(r1));
				s.append(",");
				s.append(format(r2));
				s.append(")}\n");

				String end = "\\endpsclip\n";
				boolean fill1 = false;
				boolean draw = !geos[0].isEuclidianVisible();
				if (draw) {
					fill1 = geos[0].isFillable()
							&& geos[0].getAlphaValue() > 0.0f;
					if (fill1)
						codeFilledObject.append(s);
					else
						code.append(s);
					drawGeoElement(geos[0], false, true);
				}
				if (geos.length > 1 && !geos[1].isEuclidianVisible()) {
					boolean fill2 = geos[1].isFillable()
							&& (geos[1].getAlphaValue() > 0.0f);
					if (draw) {
						if (fill1 == fill2) {
							drawGeoElement(geos[1], false, true);
							if (fill1)
								codeFilledObject.append(end);
							else
								code.append(end);
						} else {
							if (fill1)
								codeFilledObject.append(end);
							else
								code.append(end);
							if (fill2)
								codeFilledObject.append(s);
							else
								code.append(s);
							drawGeoElement(geos[1], false, true);
							if (fill2)
								codeFilledObject.append(end);
							else
								code.append(end);
						}
					} else {
						if (fill2)
							codeFilledObject.append(s);
						else
							code.append(s);
						drawGeoElement(geos[1], false, true);
						if (fill2)
							codeFilledObject.append(end);
						else
							code.append(end);
					}
				} else if (draw) {
					if (fill1)
						codeFilledObject.append(end);
					else
						code.append(end);
				}
			}
		}
	}

	@Override
	protected void drawGeoLine(GeoLine geo) {
		double x = geo.getX();
		double y = geo.getY();
		double z = geo.getZ();
		startBeamer(code);
		if (y != 0)
			code.append("\\psplot");
		else
			code.append("\\psline");
		code.append(lineOptionCode(geo, true));
		if (y != 0) {
			code.append("{");
			code.append(format(xmin));
			code.append("}{");
			code.append(format(xmax));
			code.append("}{(-");
			code.append(format(z));
			code.append("-");
			code.append(format(x));
			code.append("*x)/");
			String tmpy = format(y);
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpy) != 0)
				code.append(tmpy);
			else
				code.append(y);
			code.append("}\n");
		} else {
			String s = format(-z / x);
			code.append("(");
			code.append(s);
			code.append(",");
			code.append(format(ymin));
			code.append(")(");
			code.append(s);
			code.append(",");
			code.append(format(ymax));
			code.append(")\n");
		}
		endBeamer(code);
	}

	@Override
	protected void drawGeoSegment(GeoSegment geo) {
		double[] A = new double[2];
		double[] B = new double[2];
		GeoPoint pointStart = geo.getStartPoint();
		GeoPoint pointEnd = geo.getEndPoint();
		pointStart.getInhomCoords(A);
		pointEnd.getInhomCoords(B);
		String x1 = format(A[0]);
		String y1 = format(A[1]);
		String x2 = format(B[0]);
		String y2 = format(B[1]);
		startBeamer(code);
		code.append("\\psline");
		code.append(lineOptionCode(geo, true));
		code.append("(");
		code.append(x1);
		code.append(",");
		code.append(y1);
		code.append(")(");
		code.append(x2);
		code.append(",");
		code.append(y2);
		code.append(")\n");
		int deco = geo.getDecorationType();
		if (deco != GeoElement.DECORATION_NONE)
			mark(A, B, deco, geo);
		endBeamer(code);
	}

	@Override
	protected void drawLine(double x1, double y1, double x2, double y2,
			GeoElement geo) {
		String sx1 = format(x1);
		String sy1 = format(y1);
		String sx2 = format(x2);
		String sy2 = format(y2);
		if (isBeamer)
			code.append("  ");
		code.append("\\psline");
		code.append(lineOptionCode(geo, true));
		code.append("(");
		code.append(sx1);
		code.append(",");
		code.append(sy1);
		code.append(")(");
		code.append(sx2);
		code.append(",");
		code.append(sy2);
		code.append(")\n");
	}

	@Override
	protected void drawGeoRay(GeoRay geo) {
		GeoPoint pointStart = geo.getStartPoint();
		double x1 = pointStart.getX();
		double z1 = pointStart.getZ();
		x1 = x1 / z1;
		String y1 = format(pointStart.getY() / z1);

		double x = geo.getX();
		double y = geo.getY();
		double z = geo.getZ();
		startBeamer(code);
		if (y != 0)
			code.append("\\psplot");
		else
			code.append("\\psline");
		code.append(lineOptionCode(geo, true));
		double inf = xmin, sup = xmax;
		if (y > 0) {
			inf = x1;
		} else {
			sup = x1;
		}
		if (y != 0) {
			code.append("{");
			code.append(format(inf));
			code.append("}{");
			code.append(format(sup));
			code.append("}{(-");
			code.append(format(z));
			code.append("-");
			code.append(format(x));
			code.append("*x)/");
			String tmpy = format(y);
			if (kernel.getAlgebraProcessor().evaluateToDouble(tmpy) != 0)
				code.append(tmpy);
			else
				code.append(y);
			code.append("}\n");
		} else {
			if (-x > 0)
				sup = ymax;
			else
				sup = ymin;
			code.append("(");
			code.append(format(x1));
			code.append(",");
			code.append(y1);
			code.append(")(");
			code.append(format(x1));
			code.append(",");
			code.append(format(sup));
			code.append(")\n");
		}
		endBeamer(code);
	}

	private void initUnitAndVariable() {
		// Initaialze uits, dot style, dot size ....
		codeBeginPic.append("\\psset{xunit=");
		codeBeginPic.append(sci2dec(xunit));
		codeBeginPic.append("cm,yunit=");
		codeBeginPic.append(sci2dec(yunit));
		codeBeginPic
				.append("cm,algebraic=true,dimen=middle,dotstyle=o,dotsize=");
		codeBeginPic.append(EuclidianStyleConstants.DEFAULT_POINT_SIZE);
		codeBeginPic.append("pt 0");
		codeBeginPic.append(",linewidth=");
		codeBeginPic
				.append(format(EuclidianStyleConstants.DEFAULT_LINE_THICKNESS / 2 * 0.8));
		codeBeginPic.append("pt,arrowsize=3pt 2,arrowinset=0.25}\n");
	}

	// if label is Visible, draw it
	@Override
	protected void drawLabel(GeoElement geo, DrawableND drawGeo) {
		try {
			if (geo.isLabelVisible()) {
				String name = geo.getLabelDescription();
				if (geo.getLabelMode() == GeoElement.LABEL_CAPTION) {
					String nameSym = name;
					for (int i = 0; i < name.length(); i++) {
						char uCode = name.charAt(i);
						if (UnicodeTeX.getMap().containsKey(uCode)) {
							nameSym = nameSym.replaceAll("\\" + uCode,
									"\\$\\\\" + UnicodeTeX.getMap().get(uCode)
											+ "\\$");
						}
					}
					nameSym = nameSym.replace("$\\euro$", "\\euro");
					name = nameSym;
					if (!eurosym && name.contains("\\euro"))
						codePreamble.append("\\usepackage{eurosym}\n");
					if (name.contains("_")) {
						name = "$" + name + "$";
					}
				} else {
					name = "$"
							+ StringUtil.toLaTeXString(
									geo.getLabelDescription(), true) + "$";
				}

				if (name.indexOf(Unicode.DEGREE) != -1) {
					name = name.replaceAll(Unicode.DEGREE,
							"\\\\textrm{\\\\degre}");
					if (codePreamble.indexOf("\\degre") == -1)
						codePreamble
								.append("\\newcommand{\\degre}{\\ensuremath{^\\circ}}\n");
				}
				if (null == drawGeo)
					drawGeo = euclidianView.getDrawableFor(geo);
				double xLabel = drawGeo.getxLabel();
				double yLabel = drawGeo.getyLabel();
				xLabel = euclidianView.toRealWorldCoordX(Math.round(xLabel));
				yLabel = euclidianView.toRealWorldCoordY(Math.round(yLabel));

				GColor geocolor = geo.getObjectColor();
				startBeamer(codePoint);
				codePoint.append("\\rput[bl](");
				codePoint.append(format(xLabel));
				codePoint.append(",");
				codePoint.append(format(yLabel));
				codePoint.append("){");
				if (!geocolor.equals(GColor.BLACK)) {
					codePoint.append("\\");
					colorCode(geocolor, codePoint);
					codePoint.append("{");
				}
				codePoint.append(name);
				if (!geocolor.equals(GColor.BLACK)) {
					codePoint.append("}");
				}
				codePoint.append("}\n");
				endBeamer(codePoint);
			}
		}
		// For GeoElement that don't have a Label
		// For example (created with geoList)
		catch (NullPointerException e) {
		}
	}

	private void drawGrid() {

		GColor GridCol = euclidianView.getGridColor();
		double[] GridDist = euclidianView.getGridDistances();
		double myx = xmin;
		long truncx = (long) myx;
		double myy = ymin;
		long truncy = (long) myy;
		double RX = Math.abs(xmax - xmin) / GridDist[0] + 1;
		long repx = (long) RX;
		double RY = Math.abs(ymax - ymin) / GridDist[1] + 1;
		long repy = (long) RY;

		// My Grid eje y\multips(0,ymin)(0,griddisy){numero de
		// repeticiones}{\psline(xmin,0)(xmax,0)}
		codeBeginPic.append("\\multips(0,");
		codeBeginPic.append(truncy);
		codeBeginPic.append(")(0,");
		codeBeginPic.append(sci2dec(GridDist[1]));
		codeBeginPic.append("){");
		codeBeginPic.append(repy);
		codeBeginPic
				.append("}{\\psline[linestyle=dashed,linecap=1,dash=1.5pt 1.5pt,linewidth=0.4pt,linecolor=");
		colorCode(GridCol, codeBeginPic);
		codeBeginPic.append("]{c-c}(");
		codeBeginPic.append(format(xmin));
		codeBeginPic.append(",0)(");
		codeBeginPic.append(format(xmax));
		codeBeginPic.append(",0)}\n");
		// My Grid eje
		// x\multips(xmin,0)(griddisx,0){num}{\psline(0,ymin)(0,ymax)}
		codeBeginPic.append("\\multips(");
		codeBeginPic.append(truncx);
		codeBeginPic.append(",0)(");
		codeBeginPic.append(sci2dec(GridDist[0]));
		codeBeginPic.append(",0){");
		codeBeginPic.append(repx);
		codeBeginPic
				.append("}{\\psline[linestyle=dashed,linecap=1,dash=1.5pt 1.5pt,linewidth=0.4pt,linecolor=");
		colorCode(GridCol, codeBeginPic);
		codeBeginPic.append("]{c-c}(0,");
		codeBeginPic.append(format(ymin));
		codeBeginPic.append(")(0,");
		codeBeginPic.append(format(ymax));
		codeBeginPic.append(")}\n");
	}

	// Draw the grid
	/*
	 * private void drawGrid() { geogebra.common.awt.GColor GridCol =
	 * euclidianView.getGridColor(); double[] GridDist =
	 * euclidianView.getGridDistances(); // int
	 * GridLine=euclidianView.getGridLineStyle();
	 * 
	 * // Set Units for grid codeBeginPic.append("\\psset{xunit="); //
	 * Application.debug(GridDist[0]*xunit);
	 * codeBeginPic.append(sci2dec(GridDist[0] * xunit));
	 * codeBeginPic.append("cm,yunit="); codeBeginPic.append(sci2dec(GridDist[1]
	 * * yunit)); codeBeginPic.append("cm}\n");
	 * 
	 * // environment pspicture codeBeginPic.append("\\begin{pspicture*}(");
	 * codeBeginPic.append(format(xmin / GridDist[0]));
	 * codeBeginPic.append(","); codeBeginPic.append(format(ymin /
	 * GridDist[1])); codeBeginPic.append(")("); codeBeginPic.append(format(xmax
	 * / GridDist[0])); codeBeginPic.append(",");
	 * codeBeginPic.append(format(ymax / GridDist[1]));
	 * codeBeginPic.append(")\n");
	 * 
	 * // Draw Grid
	 * codeBeginPic.append("\\psgrid[subgriddiv=0,gridlabels=0,gridcolor=");
	 * ColorCode(GridCol, codeBeginPic); codeBeginPic.append("](0,0)(");
	 * codeBeginPic.append(format(xmin / GridDist[0]));
	 * codeBeginPic.append(","); codeBeginPic.append(format(ymin /
	 * GridDist[1])); codeBeginPic.append(")("); codeBeginPic.append(format(xmax
	 * / GridDist[0])); codeBeginPic.append(",");
	 * codeBeginPic.append(format(ymax / GridDist[1]));
	 * codeBeginPic.append(")\n");
	 * 
	 * // Set units for the pspicture initUnitAndVariable(); /*
	 * code.append("\\psset{xunit="); code.append(xunit);
	 * code.append("cm,yunit="); code.append(yunit); code.append("cm}\n");
	 */

	// }

	// Draw Axis
	private void drawAxis() {
		boolean[] positiveOnly = euclidianView.getPositiveAxes();
		boolean xAxis = euclidianView.getShowXaxis();
		boolean yAxis = euclidianView.getShowYaxis();
		// \psaxes[Dx=5,Dy=0.5]{->}(0,0)(-10.5,-0.4)(10.5,1.2)
		double Dx = euclidianView.getAxesNumberingDistances()[0];
		double Dy = euclidianView.getAxesNumberingDistances()[1];
		String[] label = euclidianView.getAxesLabels(false);
		String styleAx = "";
		if ((euclidianView.getAxesLineStyle() & EuclidianStyleConstants.AXES_BOLD) == EuclidianStyleConstants.AXES_BOLD) {
			styleAx = "linewidth=1.4pt,";
		}
		codeBeginPic.append("\\psaxes[" + styleAx
				+ "labelFontSize=\\scriptstyle,xAxis=");
		codeBeginPic.append(xAxis);
		codeBeginPic.append(",yAxis=");
		codeBeginPic.append(yAxis);
		codeBeginPic.append(',');
		boolean bx = euclidianView.getShowAxesNumbers()[0];
		boolean by = euclidianView.getShowAxesNumbers()[1];
		if (!bx && !by)
			codeBeginPic.append("labels=none,");
		else if (bx && !by)
			codeBeginPic.append("labels=x,");
		else if (!bx && by)
			codeBeginPic.append("labels=y,");
		codeBeginPic.append("Dx=");

		codeBeginPic.append(format(Dx));
		codeBeginPic.append(",Dy=");
		codeBeginPic.append(format(Dy));
		codeBeginPic.append(",ticksize=-2pt 0,subticks=2]{");

		styleAx = "";
		if ((euclidianView.getAxesLineStyle() & EuclidianStyleConstants.AXES_RIGHT_ARROW) == EuclidianStyleConstants.AXES_RIGHT_ARROW) {
			styleAx = "->";
		}
		if ((euclidianView.getAxesLineStyle() & EuclidianStyleConstants.AXES_LEFT_ARROW) == EuclidianStyleConstants.AXES_LEFT_ARROW) {
			styleAx = "<" + styleAx;
			;
		}
		codeBeginPic.append(styleAx);
		codeBeginPic.append("}(0,0)(");
		double assignMin = xmin;
		if (positiveOnly[0]) {
			assignMin = 0;
		}
		codeBeginPic.append(format(assignMin));
		codeBeginPic.append(",");
		assignMin = ymin;
		if (positiveOnly[0]) {
			assignMin = 0;
		}
		codeBeginPic.append(format(assignMin));
		codeBeginPic.append(")(");
		codeBeginPic.append(format(xmax));
		codeBeginPic.append(",");
		codeBeginPic.append(format(ymax));
		codeBeginPic.append(")");
		if (null != label[0] || null != label[1]) {
			codeBeginPic.append("[");
			if (null != label[0])
				codeBeginPic.append(label[0]);
			codeBeginPic.append(",140] [");
			if (null != label[1])
				codeBeginPic.append(label[1]);
			codeBeginPic.append(",-40]");
		}
		codeBeginPic.append("\n");
	}

	private void PointOptionCode(GeoPoint geo) {
		GColor dotcolor = geo.getObjectColor();
		int dotsize = geo.getPointSize();
		int dotstyle = geo.getPointStyle();
		if (dotstyle == -1) { // default
			dotstyle = EuclidianStyleConstants.POINT_STYLE_DOT;
		}
		boolean coma = false;
		boolean bracket = false;
		if (dotsize != EuclidianStyleConstants.DEFAULT_POINT_SIZE) {
			// coma needed
			coma = true;
			// bracket needed
			bracket = true;
			codePoint.append("[dotsize=");
			codePoint.append(dotsize);
			codePoint.append("pt 0");
		}

		if (dotstyle != EuclidianStyleConstants.POINT_STYLE_CIRCLE) {
			if (coma)
				codePoint.append(",");
			if (!bracket)
				codePoint.append("[");
			coma = true;
			bracket = true;
			codePoint.append("dotstyle=");
			switch (dotstyle) {
			case EuclidianStyleConstants.POINT_STYLE_CROSS:
				codePoint.append("x");
				break;
			case EuclidianStyleConstants.POINT_STYLE_DOT:
				codePoint.append("*");
				break;
			case EuclidianStyleConstants.POINT_STYLE_EMPTY_DIAMOND:
				codePoint.append("square,dotangle=45");
				break;
			case EuclidianStyleConstants.POINT_STYLE_FILLED_DIAMOND:
				codePoint.append("square*,dotangle=45");
				break;
			case EuclidianStyleConstants.POINT_STYLE_PLUS:
				codePoint.append("+");
				break;
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_EAST:
				codePoint.append("triangle*,dotangle=270");
				break;
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_NORTH:
				codePoint.append("triangle*");
				break;
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_SOUTH:
				codePoint.append("triangle*,dotangle=180");
				break;
			case EuclidianStyleConstants.POINT_STYLE_TRIANGLE_WEST:
				codePoint.append("triangle*,dotangle=90");
				break;
			default:
				codePoint.append("*");
				break;
			}
		}
		if (!dotcolor.equals(GColor.BLACK)) {
			if (coma)
				codePoint.append(",");
			if (!bracket)
				codePoint.append("[");
			bracket = true;
			codePoint.append("linecolor=");
			colorCode(dotcolor, codePoint);
		}
		if (bracket)
			codePoint.append("]");

	}

	public String lineOptionCode(GeoElement geo, boolean transparency) {
		StringBuilder sb = new StringBuilder();

		int linethickness = geo.getLineThickness();
		int linestyle = geo.getLineType();

		Info info = new Info(geo);

		boolean coma = false;
		boolean bracket = false;
		if (linethickness != EuclidianStyleConstants.DEFAULT_LINE_THICKNESS) {
			// coma needed
			coma = true;
			// bracket needed
			bracket = true;
			sb.append("[linewidth=");
			sb.append(format(linethickness / 2.0 * 0.8));
			sb.append("pt");
		}
		if (linestyle != EuclidianStyleConstants.DEFAULT_LINE_TYPE) {
			if (coma)
				sb.append(",");
			else
				coma = true;
			if (!bracket)
				sb.append("[");
			bracket = true;
			LinestyleCode(linestyle, sb);
		}
		if (!info.getLinecolor().equals(GColor.BLACK)) {
			if (coma)
				sb.append(",");
			else
				coma = true;
			if (!bracket)
				sb.append("[");
			bracket = true;
			sb.append("linecolor=");
			colorCode(info.getLinecolor(), sb);
		}
		// System.out.println(geo.isFillable()+" "+transparency+" "+geo.getObjectType());
		if (geo.isFillable() && transparency) {
			switch (info.getFillType()) {
			default:
			case STANDARD:
				if (info.getAlpha() > 0.0f) {
					if (coma)
						sb.append(",");
					else
						coma = true;
					if (!bracket)
						sb.append("[");
					bracket = true;
					sb.append("fillcolor=");
					colorCode(info.getLinecolor(), sb);
					sb.append(",fillstyle=solid,opacity=");
					sb.append(info.getAlpha());
				}
				break;
			case SYMBOLS:
			case CHESSBOARD:
			case HONEYCOMB:
			case BRICK:
			case WEAVING:
			case DOTTED:
				bracket = appendHatch(sb, info, ",fillstyle=dots*,hatchangle=",
						bracket, coma);
				break;
			case CROSSHATCHED:
				bracket = appendHatch(sb, info,
						",fillstyle=crosshatch,hatchangle=",
						bracket, coma);
				break;
			case HATCH:
				bracket = appendHatch(sb, info,
						",fillstyle=hlines,hatchangle=", bracket,
						coma);
				break;
			}
		}
		if (bracket)
			sb.append("]");
		return new String(sb);
	}

	private boolean appendHatch(StringBuilder sb, Info info, String style,
			boolean bracket, boolean coma) {
		if (coma) {
			sb.append(",");
		}

		if (!bracket)
			sb.append("[");

		sb.append("hatchcolor=");
		colorCode(info.getLinecolor(), sb);

		sb.append(style);
		sb.append(info.getAngle());
		sb.append(",hatchsep=");
		// double x0=euclidianView.toRealWorldCoordX(0);
		double y0 = euclidianView.toRealWorldCoordY(0);
		double y = euclidianView.toRealWorldCoordY(info.getY());
		sb.append(format(Math.abs((y - y0))));
		return true;
	}

	// Append the linestyle to PSTricks code
	private void LinestyleCode(int linestyle, StringBuilder sb) {
		switch (linestyle) {
		case EuclidianStyleConstants.LINE_TYPE_DOTTED:
			sb.append("linestyle=dotted");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_SHORT:
			// sb.append("linestyle=dashed,dash=4pt 4pt");
			sb.append("linestyle=dashed,dash=");
			int size = resizePt(4);
			sb.append(size);
			sb.append("pt ");
			sb.append(size);
			sb.append("pt");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_LONG:
			// sb.append("linestyle=dashed,dash=8pt 8pt");
			sb.append("linestyle=dashed,dash=");
			size = resizePt(8);
			sb.append(size);
			sb.append("pt ");
			sb.append(size);
			sb.append("pt");
			break;
		case EuclidianStyleConstants.LINE_TYPE_DASHED_DOTTED:
			// sb.append("linestyle=dashed,dash=1pt 4pt 8pt 4pt");
			sb.append("linestyle=dashed,dash=");
			int size1 = resizePt(1);
			int size2 = resizePt(4);
			int size3 = resizePt(8);
			sb.append(size1);
			sb.append("pt ");
			sb.append(size2);
			sb.append("pt ");
			sb.append(size3);
			sb.append("pt ");
			sb.append(size2);
			sb.append("pt ");
			break;
		}
	}

	// Append the name color to StringBuilder sb
	@Override
	protected void colorCode(GColor c, StringBuilder sb) {
		if (frame.isGrayscale()) {
			String colorname = "";
			int red = c.getRed();
			int green = c.getGreen();
			int blue = c.getBlue();
			int grayscale = (red + green + blue) / 3;
			c = GColor.newColor(grayscale, grayscale, grayscale);
			if (customColor.containsKey(c)) {
				colorname = customColor.get(c).toString();
			} else {
				colorname = createCustomColor(grayscale, grayscale, grayscale);
				codeBeginDoc.append("\\newrgbcolor{" + colorname + "}{"
						+ format(grayscale / 255d) + " "
						+ format(grayscale / 255d) + " "
						+ format(grayscale / 255d) + "}\n");
				customColor.put(c, colorname);
			}
			if (c.equals(GColor.BLACK))
				sb.append("black");
			else if (c.equals(GColor.DARK_GRAY))
				sb.append("darkgray");
			else if (c.equals(GColor.GRAY))
				sb.append("gray");
			else if (c.equals(GColor.LIGHT_GRAY))
				sb.append("lightgray");
			else if (c.equals(GColor.WHITE))
				sb.append("white");
			else
				sb.append(colorname);
		} else {
			// final String
			// suffix="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (c.equals(GColor.BLACK))
				sb.append("black");
			else if (c.equals(GColor.DARK_GRAY))
				sb.append("darkgray");
			else if (c.equals(GColor.GRAY))
				sb.append("gray");
			else if (c.equals(GColor.LIGHT_GRAY))
				sb.append("lightgray");
			else if (c.equals(GColor.WHITE))
				sb.append("white");
			else if (c.equals(GColor.RED))
				sb.append("red");
			else if (c.equals(GColor.GREEN))
				sb.append("green");
			else if (c.equals(GColor.BLUE))
				sb.append("blue");
			else if (c.equals(GColor.CYAN))
				sb.append("cyan");
			else if (c.equals(GColor.MAGENTA))
				sb.append("magenta");
			else if (c.equals(GColor.YELLOW))
				sb.append("yellow");
			else {
				String colorname = "";
				if (customColor.containsKey(c)) {
					colorname = customColor.get(c).toString();
				} else {
					int red = c.getRed();
					int green = c.getGreen();
					int blue = c.getBlue();
					colorname = createCustomColor(red, green, blue);
					codeBeginDoc.append("\\newrgbcolor{" + colorname + "}{"
							+ format(red / 255d) + " " + format(green / 255d)
							+ " " + format(blue / 255d) + "}\n");
					customColor.put(c, colorname);
				}
				sb.append(colorname);
			}
		}
	}

	/*
	 * // Resize text // Keep the ratio between font size and picture height
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
	/*
	 * private void defineTransparency(){ String str=
	 * "\\makeatletter\n\\define@key[psset]{}{transpalpha}{\\pst@checknum{#1}\\pstranspalpha}\n"
	 * + "\\psset{transpalpha=1}\n"+ "\\def\\psfs@transp{%\n"+
	 * "  \\addto@pscode{/Normal .setblendmode \\pstranspalpha .setshapealpha }%\n"
	 * + "  \\psfs@solid}\n"; if (!transparency) codePreamble.append(str);
	 * transparency=true; }
	 */
	private void addText(String st, boolean isLatex, int style, GColor geocolor) {
		if (isLatex) {
			if (!st.startsWith("$"))
				code.append("$");
			String stSym = st;
			for (int i = 0; i < st.length(); i++) {
				char uCode = st.charAt(i);
				if (UnicodeTeX.getMap().containsKey(uCode)) {
					addTextPackage();
					stSym = stSym.replaceAll("\\" + uCode, "\\\\"
							+ UnicodeTeX.getMap().get(uCode) + " ");
				}
			}
			st = stSym;
		}
		// Replace all backslash symbol with \textbackslash
		else {
			st = st.replaceAll("\\\\", "\\\\textbackslash ");
			String stSym = st;
			for (int i = 0; i < st.length(); i++) {
				char uCode = st.charAt(i);
				if (UnicodeTeX.getMap().containsKey(uCode)) {
					addTextPackage();
					stSym = stSym.replaceAll("\\" + uCode, "\\$\\\\"
							+ UnicodeTeX.getMap().get(uCode) + "\\$ ");
				}
			}
			st = stSym;
			if (!eurosym && st.contains("$\\euro$")) {
				codePreamble.append("\\usepackage{eurosym}\n");
				st = st.replace("$\\euro$", "\\euro");
			}
		}
		switch (style) {
		case 1:
			if (isLatex)
				code.append("\\mathbf{");
			else
				code.append("\\textbf{");
			break;
		case 2:
			if (isLatex)
				code.append("\\mathit{");
			else
				code.append("\\textit{");
			break;
		case 3:
			if (isLatex)
				code.append("\\mathit{\\mathbf{");
			else
				code.append("\\textit{\\textbf{");
			break;
		}
		if (!geocolor.equals(GColor.BLACK)) {
			code.append("\\");
			colorCode(geocolor, code);
			code.append("{");
		}
		/*
		 * if (size!=app.getFontSize()) { String formatFont=resizeFont(size); if
		 * (null!=formatFont) code.append(formatFont); }
		 */
		code.append(st);
		// if (size!=app.getFontSize()) code.append("}");
		if (!geocolor.equals(GColor.BLACK)) {
			code.append("}");
		}
		switch (style) {
		case 1:
		case 2:
			code.append("}");
			break;
		case 3:
			code.append("}}");
			break;
		}
		if (isLatex && !st.endsWith("$"))
			code.append("$");
	}

	/**
	 * Export PSTricks code for implicit polynom (degree greater than 2)
	 */
	@Override
	protected void drawImplicitPoly(GeoImplicit geo) {
		if (codePreamble.indexOf("pst-func") == -1) {
			codePreamble.append("\\usepackage{pst-func}\n");
		}
		code.append("\\psplotImp");
		code.append(lineOptionCode(geo.toGeoElement(), true));
		code.append("(");
		code.append(Math.floor(xmin) - 1);
		code.append(",");
		code.append(Math.floor(ymin) - 1);
		code.append(")(");
		code.append(Math.floor(xmax) + 1);
		code.append(",");
		code.append(Math.floor(ymax) + 1);
		code.append("){");
		code.append(getImplicitExpr(geo));
		code.append("}\n");
	}

	@Override
	protected void drawPolyLine(GeoPolyLine geo) {
		GeoPointND[] path = geo.getPoints();
		if (path.length < 2)
			return;
		startBeamer(code);
		code.append("\\psline");
		code.append(lineOptionCode(geo, true));

		for (int i = 0; i < path.length; i++) {
			Coords coords = path[i].getInhomCoords();
			String x1 = format(coords.getX());
			String y1 = format(coords.getY());
			if (x1.contains("?") || y1.contains("?")) {
				code.append("\n\\psline");
				code.append(lineOptionCode(geo, true));
			} else {
				code.append("(");
				code.append(x1);
				code.append(",");
				code.append(y1);
				code.append(")");
			}
		}
		code.append("\n");
		endBeamer(code);
	}

	@Override
	protected void drawHistogramOrBarChartBox(double[] y, double[] x,
			int length, double width, GeoNumeric g) {
		String command = g.getDefinition(StringTemplate.noLocalDefault);
		startBeamer(codeFilledObject);
		if (command.contains("Binomial") && command.contains("true")) {
			codeFilledObject.append("\\psline");
			codeFilledObject.append(lineOptionCode(g, true));
			codeFilledObject.append("(");
			codeFilledObject.append(format(x[0] + width / 2));
			codeFilledObject.append(",0)(");
			codeFilledObject.append(format(x[0] + width / 2));
			codeFilledObject.append(",");
			codeFilledObject.append(format(y[0]));
			codeFilledObject.append(")\n");
			for (int i = 0; i < length - 1; i++) {
				codeFilledObject.append("\\psline");
				codeFilledObject.append(lineOptionCode(g, true));
				codeFilledObject.append("(");
				codeFilledObject.append(format(x[i] + width / 2));
				codeFilledObject.append("," + format(y[i]) + ")(");
				codeFilledObject.append(format(x[i + 1] + width / 2));
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i]));
				codeFilledObject.append(")\n");
				if (i != x.length - 2 && isBeamer)
					codeFilledObject.append("  ");

				codeFilledObject.append("\\psline");
				codeFilledObject.append(lineOptionCode(g, true));
				codeFilledObject.append("(");
				codeFilledObject.append(format(x[i + 1] + width / 2));
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i]));
				codeFilledObject.append(")");
				codeFilledObject.append("(");
				codeFilledObject.append(format(x[i + 1] + width / 2));
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i + 1]));
				codeFilledObject.append(")\n");

			}
		} else {
			for (int i = 0; i < length; i++) {
				barNumber = i + 1;
				codeFilledObject.append("\\psframe");
				codeFilledObject.append(lineOptionCode(g, true));
				codeFilledObject.append("(");
				codeFilledObject.append(format(x[i]));
				codeFilledObject.append(",0)(");
				if (x.length == length) {
					codeFilledObject.append(format(x[i] + width));
				} else {
					codeFilledObject.append(format(x[i + 1]));
				}
				codeFilledObject.append(",");
				codeFilledObject.append(format(y[i]));
				codeFilledObject.append(")\n");
				if (i != x.length - 2 && isBeamer)
					codeFilledObject.append("  ");
			}
		}
		endBeamer(codeFilledObject);
	}

	@Override
	protected void drawNyquist(GeoTransferFunction g) {
		startBeamer(code);
		String liopco = lineOptionCode(g, true);
		String template = "\\psline" + liopco + "" + Unicode.SECTION_SIGN
				+ "arrows" + Unicode.SECTION_SIGN + "(%0,%1)(%2,%3)\n";
		StringBuilder lineBuilder = drawNyquistDiagram(g, template, ""
				+ Unicode.SECTION_SIGN + "arrows" + Unicode.SECTION_SIGN + "",
				"{<-}", "{->}");
		code.append(lineBuilder.toString());
		endBeamer(code);
	}

	/**
	 * @param curves
	 */
	@Override
	protected boolean fillSpline(GeoCurveCartesian[] curves) {
		String liopco = lineOptionCode(curves[0], true);
		if (!liopco.contains("fill")) {
			return false;
		}
		StringBuilder fill = new StringBuilder("\\pscustom");
		fill.append(liopco);
		fill.append("\n{\n");
		code.append(fill);
		for (int i = 0; i < curves.length; i++)
			drawSingleCurveCartesian(curves[i], false);
		code.append("}\n");
		return true;
	}

	public void superFill(GShape s, Inequality ineq, FunctionalNVar geo,
			double[] ds) {
		((GeoElement) geo).setLineType(ineq.getBorder().lineType);
		switch (ineq.getType()) {
		case INEQUALITY_CONIC:
			GeoConicND conic = ineq.getConicBorder();
			if (conic.getType() == GeoConicNDConstants.CONIC_ELLIPSE
					|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
				((GeoElement) conic)
						.setObjColor(((GeoElement) geo).getObjectColor());
				((GeoElement) conic)
						.setAlphaValue(((GeoElement) geo).getAlphaValue());
				conic.setType(GeoConicNDConstants.CONIC_ELLIPSE);
				((GeoElement) conic).setHatchingAngle(
						(int) ((GeoElement) geo).getHatchingAngle());
				((GeoElement) conic).setHatchingDistance(
						((GeoElement) geo).getHatchingDistance());
				((GeoElement) conic)
						.setFillType(((GeoElement) geo).getFillType());
				drawGeoConic((GeoConic) conic);
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
			code.append("\\pspolygon");
			code.append(lineOptionCode((GeoElement) geo, true));
			double precX = Integer.MAX_VALUE;
			double precY = Integer.MAX_VALUE;
			while (!path.isDone()) {
				path.currentSegment(coords);
				if (coords[0] == precX && coords[1] == precY) {
					code.append("\\pspolygon");
					code.append(lineOptionCode((GeoElement) geo, true));
				} else {
					code.append("(");
					code.append(format((coords[0] - zeroX) / ds[4]));
					code.append(",");
					code.append(format(-(coords[1] - zeroY) / ds[5]));
					code.append(")");
				}
				precX = coords[0];
				precY = coords[1];
				path.next();
			}
			int i = code.lastIndexOf(")");
			code.delete(i + 1, code.length());
			code.append("\n");
			break;
		}
	}
}
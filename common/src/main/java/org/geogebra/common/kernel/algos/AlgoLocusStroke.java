/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.StringUtil;

/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author Michael Borcherds
 */
public class AlgoLocusStroke extends AlgoElement
		implements AlgoStrokeInterface {

	protected GeoLocusStroke poly; // output
	// list of all points (also newly calculated control points of
	// bezier curve)
	private ArrayList<MyPoint> pointList = new ArrayList<MyPoint>();

	/**
	 * @param cons
	 *            the construction
	 * @param points
	 *            vertices of the polygon
	 */


	public AlgoLocusStroke(Construction cons, GeoPointND[] points) {
		super(cons);
		poly = new GeoLocusStroke(this.cons);
		updatePointArray(points);
		// poly = new GeoPolygon(cons, points);
		// updatePointArray already covered compute
		input = new GeoElement[1];
		// for (int i = 0; i < points.length; i++) {
		// input[i] = (GeoElement) points[i];
		// }

		input[0] = new GeoBoolean(cons, true); // dummy to
															// force
															// PolyLine[...,
															// true]

		setInputOutput(); // for AlgoElement

	}

	@Override
	public Commands getClassName() {
		return Commands.PolyLine;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POLYLINE;
	}

	/**
	 * @return - true, if poly is pen stroke
	 */
	public boolean getIsPenStroke() {
		return true;
	}

	// data has to have at least 2 defined points after each other
	private static boolean canBeBezierCurve(GeoPointND[] data) {
		boolean firstDefFound = false;
		for (int i=0;i<data.length;i++) {
			if (data[i].isDefined()) {
				if (firstDefFound) {
					return true;
				}
				firstDefFound = true;
			} else {
				firstDefFound = false;
			}
		}
		return false;
	}

	/**
	 * Update point array of polygon using the given array list
	 * 
	 * @param pointList
	 */
	public void updatePointArray(GeoPointND[] data) {
		// check if we have a point list
		// create new points array
		int size = data.length;
		poly.setDefined(true);
		poly.getPoints().clear();
		// to use bezier curve we need at least 2 points
		// stroke is: (A),(?),(A),(B) -> size 4
		if (canBeBezierCurve(data) && poly.getKernel().getApplication()
				.has(Feature.PEN_SMOOTHING)) {
			int index = 0;
			pointList.clear();
			if (data[0].isDefined()) {
				// move at first point
				pointList.add(
						new MyPoint(data[0].getInhomX(), data[0].getInhomY(),
					SegmentType.MOVE_TO));
			}
			// Log.debug("1: (" + data[0].getInhomX() + "," +
			// data[0].getInhomY()
			// + ") -> " +
			// SegmentType.MOVE_TO);
			while (index <= data.length) {
				if (!pointList.isEmpty()
						&& pointList.get(pointList.size() - 1).isDefined()) {
					pointList.add(
						new MyPoint(Double.NaN, Double.NaN,
								SegmentType.LINE_TO));
				}
				GeoPointND[] partOfStroke = getPartOfPenStroke(index, data);
				// if we found single point
				// just add it to the list without control points
				if (partOfStroke.length == 1) {
					pointList.add(new MyPoint(partOfStroke[0].getInhomX(),
							partOfStroke[0].getInhomY(), SegmentType.MOVE_TO));
				} else if (partOfStroke.length > 1) {
					ArrayList<double[]> controlPoints = getControlPoints(
							partOfStroke);
					for (int i = 0; i < partOfStroke.length - 1; i++) {
						// start point of segment
						pointList.add(new MyPoint(partOfStroke[i].getInhomX(),
								partOfStroke[i].getInhomY(),
								i == 0 ? SegmentType.MOVE_TO
										: SegmentType.CURVE_TO));
						// first control point
						pointList.add(new MyPoint(controlPoints.get(0)[i],
								controlPoints.get(1)[i], SegmentType.CONTROL));
						// second control point
						pointList.add(new MyPoint(controlPoints.get(2)[i],
								controlPoints.get(3)[i], SegmentType.CONTROL));
					}
					// end point of curve
					pointList.add(new MyPoint(
							partOfStroke[partOfStroke.length - 1].getInhomX(),
							partOfStroke[partOfStroke.length - 1].getInhomY(),
							SegmentType.CURVE_TO));
				}
				if (partOfStroke.length == 3 && Kernel.isZero(partOfStroke[partOfStroke.length-1].distance(partOfStroke[partOfStroke.length-2]))) {
					index = index + partOfStroke.length;
				} else {
					index = index + partOfStroke.length + 1;
				}
			}
			poly.setPoints(pointList);
		} else {
			for (int i = 0; i < size; i++) {
				poly.getPoints().add(new MyPoint(data[i].getInhomX(),
					data[i].getInhomY(),
					i == 0 ? SegmentType.MOVE_TO : SegmentType.LINE_TO));
			}
		}
	}

	// returns the part of array started at index until first undef point
	private static GeoPointND[] getPartOfPenStroke(int index,
			GeoPointND[] data) {
		int size = 0;
		for (int i=index;i<data.length;i++) {
			if (data[i].isDefined()) {
				size++;
			} else {
				break;
			}
		}
		GeoPointND[] partOfStroke;
		// for simple segment add endpoint once again
		// trick needed for bezier curve
		if (size == 2) {
			partOfStroke = new GeoPointND[size + 1];
			for (int i = 0; i < size; i++) {
				partOfStroke[i] = data[i + index];
			}
			partOfStroke[size] = data[size + index - 1];
		} else {
			partOfStroke = new GeoPointND[size];
			for (int i = 0; i < size; i++) {
				partOfStroke[i] = data[i + index];
			}
		}
		return partOfStroke;
	}

	// calculate control points for bezier curve
	private static ArrayList<double[]> getControlPoints(GeoPointND[] data) {
		ArrayList<double[]> values = new ArrayList<double[]>();
		if (data.length == 0) {
			return values;
		}
		double[] xCoordsP1 = new double[data.length - 1];
		double[] xCoordsP2 = new double[data.length - 1];
		double[] yCoordsP1 = new double[data.length - 1];
		double[] yCoordsP2 = new double[data.length - 1];

		double[] a = new double[data.length - 1];
		double[] b = new double[data.length - 1];
		double[] c = new double[data.length - 1];
		double[] rX = new double[data.length - 1];
		double[] rY = new double[data.length - 1];
		int n = data.length - 1;
		/* left most segment */
		a[0] = 0;
		b[0] = 2;
		c[0] = 1;
		rX[0] = data[0].getInhomX() + 2 * data[1].getInhomX();
		rY[0] = data[0].getInhomY() + 2 * data[1].getInhomY();
		/* internal segments */
		for (int i = 1; i < n - 1; i++) {
			a[i] = 1;
			b[i] = 4;
			c[i] = 1;
			rX[i] = 4 * data[i].getInhomX() + 2 * data[i + 1].getInhomX();
			rY[i] = 4 * data[i].getInhomY() + 2 * data[i + 1].getInhomY();
		}
		/* right segment */
		a[n - 1] = 2;
		b[n - 1] = 7;
		c[n - 1] = 0;
		rX[n - 1] = 8 * data[n - 1].getInhomX() + data[n].getInhomX();
		rY[n - 1] = 8 * data[n - 1].getInhomY() + data[n].getInhomY();

		/* solves Ax=b with the Thomas algorithm (from Wikipedia) */
		for (int i = 1; i < n; i++) {
			double m = a[i] / b[i - 1];
			b[i] = b[i] - m * c[i - 1];
			rX[i] = rX[i] - m * rX[i - 1];
			rY[i] = rY[i] - m * rY[i - 1];
		}

		xCoordsP1[n - 1] = rX[n - 1] / b[n - 1];
		yCoordsP1[n - 1] = rY[n - 1] / b[n - 1];
		for (int i = n - 2; i >= 0; --i) {
			xCoordsP1[i] = (rX[i] - c[i] * xCoordsP1[i + 1]) / b[i];
			yCoordsP1[i] = (rY[i] - c[i] * yCoordsP1[i + 1]) / b[i];
		}

		/* we have p1, now compute p2 */
		for (int i = 0; i < n - 1; i++) {
			xCoordsP2[i] = 2 * data[i + 1].getInhomX() - xCoordsP1[i + 1];
			yCoordsP2[i] = 2 * data[i + 1].getInhomY() - yCoordsP1[i + 1];
		}
		xCoordsP2[n - 1] = 0.5 * (data[n].getInhomX() + xCoordsP1[n - 1]);
		yCoordsP2[n - 1] = 0.5 * (data[n].getInhomY() + yCoordsP1[n - 1]);

		values.add(xCoordsP1);
		values.add(yCoordsP1);
		values.add(xCoordsP2);
		values.add(yCoordsP2);
		return values;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}

		// set output
		setOutputLength(1);
		setOutput(0, poly);
		setDependencies();
	}

	@Override
	public void update() {
		// compute output from input
		compute();
		getOutput(0).update();
	}



	@Override
	public void compute() {

		// poly.getPoints().clear();
		// for (int i = 0; i < input.length - 1; i++) {
		// GeoPoint pt = (GeoPoint) input[i];
		// poly.getPoints().add(new MyPoint(pt.getInhomX(), pt.getInhomY(),
		// i == 0 ? SegmentType.MOVE_TO : SegmentType.LINE_TO));
		// }

	}


	@Override
	final public String toString(StringTemplate tpl) {

		return "";

	}

	public final MyPoint[] getPointsND() {
		return poly.getPointsND();
	}

	@Override
	public int getPointsLength() {
		return poly.getPointLength();
	}

	@Override
	public GeoPoint getPointCopy(int i) {
		return new GeoPoint(cons, poly.getPoints().get(i).getX(),
				poly.getPoints().get(i).getY(), 1);
	}

	/**
	 * @return list of points without the control points
	 */
	public ArrayList<MyPoint> getPointsWithoutControl() {
		return poly.getPointsWithoutControl();
	}

	/**
	 * @return full list of definition points
	 */
	public ArrayList<MyPoint> getPoints() {
		return poly.getPoints();
	}

	public void updateFrom(List<MyPoint> data) {
		if (poly.getPoints() != data) {
			poly.setDefined(true);
			poly.getPoints().clear();
			for (MyPoint pt : data) {
				poly.getPoints().add(pt);
			}
		}
		poly.resetPointsWithoutControl();
		getOutput(0).update();
	}

	/**
	 * Expressions should be shown as out = expression e.g.
	 * <expression label="u" exp="a + 7 b"/>
	 * 
	 * @param tpl
	 *            string template
	 * @return expression XML tag
	 */
	@Override
	protected String getExpXML(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("<expression");
		// add label
		if (/* output != null && */getOutputLength() == 1) {
			if (getOutput(0).isLabelSet()) {
				sb.append(" label=\"");
				StringUtil.encodeXML(sb, getOutput(0).getLabel(tpl));
				sb.append("\"");
			}
		}
		// add expression
		sb.append(" exp=\"PolyLine[");
		appendPoints(sb, tpl);
		sb.append("]\"");

		// expression
		sb.append(" />\n");
		return sb.toString();
	}

	private void appendPoints(StringBuilder sb, StringTemplate tpl) {
		ArrayList<MyPoint> pts = this.getPointsWithoutControl();
		for (MyPoint m : pts) {
			sb.append("(");
			sb.append(kernel.format(m.getX(), tpl));
			sb.append(",");
			sb.append(kernel.format(m.getY(), tpl));
			sb.append("), ");
		}
		sb.append("true");
	}

	@Override
	protected boolean hasExpXML(String cmd) {
		return true;
	}

	@Override
	public String getDefinition(StringTemplate tpl) {
		String def = "PolyLine";

		// #2706
		if (input == null) {
			return null;
		}
		sbAE.setLength(0);
		if (tpl.isPrintLocalizedCommandNames()) {
			sbAE.append(getLoc().getCommand(def));
		} else {
			sbAE.append(def);
		}

		

		sbAE.append(tpl.leftSquareBracket());
		// input legth is 0 for ConstructionStep[]
		
		appendPoints(sbAE, tpl);
		sbAE.append(tpl.rightSquareBracket());
		return sbAE.toString();

	}
}

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
import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.ScientificFormatAdapter;
import org.geogebra.common.util.StringUtil;

/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author Michael Borcherds
 */
public class AlgoLocusStroke extends AlgoElement
		implements AlgoStrokeInterface {

	private static final double MIN_CURVE_ANGLE = Math.PI / 60; // 3degrees
	/** output */
	protected GeoLocusStroke poly;
	// list of all points (also newly calculated control points of
	// bezier curve)
	private ArrayList<MyPoint> pointList = new ArrayList<>();

	/**
	 * @param cons
	 *            the construction
	 * @param points
	 *            vertices of the polygon
	 */
	public AlgoLocusStroke(Construction cons, List<MyPoint> points) {
		super(cons);
		poly = new GeoLocusStroke(this.cons);
		updatePointArray(points, 0, 0);
		// poly = new GeoPolygon(cons, points);
		// updatePointArray already covered compute
		input = new GeoElement[1];

		// dummy to force PolyLine[..., true]
		input[0] = new GeoBoolean(cons, true);

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
	private static boolean canBeBezierCurve(List<MyPoint> data) {
		boolean firstDefFound = false;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isDefined()) {
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
	 * @param data
	 *            points
	 * @param initialIndex
	 *            index from which to start smoothing (consider previous points
	 *            smooth)
	 * @param xscale
	 *            view scale, used for filtering
	 */
	public void updatePointArray(List<MyPoint> data, int initialIndex,
			double xscale) {
		poly.resetPointsWithoutControl();
		// check if we have a point list
		// create new points array
		int size = data.size();
		poly.setDefined(true);
		poly.getPoints().clear();
		// to use bezier curve we need at least 2 points
		// stroke is: (A),(?),(A),(B) -> size 4
		if (canBeBezierCurve(data)) {
			pointList.clear();
			for (int i = 0; i < initialIndex; i++) {
				pointList.add(data.get(i));
			}
			int index = initialIndex;
			// if (data.get(index).isDefined()) {
			// // move at first point
			// pointList.add(
			// new MyPoint(data.get(index).getX(),
			// data.get(index).getY(),
			// SegmentType.MOVE_TO));
			// }
			// Log.debug("1: (" + data[0].getInhomX() + "," +
			// data[0].getInhomY()
			// + ") -> " +
			// SegmentType.MOVE_TO);
			while (index <= data.size()) {
				// TODO
				// separator for XML
				List<MyPoint> unfiltered = getPartOfPenStroke(index, data);
				if (!pointList.isEmpty()
						&& pointList.get(pointList.size() - 1).isDefined()
						&& unfiltered.size() > 0) {
					pointList.add(new MyPoint(Double.NaN, Double.NaN,
							SegmentType.LINE_TO));
				}
				// if we found single point
				// just add it to the list without control points
				if (unfiltered.size() > 0) {
					pointList.add(
							unfiltered.get(0).withType(SegmentType.MOVE_TO));
				}
				List<MyPoint> partOfStroke = unfiltered;
				if (partOfStroke.size() == 1) {
					pointList.add(
							partOfStroke.get(0).withType(SegmentType.LINE_TO));
				} else if (partOfStroke.size() == 2) {
					pointList.add(
							partOfStroke.get(1).withType(SegmentType.LINE_TO));
				} else if (partOfStroke.size() > 1) {
					ArrayList<double[]> controlPoints = getControlPoints(
							partOfStroke);
					for (int i = 1; i < partOfStroke.size(); i++) {
						MyPoint ctrl1 = new MyPoint(controlPoints.get(0)[i - 1],
								controlPoints.get(1)[i - 1],
								SegmentType.CONTROL);
						MyPoint ctrl2 = new MyPoint(controlPoints.get(2)[i - 1],
								controlPoints.get(3)[i - 1],
								SegmentType.CONTROL);
						MyPoint endpoint = partOfStroke.get(i);
						if (angle(pointList.get(pointList.size() - 1), ctrl1,
								endpoint) > MIN_CURVE_ANGLE
								|| angle(pointList.get(pointList.size() - 1),
										ctrl2, endpoint) > MIN_CURVE_ANGLE) {
							pointList.add(ctrl1);
							pointList.add(ctrl2);
							pointList.add(
									endpoint.withType(SegmentType.CURVE_TO));
						} else {
							pointList.add(
									endpoint.withType(SegmentType.LINE_TO));
						}
					}
				}
				index = index + Math.max(unfiltered.size(), 1);
			}
			poly.setPoints(pointList);
		} else {
			for (int i = 0; i < size; i++) {
				poly.getPoints().add(new MyPoint(data.get(i).getX(),
						data.get(i).getY(),
					i == 0 ? SegmentType.MOVE_TO : SegmentType.LINE_TO));
			}
		}
	}

	private static double angle(MyPoint a, MyPoint b, MyPoint c) {
		double dx1 = a.x - b.x;
		double dx2 = c.x - b.x;
		double dy1 = a.y - b.y;
		double dy2 = c.y - b.y;
		double ret = Math.PI - MyMath.angle(dx1, dy1, dx2, dy2);
		return ret;
	}

	// returns the part of array started at index until first undef point
	private static List<MyPoint> getPartOfPenStroke(int index,
			List<MyPoint> data) {
		ArrayList<MyPoint> partOfStroke = new ArrayList<>(
				data.size() - index + 1);
		for (int i = index; i < data.size() && data.get(i).isDefined()
				&& (data.get(i).getSegmentType() != SegmentType.MOVE_TO
						|| i == index); i++) {
			partOfStroke.add(data.get(i));
		}
		return partOfStroke;
	}

	// calculate control points for bezier curve
	private static ArrayList<double[]> getControlPoints(List<MyPoint> data) {
		ArrayList<double[]> values = new ArrayList<>();

		if (data.size() == 0) {
			return values;
		}

		double[] a = new double[data.size() - 1];
		double[] b = new double[data.size() - 1];
		double[] c = new double[data.size() - 1];
		double[] rX = new double[data.size() - 1];
		double[] rY = new double[data.size() - 1];
		int n = data.size() - 1;
		/* left most segment */
		a[0] = 0;
		b[0] = 2;
		c[0] = 1;
		rX[0] = data.get(0).getX() + 2 * data.get(1).getX();
		rY[0] = data.get(0).getY() + 2 * data.get(1).getY();
		/* internal segments */
		for (int i = 1; i < n - 1; i++) {
			a[i] = 1;
			b[i] = 4;
			c[i] = 1;
			rX[i] = 4 * data.get(i).getX() + 2 * data.get(i + 1).getX();
			rY[i] = 4 * data.get(i).getY() + 2 * data.get(i + 1).getY();
		}
		/* right segment */
		a[n - 1] = 2;
		b[n - 1] = 7;
		c[n - 1] = 0;
		rX[n - 1] = 8 * data.get(n - 1).getX() + data.get(n).getX();
		rY[n - 1] = 8 * data.get(n - 1).getY() + data.get(n).getY();

		/* solves Ax=b with the Thomas algorithm (from Wikipedia) */
		for (int i = 1; i < n; i++) {
			double m = a[i] / b[i - 1];
			b[i] = b[i] - m * c[i - 1];
			rX[i] = rX[i] - m * rX[i - 1];
			rY[i] = rY[i] - m * rY[i - 1];
		}

		double[] xCoordsP1 = new double[data.size() - 1];
		double[] xCoordsP2 = new double[data.size() - 1];
		double[] yCoordsP1 = new double[data.size() - 1];
		double[] yCoordsP2 = new double[data.size() - 1];
		xCoordsP1[n - 1] = rX[n - 1] / b[n - 1];
		yCoordsP1[n - 1] = rY[n - 1] / b[n - 1];
		for (int i = n - 2; i >= 0; --i) {
			xCoordsP1[i] = (rX[i] - c[i] * xCoordsP1[i + 1]) / b[i];
			yCoordsP1[i] = (rY[i] - c[i] * yCoordsP1[i + 1]) / b[i];
		}

		/* we have p1, now compute p2 */
		for (int i = 0; i < n - 1; i++) {
			xCoordsP2[i] = 2 * data.get(i + 1).getX() - xCoordsP1[i + 1];
			yCoordsP2[i] = 2 * data.get(i + 1).getY() - yCoordsP1[i + 1];
		}
		xCoordsP2[n - 1] = 0.5 * (data.get(n).getX() + xCoordsP1[n - 1]);
		yCoordsP2[n - 1] = 0.5 * (data.get(n).getY() + yCoordsP1[n - 1]);

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
		getOutput(0).update();
	}

	@Override
	public void compute() {
		// no recomputation needed
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return "";
	}

	@Override
	public int getPointsLength() {
		return poly.getPointLength();
	}

	@Override
	public MyPoint getPointCopy(int i) {
		return new MyPoint(poly.getPoints().get(i).getX(),
				poly.getPoints().get(i).getY());
	}

	/**
	 * @return list of points without the control points
	 */
	public ArrayList<MyPoint> getPointsWithoutControl() {
		final ArrayList<MyPoint> pointsNoControl = new ArrayList<>();
		poly.processPointsWithoutControl(new AsyncOperation<MyPoint>() {

			@Override
			public void callback(MyPoint obj) {
				pointsNoControl.add(obj);
			}
		});
		return pointsNoControl;
	}

	/**
	 * @return full list of definition points
	 */
	public ArrayList<MyPoint> getPoints() {
		return poly.getPoints();
	}

	/**
	 * @param data
	 *            new point array
	 */
	public void updateFrom(List<MyPoint> data) {
		if (poly.getPoints() != data) {
			poly.setDefined(true);
			poly.getPoints().clear();
			poly.getPoints().addAll(data);
		}
		poly.resetPointsWithoutControl();
		getOutput(0).updateCascade();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void getExpXML(StringTemplate tpl, StringBuilder sb) {
		sb.append("<expression");
		if (/* output != null && */getOutputLength() == 1) {
			if (getOutput(0).isLabelSet()) {
				sb.append(" label=\"");
				StringUtil.encodeXML(sb, getOutput(0).getLabel(tpl));
				sb.append("\"");
			}
		}
		StringBuilder xmlPoints = poly.getXMLPointBuilder();
		if (xmlPoints == null) {
			xmlPoints = new StringBuilder();
			// add expression
			xmlPoints.append(" exp=\"PolyLine[");
			appendPoints(xmlPoints);
			xmlPoints.append("]\" />\n");
		}
		poly.setXMLPointBuilder(xmlPoints);
		sb.append(xmlPoints);
	}

	private void appendPoints(final StringBuilder sb) {
		final ScientificFormatAdapter formatter = FormatFactory.getPrototype()
				.getFastScientificFormat(5);
		poly
				.processPointsWithoutControl(new AsyncOperation<MyPoint>() {

					@Override
					public void callback(MyPoint m) {
						sb.append("(");
						sb.append(formatter.format(m.getX()));
						sb.append(",");
						sb.append(formatter.format(m.getY()));
						sb.append("), ");
					}
				});
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
		
		appendPoints(sbAE);
		sbAE.append(tpl.rightSquareBracket());
		return sbAE.toString();
	}

}

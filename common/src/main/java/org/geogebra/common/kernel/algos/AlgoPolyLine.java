/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPenStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author Michael Borcherds
 */
public class AlgoPolyLine extends AlgoElement {

	protected GeoPointND[] points; // input
	protected GeoList geoList; // alternative input
	protected GeoPolyLine poly; // output
	private boolean penStroke;

	public AlgoPolyLine(Construction cons, String label, GeoList geoList) {
		this(cons, label, null, geoList);
	}

	public AlgoPolyLine(Construction cons, String label, GeoList geoList,
			boolean penStroke) {
		this(cons, label, null, geoList, penStroke);
	}

	public AlgoPolyLine(Construction cons, GeoList geoList) {
		this(cons, (GeoPointND[]) null, geoList, false);
	}

	public AlgoPolyLine(Construction cons, String label,
			GeoPointND[] points, boolean penStroke) {
		this(cons, label, points, null, penStroke);
	}

	public AlgoPolyLine(Construction cons, GeoPointND[] points,
			boolean penStroke) {
		this(cons, points, null, penStroke);
	}

	/**
	 * @param cons
	 *            the construction
	 * @param labels
	 *            names of the polygon
	 * @param points
	 *            vertices of the polygon
	 * @param geoList
	 *            list of vertices of the polygon (alternative to points)
	 */
	public AlgoPolyLine(Construction cons, String label,
			GeoPointND[] points, GeoList geoList) {

		this(cons, points, geoList, false);

		poly.setLabel(label);

	}

	public AlgoPolyLine(Construction cons, String label,
			GeoPointND[] points, GeoList geoList, boolean penStroke) {

		this(cons, points, geoList, penStroke);

		poly.setLabel(label);

	}

	public AlgoPolyLine(Construction cons, GeoPointND[] points,
			GeoList geoList, boolean penStroke) {
		super(cons);
		this.points = points;
		this.geoList = geoList;
		this.penStroke = penStroke;

		// Log.debug(penStroke);

		// poly = new GeoPolygon(cons, points);
		createPolyLine();

		// compute polygon points
		compute();

		setInputOutput(); // for AlgoElement

	}

	/**
	 * create the polygon
	 */
	protected void createPolyLine() {
		if (penStroke) {
			poly = new GeoPenStroke(this.cons, this.points);
		} else {
			poly = new GeoPolyLine(this.cons, this.points);
		}
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
	 * Update point array of polygon using the given array list
	 * 
	 * @param pointList
	 */
	private void updatePointArray(GeoList pointList) {
		// check if we have a point list
		if (!pointList.getElementType().equals(GeoClass.POINT)) {
			poly.setUndefined();
			return;
		}

		// create new points array
		int size = pointList.size();
		points = new GeoPoint[size];
		for (int i = 0; i < size; i++) {
			points[i] = (GeoPoint) pointList.get(i);
		}
		poly.setPoints(points);

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (geoList != null) {

			if (penStroke) {
				// list as input
				input = new GeoElement[2];
				input[0] = geoList;
				input[1] = new GeoBoolean(cons, true); // dummy to force
														// PolyLine[list, true]
			} else {
				// list as input
				input = new GeoElement[1];
				input[0] = geoList;
			}
		} else {
			input = new GeoElement[points.length + (penStroke ? 1 : 0)];
			for (int i = 0; i < points.length; i++) {
				input[i] = (GeoElement) points[i];
			}

			if (penStroke) {
				input[points.length] = new GeoBoolean(cons, true); // dummy to
																	// force
																	// PolyLine[...,
																	// true]
			}

		}
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

	public GeoPolyLine getPoly() {
		return poly;
	}

	public GeoPointND[] getPoints() {
		return points;
	}

	public GeoList getPointsList() {
		return geoList;
	}

	@Override
	public void compute() {
		if (geoList != null) {
			updatePointArray(geoList);
		}

		// compute area
		poly.calcLength();
	}

	StringBuilder sb;

	@Override
	final public String toString(StringTemplate tpl) {

		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		sb.append(getLoc().getPlain("PolyLine"));
		sb.append(' ');

		// G.Sturr: get label from geoList (2010-3-15)
		if (geoList != null) {
			sb.append(geoList.getLabel(tpl));

		} else if (points.length < 20) {
			// use point labels

			int last = points.length - 1;
			for (int i = 0; i < last; i++) {
				sb.append(points[i].getLabel(tpl));
				sb.append(", ");
			}
			sb.append(points[last].getLabel(tpl));
		} else {
			// too long (eg from Pen Tool), just return empty string
			return "";
		}

		return sb.toString();
	}

	public void setPointsList(GeoList newPts) {
		this.geoList = newPts;

	}

	public final GeoPointND[] getPointsND() {
		return points;
	}

	
}

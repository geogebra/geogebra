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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
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
	/** input points */
	protected GeoPointND[] points;
	/** alternative input */
	protected GeoList geoList;
	/** output polyline */
	protected GeoPolyLine poly;
	private StringBuilder sb;

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list of vertices
	 */
	public AlgoPolyLine(Construction cons, GeoList geoList) {
		this(cons, (GeoPointND[]) null, geoList);
	}

	/**
	 * @param cons
	 *            construction
	 * @param points
	 *            vertices
	 */
	public AlgoPolyLine(Construction cons, GeoPointND[] points) {
		this(cons, points, null);
	}

	/**
	 * @param cons
	 *            the construction
	 * @param points
	 *            vertices of the polygon
	 * @param geoList
	 *            list of vertices of the polygon (alternative to points)
	 */
	public AlgoPolyLine(Construction cons, GeoPointND[] points,
			GeoList geoList) {
		super(cons);
		this.points = points;
		this.geoList = geoList;
		createPolyLine();

		// compute polygon points
		compute();

		setInputOutput(); // for AlgoElement
	}

	/**
	 * create the polygon
	 */
	protected void createPolyLine() {

		poly = new GeoPolyLine(this.cons, this.points);

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
	 *            new point list
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
			// list as input
			input = new GeoElement[1];
			input[0] = geoList;

		} else {
			updateInput();
		}
		// set dependencies
		for (GeoElement element : input) {
			element.addAlgorithm(this);
		}

		// set output
		setOnlyOutput(poly);
		setDependencies();
	}

	private void updateInput() {
		input = new GeoElement[points.length];
		for (int i = 0; i < points.length; i++) {
			input[i] = (GeoElement) points[i];
		}
	}

	@Override
	public void update() {
		// compute output from input
		compute();
		getOutput(0).update();
	}

	/**
	 * @return polyline
	 */
	public GeoPolyLine getPoly() {
		return poly;
	}

	/**
	 * @return points
	 */
	public GeoPointND[] getPoints() {
		return points;
	}

	/**
	 * @return point list
	 */
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

	@Override
	final public String toString(StringTemplate tpl) {

		if (sb == null) {
			sb = new StringBuilder();
		} else {
			sb.setLength(0);
		}

		sb.append(getLoc().getMenu("PolyLine"));
		sb.append(' ');

		// get label from geoList
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

	/**
	 * Insert a 2D point into the polyline.
	 * @param i index before which to insert
	 * @param x inhomogeneous x-coordinate
	 * @param y inhomogeneous y-coordinate
	 * @return newly inserted point
	 */
	public GeoPointND insertPoint(int i, double x, double y) {
		ArrayList<GeoPointND> newPoints = new ArrayList<>(List.of(points));
		GeoPointND copy = newPoints.get(i - 1).copy();
		copy.setCoords(x, y, 1);
		newPoints.add(i, copy);
		setPoints(newPoints);
		return copy;
	}

	/**
	 * Removes a point from the poly-line.
	 * @param i index
	 */
	public void removePoint(int i) {
		ArrayList<GeoPointND> newPoints = new ArrayList<>(List.of(points));
		newPoints.remove(i);
		setPoints(newPoints);
	}

	private void setPoints(ArrayList<GeoPointND> newPoints) {
		points = newPoints.toArray(new GeoPointND[0]);
		poly.setPoints(points);
		updateInput();
		poly.updateRepaint();
		resetFreeInputPoints();
	}
}

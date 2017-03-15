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
import org.geogebra.common.kernel.geos.GeoPenStroke;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author Michael Borcherds
 */
public class AlgoPenStroke extends AlgoElement implements AlgoStrokeInterface {

	protected GeoPointND[] points; // input
	protected GeoPolyLine poly; // output

	/**
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of the polyline
	 * @param points
	 *            vertices of the polygon
	 * @param geoList
	 *            list of vertices of the polygon (alternative to points)
	 */


	public AlgoPenStroke(Construction cons, GeoPointND[] points) {
		super(cons);
		this.points = points;

		// Log.debug(penStroke);

		// poly = new GeoPolygon(cons, points);
		poly = new GeoPenStroke(this.cons, this.points);

		// compute polygon points
		compute();

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

	/**
	 * Update point array of polygon using the given array list
	 * 
	 * @param pointList
	 */
	private void updatePointArray(GeoPoint[] data) {
		// check if we have a point list


		// create new points array
		int size = data.length;
		points = new GeoPoint[size];
		for (int i = 0; i < size; i++) {
			points[i] = data[i];
		}
		poly.setPoints(points);

	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

			input = new GeoElement[points.length + 1];
			for (int i = 0; i < points.length; i++) {
				input[i] = (GeoElement) points[i];
			}

			input[points.length] = new GeoBoolean(cons, true); // dummy to
																// force
																// PolyLine[...,
																// true]



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


	@Override
	public void compute() {
		// compute area
		poly.calcLength();
	}


	@Override
	final public String toString(StringTemplate tpl) {

		return "";

	}

	public final GeoPointND[] getPointsND() {
		return points;
	}

	public void updateFrom(GeoPoint[] data) {

		updatePointArray(data);
		updateInput();
		update();

	}

	public void updateInput() {

		input = new GeoElement[points.length + 1];
		for (int i = 0; i < points.length; i++) {
			input[i] = (GeoElement) points[i];
		}

		input[points.length] = new GeoBoolean(cons, true); // dummy to
															// force
															// PolyLine[...,
															// true]

		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
	}

	public int getPointsLength() {
		return points.length;
	}

	public GeoPoint getPointCopy(int i) {
		return (GeoPoint) points[i].copyInternal(cons);
	}

	public int getPointsLengthWihtoutControl() {
		return points.length;
	}

	public GeoPoint getNoControlPointCopy(int i) {
		return getPointCopy(i);
	}

}

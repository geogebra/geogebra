/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPolyLine3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Creates a PolyLine from a given list of points or point array.
 * 
 * @author Michael Borcherds
 */
public class AlgoPolyLine3D extends AlgoPolyLine {

	/**
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of the polyline
	 * @param geoList
	 *            list of vertices of the polygon (alternative to points)
	 */
	public AlgoPolyLine3D(Construction cons, String label, GeoList geoList) {
		this(cons, label, null, geoList);
	}

	/**
	 * @param cons
	 *            the construction
	 * @param label
	 *            name of the polyline
	 * @param points
	 *            vertices of the polygon
	 */
	public AlgoPolyLine3D(Construction cons, String label,
			GeoPointND[] points) {
		this(cons, label, points, null);
	}

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
	protected AlgoPolyLine3D(Construction cons, String label,
			GeoPointND[] points, GeoList geoList) {
		super(cons, points, geoList);
		poly.setLabel(label);
	}

	/**
	 * create the polyline
	 */
	@Override
	protected void createPolyLine() {
		poly = new GeoPolyLine3D(this.cons, this.points);
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
		if (!pointList.getElementType().equals(GeoClass.POINT)
				&& !pointList.getElementType().equals(GeoClass.POINT3D)) {
			poly.setUndefined();
			return;
		}

		// create new points array
		int size = pointList.size();
		points = new GeoPointND[size];
		for (int i = 0; i < size; i++) {
			points[i] = (GeoPointND) pointList.get(i);
		}
		poly.setPoints(points);

	}

	@Override
	public void update() {
		// compute output from input
		compute();
		getOutput(0).update();
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
	protected void setInputOutput() {

		// efficient inputs are points or list
		GeoElement[] efficientInput = createEfficientInput();

		input = new GeoElement[efficientInput.length];
		for (int i = 0; i < efficientInput.length; i++) {
			input[i] = efficientInput[i];
		}

		setEfficientDependencies(input, efficientInput);

		// set output after, to avoid segments to have this to parent algo
		// setOutput();

		setOnlyOutput(poly);
		setDependencies();
	}

	protected GeoElement[] createEfficientInput() {

		GeoElement[] efficientInput;

		if (geoList != null) {
			// list as input
			efficientInput = new GeoElement[1];
			efficientInput[0] = geoList;
		} else {
			// points as input
			efficientInput = new GeoElement[points.length];
			for (int i = 0; i < points.length; i++) {
				efficientInput[i] = (GeoElement) points[i];
			}
		}

		return efficientInput;
	}

}

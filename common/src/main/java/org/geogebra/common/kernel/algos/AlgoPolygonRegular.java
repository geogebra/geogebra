/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Creates a regular Polygon for two points and the number of vertices.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolygonRegular extends AlgoPolygonRegularND {

	private Coords centerPointCoords;

	/**
	 * Creates a new regular polygon algorithm
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels[0] for polygon, then labels for segments and then for
	 *            points
	 * @param A1
	 *            first input point
	 * @param B1
	 *            second input point
	 * @param num
	 *            number of vertices
	 */
	public AlgoPolygonRegular(Construction c, String[] labels, GeoPointND A1,
			GeoPointND B1, NumberValue num) {
		super(c, labels, A1, B1, num, null);
	}

	@Override
	protected GeoPolygon newGeoPolygon(Construction cons) {
		return new GeoPolygon(cons);
	}

	@Override
	protected GeoElement newGeoPoint(Construction cons) {
		GeoPoint newPoint = new GeoPoint(cons);
		newPoint.setCoords(0, 0, 1);
		return newPoint;
	}

	@Override
	protected void setCenterPoint(int n, double beta) {

		double xA = ((GeoPoint) A).inhomX;
		double yA = ((GeoPoint) A).inhomY;

		double xB = ((GeoPoint) B).inhomX;
		double yB = ((GeoPoint) B).inhomY;

		// some temp values
		double mx = (xA + xB) / 2; // midpoint of AB
		double my = (yA + yB) / 2;
		// normal vector of AB
		double nx = yA - yB;
		double ny = xB - xA;

		// center point of regular polygon
		double tanBetaHalf = Math.tan(beta) / 2;
		centerPoint
				.setCoords(mx + tanBetaHalf * nx, my + tanBetaHalf * ny, 1.0);
		centerPointCoords = centerPoint.getInhomCoords();
	}

	@Override
	protected void rotate(GeoPointND point) {
		point.rotate(rotAngle, centerPointCoords);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = (GeoElement) A;
		input[1] = (GeoElement) B;
		input[2] = num.toGeoElement();
		// set dependencies
		for (int i = 0; i < input.length; i++) {
			input[i].addAlgorithm(this);
		}
		cons.addToAlgorithmList(this);

		// setOutput(); done in compute

		// parent of output
		getPoly().setParentAlgorithm(this);

	}

	@Override
	final protected void setDirection(GeoDirectionND direction) {
		// used only in 3D
	}

	public void calcCentroid(GeoPoint p) {
		p.setCoords((GeoPoint) centerPoint);

	}

}

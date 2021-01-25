/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Random point in a polygon P
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoRandomPointInPolygon extends AlgoElement implements SetRandomValue {

	protected GeoPolygon polygon; // input
	protected GeoPointND randomPoint; // output

	/**
	 * @param cons
	 *            construction
	 * @param polygon
	 *            polygon
	 */
	public AlgoRandomPointInPolygon(Construction cons, GeoPolygon polygon) {
		super(cons);
		this.polygon = polygon;
		createOutput();
		setInputOutput(); // for AlgoElement

		// compute
		initCoords();
		compute();
	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	private void createOutput() {
		randomPoint = kernel.getGeoFactory()
				.newPoint(polygon.isGeoElement3D() ? 3 : 2, cons);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomPointIn;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = polygon;

		setOnlyOutput(randomPoint);
		setDependencies(); // done by AlgoElement
	}

	public GeoPointND getRandomPoint() {
		return randomPoint;
	}

	GeoPolygon getPolygon() {
		return polygon;
	}

	// find random point in the polygon P[0], ..., P[n]
	@Override
	public void compute() {

		int size = polygon.getPointsLength();

		GeoPointND p = polygon.getPointND(0);
		double xMax = p.getInhomX();
		double xMin = p.getInhomX();
		double yMax = p.getInhomY();
		double yMin = p.getInhomY();

		for (int i = 0; i < size; i++) {
			p = polygon.getPoint(i);
			double x = p.getInhomX();
			double y = p.getInhomY();

			if (Double.isInfinite(x)
					|| Double.isInfinite(y) || Double.isNaN(x)
					|| Double.isNaN(y)) {
				randomPoint.setUndefined();
				return;
			}

			if (i == 0) {
				continue;
			}

			if (xMax < x) {
				xMax = x;
			}
			if (xMin > x) {
				xMin = x;
			}
			if (yMax < y) {
				yMax = y;
			}
			if (yMin > y) {
				yMin = y;
			}
		}

		boolean foundRandom = false;
		double xRandom, yRandom;

		while (!foundRandom) {
			xRandom = xMin
					+ (xMax - xMin) * cons.getApplication().getRandomNumber();
			yRandom = yMin
					+ (yMax - yMin) * cons.getApplication().getRandomNumber();

			if (polygon.isInRegion(xRandom, yRandom)) {
				if (polygon.isGeoElement3D()) {
					randomPoint.setCoords(
							polygon.getCoordSys().getPoint(xRandom, yRandom),
							false);
				} else {
					randomPoint.setCoords(xRandom, yRandom, 1);
				}
				foundRandom = true;
			}
		}
	}

	@Override
	public boolean setRandomValue(GeoElementND val) {
		if (val instanceof GeoPointND) {
			double inhomX = ((GeoPointND) val).getInhomX();
			double inhomY = ((GeoPointND) val).getInhomY();
			if (GeoPolygon.isInRegion(inhomX, inhomY, polygon.getPoints())) {
				randomPoint.setCoords(inhomX, inhomY, 1);
				return true;
			}
		}
		return false;
	}
}

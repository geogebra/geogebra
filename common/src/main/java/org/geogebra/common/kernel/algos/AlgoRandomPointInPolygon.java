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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoRandomPointInPolygon extends AlgoElement {

	protected GeoPolygon polygon; // input
	protected GeoPoint randomPoint; // output

	public AlgoRandomPointInPolygon(Construction cons, String label,
			GeoPolygon polygon) {
		this(cons, polygon);
		randomPoint.setLabel(label);
	}

	AlgoRandomPointInPolygon(Construction cons, GeoPolygon polygon) {
		super(cons);
		this.polygon = polygon;
		createOutput(cons);
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

	protected void createOutput(Construction cons) {
		randomPoint = new GeoPoint(cons);
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

	public GeoPoint getRandomPoint() {
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
			p = polygon.getPointND(i);
			double x = p.getInhomX();
			double y = p.getInhomY();

			if (p.isGeoElement3D() || Double.isInfinite(x)
					|| Double.isInfinite(y) || Double.isNaN(x)
					|| Double.isNaN(y)) {
				randomPoint.setUndefined();
				return;
			}

			if (i == 0)
				continue;

			if (xMax < x)
				xMax = x;
			if (xMin > x)
				xMin = x;
			if (yMax < y)
				yMax = y;
			if (yMin > y)
				yMin = y;
		}

		boolean foundRandom = false;
		double xRandom, yRandom;

		while (!foundRandom) {
			xRandom = xMin + (xMax - xMin)
					* cons.getApplication().getRandomNumber();
			yRandom = yMin + (yMax - yMin)
					* cons.getApplication().getRandomNumber();

			if (polygon.isInRegion(xRandom, yRandom)) {
				randomPoint.setCoords(xRandom, yRandom, 1);
				foundRandom = true;
			}
		}
	}

	// TODO Consider locusequability

}

/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Random point in a polygon created by P[0],....,P[n]
 *
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoRandomPointInPoints extends AlgoElement {

	protected GeoPointND[] points; // input
	protected GeoPoint randomPoint; // output

	public AlgoRandomPointInPoints(Construction cons, String label,
			GeoPointND[] points) {
		this(cons, points);
		randomPoint.setLabel(label);
	}

	AlgoRandomPointInPoints(Construction cons, GeoPointND[] points) {
		super(cons);
		this.points = points;
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
		input = new GeoElement[points.length];
		for (int i = 0; i < points.length; i++)
			input[i] = (GeoElement) points[i];

		setOnlyOutput(randomPoint);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getRandomPoint() {
		return randomPoint;
	}

	GeoPointND[] getPoints() {
		return points;
	}

	// find random point in the polygon P[0], ..., P[n]
	@Override
	public void compute() {

		int size = points.length;

		GeoPointND p = points[0];
		double xMax = p.getInhomX();
		double xMin = p.getInhomX();
		double yMax = p.getInhomY();
		double yMin = p.getInhomY();

		for (int i = 0; i < size; i++) {
			p = points[i];
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

		GeoPolygon polygon = new GeoPolygon(cons, points);
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

/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.SetRandomValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Random point in a polygon created by P[0],....,P[n].
 */
public class AlgoRandomPointInPoints extends AlgoElement implements SetRandomValue {

	protected GeoPointND[] points; // input
	protected GeoPoint randomPoint; // output
	private GeoList list;

	/**
	 * @param cons
	 *            construction
	 * @param points
	 *            vertices
	 * @param list
	 *            list of vertices
	 */
	public AlgoRandomPointInPoints(Construction cons, GeoPointND[] points, GeoList list) {
		super(cons);
		this.points = points;
		this.list = list;
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

	protected void createOutput(Construction cons1) {
		randomPoint = new GeoPoint(cons1);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomPointIn;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (list != null) {
			input = new GeoElement[] { list };
		} else {
			input = new GeoElement[points.length];
			for (int i = 0; i < points.length; i++) {
				input[i] = (GeoElement) points[i];
			}
		}
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
		if (list != null) {
			updatePointArray();
		}
		int size = points.length;
		if (size == 0) {
			randomPoint.setUndefined();
			return;
		}
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

			if (GeoPolygon.isInRegion(xRandom, yRandom, points)) {
				randomPoint.setCoords(xRandom, yRandom, 1);
				foundRandom = true;
			}
		}
	}

	protected void updatePointArray() {
		// check if we have a point list
		if (!list.getElementType().equals(GeoClass.POINT)
				&& !list.getElementType().equals(GeoClass.POINT3D)) {
			points = new GeoPointND[0];
			return;
		}

		// create new points array
		int size = list.size();
		points = new GeoPointND[size];
		for (int i = 0; i < size; i++) {
			points[i] = (GeoPointND) list.get(i);
		}

	}

	@Override
	public boolean setRandomValue(GeoElementND val) {
		if (val instanceof GeoPointND) {
			double inhomX = ((GeoPointND) val).getInhomX();
			double inhomY = ((GeoPointND) val).getInhomY();
			if (GeoPolygon.isInRegion(inhomX, inhomY, points)) {
				randomPoint.setCoords(inhomX, inhomY, 1);
				return true;
			}
		}
		return false;
	}
}

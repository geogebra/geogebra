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
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Random point in a conic.
 */
public class AlgoRandomPointInConic extends AlgoElement implements SetRandomValue {

	protected GeoConicND conic; // input
	protected GeoPoint randomPoint; // output

	/**
	 * @param cons construction
	 * @param c conic
	 */
	public AlgoRandomPointInConic(Construction cons, GeoConicND c) {
		super(cons);
		this.conic = c;
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
		input = new GeoElement[1];
		input[0] = conic;

		setOnlyOutput(randomPoint);
		setDependencies(); // done by AlgoElement
	}

	public GeoPoint getRandomPoint() {
		return randomPoint;
	}

	GeoConicND getConic() {
		return conic;
	}

	// find random point in the polygon P[0], ..., P[n]
	@Override
	public void compute() {

		if (!conic.isDefined()) {
			randomPoint.setUndefined();
			return;
		}

		int type = conic.getType();
		switch (type) {
		case GeoConicNDConstants.CONIC_CIRCLE:
			GeoVec2D center = conic.getB();

			double r = conic.getHalfAxis(0)
					* Math.sqrt(cons.getApplication().getRandomNumber());
			double radians = 2 * Math.PI
					* cons.getApplication().getRandomNumber();

			double xRandom = r * Math.cos(radians);
			double yRandom = r * Math.sin(radians);

			randomPoint.setCoords(xRandom + center.getX(),
					yRandom + center.getY(), 1);
			break;

		case GeoConicNDConstants.CONIC_ELLIPSE:
			center = conic.getB();

			double a = conic.getHalfAxis(0);
			double b = conic.getHalfAxis(1);
			double angle = Math.atan2(conic.eigenvec[0].getY(),
					conic.eigenvec[0].getX());

			r = Math.sqrt(cons.getApplication().getRandomNumber());
			radians = 2 * Math.PI * cons.getApplication().getRandomNumber();

			xRandom = a * r * Math.cos(radians);
			yRandom = b * r * Math.sin(radians);

			double sin = Math.sin(angle);
			double cos = Math.cos(angle);

			// rotate to math rotation of ellipse
			double x2 = xRandom * cos - yRandom * sin;
			double y2 = xRandom * sin + yRandom * cos;

			randomPoint.setCoords(x2 + center.getX(), y2 + center.getY(), 1);
			break;

		default:
			randomPoint.setUndefined();
		}
	}

	@Override
	public boolean setRandomValue(GeoElementND val) {
		if (val instanceof GeoPointND && conic.isInRegion((GeoPointND) val)) {
			randomPoint.setCoords(((GeoPointND) val).getCoords(), true);
			return true;
		}
		return false;
	}
}

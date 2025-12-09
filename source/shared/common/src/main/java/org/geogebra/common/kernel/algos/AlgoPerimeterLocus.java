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

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusNDInterface;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Adapted from AlgoPerimeterPoly
 */
public class AlgoPerimeterLocus extends AlgoElement {

	// Take a polygon as input
	private GeoLocusNDInterface locus;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric circum;

	/**
	 * @param cons
	 *            construction
	 * @param locus
	 *            locus
	 */
	public AlgoPerimeterLocus(Construction cons,
			GeoLocusNDInterface locus) {
		super(cons);
		this.locus = locus;

		circum = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Perimeter;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = locus.toGeoElement();

		setOnlyOutput(circum);
		setDependencies();
	}

	/**
	 * Compute circumference by adding up the length of its segments.
	 */
	@Override
	public final void compute() {
		if (!locus.isDefined()) {
			circum.setUndefined();
			return;
		}

		ArrayList<? extends MyPoint> points = locus.getPoints();

		if (points.isEmpty()) {
			circum.setUndefined();
			return;
		}

		if (points.size() == 1) {
			circum.setValue(0);
			return;
		}

		MyPoint lastPoint = points.get(0);

		double length = 0;
		for (int i = 1; i < points.size(); i++) {
			MyPoint pt = points.get(i);
			length = length + pt.distance(lastPoint);
			lastPoint = pt;
		}
		circum.setValue(length);
	}

	/**
	 * Get the GeoPolygon's circumference.
	 * 
	 * @return circumference
	 */
	public GeoNumeric getResult() {
		return circum;
	}

}

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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Algorithm to compute the circumference of a
 * {@link org.geogebra.common.kernel.geos.GeoPolygon GeoPolygon}.
 * 
 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
 */
public class AlgoPerimeterPoly extends AlgoElement {

	// Take a polygon as input
	private GeoPolygon polygon;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric circum;

	/**
	 * @param cons
	 *            construction
	 * @param polygon
	 *            polygon
	 */
	public AlgoPerimeterPoly(Construction cons, GeoPolygon polygon) {
		super(cons);
		this.polygon = polygon;

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
		input[0] = polygon;

		setOnlyOutput(circum);
		setDependencies();
	}

	/**
	 * Compute circumference by adding up the length of its segments.
	 */
	@Override
	public final void compute() {
		if (!polygon.isDefined()) {
			circum.setUndefined();
			return;
		}

		GeoSegmentND[] segment = polygon.getSegments();
		double length = 0;
		for (int i = 0; i < polygon.getPointsLength(); i++) {
			length += segment[i].getLength();
		}
		circum.setValue(length);
	}

	/**
	 * Get the GeoPolygon's circumference.
	 * 
	 * @return circumference
	 */
	public GeoNumeric getCircumference() {
		return circum;
	}

}

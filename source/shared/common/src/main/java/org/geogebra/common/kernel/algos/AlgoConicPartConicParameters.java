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
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Arc or sector defined by a conic, start- and end-parameter.
 */
public class AlgoConicPartConicParameters extends AlgoConicPart {

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CONIC_PART_ARC or GeoConicPart.CONIC_PART_ARC
	 */
	public AlgoConicPartConicParameters(Construction cons, String label,
			GeoConicND circle, GeoNumberValue startParameter,
			GeoNumberValue endParameter, int type) {
		super(cons, type);
		conic = circle;
		startParam = startParameter;
		endParam = endParameter;

		conicPart = newGeoConicPart(cons, type);
		setInputOutput(); // for AlgoElement
		compute();

		conicPart.setLabel(label);
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @param partType
	 *            arc/sector
	 * @return new conic part
	 */
	protected GeoConicND newGeoConicPart(Construction cons1, int partType) {
		return new GeoConicPart(cons1, partType);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = conic;
		input[1] = startParam.toGeoElement();
		input[2] = endParam.toGeoElement();

		setOnlyOutput(conicPart);

		setDependencies();
	}

	@Override
	public GeoConicND getConicPart() {
		return super.getConicPart();
	}
}

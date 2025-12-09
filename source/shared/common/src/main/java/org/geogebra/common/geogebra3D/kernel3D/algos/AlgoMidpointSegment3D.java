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

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 *
 * @author mathieu
 */
public class AlgoMidpointSegment3D extends AlgoMidpoint3D {

	private GeoSegmentND segment;

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param segment
	 *            segment
	 */
	AlgoMidpointSegment3D(Construction cons, GeoSegmentND segment) {
		super(cons, segment);
		this.segment = segment;

		setInputOutput();

		// compute M = (P + Q)/2
		compute();
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) segment;

		setOnlyOutput(getPoint());
		setDependencies(); // done by AlgoElement
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("MidpointOfA",
				segment.getLabel(tpl));

	}

}

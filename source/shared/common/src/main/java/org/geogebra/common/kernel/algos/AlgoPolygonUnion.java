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
import org.geogebra.common.kernel.geos.GeoPolygon;

/**
 * Computes union of two polygons
 * 
 * @author George Sturr
 */
public class AlgoPolygonUnion extends AlgoPolygonOperation {

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for output
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 */
	public AlgoPolygonUnion(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.UNION);
		initialize(null);
	}

	/**
	 * @param cons
	 *            construction
	 * @param labels
	 *            labels for output
	 * @param inPoly0
	 *            first input polygon
	 * @param inPoly1
	 *            second input polygon
	 * @param outputSizes
	 *            sizes of the results of the operation. Consist of polygon
	 *            size, point size, and segment size
	 */
	public AlgoPolygonUnion(Construction cons, String[] labels,
			GeoPolygon inPoly0, GeoPolygon inPoly1, int[] outputSizes) {

		super(cons, labels, inPoly0, inPoly1, PolyOperation.UNION);
		initialize(outputSizes);
	}

	@Override
	public Commands getClassName() {
		return Commands.Union;
	}

}
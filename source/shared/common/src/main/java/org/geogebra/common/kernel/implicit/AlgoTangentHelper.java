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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Helper for tangent through point / with direction to implicit curve
 *
 */
public interface AlgoTangentHelper {
	/** @return locus of possible tangent points */
	GeoImplicit getTangentCurve();

	/**
	 * @return defining object (direction or point)
	 */
	GeoElement getVec();

	/**
	 * @return whether defining object is defined
	 */
	boolean vecDefined();

	/**
	 * @param ip
	 *            output points array
	 * @param tangents
	 *            tangents handler
	 */
	void getTangents(GeoPoint[] ip, OutputHandler<GeoLine> tangents);

	/**
	 * @param geo
	 *            implicit curve
	 * @param line
	 *            line
	 * @return tangent point of line on curve
	 */
	GeoPointND getTangentPoint(GeoElement geo, GeoLine line);

}

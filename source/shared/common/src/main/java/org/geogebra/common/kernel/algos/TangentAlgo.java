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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Common tagging interface for AlgoElement objects calculating tangents
 * 
 * @author Zbynek Konecny
 *
 */
public interface TangentAlgo {
	/**
	 * Returns intersection point of geo object and line if line is defined as
	 * tangent to geo
	 * 
	 * @param geo
	 *            GeoElement
	 * @param line
	 *            Tangent to geo
	 * @return Intersection of geo and line if line is tangent to geo, null
	 *         otherwise
	 */
	GeoPointND getTangentPoint(GeoElement geo, GeoLine line);
}

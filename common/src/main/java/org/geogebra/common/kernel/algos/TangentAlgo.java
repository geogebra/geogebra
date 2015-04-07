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

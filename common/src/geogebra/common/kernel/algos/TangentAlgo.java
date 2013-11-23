package geogebra.common.kernel.algos;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * Common tagging interface for AlgoElement objects calculating tangents
 * @author Zbynek Konecny
 *
 */
public interface TangentAlgo {
	/**
	 * Returns intersection point of geo object and line if line is defined as tangent to geo
	 * @param geo GeoElement
	 * @param line Tangent to geo
	 * @return Intersection of geo and line if line is tangent to geo, null otherwise
	 */
	GeoPoint getTangentPoint(GeoElement geo, GeoLine line);
}

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

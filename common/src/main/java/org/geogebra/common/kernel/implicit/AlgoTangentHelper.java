package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.algos.AlgoElement.OutputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public interface AlgoTangentHelper {

	GeoImplicit getTangentCurve();

	GeoElement getVec();

	boolean vecDefined();

	void getTangents(GeoPoint[] ip, OutputHandler<GeoLine> tangents);

	GeoPointND getTangentPoint(GeoElement geo, GeoLine line);

}

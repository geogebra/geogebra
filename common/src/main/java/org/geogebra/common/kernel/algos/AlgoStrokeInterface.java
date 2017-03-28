package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoPoint;

public interface AlgoStrokeInterface {

	int getPointsLength();

	GeoPoint getPointCopy(int i);

}

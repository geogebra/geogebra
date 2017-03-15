package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.geos.GeoPoint;

public interface AlgoStrokeInterface {

	int getPointsLength();

	int getPointsLengthWihtoutControl();

	GeoPoint getPointCopy(int i);

	GeoPoint getNoControlPointCopy(int i);

}

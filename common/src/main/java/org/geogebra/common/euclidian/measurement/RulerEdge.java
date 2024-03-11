package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

public interface RulerEdge {
	GeoPoint endpoint1();
	GeoPoint endpoint2();
	void update(GeoImage ruler);
}

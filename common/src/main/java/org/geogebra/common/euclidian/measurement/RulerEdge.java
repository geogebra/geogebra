package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

public interface RulerEdge {
	boolean accept(GPoint p, GPoint projection);

	GeoPoint corner1();
	GeoPoint corner2();
	void update(GeoImage ruler);
}

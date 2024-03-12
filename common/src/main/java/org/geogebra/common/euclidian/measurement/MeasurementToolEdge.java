package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Represents an edge of a measurement tool to use pen against.
 */
public interface MeasurementToolEdge {

	/**
	 * @return the first endpoind of the edge.
	 */
	GeoPoint endpoint1();

	/**
	 * @return the second endpoind of the edge.
	 */
	GeoPoint endpoint2();

	/**
	 * Update the edge endpoints based on the tool's image (and current position, rotation).
	 *
	 * @param image of the tool.
	 */
	void update(GeoImage image);
}

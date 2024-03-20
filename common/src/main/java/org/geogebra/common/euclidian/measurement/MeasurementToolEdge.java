package org.geogebra.common.euclidian.measurement;

import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Represents an edge of a measurement tool to use pen against.
 */
public interface MeasurementToolEdge {

	/**
	 * @return the first endpoint of the edge.
	 */
	GeoPoint getEndpoint1();

	/**
	 * @return the second endpoint of the edge.
	 */
	GeoPoint getEndpoint2();

	/**
	 * Update the edge endpoints based on the tool's image (and current position, rotation).
	 *
	 * @param image of the tool.
	 */
	void update(GeoImage image);
}

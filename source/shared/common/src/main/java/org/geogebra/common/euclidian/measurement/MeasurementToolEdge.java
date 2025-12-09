/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

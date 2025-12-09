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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Represents the leg edges of a right triangle.
 */
public final class LegEdge implements MeasurementToolEdge {
	private GeoPoint[] endpoints;

	// GeoImage corners 3 and 4.
	private GeoPoint corner3;
	private GeoPoint corner4;
	private final int cornerIndex;

	/**
	 *
	 * @param cornerIndex to specify which corner this connects to the opposite side's midpoint.
	 */
	public LegEdge(int cornerIndex) {
		this.cornerIndex = cornerIndex;
	}

	@Override
	public GeoPoint getEndpoint1() {
		return endpoints[0];
	}

	@Override
	public GeoPoint getEndpoint2() {
		return endpoints[1];
	}

	@Override
	public void update(GeoImage image) {
		ensureCorners(image.getKernel().getConstruction());
		image.calculateCornerPoint(corner3, 3);
		image.calculateCornerPoint(corner4, 4);
		int i = cornerIndex - 1;
		endpoints[i].x = (corner3.x + corner4.x) / 2;
		endpoints[i].y = (corner3.y + corner4.y) / 2;
		endpoints[i].z = 1;
		image.calculateCornerPoint(endpoints[1 - i], cornerIndex);
		endpoints[0].updateCoords();
		endpoints[1].updateCoords();
	}

	private void ensureCorners(Construction cons) {
		if (endpoints == null) {
			endpoints = new GeoPoint[2];
			endpoints[0] = new GeoPoint(cons, true);
			endpoints[1] = new GeoPoint(cons, true);
			corner3 = new GeoPoint(cons, true);
			corner4 = new GeoPoint(cons, true);
		}
	}
}

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
 * Represents an edge based on one of the image's edge given by two of their corners.
 * For image corners, see {@link GeoImage}.
 */
public final class RectangleEdge implements MeasurementToolEdge {
	private final int cornerIndex1;
	private final int cornerIndex2;
	private GeoPoint corner1 = null;
	private GeoPoint corner2 = null;

	/**
	 *
	 * @param cornerIndex1 image corner index1
	 * @param cornerIndex2 image corner index2
	 */
	public RectangleEdge(int cornerIndex1, int cornerIndex2) {
		this.cornerIndex1 = cornerIndex1;
		this.cornerIndex2 = cornerIndex2;
	}

	@Override
	public GeoPoint getEndpoint1() {
		return corner1;
	}

	@Override
	public GeoPoint getEndpoint2() {
		return corner2;
	}

	@Override
	public void update(GeoImage image) {
		if (image == null) {
			return;
		}

		Construction cons = image.kernel.getConstruction();
		if (corner1 == null) {
			corner1 = new GeoPoint(cons);
			corner2 = new GeoPoint(cons);
		}
		image.calculateCornerPoint(corner1, cornerIndex1);
		image.calculateCornerPoint(corner2, cornerIndex2);
	}
}

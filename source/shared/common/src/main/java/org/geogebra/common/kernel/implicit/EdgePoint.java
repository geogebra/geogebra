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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.DoubleUtil;

/**
 * Represents a point in a given edge as the projection the shape and the edge,
 * specified by EdgeType. This is the base of contour clipping.
 */
public class EdgePoint extends MyPoint {
	private final PointList list;
	private final boolean start;
	private final EdgeType edgeType;

	/**
	 * @param list that the endpoint belongs to
	 * @param point represents the endpoint as {@link MyPoint}
	 * @param edgeType {@link EdgeType}
	 * @param start if the endpoint is start or end of the list
	 */
	public EdgePoint(PointList list, MyPoint point, EdgeType edgeType, boolean start) {
		this.list = list;
		this.edgeType = edgeType;
		this.x = point.x;
		this.y = point.y;
		this.start = start;
	}

	public PointList getList() {
		return list;
	}

	public boolean isStart() {
		return start;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EdgePoint)) {
			return false;
		}

		EdgePoint point = (EdgePoint) obj;
		return DoubleUtil.isEqual(this.x, point.x, 1E-7) && DoubleUtil.isEqual(this.y, point.y, 1E-7);
	}

	@Override
	public String toString() {
		return edgeType + "(" + x + ", " + y + ")";
	}

	@Override
	public int hashCode() {
		return (int) (x + y);
	}

	/**
	 *
	 * @return the type of the edge {@link EdgeType}
	 */
	public EdgeType getEdgeType() {
		return edgeType;
	}
}

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

package org.geogebra.common.euclidian.plot.implicit.classification;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.PointList;

/**
 * Creates contour segments from clipped contour point lists.
 */
public final class ContourSegmentFactory {
	private ContourSegmentFactory() {}

	/**
	 * @param contour source contour points
	 * @param contourId source contour id
	 * @return ordered contour segments created from the source contour
	 */
	public static List<ContourSegment> createSegmentList(PointList contour, int contourId) {
		List<MyPoint> points = contour.asPoints();
		int size = points.size();
		List<ContourSegment> list = new ArrayList<>(size);
		boolean closed = contour.isClosed();
		int segmentCount = closed ? size : size - 1;
		for (int i = 0; i < segmentCount; i++) {
			MyPoint start = points.get(i);
			MyPoint end = points.get((i + 1) % size);
			if (start != null && end != null) {
				list.add(new ContourSegment(start, end, contourId, i));
			}
		}
		return list;
	}
}

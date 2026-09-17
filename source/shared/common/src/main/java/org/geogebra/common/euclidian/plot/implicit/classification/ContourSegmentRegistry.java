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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.geogebra.common.kernel.implicit.PointList;

public final class ContourSegmentRegistry {
	private final Map<SegmentKey, ContourSegment> segmentMap = new HashMap<>();
	private final Supplier<List<ContourSegment>> segmentSupplier;

	public ContourSegmentRegistry(List<PointList> contours) {
		this(() -> createSegments(contours));
	}

	public ContourSegmentRegistry(Supplier<List<ContourSegment>> segmentSupplier) {
		this.segmentSupplier = segmentSupplier;
	}

	/**
	 * Rebuilds the registry from the configured contour-segment supplier.
	 */
	public void build() {
		reset();
		Map<Integer, List<ContourSegment>> byContour = new HashMap<>();
		for (ContourSegment segment : segmentSupplier.get()) {
			byContour
					.computeIfAbsent(segment.getContourId(), key -> new ArrayList<>())
					.add(segment);
		}
		for (List<ContourSegment> segmentList : byContour.values()) {
			segmentList.sort((s1, s2) ->
					Integer.compare(s1.getKey().getSegmentIndex(), s2.getKey().getSegmentIndex()));
			int contourSegmentCount = segmentList.size();
			for (int position = 0; position < contourSegmentCount; position++) {
				ContourSegment s = segmentList.get(position);
				s.setPositionInContour(position);
				s.setContourSegmentCount(contourSegmentCount);
				segmentMap.put(s.getKey(), s);
			}
		}
	}

	private void reset() {
		segmentMap.clear();
	}

	private static List<ContourSegment> createSegments(List<PointList> contours) {
		List<ContourSegment> segments = new ArrayList<>();
		for (int i = 0; i < contours.size(); i++) {
			segments.addAll(ContourSegmentFactory.createSegmentList(contours.get(i), i));
		}
		return segments;
	}

	/**
	 * Returns the segment registered for the given key.
	 *
	 * @param key contour and segment identifier
	 * @return registered segment, or {@code null} if none exists
	 */
	public ContourSegment getSegment(SegmentKey key) {
		return segmentMap.get(key);
	}

	/**
	 * Returns a snapshot of all currently registered segments.
	 *
	 * @return copy of the registry contents
	 */
	public List<ContourSegment> getAllSegments() {
		return new ArrayList<>(segmentMap.values());
	}
}

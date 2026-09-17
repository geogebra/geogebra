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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.PointList;

/**
 * Collects and orders intersections ("hits") between contour segments and
 * the rectangular viewport.
 *
 * <p>Tests each contour segment against viewport edges, snaps near corners,
 * and creates {@link EdgeHit} records. Hits can be de-duplicated and sorted
 * by clockwise perimeter.</p>
 *
 * @apiNote Not thread-safe. Reuse one instance per clipping pass.
 */
public final class PerimeterHitAlgo {
	private final ClipRect clipRect;
	private final ClipEpsilon eps;

	/**
	 * Creates a hit collector for the viewport and tolerances.
	 *
	 * @param clipRect rectangle used for edge tests and perimeter mapping
	 * @param eps      numeric tolerances for world/param/corner snapping
	 */
	public PerimeterHitAlgo(ClipRect clipRect, ClipEpsilon eps) {
		this.clipRect = clipRect;
		this.eps = eps;
		ContourLog.clipRect(clipRect, eps);
	}

	/**
	 * Scans all contours and returns edge hits for their segments.
	 *
	 * <p>Each segment is checked against all four edges. The result is sorted
	 * by clockwise perimeter.</p>
	 *
	 * @param contours closed polylines to scan
	 * @return a new list of hits; empty if no segment intersects
	 */
	public List<EdgeHit> collectHits(List<PointList> contours) {
		List<EdgeHit> hits = new ArrayList<>();
		for (int i = 0; i < contours.size(); i++) {
			PointList contour = contours.get(i);
			List<EdgeHit> hitOneContour = new ArrayList<>();
			collectHits(contour, hitOneContour);
			for (EdgeHit hit : hitOneContour) {
				hit.setContourId(i);
			}
			hits.addAll(hitOneContour);
		}
		sortClockWise(hits);
		return hits;
	}

	/** Adds hits from one contour into {@code hits}. */
	private void collectHits(PointList contour, List<EdgeHit> hits) {
		int size = contour.size();
		for (int i = 0; i < size; i++) {
			MyPoint start = contour.get(i);
			MyPoint end = contour.get((i + 1) % size); // closed loop
			if (start == null || end == null) {
				continue;
			}

			for (ClipEdge edge : ClipEdge.values()) {
				EdgeHit h = edge.intersectSegment(clipRect, start, end, i, eps);
				if (h != null) {
					h.setOwner(contour);
					hits.add(h);
				}
			}
		}
	}

	/**
	 * Removes near-duplicate hits at the same perimeter position.
	 *
	 * <p>When two hits snap to the same corner, keeps the hit whose edge
	 * CW-ends at that corner. Modifies the list in place.</p>
	 *
	 * @param hits hits to de-duplicate; modified in place
	 */
	public void dedup(List<EdgeHit> hits) {
		if (hits.isEmpty()) {
			return;
		}
		final double sEps = 1e-12; // param-space tie epsilon
		List<EdgeHit> out = new ArrayList<>(hits.size());
		EdgeHit prev = null;

		for (EdgeHit h : hits.stream()
				.sorted(
						Comparator.comparingDouble(EdgeHit::sPerimeter).thenComparingDouble(EdgeHit::tOnEdge))
				.collect(Collectors.toList())) {

			if (prev != null && Math.abs(h.sPerimeter() - prev.sPerimeter()) <= sEps) {
				// Same perimeter position (likely a snapped corner): choose one
				// Keep the hit whose edge CW-ends at this corner:
				// (i.e., keep the hit from the edge whose cwCorner == point)
				boolean prevEndsHere = prev.edge().cwCorner(clipRect).isEqual(prev.point());
				boolean hEndsHere = h.edge().cwCorner(clipRect).isEqual(h.point());
				EdgeHit keep = hEndsHere ? h : (prevEndsHere ? prev : h);
				out.remove(out.size() - 1);
				out.add(keep);
				prev = keep;
			} else {
				out.add(h);
				prev = h;
			}
		}
		hits.clear();
		hits.addAll(out);
	}

	/**
	 * Sorts hits by their clockwise perimeter coordinate.
	 *
	 * @param hits hits to sort in place
	 */
	public void sortClockWise(List<EdgeHit> hits) {
		hits.sort(Comparator.comparingDouble(EdgeHit::sPerimeter));
	}
}

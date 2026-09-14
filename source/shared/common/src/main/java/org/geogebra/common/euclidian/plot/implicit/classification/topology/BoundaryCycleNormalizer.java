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

package org.geogebra.common.euclidian.plot.implicit.classification.topology;

import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.boundaryCycleOrder;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.boundingBoxOf;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.classifyPointInPolygon;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.extractFaceBoundary;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.hasNearlyEqualArea;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.hasViewportArea;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.signedAreaOfCycle;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.AREA_RELATIVE_TOLERANCE;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.GEOMETRY_EPSILON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ViewportInfo;

final class BoundaryCycleNormalizer {

	private final PlanarGraph graph;
	private final BoundaryCycleExtractor extractor;

	BoundaryCycleNormalizer(PlanarGraph graph, BoundaryCycleExtractor extractor) {
		this.graph = graph;
		this.extractor = extractor;
	}

	List<BoundaryCycle> canonicalBoundaryCyclesOf(List<BoundaryCycle> cycles) {
		return canonicalBoundaryCyclesOf(cycles, null);
	}

	List<BoundaryCycle> canonicalBoundaryCyclesOf(List<BoundaryCycle> cycles,
			ViewportInfo viewportInfo) {
		List<BoundaryCycle> canonical = new ArrayList<>();
		for (BoundaryCycle cycle : cycles) {
			BoundaryCycle normalized = normalizeForHierarchy(viewportInfo, cycle);
			if (!isDegenerateCanonicalCandidate(normalized)
					&& isRelevantCanonicalCandidate(viewportInfo, normalized)) {
				canonical.add(normalized);
			}
		}
		BoundaryCycle viewportCycle = findViewportCycle(viewportInfo, canonical);
		if (viewportCycle != null && canonical.size() == 1 && cycles.size() > 1) {
			addViewportContainedFallbackCycles(viewportInfo, cycles, viewportCycle, canonical);
		} else if (viewportCycle == null && canonical.size() == 1 && cycles.size() > 1) {
			BoundaryCycle soleCanonical = canonical.get(0);
			int before = canonical.size();
			addViewportContainedFallbackCycles(viewportInfo, cycles, soleCanonical, canonical);
			if (canonical.size() == before) {
				addContainedFallbackCycles(viewportInfo, cycles, soleCanonical, canonical);
			}
		}
		if (viewportCycle != null) {
			addLargerNegativeRecoveryCandidate(viewportInfo, cycles, viewportCycle, canonical);
		}
		addDroppedContainingCycleRecoveryCandidate(viewportInfo, cycles, canonical);
		canonical = dedupCanonicalCyclesBySignature(canonical);
		canonical = dedupCanonicalCyclesGeometrically(canonical);
		canonical.sort(boundaryCycleOrder());
		return canonical;
	}

	List<BoundaryCycle> dedupCanonicalCyclesGeometrically(List<BoundaryCycle> canonical) {
		List<BoundaryCycle> deduped = new ArrayList<>();
		for (BoundaryCycle candidate : canonical) {
			BoundaryCycle duplicate = null;
			for (BoundaryCycle existing : deduped) {
				if (areGeometricallyEquivalentCanonicalCycles(candidate, existing)) {
					duplicate = existing;
					break;
				}
			}
			if (duplicate == null) {
				deduped.add(candidate);
			} else if (isPreferredCanonicalCycle(candidate, duplicate)) {
				deduped.remove(duplicate);
				deduped.add(candidate);
			}
		}
		return deduped;
	}

	private List<BoundaryCycle> dedupCanonicalCyclesBySignature(List<BoundaryCycle> canonical) {
		Map<String, BoundaryCycle> bySignature = new HashMap<>();
		for (BoundaryCycle cycle : canonical) {
			if (isDegenerateCanonicalCandidate(cycle)) {
				continue;
			}
			String signature = undirectedBoundarySignature(cycle);
			BoundaryCycle previous = bySignature.get(signature);
			if (previous == null || isPreferredCanonicalCycle(cycle, previous)) {
				bySignature.put(signature, cycle);
			}
		}
		return new ArrayList<>(bySignature.values());
	}

	private boolean areGeometricallyEquivalentCanonicalCycles(BoundaryCycle first,
			BoundaryCycle second) {
		if (!hasNearlyEqualArea(first, second)) {
			return false;
		}
		PlanarGeometry.BoundingBox firstBounds = boundingBoxOf(graph, first);
		PlanarGeometry.BoundingBox secondBounds = boundingBoxOf(graph, second);
		return firstBounds.hasNearlyEqualBounds(secondBounds);
	}

	private void addDroppedContainingCycleRecoveryCandidate(ViewportInfo viewportInfo,
			List<BoundaryCycle> extractedCycles,
			List<BoundaryCycle> canonical) {
		if (canonical.size() < 2) {
			return;
		}
		BoundaryCycle best = null;
		int bestContainedCount = 0;
		for (BoundaryCycle cycle : extractedCycles) {
			if (cycle.getSignedArea() >= 0 || isDegenerateCanonicalCandidate(cycle)
					|| isViewportLike(viewportInfo, cycle)
					|| !isRelevantCanonicalCandidate(viewportInfo, cycle)) {
				continue;
			}
			int containedCount = countContainedCanonicalCycles(cycle, canonical);
			if (containedCount < 2) {
				continue;
			}
			if (best == null || containedCount > bestContainedCount
					|| (containedCount == bestContainedCount
					&& cycle.getAbsArea() > best.getAbsArea())) {
				best = cycle;
				bestContainedCount = containedCount;
			}
		}
		if (best == null) {
			return;
		}
		int bestId = best.getId();
		for (int i = canonical.size() - 1; i >= 0; i--) {
			if (canonical.get(i).getId() == bestId) {
				canonical.remove(i);
			}
		}
		canonical.add(best);
	}

	private int countContainedCanonicalCycles(BoundaryCycle parent,
			List<BoundaryCycle> canonical) {
		int count = 0;
		for (BoundaryCycle child : canonical) {
			if (child.getId() == parent.getId()
					|| child.getAbsArea() >= parent.getAbsArea()) {
				continue;
			}
			if (classifyPointInPolygon(graph, child.getContainmentProbePoint(), parent)
					== PlanarGraph.Containment.INSIDE) {
				count++;
			}
		}
		return count;
	}

	private boolean isDegenerateCanonicalCandidate(BoundaryCycle cycle) {
		return cycle.getHalfEdgeIds().size() < 3 || cycle.getAbsArea() <= GEOMETRY_EPSILON;
	}

	private boolean isRelevantCanonicalCandidate(ViewportInfo viewportInfo, BoundaryCycle cycle) {
		if (isViewportLike(viewportInfo, cycle)) {
			return true;
		}
		PlanarGeometry.BoundingBox viewportBounds = viewportBoundingBox(viewportInfo);
		if (viewportBounds == null) {
			return true;
		}
		return viewportBounds.intersects(boundingBoxOf(graph, cycle));
	}

	PlanarGeometry.BoundingBox viewportBoundingBox(ViewportInfo viewportInfo) {
		if (viewportInfo == null) {
			return null;
		}
		PlanarGeometry.BoundingBox box = new PlanarGeometry.BoundingBox();
		box.include(viewportInfo.xmin(), viewportInfo.ymin());
		box.include(viewportInfo.xmin(), viewportInfo.ymax());
		box.include(viewportInfo.xmax(), viewportInfo.ymin());
		box.include(viewportInfo.xmax(), viewportInfo.ymax());
		return box;
	}

	private String undirectedBoundarySignature(BoundaryCycle cycle) {
		int size = cycle.getHalfEdgeIds().size();
		int[] undirectedHalfEdgeIds = new int[size];
		for (int i = 0; i < size; i++) {
			int halfEdgeId = cycle.getHalfEdgeIds().get(i);
			int twinHalfEdgeId = graph.halfEdge(halfEdgeId).getTwinHalfEdgeId();
			undirectedHalfEdgeIds[i] = Math.min(halfEdgeId, twinHalfEdgeId);
		}
		java.util.Arrays.sort(undirectedHalfEdgeIds);
		StringBuilder signature = new StringBuilder(size * 4 + 2);
		signature.append('[');
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				signature.append(',');
			}
			signature.append(undirectedHalfEdgeIds[i]);
		}
		signature.append(']');
		return signature.toString();
	}

	private boolean isPreferredCanonicalCycle(BoundaryCycle candidate, BoundaryCycle current) {
		boolean candidatePositive = candidate.getSignedArea() > 0;
		boolean currentPositive = current.getSignedArea() > 0;
		if (candidatePositive != currentPositive) {
			return candidatePositive;
		}
		return candidate.getId() < current.getId();
	}

	private void addLargerNegativeRecoveryCandidate(ViewportInfo viewportInfo,
			List<BoundaryCycle> extractedCycles,
			BoundaryCycle viewportCycle, List<BoundaryCycle> canonical) {
		List<BoundaryCycle> boundedCanonical = new ArrayList<>();
		for (BoundaryCycle cycle : canonical) {
			if (cycle != viewportCycle && !isViewportLike(viewportInfo, cycle)) {
				boundedCanonical.add(cycle);
			}
		}
		if (boundedCanonical.size() != 1) {
			return;
		}
		BoundaryCycle tinyCanonical = boundedCanonical.get(0);
		BoundaryCycle bestNegative = null;
		for (BoundaryCycle cycle : extractedCycles) {
			if (cycle.getSignedArea() >= 0 || cycle == viewportCycle
					|| isViewportLike(viewportInfo, cycle)) {
				continue;
			}
			if (cycle.getAbsArea() <= tinyCanonical.getAbsArea() * 4) {
				continue;
			}
			if (bestNegative == null || cycle.getAbsArea() > bestNegative.getAbsArea()) {
				bestNegative = cycle;
			}
		}
		if (bestNegative == null) {
			return;
		}
		BoundaryCycle normalized = normalizeForHierarchy(viewportInfo, bestNegative);
		if (isDegenerateCanonicalCandidate(normalized)
				|| normalized.getAbsArea() <= tinyCanonical.getAbsArea()
				|| isViewportLike(viewportInfo, normalized)
				|| !isRelevantCanonicalCandidate(viewportInfo, normalized)
				|| !isReasonableViewportFallback(viewportInfo, normalized, viewportCycle)) {
			return;
		}
		String normalizedSignature = undirectedBoundarySignature(normalized);
		for (BoundaryCycle existing : canonical) {
			if (undirectedBoundarySignature(existing).equals(normalizedSignature)) {
				return;
			}
		}
		canonical.add(normalized);
	}

	private void addViewportContainedFallbackCycles(ViewportInfo viewportInfo,
			List<BoundaryCycle> extractedCycles,
			BoundaryCycle viewportCycle, List<BoundaryCycle> canonical) {
		Map<String, BoundaryCycle> fallbackBySignature = new HashMap<>();
		for (BoundaryCycle cycle : extractedCycles) {
			if (cycle == viewportCycle || isViewportLike(viewportInfo, cycle)) {
				continue;
			}
			BoundaryCycle normalized = normalizeForHierarchy(viewportInfo, cycle);
			if (isDegenerateCanonicalCandidate(normalized)
					|| isViewportLike(viewportInfo, normalized)
					|| !isRelevantCanonicalCandidate(viewportInfo, normalized)) {
				continue;
			}
			if (!isReasonableViewportFallback(viewportInfo, normalized, viewportCycle)) {
				continue;
			}
			String signature = undirectedBoundarySignature(normalized);
			BoundaryCycle previous = fallbackBySignature.get(signature);
			if (previous == null || isPreferredCanonicalCycle(normalized, previous)) {
				fallbackBySignature.put(signature, normalized);
			}
		}
		if (fallbackBySignature.isEmpty()) {
			BoundaryCycle contourCandidate = smallestNonViewportFallbackCycle(
					viewportInfo, extractedCycles,
					viewportCycle);
			if (contourCandidate != null) {
				BoundaryCycle normalized = normalizeForHierarchy(viewportInfo, contourCandidate);
				if (!isDegenerateCanonicalCandidate(normalized)
						&& isRelevantCanonicalCandidate(viewportInfo, normalized)) {
					fallbackBySignature.put(undirectedBoundarySignature(normalized), normalized);
				}
			}
		}
		canonical.addAll(fallbackBySignature.values());
	}

	private BoundaryCycle smallestNonViewportFallbackCycle(ViewportInfo viewportInfo,
			List<BoundaryCycle> extractedCycles,
			BoundaryCycle viewportCycle) {
		BoundaryCycle best = null;
		for (BoundaryCycle cycle : extractedCycles) {
			if (cycle == viewportCycle || isViewportLike(viewportInfo, cycle)
					|| isDegenerateCanonicalCandidate(cycle)) {
				continue;
			}
			if (best == null || cycle.getAbsArea() < best.getAbsArea()) {
				best = cycle;
			}
		}
		return best;
	}

	private void addContainedFallbackCycles(ViewportInfo viewportInfo,
			List<BoundaryCycle> extractedCycles,
			BoundaryCycle parentCycle, List<BoundaryCycle> canonical) {
		Map<String, BoundaryCycle> fallbackBySignature = new HashMap<>();
		for (BoundaryCycle cycle : extractedCycles) {
			if (cycle == parentCycle) {
				continue;
			}
			BoundaryCycle normalized = normalizeForHierarchy(viewportInfo, cycle);
			if (isDegenerateCanonicalCandidate(normalized)
					|| !isRelevantCanonicalCandidate(viewportInfo, normalized)
					|| !isReasonableContainedFallback(normalized, parentCycle)) {
				continue;
			}
			String signature = undirectedBoundarySignature(normalized);
			BoundaryCycle previous = fallbackBySignature.get(signature);
			if (previous == null || isPreferredCanonicalCycle(normalized, previous)) {
				fallbackBySignature.put(signature, normalized);
			}
		}
		canonical.addAll(fallbackBySignature.values());
	}

	private boolean isReasonableContainedFallback(BoundaryCycle cycle, BoundaryCycle parentCycle) {
		if (cycle.getAbsArea() >= parentCycle.getAbsArea()) {
			return false;
		}
		PlanarGraph.Containment containment = classifyPointInPolygon(graph,
				cycle.getContainmentProbePoint(), parentCycle);
		return containment == PlanarGraph.Containment.INSIDE;
	}

	private BoundaryCycle normalizeForHierarchy(ViewportInfo viewportInfo, BoundaryCycle cycle) {
		if (cycle.getSignedArea() > 0) {
			return cycle;
		}
		List<Integer> reversedBoundary = new ArrayList<>(cycle.getHalfEdgeIds());
		Collections.reverse(reversedBoundary);
		BoundaryCycle reversed = new BoundaryCycle(cycle.getId(), reversedBoundary,
				signedAreaOfCycle(graph, reversedBoundary),
				extractor.containmentProbePoint(reversedBoundary));
		reversed.ensureGeometry(graph);
		List<Integer> twinBoundary = extractTwinBoundaryCycle(cycle);
		BoundaryCycle twin = new BoundaryCycle(cycle.getId(), twinBoundary,
				signedAreaOfCycle(graph, twinBoundary),
				extractor.containmentProbePoint(twinBoundary));
		twin.ensureGeometry(graph);
		return preferredNormalizedCycle(viewportInfo, reversed, twin);
	}

	private BoundaryCycle preferredNormalizedCycle(ViewportInfo viewportInfo, BoundaryCycle first,
			BoundaryCycle second) {
		double epsilon = AREA_RELATIVE_TOLERANCE;
		boolean firstMeaningful = first.getAbsArea() > epsilon;
		boolean secondMeaningful = second.getAbsArea() > epsilon;
		if (firstMeaningful != secondMeaningful) {
			return firstMeaningful ? first : second;
		}
		boolean firstViewportLike = isViewportLike(viewportInfo, first);
		boolean secondViewportLike = isViewportLike(viewportInfo, second);
		if (firstViewportLike != secondViewportLike) {
			return firstViewportLike ? second : first;
		}
		if (Math.abs(first.getAbsArea() - second.getAbsArea()) > epsilon) {
			return first.getAbsArea() < second.getAbsArea() ? first : second;
		}
		return first.getId() <= second.getId() ? first : second;
	}

	private boolean isReasonableViewportFallback(ViewportInfo viewportInfo, BoundaryCycle cycle,
			BoundaryCycle viewportCycle) {
		if (cycle.getAbsArea() >= viewportCycle.getAbsArea()) {
			return false;
		}
		if (viewportCornerCount(viewportInfo, cycle) == 0
				&& cycle.getAbsArea() > GEOMETRY_EPSILON) {
			return true;
		}
		boolean hasInsideVertex = false;
		double[] xCoordinates = cycle.getXCoordinates(graph);
		double[] yCoordinates = cycle.getYCoordinates(graph);
		for (int i = 0; i < cycle.size(); i++) {
			PlanarGraph.Containment containment = classifyPointInPolygon(graph,
					new GPoint2D(xCoordinates[i], yCoordinates[i]), viewportCycle);
			if (containment == PlanarGraph.Containment.OUTSIDE) {
				return false;
			}
			if (containment == PlanarGraph.Containment.INSIDE) {
				hasInsideVertex = true;
			}
		}
		return hasInsideVertex || cycle.getContainmentProbePoint() != null;
	}

	private boolean isViewportLike(ViewportInfo viewportInfo, BoundaryCycle cycle) {
		if (viewportInfo == null) {
			return false;
		}
		if (viewportInfo.hasValidAbsArea() && hasViewportArea(viewportInfo, cycle)) {
			return true;
		}
		return viewportCornerCount(viewportInfo, cycle) == 4;
	}

	/**
	 * Finds a viewport-like cycle among canonicalization candidates.
	 * <p>
	 * This runs before containment hierarchy and face construction, so it scans all
	 * current candidates rather than only root cycles. The result is used only to
	 * steer normalization recovery, for example adding cycles that were dropped
	 * because only the viewport boundary survived canonicalization.
	 * </p>
	 *
	 * @param viewportInfo viewport metadata with corner vertices and optional area
	 * @param cycles current canonicalization candidates
	 * @return viewport-like cycle, or {@code null} if none can be identified
	 */
	private BoundaryCycle findViewportCycle(ViewportInfo viewportInfo, List<BoundaryCycle> cycles) {
		if (viewportInfo == null) {
			return null;
		}
		BoundaryCycle best = null;
		int bestCornerCount = 0;
		for (BoundaryCycle cycle : cycles) {
			if (!viewportInfo.hasValidAbsArea()) {
				int cornerCount = viewportCornerCount(viewportInfo, cycle);
				if (cornerCount > bestCornerCount
						|| (cornerCount == bestCornerCount && best != null
						&& cycle.getAbsArea() > best.getAbsArea())) {
					best = cycle;
					bestCornerCount = cornerCount;
				}
				continue;
			}
			if (hasViewportArea(viewportInfo, cycle)) {
				return cycle;
			}
			int cornerCount = viewportCornerCount(viewportInfo, cycle);
			if (cornerCount > bestCornerCount
					|| (cornerCount == bestCornerCount && best != null
					&& cycle.getAbsArea() > best.getAbsArea())) {
				best = cycle;
				bestCornerCount = cornerCount;
			}
		}
		return bestCornerCount == 4 ? best : null;
	}

	int viewportCornerCount(ViewportInfo viewportInfo, BoundaryCycle cycle) {
		if (viewportInfo == null) {
			return 0;
		}
		boolean topLeft = false;
		boolean topRight = false;
		boolean bottomLeft = false;
		boolean bottomRight = false;
		for (int halfEdgeId : cycle.getHalfEdgeIds()) {
			int vertexId = graph.halfEdge(halfEdgeId).getOriginVertexId();
			topLeft |= vertexId == viewportInfo.topLeft();
			topRight |= vertexId == viewportInfo.topRight();
			bottomLeft |= vertexId == viewportInfo.bottomLeft();
			bottomRight |= vertexId == viewportInfo.bottomRight();
		}
		int count = topLeft ? 1 : 0;
		count += topRight ? 1 : 0;
		count += bottomLeft ? 1 : 0;
		count += bottomRight ? 1 : 0;
		return count;
	}

	List<Integer> extractTwinBoundaryCycle(BoundaryCycle cycle) {
		int twinStart = graph.halfEdge(cycle.getStartHalfEdgeId()).getTwinHalfEdgeId();
		return extractFaceBoundary(graph, twinStart, new HashSet<>());
	}

}

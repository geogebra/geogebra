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

import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.classifyPointInPolygon;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.AREA_RELATIVE_TOLERANCE;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.GEOMETRY_EPSILON;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.util.debug.Log;

/**
 * Assigns containment parents and hierarchy depths to canonical boundary cycles.
 */
final class BoundaryHierarchyBuilder {

	private static final boolean HIERARCHY_DEBUG_LOGGING = false;
	private final PlanarGraph graph;

	BoundaryHierarchyBuilder(PlanarGraph graph) {
		this.graph = graph;
	}

	void build(List<BoundaryCycle> cycles) {
		buildHierarchy(cycles);
		assignBoundaryDepths(cycles);
	}

	/**
	 * Assigns each boundary cycle to its smallest containing parent cycle.
	 * @param cycles canonical cycles to organize into a containment hierarchy
	 */
	void buildHierarchy(List<BoundaryCycle> cycles) {
		List<BoundaryCycle> candidates = new ArrayList<>();
		for (BoundaryCycle inner : cycles) {
			candidates.clear();
			for (BoundaryCycle outer : cycles) {
				if (inner == outer
						|| inner.getAbsArea() >= outer.getAbsArea()
						|| BoundaryUtils.hasNearlyEqualArea(inner, outer)) {
					continue;
				}
				if (inner.getSignedArea() < 0 && outer.getSignedArea() < 0) {
					continue;
				}
				PlanarGraph.Containment containment =
						classifyPointInPolygon(graph, inner.getContainmentProbePoint(), outer);
				logParentCandidate(inner, outer, containment);
				if (containment == PlanarGraph.Containment.BOUNDARY) {
					throw new IllegalStateException(
							"Ambiguous boundary hierarchy: probe point lies on " + "candidate parent boundary");
				}
				if (containment == PlanarGraph.Containment.INSIDE) {
					candidates.add(outer);
				}
			}
			candidates.sort(Comparator.comparingDouble(BoundaryCycle::getAbsArea));
			if (candidates.size() > 1) {
				double area0 = candidates.get(0).getAbsArea();
				double area1 = candidates.get(1).getAbsArea();
				if (Math.abs(area0 - area1)
						<= Math.max(GEOMETRY_EPSILON, area0 * AREA_RELATIVE_TOLERANCE)) {
					throw new IllegalStateException(
							"Ambiguous boundary hierarchy: multiple equally small " + "parent candidates");
				}
			}
			BoundaryCycle parent = candidates.isEmpty() ? null : candidates.get(0);
			if (parent != null) {
				logAssignedParent(inner, parent, "candidate");
				inner.setParentId(parent.getId());
				parent.addChildId(inner.getId());
			}
		}
		reconcileNestedRoots(cycles);
	}

	private void reconcileNestedRoots(List<BoundaryCycle> cycles) {
		List<BoundaryCycle> roots = new ArrayList<>();
		for (BoundaryCycle cycle : cycles) {
			if (cycle.getParentId() == -1) {
				roots.add(cycle);
			}
		}
		roots.sort(Comparator.comparingDouble(BoundaryCycle::getAbsArea));
		for (int i = 0; i < roots.size(); i++) {
			BoundaryCycle inner = roots.get(i);
			if (inner.getParentId() != -1) {
				continue;
			}
			for (int j = i + 1; j < roots.size(); j++) {
				BoundaryCycle outer = roots.get(j);
				PlanarGraph.Containment probeContainment =
						classifyPointInPolygon(graph, inner.getContainmentProbePoint(), outer);
				BoundaryContainmentCheck boundaryCheck = boundaryContainmentOf(inner, outer);
				logRootPair(inner, outer, probeContainment, boundaryCheck);
				if (isNestedRoot(inner, outer, probeContainment, boundaryCheck)) {
					logAssignedParent(inner, outer, parentAssignmentReason(probeContainment));
					inner.setParentId(outer.getId());
					outer.addChildId(inner.getId());
					break;
				}
			}
		}
	}

	private boolean isNestedRoot(
			BoundaryCycle inner,
			BoundaryCycle outer,
			PlanarGraph.Containment probeContainment,
			BoundaryContainmentCheck boundaryCheck) {
		if (!boundaryCheck.contained() || inner.getAbsArea() >= outer.getAbsArea()) {
			return false;
		}
		return probeContainment == PlanarGraph.Containment.INSIDE
				|| isBadProbeWithContainedBoundary(probeContainment, boundaryCheck);
	}

	private boolean isBadProbeWithContainedBoundary(
			PlanarGraph.Containment probeContainment, BoundaryContainmentCheck boundaryCheck) {
		return probeContainment == PlanarGraph.Containment.OUTSIDE
				&& boundaryCheck.outsideVertices() == 0
				&& boundaryCheck.insideVertices() > 0;
	}

	private String parentAssignmentReason(PlanarGraph.Containment probeContainment) {
		return probeContainment == PlanarGraph.Containment.INSIDE
				? "root-reconcile"
				: "boundary-contained-fallback";
	}

	private BoundaryContainmentCheck boundaryContainmentOf(BoundaryCycle inner, BoundaryCycle outer) {
		double[] xCoordinates = inner.getXCoordinates(graph);
		double[] yCoordinates = inner.getYCoordinates(graph);
		List<Integer> halfEdgeIds = inner.getHalfEdgeIds();
		boolean hasStrictlyInsideVertex = false;
		int insideVertices = 0;
		int boundaryVertices = 0;
		int outsideVertices = 0;
		int firstOutsideHalfEdgeId = -1;
		GPoint2D firstOutsidePoint = null;
		for (int i = 0; i < inner.size(); i++) {
			GPoint2D point = new GPoint2D(xCoordinates[i], yCoordinates[i]);
			PlanarGraph.Containment containment = classifyPointInPolygon(graph, point, outer);
			if (containment == PlanarGraph.Containment.OUTSIDE) {
				outsideVertices++;
				if (firstOutsideHalfEdgeId == -1) {
					firstOutsideHalfEdgeId = halfEdgeIds.get(i);
					firstOutsidePoint = point;
				}
				continue;
			}
			if (containment == PlanarGraph.Containment.INSIDE) {
				hasStrictlyInsideVertex = true;
				insideVertices++;
			} else {
				boundaryVertices++;
			}
		}
		return new BoundaryContainmentCheck(
				outsideVertices == 0 && hasStrictlyInsideVertex,
				insideVertices,
				boundaryVertices,
				outsideVertices,
				firstOutsideHalfEdgeId,
				firstOutsidePoint);
	}

	/**
	 * Assigns hierarchy depths from root cycles down to nested child cycles.
	 * @param cycles boundary cycles with parent-child links already assigned
	 */
	void assignBoundaryDepths(List<BoundaryCycle> cycles) {
		Map<Integer, BoundaryCycle> byId = indexBoundaryCycles(cycles);
		for (BoundaryCycle cycle : cycles) {
			if (cycle.getParentId() == -1) {
				assignBoundaryDepth(cycle, 0, byId);
			}
		}
	}

	private void assignBoundaryDepth(
			BoundaryCycle cycle, int depth, Map<Integer, BoundaryCycle> byId) {
		cycle.setDepth(depth);
		for (int childId : cycle.getChildIds()) {
			assignBoundaryDepth(byId.get(childId), depth + 1, byId);
		}
	}

	Map<Integer, BoundaryCycle> indexBoundaryCycles(List<BoundaryCycle> cycles) {
		Map<Integer, BoundaryCycle> byId = new HashMap<>();
		for (BoundaryCycle cycle : cycles) {
			byId.put(cycle.getId(), cycle);
		}
		return byId;
	}

	private void logParentCandidate(
			BoundaryCycle inner, BoundaryCycle outer, PlanarGraph.Containment containment) {
		if (!HIERARCHY_DEBUG_LOGGING || !hasOverlappingBox(inner, outer)) {
			return;
		}
		Log.debug("[BoundaryHierarchy] parent candidate"
				+ " inner=" + cycleSummary(inner)
				+ " outer=" + cycleSummary(outer)
				+ " probeContainment=" + containment
				+ " reverseProbeContainment="
				+ classifyPointInPolygon(graph, outer.getContainmentProbePoint(), inner));
	}

	private void logRootPair(
			BoundaryCycle inner,
			BoundaryCycle outer,
			PlanarGraph.Containment probeContainment,
			BoundaryContainmentCheck boundaryCheck) {
		if (!HIERARCHY_DEBUG_LOGGING || !hasOverlappingBox(inner, outer)) {
			return;
		}
		Log.debug("[BoundaryHierarchy] root pair"
				+ " inner=" + cycleSummary(inner)
				+ " outer=" + cycleSummary(outer)
				+ " probeContainment=" + probeContainment
				+ " reverseProbeContainment="
				+ classifyPointInPolygon(graph, outer.getContainmentProbePoint(), inner)
				+ " boundaryContained=" + boundaryCheck.contained()
				+ " insideVertices=" + boundaryCheck.insideVertices()
				+ " boundaryVertices=" + boundaryCheck.boundaryVertices()
				+ " outsideVertices=" + boundaryCheck.outsideVertices()
				+ " firstOutsideEdge=" + boundaryCheck.firstOutsideHalfEdgeId()
				+ " firstOutsidePoint=" + boundaryCheck.firstOutsidePoint());
	}

	private void logAssignedParent(BoundaryCycle inner, BoundaryCycle parent, String reason) {
		if (!HIERARCHY_DEBUG_LOGGING) {
			return;
		}
		Log.debug("[BoundaryHierarchy] assigned parent"
				+ " reason=" + reason
				+ " inner=" + cycleSummary(inner)
				+ " parent=" + cycleSummary(parent));
	}

	private String cycleSummary(BoundaryCycle cycle) {
		return cycle.getId()
				+ "{area=" + cycle.getSignedArea()
				+ ",abs=" + cycle.getAbsArea()
				+ ",edges=" + cycle.getHalfEdgeIds().size()
				+ ",parent=" + cycle.getParentId()
				+ ",probe=" + cycle.getContainmentProbePoint()
				+ ",box=" + boxOf(cycle)
				+ "}";
	}

	private boolean hasOverlappingBox(BoundaryCycle first, BoundaryCycle second) {
		PlanarGeometry.BoundingBox firstBox = first.getBoundingBox(graph);
		PlanarGeometry.BoundingBox secondBox = second.getBoundingBox(graph);
		return firstBox.maxX >= secondBox.minX
				&& secondBox.maxX >= firstBox.minX
				&& firstBox.maxY >= secondBox.minY
				&& secondBox.maxY >= firstBox.minY;
	}

	private Box boxOf(BoundaryCycle cycle) {
		PlanarGeometry.BoundingBox boundingBox = cycle.getBoundingBox(graph);
		return new Box(boundingBox.minX, boundingBox.minY, boundingBox.maxX, boundingBox.maxY);
	}

	private record BoundaryContainmentCheck(
			boolean contained,
			int insideVertices,
			int boundaryVertices,
			int outsideVertices,
			int firstOutsideHalfEdgeId,
			GPoint2D firstOutsidePoint) {}

	private record Box(double minX, double minY, double maxX, double maxY) {
		@Override
		public String toString() {
			return "[" + minX + "," + maxX + "]x[" + minY + "," + maxY + "]";
		}
	}
}

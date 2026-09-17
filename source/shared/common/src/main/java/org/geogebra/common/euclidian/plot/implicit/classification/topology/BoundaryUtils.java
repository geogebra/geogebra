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

import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.AREA_RELATIVE_TOLERANCE;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.GEOMETRY_EPSILON;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.ViewportInfo;

/**
 * Shared boundary-cycle predicates and measurements used by topology builders.
 */
final class BoundaryUtils {
	private BoundaryUtils() {}

	/**
	 * @return the closed boundary reached from the given half-edge, or an empty
	 * list if the boundary was already visited
	 */
	static List<Integer> extractFaceBoundary(
			PlanarGraph graph, int startHalfEdgeId, Set<Integer> visited) {
		List<Integer> cycle = new ArrayList<>();
		Set<Integer> cycleVisited = new HashSet<>();
		int nextId = startHalfEdgeId;
		while (true) {
			if (nextId == -1) {
				throw new IllegalStateException("Broken half-edge linkage: next = -1");
			}
			if (cycleVisited.contains(nextId)) {
				if (nextId == startHalfEdgeId) {
					break;
				}
				int cycleStart = cycle.indexOf(nextId);
				visited.addAll(cycleVisited);
				return new ArrayList<>(cycle.subList(cycleStart, cycle.size()));
			}
			if (visited.contains(nextId)) {
				visited.addAll(cycleVisited);
				return List.of();
			}
			cycle.add(nextId);
			cycleVisited.add(nextId);
			nextId = graph.halfEdge(nextId).getNextHalfEdgeId();
		}
		visited.addAll(cycleVisited);
		return cycle;
	}

	/**
	 * @return signed polygon area of the boundary cycle
	 */
	static double signedAreaOfCycle(PlanarGraph graph, List<Integer> cycle) {
		int start = cycle.get(0);
		int current = start;
		Set<Integer> visited = new HashSet<>();
		double sum = 0;
		while (true) {
			if (!visited.add(current)) {
				if (current == start) {
					break;
				}
				throw new IllegalStateException(
						"Broken half-edge linkage: revisited edge before cycle closure");
			}
			int next = graph.halfEdge(current).getNextHalfEdgeId();
			if (next == -1) {
				throw new IllegalStateException("Broken half-edge linkage: face boundary has next = -1");
			}
			Vertex v1 = graph.vertex(graph.halfEdge(current).getOriginVertexId());
			Vertex v2 = graph.vertex(graph.halfEdge(next).getOriginVertexId());
			sum += v1.getX() * v2.getY() - v1.getY() * v2.getX();
			current = next;
			if (current == start) {
				break;
			}
		}
		return 0.5 * sum;
	}

	/**
	 * @return deterministic ordering for boundary hierarchy and diagnostics
	 */
	static Comparator<BoundaryCycle> boundaryCycleOrder() {
		return Comparator.comparingInt(BoundaryCycle::getDepth)
				.thenComparing(Comparator.comparingDouble(BoundaryCycle::getAbsArea).reversed())
				.thenComparingInt(BoundaryCycle::getId);
	}

	/**
	 * @return whether the two cycles have equivalent area at graph tolerance
	 */
	static boolean hasNearlyEqualArea(BoundaryCycle first, BoundaryCycle second) {
		double area0 = first.getAbsArea();
		double area1 = second.getAbsArea();
		return Math.abs(area0 - area1)
				<= Math.max(GEOMETRY_EPSILON, Math.max(area0, area1) * AREA_RELATIVE_TOLERANCE);
	}

	/**
	 * @return point containment relative to the given boundary cycle
	 */
	static PlanarGraph.Containment classifyPointInPolygon(
			PlanarGraph graph, GPoint2D p, BoundaryCycle polygon) {
		PlanarGeometry.BoundingBox boundingBox = polygon.getBoundingBox(graph);
		if (!containsInBoundingBox(boundingBox, p)) {
			return PlanarGraph.Containment.OUTSIDE;
		}
		double[] xCoordinates = polygon.getXCoordinates(graph);
		double[] yCoordinates = polygon.getYCoordinates(graph);
		boolean inside = false;
		int size = polygon.size();
		for (int i = 0, j = size - 1; i < size; j = i++) {
			double xi = xCoordinates[i];
			double yi = yCoordinates[i];
			double xj = xCoordinates[j];
			double yj = yCoordinates[j];
			if (PlanarGeometry.isPointOnSegment(p, xj, yj, xi, yi)) {
				return PlanarGraph.Containment.BOUNDARY;
			}
			boolean intersects =
					((yi > p.y) != (yj > p.y)) && (p.x < (xj - xi) * (p.y - yi) / (yj - yi) + xi);
			if (intersects) {
				inside = !inside;
			}
		}
		return inside ? PlanarGraph.Containment.INSIDE : PlanarGraph.Containment.OUTSIDE;
	}

	/**
	 * @return point containment relative to the given half-edge boundary
	 */
	static PlanarGraph.Containment classifyPointInPolygon(
			PlanarGraph graph, GPoint2D p, List<Integer> cycle) {
		boolean inside = false;
		int size = cycle.size();
		for (int i = 0, j = size - 1; i < size; j = i++) {
			Vertex vi = graph.vertex(graph.halfEdge(cycle.get(i)).getOriginVertexId());
			Vertex vj = graph.vertex(graph.halfEdge(cycle.get(j)).getOriginVertexId());
			double xi = vi.getX();
			double yi = vi.getY();
			double xj = vj.getX();
			double yj = vj.getY();
			if (PlanarGeometry.isPointOnSegment(p, xj, yj, xi, yi)) {
				return PlanarGraph.Containment.BOUNDARY;
			}
			boolean intersects =
					((yi > p.y) != (yj > p.y)) && (p.x < (xj - xi) * (p.y - yi) / (yj - yi) + xi);
			if (intersects) {
				inside = !inside;
			}
		}
		return inside ? PlanarGraph.Containment.INSIDE : PlanarGraph.Containment.OUTSIDE;
	}

	/**
	 * @return coordinate bounds of the given half-edge boundary
	 */
	static PlanarGeometry.BoundingBox boundingBoxOf(PlanarGraph graph, List<Integer> boundary) {
		PlanarGeometry.BoundingBox boundingBox = new PlanarGeometry.BoundingBox();
		for (int halfEdgeId : boundary) {
			Vertex vertex = graph.vertex(graph.halfEdge(halfEdgeId).getOriginVertexId());
			boundingBox.include(vertex.getX(), vertex.getY());
		}
		return boundingBox;
	}

	/**
	 * @return coordinate bounds of the given cached boundary cycle
	 */
	static PlanarGeometry.BoundingBox boundingBoxOf(PlanarGraph graph, BoundaryCycle cycle) {
		return cycle.getBoundingBox(graph);
	}

	private static boolean containsInBoundingBox(
			PlanarGeometry.BoundingBox boundingBox, GPoint2D point) {
		return point.x >= boundingBox.minX - GEOMETRY_EPSILON
				&& point.x <= boundingBox.maxX + GEOMETRY_EPSILON
				&& point.y >= boundingBox.minY - GEOMETRY_EPSILON
				&& point.y <= boundingBox.maxY + GEOMETRY_EPSILON;
	}

	/**
	 * @return whether the cycle area matches the viewport area at graph tolerance
	 */
	static boolean hasViewportArea(ViewportInfo viewportInfo, BoundaryCycle cycle) {
		double tolerance =
				Math.max(AREA_RELATIVE_TOLERANCE, viewportInfo.absArea() * AREA_RELATIVE_TOLERANCE);
		return Math.abs(cycle.getAbsArea() - viewportInfo.absArea()) <= tolerance;
	}
}

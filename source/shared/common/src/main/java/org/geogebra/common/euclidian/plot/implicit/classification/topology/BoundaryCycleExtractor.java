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

import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.extractFaceBoundary;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.signedAreaOfCycle;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGeometry.isPointOnSegment;
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph.GEOMETRY_EPSILON;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GPoint2D;

/**
 * Extracts closed boundary cycles from the graph's active linked half-edges.
 * <p>
 * The extracted cycles are the raw input for canonicalization and face
 * hierarchy construction. Each cycle also carries area and a containment probe
 * used by later topology steps.
 * </p>
 */
final class BoundaryCycleExtractor {
	private final PlanarGraph graph;

	/**
	 * @param graph graph with active linked half-edges
	 */
	BoundaryCycleExtractor(PlanarGraph graph) {
		this.graph = graph;
	}

	/**
	 * @return raw boundary cycles found in the current graph topology
	 */
	List<BoundaryCycle> extract() {
		List<List<Integer>> boundaries = new ArrayList<>();
		Set<Integer> visited = new HashSet<>();
		for (int id = 0; id < graph.halfEdgeCount(); id++) {
			HalfEdge halfEdge = graph.halfEdge(id);
			if (halfEdge.isActive() && !visited.contains(id)) {
				List<Integer> boundary = extractFaceBoundary(graph, id, visited);
				if (!boundary.isEmpty()) {
					boundaries.add(boundary);
				}
			}
		}
		List<BoundaryCycle> cycles = new ArrayList<>(boundaries.size());
		for (int i = 0; i < boundaries.size(); i++) {
			List<Integer> boundary = boundaries.get(i);
			BoundaryCycle cycle = new BoundaryCycle(
					i, boundary, signedAreaOfCycle(graph, boundary), containmentProbePoint(boundary));
			cycle.ensureGeometry(graph);
			cycles.add(cycle);
		}
		return cycles;
	}

	/**
	 * Builds a lightweight off-boundary probe used for cycle containment and hierarchy checks.
	 * This is not a validated interior sample point for the final face classification step.
	 * @param cycle half-edge ids forming a boundary cycle
	 * @return point suitable for boundary-cycle containment checks
	 */
	GPoint2D containmentProbePoint(List<Integer> cycle) {
		for (int halfEdgeId : cycle) {
			HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
			Vertex origin = graph.vertex(halfEdge.getOriginVertexId());
			Vertex target = graph.vertex(halfEdge.getTargetVertexId());
			double dx = target.getX() - origin.getX();
			double dy = target.getY() - origin.getY();
			double length = Math.hypot(dx, dy);
			if (length <= GEOMETRY_EPSILON) {
				continue;
			}
			double midX = (origin.getX() + target.getX()) * 0.5;
			double midY = (origin.getY() + target.getY()) * 0.5;
			double leftNormalX = -dy / length;
			double leftNormalY = dx / length;
			double epsilon = Math.max(GEOMETRY_EPSILON, length * 1e-3);
			for (int attempt = 0; attempt < 12; attempt++) {
				GPoint2D probe = new GPoint2D(midX + epsilon * leftNormalX, midY + epsilon * leftNormalY);
				if (classifyPointOnCycleBoundary(probe, cycle) != PlanarGraph.Containment.BOUNDARY) {
					return probe;
				}
				epsilon *= 0.5;
			}
		}

		throw new IllegalStateException("Cannot compute probe point for degenerate boundary cycle");
	}

	private PlanarGraph.Containment classifyPointOnCycleBoundary(GPoint2D p, List<Integer> cycle) {
		for (int i = 0, size = cycle.size(); i < size; i++) {
			Vertex vi = graph.vertex(graph.halfEdge(cycle.get(i)).getOriginVertexId());
			Vertex vj = graph.vertex(graph.halfEdge(cycle.get((i + 1) % size)).getOriginVertexId());
			if (isPointOnSegment(p, vi.getX(), vi.getY(), vj.getX(), vj.getY())) {
				return PlanarGraph.Containment.BOUNDARY;
			}
		}
		return PlanarGraph.Containment.OUTSIDE;
	}
}

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

import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;

record OpenFragmentClosureInput(List<Integer> orderedViewportVertexIds,
		ViewportTopology viewportTopology, List<OpenFragmentChain> chains,
		int openFragmentCount) {

	record OpenFragmentChain(int fragmentId, ClippedFragment fragment,
			ViewportEndpoint start, ViewportEndpoint end, List<Integer> forwardChain,
			List<Integer> reverseChain) {
	}

	record ViewportEndpoint(int vertexId, int viewportIndex) {
	}

	static final class ViewportTopology {
		private final PlanarGraph graph;
		private final List<Integer> orderedViewportVertexIds;
		private final Map<Integer, Integer> viewportIndices;

		ViewportTopology(PlanarGraph graph, List<Integer> orderedViewportVertexIds) {
			this.graph = graph;
			this.orderedViewportVertexIds = List.copyOf(orderedViewportVertexIds);
			viewportIndices = new HashMap<>();
			for (int i = 0; i < orderedViewportVertexIds.size(); i++) {
				viewportIndices.put(orderedViewportVertexIds.get(i), i);
			}
		}

		int indexOf(int vertexId) {
			Integer index = viewportIndices.get(vertexId);
			return index == null ? -1 : index;
		}

		List<Integer> arc(int fromVertex, int toVertex, boolean forward) {
			int size = orderedViewportVertexIds.size();
			int fromIndex = indexOf(fromVertex);
			int toIndex = indexOf(toVertex);
			if (fromIndex < 0 || toIndex < 0 || size < 2) {
				return List.of();
			}
			List<Integer> arc = new ArrayList<>();
			int index = fromIndex;
			while (index != toIndex) {
				int nextIndex = forward ? (index + 1) % size : (index - 1 + size) % size;
				int start = orderedViewportVertexIds.get(index);
				int end = orderedViewportVertexIds.get(nextIndex);
				int halfEdgeId = viewportEdge(start, end);
				if (halfEdgeId < 0) {
					return List.of();
				}
				arc.add(halfEdgeId);
				index = nextIndex;
			}
			return arc;
		}

		int step(int fromVertex, boolean forward) {
			int size = orderedViewportVertexIds.size();
			int fromIndex = indexOf(fromVertex);
			if (fromIndex < 0 || size < 2) {
				return -1;
			}
			int toIndex = forward ? (fromIndex + 1) % size : (fromIndex - 1 + size) % size;
			int toVertex = orderedViewportVertexIds.get(toIndex);
			return viewportEdge(fromVertex, toVertex);
		}

		int viewportEdge(int startVertex, int endVertex) {
			for (int halfEdgeId : graph.vertex(startVertex).getOutgoingHalfEdges()) {
				HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
				if (halfEdge.isActive() && halfEdge.isViewportEdge()
						&& halfEdge.getTargetVertexId() == endVertex) {
					return halfEdgeId;
				}
			}
			return -1;
		}
	}
}

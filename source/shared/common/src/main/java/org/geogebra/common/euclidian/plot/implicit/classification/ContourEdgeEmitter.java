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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;

final class ContourEdgeEmitter {
	private final PlanarGraph graph;
	private final FragmentTopologyRegistry state;
	private final VertexCache vertexCache;

	ContourEdgeEmitter(PlanarGraph graph, FragmentTopologyRegistry state, VertexCache vertexCache) {
		this.graph = graph;
		this.state = state;
		this.vertexCache = vertexCache;
	}

	void emit(List<BoundarySegment> segments) {
		Map<Integer, LinkedHashSet<String>> emittedByFragment = new HashMap<>();
		for (BoundarySegment segment : segments) {
			if (segment.paramCount() < 2) {
				continue;
			}
			LinkedHashSet<String> emittedEdges = emittedByFragment.get(segment.fragmentId);
			if (emittedEdges == null) {
				emittedEdges = new LinkedHashSet<>();
				emittedByFragment.put(segment.fragmentId, emittedEdges);
			}
			double startT = segment.paramAt(0);
			double x0 = segment.xAt(startT);
			double y0 = segment.yAt(startT);
			int startVertex = vertexCache.findOrAddToGraph(x0, y0);
			for (int i = 0; i < segment.paramCount() - 1; i++) {
				double endT = segment.paramAt(i + 1);
				double x1 = segment.xAt(endT);
				double y1 = segment.yAt(endT);
				int endVertex = vertexCache.findOrAddToGraph(x1, y1);
				if (startVertex != endVertex
						&& shouldAddContourSubEdge(
								segment, startVertex, endVertex, segment.contourId, emittedEdges)) {
					int halfEdgeId = addContourSubEdge(startVertex, endVertex, segment.contourId);
					List<Integer> list = state.getForwardContourEdgesBy(segment.fragmentId);
					// GWT does not have full support for computeIfAbsent with lambda expression.
					if (list == null) {
						list = new ArrayList<>();
						state.recordForwardContourEdgesBy(segment.fragmentId, list);
					}
					list.add(halfEdgeId);
				}
				startVertex = endVertex;
			}
		}
	}

	private boolean shouldAddContourSubEdge(
			BoundarySegment segment,
			int startVertex,
			int endVertex,
			int contourId,
			LinkedHashSet<String> emittedEdges) {
		String edgeKey = undirectedContourEdgeKey(startVertex, endVertex, contourId);
		if (emittedEdges.add(edgeKey)) {
			return true;
		}
		if (segment.closed || state.openViewportFragments().size() != 1) {
			return false;
		}
		List<Integer> chain = state.getForwardContourEdgesBy(segment.fragmentId);
		if (chain == null || chain.isEmpty()) {
			return false;
		}
		int previousEdge = chain.get(chain.size() - 1);
		return graph.halfEdge(previousEdge).getTargetVertexId() == startVertex;
	}

	private String undirectedContourEdgeKey(int v0, int v1, int contourId) {
		int from = Math.min(v0, v1);
		int to = Math.max(v0, v1);
		return contourId + ":" + from + "-" + to;
	}

	void addContourSubEdge(GPoint2D p0, GPoint2D p1, int contourId) {
		addContourSubEdge(p0.x, p0.y, p1.x, p1.y, contourId);
	}

	void addContourSubEdge(double x0, double y0, double x1, double y1, int contourId) {
		int v0 = vertexCache.findOrAddToGraph(x0, y0);
		int v1 = vertexCache.findOrAddToGraph(x1, y1);
		addContourSubEdge(v0, v1, contourId);
	}

	private int addContourSubEdge(int v0, int v1, int contourId) {
		if (v0 == v1) {
			return -1;
		}
		return graph.addContourEdge(v0, v1, contourId);
	}
}

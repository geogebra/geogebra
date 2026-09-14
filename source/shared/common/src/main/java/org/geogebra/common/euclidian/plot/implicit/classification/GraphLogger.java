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

import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryCycle;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Face;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.util.debug.Log;

public class GraphLogger {
	static final boolean TOPOLOGY_DEBUG_LOGGING = false;
	static final boolean TOPOLOGY_FAILURE_LOGGING = true;

	private final PlanarGraph graph;
	private ViewportInfo viewportInfo = null;
	private OpenFragmentClosureResult lastClosureResult =
			OpenFragmentClosureResult.successEmpty();

	private String formatRect(GRectangle2D rect) {
		return "[" + rect.getMinX() + "," + rect.getMaxX()
				+ "]x[" + rect.getMinY() + "," + rect.getMaxY() + "]";
	}

	public GraphLogger(PlanarGraph graph) {
		this.graph = graph;
	}

	void logStage(String stage, GRectangle2D rect, List<ClippedFragment> fragments,
			long buildSignature) {
		if (!TOPOLOGY_DEBUG_LOGGING || !"after-buildTopology".equals(stage)) {
			return;
		}
		Log.debug("[GraphBuilder] stage=" + stage
				+ " rect=" + formatRect(rect)
				+ " fragments=" + fragments.size()
				+ " signature=" + buildSignature
				+ " " + debugSummary());
	}

	/**
	 * @return compact graph state summary for topology diagnostics
	 */
	public String debugSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append("vertices=").append(graph.getVertices().size());
		sb.append(" halfEdges=").append(graph.getHalfEdges().size());
		sb.append(" activeHalfEdges=").append(activeHalfEdgeCount());
		sb.append(" faces=").append(graph.getFaces().size());
		sb.append(" extractedCycles=").append(graph.getLastExtractedBoundaryCycles().size());
		sb.append(" canonicalCycles=").append(graph.getLastCanonicalBoundaryCycles().size());
		sb.append(" viewportVertices=");
		if (viewportInfo != null) {
			appendViewportVertexSummary(sb, viewportInfo.topLeft(), "TL");
			appendViewportVertexSummary(sb, viewportInfo.topRight(), "TR");
			appendViewportVertexSummary(sb, viewportInfo.bottomRight(), "BR");
			appendViewportVertexSummary(sb, viewportInfo.bottomLeft(), "BL");
		} else {
			sb.append(" <viewport is not set!>");
		}
		appendCycleSummary(sb, " extracted", graph.getLastExtractedBoundaryCycles());
		appendCycleSummary(sb, " canonical", graph.getLastCanonicalBoundaryCycles());
		appendFaceSummary(sb);
		sb.append(" openClosure={").append(closureSummary(lastClosureResult)).append('}');
		return sb.toString();
	}

	private void appendClosureFailures(StringBuilder sb,
			List<OpenFragmentClosureResult.Failure> failures) {
		sb.append('[');
		int limit = Math.min(failures.size(), 4);
		for (int i = 0; i < limit; i++) {
			if (i > 0) {
				sb.append(';');
			}
			OpenFragmentClosureResult.Failure failure = failures.get(i);
			sb.append(failure.fragmentId()).append(':').append(failure.reason());
			if (failure.halfEdgeId() >= 0) {
				sb.append("@halfEdge=").append(failure.halfEdgeId());
				sb.append(",owner=").append(failure.ownerFragmentId());
				sb.append(",kind=").append(failure.edgeKind());
			}
		}
		if (failures.size() > limit) {
			sb.append(";...");
		}
		sb.append(']');
	}

	private int activeHalfEdgeCount() {
		int count = 0;
		for (HalfEdge halfEdge : graph.getHalfEdges()) {
			if (halfEdge.isActive()) {
				count++;
			}
		}
		return count;
	}

	private void appendFaceSummary(StringBuilder sb) {
		List<Face> faces = graph.getFaces();
		if (faces.isEmpty()) {
			return;
		}
		sb.append(" faceSummary=[");
		int limit = Math.min(faces.size(), 4);
		for (int i = 0; i < limit; i++) {
			if (i > 0) {
				sb.append("; ");
			}
			Face face = faces.get(i);
			sb.append(face.getId())
					.append(face.isExterior() ? ":ext" : ":int")
					.append(" outer=").append(face.getOuterHalfEdgeId())
					.append(" holes=").append(face.getHoleHalfEdgeIds().size())
					.append(" bbox=").append(face.getOuterHalfEdgeId() == -1 ? "n/a"
							: boundaryBounds(graph.outerBoundaryOf(face)))
					.append(" sample=").append(face.getSamplePoint() == null ? "null"
							: formatPoint(face.getSamplePoint().x, face.getSamplePoint().y));
		}
		if (faces.size() > limit) {
			sb.append("; ...");
		}
		sb.append(']');
	}

	private void appendCycleSummaryEntry(StringBuilder sb, BoundaryCycle cycle) {
		sb.append(cycle.getId())
				.append(":area=").append(cycle.getSignedArea())
				.append(":edges=").append(cycle.getHalfEdgeIds().size())
				.append(":parent=").append(cycle.getParentId())
				.append(":depth=").append(cycle.getDepth());

		GPoint2D probePoint = cycle.getContainmentProbePoint();
		if (probePoint != null) {
			sb.append(":probe=").append(formatPoint(probePoint.x,
					probePoint.y));
		} else {
			sb.append(":probe=null");
		}
		sb.append(":bbox=").append(boundaryBounds(cycle.getHalfEdgeIds()));
	}

	static String formatPoint(double x, double y) {
		return "(" + x + "," + y + ")";
	}

	private String boundaryBounds(List<Integer> boundary) {
		double minX = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (int halfEdgeId : boundary) {
			Vertex vertex = graph.vertex(graph.halfEdge(halfEdgeId).getOriginVertexId());
			minX = Math.min(minX, vertex.getX());
			maxX = Math.max(maxX, vertex.getX());
			minY = Math.min(minY, vertex.getY());
			maxY = Math.max(maxY, vertex.getY());
		}
		return "[" + minX + "," + maxX + "]x[" + minY + "," + maxY + "]";
	}

	private void appendCycleSummary(StringBuilder sb, String label, List<BoundaryCycle> cycles) {
		if (cycles.isEmpty()) {
			return;
		}
		sb.append(label).append("=[");
		int limit = Math.min(cycles.size(), 6);
		for (int i = 0; i < limit; i++) {
			if (i > 0) {
				sb.append("; ");
			}
			appendCycleSummaryEntry(sb, cycles.get(i));
		}
		if (cycles.size() > limit) {
			sb.append("; ...");
		}
		sb.append(']');
	}

	private void appendViewportVertexSummary(StringBuilder sb, int vertexId, String label) {
		sb.append(' ').append(label).append('=');
		if (vertexId < 0 || vertexId >= graph.getVertices().size()) {
			sb.append("n/a");
			return;
		}
		Vertex vertex = graph.vertex(vertexId);
		sb.append(vertex.getId()).append('[');
		List<Integer> outgoing = vertex.getOutgoingHalfEdges();
		for (int i = 0; i < outgoing.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			int halfEdgeId = outgoing.get(i);
			HalfEdge halfEdge = graph.halfEdge(halfEdgeId);
			sb.append(halfEdgeId)
					.append(':')
					.append(halfEdge.getOriginVertexId())
					.append("->")
					.append(halfEdge.getTargetVertexId());
			if (!halfEdge.isActive()) {
				sb.append('x');
			}
		}
		sb.append(']');
	}

	void topologyError(GRectangle2D rect, String errorMessage,
			List<ClippedFragment> fragments, long buildSignature) {
		Log.debug("[GraphBuilder] build failed stageSummary=" + debugSummary()
				+ " rect=" + formatRect(rect)
				+ " fragments=" + fragments.size()
				+ " signature=" + buildSignature
				+ " error=" + errorMessage);
	}

	public void setViewportInfo(ViewportInfo viewportInfo) {
		this.viewportInfo = viewportInfo;
	}

	void setLastClosureResult(OpenFragmentClosureResult closureResult) {
		lastClosureResult = closureResult;
	}

	void logClosureResult(OpenFragmentClosureResult result) {
		if (!TOPOLOGY_FAILURE_LOGGING || !result.isIncomplete()) {
			return;
		}
		Log.debug("[GraphBuilder] open fragment closure incomplete "
				+ closureSummary(result));
	}

	String closureSummary(OpenFragmentClosureResult result) {
		StringBuilder sb = new StringBuilder();
		sb.append("status=").append(result.status());
		sb.append(" open=").append(result.openFragments());
		sb.append(" accepted=").append(result.acceptedClosures());
		sb.append(" skipped=").append(result.skippedClosures());
		sb.append(" conflicts=").append(result.conflicts());
		sb.append(" missingEndpoints=").append(result.missingEndpoints());
		sb.append(" missingChains=").append(result.missingChains());
		sb.append(" failedComplement=").append(result.failedComplementWirings());
		if (!result.failures().isEmpty()) {
			sb.append(" failures=");
			appendClosureFailures(sb, result.failures());
		}
		return sb.toString();
	}

}

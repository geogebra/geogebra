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

import static org.geogebra.common.euclidian.plot.implicit.classification.GraphLogger.TOPOLOGY_DEBUG_LOGGING;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragmentsResult;
import org.geogebra.common.euclidian.plot.implicit.FragmentEndpoint;
import org.geogebra.common.euclidian.plot.implicit.ImplicitPlotTimings;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;

/**
 * Builds and updates the planar graph used for implicit-region classification.
 * <p>
 * Currently responsible for:
 * viewport boundary initialization, viewport-edge splitting from perimeter hits,
 * and contour-edge insertion with vertex reuse.
 * </p>
 */
public class GraphBuilder {
	private final PlanarGraph graph = new PlanarGraph();
	private final EpsilonPolicy epsilonPolicy;
	private final VertexCache vertexCache;
	private final ContourEdgeEmitter contourEdgeEmitter;

	private ViewportInfo viewportInfo;
	private long lastBuildSignature = Long.MIN_VALUE;
	private boolean hasReusableBuild;
	private final FragmentTopologyRegistry state;
	private final GraphLogger logger;
	private final OpenFragmentClosureProcessor openFragmentClosureProcessor;
	private final BoundarySegmentProcessor boundarySegmentProcessor;
	private final GraphBuildSignature signature = new GraphBuildSignature();
	private final List<Integer> orderedViewportVertexIds = new ArrayList<>();

	private record PerimeterVertex(double perimeter, GPoint2D point) {

	}

	private record OrderedViewportVertex(double perimeter, int vertexId) {

	}

	/**
	 * @param epsilonPolicy tolerances used during vertex reuse and future graph construction
	 */
	public GraphBuilder(EpsilonPolicy epsilonPolicy) {
		this.epsilonPolicy = epsilonPolicy;
		logger = new GraphLogger(graph);
		state = new FragmentTopologyRegistry();
		vertexCache = new VertexCache(graph, epsilonPolicy.getVertexMerge());
		openFragmentClosureProcessor = new OpenFragmentClosureProcessor(graph, state,
				epsilonPolicy, vertexCache);
		boundarySegmentProcessor = new BoundarySegmentProcessor(epsilonPolicy);
		contourEdgeEmitter = new ContourEdgeEmitter(graph, state, vertexCache);
	}

	/**
	 * Builds the full mixed boundary graph from clipped fragments and viewport bounds.
	 *
	 * @param rect current viewport bounds
	 * @param fragmentsResult explicit clipped fragment output
	 */
	public void build(GRectangle2D rect, ClippedFragmentsResult fragmentsResult) {
		if (rect == null) {
			return;
		}
		long totalStart = ImplicitPlotTimings.start();
		List<ClippedFragment> fragments = fragmentsResult != null
				? fragmentsResult.fragments() : List.of();
		long buildSignature = signature.of(rect, fragments);
		if (hasReusableBuild && buildSignature == lastBuildSignature) {
			ImplicitPlotTimings.log("GraphBuilder.build.reuse", totalStart,
					"fragments=" + fragments.size());
			return;
		}
		try {
			long stageStart = ImplicitPlotTimings.start();
			clearGraphState();
			state.setLastFragments(fragments);
			ImplicitPlotTimings.log("GraphBuilder.clear", stageStart);
			logger.logStage("after-clear", rect, fragments, buildSignature);
			stageStart = ImplicitPlotTimings.start();
			initializeViewport(rect);
			ImplicitPlotTimings.log("GraphBuilder.initializeViewport", stageStart);
			logger.logStage("after-initializeViewport", rect, fragments, buildSignature);
			stageStart = ImplicitPlotTimings.start();
			addViewportBoundaryVertices(rect, fragments);
			ImplicitPlotTimings.log("GraphBuilder.addViewportBoundaryVertices", stageStart,
					"vertices=" + graph.getVertices().size());
			logger.logStage("after-addViewportBoundaryVertices", rect, fragments, buildSignature);
			stageStart = ImplicitPlotTimings.start();
			long childStart = ImplicitPlotTimings.start();
			List<BoundarySegment> boundarySegments = boundarySegmentProcessor.process(fragments);
			ImplicitPlotTimings.log("GraphBuilder.boundarySegments", childStart,
					"fragments=" + fragments.size()
							+ " segments=" + boundarySegments.size());
			childStart = ImplicitPlotTimings.start();
			contourEdgeEmitter.emit(boundarySegments);
			ImplicitPlotTimings.log("GraphBuilder.emitContourEdges", childStart,
					"vertices=" + graph.getVertices().size()
							+ " halfEdges=" + graph.getHalfEdges().size());
			childStart = ImplicitPlotTimings.start();
			ensureOpenFragmentEndpointsOnViewport();
			ImplicitPlotTimings.log("GraphBuilder.ensureOpenFragmentEndpoints", childStart,
					"openFragments=" + state.openViewportFragments().size()
							+ " vertices=" + graph.getVertices().size()
							+ " halfEdges=" + graph.getHalfEdges().size());
			ImplicitPlotTimings.log("GraphBuilder.addFragmentEdges", stageStart,
					"vertices=" + graph.getVertices().size()
							+ " halfEdges=" + graph.getHalfEdges().size());
			logger.logStage("after-addFragmentEdges", rect, fragments, buildSignature);
			stageStart = ImplicitPlotTimings.start();
			buildTopology();
			ImplicitPlotTimings.log("GraphBuilder.buildTopology", stageStart,
					"faces=" + graph.getFaces().size()
							+ " extracted=" + graph.getLastExtractedBoundaryCycles().size()
							+ " canonical=" + graph.getLastCanonicalBoundaryCycles().size());
			logger.logStage("after-buildTopology", rect, fragments, buildSignature);
			hasReusableBuild = true;
			lastBuildSignature = buildSignature;
		} catch (IllegalStateException e) {
			if (TOPOLOGY_DEBUG_LOGGING) {
				logger.topologyError(rect, e.getMessage(), fragments, buildSignature);
			}
			throw e;
		}
		ImplicitPlotTimings.log("GraphBuilder.build.total", totalStart,
				"fragments=" + fragments.size()
						+ " vertices=" + graph.getVertices().size()
						+ " halfEdges=" + graph.getHalfEdges().size());
	}

	private void initializeViewport(GRectangle2D rect) {
		viewportInfo = ViewportInfo.from(
				graph.addVertex(rect.getMinX(), rect.getMaxY()),
				graph.addVertex(rect.getMaxX(), rect.getMaxY()),
				graph.addVertex(rect.getMinX(), rect.getMinY()),
				graph.addVertex(rect.getMaxX(), rect.getMinY()),
				rect);
		logger.setViewportInfo(viewportInfo);
		vertexCache.put(viewportInfo.topLeft(), rect.getMinX(), rect.getMaxY());
		vertexCache.put(viewportInfo.topRight(), rect.getMaxX(), rect.getMaxY());
		vertexCache.put(viewportInfo.bottomRight(), rect.getMaxX(), rect.getMinY());
		vertexCache.put(viewportInfo.bottomLeft(), rect.getMinX(), rect.getMinY());
	}

	private void clearGraphState() {
		graph.clear();
		vertexCache.clearBuckets();
		openFragmentClosureProcessor.clear();
		orderedViewportVertexIds.clear();
		logger.setLastClosureResult(OpenFragmentClosureResult.successEmpty());
		state.reset();
		lastBuildSignature = Long.MIN_VALUE;
		hasReusableBuild = false;
	}

	private void addViewportBoundaryVertices(GRectangle2D rect, List<ClippedFragment> fragments) {
		List<PerimeterVertex> perimeterVertices = new ArrayList<>();
		perimeterVertices.add(
				new PerimeterVertex(0.0, new GPoint2D(rect.getMinX(), rect.getMaxY())));
		perimeterVertices.add(
				new PerimeterVertex(1.0, new GPoint2D(rect.getMaxX(), rect.getMaxY())));
		perimeterVertices.add(
				new PerimeterVertex(2.0, new GPoint2D(rect.getMaxX(), rect.getMinY())));
		perimeterVertices.add(
				new PerimeterVertex(3.0, new GPoint2D(rect.getMinX(), rect.getMinY())));

		for (ClippedFragment fragment : fragments) {
			addPerimeterEndpoint(perimeterVertices, fragment.start());
			addPerimeterEndpoint(perimeterVertices, fragment.end());
		}

		perimeterVertices.sort(Comparator.comparingDouble(vertex -> vertex.perimeter));
		List<Integer> orderedVertexIds = new ArrayList<>();
		PerimeterVertex previous = null;
		for (PerimeterVertex perimeterVertex : perimeterVertices) {
			if (previous != null
					&& Math.abs(perimeterVertex.perimeter - previous.perimeter)
					<= epsilonPolicy.getSnap()) {
				continue;
			}
			orderedVertexIds.add(findOrAddVertex(perimeterVertex.point));
			previous = perimeterVertex;
		}
		orderedViewportVertexIds.clear();
		orderedViewportVertexIds.addAll(orderedVertexIds);
		openFragmentClosureProcessor.replaceOrderedVertexIds(orderedVertexIds);
		if (orderedVertexIds.size() < 2) {
			return;
		}
		for (int i = 0; i < orderedVertexIds.size(); i++) {
			int start = orderedVertexIds.get(i);
			int end = orderedVertexIds.get((i + 1) % orderedVertexIds.size());
			if (start != end) {
				graph.addViewportEdge(start, end);
			}
		}
	}

	private void ensureOpenFragmentEndpointsOnViewport() {
		if (orderedViewportVertexIds.size() < 2 || !state.hasOpenViewportFragments()) {
			return;
		}
		List<OrderedViewportVertex> orderedVertices = currentOrderedViewportVertices();
		Set<Integer> presentVertexIds = new HashSet<>(orderedViewportVertexIds);
		for (OpenViewportFragment openFragment : state.openViewportFragments()) {
			addMissingViewportEndpoint(orderedVertices, presentVertexIds,
					openFragment.fragment().start());
			addMissingViewportEndpoint(orderedVertices, presentVertexIds,
					openFragment.fragment().end());
		}
		if (orderedVertices.size() == orderedViewportVertexIds.size()) {
			return;
		}
		List<Integer> previousVertexIds = List.copyOf(orderedViewportVertexIds);
		orderedVertices.sort(Comparator.comparingDouble(OrderedViewportVertex::perimeter));
		orderedViewportVertexIds.clear();
		for (OrderedViewportVertex vertex : orderedVertices) {
			orderedViewportVertexIds.add(vertex.vertexId());
		}
		replaceViewportEdges(previousVertexIds, orderedViewportVertexIds);
		openFragmentClosureProcessor.replaceOrderedVertexIds(orderedViewportVertexIds);
	}

	private List<OrderedViewportVertex> currentOrderedViewportVertices() {
		List<OrderedViewportVertex> vertices = new ArrayList<>(orderedViewportVertexIds.size());
		for (int vertexId : orderedViewportVertexIds) {
			vertices.add(new OrderedViewportVertex(perimeterOf(vertexId), vertexId));
		}
		return vertices;
	}

	private void addMissingViewportEndpoint(List<OrderedViewportVertex> orderedVertices,
			Set<Integer> presentVertexIds, FragmentEndpoint endpoint) {
		if (endpoint == null || endpoint.getPoint() == null || endpoint.getEdge() == null
				|| !Double.isFinite(endpoint.getSPerimeter())) {
			return;
		}
		int vertexId = vertexCache.find(endpoint.getPoint().x, endpoint.getPoint().y);
		if (vertexId < 0 || !presentVertexIds.add(vertexId)) {
			return;
		}
		orderedVertices.add(new OrderedViewportVertex(
				normalizePerimeter(endpoint.getSPerimeter()), vertexId));
	}

	private void replaceViewportEdges(List<Integer> previousVertexIds,
			List<Integer> newVertexIds) {
		for (int i = 0; i < previousVertexIds.size(); i++) {
			int start = previousVertexIds.get(i);
			int end = previousVertexIds.get((i + 1) % previousVertexIds.size());
			graph.removeViewportEdge(start, end);
		}
		for (int i = 0; i < newVertexIds.size(); i++) {
			int start = newVertexIds.get(i);
			int end = newVertexIds.get((i + 1) % newVertexIds.size());
			if (start != end) {
				graph.addViewportEdge(start, end);
			}
		}
	}

	private double perimeterOf(int vertexId) {
		double x = graph.vertex(vertexId).getX();
		double y = graph.vertex(vertexId).getY();
		double width = viewportInfo.xmax() - viewportInfo.xmin();
		double height = viewportInfo.ymax() - viewportInfo.ymin();
		double eps = epsilonPolicy.getIntersection();
		if (Math.abs(y - viewportInfo.ymax()) <= eps) {
			return (x - viewportInfo.xmin()) / width;
		}
		if (Math.abs(x - viewportInfo.xmax()) <= eps) {
			return 1.0 + (viewportInfo.ymax() - y) / height;
		}
		if (Math.abs(y - viewportInfo.ymin()) <= eps) {
			return 2.0 + (viewportInfo.xmax() - x) / width;
		}
		return 3.0 + (y - viewportInfo.ymin()) / height;
	}

	private void addPerimeterEndpoint(List<PerimeterVertex> perimeterVertices,
			FragmentEndpoint endpoint) {
		if (endpoint == null || endpoint.getEdge() == null
				|| !Double.isFinite(endpoint.getSPerimeter())) {
			return;
		}
		double perimeter = normalizePerimeter(endpoint.getSPerimeter());
		perimeterVertices.add(new PerimeterVertex(perimeter,
				new GPoint2D(endpoint.getPoint().x, endpoint.getPoint().y)));
	}

	private double normalizePerimeter(double perimeter) {
		double normalized = perimeter % 4.0;
		if (normalized < 0) {
			normalized += 4.0;
		}
		if (Math.abs(4.0 - normalized) <= epsilonPolicy.getSnap()) {
			return 0.0;
		}
		return normalized;
	}

	/**
	 * @return the mutable planar graph being built
	 */
	public PlanarGraph getGraph() {
		return graph;
	}

	/**
	 * Reuses an existing vertex within merge tolerance, or creates a new one.
	 *
	 * @param p point to resolve
	 * @return id of the reused or newly created vertex
	 */
	int findOrAddVertex(GPoint2D p) {
		return vertexCache.findOrAddToGraph(p.x, p.y);
	}

	private void buildTopology() {
		sortVertexOutgoingHalfEdges();
		linkHalfEdges();
		if (state.hasOpenViewportFragments()) {
			processOpenFragments();
		}
		extractFaces();
		identifyExteriorFace();
		computeFaceSamplePoints();
	}

	private void computeFaceSamplePoints() {
		long stageStart;
		stageStart = ImplicitPlotTimings.start();
		graph.computeFaceSamplePoints();
		ImplicitPlotTimings.log("PlanarGraph.computeFaceSamplePoints", stageStart,
				"faces=" + graph.getFaces().size());
	}

	private void identifyExteriorFace() {
		long stageStart = ImplicitPlotTimings.start();
		graph.identifyExteriorFace();
		ImplicitPlotTimings.log("PlanarGraph.identifyExteriorFace", stageStart);
	}

	private void extractFaces() {
		long stageStart = ImplicitPlotTimings.start();
		graph.extractFaces(viewportInfo);
		ImplicitPlotTimings.log("PlanarGraph.extractFaces", stageStart,
				"faces=" + graph.getFaces().size()
						+ " extracted=" + graph.getLastExtractedBoundaryCycles().size()
						+ " canonical=" + graph.getLastCanonicalBoundaryCycles().size());
	}

	private void processOpenFragments() {
		long stageStart = ImplicitPlotTimings.start();
		OpenFragmentClosureResult closureResult = openFragmentClosureProcessor.process(
				viewportInfo);
		logger.setLastClosureResult(closureResult);
		logger.logClosureResult(closureResult);
		ImplicitPlotTimings.log("GraphBuilder.overrideOpenFragmentEndpointLinks",
				stageStart, "status=" + closureResult.status()
						+ " open=" + closureResult.openFragments()
						+ " accepted=" + closureResult.acceptedClosures()
						+ " skipped=" + closureResult.skippedClosures());
		if (closureResult.isIncomplete()) {
			throw new IllegalStateException("open fragment closure incomplete: "
					+ logger.closureSummary(closureResult));
		}
	}

	private void linkHalfEdges() {
		long stageStart;
		stageStart = ImplicitPlotTimings.start();
		graph.linkHalfEdges();
		ImplicitPlotTimings.log("GraphBuilder.linkHalfEdges", stageStart);
	}

	private void sortVertexOutgoingHalfEdges() {
		long stageStart = ImplicitPlotTimings.start();
		graph.sortVertexOutgoingHalfEdges();
		ImplicitPlotTimings.log("GraphBuilder.sortVertexOutgoingHalfEdges", stageStart);
	}

	void addContourSubEdge(GPoint2D p0, GPoint2D p1, int contourId) {
		contourEdgeEmitter.addContourSubEdge(p0, p1, contourId);
	}

	/**
	 * @return compact graph and open-fragment closure state for diagnostics
	 */
	public String debugSummary() {
		return logger.debugSummary();
	}

}

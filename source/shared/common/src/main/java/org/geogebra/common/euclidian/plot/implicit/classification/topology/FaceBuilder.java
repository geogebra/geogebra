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
import static org.geogebra.common.euclidian.plot.implicit.classification.topology.BoundaryUtils.hasViewportArea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.plot.implicit.ImplicitPlotTimings;
import org.geogebra.common.euclidian.plot.implicit.classification.ViewportInfo;
import org.geogebra.common.kernel.MyPoint;

/**
 * Builds graph faces from closed boundary cycles.
 * <p>
 * This class owns the topology step between linked half-edges and classified
 * regions: it extracts boundary cycles, keeps the canonical cycles used for
 * diagnostics, builds containment hierarchy, creates {@link Face} instances,
 * and assigns sample points for non-exterior faces.
 * </p>
 */
final class FaceBuilder {
	private final PlanarGraph graph;
	private final BoundaryHierarchyBuilder boundaryHierarchyBuilder;
	private List<BoundaryCycle> lastExtractedBoundaryCycles = List.of();
	private List<BoundaryCycle> lastCanonicalBoundaryCycles = List.of();
	private final Map<Integer, FaceInteriorPointFinder.FaceContext> sampleContextsByFaceId =
			new HashMap<>();

	private final BoundaryCycleExtractor boundaryCycleExtractor;
	private final BoundaryCycleNormalizer boundaryCycleNormalizer;
	private final FaceInteriorPointFinder faceInteriorPointFinder;
	private ViewportInfo viewportInfo;

	/**
	 * @param graph graph whose linked half-edges will be converted into faces
	 */
	FaceBuilder(PlanarGraph graph) {
		this.graph = graph;
		boundaryCycleExtractor = new BoundaryCycleExtractor(graph);
		boundaryCycleNormalizer = new BoundaryCycleNormalizer(graph, boundaryCycleExtractor);
		faceInteriorPointFinder = new FaceInteriorPointFinder(graph);
		boundaryHierarchyBuilder = new BoundaryHierarchyBuilder(graph);
	}

	/**
	 * Rebuilds all faces from the graph's current active half-edge topology.
	 * @param viewportInfo viewport metadata used to recognize viewport boundary
	 * cycles, or {@code null} when no viewport context is available
	 */
	void extract(ViewportInfo viewportInfo) {
		this.viewportInfo = viewportInfo;
		long stageStart = ImplicitPlotTimings.start();
		List<BoundaryCycle> extractedCycles = boundaryCycleExtractor.extract();
		ImplicitPlotTimings.log(
				"PlanarGraph.extractFaces.extraction", stageStart, "cycles=" + extractedCycles.size());
		lastExtractedBoundaryCycles = List.copyOf(extractedCycles);
		stageStart = ImplicitPlotTimings.start();
		List<BoundaryCycle> canonicalCycles =
				boundaryCycleNormalizer.canonicalBoundaryCyclesOf(extractedCycles, viewportInfo);
		ImplicitPlotTimings.log(
				"PlanarGraph.extractFaces.canonicalization",
				stageStart,
				"cycles=" + canonicalCycles.size());
		lastCanonicalBoundaryCycles = List.copyOf(canonicalCycles);
		stageStart = ImplicitPlotTimings.start();
		boundaryHierarchyBuilder.build(canonicalCycles);
		ImplicitPlotTimings.log(
				"PlanarGraph.extractFaces.hierarchy", stageStart, "cycles=" + canonicalCycles.size());
		stageStart = ImplicitPlotTimings.start();
		buildFacesFromBoundaryHierarchy(canonicalCycles);
		ImplicitPlotTimings.log(
				"PlanarGraph.extractFaces.buildFaces",
				stageStart,
				"faces=" + graph.getFaces().size());
	}

	/**
	 * Extracts all face-boundary cycles from the currently linked active half-edges.
	 * <p>
	 * Preconditions:
	 * outgoing half-edges must already be sorted and {@link PlanarGraph#linkHalfEdges()}
	 * must have assigned valid successor links. Each returned inner list contains
	 * half-edge ids in boundary traversal order for one closed cycle.
	 * </p>
	 * @return closed face-boundary cycles as ordered half-edge id lists
	 * @throws IllegalStateException if the current half-edge linkage is broken or does
	 * not form valid closed cycles
	 */
	List<List<Integer>> extractFaceBoundaries() {
		List<BoundaryCycle> cycles = boundaryCycleExtractor.extract();
		List<List<Integer>> boundaries = new ArrayList<>(cycles.size());
		for (BoundaryCycle cycle : cycles) {
			boundaries.add(new ArrayList<>(cycle.getHalfEdgeIds()));
		}
		return boundaries;
	}

	List<BoundaryCycle> canonicalBoundaryCyclesOf(List<BoundaryCycle> cycles) {
		return boundaryCycleNormalizer.canonicalBoundaryCyclesOf(cycles, null);
	}

	List<BoundaryCycle> canonicalBoundaryCyclesOf(
			List<BoundaryCycle> cycles, ViewportInfo viewportInfo) {
		return boundaryCycleNormalizer.canonicalBoundaryCyclesOf(cycles, viewportInfo);
	}

	/**
	 * @return boundary cycles extracted from active linked half-edges
	 */
	List<BoundaryCycle> extractBoundaryCycles() {
		return boundaryCycleExtractor.extract();
	}

	/**
	 * Checks that face extraction produced exactly one exterior face.
	 */
	void identifyExteriorFace() {
		long existingExteriorCount =
				graph.getFaces().stream().filter(Face::isExterior).count();
		if (existingExteriorCount != 1) {
			throw new IllegalStateException(
					"Expected exactly one exterior face, but found " + existingExteriorCount);
		}
	}

	/**
	 * Creates graph faces from canonical boundary-cycle hierarchy.
	 * @param cycles canonical boundary cycles with parent and depth values
	 */
	void buildFacesFromBoundaryHierarchy(List<BoundaryCycle> cycles) {
		graph.clearFaces();
		sampleContextsByFaceId.clear();
		Map<Integer, BoundaryCycle> byId = boundaryHierarchyBuilder.indexBoundaryCycles(cycles);
		List<BoundaryCycle> roots = cycles.stream()
				.filter(cycle -> cycle.getParentId() == -1)
				.sorted(BoundaryUtils.boundaryCycleOrder())
				.collect(Collectors.toList());
		int exteriorFaceId = graph.addFace();
		graph.face(exteriorFaceId).setExterior(true);
		BoundaryCycle viewportRoot = findViewportRoot(roots);
		if (viewportRoot != null) {
			for (BoundaryCycle child : immediateChildrenOf(viewportRoot, byId)) {
				validateDirectChild(viewportRoot, child);
				graph.face(exteriorFaceId).addHoleHalfEdgeId(child.getStartHalfEdgeId());
				buildFaceFromBoundary(child, byId);
			}
			for (BoundaryCycle root : roots) {
				if (root != viewportRoot) {
					graph.face(exteriorFaceId).addHoleHalfEdgeId(root.getStartHalfEdgeId());
					buildFaceFromBoundary(root, byId);
				}
			}
		} else {
			for (BoundaryCycle root : roots) {
				addExteriorHoleAndBuildFace(exteriorFaceId, root, byId);
			}
		}
	}

	private void addExteriorHoleAndBuildFace(
			int exteriorFaceId, BoundaryCycle boundary, Map<Integer, BoundaryCycle> byId) {
		List<Integer> exteriorBoundary = boundaryCycleNormalizer.extractTwinBoundaryCycle(boundary);
		graph.face(exteriorFaceId).addHoleHalfEdgeId(exteriorBoundary.get(0));
		assignFaceId(exteriorBoundary, exteriorFaceId);
		addExteriorHolesForDirectChildren(exteriorFaceId, boundary, byId);
		buildFaceFromBoundary(boundary, byId);
	}

	private void addExteriorHolesForDirectChildren(
			int exteriorFaceId, BoundaryCycle boundary, Map<Integer, BoundaryCycle> byId) {
		for (BoundaryCycle child : immediateChildrenOf(boundary, byId)) {
			validateDirectChild(boundary, child);
			graph.face(exteriorFaceId).addHoleHalfEdgeId(child.getStartHalfEdgeId());
		}
	}

	/**
	 * Finds the viewport boundary among the top-level hierarchy roots.
	 * <p>
	 * This runs after canonicalization and hierarchy building, so it deliberately
	 * inspects only cycles with no parent. The result controls face construction:
	 * a viewport root means the viewport is the outer container and its children
	 * are visible regions; without one, all roots are treated as ordinary clipped
	 * boundaries adjacent to the exterior face.
	 * </p>
	 *
	 * @param roots canonical boundary cycles with {@code parentId == -1}
	 * @return viewport root, or {@code null} if no root represents the viewport
	 */
	private BoundaryCycle findViewportRoot(List<BoundaryCycle> roots) {
		if (viewportInfo == null) {
			return null;
		}
		if (viewportInfo.hasValidAbsArea()) {
			for (BoundaryCycle root : roots) {
				if (hasViewportArea(viewportInfo, root)) {
					return root;
				}
			}
		}
		BoundaryCycle best = null;
		int bestCornerCount = 0;
		for (BoundaryCycle root : roots) {
			int cornerCount = boundaryCycleNormalizer.viewportCornerCount(viewportInfo, root);
			if (cornerCount > bestCornerCount
					|| (cornerCount == bestCornerCount
							&& best != null
							&& root.getAbsArea() > best.getAbsArea())) {
				best = root;
				bestCornerCount = cornerCount;
			}
		}
		return bestCornerCount == 4 ? best : null;
	}

	private void buildFaceFromBoundary(BoundaryCycle cycle, Map<Integer, BoundaryCycle> byId) {
		int faceId = graph.addFace();
		graph.face(faceId).setOuterHalfEdgeId(cycle.getStartHalfEdgeId());
		assignFaceId(cycle.getHalfEdgeIds(), faceId);
		List<BoundaryCycle> children = immediateChildrenOf(cycle, byId);
		for (BoundaryCycle child : children) {
			validateDirectChild(cycle, child);
			graph.face(faceId).addHoleHalfEdgeId(child.getStartHalfEdgeId());
			assignFaceId(child.getHalfEdgeIds(), faceId);
		}
		sampleContextsByFaceId.put(
				faceId, FaceInteriorPointFinder.FaceContext.from(graph, cycle, children));
		for (BoundaryCycle child : children) {
			buildFaceFromBoundary(child, byId);
		}
	}

	private List<BoundaryCycle> immediateChildrenOf(
			BoundaryCycle cycle, Map<Integer, BoundaryCycle> byId) {
		List<BoundaryCycle> children = new ArrayList<>();
		for (int childId : cycle.getChildIds()) {
			BoundaryCycle child = byId.get(childId);
			if (child != null && child.getDepth() == cycle.getDepth() + 1) {
				children.add(child);
			}
		}
		children.sort(BoundaryUtils.boundaryCycleOrder());
		return children;
	}

	private void validateDirectChild(BoundaryCycle parent, BoundaryCycle child) {
		PlanarGraph.Containment containment =
				classifyPointInPolygon(graph, child.getContainmentProbePoint(), parent);
		if (containment != PlanarGraph.Containment.INSIDE && !isBoundaryContained(child, parent)) {
			throw new IllegalStateException("Inconsistent boundary hierarchy: child boundary "
					+ child.getId() + " is not strictly inside parent boundary " + parent.getId());
		}
	}

	private boolean isBoundaryContained(BoundaryCycle child, BoundaryCycle parent) {
		boolean hasStrictlyInsideVertex = false;
		double[] xCoordinates = child.getXCoordinates(graph);
		double[] yCoordinates = child.getYCoordinates(graph);
		for (int i = 0; i < child.size(); i++) {
			PlanarGraph.Containment containment =
					classifyPointInPolygon(graph, new GPoint2D(xCoordinates[i], yCoordinates[i]), parent);
			if (containment == PlanarGraph.Containment.OUTSIDE) {
				return false;
			}
			if (containment == PlanarGraph.Containment.INSIDE) {
				hasStrictlyInsideVertex = true;
			}
		}
		return hasStrictlyInsideVertex;
	}

	private void assignFaceId(List<Integer> halfEdgeIds, int faceId) {
		for (int halfEdgeId : halfEdgeIds) {
			graph.halfEdge(halfEdgeId).setFaceId(faceId);
		}
	}

	/**
	 * Assigns one predicate sample point to every non-exterior face.
	 */
	void computeFaceSamplePoints() {
		long contextElapsed = 0;
		long searchElapsed = 0;
		int sampledFaces = 0;
		int holes = 0;
		int boundaryEdges = 0;
		for (Face face : graph.getFaces()) {
			if (face.isExterior()) {
				continue;
			}
			long stageStart = ImplicitPlotTimings.start();
			FaceInteriorPointFinder.FaceContext context = sampleContextsByFaceId.get(face.getId());
			if (context == null) {
				context = createSampleContext(face);
			}
			contextElapsed += ImplicitPlotTimings.delta(stageStart);
			holes += context.holeCount();
			boundaryEdges += context.edgeCount();
			stageStart = ImplicitPlotTimings.start();
			face.setSamplePoint(computeFaceSamplePoint(face, context));
			searchElapsed += ImplicitPlotTimings.delta(stageStart);
			sampledFaces++;
		}
		ImplicitPlotTimings.log(
				"PlanarGraph.computeFaceSamplePoints.context",
				elapsedStart(contextElapsed),
				"faces=" + sampledFaces + " holes=" + holes + " edges=" + boundaryEdges);
		ImplicitPlotTimings.log(
				"PlanarGraph.computeFaceSamplePoints.search",
				elapsedStart(searchElapsed),
				"faces=" + sampledFaces + " holes=" + holes + " edges=" + boundaryEdges);
	}

	private FaceInteriorPointFinder.FaceContext createSampleContext(Face face) {
		List<Integer> outerBoundary = graph.outerBoundaryOf(face);
		List<List<Integer>> holeBoundaries = graph.holeBoundariesOf(face);
		List<FaceInteriorPointFinder.BoundaryPath> holePaths = new ArrayList<>();
		for (List<Integer> holeBoundary : holeBoundaries) {
			holePaths.add(FaceInteriorPointFinder.BoundaryPath.from(graph, holeBoundary));
		}
		FaceInteriorPointFinder.BoundaryPath outerPath =
				FaceInteriorPointFinder.BoundaryPath.from(graph, outerBoundary);
		return new FaceInteriorPointFinder.FaceContext(
				outerPath, holePaths, outerPath.getBoundingBox());
	}

	private MyPoint computeFaceSamplePoint(Face face, FaceInteriorPointFinder.FaceContext context) {
		return faceInteriorPointFinder.find(face, context);
	}

	boolean isPointInsideFace(
			GPoint2D point, List<Integer> outerBoundary, List<List<Integer>> holeBoundaries) {
		if (classifyPointInPolygon(graph, point, outerBoundary) != PlanarGraph.Containment.INSIDE) {
			return false;
		}
		for (List<Integer> holeBoundary : holeBoundaries) {
			if (classifyPointInPolygon(graph, point, holeBoundary) != PlanarGraph.Containment.OUTSIDE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Clears diagnostic cycle snapshots and viewport context.
	 */
	void clear() {
		lastExtractedBoundaryCycles = List.of();
		lastCanonicalBoundaryCycles = List.of();
		sampleContextsByFaceId.clear();
		viewportInfo = null;
	}

	/**
	 * @return boundary cycles from the last face extraction pass
	 */
	List<BoundaryCycle> getLastExtractedBoundaryCycles() {
		return lastExtractedBoundaryCycles;
	}

	/**
	 * @return canonical boundary cycles from the last face extraction pass
	 */
	List<BoundaryCycle> getLastCanonicalBoundaryCycles() {
		return lastCanonicalBoundaryCycles;
	}

	private long elapsedStart(long elapsed) {
		return System.currentTimeMillis() - elapsed;
	}
}

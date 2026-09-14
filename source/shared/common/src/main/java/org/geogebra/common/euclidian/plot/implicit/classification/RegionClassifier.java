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
import java.util.List;
import java.util.function.Supplier;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.euclidian.plot.implicit.ClipRect;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragment;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragmentsBuilder;
import org.geogebra.common.euclidian.plot.implicit.ClippedFragmentsResult;
import org.geogebra.common.euclidian.plot.implicit.EdgeHit;
import org.geogebra.common.euclidian.plot.implicit.FragmentEndpoint;
import org.geogebra.common.euclidian.plot.implicit.ImplicitPlotTimings;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Face;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.HalfEdge;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.PlanarGraph;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.Vertex;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.implicit.PointList;
import org.geogebra.common.util.debug.Log;

/**
 * Builds and samples graph regions for clipped implicit contour fragments.
 */
public class RegionClassifier {
	private static final boolean FRAGMENT_RECT_DEBUG = false;
	private static final boolean VISIBLE_SAMPLE_DEBUG = true;
	private static final int[] VISIBLE_SAMPLE_GRID_SIZES = {8, 16, 32};
	private static final double EPS = 1e-12;
	private final Supplier<GRectangle2D> boundsSupplier;
	private final Supplier<ClippedFragmentsResult> clippedFragmentsResultSupplier;
	private final GraphBuilder graphBuilder;
	private boolean classified = false;
	private GetResultsStats activeGetResultsStats;
	private final GPoint2D[] perimeterCandidates = {
			new GPoint2D(),
			new GPoint2D(),
			new GPoint2D(),
			new GPoint2D()
	};

	/**
	 * @param contours clipped contour polylines to classify
	 * @param boundsSupplier current view bounds supplier
	 * @param edgeHits viewport hit supplier used for clipping
	 */
	public RegionClassifier(List<PointList> contours, Supplier<GRectangle2D> boundsSupplier,
			Supplier<List<EdgeHit>> edgeHits) {
		this(() -> createFragmentsResult(contours, boundsSupplier.get(), edgeHits.get()),
					boundsSupplier);
	}

	/**
	 * @param clippedFragmentsResultSupplier clipped contour fragments for one build
	 * @param boundsSupplier current view bounds supplier
	 */
	public RegionClassifier(Supplier<ClippedFragmentsResult> clippedFragmentsResultSupplier,
			Supplier<GRectangle2D> boundsSupplier) {
		this.boundsSupplier = boundsSupplier;
		this.clippedFragmentsResultSupplier = clippedFragmentsResultSupplier;
		EpsilonPolicy epsilonPolicy = EpsilonPolicy.defaults();
		graphBuilder = new GraphBuilder(epsilonPolicy);
	}

	private static ClippedFragmentsResult createFragmentsResult(List<PointList> contours,
			GRectangle2D bounds, List<EdgeHit> hits) {
		if (bounds == null) {
			return new ClippedFragmentsResult(List.of(), List.of());
		}
		List<EdgeHit> safeHits = hits == null ? List.of() : hits;
		ClipRect clipRect = new ClipRect(bounds.getMinX(), bounds.getMaxX(),
				bounds.getMinY(), bounds.getMaxY());
		ClippedFragmentsResult result = ClippedFragmentsBuilder.build(contours, safeHits, clipRect);
		return new ClippedFragmentsResult(result, clipRect);
	}

	/**
	 * Builds the topology graph for the current clipped fragments.
	 * @return whether classification succeeded
	 */
	public boolean process() {
		if (classified) {
			return false;
		}
		long totalStart = ImplicitPlotTimings.start();
		GRectangle2D suppliedRect = boundsSupplier.get();
		if (suppliedRect == null) {
			Log.warn("Bound rectangle is null");
			return false;
		}

		try {
			long stageStart = ImplicitPlotTimings.start();
			ClippedFragmentsResult fragmentsResult = clippedFragmentsResultSupplier.get();
			ImplicitPlotTimings.log("RegionClassifier.fragmentsSupplier", stageStart);
			stageStart = ImplicitPlotTimings.start();
			GRectangle2D rect = classificationRect(suppliedRect, fragmentsResult);
			validateFragmentsAgainstRect(rect, fragmentsResult);
			ImplicitPlotTimings.log("RegionClassifier.prepareRect", stageStart);
			stageStart = ImplicitPlotTimings.start();

			graphBuilder.build(rect, fragmentsResult != null
					? fragmentsResult
					: new ClippedFragmentsResult(List.of(), List.of()));
			ImplicitPlotTimings.log("GraphBuilder.build", stageStart,
					"fragments=" + fragmentCount(fragmentsResult));
			classified = true;
		} catch (IllegalStateException e) {
			Log.debug("[RegionClassifier] topology build failed: " + e.getMessage()
					+ " " + graphBuilder.debugSummary());
			classified = false;
		}
		ImplicitPlotTimings.log("RegionClassifier.process.total", totalStart,
				classified ? "classified=true" : "classified=false");
		return classified;
	}

	private int fragmentCount(ClippedFragmentsResult fragmentsResult) {
		return fragmentsResult == null ? 0 : fragmentsResult.fragments().size();
	}

	private GRectangle2D classificationRect(GRectangle2D suppliedRect,
			ClippedFragmentsResult fragmentsResult) {
		if (fragmentsResult == null || fragmentsResult.clipRect() == null) {
			return suppliedRect;
		}
		ClipRect clipRect = fragmentsResult.clipRect();
		GRectangle2D rect = AwtFactory.getPrototype().newRectangle2D();
		rect.setRect(clipRect.getXmin(), clipRect.getYmin(),
				clipRect.getXmax() - clipRect.getXmin(),
				clipRect.getYmax() - clipRect.getYmin());
		return rect;
	}

	/**
	 * @return graph built by the last successful processing pass
	 */
	public PlanarGraph getGraph() {
		return graphBuilder.getGraph();
	}

	/**
	 * Marks the classifier ready for another processing pass.
	 */
	public void reset() {
		classified = false;
	}

	private void validateFragmentsAgainstRect(GRectangle2D rect,
			ClippedFragmentsResult fragmentsResult) {
		if (!FRAGMENT_RECT_DEBUG || rect == null || fragmentsResult == null) {
			return;
		}
		double eps = EpsilonPolicy.defaults().getIntersection() * 100;
		List<ClippedFragment> fragments = fragmentsResult.fragments();
		for (int fragmentIndex = 0; fragmentIndex < fragments.size(); fragmentIndex++) {
			ClippedFragment fragment = fragments.get(fragmentIndex);
			List<org.geogebra.common.kernel.MyPoint> points = fragment.points();
			for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
				org.geogebra.common.kernel.MyPoint point = points.get(pointIndex);
				if (!isInsideRect(rect, point.x, point.y, eps)) {
					Log.debug("[RegionClassifier] fragment/rect mismatch"
							+ " rect=" + formatRect(rect)
							+ " fragmentIndex=" + fragmentIndex
							+ " contour=" + fragment.sourceContourId()
							+ " pointIndex=" + pointIndex
							+ " point=" + formatPoint(point.x, point.y)
							+ " start=" + fragment.start()
							+ " end=" + fragment.end());
					return;
				}
			}
			if (logEndpointMismatch(rect, fragmentIndex, fragment.start(), "start", eps)) {
				return;
			}
			if (logEndpointMismatch(rect, fragmentIndex, fragment.end(), "end", eps)) {
				return;
			}
		}
	}

	private boolean logEndpointMismatch(GRectangle2D rect, int fragmentIndex,
			FragmentEndpoint endpoint, String label, double eps) {
		if (endpoint == null) {
			return false;
		}
		if (isInsideRect(rect, endpoint.getPoint().x, endpoint.getPoint().y, eps)) {
			return false;
		}
		Log.debug("[RegionClassifier] fragment endpoint/rect mismatch"
				+ " rect=" + formatRect(rect)
				+ " fragmentIndex=" + fragmentIndex
				+ " label=" + label
				+ " endpoint=" + endpoint);
		return true;
	}

	private boolean isInsideRect(GRectangle2D rect, double x, double y, double eps) {
		return x >= rect.getMinX() - eps && x <= rect.getMaxX() + eps
				&& y >= rect.getMinY() - eps && y <= rect.getMaxY() + eps;
	}

	private String formatRect(GRectangle2D rect) {
		return "[" + rect.getMinX() + "," + rect.getMaxX()
				+ "]x[" + rect.getMinY() + "," + rect.getMaxY() + "]";
	}

	private String formatPoint(double x, double y) {
		return "(" + x + "," + y + ")";
	}

	/**
	 * Converts graph faces into drawable regions for the current view.
	 * @param bounds current Euclidian view bounds
	 * @return classified drawable regions
	 */
	public List<ClassifiedRegion> getResults(EuclidianViewBounds bounds) {
		long totalStart = ImplicitPlotTimings.start();
		List<ClassifiedRegion> list = new ArrayList<>();
		GetResultsStats stats = new GetResultsStats();
		activeGetResultsStats = stats;
		int exteriorCount = 0;
		int interiorCount = 0;
		int holeCount = 0;
		VisibleSampleStats visibleSampleStats = new VisibleSampleStats();
		PlanarGraph graph = getGraph();
		try {
			for (Face face : graph.getFaces()) {
				List<List<Integer>> holeBoundaries = graph.holeBoundariesOf(face);
				List<BoundaryPath> holeBoundaryPaths = boundaryPathsOf(holeBoundaries);
				int holeEdges = edgeCount(holeBoundaries);
				holeCount += holeBoundaries.size();
				if (face.isExterior()) {
					long exteriorStart = ImplicitPlotTimings.start();
					exteriorCount++;
					stats.exteriorFaces++;
					stats.exteriorHoles += holeBoundaries.size();
					stats.exteriorHoleEdges += holeEdges;
					List<GGeneralPath> holes = buildHolePath(holeBoundaries, bounds);
					GGeneralPath viewPath = buildViewPath(bounds);
					GPoint2D samplePoint = findExteriorSamplePoint(holeBoundaryPaths, bounds);
					if (samplePoint != null) {
						list.add(new ClassifiedRegion(viewPath, holes, samplePoint, face.getId()));
					}
					stats.exteriorElapsed += ImplicitPlotTimings.delta(exteriorStart);
				} else {
					long interiorStart = ImplicitPlotTimings.start();
					interiorCount++;
					stats.interiorFaces++;
					List<Integer> outerBoundaryIds = graph.outerBoundaryOf(face);
					BoundaryPath outerBoundaryPath = boundaryPathOf(outerBoundaryIds);
					GPoint2D samplePoint = face.getSamplePoint();
					if (!isWorldPointOnView(samplePoint, bounds)) {
						visibleSampleStats.offscreenSamples++;
						GPoint2D originalSamplePoint = samplePoint;
						VisibleSampleResult visibleSample = findVisibleInteriorSamplePoint(
								outerBoundaryPath, holeBoundaryPaths, bounds, visibleSampleStats);
						if (visibleSample.point() != null) {
							samplePoint = visibleSample.point();
						}
						logVisibleInteriorSample(face, originalSamplePoint, samplePoint,
								visibleSample, outerBoundaryIds, holeBoundaries, bounds);
					}
					if (samplePoint != null) {
						List<GGeneralPath> holes = buildHolePath(holeBoundaries, bounds);
						GGeneralPath outerBoundary = buildPath(outerBoundaryIds, bounds);

						list.add(new ClassifiedRegion(outerBoundary, holes, samplePoint,
								face.getId()));
					}
					stats.interiorElapsed += ImplicitPlotTimings.delta(interiorStart);
				}
			}
		} finally {
			activeGetResultsStats = null;
		}
		ImplicitPlotTimings.log("RegionClassifier.getResults.exterior",
				elapsedStart(stats.exteriorElapsed),
				"faces=" + stats.exteriorFaces
						+ " holes=" + stats.exteriorHoles
						+ " holeEdges=" + stats.exteriorHoleEdges
						+ " candidatesTested=" + stats.exteriorCandidates
						+ " outsideChecks=" + stats.outsideChecks
						+ " polygonCalls=" + stats.exteriorPolygonCalls
						+ " polygonEdges=" + stats.exteriorPolygonEdges
						+ " successStrategy=" + stats.exteriorSuccessStrategy);
		ImplicitPlotTimings.log("RegionClassifier.getResults.interior",
				elapsedStart(stats.interiorElapsed),
				"faces=" + stats.interiorFaces
						+ " offscreenSamples=" + visibleSampleStats.offscreenSamples
						+ " visibleHits=" + visibleSampleStats.hits
						+ " visibleMisses=" + visibleSampleStats.misses
						+ " visibleGrid=" + visibleSampleStats.gridSizeUsed
						+ " candidateChecks=" + stats.interiorCandidates
						+ " centerCandidates=" + visibleSampleStats.centerCandidates
						+ " preferredCandidates=" + visibleSampleStats.preferredCandidates
						+ " gridCandidates=" + visibleSampleStats.gridCandidates
						+ " perimeterCandidates=" + visibleSampleStats.perimeterCandidates
						+ " polygonCalls=" + stats.interiorPolygonCalls
						+ " polygonEdges=" + stats.interiorPolygonEdges);
		ImplicitPlotTimings.log("RegionClassifier.getResults.paths",
				elapsedStart(stats.pathsElapsed),
				"holePaths=" + stats.holePaths
						+ " holeEdges=" + stats.holePathEdges
						+ " outerPaths=" + stats.outerPaths
						+ " outerEdges=" + stats.outerPathEdges
						+ " viewPaths=" + stats.viewPaths);
		ImplicitPlotTimings.log("RegionClassifier.getResults.total", totalStart,
				"faces=" + graph.getFaces().size()
						+ " exterior=" + exteriorCount
						+ " interior=" + interiorCount
						+ " holes=" + holeCount
						+ " regions=" + list.size()
						+ " visibleOffscreen=" + visibleSampleStats.offscreenSamples
						+ " visibleHits=" + visibleSampleStats.hits
						+ " visibleMisses=" + visibleSampleStats.misses
						+ " visibleGrid=" + visibleSampleStats.gridSizeUsed
						+ " polygonCalls=" + stats.polygonCalls
						+ " polygonEdges=" + stats.polygonEdges
						+ " polygonInside=" + stats.polygonInside
						+ " polygonOutside=" + stats.polygonOutside
						+ " polygonBoundary=" + stats.polygonBoundary
						+ " exteriorCandidates=" + stats.exteriorCandidates
						+ " interiorCandidates=" + stats.interiorCandidates
						+ " pathEdges=" + stats.pathEdges());
		return list;
	}

	private static final class GetResultsStats {
		private long exteriorElapsed;
		private long interiorElapsed;
		private long pathsElapsed;
		private int exteriorFaces;
		private int interiorFaces;
		private int exteriorHoles;
		private int exteriorHoleEdges;
		private int exteriorCandidates;
		private int interiorCandidates;
		private int outsideChecks;
		private int polygonCalls;
		private int polygonEdges;
		private int polygonInside;
		private int polygonOutside;
		private int polygonBoundary;
		private int exteriorPolygonCalls;
		private int exteriorPolygonEdges;
		private int interiorPolygonCalls;
		private int interiorPolygonEdges;
		private int holePaths;
		private int holePathEdges;
		private int outerPaths;
		private int outerPathEdges;
		private int viewPaths;
		private int holePathDepth;
		private boolean inExteriorSearch;
		private boolean inInteriorSearch;
		private String exteriorSuccessStrategy = "none";

		private int pathEdges() {
			return holePathEdges + outerPathEdges;
		}
	}

	private long elapsedStart(long elapsed) {
		return ImplicitPlotTimings.start() - elapsed;
	}

	private int edgeCount(List<List<Integer>> boundaries) {
		int edges = 0;
		for (List<Integer> boundary : boundaries) {
			edges += boundary.size();
		}
		return edges;
	}

	private List<BoundaryPath> boundaryPathsOf(List<List<Integer>> boundaries) {
		List<BoundaryPath> paths = new ArrayList<>(boundaries.size());
		for (List<Integer> boundary : boundaries) {
			paths.add(boundaryPathOf(boundary));
		}
		return paths;
	}

	private BoundaryPath boundaryPathOf(List<Integer> boundary) {
		double[] xCoordinates = new double[boundary.size()];
		double[] yCoordinates = new double[boundary.size()];
		WorldBox box = new WorldBox(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		PlanarGraph graph = getGraph();
		for (int i = 0; i < boundary.size(); i++) {
			Vertex vertex = graph.vertex(graph.halfEdge(boundary.get(i)).getOriginVertexId());
			xCoordinates[i] = vertex.getX();
			yCoordinates[i] = vertex.getY();
			box.include(vertex.getX(), vertex.getY());
		}
		return new BoundaryPath(xCoordinates, yCoordinates, box);
	}

	private static final class BoundaryPath {
		private final double[] xCoordinates;
		private final double[] yCoordinates;
		private final WorldBox boundingBox;

		private BoundaryPath(double[] xCoordinates, double[] yCoordinates,
				WorldBox boundingBox) {
			this.xCoordinates = xCoordinates;
			this.yCoordinates = yCoordinates;
			this.boundingBox = boundingBox;
		}

		private int size() {
			return xCoordinates.length;
		}

		private boolean containsInBoundingBox(GPoint2D point) {
			return point.x >= boundingBox.xmin - EPS
					&& point.x <= boundingBox.xmax + EPS
					&& point.y >= boundingBox.ymin - EPS
					&& point.y <= boundingBox.ymax + EPS;
		}
	}

	private static final class VisibleSampleStats {
		private int offscreenSamples;
		private int hits;
		private int misses;
		private int gridSizeUsed;
		private int centerCandidates;
		private int preferredCandidates;
		private int gridCandidates;
		private int perimeterCandidates;
	}

	private record VisibleSampleResult(GPoint2D point, String strategy, WorldBox sampleBox) {
	}

	private boolean isWorldPointOnView(GPoint2D point, EuclidianViewBounds bounds) {
		return point != null
				&& point.x >= bounds.getXmin() && point.x <= bounds.getXmax()
				&& point.y >= bounds.getYmin() && point.y <= bounds.getYmax();
	}

	private VisibleSampleResult findVisibleInteriorSamplePoint(BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries, EuclidianViewBounds bounds,
			VisibleSampleStats stats) {
		GPoint2D center = new GPoint2D((bounds.getXmin() + bounds.getXmax()) * 0.5,
				(bounds.getYmin() + bounds.getYmax()) * 0.5);
		stats.centerCandidates++;
		countInteriorCandidate();
		if (isVisibleInteriorSample(center, outerBoundary, holeBoundaries)) {
			stats.hits++;
			return new VisibleSampleResult(center, "center", null);
		}

		WorldBox sampleBox = visibleSampleBox(outerBoundary, bounds);
		if (sampleBox == null) {
			stats.misses++;
			return new VisibleSampleResult(null, "no-visible-box", null);
		}
		GPoint2D preferredSample = firstInteriorSample(outerBoundary, holeBoundaries,
				preferredSamples(sampleBox), stats, "preferred");
		if (preferredSample != null) {
			stats.hits++;
			return new VisibleSampleResult(preferredSample, "preferred", sampleBox);
		}
		GPoint2D gridSample = findGridInteriorSample(outerBoundary, holeBoundaries,
				sampleBox, stats);
		if (gridSample != null) {
			stats.hits++;
			return new VisibleSampleResult(gridSample, "grid-" + stats.gridSizeUsed, sampleBox);
		}
		GPoint2D perimeterSample = findViewportPerimeterInteriorSample(outerBoundary,
				holeBoundaries, bounds, stats);
		if (perimeterSample != null) {
			stats.hits++;
			return new VisibleSampleResult(perimeterSample, "perimeter", sampleBox);
		}
		stats.misses++;
		return new VisibleSampleResult(null, "miss", sampleBox);
	}

	private void logVisibleInteriorSample(Face face, GPoint2D originalSample,
			GPoint2D selectedSample, VisibleSampleResult visibleSample,
			List<Integer> outerBoundary, List<List<Integer>> holeBoundaries,
			EuclidianViewBounds bounds) {
		if (VISIBLE_SAMPLE_DEBUG) {
			GetResultsStats stats = activeGetResultsStats;
			activeGetResultsStats = null;
			try {
				GPoint2D visiblePoint = visibleSample.point();
				Log.debug("[RegionClassifier] visible sample replacement"
						+ " face=" + face.getId()
						+ " original=" + (originalSample == null
							? "null"
							: formatPoint(originalSample.x, originalSample.y))
						+ " selected=" + (selectedSample == null
							? "null"
							: formatPoint(selectedSample.x, selectedSample.y))
						+ " replacement=" + (visiblePoint == null
							? "null"
							: formatPoint(visiblePoint.x, visiblePoint.y))
						+ " strategy=" + visibleSample.strategy()
						+ " outer=" + classifyPoint(visiblePoint, outerBoundary)
						+ " containingHoles="
						+ containingBoundaryCount(visiblePoint, holeBoundaries)
						+ " insideFace=" + isInteriorSampleOrFalse(visiblePoint, outerBoundary,
						holeBoundaries)
						+ " outerEdges=" + outerBoundary.size()
						+ " holes=" + holeBoundaries.size()
						+ " sampleBox=" + formatWorldBox(visibleSample.sampleBox())
						+ " boundaryBox=" + formatWorldBox(worldBoxOf(outerBoundary))
						+ " view=" + formatBounds(bounds));
			} finally {
				activeGetResultsStats = stats;
			}
		}
	}

	// try making PMD happy. Otherwise, if constant is false, it complains
	// about formatPoint(GPoint2D p) is never used.

	private WorldBox visibleSampleBox(BoundaryPath outerBoundary, EuclidianViewBounds bounds) {
		WorldBox boundaryBox = outerBoundary.boundingBox;
		double xmin = Math.max(boundaryBox.xmin, bounds.getXmin());
		double xmax = Math.min(boundaryBox.xmax, bounds.getXmax());
		double ymin = Math.max(boundaryBox.ymin, bounds.getYmin());
		double ymax = Math.min(boundaryBox.ymax, bounds.getYmax());
		if (!(xmin < xmax && ymin < ymax)) {
			return null;
		}
		double insetX = Math.min((xmax - xmin) * 0.25, Math.max(bounds.getInvXscale() * 2,
				(bounds.getXmax() - bounds.getXmin()) * 1e-6));
		double insetY = Math.min((ymax - ymin) * 0.25, Math.max(bounds.getInvYscale() * 2,
				(bounds.getYmax() - bounds.getYmin()) * 1e-6));
		return new WorldBox(xmin + insetX, xmax - insetX, ymin + insetY, ymax - insetY);
	}

	private WorldBox worldBoxOf(List<Integer> boundary) {
		WorldBox box = new WorldBox(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		PlanarGraph graph = getGraph();
		for (int edgeId : boundary) {
			HalfEdge halfEdge = graph.halfEdge(edgeId);
			Vertex origin = graph.vertex(halfEdge.getOriginVertexId());
			Vertex target = graph.vertex(halfEdge.getTargetVertexId());
			box.include(origin.getX(), origin.getY());
			box.include(target.getX(), target.getY());
		}
		return box;
	}

	private GPoint2D[] preferredSamples(WorldBox box) {
		double centerX = (box.xmin + box.xmax) * 0.5;
		double centerY = (box.ymin + box.ymax) * 0.5;
		return new GPoint2D[] {
				new GPoint2D(centerX, centerY),
				new GPoint2D(box.xmin, box.ymin),
				new GPoint2D(box.xmin, box.ymax),
				new GPoint2D(box.xmax, box.ymin),
				new GPoint2D(box.xmax, box.ymax)
		};
	}

	private GPoint2D firstInteriorSample(BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries, GPoint2D[] candidates,
			VisibleSampleStats stats, String strategy) {
		for (GPoint2D candidate : candidates) {
			if ("preferred".equals(strategy)) {
				stats.preferredCandidates++;
			} else if ("perimeter".equals(strategy)) {
				stats.perimeterCandidates++;
			}
			countInteriorCandidate();
			if (isVisibleInteriorSample(candidate, outerBoundary, holeBoundaries)) {
				return candidate;
			}
		}
		return null;
	}

	private GPoint2D findGridInteriorSample(BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries, WorldBox box, VisibleSampleStats stats) {
		for (int gridSize : VISIBLE_SAMPLE_GRID_SIZES) {
			double dx = (box.xmax - box.xmin) / gridSize;
			double dy = (box.ymax - box.ymin) / gridSize;
			for (int ix = 0; ix < gridSize; ix++) {
				double x = box.xmin + (ix + 0.5) * dx;
				for (int iy = 0; iy < gridSize; iy++) {
					double y = box.ymin + (iy + 0.5) * dy;
					GPoint2D candidate = new GPoint2D(x, y);
					stats.gridCandidates++;
					countInteriorCandidate();
					if (isVisibleInteriorSample(candidate, outerBoundary, holeBoundaries)) {
						stats.gridSizeUsed = gridSize;
						return candidate;
					}
				}
			}
		}
		return null;
	}

	private GPoint2D findViewportPerimeterInteriorSample(BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries, EuclidianViewBounds bounds,
			VisibleSampleStats stats) {
		int perimeterSamples = 32;
		for (int i = 0; i < perimeterSamples; i++) {
			double t = i / (double) (perimeterSamples - 1);
			GPoint2D[] candidates = {
					new GPoint2D(interpolate(bounds.getXmin(), bounds.getXmax(), t),
							bounds.getYmin() + bounds.getInvYscale() * 2),
					new GPoint2D(interpolate(bounds.getXmin(), bounds.getXmax(), t),
							bounds.getYmax() - bounds.getInvYscale() * 2),
					new GPoint2D(bounds.getXmin() + bounds.getInvXscale() * 2,
							interpolate(bounds.getYmin(), bounds.getYmax(), t)),
					new GPoint2D(bounds.getXmax() - bounds.getInvXscale() * 2,
							interpolate(bounds.getYmin(), bounds.getYmax(), t))
			};
			GPoint2D sample = firstInteriorSample(outerBoundary, holeBoundaries, candidates,
					stats, "perimeter");
			if (sample != null) {
				return sample;
			}
		}
		return null;
	}

	private boolean isInteriorSample(GPoint2D point, BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries) {
		if (classifyPointInPolygon(point, outerBoundary) != Containment.INSIDE) {
			return false;
		}
		for (BoundaryPath holeBoundary : holeBoundaries) {
			if (classifyPointInPolygon(point, holeBoundary) != Containment.OUTSIDE) {
				return false;
			}
		}
		return true;
	}

	private boolean isVisibleInteriorSample(GPoint2D point, BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries) {
		GetResultsStats stats = activeGetResultsStats;
		boolean previousInteriorSearch = stats != null && stats.inInteriorSearch;
		if (stats != null) {
			stats.inInteriorSearch = true;
		}
		try {
			return isInteriorSample(point, outerBoundary, holeBoundaries);
		} finally {
			if (stats != null) {
				stats.inInteriorSearch = previousInteriorSearch;
			}
		}
	}

	private double interpolate(double min, double max, double t) {
		return min + (max - min) * t;
	}

	private Containment classifyPoint(GPoint2D point, List<Integer> boundary) {
		return point == null ? null : classifyPointInPolygon(point, boundary);
	}

	private boolean isInteriorSampleOrFalse(GPoint2D point, List<Integer> outerBoundary,
			List<List<Integer>> holeBoundaries) {
		if (point == null || classifyPointInPolygon(point, outerBoundary) != Containment.INSIDE) {
			return false;
		}
		for (List<Integer> holeBoundary : holeBoundaries) {
			if (classifyPointInPolygon(point, holeBoundary) != Containment.OUTSIDE) {
				return false;
			}
		}
		return true;
	}

	private int containingBoundaryCount(GPoint2D point, List<List<Integer>> boundaries) {
		if (point == null) {
			return 0;
		}
		int count = 0;
		for (List<Integer> boundary : boundaries) {
			if (classifyPointInPolygon(point, boundary) != Containment.OUTSIDE) {
				count++;
			}
		}
		return count;
	}

	private String formatWorldBox(WorldBox box) {
		return box == null ? "null" : "[" + box.xmin + "," + box.xmax
				+ "]x[" + box.ymin + "," + box.ymax + "]";
	}

	private String formatBounds(EuclidianViewBounds bounds) {
		return "[" + bounds.getXmin() + "," + bounds.getXmax()
				+ "]x[" + bounds.getYmin() + "," + bounds.getYmax() + "]";
	}

	private static final class WorldBox {
		private double xmin;
		private double xmax;
		private double ymin;
		private double ymax;

		private WorldBox(double xmin, double xmax, double ymin, double ymax) {
			this.xmin = xmin;
			this.xmax = xmax;
			this.ymin = ymin;
			this.ymax = ymax;
		}

		private void include(double x, double y) {
			xmin = Math.min(xmin, x);
			xmax = Math.max(xmax, x);
			ymin = Math.min(ymin, y);
			ymax = Math.max(ymax, y);
		}
	}

	@SuppressWarnings("PMD.VariableDeclarationUsageDistance")
	private GPoint2D findExteriorSamplePoint(List<BoundaryPath> holeBoundaries,
			EuclidianViewBounds bounds) {
		GetResultsStats stats = activeGetResultsStats;
		boolean previousExteriorSearch = stats != null && stats.inExteriorSearch;
		if (stats != null) {
			stats.inExteriorSearch = true;
		}

		double insetX = Math.max(bounds.getInvXscale() * 2,
				(bounds.getXmax() - bounds.getXmin()) * 1e-6);
		double insetY = Math.max(bounds.getInvYscale() * 2,
				(bounds.getYmax() - bounds.getYmin()) * 1e-6);
		double xmin = bounds.getXmin() + insetX;
		double xmax = bounds.getXmax() - insetX;
		double ymin = bounds.getYmin() + insetY;
		double ymax = bounds.getYmax() - insetY;

		double[][] cornerCandidates = {
				{xmin, ymin},
				{xmin, ymax},
				{xmax, ymin},
				{xmax, ymax}
		};
		for (double[] candidate : cornerCandidates) {
			GPoint2D point = new GPoint2D(candidate[0], candidate[1]);
			countExteriorCandidate();
			if (isOutsideAllBoundaries(point, holeBoundaries)) {
				recordExteriorSuccess("corner", previousExteriorSearch);
				return point;
			}
		}
		int perimeterSamples = 32;
		for (int i = 0; i < perimeterSamples; i++) {
			double t = i / (double) (perimeterSamples - 1);
			perimeterCandidates[0].setLocation(xmin + (xmax - xmin) * t, ymin);
			perimeterCandidates[1].setLocation(xmin + (xmax - xmin) * t, ymax);
			perimeterCandidates[2].setLocation(xmin, ymin + (ymax - ymin) * t);
			perimeterCandidates[3].setLocation(xmax, ymin + (ymax - ymin) * t);
			for (GPoint2D point : perimeterCandidates) {
				countExteriorCandidate();
				if (isOutsideAllBoundaries(point, holeBoundaries)) {
					recordExteriorSuccess("perimeter", previousExteriorSearch);
					return point;
				}
			}
		}
		int[] gridSizes = {8, 16, 32};

		GPoint2D point = new GPoint2D();

		for (int gridSize : gridSizes) {
			double dx = (xmax - xmin) / gridSize;
			double dy = (ymax - ymin) / gridSize;

			double x = xmin + 0.5 * dx;
			for (int ix = 0; ix < gridSize; ix++, x += dx) {
				double y = ymin + 0.5 * dy;
				for (int iy = 0; iy < gridSize; iy++, y += dy) {
					point.setLocation(x, y);

					countExteriorCandidate();
					if (isOutsideAllBoundaries(point, holeBoundaries)) {
						recordExteriorSuccess("grid-" + gridSize, previousExteriorSearch);
						return new GPoint2D(x, y);
					}
				}
			}
		}
		recordExteriorSuccess("miss", previousExteriorSearch);
		return null;
	}

	private void recordExteriorSuccess(String strategy, boolean previousExteriorSearch) {
		GetResultsStats stats = activeGetResultsStats;
		if (stats == null) {
			return;
		}
		stats.exteriorSuccessStrategy = strategy;
		stats.inExteriorSearch = previousExteriorSearch;
	}

	private void countExteriorCandidate() {
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.exteriorCandidates++;
		}
	}

	private void countInteriorCandidate() {
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.interiorCandidates++;
		}
	}

	private boolean isOutsideAllBoundaries(GPoint2D point, List<BoundaryPath> boundaries) {
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.outsideChecks++;
		}
		for (BoundaryPath boundary : boundaries) {
			if (classifyPointInPolygon(point, boundary) != Containment.OUTSIDE) {
				return false;
			}
		}
		return true;
	}

	private List<GGeneralPath> buildHolePath(List<List<Integer>> lists,
			EuclidianViewBounds bounds) {
		List<GGeneralPath> paths = new ArrayList<>();
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.holePathDepth++;
		}
		try {
			for (List<Integer> hole : lists) {
				paths.add(buildPath(hole, bounds));
			}
		} finally {
			if (stats != null) {
				stats.holePathDepth--;
			}
		}
		if (stats != null) {
			stats.holePaths += lists.size();
			stats.holePathEdges += edgeCount(lists);
		}
		return paths;
	}

	private GGeneralPath buildViewPath(EuclidianViewBounds bounds) {
		long start = ImplicitPlotTimings.start();
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		gp.moveTo(bounds.toScreenCoordXd(bounds.getXmin()),
				bounds.toScreenCoordYd(bounds.getYmin()));
		gp.lineTo(bounds.toScreenCoordXd(bounds.getXmax()),
				bounds.toScreenCoordYd(bounds.getYmin()));
		gp.lineTo(bounds.toScreenCoordXd(bounds.getXmax()),
				bounds.toScreenCoordYd(bounds.getYmax()));
		gp.lineTo(bounds.toScreenCoordXd(bounds.getXmin()),
				bounds.toScreenCoordYd(bounds.getYmax()));
		gp.closePath();
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.pathsElapsed += ImplicitPlotTimings.delta(start);
			stats.viewPaths++;
		}
		return gp;
	}

	private GGeneralPath buildPath(List<Integer> edgeIds, EuclidianViewBounds bounds) {
		long start = ImplicitPlotTimings.start();
		GGeneralPath gp = AwtFactory.getPrototype().newGeneralPath();
		if (edgeIds.isEmpty()) {
			recordBuiltPath(start, 0);
			return gp;
		}
		PlanarGraph graph = getGraph();
		HalfEdge firstHalfEdge = graph.halfEdge(edgeIds.get(0));
		Vertex firstOrigin = graph.vertex(firstHalfEdge.getOriginVertexId());
		gp.moveTo(bounds.toScreenCoordXd(firstOrigin.getX()),
				bounds.toScreenCoordYd(firstOrigin.getY()));
		for (Integer edgeId: edgeIds) {
			HalfEdge halfEdge = graph.halfEdge(edgeId);
			Vertex target = graph.vertex(halfEdge.getTargetVertexId());
			gp.lineTo(bounds.toScreenCoordXd(target.getX()), bounds.toScreenCoordYd(target.getY()));
		}
		gp.closePath();

		recordBuiltPath(start, edgeIds.size());
		return gp;
	}

	private void recordBuiltPath(long start, int edges) {
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.pathsElapsed += ImplicitPlotTimings.delta(start);
			if (stats.holePathDepth == 0) {
				stats.outerPaths++;
				stats.outerPathEdges += edges;
			}
		}
	}

	private enum Containment {
		INSIDE,
		OUTSIDE,
		BOUNDARY
	}

	private Containment classifyPointInPolygon(GPoint2D p, List<Integer> cycle) {
		int size = cycle.size();
		if (size < 3) {
			return recordPolygonResult(size, Containment.OUTSIDE);
		}

		PlanarGraph graph = getGraph();
		double px = p.x;
		double py = p.y;
		int prevEdgeId = cycle.get(size - 1);
		Vertex vj = graph.vertex(graph.halfEdge(prevEdgeId).getOriginVertexId());
		double xj = vj.getX();
		double yj = vj.getY();

		boolean inside = false;
		for (int edgeId : cycle) {
			Vertex vi = graph.vertex(graph.halfEdge(edgeId).getOriginVertexId());

			double xi = vi.getX();
			double yi = vi.getY();

			if (isPointOnSegment(px, py, xj, yj, xi, yi)) {
				return recordPolygonResult(size, Containment.BOUNDARY);
			}

			if ((yi > py) != (yj > py)) {
				double xIntersect = (xj - xi) * (py - yi) / (yj - yi) + xi;
				if (px < xIntersect) {
					inside = !inside;
				}
			}

			xj = xi;
			yj = yi;
		}

		return recordPolygonResult(size, inside ? Containment.INSIDE : Containment.OUTSIDE);
	}

	private Containment classifyPointInPolygon(GPoint2D p, BoundaryPath polygon) {
		int size = polygon.size();
		if (size < 3) {
			return recordPolygonResult(size, Containment.OUTSIDE);
		}
		if (!polygon.containsInBoundingBox(p)) {
			return recordPolygonResult(0, Containment.OUTSIDE);
		}
		boolean inside = false;
		double px = p.x;
		double py = p.y;
		for (int i = 0, j = size - 1; i < size; j = i++) {
			double xi = polygon.xCoordinates[i];
			double yi = polygon.yCoordinates[i];
			double xj = polygon.xCoordinates[j];
			double yj = polygon.yCoordinates[j];
			if (isPointOnSegment(px, py, xj, yj, xi, yi)) {
				return recordPolygonResult(size, Containment.BOUNDARY);
			}
			if ((yi > py) != (yj > py)) {
				double xIntersect = (xj - xi) * (py - yi) / (yj - yi) + xi;
				if (px < xIntersect) {
					inside = !inside;
				}
			}
		}
		return recordPolygonResult(size, inside ? Containment.INSIDE : Containment.OUTSIDE);
	}

	private Containment recordPolygonResult(int edgeCount, Containment result) {
		GetResultsStats stats = activeGetResultsStats;
		if (stats != null) {
			stats.polygonCalls++;
			stats.polygonEdges += edgeCount;
			if (stats.inExteriorSearch) {
				stats.exteriorPolygonCalls++;
				stats.exteriorPolygonEdges += edgeCount;
			}
			if (stats.inInteriorSearch) {
				stats.interiorPolygonCalls++;
				stats.interiorPolygonEdges += edgeCount;
			}
			if (result == Containment.INSIDE) {
				stats.polygonInside++;
			} else if (result == Containment.OUTSIDE) {
				stats.polygonOutside++;
			} else {
				stats.polygonBoundary++;
			}
		}
		return result;
	}

	private boolean isPointOnSegment(
			double px, double py,
			double x1, double y1,
			double x2, double y2) {

		if (px < Math.min(x1, x2) - EPS || px > Math.max(x1, x2) + EPS
				|| py < Math.min(y1, y2) - EPS || py > Math.max(y1, y2) + EPS) {
			return false;
		}

		double dx = x2 - x1;
		double dy = y2 - y1;
		double cross = (py - y1) * dx - (px - x1) * dy;

		double len2 = dx * dx + dy * dy;
		return cross * cross <= EPS * EPS * len2;
	}
}

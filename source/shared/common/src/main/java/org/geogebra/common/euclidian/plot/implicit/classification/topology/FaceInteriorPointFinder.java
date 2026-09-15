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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.util.debug.Log;

final class FaceInteriorPointFinder {
	private static final double GEOMETRY_EPSILON = PlanarGraph.GEOMETRY_EPSILON;

	private static final int SAMPLE_SEARCH_CANDIDATE_LIMIT = 12;
	private static final int MIN_PRIMARY_CANDIDATES_BEFORE_GRID = 8;
	private static final int MAX_EXACT_CLEARANCE_CANDIDATES = 24;
	private static final int EXACT_CLEARANCE_PER_STRATEGY_LIMIT = 8;
	private static final int COLLECTED_CANDIDATES_PER_STRATEGY_LIMIT = 16;
	private static final int[] SAMPLE_GRID_SIZES = {8, 16, 32, 64, 128};
	private static final boolean SAMPLE_CLEARANCE_DEBUG_LOGGING = false;
	private static final Comparator<SampleCandidate> CHEAP_CANDIDATE_ORDER =
			Comparator.comparingInt((SampleCandidate candidate) -> candidate.fallbackPriority)
					.thenComparing(Comparator.comparingDouble(
							(SampleCandidate candidate) -> candidate.cheapScore).reversed())
					.thenComparingDouble(candidate -> candidate.point.x)
					.thenComparingDouble(candidate -> candidate.point.y);
	private static final Comparator<SampleCandidate> EXACT_CANDIDATE_ORDER =
			Comparator.comparingDouble(
							(SampleCandidate candidate) -> candidate.exactClearanceSquared)
					.thenComparingInt(candidate -> -candidate.fallbackPriority)
					.thenComparingDouble(candidate -> candidate.cheapScore)
					.thenComparingDouble(candidate -> -candidate.point.x)
					.thenComparingDouble(candidate -> -candidate.point.y);
	private final PlanarGraph graph;

	enum StrategyFamily {
		BOUNDARY,
		GRID,
		REPRESENTATIVE,
		SCANLINE,
		VERTEX
	}

	enum Strategy {
		BOUNDARY_TOPOLOGICAL(StrategyFamily.BOUNDARY),
		BOUNDARY_NORMAL(StrategyFamily.BOUNDARY),
		VERTEX_ALIGNED(StrategyFamily.VERTEX),
		SCANLINE(StrategyFamily.SCANLINE),
		REPRESENTATIVE(StrategyFamily.REPRESENTATIVE),
		GRID_8(StrategyFamily.GRID),
		GRID_16(StrategyFamily.GRID),
		GRID_32(StrategyFamily.GRID),
		GRID_64(StrategyFamily.GRID),
		GRID_128(StrategyFamily.GRID);

		private final StrategyFamily family;

		Strategy(StrategyFamily family) {
			this.family = family;
		}
	}

	record FaceContext(BoundaryPath outerBoundary, List<BoundaryPath> holeBoundaries,
			PlanarGeometry.BoundingBox boundingBox) {

		static FaceContext from(PlanarGraph graph, BoundaryCycle outerBoundary,
				List<BoundaryCycle> holeBoundaries) {
			List<BoundaryPath> holePaths = new ArrayList<>(holeBoundaries.size());
			for (BoundaryCycle holeBoundary : holeBoundaries) {
				holePaths.add(BoundaryPath.from(graph, holeBoundary));
			}
			BoundaryPath outerPath = BoundaryPath.from(graph, outerBoundary);
			return new FaceContext(outerPath, holePaths, outerPath.boundingBox);
		}

		int holeCount() {
			return holeBoundaries.size();
		}

		int edgeCount() {
			int edges = outerBoundary.size();
			for (BoundaryPath holeBoundary : holeBoundaries) {
				edges += holeBoundary.size();
			}
			return edges;
		}

	}

	private record Interval(double start, double end) {
		double midpoint() {
			return (start + end) * 0.5;
		}

		double width() {
			return end - start;
		}
	}

	private static final class SampleCandidate {
		private final GPoint2D point;
		private final Strategy strategy;
		private final int fallbackPriority;
		private final double cheapScore;
		private double exactClearanceSquared = Double.NaN;

		private SampleCandidate(GPoint2D point, Strategy strategy, int fallbackPriority,
				double cheapScore) {
			this.point = point;
			this.strategy = strategy;
			this.fallbackPriority = fallbackPriority;
			this.cheapScore = cheapScore;
		}
	}

	static final class BoundaryPath {
		private final double[] xCoordinates;
		private final double[] yCoordinates;
		private final PlanarGeometry.BoundingBox boundingBox;

		private BoundaryPath(double[] xCoordinates, double[] yCoordinates,
				PlanarGeometry.BoundingBox boundingBox) {
			this.xCoordinates = xCoordinates;
			this.yCoordinates = yCoordinates;
			this.boundingBox = boundingBox;
		}

		static BoundaryPath from(PlanarGraph graph, List<Integer> boundary) {
			double[] xCoordinates = new double[boundary.size()];
			double[] yCoordinates = new double[boundary.size()];
			PlanarGeometry.BoundingBox boundingBox = new PlanarGeometry.BoundingBox();
			for (int i = 0; i < boundary.size(); i++) {
				Vertex vertex = graph.vertex(
						graph.halfEdge(boundary.get(i)).getOriginVertexId());
				xCoordinates[i] = vertex.getX();
				yCoordinates[i] = vertex.getY();
				boundingBox.include(vertex.getX(), vertex.getY());
			}
			return new BoundaryPath(xCoordinates, yCoordinates, boundingBox);
		}

		static BoundaryPath from(PlanarGraph graph, BoundaryCycle boundary) {
			return new BoundaryPath(boundary.getXCoordinates(graph),
					boundary.getYCoordinates(graph), boundary.getBoundingBox(graph));
		}

		PlanarGeometry.BoundingBox getBoundingBox() {
			return boundingBox;
		}

		private int size() {
			return xCoordinates.length;
		}

		private boolean containsInBoundingBox(GPoint2D point) {
			return point.x >= boundingBox.minX - GEOMETRY_EPSILON
					&& point.x <= boundingBox.maxX + GEOMETRY_EPSILON
					&& point.y >= boundingBox.minY - GEOMETRY_EPSILON
					&& point.y <= boundingBox.maxY + GEOMETRY_EPSILON;
		}
	}

	FaceInteriorPointFinder(PlanarGraph graph) {
		this.graph = graph;
	}

	MyPoint find(Face face) {
		FaceContext context = createFaceContext(face);
		return find(face, context);
	}

	MyPoint find(Face face, FaceContext context) {
		List<SampleCandidate> candidates = new ArrayList<>();

		collectPrimaryCandidates(context, candidates);
		if (nonBoundaryCandidateCount(candidates) < MIN_PRIMARY_CANDIDATES_BEFORE_GRID) {
			collectGridInteriorPoints(context, candidates);
		}
		if (nonBoundaryCandidateCount(candidates) == 0) {
			collectTopologicalBoundaryProbeCandidates(context, candidates);
			collectBoundaryProbeCandidates(context, candidates);
		}

		SampleCandidate selected = selectBestCandidate(candidates, context);
		if (selected == null) {
			throwSamplePointNotFound(face, context);
		}
		logSelectedSample(face, selected, candidates, context);
		return new MyPoint(selected.point.x, selected.point.y);
	}

	private static void throwSamplePointNotFound(Face face, FaceContext context) {
		throw new IllegalStateException(
				"Cannot find interior sample point for face " + face.getId()
						+ " outerEdges=" + context.outerBoundary.size()
						+ " holes=" + context.holeBoundaries.size()
						+ " bbox=[" + context.boundingBox.minX + ","
						+ context.boundingBox.minY + " -> "
						+ context.boundingBox.maxX + ","
						+ context.boundingBox.maxY + "]");
	}

	private FaceContext createFaceContext(Face face) {
		List<Integer> outerBoundary = graph.outerBoundaryOf(face);
		List<List<Integer>> holeBoundaries = graph.holeBoundariesOf(face);
		List<BoundaryPath> holePaths = new ArrayList<>();
		for (List<Integer> holeBoundary : holeBoundaries) {
			holePaths.add(BoundaryPath.from(graph, holeBoundary));
		}
		BoundaryPath outerPath = BoundaryPath.from(graph, outerBoundary);
		return new FaceContext(outerPath, holePaths, outerPath.boundingBox);
	}

	private SampleCandidate selectBestCandidate(List<SampleCandidate> candidates,
			FaceContext context) {
		if (candidates.isEmpty()) {
			return null;
		}
		List<SampleCandidate> shortlist = exactClearanceShortlist(candidates);
		int exactCount = Math.min(shortlist.size(), MAX_EXACT_CLEARANCE_CANDIDATES);
		for (int i = 0; i < exactCount; i++) {
			SampleCandidate candidate = shortlist.get(i);
			candidate.exactClearanceSquared = minimumBoundaryDistanceSquared(candidate.point,
					context);
		}
		return shortlist.subList(0, exactCount).stream()
				.max(EXACT_CANDIDATE_ORDER)
				.orElse(null);
	}

	private List<SampleCandidate> exactClearanceShortlist(List<SampleCandidate> candidates) {
		List<SampleCandidate> sorted = new ArrayList<>(candidates);
		sorted.sort(CHEAP_CANDIDATE_ORDER);
		List<SampleCandidate> shortlist = new ArrayList<>();
		addStrategyCandidates(sorted, shortlist, StrategyFamily.REPRESENTATIVE);
		addStrategyCandidates(sorted, shortlist, StrategyFamily.VERTEX);
		addStrategyCandidates(sorted, shortlist, StrategyFamily.SCANLINE);
		addStrategyCandidates(sorted, shortlist, StrategyFamily.GRID);
		addStrategyCandidates(sorted, shortlist, StrategyFamily.BOUNDARY);
		for (SampleCandidate candidate : sorted) {
			if (shortlist.size() >= MAX_EXACT_CLEARANCE_CANDIDATES) {
				break;
			}
			addIfAbsent(shortlist, candidate);
		}
		return shortlist;
	}

	private void addStrategyCandidates(List<SampleCandidate> sorted,
			List<SampleCandidate> shortlist, StrategyFamily strategyPrefix) {
		int added = 0;
		for (SampleCandidate candidate : sorted) {
			if (shortlist.size() >= MAX_EXACT_CLEARANCE_CANDIDATES
					|| added >= EXACT_CLEARANCE_PER_STRATEGY_LIMIT) {
				return;
			}
			if (candidate.strategy.family == strategyPrefix
					&& addIfAbsent(shortlist, candidate)) {
				added++;
			}
		}
	}

	private boolean addIfAbsent(List<SampleCandidate> shortlist, SampleCandidate candidate) {
		if (shortlist.contains(candidate)) {
			return false;
		}
		shortlist.add(candidate);
		return true;
	}

	private void logSelectedSample(Face face, SampleCandidate selected,
			List<SampleCandidate> candidates, FaceContext context) {
		if (!SAMPLE_CLEARANCE_DEBUG_LOGGING) {
			return;
		}
		Log.debug("[FaceInteriorPointFinder] selected sample"
				+ " face=" + face.getId()
				+ " strategy=" + selected.strategy
				+ " point=" + formatPoint(selected.point)
				+ " clearance=" + Math.sqrt(selected.exactClearanceSquared)
				+ " candidates=" + candidates.size()
				+ " bestBoundaryClearance=" + bestBoundaryClearance(candidates)
				+ " outerEdges=" + context.outerBoundary.size()
				+ " holes=" + context.holeBoundaries.size()
				+ " bbox=" + formatBoundingBox(context.boundingBox));
	}

	private double bestBoundaryClearance(List<SampleCandidate> candidates) {
		double best = -1;
		for (SampleCandidate candidate : candidates) {
			if (candidate.fallbackPriority == 2
					&& !Double.isNaN(candidate.exactClearanceSquared)) {
				best = Math.max(best, candidate.exactClearanceSquared);
			}
		}
		return best < 0 ? -1 : Math.sqrt(best);
	}

	private void collectPrimaryCandidates(FaceContext context, List<SampleCandidate> candidates) {
		collectRepresentativeCandidates(context, candidates);
		collectVertexAlignedInteriorPoints(context, candidates);
		collectScanlineInteriorPoints(context, candidates);
	}

	private int nonBoundaryCandidateCount(List<SampleCandidate> candidates) {
		int count = 0;
		for (SampleCandidate candidate : candidates) {
			if (candidate.fallbackPriority < 2) {
				count++;
			}
		}
		return count;
	}

	private boolean addCandidate(List<SampleCandidate> candidates, GPoint2D point,
			Strategy strategy, int fallbackPriority, double cheapScore, FaceContext context) {
		if (!canImproveStrategyCandidates(candidates, strategy, cheapScore)) {
			return false;
		}
		if (!isPointInsideFace(point, context)) {
			return false;
		}
		addBoundedCandidate(candidates,
				new SampleCandidate(point, strategy, fallbackPriority, cheapScore));
		return true;
	}

	private boolean canImproveStrategyCandidates(List<SampleCandidate> candidates,
			Strategy strategy, double cheapScore) {
		if (strategyCandidateCount(candidates, strategy)
				< COLLECTED_CANDIDATES_PER_STRATEGY_LIMIT) {
			return true;
		}
		return cheapScore > worstStrategyCheapScore(candidates, strategy);
	}

	private void addBoundedCandidate(List<SampleCandidate> candidates, SampleCandidate candidate) {
		if (strategyCandidateCount(candidates, candidate.strategy)
				< COLLECTED_CANDIDATES_PER_STRATEGY_LIMIT) {
			candidates.add(candidate);
			return;
		}
		int worstIndex = worstStrategyCandidateIndex(candidates, candidate.strategy);
		if (worstIndex >= 0 && candidate.cheapScore > candidates.get(worstIndex).cheapScore) {
			candidates.set(worstIndex, candidate);
		}
	}

	private int strategyCandidateCount(List<SampleCandidate> candidates, Strategy strategy) {
		int count = 0;
		StrategyFamily family = strategyFamily(strategy);
		for (SampleCandidate candidate : candidates) {
			if (strategyFamily(candidate.strategy) == family) {
				count++;
			}
		}
		return count;
	}

	private double worstStrategyCheapScore(List<SampleCandidate> candidates, Strategy strategy) {
		int worstIndex = worstStrategyCandidateIndex(candidates, strategy);
		return worstIndex < 0 ? Double.NEGATIVE_INFINITY : candidates.get(worstIndex).cheapScore;
	}

	private int worstStrategyCandidateIndex(List<SampleCandidate> candidates, Strategy strategy) {
		StrategyFamily family = strategyFamily(strategy);
		int worstIndex = -1;
		double worstScore = Double.POSITIVE_INFINITY;
		for (int i = 0; i < candidates.size(); i++) {
			SampleCandidate candidate = candidates.get(i);
			if (strategyFamily(candidate.strategy) == family
					&& candidate.cheapScore < worstScore) {
				worstScore = candidate.cheapScore;
				worstIndex = i;
			}
		}
		return worstIndex;
	}

	private StrategyFamily strategyFamily(Strategy strategy) {
		return strategy.family;
	}

	private double boundingBoxScore(GPoint2D point, PlanarGeometry.BoundingBox boundingBox) {
		if (!boundingBox.isFinite()) {
			return 0;
		}
		return Math.min(Math.min(point.x - boundingBox.minX, boundingBox.maxX - point.x),
				Math.min(point.y - boundingBox.minY, boundingBox.maxY - point.y));
	}

	private boolean isPointInsideFace(GPoint2D point, FaceContext context) {
		if (classifyPointInPolygon(point, context.outerBoundary)
				!= PlanarGraph.Containment.INSIDE) {
			return false;
		}
		for (BoundaryPath holeBoundary : context.holeBoundaries) {
			if (!holeBoundary.containsInBoundingBox(point)) {
				continue;
			}
			if (classifyPointInPolygon(point, holeBoundary)
					!= PlanarGraph.Containment.OUTSIDE) {
				return false;
			}
		}
		return true;
	}

	private PlanarGraph.Containment classifyPointInPolygon(GPoint2D point,
			BoundaryPath polygon) {
		if (!polygon.containsInBoundingBox(point)) {
			return PlanarGraph.Containment.OUTSIDE;
		}
		boolean inside = false;
		int size = polygon.size();
		for (int i = 0, j = size - 1; i < size; j = i++) {
			double xi = polygon.xCoordinates[i];
			double yi = polygon.yCoordinates[i];
			double xj = polygon.xCoordinates[j];
			double yj = polygon.yCoordinates[j];
			if (PlanarGeometry.isPointOnSegment(point, xj, yj, xi, yi)) {
				return PlanarGraph.Containment.BOUNDARY;
			}
			boolean intersects = ((yi > point.y) != (yj > point.y))
					&& (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi);
			if (intersects) {
				inside = !inside;
			}
		}
		return inside ? PlanarGraph.Containment.INSIDE : PlanarGraph.Containment.OUTSIDE;
	}

	private double minimumBoundaryDistanceSquared(GPoint2D point, FaceContext context) {
		double minDistanceSquared = minimumBoundaryDistanceSquared(point,
				context.outerBoundary, Double.POSITIVE_INFINITY);
		for (BoundaryPath holeBoundary : context.holeBoundaries) {
			minDistanceSquared = minimumBoundaryDistanceSquared(point, holeBoundary,
					minDistanceSquared);
		}
		return minDistanceSquared;
	}

	private double minimumBoundaryDistanceSquared(GPoint2D point, BoundaryPath boundary,
			double currentMinimum) {
		double minDistanceSquared = currentMinimum;
		int size = boundary.size();
		for (int i = 0; i < size; i++) {
			int next = (i + 1) % size;
			minDistanceSquared = Math.min(minDistanceSquared, distanceSquaredToSegment(point,
					boundary.xCoordinates[i], boundary.yCoordinates[i],
					boundary.xCoordinates[next], boundary.yCoordinates[next]));
		}
		return minDistanceSquared;
	}

	private double distanceSquaredToSegment(GPoint2D point,
			double originX, double originY, double targetX, double targetY) {
		double dx = targetX - originX;
		double dy = targetY - originY;
		double lengthSquared = dx * dx + dy * dy;
		if (lengthSquared <= GEOMETRY_EPSILON) {
			return distanceSquared(point.x, point.y, originX, originY);
		}
		double t = ((point.x - originX) * dx + (point.y - originY) * dy)
				/ lengthSquared;
		double clampedT = Math.max(0, Math.min(1, t));
		double closestX = originX + clampedT * dx;
		double closestY = originY + clampedT * dy;
		return distanceSquared(point.x, point.y, closestX, closestY);
	}

	private double distanceSquared(double x1, double y1, double x2, double y2) {
		double dx = x1 - x2;
		double dy = y1 - y2;
		return dx * dx + dy * dy;
	}

	private String formatPoint(GPoint2D point) {
		return "(" + point.x + "," + point.y + ")";
	}

	private String formatBoundingBox(PlanarGeometry.BoundingBox boundingBox) {
		return "[" + boundingBox.minX + "," + boundingBox.maxX
				+ "]x[" + boundingBox.minY + "," + boundingBox.maxY + "]";
	}

	private void collectBoundaryProbeCandidates(FaceContext context, List<SampleCandidate> out) {
		collectBoundaryProbeCandidates(context, out, 1, -1);
	}

	private void collectRepresentativeCandidates(FaceContext context,
			List<SampleCandidate> out) {
		PlanarGeometry.BoundingBox boundingBox = context.boundingBox;
		double centerX = (boundingBox.minX + boundingBox.maxX) * 0.5;
		double centerY = (boundingBox.minY + boundingBox.maxY) * 0.5;
		GPoint2D[] candidates = {
				new GPoint2D(centerX, centerY),
				centroidOf(context.outerBoundary)
		};
		for (GPoint2D candidate : candidates) {
			if (!isAmbiguousRepresentativeCandidate(candidate, context, centerX, centerY)) {
				addCandidate(out, candidate, Strategy.REPRESENTATIVE, 0,
						boundingBoxScore(candidate, boundingBox), context);
			}
		}
	}

	private boolean isAmbiguousRepresentativeCandidate(GPoint2D candidate, FaceContext context,
			double centerX, double centerY) {
		PlanarGeometry.BoundingBox boundingBox = context.boundingBox;
		if (candidate == null || !context.holeBoundaries.isEmpty()
				|| context.outerBoundary.size() < 32 || !boundingBox.isFinite()) {
			return false;
		}
		double width = boundingBox.maxX - boundingBox.minX;
		double height = boundingBox.maxY - boundingBox.minY;
		if (width <= GEOMETRY_EPSILON || height <= GEOMETRY_EPSILON) {
			return false;
		}
		if (!(boundingBox.minX < 0 && boundingBox.maxX > 0
				&& boundingBox.minY < 0 && boundingBox.maxY > 0)) {
			return false;
		}
		double dx = Math.abs(candidate.x - centerX);
		double dy = Math.abs(candidate.y - centerY);
		return dx <= Math.max(1e-9, width * 0.02)
				&& dy <= Math.max(1e-9, height * 0.02);
	}

	private GPoint2D centroidOf(BoundaryPath boundary) {
		double signedArea = 0;
		double centroidX = 0;
		double centroidY = 0;
		int size = boundary.size();
		for (int i = 0; i < size; i++) {
			int next = (i + 1) % size;
			double currentX = boundary.xCoordinates[i];
			double currentY = boundary.yCoordinates[i];
			double nextX = boundary.xCoordinates[next];
			double nextY = boundary.yCoordinates[next];
			double cross = currentX * nextY - nextX * currentY;
			signedArea += cross;
			centroidX += (currentX + nextX) * cross;
			centroidY += (currentY + nextY) * cross;
		}
		if (Math.abs(signedArea) <= GEOMETRY_EPSILON) {
			PlanarGeometry.BoundingBox boundingBox = boundary.boundingBox;
			return new GPoint2D((boundingBox.minX + boundingBox.maxX) * 0.5,
					(boundingBox.minY + boundingBox.maxY) * 0.5);
		}
		double factor = 1.0 / (3.0 * signedArea);
		return new GPoint2D(centroidX * factor, centroidY * factor);
	}

	private void collectVertexAlignedInteriorPoints(FaceContext context,
			List<SampleCandidate> out) {
		List<WeightedCoordinate> xCandidates = buildCoordinateMidpoints(context, true);
		List<WeightedCoordinate> yCandidates = buildCoordinateMidpoints(context, false);
		for (WeightedCoordinate x : xCandidates) {
			for (WeightedCoordinate y : yCandidates) {
				addCandidate(out, new GPoint2D(x.coordinate(), y.coordinate()),
						Strategy.VERTEX_ALIGNED,
						0, Math.min(x.weight(), y.weight()) * 0.5, context);
			}
		}
	}

	private record WeightedCoordinate(double coordinate, double weight) {
	}

	private List<WeightedCoordinate> buildCoordinateMidpoints(FaceContext context, boolean xAxis) {
		List<Double> coordinates = new ArrayList<>();
		collectVertexCoordinates(context.outerBoundary, xAxis, coordinates);
		for (BoundaryPath holeBoundary : context.holeBoundaries) {
			collectVertexCoordinates(holeBoundary, xAxis, coordinates);
		}
		coordinates.sort(Double::compareTo);
		List<WeightedCoordinate> midpoints = new ArrayList<>();
		double previous = Double.NaN;
		for (double coordinate : coordinates) {
			if (!midpoints.isEmpty() && Math.abs(coordinate - previous) <= GEOMETRY_EPSILON) {
				continue;
			}
			if (Double.isFinite(previous)) {
				double width = coordinate - previous;
				if (width > GEOMETRY_EPSILON) {
					midpoints.add(new WeightedCoordinate((previous + coordinate) * 0.5, width));
				}
			}
			previous = coordinate;
		}
		midpoints.sort(Comparator.comparingDouble(WeightedCoordinate::weight).reversed());
		List<WeightedCoordinate> result = new ArrayList<>();
		for (int i = 0; i < midpoints.size() && i < SAMPLE_SEARCH_CANDIDATE_LIMIT; i++) {
			result.add(midpoints.get(i));
		}
		return result;
	}

	private void collectVertexCoordinates(BoundaryPath boundary, boolean xAxis, List<Double> out) {
		for (int i = 0; i < boundary.size(); i++) {
			out.add(xAxis ? boundary.xCoordinates[i] : boundary.yCoordinates[i]);
		}
	}

	private void collectTopologicalBoundaryProbeCandidates(FaceContext context,
			List<SampleCandidate> out) {
		int added = 0;
		BoundaryPath boundary = context.outerBoundary;
		for (int i = 0; i < boundary.size(); i++) {
			int next = (i + 1) % boundary.size();
			double originX = boundary.xCoordinates[i];
			double originY = boundary.yCoordinates[i];
			double targetX = boundary.xCoordinates[next];
			double targetY = boundary.yCoordinates[next];
			double dx = targetX - originX;
			double dy = targetY - originY;
			double length = Math.hypot(dx, dy);
			if (length <= GEOMETRY_EPSILON) {
				continue;
			}
			double midX = (originX + targetX) * 0.5;
			double midY = (originY + targetY) * 0.5;
			double leftNormalX = -dy / length;
			double leftNormalY = dx / length;
			double epsilon = Math.max(GEOMETRY_EPSILON, length * 1e-3);
			for (int attempt = 0; attempt < 12; attempt++) {
				GPoint2D probe = new GPoint2D(midX + epsilon * leftNormalX,
						midY + epsilon * leftNormalY);
				if (addCandidate(out, probe, Strategy.BOUNDARY_TOPOLOGICAL, 2, epsilon, context)) {
					added++;
					if (added >= SAMPLE_SEARCH_CANDIDATE_LIMIT) {
						return;
					}
				}
				epsilon *= 0.5;
			}
		}
	}

	private void collectBoundaryProbeCandidates(FaceContext context,
			List<SampleCandidate> out, double... normalSigns) {
		int added = 0;
		BoundaryPath boundary = context.outerBoundary;
		for (int i = 0; i < boundary.size(); i++) {
			int next = (i + 1) % boundary.size();
			double originX = boundary.xCoordinates[i];
			double originY = boundary.yCoordinates[i];
			double targetX = boundary.xCoordinates[next];
			double targetY = boundary.yCoordinates[next];
			double dx = targetX - originX;
			double dy = targetY - originY;
			double length = Math.hypot(dx, dy);
			if (length <= GEOMETRY_EPSILON) {
				continue;
			}
			double midX = (originX + targetX) * 0.5;
			double midY = (originY + targetY) * 0.5;
			for (double normalSign : normalSigns) {
				double leftNormalX = -dy / length * normalSign;
				double leftNormalY = dx / length * normalSign;
				double epsilon = Math.max(GEOMETRY_EPSILON, length * 1e-3);
				for (int attempt = 0; attempt < 12; attempt++) {
					GPoint2D probe = new GPoint2D(midX + epsilon * leftNormalX,
							midY + epsilon * leftNormalY);
					if (addCandidate(out, probe, Strategy.BOUNDARY_NORMAL, 2, epsilon,
							context)) {
						added++;
						if (added >= SAMPLE_SEARCH_CANDIDATE_LIMIT) {
							return;
						}
					}
					epsilon *= 0.5;
				}
			}
		}
	}

	private void collectScanlineInteriorPoints(FaceContext context,
			List<SampleCandidate> out) {
		List<Double> yCandidates = buildScanlineCandidates(context.outerBoundary,
				context.holeBoundaries, context.boundingBox.minY, context.boundingBox.maxY,
				false);
		collectHorizontalScanlineCandidates(yCandidates, context, out);
		List<Double> xCandidates = buildScanlineCandidates(context.outerBoundary,
				context.holeBoundaries, context.boundingBox.minX, context.boundingBox.maxX,
				true);
		collectVerticalScanlineCandidates(xCandidates, context, out);
	}

	private List<Double> buildScanlineCandidates(BoundaryPath outerBoundary,
			List<BoundaryPath> holeBoundaries, double min, double max, boolean xAxis) {
		List<Double> coordinates = new ArrayList<>();
		collectVertexCoordinates(outerBoundary, xAxis, coordinates);
		for (BoundaryPath holeBoundary : holeBoundaries) {
			collectVertexCoordinates(holeBoundary, xAxis, coordinates);
		}
		List<WeightedCoordinate> gapCandidates = new ArrayList<>();
		double previous = Double.NaN;
		coordinates.sort(Double::compareTo);
		for (double coordinate : coordinates) {
			if (Double.isFinite(previous) && Math.abs(coordinate - previous) > GEOMETRY_EPSILON) {
				double width = coordinate - previous;
				if (width > GEOMETRY_EPSILON) {
					gapCandidates.add(new WeightedCoordinate((previous + coordinate) * 0.5,
							width));
				}
			}
			previous = coordinate;
		}
		gapCandidates.sort(Comparator.comparingDouble(WeightedCoordinate::weight).reversed());
		List<Double> candidates = new ArrayList<>();
		for (int i = 0; i < gapCandidates.size()
				&& i < SAMPLE_SEARCH_CANDIDATE_LIMIT; i++) {
			addScanlineCandidate(candidates, gapCandidates.get(i).coordinate());
		}
		double span = max - min;
		for (int i = 1; i <= SAMPLE_SEARCH_CANDIDATE_LIMIT; i++) {
			double fraction = i / (double) (SAMPLE_SEARCH_CANDIDATE_LIMIT + 1);
			addScanlineCandidate(candidates, min + fraction * span);
		}
		return candidates;
	}

	private void addScanlineCandidate(List<Double> candidates, double coordinate) {
		for (double existing : candidates) {
			if (Math.abs(existing - coordinate) <= GEOMETRY_EPSILON) {
				return;
			}
		}
		candidates.add(coordinate);
	}

	private void collectHorizontalScanlineCandidates(List<Double> yCandidates,
			FaceContext context, List<SampleCandidate> out) {
		for (double y : yCandidates) {
			List<Interval> intervals = faceIntervalsOnHorizontalScanline(context, y);
			collectMidpointCandidatesFromIntervals(intervals, true, y, context, out);
		}
	}

	private void collectVerticalScanlineCandidates(List<Double> xCandidates,
			FaceContext context, List<SampleCandidate> out) {
		for (double x : xCandidates) {
			List<Interval> intervals = faceIntervalsOnVerticalScanline(context, x);
			collectMidpointCandidatesFromIntervals(intervals, false, x, context, out);
		}
	}

	private List<Interval> faceIntervalsOnHorizontalScanline(FaceContext context, double y) {
		List<Interval> intervals = intervalsFromIntersections(
				collectHorizontalIntersections(context.outerBoundary, y));
		for (BoundaryPath holeBoundary : context.holeBoundaries) {
			intervals = subtractIntervals(intervals,
					intervalsFromIntersections(collectHorizontalIntersections(holeBoundary, y)));
		}
		return intervals;
	}

	private List<Interval> faceIntervalsOnVerticalScanline(FaceContext context, double x) {
		List<Interval> intervals = intervalsFromIntersections(
				collectVerticalIntersections(context.outerBoundary, x));
		for (BoundaryPath holeBoundary : context.holeBoundaries) {
			intervals = subtractIntervals(intervals,
					intervalsFromIntersections(collectVerticalIntersections(holeBoundary, x)));
		}
		return intervals;
	}

	private List<Interval> intervalsFromIntersections(List<Double> intersections) {
		intersections.sort(Double::compareTo);
		List<Interval> intervals = new ArrayList<>();
		for (int i = 0; i + 1 < intersections.size(); i += 2) {
			double start = intersections.get(i);
			double end = intersections.get(i + 1);
			if (end - start > GEOMETRY_EPSILON) {
				intervals.add(new Interval(start, end));
			}
		}
		return intervals;
	}

	private List<Interval> subtractIntervals(List<Interval> intervals, List<Interval> holes) {
		if (intervals.isEmpty() || holes.isEmpty()) {
			return intervals;
		}
		List<Interval> result = new ArrayList<>();
		for (Interval interval : intervals) {
			List<Interval> remaining = new ArrayList<>();
			remaining.add(interval);
			for (Interval hole : holes) {
				remaining = subtractInterval(remaining, hole);
				if (remaining.isEmpty()) {
					break;
				}
			}
			result.addAll(remaining);
		}
		return result;
	}

	private List<Interval> subtractInterval(List<Interval> intervals, Interval hole) {
		List<Interval> result = new ArrayList<>();
		for (Interval interval : intervals) {
			if (hole.end <= interval.start + GEOMETRY_EPSILON
					|| hole.start >= interval.end - GEOMETRY_EPSILON) {
				result.add(interval);
				continue;
			}
			if (hole.start - interval.start > GEOMETRY_EPSILON) {
				result.add(new Interval(interval.start, Math.min(hole.start, interval.end)));
			}
			if (interval.end - hole.end > GEOMETRY_EPSILON) {
				result.add(new Interval(Math.max(hole.end, interval.start), interval.end));
			}
		}
		return result;
	}

	private void collectMidpointCandidatesFromIntervals(List<Interval> intervals,
			boolean horizontal, double fixedCoordinate, FaceContext context,
			List<SampleCandidate> out) {
		for (Interval interval : intervals) {
			GPoint2D candidate = horizontal
					? new GPoint2D(interval.midpoint(), fixedCoordinate)
					: new GPoint2D(fixedCoordinate, interval.midpoint());
			addCandidate(out, candidate, Strategy.SCANLINE, 0, interval.width() * 0.5, context);
		}
	}

	private List<Double> collectHorizontalIntersections(BoundaryPath boundary, double y) {
		List<Double> intersections = new ArrayList<>();
		for (int i = 0; i < boundary.size(); i++) {
			int next = (i + 1) % boundary.size();
			double x1 = boundary.xCoordinates[i];
			double y1 = boundary.yCoordinates[i];
			double x2 = boundary.xCoordinates[next];
			double y2 = boundary.yCoordinates[next];
			if ((y1 > y) == (y2 > y) || Math.abs(y2 - y1) <= GEOMETRY_EPSILON) {
				continue;
			}
			double t = (y - y1) / (y2 - y1);
			if (t < 0 || t > 1) {
				continue;
			}
			intersections.add(x1 + t * (x2 - x1));
		}
		return intersections;
	}

	private List<Double> collectVerticalIntersections(BoundaryPath boundary, double x) {
		List<Double> intersections = new ArrayList<>();
		for (int i = 0; i < boundary.size(); i++) {
			int next = (i + 1) % boundary.size();
			double x1 = boundary.xCoordinates[i];
			double y1 = boundary.yCoordinates[i];
			double x2 = boundary.xCoordinates[next];
			double y2 = boundary.yCoordinates[next];
			if ((x1 > x) == (x2 > x) || Math.abs(x2 - x1) <= GEOMETRY_EPSILON) {
				continue;
			}
			double t = (x - x1) / (x2 - x1);
			if (t < 0 || t > 1) {
				continue;
			}
			intersections.add(y1 + t * (y2 - y1));
		}
		return intersections;
	}

	private void collectGridInteriorPoints(FaceContext context,
			List<SampleCandidate> out) {
		PlanarGeometry.BoundingBox boundingBox = context.boundingBox;
		if (!boundingBox.isFinite()) {
			throw new IllegalStateException("Cannot search interior point: invalid bounding box");
		}
		for (int gridSize : SAMPLE_GRID_SIZES) {
			int candidatesBeforeGrid = out.size();
			double dx = (boundingBox.maxX - boundingBox.minX) / gridSize;
			double dy = (boundingBox.maxY - boundingBox.minY) / gridSize;
			for (int xIdx = 0; xIdx < gridSize; xIdx++) {
				double x = boundingBox.minX + (xIdx + 0.5) * dx;
				for (int yIdx = 0; yIdx < gridSize; yIdx++) {
					double y = boundingBox.minY + (yIdx + 0.5) * dy;
					GPoint2D candidate = new GPoint2D(x, y);
					addCandidate(out, candidate, Strategy.valueOf("GRID_" + gridSize), 1,
							boundingBoxScore(candidate, boundingBox), context);
				}
			}
			if (out.size() > candidatesBeforeGrid) {
				return;
			}
		}
	}
}

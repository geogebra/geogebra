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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.implicit.PointList;
import org.geogebra.common.util.debug.Log;

/**
 * Builds explicit visible contour fragments from raw pre-clip contours and viewport hits.
 * <p>
 * The builder walks raw marching-produced contour segments, inserts ordered hit points
 * on each source segment, and keeps only the visible subsegments that lie inside the
 * clip rectangle. Consecutive visible subsegments are accumulated into
 * {@link ClippedFragment}s while preserving the original contour traversal order.
 * </p>
 *
 * <p>
 * This builder does not fabricate viewport-closing edges and does not attempt to produce
 * fillable region cycles. Open fragments remain open by design; viewport closure is deferred
 * to later boundary-graph construction.
 * </p>
 *
 * <p>
 * The implementation assumes piecewise-linear contour geometry, i.e. the input contours come
 * from marching-style segment output rather than higher-order curved primitives.
 * </p>
 */
public final class ClippedFragmentsBuilder {
	private static final boolean FRAGMENT_DEBUG = false;

	private record SegmentNode(MyPoint point, EdgeHit hit, int sourceSegmentIndex) {}

	private static final class FragmentAccumulator {
		private final int sourceContourId;
		private final ClipRect rect;
		private final LinkedList<MyPoint> points = new LinkedList<>();
		private SegmentNode startNode;
		private SegmentNode endNode;

		private FragmentAccumulator(int sourceContourId, ClipRect rect) {
			this.sourceContourId = sourceContourId;
			this.rect = rect;
		}

		private void append(SegmentNode node) {
			if (points.isEmpty()) {
				startNode = node;
				points.add(copyPoint(node.point, false));
				endNode = node;
				return;
			}
			MyPoint last = points.getLast();
			if (!pointsEqual(last, node.point)) {
				points.add(copyPoint(node.point, true));
				endNode = node;
				return;
			}
			if (shouldPreferAsEndpoint(node, endNode)) {
				endNode = node;
			}
		}

		private boolean shouldPreferAsEndpoint(SegmentNode candidate, SegmentNode current) {
			if (current == null) {
				return true;
			}
			if (current.hit == null && candidate.hit != null) {
				return true;
			}
			if (current.hit != null && candidate.hit == null) {
				return false;
			}
			return candidate.hit != null;
		}

		private ClippedFragment toFragment() {
			if (points.size() < 2) {
				return null;
			}
			FragmentEndpoint start = endpointFrom(startNode, sourceContourId, rect);
			FragmentEndpoint end = endpointFrom(endNode, sourceContourId, rect);
			boolean closed = pointsEqual(points.getFirst(), points.getLast());
			if (!closed && shouldCloseAcrossSameViewportEdge(start, end)) {
				closed = true;
			}
			if (closed) {
				if (pointsEqual(points.getFirst(), points.getLast())) {
					points.removeLast();
				}
			}
			start = closed ? null : start;
			end = closed ? null : end;
			return new ClippedFragment(sourceContourId, points, closed, start, end);
		}

		private boolean shouldCloseAcrossSameViewportEdge(
				FragmentEndpoint start, FragmentEndpoint end) {
			if (start == null || end == null || points.size() < 3) {
				return false;
			}
			if (start.getEdge() == null || end.getEdge() == null) {
				return false;
			}
			return start.getEdge() == end.getEdge();
		}
	}

	private ClippedFragmentsBuilder() {
		// builder, no instance
	}

	/**
	 * Builds explicit clipped fragments for the given raw contours and hit list.
	 *
	 * @param contours raw pre-clip contours in source-contour order
	 * @param hits viewport hits collected against those contours
	 * @param rect clip rectangle defining the visible region
	 * @return explicit clipped fragments together with the original hit list
	 */
	public static ClippedFragmentsResult build(
			List<PointList> contours, List<EdgeHit> hits, ClipRect rect) {
		List<ClippedFragment> fragments = new ArrayList<>();
		Map<Integer, Map<Integer, List<EdgeHit>>> byContourSegment = indexHits(hits);
		for (int contourId = 0; contourId < contours.size(); contourId++) {
			PointList contour = contours.get(contourId);
			fragments.addAll(buildFragmentsForContour(
					contourId, contour, byContourSegment.getOrDefault(contourId, Map.of()), rect));
		}
		logSuspiciousFragments(fragments, rect);
		return new ClippedFragmentsResult(fragments, List.copyOf(hits));
	}

	private static List<ClippedFragment> buildFragmentsForContour(
			int contourId, PointList contour, Map<Integer, List<EdgeHit>> hitsBySegment, ClipRect rect) {
		List<ClippedFragment> fragments = new ArrayList<>();
		FragmentAccumulator current = null;
		List<MyPoint> contourPoints = contour.asPoints();
		int size = contourPoints.size();
		int segmentCount = contour.isClosed() ? size : Math.max(0, size - 1);

		for (int segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
			MyPoint start = contourPoints.get(segmentIndex);
			MyPoint end = contourPoints.get((segmentIndex + 1) % size);
			if (start == null || end == null) {
				continue;
			}
			List<SegmentNode> nodes = buildSegmentNodes(
					start, end, hitsBySegment.getOrDefault(segmentIndex, List.of()), segmentIndex);
			for (int i = 0; i < nodes.size() - 1; i++) {
				SegmentNode nodeA = nodes.get(i);
				SegmentNode nodeB = nodes.get(i + 1);
				if (pointsEqual(nodeA.point, nodeB.point)) {
					continue;
				}
				if (isInsideRect(nodeA.point, nodeB.point, rect)) {
					if (current == null) {
						current = new FragmentAccumulator(contourId, rect);
					}
					current.append(nodeA);
					current.append(nodeB);
				} else if (current != null) {
					ClippedFragment fragment = current.toFragment();
					if (fragment != null) {
						fragments.add(fragment);
					}
					current = null;
				}
			}
		}

		if (current != null) {
			ClippedFragment fragment = current.toFragment();
			if (fragment != null) {
				fragments.add(fragment);
			}
		}
		return mergeAcrossClosedContourSeam(contour, contourPoints, fragments);
	}

	private static List<ClippedFragment> mergeAcrossClosedContourSeam(
			PointList contour, List<MyPoint> contourPoints, List<ClippedFragment> fragments) {
		if (!contour.isClosed() || fragments.size() < 2 || contourPoints.isEmpty()) {
			return fragments;
		}
		ClippedFragment first = fragments.get(0);
		ClippedFragment last = fragments.get(fragments.size() - 1);
		MyPoint seam = contourPoints.get(0);
		if (!shouldMergeAcrossSeam(first, last, seam)) {
			return fragments;
		}

		List<MyPoint> mergedPoints =
				new ArrayList<>(last.points().size() + first.points().size() - 1);
		appendPoints(mergedPoints, last.points());
		appendPointsSkippingDuplicateStart(mergedPoints, first.points());
		ClippedFragment merged =
				new ClippedFragment(last.sourceContourId(), mergedPoints, false, last.start(), first.end());
		fragments.set(0, merged);
		fragments.remove(fragments.size() - 1);
		return fragments;
	}

	private static boolean shouldMergeAcrossSeam(
			ClippedFragment first, ClippedFragment last, MyPoint seam) {
		if (first.closed() || last.closed() || first.start() == null || last.end() == null) {
			return false;
		}
		if (first.start().getEdge() != null || last.end().getEdge() != null) {
			return false;
		}
		if (first.points().isEmpty() || last.points().isEmpty()) {
			return false;
		}
		return pointsEqual(first.points().get(0), seam)
				&& pointsEqual(last.points().get(last.points().size() - 1), seam);
	}

	private static void appendPoints(List<MyPoint> target, List<MyPoint> source) {
		for (int i = 0; i < source.size(); i++) {
			target.add(copyPoint(source.get(i), i != 0 || !target.isEmpty()));
		}
	}

	private static void appendPointsSkippingDuplicateStart(
			List<MyPoint> target, List<MyPoint> source) {
		int startIndex = source.isEmpty() ? 0 : 1;
		for (int i = startIndex; i < source.size(); i++) {
			target.add(copyPoint(source.get(i), true));
		}
	}

	private static List<SegmentNode> buildSegmentNodes(
			MyPoint start, MyPoint end, List<EdgeHit> hits, int segmentIndex) {
		List<SegmentNode> nodes = new ArrayList<>(hits.size() + 2);
		EdgeHit startHit = null;
		EdgeHit endHit = null;
		List<EdgeHit> sortedHits = new ArrayList<>(hits);
		sortedHits.sort(Comparator.comparingDouble(EdgeHit::tSegment));
		for (EdgeHit hit : sortedHits) {
			if (matchesSegmentStart(hit, start, end)) {
				startHit = hit;
				continue;
			}
			if (matchesSegmentEnd(hit, start, end)) {
				endHit = hit;
				continue;
			}
			nodes.add(new SegmentNode(hit.point(), hit, segmentIndex));
		}
		nodes.add(0, new SegmentNode(start, startHit, segmentIndex));
		nodes.add(new SegmentNode(end, endHit, segmentIndex));
		return nodes;
	}

	private static boolean matchesSegmentStart(EdgeHit hit, MyPoint start, MyPoint end) {
		return matchesEndpoint(hit, start, end, true);
	}

	private static boolean matchesSegmentEnd(EdgeHit hit, MyPoint start, MyPoint end) {
		return matchesEndpoint(hit, start, end, false);
	}

	private static boolean matchesEndpoint(
			EdgeHit hit, MyPoint endpoint, MyPoint otherEndpoint, boolean startEndpoint) {
		if (pointsNear(hit.point(), endpoint, endpointMatchTolerance(endpoint, otherEndpoint))) {
			return true;
		}
		double tSegment = hit.tSegment();
		if (!Double.isFinite(tSegment)) {
			return false;
		}
		double endpointTolerance = 1e-6;
		return startEndpoint ? tSegment <= endpointTolerance : tSegment >= 1.0 - endpointTolerance;
	}

	private static double endpointMatchTolerance(MyPoint a, MyPoint b) {
		double segmentLength = distance(a, b);
		return Math.max(1e-8, segmentLength * 1e-6);
	}

	private static Map<Integer, Map<Integer, List<EdgeHit>>> indexHits(List<EdgeHit> hits) {
		Map<Integer, Map<Integer, List<EdgeHit>>> indexed = new HashMap<>();
		for (EdgeHit hit : hits) {
			indexed
					.computeIfAbsent(hit.getContourId(), ignore -> new HashMap<>())
					.computeIfAbsent(hit.segIndex(), ignore -> new ArrayList<>())
					.add(hit);
		}
		return indexed;
	}

	private static boolean isInsideRect(MyPoint a, MyPoint b, ClipRect rect) {
		double midX = (a.x + b.x) * 0.5;
		double midY = (a.y + b.y) * 0.5;
		return midX >= rect.getXmin()
				&& midX <= rect.getXmax()
				&& midY >= rect.getYmin()
				&& midY <= rect.getYmax();
	}

	private static MyPoint copyPoint(MyPoint point, boolean lineTo) {
		MyPoint copy = new MyPoint(point.x, point.y, SegmentType.LINE_TO);
		copy.setLineTo(lineTo);
		return copy;
	}

	private static boolean pointsEqual(MyPoint p0, MyPoint p1) {
		return Math.abs(p0.x - p1.x) <= 1e-10 && Math.abs(p0.y - p1.y) <= 1e-10;
	}

	private static boolean pointsNear(MyPoint p0, MyPoint p1, double eps) {
		return Math.abs(p0.x - p1.x) <= eps && Math.abs(p0.y - p1.y) <= eps;
	}

	private static double distance(MyPoint p0, MyPoint p1) {
		return Math.hypot(p0.x - p1.x, p0.y - p1.y);
	}

	private static FragmentEndpoint endpointFrom(
			SegmentNode node, int sourceContourId, ClipRect rect) {
		if (node == null) {
			return null;
		}
		if (node.hit != null) {
			return new FragmentEndpoint(
					copyPoint(node.hit.point(), false),
					node.hit.edge(),
					node.hit.sPerimeter(),
					sourceContourId,
					node.hit.segIndex(),
					node.hit.tSegment());
		}
		FragmentEndpoint inferred = inferBoundaryEndpoint(node, sourceContourId, rect);
		if (inferred != null) {
			return inferred;
		}
		return new FragmentEndpoint(
				copyPoint(node.point, false),
				null,
				Double.NaN,
				sourceContourId,
				node.sourceSegmentIndex,
				Double.NaN);
	}

	private static FragmentEndpoint inferBoundaryEndpoint(
			SegmentNode node, int sourceContourId, ClipRect rect) {
		if (rect == null) {
			return null;
		}
		double x = node.point.x;
		double y = node.point.y;
		double eps = 1e-10;
		double width = rect.getXmax() - rect.getXmin();
		double height = rect.getYmax() - rect.getYmin();
		if (Math.abs(x - rect.getXmin()) <= eps) {
			double t = clamp01((rect.getYmax() - y) / height);
			return new FragmentEndpoint(
					copyPoint(node.point, false),
					ClipEdge.LEFT,
					3.0 + t,
					sourceContourId,
					node.sourceSegmentIndex,
					Double.NaN);
		}
		if (Math.abs(x - rect.getXmax()) <= eps) {
			double t = clamp01((rect.getYmax() - y) / height);
			return new FragmentEndpoint(
					copyPoint(node.point, false),
					ClipEdge.RIGHT,
					1.0 + t,
					sourceContourId,
					node.sourceSegmentIndex,
					Double.NaN);
		}
		if (Math.abs(y - rect.getYmax()) <= eps) {
			double t = clamp01((x - rect.getXmin()) / width);
			return new FragmentEndpoint(
					copyPoint(node.point, false),
					ClipEdge.TOP,
					t,
					sourceContourId,
					node.sourceSegmentIndex,
					Double.NaN);
		}
		if (Math.abs(y - rect.getYmin()) <= eps) {
			double t = clamp01((x - rect.getXmin()) / width);
			return new FragmentEndpoint(
					copyPoint(node.point, false),
					ClipEdge.BOTTOM,
					2.0 + (1.0 - t),
					sourceContourId,
					node.sourceSegmentIndex,
					Double.NaN);
		}
		return null;
	}

	private static double clamp01(double value) {
		return Math.max(0.0, Math.min(1.0, value));
	}

	private static void logSuspiciousFragments(List<ClippedFragment> fragments, ClipRect rect) {
		if (!FRAGMENT_DEBUG || rect == null) {
			return;
		}
		double eps = 1e-8;
		for (int i = 0; i < fragments.size(); i++) {
			ClippedFragment fragment = fragments.get(i);
			FragmentBounds bounds = fragmentBounds(fragment);
			if (bounds.maxX < rect.getXmin() - eps
					|| bounds.minX > rect.getXmax() + eps
					|| bounds.maxY < rect.getYmin() - eps
					|| bounds.minY > rect.getYmax() + eps) {
				Log.debug("[ClippedFragmentsBuilder] suspicious fragment outside clip rect"
						+ " clipRect=" + formatRect(rect)
						+ " fragmentIndex=" + i
						+ " contour=" + fragment.sourceContourId()
						+ " closed=" + fragment.closed()
						+ " points=" + fragment.points().size()
						+ " bbox=" + bounds
						+ " start=" + fragment.start()
						+ " end=" + fragment.end()
						+ " first="
						+ formatPoint(fragment.points().isEmpty() ? null : fragment.points().get(0))
						+ " last="
						+ formatPoint(
								fragment.points().isEmpty()
										? null
										: fragment.points().get(fragment.points().size() - 1)));
				return;
			}
		}
	}

	private static FragmentBounds fragmentBounds(ClippedFragment fragment) {
		FragmentBounds bounds = new FragmentBounds();
		for (MyPoint point : fragment.points()) {
			bounds.include(point.x, point.y);
		}
		return bounds;
	}

	private static String formatRect(ClipRect rect) {
		return "[" + rect.getXmin() + "," + rect.getXmax() + "]x[" + rect.getYmin() + ","
				+ rect.getYmax() + "]";
	}

	private static String formatPoint(MyPoint point) {
		return point == null ? "null" : "(" + point.x + "," + point.y + ")";
	}

	private static final class FragmentBounds {
		private double minX = Double.POSITIVE_INFINITY;
		private double maxX = Double.NEGATIVE_INFINITY;
		private double minY = Double.POSITIVE_INFINITY;
		private double maxY = Double.NEGATIVE_INFINITY;

		private void include(double x, double y) {
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}

		@Override
		public String toString() {
			return "[" + minX + "," + maxX + "]x[" + minY + "," + maxY + "]";
		}
	}
}

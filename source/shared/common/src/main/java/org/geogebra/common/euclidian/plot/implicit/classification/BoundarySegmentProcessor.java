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
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.kernel.MyPoint;

final class BoundarySegmentProcessor {
	private final EpsilonPolicy epsilonPolicy;

	private record BoundaryBuildData(List<BoundarySegment> segments,
									 FragmentBounds[] fragmentBounds,
									 boolean[] fragmentPairMayIntersect) {
	}

	BoundarySegmentProcessor(EpsilonPolicy epsilonPolicy) {
		this.epsilonPolicy = epsilonPolicy;
	}

	List<BoundarySegment> process(List<ClippedFragment> fragments) {
		BoundaryBuildData buildData = buildBoundarySegments(fragments);
		List<BoundarySegment> segments = buildData.segments;
		splitBoundarySegmentsAtIntersections(segments, buildData.fragmentBounds,
				buildData.fragmentPairMayIntersect);
		return segments;
	}

	private BoundaryBuildData buildBoundarySegments(List<ClippedFragment> fragments) {
		List<BoundarySegment> segments = new ArrayList<>();
		FragmentBounds[] fragmentBounds = new FragmentBounds[fragments.size()];
		for (int fragmentId = 0; fragmentId < fragments.size(); fragmentId++) {
			ClippedFragment fragment = fragments.get(fragmentId);
			List<MyPoint> points = fragment.points();
			int size = points.size();
			int segmentCount = fragment.closed() ? size : Math.max(0, size - 1);
			FragmentBounds bounds = new FragmentBounds();
			for (int segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
				MyPoint start = points.get(segmentIndex);
				MyPoint end = points.get((segmentIndex + 1) % size);
				if (start == null || end == null || samePoint(start, end)) {
					continue;
				}
				bounds.include(start.x, start.y);
				bounds.include(end.x, end.y);
				segments.add(new BoundarySegment(fragmentId, fragment.sourceContourId(),
						segmentIndex, segmentCount, fragment.closed(), start, end));
			}
			fragmentBounds[fragmentId] = bounds;
		}
		return new BoundaryBuildData(segments, fragmentBounds,
				buildFragmentPairMatrix(fragmentBounds));
	}

	private void splitBoundarySegmentsAtIntersections(List<BoundarySegment> segments,
			FragmentBounds[] fragmentBounds, boolean[] fragmentPairMayIntersect) {
		if (segments.size() < 2) {
			for (BoundarySegment segment : segments) {
				segment.normalize(epsilonPolicy.getIntersection());
			}
			return;
		}

		double cellSize = computeCellSize(segments);
		Map<Long, IntBucket> cells = new HashMap<>();
		int[] seenByCurrentSegment = new int[segments.size()];
		for (int i = 0; i < segments.size(); i++) {
			BoundarySegment segment = segments.get(i);
			int stamp = i + 1;
			int minCellX = cellCoord(segment.minX, cellSize);
			int maxCellX = cellCoord(segment.maxX, cellSize);
			int minCellY = cellCoord(segment.minY, cellSize);
			int maxCellY = cellCoord(segment.maxY, cellSize);
			for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
				for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
					long cellKey = cellKey(cellX, cellY);
					IntBucket bucket = cells.get(cellKey);
					if (bucket != null) {
						for (int bucketIndex = 0; bucketIndex < bucket.size(); bucketIndex++) {
							int otherIndex = bucket.get(bucketIndex);
							if (seenByCurrentSegment[otherIndex] == stamp) {
								continue;
							}
							seenByCurrentSegment[otherIndex] = stamp;
							BoundarySegment other = segments.get(otherIndex);
							if (shouldProcessPair(other, segment, fragmentBounds,
									fragmentPairMayIntersect)) {
								addIntersectionParams(other, segment);
							}
						}
					}
					if (bucket == null) {
						bucket = new IntBucket();
						cells.put(cellKey, bucket);
					}
					bucket.add(i);
				}
			}
		}
		for (BoundarySegment segment : segments) {
			segment.normalize(epsilonPolicy.getIntersection());
		}
	}

	private boolean[] buildFragmentPairMatrix(FragmentBounds[] fragmentBounds) {
		int fragmentCount = fragmentBounds.length;
		boolean[] mayIntersect = new boolean[fragmentCount * fragmentCount];
		for (int i = 0; i < fragmentCount; i++) {
			for (int j = i; j < fragmentCount; j++) {
				boolean overlaps = i == j
						|| boundingBoxesOverlap(fragmentBounds[i], fragmentBounds[j]);
				mayIntersect[i * fragmentCount + j] = overlaps;
				mayIntersect[j * fragmentCount + i] = overlaps;
			}
		}
		return mayIntersect;
	}

	private double computeCellSize(List<BoundarySegment> segments) {
		double totalSpan = 0;
		for (BoundarySegment segment : segments) {
			totalSpan += Math.max(segment.maxX - segment.minX, segment.maxY - segment.minY);
		}
		double avgSpan = totalSpan / segments.size();
		return Math.max(avgSpan, epsilonPolicy.getIntersection() * 16);
	}

	private int cellCoord(double value, double cellSize) {
		return (int) Math.floor(value / cellSize);
	}

	private long cellKey(int cellX, int cellY) {
		return (((long) cellX) << 32) ^ (cellY & 0xffffffffL);
	}

	private boolean shouldProcessPair(BoundarySegment segment1, BoundarySegment segment2,
			FragmentBounds[] fragmentBounds, boolean[] fragmentPairMayIntersect) {
		if (!fragmentPairMayIntersect(segment1.fragmentId, segment2.fragmentId,
				fragmentBounds.length, fragmentPairMayIntersect)) {
			return false;
		}

		return !segment1.isAdjacent(segment2) && boundingBoxesOverlap(segment1, segment2);
	}

	private boolean fragmentPairMayIntersect(int fragmentId1, int fragmentId2, int fragmentCount,
			boolean[] fragmentPairMayIntersect) {
		return fragmentPairMayIntersect[fragmentId1 * fragmentCount + fragmentId2];
	}

	private boolean boundingBoxesOverlap(FragmentBounds bounds1, FragmentBounds bounds2) {
		if (bounds1 == null || bounds2 == null || !bounds1.present || !bounds2.present) {
			return false;
		}
		double eps = epsilonPolicy.getIntersection();
		boolean xOverlap = bounds1.minX <= bounds2.maxX + eps
				&& bounds2.minX <= bounds1.maxX + eps;
		boolean yOverlap = bounds1.minY <= bounds2.maxY + eps
				&& bounds2.minY <= bounds1.maxY + eps;
		return xOverlap && yOverlap;
	}

	private boolean boundingBoxesOverlap(BoundarySegment segment1, BoundarySegment segment2) {
		double eps = epsilonPolicy.getIntersection();
		boolean xOverlap = segment1.minX <= segment2.maxX + eps
				&& segment2.minX <= segment1.maxX + eps;
		boolean yOverlap = segment1.minY <= segment2.maxY + eps
				&& segment2.minY <= segment1.maxY + eps;
		return xOverlap && yOverlap;
	}

	private void addIntersectionParams(BoundarySegment segment1, BoundarySegment segment2) {
		double rX = segment1.dx;
		double rY = segment1.dy;
		double sX = segment2.dx;
		double sY = segment2.dy;
		double denom = cross(rX, rY, sX, sY);
		double eps = epsilonPolicy.getIntersection();
		double pX = segment1.startX;
		double pY = segment1.startY;
		double qX = segment2.startX;
		double qY = segment2.startY;
		if (Math.abs(denom) <= eps) {
			if (Math.abs(cross(qX - pX, qY - pY, rX, rY)) <= eps) {
				addOverlapParams(segment1, segment2);
			}
			return;
		}
		double qMinusPX = qX - pX;
		double qMinusPY = qY - pY;
		double t1 = cross(qMinusPX, qMinusPY, sX, sY) / denom;
		double t2 = cross(qMinusPX, qMinusPY, rX, rY) / denom;
		if (!isValidSegmentParam(t1) || !isValidSegmentParam(t2)) {
			return;
		}
		segment1.addParam(t1);
		segment2.addParam(t2);
	}

	private void addOverlapParams(BoundarySegment segment1, BoundarySegment segment2) {
		addParamIfValid(segment1, paramOnSegment(segment1, segment2.startX, segment2.startY));
		addParamIfValid(segment1, paramOnSegment(segment1, segment2.endX, segment2.endY));
		addParamIfValid(segment2, paramOnSegment(segment2, segment1.startX, segment1.startY));
		addParamIfValid(segment2, paramOnSegment(segment2, segment1.endX, segment1.endY));
	}

	private void addParamIfValid(BoundarySegment segment, double t) {
		if (isValidSegmentParam(t)) {
			segment.addParam(t);
		}
	}

	private double paramOnSegment(BoundarySegment segment, double x, double y) {
		if (Math.abs(segment.dx) >= Math.abs(segment.dy)) {
			return segment.dx == 0 ? 0 : (x - segment.startX) / segment.dx;
		}
		return segment.dy == 0 ? 0 : (y - segment.startY) / segment.dy;
	}

	private boolean isValidSegmentParam(double t) {
		double eps = epsilonPolicy.getSnap();
		return Double.isFinite(t) && t >= -eps && t <= 1.0 + eps;
	}

	private static double cross(double ax, double ay, double bx, double by) {
		return ax * by - ay * bx;
	}

	private static boolean samePoint(MyPoint p0, MyPoint p1) {
		return Math.abs(p0.x - p1.x) <= 1e-10 && Math.abs(p0.y - p1.y) <= 1e-10;
	}
}

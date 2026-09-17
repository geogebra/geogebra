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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;

/**
 * Finds intersections between contour segments and records split parameters.
 */
public final class ContourIntersectionFinder {
	private final ContourSegmentRegistry registry;
	private final EpsilonPolicy epsilonPolicy;

	/**
	 * @param registry contour segments to update with intersection parameters
	 * @param epsilonPolicy tolerances used for intersection tests
	 */
	public ContourIntersectionFinder(ContourSegmentRegistry registry, EpsilonPolicy epsilonPolicy) {
		this.registry = registry;
		this.epsilonPolicy = epsilonPolicy;
	}

	/**
	 * Finds segment intersections and updates the registered segments in place.
	 */
	public void run() {
		List<ContourSegment> segments = new ArrayList<>(registry.getAllSegments());
		if (segments.size() < 2) {
			return;
		}
		double cellSize = computeCellSize(segments);
		Map<Long, List<Integer>> cells = new HashMap<>();
		Set<Long> seenPairs = new HashSet<>();
		for (int i = 0; i < segments.size(); i++) {
			ContourSegment segment = segments.get(i);
			int minCellX = cellCoord(segment.getMinX(), cellSize);
			int maxCellX = cellCoord(segment.getMaxX(), cellSize);
			int minCellY = cellCoord(segment.getMinY(), cellSize);
			int maxCellY = cellCoord(segment.getMaxY(), cellSize);
			for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
				for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
					long cellKey = cellKey(cellX, cellY);
					List<Integer> bucket = cells.get(cellKey);
					if (bucket != null) {
						for (int otherIndex : bucket) {
							long pairKey = pairKey(otherIndex, i);
							if (seenPairs.add(pairKey)) {
								processCandidatePair(segments.get(otherIndex), segment);
							}
						}
					}
					cells.computeIfAbsent(cellKey, k -> new ArrayList<>()).add(i);
				}
			}
		}
	}

	private void processCandidatePair(ContourSegment segment1, ContourSegment segment2) {
		if (segment1.getContourId() == segment2.getContourId()) {
			int pos1 = segment1.getPositionInContour();
			int pos2 = segment2.getPositionInContour();
			int contourSegmentCount = segment1.getContourSegmentCount();
			if (pos1 >= 0 && pos2 >= 0 && contourSegmentCount >= 2) {
				int diff = Math.abs(pos1 - pos2);
				if (diff == 1 || diff == contourSegmentCount - 1) {
					return;
				}
			}
		}
		if (!isBoundingBoxesOverlap(segment1, segment2)) {
			return;
		}
		doIntersectSegments(segment1, segment2);
	}

	private double computeCellSize(List<ContourSegment> segments) {
		double totalSpan = 0;
		for (ContourSegment segment : segments) {
			totalSpan +=
					Math.max(segment.getMaxX() - segment.getMinX(), segment.getMaxY() - segment.getMinY());
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

	private long pairKey(int i, int j) {
		int min = Math.min(i, j);
		int max = Math.max(i, j);
		return (((long) min) << 32) | (max & 0xffffffffL);
	}

	private void doIntersectSegments(ContourSegment segment1, ContourSegment segment2) {
		double pX = segment1.getStart().x;
		double pY = segment1.getStart().y;
		double rX = segment1.getEnd().x - pX;
		double rY = segment1.getEnd().y - pY;

		double qX = segment2.getStart().x;
		double qY = segment2.getStart().y;
		double sX = segment2.getEnd().x - qX;
		double sY = segment2.getEnd().y - qY;

		double denom = cross(rX, rY, sX, sY);
		double eps = epsilonPolicy.getIntersection();
		if (Math.abs(denom) <= eps) {
			if (Math.abs(cross(qX - pX, qY - pY, rX, rY)) <= eps) {
				addCollinearOverlapIntersections(segment1, segment2);
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

		segment1.addIntersectParam(t1);
		segment2.addIntersectParam(t2);
	}

	private void addCollinearOverlapIntersections(ContourSegment segment1, ContourSegment segment2) {
		addParamIfValid(segment1, paramOnSegment(segment1, segment2.getStart()));
		addParamIfValid(segment1, paramOnSegment(segment1, segment2.getEnd()));
		addParamIfValid(segment2, paramOnSegment(segment2, segment1.getStart()));
		addParamIfValid(segment2, paramOnSegment(segment2, segment1.getEnd()));
	}

	private boolean isBoundingBoxesOverlap(ContourSegment segment1, ContourSegment segment2) {
		double eps = epsilonPolicy.getIntersection();
		boolean xOverlap = segment1.getMinX() <= segment2.getMaxX() + eps
				&& segment2.getMinX() <= segment1.getMaxX() + eps;
		boolean yOverlap = segment1.getMinY() <= segment2.getMaxY() + eps
				&& segment2.getMinY() <= segment1.getMaxY() + eps;
		return xOverlap && yOverlap;
	}

	private boolean isValidSegmentParam(double t) {
		double eps = epsilonPolicy.getSnap();
		return Double.isFinite(t) && t >= -eps && t <= 1.0 + eps;
	}

	private void addParamIfValid(ContourSegment segment, double t) {
		if (isValidSegmentParam(t)) {
			segment.addIntersectParam(t);
		}
	}

	private double paramOnSegment(ContourSegment segment, org.geogebra.common.kernel.MyPoint point) {
		double dx = segment.getEnd().x - segment.getStart().x;
		double dy = segment.getEnd().y - segment.getStart().y;
		if (Math.abs(dx) >= Math.abs(dy)) {
			return dx == 0 ? 0 : (point.x - segment.getStart().x) / dx;
		}
		return dy == 0 ? 0 : (point.y - segment.getStart().y) / dy;
	}

	private static double cross(double ax, double ay, double bx, double by) {
		return ax * by - ay * bx;
	}
}

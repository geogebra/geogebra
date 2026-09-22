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
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.kernel.MyPoint;

/**
 * One polyline segment of a contour plus split parameters from intersections.
 */
public final class ContourSegment {
	private final MyPoint start;
	private final MyPoint end;
	private final SegmentKey key;
	private final double minX;
	private final double maxX;
	private final double minY;
	private final double maxY;
	private int positionInContour = -1;
	private int contourSegmentCount = -1;
	private final List<Double> intersectParams = new ArrayList<>();

	/**
	 * @param start segment start point
	 * @param end segment end point
	 * @param contourId source contour id
	 * @param segmentIndex source segment index inside the contour
	 */
	public ContourSegment(MyPoint start, MyPoint end, int contourId, int segmentIndex) {
		this.start = start;
		this.end = end;
		this.key = new SegmentKey(contourId, segmentIndex);
		this.minX = Math.min(start.x, end.x);
		this.maxX = Math.max(start.x, end.x);
		this.minY = Math.min(start.y, end.y);
		this.maxY = Math.max(start.y, end.y);
		addIntersectParam(0.0);
		addIntersectParam(1.0);
	}

	public MyPoint getStart() {
		return start;
	}

	public MyPoint getEnd() {
		return end;
	}

	public SegmentKey getKey() {
		return key;
	}

	/**
	 * @return id of the contour this segment belongs to.
	 */
	public int getContourId() {
		return key.getContourId();
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public int getPositionInContour() {
		return positionInContour;
	}

	public void setPositionInContour(int positionInContour) {
		this.positionInContour = positionInContour;
	}

	public int getContourSegmentCount() {
		return contourSegmentCount;
	}

	public void setContourSegmentCount(int contourSegmentCount) {
		this.contourSegmentCount = contourSegmentCount;
	}

	/**
	 * Adds a parameter where this segment must be split.
	 * @param t segment parameter in the interval around {@code [0, 1]}
	 */
	public void addIntersectParam(double t) {
		intersectParams.add(t);
	}

	/**
	 * @return immutable split parameters for this segment
	 */
	public List<Double> getIntersectParams() {
		return Collections.unmodifiableList(intersectParams);
	}

	/**
	 * Normalizes split parameters before graph edge emission.
	 * @param eps endpoint and duplicate tolerance
	 */
	public void normalizeSortDedup(double eps) {
		intersectParams.replaceAll(t -> clamp(t, eps));
		Collections.sort(intersectParams);
		List<Double> deduped = new ArrayList<>(intersectParams.size());
		for (double t : intersectParams) {
			if (deduped.isEmpty() || Math.abs(t - deduped.get(deduped.size() - 1)) > eps) {
				deduped.add(t);
			}
		}
		intersectParams.clear();
		intersectParams.addAll(deduped);
	}

	static double clamp(double t, double eps) {
		if (Math.abs(t) <= eps) {
			return 0.0;
		}
		if (Math.abs(1.0 - t) <= eps) {
			return 1.0;
		}
		return Math.max(0.0, Math.min(1.0, t));
	}

	/**
	 * @return whether this segment has endpoint split parameters after normalization
	 */
	public boolean isValid() {
		return intersectParams.size() >= 2
				&& intersectParams.get(0) == 0
				&& intersectParams.get(intersectParams.size() - 1) == 1;
	}

	/**
	 * @return adjacent parameter intervals created by the split parameters
	 */
	public List<ParamInterval> getAdjacentParamPairs() {
		List<ParamInterval> list = new ArrayList<>(intersectParams.size() - 1);
		for (int i = 0; i < intersectParams.size() - 1; i++) {
			list.add(new ParamInterval(intersectParams.get(i), intersectParams.get(i + 1)));
		}
		return list;
	}

	/**
	 * @param t segment parameter
	 * @return point on this segment at the given parameter
	 */
	public GPoint2D pointAt(double t) {
		return new GPoint2D(start.x + t * (end.x - start.x), start.y + t * (end.y - start.y));
	}

	@Override
	public String toString() {
		return "ContourSegment{" + "start=" + start + ", end=" + end + ", key=" + key + '}';
	}
}

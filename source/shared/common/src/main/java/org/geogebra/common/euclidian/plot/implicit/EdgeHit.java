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

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.PointList;

/**
 * A single intersection of a loop segment with a viewport edge.
 *
 * <ul>
 *   <li>{@code edge} - which edge was hit (TOP/RIGHT/BOTTOM/LEFT)</li>
 *   <li>{@code tOnEdge} - local parameter on that edge in [0,1]
 *       (TOP/BOTTOM: left -> right, RIGHT/LEFT: bottom -> top)</li>
 *   <li>{@code segIndex} - index of the loop segment A->B that produced the hit</li>
 *   <li>{@code alpha} - parameter along A->B in (0,1] (half-open rule)</li>
 *   <li>{@code sPerimeter} - perimeter parameter in [0,4) clockwise, for global ordering</li>
 *   <li>{@code point} - world coordinates of the hit (corner-snapped, clamped to edge)</li>
 * </ul>
 */
public final class EdgeHit {

	private final ClipEdge edge;
	private final double tOnEdge;     // [0..1]
	private final int segIndex;       // segment index A->B
	private final double tSegment;       // (0..1] half-open
	private final double sPerimeter;  // [0..4)
	private final MyPoint point;      // world coords (snapped/clamped)
	private PointList owner;
	private int contourId;

	/**
	 * Constructs an edge hit with the specified edge, edge-relative and segment-relative
	 * parameters, perimeter location, and hit coordinates.
	 *
	 * @param edge the edge of the viewport that was hit
	 * @param tOnEdge parameter along the edge (in [0,1])
	 * @param segIndex index of the segment that produced the hit
	 * @param tSegment parameter along the segment (in (0,1])
	 * @param sPerimeter perimeter parameter in [0,4) for ordering hits
	 * @param x the x-coordinate of the hit in world coordinates
	 * @param y the y-coordinate of the hit in world coordinates
	 */
	public EdgeHit(ClipEdge edge,
			double tOnEdge,
			int segIndex,
			double tSegment,
			double sPerimeter,
			double x,
			double y) {
		this(edge, tOnEdge, segIndex, tSegment, sPerimeter, new MyPoint(x, y));
	}

	/**
	 * Constructs an edge hit with the specified edge, edge-relative
	 * and segment-relative parameters, perimeter location, and hit point.
	 *
	 * @param edge the edge of the viewport that was hit
	 * @param tOnEdge parameter along the edge (in [0,1])
	 * @param segIndex index of the segment that produced the hit
	 * @param tSegment parameter along the segment (in (0,1])
	 * @param sPerimeter perimeter parameter in [0,4) for ordering hits
	 * @param point the location of the hit in world coordinates
	 */
	public EdgeHit(ClipEdge edge,
			double tOnEdge,
			int segIndex,
			double tSegment,
			double sPerimeter,
			MyPoint point) {
		this.edge = edge;
		this.tOnEdge = tOnEdge;
		this.segIndex = segIndex;
		this.tSegment = tSegment;
		this.sPerimeter = sPerimeter;
		this.point = point;
	}

	/**
	 * Returns the edge of the viewport where this hit occurred.
	 *
	 * @return the edge (TOP, RIGHT, BOTTOM, or LEFT)
	 */
	public ClipEdge edge() {
		return edge;
	}

	/**
	 * Returns the normalized coordinate on the edge that was hit, in the range [0,1].
	 *
	 * @return edge-relative parameter
	 */
	public double tOnEdge() {
		return tOnEdge;
	}

	/**
	 * Returns the index of the original segment that produced this hit.
	 *
	 * @return index of the curve segment
	 */
	public int segIndex() {
		return segIndex;
	}

	/**
	 * Returns the parameter along the segment (in (0,1]) where this hit occurs.
	 *
	 * @return curve-segment-relative parameter
	 */
	public double tSegment() {
		return tSegment;
	}

	/**
	 * Returns the perimeter position of this hit (in [0,4)), used for sorting.
	 *
	 * @return perimeter parameter
	 */
	public double sPerimeter() {
		return sPerimeter;
	}

	/**
	 * Returns the hit position in world coordinates.
	 *
	 * @return world-space hit location
	 */
	public MyPoint point() {
		return point;
	}

	/**
	 * Returns the PointList this hit belongs to, if any.
	 *
	 * @return the owning point list, or null
	 */
	public PointList getOwner() {
		return owner;
	}

	/**
	 * Assigns the owning PointList of this hit.
	 *
	 * @param owner the curve segment that this hit belongs to
	 */
	public void setOwner(PointList owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "HitEdge{" + edge.name()
				+ ", t=" + tOnEdge
				+ ", s=" + sPerimeter
				+ ", p=(" + point.x + "," + point.y + ")}"
				+ "\n";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof EdgeHit)) {
			return false;
		}

		EdgeHit that = (EdgeHit) o;
		return edge == that.edge
				&& Double.doubleToLongBits(tOnEdge) == Double.doubleToLongBits(that.tOnEdge)
				&& segIndex == that.segIndex
				&& Double.doubleToLongBits(tSegment) == Double.doubleToLongBits(that.tSegment)
				&& Double.doubleToLongBits(sPerimeter) == Double.doubleToLongBits(that.sPerimeter)
				&& Double.doubleToLongBits(point.x) == Double.doubleToLongBits(that.point.x)
				&& Double.doubleToLongBits(point.y) == Double.doubleToLongBits(that.point.y);
	}

	@Override
	public int hashCode() {
		int h = edge.hashCode();
		h = 31 * h + Double.hashCode(tOnEdge);
		h = 31 * h + Integer.hashCode(segIndex);
		h = 31 * h + Double.hashCode(tSegment);
		h = 31 * h + Double.hashCode(sPerimeter);
		h = 31 * h + Double.hashCode(point.x);
		h = 31 * h + Double.hashCode(point.y);
		return h;
	}

	public void setContourId(int contourId) {
		this.contourId = contourId;
	}

	public int getContourId() {
		return contourId;
	}

	/**
	 * Checks whether {@link #tSegment()} is finite and within the tolerated segment range.
	 *
	 * @param eps allowed numeric tolerance around {@code [0, 1]}
	 * @return {@code true} if {@code tSegment} is finite and lies within the tolerated range
	 */
	public boolean isValidTSegment(double eps) {
		return !(Double.isNaN(tSegment) || Double.isInfinite(tSegment))
				&& tSegment > -eps && tSegment < 1.0 + eps;
	}
}

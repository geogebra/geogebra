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

/**
 * Endpoint metadata for one open {@link ClippedFragment}.
 * <p>
 * This object preserves how a fragment endpoint relates back to the raw contour and,
 * when applicable, to the viewport clipping boundary.
 * </p>
 *
 * <p>
 * If the endpoint corresponds to a viewport hit, {@link #getEdge()},
 * {@link #getSPerimeter()}, {@link #getTSegment()} describe
 * the clipped-edge intersection. If the endpoint comes from a visible source point
 * rather than a viewport hit, edge-related fields may be {@code null} or {@code NaN}.
 * </p>
 *
 * <p>
 * {@link #getSourceSegmentIndex()} always refers to the original source-contour segment
 * index, not a fragment-local segment index.
 * </p>
 */
public final class FragmentEndpoint {
	private final MyPoint point;
	private final ClipEdge edge;
	private final double sPerimeter;
	private final int sourceContourId;
	private final int sourceSegmentIndex;
	private final double tSegment;

	/**
	 * Creates endpoint metadata for one clipped fragment endpoint.
	 * @param point endpoint coordinates
	 * @param edge viewport edge on which this endpoint lies, or {@code null} if it is not a viewport hit
	 * @param sPerimeter perimeter-order coordinate of the hit on the clip rectangle,
	 * or {@code NaN} if not applicable
	 * @param sourceContourId id of the raw source contour
	 * @param sourceSegmentIndex index of the original source-contour segment containing this endpoint
	 * @param tSegment parametric position on the original source segment, or {@code NaN} if not applicable
	 */
	public FragmentEndpoint(
			MyPoint point,
			ClipEdge edge,
			double sPerimeter,
			int sourceContourId,
			int sourceSegmentIndex,
			double tSegment) {
		this.point = point;
		this.edge = edge;
		this.sPerimeter = sPerimeter;
		this.sourceContourId = sourceContourId;
		this.sourceSegmentIndex = sourceSegmentIndex;
		this.tSegment = tSegment;
	}

	public MyPoint getPoint() {
		return point;
	}

	public ClipEdge getEdge() {
		return edge;
	}

	public double getSPerimeter() {
		return sPerimeter;
	}

	public int getSourceContourId() {
		return sourceContourId;
	}

	/**
	 * Returns the index of the original raw source-contour segment containing this endpoint.
	 *
	 * @return source-contour segment index
	 */
	public int getSourceSegmentIndex() {
		return sourceSegmentIndex;
	}

	public double getTSegment() {
		return tSegment;
	}

	@Override
	public String toString() {
		return "FragmentEndpoint{"
				+ "point=" + point
				+ ", edge=" + edge
				+ ", sPerimeter=" + sPerimeter
				+ ", sourceContourId=" + sourceContourId
				+ ", sourceSegmentIndex=" + sourceSegmentIndex
				+ ", tSegment=" + tSegment
				+ '}';
	}
}

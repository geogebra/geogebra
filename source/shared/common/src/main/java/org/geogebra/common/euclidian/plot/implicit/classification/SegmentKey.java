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

import java.util.Objects;

/**
 * Stable identifier of a segment inside one source contour.
 */
public final class SegmentKey {
	private final int contourId;
	private final int segmentIndex;

	/**
	 * @param contourId source contour id
	 * @param segmentIndex segment index inside the contour
	 */
	public SegmentKey(int contourId, int segmentIndex) {
		this.contourId = contourId;
		this.segmentIndex = segmentIndex;
	}

	public int getContourId() {
		return contourId;
	}

	public int getSegmentIndex() {
		return segmentIndex;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SegmentKey that = (SegmentKey) o;
		return contourId == that.contourId && segmentIndex == that.segmentIndex;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contourId, segmentIndex);
	}

	@Override
	public String toString() {
		return "SegmentKey{"
				+ "contourId=" + contourId
				+ ", segmentIndex=" + segmentIndex
				+ '}';
	}
}

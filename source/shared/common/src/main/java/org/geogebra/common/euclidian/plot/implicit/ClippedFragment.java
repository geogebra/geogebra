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

import java.util.List;

import org.geogebra.common.kernel.MyPoint;

/**
 * One explicit post-clip contour fragment derived from a raw pre-clip contour.
 * <p>
 * A fragment preserves the traversal order of the source contour inside the current
 * clip rectangle. Fragments may be either:
 * <ul>
 *   <li><b>closed</b>: a naturally closed visible loop that remains closed after clipping</li>
 *   <li><b>open</b>: a visible contour fragment whose endpoints are not connected by any
 *       synthetic closure edge</li>
 * </ul>
 * Open fragments intentionally remain open in this model; viewport-closing runs are
 * not embedded into the fragment geometry and are expected to be introduced later by
 * boundary-graph construction.
 * </p>
 *
 * <p>
 * For open fragments, {@link #start ()} and {@link #end ()} carry endpoint metadata
 * derived from the clipping pass. For closed fragments, both endpoint descriptors are
 * {@code null}.
 * </p>
 */
public record ClippedFragment(int sourceContourId, List<MyPoint> points, boolean closed,
							  FragmentEndpoint start, FragmentEndpoint end) {

	/**
	 * Returns whether this fragment is naturally closed after clipping.
	 * <p>
	 * If {@code false}, no synthetic edge is implied between the last point and
	 * the first point.
	 * </p>
	 * @return {@code true} for naturally closed visible loops, {@code false} for open fragments
	 */
	@Override
	public boolean closed() {
		return closed;
	}

	@Override
	public String toString() {
		return "ClippedFragment{"
				+ "sourceContourId=" + sourceContourId
				+ ", points=" + points.size()
				+ ", closed=" + closed
				+ ", start=" + start
				+ ", end=" + end
				+ '}';
	}
}

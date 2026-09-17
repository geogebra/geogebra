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

/**
 * Primary output of the clipping stage: explicit visible contour fragments together with
 * the collected viewport hits used to derive them.
 * <p>
 * The fragment list is the post-clip geometry contract for downstream consumers such as
 * visible contour drawing and later boundary-graph construction. The hit list is preserved
 * as auxiliary clipping metadata for downstream topology and diagnostics.
 * </p>
 * @apiNote if using the canonical constructor, make sure both lists passed to it are not referenced
 * from elsewhere.
 */
public record ClippedFragmentsResult(
		List<ClippedFragment> fragments, List<EdgeHit> hits, ClipRect clipRect) {
	/**
	 * Creates a clipping result bundle without explicit clip-rectangle metadata.
	 * @param fragments explicit visible contour fragments produced by clipping
	 * @param hits collected viewport hits from the same clipping pass
	 * @apiNote make sure both lists are not referenced from elsewhere.
	 */
	public ClippedFragmentsResult(List<ClippedFragment> fragments, List<EdgeHit> hits) {
		this(fragments, hits, null);
	}

	/**
	 * Creates a clipping result bundle.
	 * @param result contains explicit visible contour fragments produced by clipping
	 *           and collected viewport hits from the same clipping pass
	 * @param clipRect rectangle used by the clipping pass, or {@code null} if unknown
	 */
	public ClippedFragmentsResult(ClippedFragmentsResult result, ClipRect clipRect) {
		this(List.copyOf(result.fragments()), List.copyOf(result.hits()), clipRect);
	}
}

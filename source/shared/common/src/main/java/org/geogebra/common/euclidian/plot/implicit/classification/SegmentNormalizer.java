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
import java.util.Collection;
import java.util.List;

import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.util.debug.Log;

/**
 * Normalizes contour segment split parameters before graph emission.
 */
public class SegmentNormalizer {
	private final List<ContourSegment> invalidSegments = new ArrayList<>();
	private final EpsilonPolicy epsilonPolicy;

	public SegmentNormalizer(EpsilonPolicy epsilonPolicy) {
		this.epsilonPolicy = epsilonPolicy;
	}

	/**
	 * Clears invalid-segment diagnostics from the previous normalization pass.
	 */
	public void reset() {
		invalidSegments.clear();
	}

	/**
	 * Normalizes all segment split parameters and records invalid segments.
	 * @param segments segments to normalize in place
	 */
	public void process(Collection<ContourSegment> segments) {
		invalidSegments.clear();
		segments.forEach(segment -> {
			segment.normalizeSortDedup(epsilonPolicy.getIntersection());
			if (!segment.isValid()) {
				invalidSegments.add(segment);
			}
		});

		if (!isSuccessful()) {
			Log.debug("There are invalid segments: " + invalidSegments);
		}
	}

	/**
	 * @return whether the last normalization pass accepted all segments
	 */
	public boolean isSuccessful() {
		return invalidSegments.isEmpty();
	}
}

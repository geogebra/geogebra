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

import java.util.List;

import org.geogebra.common.euclidian.plot.implicit.EdgeHit;
import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;

public class ViewportHitIngestor {
	private final EpsilonPolicy epsilonPolicy;

	public ViewportHitIngestor(EpsilonPolicy epsilonPolicy) {
		this.epsilonPolicy = epsilonPolicy;
	}

	HitIngestionDiagnostics process(ContourSegmentRegistry registry, List<EdgeHit> hits) {
		HitIngestionDiagnostics diagnostics = new HitIngestionDiagnostics();
		for (EdgeHit hit : hits) {
			if (hit.isValidTSegment(epsilonPolicy.getSnap())) {
				diagnostics.accept();
				SegmentKey key = new SegmentKey(hit.getContourId(), hit.segIndex());
				ContourSegment contourSegment = registry.getSegment(key);
				if (contourSegment != null) {
					contourSegment.addIntersectParam(hit.tSegment());
				} else {
					diagnostics.addMissingKey(key);
				}
			} else {
				diagnostics.reject(hit);
			}
		}
		return diagnostics;
	}
}

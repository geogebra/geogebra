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

import org.geogebra.common.euclidian.plot.implicit.EdgeHit;

final class HitIngestionDiagnostics {
	private static final int SAMPLE_LIMIT = 8;

	private int acceptedCount;
	private int rejectedCount;
	private int missingKeyCount;

	private final List<EdgeHit> rejectedSamples = new ArrayList<>();
	private final List<SegmentKey> missingKeySamples = new ArrayList<>();

	void accept() {
		acceptedCount++;
	}

	void reject(EdgeHit hit) {
		rejectedCount++;
		if (rejectedSamples.size() < SAMPLE_LIMIT) {
			rejectedSamples.add(hit);
		}
	}

	void addMissingKey(SegmentKey key) {
		missingKeyCount++;
		if (missingKeySamples.size() < SAMPLE_LIMIT) {
			missingKeySamples.add(key);
		}
	}

	int getAcceptedCount() {
		return acceptedCount;
	}

	int getRejectedCount() {
		return rejectedCount;
	}

	int getMissingKeyCount() {
		return missingKeyCount;
	}

	List<EdgeHit> getRejectedSamples() {
		return Collections.unmodifiableList(rejectedSamples);
	}

	List<SegmentKey> getMissingKeySamples() {
		return Collections.unmodifiableList(missingKeySamples);
	}

	boolean hasFatalIssues() {
		return rejectedCount > 0;
	}

	boolean hasIssues() {
		return rejectedCount > 0 || missingKeyCount > 0;
	}

	@Override
	public String toString() {
		return "HitIngestionDiagnostics{"
				+ "acceptedCount=" + acceptedCount
				+ ", rejectedCount=" + rejectedCount
				+ ", missingKeyCount=" + missingKeyCount
				+ ", rejectedSamples=" + rejectedSamples
				+ ", missingKeySamples=" + missingKeySamples
				+ '}';
	}

	void reset() {
		acceptedCount = 0;
		rejectedCount = 0;
		missingKeyCount = 0;
		rejectedSamples.clear();
		missingKeySamples.clear();
	}
}

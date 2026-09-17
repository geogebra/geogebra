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
import java.util.List;

final class OpenFragmentClosureResult {
	private static final OpenFragmentClosureResult SUCCESS_EMPTY =
			new OpenFragmentClosureResult(Status.SUCCESS, 0, 0, 0, 0, 0, 0, 0, List.of());

	private final Status status;
	private final int openFragments;
	private final int acceptedClosures;
	private final int skippedClosures;
	private final int conflicts;
	private final int missingEndpoints;
	private final int missingChains;
	private final int failedComplementWirings;
	private final List<Failure> failures;

	enum Status {
		SUCCESS,
		INCOMPLETE
	}

	enum FailureReason {
		MISSING_ENDPOINT,
		MISSING_VIEWPORT_ENDPOINT,
		IDENTICAL_ENDPOINTS,
		MISSING_CONTOUR_CHAIN,
		NON_CONTIGUOUS_CONTOUR_CHAIN,
		CONTOUR_CHAIN_ENDPOINT_MISMATCH,
		MISSING_FORWARD_CLOSURE_ARC,
		MISSING_REVERSE_CLOSURE_ARC,
		CONFLICT,
		FAILED_COMPLEMENT_WIRING
	}

	record Failure(
			int fragmentId,
			FailureReason reason,
			int halfEdgeId,
			int ownerFragmentId,
			EdgeKind edgeKind) {}

	enum EdgeKind {
		NONE,
		CONTOUR,
		VIEWPORT,
		OTHER
	}

	private OpenFragmentClosureResult(
			Status status,
			int openFragments,
			int acceptedClosures,
			int skippedClosures,
			int conflicts,
			int missingEndpoints,
			int missingChains,
			int failedComplementWirings,
			List<Failure> failures) {
		this.status = status;
		this.openFragments = openFragments;
		this.acceptedClosures = acceptedClosures;
		this.skippedClosures = skippedClosures;
		this.conflicts = conflicts;
		this.missingEndpoints = missingEndpoints;
		this.missingChains = missingChains;
		this.failedComplementWirings = failedComplementWirings;
		this.failures = List.copyOf(failures);
	}

	static OpenFragmentClosureResult successEmpty() {
		return SUCCESS_EMPTY;
	}

	Status status() {
		return status;
	}

	int openFragments() {
		return openFragments;
	}

	int acceptedClosures() {
		return acceptedClosures;
	}

	int skippedClosures() {
		return skippedClosures;
	}

	int conflicts() {
		return conflicts;
	}

	int missingEndpoints() {
		return missingEndpoints;
	}

	int missingChains() {
		return missingChains;
	}

	int failedComplementWirings() {
		return failedComplementWirings;
	}

	List<Failure> failures() {
		return failures;
	}

	boolean isIncomplete() {
		return status == Status.INCOMPLETE;
	}

	static final class Builder {
		private int openFragments;
		private int acceptedClosures;
		private int conflicts;
		private int missingEndpoints;
		private int missingChains;
		private int failedComplementWirings;
		private final List<Failure> failures = new ArrayList<>();

		void setOpenFragments(int openFragments) {
			this.openFragments = openFragments;
		}

		void acceptClosure() {
			acceptedClosures++;
		}

		void addFailure(int fragmentId, FailureReason reason) {
			addFailure(fragmentId, reason, -1, -1, EdgeKind.NONE);
		}

		void addFailure(
				int fragmentId,
				FailureReason reason,
				int halfEdgeId,
				int ownerFragmentId,
				EdgeKind edgeKind) {
			failures.add(new Failure(fragmentId, reason, halfEdgeId, ownerFragmentId, edgeKind));
			if (reason == FailureReason.CONFLICT) {
				conflicts++;
			} else if (reason == FailureReason.MISSING_ENDPOINT
					|| reason == FailureReason.IDENTICAL_ENDPOINTS) {
				missingEndpoints++;
			} else if (reason == FailureReason.MISSING_CONTOUR_CHAIN) {
				missingChains++;
			} else if (reason == FailureReason.FAILED_COMPLEMENT_WIRING) {
				failedComplementWirings++;
			}
		}

		boolean hasFailures() {
			return !failures.isEmpty();
		}

		OpenFragmentClosureResult build() {
			Status status = failures.isEmpty() ? Status.SUCCESS : Status.INCOMPLETE;
			return new OpenFragmentClosureResult(
					status,
					openFragments,
					acceptedClosures,
					failures.size(),
					conflicts,
					missingEndpoints,
					missingChains,
					failedComplementWirings,
					failures);
		}
	}
}

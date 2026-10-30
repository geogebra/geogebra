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

package org.geogebra.common.cas;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Immutable outcome of one CAS evaluation, retained across string conversion. */
public final class CasEvaluation {

	/** Failure information available before Giac errors are converted to undefined values. */
	public enum Failure {
		NONE,
		GIAC_ERROR,
		UNSUPPORTED_SUM_DERIVATIVE
	}

	private final @Nullable String value;
	private final Failure failure;

	private CasEvaluation(@Nullable String value, Failure failure) {
		this.value = value;
		this.failure = failure;
	}

	/** @param value expression without failure metadata
	 * @return evaluation with no known Giac failure
	 */
	public static CasEvaluation value(@Nullable String value) {
		return new CasEvaluation(value, Failure.NONE);
	}

	/** @param raw unprocessed Giac output, including its error message
	 * @param value converted expression exposed to existing callers
	 * @return evaluation classified using the original output
	 */
	public static CasEvaluation fromGiac(@Nullable String raw, @Nullable String value) {
		Failure failure = Failure.NONE;
		if (raw != null && raw.contains("GIAC_ERROR")) {
			failure = raw.contains("diff of sum with boundaries or mute variable depending"
							+ " on differentiation variable")
					? Failure.UNSUPPORTED_SUM_DERIVATIVE
					: Failure.GIAC_ERROR;
		}
		return new CasEvaluation(value, failure);
	}

	/** @return expression in the syntax of the current evaluation layer */
	public @Nullable String getValue() {
		return value;
	}

	/** @return classified failure, or NONE if no recognized error was reported */
	public @NonNull Failure getFailure() {
		return failure;
	}

	/** @param converted converted expression
	 * @return outcome with the original failure and a converted value
	 */
	public CasEvaluation withValue(@Nullable String converted) {
		return new CasEvaluation(converted, failure);
	}
}

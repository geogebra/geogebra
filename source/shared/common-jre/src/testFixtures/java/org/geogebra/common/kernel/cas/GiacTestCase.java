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

package org.geogebra.common.kernel.cas;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * One test case from giacTests.js.
 */
public final class GiacTestCase {

	private final String command;
	private final List<String> expected;
	private final @Nullable String rounding;

	/**
	 * @param command CAS input
	 * @param expected accepted outputs
	 * @param rounding rounding (number of decimals), null for default
	 */
	public GiacTestCase(
			@NonNull String command, @NonNull List<String> expected, @Nullable String rounding) {
		this.command = command;
		this.expected = expected;
		this.rounding = rounding;
	}

	/**
	 * @return CAS input
	 */
	public @NonNull String getCommand() {
		return command;
	}

	/**
	 * @return accepted outputs
	 */
	public @NonNull List<String> getExpected() {
		return expected;
	}

	/**
	 * @return rounding (number of decimals), null for default
	 */
	public @Nullable String getRounding() {
		return rounding;
	}
}

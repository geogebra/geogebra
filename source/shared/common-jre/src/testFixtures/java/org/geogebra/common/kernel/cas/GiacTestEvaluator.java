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
import java.util.stream.Collectors;

import org.geogebra.common.main.App;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Runs giacTests.js test cases through {@link org.geogebra.common.plugin.GgbAPI#evalCommandCAS},
 * with the same matching rules as the web tests (uitest/cypress/support/giac/setup.js).
 */
public final class GiacTestEvaluator {

	private static final String GGBVECT = "ggbvect\\(((\\((\\([^()]*\\)|[^()])*\\)|[^()])*)\\)";

	private final App app;

	/**
	 * @param app app with CAS enabled
	 */
	public GiacTestEvaluator(@NonNull App app) {
		this.app = app;
	}

	/**
	 * @param command CAS input
	 * @param rounding rounding (number of decimals), null for default
	 * @return normalized CAS output
	 */
	public @NonNull String evaluate(@NonNull String command, @Nullable String rounding) {
		return normalizeActual(
				app.getGgbApi().evalCommandCAS(command, rounding == null ? "2" : rounding));
	}

	/**
	 * Runs all test cases of a category in an empty construction.
	 * @param testCases test cases
	 * @return description of failed test cases, empty if all passed
	 */
	public @NonNull String runCategory(@NonNull List<GiacTestCase> testCases) {
		app.getKernel().clearConstruction(true);
		StringBuilder failures = new StringBuilder();
		for (GiacTestCase testCase : testCases) {
			if (testCase.getExpected().contains("RANDOM")) {
				continue;
			}
			String actual = evaluate(testCase.getCommand(), testCase.getRounding());
			List<String> expected = testCase.getExpected().stream()
					.map(GiacTestEvaluator::normalizeExpected)
					.collect(Collectors.toList());
			if (!expected.contains(actual)) {
				failures
						.append("\n  in: ")
						.append(testCase.getCommand())
						.append("\n exp: ")
						.append(expected.get(0))
						.append("\n out: ")
						.append(actual)
						.append('\n');
			}
		}
		return failures.toString();
	}

	private static String normalizeActual(String actual) {
		return actual
				.replaceAll("c_[0-9]", "c_0")
				.replaceAll("k_[0-9]", "k_0")
				.replaceAll("c_\\{[0-9]+\\}", "c_0")
				.replaceAll("k_\\{[0-9]+\\}", "k_0")
				.replace(" ", "")
				.replaceAll(GGBVECT, "$1")
				// JVM tests run in German to test translation of commands
				.replace("Wenn(", "If(")
				.replace("NteWurzel", "nroot");
	}

	private static String normalizeExpected(String expected) {
		return expected
				.replaceAll("c_[0-9]+", "c_0")
				.replaceAll("n_[0-9]+", "k_0")
				.replace(" ", "")
				.replaceFirst("GEOGEBRAERROR", "")
				.replaceAll(GGBVECT, "$1");
	}
}

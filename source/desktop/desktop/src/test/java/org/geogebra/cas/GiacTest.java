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

package org.geogebra.cas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import org.geogebra.common.kernel.cas.GiacTestEvaluator;
import org.geogebra.common.kernel.cas.GiacTestFile;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Runs the test cases from giacTests.js, one test per category.
 */
class GiacTest {

	private static final Set<String> SKIPPED = skippedCategories();

	private static Set<String> skippedCategories() {
		Set<String> skipped = new HashSet<>();
		// Giac built with clang orders the terms differently
		if (AppD.MAC_OS) {
			skipped.add("EvaluateOrdering");
		}
		// Giac gives a different result on ARM
		if ("aarch64".equals(System.getProperty("os.arch"))) {
			skipped.add("IntegralNoArm");
		}
		return skipped;
	}

	@TestFactory
	Stream<DynamicTest> categories() {
		AppDNoGui app = new AppDNoGui(new LocalizationD(3), false);
		// test translation of commands
		app.setLanguage(Locale.GERMANY);
		app.getSettings().getCasSettings().setTimeoutMilliseconds(13000);
		GiacTestEvaluator evaluator = new GiacTestEvaluator(app);
		return GiacTestFile.load().entrySet().stream()
				.map(category -> DynamicTest.dynamicTest(category.getKey(), () -> {
					assumeFalse(SKIPPED.contains(category.getKey()), "platform bug");
					assertEquals("", evaluator.runCategory(category.getValue()));
				}));
	}
}

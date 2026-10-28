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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geogebra.common.cas.giac.Ggb2giac;
import org.junit.jupiter.api.Test;

class GiacTestCoverageTest {

	private static final Set<String> NOT_TESTED = Set.of(
			"Binomial.2",
			"ExpSimplify.1",
			"SolveODEPoint.2",
			"SolveQuartic.1",
			// not supported in CAS
			"Cell.2",
			"CellRange.2",
			"Column.1",
			"CopyFreeObject.1",
			"Object.1",
			"Row.1",
			"Segment.2");

	@Test
	void everyCasCommandShouldHaveTests() {
		Map<String, List<GiacTestCase>> categories = GiacTestFile.load();
		List<String> missing = Ggb2giac.getMap().keySet().stream()
				.filter(key -> !NOT_TESTED.contains(key)
						&& !categories.containsKey(key)
						&& !categories.containsKey(key.substring(0, key.indexOf('.'))))
				.sorted()
				.collect(Collectors.toList());
		assertEquals(List.of(), missing);
	}
}

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

package org.geogebra.common.euclidian.tools;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.geogebra.common.kernel.geos.GeoLine;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Regression checks for geometric assertions shared by transformation tool tests. */
class TransformationAssertionsTest extends BaseTransformationToolTest {

	@ParameterizedTest
	@ValueSource(doubles = {1, 1E200, 1.3E308})
	void lineIncidenceShouldRemainCorrectWhenCoefficientNormWouldOverflow(double scale) {
		GeoLine line = add("g: x + y = 0");
		line.setCoords(scale, scale, 0);

		assertLineThrough(line, 1, -1);
		assertThrows(AssertionError.class, () -> assertLineThrough(line, 1, 0));
	}
}

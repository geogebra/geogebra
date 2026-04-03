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

package org.geogebra.common.kernel.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AlgoClosestPointTest extends BaseAppTestSetup {
	@BeforeEach
	void setup() {
		setupClassicApp();
	}

	@ParameterizedTest
	@Issue("APPS-7382")
	@CsvSource(value = {"(0,2);(2.23607, 1)",
			"(0,-2);(2.23607, -1)",
			"(-1E-13,2);(-2.23607, 1)"}, delimiter = ';')
	public void testHyperbola(String source, String expected) {
		GeoElement closest = evaluateGeoElement("ClosestPoint(" + source + ",xx-yy=4)");
		assertEquals(expected,
				closest.toValueString(StringTemplate.editTemplate));
	}

}

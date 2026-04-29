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

package org.geogebra.common.euclidian.plot.interval;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FunctionSamplerTopologyTest extends BaseFunctionSamplerSetup {

	@ParameterizedTest
	@CsvSource({
			"1/x",
			"1 - exp(-5x)",
			"0/sin(x)",
			"((1*10^(-13) x)/(1*10^(-13)))",
			"((1*10^(-13))/(1*10^(-13)))x"
	})
	void testNoEmptyGapsInData(String definition) {
		withDefaultScreen();
		QueryFunctionData data = query(definition);
		assertAll(
				() -> assertTrue(data.hasValidData(), "Data is empty"),
				() -> assertEquals(0, data.stream().filter(
						tuple -> tuple.ySet().isEmpty()).count())
		);
	}

	@Test
	void testAllDataIsWholeAtHighZoom() {
		withHighZoomScreen();
		QueryFunctionData data = query("tan(x)");
		assertAll(
				() -> assertTrue(data.hasValidData(), "Data is empty"),
				() -> assertEquals(0, data.stream().filter(tuple
						-> !tuple.ySet().isWhole()).count())
		);
	}

}

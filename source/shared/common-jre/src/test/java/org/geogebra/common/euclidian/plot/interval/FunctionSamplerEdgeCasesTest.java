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

import static org.geogebra.common.kernel.interval.IntervalSetOps.connectedInterval;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalSet;
import org.geogebra.common.util.DoubleUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class FunctionSamplerEdgeCasesTest extends BaseFunctionSamplerSetup {

	@ParameterizedTest
	@CsvSource({
			"0(1/x), 0",
			"(1/ln(x)) * 0, 0",
			"sin(0)^x, 0"})
	void testNonEmptySamplesStayAtConstantValue(String definition, double value) {
		withDefaultScreen();
		QueryFunctionData data = query(definition);
		long nonEmptyCount = data.stream().filter(tuple -> !tuple.ySet().isEmpty()).count();
		assertAll(
				() -> assertNotEquals(0, data.getCount()),
				() -> assertTrue(nonEmptyCount > 1, "Expected substantial non-empty sampled data"),
				() -> assertEquals(0, data.stream()
						.filter(tuple -> !tuple.ySet().isEmpty() && !tuple.ySet().isConnected())
						.count()),
				() -> Assertions.assertEquals(0, data
						.stream().filter(tuple ->
								// ignore empty sets
								!tuple.ySet().isEmpty()
										&& !hasConstantValue(tuple.ySet(), value))
						.count())
		);
	}

	private boolean hasConstantValue(IntervalSet ySet, double value) {
		if (!ySet.isConnected()) {
			return false;
		}
		Interval y = connectedInterval(ySet);
		return y.isSingleton() && DoubleUtil.isEqual(value, y.getLow(), Kernel.MAX_PRECISION);
	}

	@ParameterizedTest
	@CsvSource({
			"1/(0x)"
	})
	void testFunctionProducesNoData(String definition) {
		withDefaultScreen();
		QueryFunctionData data = query(definition);
		assertAll(
				() -> assertFalse(data.hasValidData(), "Data is not empty"),
				() -> assertEquals(0, data.stream().filter(
						tuple -> !tuple.ySet().isEmpty()).count())
		);
	}

}

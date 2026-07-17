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

package org.geogebra.common.kernel.interval.samplers;

import static org.geogebra.common.kernel.interval.IntervalSetOps.isZero;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Predicate;

import org.geogebra.common.kernel.interval.SamplerTest;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class InverseSamplerTest extends SamplerTest {

	@Test
	void zeroXInverseShouldBeInfiniteOnly() {
		assertAll("1/(0x)", IntervalTuple::isEmpty);
	}

	private void assertAll(String description, Predicate<? super IntervalTuple> predicate) {
		IntervalTupleList samples = functionValues(description, -4, 4, -5, -5);
		assertEquals(samples.count(), samples.stream().filter(predicate).count());
	}

	@Test
	void inverseOfzeroXInverse() {
		assertAll("1/(1/(0x))", IntervalTuple::isEmpty);
	}

	@Disabled("whole or empty now. Test is on wrong layer")
	@Test
	void zeroDividedByTanSecXShouldBeZero() {
		assertAll("0/(tan(sec(x)))", t -> isZero(t.ySet()));
	}
}

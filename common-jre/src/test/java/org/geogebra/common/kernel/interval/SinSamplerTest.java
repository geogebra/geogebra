package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SinSamplerTest extends SamplerTest {

	@Test
	public void sinOfSinInverse() {
		TuplesQuery query = new TuplesQuery(
				functionValuesWithSampleCount("1/sin(1/x)",
						-5.05865, 9.66763,
						-3.03268, 7.93424, 562));
		assertTrue(query.emptyTuples().isEmpty());
	}
}

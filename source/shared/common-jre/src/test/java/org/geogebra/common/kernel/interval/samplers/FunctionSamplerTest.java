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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.euclidian.EuclidianViewBoundsImp;
import org.geogebra.common.euclidian.plot.interval.PlotterUtils;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;
import org.junit.Test;

public class FunctionSamplerTest extends BaseUnitTest {

	@Test
	public void testExtend() {
		GeoFunction function = add("sin(x)");
		IntervalTuple range = PlotterUtils.newRange(-100, -99, 1, 1);
		FunctionSampler sampler = PlotterUtils.newSampler(function, range,
				5, new EuclidianViewBoundsImp(getApp().getActiveEuclidianView()));
		assertEquals(6, sampler.tuples().count());

		sampler.extend(new Interval(1000, 1005));
		assertEquals(Arrays.asList(999, 1000, 1001, 1002, 1003, 1004),
				getX(sampler.tuples()));

		sampler.extend(new Interval(1002, 1007));
		assertEquals(Arrays.asList(999, 1000, 1001, 1002, 1003, 1004, 1005, 1006),
				getX(sampler.tuples()));

		sampler.extend(new Interval(1001, 1016));
		assertEquals(Arrays.asList(999, 1002, 1005, 1008, 1011, 1014),
				getX(sampler.tuples()));

	}

	private List<Integer> getX(IntervalTupleList tuples) {
		return tuples.stream().map(t -> (int) (t.x().getLow())).collect(Collectors.toList());
	}
}

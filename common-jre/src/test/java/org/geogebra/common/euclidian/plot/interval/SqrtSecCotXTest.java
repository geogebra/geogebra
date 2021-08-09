package org.geogebra.common.euclidian.plot.interval;

import org.junit.Before;
import org.junit.Test;

public class SqrtSecCotXTest {
	private final IntervalPlotterCommon common = new IntervalPlotterCommon();

	@Before
	public void setUp() throws Exception {
		common.setup();
	}

	@Test
	public void plotTest() {
		common.withBounds(-8.5, 8.5, -11, 7);
		common.withScreenSize(393, 412);
		common.withFunction("sqrt(sec(cot(x)))");
		common.valuesShouldBeBetween(0.5, 8);
		}
}

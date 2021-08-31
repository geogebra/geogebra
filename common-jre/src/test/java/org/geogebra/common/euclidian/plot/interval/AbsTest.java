package org.geogebra.common.euclidian.plot.interval;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AbsTest extends IntervalPlotterCommon {

	@Test
	public void testAbsXInverse() {
		withBounds(-1, 1, -8, 8);
		withScreenSize(100, 100);
		withFunction("abs(1/x)");
	}
}

package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.LINE_TO;

import org.junit.Test;

public class LnTest extends IntervalPlotterCommon {

	@Test
	public void testInverseOfLnTanX() {
		withBounds(1, 3.2, -8, 8);
		withScreenSize(100, 100);
		withFunction("1/(ln(tan(x)))");
		logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		}

	@Test
	public void testLnOfXInverse() {
		withBounds(-1, 1, -8, 8);
		withScreenSize(100, 100);
		withFunction("ln(1/x)");
		logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		}
}

package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.LINE_TO;

import org.junit.Test;

public class AbsTest extends IntervalPlotterCommon {

	@Test
	public void testAbsXInverse() {
		withBounds(-1, 1, -8, 8);
		withScreenSize(100, 100);
		withFunction("abs(1/x)");
		logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		}
}

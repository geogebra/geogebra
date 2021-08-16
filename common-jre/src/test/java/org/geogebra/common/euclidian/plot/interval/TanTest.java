package org.geogebra.common.euclidian.plot.interval;

import static java.lang.Math.PI;
import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.LINE_TO;

import org.junit.Test;

public class TanTest extends IntervalPlotterCommon {

	@Test
	public void testMinusTanX() {
		withBounds(-PI/2 -1E-2, PI + 1E-2, -6, 6);
		withScreenSize(100, 100);
		withFunction("-tan(x)");
		logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		}
}

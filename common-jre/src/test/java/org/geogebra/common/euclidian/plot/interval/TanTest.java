package org.geogebra.common.euclidian.plot.interval;

import static java.lang.Math.PI;
import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.LINE_TO;
import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.MOVE_TO;

import org.junit.Test;

public class TanTest extends IntervalPlotterCommon {

	public static final String TAN_X = "tan(x)";

	@Test
	public void testTanX() {
		withBounds(-PI/2 -1E-2, PI + 1E-2, -6, 6);
		withScreenSize((int) (2 * PI + 2E-2), 12);
		withFunction(TAN_X);
		logShouldBeAt(1, MOVE_TO, -1.486148547187203, -26.78004903932306);logShouldBeAt(2, LINE_TO, -1.486148547187203, -11.785429545911258);
		logShouldBeAt(3, LINE_TO, -1.486148547187203, -11.785429545911262);
		logShouldBeAt(194, LINE_TO, 3.1042687637859485, -0.0373412310982536);
		logShouldBeAt(195, LINE_TO, 3.1042687637859485, -0.037341231098253616);
		logShouldBeAt(196, LINE_TO, 3.1515926535897956, 0.01000033334666966);
	}

	@Test
	public void testHiResTanX() {
		super.withHiResFunction(TAN_X);
		logShouldBeAt(1, MOVE_TO, -4994.791666666667, -1.0E-4);
		logShouldBeAt(2, LINE_TO, -4994.791666666667, 1.0E-4 );
		logShouldBeAt(3, MOVE_TO, -4994.791666666667, 0.0);
		logShouldBeAt(4, LINE_TO, -4989.583333333334, -1.0E-4);
		logShouldBeAt(5, MOVE_TO, -4994.791666666667, 1280.0);
	}
}

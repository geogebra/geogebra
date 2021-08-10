package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.LINE_TO;
import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.MOVE_TO;

import org.junit.Test;

public class XSquaredInverseTest extends IntervalPlotterCommon {

	@Test
	public void plotTest() {
		withBounds(-8.5, 8.5, -11, 7);
		withScreenSize(393, 412);
		withFunction("1/(-15+x^2)");
		logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		logShouldBeAt(214, MOVE_TO, -3.8931297709923784, 412.0);
		logShouldBeAt(215, LINE_TO, -3.871501272264643,
				-2.9023719927283493);
		logShouldBeAt(573, LINE_TO, 3.8931297709923394, 412.0);
		logShouldBeAt(574, MOVE_TO, 3.8931297709923394, 0);
		logShouldBeAt(575, LINE_TO, 3.9147582697200747, 0.0);
		logShouldBeAt(576, LINE_TO, 3.9147582697200747,
				1.5017611411324263);
		}

	@Test
	public void tanOfXInverse() {
		withBounds(1, 2, -10, 10);
		withScreenSize(100, 100);
		withFunction("sqrt(sec(cot(x)))");
		logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		}
}

package org.geogebra.common.euclidian.plot.interval;

import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.LINE_TO;
import static org.geogebra.common.euclidian.plot.interval.IntervalPathMockEntry.PathOperation.MOVE_TO;

import org.junit.Before;
import org.junit.Test;

public class XSquaredInverseTest {
	private final IntervalPlotterCommon common = new IntervalPlotterCommon();

	@Before
	public void setUp() throws Exception {
		common.setup();
	}

	@Test
	public void plotTest() {
		common.withBounds(-8.5, 8.5, -11, 7);
		common.withScreenSize(393, 412);
		common.withFunction("1/(-15+x^2)");
		common.logShouldBeAt(213, LINE_TO, -3.8931297709923784, 0.0);
		common.logShouldBeAt(214, MOVE_TO, -3.8931297709923784, 412.0);
		common.logShouldBeAt(215, LINE_TO, -3.871501272264643,
				-2.9023719927283493);
		common.logShouldBeAt(573, LINE_TO, 3.8931297709923394, 412.0);
		common.logShouldBeAt(574, MOVE_TO, 3.8931297709923394, 0);
		common.logShouldBeAt(575, LINE_TO, 3.9147582697200747, 0.0);
		common.logShouldBeAt(576, LINE_TO, 3.9147582697200747,
				1.5017611411324263);
		}
}

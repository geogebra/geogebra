package org.geogebra.common.euclidian.plot.interval;


import static org.geogebra.common.euclidian.plot.interval.PlotterUtils.newRange;
import static org.geogebra.common.euclidian.plot.interval.PlotterUtils.newSampler;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.samplers.FunctionSampler;
import org.junit.Test;

public class IntervalPlotModelTest extends BaseUnitTest {

	private static IntervalPlotModel model;

	@Test
	public void updateDomainTestMoveLeft() {
		IntervalTuple range = newRange(-50, 50, -1, 20);
		withModel(range);
		model.updateDomain(new Interval(-55, 45), range.x());
		assertEquals(Arrays.asList(interval(-55, -54), interval(44, 45)),
				Arrays.asList(model.at(0).x(), model.at(model.getCount() - 1).x()));
	}

	@Test
	public void updateDomainTestMoveLeftFraction() {
		IntervalTuple range = newRange(-50, 50, -1, 20);
		withModel(range);
		model.updateDomain(new Interval(-54.2, 44.2), range.x());
		assertEquals(Arrays.asList(interval(-55, -54), interval(44, 45)),
				Arrays.asList(model.at(0).x(), model.at(model.getCount() - 1).x()));
	}

	@Test
	public void updateDomainTestMoveRight() {
		IntervalTuple range = newRange(-50, 50, -1, 20);
		withModel(range);
		model.updateDomain(new Interval(-55, 55), range.x());
		assertEquals(Arrays.asList(interval(-55, -54), interval(54, 55)),
				Arrays.asList(model.at(0).x(), model.at(model.getCount() - 1).x()));
	}

	private void withModel(IntervalTuple range) {
		FunctionSampler sampler = newSampler(add("x^2"),
				range,
				100);
		EuclidianViewBoundsMock bounds = new EuclidianViewBoundsMock(range, 100, 100);
		model =
				new IntervalPlotModel(range, sampler,
						bounds);
		IntervalPathPlotterMock plotter = new IntervalPathPlotterMock();
		IntervalPath gp = new IntervalPath(plotter, bounds, model);
		model.setPath(gp);
		model.updateAll();
	}
}
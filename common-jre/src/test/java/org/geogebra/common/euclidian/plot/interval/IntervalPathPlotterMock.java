package org.geogebra.common.euclidian.plot.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.StringUtil;

public class IntervalPathPlotterMock implements IntervalPathPlotter {
	private List<String> log = new ArrayList<>();

	@Override
	public void reset() {
		log.clear();
		log.add("R");
	}

	@Override
	public void moveTo(double x, double y) {
		log.add("M " + x + " " + y);
	}

	@Override
	public void lineTo(double x, double y) {
		log.add("L " + x + " " + y);

	}

	@Override
	public void draw(GGraphics2D g2) {
		// stub.
	}

	public String getLog() {
		return StringUtil.join(",", log);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalPathPlotterMock) {
			return getLog().equals(((IntervalPathPlotterMock) obj).getLog());
		}
		return super.equals(obj);
	}
}


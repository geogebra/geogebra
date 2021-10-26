package org.geogebra.common.euclidian.plot.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.util.DoubleUtil;

public class IntervalPathPlotterMock implements IntervalPathPlotter {
	private final List<IntervalPathMockEntry> log = new ArrayList<>();
	private final EuclidianViewBounds bounds;

	public IntervalPathPlotterMock(EuclidianViewBounds bounds) {
		this.bounds = bounds;
	}

	@Override
	public void reset() {
		log.clear();
		log.add(new IntervalPathMockEntry());
	}

	@Override
	public void moveTo(double x, double y) {
		log.add(
				new IntervalPathMockEntry(IntervalPathMockEntry.PathOperation.MOVE_TO,
						rwX(x),
						rwY(y)));
	}

	private double rwX(double x) {
		return bounds == null ? x : bounds.toRealWorldCoordX(x);
	}

	private double rwY(double y) {
		double realWorldY = bounds.toRealWorldCoordY(y);
		if (DoubleUtil.isEqual(realWorldY, bounds.getYmin())) {
			return bounds.getXmin();
		} else if (DoubleUtil.isEqual(realWorldY, bounds.getYmax())) {
			return bounds.getYmax();
		}

		return realWorldY;
	}

	@Override
	public void lineTo(double x, double y) {
		log.add(new IntervalPathMockEntry(
				IntervalPathMockEntry.PathOperation.LINE_TO,
				rwX(x),
				rwY(y)));

	}

	@Override
	public void draw(GGraphics2D g2) {
		// stub.
	}

	public List<IntervalPathMockEntry> getLog() {
		return log;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalPathPlotterMock) {
			return getLog().equals(((IntervalPathPlotterMock) obj).getLog());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getLog().hashCode();
	}
}


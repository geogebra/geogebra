/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.plot.interval;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.util.DoubleUtil;

public class IntervalPathPlotterMock implements IntervalPathPlotter {
	private final List<IntervalPathMockEntry> log = new ArrayList<>();
	private EuclidianViewBounds bounds;

	public IntervalPathPlotterMock(EuclidianViewBounds bounds) {
		this.bounds = bounds;
	}

	public IntervalPathPlotterMock() {
		//
	}

	/**
	 *
	 * @param bounds to set
	 */
	public void setBounds(EuclidianViewBounds bounds) {
		this.bounds = bounds;
		log.clear();
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
		if (bounds == null) {
			return 0;
		}
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
	public void segment(double x1, double y1, double x2, double y2) {
		moveTo(x1, y1);
		lineTo(x2, y2);
	}

	@Override
	public void segment(EuclidianViewBounds bounds, double x1, double y1, double x2, double y2) {
		segment(bounds.toScreenCoordXd(x1),
				bounds.toScreenCoordYd(y1),
				bounds.toScreenCoordXd(x2),
				bounds.toScreenCoordYd(y2));
	}

	@Override
	public void draw(GGraphics2D g2) {
		// stub.
	}

	@Override
	public void leftToTop(EuclidianViewBounds bounds, Interval x, Interval y) {
		// stub.
	}

	@Override
	public void leftToBottom(EuclidianViewBounds bounds, Interval x, Interval y) {
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


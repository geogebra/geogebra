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

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

public class EuclidianViewBoundsMock implements EuclidianViewBounds {
	private final double xmin;
	private final double xmax;
	private final double ymin;
	private final double ymax;
	private int width;
	private int height;

	/**
	 * Constructor with bound values.
	 *
	 * @param xmin minimum x coordinate
	 * @param xmax maximum x coordinate
	 * @param ymin minimum y coordinate
	 * @param ymax maximum y coordinate
	 */
	public EuclidianViewBoundsMock(double xmin, double xmax, double ymin, double ymax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		width = (int) Math.round(xmax - xmin);
		height = (int) Math.round(ymax - ymin);
	}

	/**
	 * @param range of the view
	 * @param width in pixels
	 * @param height in pixels
	 */
	public EuclidianViewBoundsMock(IntervalTuple range, int width, int height) {
		this(range.x().getLow(), range.x().getHigh(), range.y().getLow(), range.y().getHigh());
		this.width = width;
		this.height = height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Interval domain() {
		return new Interval(xmin, xmax);
	}

	@Override
	public Interval range() {
		return new Interval(ymin, ymax);
	}

	@Override
	public double getXmin() {
		return xmin;
	}

	@Override
	public double getXmax() {
		return xmax;
	}

	@Override
	public double getYmin() {
		return ymin;
	}

	@Override
	public double getYmax() {
		return ymax;
	}

	@Override
	public Interval toScreenIntervalX(Interval x) {
		return x;
	}

	/**
	 * Sets mocked screen size
	 * @param width in pixels.
	 * @param height in pixels.
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public Interval toScreenIntervalY(Interval y) {
		return new Interval(toScreenCoordXd(y.getLow()),
				toScreenCoordYd(y.getHigh()));
	}

	@Override
	public boolean isOnView(double x, double y) {
		return x >= 0 && x <= width && y >= 0 && y <= height;
	}

	@Override
	public double toScreenCoordXd(double x) {
		return x;
	}

	@Override
	public double toScreenCoordYd(double y) {
		return y;
	}

	@Override
	public double toRealWorldCoordX(double x) {
		return x;
	}

	@Override
	public double toRealWorldCoordY(double y) {
		return y;
	}

	@Override
	public boolean isOnView(Interval y) {
		return (y.getLow() >= getYmin() && y.getLow() <= getXmax())
				|| (y.getHigh() >= getYmin() && y.getHigh() <= getXmax());
	}

	@Override
	public double getInvYscale() {
		return 1;
	}

	@Override
	public String toString() {
		return "EuclidianViewBoundsMock{"
				+ "xmin=" + xmin
				+ ", xmax=" + xmax
				+ ", ymin=" + ymin
				+ ", ymax=" + ymax
				+ ", width=" + width
				+ ", height=" + height
				+ '}';
	}
}

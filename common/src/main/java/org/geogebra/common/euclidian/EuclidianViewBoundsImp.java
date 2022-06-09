package org.geogebra.common.euclidian;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.IntervalConstants;

public class EuclidianViewBoundsImp implements EuclidianViewBounds {
	private final EuclidianView view;

	public EuclidianViewBoundsImp(EuclidianView view) {
		this.view = view;
	}

	@Override
	public int getWidth() {
		return view.getWidth();
	}

	@Override
	public int getHeight() {
		return view.getHeight();
	}

	@Override
	public Interval domain() {
		return new Interval(getXmin(), getXmax());
	}

	@Override
	public Interval range() {
		return new Interval(getYmin(), getYmax());
	}

	@Override
	public double getXmin() {
		return view.getXmin();
	}

	@Override
	public double getXmax() {
		return view.getXmax();
	}

	@Override
	public double getYmin() {
		return view.getYmin();
	}

	@Override
	public double getYmax() {
		return view.getYmax();
	}

	@Override
	public Interval toScreenIntervalX(Interval x) {
		return new Interval(toScreenCoordXd(x.getLow()),
				toScreenCoordXd(x.getHigh()));
	}

	@Override
	public Interval toScreenIntervalY(Interval y) {
		if (y.isWhole()) {
			return y;
		}

		if (y.isNegativeInfinity()) {
			return new Interval(toScreenCoordYd(view.getYmin()));
		}

		if (y.isPositiveInfinity()) {
			return IntervalConstants.zero();
		}

		double screenYLow = y.getHigh() == Double.POSITIVE_INFINITY
				? 0
				: toScreenCoordYd(y.getHigh());
		double screenYHigh = y.getLow() == Double.NEGATIVE_INFINITY
				? getHeight()
				: toScreenCoordYd(y.getLow());
		return new Interval(screenYLow, screenYHigh);
	}

	@Override
	public boolean isOnView(double x, double y) {
		EdgeInsets safeAreaInsets = view.getSafeAreaInsets();
		double safeAreaLeft = toRealWorldCoordX(safeAreaInsets.getLeft());
		double safeAreaRight = toRealWorldCoordX(getWidth() - safeAreaInsets.getRight());
		double safeAreaTop = toRealWorldCoordY(safeAreaInsets.getTop());
		double safeAreaBottom = toRealWorldCoordY(getHeight() - safeAreaInsets.getBottom());
		return (x >= safeAreaLeft) && (x <= safeAreaRight)
				&& (y >= safeAreaBottom) && (y <= safeAreaTop);
	}

	@Override
	public double toScreenCoordXd(double x) {
		return view.toScreenCoordXd(x);
	}

	@Override
	public double toScreenCoordYd(double y) {
		return view.toScreenCoordYd(y);
	}

	@Override
	public double toRealWorldCoordX(double x) {
		return view.toRealWorldCoordX(x);
	}

	@Override
	public double toRealWorldCoordY(double y) {
		return view.toRealWorldCoordY(y);
	}

	@Override
	public boolean isOnView(Interval y) {
		return (y.getLow() >= getYmin() && y.getLow() <= getXmax())
			|| (y.getHigh() >= getYmin() && y.getHigh() <= getXmax());
	}

	@Override
	public String toString() {
		return "EuclidianViewBoundsImp{"
				+ "xmin=" + getXmin()
				+ ", xmax=" + getXmax()
				+ ", ymin=" + getYmin()
				+ ", ymax=" + getYmax()
				+ ", width=" + getWidth()
				+ ", height=" + getHeight()
				+ '}';
	}
}

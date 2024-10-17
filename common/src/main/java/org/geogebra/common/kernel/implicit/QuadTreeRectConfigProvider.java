package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public class QuadTreeRectConfigProvider extends PlotRectConfigProvider {
	private final GeoImplicitCurve curve;
	private final int factor;

	public QuadTreeRectConfigProvider(GeoImplicitCurve curve, int factor) {
		this.curve = curve;
		this.factor = factor;
	}

	@Override
	protected PlotRectConfig checkContinouty(PlotRectConfig config, PlotRect plotRect, MyPoint[] points) {
		QuadTreeEdgeConfig quadTreeEdgeConfig = (QuadTreeEdgeConfig) config;
		return limitOf(points[0]) <= quadTreeEdgeConfig.getQ1(plotRect)
				&& limitOf(points[1]) <= quadTreeEdgeConfig.getQ2(plotRect)
				? QuadTreeEdgeConfig.VALID
				: QuadTreeEdgeConfig.EMPTY;
	}

	private double limitOf(MyPoint point) {
		return Math.abs(curve.evaluateImplicitCurve(point.x, point.y, factor));
	}

	@Override
	protected PlotRectConfig getConfigFromPlotRect(PlotRect r) {
		return QuadTreeEdgeConfig.fromFlag(config(r));
	}

	@Override
	protected PlotRectConfig empty() {
		return QuadTreeEdgeConfig.EMPTY;
	}

	private int config(PlotRect r) {
		int config = 0;
		for (int i = 0; i < 4; i++) {
			config = (config << 1) | sign(r.cornerAt(i));
		}
		return config >= 8 ? (~config) & 0xf : config;
	}

	/**
	 *
	 * @param val
	 *            value to check
	 * @return the sign depending on the value. if value is infinity or NaN it
	 *         returns T_INV, otherwise it returns 1 for +ve value 0 otherwise
	 */
	private int sign(double val) {
		if (Double.isInfinite(val) || Double.isNaN(val)) {
			return QuadTreeEdgeConfig.T_INV.flag();
		} else if (val > 0.0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public int listThreshold() {
		return 48;
	}

	@Override
	public boolean canChangePointOrder() {
		return true;
	}

	@Override
	protected boolean isConfigFinal(PlotRectConfig config) {
		return config == QuadTreeEdgeConfig.T0101 || config.isInvalid();
	}
}

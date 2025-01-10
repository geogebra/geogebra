package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public class QuadTreeRectConfigProvider extends MarchingConfigProvider {
	private final GeoImplicitCurve curve;
	private final int factor;

	/**
	 *
	 * @param curve {@link GeoImplicitCurve}
	 * @param factor of the curve.
	 */
	public QuadTreeRectConfigProvider(GeoImplicitCurve curve, int factor) {
		this.curve = curve;
		this.factor = factor;
	}

	@Override
	protected MarchingConfig checkContinouty(MarchingConfig config, MarchingRect marchingRect,
			MyPoint[] points) {
		QuadTreeEdgeConfig quadTreeEdgeConfig = (QuadTreeEdgeConfig) config;
		return limitOf(points[0]) <= quadTreeEdgeConfig.getQ1(marchingRect)
				&& limitOf(points[1]) <= quadTreeEdgeConfig.getQ2(marchingRect)
				? QuadTreeEdgeConfig.VALID
				: QuadTreeEdgeConfig.EMPTY;
	}

	private double limitOf(MyPoint point) {
		return Math.abs(curve.evaluateImplicitCurve(point.x, point.y, factor));
	}

	@Override
	protected MarchingConfig getConfigFrom(MarchingRect r) {
		return QuadTreeEdgeConfig.fromFlag(configure(r));
	}

	@Override
	protected MarchingConfig empty() {
		return QuadTreeEdgeConfig.EMPTY;
	}

	@Override
	protected int configure(MarchingRect r) {
		int config = super.configure(r);
		return config >= 8 ? (~config) & 0xf : config;
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
	protected boolean isConfigFinal(MarchingConfig config) {
		return config == QuadTreeEdgeConfig.T0101 || config.isInvalid();
	}
}

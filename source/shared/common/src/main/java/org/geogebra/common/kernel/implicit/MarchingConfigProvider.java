package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

public abstract class MarchingConfigProvider {
	private MyPoint[] pts;

	/**
	 * @param rect {@link MarchingRect}
	 * @return config from the marching rectangle.
	 */
	public MarchingConfig create(MarchingRect rect) {
		MarchingConfig config = getConfigFrom(rect);
		if (isConfigFinal(config)) {
			return config;
		}

		pts = config.getPoints(rect);
		if (pts == null) {
			return empty();
		}

		return checkContinouty(config, rect, pts);
	}

	protected abstract boolean isConfigFinal(MarchingConfig gridType);

	protected abstract MarchingConfig checkContinouty(MarchingConfig config,
			MarchingRect marchingRect, MyPoint[] points);

	protected abstract MarchingConfig getConfigFrom(MarchingRect r);

	MyPoint[] getPoints() {
		return pts;
	}

	protected abstract MarchingConfig empty();

	public abstract int listThreshold();

	public abstract boolean canChangePointOrder();

	protected int configure(MarchingRect r) {
		int config = 0;

		for (int i = 0; i < 4; i++) {
			config = (config << 1) | sign(r.cornerAt(i));
		}

		return config;
	}

	private static int sign(double val) {
		if (Double.isInfinite(val) || Double.isNaN(val)) {
			return -1;
		} else if (val > 0.0) {
			return 1;
		} else {
			return 0;
		}
	}
}

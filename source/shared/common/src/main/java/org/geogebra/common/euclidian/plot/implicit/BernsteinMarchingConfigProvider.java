package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.MarchingConfig;
import org.geogebra.common.kernel.implicit.MarchingConfigProvider;
import org.geogebra.common.kernel.implicit.MarchingRect;

public class BernsteinMarchingConfigProvider extends MarchingConfigProvider {
	private final MarchingRect marchingRect;

	/**
	 * @param cell {@link BernsteinPlotCell}
	 */
	public BernsteinMarchingConfigProvider(BernsteinPlotCell cell) {
		marchingRect = new BernsteinMarchingRect(cell);
		MarchingConfig config = BernsteinMarchingConfig.fromFlag(configure(marchingRect));
		cell.setMarchingConfig(config);
	}

	@Override
	public BernsteinMarchingConfig getConfigFrom(MarchingRect r) {
		return BernsteinMarchingConfig.fromFlag(configure(r));
	}

	@Override
	protected MarchingConfig empty() {
		return BernsteinMarchingConfig.EMPTY;
	}

	@Override
	public int listThreshold() {
		return 1;
	}

	@Override
	public boolean canChangePointOrder() {
		return false;
	}

	@Override
	protected boolean isConfigFinal(MarchingConfig config) {
		return config.isInvalid();
	}

	@Override
	protected MarchingConfig checkContinouty(MarchingConfig config, MarchingRect marchingRect,
			MyPoint[] points) {
		return BernsteinMarchingConfig.VALID;
	}

	public MarchingRect getMarchingRect() {
		return marchingRect;
	}
}

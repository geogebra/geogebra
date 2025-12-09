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
	protected MarchingConfig checkContinuity(MarchingConfig config, MarchingRect marchingRect,
			MyPoint[] points) {
		return BernsteinMarchingConfig.VALID;
	}

	public MarchingRect getMarchingRect() {
		return marchingRect;
	}
}

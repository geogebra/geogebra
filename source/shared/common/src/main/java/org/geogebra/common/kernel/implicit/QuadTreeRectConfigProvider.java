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
	protected MarchingConfig checkContinuity(MarchingConfig config, MarchingRect marchingRect,
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
		return config >= 8 ? ~config & 0xf : config;
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

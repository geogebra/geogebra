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

		return checkContinuity(config, rect, pts);
	}

	protected abstract boolean isConfigFinal(MarchingConfig gridType);

	protected abstract MarchingConfig checkContinuity(MarchingConfig config,
			MarchingRect marchingRect, MyPoint[] points);

	protected abstract MarchingConfig getConfigFrom(MarchingRect r);

	MyPoint[] getPoints() {
		return pts;
	}

	protected abstract MarchingConfig empty();

	/**
	 * @return maximal number of segment lists that can be simultaneously open
	 */
	public abstract int listThreshold();

	/**
	 * @return whether order can change when new points are added
	 */
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

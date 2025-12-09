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
import org.geogebra.common.kernel.interval.IntervalConstants;

/**
 *  Optional settings for debug purposes
 *
 */

public final class IntervalPlotSettings {

	/**
	 *
	 * @return if model should check its x range and re-evaluate them.
	 * Set it false to debug line joins.
	 */
	static boolean isUpdateOnMoveEnabled() {
		return true;
	}

	/**
	 *
	 * @return if model should check its x range and re-evaluate them.
	 * Set it false to debug line joins.
	 */
	static boolean isUpdateOnMoveStopEnabled() {
		return true;
	}

	/**
	 *
	 * @return if model should check its x range and re-evaluate them.
	 * Set it false to debug line joins.
	 */
	static boolean isUpdateOnZoomStopEnabled() {
		return true;
	}

	/**
	 * Limit the data to the given x range to plot.
	 * Useful to debug glitches.
	 */
	static Interval visibleXRange() {
		return IntervalConstants.undefined();
	}

	private IntervalPlotSettings() {
		throw new IllegalArgumentException("Should be not initialized");
	}

	/**
	 *
	 * @return if data should resampled on view settings (practically: width) change
	 */
	public static boolean isUpdateOnSettingsChangeEnabled() {
		return true;
	}
}

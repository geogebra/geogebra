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
 * Debug settings for interval plotting.
 */
public final class IntervalPlotSettings {
	/**
	 * Whether the plot model updates continuously while the view moves.
	 */
	static final boolean UPDATE_ON_MOVE_ENABLED = true;

	/**
	 * Whether the plot model updates when view movement stops.
	 */
	static final boolean UPDATE_ON_MOVE_STOP_ENABLED = true;

	/**
	 * Whether the plot model updates when zooming stops.
	 */
	static final boolean UPDATE_ON_ZOOM_STOP_ENABLED = true;

	/**
	 * Optional visible x-range override for glitch debugging.
	 */
	static final Interval VISIBLE_X_RANGE = IntervalConstants.undefined();

	/**
	 * Whether data is resampled when view settings such as width change.
	 */
	static final boolean UPDATE_ON_SETTINGS_CHANGE_ENABLED = true;

	/**
	 * Whether the plot model updates when axis-only zooming stops.
	 */
	static final boolean UPDATE_ON_AXIS_ZOOM_STOP_ENABLED = true;

	private IntervalPlotSettings() {
		throw new IllegalArgumentException("Should be not initialized");
	}
}

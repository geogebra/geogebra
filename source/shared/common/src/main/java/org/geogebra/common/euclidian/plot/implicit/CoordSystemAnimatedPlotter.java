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

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;

/**
 * Plotter with the feature to enable/disable updating.
 * Update can be expensive so sometimes it is good to disable at animation, like zoom or pan.
 */
public abstract class CoordSystemAnimatedPlotter implements CoordSystemAnimationListener {

	private boolean updateEnabled = true;

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		enableUpdate();
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		disableUpdate();
	}

	@Override
	public void onMoveStop() {
		enableUpdate();
	}

	/**
	 * Updates the plotter if update is enabled.
	 */
	public void updateOnDemand() {
		if (updateEnabled) {
			update();
		}
	}

	/**
	 * Actually update the state.
	 */
	public abstract void update();

	protected void enableUpdate() {
		updateEnabled = true;
	}

	private void disableUpdate() {
		updateEnabled = false;
	}

	/**
	 * Draw the results of this plotter.
	 * @param g2 {@link GGraphics2D}
	 */
	public abstract void draw(GGraphics2D g2);
}

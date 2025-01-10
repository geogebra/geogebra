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

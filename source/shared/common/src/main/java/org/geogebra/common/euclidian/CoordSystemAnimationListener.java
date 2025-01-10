package org.geogebra.common.euclidian;

public interface CoordSystemAnimationListener {

	/**
	 * Called when zoom stops animating.
	 * @param info about the coordinate system changes.
	 */
	void onZoomStop(CoordSystemInfo info);

	/**
	 * Called when coordinate system has moved.
	 *
	 * @param info about the coordinate system changes.
	 */
	void onMove(CoordSystemInfo info);

	/**
	 * Called when coordinate system stops moving.
	 */
	void onMoveStop();
}

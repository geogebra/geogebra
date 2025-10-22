package org.geogebra.common.euclidian;

/**
 * EV mode change listener
 *
 */
@FunctionalInterface
public interface ModeChangeListener {
	
	/**
	 * @param mode
	 *            new EV mode
	 */
	void onModeChange(int mode);

}

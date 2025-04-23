package org.geogebra.common.main;

/**
 * Saved state listener.
 */
public interface SavedStateListener {

	/**
	 * Called when saved state changes
	 * @param saved whether construction is saved
	 */
	public void stateChanged(boolean saved);
}

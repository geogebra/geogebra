package org.geogebra.common.main.undo;

/**
 * This listener can be set to listen to the event when undo info is stored.
 */
@FunctionalInterface
public interface UndoInfoStoredListener {

	/**
	 * This method will be called when undo info is stored.
	 */
	void onUndoInfoStored();
}

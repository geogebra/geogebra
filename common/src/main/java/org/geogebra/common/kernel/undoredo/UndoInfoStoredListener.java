package org.geogebra.common.kernel.undoredo;

/**
 * This listener can be set to listen to the event when undo info is stored.
 */
public interface UndoInfoStoredListener {

	/**
	 * This method will be called when undo info is stored.
	 */
	void onUndoInfoStored();
}

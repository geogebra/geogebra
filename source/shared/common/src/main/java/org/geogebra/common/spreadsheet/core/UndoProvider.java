package org.geogebra.common.spreadsheet.core;

@FunctionalInterface
public interface UndoProvider {

	/**
	 * Stores current undo info
	 */
	void storeUndoInfo();
}

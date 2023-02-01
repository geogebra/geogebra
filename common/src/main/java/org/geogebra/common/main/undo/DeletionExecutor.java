package org.geogebra.common.main.undo;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;

public interface DeletionExecutor {

	/**
	 * Delete the element or set undefined when it's fixed.
	 * Stores information needed for undoing deletion of an element (including all descendants)
	 * @param geo deleted element
	 */
	void delete(GeoElement geo);

	/**
	 * Store the recorded information in UndoManager
	 * @param kernel kernel
	 * @return whether some information was stored
	 */
	boolean storeUndoAction(Kernel kernel);
}

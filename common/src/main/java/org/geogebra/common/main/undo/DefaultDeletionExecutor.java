package org.geogebra.common.main.undo;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * We cannot use the undoable deletion executor in Classic or Suite because
 * it changes construction order and that is reflected in many places (AV, graphics, cons. protocol)
 */
public class DefaultDeletionExecutor implements DeletionExecutor {

	@Override
	public void delete(GeoElement ancestor) {
		ancestor.removeOrSetUndefinedIfHasFixedDescendent();
	}

	@Override
	public boolean storeUndoAction(Kernel kernel) {
		if (kernel.isUndoActive()) {
			kernel.storeUndoInfoAndStateForModeStarting();
			return true;
		}
		return false;
	}
}

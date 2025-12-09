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

package org.geogebra.common.main.undo;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * We cannot use the undoable deletion executor in Classic or Suite because
 * it changes construction order and that is reflected in many places (AV, graphics, cons. protocol)
 */
public class DefaultDeletionExecutor implements DeletionExecutor {

	private int deletions = 0;

	@Override
	public void delete(GeoElement ancestor) {
		ancestor.removeOrSetUndefinedIfHasFixedDescendent();
		deletions++;
	}

	@Override
	public boolean storeUndoAction(Kernel kernel) {
		if (kernel.isUndoActive() && deletions > 0) {
			kernel.storeUndoInfoAndStateForModeStarting();
			return true;
		}
		return false;
	}
}

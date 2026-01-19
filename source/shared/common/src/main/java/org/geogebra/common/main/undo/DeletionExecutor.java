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
 * Performs deletion of objects.
 */
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

	/**
	 * @return whether any deletions were performed
	 */
	boolean hasDeletedElements();
}

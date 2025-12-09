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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.util.debug.Log;

/**
 * String based undo manager
 * 
 * @author Balazs
 */
public class DefaultUndoManager extends UndoManager {

	/**
	 * @param cons
	 *            construction
	 */
    public DefaultUndoManager(Construction cons) {
        super(cons);
    }

    @Override
    public void storeUndoInfo(StringBuilder currentUndoXML) {
        doStoreUndoInfo(currentUndoXML);
        informListener();
    }

    /**
     * Adds construction state to undo info list.
     *
     * @param undoXML
     *            string builder with construction XML
     */
    private synchronized void doStoreUndoInfo(final StringBuilder undoXML) {
        AppState appStateToAdd = new StringAppState(undoXML.toString());
        UndoCommand command = createUndoCommand(appStateToAdd);
        maybeStoreUndoCommand(command);
        pruneStateList();
        notifyUnsaved();
        onStoreUndo();
    }

	protected UndoCommand createUndoCommand(AppState appState) {
		return new UndoCommand(appState);
	}

	@Override
	protected void loadUndoInfo(AppState state, String slideID) {
		try {
			construction.processXML(state.getXml(), false, null);
		} catch (Exception e) {
			Log.debug(e);
		}
	}
}

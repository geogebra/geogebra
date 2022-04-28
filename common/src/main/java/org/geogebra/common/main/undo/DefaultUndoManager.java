package org.geogebra.common.main.undo;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
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
    public void storeUndoInfo(StringBuilder currentUndoXML, boolean refresh) {
        doStoreUndoInfo(currentUndoXML);
        if (refresh) {
            restoreCurrentUndoInfo();
        }
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
        app.getEventDispatcher().dispatchEvent(new Event(EventType.STOREUNDO));
        onStoreUndo();
    }

	protected UndoCommand createUndoCommand(AppState appState) {
		return new UndoCommand(appState);
	}

	@Override
	protected void loadUndoInfo(AppState state, String slideID) {
		try {
			processXML(state.getXml(), false, null);
		} catch (Exception e) {
			Log.debug(e);
		}
	}
}

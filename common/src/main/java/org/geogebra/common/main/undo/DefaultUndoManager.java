package org.geogebra.common.main.undo;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;

/**
 * String based undo manager
 * 
 * @author Balazs
 */
public class DefaultUndoManager extends UndoManager {

	private ArrayList<UndoPossibleListener> mListener = new ArrayList<>();

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

    @Override
    protected void updateUndoActions() {
        super.updateUndoActions();
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
        updateUndoActions();
    }

    protected UndoCommand createUndoCommand(AppState appState) {
    	return new UndoCommand(appState);
    }

    @Override
	protected void loadUndoInfo(AppState state, String slideID) {
        try {
			processXML(state.getXml(), false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * inform listener that undo - action happened
     */
    private void informListener() {
        for (UndoPossibleListener listener : mListener) {
            listener.undoPossible(undoPossible());
            listener.redoPossible(redoPossible());
        }
    }

	/**
	 * @param undoPossibleListener
	 *            undo listener
	 */
    public void addUndoListener(UndoPossibleListener undoPossibleListener) {
        mListener.add(undoPossibleListener);
    }

	/**
	 *
	 * @param undoPossibleListener
	 * 			  undo listener
	 */
	public void removeUndoListener(UndoPossibleListener undoPossibleListener) {
    	mListener.remove(undoPossibleListener);
	}
}

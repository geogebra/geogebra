package org.geogebra.common.kernel;

import java.util.ArrayList;

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
	 * Wrapper around string
	 */
    static protected class DefaultAppState implements AppState {
        private String xml;

		/**
		 * @param xml
		 *            wrapped XML
		 */
        DefaultAppState(String xml) {
            this.xml = xml;
        }

		/**
		 * @return wrapped XML
		 */
        public String getXml() {
            return xml;
        }

        @Override
        public void delete() {
			// overridden in subclases
        }
    }

	/**
	 * @param cons
	 *            construction
	 */
    public DefaultUndoManager(Construction cons) {
        super(cons);
        iterator = undoInfoList.listIterator();
    }

    @Override
    public void processXML(String string) throws Exception {
        construction.getXMLio().processXMLString(string, true, false);
    }

    @Override
    public void storeUndoInfoAfterPasteOrAdd() {
		// overridden in subclases
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
        AppState appStateToAdd = new DefaultAppState(undoXML.toString());
		iterator.add(new UndoCommand(appStateToAdd));
        pruneStateList();
        app.getEventDispatcher().dispatchEvent(new Event(EventType.STOREUNDO, null));
    }

    @Override
	protected void loadUndoInfo(AppState state, String slideID) {
        try {
            processXML(((DefaultAppState) state).getXml());
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
}

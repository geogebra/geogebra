package org.geogebra.common.kernel;

import java.util.LinkedList;
import java.util.ListIterator;

import org.geogebra.common.main.App;
import org.geogebra.common.plugin.EventType;

/**
 * Undo manager common to Desktop and Web
 */
public abstract class UndoManager {

	/**
	 * maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps
	 */
	private static final int MAX_CAPACITY = 100;

	/** application */
	public App app;
	/** construction */
	protected Construction construction;
	/** list of undo steps */
	protected LinkedList<UndoCommand> undoInfoList;
	/** invariant: iterator.previous() is current state */
	public ListIterator<UndoCommand> iterator;
	private boolean storeUndoInfoNeededForProperties = false;

	/**
	 * Interface for application state
	 *
	 */
	protected interface AppState {
		/** deletes this application state (i.e. deletes file) */
		void delete();

		String getXml();

	}

	/**
	 * @param cons
	 *            construction
	 */
	public UndoManager(Construction cons) {
		construction = cons;
		app = cons.getApplication();
		undoInfoList = new LinkedList<>();
	}

	public String getCheckpoint(String string) {
		String xml = null;
		for (UndoCommand cmd : undoInfoList) {
			if (cmd.getAppState() != null && cmd.getSlideID().equals(string)) {
				xml = cmd.getAppState().getXml();
			}
		}
		return xml;
	}

	public void executeAction(EventType action, String[] args) {
		app.executeAction(action, args);
	}

	/**
	 * Processes XML
	 * 
	 * @param string
	 *            XML string
	 * @throws Exception
	 *             on trouble with parsing or running commands
	 */
	public abstract void processXML(String string) throws Exception;

	/**
	 * Loads previous construction state from undo info list.
	 */
	public synchronized void undo() {

		if (undoPossible()) {
			UndoCommand last = iterator.previous();
			if (last.getAction() != null) {
				last.undoAction(this);
			} else {
				UndoCommand prev = iterator.previous();
				if (prev.getAppState() != null) {
					loadUndoInfo(prev.getAppState());
				} else {
					// TODO if prev is ADD_SLIDE this resets last slide; not
					// generic
					prev.undoAction(this);
					prev.redo(this);
				}
				iterator.next();
			}
			updateUndoActions();
		}
	}



	/**
	 * Loads next construction state from undo info list.
	 */
	public synchronized void redo() {
		if (redoPossible()) {
			iterator.next().redo(this);
			updateUndoActions();
		}
	}

	/**
	 * Update undo/redo buttons in GUI
	 */
	protected void updateUndoActions() {
		app.updateActions();
	}

	/**
	 * Get current undo info for later comparisons
	 * 
	 * @return Object (the file of last undo)
	 */
	final public synchronized AppState getCurrentUndoInfo() {
		AppState ret = iterator.previous().getAppState();
		iterator.next();
		return ret;
	}

	/**
	 * Store undo info
	 */
	public void storeUndoInfo() {
		storeUndoInfo(false);
	}

	/**
	 * Reloads construction state at current position of undo list (this is
	 * needed for "cancel" actions).
	 */
	final public synchronized void restoreCurrentUndoInfo() {
		app.getSelectionManager().storeSelectedGeosNames();
		if (iterator != null) {
			loadUndoInfo(iterator.previous().getAppState());
			iterator.next();
			updateUndoActions();
		}
		app.getSelectionManager().recallSelectedGeosNames(app.getKernel());
	}

	/**
	 * Clears undo info list and adds current state to the undo info list.
	 */
	public synchronized void initUndoInfo() {
		storeUndoInfoNeededForProperties = false;
		clearUndoInfo();
		storeUndoInfo();
	}

	/**
	 * Returns whether undo operation is possible or not.
	 * 
	 * @return whether undo operation is possible or not.
	 */
	public boolean undoPossible() {
		if (!app.isUndoActive()) {
			return false;
		}
		return iterator.nextIndex() > 1;
	}

	/**
	 * Returns whether redo operation is possible or not.
	 * 
	 * @return whether redo operation is possible or not.
	 */
	public boolean redoPossible() {
		if (!app.isUndoActive()) {
			return false;
		}
		return iterator.hasNext();
	}

	/**
	 * Stores undo info after pasting or adding new objects
	 */
	public abstract void storeUndoInfoAfterPasteOrAdd();

	/**
	 * @param currentUndoXML
	 *            construction XML
	 * @param refresh
	 *            whether to reload afterwards
	 */
	public abstract void storeUndoInfo(StringBuilder currentUndoXML,
			boolean refresh);

	/**
	 * Stores undo info
	 *
	 * @param refresh
	 *            true to restore current
	 */
	final public void storeUndoInfo(final boolean refresh) {
		storeUndoInfo(construction.getCurrentUndoXML(true), refresh);
		storeUndoInfoNeededForProperties = false;
	}

	/**
	 * Loads undo info
	 * 
	 * @param state
	 *            stored state
	 */
	protected abstract void loadUndoInfo(AppState state);

	/**
	 * Clears all undo information
	 */
	public synchronized void clearUndoInfo() {
		undoInfoList.clear();
		iterator = undoInfoList.listIterator();
	}

	/**
	 * Removes all stored states newer than current or too old
	 */
	public void pruneStateList() {
		// remove everything after the insert position until end of
		// list
		UndoCommand appState = null;
		while (iterator.hasNext()) {
			appState = iterator.next();
			iterator.remove();
			appState.delete();
		}

		// delete first if too many in list
		if (undoInfoList.size() > MAX_CAPACITY) {
			// use iterator to delete to avoid
			// ConcurrentModificationException
			// go to beginning of list
			while (iterator.hasPrevious()) {
				appState = iterator.previous();
			}

			iterator.remove();
			appState.delete();

			while (iterator.hasNext()) {
				iterator.next();
			}
		}

	}

	/**
	 * Notify about properties change
	 */
	public void setPropertiesOccured() {
		if (!storeUndoInfoNeededForProperties) {
			storeUndoInfoNeededForProperties = true;
			if (redoPossible()) {
				pruneStateList();
				updateUndoActions();
			}
		}
	}

	/**
	 * End batch of properties changes; reset properties change flag and store
	 * undo if necessary
	 * 
	 * @param isUndoActive
	 *            whether undo should be actually stored
	 */
	public void storeUndoInfoForProperties(boolean isUndoActive) {
		if (isUndoActive && storeUndoInfoNeededForProperties) {
				storeUndoInfo();
		}
		storeUndoInfoNeededForProperties = false;
	}

	public void storeAction(EventType action, String... args) {
		iterator.add(new UndoCommand(action, args));
		this.pruneStateList();

	}
}

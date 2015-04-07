package org.geogebra.common.kernel;

import java.util.LinkedList;
import java.util.ListIterator;

import org.geogebra.common.main.App;

/**
 * Undo manager common to Desktop and Web
 */
public abstract class UndoManager {

	/** maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps */
	private static final int MAX_CAPACITY = 100;

	/**
	 * Interface for application state
	 * 
	 * @author kondr
	 *
	 */
	protected interface AppState {
		/** deletes this application state (i.e. deletes file) */
		void delete();

	}

	/** application */
	public App app;
	/** construction */
	protected Construction construction;
	/** list of undo steps */
	protected LinkedList<AppState> undoInfoList;
	/** invariant: iterator.previous() is current state */
	public ListIterator<AppState> iterator;

	/**
	 * @param cons
	 *            construction
	 */
	public UndoManager(Construction cons) {
		construction = cons;
		app = cons.getApplication();
		undoInfoList = new LinkedList<AppState>();
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
			iterator.previous();
			loadUndoInfo(iterator.previous());
			iterator.next();
			updateUndoActions();
		}
	}

	/**
	 * Loads next construction state from undo info list.
	 */
	public synchronized void redo() {
		if (redoPossible()) {
			loadUndoInfo(iterator.next());
			updateUndoActions();
		}
	}

	/**
	 * Update undo/redo buttons in GUI
	 */
	protected final void updateUndoActions() {
		app.updateActions();
	}

	/**
	 * Get current undo info for later comparisons
	 * 
	 * @return Object (the file of last undo)
	 */
	final public synchronized AppState getCurrentUndoInfo() {
		AppState ret = iterator.previous();
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
		app.getKernel().storeSelectedGeosNames();
		loadUndoInfo(iterator.previous());
		iterator.next();
		updateUndoActions();
		app.getKernel().recallSelectedGeosNames();
	}

	/**
	 * Clears undo info list and adds current state to the undo info list.
	 */
	public synchronized void initUndoInfo() {
		clearUndoInfo();
		storeUndoInfo();
	}

	/**
	 * Returns whether undo operation is possible or not.
	 * 
	 * @return whether undo operation is possible or not.
	 */
	public boolean undoPossible() {
		if (!app.isUndoActive())
			return false;
		return iterator.nextIndex() > 1;
	}

	/**
	 * Returns whether redo operation is possible or not.
	 * 
	 * @return whether redo operation is possible or not.
	 */
	public boolean redoPossible() {
		if (!app.isUndoActive())
			return false;
		return iterator.hasNext();
	}

	/**
	 * Stores undo info after pasting or adding new objects
	 */
	public abstract void storeUndoInfoAfterPasteOrAdd();

	/**
	 * Stores undo info
	 * 
	 * @param refresh
	 *            true to restore current
	 */
	public abstract void storeUndoInfo(boolean refresh);

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
	protected synchronized void clearUndoInfo() {
		undoInfoList.clear();
		iterator = undoInfoList.listIterator();
	}

	/**
	 * Removes all stored states newer than current or too old
	 */
	public void pruneStateList() {
		// remove everything after the insert position until end of
		// list
		AppState appState = null;
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
			while (iterator.hasPrevious())
				appState = iterator.previous();

			iterator.remove();
			appState.delete();

			while (iterator.hasNext())
				iterator.next();
		}

	}
}

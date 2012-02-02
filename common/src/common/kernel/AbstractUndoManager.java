package geogebra.common.kernel;

import java.util.LinkedList;
import java.util.ListIterator;

import geogebra.common.main.AbstractApplication;

public abstract class AbstractUndoManager {
	
	/** maximum capacity of undo info list: you can undo MAX_CAPACITY - 1 steps */
	private static final int MAX_CAPACITY = 100;
	
	/**
	 * Interface for application state
	 * @author kondr
	 *
	 */
	protected interface AppState {

		void delete();
		
	}
	
	protected AbstractApplication app;
	protected Construction construction;
	protected LinkedList<AppState> undoInfoList;
	protected ListIterator<AppState> iterator; // invariant: iterator.previous() is
											// the current state

	public AbstractUndoManager(Construction cons) {
		construction = cons;
		app = cons.getApplication();
		undoInfoList = new LinkedList<AppState>();
	}

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
	
	protected void updateUndoActions() {
		if (app.isUsingFullGui())
			app.getGuiManager().updateActions();
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

	
	public void storeUndoInfo() {
		storeUndoInfo(false);
	}
	
	/**
	 * Reloads construction state at current position of undo list (this is
	 * needed for "cancel" actions).
	 */
	final public synchronized void restoreCurrentUndoInfo() {
		loadUndoInfo(iterator.previous());
		iterator.next();
		updateUndoActions();
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
	 */
	public boolean undoPossible() {
		if (!app.isUndoActive())
			return false;
		return iterator.nextIndex() > 1;
	}

	/**
	 * Returns whether redo operation is possible or not.
	 */
	public boolean redoPossible() {
		if (!app.isUndoActive())
			return false;
		return iterator.hasNext();
	}

	public abstract void storeUndoInfoAfterPasteOrAdd();
	
	public abstract void storeUndoInfo(boolean b);
	
	protected abstract void loadUndoInfo(AppState state);
	
	protected synchronized void clearUndoInfo() {
		undoInfoList.clear();
		iterator = undoInfoList.listIterator();
		System.gc();
	}
	
	/**
	 * Removes all stored states newer than current or too old
	 */
	public void pruneStateList(){
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

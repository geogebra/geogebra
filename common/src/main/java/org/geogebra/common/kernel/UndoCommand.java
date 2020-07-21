package org.geogebra.common.kernel;

import java.util.ListIterator;

import org.geogebra.common.plugin.EventType;

/**
 * Item of undo list; can be either a checkpoint or undoable action
 * 
 * @author Zbynek
 */
public class UndoCommand {

	private AppState appState;
	private EventType action;
	private String[] args;
	private String slideID;

	/**
	 * @param appStateToAdd
	 *            checkpoint state
	 */
	public UndoCommand(AppState appStateToAdd) {
		this.appState = appStateToAdd;
	}

	/**
	 * @param appStateToAdd
	 *            checkpoint state
	 * @param slideID
	 *            slide identifier
	 */
	public UndoCommand(AppState appStateToAdd, String slideID) {
		this.appState = appStateToAdd;
		this.slideID = slideID;
	}

	/**
	 * @param action
	 *            action
	 * @param args
	 *            action arguments
	 */
	public UndoCommand(EventType action, String[] args) {
		this.action = action;
		this.args = args;
	}

	/**
	 * @return checkpoint state
	 */
	public AppState getAppState() {
		return appState;
	}

	/**
	 * Delete associated checkpoint state if applicable
	 */
	public void delete() {
		if (appState != null) {
			appState.delete();
		}
	}

	/**
	 * Execute the command again
	 * 
	 * @param undoManager
	 *            undo manager
	 */
	public void redo(UndoManager undoManager) {
		if (appState != null) {
			undoManager.loadUndoInfo(appState, slideID);
		} else {
			if (action == EventType.EMBEDDED_STORE_UNDO) {
				undoManager.embeddedAction(EventType.REDO, args[0]);
			} else {
				undoManager.executeAction(action, null, args);
			}
		}
	}

	/**
	 * @return action
	 */
	public EventType getAction() {
		return action;
	}

	/**
	 * @return action arguments
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * Revert the action of this command
	 * 
	 * @param mgr
	 *            undo manager
	 */
	public void undoAction(UndoManager mgr) {
		if (action == EventType.ADD_SLIDE) {
			mgr.executeAction(EventType.REMOVE_SLIDE, null, args[0]);
		} else if (action == EventType.PASTE_SLIDE) {
			mgr.executeAction(EventType.REMOVE_SLIDE, null,
					(Integer.parseInt(args[0]) + 1) + "");
		} else if (action == EventType.REMOVE_SLIDE) {
			mgr.executeAction(EventType.ADD_SLIDE, mgr.getCheckpoint(args[1]),
					args[0], args[1]);
		} else if (action == EventType.CLEAR_SLIDE) {
			mgr.executeAction(EventType.ADD_SLIDE, mgr.getCheckpoint(args[0]),
					"-1", args[0]);
		} else if (action == EventType.MOVE_SLIDE) {
			mgr.executeAction(EventType.MOVE_SLIDE, null,
					args[1], args[0]);
		} else if (action == EventType.EMBEDDED_STORE_UNDO) {
			mgr.embeddedAction(EventType.UNDO, args[0]);
		}
	}

	/**
	 * @return slide ID
	 */
	public String getSlideID() {
		return slideID;
	}

	/**
	 * @param undoManager
	 *            undo manager
	 * @param iterator
	 *            pointer to current undo point in manager
	 */
	public void undo(UndoManager undoManager,
			ListIterator<UndoCommand> iterator) {
		if (getAction() != null) {
			undoAction(undoManager);
		} else {
			AppState checkpoint = undoManager.getCheckpoint(slideID);
			if (checkpoint != null) {
				undoManager.loadUndoInfo(checkpoint, slideID);
			} else {
				undoManager.getCreationCommand(slideID);
			}
		}
	}

	/**
	 * Get the app to the state right after this was executed.
	 * 
	 * @param mgr
	 *            undo manager
	 */
	public void loadStateAfter(UndoManager mgr) {
		if (action == EventType.ADD_SLIDE) {
			mgr.executeAction(EventType.CLEAR_SLIDE, null, args[1]);
		}
	}

	/**
	 * Returns true only if there is an app state that is equal to the other app state.
	 * It also takes into account slide ids, as app state and slide id go together.
	 *
	 * @param other other undo command
	 * @return true if states and slide ids are equal
	 */
	public boolean equalsState(UndoCommand other) {
		if (appState == null || !appState.equalsTo(other.appState)) {
			return false;
		}
		if (slideID != null ? !slideID.equals(other.slideID) : other.slideID != null) {
			return false;
		}
		return true;
	}
}
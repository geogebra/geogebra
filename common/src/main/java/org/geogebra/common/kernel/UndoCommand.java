package org.geogebra.common.kernel;

import java.util.ListIterator;

import org.geogebra.common.kernel.UndoManager.AppState;
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
			undoManager.executeAction(action, null, args);
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
		if(action == EventType.ADD_SLIDE){
			mgr.executeAction(EventType.REMOVE_SLIDE, null, new String[0]);
		}
		else if (action == EventType.DUPLICATE_SLIDE) {
			mgr.executeAction(EventType.REMOVE_SLIDE, null,
					new String[] { (Integer.parseInt(args[0]) + 1) + "" });
		}
		else if (action == EventType.REMOVE_SLIDE) {
			mgr.executeAction(EventType.ADD_SLIDE,
					mgr.getCheckpoint(args[1]), args[0], args[1]);
		} else if (action == EventType.CLEAR_SLIDE) {
			mgr.executeAction(EventType.ADD_SLIDE, mgr.getCheckpoint(args[0]),
					"-1", args[0]);
		} else if (action == EventType.MOVE_SLIDE) {
			mgr.executeAction(EventType.MOVE_SLIDE, null,
					new String[] { args[1], args[0] });
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
			UndoCommand prev = iterator.previous();
			if (prev.getAppState() != null && (prev.getSlideID() == null
					|| prev.getSlideID().equals(slideID))) {
				undoManager.loadUndoInfo(prev.getAppState(), prev.getSlideID());
				iterator.next();
			} else {
				if(prev.action == EventType.DUPLICATE_SLIDE ||  prev.action == EventType.ADD_SLIDE){
					prev.undoAction(undoManager);
					prev.redo(undoManager);
				} else {
					undoManager.loadUndoInfo(
							undoManager.getCheckpoint(slideID),
							getSlideID());
				}
				iterator.next();
			}
		}
	}

}
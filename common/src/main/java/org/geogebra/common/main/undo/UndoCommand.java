package org.geogebra.common.main.undo;

import java.util.Objects;

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
	 * Copy constructor
	 * @param command to copy from.
	 */
	public UndoCommand(UndoCommand command) {
		this.appState = command.appState;
		this.action = command.action;
		this.args = command.args;
		this.slideID = command.slideID;
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
	public UndoCommand(EventType action, String slideId, String[] args) {
		this.action = action;
		this.args = args;
		this.slideID = slideId;
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
	public void redo(final UndoManager undoManager) {
		if (appState != null) {
			undoManager.resetBeforeReload();
			undoManager.loadUndoInfo(appState, slideID);
			undoManager.restoreAfterReload();
		} else {
			withCurrentSlide(undoManager, new Runnable() {

				@Override
				public void run() {
					undoManager.executeAction(action, args);
				}
			});
		}
	}

	private void withCurrentSlide(final UndoManager undoManager, Runnable runnable) {
		if (action == EventType.ADD || action == EventType.UPDATE) {
			undoManager.runAfterSlideLoaded(slideID, runnable);
		} else {
			runnable.run();
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
	 * @return slide ID
	 */
	public String getSlideID() {
		return slideID;
	}

	/**
	 * @param undoManager
	 *            undo manager
	 */
	public void undo(final UndoManager undoManager) {
		if (getAction() != null) {
			withCurrentSlide(undoManager, new Runnable() {
				@Override
				public void run() {
					undoManager.undoAction(action, args);
					//TODO: maybe these actions should also take care of reloading
					// the correct information without replay?
					if (action == EventType.CLEAR_SLIDE || action == EventType.REMOVE_SLIDE) {
						undoManager.replayActions(slideID, UndoCommand.this);
					}
				}
			});
		} else {
			undoManager.resetBeforeReload();
			UndoCommand checkpoint = undoManager.getCheckpoint(slideID);
			if (checkpoint != null) {
				undoManager.loadUndoInfo(checkpoint, slideID, this);
			} else {
				undoManager.redoCreationCommand(slideID);
			}
			undoManager.restoreAfterReload();
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
			mgr.executeAction(EventType.CLEAR_SLIDE, args[1]);
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
		return Objects.equals(slideID, other.slideID);
	}
}
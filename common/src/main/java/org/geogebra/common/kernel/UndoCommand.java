package org.geogebra.common.kernel;

import org.geogebra.common.kernel.UndoManager.AppState;
import org.geogebra.common.plugin.EventType;

public class UndoCommand {

	private AppState appState;
	private EventType action;
	private String[] args;
	private String slideID;

	public UndoCommand(AppState appStateToAdd) {
		this.appState = appStateToAdd;
	}

	public UndoCommand(AppState appStateToAdd, String slideID) {
		this.appState = appStateToAdd;
		this.slideID = slideID;
	}

	public UndoCommand(EventType action, String[] args) {
		this.action = action;
		this.args = args;
	}

	public AppState getAppState() {
		return appState;
	}

	public void delete() {
		if (appState != null) {
			appState.delete();
		}
	}

	public void redo(UndoManager undoManager) {
		if (appState != null) {
			undoManager.loadUndoInfo(appState);
		} else {
			undoManager.executeAction(action, args);
		}
	}

	public EventType getAction() {
		return action;
	}

	public String[] getArgs() {
		return args;
	}

	public void undoAction(UndoManager mgr) {
		if(action == EventType.ADD_SLIDE){
			mgr.executeAction(EventType.REMOVE_SLIDE, new String[0]);
		}
		else if (action == EventType.DUPLICATE_SLIDE) {
			mgr.executeAction(EventType.REMOVE_SLIDE,
					new String[] { (Integer.parseInt(args[0]) + 1) + "" });
		}
		else if (action == EventType.REMOVE_SLIDE) {
			mgr.executeAction(EventType.ADD_SLIDE,
					new String[] { args[0], mgr.getCheckpoint(args[0]) });
		} else if (action == EventType.MOVE_SLIDE) {
			mgr.executeAction(EventType.MOVE_SLIDE,
					new String[] { args[1], args[0] });
		}
	}

	public String getSlideID() {
		return slideID;
	}

}
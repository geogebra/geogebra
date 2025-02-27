package org.geogebra.common.main.undo;

import java.util.Arrays;

import org.geogebra.common.plugin.ActionType;

public class UndoCommandBuilder {
	private final UndoManager undoManager;
	private final UndoCommand command;

	/**
	 * @param undoManager undo manager
	 * @param slideID slide ID
	 * @param action action (for redo)
	 * @param args action arguments
	 */
	public UndoCommandBuilder(UndoManager undoManager, String slideID, ActionType action,
			String[] args) {
		this.undoManager = undoManager;
		this.command = new UndoCommand(slideID, action, args, null, new String[0]);
	}

	/**
	 * @param undoAction action for undo
	 * @param undoArgs action arguments
	 * @return this
	 */
	public UndoCommandBuilder withUndo(ActionType undoAction, String... undoArgs) {
		command.undoAction = undoAction;
		command.undoArgs = undoArgs;
		return this;
	}

	/**
	 * Store undo action and notify all listeners
	 */
	public void storeAndNotifyUnsaved() {
		undoManager.storeAndNotify(command);
		undoManager.notifyUnsaved();
	}

	/**
	 * @param labels related geo labels
	 * @return this
	 */
	public UndoCommandBuilder withLabels(String... labels) {
		command.labels = Arrays.asList(labels);
		return this;
	}

	/**
	 * Mark the command as stitched to next (they get un/redone together)
	 * @return this
	 */
	public UndoCommandBuilder withStitchToNext() {
		command.stitchToNext = true;
		return this;
	}
}

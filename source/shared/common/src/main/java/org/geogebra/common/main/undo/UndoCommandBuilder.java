/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

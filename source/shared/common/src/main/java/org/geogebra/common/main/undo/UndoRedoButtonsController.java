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

import org.geogebra.common.gui.view.ActionView;
import org.geogebra.common.kernel.Kernel;

import com.google.j2objc.annotations.Weak;

/**
 * Adds the undo-redo functionality to two ActionView objects.
 * To achieve this the class adds executable actions to the ActionView objects.
 * This class also sets an UndoInfoStoredListener to the UndoManager class. This listener listens to
 * the event when undo info is stored, so it gets triggered every time when there is something new
 * to undo.
 * The startListeningToTriggerEvents() method has to be called in order add the actions and set the
 * listener.
 */
public class UndoRedoButtonsController implements UndoPossibleListener {

	@Weak
	private Kernel kernel;
	private ActionView undoWidget;
	private ActionView redoWidget;

	/**
	 * @param kernel The kernel.
	 * @param undoWidget The widget that will have the undo functionality.
	 * @param redoWidget The widget that will have the redo functionality.
	 */
	public UndoRedoButtonsController(Kernel kernel, ActionView undoWidget, ActionView redoWidget) {
		this.kernel = kernel;
		this.undoWidget = undoWidget;
		this.redoWidget = redoWidget;
	}

	/**
	 * Adds the actions to the ActionView objects and sets the listener to the UndoManager.
	 */
	public void startListeningToTriggerEvents() {
		registerUndoInfoStoredListener();
		undoWidget.setAction(this::undoAndUpdateAppearance);
		redoWidget.setAction(this::redoAndUpdateAppearance);
		updateAppearance();
	}

	private void registerUndoInfoStoredListener() {
		kernel
				.getConstruction()
				.getUndoManager()
				.addUndoListener(this);
	}

	void undoAndUpdateAppearance() {
		kernel.undo();
		updateAppearance();
	}

	void redoAndUpdateAppearance() {
		kernel.redo();
		updateAppearance();
	}

	void updateAppearance() {
		undoWidget.setEnabled(kernel.undoPossible());
		redoWidget.setEnabled(kernel.redoPossible());
	}

	/**
	 * Adds the undo and redo functionalities to the views by creating a new UndoRedoExecutor
	 * instance and calling the startListeningToTriggerEvents() method on this instance.
	 *
	 * @param undoWidget This view gets the undo functionality.
	 * @param redoWidget This view gets the redo functionality
	 * @param kernel The kernel.
	 */
	public static void addUndoRedoFunctionality(
			ActionView undoWidget, ActionView redoWidget,
			Kernel kernel) {

		new UndoRedoButtonsController(kernel, undoWidget, redoWidget)
				.startListeningToTriggerEvents();
	}

	@Override
	public void undoPossible(boolean isPossible) {
		undoWidget.setEnabled(isPossible);
	}

	@Override
	public void redoPossible(boolean isPossible) {
		redoWidget.setEnabled(isPossible);
	}
}

package org.geogebra.common.kernel.undoredo;

import org.geogebra.common.gui.view.ActionView;
import org.geogebra.common.kernel.Kernel;

public class UndoRedoExecutor {

	private Kernel kernel;
	private ActionView undoWidget;
	private ActionView redoWidget;

	public UndoRedoExecutor(Kernel kernel, ActionView undoWidget, ActionView redoWidget) {
		this.kernel = kernel;
		this.undoWidget = undoWidget;
		this.redoWidget = redoWidget;
	}

	public void startListeningToTriggerEvents() {
		updateAppearance();
		registerUndoInfoStoredListener();
		undoWidget.setAction(createUndoAction());
		redoWidget.setAction(createRedoAction());
	}

	private void registerUndoInfoStoredListener() {
		kernel
				.getConstruction()
				.getUndoManager()
				.addUndoInfoStoredListener(createUndoInfoStoredListener());
	}

	private UndoInfoStoredListener createUndoInfoStoredListener() {
		return new UndoInfoStoredListener() {
			@Override
			public void onUndoInfoStored() {
				updateAppearance();
			}
		};
	}

	private Runnable createUndoAction() {
		return new Runnable() {

			@Override
			public void run() {
				kernel.undo();
				updateAppearance();
			}
		};
	}

	private Runnable createRedoAction() {
		return new Runnable() {

			@Override
			public void run() {
				kernel.redo();
				updateAppearance();
			}
		};
	}

	private void updateAppearance() {
		undoWidget.setEnabled(kernel.undoPossible());
		redoWidget.setEnabled(kernel.redoPossible());
	}

	/**
	 * Adds the undo and redo functionalities to the views.
	 * @param undoWidget This view gets the undo functionality.
	 * @param redoWidget This view gets the redo functionality
	 * @param kernel
	 */
	public static void addUndoRedoFunctionality(
			ActionView undoWidget, ActionView redoWidget,
			Kernel kernel) {

		new UndoRedoExecutor(kernel, undoWidget, redoWidget).startListeningToTriggerEvents();
	}
}

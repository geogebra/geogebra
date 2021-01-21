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
public class UndoRedoButtonsController implements UndoInfoStoredListener {

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
		undoWidget.setAction(createUndoAction());
		redoWidget.setAction(createRedoAction());
		updateAppearance();
	}

	private void registerUndoInfoStoredListener() {
		kernel
				.getConstruction()
				.getUndoManager()
				.addUndoInfoStoredListener(this);
	}

	@Override
	public void onUndoInfoStored() {
		updateAppearance();
	}

	private Runnable createUndoAction() {
		return new Runnable() {

			@Override
			public void run() {
				undoAndUpdateAppearance();
			}
		};
	}

	void undoAndUpdateAppearance() {
		kernel.undo();
		updateAppearance();
	}

	private Runnable createRedoAction() {
		return new Runnable() {

			@Override
			public void run() {
				redoAndUpdateAppearance();
			}
		};
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
}

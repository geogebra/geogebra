package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.PersistablePanel;

/**
 * Undo/redo panel for graphics view
 */
public class UndoRedoPanel extends PersistablePanel {
	private final UndoRedoProvider undoRedoProvider;

	/**
	 * @param app application
	 */
	public UndoRedoPanel(AppW app) {
		undoRedoProvider = new UndoRedoProvider(app, AccessibilityGroup.UNDO_GRAPHICS,
				AccessibilityGroup.REDO_GRAPHICS);
		addStyleName("undoRedoPanel");
		buildPanel();
	}

	private void buildPanel() {
		add(undoRedoProvider.getUndoButton());
		add(undoRedoProvider.getRedoButton());
	}

	/**
	 * Enable/disable undo and redo buttons if undo/redo action is possible.
	 */
	public void updateUndoRedoActions() {
		undoRedoProvider.updateUndoRedoActions();
	}

	protected void setLabels() {
		undoRedoProvider.setLabels();
	}
}

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.topbar.TopBarIcon;

/**
 * Undo/redo panel for unbundled apps
 */
public class UndoRedoProvider {
	private final AppW app;
	protected FocusableWidget redoAnchor;
	IconButton btnUndo;
	IconButton btnRedo;

	/**
	 * Undo/redo button provider with default {@link AccessibilityGroup#UNDO} and
	 * {@link AccessibilityGroup#REDO}
	 * @param app {@link AppW}
	 */
	public UndoRedoProvider(AppW app) {
		this(app, AccessibilityGroup.UNDO, AccessibilityGroup.REDO);
	}

	/**
	 * Undo/redo button provider with defined {@link AccessibilityGroup}
	 * @param app {@link AppW}
	 */
	public UndoRedoProvider(AppW app, AccessibilityGroup undoGroup, AccessibilityGroup redoGroup) {
		this.app = app;
		initUndoButton(undoGroup);
		initRedoButton(redoGroup);
	}

	private void initUndoButton(AccessibilityGroup undoGroup) {
		 btnUndo = new IconButton(app, () -> onUndoPressed(app), app.getTopBarIconResource()
				.getImageResource(TopBarIcon.UNDO), "Undo");
		new FocusableWidget(undoGroup, null, btnUndo).attachTo(app);
		btnUndo.addStyleName("undo");
	}

	private void initRedoButton(AccessibilityGroup redoGroup) {
		btnRedo = new IconButton(app, () -> onRedoPressed(app), app.getTopBarIconResource()
				.getImageResource(TopBarIcon.REDO), "Redo");
		new FocusableWidget(redoGroup, null, btnRedo).attachTo(app);
		btnRedo.addStyleName("redo");
	}

	public IconButton getUndoButton() {
		return btnUndo;
	}

	public IconButton getRedoButton() {
		return btnRedo;
	}

	/**
	 * Handler for Undo button.
	 */
	public void onUndoPressed(AppW app) {
		app.closePopups();
		app.closeMenuHideKeyboard();
		app.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	void onRedoPressed(AppW app) {
		app.closePopups();
		app.closeMenuHideKeyboard();
		app.getAccessibilityManager().setAnchor(redoAnchor);
		app.getGuiManager().redo();
		app.getAccessibilityManager().cancelAnchor();
	}

	/**
	 * Enable/disable undo and redo buttons if undo/redo action is possible.
	 */
	public void updateUndoRedoActions() {
		if (btnUndo == null || btnRedo == null) {
			return;
		}
		btnUndo.setDisabled(!app.getKernel().undoPossible());
		btnRedo.setDisabled(!app.getKernel().redoPossible());
	}

	protected void setLabels() {
		btnUndo.setLabels();
		btnRedo.setLabels();
	}
}

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.PersistablePanel;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.user.client.ui.Widget;

/**
 * Undo/redo panel for unbundled apps
 */
public class UndoRedoPanel extends PersistablePanel {
	protected FocusableWidget redoAnchor;
	StandardButton btnUndo;
	StandardButton btnRedo;
	private final AppW app;

	/**
	 * @param app application
	 */
	public UndoRedoPanel(AppW app) {
		this.app = app;
		addStyleName("undoRedoPanel");
		addUndoButton();
		addRedoButton();
		setLabels();
		setTabIndexes();
	}

	void addUndoButton() {
		btnUndo = new StandardButton(MaterialDesignResources.INSTANCE.undo_border(), 24);
		btnUndo.setTitle(app.getLocalization().getMenu("Undo"));
		btnUndo.addStyleName("flatButton");
		btnUndo.addStyleName("undo");
		btnUndo.addFastClickHandler(event -> onUndoPressed());
		add(btnUndo);
	}

	private void addRedoButton() {
		btnRedo = new StandardButton(MaterialDesignResources.INSTANCE.redo_border(), 24);
		btnRedo.setTitle(app.getLocalization().getMenu("Redo"));
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
		btnRedo.addStyleName("redo");
		btnRedo.addFastClickHandler(event -> onRedoPressed());
		add(btnRedo);
	}

	/**
	 * Handler for Undo button.
	 */
	protected void onUndoPressed() {
		app.closePopups();
		app.closeMenuHideKeyboard();
		app.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	protected void onRedoPressed() {
		app.closePopups();
		app.closeMenuHideKeyboard();
		app.getAccessibilityManager().setAnchor(redoAnchor);
		app.getGuiManager().redo();
		app.getAccessibilityManager().cancelAnchor();
	}

	protected void updateUndoActions() {
		Dom.toggleClass(btnUndo, "buttonActive", "buttonInactive",
				app.getKernel().undoPossible());

		if (app.getKernel().redoPossible()) {
			btnRedo.removeStyleName("hideButton");
		} else {
			if (!btnRedo.getElement().hasClassName("hideButton")) {
				app.getAccessibilityManager().focusAnchor();
			}
			btnRedo.addStyleName("hideButton");
		}
	}

	protected void setTabIndexes() {
		tabIndex(btnUndo, AccessibilityGroup.UNDO);
		tabIndex(btnRedo, AccessibilityGroup.REDO);
		setAltTexts();
	}

	private void tabIndex(StandardButton btn, AccessibilityGroup group) {
		if (btn != null) {
			new FocusableWidget(group, null, btn).attachTo(app);
		}
	}

	protected void setLabels() {
		setTitle(btnUndo, "Undo");
		setTitle(btnRedo, "Redo");
		setAltTexts();
	}

	private void setAltTexts() {
		setAltText(btnUndo, "Undo");
		setAltText(btnRedo, "Redo");
	}

	private void setTitle(Widget btn, String avTitle) {
		if (btn != null) {
			btn.setTitle(app.getLocalization().getMenu(avTitle));
			TestHarness.setAttr(btn, "btn_" + avTitle);
		}
	}

	private void setAltText(StandardButton btn, String string) {
		if (btn != null) {
			btn.setAltText(app.getLocalization().getMenu(string));
		}
	}
}

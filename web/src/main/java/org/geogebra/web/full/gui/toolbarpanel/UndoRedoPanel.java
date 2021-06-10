package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GCustomButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.PersistablePanel;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Undo/redo panel for unbundled apps
 */
public class UndoRedoPanel extends PersistablePanel implements KeyDownHandler {
	protected FocusableWidget redoAnchor;
	MyToggleButton btnUndo;
	MyToggleButton btnRedo;
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
		btnUndo = new MyToggleButton(
				new NoDragImage(MaterialDesignResources.INSTANCE.undo_border(),
						24),
				app);
		btnUndo.setTitle(app.getLocalization().getMenu("Undo"));
		btnUndo.addStyleName("flatButton");
		btnUndo.addStyleName("undo");

		ClickStartHandler.init(btnUndo, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onUndoPressed();
			}
		});

		btnUndo.addKeyDownHandler(this);

		add(btnUndo);
	}

	private void addRedoButton() {
		btnRedo = new MyToggleButton(
				new NoDragImage(MaterialDesignResources.INSTANCE.redo_border(),
						24),
				app);
		btnRedo.setTitle(app.getLocalization().getMenu("Redo"));
		btnRedo.addStyleName("flatButton");
		btnRedo.addStyleName("buttonActive");
		btnRedo.addStyleName("redo");

		ClickStartHandler.init(btnRedo, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onRedoPressed();
			}
		});

		btnRedo.addKeyDownHandler(this);
		add(btnRedo);
	}

	/**
	 * Handler for Undo button.
	 */
	protected void onUndoPressed() {
		app.closeMenuHideKeyboard();
		app.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	protected void onRedoPressed() {
		app.closeMenuHideKeyboard();
		app.getAccessibilityManager().setAnchor(redoAnchor);
		app.getGuiManager().redo();
		app.getAccessibilityManager().cancelAnchor();
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		if (key != GWTKeycodes.KEY_ENTER && key != GWTKeycodes.KEY_SPACE) {
			return;
		}
		Object source = event.getSource();
		if (source == btnUndo) {
			onUndoPressed();
		} else if (source == btnRedo) {
			onRedoPressed();
		}
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

	private void tabIndex(GCustomButton btn, AccessibilityGroup group) {
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

	private void setAltText(MyToggleButton btn, String string) {
		if (btn != null) {
			btn.setAltText(app.getLocalization().getMenu(string));
		}
	}
}

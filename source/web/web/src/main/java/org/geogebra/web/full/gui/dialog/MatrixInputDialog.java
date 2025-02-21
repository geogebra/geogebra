package org.geogebra.web.full.gui.dialog;

import java.util.function.Consumer;

import org.geogebra.common.kernel.validator.NumberValidator;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import elemental2.dom.EventListener;

public class MatrixInputDialog extends ComponentDialog {

	private final AppW app;
	private ComponentInputField rows;
	private ComponentInputField columns;
	private Consumer<String> processInput;

	/**
	 * Constructor
	 * @param app AppW
	 * @param processInput Callback used to process the selected input
	 */
	public MatrixInputDialog(AppW app, Consumer<String> processInput) {
		super(app, new DialogData("Matrix", "Cancel", "OK"), false, true);
		this.app = app;
		this.processInput = processInput;
		buildGUI();

		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
		});
	}

	private void buildGUI() {
		rows = addInputRow(app.getLocalization().getMenu("NumberOfRows"));
		columns = addInputRow(app.getLocalization().getMenu("NumberOfColumns"));
	}

	private ComponentInputField addInputRow(String label) {
		ComponentInputField inputField = new ComponentInputField(
				app, null, label, "", "2", 28, null, true);
		addDialogContent(inputField);
		inputField.getTextField().addTextComponentInputListener(onContentChanged(inputField));
		return inputField;
	}

	@Override
	public void onPositiveAction() {
		hide();
		if (isInputAllowed(rows) && isInputAllowed(columns)) {
			processInput.accept("$matrix:" + Integer.parseInt(rows.getText())
					+ ":" + Integer.parseInt(columns.getText()));
			app.storeUndoInfo();
		}
	}

	@Override
	public void show() {
		app.hideKeyboard();
		super.show();
		rows.focusDeferred();
	}

	private boolean isInputAllowed(ComponentInputField inputField) {
		try {
			return Integer.parseInt(inputField.getText()) > 0;
		} catch (NumberFormatException ignore) {
			return false;
		}
	}

	private void setErrorState(ComponentInputField inputField, boolean error) {
		Dom.toggleClass(inputField.asWidget().getParent(), "error", error);
	}

	private EventListener onContentChanged(ComponentInputField inputField) {
		return ignore -> {
			setPosBtnDisabled(inputField.getText().isBlank() || !isInputAllowed(inputField));
			if (!isInputAllowed(inputField) && !inputField.getText().isBlank()) {
				setErrorState(inputField, true);
				inputField.setError(app.getLocalization().getError(
						NumberValidator.NUMBER_NEGATIVE_ERROR_MESSAGE_KEY));
			} else {
				setErrorState(inputField, false);
				inputField.setError("");
			}
		};
	}
}

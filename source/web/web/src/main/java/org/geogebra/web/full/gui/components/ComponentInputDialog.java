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

package org.geogebra.web.full.gui.components;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.dialog.ProcessInput;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * dialog component for dialogs with one input text field
 * e.g. regular polygon tool dialog
 */
public class ComponentInputDialog extends ComponentDialog
		implements ErrorHandler, HasKeyboardPopup {
	private InputHandler inputHandler;
	private ComponentInputField inputTextField;

	/**
	 * Base dialog constructor: single row
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 * @param inputHandler - input handler
	 */
	public ComponentInputDialog(AppW app, DialogData dialogData,
			boolean autoHide, boolean hasScrim, InputHandler inputHandler, String labelText,
			String initText) {
		this(app, dialogData, autoHide, hasScrim, inputHandler);
		createGUI(labelText, initText);
	}

	/**
	 *
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param autoHide - if the dialog should be closed on click outside
	 * @param hasScrim - background should be greyed out
	 * @param inputHandler - input handler
	 */
	protected ComponentInputDialog(AppW app, DialogData dialogData,
			boolean autoHide, boolean hasScrim, InputHandler inputHandler) {
		super(app, dialogData, autoHide, hasScrim);
		addStyleName("inputDialogComponent");
		setPreventHide(true);
		setInputHandler(inputHandler);
		setOnPositiveAction(this::processInput);
		if (!app.isWhiteboardActive()) {
			app.registerPopup(this);
		}
		this.addCloseHandler(event -> {
			app.unregisterPopup(this);
			app.hideKeyboard();
		});
	}

	private void createGUI(String labelText, String initText) {
		inputTextField = new ComponentInputField((AppW) app,
				"", labelText, "", initText, "");
		addDialogContent(inputTextField);
	}

	protected InputHandler getInputHandler() {
		return inputHandler;
	}

	protected void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}

	protected void processInputHandler(String inputText,
			AsyncOperation<Boolean> callback) {
		inputHandler.processInput(inputText, this, callback);
	}

	public String getInputText() {
		return inputTextField.getTextField().getText();
	}

	/**
	 * Note: package visibility to make this accessible from anonymous classes
	 *
	 * @return single line text input
	 */
	protected AutoCompleteTextFieldW getTextComponent() {
		return inputTextField == null ? null : inputTextField.getTextField().getTextComponent();
	}

	@Override
	public void show() {
		super.show();
		if (inputTextField != null) {
			inputTextField.focusDeferred();
		}
	}

	@Override
	public void showCommandError(String command, String message) {
		app.getDefaultErrorHandler().showCommandError(command, message);
	}

	@Override
	public String getCurrentCommand() {
		AutoCompleteTextFieldW textComponent = getTextComponent();
		if (textComponent != null) {
			return textComponent.getCommand();
		}
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void resetError() {
		showError(null);
	}

	@Override
	public void showError(String msg) {
		if (inputTextField != null) {
			inputTextField.setError(msg);
		}
	}

	/**
	 * process input, show error if input wrong,
	 * otherwise hide dialog
	 */
	public void processInput() {
		inputHandler.processInput(getInputText(), this,
				ok -> {
					if (ok) {
						toolAction();
						hide();
					}
				});
	}

	/**
	 * Callback for tool dialogs
	 */
	protected void toolAction() {
		// overridden in subclasses
	}

	/**
	 * @param inputHandler input event handler
	 */
	public void addInputHandler(ProcessInput inputHandler) {
		Dom.addEventListener(getTextComponent().getTextBox().getElement(),
				"input", event -> inputHandler.onInput());
	}
}
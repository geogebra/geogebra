package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class MediaInputPanel extends FlowPanel implements ProcessInput {

	private AppW app;
	private ComponentDialog parentDialog;
	private boolean required;

	protected InputPanelW inputField;
	private Label errorLabel;
	private Label infoLabel;

	/**
	 * @param app
	 *         application
	 * @param parentDialog
	 *         parent dialog
	 * @param labelTransKey
	 *         label translation key
	 * @param required
	 *         whether nonempty string is expected
	 */
	public MediaInputPanel(AppW app, ComponentDialog parentDialog,
			String labelTransKey, boolean required) {
		this.app = app;
		this.parentDialog = parentDialog;
		this.required = required;

		setStyleName("mowInputPanelContent");
		addStyleName("emptyState");

		inputField = new InputPanelW("", app, 1, 25, false);

		FormLabel inputLabel = new FormLabel().setFor(inputField.getTextComponent());
		inputLabel.setText(app.getLocalization().getMenu(labelTransKey));
		inputLabel.addStyleName("inputLabel");
		inputField.addStyleName("inputText");

		errorLabel = new Label();
		errorLabel.addStyleName("msgLabel errorLabel");

		add(inputLabel);
		add(inputField);
		add(errorLabel);

		addHoverHandlers();
		addFocusBlurHandlers();
		addInputHandler();
	}

	/**
	 * Set focus the text field of the input panel
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(() -> {
			inputField.getTextComponent().setFocus(true);
			inputField.getTextComponent().selectAll();
		});
	}

	/**
	 * Add placeholder to the text field of the input panel
	 * @param placeholder localized placeholder string
	 */
	public void addPlaceholder(String placeholder) {
		inputField.getTextComponent().getTextBox().getElement()
				.setAttribute("placeholder", placeholder);
	}

	/**
	 * Set input text and update error state.
	 * @param text
	 *         input text
	 */
	public void setText(String text) {
		inputField.getTextComponent().setText(text);
		resetError();
	}

	/**
	 * Add info label to the input panel
	 */
	public void addInfoLabel() {
		infoLabel = new Label();
		infoLabel.addStyleName("msgLabel");
		add(infoLabel);
	}

	/**
	 * @return trimmed input text
	 */
	public String getInput() {
		return inputField.getText().trim();
	}

	/**
	 * Set the input panel to the error state
	 * @param msg error message to show
	 */
	public void showError(String msg) {
		setStyleName("mowInputPanelContent");
		addStyleName("errorState");
		errorLabel.setText(app.getLocalization().getMenu("Error") + ": "
				+ app.getLocalization().getError(msg));
		parentDialog.setPosBtnDisabled(true);
	}

	/**
	 * @param info permanent information message
	 */
	public void showInfo(String info) {
		infoLabel.setText(info);
	}

	/**
	 * Remove error state from input panel
	 */
	public void resetError() {
		setStyleName("mowInputPanelContent");
		addStyleName("emptyState");
		removeStyleName("errorState");
		if (required) {
			parentDialog.setPosBtnDisabled(isInputEmpty());
		}
	}

	private boolean isInputEmpty() {
		return StringUtil.emptyTrim(inputField.getText());
	}

	@Override
	public void onInput() {
		resetError();
		addStyleName("focusState");
		removeStyleName("emptyState");
	}

	@Override
	public void processInput() {
		parentDialog.onPositiveAction();
	}

	/**
	 * Add handler for input event
	 */
	private void addInputHandler() {
		new MediaInputKeyHandler(this).attachTo(inputField.getTextComponent());
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputField.getTextComponent().getTextBox().addMouseOverHandler(
				event -> this.addStyleName("hoverState"));

		inputField.getTextComponent().getTextBox().addMouseOutHandler(
				event -> this.removeStyleName("hoverState"));
	}

	private void addFocusBlurHandlers() {
		inputField.getTextComponent().getTextBox().addFocusHandler(event -> setFocusState());

		inputField.getTextComponent().getTextBox().addBlurHandler(event -> resetInputField());
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	private void setFocusState() {
		setStyleName("mowInputPanelContent");
		addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	private void resetInputField() {
		removeStyleName("focusState");
		addStyleName("emptyState");
	}
}
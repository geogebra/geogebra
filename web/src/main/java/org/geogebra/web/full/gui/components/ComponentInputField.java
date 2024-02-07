package org.geogebra.web.full.gui.components;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.inputfield.Input;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.Shades;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * input field material design component
 */
public class ComponentInputField extends FlowPanel implements SetLabels, Input {
	private final Localization loc;
	private String errorTextKey;
	private final String labelTextKey;
	private final String placeholderTextKey;
	private final String suffixTextKey;
	private FlowPanel contentPanel;
	private FormLabel labelText;
	private InputPanelW inputTextField;
	private Label errorLabel;
	private Label suffixLabel;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param placeholder
	 *            placeholder text (can be null)
	 * @param labelTxt
	 *            label of input field
	 * @param errorTxt
	 *            error label of input field
	 * @param defaultValue
	 *            default text of input text field
	 * @param width
	 *            of input text field
	 * @param suffixTxt
	 *            suffix at end of text field
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue, int width,
			String suffixTxt) {
		this(app, placeholder, labelTxt, errorTxt, defaultValue, width, suffixTxt, true);
	}

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param placeholder
	 *            placeholder text (can be null)
	 * @param labelTxt
	 *            label of input field
	 * @param errorTxt
	 *            error label of input field
	 * @param defaultValue
	 *            default text of input text field
	 * @param width
	 *            of input text field
	 * @param suffixTxt
	 *            suffix at end of text field
	 * @param hasKeyboardBtn
	 *            whether to show keyboard button or not (disabled in Export3dDialog)
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue, int width,
			String suffixTxt, boolean hasKeyboardBtn) {
		this.loc = app.getLocalization();
		this.labelTextKey = labelTxt;
		this.errorTextKey = errorTxt;
		this.placeholderTextKey = placeholder;
		this.suffixTextKey = suffixTxt;
		buildGui(width, app, hasKeyboardBtn);
		if (!StringUtil.empty(defaultValue)) {
			setInputText(defaultValue);
		}
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param placeholder
	 *            placeholder text (can be null)
	 * @param labelTxt
	 *            label of input field
	 * @param errorTxt
	 *            error label of input field
	 * @param defaultValue
	 *            default text of input text field
	 * @param width
	 *            of input text field
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue, int width) {
		this(app, placeholder, labelTxt, errorTxt, defaultValue,
				width, null);
	}

	/**
	 * @return panel containing the whole component
	 */
	public FlowPanel getContentPanel() {
		return contentPanel;
	}

	private void buildGui(int width, AppW app, boolean hasKeyboardBtn) {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("inputTextField");
		// input text field
		inputTextField = new InputPanelW("", app, width, hasKeyboardBtn);
		inputTextField.addStyleName("textField");
		// label of text field
		labelText = new FormLabel().setFor(inputTextField.getTextComponent());
		labelText.setStyleName("inputLabel");
		labelText.addStyleName(Shades.NEUTRAL_700.getFgColName());
		// placeholder if there is any
		if (placeholderTextKey != null && !placeholderTextKey.isEmpty()) {
			inputTextField.getTextComponent().getTextBox().getElement()
				.setAttribute("placeholder",
							app.getLocalization().getMenu(placeholderTextKey));
		}
		// suffix if there is any
		addSuffix();
		// build component
		contentPanel.add(labelText);
		contentPanel.add(inputTextField);
		// add error label if there is any
		addErrorLabel(contentPanel);
		add(contentPanel);
		setLabels();
	}

	private void addErrorLabel(FlowPanel root) {
		if (!StringUtil.empty(errorTextKey)) {
			if (errorLabel == null) {
				errorLabel = new Label();
			}
			errorLabel.setText(errorTextKey);
			errorLabel.setStyleName("errorLabel");
			root.add(errorLabel);
		} else if (errorLabel != null) {
			errorLabel.removeFromParent();
		}
	}

	private void addSuffix() {
		if (!StringUtil.empty(suffixTextKey)) {
			if (suffixLabel == null) {
				suffixLabel = new Label();
			}
			suffixLabel.setText(suffixTextKey);
			suffixLabel.addStyleName("suffix");
			inputTextField.getTextComponent().add(suffixLabel);
		} else if (suffixLabel != null) {
			suffixLabel.removeFromParent();
		}
	}

	private void addFocusBlurHandlers() {
		inputTextField.getTextComponent().getTextBox()
				.addFocusHandler(event -> setFocusState());
		inputTextField.getTextComponent().getTextBox()
				.addBlurHandler(event -> resetInputField());
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputTextField.getTextComponent().getTextBox()
				.addMouseOverHandler(event -> getContentPanel().addStyleName("hoverState"));
		inputTextField.getTextComponent().getTextBox()
				.addMouseOutHandler(event -> getContentPanel().removeStyleName("hoverState"));
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		contentPanel.addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	public void resetInputField() {
		contentPanel.removeStyleName("focusState");
	}

	@Override
	public void setLabels() {
		labelText.setText(loc.getMenu(labelTextKey));
		if (errorLabel != null) {
			errorLabel.setText(loc.getMenu(errorTextKey));
		}
		if (placeholderTextKey != null && !placeholderTextKey.isEmpty()) {
			inputTextField.getTextComponent().getTextBox().getElement()
					.setAttribute("placeholder",
							loc.getMenu(placeholderTextKey));
		}
	}

	/**
	 * @return text field
	 */
	public InputPanelW getTextField() {
		return inputTextField;
	}

	/**
	 * @param text
	 *            should appear in the input text field
	 */
	public void setInputText(String text) {
		inputTextField.getTextComponent().setText(text);
		setError(null);
	}

	/**
	 * @param message
	 *            localized error
	 */
	public void setError(String message) {
		this.errorTextKey = message;
		addErrorLabel(contentPanel);
		Dom.toggleClass(this.contentPanel, "error", !StringUtil.empty(message));
	}

	@Override
	public String getText() {
		return inputTextField.getText();
	}

	@Override
	public void showError(String errorMessage) {
		setError(errorMessage);
	}

	@Override
	public void setErrorResolved() {
		setError(null);
	}

	/**
	 * @return whether an error is shown
	 */
	public boolean hasError() {
		return !StringUtil.empty(errorTextKey);
	}

	/**
	 * focus input text field
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(
				() -> this.getTextField().getTextComponent().setFocus(true));
	}
}
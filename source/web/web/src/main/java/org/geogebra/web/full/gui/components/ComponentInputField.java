package org.geogebra.web.full.gui.components;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.inputfield.Input;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.dialog.ProcessInput;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * input field material design component
 */
public class ComponentInputField extends FlowPanel implements SetLabels, Input, HasDisabledState {
	private final Localization loc;
	private String errorTextKey;
	private final String labelTextKey;
	private final String placeholderTextKey;
	private final String suffixTextKey;
	private FlowPanel contentPanel;
	private Label labelText;
	private InputPanelW inputTextField;
	private Label errorLabel;
	private Label suffixLabel;

	/**
	 * @param app see {@link AppW}
	 * @param placeholder placeholder text (can be null)
	 * @param labelTxt label of input field
	 * @param errorTxt error label of input field
	 * @param defaultValue default text of input text field
	 * @param suffixTxt suffix at end of text field
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue, String suffixTxt) {
		this(app, placeholder, labelTxt, errorTxt, defaultValue, suffixTxt, true);
	}

	/**
	 * @param app see {@link AppW}
	 * @param placeholder placeholder text (can be null)
	 * @param labelTxt label of input field
	 * @param errorTxt error label of input field
	 * @param defaultValue default text of input text field
	 * @param suffixTxt suffix at end of text field
	 * @param hasKeyboardBtn whether to show keyboard button or not
	 * (disabled in {@link org.geogebra.web.full.gui.dialog.Export3dDialog})
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue, String suffixTxt, boolean hasKeyboardBtn) {
		this.loc = app.getLocalization();
		this.labelTextKey = labelTxt;
		this.errorTextKey = errorTxt;
		this.placeholderTextKey = placeholder;
		this.suffixTextKey = suffixTxt;
		buildGui(app, hasKeyboardBtn);
		if (!StringUtil.empty(defaultValue)) {
			setInputText(defaultValue);
		}
		addClickHandler();
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	/**
	 * @param app see {@link AppW}
	 * @param placeholder placeholder text (can be null)
	 * @param labelTxt label of input field
	 * @param errorTxt error label of input field
	 * @param defaultValue default text of input text field
	 */
	public ComponentInputField(AppW app, String placeholder, String labelTxt,
			String errorTxt, String defaultValue) {
		this(app, placeholder, labelTxt, errorTxt, defaultValue, null);
	}

	// BUILD UI

	private void buildGui(AppW app, boolean hasKeyboardBtn) {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("inputTextField");
		contentPanel.addStyleName("validation");
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");
		// input text field
		inputTextField = new InputPanelW("", app, -1, hasKeyboardBtn);
		inputTextField.addStyleName("textField");
		inputTextField.getTextComponent().prepareShowSymbolButton(false);
		// label of text field
		if (labelTextKey != null && !labelTextKey.isBlank()) {
			labelText = BaseWidgetFactory.INSTANCE.newSecondaryText(
					app.getLocalization().getMenu(labelTextKey), "label");
		}
		// placeholder if there is any
		if (placeholderTextKey != null && !placeholderTextKey.isEmpty()) {
			inputTextField.getTextComponent().getTextBox().getElement()
				.setAttribute("placeholder",
							app.getLocalization().getMenu(placeholderTextKey));
		}
		// suffix if there is any
		addSuffix();
		// build component
		if (labelText != null) {
			optionHolder.add(labelText);
		}
		optionHolder.add(inputTextField);
		contentPanel.add(optionHolder);
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

	// HANDLERS

	private void addClickHandler() {
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!isDisabled()) {
					setFocusState();
					focusDeferred();
				}
			}
		});
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
	 * @param inputHandler input event handler
	 */
	public void addInputHandler(ProcessInput inputHandler) {
		Dom.addEventListener(inputTextField.getTextComponent().getTextBox().getElement(),
				"input", event -> inputHandler.onInput());
	}

	/**
	 * Sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		contentPanel.addStyleName("active");
	}

	/**
	 * Focus input text field
	 */
	public void focusDeferred() {
		Scheduler.get().scheduleDeferred(
				() -> getTextField().getTextComponent().setFocus(true));
	}

	/**
	 * Resets input style on blur
	 */
	public void resetInputField() {
		contentPanel.removeStyleName("active");
	}

	// INPUT ERROR HANDLING

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

	// HELPERS

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

	/**
	 * @return whether an error is shown
	 */
	public boolean hasError() {
		return !StringUtil.empty(errorTextKey);
	}

	@Override
	public void setDisabled(boolean disabled) {
		Dom.toggleClass(getContentPanel(), "disabled", disabled);
		inputTextField.setEnabled(!disabled);
	}

	/**
	 * @return whether the text field is disabled
	 */
	public boolean isDisabled() {
		return getContentPanel().getStyleName().contains("disabled");
	}

	/**
	 * @return panel containing the whole component
	 */
	public FlowPanel getContentPanel() {
		return contentPanel;
	}

	@Override
	public void setLabels() {
		if (labelText != null) {
			labelText.setText(loc.getMenu(labelTextKey));
		}
		if (errorLabel != null) {
			errorLabel.setText(loc.getMenu(errorTextKey));
		}
		if (placeholderTextKey != null && !placeholderTextKey.isEmpty()) {
			inputTextField.getTextComponent().getTextBox().getElement()
					.setAttribute("placeholder",
							loc.getMenu(placeholderTextKey));
		}
	}
}
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

import static org.geogebra.common.properties.PropertyView.ConfigurationUpdateDelegate;
import static org.geogebra.common.properties.PropertyView.TextField;
import static org.geogebra.common.properties.PropertyView.VisibilityUpdateDelegate;

import java.util.function.Consumer;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.inputfield.Input;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.TextFormat;
import org.geogebra.editor.web.MathFieldW;
import org.geogebra.web.full.gui.dialog.ProcessInput;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.full.gui.view.probcalculator.MathTextFieldW;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.accessibility.HasFocus;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.FocusWidget;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

/**
 * Input field material design component, supports plain text field and math text field.
 */
public class ComponentInputField extends FlowPanel implements SetLabels, Input,
		ConfigurationUpdateDelegate, VisibilityUpdateDelegate, HasFocus {
	private final Localization loc;
	private String errorTextKey;
	private String labelTextKey;
	private final String placeholderTextKey;
	private final String suffixTextKey;
	private FlowPanel contentPanel;
	private Label labelText;
	private InputAdapter adapter;
	private Label errorLabel;
	private Label suffixLabel;
	private TextField textFieldProperty;

	private interface InputAdapter extends IsWidget {

		void focus();

		void addFocusBlurHandlers();

		/**
		 * Add mouse over/ out handlers
		 */
		void addHoverHandlers();

		void addEnterHandler(Consumer<String> setValue);

		void addInputHandler(ProcessInput inputHandler);

		void addSuffix(Label suffixLabel);

		void setEnabled(boolean enabled);

		String getText();

		void setText(String text);

		void setPlaceholder(String localizedPlaceholder);

		void setAriaLabel(String localizedLabel);

		void focusAndSelectAll();
	}

	private final class TextInputAdapter implements InputAdapter {
		private final InputPanelW inputTextField;

		public TextInputAdapter(InputPanelW inputTextField) {
			this.inputTextField = inputTextField;
		}

		@Override
		public void focus() {
			inputTextField.getTextComponent().setFocus(true);
		}

		@Override
		public void addFocusBlurHandlers() {
			FocusWidget inputFocusWidget = inputTextField.getTextComponent().getTextBox();
			inputFocusWidget.addFocusHandler(event -> setFocusState());
			inputFocusWidget.addBlurHandler(event -> resetInputField());
		}

		@Override
		public void addHoverHandlers() {
			FocusWidget inputFocusWidget = inputTextField.getTextComponent().getTextBox();
			inputFocusWidget.addMouseOverHandler(event -> getContentPanel()
					.addStyleName("hoverState"));
			inputFocusWidget.addMouseOutHandler(event -> getContentPanel()
					.removeStyleName("hoverState"));
		}

		@Override
		public void addEnterHandler(Consumer<String> setValue) {
			inputTextField.getTextComponent().addEnterPressHandler(() -> {
				String text = inputTextField.getText();
				setValue.accept(text);
			});
		}

		@Override
		public void addInputHandler(ProcessInput inputHandler) {
			Dom.addEventListener(inputTextField.getTextComponent().getTextBox().getElement(),
					"input", event -> inputHandler.onInput());
		}

		@Override
		public void addSuffix(Label suffixLabel) {
			inputTextField.getTextComponent().add(suffixLabel);
		}

		@Override
		public void setEnabled(boolean enabled) {
			inputTextField.setEnabled(enabled);
		}

		@Override
		public String getText() {
			return inputTextField.getText();
		}

		@Override
		public void setText(String text) {
			inputTextField.getTextComponent().setText(text);
		}

		@Override
		public void setPlaceholder(String localizedPlaceholder) {
			inputTextField.getTextComponent().getTextBox().getElement()
					.setAttribute("placeholder", localizedPlaceholder);
		}

		@Override
		public void setAriaLabel(String localizedLabel) {
			AriaHelper.setTitle(inputTextField.getTextComponent().getTextField(), localizedLabel);
		}

		@Override
		public void focusAndSelectAll() {
			inputTextField.getTextComponent().setFocus(true);
			inputTextField.getTextComponent().selectAll();
		}

		@Override
		public Widget asWidget() {
			return inputTextField;
		}
	}

	private final class MathInputAdapter implements InputAdapter {

		private final MathTextFieldW inputMathField;

		public MathInputAdapter(MathTextFieldW inputMathField) {
			this.inputMathField = inputMathField;
		}

		@Override
		public void focus() {
			inputMathField.focus();
		}

		@Override
		public void addFocusBlurHandlers() {
			MathFieldW mathField = inputMathField.getMathField();
			mathField.setOnFocus(event -> setFocusState());
			inputMathField.addBlurHandler(event -> {
				textFieldProperty.setValue(inputMathField.getText());
				resetInputField();
			});
		}

		@Override
		public void addHoverHandlers() {
			Widget inputMathFieldWidget = inputMathField.asWidget();
			Dom.addEventListener(inputMathFieldWidget.getElement(), "mouseover",
					event -> getContentPanel().addStyleName("hoverState"));
			Dom.addEventListener(inputMathFieldWidget.getElement(), "mouseout",
					event -> getContentPanel().removeStyleName("hoverState"));
		}

		@Override
		public void addEnterHandler(Consumer<String> setValue) {
			inputMathField.addChangeHandler((enter) -> {
				if (enter) {
					String text = inputMathField.getText();
					setValue.accept(text);
				}
			});
		}

		@Override
		public void addInputHandler(ProcessInput inputHandler) {
			inputMathField.getMathField().getInternal()
					.registerMathFieldInternalListener(ignore -> inputHandler.onInput());
		}

		@Override
		public void addSuffix(Label suffixLabel) {
			// not needed
		}

		@Override
		public void setEnabled(boolean enabled) {
			inputMathField.getMathField().setEnabled(enabled);
		}

		@Override
		public String getText() {
			return inputMathField.getText();
		}

		@Override
		public void setText(String text) {
			inputMathField.setText(text);
		}

		@Override
		public void setPlaceholder(String localizedPlaceholder) {
			// not supported with LaTeX
		}

		@Override
		public void setAriaLabel(String localizedLabel) {
			AriaHelper.setTitle(inputMathField.asWidget(), localizedLabel);
		}

		@Override
		public void focusAndSelectAll() {
			focus();
			inputMathField.selectEntryAt(0, 0);
		}

		@Override
		public Widget asWidget() {
			return inputMathField.asWidget();
		}
	}

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
		adapter.addFocusBlurHandlers();
		adapter.addHoverHandlers();
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

	/**
	 * @param app see {@link AppW}
	 * @param placeholder placeholder text (can be null)
	 * @param errorTxt error label of input field
	 * @param property {@link TextField}
	 */
	public ComponentInputField(AppW app, String placeholder, String errorTxt, TextField property) {
		this.loc = app.getLocalization();
		this.labelTextKey = property.getLabel();
		this.errorTextKey = errorTxt;
		this.placeholderTextKey = placeholder;
		this.suffixTextKey = null;
		textFieldProperty = property;
		buildGui(app, true);
		if (!StringUtil.empty(property.getValue())) {
			setInputText(property.getValue());
		}
		addClickHandler();
		adapter.addFocusBlurHandlers();
		adapter.addHoverHandlers();
		textFieldProperty.setConfigurationUpdateDelegate(this);
		textFieldProperty.setVisibilityUpdateDelegate(this);
	}

	// BUILD UI

	private void buildGui(AppW app, boolean hasKeyboardBtn) {
		boolean isMathTextField = textFieldProperty != null
				&& textFieldProperty.getFormat().equals(TextFormat.MATH);
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("inputTextField");
		contentPanel.addStyleName("validation");
		contentPanel.addStyleName(isMathTextField ? "mathInput" : "textInput");

		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");
		// input text field
		if (isMathTextField) {
			createInputMathField(app);
		} else {
			createInputTextField(app, hasKeyboardBtn);
		}
		if (labelTextKey != null && !labelTextKey.isBlank()) {
			String localizedLabel = app.getLocalization().getMenu(labelTextKey);
			labelText = BaseWidgetFactory.INSTANCE.newSecondaryText(
					localizedLabel, "label");
			adapter.setAriaLabel(localizedLabel);
		}
		if (placeholderTextKey != null && !placeholderTextKey.isEmpty()) {
			adapter.setPlaceholder(app.getLocalization().getMenu(placeholderTextKey));
		}
		// suffix if there is any
		addSuffix();
		// build component
		if (labelText != null) {
			optionHolder.add(labelText);
		}

		optionHolder.add(adapter.asWidget());
		contentPanel.add(optionHolder);
		// add error label if there is any
		addErrorLabel(contentPanel);
		add(contentPanel);
		setLabels();
	}

	private void createInputTextField(AppW app, boolean hasKeyboardBtn) {
		InputPanelW inputTextField = new InputPanelW("", app, -1, hasKeyboardBtn);
		inputTextField.addStyleName("textField");
		inputTextField.getTextComponent().prepareShowSymbolButton(false);
		adapter = new TextInputAdapter(inputTextField);
	}

	private void createInputMathField(AppW app) {
		MathTextFieldW inputMathField = new MathTextFieldW(app);
		adapter = new MathInputAdapter(inputMathField);
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
			adapter.addSuffix(suffixLabel);
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

	/**
	 * @param enterHandler handler for Enter key
	 */
	public void addEnterHandler(Consumer<String> enterHandler) {
		adapter.addEnterHandler(enterHandler);
	}

	/**
	 * @param inputHandler input event handler
	 */
	public void addInputHandler(ProcessInput inputHandler) {
		adapter.addInputHandler(inputHandler);
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
		Scheduler.get().scheduleDeferred(adapter::focus);
	}

	/**
	 * Focus and select content.
	 */
	public void focusAndSelectAll() {
		adapter.focusAndSelectAll();
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
		return adapter.getText();
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
	 * @param text
	 *            should appear in the input text field
	 */
	public void setInputText(String text) {
		adapter.setText(text);
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

	/**
	 * Enable/disable input text field
	 * @param disabled whether it should be disabled or not
	 */
	public void setDisabled(boolean disabled) {
		Dom.toggleClass(getContentPanel(), "disabled", disabled);
		adapter.setEnabled(!disabled);
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
			adapter.setPlaceholder(loc.getMenu(placeholderTextKey));
		}
	}

	@Override
	public void configurationUpdated() {
		labelTextKey = textFieldProperty.getLabel();
		String localizedLabel = loc.getMenu(labelTextKey);
		if (labelText != null) {
			labelText.setText(localizedLabel);
		}
		setInputText(textFieldProperty.getValue());
		adapter.setAriaLabel(localizedLabel);
		setDisabled(!textFieldProperty.isEnabled());
		String error = textFieldProperty.getErrorMessage();
		setError(error);
	}

	@Override
	public void visibilityUpdated() {
		setVisible(textFieldProperty.isVisible());
	}

	@Override
	public void focus() {
		focusDeferred();
	}

	public AutoCompleteTextFieldW getTextWidget() {
		return adapter instanceof TextInputAdapter textInputAdapter
				? textInputAdapter.inputTextField.getTextComponent() : null;
	}
}

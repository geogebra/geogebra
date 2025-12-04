package org.geogebra.web.full.gui.components;

import static org.geogebra.common.properties.PropertyView.*;

import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class ComponentComboBox extends FlowPanel implements SetLabels,
		ConfigurationUpdateDelegate, VisibilityUpdateDelegate {
	private final AppW appW;
	private final AutoCompleteTextFieldW inputTextField;
	private Label label;
	private final String labelTextKey;
	private DropDownComboBoxController controller;
	private final String controlsID;
	private ComboBox comboBoxProperty;

	/**
	 * Creates a combo box using a list of String.
	 * @param app see {@link AppW}
	 * @param label label of combo box
	 * @param items popup items
	 */
	public ComponentComboBox(AppW app, String label, List<String> items) {
		appW = app;
		labelTextKey = label;
		controlsID = DOM.createUniqueId();
		addStyleName("comboBox");
		addStyleName("validation");
		inputTextField = new AutoCompleteTextFieldW(-1, appW, false, null);
		buildGUI();
		addHandlers();

		initController(items);
	}

	/**
	 * Creates a combo box using a {@link StringPropertyWithSuggestions}.
	 * @param app see {@link AppW}
	 * @param property see {@link org.geogebra.common.properties.PropertyView.ComboBox}
	 */
	public ComponentComboBox(AppW app, ComboBox property) {
		this(app, property.getLabel(), property.getItems());
		this.comboBoxProperty = property;
		setValue(property.getValue());
		addChangeHandler(() -> {
			String text = getSelectedText().trim();
			property.setValue(text);
			String message = property.getErrorMessage();
			AriaHelper.setErrorMessage(inputTextField.getTextBox(), message);
			setStyleName("error", message != null);
		});
		comboBoxProperty.setConfigurationUpdateDelegate(this);
		comboBoxProperty.setVisibilityUpdateDelegate(this);
	}

	private void initController(List<String> items) {
		controller = new DropDownComboBoxController(appW, this, items, labelTextKey,
				this::onClose);
		controller.addChangeHandler(() -> updateSelectionText(getSelectedText()));
		controller.setPopupID(controlsID);
		controller.setFocusAnchor(inputTextField.getInputElement());
		controller.addHighlightingListener(id ->
				AriaHelper.setActiveDescendant(inputTextField.getTextBox(), id));
		inputTextField.setUpDownArrowHandler(controller);
		updateSelectionText(getSelectedText());
	}

	private void buildGUI() {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");
		if (!StringUtil.empty(labelTextKey)) {
			label = BaseWidgetFactory.INSTANCE.newSecondaryText("", "label");
			label.getElement().setId(DOM.createUniqueId());
			optionHolder.add(label);
		}

		inputTextField.setAutoComplete(false);
		inputTextField.prepareShowSymbolButton(false);
		inputTextField.enableGGBKeyboard();
		inputTextField.addStyleName("textField");
		updateLabel();
		AriaHelper.setRole(inputTextField.getTextBox(), "combobox");
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), false);
		AriaHelper.setAutocomplete(inputTextField.getTextBox(), "none");
		AriaHelper.setControls(inputTextField.getTextBox(), controlsID);
		inputTextField.addBlurHandler(event -> controller.onInputChange(inputTextField.getText()));

		optionHolder.add(inputTextField);
		add(optionHolder);

		add(ComponentDropDown.createArrowIcon());
	}

	private void addHandlers() {
		addClickHandler();
		addFocusBlurHandlers();
		addHoverHandlers();
		addFieldKeyAndPointerHandler();
	}

	// Status helpers

	/**
	 * Enable/disable combo-box
	 * @param disabled whether it should be disabled or not
	 */
	public void setDisabled(boolean disabled) {
		inputTextField.setEnabled(!disabled);
		Dom.toggleClass(this, "disabled", disabled);
	}

	private boolean isDisabled() {
		return getElement().getClassName().contains("disabled");
	}

	private boolean isInputFocused() {
		return inputTextField.getElement().isOrHasChild(Dom.getActiveElement());
	}

	// Combo box and input field related handlers

	private void addClickHandler() {
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				controller.getPopup().forceKeyboardFocus(false);
				if (!isDisabled() && !isInputFocused()) {
					toggleExpanded();
				}
			}
		});
	}

	/**
	 * Add focus/blur handlers.
	 */
	private void addFocusBlurHandlers() {
		inputTextField.getTextBox().addFocusHandler(event -> {
			addStyleName("focusState");
			addStyleName("active");
		});
		inputTextField.getTextBox().addBlurHandler(event -> {
			removeStyleName("focusState");
			removeStyleName("active");
		});
	}

	/**
	 * Add mouse over/ out handlers.
	 */
	private void addHoverHandlers() {
		inputTextField.getTextBox()
				.addMouseOverHandler(event -> addStyleName("hoverState"));
		inputTextField.getTextBox()
				.addMouseOutHandler(event -> removeStyleName("hoverState"));
	}

	private void addFieldKeyAndPointerHandler() {
		inputTextField.addKeyUpHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER) {
				if (controller.isOpened()) {
					inputTextField.setText(getSelectedText());
					setExpanded(false);
				}
				controller.onInputChange(inputTextField.getText());
				inputTextField.setFocus(true);
			} else if (event.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
				setExpanded(false);
				inputTextField.setFocus(true);
			}
		});
	}

	/**
	 * Add a change handler.
	 * @param handler change handler
	 */
	public void addChangeHandler(Runnable handler) {
		controller.addChangeHandler(handler);
	}

	// Open/close related methods

	private void onClose() {
		resetTextField();
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), false);
		AriaHelper.setActiveDescendant(inputTextField.getTextBox(), null);
	}

	private void toggleExpanded() {
		setExpanded(!controller.isOpened());
	}

	private void setExpanded(boolean expanded) {
		if (expanded) {
			controller.setSelectedOption(controller.possibleSelectedIndex(
					inputTextField.getText()));
			controller.showAsComboBox();
			AriaHelper.setAriaExpanded(inputTextField.getTextBox(), true);
			Scheduler.get().scheduleDeferred(() -> inputTextField.setFocus(true));
		} else {
			inputTextField.setFocus(false);
			resetTextField();
			controller.closePopup();
		}
		Dom.toggleClass(this, "active", expanded);
	}

	// Helpers

	/**
	 * Update selection text
	 */
	public void updateSelectionText(String text) {
		inputTextField.setText(text);
	}

	private void resetTextField() {
		if (inputTextField.getText().isEmpty()) {
			inputTextField.setText(getSelectedText());
		}
	}

	public int getSelectedIndex() {
		return controller.getSelectedIndex();
	}

	/**
	 * @return if nothing selected text input, selected text otherwise
	 */
	public String getSelectedText() {
		return getSelectedIndex() == -1 ? inputTextField.getText() : controller.getSelectedText();
	}

	/**
	 * set text field value
	 * @param value value
	 */
	public void setValue(String value) {
		controller.setSelectedOption(controller.possibleSelectedIndex(value));
		inputTextField.setValue(value);
	}

	/**
	 * @param newLabel nem label of combo box
	 */
	public void setLabel(String newLabel) {
		label.setText(appW.getLocalization().getMenu(newLabel));
	}

	@Override
	public void setLabels() {
		controller.setLabels();
		updateSelectionText(getSelectedText());
	}

	private void updateLabel() {
		if (label != null) {
			String menu = appW.getLocalization().getMenu(labelTextKey);
			label.setText(menu);
			AriaHelper.setLabel(inputTextField.getTextBox(), menu);
		}
	}

	@Override
	public void configurationUpdated() {
		setValue(comboBoxProperty.getValue());
		setDisabled(!comboBoxProperty.isEnabled());
		setVisible(comboBoxProperty.isVisible());
		String message = comboBoxProperty.getErrorMessage();
		AriaHelper.setErrorMessage(inputTextField.getTextBox(), message);
		setStyleName("error", message != null);
	}

	@Override
	public void visibilityUpdated() {
		setVisible(comboBoxProperty.isVisible());
	}
}

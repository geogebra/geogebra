package org.geogebra.web.full.gui.components;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.NamedEnumeratedProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.event.dom.client.KeyCodes;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.SimplePanel;

import com.himamis.retex.editor.share.util.GWTKeycodes;

public class ComponentComboBox extends FlowPanel implements SetLabels, IsWidget {
	private final AppW appW;
	private AutoCompleteTextFieldW inputTextField;
	private Label label;
	private final String labelTextKey;
	private DropDownComboBoxController controller;

	/**
	 * Creates a combo box using a list of String.
	 * @param app see {@link AppW}
	 * @param label label of combo box
	 * @param items popup items
	 */
	public ComponentComboBox(AppW app, String label, List<String> items) {
		appW = app;
		labelTextKey = label;

		addStyleName("comboBox");
		buildGUI();
		addHandlers();

		initController(items);
	}

	/**
	 * Creates a combo box using a {@link NamedEnumeratedProperty}.
	 * @param app see {@link AppW}
	 * @param label label of combo box
	 * @param property popup items
	 */
	public ComponentComboBox(AppW app, String label, NamedEnumeratedProperty<?> property) {
		this(app, label, Arrays.asList(property.getValueNames()));
	}

	private void initController(List<String> items) {
		controller = new DropDownComboBoxController(appW, this, items, this::onClose);
		controller.addChangeHandler(() -> updateSelectionText(getSelectedText()));
		updateSelectionText(getSelectedText());
	}

	private void buildGUI() {
		FlowPanel optionHolder = new FlowPanel();
		optionHolder.addStyleName("optionLabelHolder");

		if (labelTextKey != null && !labelTextKey.isEmpty()) {
			label = BaseWidgetFactory.INSTANCE.newSecondaryText(
					appW.getLocalization().getMenu(labelTextKey), "label");
			optionHolder.add(label);
		}

		inputTextField = new AutoCompleteTextFieldW(-1, appW, false, null);
		inputTextField.setAutoComplete(false);
		inputTextField.prepareShowSymbolButton(false);
		inputTextField.enableGGBKeyboard();
		inputTextField.addStyleName("textField");
		inputTextField.addKeyUpHandler((event) -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				controller.onInputChange();
			}
		});
		inputTextField.addBlurHandler(event -> controller.onInputChange());

		optionHolder.add(inputTextField);
		add(optionHolder);

		SimplePanel arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());
		add(arrowIcon);
	}

	private void addHandlers() {
		addClickHandler();
		addFocusBlurHandlers();
		addHoverHandlers();
		addFieldKeyAndPointerHandler();
	}

	// Status helpers

	/**
	 * Disable drop-down component.
	 * @param disabled true, if drop-down should be disabled
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
		inputTextField.getTextBox()
				.addFocusHandler(event -> addStyleName("focusState"));
		inputTextField.getTextBox()
				.addBlurHandler(event -> removeStyleName("focusState"));
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
				toggleExpanded();
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
		removeStyleName("active");
		resetTextField();
	}

	private void toggleExpanded() {
		if (controller.isOpened()) {
			inputTextField.setFocus(false);
			resetTextField();
			controller.closePopup();
		} else {
			controller.showAsComboBox();
			Scheduler.get().scheduleDeferred(() -> inputTextField.selectAll());
		}
		boolean isOpen = controller.isOpened();
		Dom.toggleClass(this, "active", isOpen);
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
		controller.setSelectedOption(-1);
		inputTextField.setValue(value);
	}

	@Override
	public void setLabels() {
		if (label != null) {
			label.setText(appW.getLocalization().getMenu(labelTextKey));
		}
		controller.setLabels();
		updateSelectionText(getSelectedText());
	}
}

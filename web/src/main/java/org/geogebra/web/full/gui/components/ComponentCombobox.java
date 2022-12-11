package org.geogebra.web.full.gui.components;

import java.util.Arrays;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.properties.EnumerableProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.IsWidget;
import org.gwtproject.user.client.ui.SimplePanel;

import com.himamis.retex.editor.share.util.GWTKeycodes;

public class ComponentCombobox extends FlowPanel implements SetLabels, IsWidget {
	private final AppW appW;
	private FlowPanel contentPanel;
	private AutoCompleteTextFieldW inputTextField;
	private FormLabel labelText;
	private String labelTextKey;
	private SimplePanel arrowIcon;
	private boolean isDisabled = false;
	private DropDownComboBoxController controller;

	/**
	 * Constructor
	 * @param app - see {@link AppW}
	 * @param label - label of combobox
	 * @param property - popup items
	 */
	public ComponentCombobox(AppW app, String label, EnumerableProperty property) {
		appW = app;
		labelTextKey = label;

		addStyleName("combobox");
		buildGUI();
		addHandlers();

		initController(property);
	}

	private void initController(EnumerableProperty property) {
		controller = new DropDownComboBoxController(appW, this,
				Arrays.asList(property.getValues()), this::onClose);
		controller.setChangeHandler(() -> updateSelectionText(controller.getSelectedText()));
		updateSelectionText(controller.getSelectedText());
	}

	private void buildGUI() {
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("inputPanel");

		inputTextField = new AutoCompleteTextFieldW(-1, appW, false, null, false);
		inputTextField.addStyleName("textField");

		if (labelTextKey != null) {
			labelText = new FormLabel().setFor(inputTextField);
			labelText.setStyleName("inputLabel");
			labelText.setText(appW.getLocalization().getMenu(labelTextKey));
			add(labelText);
		}

		arrowIcon = new SimplePanel();
		arrowIcon.addStyleName("arrow");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE
				.arrow_drop_down().getSVG());

		contentPanel.add(inputTextField);
		contentPanel.add(arrowIcon);
		add(contentPanel);
	}

	private void addHandlers() {
		addClickHandler();
		addFocusBlurHandlers();
		addHoverHandlers();
		addFieldKeyAndPointerHandler();
	}

	private void addClickHandler() {
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (!isDisabled) {
					toggleExpanded();
				}
			}
		});
	}

	private void addFocusBlurHandlers() {
		inputTextField.getTextBox()
				.addFocusHandler(event -> addStyleName("focusState"));
		inputTextField.getTextBox()
				.addBlurHandler(event -> removeStyleName("focusState"));
	}

	/**
	 * Add mouse over/ out handlers
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

	private void onClose() {
		removeStyleName("active");
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE.arrow_drop_down()
				.withFill(GColor.BLACK.toString()).getSVG());
		resetTextField();
	}

	private void toggleExpanded() {
		if (controller.isOpened()) {
			inputTextField.setFocus(false);
			resetTextField();
			controller.closePopup();
		} else {
			controller.showAsComboBox();
			Scheduler.get().scheduleDeferred(() -> {
				inputTextField.selectAll();
			});
		}
		boolean isOpen = controller.isOpened();
		Dom.toggleClass(this, "active", isOpen);
		GColor arrowCol = isOpen
				? GeoGebraColorConstants.GEOGEBRA_ACCENT : GColor.BLACK;
		arrowIcon.getElement().setInnerHTML(MaterialDesignResources.INSTANCE.arrow_drop_down()
				.withFill(arrowCol.toString()).getSVG());
	}

	private void updateSelectionText(String text) {
		inputTextField.setText(text);
	}

	private void resetTextField() {
		if (inputTextField.getText().isEmpty()) {
			inputTextField.setText(controller.getSelectedText());
		}
	}

	/**
	 * Disable drop-down component
	 * @param disabled - true, if drop-down should be disabled
	 */
	public void setDisabled(boolean disabled) {
		isDisabled = disabled;
		inputTextField.setEnabled(!disabled);
		Dom.toggleClass(this, "disabled", disabled);
	}

	@Override
	public void setLabels() {
		if (labelText != null) {
			labelText.setText(appW.getLocalization().getMenu(labelTextKey));
		}
		controller.setLabels();
		updateSelectionText(controller.getSelectedText());
	}
}

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

package org.geogebra.web.full.euclidian.quickstylebar.components;

import java.util.List;
import java.util.function.Supplier;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.full.gui.components.ComponentDropDown;
import org.geogebra.web.full.gui.components.DropDownComboBoxController;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;
import org.jspecify.annotations.NonNull;

/**
 * Compact inline combo box for the quick style bar and top bar in Notes.
 */
public final class InlineComboBox extends FlowPanel implements SetLabels {

	private final AppW app;
	private final AutoCompleteTextFieldW inputTextField;
	private final DropDownComboBoxController controller;
	private final PropertyView.PropertyViewWithSuggestions property;
	private final Runnable requestFocusAfterUpdate;
	private String previousValue;
	private boolean handlingBlur;
	private boolean suppressPopupOnFocus;
	private final String titleKey;

	/**
	 * @param app application
	 * @param property combo box property
	 * @param requestFocusAfterUpdate called before an Enter commit so the rebuilt toolbar can
	 * focus the input
	 * @param titleKey translation key for title / ARIA label
	 */
	public InlineComboBox(
			@NonNull AppW app,
			PropertyView.@NonNull PropertyViewWithSuggestions property,
			@NonNull Runnable requestFocusAfterUpdate,
			@NonNull String titleKey) {
		this.app = app;
		this.property = property;
		this.titleKey = titleKey;
		this.requestFocusAfterUpdate = requestFocusAfterUpdate;
		inputTextField = new AutoCompleteTextFieldW(-1, app, false, null);
		ComponentDropDown.Styler styler = createStyler(property);
		Supplier<List<Integer>> dividers = property instanceof PropertyView.ComboBoxWithIcons withIcons
				? withIcons::getDividerLocations
				: () -> null;
		controller = new DropDownComboBoxController(
				app, dividers, this, property::getItems, property.getLabel(), this::onClose, styler);
		controller.getPopup().addStyleName("inlineComboBoxPopup");

		addStyleName("inlineComboBox");
		buildGUI();
		addHandlers();
		previousValue = property.getValue();
		updateFromProperty();
	}

	/**
	 * Update displayed value from underlying property.
	 */
	public void updateValue() {
		inputTextField.setText(property.getValue());
	}

	/**
	 * @param className add CSS class name to the dropdown popup
	 */
	public void addPopupClassName(String className) {
		controller.getPopup().addStyleName(className);
	}

	private ComponentDropDown.Styler createStyler(
			PropertyView.@NonNull PropertyViewWithSuggestions property) {
		return (item, index) -> {
			if (property instanceof PropertyView.ComboBoxWithIcons withIcons
					&& withIcons.getIcon(index) != null) {
				IconSpec spec =
						((AppWFull) app).getPropertiesIconResource().getImageResource(withIcons.getIcon(index));
				item.setIcon(spec);
			}
		};
	}

	private void buildGUI() {
		String controlsID = DOM.createUniqueId();
		controller.setPopupID(controlsID);
		controller.setFocusAnchor(inputTextField.getInputElement());

		inputTextField.setAutoComplete(false);
		inputTextField.prepareShowSymbolButton(false);
		inputTextField.enableGGBKeyboard();
		inputTextField.addStyleName("inlineComboBoxInput");

		AriaHelper.setRole(inputTextField.getTextBox(), "combobox");
		setExpanded(false);
		AriaHelper.setAutocomplete(inputTextField.getTextBox(), "none");
		AriaHelper.setControls(inputTextField.getTextBox(), controlsID);
		setLabels();

		add(inputTextField);
	}

	private void setExpanded(boolean expanded) {
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), expanded);
		AriaHelper.setAriaExpanded(this, expanded);
	}

	private void addHandlers() {
		controller.addChangeHandler(() -> {
			String selectedText = controller.getSelectedText();
			if (selectedText.isEmpty()) {
				return;
			}
			handlingBlur = true;
			inputTextField.setText(selectedText);
			stopEditing();
			commitSelectedValue(selectedText);
			inputTextField.setFocus(false);
			closePopup();
			handlingBlur = false;
		});

		inputTextField.addFocusHandler(event -> {
			addStyleName("focusState");
			app.closePopups();
			previousValue = getDisplayValue();
			inputTextField.setText(previousValue);
			property.startEditing();
			if (suppressPopupOnFocus) {
				suppressPopupOnFocus = false;
			} else {
				Scheduler.get().scheduleDeferred(inputTextField::selectAll);
				openPopup();
			}
		});

		inputTextField.addBlurHandler(event -> Scheduler.get().scheduleDeferred(() -> {
			if (!handlingBlur && !controller.isOpened()) {
				commitAndStopEditing();
			}
		}));

		inputTextField.addInputListener(event ->
				controller.setSelectedOption(controller.possibleSelectedIndex(inputTextField.getText())));

		inputTextField.getTextBox().addKeyDownHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER) {
				event.preventDefault();
				event.stopPropagation();
				commitAndKeepFocus();
			}
		});

		inputTextField.addKeyUpHandler(event -> {
			if (event.getNativeKeyCode() == GWTKeycodes.KEY_ESCAPE) {
				cancelAndStopEditing();
			}
		});

		inputTextField.setUpDownArrowHandler(controller);
	}

	private void openPopup() {
		controller.setSelectedOption(controller.possibleSelectedIndex(inputTextField.getText()));
		controller.showAsComboBox();
		addStyleName("active");
		setDataTitleEnabled(false);
		setExpanded(true);
	}

	/**
	 * Closes the dropdown.
	 */
	public void closePopup() {
		controller.closePopup();
		removeStyleName("active");
		setDataTitleEnabled(true);
		setExpanded(false);
	}

	private void commitAndKeepFocus() {
		handlingBlur = true;
		commitInputValue(true);
		closePopup();
		property.stopEditing();
		Scheduler.get().scheduleDeferred(this::focusWithoutPopup);
		handlingBlur = false;
	}

	private void commitAndStopEditing() {
		handlingBlur = true;
		commitInputValue(false);
		inputTextField.setFocus(false);
		closePopup();
		stopEditing();
		handlingBlur = false;
	}

	private void cancelAndStopEditing() {
		handlingBlur = true;
		inputTextField.setText(previousValue);
		inputTextField.setFocus(false);
		closePopup();
		stopEditing();
		handlingBlur = false;
	}

	private void commitInputValue(boolean keepFocus) {
		if (!property.validate(inputTextField.getText())) {
			inputTextField.setText(previousValue);
			controller.setSelectedOption(controller.possibleSelectedIndex(previousValue));
			return;
		}
		commitSelectedValue(inputTextField.getText(), keepFocus);
	}

	private void commitSelectedValue(String value) {
		commitSelectedValue(value, false);
	}

	private void commitSelectedValue(String value, boolean keepFocus) {
		if (!value.equals(property.getValue())) {
			if (keepFocus) {
				requestFocusAfterUpdate.run();
			}
			property.setValue(value);
			previousValue = property.getValue();
		}
		updateFromProperty();
		Dom.toggleClass(this, "error", property.getErrorMessage() != null);
	}

	private void stopEditing() {
		removeStyleName("focusState");
		property.stopEditing();
	}

	private void updateFromProperty() {
		String value = getDisplayValue();
		inputTextField.setText(value);
		controller.setSelectedOption(controller.possibleSelectedIndex(value));
	}

	private void onClose() {
		removeStyleName("active");
		setDataTitleEnabled(true);
		setExpanded(false);
	}

	private String getDisplayValue() {
		return property.getValue();
	}

	/**
	 * Focuses the input without reopening the dropdown.
	 */
	public void focusWithoutPopup() {
		suppressPopupOnFocus = true;
		inputTextField.setFocus(true);
		addStyleName("focusState");
	}

	@Override
	public void setLabels() {
		String label = app.getLocalization().getMenu(titleKey);
		AriaHelper.setLabel(this, label);
		AriaHelper.setLabel(inputTextField.getTextBox(), label);
		controller.setLabels();
		setDataTitleEnabled(!controller.isOpened());
	}

	private void setDataTitleEnabled(boolean enabled) {
		if (enabled) {
			inputTextField
					.getElement()
					.setAttribute("data-title", app.getLocalization().getMenu(titleKey));
		} else {
			inputTextField.getElement().removeAttribute("data-title");
		}
	}
}

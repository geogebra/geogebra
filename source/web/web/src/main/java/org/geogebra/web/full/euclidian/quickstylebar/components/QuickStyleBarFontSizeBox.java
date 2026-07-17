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

import static org.geogebra.common.properties.PropertyView.ComboBox;

import javax.annotation.Nonnull;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.properties.impl.objects.NotesFontSizeProperty;
import org.geogebra.editor.share.util.GWTKeycodes;
import org.geogebra.web.full.gui.components.DropDownComboBoxController;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * Compact editable font size combo box for the quick style bar in Notes.
 */
public class QuickStyleBarFontSizeBox extends FlowPanel implements SetLabels {

	private static final int POPUP_WIDTH = 52;

	private final AppW app;
	private final AutoCompleteTextFieldW inputTextField;
	private final DropDownComboBoxController controller;
	private final ComboBox property;
	private final Runnable requestFocusAfterUpdate;
	private String previousValue;
	private boolean handlingBlur;
	private boolean suppressPopupOnFocus;

	/**
	 * @param app application
	 * @param property font size combo box property
	 * @param requestFocusAfterUpdate called before an Enter commit so the rebuilt toolbar can
	 * focus the font size input
	 */
	public QuickStyleBarFontSizeBox(@Nonnull AppW app, @Nonnull ComboBox property,
			@Nonnull Runnable requestFocusAfterUpdate) {
		this.app = app;
		this.property = property;
		this.requestFocusAfterUpdate = requestFocusAfterUpdate;
		inputTextField = new AutoCompleteTextFieldW(-1, app, false, null);
		controller = new DropDownComboBoxController(app, property, this, property::getItems,
				property.getLabel(), this::onClose, null);
		controller.getPopup().addStyleName("quickStyleBarFontSizePopup");

		addStyleName("quickStyleBarFontSizeBox");
		buildGUI();
		addHandlers();
		previousValue = property.getValue();
		updateFromProperty();
	}

	private void buildGUI() {
		String controlsID = DOM.createUniqueId();
		controller.setPopupID(controlsID);
		controller.setFocusAnchor(inputTextField.getInputElement());

		inputTextField.setAutoComplete(false);
		inputTextField.prepareShowSymbolButton(false);
		inputTextField.enableGGBKeyboard();
		inputTextField.addStyleName("quickStyleBarFontSizeInput");

		AriaHelper.setRole(inputTextField.getTextBox(), "combobox");
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), false);
		AriaHelper.setAutocomplete(inputTextField.getTextBox(), "none");
		AriaHelper.setControls(inputTextField.getTextBox(), controlsID);
		setLabels();

		add(inputTextField);
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

		inputTextField.addBlurHandler(event -> {
			Scheduler.get().scheduleDeferred(() -> {
				if (!handlingBlur && !controller.isOpened()) {
					commitAndStopEditing();
				}
			});
		});

		inputTextField.addInputListener(event -> controller.setSelectedOption(
				controller.possibleSelectedIndex(inputTextField.getText())));

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
		controller.getPopup().setWidthInPx(POPUP_WIDTH);
		addStyleName("active");
		setDataTitleEnabled(false);
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), true);
	}

	/**
	 * Closes the dropdown.
	 */
	public void closePopup() {
		controller.closePopup();
		removeStyleName("active");
		setDataTitleEnabled(true);
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), false);
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
		Integer fontSize = NotesFontSizeProperty.parse(inputTextField.getText());
		if (fontSize == null) {
			inputTextField.setText(previousValue);
			controller.setSelectedOption(controller.possibleSelectedIndex(previousValue));
			return;
		}
		commitSelectedValue(String.valueOf(fontSize), keepFocus);
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
		AriaHelper.setAriaExpanded(inputTextField.getTextBox(), false);
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
		String label = app.getLocalization().getMenu("FontSize");
		AriaHelper.setLabel(this, label);
		AriaHelper.setLabel(inputTextField.getTextBox(), label);
		setDataTitleEnabled(!controller.isOpened());
	}

	private void setDataTitleEnabled(boolean enabled) {
		if (enabled) {
			inputTextField.getElement().setAttribute("data-title",
					app.getLocalization().getMenu("FontSize"));
		} else {
			inputTextField.getElement().removeAttribute("data-title");
		}
	}
}

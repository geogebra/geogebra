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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.dialog.SliderInputDialogModel;
import org.geogebra.common.gui.dialog.SliderInputDialogModel.Field;
import org.geogebra.common.gui.dialog.SliderInputDialogModel.SliderType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;

public class SliderDialog extends ComponentDialog implements HasKeyboardPopup {
	private final AppW appW;
	private final SliderInputDialogModel model;
	private ComponentInputField nameTextField;
	private IconButton numberButton;
	private IconButton angleButton;
	private ComponentInputField minTextField;
	private ComponentInputField maxTextField;
	private ComponentInputField stepTextField;
	private SliderType sliderType = SliderType.NUMBER;

	/**
	 * base dialog constructor
	 * @param appW see {@link AppW}
	 * @param dialogData contains trans keys for title and buttons
	 * @param x x-coordinate of slider in screen coords
	 * @param y x-coordinate of slider in screen coords
	 */
	public SliderDialog(final AppW appW, DialogData dialogData, int x, int y) {
		super(appW, dialogData, false, true);
		this.appW = appW;
		this.model = new SliderInputDialogModel(appW, appW.getActiveEuclidianView(),
				appW.getLocalization(), appW.getKernel(), x, y);
		addStyleName("sliderDialog");
		buildDialog();
		appW.registerPopup(this);
		addCloseHandler(event -> {
			appW.unregisterPopup(this);
			appW.hideKeyboard();
		});
		setOnPositiveAction(this::createSlider);
	}

	private void buildDialog() {
		createNameTextField();

		numberButton = createIconButton(SliderType.NUMBER);
		numberButton.setActive(true);
		angleButton = createIconButton(SliderType.ANGLE);

		FlowPanel buttonsHolder = new FlowPanel();
		buttonsHolder.addStyleName("buttonsHolder");
		buttonsHolder.add(numberButton);
		buttonsHolder.add(angleButton);

		FlowPanel firstRow = new FlowPanel();
		firstRow.addStyleName("firstRow");
		firstRow.add(nameTextField);
		firstRow.add(buttonsHolder);

		minTextField = createTextField("min", Field.MIN);
		maxTextField = createTextField("max", Field.MAX);
		stepTextField = createTextField("Step", Field.STEP);

		FlowPanel secondRow = new FlowPanel();
		secondRow.addStyleName("secondRow");
		secondRow.add(minTextField);
		secondRow.add(maxTextField);
		secondRow.add(stepTextField);
		updateTextFields();

		addDialogContent(firstRow, secondRow);
	}

	private void createNameTextField() {
		nameTextField = new ComponentInputField(appW, "",
				app.getLocalization().getMenu("Name"), "",
				model.getLastValidField(sliderType, Field.NAME), "");
		nameTextField.addStyleName("nameField");
		nameTextField.getTextWidget().addBlurHandler(event ->
				validateField(nameTextField, Field.NAME));
	}

	private ComponentInputField createTextField(String labelKey, Field field) {
		ComponentInputField textField = new ComponentInputField(appW, "",
				app.getLocalization().getMenu(labelKey), "",
				model.getLastValidField(sliderType, field), "");
		textField.getTextWidget().addBlurHandler(event -> validateField(textField, field));
		textField.addInputHandler(() -> {
			if (!isNumeric()) {
				insertDegreeSymbolIfNeeded(textField);
			}
		});

		return textField;
	}

	private void validateField(ComponentInputField textField, Field field) {
		textField.setError(model.validateField(sliderType, field,
				textField.getText()));
	}

	private IconButton createIconButton(SliderType sliderType) {
		return new IconButton(appW, () -> updateUI(sliderType),
				new ImageIconSpec(getIcon(sliderType)), getAriaLabel(sliderType));
	}

	private String getAriaLabel(SliderType type) {
		return type == SliderType.ANGLE ? "Angle" : "Numeric";
	}

	private SVGResource getIcon(SliderType type) {
		return type == SliderType.ANGLE
				? MaterialDesignResources.INSTANCE.angle_black()
				: MaterialDesignResources.INSTANCE.number();
	}

	private void updateUI(SliderType sliderType) {
		this.sliderType = sliderType;
		updateSelection();
		updateTextFields();
	}

	private void updateSelection() {
		boolean isNumeric = isNumeric();
		numberButton.setActive(isNumeric);
		angleButton.setActive(!isNumeric);
	}

	private boolean isNumeric() {
		return SliderType.NUMBER == sliderType;
	}

	private void updateTextFields() {
		nameTextField.setInputText(model.getLastValidField(sliderType, Field.NAME));
		minTextField.setInputText(model.getLastValidField(sliderType, Field.MIN));
		maxTextField.setInputText(model.getLastValidField(sliderType, Field.MAX));
		stepTextField.setInputText(model.getLastValidField(sliderType, Field.STEP));
	}

	private void createSlider() {
		validateField(nameTextField, Field.NAME);
		validateField(minTextField, Field.MIN);
		validateField(maxTextField, Field.MAX);
		validateField(stepTextField, Field.STEP);

		boolean created = model.submit(sliderType, nameTextField.getText(),
				minTextField.getText(), maxTextField.getText(), stepTextField.getText());
		setPreventHide(!created);
	}

	private void insertDegreeSymbolIfNeeded(ComponentInputField inputField) {
		String text = inputField.getText();

		for (int i = 0; i < text.length(); i++) {
			if (!StringUtil.isDigit(text.charAt(i))) {
				return;
			}
		}

		int caretPos = inputField.getTextWidget().getCaretPosition();
		inputField.setInputText(inputField.getText() + Unicode.DEGREE_STRING);
		inputField.getTextWidget().setCaretPosition(caretPos);
	}
}

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

import static org.geogebra.editor.share.util.Unicode.DEGREE_CHAR;

import java.util.EnumMap;
import java.util.Map;

import org.geogebra.common.euclidian.smallscreen.AdjustSlider;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.properties.impl.NumericPropertyUtil;
import org.geogebra.common.util.StringUtil;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.components.ComponentInputField;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;

public class SliderDialog extends ComponentDialog implements HasKeyboardPopup {
	public enum SliderType {
		NUMERIC("Numeric", MaterialDesignResources.INSTANCE.number()),
		ANGLE("Angle", MaterialDesignResources.INSTANCE.angle_black());

		private final String ariaLabel;
		private final SVGResource svgResource;

		SliderType(String ariaLabel, SVGResource svgResource) {
			this.ariaLabel = ariaLabel;
			this.svgResource = svgResource;
		}

		public String getAriaLabel() {
			return ariaLabel;
		}

		public IconSpec getIcon() {
			return new ImageIconSpec(svgResource);
		}
	}

	private final AppW appW;
	private ComponentInputField nameTextField;
	private IconButton numberButton;
	private IconButton angleButton;
	private ComponentInputField minTextField;
	private ComponentInputField maxTextField;
	private ComponentInputField stepTextField;
	private SliderType sliderType = SliderType.NUMERIC;
	private final EnumMap<SliderType, String> min = new EnumMap<>(Map.of(
			SliderType.NUMERIC, "-5",
			SliderType.ANGLE, "0" + DEGREE_CHAR
	));
	private final EnumMap<SliderType, String> max = new EnumMap<>(Map.of(
			SliderType.NUMERIC, "5",
			SliderType.ANGLE, "360" + DEGREE_CHAR
	));
	private final EnumMap<SliderType, String> step = new EnumMap<>(Map.of(
			SliderType.NUMERIC, "0.1",
			SliderType.ANGLE, "1" + DEGREE_CHAR
	));
	private GeoNumeric geoResult;
	private GeoNumeric number;
	private GeoAngle angle;
	private final NumericPropertyUtil util;

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
		this.util = new NumericPropertyUtil(appW.getKernel().getAlgebraProcessor());
		initResultGeo(x, y);
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

		numberButton = createIconButton(SliderType.NUMERIC);
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

		minTextField = createTextField("min", min);
		maxTextField = createTextField("max", max);
		stepTextField = createTextField("Step", step);

		FlowPanel secondRow = new FlowPanel();
		secondRow.addStyleName("secondRow");
		secondRow.add(minTextField);
		secondRow.add(maxTextField);
		secondRow.add(stepTextField);
		updateTextFields();

		addDialogContent(firstRow, secondRow);
	}

	private void createNameTextField() {
		nameTextField = new ComponentInputField((AppW) app, "",
				app.getLocalization().getMenu("Name"), "", number.getDefaultLabel(), "");
		nameTextField.addStyleName("nameField");
		nameTextField.getTextField().getTextComponent().addBlurHandler(event -> {
			String value = nameTextField.getText();
			GeoNumeric element = isNumeric() ? number : angle;
			if (value.isEmpty()
					|| !LabelManager.isValidLabel(value, element.getKernel(), element)) {
				nameTextField.setError(appW.getLocalization().getError("InvalidInput"));
			} else {
				nameTextField.setError(null);
			}
		});
	}

	private ComponentInputField createTextField(String labelKey,
			Map<SliderType, String> valuePerType) {
		ComponentInputField textField = new ComponentInputField((AppW) app, "",
				app.getLocalization().getMenu(labelKey), "", valuePerType.get(sliderType), "");
		textField.getTextField().getTextComponent().addBlurHandler(event -> {
			if (!util.isNumber(textField.getText())) {
				textField.setError(appW.getLocalization().getError("InvalidInput"));
			} else {
				textField.setError(null);
				valuePerType.put(sliderType, textField.getText());
			}
		});
		textField.addInputHandler(() -> {
			if (!isNumeric()) {
				insertDegreeSymbolIfNeeded(textField);
			}
		});

		return textField;
	}

	private IconButton createIconButton(SliderType sliderType) {
		return new IconButton(appW, () -> updateUI(sliderType),
				sliderType.getIcon(), sliderType.getAriaLabel());
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
		return SliderType.NUMERIC.equals(sliderType);
	}

	private void updateTextFields() {
		nameTextField.setInputText(isNumeric()
				? number.getDefaultLabel() : angle.getDefaultLabel());
		minTextField.setInputText(min.get(sliderType));
		maxTextField.setInputText(max.get(sliderType));
		stepTextField.setInputText(step.get(sliderType));

	}

	private void initResultGeo(int x, int y) {
		Construction cons = app.getKernel().getConstruction();

		number = new GeoNumeric(cons);
		angle = new GeoAngle(cons);

		// allow outside range 0-360
		angle.setAngleStyle(GeoAngle.AngleStyle.UNBOUNDED);

		GeoNumeric.setSliderFromDefault(number, false);
		GeoNumeric.setSliderFromDefault(angle, true);
		number.setValue(1);
		angle.setValue(45 * Math.PI / 180);

		number.setSliderLocation(x, y, true);
		number.setAVSliderOrCheckboxVisible(true);
		angle.setSliderLocation(x, y, true);
		angle.setAVSliderOrCheckboxVisible(true);
		geoResult = null;
	}

	private boolean hasError() {
		return nameTextField.hasError() || minTextField.hasError() || maxTextField.hasError()
				|| stepTextField.hasError();
	}

	private void createSlider() {
		if (hasError()) {
			setPreventHide(true);
			return;
		}
		setPreventHide(false);

		geoResult = !isNumeric() ? angle : number;
		String label = nameTextField.getText();
		if (!label.isBlank()) {
			geoResult.setLabel(label);
		}
		String minValue = minTextField.getText();
		String maxValue = maxTextField.getText();
		String stepValue = stepTextField.getText();
		geoResult.setIntervalMin(getNumberFromInput(minValue));
		geoResult.setIntervalMax(getNumberFromInput(maxValue));
		geoResult.setAnimationStep(getNumberFromInput(stepValue));

		geoResult.setLabelMode(GeoElementND.LABEL_NAME_VALUE);
		geoResult.setLabelVisible(true);
		geoResult.update();
		AdjustSlider.ensureOnScreen(geoResult, app.getActiveEuclidianView());

		app.getActiveEuclidianView().requestFocusInWindow();
		app.storeUndoInfo();
		app.getKernel().notifyRepaint();
	}

	private NumberValue getNumberFromInput(final String inputText) {
		boolean emptyString = "".equals(inputText);
		NumberValue value = new MyDouble(appW.getKernel(), Double.NaN);
		if (!emptyString) {
			NumberValue parsed = appW.getKernel().getAlgebraProcessor()
					.evaluateToNumeric(inputText, false);
			if (parsed != null) {
				value = parsed;
			}
		}
		return value;
	}

	private void insertDegreeSymbolIfNeeded(ComponentInputField inputField) {
		String text = inputField.getText();

		for (int i = 0; i < text.length(); i++) {
			if (!StringUtil.isDigit(text.charAt(i))) {
				return;
			}
		}

		int caretPos = inputField.getTextField().getTextComponent().getCaretPosition();
		inputField.setInputText(inputField.getText() + Unicode.DEGREE_STRING);
		inputField.getTextField().getTextComponent().setCaretPosition(caretPos);
	}
}

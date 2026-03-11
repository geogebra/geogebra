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

package org.geogebra.common.gui.dialog;

import static org.geogebra.editor.share.input.Character.isLetter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.smallscreen.AdjustSlider;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Model for the slider creation dialog. Manages two pre-created geo elements
 * (a {@link GeoNumeric} and a {@link GeoAngle}), validates user input for each
 * field, and commits the slider to the construction on confirmation.
 */
@SuppressWarnings("checkstyle:UnicodeRange")
public final class SliderInputDialogModel {

	/** Whether the slider represents a plain number or an angle. */
	public enum SliderType { NUMBER, ANGLE }

	/** Editable fields in the slider dialog. */
	public enum Field { NAME, MIN, MAX, STEP }

	private final App app;
	private final EuclidianView view;
	private final Kernel kernel;
	private final Localization localization;
	private final GeoNumeric number;
	private final GeoAngle angle;
	private final Map<SliderType, Map<Field, String>> lastValidInputs;
	private String error;

	/**
	 * Creates the model.
	 * @param app application
	 * @param euclidianView view used for on-screen placement adjustments
	 * @param localization source for localized error messages
	 * @param kernel the kernel that owns the construction
	 * @param x screen x-coordinate for the slider
	 * @param y screen y-coordinate for the slider
	 */
	public SliderInputDialogModel(App app, EuclidianView euclidianView,
			Localization localization, Kernel kernel, int x, int y) {
		this.app = app;
		this.view = euclidianView;
		this.localization = localization;
		this.kernel = kernel;
		this.number = createNumber(kernel.getConstruction(), x, y);
		this.angle = createAngle(kernel.getConstruction(), x, y);
		this.lastValidInputs = new HashMap<>(Map.of(
				SliderType.NUMBER, new HashMap<>(Map.of(
						Field.NAME, number.getDefaultLabel(),
						Field.MIN, "-5",
						Field.MAX, "5",
						Field.STEP, "0.1")),
				SliderType.ANGLE, new HashMap<>(Map.of(
						Field.NAME, angle.getDefaultLabel(),
						Field.MIN, "0°",
						Field.MAX, "360°",
						Field.STEP, "1°"))));
	}

	/**
	 * Returns the last successfully validated value for the given field,
	 * falling back to the default if no valid input has been entered yet.
	 * @param type slider type
	 * @param field the field
	 * @return last valid string value
	 */
	public @Nonnull String getLastValidField(SliderType type, Field field) {
		return lastValidInputs.get(type).get(field);
	}

	/**
	 * Validates a single field. Intended to be called when the user finishes editing.
	 * @param type slider type
	 * @param field field to validate
	 * @param value current input value
	 * @return localized error message or {@code null} when valid
	 */
	public @CheckForNull String validateField(SliderType type, Field field, String value) {
		String error = field == Field.NAME ? validateName(value) : validateNumber(type, value);
		if (error == null) {
			lastValidInputs.get(type).put(field, value);
		}
		return error;
	}

	/**
	 * Creates the slider from the field values.
	 * @param sliderType slider type (NUMBER or ANGLE)
	 * @param name label to assign to the slider geo
	 * @param min string representation of the minimum bound
	 * @param max string representation of the maximum bound
	 * @param step string representation of the animation step
	 * @return whether slider creation succeeded
	 */
	public boolean submit(SliderType sliderType, String name, String min,
			String max, String step) {
		if (validateField(sliderType, Field.NAME, name) != null
				|| validateField(sliderType, Field.MIN, min) != null
				|| validateField(sliderType, Field.MAX, max) != null
				|| validateField(sliderType, Field.STEP, step) != null) {
			return false;
		}
		try {
			createSlider(sliderType, name, min, max, step);
		} catch (IllegalArgumentException e) {
			return false;
		}
		view.requestFocusInWindow();
		kernel.notifyRepaint();
		return true;
	}

	private static GeoNumeric createNumber(Construction construction, int x, int y) {
		GeoNumeric number = new GeoNumeric(construction);
		GeoNumeric.setSliderFromDefault(number, false);
		number.setValue(1);
		number.setSliderLocation(x, y, true);
		number.setAVSliderOrCheckboxVisible(true);
		return number;
	}

	private static GeoAngle createAngle(Construction construction, int x, int y) {
		GeoAngle angle = new GeoAngle(construction);
		angle.setAngleStyle(GeoAngle.AngleStyle.UNBOUNDED);
		GeoNumeric.setSliderFromDefault(angle, true);
		angle.setValue(45 * Math.PI / 180);
		angle.setSliderLocation(x, y, true);
		angle.setAVSliderOrCheckboxVisible(true);
		return angle;
	}

	private String validateName(String name) {
		if (name.isEmpty() || !isLetter(name.charAt(0))) {
			return localization.getError("IllegalArgument");
		}
		return null;
	}

	private String validateNumber(SliderType type, String value) {
		try {
			evaluateNumber(type, value);
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private void createSlider(SliderType sliderType, String name, String min, String max,
			String step) {
		NumberValue minNumber = evaluateNumber(sliderType, min);
		NumberValue maxNumber = evaluateNumber(sliderType, max);
		NumberValue stepNumber = evaluateNumber(sliderType, step);
		GeoNumeric element = switch (sliderType) {
			case NUMBER -> number;
			case ANGLE -> angle;
		};
		element.setLabel(name);
		element.setIntervalMin(minNumber);
		element.setIntervalMax(maxNumber);
		element.setAnimationStep(stepNumber);
		element.setAutoStep(false);
		element.setEuclidianVisible(true);
		element.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		element.setLabelVisible(true);
		element.setAVSliderOrCheckboxVisible(true);
		element.update();

		AdjustSlider.ensureOnScreen(element, view);
		app.storeUndoInfo();
	}

	private NumberValue evaluateNumber(SliderType sliderType, String inputText) {
		error = null;

		NumberValue value = null;
		if (!inputText.isBlank()) {
			String input = inputText.trim();
			if (input.charAt(input.length() - 1) != '°'
					&& sliderType == SliderType.ANGLE) {
				input = input + '°';
			}
			value = kernel.getAlgebraProcessor().evaluateToNumeric(input, errorHandler);
		}
		if (value == null) {
			if (error == null) {
				error = localization.getError("NumberExpected");
			}
			throw new IllegalArgumentException(error);
		}
		return value;
	}

	private final ErrorHandler errorHandler = new ErrorHandler() {
		@Override
		public void showError(@CheckForNull String msg) {
			error = msg;
		}

		@Override
		public void showCommandError(String command, String message) {
			// Nothing to do
		}

		@Override
		public String getCurrentCommand() {
			return "";
		}

		@Override
		public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
			return false;
		}

		@Override
		public void resetError() {
			// Nothing to do
		}
	};
}

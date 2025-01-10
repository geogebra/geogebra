package org.geogebra.common.gui.dialog.validator;

import org.geogebra.common.gui.inputfield.Input;
import org.geogebra.common.kernel.validator.NumberValidator;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Validates the inputs of the Table Values dialog
 */
public class TableValuesDialogValidator {

	private NumberValidator numberValidator;
	private Localization localization;

	/**
	 * @param app
	 *            application
	 */
	public TableValuesDialogValidator(App app) {
		numberValidator = new NumberValidator(app.getKernel().getAlgebraProcessor());
		localization = app.getLocalization();
	}

	private Double getDouble(Input input, Double minValue,
			String outOfBoundsKey) {
		try {
			double value = numberValidator.getDouble(input.getText(), minValue);
			input.setErrorResolved();
			return value;
		} catch (NumberFormatException e) {
			input.showError(
					localization.getError(
							NumberValidator.NUMBER_FORMAT_ERROR_MESSAGE_KEY));
		} catch (NumberValueOutOfBoundsException e) {
			input.showError(localization.getError(outOfBoundsKey));
		}
		return null;
	}

	/**
	 *
	 * @param minField Start value input field.
	 * @param maxField End value input field.
	 * @param stepField Step input field.
	 * @return If all the strings of the inputs can be converted to doubles
	 *         then this method returns an array with length 3 which contains
	 *         the double values of the input strings.
	 *         If the strings cannot be converted to doubles then the method returns null.
	 */
	public double[] getDoubles(Input minField, Input maxField, Input stepField) {
		Double min = getDouble(minField, null, null);
		Double max = getDouble(maxField, min,
				NumberValidator.NUMBER_TOO_SMALL_ERROR_MESSAGE_KEY);
		Double step = getDouble(stepField, 0.0,
				NumberValidator.NUMBER_NEGATIVE_ERROR_MESSAGE_KEY);

		if (min == null || max == null || step == null) {
			return null;
		} else {
			return new double[]{min, max, step};
		}
	}
}

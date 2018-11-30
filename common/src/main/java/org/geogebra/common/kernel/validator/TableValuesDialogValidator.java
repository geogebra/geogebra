package org.geogebra.common.kernel.validator;

import org.geogebra.common.gui.inputfield.Input;
import org.geogebra.common.kernel.validator.exception.NumberValueOutOfBoundsException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class TableValuesDialogValidator {

	private static final String NUMBER_FORMAT_ERROR_MESSAGE_KEY = "InputError.Enter_a_number";
	private static final String NUMBER_TOO_SMALL_ERROR_MESSAGE_KEY = "InputError.EndValueLessThanStartValue";
	private static final String NUMBER_NEGATIVE_ERROR_MESSAGE_KEY = "InputError.Enter_a_number_greater_than_0";

	private NumberValidator numberValidator;
	private Localization localization;

	public TableValuesDialogValidator(App app) {
		numberValidator = new NumberValidator(app.getKernel().getAlgebraProcessor());
		localization = app.getLocalization();
	}

	private Double getDouble(Input input, Double minValue) {
		try {
			double value = numberValidator.getDouble(input.getText(), minValue);
			input.setErrorResolved();
			return value;
		} catch (NumberFormatException e) {
			input.showError(localization.getError(NUMBER_FORMAT_ERROR_MESSAGE_KEY));
			return null;
		}
	}

	public double[] getDoubles(Input minField, Input maxField, Input stepField) {
		Double min;
		Double max = null;
		Double step = null;

		min = getDouble(minField, null);

		try {
			max = getDouble(maxField, min);
			if (max != null) {
				maxField.setErrorResolved();
			}
		} catch (NumberValueOutOfBoundsException e) {
			maxField.showError(localization.getError(NUMBER_TOO_SMALL_ERROR_MESSAGE_KEY));
		}

		try {
			step = getDouble(stepField, 0.0);
			if (step != null) {
				stepField.setErrorResolved();
			}
		} catch (NumberValueOutOfBoundsException e) {
			stepField.showError(localization.getError(NUMBER_NEGATIVE_ERROR_MESSAGE_KEY));
		}

		if (min == null || max == null || step == null) {
			return null;
		} else {
			return new double[]{min, max, step};
		}
	}
}

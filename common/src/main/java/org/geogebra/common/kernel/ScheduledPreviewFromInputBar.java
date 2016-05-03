package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.debug.Log;

/**
 * Periodically tries evaluating current input and creates preview
 * 
 * @author Mathieu + Zbynek
 */
public class ScheduledPreviewFromInputBar implements Runnable {

	private static final int DEFAULT_MAX_LENGTH = 1000;
	/**
	 * 
	 */
	private final Kernel kernel;

	/**
	 * @param kernel
	 *            kernel
	 */
	ScheduledPreviewFromInputBar(Kernel kernel) {
		this.kernel = kernel;
	}

	private String input = "";
	private String validInput = "";
	private ErrorHandler validation;
	private int maxLength = DEFAULT_MAX_LENGTH;

	private void setInput(String str, ErrorHandler validation) {
		this.input = str;
		this.validation = validation;
		if (str.length() > maxLength || str.length() == 0) {
			return;
		}
		long start = System.currentTimeMillis();
		try {

			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(input);
			if (ve != null) {
				validInput = input;
			}
		} catch (MyError t) {
			ErrorHelper.handleError(t, null, kernel.getLocalization(),
					validation);
		} catch (Exception e) {
			ErrorHelper.handleException(e, kernel.getApplication(),
					validation);
		}
		if (System.currentTimeMillis() > start + 200) {
			maxLength = str.length();
			validInput = null;
		} else {
			Log.debug(str.length() + " / " + maxLength + " time "
					+ (System.currentTimeMillis() - start));
			maxLength = DEFAULT_MAX_LENGTH;
		}
	}

	/**
	 * @param fallback
	 *            what to return if no input is valid
	 * @return last valid value of input
	 */
	public String getInput(String fallback) {
		String ret = validInput;
		validInput = null;
		if (ret == null || ret.length() == 0) {
			return fallback;
		}
		return ret;
	}

	private GeoElement[] previewGeos;

	public void run() {
		if (input.length() == 0) {
			if (validation != null) {
				validation.showError(null);
			}
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			return;
		}
		if (validInput == null) {
			if (validation != null) {
				// timeout -- assume OK as we don't know if it's wrong
				validation.showError(maxLength != DEFAULT_MAX_LENGTH ? null
						: kernel.getLocalization().getError("InvalidInput"));
			}
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			return;
		}
		EvalInfo info = new EvalInfo(false, true).withScripting(false);
		Log.debug("preview for: " + validInput);
		boolean silentModeOld = this.kernel.isSilentMode();
		previewGeos = null;
		try {
			this.kernel.setSilentMode(true);
			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(validInput);
			GeoCasCell casEval = this.kernel.getAlgebraProcessor()
					.checkCasEval(ve.getLabel(), input, null);
			if (casEval == null) {
				GeoElement existingGeo = this.kernel.lookupLabel(ve.getLabel());
				if (existingGeo == null) {

					previewGeos = this.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(ve, false,
									validation, false, null, info);
					if (previewGeos != null) {
						for (GeoElement geo : previewGeos) {
							geo.setSelectionAllowed(false);

						}
					}

					this.kernel.notifyUpdatePreviewFromInputBar(previewGeos);
				} else {
					Log.debug("existing geo: " + existingGeo);
					this.kernel.notifyUpdatePreviewFromInputBar(null);
				}
			} else {
				Log.debug("cas cell ");
				this.kernel.notifyUpdatePreviewFromInputBar(null);
			}
			if (validation != null && previewGeos != null
					&& validInput.equals(input)) {
				validation.showError(null);
			}
			this.kernel.setSilentMode(silentModeOld);

		} catch (Throwable ee) {
			Log.debug("-- invalid input");
			this.kernel.setSilentMode(true);
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			this.kernel.setSilentMode(silentModeOld);
		}
	}

	/**
	 * try to create/update preview for input typed
	 * 
	 * @param newInput
	 *            current algebra input
	 * @param validate
	 *            validation callback
	 */
	public void updatePreviewFromInputBar(String newInput,
			ErrorHandler validate) {

		if (this.input.equals(newInput)) {
			Log.debug("no update needed (same input)");
			return;
		}

		setInput(newInput, validate);

		kernel.getApplication().schedulePreview(this);

	}

}
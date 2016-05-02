package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.util.AsyncOperation;
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
	private AsyncOperation<Boolean> validation;
	private int maxLength = DEFAULT_MAX_LENGTH;

	private void setInput(String str, AsyncOperation<Boolean> validation) {
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
		} catch (Throwable t) {
			// input is invalid quite often
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
		if (input.length() == 0 || validInput == null) {
			if (validation != null) {
				// timeout -- assume OK as we don't know if it's wrong
				validation.callback(maxLength != DEFAULT_MAX_LENGTH);
			}
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			return;
		}
		EvalInfo info = new EvalInfo(false, true).withScripting(false);
		Log.debug("preview for: " + input);
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
									ErrorHelper.silent(), false, null, info);
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
			if (validation != null) {
				validation.callback(new Boolean(
						input.equals(validInput) && previewGeos != null));
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
										  AsyncOperation<Boolean> validate) {

		if (this.input.equals(newInput)) {
			Log.debug("no update needed (same input)");
			return;
		}

		setInput(newInput, validate);

		kernel.getApplication().schedulePreview(this);

	}

	/**
	 * preview is not recalculated if input has not changed
	 * since last calculation
	 *
	 * @param newInput input
	 * @return GeoElement[] preview for this input
	 */
	public GeoElement[] getPreview(String newInput) {

		if (this.input.equals(newInput)) {
			Log.debug("no update needed (same input)");
			return previewGeos;
		}

		// create new preview immediately
		kernel.getApplication().cancelPreview();
		setInput(newInput, null);
		run();
		return previewGeos;

	}

}
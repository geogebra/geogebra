package org.geogebra.common.kernel;

import org.geogebra.common.gui.inputfield.InputHelper;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
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

	private static final int DEFAULT_MAX_LENGTH = 5;
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
		if (str.length() == 0) {
			maxLength = DEFAULT_MAX_LENGTH;
		}
		if (str.length() > maxLength || str.length() == 0) {
			validInput = null;
			Log.debug("Timeout at length " + maxLength);
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
			t.printStackTrace();
			ErrorHelper.handleError(t, null, kernel.getLocalization(),
					validation);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorHelper.handleException(e, kernel.getApplication(),
					validation);
		} catch (Error e) {
			e.printStackTrace();
			ErrorHelper.handleException(new Exception(e),
					kernel.getApplication(), validation);
		}
		if (System.currentTimeMillis() > start + 200) {
			maxLength = str.length();
			validInput = null;
		} else {
			Log.debug(str + (validInput == input ? "OK" : "INVALID") + " time "
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
		if (fallback == null || fallback.length() == 0) {
			return "";
		}
		String ret = validInput;
		validInput = null;
		if (ret == null || ret.length() == 0) {
			return fallback;
		}
		return ret;
	}

	private GeoElement[] previewGeos;
	private String[] sliders;

	public void run() {
		cleanOldSliders();
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
		EvalInfo info = new EvalInfo(false, true).withScripting(false)
				.withCAS(false);
		Log.debug("preview for: " + validInput);
		boolean silentModeOld = this.kernel.isSilentMode();
		previewGeos = null;
		Long start = System.currentTimeMillis();
		try {
			this.kernel.setSilentMode(true);
			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(validInput);

			if (!isCASeval(ve)) {
				GeoElement existingGeo = this.kernel.lookupLabel(ve.getLabel());
				if (existingGeo == null) {

					GeoElement[] inputGeos = this.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(ve, false,
									validation, false, null, info);
					previewGeos = null;
					if (inputGeos != null) {
						InputHelper.updateProperties(previewGeos, kernel
								.getApplication().getActiveEuclidianView());
						int unlabeled = 0;
						for (GeoElement geo : inputGeos) {
							if (geo instanceof GeoFunction) {
								boolean b = ((GeoFunction) geo)
										.validate(ve.getLabel() == null, false);
								if (!b) {
									geo.setUndefined();
								}
							}
							if (!geo.isLabelSet()) {
								geo.setSelectionAllowed(false);
								unlabeled++;
							}
						}
						previewGeos = new GeoElement[unlabeled];
						int i = 0;
						for (GeoElement geo : inputGeos) {
							if (!geo.isLabelSet()) {
								previewGeos[i++] = geo;
							}
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
		if (System.currentTimeMillis() > start + 200) {
			maxLength = validInput.length();
			validInput = null;
		}
	}

	private static boolean isCASeval(ValidExpression ve) {
		String label = ve.getLabel();
		if (label != null && label.startsWith("$")) {
			Integer row = -1;
			try {
				row = Integer.parseInt(label.substring(1)) - 1;
			} catch (Exception e) {
				// spreadsheet reference
			}
			return row > 0;
		}
		return false;
	}

	private void cleanOldSliders() {
		if (sliders != null) {
			for (int i = 0; i < sliders.length; i++) {
				GeoElement slider = kernel.lookupLabel(sliders[i].trim());
				slider.setFixed(false);
				slider.remove();

			}
			kernel.notifyRepaint();
			sliders = null;
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

	/**
	 * preview is not recalculated if input has not changed since last
	 * calculation
	 *
	 * @param newInput
	 *            input
	 * @return GeoElement[] preview for this input
	 */
	public GeoElement[] getPreview(String newInput) {

		if (this.input.equals(newInput)) {
			Log.debug("no update needed (same input)");
			return previewGeos;
		}

		// create new preview immediately
		kernel.getApplication().cancelPreview();
		setInput(newInput, validation);
		run();
		return previewGeos;

	}

	/**
	 * @param string
	 *            slider names
	 */
	public void addSliders(String string) {
		cleanOldSliders();
		sliders = string.split(",");

	}

	/**
	 * @return whether last input parses OK
	 */
	public boolean isValid() {
		if (validInput == null && input != null) {
			setInput(input, validation);
		}
		return input != null && input.equals(validInput);
	}

}
package org.geogebra.common.kernel;

import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

public class ScheduledPreviewFromInputBar implements Runnable {

	/**
	 * 
	 */
	private final Kernel kernel;

	/**
	 * @param kernel
	 */
	ScheduledPreviewFromInputBar(Kernel kernel) {
		this.kernel = kernel;
	}

	private String input = "";
	private String validInput = "";
	private AsyncOperation<Boolean> validation;

	public void setInput(String str, AsyncOperation<Boolean> validation) {
		this.input = str;
		this.validation = validation;
		try {

			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(input);
			if (ve != null) {
				validInput = input;
			}
		} catch (Throwable t) {

		}
	}

	public String getInput(String fallback) {
		String ret = validInput;
		validInput = null;
		if (ret == null || ret.length() == 0) {
			return fallback;
		}
		return ret;
	}

	public void run() {
		// TODO Auto-generated method stub
		if (input.length() == 0) {
			// remove preview (empty input)
			Log.debug("remove preview (empty input)");
			this.kernel.notifyUpdatePreviewFromInputBar(null);
			return;
		}

		Log.debug("preview for: " + input);
		boolean silentModeOld = this.kernel.isSilentMode();
		GeoElement[] geos = null;
		try {
			this.kernel.setSilentMode(true);
			ValidExpression ve = this.kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(validInput);
			GeoCasCell casEval = this.kernel.getAlgebraProcessor().checkCasEval(
					ve.getLabel(), input, null);
			if (casEval == null) {
				GeoElement existingGeo = this.kernel.lookupLabel(ve.getLabel());
				if (existingGeo == null) {

					geos = this.kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(ve,
									false, false, true,
									false, null);
					if (geos != null) {
						for (GeoElement geo : geos) {
							geo.setSelectionAllowed(false);

						}
					}



					this.kernel.notifyUpdatePreviewFromInputBar(geos);
				} else {
					Log.debug("existing geo: " + existingGeo);
					this.kernel.notifyUpdatePreviewFromInputBar(null);
				}
			} else {
				Log.debug("cas cell ");
				this.kernel.notifyUpdatePreviewFromInputBar(null);
			}
			validation.callback(
					new Boolean(input.equals(validInput) && geos != null));
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
	 * @param input
	 *            current algebra input
	 */
	public void updatePreviewFromInputBar(String input,
			AsyncOperation warning) {

		if (this.input.equals(input)) {
			Log.debug("no update needed (same input)");
			return;
		}

		setInput(input, warning);

		kernel.getApplication().schedulePreview(this);

	}

}
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

package org.geogebra.common.cas.view;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;

/**
 * Controller for CAS mouse and keyboard events
 */
public class CASTableCellController {
	/**
	 * Handles pressing of Enter key after user input. The behaviour depends on
	 * the currently selected mode in the toolbar (Evaluate, Keep Input,
	 * Numeric) and Ctrl, Alt keys.
	 * 
	 * @param control
	 *            whether control is pressed
	 * @param alt
	 *            whether alt is pressed
	 * 
	 * @param app
	 *            application
	 * @param focus
	 *            whether this was triggered by enter rather than blur
	 */
	public synchronized void handleEnterKey(boolean control, boolean alt,
			App app, boolean focus) {
		// AppD app = view.getApp();
		int mode = app.getMode();
		ModeSetter ms = focus ? ModeSetter.TOOLBAR : ModeSetter.CAS_BLUR;
		// Ctrl + Enter toggles between the modes Evaluate and Numeric
		if (control) {
			if (mode == EuclidianConstants.MODE_CAS_NUMERIC) {
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE, ms);
			} else {
				app.setMode(EuclidianConstants.MODE_CAS_NUMERIC, ms);
			}
			app.setMode(mode, ms);
			return;
		}

		// Alt + Enter toggles between the modes Evaluate and Keep Input
		if (alt) {
			if (mode == EuclidianConstants.MODE_CAS_KEEP_INPUT) {
				app.setMode(EuclidianConstants.MODE_CAS_EVALUATE, ms);
			} else {
				app.setMode(EuclidianConstants.MODE_CAS_KEEP_INPUT, ms);
			}
			app.setMode(mode, ms);
			return;
		}

		// Enter depends on current mode
		switch (mode) {
		default:
			// switch back to Evaluate
			app.setMode(EuclidianConstants.MODE_CAS_EVALUATE, ms);
			break;
		case EuclidianConstants.MODE_CAS_EVALUATE:
		case EuclidianConstants.MODE_CAS_NUMERIC:
		case EuclidianConstants.MODE_CAS_KEEP_INPUT:
			// apply current tool again
			app.setMode(mode, ms);
			break;

		}
	}
}

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

import org.geogebra.common.gui.dialog.handler.NumberChangeSignInputHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog for one number and changing sign
 */
public class NumberChangeSignInputDialogW extends ComponentInputDialog {
	private boolean changingSign;

	/**
	 * @param app application
	 * @param message message
	 * @param data dialog data
	 * @param initString initial content
	 * @param handler input handler
	 * @param changingSign
	 *            says if the sign has to be changed
	 */
	public NumberChangeSignInputDialogW(AppW app, String message, DialogData data,
			String initString, NumberChangeSignInputHandler handler,
			boolean changingSign) {
		super(app, data, false, false, handler, message, initString);
		this.changingSign = changingSign;
	}

	@Override
	protected void processInputHandler(String inputText,
			AsyncOperation<Boolean> callback) {
		((NumberChangeSignInputHandler) getInputHandler()).processInput(
				inputText, changingSign, this, callback);
	}
}
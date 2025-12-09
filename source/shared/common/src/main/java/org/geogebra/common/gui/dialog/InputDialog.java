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

import java.util.ArrayList;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Abstract class for input dialogs
 */
public abstract class InputDialog implements ErrorHandler {

	private String initString;
	private InputHandler inputHandler;

	protected void processInputHandler(String inputText,
			AsyncOperation<Boolean> callback) {
		getInputHandler().processInput(inputText, this, callback);
	}

	protected void openProperties(App app, GeoElement geo) {
		ArrayList<GeoElement> tempArrayList = new ArrayList<>(1);
		tempArrayList.add(geo);
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS,
				tempArrayList);

	}

	protected String getInitString() {
		return initString;
	}

	protected void setInitString(String initString) {
		this.initString = initString;
	}

	protected InputHandler getInputHandler() {
		return inputHandler;
	}

	protected void setInputHandler(InputHandler inputHandler) {
		this.inputHandler = inputHandler;
	}
}

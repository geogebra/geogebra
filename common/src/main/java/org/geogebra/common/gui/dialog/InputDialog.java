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

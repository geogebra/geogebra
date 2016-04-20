package org.geogebra.common.gui.dialog;

import java.util.ArrayList;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.AsyncOperation;

/**
 * Abstract class for input dialogs
 */
public abstract class InputDialog {

	protected String initString;
	protected InputHandler inputHandler;
	protected String inputText = null;
	protected ArrayList<GeoElement> tempArrayList = new ArrayList<GeoElement>();

	protected void processInputHandler(AsyncOperation<Boolean> callback) {
		inputHandler.processInput(inputText, callback);
	}


}

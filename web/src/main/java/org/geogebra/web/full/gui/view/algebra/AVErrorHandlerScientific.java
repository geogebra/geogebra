package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Algebra View Error Handling for Scientific Calculator
 * 
 * @author laszlo
 */
public class AVErrorHandlerScientific implements ErrorHandler {

	public AVErrorHandlerScientific() {
		// nothing to do
	}

	@Override
	public void showError(String msg) {
		// nothing to do
	}

	@Override
	public void showCommandError(String command, String message) {
		// nothing to do
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void resetError() {
		// nothing to do
	}
}
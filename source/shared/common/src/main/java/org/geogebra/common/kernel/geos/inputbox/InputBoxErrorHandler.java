package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

class InputBoxErrorHandler implements ErrorHandler {

	boolean errorOccurred;

	@Override
	public void showError(String msg) {
		errorOccurred = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccurred = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccurred = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccurred = false;
	}
}

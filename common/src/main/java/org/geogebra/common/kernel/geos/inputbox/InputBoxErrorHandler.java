package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

class InputBoxErrorHandler implements ErrorHandler {

	boolean errorOccured;

	@Override
	public void showError(String msg) {
		errorOccured = true;
	}

	@Override
	public void showCommandError(String command, String message) {
		errorOccured = true;
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		errorOccured = true;
		return false;
	}

	@Override
	public void resetError() {
		errorOccured = false;
	}
}

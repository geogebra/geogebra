package org.geogebra.commands;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class ErrorAccumulator implements ErrorHandler {
	String errors = "";
	public void showError(String msg) {
		errors += msg;

	}

	public void showCommandError(String command, String message) {
		errors += message;

	}

	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetError() {
		// TODO Auto-generated method stub

	}

	public Object getErrors() {
		return errors;
	}

}

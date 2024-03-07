package org.geogebra.test.commands;

import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;

public class ErrorAccumulator implements ErrorLogger {
	String errors = "";
	String errorsSinceReset = "";

	@Override
	public void showError(String msg) {
		errors += msg;
		errorsSinceReset += msg;
	}

	@Override
	public void showCommandError(String command, String message) {
		showError(message);
	}

	@Override
	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetError() {
		errorsSinceReset = "";
	}

	public String getErrors() {
		return errors;
	}

	public String getErrorsSinceReset() {
		return errorsSinceReset;
	}

	@Override
	public void log(Throwable e) {
		// errors expected, don't print
	}
}

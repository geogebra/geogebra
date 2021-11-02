package org.geogebra.test.commands;

import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;

public class ErrorAccumulator implements ErrorLogger {
	String errors = "";

	@Override
	public void showError(String msg) {
		errors += msg;

	}

	@Override
	public void showCommandError(String command, String message) {
		errors += message;

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
		// TODO Auto-generated method stub

	}

	public String getErrors() {
		return errors;
	}

	@Override
	public void log(Throwable e) {
		// errors expected, don't print
	}
}

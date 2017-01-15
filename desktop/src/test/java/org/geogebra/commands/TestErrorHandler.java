package org.geogebra.commands;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.junit.Assert;

public class TestErrorHandler implements ErrorHandler {

	public static final ErrorHandler INSTANCE = new TestErrorHandler();

	@Override
	public void showError(String msg) {
		Assert.assertNull(msg);

	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void showCommandError(String command, String message) {
		Assert.assertNull(command);

	}

	@Override
	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetError() {
		// nothing to do

	}

}

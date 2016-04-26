package org.geogebra.commands;

import org.geogebra.common.main.error.ErrorHandler;
import org.junit.Assert;

public class TestErrorHandler implements ErrorHandler {

	public static final ErrorHandler INSTANCE = new TestErrorHandler();

	public void showError(String msg) {
		Assert.assertNull(msg);

	}

	public void setActive(boolean b) {
		// TODO Auto-generated method stub

	}

	public void showCommandError(String command, String message) {
		Assert.assertNull(command);

	}

	public String getCurrentCommand() {
		// TODO Auto-generated method stub
		return null;
	}

}

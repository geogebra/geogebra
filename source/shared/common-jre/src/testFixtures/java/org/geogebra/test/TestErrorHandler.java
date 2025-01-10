package org.geogebra.test;

import static org.junit.Assert.fail;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorLogger;
import org.geogebra.common.util.AsyncOperation;

/**
 * Error handler that fails a JUnit test when error is found
 */
public final class TestErrorHandler implements ErrorLogger {

	/**
	 * Singleton instance
	 */
	public static final ErrorHandler INSTANCE = new TestErrorHandler();

	private TestErrorHandler() {
		// singleton
	}

	@Override
	public void showError(String msg) {
		fail("Unexpected error:" + msg);
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void showCommandError(String command, String message) {
		fail("Error for " + command + ": " + message);
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public void resetError() {
		// nothing to do
	}

	@Override
	public void log(Throwable e) {
		e.printStackTrace();
		Throwable cause = e.getCause() == null ? e : e.getCause();
		fail("Unexpected error: " + cause.getMessage());
	}

}

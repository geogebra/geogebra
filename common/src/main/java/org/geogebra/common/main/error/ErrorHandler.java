package org.geogebra.common.main.error;

public interface ErrorHandler {

	void showError(String msg);

	void showCommandError(String command, String message);

	String getCurrentCommand();

}

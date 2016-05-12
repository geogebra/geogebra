package org.geogebra.common.main.error;

import org.geogebra.common.util.AsyncOperation;

public interface ErrorHandler {

	void showError(String msg);

	void showCommandError(String command, String message);

	String getCurrentCommand();

	boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback);

}

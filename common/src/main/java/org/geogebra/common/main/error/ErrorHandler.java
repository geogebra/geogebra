package org.geogebra.common.main.error;

public interface ErrorHandler {

	void showError(String msg);

	void setActive(boolean b);

	void showCommandError(String command, String message);

}

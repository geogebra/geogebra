package org.geogebra.web.html5.main;

import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Default error handler
 */
public class ErrorHandlerW implements ErrorHandler {
	private final AppW app;

	/**
	 * @param app
	 *            application
	 */
	public ErrorHandlerW(AppW app) {
		this.app = app;
	}

	@Override
	public void showError(String msg) {
		if (!app.isErrorDialogsActive()) {
			return;
		}
		app.showErrorDialog("Error", null, "OK", msg, null);
	}

	@Override
	public void resetError() {
		// do nothing
	}

	@Override
	public boolean onUndefinedVariables(String string,
			AsyncOperation<String[]> callback) {
		return app.getGuiManager().checkAutoCreateSliders(string, callback);
	}

	@Override
	public void showCommandError(final String command, String message) {
		if (!app.isErrorDialogsActive()) {
			return;
		}
		app.showErrorDialog(app.getLocalization().getError("Error"), "Close",
				"ShowOnlineHelp", message, () -> openCommandHelp(command));
	}

	/**
	 * @param command
	 *            command name
	 */
	protected void openCommandHelp(String command) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().openCommandHelp(command);
		}
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}
}

package org.geogebra.web.html5.main;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.javax.swing.GOptionPane;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

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
		Log.trace("");
		if (!app.isErrorDialogsActive()) {
			return;
		}
		String title = GeoGebraConstants.APPLICATION_NAME + " - "
				+ app.getLocalization().getError("Error");

		app.getOptionPane().showConfirmDialog(msg, title,
				GOptionPane.DEFAULT_OPTION, GOptionPane.ERROR_MESSAGE, null);
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
		String title = GeoGebraConstants.APPLICATION_NAME + " - "
				+ app.getLocalization().getError("Error");

		String[] optionNames = { app.getLocalization().getMenu("OK"),
				app.getLocalization().getMenu("ShowOnlineHelp") };
		app.getOptionPane().showOptionDialog(message, title, 0,
				GOptionPane.ERROR_MESSAGE, null, optionNames,
				new AsyncOperation<String[]>() {
					@Override
					public void callback(String[] dialogResult) {
						if ("1".equals(dialogResult[0])) {
							openCommandHelp(command);
						}
					}
				});

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

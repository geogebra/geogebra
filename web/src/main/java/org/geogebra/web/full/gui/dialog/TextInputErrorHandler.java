package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

public class TextInputErrorHandler implements ErrorHandler {
	private final App app;

	public TextInputErrorHandler(App app) {
		this.app = app;
	}

	@Override
	public void showError(String msg) {
		((AppW) app).getToolTipManager().showBottomMessage(msg, (AppW) app);
	}

	@Override
	public void showCommandError(String command, String message) {
		showError(message);
	}

	@Override
	public String getCurrentCommand() {
		return null;
	}

	@Override
	public boolean onUndefinedVariables(String string, AsyncOperation<String[]> callback) {
		return false;
	}

	@Override
	public void resetError() {
		// nothing to reset
	}
}
